package com.alura.concord

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.alura.concord.extensions.showMessage
import com.alura.concord.medias.launchPickVisualMedia
import com.alura.concord.medias.setResultFromFileSelection
import com.alura.concord.medias.setResultFromImageSelection
import com.alura.concord.navigation.ConcordRoute
import com.alura.concord.ui.chat.ChatRoute
import com.alura.concord.ui.components.BottomSheetFiles
import com.alura.concord.ui.components.BottomSheetStickers
import com.alura.concord.ui.chat.ChatViewModel
import com.google.accompanist.navigation.material.ExperimentalMaterialNavigationApi
import com.google.accompanist.navigation.material.ModalBottomSheetLayout
import com.google.accompanist.navigation.material.bottomSheet
import com.google.accompanist.navigation.material.rememberBottomSheetNavigator

@OptIn(ExperimentalMaterialNavigationApi::class)
@Composable
fun ConcordNavHost(modifier: Modifier = Modifier) {
    val bottomSheetNavigator = rememberBottomSheetNavigator()
    val navController = rememberNavController(bottomSheetNavigator)
    val context = LocalContext.current

    val viewModel = viewModel<ChatViewModel>()
    viewModel.loadSampleMessages(context)
    val state by viewModel.uiState.collectAsState()

    ModalBottomSheetLayout(
        bottomSheetNavigator = bottomSheetNavigator,
        modifier = modifier
    ) {
        NavHost(navController, ConcordRoute.HOME) {
            composable(ConcordRoute.HOME) {
                ChatRoute(state = state,
                    sendMessage = {
                        viewModel.saveInDataStore(context)
                        viewModel.sendMessage()
                    }, updateshowError = {
                        viewModel.updateshowError()
                    }, showSheetFiles = {
                        navController.navigate(ConcordRoute.BOTTOMSHEETFILE)
                    }, showSheetStickers = {
                        navController.navigate(ConcordRoute.BOTTOMSHEETSTICKER)
                    },
                    onDeselectMedia = {
                        viewModel.deselectMedia()
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