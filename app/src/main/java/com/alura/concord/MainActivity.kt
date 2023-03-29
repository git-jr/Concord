package com.alura.concord

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
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
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.alura.concord.extensions.showMessage
import com.alura.concord.ui.components.BottomSheetFiles
import com.alura.concord.ui.components.BottomSheetStickers
import com.alura.concord.ui.home.ChatScreen
import com.alura.concord.ui.home.ChatScreenUiState
import com.alura.concord.ui.home.ChatViewModel
import com.alura.concord.ui.navigation.ConcordRoute
import com.alura.concord.ui.theme.ConcordTheme
import com.google.accompanist.navigation.material.ExperimentalMaterialNavigationApi
import com.google.accompanist.navigation.material.ModalBottomSheetLayout
import com.google.accompanist.navigation.material.bottomSheet
import com.google.accompanist.navigation.material.rememberBottomSheetNavigator
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream
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
    val state by viewModel.uiState.collectAsState()

    ModalBottomSheetLayout(bottomSheetNavigator) {
        NavHost(navController, ConcordRoute.HOME) {

            composable(ConcordRoute.HOME) {

                HomeScreen(state = state,
                    sendMessage = {
                        viewModel.sendMessage()
                       // viewModel.saveInDataStore(context)
                    }, updateshowError = {
                        viewModel.updateshowError()
                    }, showSheetFiles = {
                        navController.navigate(ConcordRoute.BOTTOMSHEETFILE)
                    }, showSheetStickers = {
                        navController.navigate(ConcordRoute.BOTTOMSHEETSTICKER)
                    })
            }

            bottomSheet(ConcordRoute.BOTTOMSHEETFILE) {
                val pickMedia =
                    setResultFromImageSelection(context, viewModel, navController)

                BottomSheetFiles(
                    onItemClick = {
                        navController.navigate(ConcordRoute.BOTTOMSHEETFILE)
                    },
                    onSelectPhoto = {
                        launchPickVisualMediaImage(pickMedia)
                    })
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
                try {
                    val contentResolver = context.contentResolver
                    val takeFlags =
                        Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_WRITE_URI_PERMISSION

                    contentResolver.takePersistableUriPermission(uri, takeFlags)
                    viewModel.loadMediaInScreen(uri.toString())

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
                context.getDir("temImages", Context.MODE_PRIVATE),
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

private fun launchPickVisualMediaImage(pickMedia: ManagedActivityResultLauncher<PickVisualMediaRequest, Uri?>) {
    val mimeType = "image/*"
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


