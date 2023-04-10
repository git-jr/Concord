package com.alura.concord.ui.chat.files

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
class DocumentListViewModel @Inject constructor(
    private val chatDao: ChatDao,
) : ViewModel() {

    private val _uiState = MutableStateFlow(DocumentListUiState())
    val uiState: StateFlow<DocumentListUiState>
        get() = _uiState.asStateFlow()

    init {
        loadFiles()
    }

    private fun loadFiles() {
        viewModelScope.launch {
            chatDao.getAll().collect { chatList ->
                chatList?.let {
                    _uiState.value = _uiState.value.copy(
//                        chats  = chatList
                    )
                } ?: run {
                    _uiState.value = _uiState.value.copy(
//                        chats = chatListSample
                    )
                }
            }
        }
    }
}