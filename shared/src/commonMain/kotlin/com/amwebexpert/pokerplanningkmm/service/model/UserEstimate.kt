package com.amwebexpert.pokerplanningkmm.service.model

import kotlinx.serialization.Serializable

@Serializable
data class UserEstimate(val username: String, val estimate: String? = "", val estimatedAtISO8601: String? = "")
