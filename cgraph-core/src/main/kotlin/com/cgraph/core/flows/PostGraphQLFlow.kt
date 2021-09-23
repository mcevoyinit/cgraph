package com.cgraph.core.flows

import co.paralleluniverse.fibers.Suspendable
import com.cgraph.contract.uuid
import com.cgraph.core.services.GraphQLRequestType
import com.cgraph.core.services.graphService
import net.corda.core.flows.FlowLogic
import net.corda.core.flows.InitiatingFlow
import net.corda.core.flows.StartableByRPC
import net.corda.core.flows.StartableByService
import net.corda.core.utilities.ProgressTracker
import java.util.*

/**
 * Flow to perform requests to GraphQL endpoint specified in CorDapp config.
 */
@InitiatingFlow
@StartableByService
@StartableByRPC
class PostGraphQLFlow(val graphQL: String, val graphQLRequestType: GraphQLRequestType) : FlowLogic<UUID?>() {
    override val progressTracker = ProgressTracker()

    @Suspendable
    override fun call() : UUID? {
        val graphService = serviceHub.graphService()
        return graphService.performGraphQLRequest(graphQL, graphQLRequestType)?.uuid()
    }
}
