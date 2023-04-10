package com.example.englishassistantapp.domain.network.internal

import com.aallam.openai.client.*
import com.aallam.openai.client.internal.api.*
import com.example.englishassistantapp.domain.network.internal.http.HttpRequester
import com.example.englishassistantapp.domain.network.OpenAI
import com.example.englishassistantapp.domain.network.Chat
import com.example.englishassistantapp.domain.network.Completions
import com.example.englishassistantapp.domain.network.internal.api.ChatApi
import com.example.englishassistantapp.domain.network.internal.api.CompletionsApi

/**
 * Implementation of [OpenAI].
 *
 * @param requester http transport layer
 */
internal class OpenAIApi(
    private val requester: HttpRequester
) : OpenAI,
    Completions by CompletionsApi(requester),
    Chat by ChatApi(requester),
    AutoCloseable by requester
