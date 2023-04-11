@file:OptIn(ExperimentalMaterial3Api::class)

package com.alura.concord.navigation

import android.net.Uri
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import com.alura.concord.ui.chat.MessageScreen
import com.alura.concord.ui.chat.MessageScreenUiState
import com.alura.concord.ui.components.BottomSheetFiles
import com.alura.concord.ui.components.BottomSheetStickers
import com.google.accompanist.navigation.material.ExperimentalMaterialNavigationApi
import com.google.accompanist.navigation.material.bottomSheet
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterialNavigationApi::class)
internal fun NavGraphBuilder.messageGraphNew(
    state: MessageScreenUiState,
    onFilesClick: () -> Unit = {},
    onBack: () -> Unit = {},
    onSendMessage: () -> Unit = {},
    onDeselectMedia: () -> Unit = {},
    onSelectPhoto: () -> Unit = {},
    onSelectFile: () -> Unit = {},
    onShowStickers: (Boolean) -> Unit = {},
    onSelectedSticker: (Uri) -> Unit = {},
    onShowSelectorStickers: () -> Unit = {},
) {
    composable("messageChatFullPath") { backStackEntry ->
        backStackEntry.arguments?.getString(messageChatIdArgument)?.let { chatId ->

            MessageScreen(
                state = state,
                onSendMessage = {
                    onSendMessage()
                },
                onShowSelectorFile = {
                    onFilesClick()
                },
                onShowSelectorStickers = {
                    onShowSelectorStickers()
                },
                onDeselectMedia = {
                    onDeselectMedia()
                },
                onBack = {
                    onBack()
                }
            )
        }
    }

//    bottomSheet(bottomsheet_files) {
//        BottomSheetFiles(
//            onSelectPhoto = {
//                onSelectPhoto()
//            },
//            onSelectFile = {
//                onSelectFile()
//            }
//        )
//    }

    composable(bottomsheet_stickers) {
        val scope = rememberCoroutineScope()

        val sheetState = rememberModalBottomSheetState()
        ModalBottomSheet(
            sheetState = sheetState,
            content = {
                BottomSheetStickers(
                    onSelectedSticker = { uri ->
                        onSelectedSticker(uri)
                        scope.launch {
                            sheetState.hide()
                        }
                    }
                )
            },
            onDismissRequest = {
                onBack()
//            scope.launch {
//                sheetState.hide()
//            }
            }
        )

    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun BoottomSheetSticker(onSelectedSticker: (Uri) -> Unit, onBack: () -> Unit) {
    val scope = rememberCoroutineScope()

    val sheetState = rememberModalBottomSheetState()
    ModalBottomSheet(
        sheetState = sheetState,
        content = {
            BottomSheetStickers(
                onSelectedSticker = { uri ->
                    onSelectedSticker(uri)
                    scope.launch {
                        sheetState.hide()
                    }
                }
            )
        },
        onDismissRequest = {
            onBack()
//            scope.launch {
//                sheetState.hide()
//            }
        }
    )
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
