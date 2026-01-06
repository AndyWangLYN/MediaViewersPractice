package com.example.practiceplayers

import androidx.compose.foundation.AndroidExternalSurface
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

@Composable
fun FullscreenVideoScreen(
    navController: NavController
) {
    val videoViewModel = rememberVideoViewModel(navController)

    Box(
        modifier = Modifier
            .fillMaxSize()
            .systemBarsPadding()
            .background(Color.Black)
    ) {
        AndroidExternalSurface(
            modifier = Modifier.fillMaxSize()
            //.aspectRatio(4f / 3f)
        ) {
            onSurface { surface, _, _ ->
                videoViewModel.onAttachSurface(surface)
                surface.onDestroyed {
                    videoViewModel.onDetachSurface(surface)
                }
            }
        }

        // Back button
        Icon(
            painter = painterResource(R.drawable.collapse),
            contentDescription = "Exit fullscreen",
            modifier = Modifier
                .padding(16.dp)
                .align(Alignment.TopStart)
                .clickable {
                    navController.popBackStack()
                }
        )
    }
}