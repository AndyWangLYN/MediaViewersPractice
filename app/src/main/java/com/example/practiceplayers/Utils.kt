package com.example.practiceplayers

import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import java.util.Locale

object Utils {
    fun formatMsToString(timeInMs: Long): String {
        var secondsRemaining = timeInMs / 1000
        val hours = secondsRemaining / 3600
        secondsRemaining -= hours * 3600
        val minutes = secondsRemaining / 60
        secondsRemaining -= minutes * 60
        val seconds = secondsRemaining

        return String.format(Locale.getDefault(), "%02d:%02d:%02d", hours, minutes, seconds)
    }

    fun Modifier.videoGestures(
        playbackState: PlaybackState,
        onTap: () -> Unit,
        onDoubleTapForward: () -> Unit = {},
        onDoubleTapBackward: () -> Unit = {},
        onLongPressStart: () -> Unit,
        onLongPressEnd: () -> Unit
    ): Modifier = pointerInput(playbackState) {
        detectTapGestures(
            onTap = { onTap() },
            onDoubleTap = { offset ->
                if (offset.x <= size.width / 3) {
                    onDoubleTapBackward.invoke()
                } else if (offset.x > size.width / 3 * 2) {
                    onDoubleTapForward.invoke()
                }
            },
            onLongPress = {
                if (playbackState == PlaybackState.PLAYING) {
                    onLongPressStart()
                }
            },
            onPress = {
                try {
                    awaitRelease()
                } finally {
                    onLongPressEnd()
                }
            }
        )
    }
}