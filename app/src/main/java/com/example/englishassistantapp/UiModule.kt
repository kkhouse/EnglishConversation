package com.example.englishassistantapp

import com.example.englishassistantapp.domain.MainRepository
import com.example.englishassistantapp.ui.uimodel.UiState
import com.example.englishassistantapp.ui.logic.UiStateHandler
import com.example.englishassistantapp.ui.logic.UiStateHandlerImpl
import com.example.englishassistantapp.ui.uimodel.UiEffect
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
class UiModule {

    @Singleton
    @Provides
    internal fun provideUiState(): MutableStateFlow<UiState> {
        return MutableStateFlow(UiState())
    }

    @Singleton
    @Provides
    internal fun provideUiEffect(): Channel<UiEffect> {
        return Channel(Channel.UNLIMITED)
    }

    @Singleton
    @Provides
    internal fun provideUiStateHandler(
        repository: MainRepository
    ): UiStateHandler {
        return UiStateHandlerImpl(
            repository = repository,
            chatCache = mutableListOf()
        )
    }
}