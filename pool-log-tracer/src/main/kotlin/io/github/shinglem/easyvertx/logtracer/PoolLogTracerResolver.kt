package io.github.shinglem.easyvertx.logtracer


import com.github.freva.asciitable.AsciiTable
import com.github.freva.asciitable.Column
import io.vertx.core.json.jackson.DatabindCodec
import io.vertx.sqlclient.Row
import io.vertx.sqlclient.RowSet
import io.vertx.sqlclient.impl.SqlResultImpl
import io.vertx.sqlclient.impl.tracing.QueryRequest
import kotlin.reflect.KClass

class QueryRequestTraceTypeResolver: TraceTypeResolver<QueryRequest> {
    override fun getClass(): KClass<QueryRequest> {
        return QueryRequest::class
    }

    override fun resolve(data: QueryRequest?): String {
        if (data == null) return ""

        return "\n" + """
                        sql => ${data.sql()}
                        tuple => ${data.tuples().map { it.deepToString() }}
                    """.trimIndent()
    }

}
class RowSetTraceTypeResolver: TraceTypeResolver<RowSet<*>> {
    override fun getClass(): KClass<RowSet<*>> {
        return RowSet::class
    }

    override fun resolve(data: RowSet<*>?): String {
        if (data == null) return ""
        if (data.columnsNames() == null) {
            return data.toList().toString()
        }
        val cols = data.columnsNames()
            .map {
                Column().header(it).with<Map<String, Any?>> { map -> map[it].toString() }
            }
        val rows = data.map {
            if (it is Row) {
                it.toJson().map
            } else {
                DatabindCodec.mapper().convertValue(it, Map::class.java) as Map<String, Any?>
            }
        }

        val ret = AsciiTable.getTable(rows, cols)
        return """
$ret  
                    """
    }

}
class SqlResultImplTraceTypeResolver: TraceTypeResolver<SqlResultImpl<*>> {
    override fun getClass(): KClass<SqlResultImpl<*>> {
        return SqlResultImpl::class
    }

    override fun resolve(data: SqlResultImpl<*>?): String {
        if (data == null) return ""
        val cols = listOf(
            Column().header("rowCount").with<Map<String, Any?>> { map -> map["rowCount"].toString() }
        )
        val rows = listOf(mapOf("rowCount" to data.rowCount()))

        val ret = AsciiTable.getTable(rows, cols)
        return """
$ret  
                    """
    }

}
