package com.alura.concord.ui.chat

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.LocalContext
import com.alura.concord.extensions.showMessage
import kotlinx.coroutines.launch

@Composable
fun ChatRoute(
    state: ChatScreenUiState,
    sendMessage: () -> Unit,
    updateshowError: () -> Unit,
    showSheetFiles: () -> Unit,
    showSheetStickers: () -> Unit,
    onDeselectMedia: () -> Unit,
) {
    val context = LocalContext.current

    ChatScreen(
        state = state,
        onSendMessage = {
            sendMessage()
        },
        onShowSelectorFile = {
            showSheetFiles()
        },
        onShowSelectorStickers = {
            showSheetStickers()
        },
        onDeselectMedia = {
            onDeselectMedia()
        }
    )

    LaunchedEffect(state.showError) {
        if (state.showError) {
            context.showMessage("R.string.error_message" + state.error)
            updateshowError()
        }
    }
}

