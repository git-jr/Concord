package com.alura.concord.extensions

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.alura.concord.medias.requestImagePermission

fun Context.showMessage(message: String, longTime: Boolean = false) {
    Toast.makeText(
        this,
        message,
        if (longTime) {
            Toast.LENGTH_LONG
        } else {
            Toast.LENGTH_SHORT
        }
    ).show()
}


fun Context.getPath(
    uri: Uri,
    onFoundPath: (String) -> Unit
) {
    contentResolver.query(
        uri,
        null,
        null,
        null,
        null
    )?.use { cursor ->
        cursor.moveToFirst()

        val takeFlags = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            Intent.FLAG_GRANT_READ_URI_PERMISSION
        } else {
            Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_WRITE_URI_PERMISSION
        }

        contentResolver.takePersistableUriPermission(uri, takeFlags)
        val path = uri.toString()
        onFoundPath(path)
    }
}

fun Context.getPathAndName(
    uri: Uri,
    onFound: (String, String) -> Unit
) {
    contentResolver.query(
        uri,
        null,
        null,
        null,
        null
    )?.use { cursor ->
        cursor.moveToFirst()

        val columnIndexName =
            cursor.getColumnIndexOrThrow(MediaStore.Downloads.DISPLAY_NAME)
        val fileName = cursor.getString(columnIndexName)

        val path = uri.toString()
        onFound(path, fileName)
    }

}


fun Context.checkImagePermission(
    onPermissionHasObtained: () -> Unit = {},
    requestPermissionLauncher: ManagedActivityResultLauncher<String, Boolean>
) {

    when {
        ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.READ_MEDIA_IMAGES
        ) == PackageManager.PERMISSION_GRANTED -> {
            Log.i("checkImagePermission", "checkSelfPermission: Permissão checada")
            onPermissionHasObtained()
        }

        ActivityCompat.shouldShowRequestPermissionRationale(
            this as Activity,
            Manifest.permission.READ_MEDIA_IMAGES
        ) -> {
            Log.i(
                "checkImagePermission",
                "shouldShowRequestPermissionRationale: Permissão já solicitada"
            )
            requestImagePermission(requestPermissionLauncher)
            this.showMessage(
                "É preciso conceder a permissão de acesso às imagens para usar essa função",
                true
            )
        }

        else -> {
            Log.i("checkImagePermission", "Permissão não concedida")
            requestImagePermission(requestPermissionLauncher)
        }
    }
}
