package com.alura.concord.medias

import android.Manifest
import android.content.ContentUris
import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.util.Size
import androidx.activity.compose.ManagedActivityResultLauncher
import com.alura.concord.data.Image


fun loadImagesAndThumbs(context: Context): MutableList<Image> {
    val imageList = mutableListOf<Image>()

    val collection: Uri =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            MediaStore.Images.Media.getContentUri(
                MediaStore.VOLUME_EXTERNAL
            )
        } else {
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        }

    val projection =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            arrayOf(
                MediaStore.Images.Media._ID,
                MediaStore.Images.Media.DISPLAY_NAME,
                MediaStore.Images.Media.SIZE,
            )
        } else {
            arrayOf(
                MediaStore.Images.Media._ID,
                MediaStore.Images.Media.DISPLAY_NAME,
                MediaStore.Images.Media.SIZE,
                MediaStore.Images.Thumbnails._ID,
                MediaStore.Images.Thumbnails.DATA
            )
        }

    val query = context.contentResolver.query(
        collection,
        projection,
        null,
        null,
        null
    )

    query?.use { cursor ->
        val idColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media._ID)
        val nameColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DISPLAY_NAME)
        val sizeColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.SIZE)

        while (cursor.moveToNext()) {
            val id = cursor.getLong(idColumn)
            val name = cursor.getString(nameColumn)
            val size = cursor.getInt(sizeColumn)

            val contentUri: Uri = ContentUris.withAppendedId(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                id
            )

            var thumbnail: Bitmap

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                // Load thumbs on Android >10
                // https://developer.android.com/reference/android/content/ContentResolver#loadThumbnail(android.net.Uri,%20android.util.Size,%20android.os.CancellationSignal)

                val thumbnailSize = Size(640, 480)
                val thumbnailAboveAndroid10: Bitmap = context.contentResolver.loadThumbnail(
                    contentUri,
                    thumbnailSize,
                    null
                )

                thumbnail = thumbnailAboveAndroid10
            } else {
                // Load thumbs on Android <10
                // https://developer.android.com/reference/android/provider/MediaStore.Images.Thumbnails
                val thumbnailIdColumn = cursor.getColumnIndex(MediaStore.Images.Thumbnails._ID)
                val thumbnailDataColumn = cursor.getColumnIndex(MediaStore.Images.Thumbnails.DATA)
                val thumbnailId = cursor.getLong(thumbnailIdColumn)
                val thumbnailPath = cursor.getString(thumbnailDataColumn)
                val thumbnailUri = ContentUris.withAppendedId(
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                    thumbnailId
                )

                val thumbnailBelowAndroid10 = MediaStore.Images.Thumbnails.getThumbnail(
                    context.contentResolver,
                    thumbnailId,
                    MediaStore.Images.Thumbnails.MINI_KIND,
                    null
                )

                thumbnail = thumbnailBelowAndroid10
            }


            imageList += Image(
                name = name,
                thumbnail = thumbnail,
                uri = contentUri,
                size = size,
            )
        }
    }

    return imageList
}

fun requestImagePermission(requestPermissionLauncher: ManagedActivityResultLauncher<String, Boolean>) {
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