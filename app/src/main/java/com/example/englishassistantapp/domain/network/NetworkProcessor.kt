@file:OptIn(BetaOpenAI::class)

package com.example.englishassistantapp.domain.network

import com.aallam.openai.api.BetaOpenAI
import com.aallam.openai.api.chat.ChatRole
import com.aallam.openai.api.chat.ChatRole.Companion.Assistant
import com.aallam.openai.api.chat.ChatRole.Companion.User
import com.aallam.openai.api.model.ModelId
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

    override suspend fun getMessage(): Result<ChatCompletion> {
        return withContext(coroutineContext) {
            kotlin.runCatching {
//                openAI.chatCompletion(chatHandler.createRequest(null))
                mockChatCompletion
            }.onSuccess { chatCompletion -> chatHandler.handleResponse(chatCompletion) }
        }
    }

    override suspend fun postMessage(message: String): Result<ChatCompletion> {
        return withContext(coroutineContext) {
            kotlin.runCatching {
//                openAI.chatCompletion(chatHandler.createRequest(message))
                mockChatCompletion
            }.onSuccess { chatCompletion -> chatHandler.handleResponse(chatCompletion) }
        }
    }
}


private val mockChatCompletion = ChatCompletion(
    id = "0", created = 1, model = ModelId("mockModelId"),
    choices = listOf(
        ChatChoice(
            index = 1, message = ChatMessage(
                role = ChatRole(role = Assistant.role), content = "Hi Im Assistant role", name = null

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