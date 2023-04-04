package com.alura.concord

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.core.graphics.drawable.toBitmap
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import coil.imageLoader
import coil.request.ImageRequest
import com.alura.concord.extensions.showMessage
import com.alura.concord.ui.components.BottomSheetFiles
import com.alura.concord.ui.components.BottomSheetStickers
import com.alura.concord.ui.home.ChatScreen
import com.alura.concord.ui.home.ChatScreenUiState
import com.alura.concord.ui.home.ChatViewModel
import com.alura.concord.navigation.ConcordRoute
import com.alura.concord.ui.theme.ConcordTheme
import com.google.accompanist.navigation.material.ExperimentalMaterialNavigationApi
import com.google.accompanist.navigation.material.ModalBottomSheetLayout
import com.google.accompanist.navigation.material.bottomSheet
import com.google.accompanist.navigation.material.rememberBottomSheetNavigator
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.util.*

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ConcordTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background
                ) {
                    ConcordApp()
                }
            }
        }
    }


}

@OptIn(ExperimentalMaterialNavigationApi::class)
@Composable
fun ConcordApp() {
    val bottomSheetNavigator = rememberBottomSheetNavigator()
    val navController = rememberNavController(bottomSheetNavigator)
    val context = LocalContext.current

    val viewModel = viewModel<ChatViewModel>()
    viewModel.loadSampleMessages(context)
    val state by viewModel.uiState.collectAsState()

    ModalBottomSheetLayout(bottomSheetNavigator) {
        NavHost(navController, ConcordRoute.HOME) {

            composable(ConcordRoute.HOME) {

                HomeScreen(state = state,
                    sendMessage = {
                        viewModel.saveInDataStore(context)
                        viewModel.sendMessage()
                    }, updateshowError = {
                        viewModel.updateshowError()
                    }, showSheetFiles = {
                        navController.navigate(ConcordRoute.BOTTOMSHEETFILE)
                    }, showSheetStickers = {
                        navController.navigate(ConcordRoute.BOTTOMSHEETSTICKER)
                    })
            }

            bottomSheet(ConcordRoute.BOTTOMSHEETFILE) {
                val pickMediaImage =
                    setResultFromImageSelection(context, viewModel, navController)
                val pickMediaFiles =
                    setResultFromFileSelection(context, viewModel, navController)

                BottomSheetFiles(
                    onItemClick = {
                        navController.navigate(ConcordRoute.BOTTOMSHEETFILE)
                    },
                    onSelectPhoto = {
                        launchPickVisualMedia(pickMediaImage, "image/*")
                    },
                    onSelectFile = {
                        launchPickVisualMedia(pickMediaFiles)
                    }
                )
            }

            bottomSheet(ConcordRoute.BOTTOMSHEETSTICKER) {

                BottomSheetStickers(onSelectedSticker = {
                    context.showMessage(it)
                    // navController.navigate(ConcordRoute.BOTTOMSHEETSTICKER)
                    navController.navigateUp()
                })
            }
        }
    }
}

@Composable
private fun setResultFromImageSelection(
    context: Context,
    viewModel: ChatViewModel,
    navController: NavHostController
): ManagedActivityResultLauncher<PickVisualMediaRequest, Uri?> {
    val pickMedia =
        rememberLauncherForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
            if (uri != null) {
                createCopyFromInternalStorage(context, uri)
                try {
//                    val contentResolver = context.contentResolver
//                    val takeFlags =
//                        Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_WRITE_URI_PERMISSION
//
//                    contentResolver.takePersistableUriPermission(uri, takeFlags)
//                    viewModel.loadMediaInScreen(uri.toString())


                    var filePath: String? = null
                    if (uri.scheme == "content") { // For PhotoPicker in recents versions from Android
                        val cursor = context.contentResolver.query(uri, null, null, null, null)
                        if (cursor != null && cursor.moveToFirst()) {
                            val columnIndex =
                                cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
                            filePath = cursor.getString(columnIndex)
                            cursor.close()
                        }
                    } else { // For "documentPicker" from old versions Android (below 13)
                        filePath = uri.path
                    }

                    filePath?.let { viewModel.loadMediaInScreen(it) }
                    Log.i("PhotoPicker", "Sucesso ao tentar persistir a URI ")
                } catch (e: Exception) {
                    // errors from Android 13
                    Log.e("PhotoPicker", "Erro ao tentar persistir a URI ${e.cause} ")

                    val file = createCopyFromInternalStorage(context, uri)
                    file?.let {
                        viewModel.loadMediaInScreen(file.path)
                    }
                }

                navController.navigateUp()
                Log.d("PhotoPicker", "Selected URI: $uri")
            } else {
                Log.d("PhotoPicker", "No media selected")
            }
        }
    return pickMedia
}

