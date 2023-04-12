package com.alura.concord.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Chat(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,
    val owner: String = "",
    val profilePicOwner: String = "",
    val lastMessage: String = "",
    val dateLastMessage: String = "",
)
