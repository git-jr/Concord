package com.alura.concord.ui.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.graphics.toColorInt
import com.alura.concord.R
import com.alura.concord.data.Author
import com.alura.concord.data.messageListSample
import com.alura.concord.ui.components.MessageItemAi
import com.alura.concord.ui.components.MessageItemLoad
import com.alura.concord.ui.components.MessageItemUser

@Composable
fun ChatScreen(
    state: ChatScreenUiState,
    modifier: Modifier = Modifier,
    onSendMessage: () -> Unit = {},
    onShowSelectorFile: () -> Unit = {},
    onShowSelectorStickers: () -> Unit = {},
) {
    Scaffold { paddingValues ->
        Column(modifier.padding(paddingValues)) {
            LazyColumn(
                Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .weight(8f), reverseLayout = true
            ) {
                items(state.messages.reversed(), contentType = { it.author }) { it ->
                    when (it.author) {
                        Author.AI -> {
                            MessageItemAi(value = it.content)
                        }

                        Author.USER -> {
                            MessageItemUser(value = it.content)
                        }

                        Author.LOAD -> {
                            MessageItemLoad()
                        }
                    }

                }
            }
            Spacer(modifier = Modifier.height(4.dp))
            EntryTextBar(
                state,
                onShowSelectorFile = onShowSelectorFile,
                onClickSendMessage = onSendMessage,
                onAcessSticker = onShowSelectorStickers
            )
        }
    }
}


@Composable
@OptIn(ExperimentalMaterial3Api::class)
private fun EntryTextBar(
    state: ChatScreenUiState,
    onShowSelectorFile: () -> Unit = {},
    onClickSendMessage: () -> Unit = {},
    onAcessSticker: () -> Unit = {},
) {
    Row(
        modifier = Modifier
            .height(56.dp)
            .background(Color("#FFE9EFFD".toColorInt())),
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = onAcessSticker) {
            Icon(
                Icons.Filled.Face,
                "file",
                tint = Color("#FF567AF4".toColorInt()),
                modifier = Modifier
                    .weight(1f)
            )
        }

        TextField(
            placeholder = {
                Text(text = "Manda aí!", color = Color.Gray)
            },
            value = state.messageValue,
            onValueChange = state.onMessageValueChange,
            modifier = Modifier
                .weight(5f)
                .background(color = Color.Transparent),

            // For future reference: I know... "use BasicText" instead od doing all that customization, but, just this time:
            colors = TextFieldDefaults.textFieldColors(
                containerColor = Color.Transparent,
                focusedIndicatorColor = Color.Transparent,
                disabledIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent
            )

        )

        IconButton(onClick = onShowSelectorFile) {
            Icon(
                painterResource(id = R.drawable.ic_action_files),
                "file",
                tint = Color("#FF567AF4".toColorInt()),
                modifier = Modifier
                    .weight(1f)
            )
        }

        IconButton(onClick = onClickSendMessage) {
            Icon(
                Icons.Filled.Send,
                "send",
                tint = Color("#FF567AF4".toColorInt()),
                modifier = Modifier
                    .weight(1f)
            )
        }
    }
}

@Preview
@Composable
fun ChatScreenPreview() {
    ChatScreen(
        ChatScreenUiState(
            messages = messageListSample,
        )
    )
}