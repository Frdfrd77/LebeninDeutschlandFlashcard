package com.example.lebenindeutschlandflashcard

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.*
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.lebenindeutschlandflashcard.ui.screens.FlashcardScreen
import com.example.lebenindeutschlandflashcard.ui.screens.SplashScreen
import com.example.lebenindeutschlandflashcard.ui.screens.StartScreen
import com.example.lebenindeutschlandflashcard.ui.theme.LebenInDeutschlandFlashcardTheme
import com.example.lebenindeutschlandflashcard.viewmodel.FlashcardViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            LebenInDeutschlandFlashcardTheme {
                val viewModel: FlashcardViewModel = viewModel()
                var currentScreen by remember { mutableStateOf("splash") }

                when (currentScreen) {
                    "splash" -> {
                        SplashScreen(onTimeout = { currentScreen = "start" })
                    }
                    "start" -> {
                        StartScreen(onStart = { currentScreen = "flashcard" })
                    }
                    "flashcard" -> {
                        FlashcardScreen(viewModel, onBackToMenu = { currentScreen = "start" })
                    }
                }
            }
        }
    }
}
