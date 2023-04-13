package com.alura.concord.navigation

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import com.alura.concord.*
import com.alura.concord.R
import com.alura.concord.extensions.showMessage
import com.alura.concord.medias.createFileSelectionResult
import com.alura.concord.medias.createImageSelectionResult
import com.alura.concord.medias.loadImagesAndThumbs
import com.alura.concord.ui.chat.MessageListViewModel
import com.alura.concord.ui.chat.MessageScreen
import com.alura.concord.ui.components.ModalBottomSheetFile
import com.alura.concord.ui.components.ModalBottomSheetSticker
import kotlinx.coroutines.launch

internal const val messageChatRoute = "messages"
internal const val messageChatIdArgument = "chatId"
internal const val messageChatFullPath = "$messageChatRoute/{$messageChatIdArgument}"


fun NavGraphBuilder.messageGraph(
    onBack: () -> Unit = {},
) {
    composable(messageChatFullPath) { backStackEntry ->
        backStackEntry.arguments?.getString(messageChatIdArgument)?.let { chatId ->
            val viewModelMessage = hiltViewModel<MessageListViewModel>()
            val state by viewModelMessage.uiState.collectAsState()
            val coroutineScope = rememberCoroutineScope()
            val context = LocalContext.current


            val selectorMediaFiles =
                createFileSelectionResult(
                    onSelectedFile = { path, name ->
                        if (state.messageValue.isEmpty()) {
                            state.onMessageValueChange(
                                name ?: context.getString(R.string.document)
                            )
                        }
                        viewModelMessage.loadMediaInScreen(uri = path)
                        coroutineScope.launch {
                            viewModelMessage.sendMessage()
                        }
                    }
                )


            val selectorMediaImages =
                createImageSelectionResult(
                    onSelectedImage = {
                        viewModelMessage.loadMediaInScreen(uri = it)
                    },
                )

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
                state = state,
                onSendMessage = {
                    coroutineScope.launch {
                        viewModelMessage.sendMessage()
                    }
                },
                onShowSelectorFile = {
                    viewModelMessage.setShowBottomSheetFile(true)

                },
                onShowSelectorStickers = {
                    checkImagePermission(
                        context = context,
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
                    coroutineScope.launch {
                        viewModelMessage.cleanLastOpenChat()
                    }
                    onBack()
                }
            )

            if (state.showBottomSheetSticker) {
                val stickerList = loadImagesAndThumbs(LocalContext.current)
                ModalBottomSheetSticker(
                    stickerList,
                    onSelectedSticker = {
                        viewModelMessage.setShowBottomSheetSticker(false)
                        viewModelMessage.loadMediaInScreen(uri = it.toString())
                        coroutineScope.launch {
                            viewModelMessage.sendMessage()
                        }
                    }, onBack = {
                        viewModelMessage.setShowBottomSheetSticker(false)
                    })
            }

            if (state.showBottomSheetFile) {
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


fun checkImagePermission(
    context: Context,
    onPermissionHasObtained: () -> Unit = {},
    requestPermissionLauncher: ManagedActivityResultLauncher<String, Boolean>
) {
    when {
        ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.READ_MEDIA_IMAGES
        ) == PackageManager.PERMISSION_GRANTED -> {
            onPermissionHasObtained()
        }
        ActivityCompat.shouldShowRequestPermissionRationale(
            context as MainActivity,
            Manifest.permission.READ_MEDIA_IMAGES
        ) -> {
            requestImagePermission(requestPermissionLauncher)
            context.showMessage(
                "É preciso conceder a permissão de acesso às imagens para usar essa função",
                true
            )
        }
        else -> {
            requestImagePermission(requestPermissionLauncher)
        }
    }
}


private fun requestImagePermission(requestPermissionLauncher: ManagedActivityResultLauncher<String, Boolean>) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        requestPermissionLauncher.launch(
            Manifest.permission.READ_MEDIA_IMAGES
        )
    } else {
        requestPermissionLauncher.launch(
            Manifest.permission.READ_EXTERNAL_STORAGE
        )
    }
}


internal fun NavHostController.navigateToMessageScreen(
    chatId: Long,
    navOptions: NavOptions? = null
) {
    navigate("$messageChatRoute/$chatId", navOptions)
}

