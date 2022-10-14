package com.amwebexpert.pokerplanningkmm.ws

interface WsTextMessageListener {
    fun onConnectSuccess()
    fun onConnectFailed()
    fun onClose()
    fun onMessage(text: String)
}
