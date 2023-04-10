package com.example.englishassistantapp.ui.utils

import android.content.Context
import android.content.Intent
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import java.util.*

object SpeechRecognizerFactory {
    fun create(
        context: Context,
        recognizerListener: RecognitionListener
    ): SpeechRecognizer {
        return SpeechRecognizer.createSpeechRecognizer(context).apply {
            setRecognitionListener(recognizerListener)
        }
    }

    fun getRecognizerIntent(): Intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
        putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
        putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.ENGLISH)
    }
}