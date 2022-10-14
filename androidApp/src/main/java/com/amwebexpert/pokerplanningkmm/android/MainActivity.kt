package com.amwebexpert.pokerplanningkmm.android

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.amwebexpert.pokerplanningkmm.Greeting

import androidx.compose.runtime.*
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import com.amwebexpert.pokerplanningkmm.service.PokerPlanningService
import com.amwebexpert.pokerplanningkmm.service.model.PokerPlanningSession
import com.amwebexpert.pokerplanningkmm.ws.WebSocketService
import com.amwebexpert.pokerplanningkmm.ws.WsTextMessageListener
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colors = if (darkTheme) {
        darkColors(
            primary = Color(0xFFBB86FC),
            primaryVariant = Color(0xFF3700B3),
            secondary = Color(0xFF03DAC5)
        )
    } else {
        lightColors(
            primary = Color(0xFF6200EE),
            primaryVariant = Color(0xFF3700B3),
            secondary = Color(0xFF03DAC5)
        )
    }
    val typography = Typography(
        body1 = TextStyle(
            fontFamily = FontFamily.Default,
            fontWeight = FontWeight.Normal,
            fontSize = 16.sp
        )
    )
    val shapes = Shapes(
        small = RoundedCornerShape(4.dp),
        medium = RoundedCornerShape(4.dp),
        large = RoundedCornerShape(0.dp)
    )

    MaterialTheme(
        colors = colors,
        typography = typography,
        shapes = shapes,
        content = content
    )
}

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
                    val scope = rememberCoroutineScope()
                    var text by remember { mutableStateOf("Loading") }
                    LaunchedEffect(true) {
                        scope.launch {
                            text = try {
                                Greeting().greetingRemote()
                            } catch (e: Exception) {
                                e.localizedMessage ?: "error"
                            }
                        }
                    }
                    Greeting("Normal sync call result: '${Greeting().greeting()}'.\n\n Async result:result:\n $text")
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

@Composable
fun Greeting(text: String) {
    Text(text = text)
}

@Preview
@Composable
fun DefaultPreview() {
    MyApplicationTheme {
        Greeting("Hello, Android!")
    }
}
