package com.example.practiceplayers

import android.app.Application
import android.widget.VideoView
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.media3.ui.PlayerView
import com.example.practiceplayers.viewmodels.VideoViewModel

@Composable
fun VideoScreen() {
    val videoViewModel: VideoViewModel = viewModel(
        factory = VideoViewModel.buildFactory(
            LocalContext.current.applicationContext as Application
        )
    )
    val isPlayerActive by videoViewModel.isPlayerActive.collectAsState()
    // val isPlaying by videoViewModel.isVideoPlaying.collectAsState()

    Surface(modifier = Modifier.fillMaxWidth()) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(4f / 3f)
        ) {
            if (isPlayerActive) {
                ExoPlayerView(videoViewModel)
            } else {
                CoverImageView(videoViewModel)
            }
        }
    }
}

@Composable
fun ExoPlayerView(
    videoViewModel: VideoViewModel
) {
    AndroidView(
        modifier = Modifier.fillMaxSize(),
        factory = { ctx ->
            PlayerView(ctx).apply {
                player = videoViewModel.getExoPlayer()
            }
        }
    )

    // overlay play/pause button
//            if (!isPlaying) {
//                Image(
//                    modifier = Modifier
//                        .size(50.dp)
//                        .align(Alignment.Center)
//                        .clickable {
//                            videoViewModel.resumePlayback()
//                        },
//                    painter = painterResource(R.drawable.play),
//                    contentDescription = "Resume"
//                )
//            }
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
