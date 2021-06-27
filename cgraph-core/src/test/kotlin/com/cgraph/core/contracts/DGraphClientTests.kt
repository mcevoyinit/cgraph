package com.cgraph.core.contracts

import com.cgraph.core.states.GraphableState
import com.google.gson.Gson
import org.junit.Test

class DGraphClientTests {

   /* @Test
    fun `dgraph corda client connects and uploads schema`() {
        val dgraphClient = DGraphGRPCClient(
            schema = String.format(DGRAPH_SCHEMA),
            url = DGRAPH_LOCAL_URL,
            port = DGRAPH_LOCAL_PORT
        )
        print("DGraph Client Connected")
    }

    @Test
    fun `dgraph client connects and uploads schema`() {
        val dgraphClient = DGraphGRPCClient(
            schema = String.format(DGRAPH_SCHEMA),
            url = DGRAPH_LOCAL_URL,
            port = DGRAPH_LOCAL_PORT
        )
        print("DGraph Client Connected")
        dgraphClient.uploadSchema(DGRAPH_SCHEMA)
        dgraphClient.query("","")
    }

    @Test
    fun `dgraph corda client connects and upserts currency`() {

        val dgraphClient = DGraphGRPCClient(
            schema = String.format(DGRAPH_SCHEMA),
            url = DGRAPH_LOCAL_URL,
            port = DGRAPH_LOCAL_PORT
        )
        print("DGraph Client Connected")
        val mutation = """
            mutation {
              addCurrency(input: [
                  {
                      id: "currency1"
                      name: "Pound Sterling"
                      isoCode: "GBP"
                  },
                	{
                      id: "currency2"
                      name: "US Dollars"
                      isoCode: "USD"
                  },
                	{
                      id: "currency3"
                      name: "Euro"
                      isoCode: "EUR"
                  },
               	  {
                      id: "currency4"
                      name: "Singpore Dollar"
                      isoCode: "SGP"
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
        //dgraphClient.mutate(mutation)
    }*/

    val DGRAPH_URL = "dawn-sky.grpc.eu-west-1.aws.cloud.dgraph.io"
    val DGRAPH_PORT = 443
    val DGRAPH_LOCAL_URL = "localhost"
    val DGRAPH_LOCAL_PORT = 9082
    val DGRAPH_SCHEMA =
        """
            type IOU {
              id: String! @id
              value: String!
              currency: Currency!
              lender: Member!
              borrower: Member!
            }

            type Member {
              id: String! @id
              cordaNodeName: String
              borrowed: [IOU] @hasInverse(field: "borrower")
              lended: [IOU] @hasInverse(field: "lender")
              balances: [Balance] @hasInverse(field: "holder")
            }

            type Balance {
              id: String! @id
              holder: Member,
              value: String
              currency: Currency
            }

            type Currency {
              id: String! @id
              name: String,
              isoCode: String
            }
        """.trimIndent()
}

fun main(args: Array<String>) {
    val json = """{"title": "Kotlin Tutorial #1", "author": "bezkoder", "categories" : ["Kotlin","Basic"]}"""
    val gson = Gson()

    val tutorial_1: GraphableState = gson.fromJson(json, GraphableState::class.java)
    println("> From JSON String:\n" + tutorial_1)

   // val tutorial_2: GraphableState = gson.fromJson(GraphableState("tutorial.json"), GraphableState::class.java)
    /* tutorial.json
    {
        "title": "Kotlin Tutorial #2",
        "author": "bezkoder",
        "categories": [
            "Kotlin",
            "Basic"
        ],
        "dummy": "dummy text"
    }
    */
   // println("> From JSON File:\n" + tutorial_2)
}