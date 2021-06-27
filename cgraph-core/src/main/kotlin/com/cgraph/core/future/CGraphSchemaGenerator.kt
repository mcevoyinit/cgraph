package com.cgraph.core.future

import com.cgraph.core.support.MapOfMaps

/**
 * Place holder interface for generating schema based on state data.
 * This is not the highest priority since the schema is a long lived document.
 * Generating schemas should be done as as guideline, not the final word.
 */
interface CGraphSchemaGenerator {
    fun scanCordapps() : List<Class<*>>?
    fun generateSchema(states: List<Class<*>>?) : MapOfMaps?
}

fun main(args: Array<String>) {
    val dGraphSchemaGenerator = DGraphSchemaGenerator()
    val graphableStates = dGraphSchemaGenerator.scanCordapps()
    val schema = dGraphSchemaGenerator.generateSchema(graphableStates)
    println(schema)
}