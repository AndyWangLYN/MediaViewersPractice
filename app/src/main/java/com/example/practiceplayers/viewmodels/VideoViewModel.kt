package com.example.practiceplayers.viewmodels

import android.app.Application
import android.view.Surface
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.media3.common.MediaItem
import androidx.media3.common.PlaybackException
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import com.example.practiceplayers.PlaybackState
import com.example.practiceplayers.VIDEO_URL_DEMO
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class VideoViewModel(application: Application) : ViewModel() {
    private val _isPlayerActive = MutableStateFlow(false)
    val isPlayerActive = _isPlayerActive.asStateFlow()

    private val _isVideoPlaying = MutableStateFlow(false)
    val isVideoPlaying = _isVideoPlaying.asStateFlow()

    private val _playbackState = MutableStateFlow(PlaybackState.IDLE)
    val playbackState = _playbackState.asStateFlow()

    private var exoPlayer: ExoPlayer? = null
    private val playerListener = object : Player.Listener {
        override fun onPlaybackStateChanged(playbackState: Int) {
            super.onPlaybackStateChanged(playbackState)
            _playbackState.value = when (playbackState) {
                Player.STATE_IDLE -> PlaybackState.IDLE
                Player.STATE_BUFFERING -> PlaybackState.BUFFERING
                // do nothing we want play pause in onIsPlayingChanged callback
                Player.STATE_READY -> _playbackState.value
                Player.STATE_ENDED -> PlaybackState.COMPLETED
                else -> PlaybackState.IDLE
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
    }

    init {
        exoPlayer = exoPlayer ?: ExoPlayer.Builder(application).build().apply {
            addListener(playerListener)
        }
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

    fun stopPlayback() {
        exoPlayer?.stop()
    }

    fun onAttachSurface(surface: Surface) {
        exoPlayer?.setVideoSurface(surface)
    }

    fun onDetachSurface() {
        exoPlayer?.setVideoSurface(null)
        releaseExoPlayer()
    }

    fun releaseExoPlayer() {
        exoPlayer?.release()
        exoPlayer = null
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
