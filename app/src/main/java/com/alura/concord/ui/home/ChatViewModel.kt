package com.alura.concord.ui.home

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.alura.concord.data.Author
import com.alura.concord.data.Message
import com.alura.concord.data.messageListSample
import com.alura.concord.database.preferences.PreferencesKey
import com.alura.concord.database.preferences.PreferencesKey.RECENT_IMAGES
import com.alura.concord.database.preferences.dataStoreFiles
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class ChatViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(ChatScreenUiState())
    val uiState: StateFlow<ChatScreenUiState>
        get() = _uiState.asStateFlow()

    init {
        _uiState.update { state ->
            state.copy(
                onMessageValueChange = {
                    _uiState.value = _uiState.value.copy(
                        messageValue = it
                    )
                },
                onMediaInSelectionChange = {
                    _uiState.value = _uiState.value.copy(
                        mediaInSelection = it
                    )
                },
            )
        }
    }

    fun loadSampleMessages(context: Context) {
//        viewModelScope.launch {
//            readArrayStringFromDataStore(context).collect {
//                _uiState.value = _uiState.value.copy(
//                    messages = it,
//                )
//            }
//        }

        _uiState.value = _uiState.value.copy(
            messages = messageListSample,
        )
    }

    fun sendMessage() {
        with(_uiState) {
            //val messageValue = value.messageValue
            // updateUi()
            //searchResponse(messageValue)
        }
    }

    fun saveInDataStore(context: Context) {
        updateUi()
        viewModelScope.launch {
            context.dataStoreFiles.edit { preferences ->
                val currentArrayMesssage = preferences[RECENT_IMAGES] ?: "[]"
                val currentMessageSerialized =
                    Json.decodeFromString<List<Message>>(currentArrayMesssage.toString())
                        .toMutableList()

                val userMessage = Message(
                    content = _uiState.value.messageValue,
                    author = Author.USER,
                    mediaLink = _uiState.value.mediaInSelection
                )

                currentMessageSerialized.add(userMessage)

                val newArrayString = Json.encodeToString(currentMessageSerialized)
                preferences[RECENT_IMAGES] = newArrayString

                _uiState.value = _uiState.value.copy(
                    messageValue = "", mediaInSelection = ""
                )
            }
        }
    }

    private fun searchResponse(messageValue: String) {
    }

    private fun updateUi() {
        with(_uiState) {
            val userMessage = Message(
                content = value.messageValue,
                author = Author.USER,
                mediaLink = value.mediaInSelection
            )

            value = value.copy(
                messages = value.messages.plus(
                    listOf(
                        userMessage, Message(author = Author.LOAD)
                    )
                ),
            )
        }
    }

    fun updateshowError() {
        _uiState.value = _uiState.value.copy(
            showError = false, error = ""
        )
    }


    private fun readArrayStringFromDataStore(context: Context): Flow<List<Message>> {
        return context.dataStoreFiles.data.map { preferences ->
            val messageArray: String = preferences[RECENT_IMAGES] ?: "[]"
            val messageList = Json.decodeFromString<List<Message>>(messageArray)
            messageList
        }
    }

    fun loadMediaInScreen(
        uri: String
    ) {
        _uiState.value = _uiState.value.copy(
            mediaInSelection = uri
        )
    }
}