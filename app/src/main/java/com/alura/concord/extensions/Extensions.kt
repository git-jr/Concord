package com.alura.concord.extensions

import android.content.ContentUris
import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.util.Size
import android.widget.Toast
import androidx.core.net.toUri

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


fun Context.toThumbnail(imageId: Long): Bitmap? {
    val thumbnail = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
        contentResolver.loadThumbnail(
            imageId.toURI(),
            Size(100, 100),
            null
        )
    } else {
        MediaStore.Images.Thumbnails.getThumbnail(
            contentResolver,
            imageId,
            MediaStore.Images.Thumbnails.MINI_KIND,
            null
        )
    }

    return thumbnail
}

fun Long.toURI(): Uri {
    return ContentUris.withAppendedId(
        MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
        this
    )
}