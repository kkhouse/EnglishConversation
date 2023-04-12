package com.example.englishassistantapp.ui.utils

import android.content.Context
import android.content.Intent
import android.media.MediaDrm.OnEventListener
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.util.Log
import java.util.*


private const val TAG = "com.example.englishassistantapp.ui.utils"
object SpeechRecognizerFactory {
    fun create(
        context: Context,
        onReadyForSpeech: (Bundle?) -> Unit = {},
        onBeginningOfSpeech: () -> Unit= {},
        onRmsChanged: (Float) -> Unit= {},
        onBufferReceived: (ByteArray?) -> Unit= {},
        onEndOfSpeech: () -> Unit= {},
        onError: () -> Unit= {},
        onResult: (String) -> Unit,
        onPartialResults: (String) -> Unit = {},
        onEvent: (Int, Bundle?) -> Unit = {_, _ -> }
    ): SpeechRecognizer {
        return SpeechRecognizer.createSpeechRecognizer(context).apply {
            setRecognitionListener(
                object : RecognitionListener {
                    override fun onReadyForSpeech(p0: Bundle?) {
                        onReadyForSpeech(p0)
                    }

                    override fun onBeginningOfSpeech() {
                        onBeginningOfSpeech()
                    }

                    override fun onRmsChanged(p0: Float) {
                        onRmsChanged(p0)
                    }

                    override fun onBufferReceived(p0: ByteArray?) {
                        onBufferReceived(p0)
                    }

                    override fun onEndOfSpeech() {
                        onEndOfSpeech()
                    }

                    override fun onError(p0: Int) {
                        Log.d(TAG, "onError: error code $p0")
                        onError()
                    }

                    override fun onResults(p0: Bundle?) {
                        val result = p0?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION).toString()
                        onResult(result)
                    }

                    override fun onPartialResults(p0: Bundle?) {
                        val result = p0?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION).toString()
                        onPartialResults(result)
                    }

                    override fun onEvent(errorType: Int, params: Bundle?) {
                        onEvent(errorType, params)
                    }

                }
            )
        }
    }

    fun getRecognizerIntent(): Intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
        putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
        putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.ENGLISH)
    }
}

