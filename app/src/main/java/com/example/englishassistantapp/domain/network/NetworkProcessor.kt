@file:OptIn(BetaOpenAI::class)

package com.example.englishassistantapp.domain.network

import android.util.Log
import com.aallam.openai.api.BetaOpenAI
import com.example.englishassistantapp.domain.network.model.ChatRole
import com.example.englishassistantapp.domain.network.model.ChatRole.Companion.Assistant
import com.example.englishassistantapp.domain.network.model.ChatRole.Companion.User
import com.aallam.openai.api.model.ModelId
import com.example.englishassistantapp.BuildConfig
import com.example.englishassistantapp.domain.network.model.ChatChoice
import com.example.englishassistantapp.domain.network.model.ChatCompletion
import com.example.englishassistantapp.domain.network.model.ChatMessage
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import javax.inject.Inject

interface NetworkProcessor {
    suspend fun getMessage(): Result<ChatCompletion>
    suspend fun postMessage(message: String): Result<ChatCompletion>
}

class NetworkProcessorImpl @Inject constructor(
    private val openAI: OpenAI,
    private val chatHandler: ChatCompletionHandler,
    private val coroutineContext: CoroutineDispatcher
): NetworkProcessor {

    companion object {
        private const val TAG = "NetworkProcessorImpl"
    }

    override suspend fun getMessage(): Result<ChatCompletion> {
        return withContext(coroutineContext) {
            kotlin.runCatching {
                when(BuildConfig.IS_MOCKED_RESPONSE_NEEDED) {
                    true -> mockChatCompletion
                    else -> openAI.chatCompletion(chatHandler.createRequest(null))
                }
            }.onSuccess { chatCompletion ->
                Log.d(TAG, "getMessage: response is $chatCompletion")
                chatHandler.handleResponse(chatCompletion)
            }
        }
    }

    override suspend fun postMessage(message: String): Result<ChatCompletion> {
        return withContext(coroutineContext) {
            kotlin.runCatching {
                when(BuildConfig.IS_MOCKED_RESPONSE_NEEDED) {
                    true -> mockChatCompletion
                    else -> openAI.chatCompletion(chatHandler.createRequest(message))
                }
            }.onSuccess { chatCompletion -> chatHandler.handleResponse(chatCompletion) }
        }
    }
}


private val mockChatCompletion = ChatCompletion(
    id = "0", created = 1, model = ModelId("mockModelId"),
    choices = listOf(
        ChatChoice(
            index = 1, message = ChatMessage(
                role = ChatRole(role = Assistant.role),
                content = "Hello KK, it's nice to meet you! My name is Sarah, and I'll be happy to help you practice your English conversation skills. How has your day been so far?", name = null

            ), finishReason = null
        ),
        ChatChoice(
            index = 1, message = ChatMessage(
                role = ChatRole(role = User.role), content = "Hi Im User role", name = null

            ), finishReason = null
        )

    ),
    usage = null
)