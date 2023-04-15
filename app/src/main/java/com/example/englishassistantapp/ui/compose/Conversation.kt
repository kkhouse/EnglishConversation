@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.englishassistantapp.ui.compose

import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.LastBaseline
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.englishassistantapp.ui.compose.utils.messageFormatter
import com.example.englishassistantapp.ui.logic.MainViewModel
import com.example.englishassistantapp.ui.theme.EnglishAssistantAppTheme
import com.example.englishassistantapp.ui.uimodel.Author
import com.example.englishassistantapp.ui.uimodel.Author.Companion.toValue
import com.example.englishassistantapp.ui.uimodel.Message
import dagger.hilt.android.lifecycle.HiltViewModel
import androidx.lifecycle.viewmodel.compose.viewModel as injectedViewModel

@Composable
fun Conversation(
    modifier: Modifier = Modifier,
    viewModel: MainViewModel = androidx.lifecycle.viewmodel.compose.viewModel()
) {
    val state = viewModel.uiState.collectAsState().value
    val scrollState = rememberLazyListState()
    val topBarState = rememberTopAppBarState()
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(topBarState)

    Scaffold(
        topBar = { ChannelNameBar() },
        contentWindowInsets =  ScaffoldDefaults
            .contentWindowInsets
            .exclude(WindowInsets.navigationBars),
//            .exclude(WindowInsets.ime),
        modifier = modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        floatingActionButton = {}
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            Messages(messages = state.messages, scrollState = scrollState, modifier = Modifier.weight(1f))
            ConversationButton(
                onSpeakingEnd = viewModel::finishedSpeaking,
                onRecognitionError = {},
                isMicTapDisable = false
            )
        }
    }
}

@Composable
fun Messages(
    messages: List<Message>,
    scrollState: LazyListState,
    modifier: Modifier,
//    onMessageTap: (Message) -> Unit, TODO
) {
    Box(modifier = modifier) {
        LazyColumn(
            state = scrollState,
            modifier = Modifier.fillMaxSize()
        ) {
            messages.forEachIndexed { index, content ->
                val prevAuthor = messages.getOrNull(index - 1)?.author
                val nextAuthor = messages.getOrNull(index + 1)?.author
                val isFirstMessageByAuthor = prevAuthor != content.author
                val isLastMessageByAuthor = nextAuthor != content.author
                item {
                    Message(
                        msg = content,
                        isUserMe = content.author == Author.Me,
                        isFirstMessageByAuthor = isFirstMessageByAuthor,
                        isLastMessageByAuthor = isLastMessageByAuthor
                    )
                }
            }
        }
    }
}

@Composable
fun Message(
    msg: Message,
    isUserMe: Boolean,
    isFirstMessageByAuthor: Boolean,
    isLastMessageByAuthor: Boolean
) {
    val borderColor = if (isUserMe) {
        MaterialTheme.colorScheme.primary
    } else {
        MaterialTheme.colorScheme.tertiary
    }
    val spaceBetweenAuthors = if (isLastMessageByAuthor) Modifier.padding(top = 8.dp) else Modifier
    Row(modifier = spaceBetweenAuthors) {
        when(isLastMessageByAuthor) {
            true -> {
                Image(
                    modifier = Modifier
                        .padding(horizontal = 16.dp)
                        .size(42.dp)
                        .border(1.5.dp, borderColor, CircleShape)
                        .border(3.dp, MaterialTheme.colorScheme.surface, CircleShape)
                        .clip(CircleShape)
                        .align(Alignment.Top),
                    painter = painterResource(id = msg.authorImage),
                    contentScale = ContentScale.Crop,
                    contentDescription = null,
                )
            }
            else -> {
                Spacer(modifier = Modifier.width(74.dp))
            }
        }
        AuthorAndTextMessage(
            msg = msg,
            isUserMe = isUserMe,
            isFirstMessageByAuthor = isFirstMessageByAuthor,
            isLastMessageByAuthor = isLastMessageByAuthor,
            modifier = Modifier
                .padding(end = 16.dp)
                .weight(1f)
        )
    }
}

@Composable
fun AuthorAndTextMessage(
    msg: Message,
    isUserMe: Boolean,
    isFirstMessageByAuthor: Boolean,
    isLastMessageByAuthor: Boolean,
    modifier: Modifier
) {
    Column(modifier = modifier) {
        if (isLastMessageByAuthor) {
            Text(
                text = msg.author.toValue(),
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.paddingFrom(LastBaseline, after = 8.dp) // Space to 1st bubble
            )
        }
        ChatItemBubble(msg, isUserMe)
        if (isFirstMessageByAuthor) {
            // Last bubble before next author
            Spacer(modifier = Modifier.height(8.dp))
        } else {
            // Between bubbles
            Spacer(modifier = Modifier.height(4.dp))
        }
    }
}

private val ChatBubbleShape = RoundedCornerShape(4.dp, 20.dp, 20.dp, 20.dp)

@Composable
fun ChatItemBubble(
    message: Message,
    isUserMe: Boolean
) {
    val backgroundBubbleColor = if (isUserMe) {
        MaterialTheme.colorScheme.primary
    } else {
        MaterialTheme.colorScheme.surfaceVariant
    }

    Column {
        Surface(
            color = backgroundBubbleColor,
            shape = ChatBubbleShape
        ) {
        val styledMessage = messageFormatter(
            text = message.content,
            primary = isUserMe
        )

        ClickableText(
            text = styledMessage,
            style = MaterialTheme.typography.bodyLarge.copy(color = LocalContentColor.current),
            modifier = Modifier.padding(16.dp),
            onClick = {
//                styledMessage
//                    .getStringAnnotations(start = it, end = it)
//                    .firstOrNull()
//                    ?.let { annotation ->
//                        when (annotation.tag) {
//                            SymbolAnnotationType.LINK.name -> uriHandler.openUri(annotation.item)
//                            SymbolAnnotationType.PERSON.name -> authorClicked(annotation.item)
//                            else -> Unit
//                        }
//                    }
            }
        )
        }
    }
}

@Preview
@Composable
fun PreviewMessage() {
    EnglishAssistantAppTheme() {
        Messages(
            scrollState = rememberLazyListState(),
            modifier = Modifier.fillMaxSize(),
            messages = listOf(
                Message(author = Author.Assistant, content = "assistant message"),
                Message(author = Author.Me, content = "Me message")
            )
        )
    }
}

