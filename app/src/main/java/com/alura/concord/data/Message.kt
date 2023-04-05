package com.alura.concord.data

import kotlinx.serialization.Serializable

@Serializable // Usando apenas para serialização temporária, remover quando trocar para Room
data class Message(
    val content: String = "",
    val author: Author = Author.AI,
    val date: String = "",
    val mediaLink: String = ""
)

@Serializable  // Usando apenas para serialização temporária, remover quando trocar para Room
enum class Author {
    LOAD, USER, AI
}