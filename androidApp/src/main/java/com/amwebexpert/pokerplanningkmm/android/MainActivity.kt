package com.amwebexpert.pokerplanningkmm.android

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.amwebexpert.pokerplanningkmm.Greeting

import androidx.compose.runtime.*
import androidx.lifecycle.Lifecycle
import com.amwebexpert.pokerplanningkmm.service.PokerPlanningService
import com.amwebexpert.pokerplanningkmm.ws.WebSocketService
import com.amwebexpert.pokerplanningkmm.ws.WsTextMessageListener
import com.plcoding.kotlinflows.ui.theme.MyApplicationTheme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainActivity : ComponentActivity() {
    companion object {
        private val TAG = MainActivity::class.java.simpleName
    }

    private fun isActivityInForeground() = lifecycle.currentState.isAtLeast(Lifecycle.State.RESUMED)
    private val webSocketService get() = WebSocketService.instance
    private val pokerPlanningService get() = PokerPlanningService.instance

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            MyApplicationTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    val viewModel = viewModel<MainViewModel>()
                    val greetingsState = viewModel.greetingsFlow.collectAsState(initial = "Loading...")

                    Column() {
                        Box() {
                            Text("Normal sync call result: '${Greeting().greeting()}'.")
                        }
                        Box() {
                            Text("Async result:result:\n ${greetingsState.value}")
                        }
                    }
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        handleWebSocketsCommunications()
    }

    private fun handleWebSocketsCommunications() {
        lifecycleScope.launch { // or GlobalScope.launch {
            withContext(Dispatchers.IO) {
                connectToWebSocket()
            }
        }
    }

    override fun onPause() {
        super.onPause()
        handleWebSocketsDisconnection()
    }

    private fun handleWebSocketsDisconnection() {
        lifecycleScope.launch { // or GlobalScope.launch {
            withContext(Dispatchers.IO) {
                webSocketService.disconnect()
            }
        }
    }

    private suspend fun connectToWebSocket() {
        webSocketService.connect(
            hostname = "ws-poker-planning.herokuapp.com",
            roomUUID = "e78caaee-a1a2-4298-860d-81d7752226ae",
            listener = object : WsTextMessageListener {

                override fun onConnectSuccess() {
                    Log.i(TAG, "onConnectSuccess")
                    runOnUiThread {
                        //_binding?.btnJoinRoom?.isEnabled = true
                    }
                }

                override fun onConnectFailed() {
                    Log.w(TAG, "onConnectFailed, nothing to do because the onClose already handle reconnections")
                }

                override fun onClose() {
                    Log.w(TAG, "onClose")
                    runOnUiThread {
                        // _binding?.btnJoinRoom?.isEnabled = false
                    }

                    if (isActivityInForeground()) {
                        handleWebSocketsCommunications()
                    }
                }

                override fun onMessage(text: String) {
                    Log.i(TAG, "onMessage:\n\t $text")
                    runOnUiThread {
                        // val session = Json.decodeFromString<PokerPlanningSession>(text)//_binding?.textSocketResponse?.setText(session.toString())
                    }
                }
            })
    }
}

@Preview
@Composable
fun DefaultPreview() {
    MyApplicationTheme {
        Text("Hello, Android!")
    }
}
