package com.cgraph.core.client

import com.cgraph.core.support.MapOfMaps
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.KotlinModule
import okhttp3.MediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import java.net.URL
import java.time.Duration

private val GQLBodyMediaType: MediaType = MediaType.get("application/graphql")
private val Timeout = Duration.ofSeconds(200)

/**
 * HTTP Client used for performing request to a GraphQL endpoint.
 *
 * In GraphQL HTTP POST is used for queries and mutations
 */
class GQLClient(private val graphqlEndpoint: String) {

    private val mapper: ObjectMapper = ObjectMapper()
        .registerModule(KotlinModule(
            nullToEmptyMap = true,
            nullToEmptyCollection = true))
        .findAndRegisterModules()
        .configure(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY, true)


    fun request(token: String, request: String): GraphQLResponse {
        val httpRequest = Request.Builder()
            .url(URL("$graphqlEndpoint/graphql"))
            .addHeader("X-Auth-Token", token)
            .post(RequestBody.create(GQLBodyMediaType, request))
            .build()

        return OkHttpClient.Builder()
            .callTimeout(Timeout)
            .readTimeout(Timeout)
            .build()
            .newCall(httpRequest)
            .execute()
            .use { response ->
                response.body()
                    ?.let {
                       mapper.readValue(it.string(), GraphQLResponse::class.java)
                    } ?: throw IllegalStateException("Empty Response Body for: $httpRequest")
            }
    }

    fun dropData(token: String): Int {
        val mediaType: MediaType? = MediaType.parse("application/json")
        val body = RequestBody.create(mediaType,
            """
            {"drop_op": "DATA"}
             """.trimIndent())
        val dropRequest = Request.Builder()
            .url(URL("$graphqlEndpoint/alter"))
            .post(body)
            .addHeader("X-Auth-Token", token)
            .build()

        val response = OkHttpClient.Builder()
            .callTimeout(Timeout)
            .readTimeout(Timeout)
            .build()
            .newCall(dropRequest)
            .execute()
        return response.code()
    }
}

class GraphQLResponse {
    val errors: Any? = null
    val extensions: Any? = null
    val data: Map<String, Collection<MapOfMaps>>? = null
}
