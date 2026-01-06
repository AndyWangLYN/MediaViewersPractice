package com.example.practiceplayers

import android.content.Context
import android.content.pm.ActivityInfo
import androidx.compose.foundation.AndroidExternalSurface
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.navigation.NavController

@Composable
fun FullscreenVideoScreen(
    navController: NavController
) {
    val context = LocalContext.current
    val videoViewModel = rememberVideoViewModel(navController)
    val shouldShowOverlay by videoViewModel.shouldShowPlaybackControls.collectAsState()
    // Handle Orientation Change
    handleOrientationChange(context)
    // Video view
    Box(
        modifier = Modifier
            .fillMaxSize()
            .systemBarsPadding()
            .background(Color.Black)
    ) {
        AndroidExternalSurface(
            modifier = Modifier
                .fillMaxSize()
                .clickable {
                    videoViewModel.showPlaybackControls()
                }
        ) {
            onSurface { surface, _, _ ->
                videoViewModel.onAttachSurface(surface)
                surface.onDestroyed {
                    videoViewModel.onDetachSurface(surface)
                }
            }
        }

        if (shouldShowOverlay) {
            VideoOverlayControls(
                videoViewModel = videoViewModel,
                isFullScreen = true,
                onExpandCollapseClicked = {
                    navController.popBackStack()
                },
                modifier = Modifier
                    .matchParentSize()
                    .clickable {
                        videoViewModel.hidePlaybackControls()
                    }
            )
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
