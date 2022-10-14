package com.amwebexpert.pokerplanningkmm.android

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.amwebexpert.pokerplanningkmm.Greeting

import androidx.compose.runtime.*
import androidx.lifecycle.ViewModelProvider
import com.plcoding.kotlinflows.ui.theme.MyApplicationTheme

class MainActivity : ComponentActivity() {
    companion object {
        private val TAG = MainActivity::class.java.simpleName
    }

    private lateinit var viewModel: MainViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProvider(this)[MainViewModel::class.java]

        setContent {
            MyApplicationTheme {
                Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colors.background) {
                    val greetingsState = viewModel.greetingsFlow.collectAsState(initial = "Loading...")
                    val incomingMessages = viewModel.incomingWebSocketMessages.collectAsState(initial = "N/A")

                    Column() {
                        Box() {
                            Text("Normal sync call result: '${Greeting().greeting()}'.")
                        }
                        Box() {
                            Text("Async result:result:\n ${greetingsState.value}")
                        }
                        Box() {
                            Text("Async poker state:\n ${incomingMessages.value}")
                        }
                    }
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        viewModel.handleWebSocketsCommunications()
    }

    override fun onPause() {
        super.onPause()
        viewModel.handleWebSocketsDisconnection()
    }
}

@Preview
@Composable
fun DefaultPreview() {
    MyApplicationTheme {
        Text("Hello, Android!")
    }
}
