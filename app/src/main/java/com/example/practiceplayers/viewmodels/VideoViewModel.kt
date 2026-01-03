package com.example.practiceplayers.viewmodels

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer
import com.example.practiceplayers.VIDEO_URL_DEMO
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class VideoViewModel(application: Application): ViewModel() {
    private val _isPlayerActive = MutableStateFlow(false)
    val isPlayerActive = _isPlayerActive.asStateFlow()

    private val _isVideoPlaying = MutableStateFlow(false)
    val isVideoPlaying = _isVideoPlaying.asStateFlow()

    private var exoPlayer: ExoPlayer? = null

    init {
        exoPlayer = exoPlayer ?: ExoPlayer.Builder(application).build()
    }

    fun startPlayback() {
        exoPlayer?.let { player ->
            val mediaItem = MediaItem.Builder().setUri(VIDEO_URL_DEMO).build()
            player.setMediaItem(mediaItem)
            player.prepare()
            player.play()
            _isVideoPlaying.value = true
            _isPlayerActive.value = true
        }
    }

    fun pausePlayback() {
        exoPlayer?.pause()
        _isVideoPlaying.value = false
    }

    fun resumePlayback() {
        exoPlayer?.play()
        _isVideoPlaying.value = true
    }

    fun stopPlayback() {
        exoPlayer?.stop()
        _isVideoPlaying.value = false
    }

    fun getExoPlayer() = exoPlayer

    fun releaseExoPlayer() {
        exoPlayer?.release()
        exoPlayer = null
    }

    override fun onCleared() {
        super.onCleared()
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
