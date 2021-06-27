package com.cgraph.core.mutations

import com.cgraph.core.states.GraphableState
import com.cgraph.core.support.MapOfMaps
import com.cgraph.core.support.property
import net.corda.core.contracts.ContractState
import net.corda.core.contracts.StateAndRef
import java.util.*

/**
 * generates a mutation based on the shape of the provided [GraphableState] entity property map.
 * Enhancement is required but it can detect, based on the presence of UUID type,
 * if a nested mutation is needed, in order to write a separate entity.
 */
class GraphQLMutationGenerator {

    fun processStates(entity: MapOfMaps) : String {
        val type = entity.property("entityType") as String
        val keys = entity.keys
        val values = entity.values
        val mutationEntries = generateMutationsEntries(keys, values)

        return """
            mutation {
              add$type(input: [
                {
                    ${mutationEntries}
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