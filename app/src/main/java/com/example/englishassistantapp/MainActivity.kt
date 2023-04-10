package com.example.englishassistantapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.aallam.openai.api.BetaOpenAI
import com.aallam.openai.api.chat.ChatCompletionRequest
import com.aallam.openai.api.chat.ChatMessage
import com.aallam.openai.api.chat.ChatRole
import com.aallam.openai.api.logging.LogLevel
import com.aallam.openai.api.model.ModelId
import com.example.englishassistantapp.domain.network.OpenAI
import com.example.englishassistantapp.domain.network.OpenAIConfig
import com.example.englishassistantapp.ui.theme.EnglishAssistantAppTheme

@OptIn(BetaOpenAI::class)
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            EnglishAssistantAppTheme {
                val token = BuildConfig.OPENAI_API_KEY
                val openAI = OpenAI(OpenAIConfig(token, LogLevel.All))
                val chatCompletionRequest = ChatCompletionRequest(
                    model = ModelId("gpt-3.5-turbo"),
                    messages = listOf(
                        ChatMessage(
                            role = ChatRole.System,
                            content = "You are a helpful and friendly assistant that helps people practice their English conversation skills. Engage in natural conversation, try to behave like a human, and avoid mentioning that you are an AI."
                        ),
                        ChatMessage(
                            role = ChatRole.User,
                            content = "Hi there! I'm KK, and I'd like to practice my English conversation skills with you. Can you help me with that?"
                        ),
                        ChatMessage(
                            role = ChatRole.Assistant,
                            content = "Hi KK! Of course, I'd be happy to help you practice your English. Feel free to ask me any questions or start a conversation on any topic you'd like."
                        ),
                        ChatMessage(
                            role = ChatRole.User,
                            content = "Great, thanks! So, how have you been lately?"
                        )
                    ),
                )
                LaunchedEffect(key1 = Unit) {
                    openAI.chatCompletion(chatCompletionRequest).choices.forEach {
                        chatCompletionRequest.messages.forEach { oldMessage ->
                            println("${oldMessage.role}:\n ${oldMessage.content} \n")
                        }
                        println("${it.message?.role}:\n ${it.message?.content} \n")
                    }
                }
            }
        }
    }
}