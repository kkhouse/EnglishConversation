package com.example.englishassistantapp.ui.compose.utils

import android.content.Context
import android.speech.tts.TextToSpeech
import android.util.Log
import java.util.*

object TextToSpeechFactory {
    fun create(
        context: Context,
        locale: Locale= Locale.ENGLISH
    ) : TextToSpeech {
        var status: Int = TextToSpeech.ERROR
        val textToSpeech = TextToSpeech(context) { status = it }
        return textToSpeech.apply {
            if(status != TextToSpeech.ERROR) {
                language = locale
            } else {
                Log.d("Error", "create: init error")
            }
        }
    }
}