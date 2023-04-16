@file:OptIn(BetaOpenAI::class)

package com.example.englishassistantapp.ui.uimodel

import com.aallam.openai.api.BetaOpenAI
import com.example.englishassistantapp.domain.network.model.ChatRole
import com.example.englishassistantapp.R

data class UiState(
    val messages : List<Message> = listOf(),
    val isLoading: Boolean = false,
    val failedMessage: String? = null
)

data class Message(
    val author: Author = Author.Me,
    val content: String = "",
    val authorImage: Int = if (author == Author.Me) R.drawable.ic_me_24dp else R.drawable.ic_assistant_24dp,
)

enum class Author {
    Me, Assistant, System;
    companion object {
        fun fromRole(role: ChatRole): Author {
            return when(role) {
                ChatRole.User -> Me
                ChatRole.Assistant -> Assistant
                else -> throw IllegalStateException()
            }
        }
        fun Author.toValue(): String {
            return when(this) {
                Me -> "Me"
                Assistant -> "Assistant"
                System -> throw IllegalStateException()
            }
        }
    }
}
