package com.alura.concord.ui.home

import com.alura.concord.data.Chat

data class ChatListUiState(
    val chats: List<Chat> = emptyList(),
)