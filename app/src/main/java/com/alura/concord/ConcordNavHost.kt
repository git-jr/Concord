package com.alura.concord

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import com.alura.concord.extensions.showMessage
import com.alura.concord.medias.launchPickDocumentMedia
import com.alura.concord.medias.launchPickVisualMedia
import com.alura.concord.medias.setResultFromFileSelection
import com.alura.concord.medias.setResultFromImageSelection
import com.alura.concord.navigation.*
import com.alura.concord.ui.chat.MessageListViewModel
import com.google.accompanist.navigation.material.*
import kotlinx.coroutines.launch

private lateinit var requestPermissionLauncher: ManagedActivityResultLauncher<String, Boolean>


@OptIn(ExperimentalMaterialNavigationApi::class)
@Composable
fun ConcordNavHost(
    modifier: Modifier = Modifier,
    bottomSheetNavigator: BottomSheetNavigator = rememberBottomSheetNavigator(),
    navController: NavHostController = rememberNavController(bottomSheetNavigator)
) {

    val viewModelMessage = hiltViewModel<MessageListViewModel>()
    val state by viewModelMessage.uiState.collectAsState()

    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    requestPermissionLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (isGranted) {
                viewModelMessage.setImagePermission(true)
                context.showMessage("Permissões concedidas")
            } else {

                context.showMessage("Permissões ainda não concedidas")
            }
        }


    if (state.showStickers) {
        navController.navigate(bottomsheet_stickers)
    }

    val pickMediaFiles =
        setResultFromImageSelection(
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
            },
            onBack = { navController.navigateUp() }
        )

    val pickMediaImage =
        setResultFromFileSelection(
            onSelectedImage = {
                viewModelMessage.loadMediaInScreen(uri = it)
            },
//            onBack = { navController.navigateUp() }
        )


    NavHost(
        navController = navController,
        startDestination = chatListRoute,
        modifier = modifier,
    ) {
        chatListGraph(
            onOpenChat = { chatId ->
               // viewModelMessage.setChatId(chatId)
                navController.navigateToMessageScreen(chatId)
            }
        )

        messageGraphNew(
            state = state,
            onFilesClick = {
                navController.navigate(bottomsheet_files)
            },
            onBack = {
                navController.navigateUp()
            },
            onSendMessage = {
                coroutineScope.launch {
                    viewModelMessage.sendMessage()
                }
            },
            onDeselectMedia = {
                viewModelMessage.deselectMedia()
            },
            onSelectPhoto = {
                launchPickVisualMedia(pickMediaImage, "image/*")
            },
            onSelectFile = {
                launchPickDocumentMedia(pickMediaFiles)
            },
            onSelectedSticker = {
                viewModelMessage.loadMediaInScreen(uri = it.toString())
                coroutineScope.launch {
                    viewModelMessage.sendMessage()
                }
                navController.navigateUp()
            },
            onShowSelectorStickers = {
                checkImagePermission(context, onSetPermission = {
                    viewModelMessage.setImagePermission(true)
                })
            },
            onShowStickers = { value ->
                viewModelMessage.setImagePermission(value)
            }
        )
    }
}


fun checkImagePermission(
    context: Context,
    onSetPermission: () -> Unit = {}
) {
    when {
        ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.READ_MEDIA_IMAGES
        ) == PackageManager.PERMISSION_GRANTED -> {
            onSetPermission()
        }
        ActivityCompat.shouldShowRequestPermissionRationale(
            context as MainActivity,
            Manifest.permission.READ_MEDIA_IMAGES
        ) -> {
            requestImagePermission()
            context.showMessage("Aceite as permissões para usar essa função")
        }
        else -> {
            requestImagePermission()
        }
    }
}

private fun requestImagePermission() {
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