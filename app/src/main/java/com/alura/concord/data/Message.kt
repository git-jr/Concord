package com.alura.concord.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Message(
    @PrimaryKey
    val id: Long = 0L,
    val chatId: Long = 0L,
    val content: String = "",
    val author: Author = Author.AI,
    val date: String = "",
    val mediaLink: String = ""
)


enum class Author {
    LOAD, USER, AI
}