package com.cgraph.core.flows

import co.paralleluniverse.fibers.Suspendable
import com.cgraph.core.support.graphService
import com.cgraph.core.support.uuid
import net.corda.core.flows.FlowLogic
import net.corda.core.flows.InitiatingFlow
import net.corda.core.flows.StartableByService
import net.corda.core.utilities.ProgressTracker
import java.util.*

/**
 * Flow to perform requests to GraphQL endpoint specified in cordapp config.
 */
@InitiatingFlow
@StartableByService
class PostGraphQLFlow(val graphQL: String) : FlowLogic<UUID?>() {
    override val progressTracker = ProgressTracker()

    @Suspendable
    override fun call() : UUID? {
        val graphService = serviceHub.graphService()
        val response = graphService.performGraphQLRequest(graphQL)?.uuid()
        return response
    }
}
