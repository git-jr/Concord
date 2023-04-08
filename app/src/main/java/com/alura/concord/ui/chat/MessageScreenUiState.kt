package com.alura.concord.ui.chat

import com.alura.concord.data.Message

data class MessageScreenUiState(
    val messages: List<Message> = emptyList(),
    val messageValue: String = "",
    val onMessageValueChange: (String) -> Unit = {},
    val onMediaInSelectionChange: (String) -> Unit = {},
    val hasContentToSend: Boolean = false,
    val onHasContentToSend:(Boolean) -> Unit = {},
    val error: String = "",
    val mediaInSelection: String = "",
    val profilePicOwner: String = "",
    val ownerName: String = "",
)