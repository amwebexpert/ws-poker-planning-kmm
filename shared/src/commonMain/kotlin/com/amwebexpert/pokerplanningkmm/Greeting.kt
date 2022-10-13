package com.amwebexpert.pokerplanningkmm

import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.client.statement.*

class Greeting {
    private val platform: Platform = getPlatform()
    private val client = HttpClient()

    fun greeting(): String {
        return "Hello, ${platform.name}!"
    }

    suspend fun greetingRemote(): String {
        val response = client.get("https://ktor.io/docs/")
        return response.bodyAsText()
    }
}
