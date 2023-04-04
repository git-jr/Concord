package com.alura.concord.data

import androidx.compose.ui.tooling.preview.datasource.LoremIpsum

val messageListSample = listOf(
    Message("Olá", Author.USER),
    Message(LoremIpsum(2).values.first(), Author.AI),
    Message(
        LoremIpsum(13).values.first(),
        Author.USER,
    ),
    Message(LoremIpsum(14).values.last(), Author.AI),
    Message(
        LoremIpsum(8).values.first(),
        Author.USER,
        mediaLink = "/data/user/0/com.alura.concord/app_temImages/94f077ed-a81f-4a74-ac5b-6e44130e80b1"
    ),
    Message(
        LoremIpsum(13).values.first(),
        Author.USER,
        mediaLink = "/storage/emulated/0/Download/20210329_105243.jpg"
    ),
    Message(
        LoremIpsum(1).values.first(),
        Author.USER,
        mediaLink = "/sdcard/.transforms/synthetic/picker/0/com.android.providers.media.photopicker/media/1000000049.jpg"
    ),
    Message(LoremIpsum(7).values.first(), Author.AI),
)

val stickersListSample = List(24) { index ->
    Sticker(
        name = "Sticker ${index + 1}",
        url = "https://picsum.photos/200?random=${index + 1}"
    )
}

