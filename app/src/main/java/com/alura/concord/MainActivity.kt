package com.alura.concord

import android.content.Context
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.alura.concord.extensions.showMessage
import com.alura.concord.medias.launchPickDocumentMedia
import com.alura.concord.medias.launchPickVisualMedia
import com.alura.concord.medias.setResultFromFileSelection
import com.alura.concord.medias.setResultFromImageSelection
import com.alura.concord.navigation.*
import com.alura.concord.ui.chat.MessageListViewModel
import com.alura.concord.ui.chat.MessageScreen
import com.alura.concord.ui.chat.MessageScreenUiState
import com.alura.concord.ui.components.BottomSheetFiles
import com.alura.concord.ui.components.BottomSheetStickers
import com.alura.concord.ui.theme.ConcordTheme
import com.google.accompanist.navigation.material.ExperimentalMaterialNavigationApi
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            ConcordTheme {
                ConcordNavHost2()
//                ConcordNavHost()
            }
        }
    }
}

@Composable
fun ConcordNavHost2(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController()
) {
    NavHost(
        navController = navController,
        startDestination = chatListRoute,
        modifier = modifier,
    ) {
        chatListGraph(
            onOpenChat = { chatId ->
                navController.navigateToMessageScreen(chatId)
            }
        )

        messageGraphBottoms(
            onBack = {
                navController.navigateUp()
            }
        )

    }
}


internal fun NavGraphBuilder.messageGraphBottoms(
    onBack: () -> Unit = {},
) {
    composable(messageChatFullPath) { backStackEntry ->
        backStackEntry.arguments?.getString(messageChatIdArgument)?.let { chatId ->
            val viewModelMessage = hiltViewModel<MessageListViewModel>()
            val state by viewModelMessage.uiState.collectAsState()
            val context = LocalContext.current

            val showBottomSheetSticker = remember { mutableStateOf(false) }
            val showBottomSheetFile = remember { mutableStateOf(false) }
            val coroutineScope = rememberCoroutineScope()

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
                    }
                )

            val pickMediaImage =
                setResultFromFileSelection(
                    onSelectedImage = {
                        viewModelMessage.loadMediaInScreen(uri = it)
                    },
                )


            MessageScreen(
                state = state,
                onSendMessage = {
                    coroutineScope.launch {
                        viewModelMessage.sendMessage()
                    }
                    //onSendMessage()
                },
                onShowSelectorFile = {
                    showBottomSheetFile.value = true
//                    onFilesClick()
                },
                onShowSelectorStickers = {
                    showBottomSheetSticker.value = true
//                    onShowSelectorStickers()
                },
                onDeselectMedia = {
                    viewModelMessage.deselectMedia()
//                    onDeselectMedia()
                },
                onBack = {
                    onBack()
                }
            )

//                 Código que funciona
//                MessagegeList(onShowStickers = {
//                    showBottomSheetSticker.value = true
//                }, state)

            if (showBottomSheetSticker.value) {
                ModalBottomSheetSticker(
                    onSelectedSticker = {
                        showBottomSheetSticker.value = false
                        viewModelMessage.loadMediaInScreen(uri = it.toString())
                        coroutineScope.launch {
                            viewModelMessage.sendMessage()
                        }
                    }, onBack = {
                        showBottomSheetSticker.value = false
                    })
            }

            if (showBottomSheetFile.value) {
                ModalBottomSheetFile(
                    onSelectPhoto = {
                        showBottomSheetFile.value = false
                        launchPickVisualMedia(pickMediaImage, "image/*")
                    },
                    onSelectFile = {
                        showBottomSheetFile.value = false
                        launchPickDocumentMedia(pickMediaFiles)
                    }, onBack = {
                        showBottomSheetFile.value = false
                    })
            }
        }


    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ModalBottomSheetSticker(
    onSelectedSticker: (Uri) -> Unit = {},
    onBack: () -> Unit = {}
) {
    val modalSheetState = rememberModalBottomSheetState()

    ModalBottomSheet(
        sheetState = modalSheetState,
        containerColor = MaterialTheme.colorScheme.onPrimaryContainer,
        content = {
            BottomSheetStickers(
                onSelectedSticker = { uri ->
                    onSelectedSticker(uri)
//                    coroutineScope.launch { modalSheetState.hide() }
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


@Composable
private fun MessagegeList(onShowStickers: () -> Unit, state: MessageScreenUiState) {

    MessageScreen(
        state = state,
        onSendMessage = {
            //          onSendMessage()
        },
        onShowSelectorFile = {
            //       onFilesClick()
        },
        onShowSelectorStickers = {
            onShowStickers()
            //        onShowSelectorStickers()
        },
        onDeselectMedia = {
            //       onDeselectMedia()
        },
        onBack = {
            //   onBack()
        }
    )
}

@Composable
private fun ChatList(
    show: MutableState<Boolean>,
    context: Context
) {
    Text(
        text = "Click",
        Modifier.fillMaxSize().clickable {
            show.value = true
            context.showMessage("Abrir Bottom Sheet")
        },
        textAlign = TextAlign.Center
    )
}



