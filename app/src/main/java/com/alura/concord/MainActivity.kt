package com.alura.concord

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModelProvider
import com.alura.concord.extensions.showMessage
import com.alura.concord.ui.chat.MessageListViewModel
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


