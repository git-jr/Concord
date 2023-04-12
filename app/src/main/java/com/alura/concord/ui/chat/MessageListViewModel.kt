package com.alura.concord.ui.chat

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.alura.concord.ConcordApplication
import com.alura.concord.R
import com.alura.concord.data.Author
import com.alura.concord.data.Message
import com.alura.concord.data.messageListSample
import com.alura.concord.database.ChatDao
import com.alura.concord.database.MessageDao
import com.alura.concord.database.preferences.PreferencesKey.LAST_OPEN_CHAT
import com.alura.concord.navigation.messageChatIdArgument
import com.alura.concord.util.getFormattedCurrentDate
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MessageListViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    private val messageDao: MessageDao,
    private val dataStore: DataStore<Preferences>,
    private val chatDao: ChatDao,
    private val application: ConcordApplication,
) : ViewModel() {
    private val _uiState = MutableStateFlow(MessageListUiState())
    val uiState: StateFlow<MessageListUiState>
        get() = _uiState.asStateFlow()

    private var chatId: Long = savedStateHandle.get<String>(messageChatIdArgument)?.toLong() ?: 0

    init {
//        initWithSamples()
        loadMessages()

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

    fun loadMessages() {
        viewModelScope.launch {
            val chat = chatDao.getById(chatId).first()
            chat?.let {
                _uiState.value = _uiState.value.copy(
                    ownerName = chat.owner,
                    profilePicOwner = chat.profilePicOwner
                )
            }
        }


        viewModelScope.launch {
            messageDao.getByChatId(chatId).collect { messages ->
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
            updateLastMessageChat(userMessage)
            cleanFields()
        }
    }

    private suspend fun updateLastMessageChat(
        userMessage: Message
    ) {
        userMessage.let { messageDao.insert(it) }
        val lastMessage =
            _uiState.value.messageValue.ifEmpty {
                application.applicationContext.getString(
                    R.string.media
                )
            }

        chatDao.updateLastMessage(chatId, lastMessage, userMessage.date)
    }

    private fun cleanFields() {
        _uiState.value = _uiState.value.copy(
            messageValue = "",
            mediaInSelection = "",
            hasContentToSend = false
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
            hasContentToSend = false
        )
    }

//    fun setChatId(chatId: Long) {
//        this.chatId = chatId
//
//        viewModelScope.launch {
//            loadMessages()
//
//            val chat = chatDao.getById(chatId).first()
//            chat?.let {
//                _uiState.value = _uiState.value.copy(
//                    ownerName = chat.owner,
//                    profilePicOwner = chat.profilePicOwner
//                )
//            }
//        }
//    }

    fun setImagePermission(value: Boolean) {
        _uiState.value = _uiState.value.copy(
            hasImagePermission = value,
        )
    }


    suspend fun cleanLastOpenChat() {
        viewModelScope.launch {
            dataStore.edit {
                it.remove(LAST_OPEN_CHAT)
            }
        }
    }

}