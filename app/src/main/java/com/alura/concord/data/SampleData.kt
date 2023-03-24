package com.alura.concord.data

import androidx.compose.ui.tooling.preview.datasource.LoremIpsum

val messageListSample = listOf(
    Message("Olá", Author.USER),
    Message(LoremIpsum(2).values.first(), Author.AI),
    Message(
        LoremIpsum(13).values.first(),
        Author.USER
    ),
    Message(LoremIpsum(14).values.last(), Author.AI),
)

val stickers = List(24) { index ->
    Sticker(
        name = "Sticker ${index + 1}",
        url = "https://picsum.photos/200?random=${index + 1}"
    )
}

