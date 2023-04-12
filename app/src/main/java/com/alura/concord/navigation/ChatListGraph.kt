package com.alura.concord.navigation

import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import com.alura.concord.ui.home.ChatListScreen
import com.alura.concord.ui.home.ChatListViewModel

internal const val chatListRoute = "chat"

fun NavGraphBuilder.chatListGraph(
    onOpenChat: (Long) -> Unit = {},
    onSendNewMessage: () -> Unit = {},
) {
    composable(chatListRoute) {
        val chatViewModel = hiltViewModel<ChatListViewModel>()
        val chatState by chatViewModel.uiState.collectAsState()

        LaunchedEffect(chatState.selectedId) {
            chatState.selectedId?.let { selectedId ->
                onOpenChat(selectedId)
                chatViewModel.setChatId(null)
            }
        }


        ChatListScreen(
            state = chatState,
            onOpenChat = {
                chatViewModel.setChatId(it)
            },
            onSendNewMessage = onSendNewMessage,
        )
    }
}
