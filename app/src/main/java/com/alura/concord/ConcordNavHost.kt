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
import com.alura.concord.navigation.*
import com.alura.concord.ui.chat.MessageListViewModel
import com.alura.concord.ui.chat.MessageScreenUiState
import com.google.accompanist.navigation.material.*

private lateinit var requestPermissionLauncher: ManagedActivityResultLauncher<String, Boolean>


@OptIn(ExperimentalMaterialNavigationApi::class)
@Composable
fun ConcordNavHost(
    modifier: Modifier = Modifier,
    bottomSheetNavigator: BottomSheetNavigator = rememberBottomSheetNavigator(),
    navController: NavHostController = rememberNavController(bottomSheetNavigator)
) {

    // viewModel "Global'
    val viewModel = hiltViewModel<MessageListViewModel>()
    val context = LocalContext.current

    requestPermissionLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (isGranted) {
                viewModel.setImagePerssion(true)
                context.showMessage("Permissões concedidas")
            } else {
                navController.navigateUp()
                context.showMessage("Permissões ainda não concedidas")
            }
        }

    ModalBottomSheetLayout(
        bottomSheetNavigator = bottomSheetNavigator,
        modifier = modifier,
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

            messageGraph(
                showSheetFiles = {
                    navController.navigate(bottomsheet_files)
                },
                showSheetStickers = {
                    navController.navigate(bottomsheet_stickers)
                },
                onBack = {
                    navController.navigateUp()
                },
                viewModel = viewModel
            )
        }
    }
}


fun checkImagePermission(context: Context, onBack: () -> Unit) {
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