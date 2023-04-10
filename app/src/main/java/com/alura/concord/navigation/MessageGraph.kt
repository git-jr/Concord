package com.alura.concord.navigation

import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import com.alura.concord.R
import com.alura.concord.checkImagePermission
import com.alura.concord.medias.launchPickDocumentMedia
import com.alura.concord.medias.launchPickVisualMedia
import com.alura.concord.medias.setResultFromFileSelection
import com.alura.concord.medias.setResultFromImageSelection
import com.alura.concord.ui.chat.MessageListViewModel
import com.alura.concord.ui.chat.MessageScreen
import com.alura.concord.ui.chat.MessageScreenUiState
import com.alura.concord.ui.components.BottomSheetFiles
import com.alura.concord.ui.components.BottomSheetStickers
import com.google.accompanist.navigation.material.ExperimentalMaterialNavigationApi
import com.google.accompanist.navigation.material.bottomSheet
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterialNavigationApi::class)
internal fun NavGraphBuilder.messageGraph(
    viewModel: MessageListViewModel,
    showSheetFiles: () -> Unit = {},
    showSheetStickers: () -> Unit = {},
    onBack: () -> Unit = {}
) {

    composable(messageChatFullPath) { backStackEntry ->
        backStackEntry.arguments?.getString(messageChatIdArgument)?.let { chatId ->
            val state: MessageScreenUiState by viewModel.uiState.collectAsState()

            viewModel.setChatId(chatId.toLong())
            val coroutineScope = rememberCoroutineScope()

            MessageScreen(state = state,
                onSendMessage = {
                    coroutineScope.launch {
                        viewModel.sendMessage()
                    }
                }, onShowSelectorFile = {
                    showSheetFiles()
                }, onShowSelectorStickers = {
                    showSheetStickers()
                },
                onDeselectMedia = {
                    viewModel.deselectMedia()
                },
                onBack = {
                    onBack()
                }
            )
        }
    }

    bottomSheet(bottomsheet_files) {
        val coroutineScope = rememberCoroutineScope()
        val context = LocalContext.current
        val state: MessageScreenUiState by viewModel.uiState.collectAsState()


        val pickMediaFiles =
            setResultFromImageSelection(
                onSelectedFile = { path, name ->
                    if (state.messageValue.isEmpty()) {
                        state.onMessageValueChange(
                            name ?: context.getString(R.string.document)
                        )
                    }
                    viewModel.loadMediaInScreen(uri = path)
                    coroutineScope.launch {
                        viewModel.sendMessage()
                    }
                },
                onBack = onBack
            )
        val pickMediaImage =
            setResultFromFileSelection(
                onSelectedImage = {
                    viewModel.loadMediaInScreen(uri = it)
                },
                onBack = onBack
            )

        BottomSheetFiles(
            onSelectPhoto = {
                launchPickVisualMedia(pickMediaImage, "image/*")
            },
            onSelectFile = {
                launchPickDocumentMedia(pickMediaFiles)
            }
        )
    }

    bottomSheet(bottomsheet_stickers) {
        val state: MessageScreenUiState by viewModel.uiState.collectAsState()

        val coroutineScope = rememberCoroutineScope()
        val context = LocalContext.current

        checkImagePermission(context, onBack = {
            viewModel.setImagePerssion(true)
        })

        if (state.imagePermission) {
            BottomSheetStickers(
                onSelectedSticker = {
                    viewModel.loadMediaInScreen(uri = it.toString())
                    coroutineScope.launch {
                        viewModel.sendMessage()
                    }
                    onBack()
                })
        }else{
//            onResquestPermission()
        }
    }

}


internal const val messageChatRoute = "messages"
internal const val messageChatIdArgument = "chatId"
internal const val messageChatFullPath = "$messageChatRoute/{$messageChatIdArgument}"


const val bottomsheet_files = "bottomSheet_files"
const val bottomsheet_stickers = "bottomSheet_stickers"


fun NavHostController.navigateToMessageScreen(
    chatId: Long,
    navOptions: NavOptions? = null
) {
    navigate("${messageChatRoute}/$chatId", navOptions)
}
