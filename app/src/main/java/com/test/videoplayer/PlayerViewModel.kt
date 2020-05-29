package com.test.videoplayer

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import java.util.*

//seconds
const val AWARENESS_TIMEOUT = 10_000L

class PlayerViewModel(val userAlertnessTracker: UserAlertnessTracker = UserAlertnessTracker()) :
    ViewModel(),
    PlayerUserActions, UserAlertness.AlertnessCheck {
    var mCurrentPosition = 1
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
        userAlertnessTracker.startPlaying(scope = viewModelScope, listener = this)
    }

    override fun onPause(elapsedTime: Int, userPaused: Boolean) {
        userAlertnessTracker.stopPlaying()
        if (userPaused) {
            isPlaying = false
            isPaused = true
        }
        mCurrentPosition = elapsedTime
        mutableLiveData.postValue(PlayerViewActions.Pause)
    }

    override fun onStop() {
        userAlertnessTracker.stopPlaying()
        isPlaying = false
        isPaused = false
        isInitialized = false
        mCurrentPosition = 1
        mutableLiveData.postValue(PlayerViewActions.Stop)
        start()
    }

    override fun confirmAlert(isAlert: Boolean) {
        if (isAlert)
            onPlay()
        else {
            Log.e("check", "not alert")
            onStop()
        }
    }

    override fun check() {
        Log.e("check", "${Date()}")
        mutableLiveData.postValue(PlayerViewActions.AlertnessCheck)
    }
}