package com.example.practiceplayers

enum class PlaybackState {
    IDLE,
    PLAYING,
    PAUSE,
    BUFFERING,
    COMPLETED,
    ERROR
}

fun PlaybackState.isReady(): Boolean {
    return this == PlaybackState.PLAYING || this == PlaybackState.PAUSE
}