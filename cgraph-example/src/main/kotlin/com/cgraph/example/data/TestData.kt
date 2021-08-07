package com.cgraph.example.data

fun UPSERT_MEMBER_GQL(id: String, cordaNodeName: String) =
"""
    mutation {
      addMember(input: [{
        id: "$id"
        cordaNodeName: "$cordaNodeName"
        lended: []
        borrowed: []
        balances: []
      }]) {
    member {
      id
      cordaNodeName
      lended {
        id
      }
      borrowed {
        id
      }
      balances {
        id
      }
    }
  }
}
""".trimIndent()

fun QUERY_CURRENCY_BY_ISO(isoCode: String) =
"""
    query {
      queryCurrency(filter: {
        isoCode: { eq: $isoCode }
      }) {
        id
      }
    }
""".trimIndent()