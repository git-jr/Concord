package com.alura.concord.ui.home

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.alura.concord.data.chatListSample
import com.alura.concord.database.ChatDao
import com.alura.concord.database.preferences.PreferencesKey.LAST_OPEN_CHAT
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ChatListViewModel @Inject constructor(
    private val dataStore: DataStore<Preferences>,
    private val chatDao: ChatDao,
) : ViewModel() {

    private val _uiState = MutableStateFlow(ChatListUiState())
    val uiState: StateFlow<ChatListUiState>
        get() = _uiState.asStateFlow()

    init {
        loadChats()
        checkLastOpenChat()
    }

    private fun checkLastOpenChat() {
        viewModelScope.launch {
            val lastOpenChat = dataStore.data.first()[LAST_OPEN_CHAT]
            lastOpenChat?.let {
                _uiState.value = _uiState.value.copy(
                    selectedId = lastOpenChat
                )
            }
        }
    }

    fun setChatId(chatId: Long?) {
        _uiState.value = _uiState.value.copy(
            selectedId = chatId
        )

        chatId?.let {
            saveIdLastChatOpened(chatId)
        }
    }

    private fun saveIdLastChatOpened(chatId: Long) {
        viewModelScope.launch {
            dataStore.edit {
                it[LAST_OPEN_CHAT] = chatId
            }
        }
    }

    private fun loadChats() {
        viewModelScope.launch {
            chatDao.getAll().collect { chatList ->
                chatList?.let {
                    _uiState.value = _uiState.value.copy(
                        chats = chatList
                    )
                } ?: run {
                    _uiState.value = _uiState.value.copy(
                        chats = chatListSample
                    )
                }
            }
        }

    }
}