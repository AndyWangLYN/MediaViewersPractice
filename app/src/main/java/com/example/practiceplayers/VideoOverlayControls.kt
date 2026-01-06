package com.example.practiceplayers

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.practiceplayers.viewmodels.VideoViewModel

@Composable
fun VideoOverlayControls(
    videoViewModel: VideoViewModel,
    isFullScreen: Boolean,
    onExpandCollapseClicked: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(modifier = modifier) {
        PlaybackControls(
            videoViewModel = videoViewModel,
            isFullScreen = isFullScreen,
            onExpandCollapseClicked = onExpandCollapseClicked,
            modifier = Modifier
        )
    }
}

@Composable
fun PlaybackControls(
    videoViewModel: VideoViewModel,
    isFullScreen: Boolean,
    onExpandCollapseClicked: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val playbackState by videoViewModel.playbackState.collectAsState()

    Box(
        modifier = modifier
            .background(Color(0xA0000000))
            .fillMaxSize()
            .padding(8.dp)
    ) {
        Row(
            modifier = Modifier.align(Alignment.TopEnd)
        ) {
            PlaybackButton(
                if (isFullScreen) R.drawable.collapse else R.drawable.expand,
                description = if (isFullScreen) "Exit full screen" else "Enter full screen"
            ) {
                onExpandCollapseClicked.invoke()
            }
        }

        Row(
            modifier = Modifier.align(Alignment.Center),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // The replay and rewind buttons should show when playback is normal
            if (playbackState.isReady()) {
                ReplayRewindButton(videoViewModel)
            }
            // the center playback icon (play, pause, error, buffer etc.)
            CenterPlaybackButton(playbackState, videoViewModel)
            // The fast forward button should show when playback is normal
            if (playbackState.isReady()) {
                FastForwardButton(videoViewModel)
            }
        }
    }
}

@Composable
fun PlaybackButton(
    @DrawableRes resId: Int,
    description: String,
    onClick: () -> Unit = {}
) {
    Image(
        modifier = Modifier
            .size(32.dp)
            .clickable { onClick() },
        contentDescription = description,
        painter = painterResource(id = resId)
    )
}

@Composable
fun ReplayRewindButton(
    videoViewModel: VideoViewModel
) {
    PlaybackButton(
        R.drawable.replay,
        description = "Start Over"
    ) {
        videoViewModel.seekTo(0)
    }
    PlaybackButton(
        R.drawable.fast_forward,
        description = "Rewind"
    ) {
        videoViewModel.rewind(10_000L)
    }
}

@Composable
fun CenterPlaybackButton(
    playbackState: PlaybackState,
    videoViewModel: VideoViewModel
) {
    when (playbackState) {
        PlaybackState.IDLE -> {
            PlaybackButton(
                R.drawable.play,
                description = "Start"
            ) {
                videoViewModel.startPlayback()
            }
        }

        PlaybackState.PLAYING -> {
            PlaybackButton(
                R.drawable.pause,
                description = "Play"
            ) {
                videoViewModel.pausePlayback()
            }
        }

        PlaybackState.PAUSE -> {
            PlaybackButton(
                R.drawable.play,
                description = "Pause playback"
            ) {
                videoViewModel.resumePlayback()
            }
        }

        PlaybackState.BUFFERING -> {
            CircularProgressIndicator(
                modifier = Modifier.size(32.dp),
                color = Color.White
            )
        }

        PlaybackState.COMPLETED -> {
            PlaybackButton(
                R.drawable.replay,
                description = "Replay"
            ) {
                videoViewModel.seekTo(0)
            }
        }

        PlaybackState.ERROR -> {
            PlaybackButton(
                R.drawable.error,
                description = "Error"
            )

            PlaybackButton(
                R.drawable.replay,
                description = "Retry"
            ) {
                videoViewModel.startPlayback()
            }
        }
    }
}

@Composable
fun FastForwardButton(
    videoViewModel: VideoViewModel
) {
    PlaybackButton(
        R.drawable.forward_10,
        description = "Fast Forward"
    ) {
        videoViewModel.fastForward(10_000L)
    }
}