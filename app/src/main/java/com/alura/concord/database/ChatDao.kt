package com.alura.concord.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy.Companion.REPLACE
import androidx.room.Query
import com.alura.concord.data.Chat
import kotlinx.coroutines.flow.Flow

@Dao
interface ChatDao {

    @Insert(onConflict = REPLACE)
    suspend fun insert(chat: Chat)

    @Query("SELECT * FROM Chat")
    fun searchAll(): Flow<List<Chat>?>

    @Query("SELECT * FROM Chat WHERE id = :id")
    fun searchById(id: Long): Flow<Chat?>

    @Query("DELETE FROM Chat WHERE id = :id")
    suspend fun delete(id: Long)
}