package com.cgraph.core.mutations

import com.cgraph.contract.GraphableState
import com.cgraph.contract.MapOfMaps
import com.cgraph.contract.property
import com.cgraph.contract.remove
import net.corda.core.contracts.ContractState
import net.corda.core.contracts.StateAndRef
import java.util.*

/**
 * Generates a mutation based on the shape of the provided [GraphableState] entity property map.
 * Enhancement is required but it can detect, based on the presence of UUID type,
 * if a nested mutation is needed, in order to write a separate entity.
 *
 * Also supports update mutations.
 * [CGraphService] will detect if an update is needed based on if the input state matches to an output state
 */
class GraphQLMutationGenerator {

    fun processStates(entity: MapOfMaps, txnType: TransactionType) : String {
        return when(txnType) {
            TransactionType.GENERAL -> generateUpsertMutation(entity)
            TransactionType.ISSUANCE -> generateInsertMutation(entity)
        }
    }

    private fun generateUpsertMutation(entity: MapOfMaps) : String {
        val type = entity.property("entityType") as String
        var mapForMutation = entity.remove("entityType").toMutableMap()
        val filter = """
            id : { 
                eq : "${mapForMutation["id"]}" 
            }
            """.trimIndent()
        //mapForMutation = 0
        mapForMutation.remove("id") //as MutableMap<String, Any>
        var set = ""
        mapForMutation.forEach {
            set+= "${it.key} : \"${it.value}\" \n"
        }
        // Core mutation kv mappings
        var mutationEntries = ""
        (mapForMutation.keys).zip(mapForMutation.values).forEach { pair ->
            if(pair.first == "entityType") {
                mutationEntries += ""
            } else {
                mapForMutation[pair.first] = pair.second
                mutationEntries += when (pair.second) {
                    is UUID -> {
                        "${(pair.first)} : { id: \"${pair.second}\" }"
                    }
                    null -> {
                        "${(pair.first)}: [] \n"
                    }
                    else -> {
                        "${(pair.first)}: \"${pair.second}\" \n"
                    }
                }
            }
        }

        return """
                mutation {
                  update$type(input: 
                    {
                        filter : {
                            $filter
                        },
                        set : {
                            $mutationEntries
                          }
                        }
                       )
                    } {
                    ${type.decapitalize()} {
                        id
                    }
                }
        """.trimIndent()
    }

    private fun generateInsertMutation(entity: MapOfMaps) : String {
        val type = entity.property("entityType") as String
        val mapForMutation = entity.remove("entityType").toMutableMap()

        // Core mutation kv mappings
        var mutationEntries = ""
        (mapForMutation.keys).zip(mapForMutation.values).forEach { pair ->
            if(pair.first == "entityType") {
                mutationEntries += ""
            } else {
                mapForMutation[pair.first] = pair.second
                mutationEntries += when (pair.second) {
                    is UUID -> {
                        "${(pair.first)} : { id: \"${pair.second}\" }"
                    }
                    null -> {
                        "${(pair.first)}: [] \n"
                    }
                    else -> {
                        "${(pair.first)}: \"${pair.second}\" \n"
                    }
                }
            }
        }
        return """
                mutation {
                  add$type(input: [
                    {
                        $mutationEntries
                    }
                  ]) {
                    ${type.decapitalize()} {
                         id
                       }
                    }
                  }
            """.trimIndent()
    }

    private fun generateMutationsEntries(keys: Set<String>, values: Collection<Any>): String {
        val map = mutableMapOf<String, Any>()
        map.remove("entityType")
        var mutationEntries = ""
        keys.zip(values).forEach { pair ->
            if(pair.first == "entityType") {
                mutationEntries += ""
            } else {
                map[pair.first] = pair.second
                mutationEntries += when (pair.second) {
                    is UUID -> {
                        "${(pair.first)} : { id: \"${pair.second}\" }"
                    }
                    null -> {
                        "${(pair.first)}: [] \n"
                    }
                    else -> {
                        "${(pair.first)}: \"${pair.second}\" \n"
                    }
                }
            }
        }
        return mutationEntries
    }

    private fun generateCordaTxnMutationHeaders(stateAndRef: StateAndRef<ContractState>): MapOfMaps {
        val stateRef = mapOf("txhash" to stateAndRef.ref.txhash.bytes, "index" to stateAndRef.ref.index)
        val txnMap = mapOf("_stateref" to stateRef)
        val state = stateAndRef.state.data
        val mapDataState = when (state) {
            is GraphableState -> state.buildEntityMap()
            else -> throw IllegalArgumentException("Only GraphableState is supported")
        }
        return mapDataState.plus(txnMap).also {
            println("Txn Map: $stateAndRef -> $it")
        }
    }
}

enum class TransactionType { ISSUANCE, GENERAL }

