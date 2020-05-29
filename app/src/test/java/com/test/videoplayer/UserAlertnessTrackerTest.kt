package com.test.videoplayer

import com.nhaarman.mockitokotlin2.never
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.verifyNoMoreInteractions
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest

import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
class UserAlertnessTrackerTest {
    var sut: UserAlertnessTracker = UserAlertnessTracker()

    @Mock
    lateinit var listener: UserAlertnessCheck

    @ExperimentalCoroutinesApi
    @Test
    fun `when we start playback we are notified after 60 seconds`() {
        runBlockingTest {
            sut.playbackStarted(this, listener)
            advanceTimeBy(AWARENESS_TIMEOUT)
            verify(listener).check()
            verifyNoMoreInteractions(listener)
        }
    }

    @ExperimentalCoroutinesApi
    @Test
    fun `when we start playback and pause we are not notified after 60 seconds`() {
        runBlockingTest {
            sut.playbackStarted(this, listener)
            sut.playbackStopped()
            advanceTimeBy(AWARENESS_TIMEOUT)
            verify(listener, never()).check()
            verifyNoMoreInteractions(listener)
        }
    }
}