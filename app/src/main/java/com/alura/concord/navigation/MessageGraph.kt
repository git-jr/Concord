package com.alura.concord.navigation

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.material3.*
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
import com.alura.concord.data.Image
import com.alura.concord.extensions.showMessage
import com.alura.concord.medias.loadImagesAndThumbs
import com.alura.concord.medias.setResultFromFileSelection
import com.alura.concord.medias.setResultFromImageSelection
import com.alura.concord.ui.chat.MessageListViewModel
import com.alura.concord.ui.chat.MessageScreen
import com.alura.concord.ui.components.BottomSheetFiles
import com.alura.concord.ui.components.BottomSheetStickers
import kotlinx.coroutines.launch

fun NavGraphBuilder.messageGraph(
    onBack: () -> Unit = {},
) {
    composable(messageChatFullPath) { backStackEntry ->
        backStackEntry.arguments?.getString(messageChatIdArgument)?.let { chatId ->
            val viewModelMessage = hiltViewModel<MessageListViewModel>()
            val state by viewModelMessage.uiState.collectAsState()
            val coroutineScope = rememberCoroutineScope()
            val context = LocalContext.current


            val pickMediaFiles =
                setResultFromFileSelection(
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

//            setResultFromFileSelection
            val pickMediaImage =
                setResultFromImageSelection(
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

                val stickerList: MutableList<Image> = loadImagesAndThumbs(LocalContext.current)


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
                        pickMediaImage.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
                    },
                    onSelectFile = {
                        viewModelMessage.setShowBottomSheetFile(false)
                        pickMediaFiles.launch(arrayOf("*/*")) // Others: arrayOf("application/pdf", "image/png")
                    }, onBack = {
                        viewModelMessage.setShowBottomSheetFile(false)
                    })
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ModalBottomSheetSticker(
    stickerList: MutableList<Image>,
    onSelectedSticker: (Uri) -> Unit = {},
    onBack: () -> Unit = {}
) {
    val modalSheetState = rememberModalBottomSheetState()

    ModalBottomSheet(
        sheetState = modalSheetState,
        containerColor = MaterialTheme.colorScheme.onPrimaryContainer,
        content = {
            BottomSheetStickers(
                stickerList = stickerList,
                onSelectedSticker = { uri ->
                    onSelectedSticker(uri)
                }
            )
        },
        onDismissRequest = {
            onBack()
        },
    )
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ModalBottomSheetFile(
    onSelectPhoto: () -> Unit = {},
    onSelectFile: () -> Unit = {},
    onBack: () -> Unit = {}
) {
    val modalSheetState = rememberModalBottomSheetState()

    ModalBottomSheet(
        sheetState = modalSheetState,
        containerColor = MaterialTheme.colorScheme.onPrimaryContainer,
        content = {
            BottomSheetFiles(
                onSelectPhoto = {
                    onSelectPhoto()
                },
                onSelectFile = {
                    onSelectFile()
                }
            )

        },
        onDismissRequest = {
            onBack()
        },
    )
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
            context.showMessage("Aceite as permissões para usar essa função")
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

internal const val messageChatRoute = "messages"
internal const val messageChatIdArgument = "chatId"
internal const val messageChatFullPath = "$messageChatRoute/{$messageChatIdArgument}"


internal fun NavHostController.navigateToMessageScreen(
    chatId: Long,
    navOptions: NavOptions? = null
) {
    navigate("$messageChatRoute/$chatId", navOptions)
}

