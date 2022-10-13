package com.amwebexpert.pokerplanningkmm

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform