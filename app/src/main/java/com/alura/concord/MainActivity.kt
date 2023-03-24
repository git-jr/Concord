package com.alura.concord

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
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

    ModalBottomSheetLayout(bottomSheetNavigator) {
        NavHost(navController, ConcordRoute.HOME) {
            composable(ConcordRoute.HOME) {

                val viewModel = viewModel<ChatViewModel>()
                val state by viewModel.uiState.collectAsState()

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
                BottomSheetFiles(onItemClick = {
                    navController.navigate(ConcordRoute.BOTTOMSHEETFILE)
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


