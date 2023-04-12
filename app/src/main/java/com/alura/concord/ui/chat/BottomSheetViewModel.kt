package com.alura.concord.ui.chat

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import javax.inject.Inject

@HiltViewModel
class BottomSheetViewModel @Inject constructor(

) : ViewModel() {
    private val _uiState = MutableStateFlow(BottomSheetState())
    val uiState: StateFlow<BottomSheetState>
        get() = _uiState.asStateFlow()


    init {
//        initWithSamples()
//        loadMessages()

        _uiState.update { state ->
            state.copy(
                onShowBottomSheetFileChange = {
                    _uiState.value = _uiState.value.copy(
                        showBottomSheetFile = it
                    )
                },

                onShowBottomSheetStickerChange = {
                    _uiState.value = _uiState.value.copy(
                        showBottomSheetSticker = it
                    )

                },
            )
        }
    }

//    fun initWithSamples() {
//        _uiState.value = _uiState.value.copy(
//            messages = messageListSample,
//        )
//    }

}