package com.alura.concord.util

import java.text.SimpleDateFormat
import java.util.*
import kotlin.random.Random


fun getFormattedCurrentDate(): String {
    val currentTime = Calendar.getInstance().time
    val dateFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
    return dateFormat.format(currentTime)
}

fun getRandomDate(): String {
    val hour = Random.nextInt(0, 24)
    val minute = Random.nextInt(0, 60)
    return String.format("%02d:%02d", hour, minute)
}