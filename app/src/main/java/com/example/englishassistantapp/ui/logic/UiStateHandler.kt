@file:OptIn(BetaOpenAI::class)

package com.example.englishassistantapp.ui.logic

import android.util.Log
import com.aallam.openai.api.BetaOpenAI
import com.example.englishassistantapp.domain.MainRepository
import com.example.englishassistantapp.domain.network.model.ChatChoice
import com.example.englishassistantapp.domain.removeBrackets
import com.example.englishassistantapp.ui.uimodel.Author
import com.example.englishassistantapp.ui.uimodel.Message
import com.example.englishassistantapp.ui.uimodel.UiEffect
import com.example.englishassistantapp.ui.uimodel.UiState
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import javax.inject.Inject


interface UiStateHandler {
    suspend fun initUiState(currentState: UiState): Flow<UiState>

    suspend fun addNewMessage(currentState: UiState, message: String): Flow<UiState>

    suspend fun postNewMessage(currentState: UiState, effectChannel: Channel<UiEffect>, message: String): Flow<UiState>

    suspend fun handleMicState(currentState: UiState, effectChannel: Channel<UiEffect>, isOn: Boolean): Flow<UiState>
}

class UiStateHandlerImpl @Inject constructor(
    private val repository: MainRepository,
    private val chatCache: MutableList<Message>
): UiStateHandler {

    companion object {
        private val TAG = UiStateHandlerImpl::class.java.simpleName
    }

    override suspend fun initUiState(currentState: UiState): Flow<UiState> {
        return flowOn(
            onStart = { emit(currentState.copy(isLoading = true)) },
            main = {
                repository.getMessage()
                    .onFailure { emit(currentState.copy(isLoading = false, failedMessage = it.message)) }
                    .onSuccess { chatCompletion ->
                        chatCache.addAll(createNewMessages(chatCompletion.choices, lastUserMessage = null))
                        emit(
                            currentState.copy(
                                messages = chatCache,
                                isLoading = false
                            )
                        )
                    }
            }
        )
    }

    override suspend fun addNewMessage(currentState: UiState, message: String): Flow<UiState> {
        return flowOn(
            main = {
                emit(currentState.copy(recognizedText = message.removeBrackets()))
            }
        )
    }

    override suspend fun postNewMessage(currentState: UiState, effectChannel: Channel<UiEffect>, message: String): Flow<UiState> {
        val lastUserMessage = Message(author = Author.Me, content = message.removeBrackets())
        return flowOn(
            onStart = {
                emit(
                    currentState.copy(
                        isLoading = true,
                        recognizedText = message.removeBrackets(),
                        messages = currentState.messages + lastUserMessage
                    )
                )
            },
            main = {
                repository.postMessage(message = message.removeBrackets())
                    .onFailure {
                        emit(
                            currentState.copy(
                                isLoading = false,
                                failedMessage = it.message,
                                messages = currentState.messages - lastUserMessage
                            )
                        )
                    }
                    .onSuccess { chatCompletion ->
                        chatCache.addAll(createNewMessages(choices = chatCompletion.choices, lastUserMessage = lastUserMessage))
                        emit(
                            currentState.copy(
                                messages = chatCache,
                                isLoading = false
                            )
                        )
                        delay(500)
                        effectChannel.send(UiEffect.FinishAssistantConversation)
                    }
            },
            onCompletion = {
                emit(currentState.copy(recognizedText = ""))
            }
        )
    }

    override suspend fun handleMicState(
        currentState: UiState,
        effectChannel: Channel<UiEffect>,
        isOn: Boolean
    ): Flow<UiState> {
        return flowOn(
            main = {
                when(isOn) {
                    true -> {
                        emit(currentState.copy(isUserSpeaking = true))
                        effectChannel.send(UiEffect.StartRecognition)
                    }
                    else -> {
                        emit(currentState.copy(isUserSpeaking = false))
                        effectChannel.send(UiEffect.StartRecognition)
                    }
                }
            }
        )
    }

    private fun createNewMessages(choices: List<ChatChoice>, lastUserMessage: Message?) : List<Message> {
        val accList = lastUserMessage?.let { mutableListOf(it) } ?: mutableListOf()
        return choices.fold(accList) { acc, chatChoice ->
            chatChoice.message?.let { message ->
                acc.add(Message(author = Author.fromRole(message.role), content = message.content))
            }
            Log.d(TAG, "initUiState: choices fold acc : $acc")
            acc
        }
    }

    private fun saveMessageCache(newMessages: List<Message>) {
        chatCache.addAll(newMessages)
    }
}

private fun <T> flowOn(
    onStart : suspend FlowCollector<T>.() -> Unit = {},
    main: suspend FlowCollector<T>.() -> Unit,
    onEachEmit: suspend (T) -> Unit = {},
    onCompletion: suspend FlowCollector<T>.(cause: Throwable?) -> Unit = {}
) : Flow<T> {
    return flow(main)
        .onStart(onStart)
        .onEach(onEachEmit)
        .onCompletion(onCompletion)
}