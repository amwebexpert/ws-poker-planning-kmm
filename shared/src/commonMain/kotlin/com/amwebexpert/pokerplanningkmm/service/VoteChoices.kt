package com.amwebexpert.pokerplanningkmm.service

data class VoteChoices(val choices: List<String>) {
    override fun toString(): String = this.choices.joinToString(", ")
}
