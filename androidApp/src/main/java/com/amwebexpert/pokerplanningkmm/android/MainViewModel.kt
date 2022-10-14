package com.amwebexpert.pokerplanningkmm.android

import com.amwebexpert.pokerplanningkmm.Greeting
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.flow

class MainViewModel: ViewModel() {

    val greetingsFlow = flow<String> {
        val currentValue = try {
            Greeting().greetingRemote()
        } catch (e: Exception) {
            e.localizedMessage ?: "error"
        }

        emit(currentValue)
    }
}