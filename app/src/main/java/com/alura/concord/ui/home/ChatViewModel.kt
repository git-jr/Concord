package com.alura.concord.ui.home

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.alura.concord.data.Author
import com.alura.concord.data.Message
import com.alura.concord.data.messageListSample
import com.alura.concord.database.preferences.PreferencesKey
import com.alura.concord.database.preferences.dataStoreFiles
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

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
                onImageInSelectionChange = {
                    _uiState.value = _uiState.value.copy(
                        imageInSelection = it
                    )
                },
            )
        }

        _uiState.value = _uiState.value.copy(
            messages = messageListSample,
        )

    }

    fun loadRecentImages(context: Context) {
        viewModelScope.launch {
            readArrayStringFromDataStore(context).collect {
                if (it.isEmpty()) return@collect
                val imageUri = it.last()
                if (imageUri.isNotEmpty()) {
                    _uiState.value = _uiState.value.copy(
                        imageInSelection = imageUri
                    )
                }
            }
        }
    }

    fun addNewRecentImage(context: Context, newIamge: String) {
        viewModelScope.launch {
            addToArrayStringAndSaveToDataStoreIfNotExists(context, newIamge)
        }
    }

    fun sendMessage() {
        with(_uiState) {
            val messageValue = value.messageValue
            updateUi()
            searchResponse(messageValue)
        }
    }

    private fun searchResponse(messageValue: String) {
    }

    private fun updateUi() {
        with(_uiState) {
            val userMessage = Message(
                content = value.messageValue, author = Author.USER
            )

            value = value.copy(
                messages = value.messages.plus(
                    listOf(
                        userMessage,
                        Message(author = Author.LOAD)
                    )
                ),
                messageValue = ""
            )
        }
    }

    fun updateshowError() {
        _uiState.value = _uiState.value.copy(
            showError = false,
            error = ""
        )
    }


    private fun readArrayStringFromDataStore(context: Context): Flow<Array<String>> {
        return context.dataStoreFiles.data.map { preferences ->
            preferences[PreferencesKey.RECENT_IMAGES]?.split(",")?.toTypedArray() ?: emptyArray()
        }
    }

    private suspend fun addToArrayStringAndSaveToDataStoreIfNotExists(
        context: Context,
        newString: String
    ) {
        context.dataStoreFiles.edit { preferences ->
            val currentArray =
                preferences[PreferencesKey.RECENT_IMAGES]?.split(",")?.toMutableList()
                    ?: mutableListOf()

            if (!currentArray.contains(newString)) {
                currentArray.add(newString)
                preferences[PreferencesKey.RECENT_IMAGES] =
                    currentArray.joinToString(separator = ",")
            }
        }
    }
}