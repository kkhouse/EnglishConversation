package com.example.englishassistantapp.ui.logic

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.englishassistantapp.ui.uimodel.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val handler: UiStateHandler,
    private val _uiState: MutableStateFlow<UiState>
) : ViewModel() {

    val uiState = _uiState.stateIn(viewModelScope, SharingStarted.Eagerly, _uiState.value)

    init {
        Log.d("TAGTAGTAG", ": viewMOdel init ")
        viewModelScope.launch {
            handler.initUiState(_uiState.value).collect { newState ->
                _uiState.value = newState
            }
        }
    }

    fun finishedSpeaking(content: String) {
        viewModelScope.launch {
            handler.addNewMessage(
                currentState = _uiState.value,
                message = content
            ).collect { newState ->
                _uiState.value = newState
            }
        }
    }
}