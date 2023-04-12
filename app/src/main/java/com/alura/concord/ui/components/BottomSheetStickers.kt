package com.alura.concord.ui.components

import android.content.ContentUris
import android.content.Context
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.alura.concord.R
import com.alura.concord.data.Image

@Composable
fun BottomSheetStickers(
    onSelectedSticker: (Uri) -> Unit = {},
) {
    Column(
        Modifier
            .fillMaxWidth()
            .heightIn(100.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "Stickers",
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            fontSize = 18.sp,
            color = MaterialTheme.colorScheme.primary
        )
        Spacer(modifier = Modifier.height(16.dp))

        val stickerList = loadImagesAndThumbs(LocalContext.current)

        LazyVerticalGrid(
            columns = GridCells.Fixed(4),
            contentPadding = PaddingValues(8.dp),
        ) {
            items(stickerList) { item ->
                Box(
                    Modifier
                        .fillMaxSize()
                        .padding(8.dp)
                        .clickable { onSelectedSticker(item.contentUri) }
                ) {

                    coil.compose.AsyncImage(
                        modifier = Modifier
                            .size(50.dp)
                            .align(Alignment.Center),
                        contentScale = ContentScale.Inside,
                        model = item.contentUri,
                        placeholder = painterResource(R.drawable.image_place_holder),
                        error = painterResource(R.drawable.image_place_holder),
                        contentDescription = null,
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(32.dp))
    }
}


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

    val projection = arrayOf(
        MediaStore.Images.Media._ID,
        MediaStore.Images.Media.DISPLAY_NAME,
        MediaStore.Images.Media.SIZE
    )

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
//
//            val thumb: Bitmap = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
//                context.contentResolver.loadThumbnail(
//                    contentUri, Size(640, 480), null
//                )
//            } else {
//                MediaStore.Images.Thumbnails.getThumbnail(
//                    context.contentResolver,
//                    id,
//                    MediaStore.Images.Thumbnails.MINI_KIND,
//                    null
//                )
//            }

            imageList += Image(contentUri, name, size)
        }
    }

    return imageList
}

@Preview
@Composable
fun BottomSheetStickersPreview() {
    BottomSheetStickers()
}