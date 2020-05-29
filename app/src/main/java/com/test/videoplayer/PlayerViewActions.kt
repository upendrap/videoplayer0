package com.test.videoplayer

sealed class PlayerViewActions {
    data class Initialize(val elapsedTime: Int = 1) : PlayerViewActions()
    data class Play(val elapsedTime: Int = 1) : PlayerViewActions()
    object Stop : PlayerViewActions()
    object Pause : PlayerViewActions()
    object AlertnessCheck : PlayerViewActions()
}