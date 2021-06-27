package com.cgraph.core.services

import com.cgraph.core.client.GQLClient
import com.cgraph.core.client.GraphQLResponse
import com.cgraph.core.mutations.GraphQLMutationGenerator
import com.cgraph.core.states.GraphableState
import com.cgraph.core.support.MapOfMaps
import net.corda.core.node.AppServiceHub
import net.corda.core.node.ServiceHub
import net.corda.core.node.services.CordaService
import net.corda.core.serialization.SingletonSerializeAsToken
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import rx.Subscription

/**
 * [CGraphService] is the heart of cgraph.
 *
 * This service detects new ledger entries of type GraphableState
 * and transforms them by passing the result of the buildEntityMap() function into the mutation generator.
 *
 * [GraphQLMutationGenerator] generates a mutation based on the shape of the provided state property map.
 *
 * [GQLClient] is then used to write the mutation over HTTP to the graph.
 *
 * Further enhancements to come around two phased commits used [GraphableState] queryable schema
 */
@CordaService
class CGraphService(serviceHub: AppServiceHub) : SingletonSerializeAsToken() {

    private val nodeOrganisation = serviceHub.myInfo.legalIdentities.first().name.organisation
    private val vaultUpdateSubscription: Subscription by lazy { registerRawUpdatesSubscription(serviceHub) }
    private val graphQLMutationGenerator: GraphQLMutationGenerator = GraphQLMutationGenerator()
    private val graphQLUrl: String by lazy { getGraphQLURL(serviceHub) }
    private val graphQLToken: String by lazy { getGraphQLToken(serviceHub)}
    private val graphQlClient: GQLClient by lazy { GQLClient(graphQLUrl)}

    init {
        // Start post node failure with loss of update
        // 1. Query table queue
        // 2. Find GraphableStates with graphable = false
        // 3. Check DGraph for ID, if there, mark [graphed] = true
        // 4. If not in DGraph, generate Mutation and write to DGraph
        // 5. Marked graphed = true
        logger.info("Starting the CGraph Service for '$nodeOrganisation'.")
        vaultUpdateSubscription
        try {
            logger.info("CGraph ($nodeOrganisation) starting targeting URL $graphQLUrl")
        } catch (e: Exception) {
            logger.error("CGraph ($nodeOrganisation) has failed to start.", e)
            throw e
        } finally {
            logger.info("CGraph ($nodeOrganisation) has started.")
        }
    }

    private fun registerRawUpdatesSubscription(serviceHub: ServiceHub): Subscription  {
        logger.debug("Registering CGraph Service ($nodeOrganisation) for Vault Raw Updates.")
        return serviceHub.vaultService.rawUpdates.subscribe { vaultUpdate ->
            val graphables = vaultUpdate.produced
                .filter { (it.state.data is GraphableState)}
                .map { state ->
                    val toGraph = (state.state.data as GraphableState)
                    graphQLMutationGenerator.processStates(toGraph.buildEntityMap())
                }
            if (graphables.isNotEmpty()) {
                graphables.forEach { mutation ->
                    var request: GraphQLResponse? = null
                    try {
                        request = graphQlClient.request(graphQLToken, mutation)
                    } catch (ex: Exception) {
                        logger.info("GraphQL request failed: $ex")
                    } finally {
                        logger.info("GraphQL request success for $request")
                    }
                }
            }
        }
    }

    private fun getGraphQLURL(serviceHub: ServiceHub): String {
        val url= serviceHub.getAppContext().config.get("graphQLUrl") as String
        logger.info("Loaded GraphQL URL: $url from cordapp config")
        return url
    }

    private fun getGraphQLToken(serviceHub: ServiceHub): String {
        val token = serviceHub.getAppContext().config.get("graphQLToken") as String
        logger.info("Loaded GraphQL token: $token from cordapp config")
        return token
    }

    fun performGraphQLRequest(graphQlSelection: String): MapOfMaps? {
        return graphQlClient.request(graphQLToken, graphQlSelection).let {
            it.errors?.let { errors ->
                error("Errors occurred when querying the graph $errors")
            }
            it.data?.let { data ->
                // TODO handle values empty case
                return data.values.first().toTypedArray().first()
            }
        }
    }

    fun performDropDataRequest() : Int {
        return graphQlClient.dropData(graphQLToken)
    }

    companion object {
        private val logger: Logger = LoggerFactory.getLogger(CGraphService::class.java)
        //val DGRAPH_SCHEMA = getResourceAsText("schema.graphql")
    }
}
