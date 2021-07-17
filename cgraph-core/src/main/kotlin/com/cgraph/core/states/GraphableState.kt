package com.cgraph.core.states

import com.cgraph.core.support.MapOfMaps
import net.corda.core.contracts.LinearState
import net.corda.core.serialization.CordaSerializable

@CordaSerializable
interface GraphableState : LinearState {
    /**
     * @return [MapOfMaps] representation of the implementing contract state.
     * This map is used by CGraph to generate mutations in order to write persisted states to the graph db instance.
     * Future enhances may offer more niche annotation support to better track relationships across state model and graph entities.
     */
    fun buildEntityMap(): MapOfMaps
}
