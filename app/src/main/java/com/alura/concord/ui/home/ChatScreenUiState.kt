package com.alura.concord.ui.home

import com.alura.concord.data.Message

data class ChatScreenUiState(
    val messages: List<Message> = emptyList(),
    val messageValue: String = "",
    val onMessageValueChange: (String) -> Unit = {},
    val showError: Boolean = false,
    val error: String = ""
)