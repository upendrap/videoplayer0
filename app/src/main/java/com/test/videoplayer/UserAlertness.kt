package com.test.videoplayer

interface UserAlertness {
    interface AlertnessCheck {
        fun check()
    }

    interface Action {
        fun play()
        fun pause()
        fun resume()
        fun stop()
        fun confirmUserAlertness()
    }

    interface AlertnessResponseListener {
        fun onUserAlert()
        fun onUserNotAlert()
    }
}