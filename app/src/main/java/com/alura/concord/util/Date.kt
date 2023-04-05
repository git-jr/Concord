package com.alura.concord.util

import java.text.SimpleDateFormat
import java.util.*


fun getFormattedCurrentDate(): String {
    val currentTime = Calendar.getInstance().time
    val dateFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
    return dateFormat.format(currentTime)
}