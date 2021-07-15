package com.cgraph.core.flows

import co.paralleluniverse.fibers.Suspendable
import com.cgraph.core.support.graphService
import net.corda.core.flows.FlowLogic
import net.corda.core.flows.InitiatingFlow
import net.corda.core.flows.StartableByRPC
import net.corda.core.flows.StartableByService
import net.corda.core.utilities.ProgressTracker

/**
 * Drop data flow will remove all data from the DGraph instance at configured URL
 */
@InitiatingFlow
@StartableByService
@StartableByRPC
class DropGraphDataFlow : FlowLogic<Boolean>() {
    override val progressTracker = ProgressTracker()

    @Suspendable
    override fun call(): Boolean {
        val graphService = serviceHub.graphService()
        val success =  graphService.performDropDataRequest()
        return success == 200
    }
}