@Composable
private fun setResultFromFileSelection(
    context: Context,
    viewModel: ChatViewModel,
    navController: NavHostController
): ManagedActivityResultLauncher<PickVisualMediaRequest, Uri?> {
    val pickMedia =
        rememberLauncherForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
            if (uri != null) {
                try {
                    val contentResolver = context.contentResolver
                    val takeFlags =
                        Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_WRITE_URI_PERMISSION

                    contentResolver.takePersistableUriPermission(uri, takeFlags)
                    //viewModel.loadMediaInScreen(uri.toString())

                } catch (e: Exception) {
                    // errors from Android 13
                    Log.e("TAG", "Erro ao tentar persistir a URI ")

                    val file = createCopyFromInternalStorage(context, uri)
                    file?.let {
                        viewModel.loadMediaInScreen(file.path)
                    }
                }

                navController.navigateUp()
                Log.d("PhotoPicker", "Selected URI: $uri")
            } else {
                Log.d("PhotoPicker", "No media selected")
            }
        }
    return pickMedia
}


private fun createCopyFromInternalStorage(context: Context, uri: Uri): File? {
    // Obtenha um InputStream a partir da Uri usando o ContentResolver
    val inputStream = context.contentResolver.openInputStream(uri)

    // Verifique se o InputStream não é nulo
    inputStream?.use {

        // Crie um arquivo para salvar o conteúdo
        val file =
            File(
                context.getDir("tempImages", Context.MODE_PRIVATE),
                UUID.randomUUID().toString()
            )

        // Crie um FileOutputStream para gravar o conteúdo do InputStream no arquivo
        val outputStream = FileOutputStream(file)

        // Crie um buffer para armazenar os dados lidos do InputStream
        val buffer = ByteArray(4096)

        // Leia os dados do InputStream e grave-os no FileOutputStream usando o buffer
        var bytesRead = inputStream.read(buffer)
        while (bytesRead >= 0) {
            outputStream.write(buffer, 0, bytesRead)
            bytesRead = inputStream.read(buffer)
        }

        // Feche o FileOutputStream
        outputStream.close()
        return file
    }
    return null
}

private fun launchPickVisualMedia(
    pickMedia: ManagedActivityResultLauncher<PickVisualMediaRequest, Uri?>,
    mimeType: String = "*/*"
) {
    pickMedia.launch(
        PickVisualMediaRequest(
            ActivityResultContracts.PickVisualMedia.SingleMimeType(
                mimeType
            )
        )
    )
}

@Composable
private fun HomeScreen(
    state: ChatScreenUiState,
    sendMessage: () -> Unit,
    updateshowError: () -> Unit,
    showSheetFiles: () -> Unit,
    showSheetStickers: () -> Unit,
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    ChatScreen(state = state,
        onSendMessage = {
            scope.launch {
                sendMessage()
            }
        }, onShowSelectorFile = {
            showSheetFiles()
        }, onShowSelectorStickers = {
            showSheetStickers()
        })

    LaunchedEffect(state.showError) {
        if (state.showError) {
            context.showMessage("R.string.error_message" + state.error)
            updateshowError()
        }
    }
}


