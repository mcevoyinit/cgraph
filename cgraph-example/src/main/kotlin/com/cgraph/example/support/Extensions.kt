package com.cgraph.example.support

import com.cgraph.core.services.CGraphService
import com.cgraph.core.services.GraphQLRequestType

fun CGraphService.queryCurrencyIdByIsoCode(isoCode: String) =
    performGraphQLRequest(
        graphQlSelection = """
         query {
            queryCurrency(filter: {
              isoCode: { eq: "$isoCode" }
            }) {
              id
            }
        }
    """.trimIndent(),
        graphQLRequestType = GraphQLRequestType.QUERY
)

fun CGraphService.queryCurrencyIdByName(name: String) =
    performGraphQLRequest(
        graphQlSelection = """
        query {
           queryCurrency(filter : { name: { eq: "$name" } }) {
              id
            }
        }
    """.trimIndent(),
        graphQLRequestType = GraphQLRequestType.QUERY
    )

fun CGraphService.queryBalanceIdForCurrencyId(currencyId: String) =
    performGraphQLRequest(
        graphQlSelection = """
        query {
          queryBalance {
            currency(filter: { id: { eq: "$currencyId" } }) {
                id
            }
            id
          }
        }
    """.trimIndent(),
        graphQLRequestType = GraphQLRequestType.QUERY
    )

fun CGraphService.memberIdForX500Name(nodeName: String) =
    performGraphQLRequest(
        graphQlSelection = """
        query {
           queryMember(filter: { cordaNodeName: { eq: "$nodeName" } }) {
              id
            }   
        }
    """.trimIndent(),
        graphQLRequestType = GraphQLRequestType.QUERY
    )

fun CGraphService.findPartyForMemberCordaNodeName(nodeName: String) {
    performGraphQLRequest(
        graphQlSelection = """
        query {
           queryMember(filter: { cordaNodeName: { eq: "$nodeName" } }) {
              id
            }   
        }
    """.trimIndent(),
        graphQLRequestType = GraphQLRequestType.QUERY
    )
}