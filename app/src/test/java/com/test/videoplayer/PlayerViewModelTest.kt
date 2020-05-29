package com.test.videoplayer

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import com.nhaarman.mockitokotlin2.*
import com.test.videoplayer.PlayerViewActions.*
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
class PlayerViewModelTest {
    lateinit var sut: PlayerViewModel

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @Mock
    lateinit var userAlertnessTracker: UserAlertnessTracker

    @Mock
    lateinit var testObserver: Observer<PlayerViewActions>

    @Before
    fun setup() {
        sut = PlayerViewModel(userAlertnessTracker = userAlertnessTracker)
    }

    @Test
    fun `when screen starts the flow we initialize the player`() {
        sut.playerViewActions.observeForever(testObserver)
        sut.start()
        verify(testObserver).onChanged(Initialize(elapsedTime = 1))
        verifyNoMoreInteractions(testObserver)
    }

    @Test
    fun `if playback was already in progress then after initialization playback resumes`() {
        sut.isPlaying = true
        sut.elapsedTimeMilliseconds = 20
        sut.playerViewActions.observeForever(testObserver)
        sut.start()
        verify(testObserver).onChanged(eq(Initialize(elapsedTime = 20)))
        sut.initialize()
        verify(testObserver).onChanged(any<Play>())
        verifyNoMoreInteractions(testObserver)
    }

    @Test
    fun `when user pauses playback it is paused`() {
        sut.isPlaying = true
        sut.playerViewActions.observeForever(testObserver)
        sut.start()
        sut.initialize()
        sut.onPause(elapsedTime = 20)
        verify(testObserver).onChanged(any<Pause>())
        assertFalse(sut.isPlaying)
        assertTrue(sut.isPaused)
        assertEquals(20, sut.elapsedTimeMilliseconds)
    }

    @Test
    fun `when playback is paused due to rotation it is not paused`() {
        sut.isPlaying = true
        sut.playerViewActions.observeForever(testObserver)
        sut.start()
        verify(testObserver).onChanged(any<Initialize>())
        sut.initialize()
        verify(testObserver).onChanged(any<Play>())
        sut.onPause(elapsedTime = 20, userPaused = false)
        verify(testObserver).onChanged(any<Pause>())
        verifyNoMoreInteractions(testObserver)
        assertTrue(sut.isPlaying)
        assertFalse(sut.isPaused)
        assertTrue(sut.isInitialized)
        assertEquals(20, sut.elapsedTimeMilliseconds)
    }

    @Test
    fun `when playback is stopped by user it is stopped`() {
        sut.isPlaying = true
        sut.playerViewActions.observeForever(testObserver)
        sut.start()
        sut.initialize()
        sut.onStop()
        verify(testObserver).onChanged(any<Stop>())
        assertFalse(sut.isPlaying)
        assertFalse(sut.isPaused)
        assertFalse(sut.isInitialized)
        assertEquals(1, sut.elapsedTimeMilliseconds)
    }

    @Test
    fun `when playback starts we notify alertness checker`() {
        sut.playerViewActions.observeForever(testObserver)
        sut.start()
        sut.initialize()
        sut.onPlay()
        verify(userAlertnessTracker).playbackStarted(any(), any())
        verifyNoMoreInteractions(userAlertnessTracker)
    }

    @Test
    fun `when playback paused we notify alertness checker`() {
        sut.playerViewActions.observeForever(testObserver)
        sut.start()
        sut.initialize()
        sut.onPlay()
        sut.onPause(elapsedTime = 20)
        verify(userAlertnessTracker).playbackStarted(any(), any())
        verify(userAlertnessTracker).playbackStopped()
        verifyNoMoreInteractions(userAlertnessTracker)
    }

    @Test
    fun `when playback stopped we notify alertness checker`() {
        sut.playerViewActions.observeForever(testObserver)
        sut.start()
        sut.initialize()
        sut.onPlay()
        sut.onStop()
        verify(userAlertnessTracker).playbackStarted(any(), any())
        verify(userAlertnessTracker).playbackStopped()
        verifyNoMoreInteractions(userAlertnessTracker)
    }

    @Test
    fun `when user confirms that user is alert we resume playback`() {
        sut.playerViewActions.observeForever(testObserver)
        sut.confirmAlert(isAlert = true)
        verify(testObserver).onChanged(any<Play>())
        verifyNoMoreInteractions(testObserver)
    }

    @Test
    fun `when user confirms that user is not alert we stop playback`() {
        sut.playerViewActions.observeForever(testObserver)
        sut.confirmAlert(isAlert = false)
        verify(testObserver).onChanged(any<Stop>())
        verifyNoMoreInteractions(testObserver)
    }

    @Test
    fun `when is flipped down playback is paused`() {
        sut.isInitialized = true
        sut.isPlaying = true
        sut.playerViewActions.observeForever(testObserver)
        sut.onDeviceFlipped(faceUp = false, elapsedTime = 20)
        verify(testObserver).onChanged(any<Pause>())
        verifyNoMoreInteractions(testObserver)
    }

    @Test
    fun `when device is flipped up playback is resumed`() {
        sut.isInitialized = true
        sut.isPaused = true
        sut.playerViewActions.observeForever(testObserver)
        sut.onDeviceFlipped(faceUp = true, elapsedTime = 1)
        verify(testObserver).onChanged(any<Play>())
        verifyNoMoreInteractions(testObserver)
    }

    @Test
    fun `when alertness check requests to check corresponding event is dispatched`() {
        sut.playerViewActions.observeForever(testObserver)
        sut.check()
        verify(testObserver).onChanged(any<AlertnessCheck>())
        verifyNoMoreInteractions(testObserver)
    }
}