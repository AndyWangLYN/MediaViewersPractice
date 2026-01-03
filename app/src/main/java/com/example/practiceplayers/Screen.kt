package com.example.practiceplayers

sealed class Screen(val route: String) {
    data object Home : Screen("home")
    data object Video : Screen("video")
    data object Images : Screen("images")
    data object Documents : Screen("documents")
}