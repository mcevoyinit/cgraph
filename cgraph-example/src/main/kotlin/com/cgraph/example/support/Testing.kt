package com.cgraph.example.support

import com.cgraph.core.flows.PostGraphQLFlow
import com.cgraph.core.services.CGraphService
import com.cgraph.core.services.GraphQLRequestType
import net.corda.core.messaging.startFlow
import net.corda.core.utilities.getOrThrow
import net.corda.testing.driver.InProcess
import net.corda.testing.driver.NodeHandle
import kotlin.test.assertNotNull

fun InProcess.verifyMember(service: CGraphService, x500Name: String) {
    val node = rpc.startFlowDynamic(PostGraphQLFlow::class.java,
        """
        query {
           queryMember(
                filter: { 
                    cordaNodeName: { eq: "$x500Name" } 
                }
            ) 
            {
              id
            }
        }
        """.trimIndent(),
        GraphQLRequestType.QUERY
    ).returnValue.getOrThrow()
    assertNotNull(node, "Member $x500Name not present in graph")
}

fun NodeHandle.verifyCurrency(isoCode: String) {
    val node = rpc.startFlowDynamic(PostGraphQLFlow::class.java,
         """
        query {
           queryCurrency(
                filter: { 
                    isoCode: { eq: "$isoCode" } 
                }
            ) 
            {
              id
            }
        }
        """.trimIndent(),
        GraphQLRequestType.QUERY
    ).returnValue.getOrThrow()
    assertNotNull(node, "Currency $isoCode not present in graph")
}

fun NodeHandle.verifyBalance(name: String, isoCode: String, value: Int) {
    rpc.startFlowDynamic(PostGraphQLFlow::class.java,
        """
          query {
            queryBalance(filter: {
                value : { eq : "$value" }
            }) {
             currency(filter: 
                {
                    isoCode: { eq: "$isoCode" },
                and : {
                    name: { eq: "$name" }
                }
              }
            ){
              id
            }
            id
          }
        }
        """.trimIndent(),
        GraphQLRequestType.QUERY
    ).returnValue.getOrThrow().also {
        assertNotNull(it, "Currency $isoCode not present in graph")
    }
}

fun NodeHandle.verifyIOU(name: String, isoCode: String, value: Int) {
    rpc.startFlowDynamic(PostGraphQLFlow::class.java,
        """
          query {
            queryIOU(filter: {
                value : { eq : "$value" }
            }) {
             currency(filter: 
                {
                    isoCode: { eq: "$isoCode" },
                and : {
                    name: { eq: "$name" }
                }
              }
            ){
              id
            }
            id
          }
        }
        """.trimIndent(),
        GraphQLRequestType.QUERY
    ).returnValue.getOrThrow().also {
        assertNotNull(it, "Currency $isoCode not present in graph")
    }
}