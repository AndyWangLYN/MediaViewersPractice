package com.example.practiceplayers

import android.content.Context
import android.content.pm.ActivityInfo
import androidx.compose.foundation.AndroidExternalSurface
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.navigation.NavController
import com.example.practiceplayers.Utils.videoGestures

@Composable
fun FullscreenVideoScreen(
    navController: NavController
) {
    val context = LocalContext.current
    val videoViewModel = rememberVideoViewModel(navController)
    val shouldShowOverlay by videoViewModel.shouldShowPlaybackControls.collectAsState()
    val playbackState by videoViewModel.playbackState.collectAsState()
    val videoSpeed by videoViewModel.videoPlaybackSpeed.collectAsState()
    val seekOverlay by videoViewModel.seekOverlay.collectAsState()

    // Handle Orientation Change
    handleOrientationChange(context)
    // Video view
    Box(
        modifier = Modifier
            .fillMaxSize()
            .systemBarsPadding()
            .background(Color.Black)
    ) {
        // Video surface (passive with no controls)
        AndroidExternalSurface(
            modifier = Modifier.fillMaxSize()
        ) {
            onSurface { surface, _, _ ->
                videoViewModel.onAttachSurface(surface)
                surface.onDestroyed {
                    videoViewModel.onDetachSurface(surface)
                }
            }
        }
        // Single gesture layer
        Box(
            modifier = Modifier
                .matchParentSize()
                .videoGestures(
                    playbackState = playbackState,
                    onTap = {
                        if (videoSpeed == 1f) videoViewModel.togglePlaybackControls()
                    },
                    onDoubleTapForward = {
                        videoViewModel.fastForward(10_000L)
                    },
                    onDoubleTapBackward = {
                        videoViewModel.rewind(10_000L)
                    },
                    onLongPressStart = {
                        videoViewModel.setPlaybackSpeed(2f)
                    },
                    onLongPressEnd = {
                        videoViewModel.setPlaybackSpeed(1f)
                    }
                )
        )
        // Overlay layer
        when {
            videoSpeed != 1f -> {
                PlaybackSpeedOverlay(speed = videoSpeed, modifier = Modifier.matchParentSize())
            }

            seekOverlay != null -> {
                SeekOverlayView(seekOverlay)
            }

            shouldShowOverlay -> {
                VideoOverlayControls(
                    videoViewModel = videoViewModel,
                    isFullScreen = true,
                    onExpandCollapseClicked = { navController.popBackStack() },
                    modifier = Modifier.matchParentSize()
                )
            }
        }
    }
}

@Composable
private fun handleOrientationChange(context: Context) {
    DisposableEffect(Unit) {
        val activity = context.findActivity()
        val window = activity?.window
        if (window != null) {
            val controller = WindowInsetsControllerCompat(window, window.decorView)
            // Hide bars
            controller.hide(WindowInsetsCompat.Type.systemBars())
            controller.systemBarsBehavior =
                WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE

            activity.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
        }

        onDispose {
            val window = activity?.window
            if (window != null) {
                val controller = WindowInsetsControllerCompat(window, window.decorView)
                // Show bars again
                controller.show(WindowInsetsCompat.Type.systemBars())
                activity.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
            }
        }
    }
}

@Composable
fun PlaybackSpeedOverlay(
    speed: Float,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .background(Color(0x80000000)),
        contentAlignment = Alignment.TopCenter
    ) {
        Text(
            text = "${speed}×",
            color = Color.White,
            fontSize = 36.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
fun SeekOverlayView(seekOverlay: SeekOverlay?) {
    if (seekOverlay == null) return

    val icon = when (seekOverlay.direction) {
        SeekOverlay.Direction.FORWARD -> R.drawable.backwards_10
        SeekOverlay.Direction.BACKWARD -> R.drawable.forward_10
    }
    val text = when (seekOverlay.direction) {
        SeekOverlay.Direction.FORWARD -> "+${seekOverlay.seekTime / 1000}s"
        SeekOverlay.Direction.BACKWARD -> "-${seekOverlay.seekTime / 1000}s"
    }

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Image(
                painter = painterResource(id = icon),
                contentDescription = null,
                modifier = Modifier.size(48.dp)
            )
            Text(text, color = Color.White)
        }
    }
}
