package com.cgraph.core.future

import com.cgraph.contract.MapOfMaps

/**
 * Placeholder interface for generating schemas based on state data.
 * This is not the highest priority since the schema is a long lived document.
 * Generating schemas should be done as as guideline, not as the final word.
 */
interface CGraphSchemaGenerator {
    fun scanCordapps() : List<Class<*>>?
    fun generateSchema(states: List<Class<*>>?) : MapOfMaps?
}