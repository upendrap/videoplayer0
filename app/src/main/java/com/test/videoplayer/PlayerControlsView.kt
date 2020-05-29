package com.test.videoplayer

import android.content.Context
import android.os.Parcelable
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.LinearLayout
import kotlinx.android.synthetic.main.layout_player_controls.view.*

class PlayerControlsView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : LinearLayout(context, attrs, defStyleAttr) {
    private var isPlaying = false
        set(value) {
            if (value)
                play()
            else pause()
            field = value
        }
    private var listener: ActionListener? = null

    init {
        orientation = HORIZONTAL
        LayoutInflater.from(context)
            .inflate(R.layout.layout_player_controls, this)
        btnPlayPause.setOnClickListener {
            if (isPlaying) {
                listener?.onPausePressed()
            } else {
                listener?.onPlayPressed()
            }
            isPlaying = !isPlaying
        }
        btnStop.setOnClickListener {
            isPlaying = false
            stop()
            listener?.onStopPressed()
        }
    }

    override fun setOrientation(orientation: Int) {
        super.setOrientation(HORIZONTAL)
    }

    override fun onRestoreInstanceState(state: Parcelable?) {
        if (state is PlayerControlsViewState) {
            super.onRestoreInstanceState(state.superState)
            isPlaying = state.isPlaying
        }
    }

    override fun onSaveInstanceState(): Parcelable? {
        val superState = super.onSaveInstanceState()
        val state = PlayerControlsViewState(superState)
        state.isPlaying = isPlaying
        return state
    }

    fun setActionListener(listener: ActionListener) {
        this.listener = listener
    }

    private fun play() {
        btnPlayPause.setImageResource(R.drawable.exo_icon_pause)
    }

    private fun pause() {
        btnPlayPause.setImageResource(R.drawable.exo_controls_play)
    }

    private fun stop() {
        btnPlayPause.setImageResource(R.drawable.exo_controls_play)
    }

    fun setIsPlaying(isPlaying: Boolean) {
        this.isPlaying = isPlaying
    }

    interface ActionListener {
        fun onPlayPressed()
        fun onPausePressed()
        fun onStopPressed()
    }

    class PlayerControlsViewState(val parcelable: Parcelable?) : BaseSavedState(parcelable) {
        var isPlaying: Boolean = false
    }
}