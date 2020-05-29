package com.test.videoplayer

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class UserAlertnessTracker {
    private var job: Job? = null

    fun playbackStarted(scope: CoroutineScope, listener: UserAlertnessCheck) {
        job = scope.launch {
            delay(AWARENESS_TIMEOUT)
            listener.check()
        }
    }

    fun playbackStopped() {
        job?.cancel()
        job = null
    }
}