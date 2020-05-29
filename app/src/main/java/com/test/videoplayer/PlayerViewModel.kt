package com.test.videoplayer

import androidx.annotation.VisibleForTesting
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope

//seconds
const val AWARENESS_TIMEOUT = 60_000L

class PlayerViewModel(private val userAlertnessTracker: UserAlertnessTracker = UserAlertnessTracker()) :
    ViewModel(),
    PlayerUserActions,
    UserAlertnessCheck {
    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    var elapsedTimeMilliseconds = 1

    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    var isPlaying: Boolean = false

    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    var isPaused: Boolean = false

    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    var isInitialized: Boolean = false

    private val mutableLiveData =
        MutableLiveData<PlayerViewActions>()
    val playerViewActions: LiveData<PlayerViewActions> = mutableLiveData

    override fun start() {
        mutableLiveData.postValue(PlayerViewActions.Initialize(elapsedTime = elapsedTimeMilliseconds))
    }

    override fun initialize() {
        isInitialized = true
        if (isPlaying)
            onPlay()
    }

    override fun onPlay() {
        mutableLiveData.postValue(PlayerViewActions.Play(elapsedTime = elapsedTimeMilliseconds))
        isPlaying = true
        isPaused = false
        userAlertnessTracker.playbackStarted(scope = viewModelScope, listener = this)
    }

    override fun onPause(elapsedTime: Int, userPaused: Boolean) {
        if (isPlaying) {
            userAlertnessTracker.playbackStopped()
            //when device orientation changes we want playback to resume so dont update
            if (userPaused) {
                isPlaying = false
                isPaused = true
            }
            elapsedTimeMilliseconds = elapsedTime
            mutableLiveData.postValue(PlayerViewActions.Pause)
        }
    }

    override fun onStop() {
        userAlertnessTracker.playbackStopped()
        isPlaying = false
        isPaused = false
        isInitialized = false
        elapsedTimeMilliseconds = 1
        mutableLiveData.postValue(PlayerViewActions.Stop)
    }

    override fun confirmAlert(isAlert: Boolean) {
        if (isAlert)
            onPlay()
        else {
            onStop()
        }
    }

    override fun onDeviceFlipped(faceUp: Boolean, elapsedTime: Int) {
        if (isInitialized and isPaused and faceUp)
            onPlay()
        else if (isInitialized and isPlaying and !faceUp)
            onPause(elapsedTime = elapsedTime)
    }

    override fun check() {
        mutableLiveData.postValue(PlayerViewActions.AlertnessCheck)
    }
}