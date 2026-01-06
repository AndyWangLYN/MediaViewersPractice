package com.example.practiceplayers

import android.app.Application
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.practiceplayers.viewmodels.VideoViewModel

@Composable
fun rememberVideoViewModel(
    navController: NavController
): VideoViewModel {
    val application = LocalContext.current.applicationContext as Application

    val parentEntry = remember(navController) {
        navController.getBackStackEntry(Screen.Video.route)
    }

    return viewModel(
        parentEntry,
        factory = VideoViewModel.buildFactory(application)
    )
}