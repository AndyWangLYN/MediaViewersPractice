package com.example.practiceplayers.viewmodels

import android.app.Application
import android.os.Build
import android.view.Surface
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.media3.common.MediaItem
import androidx.media3.common.PlaybackException
import androidx.media3.common.Player
import androidx.media3.common.Timeline
import androidx.media3.exoplayer.ExoPlayer
import com.example.practiceplayers.PlaybackState
import com.example.practiceplayers.VIDEO_URL_DEMO
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

class VideoViewModel(application: Application) : ViewModel() {
    private val _isPlayerActive = MutableStateFlow(false)
    val isPlayerActive = _isPlayerActive.asStateFlow()

    private val _isVideoPlaying = MutableStateFlow(false)
    val isVideoPlaying = _isVideoPlaying.asStateFlow()

    private val _shouldShowPlaybackControls = MutableStateFlow(true)
    val shouldShowPlaybackControls = _shouldShowPlaybackControls.asStateFlow()

    private val _playbackState = MutableStateFlow(PlaybackState.IDLE)
    val playbackState = _playbackState.asStateFlow()

    private val _currentPositionMs = MutableStateFlow(0L)
    val currentPositionMs = _currentPositionMs.asStateFlow()

    private val _bufferedPositionMs = MutableStateFlow(0L)
    val bufferedPositionMs = _bufferedPositionMs.asStateFlow()

    private val _videoDurationMs = MutableStateFlow(0L)
    val videoDurationMs = _videoDurationMs.asStateFlow()

    private var positionTrackingJob: Job? = null
    private var currentSurface: Surface? = null
    private var exoPlayer: ExoPlayer? = null

    private val playerListener = object : Player.Listener {
        override fun onPlaybackStateChanged(playbackState: Int) {
            super.onPlaybackStateChanged(playbackState)
            _playbackState.value = when (playbackState) {
                Player.STATE_IDLE -> PlaybackState.IDLE
                Player.STATE_BUFFERING -> {
                    _shouldShowPlaybackControls.value = true
                    PlaybackState.BUFFERING
                }
                // do nothing we want play pause in onIsPlayingChanged callback
                Player.STATE_READY -> _playbackState.value
                Player.STATE_ENDED -> PlaybackState.COMPLETED
                else -> PlaybackState.IDLE
            }

            when (playbackState) {
                Player.STATE_READY -> {
                    updateVideoDuration()
                    startTrackingPlaybackPosition()
                }

                else -> stopTrackingPlaybackPosition()
            }
        }

        override fun onIsPlayingChanged(isPlaying: Boolean) {
            super.onIsPlayingChanged(isPlaying)
            _playbackState.value =
                if (isPlaying) PlaybackState.PLAYING else PlaybackState.PAUSE
        }

        override fun onPlayerError(error: PlaybackException) {
            _playbackState.value = PlaybackState.ERROR
        }

        override fun onMediaItemTransition(mediaItem: MediaItem?, reason: Int) {
            super.onMediaItemTransition(mediaItem, reason)
            updateVideoDuration()
        }

        override fun onTimelineChanged(timeline: Timeline, reason: Int) {
            super.onTimelineChanged(timeline, reason)
            updateVideoDuration()
        }
    }

    init {
        exoPlayer = exoPlayer ?: ExoPlayer.Builder(application).build().apply {
            addListener(playerListener)
        }
    }

    private fun startTrackingPlaybackPosition() {
        stopTrackingPlaybackPosition()

        positionTrackingJob = viewModelScope.launch {
            while (isActive) {
                exoPlayer?.let { player ->
                    if (player.isPlaying) {
                        _currentPositionMs.value = player.currentPosition
                    }
                }
                delay(1_000L)
            }
        }
    }

    private fun stopTrackingPlaybackPosition() {
        positionTrackingJob?.cancel()
        positionTrackingJob = null
    }

    private fun updateVideoDuration() {
        _videoDurationMs.value = exoPlayer?.duration ?: 0L
    }

    fun startPlayback() {
        exoPlayer?.let { player ->
            val mediaItem = MediaItem.Builder().setUri(VIDEO_URL_DEMO).build()
            player.setMediaItem(mediaItem)
            player.prepare()
            player.play()
            _isPlayerActive.value = true
        }
    }

    fun pausePlayback() {
        exoPlayer?.playWhenReady = false
    }

    fun resumePlayback() {
        exoPlayer?.playWhenReady = true
    }

    fun seekTo(timeInMs: Long) {
        exoPlayer?.seekTo(timeInMs)
    }

    fun fastForward(timeInMs: Long) {
        exoPlayer?.let { player ->
            player.seekTo(player.currentPosition + timeInMs)
        }
    }

    fun rewind(timeInMs: Long) {
        exoPlayer?.let { player ->
            val targetTimeInMs = (player.currentPosition - timeInMs).coerceAtLeast(0)
            player.seekTo(targetTimeInMs)
        }
    }

    fun onAttachSurface(surface: Surface) {
        if (currentSurface == surface) return
        currentSurface = surface
        exoPlayer?.setVideoSurface(surface)
        // build 23 and below forces video frame update to make sure smooth
        if (Build.VERSION.SDK_INT < 23) {
            val pos = exoPlayer?.currentPosition ?: 0L
            exoPlayer?.seekTo(pos)
        }
    }

    fun onDetachSurface(surface: Surface) {
        if (currentSurface == surface) {
            exoPlayer?.setVideoSurface(null)
            currentSurface = null
        }
    }

    fun releaseExoPlayer() {
        exoPlayer?.release()
        exoPlayer = null
    }

    fun showPlaybackControls() {
        _shouldShowPlaybackControls.value = true
    }

    fun hidePlaybackControls() {
        if (playbackState.value == PlaybackState.BUFFERING) return
        _shouldShowPlaybackControls.value = false
    }

    override fun onCleared() {
        super.onCleared()
        exoPlayer?.removeListener(playerListener)
        releaseExoPlayer()
    }

    companion object {
        fun buildFactory(application: Application): ViewModelProvider.Factory {
            return object : ViewModelProvider.Factory {
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    if (modelClass.isAssignableFrom(VideoViewModel::class.java)) {
                        return VideoViewModel(application) as T
                    } else {
                        throw IllegalArgumentException("Unknown ViewModel class $modelClass")
                    }
                }
            }
        }
    }
}
