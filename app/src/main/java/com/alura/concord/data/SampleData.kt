package com.alura.concord.data

import androidx.compose.ui.tooling.preview.datasource.LoremIpsum
import kotlin.random.Random

val messageListSample = listOf(
    Message(
        "Olá",
        Author.USER,
        date = getRandomDate(),
    ),
    Message(LoremIpsum(2).values.first(), Author.AI, date = getRandomDate()),
    Message(
        LoremIpsum(13).values.first(),
        Author.USER,
        date = getRandomDate(),
    ),
    Message(LoremIpsum(14).values.last(), Author.AI),
    Message(
        LoremIpsum(8).values.first(),
        Author.USER,
        date = getRandomDate(),
        mediaLink = "/data/user/0/com.alura.concord/app_temImages/94f077ed-a81f-4a74-ac5b-6e44130e80b1"
    ),
    Message(
        LoremIpsum(13).values.first(),
        Author.USER,
        date = getRandomDate(),
        mediaLink = "/storage/emulated/0/Download/20210329_105243.jpg"
    ),
    Message(
        LoremIpsum(1).values.first(),
        Author.USER,
        date = getRandomDate(),
        mediaLink = "/storage/07E9-111C/Android/data/com.alura.concord/files/IMG_20230405_190725.jpg"
    ),
    Message(LoremIpsum(7).values.first(), Author.AI),
)

val stickersListSample = List(24) { index ->
    Sticker(
        name = "Sticker ${index + 1}",
        url = "https://picsum.photos/200?random=${index + 1}"
    )
}

fun getRandomDate(): String {
    val hour = Random.nextInt(0, 24)
    val minute = Random.nextInt(0, 60)
    return String.format("%02d:%02d", hour, minute)
}
