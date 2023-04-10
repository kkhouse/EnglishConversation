package com.example.englishassistantapp.domain.network

import com.aallam.openai.client.*
import com.example.englishassistantapp.domain.network.internal.OpenAIApi
import com.example.englishassistantapp.domain.network.internal.createHttpClient
import com.example.englishassistantapp.domain.network.internal.http.HttpTransport

/**
 * OpenAI API.
 */
public interface OpenAI : Completions,Chat, AutoCloseable

/**
 * Creates an instance of [OpenAI].
 *
 * @param token secret API key
 */
public fun OpenAI(token: String): OpenAI {
    val config = OpenAIConfig(token = token)
    return OpenAI(config)
}

/**
 * Creates an instance of [OpenAI].
 *
 * @param config client config
 */
public fun OpenAI(config: OpenAIConfig): OpenAI {
    val httpClient = createHttpClient(config)
    val transport = HttpTransport(httpClient)
    return OpenAIApi(transport)
}
