package com.alura.concord.data

import android.graphics.Bitmap
import android.net.Uri


data class Image(
    val uri: Uri,
    val name: String,
    val size: Int,
    val thumbnail: Bitmap? = null
)
