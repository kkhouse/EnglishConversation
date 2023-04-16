package com.example.englishassistantapp.domain.network.internal.api

import com.aallam.openai.api.BetaOpenAI
import com.example.englishassistantapp.domain.network.model.ChatCompletion
import com.example.englishassistantapp.domain.network.model.ChatCompletionChunk
import com.example.englishassistantapp.domain.network.model.ChatCompletionRequest
import com.example.englishassistantapp.domain.network.Chat
import com.example.englishassistantapp.domain.network.internal.extension.streamEventsFrom
import com.example.englishassistantapp.domain.network.internal.extension.streamRequestOf
import com.example.englishassistantapp.domain.network.internal.http.HttpRequester
import com.example.englishassistantapp.domain.network.internal.http.perform
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.http.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

@OptIn(BetaOpenAI::class)
internal class ChatApi(private val requester: HttpRequester) : Chat {
    override suspend fun chatCompletion(request: ChatCompletionRequest): ChatCompletion {
        return requester.perform {
            it.post {
                url(path = ChatCompletionsPathV1)
                setBody(request)
                contentType(ContentType.Application.Json)
            }.body()
        }
    }

    override fun chatCompletions(request: ChatCompletionRequest): Flow<ChatCompletionChunk> {
        val builder = HttpRequestBuilder().apply {
            method = HttpMethod.Post
            url(path = ChatCompletionsPathV1)
            setBody(streamRequestOf(request))
            contentType(ContentType.Application.Json)
            accept(ContentType.Text.EventStream)
            headers {
                append(HttpHeaders.CacheControl, "no-cache")
                append(HttpHeaders.Connection, "keep-alive")
            }
        }
        return flow {
            requester.perform(builder) { response -> streamEventsFrom(response) }
        }
    }

    companion object {
        private const val ChatCompletionsPathV1 = "v1/chat/completions"
    }
}
