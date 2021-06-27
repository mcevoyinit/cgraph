package com.cgraph.core.api

import java.util.*

/**
 * Used at the moment by [DGraphGRPCClient]. It would be interesting to see a standard interface
 * for all external graph reads and writes
 */
interface CGraphClientAPI {
    fun start(schema: String, url: String, port: Int)
    fun mutate(states: List<Map<String, Any>>): Boolean
    fun update(states: List<Map<String, String>>, id: UUID): Boolean
    fun query(entity: String, gqlSelection: String): Collection<Map<String, String>>
}