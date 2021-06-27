package com.cgraph.example.sample

import com.cgraph.core.services.CGraphService

fun CGraphService.queryCurrencyIdByIsoCode(isoCode: String) =
    performGraphQLRequest("""
         query {
            queryCurrency(filter: {
              isoCode: { eq: "$isoCode" }
            }) {
              id
            }
        }
    """.trimIndent()
)

fun CGraphService.queryCurrencyIdByName(name: String) =
    performGraphQLRequest("""
        query {
           queryCurrency(filter : { name: { eq: "$name" } }) {
              id
            }
        }
    """.trimIndent()
    )

fun CGraphService.queryBalanceIdForCurrencyId(currencyId: String) =
    performGraphQLRequest("""
        query {
          queryBalance {
            currency(filter: { id: { eq: "$currencyId" } }) {
                id
            }
            id
          }
        }
    """.trimIndent()
    )

fun CGraphService.memberIdForX500Name(nodeName: String) =
    performGraphQLRequest("""
        query {
           queryMember(filter: { cordaNodeName: { eq: "$nodeName" } }) {
              id
            }   
        }
    """.trimIndent()
    )

fun CGraphService.findPartyForMemberCordaNodeName(nodeName: String) {
    val cordaX500Name = performGraphQLRequest("""
        query {
           queryMember(filter: { cordaNodeName: { eq: "$nodeName" } }) {
              id
            }   
        }
    """.trimIndent()
    )
}