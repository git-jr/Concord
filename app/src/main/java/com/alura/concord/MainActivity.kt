package com.alura.concord

import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
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

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // requestFilesPermission()

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

    private fun requestFilesPermission() {
//        val requestPermissionLauncher =
//            registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
//                if (isGranted) {
//                    // A permissão foi concedida. Agora você pode acessar os arquivos protegidos pelo sistema.
//                } else {
//                    // A permissão não foi concedida. Você precisa informar o usuário para conceder a permissão para o aplicativo.
//                }
//            }

        val readExternalStoragePermission = android.Manifest.permission.READ_EXTERNAL_STORAGE
        val readMediaImagesPermission = "android.permission.READ_MEDIA_IMAGES"

        if (ContextCompat.checkSelfPermission(
                this,
                readExternalStoragePermission
            ) == PackageManager.PERMISSION_GRANTED &&
            ContextCompat.checkSelfPermission(
                this,
                readMediaImagesPermission
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            // As permissões já foram concedidas. Você pode acessar as imagens.
        } else {
            // As permissões ainda não foram concedidas. Solicite as permissões explicitamente.
            // requestPermissionLauncher.launch(arrayOf(readExternalStoragePermission, readMediaImagesPermission))
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
    viewModel.loadRecentImages(context)

    ModalBottomSheetLayout(bottomSheetNavigator) {
        NavHost(navController, ConcordRoute.HOME) {

            composable(ConcordRoute.HOME) {

                HomeScreen(state = state,
                    sendMessage = {
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

                val pickMedia =
                    rememberLauncherForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
                        if (uri != null) {

                            val contentResolver = context.contentResolver
                            val takeFlags =
                                Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_WRITE_URI_PERMISSION

                            contentResolver.takePersistableUriPermission(uri, takeFlags)

                            viewModel.addNewRecentImage(context, uri.toString())
                            navController.navigateUp()
                            Log.d("PhotoPicker", "Selected URI: $uri")
                        } else {
                            Log.d("PhotoPicker", "No media selected")
                        }
                    }

                BottomSheetFiles(
                    onItemClick = {
                        navController.navigate(ConcordRoute.BOTTOMSHEETFILE)
                    },
                    onSelectPhoto = {
                        val mimeType = "image/*"
                        pickMedia.launch(
                            PickVisualMediaRequest(
                                ActivityResultContracts.PickVisualMedia.SingleMimeType(
                                    mimeType
                                )
                            )
                        )
                    })
            }

            bottomSheet(ConcordRoute.BOTTOMSHEETSTICKER) {
                val context = LocalContext.current

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

@Preview
@Composable
fun SelectedImagePreviw() {
    SelectedImagePreviw()
}


