package com.alura.concord.navigation

import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.alura.concord.ui.home.ChatListScreen
import com.alura.concord.ui.home.ChatListViewModel

internal const val chatListRoute = "chat"

internal fun NavGraphBuilder.chatListGraph(
    onOpenChat: (Long) -> Unit = {},
    onSendNewMessage: () -> Unit = {},
) {
    composable(chatListRoute) {
        val chatViewModel = hiltViewModel<ChatListViewModel>()
        val chatState by chatViewModel.uiState.collectAsState()

        ChatListScreen(
            state = chatState,
            onOpenChat = {
                onOpenChat(it)
            },
            onSendNewMessage = onSendNewMessage,
        )
    }
}