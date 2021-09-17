package io.github.shinglem.easyvertx.core.spring

import com.fasterxml.jackson.dataformat.javaprop.JavaPropsMapper
import org.springframework.context.ApplicationContext
import org.springframework.core.env.AbstractEnvironment
import org.springframework.core.env.MapPropertySource


fun getConfigMap(ctx: ApplicationContext, configPrefix: String): Map<String, *> {
    val c =  ctx.environment as AbstractEnvironment
    val mergeMap = mutableMapOf<String , Any?>()
    c.propertySources.filterIsInstance<MapPropertySource>()
        .map {
            it.source.filter {
                it.key.startsWith(configPrefix)
            }
        }
        .forEach {
            mergeMap.putAll(it)
        }
    val propstr = mergeMap.map {
        it.key + "=" + it.value
    }.joinToString("\n") ?: ""
    val mapper = JavaPropsMapper()
    val map = mapper.readValue(propstr, Map::class.java) as Map<String, *>
    return map
}
