package com.example.practiceplayers

import androidx.compose.foundation.AndroidExternalSurface
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.practiceplayers.viewmodels.VideoViewModel

@Composable
fun VideoScreen(
    navController: NavController
) {
    val videoViewModel = rememberVideoViewModel(navController)
    val isPlayerActive by videoViewModel.isPlayerActive.collectAsState()

    Surface(modifier = Modifier.fillMaxWidth()) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .systemBarsPadding()
                .aspectRatio(4f / 3f)
        ) {
            if (isPlayerActive) {
                ExoPlayerView(
                    videoViewModel = videoViewModel,
                    onExpandCollapseClicked = {
                        navController.navigate(Screen.FullScreenVideo.route)
                    }
                )
            } else {
                CoverImageView(videoViewModel)
            }
        }
    }
}

@Composable
fun ExoPlayerView(
    videoViewModel: VideoViewModel,
    onExpandCollapseClicked: () -> Unit
) {
    val shouldShowOverlay by videoViewModel.shouldShowPlaybackControls.collectAsState()

    Box {
        AndroidExternalSurface(
            modifier = Modifier
                .aspectRatio(4f / 3f)
                .clickable {
                    videoViewModel.showPlaybackControls()
                }
        ) {
            onSurface { surface, _, _ ->
                // tell exoplayer the surface is ready, send this surface to it
                videoViewModel.onAttachSurface(surface)
                // release surface on destroy
                surface.onDestroyed {
                    videoViewModel.onDetachSurface(surface)
                }
            }
        }

        if (shouldShowOverlay) {
            VideoOverlayControls(
                videoViewModel = videoViewModel,
                isFullScreen = false,
                onExpandCollapseClicked = {
                    onExpandCollapseClicked.invoke()
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
fun CoverImageView(videoViewModel: VideoViewModel) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .systemBarsPadding(),
        verticalArrangement = Arrangement.Top
    ) {
        Box {
            CoverImage(
                modifier = Modifier.fillMaxWidth(),
                imageRes = R.drawable.tears_of_steel_cover
            )
            Image(
                modifier = Modifier
                    .size(40.dp)
                    .align(Alignment.Center)
                    .clickable {
                        // start the video playback
                        videoViewModel.startPlayback()
                    },
                contentDescription = "Start playback",
                painter = painterResource(R.drawable.play)
            )
        }
    }
}

@Composable
fun CoverImage(
    modifier: Modifier = Modifier,
    imageRes: Int
) {
    Image(
        modifier = modifier,
        contentDescription = "Tears of Steel",
        contentScale = ContentScale.Crop,
        painter = painterResource(id = imageRes)
    )
}
