package com.amwebexpert.pokerplanningkmm.service.model

import com.amwebexpert.pokerplanningkmm.service.model.UserEstimate
import kotlinx.serialization.Serializable

@Serializable
data class PokerPlanningSession(val version: String, val lastUpdateISO8601: String, val estimates: List<UserEstimate>)
