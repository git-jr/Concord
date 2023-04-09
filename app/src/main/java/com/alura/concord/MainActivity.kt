package com.alura.concord

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.alura.concord.ui.theme.ConcordTheme
import com.google.accompanist.navigation.material.ExperimentalMaterialNavigationApi
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @OptIn(ExperimentalMaterialNavigationApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        setContent {
            ConcordTheme {
                ConcordNavHost()

//
////                val viewModel: MessageListViewModel by viewModels()
////                val state by viewModel.uiState.collectAsState()
//
//
////                val viewModel = ViewModelProvider(this)[MessageListViewModel::class.java]
////                val state by viewModel.uiState.collectAsState()
//
//
////                val viewModel = hiltViewModel<MessageListViewModel>()
//                val viewModel: MessageListViewModel by viewModels()
//
//                val state by viewModel.uiState.collectAsState()
//
////                if (state.mediaInSelection.isNotEmpty()) {
////                    LocalContext.current.showMessage("Mudou aqui no iniicio")
////                }
//                val context = LocalContext.current
//                LaunchedEffect(state.messageValue) {
//                    context.showMessage("Mudou aqui no iniicio")
//                }
//

            }
        }
    }

}





