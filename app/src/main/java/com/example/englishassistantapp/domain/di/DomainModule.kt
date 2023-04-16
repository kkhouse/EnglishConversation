package com.example.englishassistantapp.domain.di

import com.aallam.openai.api.BetaOpenAI
import com.example.englishassistantapp.domain.network.model.ChatMessage
import com.example.englishassistantapp.domain.network.model.ChatRole
import com.aallam.openai.api.logging.LogLevel
import com.example.englishassistantapp.BuildConfig
import com.example.englishassistantapp.domain.MainRepository
import com.example.englishassistantapp.domain.MainRepositoryImpl
import com.example.englishassistantapp.domain.network.*
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.Dispatchers


@InstallIn(SingletonComponent::class)
@Module
class DomainModule {
    @Provides
    internal fun provideNetworkProcessor(
        chatHandler: ChatCompletionHandler
    ): NetworkProcessor {
        return NetworkProcessorImpl(
            openAI = OpenAI(OpenAIConfig(token = BuildConfig.OPENAI_API_KEY, LogLevel.All)),
            chatHandler = chatHandler,
            coroutineContext = Dispatchers.IO
        )
    }

    @OptIn(BetaOpenAI::class)
    @Provides
    internal fun provideHandler(): ChatCompletionHandler {
        return ChatCompletionHandlerImpl(
            chatMessageCache = mutableListOf(
                ChatMessage(
                    role = ChatRole.System,
                    content = "You are a helpful and friendly assistant that helps people practice their English conversation skills.Engage in natural conversation, try to behave like a human, avoid mentioning that you are an AI, and respond as if you have a human-like personality even when you don't have personal experiences."
                ),
                ChatMessage(
                    role = ChatRole.User,
                    content = "Hi! I'm Me, Please start with a friendly brief self-introduction and a simple question!"
                ),
            )
        )
    }

    @Provides
    internal fun provideRepository(
        networkProcessor: NetworkProcessor
    ): MainRepository {
        return MainRepositoryImpl(
            networkProcessor = networkProcessor
        )
    }
}
