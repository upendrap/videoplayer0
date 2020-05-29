package com.test.videoplayer

interface PlayerUserActions {
    fun start()
    fun initialize()
    fun onPlay()
    fun onPause(elapsedTime: Int = 0, userPaused: Boolean = true)
    fun onStop()
    fun confirmAlert(isAlert: Boolean)
    fun onDeviceFlipped(faceUp: Boolean, elapsedTime: Int)
}