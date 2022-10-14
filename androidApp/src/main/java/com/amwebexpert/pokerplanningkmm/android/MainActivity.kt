package com.amwebexpert.pokerplanningkmm.android

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.amwebexpert.pokerplanningkmm.ExampleService

import androidx.compose.runtime.*
import androidx.compose.ui.unit.dp
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
                    val httpGetResponse = viewModel.httpGetResponse.collectAsState(initial = "Loading...")
                    val incomingMessage = viewModel.incomingWebSocketMessage.collectAsState(initial = "N/A")

                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Box() {
                            Text("** Normal sync call result:result\n ${ExampleService.instance.getPlatformAndDate()}")
                        }
                        Box() {
                            Text("** Poker state:\n ${incomingMessage.value}")
                        }
                        Button(onClick = { viewModel.vote("KMM", "8") }) {
                            Text("VOTE")
                        }
                        Box() {
                            Text("** Async result:result:\n ${httpGetResponse.value}")
                        }
                    }
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        viewModel.startCommunication()
    }

    override fun onPause() {
        super.onPause()
        viewModel.stopCommunication()
    }
}

@Preview
@Composable
fun DefaultPreview() {
    MyApplicationTheme {
        Text("Hello, Android!")
    }
}
