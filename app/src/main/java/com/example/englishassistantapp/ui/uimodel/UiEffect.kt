package com.example.englishassistantapp.ui.uimodel

sealed class UiEffect {
    data class SpeakMessage(val mes: String): UiEffect()

    object StartRecognition: UiEffect()

    object StopRecognition: UiEffect()

    object FinishAssistantConversation: UiEffect()
}