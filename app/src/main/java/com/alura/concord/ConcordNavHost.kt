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
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavOptions
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.alura.concord.extensions.showMessage
import com.alura.concord.medias.launchPickDocumentMedia
import com.alura.concord.medias.launchPickVisualMedia
import com.alura.concord.medias.setResultFromFileSelection
import com.alura.concord.medias.setResultFromImageSelection
import com.alura.concord.navigation.ConcordRoute
import com.alura.concord.ui.chat.ChatScreen
import com.alura.concord.ui.chat.MessageListViewModel
import com.alura.concord.ui.chat.files.DocumentListScreen
import com.alura.concord.ui.chat.files.DocumentListViewModel
import com.alura.concord.ui.components.BottomSheetFiles
import com.alura.concord.ui.components.BottomSheetStickers
import com.alura.concord.ui.home.ChatListScreen
import com.alura.concord.ui.home.ChatListViewModel
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

    val viewModel = hiltViewModel<MessageListViewModel>()

    val context = LocalContext.current

    requestPermissionLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (isGranted) {
                viewModel.setImagePerssion(true)
                context.showMessage("Permissões concedidas")
                // A permissão foi concedida. Agora você pode acessar os arquivos protegidos pelo sistema.
            } else {
                navController.navigateUp()
                context.showMessage("Permissões ainda não concedidas")
                // A permissão não foi concedida. Você precisa informar o usuário para conceder a permissão para o aplicativo.
            }
        }


//    val bottomSheetNavigator: BottomSheetNavigator = rememberBottomSheetNavigator()
//    val navController: NavHostController = rememberNavController(bottomSheetNavigator)


    ModalBottomSheetLayout(
        bottomSheetNavigator = bottomSheetNavigator,
        modifier = modifier,
    ) {
        NavHost(
            navController = navController,
            startDestination = ConcordRoute.CHAT_LIST,
            modifier = modifier,
        ) {
            chatListGraph(
                onOpenChat = { chatId ->
                    navController.navigateToChatScreen(chatId)
                },
                onSendNewMessage = {
                }
            )

            chatGraph(
                showSheetFiles = {
                    navController.navigate(ConcordRoute.BOTTOMSHEET_FILE)
                },
                showSheetStickers = {
                    navController.navigate(ConcordRoute.BOTTOMSHEET_STICKER)
                },
                onBack = {
                    navController.navigateUp()
                },
                viewModel = viewModel
            )
        }
    }
}

private fun NavGraphBuilder.chatListGraph(
    onOpenChat: (Long) -> Unit = {},
    onSendNewMessage: () -> Unit = {},
) {
    composable(ConcordRoute.CHAT_LIST) {
        val chatViewModel = hiltViewModel<ChatListViewModel>()
        val chatState by chatViewModel.uiState.collectAsState()

        ChatListScreen(
            state = chatState,
            onOpenChat = {
                onOpenChat(it)
            },
            onSendNewMessage = onSendNewMessage,
        )
    }
}

@OptIn(ExperimentalMaterialNavigationApi::class)
private fun NavGraphBuilder.chatGraph(
    showSheetFiles: () -> Unit = {},
    showSheetStickers: () -> Unit = {},
    onBack: () -> Unit = {},
    viewModel: MessageListViewModel,
) {
    composable(messageChatFullPath) { backStackEntry ->
        backStackEntry.arguments?.getString(messageChatIdArgument)?.let { chatId ->
            viewModel.setChatId(chatId.toLong())
            val state by viewModel.uiState.collectAsState()
            val coroutineScope = rememberCoroutineScope()

// Se uso temporarimente para o uso do viewModel global
//        val viewModel: MessageListViewModel = viewModel()
//        val state by viewModel.uiState.collectAsState()


            ChatScreen(state = state,
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

    bottomSheet(ConcordRoute.BOTTOMSHEET_FILE) {
        val state = viewModel.uiState.collectAsState()
        val context = LocalContext.current

        val coroutineScope = rememberCoroutineScope()
        val pickMediaFiles =
            setResultFromImageSelection(
                onSelectedFile = { path, name ->
                    if (state.value.messageValue.isEmpty()) {
                        state.value.onMessageValueChange(
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

    bottomSheet(ConcordRoute.BOTTOMSHEET_STICKER) {
        val context = LocalContext.current
        val coroutineScope = rememberCoroutineScope()
        val state = viewModel.uiState.collectAsState()

        requestCorrectPermission(context, onBack = {
            viewModel.setImagePerssion(true)
        })

        if (state.value.imagePermission) {
            BottomSheetStickers(
                onSelectedSticker = {
                    viewModel.loadMediaInScreen(uri = it.toString())
                    coroutineScope.launch {
                        viewModel.sendMessage()
                    }
                    // context.showMessage(it)
                    // navController.navigate(ConcordRoute.BOTTOMSHEETSTICKER)
                    onBack()
                })
        }
    }

    composable(documentListRoute) {
        val documentListViewModel = hiltViewModel<DocumentListViewModel>()
        val documentState by documentListViewModel.uiState.collectAsState()
        val context = LocalContext.current

        DocumentListScreen(
            state = documentState,
            onOpenDocument = {
                context.showMessage("Abrir documento $it")
            }
        )
    }
}


private fun requestCorrectPermission(context: Context, onBack: () -> Unit) {

    when {
        ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.READ_MEDIA_IMAGES
        ) == PackageManager.PERMISSION_GRANTED -> {
            onBack()
        }
        ActivityCompat.shouldShowRequestPermissionRationale(
            context as MainActivity,
            Manifest.permission.READ_MEDIA_IMAGES
        ) -> {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                requestPermissionLauncher.launch(
                    Manifest.permission.READ_MEDIA_IMAGES
                )
            } else {
                requestPermissionLauncher.launch(
                    Manifest.permission.READ_EXTERNAL_STORAGE
                )
            }
            context.showMessage("Aceite as permissões para usar essa função")
        }
        else -> {
            // You can directly ask for the permission.
            // The registered ActivityResultCallback gets the result of this request.
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
    }
}


internal const val messageChatRoute = "messages"
internal const val messageChatIdArgument = "chatId"
internal const val messageChatFullPath = "$messageChatRoute/{$messageChatIdArgument}"

fun NavHostController.navigateToChatScreen(
    chatId: Long,
    navOptions: NavOptions? = null
) {
    navigate("${messageChatRoute}/$chatId", navOptions)
//    navigate("${ConcordRoute.MESSAGE_CHAT}/$chatId")
}


internal const val documentListRoute = "document"
fun NavHostController.navigateToDocumentListScreen(
    navOptions: NavOptions? = null
) {
    navigate(documentListRoute, navOptions)
}

