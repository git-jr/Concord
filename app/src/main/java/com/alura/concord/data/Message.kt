package com.alura.concord.data

data class Message(val content: String = "", val author: Author = Author.AI)

enum class Author {
    LOAD, USER, AI
}