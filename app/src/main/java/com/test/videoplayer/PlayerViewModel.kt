package com.test.videoplayer

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope

//seconds
const val AWARENESS_TIMEOUT = 60_000L

class PlayerViewModel(val userAlertnessTracker: UserAlertnessTracker = UserAlertnessTracker()) :
    ViewModel(),
    PlayerUserActions,
    UserAlertnessCheck {
    private var mCurrentPosition = 1
    private var isPlaying: Boolean = false
    private var isPaused: Boolean = false
    private var isInitialized: Boolean = false
    private val mutableLiveData =
        MutableLiveData<PlayerViewActions>()
    val playerViewActions: LiveData<PlayerViewActions> = mutableLiveData

    override fun start() {
        mutableLiveData.postValue(PlayerViewActions.Initialize(elapsedTime = mCurrentPosition))
    }

    override fun initialize() {
        isInitialized = true
        if (isPlaying)
            onPlay()
    }

    override fun onPlay() {
        mutableLiveData.postValue(PlayerViewActions.Play(elapsedTime = mCurrentPosition))
        isPlaying = true
        isPaused = false
        userAlertnessTracker.playbackStarted(scope = viewModelScope, listener = this)
    }

    override fun onPause(elapsedTime: Int, userPaused: Boolean) {
        if (isPlaying) {
            userAlertnessTracker.playbackPaused()
            if (userPaused) {
                isPlaying = false
                isPaused = true
            }
            mCurrentPosition = elapsedTime
            mutableLiveData.postValue(PlayerViewActions.Pause)
        }
    }

    override fun onStop() {
        userAlertnessTracker.playbackPaused()
        isPlaying = false
        isPaused = false
        isInitialized = false
        mCurrentPosition = 1
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