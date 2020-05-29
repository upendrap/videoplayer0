package com.test.videoplayer

import android.content.DialogInterface
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.VibrationEffect
import android.os.Vibrator
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity(),
    PlayerControlsView.ActionListener,
    DialogInterface.OnClickListener, DeviceFlipListener {
    private val playerViewModel: PlayerViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        playerControlsView.setActionListener(this)
        playerViewModel.playerViewActions.observe(this,
            Observer<PlayerViewActions> { t ->
                when (t) {
                    is PlayerViewActions.Initialize -> {
                        initializePlayer(t)
                    }
                    is PlayerViewActions.Play -> {
                        play()
                    }
                    is PlayerViewActions.Pause -> {
                        pause()
                    }
                    is PlayerViewActions.Stop -> {
                        stop()
                        playerViewModel.start()
                    }
                    is PlayerViewActions.AlertnessCheck -> {
                        checkAlertness()
                    }
                }
            })
        lifecycle.addObserver(DeviceFlipDetector(applicationContext, this))
    }

    override fun onStart() {
        super.onStart()
        playerViewModel.start()
    }

    override fun onPause() {
        playerViewModel.onPause(elapsedTime = videoView.currentPosition, userPaused = false)
        super.onPause()
    }

    override fun onStop() {
        super.onStop()
        videoView.stopPlayback()
    }

    override fun onClick(dialog: DialogInterface?, which: Int) {
        if (which == DialogInterface.BUTTON_POSITIVE) {
            playerViewModel.confirmAlert(isAlert = true)
        } else {
            playerViewModel.confirmAlert(isAlert = false)
        }
    }

    private fun initializePlayer(initialize: PlayerViewActions.Initialize) {
        videoView.setVideoURI(Uri.parse("android.resource://$packageName/raw/vid"))

        videoView.setOnPreparedListener {
            videoView.seekTo(initialize.elapsedTime)
            playerViewModel.initialize()
        }
    }

    override fun onPlayPressed() {
        playerViewModel.onPlay()
    }

    override fun onPausePressed() {
        playerViewModel.onPause(elapsedTime = videoView.currentPosition)
    }

    override fun onStopPressed() {
        playerViewModel.onStop()
    }

    private fun play() {
        playerControlsView.setIsPlaying(isPlaying = true)
        videoView.start()
    }

    private fun stop() {
        playerControlsView.setIsPlaying(isPlaying = false)
        videoView.stopPlayback()
    }

    private fun pause() {
        playerControlsView.setIsPlaying(isPlaying = false)
        videoView.pause()
    }

    private fun checkAlertness() {
        playerViewModel.onPause(elapsedTime = videoView.currentPosition)
        (getSystemService(VIBRATOR_SERVICE) as Vibrator)?.run {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                vibrate(VibrationEffect.createOneShot(300, 100));
            } else {
                vibrate(300);
            }
        }
        AlertnessCheckDialog().show(supportFragmentManager, AlertnessCheckDialog::class.java.name)
    }

    override fun onDeviceFlipped(faceUp: Boolean) {
        playerViewModel.onDeviceFlipped(faceUp = faceUp, elapsedTime = videoView.currentPosition)
    }
}