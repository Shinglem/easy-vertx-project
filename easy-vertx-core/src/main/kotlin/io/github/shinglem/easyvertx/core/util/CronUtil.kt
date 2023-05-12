package io.github.shinglem.easyvertx.core.util

import com.cronutils.model.CronType
import com.cronutils.model.definition.CronDefinitionBuilder
import com.cronutils.model.time.ExecutionTime
import com.cronutils.parser.CronParser
import io.vertx.core.Handler
import io.vertx.core.Vertx
import io.vertx.kotlin.coroutines.dispatcher
import io.vertx.kotlin.coroutines.toReceiveChannel
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.receiveAsFlow
import java.time.Duration
import java.time.ZonedDateTime


fun main() {
    runBlocking {
        val myThreadLocal = ThreadLocal.withInitial { "init" }
        myThreadLocal.set("aaa")
        println("1  " + myThreadLocal.get()) // Prints "null"
        withContext(Dispatchers.IO+ myThreadLocal.asContextElement()) {
            println("2  " + myThreadLocal.get()) // Prints "foo"
            withContext(Dispatchers.IO + myThreadLocal.apply { set("bbb") }.asContextElement()) {
                println("3  " + myThreadLocal.get()) // Prints "foo", but it's on UI thread
            }
        }
        println("4  " + myThreadLocal.get()) // Prints "null"
    }
}


fun Vertx.cron(
    cronStr: String,
    type: CronType = CronType.QUARTZ,
    execNow: Boolean = false,
    handler: (CronScheduler) -> Unit
) {
    CronScheduler(this, cronStr, type, execNow)
        .schedule(handler)
}

suspend fun Vertx.cronSuspend(
    cronStr: String,
    type: CronType = CronType.QUARTZ,
    execNow: Boolean = false,
    handler: suspend (CronStream) -> Unit
) {
    CronStream(this, cronStr, type, execNow)
        .schedule(handler)
}

class CronStream(
    private val vertx: Vertx,
    cronExpression: String,
    type: CronType,
    private val execNow: Boolean = false
) {
    val expression: ExecutionTime

    var timerId: Long

    var executionTime: ZonedDateTime? = null
        private set

    private var suspendFunction: (suspend (CronStream) -> Unit?)? = null

    init {
        timerId = -1L
        val definition = CronDefinitionBuilder.instanceDefinitionFor(type)
        val parser = CronParser(definition)
        expression = ExecutionTime.forCron(parser.parse(cronExpression))
    }


    suspend fun schedule(handler: suspend (CronStream) -> Unit): CronStream {
        this.suspendFunction = handler
        if (execNow) {
            suspendFunction?.let { it(this) }
        }
        scheduleNextSuspendTimer(0L)
        return this
    }

    suspend fun handle(event: Long) {
        timerId = -1L
        if (suspendFunction == null) {
            return
        }
        scheduleNextSuspendTimer(20L)
        suspendFunction!!(this)
    }

    private suspend fun scheduleNextSuspendTimer(addMilliseconds: Long) {
        val now = ZonedDateTime.now()
        if (executionTime == null) {
            executionTime = now
        }
        expression.nextExecution(executionTime).orElse(null).let { next: ZonedDateTime? ->
            executionTime = next
            val delay = getNextDelay(now)

            CoroutineScope(vertx.dispatcher()).launch {
                vertx.timerStream(delay + addMilliseconds)
                    .toReceiveChannel(vertx)
                    .receiveAsFlow()
                    .collect {
                        timerId = it
                        handle(it)
                    }
            }

        }
    }

    private fun getNextDelay(time: ZonedDateTime): Long {
        val timeToNextExecution = Duration.between(time, executionTime)
        // If the program is paused for long enough (debugging) and a task is scheduled to run frequently,
        // the newly calculated execution time can be less than current time, that could cause negative value here.
        // With this solution the missed runs will happen in a burst after the pause until it catches up.
        // (Vertx does not allow to set a timer with delay < 1 ms)
        return Math.max(1, timeToNextExecution.toMillis()).toLong()
    }
}

class CronScheduler(
    private val vertx: Vertx,
    cronExpression: String,
    type: CronType,
    private val execNow: Boolean = false
) :
    Handler<Long> {
    private val expression: ExecutionTime
    private var handler: Handler<CronScheduler>? = null
    private var timerId: Long
    private var executionTime: ZonedDateTime? = null

    init {
        timerId = -1L
        val definition = CronDefinitionBuilder.instanceDefinitionFor(type)
        val parser = CronParser(definition)
        expression = ExecutionTime.forCron(parser.parse(cronExpression))
    }

    fun schedule(handler: Handler<CronScheduler>): CronScheduler {
        this.handler = handler
        if (execNow) {
            this.handler?.handle(this)
        }
        scheduleNextTimer(0L)
        return this
    }

    fun cancel() {
        if (vertx.cancelTimer(timerId)) {
            handler = null
        }
        timerId = -1L
    }

    override fun handle(event: Long) {
        timerId = -1L
        if (handler == null) {
            return
        }
        scheduleNextTimer(20L)
        handler!!.handle(this)
    }

    fun active(): Boolean {
        return timerId >= 0L
    }

    private fun scheduleNextTimer(addMilliseconds: Long) {
        val now = ZonedDateTime.now()
        if (executionTime == null) {
            executionTime = now
        }
        expression.nextExecution(executionTime).ifPresent { next: ZonedDateTime? ->
            executionTime = next
            val delay = getNextDelay(now)
            timerId = vertx.setTimer(delay + addMilliseconds, this)
        }
    }

    private fun getNextDelay(time: ZonedDateTime): Long {
        val timeToNextExecution = Duration.between(time, executionTime)
        // If the program is paused for long enough (debugging) and a task is scheduled to run frequently,
        // the newly calculated execution time can be less than current time, that could cause negative value here.
        // With this solution the missed runs will happen in a burst after the pause until it catches up.
        // (Vertx does not allow to set a timer with delay < 1 ms)
        return Math.max(1, timeToNextExecution.toMillis()).toLong()
    }
}
