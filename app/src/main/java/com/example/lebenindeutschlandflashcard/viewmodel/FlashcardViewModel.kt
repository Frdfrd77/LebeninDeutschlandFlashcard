package com.example.lebenindeutschlandflashcard.viewmodel

import android.app.Application
import android.speech.tts.TextToSpeech
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.lebenindeutschlandflashcard.data.AppDatabase
import com.example.lebenindeutschlandflashcard.data.Question
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.Locale

/**
 * ViewModel responsible for managing flashcard state (index, flip status)
 * and Text-to-Speech (TTS) lifecycle.
 */
class FlashcardViewModel(application: Application) : AndroidViewModel(application), TextToSpeech.OnInitListener {

    private val db = AppDatabase.getDatabase(application, viewModelScope)
    private val questionDao = db.questionDao()

    // Question list from database
    val questions: StateFlow<List<Question>> = questionDao.getAllQuestions()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Current card index
    private val _currentIndex = MutableStateFlow(0)
    val currentIndex: StateFlow<Int> = _currentIndex.asStateFlow()

    // Flip status (false = question, true = answer)
    private val _isFlipped = MutableStateFlow(false)
    val isFlipped: StateFlow<Boolean> = _isFlipped.asStateFlow()

    // Selected option index (-1 means no selection)
    private val _selectedOptionIndex = MutableStateFlow(-1)
    val selectedOptionIndex: StateFlow<Int> = _selectedOptionIndex.asStateFlow()

    // Statistics
    private val _correctCount = MutableStateFlow(0)
    val correctCount: StateFlow<Int> = _correctCount.asStateFlow()

    private val _incorrectCount = MutableStateFlow(0)
    val incorrectCount: StateFlow<Int> = _incorrectCount.asStateFlow()

    // Flag to track if user listened to the answer before answering
    private var hasListenedToAnswer = false

    // TTS engine
    private var tts: TextToSpeech? = TextToSpeech(application, this)
    private val _isTtsReady = MutableStateFlow(false)

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            val result = tts?.setLanguage(Locale.GERMANY)
            if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                Log.e("FlashcardViewModel", "Language not supported")
            } else {
                _isTtsReady.value = true
            }
        } else {
            Log.e("FlashcardViewModel", "TTS Initialization failed")
        }
    }

    /**
     * Reads the provided text aloud in German.
     */
    fun speak(text: String, isAnswerSide: Boolean = false) {
        if (_isTtsReady.value) {
            if (isAnswerSide && _selectedOptionIndex.value == -1) {
                hasListenedToAnswer = true
            }
            tts?.speak(text, TextToSpeech.QUEUE_FLUSH, null, null)
        }
    }

    fun toggleFlip() {
        _isFlipped.value = !_isFlipped.value
    }

    fun selectOption(index: Int) {
        if (_selectedOptionIndex.value != -1) return // Already answered

        val currentQuestion = questions.value.getOrNull(_currentIndex.value) ?: return
        _selectedOptionIndex.value = index

        if (index == currentQuestion.correctAnswerIndex) {
            // Only count as correct if they didn't listen to the answer first
            if (!hasListenedToAnswer) {
                _correctCount.value += 1
            }
        } else {
            _incorrectCount.value += 1
        }
    }

    /**
     * Navigates to the next question and resets the flip state.
     */
    fun jumpToQuestion(index: Int) {
        if (index in 0 until questions.value.size) {
            _currentIndex.value = index
            _isFlipped.value = false
            _selectedOptionIndex.value = -1
            hasListenedToAnswer = false
            stopSpeaking()
        }
    }

    fun nextQuestion() {
        val currentList = questions.value
        if (currentList.isNotEmpty()) {
            _currentIndex.value = (_currentIndex.value + 1) % currentList.size
            _isFlipped.value = false
            _selectedOptionIndex.value = -1
            hasListenedToAnswer = false
            stopSpeaking()
        }
    }

    /**
     * Navigates to the previous question and resets the flip state.
     */
    fun previousQuestion() {
        val currentList = questions.value
        if (currentList.isNotEmpty()) {
            _currentIndex.value = if (_currentIndex.value > 0) {
                _currentIndex.value - 1
            } else {
                currentList.size - 1
            }
            _isFlipped.value = false
            _selectedOptionIndex.value = -1
            hasListenedToAnswer = false
            stopSpeaking()
        }
    }

    private fun stopSpeaking() {
        tts?.stop()
    }

    override fun onCleared() {
        super.onCleared()
        tts?.stop()
        tts?.shutdown()
    }
}
