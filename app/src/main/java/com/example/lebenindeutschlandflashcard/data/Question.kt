package com.example.lebenindeutschlandflashcard.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "questions")
data class Question(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val questionText: String,
    val option1: String,
    val option2: String,
    val option3: String,
    val option4: String,
    val correctAnswerIndex: Int,
    val category: String = "",
    val imageUrl: String? = null
)
