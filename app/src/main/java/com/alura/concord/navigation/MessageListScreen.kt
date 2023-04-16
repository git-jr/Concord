package com.alura.concord.navigation

import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import com.alura.concord.extensions.checkImagePermission
import com.alura.concord.extensions.getPathAndName
import com.alura.concord.extensions.getPath
import com.alura.concord.extensions.showMessage
import com.alura.concord.medias.loadExternalImages
import com.alura.concord.ui.chat.MessageListViewModel
import com.alura.concord.ui.chat.MessageScreen
import com.alura.concord.ui.components.ModalBottomSheetFile
import com.alura.concord.ui.components.ModalBottomSheetSticker

internal const val messageChatRoute = "messages"
internal const val messageChatIdArgument = "chatId"
internal const val messageChatFullPath = "$messageChatRoute/{$messageChatIdArgument}"


fun NavGraphBuilder.messageListScreen(
    onBack: () -> Unit = {},
) {
    composable(messageChatFullPath) { backStackEntry ->
        backStackEntry.arguments?.getString(messageChatIdArgument)?.let { chatId ->
            val viewModelMessage = hiltViewModel<MessageListViewModel>()
            val uiState by viewModelMessage.uiState.collectAsState()
            val context = LocalContext.current


            val requestPermissionLauncher: ManagedActivityResultLauncher<String, Boolean> =
                rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
                    if (isGranted) {
                        viewModelMessage.setImagePermission(true)
                        viewModelMessage.setShowBottomSheetSticker(true)
                        context.showMessage("Permissões concedidas")
                    } else {
                        context.showMessage("Permissões ainda não concedidas")
                    }
                }

            MessageScreen(
                state = uiState,
                onSendMessage = {
                    viewModelMessage.sendMessage()

                },
                onShowSelectorFile = {
                    viewModelMessage.setShowBottomSheetFile(true)

                },
                onShowSelectorStickers = {
                    context.checkImagePermission(
                        requestPermissionLauncher = requestPermissionLauncher,
                        onPermissionHasObtained = {
                            viewModelMessage.setImagePermission(true)
                            viewModelMessage.setShowBottomSheetSticker(true)
                        })
                },
                onDeselectMedia = {
                    viewModelMessage.deselectMedia()
                },
                onBack = {
                    onBack()
                }
            )

            if (uiState.showBottomSheetSticker) {
                val stickerList = loadExternalImages(LocalContext.current)
                ModalBottomSheetSticker(
                    stickerList,
                    onSelectedSticker = {
                        viewModelMessage.setShowBottomSheetSticker(false)
                        viewModelMessage.loadMediaInScreen(path = it.toString())
                        viewModelMessage.sendMessage()
                    }, onBack = {
                        viewModelMessage.setShowBottomSheetSticker(false)
                    })
            }

            val selectorMediaFiles =
                rememberLauncherForActivityResult(
                    ActivityResultContracts.OpenDocument(),
                    onResult = {
                        it?.let { uri ->
                            context.getPathAndName(uri, onFound = { path, name ->
                                if (uiState.messageValue.isEmpty()) {
                                    uiState.onMessageValueChange(name)
                                }
                                viewModelMessage.loadMediaInScreen(path = path)
                                viewModelMessage.sendMessage()
                            })
                        }
                    })

            val selectorMediaImages =
                rememberLauncherForActivityResult(
                    ActivityResultContracts.PickVisualMedia(),
                    onResult = {
                        it?.let { uri ->
                            context.getPath(
                                uri = uri,
                                onFoundPath = { path ->
                                    viewModelMessage.loadMediaInScreen(path)
                                }
                            )
                        }
                    }
                )

            if (uiState.showBottomSheetFile) {
                ModalBottomSheetFile(
                    onSelectPhoto = {
                        viewModelMessage.setShowBottomSheetFile(false)
                        selectorMediaImages.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
                    },
                    onSelectFile = {
                        viewModelMessage.setShowBottomSheetFile(false)
                        selectorMediaFiles.launch(arrayOf("*/*")) // Others: arrayOf("application/pdf", "image/png")
                    }, onBack = {
                        viewModelMessage.setShowBottomSheetFile(false)
                    })
            }
        }
    }
}


internal fun NavHostController.navigateToMessageScreen(
    chatId: Long,
    navOptions: NavOptions? = null
) {
    navigate("$messageChatRoute/$chatId", navOptions)
}

