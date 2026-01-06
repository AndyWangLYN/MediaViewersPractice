package com.example.practiceplayers

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.practiceplayers.ui.theme.PracticePlayersTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            PracticePlayersTheme {
                // create a controlled NavController
                val navController = rememberNavController()
                // NavHost: controlled by navController
                NavHost(
                    navController = navController,
                    startDestination = Screen.Home.route
                ) {
                    // Home screen
                    composable(route = Screen.Home.route) {
                        HomeScreen(
                            onVideoClicked = {
                                navController.navigate(Screen.Video.route)
                            },
                            onImageClicked = {
                                navController.navigate(Screen.Images.route)
                            },
                            onDocumentClicked = {
                                navController.navigate(Screen.Documents.route)
                            }
                        )
                    }
                    // Video Screen
                    composable(route = Screen.Video.route) {
                        VideoScreen(navController)
                    }
                    // video full screen
                    composable(route = Screen.FullScreenVideo.route) {
                        FullscreenVideoScreen(navController)
                    }

                    // Images Screen
//                    composable(route = Screen.Images.route) {
//                        ImagesScreen()
//                    }
//                    // Documents Screen
//                    composable(route = Screen.Documents.route) {
//                        DocumentsScreen()
//                    }
                }
            }
        }
    }
}

@Composable
fun HomeScreen(
    onVideoClicked: () -> Unit = {},
    onImageClicked: () -> Unit = {},
    onDocumentClicked: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            ProjectButton(
                buttonText = VIDEO_BUTTON_TEXT,
                onClick = {
                    onVideoClicked.invoke()
                },
                modifier = Modifier.fillMaxWidth(0.6f)
            )
            ProjectButton(
                buttonText = IMAGE_BUTTON_TEXT,
                onClick = {
                    onImageClicked.invoke()
                },
                modifier = Modifier.fillMaxWidth(0.6f)
            )
            ProjectButton(
                buttonText = DOCUMENT_BUTTON_TEXT,
                onClick = {
                    onDocumentClicked.invoke()
                },
                modifier = Modifier.fillMaxWidth(0.6f)
            )
        }
    }
}

@Composable
fun ProjectButton(
    buttonText: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Button(onClick = onClick, modifier = modifier) {
        Text(buttonText)
    }
}

fun Context.findActivity(): Activity? {
    var context = this
    while (context is ContextWrapper) {
        if (context is Activity) return context
        context = context.baseContext
    }
    return null
}
