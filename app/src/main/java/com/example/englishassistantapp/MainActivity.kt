package com.example.englishassistantapp

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.*
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.aallam.openai.api.BetaOpenAI
import com.aallam.openai.api.chat.ChatCompletionRequest
import com.example.englishassistantapp.domain.network.model.ChatMessage
import com.aallam.openai.api.chat.ChatRole
import com.aallam.openai.api.logging.LogLevel
import com.aallam.openai.api.model.ModelId
import com.example.englishassistantapp.domain.network.OpenAI
import com.example.englishassistantapp.domain.network.OpenAIConfig
import com.example.englishassistantapp.ui.compose.Conversation
import com.example.englishassistantapp.ui.theme.EnglishAssistantAppTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    companion object {
        private const val PERMISSIONS_RECORD_AUDIO = 1000
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            EnglishAssistantAppTheme {
                // TODO
                val granted = ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO)
                var grant by remember { mutableStateOf(granted) }
                if (granted != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.RECORD_AUDIO), PERMISSIONS_RECORD_AUDIO)
                }

                if(grant ==  PackageManager.PERMISSION_GRANTED) {
                    Conversation()
                }
            }
        }
    }
}