package com.jjapps.tipcalculator.util

import java.text.DateFormat
import java.text.DateFormat.getDateInstance
import java.text.SimpleDateFormat
import java.util.*

fun Date.format(pattern: String): String {
    val simpleDateFormat = SimpleDateFormat(pattern, Locale.getDefault())
    return simpleDateFormat.format(this)
}

fun Date.formatMedium(): String {
    val formatter = getDateInstance(DateFormat.MEDIUM)
    return formatter.format(this)
}
