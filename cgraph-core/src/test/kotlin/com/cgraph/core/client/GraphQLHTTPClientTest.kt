package com.cgraph.core.client

import org.junit.Test
import java.util.*

class GraphQLHTTPClientTest {

    val DGRAPH_URL = "https://dawn-sky.eu-west-1.aws.cloud.dgraph.io/graphql"
    val DGRAPH__GRPC_PORT = 443
    val DGRAPH_LOCAL_URL = "localhost"
    val DGRAPH_LOCAL_PORT = 9082

    @Test
    fun `perform graphql request with currency query to dgraph`() {
        val currencySelection = """
                query {
                  queryCurrency {
                    id
                    isoCode
                    name
                  }
                }
            """.trimIndent()
       // val request = GraphQLHTTPClient().makeGQLRequest(DGRAPH_URL, currencySelection)
        //val response = request.toString()
        //print("HTTP request: ${response}}")
    }

    @Test
    fun `perform graphql http mutation request to dgraph instance`() {
        val randomId = UUID.randomUUID().toString()
        val mutation = """
            mutation {
              addCurrency(input: [
                  {
                      id: $randomId
                      name: "CGraph Coin"
                      isoCode: "CGC"
                  }
              ]) {
                currency {
                  id
                  name
                  isoCode
                }
              }
            }
        """.trimIndent()
        /*val response = GraphQLHTTPClient().makeGQLRequest(DGRAPH_URL, mutation)!!
        response.data!!.forEach {
            it.value.forEachIndexed { index, map ->
                print("Key: ${it.key} Value: ${it.value.toString()}")
            }
        }*/
        //print("HTTP request: ${response}}")
    }
}