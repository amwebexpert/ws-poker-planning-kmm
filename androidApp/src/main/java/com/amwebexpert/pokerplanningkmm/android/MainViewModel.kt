package com.amwebexpert.pokerplanningkmm.android

import android.util.Log
import com.amwebexpert.pokerplanningkmm.ExampleService
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.amwebexpert.pokerplanningkmm.service.PokerPlanningService
import com.amwebexpert.pokerplanningkmm.ws.WebSocketService
import com.amwebexpert.pokerplanningkmm.ws.WsTextMessageListener
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainViewModel: ViewModel() {
    companion object {
        private val TAG = MainViewModel::class.java.simpleName
    }

    private val exampleService get() = ExampleService.instance
    private val webSocketService get() = WebSocketService.instance
    private val pokerPlanningService get() = PokerPlanningService.instance

    val incomingWebSocketMessage = MutableSharedFlow<String>()
    var isReadyToCommunicate = false

    val httpGetResponse = flow<String> {
        val currentValue = try {
            exampleService.apiCallTextResult()
        } catch (e: Exception) {
            e.localizedMessage ?: "error"
        }

        emit(currentValue)
    }

    fun vote(username: String, value: String) {
        val jsonMessage = pokerPlanningService.buildEstimateMessageAsJson(username= username, estimate = value)
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                webSocketService.sendMessage(text = jsonMessage)
            }
        }
    }

    fun startCommunication() {
        isReadyToCommunicate = true
        viewModelScope.launch {
            withContext(Dispatchers.IO) { connectToWebSocket() }
        }
    }

    fun stopCommunication() {
        isReadyToCommunicate = false
        viewModelScope.launch {
            withContext(Dispatchers.IO) { webSocketService.disconnect() }
        }
    }

    private suspend fun connectToWebSocket() {
        webSocketService.connect(
            hostname = "ws-poker-planning.herokuapp.com",
            roomUUID = "e78caaee-a1a2-4298-860d-81d7752226ae",
            listener = object : WsTextMessageListener {
                override fun onConnectSuccess() {
                    Log.i(TAG, "onConnectSuccess")
                }

                override fun onConnectFailed() {
                    Log.w(TAG, "onConnectFailed, nothing to do because the onClose already handle reconnections")
                }

                override fun onClose() {
                    Log.w(TAG, "onClose")

                    if (!isReadyToCommunicate) {
                        return
                    }

                    viewModelScope.launch {
                        withContext(Dispatchers.IO) { connectToWebSocket() }
                    }
                }

                override fun onMessage(text: String) {
                    Log.i(TAG, "onMessage:\n\t $text")

                    viewModelScope.launch {
                        withContext(Dispatchers.IO) { incomingWebSocketMessage.emit(text) }
                    }
                }
            })
    }
}