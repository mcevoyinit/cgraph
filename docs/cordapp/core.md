The core lib. Inside this "CorDapp middleware", there are various classes and services aimed to do as much work as possible for you, the user CorDapp.
 It contains:

## GraphableState

 This is the translation point for the Corda LDM and the Graph LDM. It is used solely by CGraph under the hood to perform a commit to the graph.
User states should implement this, and it's member function, the `buildEntityMap()` function.
 This function returns a property map which the below service will use to generate mutations for the graph.
  Not all state properties need to be in here, only those needed to build a coherent mutation which aligns to the state’s corresponding schema entity entry, in the graph. 
  
```kotlin
interface GraphableState : LinearState,  QueryableState {
    /**
     * @return [MapOfMaps] representation of the implementing contract state.
     * This map is used by CGraph to generate mutations in order to write persisted states to the graph db instance.
     * Future enhances may offer more niche annotation support to better track relationships across state model and graph entities.
     */
    fun buildEntityMap(): MapOfMaps
}
```

## CGraphService
 This is the heart of CGraph. This service detects new ledger entries of type `GraphableState` and transforms them by passing the result of the `buildEntityMap()` function into the generator.
 Here's a snippet of the core function.
 
```
private fun registerGraphableUpdatesSubscription(serviceHub: ServiceHub): Subscription  {
        logger.debug("Registering CGraph Service ($nodeOrganisation) for Vault Raw Updates.")
        return serviceHub.vaultService.rawUpdates.subscribe { vaultUpdate ->
            val graphables = vaultUpdate.produced
                .filter {
                    (it.state.data is GraphableState)
                }
                .map { state ->
                    val toGraph = (state.state.data as GraphableState)
                    if(vaultUpdate.consumed
                            .filter {
                                it.state.data is GraphableState
                             }
                            .any { graphableInputs ->
                                toGraph.linearId.id == (graphableInputs.state.data as GraphableState).linearId.id 
                            }
                    ) {
                        graphQLMutationGenerator.processStates(toGraph.buildEntityMap(), TransactionType.GENERAL)
                    }
                    else {
                        graphQLMutationGenerator.processStates(toGraph.buildEntityMap(), TransactionType.ISSUANCE)
                    }
                }
            if (graphables.isNotEmpty()) {
                graphables.forEach { mutation ->
                    var request: MapOfMaps? = null
                    try {
                        request = performGraphQLRequest(mutation, GraphQLRequestType.MUTATION)
                    } catch (ex: Exception) {
                        logger.info("GraphQL request failed: $ex")
                    } finally {
                        logger.info("GraphQL request success for ${request}")
                    }
                }
            }
        }
    }
```

## GraphQLMutationGenerator 
 This class generates a mutation based on the shape of the provided state property map. There are two types of mutations supported currenty
    * Write mutations. These are generated for issued output `GraphableState`s which do not have any inputs.
    * Update mutations. If a `GraphableState` is transacted from an in input state into a new output state, this class will generate an update mutation, filtering based on the linearid, updating the old graph entry corresponding to the input state with the new fields on the output state. 
 
 The generator can detect based on the presence of UUID type if a nested mutation is needed to write a separate entity. Similarly if a field is empty will setup the mutation to submit an empty entry. 
 These mutations must conform with the schema of course. The samples should illustrates a multi state transaction being written to graph.

## GQLClient
 This is a vanilla GraphQL HTTP client that uses OKHttp. The above service uses this to write mutations over HTTP to the graph.
  At the moment, the unique id between states and graph objects is linearid. We can mark a states corresponding id entry in the schema with the `@id` annotation.
  The graph will those be idempotent to duplicate writes. Handling success and failure will come later via `PersistentGraphableState`