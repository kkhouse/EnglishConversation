@file:OptIn(BetaOpenAI::class)

package com.example.englishassistantapp.ui.logic

import android.util.Log
import com.aallam.openai.api.BetaOpenAI
import com.example.englishassistantapp.domain.MainRepository
import com.example.englishassistantapp.domain.network.model.ChatChoice
import com.example.englishassistantapp.ui.uimodel.Author
import com.example.englishassistantapp.ui.uimodel.Message
import com.example.englishassistantapp.ui.uimodel.UiState
import kotlinx.coroutines.flow.*
import javax.inject.Inject


interface UiStateHandler {
    suspend fun initUiState(currentState: UiState): Flow<UiState>

    suspend fun addNewMessage(currentState: UiState, message: String): Flow<UiState>
}

class UiStateHandlerImpl @Inject constructor(
    private val repository: MainRepository,
    private val chatCache: MutableList<Message>
): UiStateHandler {

    companion object {
        private val TAG = UiStateHandlerImpl::class.java.simpleName
    }

    override suspend fun initUiState(currentState: UiState): Flow<UiState> {
        Log.d("TAGTAGTAG", ": uiStateHandler initUiState ")
        return flowOn(
            onStart = { emit(currentState.copy(isLoading = true)) },
            main = {
                repository.getMessage()
                    .onFailure { emit(currentState.copy(isLoading = false, failedMessage = it.message)) }
                    .onSuccess { chatCompletion ->
                        val messages = createNewMessages(chatCompletion.choices)
                        chatCache.addAll(messages)
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
            onStart = { emit(currentState.copy(isLoading = true)) },
            main = {
                repository.postMessage(message = message)
                    .onFailure { emit(currentState.copy(isLoading = false, failedMessage = it.message)) }
                    .onSuccess { chatCompletion ->
                        val messages = createNewMessages(chatCompletion.choices)
                        saveMessageCache(messages = messages)
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

    private fun createNewMessages(choices: List<ChatChoice>) : List<Message> {
        return choices.fold(mutableListOf()) { acc, chatChoice ->
            chatChoice.message?.let { message ->
                acc.add(Message(author = Author.fromRole(message.role), content = message.content))
            }
            Log.d(TAG, "initUiState: choices fold acc : $acc")
            acc
        }
    }

    private fun saveMessageCache(messages: List<Message>) {
        when(chatCache.size) {
            0 -> chatCache.addAll(messages)
            else -> chatCache.add(messages[messages.lastIndex])
        }
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