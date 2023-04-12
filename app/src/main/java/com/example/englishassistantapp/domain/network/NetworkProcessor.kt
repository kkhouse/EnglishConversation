@file:OptIn(BetaOpenAI::class)

package com.example.englishassistantapp.domain.network

import com.aallam.openai.api.BetaOpenAI
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
                openAI.chatCompletion(chatHandler.createRequest(null))
            }.onSuccess { chatCompletion -> chatHandler.handleResponse(chatCompletion) }
        }
    }

    override suspend fun postMessage(message: String): Result<ChatCompletion> {
        return withContext(coroutineContext) {
            kotlin.runCatching {
                openAI.chatCompletion(chatHandler.createRequest(message))
            }.onSuccess { chatCompletion -> chatHandler.handleResponse(chatCompletion) }
        }
    }
}