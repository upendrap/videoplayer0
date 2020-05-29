package com.test.videoplayer

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorManager
import android.media.MediaPlayer.MEDIA_INFO_VIDEO_RENDERING_START
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    private var mCurrentPosition = 0
    private var isPlaying = false
    private var isInitialized = false
    private var isPaused = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        if (savedInstanceState != null) {
            mCurrentPosition = savedInstanceState.getInt(PLAYBACK_TIME)
            Log.e("playa", "onCreate=${mCurrentPosition}")
            isPlaying = savedInstanceState.getBoolean(IS_PLAYING)
            isPaused = savedInstanceState.getBoolean(IS_PAUSED)
        }
        ivPlayPause.setOnClickListener {
            if (isPlaying) {
                Log.e("playa", "manual Pause()=${videoView.currentPosition}")
                videoView.pause()
                isPlaying = false
                isPaused = true
                ivPlayPause.setImageResource(R.drawable.exo_controls_play)
                mCurrentPosition = videoView.currentPosition
                Log.e("playa", "manual Pause currentPosition=${videoView.currentPosition}")
            } else {
                if (isPaused) {
                    if (isInitialized)
                        videoView.start()
                    else initializePlayer()
                } else
                    initializePlayer()
                ivPlayPause.setImageResource(R.drawable.exo_icon_pause)
            }
        }
        ivStop.setOnClickListener {
            isPlaying = false
            isPaused = false
            isInitialized = false
            mCurrentPosition = 0
            videoView.stopPlayback()
            initializePlayer(shouldStartPlayback = false)
            ivPlayPause.setImageResource(R.drawable.exo_controls_play)
        }
        checkSensors()
    }

    private fun checkSensors() {
        val sensor = getSystemService(Context.SENSOR_SERVICE) as SensorManager
    }

    override fun onStart() {
        super.onStart()
        if (isPlaying) {
            initializePlayer()
            ivPlayPause.setImageResource(R.drawable.exo_icon_pause)
        } else if (isPaused) {
            initializePlayer(shouldStartPlayback = false)
            ivPlayPause.setImageResource(R.drawable.exo_controls_play)
        }
    }

    override fun onPause() {
        videoView.pause()
        Log.e("playa", "onPause:Pause=${videoView.currentPosition}")
        mCurrentPosition = videoView.currentPosition
        Log.e("playa", "currentPosition=${mCurrentPosition}")
        super.onPause()
    }

    override fun onStop() {
        super.onStop()
        videoView.stopPlayback()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putInt(PLAYBACK_TIME, mCurrentPosition)
        outState.putBoolean(IS_PLAYING, isPlaying)
        outState.putBoolean(IS_PAUSED, isPaused)
        Log.e("playa", "onSaveInstanceState=${mCurrentPosition}")
    }

    private fun initializePlayer(shouldStartPlayback: Boolean = true) {
        videoView.setVideoURI(
            Uri.parse(
                "android.resource://" + packageName +
                        "/raw/vid"
            )
        )

        videoView.setOnInfoListener { _, what, _ ->
            if (what == MEDIA_INFO_VIDEO_RENDERING_START) {
                isPlaying = true
                isPaused = false
                return@setOnInfoListener true
            }
            false
        }
        // Listener for onPrepared() event (runs after the media is prepared).
        videoView.setOnPreparedListener { // Restore saved position, if available.
            if (mCurrentPosition > 0) {
                videoView.seekTo(mCurrentPosition)
            } else {
                // Skipping to 1 shows the first frame of the video.
                videoView.seekTo(1)
            }
            if (shouldStartPlayback)
                videoView.start()
            isInitialized = true
        }
    }
}

private const val PLAYBACK_TIME = "play_time"
private const val IS_PLAYING = "is_playing"
private const val IS_PAUSED = "is_paused"

interface Player {
    fun play()
    fun pause()
    fun resume()
    fun stop()
    fun seekTo(milliseconds: Int)
}

interface UserAlertness {
    interface PlayerListener {
        /**
         * The method will be called with current
         * @param playbackTimeMs which corresponds to current media playback time in milliseconds
         * */
        fun onPlaybackTimeChanged(playbackTimeMs: Int)
    }

    interface Action : Player {
        fun confirmUserAlertness()
    }

    interface AlertnessResponseListener {
        fun onUserAlert()
        fun onUserNotAlert()
    }
}
