package com.cgraph.core.support

import com.cgraph.core.services.DGraphInstanceMethod
import com.cgraph.core.services.DGraphServiceFactory
import okhttp3.MediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody

/**
 * Drops data from dgraph db instance but not schema
 */
fun dropData(url: String, token: String): Int {
    val client = OkHttpClient()
    val mediaType: MediaType? = MediaType.parse("application/json")
    val body = RequestBody.create(mediaType,
        """
        {"drop_op": "DATA"}
        """.trimIndent())
    val request = Request.Builder()
        .url(url)
        .post(body)
        .addHeader("X-Auth-Token", token)
        .build()

    val response = client.newCall(request).execute()
    return response.code()
}