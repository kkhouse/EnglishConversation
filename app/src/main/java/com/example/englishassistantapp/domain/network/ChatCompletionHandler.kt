@file:OptIn(BetaOpenAI::class)

package com.example.englishassistantapp.domain.network

import android.util.Log
import com.aallam.openai.api.BetaOpenAI
import com.example.englishassistantapp.domain.network.model.ChatCompletion
import com.aallam.openai.api.chat.ChatCompletionRequest
import com.example.englishassistantapp.domain.network.model.ChatMessage
import com.aallam.openai.api.chat.ChatRole
import com.aallam.openai.api.model.ModelId
import javax.inject.Inject
import javax.inject.Singleton


private const val TAG = "com.example.englishassistantapp.domain.network.ChatCompletionHandler"

interface ChatCompletionHandler {
    fun handleResponse(data : ChatCompletion): ChatCompletion
    fun createRequest(newContent: String?): ChatCompletionRequest
}

@Singleton
class ChatCompletionHandlerImpl @Inject constructor(
    private val chatMessageCache: MutableList<ChatMessage>
): ChatCompletionHandler {

    override fun handleResponse(data: ChatCompletion): ChatCompletion {
        /**
         * 記憶しておくチャット量は全部で６件まで
         * 超えた場合は一番古いチャットを消しておく
         * UI表示はUI側にて全て記憶しておく。
         */
        when(chatMessageCache.size >= 6) {
            true -> {
                chatMessageCache.removeAt(1)
                chatMessageCache.add(
                    ChatMessage(
                        role = ChatRole.User,
                        content = data.choices.lastOrNull()?.message?.content ?:""
                    )
                )
            }
            else -> {
                chatMessageCache.add(
                    ChatMessage(
                        role = ChatRole.User,
                        content = data.choices.lastOrNull()?.message?.content ?:""
                    )
                )
            }
        }
        Log.d(TAG, "handleResponse: chatMessageCache via response : $chatMessageCache")
        return data
    }

    override fun createRequest(newContent: String?): ChatCompletionRequest {
        newContent?.let { chatMessageCache.add(ChatMessage(role = ChatRole.User, content = it)) }
        Log.d(TAG, "createRequest: chatMessageCache for request : $chatMessageCache")
        return ChatCompletionRequest(model = ModelId("gpt-3.5-turbo"), messages = chatMessageCache)
    }
}