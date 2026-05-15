package com.example.lebenindeutschlandflashcard.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.VolumeUp
import androidx.compose.material.icons.rounded.HelpCenter
import androidx.compose.material.icons.rounded.Image
import androidx.compose.material.icons.rounded.Lightbulb
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.lebenindeutschlandflashcard.data.Question

@Composable
fun Flashcard(
    question: Question,
    isFlipped: Boolean,
    selectedOptionIndex: Int,
    onFlip: () -> Unit,
    onOptionSelected: (Int) -> Unit,
    onSpeak: (String, Boolean) -> Unit
) {
    val rotation by animateFloatAsState(
        targetValue = if (isFlipped) 180f else 0f,
        animationSpec = tween(durationMillis = 500),
        label = "FlashcardFlip"
    )

    Card(
        modifier = Modifier
            .fillMaxWidth(0.9f)
            .fillMaxHeight(0.75f)
            .graphicsLayer {
                rotationY = rotation
                cameraDistance = 12f * density
            }
            .clickable { onFlip() },
        shape = RoundedCornerShape(24.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            if (rotation <= 90f) {
                FlashcardContent(
                    title = "Frage",
                    content = question.questionText,
                    category = question.category,
                    imageUrl = question.imageUrl,
                    onSpeak = { onSpeak(question.questionText, false) }
                )
            } else {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .graphicsLayer { rotationY = 180f }
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Optionen",
                        style = MaterialTheme.typography.headlineSmall,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(bottom = 12.dp)
                    )

                    val options = listOf(question.option1, question.option2, question.option3, question.option4)
                    
                    Column(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        options.forEachIndexed { index, option ->
                            val isCorrect = index == question.correctAnswerIndex
                            val isSelected = index == selectedOptionIndex
                            
                            val backgroundColor = when {
                                selectedOptionIndex == -1 -> MaterialTheme.colorScheme.surface
                                isCorrect -> Color(0xFFE8F5E9)
                                isSelected && !isCorrect -> Color(0xFFFFEBEE)
                                else -> MaterialTheme.colorScheme.surface
                            }

                            val borderColor = when {
                                selectedOptionIndex == -1 -> Color.LightGray
                                isCorrect -> Color(0xFF4CAF50)
                                isSelected && !isCorrect -> Color(0xFFEF5350)
                                else -> Color.LightGray
                            }

                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { 
                                        if (selectedOptionIndex == -1) onOptionSelected(index) 
                                    },
                                colors = CardDefaults.cardColors(containerColor = backgroundColor),
                                border = BorderStroke(if (isSelected || (selectedOptionIndex != -1 && isCorrect)) 2.dp else 1.dp, borderColor),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Row(
                                    modifier = Modifier.padding(12.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "${'A' + index}:",
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.padding(end = 8.dp),
                                        color = if (isCorrect && selectedOptionIndex != -1) Color(0xFF2E7D32) else Color.Unspecified
                                    )
                                    Text(
                                        text = option,
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = if (isCorrect && selectedOptionIndex != -1) Color(0xFF2E7D32) else Color.Unspecified
                                    )
                                }
                            }
                        }
                    }

                    if (selectedOptionIndex != -1) {
                        val isUserCorrect = selectedOptionIndex == question.correctAnswerIndex
                        Text(
                            text = if (isUserCorrect) "Richtig! ✓" else "Falsch! ✗",
                            style = MaterialTheme.typography.titleLarge,
                            color = if (isUserCorrect) Color(0xFF4CAF50) else Color(0xFFEF5350),
                            fontWeight = FontWeight.ExtraBold,
                            modifier = Modifier.padding(vertical = 8.dp)
                        )
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Sound Button (Just reads, doesn't show answer)
                        IconButton(
                            onClick = { onSpeak(getCorrectAnswerText(question), true) },
                            modifier = Modifier.size(48.dp)
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Rounded.VolumeUp,
                                contentDescription = "Antwort vorlesen",
                                tint = MaterialTheme.colorScheme.secondary
                            )
                        }

                        // Hint Button (Shows the answer visually)
                        if (selectedOptionIndex == -1) {
                            IconButton(
                                onClick = { onOptionSelected(question.correctAnswerIndex) },
                                modifier = Modifier.size(48.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Rounded.Lightbulb,
                                    contentDescription = "Antwort zeigen",
                                    tint = Color(0xFFFFB300) // Amber color for lightbulb
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun FlashcardContent(
    title: String,
    content: String,
    category: String,
    imageUrl: String? = null,
    onSpeak: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Surface(
            color = MaterialTheme.colorScheme.primaryContainer,
            shape = RoundedCornerShape(12.dp)
        ) {
            Text(
                text = category,
                style = MaterialTheme.typography.labelMedium,
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
        }

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Bold
            )
            
            if (!imageUrl.isNullOrEmpty()) {
                val context = LocalContext.current
                val isUrl = imageUrl.startsWith("http")
                val imageModel = if (isUrl) {
                    imageUrl
                } else {
                    val resId = context.resources.getIdentifier(
                        imageUrl.lowercase().removeSuffix(".png").removeSuffix(".jpg"),
                        "drawable",
                        context.packageName
                    )
                    if (resId != 0) resId else null
                }

                if (imageModel != null) {
                    AsyncImage(
                        model = ImageRequest.Builder(context)
                            .data(imageModel)
                            .crossfade(true)
                            .build(),
                        contentDescription = "Frage Bild",
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(150.dp)
                            .padding(8.dp),
                        contentScale = ContentScale.Fit
                    )
                } else {
                    Icon(
                        imageVector = Icons.Rounded.Image,
                        contentDescription = "Bild fehlt",
                        modifier = Modifier
                            .size(100.dp)
                            .padding(8.dp),
                        tint = MaterialTheme.colorScheme.outline
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = content,
                style = MaterialTheme.typography.bodyLarge,
                textAlign = TextAlign.Center,
                lineHeight = MaterialTheme.typography.bodyLarge.lineHeight * 1.2
            )
        }

        IconButton(
            onClick = { onSpeak() },
            modifier = Modifier.size(48.dp)
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Rounded.VolumeUp,
                contentDescription = "Vorlesen",
                tint = MaterialTheme.colorScheme.secondary
            )
        }
    }
}

private fun getCorrectAnswerText(question: Question): String {
    return when (question.correctAnswerIndex) {
        0 -> question.option1
        1 -> question.option2
        2 -> question.option3
        3 -> question.option4
        else -> ""
    }
}
