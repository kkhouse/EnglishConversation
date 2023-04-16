package com.example.englishassistantapp.domain.network

import com.aallam.openai.api.BetaOpenAI
import com.example.englishassistantapp.domain.network.model.ChatCompletion
import com.example.englishassistantapp.domain.network.model.ChatCompletionChunk
import com.example.englishassistantapp.domain.network.model.ChatCompletionRequest
import kotlinx.coroutines.flow.Flow

/**
 * Given a chat conversation, the model will return a chat completion response.
 */
public interface Chat {

    /**
     * Creates a completion for the chat message.
     */
    @BetaOpenAI
    public suspend fun chatCompletion(request: ChatCompletionRequest): ChatCompletion

    /**
     * Stream variant of [chatCompletion].
     */
    @BetaOpenAI
    public fun chatCompletions(request: ChatCompletionRequest): Flow<ChatCompletionChunk>
}
