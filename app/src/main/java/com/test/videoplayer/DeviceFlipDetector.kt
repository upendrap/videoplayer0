package com.test.videoplayer

import android.content.Context
import android.content.Context.SENSOR_SERVICE
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import java.util.concurrent.TimeUnit
import kotlin.math.abs

class DeviceFlipDetector(
    private val context: Context,
    val deviceFlipListener: DeviceFlipListener
) : LifecycleObserver,
    SensorEventListener {
    private var lastEvent: EventData? = null
    private var isRegistered: Boolean = false

    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
    fun start() {
        val sensorManager =
            context.getSystemService(SENSOR_SERVICE) as? SensorManager
        val sensors = sensorManager?.getSensorList(Sensor.TYPE_ACCELEROMETER)
        if (sensors?.isNotEmpty() == true) {
            sensorManager.registerListener(this, sensors.first(), SensorManager.SENSOR_DELAY_UI)
            isRegistered = true
        }

    }

    @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
    fun pause() {
        if (isRegistered) {
            val sensorManager =
                context.getSystemService(SENSOR_SERVICE) as? SensorManager
            val sensors = sensorManager?.getSensorList(Sensor.TYPE_ACCELEROMETER)
            if (sensors?.isNotEmpty() == true) {
                sensorManager.unregisterListener(this, sensors.first())
                isRegistered = false
            }
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {

    }

    override fun onSensorChanged(event: SensorEvent?) {
        if (null == lastEvent) {
            lastEvent = EventData(
                TimeUnit.NANOSECONDS.toSeconds(event?.timestamp ?: 0),
                event?.values?.get(2) ?: 0.0f
            )
        } else {
            val currentEventTime = TimeUnit.NANOSECONDS.toSeconds(event?.timestamp ?: 0)
            //we check data after 2 seconds
            if (currentEventTime - lastEvent!!.seconds > 2) {
                val lastZVal = lastEvent?.z ?: 0.0f
                val currentZVal = event?.values?.get(2) ?: 0.0f
                //if the absolute difference between z accelerations is > 12
                //9.8- -9.8 ~20
                //-9.8 - 9.8 ~20
                //picking 12 as seems accurate enough on OPO
                if (abs(lastZVal - currentZVal) > 12) {
                    deviceFlipListener.onDeviceFlipped(lastZVal < 0)
                    //this data becomes reference for next comparisons
                    lastEvent = EventData(
                        TimeUnit.NANOSECONDS.toSeconds(event?.timestamp ?: 0),
                        event?.values?.get(2) ?: 0.0f
                    )
                }
            }
        }
    }
}

private data class EventData(val seconds: Long, val z: Float)

interface DeviceFlipListener {
    fun onDeviceFlipped(faceUp: Boolean)
}