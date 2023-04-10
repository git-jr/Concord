package com.alura.concord.ui.chat.files

import com.alura.concord.data.Document

data class DocumentListUiState(
    val documents: List<Document> = emptyList(),
)