package io.github.shinglem.easyvertx.core.spring

import io.vertx.core.Promise
import io.vertx.core.Verticle
import io.vertx.core.spi.VerticleFactory
import org.springframework.context.ApplicationContext
import org.springframework.context.annotation.AnnotationConfigApplicationContext
import java.util.*
import java.util.concurrent.Callable


class SpringVerticleFactory : VerticleFactory {
    /* (non-Javadoc)
     * @see io.vertx.core.spi.VerticleFactory#prefix()
     */
    override fun prefix(): String {
        return PREFIX
    }

    override fun createVerticle(
        verticleName: String,
        classLoader: ClassLoader,
        promise: Promise<Callable<Verticle>>
    ) {
        val v = createVerticle(verticleName, classLoader)
        promise.complete(Callable {
            v
        })
    }


    @Throws(Exception::class)
    fun createVerticle(verticleName: String, classLoader: ClassLoader): Verticle {
        var verticleName = verticleName
        Objects.requireNonNull(verticleName, "Verticle Name is required")
        verticleName = VerticleFactory.removePrefix(verticleName)
        Objects.requireNonNull(verticleName, "Verticle Name must be more than just the prefix")
        val ctx = applicationContext

        val clz = Class.forName(verticleName)

        return ctx.getBean(clz) as Verticle
    }

    /**
     * Gets the application context to be used for obtaining the verticles.
     * @return The application context
     */
    private val applicationContext: ApplicationContext
        private get() = ApplicationContextProvider.applicationContext
            ?: throw IllegalStateException(
                "No Application Context Instance has been "
                        + "set in ApplicationContextProvider."
            )

    companion object {
        const val PREFIX = "spring"
    }
}

object ApplicationContextProvider {
    private var appCtx: ApplicationContext? = null

    /**
     * Sets the annotated-configuration class to be used for the Application Context. It creates
     * an [ AnnotationConfigApplicationContext][org.springframework.context.annotation.AnnotationConfigApplicationContext] using the provided annotated-configuration class.
     * @param annotatedConfigClass The class of the annotated-configuration
     */
    @Synchronized
    fun setConfigurationClass(annotatedConfigClass: Class<*>?) {
        Objects.requireNonNull(annotatedConfigClass, "Annotated Configuration class is required")
        appCtx = AnnotationConfigApplicationContext(annotatedConfigClass)
    }
    /**
     * Returns the actual application context
     * @return The actual application context
     */
    /**
     * Sets the actual application context that this provider will return.
     * @param appCtx The actual application context to be provided byt this class.
     */
    @get:Synchronized
    @set:Synchronized
    var applicationContext: ApplicationContext?
        get() = appCtx
        set(appCtx) {
            Objects.requireNonNull(appCtx, "Application Context is required")
            ApplicationContextProvider.appCtx = appCtx
        }
}