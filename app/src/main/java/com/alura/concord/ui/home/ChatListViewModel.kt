package com.alura.concord.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.alura.concord.data.chatListSample
import com.alura.concord.database.ChatDao
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ChatListViewModel @Inject constructor(
    private val chatDao: ChatDao,
) : ViewModel() {

    private val _uiState = MutableStateFlow(ChatListUiState())
    val uiState: StateFlow<ChatListUiState>
        get() = _uiState.asStateFlow()

    init {
        loadChats()
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