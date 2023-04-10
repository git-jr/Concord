package com.alura.concord.data

import androidx.compose.ui.tooling.preview.datasource.LoremIpsum
import com.alura.concord.util.getRandomDate

val messageListSample = mutableListOf(
    Message(
        content = "Olá",
        author = Author.USER,
        date = getRandomDate(),
    ),
    Message(
        content = LoremIpsum(2).values.first(),
        author = Author.AI,
        date = getRandomDate()
    ),
    Message(
        content = LoremIpsum(13).values.first(),
        author = Author.USER,
        date = getRandomDate(),
    ),
    Message(
        content = LoremIpsum(14).values.last(),
        author = Author.AI
    ),
    Message(
        content = LoremIpsum(8).values.first(),
        author = Author.USER,
        date = getRandomDate(),
        mediaLink = "/data/user/0/com.alura.concord/app_temImages/94f077ed-a81f-4a74-ac5b-6e44130e80b1"
    ),
    Message(
        content = LoremIpsum(13).values.first(),
        author = Author.USER,
        date = getRandomDate(),
        mediaLink = "/storage/emulated/0/Download/20210329_105243.jpg"
    ),
    Message(
        content = LoremIpsum(1).values.first(),
        author = Author.USER,
        date = getRandomDate(),
        mediaLink = "/storage/07E9-111C/Android/data/com.alura.concord/files/IMG_20230405_190725.jpg"
    ),
    Message(
        content = LoremIpsum(7).values.first(),
        author = Author.AI
    ),
)

val stickersListSample = List(24) { index ->
    Sticker(
        name = "Sticker ${index + 1}",
        url = "https://picsum.photos/200?random=${index + 1}"
    )
}

val chatListSample = mutableListOf(
    Chat(
        id = 1L,
        owner = "João",
        profilePicOwner = "https://picsum.photos/id/1015/200/200",
        lastMessage = "Olá, tudo bem?",
        date = "09:00"
    ),
    Chat(
        id = 2L,
        owner = "Maria",
        profilePicOwner = "https://picsum.photos/id/1020/200/200",
        lastMessage = "Sim, e com você?",
        date = "10:15"
    ),
    Chat(
        id = 3L,
        owner = "Grupo de Estudos",
        profilePicOwner = "https://picsum.photos/id/1030/200/200",
        lastMessage = "A reunião será na sexta-feira",
        date = "11:30"
    ),
    Chat(
        id = 4L,
        owner = "Pedro",
        profilePicOwner = "https://picsum.photos/id/1040/200/200",
        lastMessage = "O que você está fazendo?",
        date = "12:45"
    ),
    Chat(
        id = 5L,
        owner = "Grupo de Família",
        profilePicOwner = "https://picsum.photos/id/1050/200/200",
        lastMessage = "Feliz Aniversário, tio!",
        date = "13:00"
    ),
    Chat(
        id = 6L,
        owner = "Ana",
        profilePicOwner = "https://picsum.photos/id/1060/200/200",
        lastMessage = "Estou no caminho",
        date = "14:30"
    ),
    Chat(
        id = 7L,
        owner = "Lucas",
        profilePicOwner = "https://picsum.photos/id/1070/200/200",
        lastMessage = "Vamos ao cinema hoje à noite?",
        date = "15:00"
    ),
    Chat(
        id = 8L,
        owner = "Grupo de Trabalho",
        profilePicOwner = "https://picsum.photos/id/1080/200/200",
        lastMessage = "Precisamos terminar o relatório",
        date = "16:00"
    ),
    Chat(
        id = 9L,
        owner = "Fernanda",
        profilePicOwner = "https://picsum.photos/id/1090/200/200",
        lastMessage = "Obrigado pelo presente!",
        date = "17:30"
    ),
    Chat(
        id = 10L,
        owner = "Grupo de Amigos",
        profilePicOwner = "https://picsum.photos/id/1100/200/200",
        lastMessage = "Vamos marcar um encontro",
        date = "18:45"
    )
)
val documentListSample = mutableListOf(
    Document(
        id = 1L,
        name = "Documento em PDF",
        size = "42 kb",
        icon = "",
        date = "02/02/2020",
        path = ""
    ),

    Document(
        id = 1L,
        name = "Documento em Docx",
        size = "1,5 MB",
        icon = "",
        date = "12/12/2012",
        path = ""
    ),
)

