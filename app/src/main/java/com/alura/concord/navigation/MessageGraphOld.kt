////@file:OptIn(ExperimentalMaterial3Api::class)
//
package com.alura.concord.navigation
//
//import android.net.Uri
//import androidx.compose.material3.BottomSheetScaffold
//import androidx.compose.material3.ExperimentalMaterial3Api
//import androidx.compose.material3.rememberBottomSheetScaffoldState
//import androidx.compose.runtime.LaunchedEffect
//import androidx.compose.runtime.collectAsState
//import androidx.compose.runtime.getValue
//import androidx.compose.runtime.rememberCoroutineScope
//import androidx.hilt.navigation.compose.hiltViewModel
//import androidx.navigation.NavGraphBuilder
//import androidx.navigation.NavHostController
//import androidx.navigation.NavOptions
//import androidx.navigation.compose.composable
//import com.alura.concord.ui.chat.MessageScreen
//import com.alura.concord.ui.chat.MessageScreenUiState
//import com.alura.concord.ui.chat.SecondMessageListViewModel
//import com.alura.concord.ui.components.BottomSheetFiles
//import com.alura.concord.ui.components.BottomSheetStickers
//import com.google.accompanist.navigation.material.ExperimentalMaterialNavigationApi
//import com.google.accompanist.navigation.material.bottomSheet
//import kotlinx.coroutines.launch
//
//@OptIn(ExperimentalMaterialNavigationApi::class)
//internal fun NavGraphBuilder.messageGraph(
////    viewModel: MessageListViewModel,
//    onFilesClick: () -> Unit = {},
//    onStickersClick: () -> Unit = {},
//    onBack: () -> Unit = {},
//    onsetChatId: (Long) -> Unit = {},
//    onSendMessage: () -> Unit = {},
//    onDeselectMedia: () -> Unit = {},
//    onSelectPhoto: () -> Unit = {},
//    onSelectFile: () -> Unit = {},
//    onSetImagePerssion: () -> Unit = {},
//    imagePermissionIsOk: Boolean,
//    onSelectedSticker: (Uri) -> Unit = {},
//    ) {
//
//    composable(messageChatFullPath) { backStackEntry ->
//        backStackEntry.arguments?.getString(messageChatIdArgument)?.let { chatId ->
//            val viewModel = hiltViewModel<SecondMessageListViewModel>()
//            val secondState: MessageScreenUiState by viewModel.uiState.collectAsState()
//
//            MessageScreen(
//                state = secondState,
//                onSendMessage = {
//                    onSendMessage()
//                },
//                onShowSelectorFile = {
//                    onFilesClick()
//                },
//                onShowSelectorStickers = {
//                    onStickersClick()
//                },
//                onDeselectMedia = {
//                    onDeselectMedia()
//                },
//                onBack = {
//                    onBack()
//                }
//            )
//        }
//    }
//
//    bottomSheet(bottomsheet_files) {
//        BottomSheetFiles(
//            onSelectPhoto = {
//                onSelectPhoto()
//            },
//            onSelectFile = {
//                onSelectFile()
//            }
//        )
//    }
//
//    bottomSheet(bottomsheet_stickers) {
//
//        BottomSheetStickers(
//            onSelectedSticker = { uri ->
//                onSelectedSticker(uri)
//            })
//
//    }
//
//}
//
//
//internal const val messageChatRoute = "messages"
//internal const val messageChatIdArgument = "chatId"
//internal const val messageChatFullPath = "$messageChatRoute/{$messageChatIdArgument}"
//
//
//const val bottomsheet_files = "bottomSheet_files"
//const val bottomsheet_stickers = "bottomSheet_stickers"
//
//
//fun NavHostController.navigateToMessageScreen(
//    chatId: Long,
//    navOptions: NavOptions? = null
//) {
//    navigate("${messageChatRoute}/$chatId", navOptions)
//}
