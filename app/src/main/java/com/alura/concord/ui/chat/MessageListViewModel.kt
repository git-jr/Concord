package com.alura.concord.ui.chat

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.alura.concord.data.Author
import com.alura.concord.data.Message
import com.alura.concord.data.messageListSample
import com.alura.concord.database.MessageDao
import com.alura.concord.messageChatIdArgument
import com.alura.concord.util.CHAT_ID
import com.alura.concord.util.getFormattedCurrentDate
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MessageListViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    private val messageDao: MessageDao
) : ViewModel() {
    private val _uiState = MutableStateFlow(MessageScreenUiState())
    val uiState: StateFlow<MessageScreenUiState>
        get() = _uiState.asStateFlow()

//    private val chatId = savedStateHandle[messageChatIdArgument]
    private var chatId = savedStateHandle.get<Long>(messageChatIdArgument) ?: 0L

    init {

//        initWithSamples()
        getMessages()

        _uiState.update { state ->
            state.copy(
                onMessageValueChange = {
                    _uiState.value = _uiState.value.copy(
                        messageValue = it
                    )

                    _uiState.value = _uiState.value.copy(
                        hasContentToSend = (it.isNotEmpty() || _uiState.value.mediaInSelection.isNotEmpty())
                    )
                },

                onMediaInSelectionChange = {
                    _uiState.value = _uiState.value.copy(
                        mediaInSelection = it
                    )
                    _uiState.value = _uiState.value.copy(
                        hasContentToSend = (it.isNotEmpty() || _uiState.value.messageValue.isNotEmpty())
                    )
                },
            )
        }
    }

    fun initWithSamples() {
        _uiState.value = _uiState.value.copy(
            messages = messageListSample,
        )
    }

    fun getMessages() {
        viewModelScope.launch {
            messageDao.getAll().collect { messages ->
                messages?.let {
                    _uiState.value = _uiState.value.copy(
                        messages = it
                    )
                }
            }
        }
    }

    suspend fun sendMessage() {
        with(_uiState) {
            if (!value.hasContentToSend) {
                return
            }

            val userMessage = Message(
                content = value.messageValue,
                author = Author.USER,
                chatId = chatId,
                mediaLink = value.mediaInSelection,
                date = getFormattedCurrentDate(),
            )
            userMessage.let { messageDao.insert(it) }
            cleanFields()
        }
    }

    private fun cleanFields() {
        _uiState.value = _uiState.value.copy(
            messageValue = "", mediaInSelection = "", hasContentToSend = false
        )
    }


    fun loadMediaInScreen(
        uri: String
    ) {
        _uiState.value.onMediaInSelectionChange(uri)
    }

    fun deselectMedia() {
        _uiState.value = _uiState.value.copy(
            mediaInSelection = "",
        )
    }

    fun setChatId(chatId: Long) {
         this.chatId = chatId
    }


}