package com.alura.concord.ui.home

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.alura.concord.R
import com.alura.concord.data.Chat
import com.alura.concord.data.chatListSample
import com.alura.concord.ui.components.AsyncImage
import com.alura.concord.ui.theme.ConcordTheme

@Composable
fun ChatListScreen(
    state: ChatListUiState,
    modifier: Modifier = Modifier,
    onClickOpenChat: (Long) -> Unit = {},
    onClickSendNewMessage: () -> Unit = {}
) {
    Scaffold(topBar = {
        AppBarChatList()
    }, floatingActionButton = {
        FloatingActionButton(
            containerColor = MaterialTheme.colorScheme.surfaceVariant,
            onClick = {
                onClickSendNewMessage()
            },
        ) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = stringResource(R.string.send_new_messa)
            )
        }
    }) { paddingValues ->
        LazyColumn(modifier.padding(paddingValues)) {
            items(state.chats) { chat ->
                ChatItem(chat) { chatId ->
                    onClickOpenChat(chatId)
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppBarChatList() {
    TopAppBar(
        title = {
            Text(text = stringResource(id = R.string.app_name))
        }
    )
}

@Composable
fun ChatItem(
    chat: Chat, onClick: (Long) -> Unit
) {
    Card(
        modifier = Modifier.clickable { onClick(chat.id) },
    ) {
        Row(
            Modifier.padding(16.dp),
        ) {
            AsyncImage(
                imageUrl = chat.profilePicOwner,
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape),
            )
            Column(
                Modifier
                    .padding(start = 8.dp)
                    .align(Alignment.CenterVertically)
            ) {
                Text(
                    text = chat.owner,
                    modifier = Modifier.fillMaxWidth(),
                    fontWeight = FontWeight.Bold,
                )
                Text(
                    text = chat.lastMessage,
                    modifier = Modifier.fillMaxWidth(),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

@Preview
@Composable
fun ChatListPreview() {
    ConcordTheme {
        ChatListScreen(
            state = ChatListUiState(chatListSample)
        )
    }
}

@Preview
@Composable
fun ChatListItemPreview() {
    ConcordTheme {
        ChatItem(chatListSample.first()) {}
    }
}
