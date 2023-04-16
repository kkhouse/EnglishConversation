@file:OptIn(BetaOpenAI::class)

package com.example.englishassistantapp.domain

import com.aallam.openai.api.BetaOpenAI
import com.example.englishassistantapp.domain.network.NetworkProcessor
import com.example.englishassistantapp.domain.network.model.ChatCompletion
import javax.inject.Inject

interface MainRepository {
    suspend fun getMessage(): Result<ChatCompletion>

    suspend fun postMessage(message: String): Result<ChatCompletion>
}

class MainRepositoryImpl @Inject constructor(
    private val networkProcessor: NetworkProcessor
): MainRepository {
    override suspend fun getMessage(): Result<ChatCompletion> {
        return networkProcessor.getMessage()
    }

    override suspend fun postMessage(message: String): Result<ChatCompletion> = networkProcessor.postMessage(message)
}