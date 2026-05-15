package com.example.lebenindeutschlandflashcard.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.automirrored.rounded.ArrowForward
import androidx.compose.material.icons.rounded.Home
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.lebenindeutschlandflashcard.ui.components.Flashcard
import com.example.lebenindeutschlandflashcard.viewmodel.FlashcardViewModel

@Composable
fun FlashcardScreen(viewModel: FlashcardViewModel, onBackToMenu: () -> Unit) {
    val questions by viewModel.questions.collectAsStateWithLifecycle()
    val currentIndex by viewModel.currentIndex.collectAsStateWithLifecycle()
    val isFlipped by viewModel.isFlipped.collectAsStateWithLifecycle()
    val selectedOptionIndex by viewModel.selectedOptionIndex.collectAsStateWithLifecycle()
    val correctCount by viewModel.correctCount.collectAsStateWithLifecycle()
    val incorrectCount by viewModel.incorrectCount.collectAsStateWithLifecycle()

    var searchQuery by remember { mutableStateOf("") }
    val focusManager = LocalFocusManager.current

    val context = LocalContext.current
    val bgResId = context.resources.getIdentifier("bg_germany", "drawable", context.packageName)

    Box(modifier = Modifier.fillMaxSize()) {
        if (bgResId != 0) {
            Image(
                painter = painterResource(id = bgResId),
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
        }

        Scaffold(
            modifier = Modifier.fillMaxSize(),
            containerColor = Color.Transparent,
            topBar = {
                if (questions.isNotEmpty()) {
                    Surface(
                        modifier = Modifier.fillMaxWidth().statusBarsPadding(),
                        color = MaterialTheme.colorScheme.surface.copy(alpha = 0.7f),
                        shadowElevation = 4.dp
                    ) {
                        Column {
                            Row(
                                modifier = Modifier
                                    .padding(horizontal = 16.dp, vertical = 8.dp)
                                    .fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                IconButton(onClick = onBackToMenu) {
                                    Icon(Icons.Rounded.Home, contentDescription = "Home", tint = MaterialTheme.colorScheme.primary)
                                }
                                OutlinedTextField(
                                    value = searchQuery,
                                    onValueChange = { searchQuery = it },
                                    modifier = Modifier.weight(1f),
                                    placeholder = { Text("Frage Nr.") },
                                    leadingIcon = { Icon(Icons.Rounded.Search, contentDescription = null) },
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                    singleLine = true,
                                    shape = RoundedCornerShape(12.dp)
                                )
                                Button(
                                    onClick = {
                                        val index = searchQuery.toIntOrNull()?.minus(1)
                                        if (index != null && index in questions.indices) {
                                            viewModel.jumpToQuestion(index)
                                            searchQuery = ""
                                            focusManager.clearFocus()
                                        }
                                    },
                                    shape = RoundedCornerShape(12.dp)
                                ) {
                                    Text("Los")
                                }
                            }
                            
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(bottom = 8.dp),
                                horizontalArrangement = Arrangement.Center
                            ) {
                                Text("Richtig: $correctCount", color = Color(0xFF2E7D32), fontWeight = FontWeight.Bold)
                                Spacer(modifier = Modifier.width(24.dp))
                                Text("Falsch: $incorrectCount", color = Color(0xFFC62828), fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            },
            bottomBar = {
                if (questions.isNotEmpty()) {
                    BottomNav(
                        onPrevious = { viewModel.previousQuestion() },
                        onNext = { viewModel.nextQuestion() },
                        currentIndex = currentIndex + 1,
                        totalCount = questions.size
                    )
                }
            }
        ) { innerPadding ->
            Box(modifier = Modifier.fillMaxSize().padding(innerPadding), contentAlignment = Alignment.Center) {
                if (questions.isNotEmpty()) {
                    Flashcard(
                        question = questions[currentIndex],
                        isFlipped = isFlipped,
                        selectedOptionIndex = selectedOptionIndex,
                        onFlip = { viewModel.toggleFlip() },
                        onOptionSelected = { viewModel.selectOption(it) },
                        onSpeak = { text, isAnswer -> viewModel.speak(text, isAnswer) }
                    )
                }
            }
        }
    }
}

@Composable
fun BottomNav(onPrevious: () -> Unit, onNext: () -> Unit, currentIndex: Int, totalCount: Int) {
    Surface(
        modifier = Modifier.fillMaxWidth().padding(16.dp).padding(bottom = 32.dp),
        color = MaterialTheme.colorScheme.surface.copy(alpha = 0.85f),
        shape = RoundedCornerShape(16.dp),
        shadowElevation = 8.dp
    ) {
        Row(
            modifier = Modifier.padding(12.dp).fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            FilledTonalIconButton(onClick = onPrevious) { Icon(Icons.AutoMirrored.Rounded.ArrowBack, contentDescription = null) }
            Text("$currentIndex / $totalCount", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
            FilledTonalIconButton(onClick = onNext) { Icon(Icons.AutoMirrored.Rounded.ArrowForward, contentDescription = null) }
        }
    }
}
