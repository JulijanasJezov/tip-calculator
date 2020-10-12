package com.example.tipcalculator.util

import java.text.DateFormat.getDateInstance
import java.text.SimpleDateFormat
import java.util.*

fun Date.format(pattern: String): String {
    val simpleDateFormat = SimpleDateFormat(pattern, Locale.getDefault())
    return simpleDateFormat.format(Date())
}