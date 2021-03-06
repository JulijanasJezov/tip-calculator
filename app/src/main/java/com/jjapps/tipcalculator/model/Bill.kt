package com.jjapps.tipcalculator.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.*


@Entity
data class Bill(
    val tip: String,
    val partySize: String,
    val tipAmount: String,
    val totalAmount: String,
    val perPersonAmount: String,
    var name: String? = null,
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val creationDate: Date = Date(System.currentTimeMillis())
)