package com.example.englishassistantapp.domain.di

import com.aallam.openai.api.BetaOpenAI
import com.example.englishassistantapp.domain.network.model.ChatMessage
import com.aallam.openai.api.chat.ChatRole
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
