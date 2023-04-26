package com.alura.concord.navigation

import android.Manifest
import android.content.ContentUris
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.provider.MediaStore
import android.util.Log
import android.util.Size
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import com.alura.concord.extensions.showMessage
import com.alura.concord.ui.chat.MessageListViewModel
import com.alura.concord.ui.chat.MessageScreen
import com.alura.concord.ui.components.ModalBottomSheetFile
import com.alura.concord.ui.components.ModalBottomSheetSticker
import java.io.File

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


            MessageScreen(
                state = uiState,
                onSendMessage = {
                    viewModelMessage.sendMessage()

                },
                onShowSelectorFile = {
                    viewModelMessage.setShowBottomSheetFile(true)

                },
                onShowSelectorStickers = {
                    viewModelMessage.setShowBottomSheetSticker(true)
                },
                onDeselectMedia = {
                    viewModelMessage.deselectMedia()
                },
                onBack = {
                    onBack()
                }
            )

            val requestImagePermission = rememberLauncherForActivityResult(
                ActivityResultContracts.RequestPermission(),
                onResult = { isGranted ->
                    if (isGranted) {
                        context.showMessage("Permissão concedidas")
                    } else {
                        context.showMessage("Permissão NÃO concedidas")
                    }
                })

            if (uiState.showBottomSheetSticker) {
                requestPermission(requestImagePermission)

                val stickerList = mutableStateListOf<Long>()

//                if (ContextCompat.checkSelfPermission(
//                        context,
//                        Manifest.permission.READ_EXTERNAL_STORAGE
//                    ) == PackageManager.PERMISSION_GRANTED
//                ) {
                getAllImages(context, onFoundImages = { images ->
                    stickerList.addAll(images)
                })
//                } else {
//                    context.showMessage("Sem permissão")
//                }


//                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
//                    ?.listFiles()?.forEach {
//                        if (it.isFile && (it.extension == "png" || it.extension == "jpg")) {
//                            stickerList.add(it.path)
//                            Log.i("files", "onCreate externo: ${it.name}")
//                        }
//                    }

                ModalBottomSheetSticker(
                    stickerList = stickerList,
                    onSelectedSticker = {
                        viewModelMessage.setShowBottomSheetSticker(false)
                        viewModelMessage.loadMediaInScreen(path = it.toString())
                        viewModelMessage.sendMessage()
                    }, onBack = {
                        viewModelMessage.setShowBottomSheetSticker(false)
                    })
            }

            val resultLauncherPhotos = rememberLauncherForActivityResult(
                ActivityResultContracts.PickVisualMedia(),
                onResult = {
                    it?.let { uri ->

                        val takeFlags =
                            Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_READ_URI_PERMISSION
                        context.contentResolver.takePersistableUriPermission(uri, takeFlags)

                        context.contentResolver.takePersistableUriPermission(uri, takeFlags)
                        Log.i("uri", uri.toString())
                        viewModelMessage.loadMediaInScreen(path = uri.toString())
                    }
                }
            )

            val resultLauncherFiles = rememberLauncherForActivityResult(
                ActivityResultContracts.OpenDocument(),
                onResult = {
                    it?.let { uri ->
                        val takeFlags =
                            Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_READ_URI_PERMISSION
                        context.contentResolver.takePersistableUriPermission(uri, takeFlags)

                        if (uiState.messageValue.isEmpty()) {
                            val file = File(uri.path.toString())
                            uiState.onMessageValueChange(file.name)
                        }
                        viewModelMessage.loadMediaInScreen(path = uri.toString())
                        viewModelMessage.sendMessage()
                    }
                }
            )

            if (uiState.showBottomSheetFile) {
                ModalBottomSheetFile(
                    onSelectPhoto = {
                        resultLauncherPhotos.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
                        viewModelMessage.setShowBottomSheetFile(false)
                    },
                    onSelectFile = {
                        resultLauncherFiles.launch(arrayOf("*/*"))
                        viewModelMessage.setShowBottomSheetFile(false)
                    }, onBack = {
                        viewModelMessage.setShowBottomSheetFile(false)
                    })
            }
        }
    }
}

private fun getAllImages(context: Context, onFoundImages: (MutableList<Long>) -> Unit = {}) {
    val imagesList = mutableListOf<Long>()

    // Coleção de itens ou item específico
    val uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI

    // Quais "colunas" nos queremos trazer
//    val projection = null
    val projection = arrayOf(
        MediaStore.Images.Media._ID,
        MediaStore.Images.Media.DISPLAY_NAME,
        MediaStore.Images.Media.DATA,
        MediaStore.Images.Media.SIZE,
    )

//    /sdcard/Download/Stickers e Imagens para curso storage/stickers
    // Por quais campos vamos filtra
    val selection =
        "${MediaStore.Images.Media.DATA} LIKE '%/Download/Images/Stickers/%' AND ${MediaStore.Video.Media.SIZE} < ?"
    val selectionArgs = arrayOf("102400") // (100 KB)

//    val selection = "${MediaStore.Video.Media.SIZE} >= ?"

//    val selection = "${MediaStore.Images.Media.DATA} LIKE '%/Pictures/Stickers/%'"


    // Quais paremetros vamos aplicar nos filtros
//    val selectionArgs = null

    // Como vamos ordenar
    val sortOrder = "${MediaStore.Video.Media.DISPLAY_NAME} DESC"

    context.contentResolver.query(
        uri,
        projection,
        selection,
        selectionArgs,
        sortOrder
    )?.use { cursor ->
        val idIndex = cursor.getColumnIndex(MediaStore.Images.Media._ID)
        val pathIndex = cursor.getColumnIndex(MediaStore.Images.Media.DATA)
        val nameIndex = cursor.getColumnIndex(MediaStore.Images.Media.DISPLAY_NAME)
        val sizeIndex = cursor.getColumnIndex(MediaStore.Images.Media.SIZE)

        while (cursor.moveToNext()) {
            val imageId = cursor.getLong(idIndex)

            val thumbnail = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                context.contentResolver.loadThumbnail(
                    ContentUris.withAppendedId(
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                        imageId.toLong()
                    ),
                    Size(100, 100),
                    null
                )
            } else {
                MediaStore.Images.Thumbnails.getThumbnail(
                    context.contentResolver,
                    imageId.toLong(),
                    MediaStore.Images.Thumbnails.MINI_KIND,
                    null
                )
            }


            val imageName = cursor.getString(nameIndex)
            val imageSize = cursor.getString(sizeIndex)
            val imagePath = cursor.getString(pathIndex)

            Log.i("files", "Nome do arquivo: $imageName")
            Log.i("files", "Size do arquivo: $imageSize")
            imagesList.add(imageId)
        }
    }
    onFoundImages(imagesList)
}

private fun requestPermission(requestImagePermission: ManagedActivityResultLauncher<String, Boolean>) {
    val permission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        Manifest.permission.READ_MEDIA_IMAGES
    } else {
        Manifest.permission.READ_EXTERNAL_STORAGE
    }
    requestImagePermission.launch(permission)
}


internal fun NavHostController.navigateToMessageScreen(
    chatId: Long,
    navOptions: NavOptions? = null
) {
    navigate("$messageChatRoute/$chatId", navOptions)
}

