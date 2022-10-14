package com.amwebexpert.pokerplanningkmm

import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import kotlinx.coroutines.delay
import kotlinx.datetime.Clock

class ExampleService private constructor () {
    companion object {
        val instance = ExampleService()
    }

    private val platform: Platform = getPlatform()
    private val client = HttpClient()

    fun getPlatformAndDate(): String {
        return "Platform: [${platform.name}]. Now (ISO-8601): [${Clock.System.now()}]"
    }

    suspend fun apiCallTextResult(): String {
        delay(2000)
        val response = client.get("https://ktor.io/docs/")
        return response.bodyAsText()
    }
}
