package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.screens.CompletedScreen
import com.example.ui.screens.DrawingCoachSessionScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.viewmodel.AppScreen
import com.example.ui.viewmodel.DrawingCoachViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // STEP 1: Configure App Check with launch intent before any UI or Firebase operations
        configureAppCheck(this, intent)

        enableEdgeToEdge()

        setContent {
            MyApplicationTheme {
                Surface(
                    modifier = Modifier
                        .fillMaxSize()
                        .safeDrawingPadding()
                        .background(Color(0xFF13141C)),
                    color = Color(0xFF13141C)
                ) {
                    val coachViewModel: DrawingCoachViewModel = viewModel {
                        DrawingCoachViewModel(applicationContext)
                    }
                    ArtCoachApp(coachViewModel)
                }
            }
        }
    }
}

@Composable
fun ArtCoachApp(viewModel: DrawingCoachViewModel) {
    val screen by viewModel.screen.collectAsState()

    when (screen) {
        AppScreen.HOME -> {
            HomeScreen(
                viewModel = viewModel,
                onStartLesson = { subject ->
                    viewModel.startSession(subject)
                }
            )
        }
        AppScreen.SESSION -> {
            DrawingCoachSessionScreen(
                viewModel = viewModel,
                onExit = {
                    viewModel.exitSession()
                }
            )
        }
        AppScreen.COMPLETED -> {
            CompletedScreen(
                viewModel = viewModel,
                onReturnHome = {
                    viewModel.exitSession()
                }
            )
        }
    }
}
