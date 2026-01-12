package com.example.practiceplayers

data class SeekOverlay(
    val direction: Direction,
    val seekTime: Long
) {
    enum class Direction {
        FORWARD,
        BACKWARD
    }
}
