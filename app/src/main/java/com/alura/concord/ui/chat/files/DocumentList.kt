package com.alura.concord.ui.chat.files

import android.content.ContentResolver
import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.alura.concord.R
import com.alura.concord.data.Document
import com.alura.concord.data.documentListSample
import com.alura.concord.ui.components.AsyncImage
import com.alura.concord.ui.theme.ConcordTheme


@Composable
fun DocumentListScreen(
    state: DocumentListUiState,
    modifier: Modifier = Modifier,
    onOpenDocument: (Long) -> Unit = {},
) {
    val documentList = readFilesFromExternalStorage(LocalContext.current)

    Scaffold(
        topBar = {
            AppBarDocumentList()
        }) { paddingValues ->
        LazyColumn(modifier.padding(paddingValues)) {
            items(documentList) { document ->
                DocumentItem(document) { documentId ->
                    onOpenDocument(documentId)
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppBarDocumentList() {
    TopAppBar(
        title = {
            Text(text = stringResource(id = R.string.documents), fontWeight = FontWeight.Medium)
        },
        navigationIcon = {
            IconButton(
                onClick = { }
            ) {
                Icon(
                    Icons.Default.ArrowBack,
                    tint = Color.White,
                    contentDescription = null
                )
            }
        },
        actions = {
            Row {
                IconButton(
                    onClick = { }
                ) {
                    Icon(
                        Icons.Default.Search,
                        tint = Color.White,
                        contentDescription = null
                    )
                }

                IconButton(onClick = { }) {
                    Icon(
                        Icons.Default.List,
                        tint = Color.White,
                        contentDescription = null
                    )
                }
            }

        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.primary,
            titleContentColor = Color.White
        )
    )
}

@Composable
fun DocumentItem(
    document: Document, onClick: (Long) -> Unit
) {
    Column(
        Modifier
            .clickable { onClick(document.id) },
    ) {
        Spacer(Modifier.height(10.dp))
        Row(
            Modifier.padding(horizontal = 16.dp),
        ) {
            AsyncImage(
                imageUrl = document.icon,
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape),
            )
            Column(
                Modifier
                    .padding(start = 8.dp)
                    .align(Alignment.CenterVertically)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()

                ) {
                    Text(
                        text = document.name,
                        maxLines = 3,
                        overflow = TextOverflow.Ellipsis,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.weight(0.8f)
                    )
                    Text(
                        text = document.date,
                        fontSize = 12.sp,
                        textAlign = TextAlign.End,
                        color = MaterialTheme.colorScheme.outline,
                        modifier = Modifier.weight(0.2f)
                    )
                }

                Text(
                    text = document.size,
                    color = MaterialTheme.colorScheme.outline,
                    modifier = Modifier.fillMaxWidth(),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
        Spacer(Modifier.height(10.dp))
    }
}

@Preview
@Composable
fun DocumentListScreenPreview() {
    ConcordTheme {
        DocumentListScreen(
            state = DocumentListUiState(documentListSample)
        )
    }
}

@Preview
@Composable
fun DocumentListItemPreview() {
    ConcordTheme {
        DocumentItem(documentListSample.first()) {}
    }
}

private fun readFilesFromExternalStorage(context: Context): MutableList<Document> {

    val collection: Uri =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            MediaStore.Files.getContentUri(MediaStore.VOLUME_EXTERNAL)
        } else {
            MediaStore.Files.getContentUri("external")
        }


    val selection = "${MediaStore.Files.FileColumns.MEDIA_TYPE}=?"
    val selectionArgs = arrayOf(MediaStore.Files.FileColumns.MEDIA_TYPE_DOCUMENT.toString())


    val documentList = mutableListOf<Document>()
    val projection = arrayOf(
        MediaStore.Files.FileColumns._ID,
        MediaStore.Files.FileColumns.DATA,
        MediaStore.Files.FileColumns.DISPLAY_NAME,
        MediaStore.Files.FileColumns.SIZE

    )

    val queryUri = MediaStore.Files.getContentUri("external")
    val cursor = context.contentResolver.query(
        collection,
        projection,
        selection,
        selectionArgs,
        null
    )

    cursor?.use { c ->
        val idColumn = c.getColumnIndexOrThrow(MediaStore.Files.FileColumns._ID)
        val dataColumn = c.getColumnIndexOrThrow(MediaStore.Files.FileColumns.DATA)
        val nameColumn = c.getColumnIndexOrThrow(MediaStore.Files.FileColumns.DISPLAY_NAME)
        val sizeColumn = c.getColumnIndexOrThrow(MediaStore.Files.FileColumns.SIZE)

        while (c.moveToNext()) {
            val id = c.getLong(idColumn)
            val data = c.getString(dataColumn)
            val name = c.getString(nameColumn)
            val size = c.getLong(sizeColumn)

            Log.i("Files", "Id: $id, Data: $data, Name: $name, Size: $size")

            name?.let {
                documentList += Document(
                    id = id,
                    name = name,
                    size = size.toString(),
                    date = data,
                    path = "contentUri.path.toString()"
                )
            }
        }
    }

    return documentList
}

private fun readFilesFromExternalStorage2(context: Context): MutableList<Document> {

    val documentList = mutableListOf<Document>()

    return documentList
}

fun loadDocuments(context: Context): MutableList<Document> {
    val documentList = mutableListOf<Document>()

    val collection: Uri =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            MediaStore.Files.getContentUri(
                MediaStore.VOLUME_EXTERNAL
            )
        } else {
            MediaStore.Files.getContentUri(MediaStore.VOLUME_EXTERNAL)
        }

    val projection = arrayOf(
        MediaStore.Files.FileColumns._ID,
        MediaStore.Files.FileColumns.DISPLAY_NAME,
        MediaStore.Files.FileColumns.SIZE
    )

    val query = context.contentResolver.query(
        collection,
        projection,
        null,
        null,
        null
    )

    query?.use { cursor ->
        val idColumn = cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns._ID)
        val nameColumn = cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.DISPLAY_NAME)
        val sizeColumn = cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.SIZE)

        while (cursor.moveToNext()) {
            val id = cursor.getLong(idColumn)
            val name = cursor.getString(nameColumn)
            val size = cursor.getInt(sizeColumn)

//            val contentUri: Uri = ContentUris.withAppendedId(
//                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
//                id
//            )

            documentList += Document(
                id = id,
                name = name,
                size = size.toString(),
                path = "contentUri.path.toString()"
            )
        }
    }

    return documentList
}