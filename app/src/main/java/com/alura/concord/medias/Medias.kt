package com.alura.concord.medias

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.MediaStore
import android.util.Log
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavHostController
import com.alura.concord.ui.chat.MessageListViewModel
import java.io.File
import java.io.FileOutputStream
import java.util.*


@Composable
fun setResultFromImageSelection(
    viewModel: MessageListViewModel,
    onBack: () -> Unit = {}
): ManagedActivityResultLauncher<PickVisualMediaRequest, Uri?> {
    val context = LocalContext.current

    val pickMedia =
        rememberLauncherForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
            if (uri != null) {
                createCopyFromInternalStorage(context, uri)
                try {
//                    val contentResolver = context.contentResolver
//                    val takeFlags =
//                        Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_WRITE_URI_PERMISSION
//
//                    contentResolver.takePersistableUriPermission(uri, takeFlags)
//                    viewModel.loadMediaInScreen(uri.toString())


                    var filePath: String? = null
                    if (uri.scheme == "content") { // For PhotoPicker in recents versions from Android
                        val cursor = context.contentResolver.query(uri, null, null, null, null)
                        if (cursor != null && cursor.moveToFirst()) {
                            val columnIndex =
                                cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
                            filePath = cursor.getString(columnIndex)
                            cursor.close()
                        }
                    } else { // For "documentPicker" from old versions Android (below 13)
                        filePath = uri.path
                    }

                    filePath?.let { viewModel.loadMediaInScreen(it) }
                    Log.i("PhotoPicker", "Sucesso ao tentar persistir a URI ")
                } catch (e: Exception) {
                    // errors from Android 13
                    Log.e("PhotoPicker", "Erro ao tentar persistir a URI ${e.cause} ")

                    val file = createCopyFromInternalStorage(context, uri)
                    file?.let {
                        viewModel.loadMediaInScreen(file.path)
                    }
                }
                onBack()
                Log.d("PhotoPicker", "Selected URI: $uri")
            } else {
                Log.d("PhotoPicker", "No media selected")
            }
        }
    return pickMedia
}

@Composable
fun setResultFromFileSelection(
    viewModel: MessageListViewModel,
    onBack: () -> Unit = {}
): ManagedActivityResultLauncher<PickVisualMediaRequest, Uri?> {
    val context = LocalContext.current
    val pickMedia =
        rememberLauncherForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
            if (uri != null) {
                try {
                    val contentResolver = context.contentResolver
                    val takeFlags =
                        Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_WRITE_URI_PERMISSION

                    contentResolver.takePersistableUriPermission(uri, takeFlags)
                    //viewModel.loadMediaInScreen(uri.toString())

                } catch (e: Exception) {
                    // errors from Android 13
                    Log.e("TAG", "Erro ao tentar persistir a URI ")

                    val file = createCopyFromInternalStorage(context, uri)
                    file?.let {
                        viewModel.loadMediaInScreen(file.path)
                    }
                }

                onBack()
                Log.d("PhotoPicker", "Selected URI: $uri")
            } else {
                Log.d("PhotoPicker", "No media selected")
            }
        }
    return pickMedia
}


fun createCopyFromInternalStorage(context: Context, uri: Uri): File? {
    // Obtenha um InputStream a partir da Uri usando o ContentResolver
    val inputStream = context.contentResolver.openInputStream(uri)

    // Verifique se o InputStream não é nulo
    inputStream?.use {

        // Crie um arquivo para salvar o conteúdo
        val file =
            File(
                context.getDir("tempImages", Context.MODE_PRIVATE),
                UUID.randomUUID().toString()
            )

        // Crie um FileOutputStream para gravar o conteúdo do InputStream no arquivo
        val outputStream = FileOutputStream(file)

        // Crie um buffer para armazenar os dados lidos do InputStream
        val buffer = ByteArray(4096)

        // Leia os dados do InputStream e grave-os no FileOutputStream usando o buffer
        var bytesRead = inputStream.read(buffer)
        while (bytesRead >= 0) {
            outputStream.write(buffer, 0, bytesRead)
            bytesRead = inputStream.read(buffer)
        }

        // Feche o FileOutputStream
        outputStream.close()
        return file
    }
    return null
}

fun launchPickVisualMedia(
    pickMedia: ManagedActivityResultLauncher<PickVisualMediaRequest, Uri?>,
    mimeType: String = "*/*"
) {
    pickMedia.launch(
        PickVisualMediaRequest(
            ActivityResultContracts.PickVisualMedia.SingleMimeType(
                mimeType
            )
        )
    )
}