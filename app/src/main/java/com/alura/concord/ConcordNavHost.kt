package com.alura.concord

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavOptions
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.alura.concord.extensions.showMessage
import com.alura.concord.medias.launchPickVisualMedia
import com.alura.concord.medias.setResultFromFileSelection
import com.alura.concord.medias.setResultFromImageSelection
import com.alura.concord.navigation.ConcordRoute
import com.alura.concord.ui.chat.ChatScreen
import com.alura.concord.ui.chat.MessageListViewModel
import com.alura.concord.ui.components.BottomSheetFiles
import com.alura.concord.ui.components.BottomSheetStickers
import com.alura.concord.ui.home.ChatListScreen
import com.alura.concord.ui.home.ChatListViewModel
import com.google.accompanist.navigation.material.*
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterialNavigationApi::class)
@Composable
fun ConcordNavHost(
    modifier: Modifier = Modifier,
    bottomSheetNavigator: BottomSheetNavigator = rememberBottomSheetNavigator(),
    navController: NavHostController = rememberNavController(bottomSheetNavigator)
) {
//    val bottomSheetNavigator: BottomSheetNavigator = rememberBottomSheetNavigator()
//    val navController: NavHostController = rememberNavController(bottomSheetNavigator)

    val viewModel = hiltViewModel<MessageListViewModel>()

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
//                    navController.navigate(ConcordRoute.MESSAGE_CHAT)
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

        val pickMediaImage =
            setResultFromImageSelection(
                viewModel,
                onBack = onBack
            )
        val pickMediaFiles =
            setResultFromFileSelection(viewModel, onBack = onBack)

        BottomSheetFiles(
            onSelectPhoto = {
                launchPickVisualMedia(pickMediaImage, "image/*")
            },
            onSelectFile = {
                launchPickVisualMedia(pickMediaFiles)
            }
        )
    }

    bottomSheet(ConcordRoute.BOTTOMSHEET_STICKER) {
        val context = LocalContext.current
        val coroutineScope = rememberCoroutineScope()
        BottomSheetStickers(onSelectedSticker = {
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
