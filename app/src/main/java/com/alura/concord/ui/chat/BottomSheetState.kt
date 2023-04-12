package com.alura.concord.ui.chat

data class BottomSheetState(
    val showBottomSheetSticker: Boolean = false,
    val showBottomSheetFile: Boolean = false,
    val onShowBottomSheetStickerChange: (Boolean) -> Unit = {},
    val onShowBottomSheetFileChange: (Boolean) -> Unit = {},
)
