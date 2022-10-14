package com.amwebexpert.pokerplanningkmm.ws

import io.ktor.client.*
import io.ktor.client.plugins.websocket.*
import io.ktor.http.*
import io.ktor.websocket.*
import kotlinx.coroutines.cancel
import kotlinx.coroutines.channels.ClosedReceiveChannelException
import kotlinx.coroutines.runBlocking

class WebSocketService {
    companion object {
        val instance = WebSocketService()

        private const val NORMAL_CLOSURE_STATUS = 1001
    }

    private val postponedMessages: ArrayList<String> = ArrayList()
    private var isConnected = false
    private lateinit var messageListener: WsTextMessageListener
    private val httpClient = HttpClient() {
        install(WebSockets) {
            // pingInterval = 20_000
        }
    }
    private lateinit var webSocketSession: DefaultClientWebSocketSession

    fun connect(hostname: String, roomUUID: String, listener: WsTextMessageListener) {
        if (isConnected) {
            return
        }

        messageListener = listener

        runBlocking {
            httpClient.wss(method = HttpMethod.Get, host = hostname, path = "/ws?roomUUID=$roomUUID") {
                webSocketSession = this
                isConnected = true
                messageListener.onConnectSuccess()

                while (isConnected) {
                    try {
                        flushPostponedMessages()

                        val incomingMessage = incoming.receive() as? Frame.Text
                        val message = incomingMessage?.readText()
                        if (message != null) {
                            messageListener.onMessage(message)
                        }
                    } catch (e: ClosedReceiveChannelException) {
                        isConnected = false
                        messageListener.onClose()
                    }
                }
            }
        }
    }

    suspend fun disconnect() {
        if (isConnected) {
            webSocketSession.cancel()
            webSocketSession.close(CloseReason(code = CloseReason.Codes.NORMAL, "disconnection"))
            isConnected = false
        }
    }

    suspend fun flushPostponedMessages() {
        if (!isConnected) {
            return
        }

        // synchronized(postponedMessages) {
        while (postponedMessages.isNotEmpty()) {
            val msg = postponedMessages.removeAt(0)
            webSocketSession.send(msg)
        }
    }

    suspend fun sendMessage(text: String): Boolean {
        if (!isConnected) {
            postponedMessages.add(text)
            return false
        }

        return try {
            webSocketSession.send(text)
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }
}