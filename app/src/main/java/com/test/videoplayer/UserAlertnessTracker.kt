package com.test.videoplayer

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class UserAlertnessTracker {
    private lateinit var job: Job
    fun startPlaying(scope: CoroutineScope, listener: UserAlertness.AlertnessCheck) {
        job = scope.launch {
            delay(AWARENESS_TIMEOUT)
            listener.check()
        }
    }

    fun stopPlaying() {
        job.cancel()
    }
}