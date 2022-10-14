package com.amwebexpert.pokerplanningkmm

import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import kotlinx.coroutines.delay
import kotlinx.datetime.Clock

class Greeting {
    private val platform: Platform = getPlatform()
    private val client = HttpClient()

    fun greeting(): String {
        return "Hello, ${platform.name}. Now is ${Clock.System.now()}"
    }

    suspend fun greetingRemote(): String {
        delay(2000)
        val response = client.get("https://ktor.io/docs/")
        return response.bodyAsText()
    }
}
