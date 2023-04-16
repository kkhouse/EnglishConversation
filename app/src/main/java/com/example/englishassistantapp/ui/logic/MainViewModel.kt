package com.example.englishassistantapp.ui.logic

import androidx.compose.runtime.collectAsState
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.englishassistantapp.ui.uimodel.Message
import com.example.englishassistantapp.ui.uimodel.UiEffect
import com.example.englishassistantapp.ui.uimodel.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val handler: UiStateHandler,
    private val _uiState: MutableStateFlow<UiState>,
    private val effectChannel: Channel<UiEffect>
) : ViewModel() {

    val uiState = _uiState.stateIn(viewModelScope, SharingStarted.Eagerly, _uiState.value)
    val effect = effectChannel.receiveAsFlow()

    init { collectNewState { handler.initUiState(_uiState.value) } }

    fun beSpeakingConversation(content: String) {
        collectNewState {
            handler.addNewMessage(_uiState.value, content)
        }
    }

    fun finishedSpeaking(content: String) {
        collectNewState {
            handler.postNewMessage(
                currentState = _uiState.value,
                effectChannel = effectChannel,
                message = content
            )
        }
    }

    fun speechSingleMessage(msg : Message) {
        viewModelScope.launch {
            effectChannel.send(UiEffect.SpeakMessage(msg.content))
        }
    }

    fun takeOnMic() {
        collectNewState {
            handler.handleMicState(
                currentState = _uiState.value,
                effectChannel = effectChannel,
                isOn = true
            )
        }
    }

    fun takeOffMic() {
        collectNewState {
            handler.handleMicState(
                currentState = _uiState.value,
                effectChannel = effectChannel,
                isOn = false
            )
        }
    }

    private fun collectNewState(f: suspend () -> Flow<UiState>) {
        viewModelScope.launch {
            f().collect { _uiState.value = it }
        }
    }
}

