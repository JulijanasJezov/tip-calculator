package com.example.tipcalculator.model

import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity
data class Bill(
    val tip: String?,
    val partySize: String?,
    val tipAmount: String,
    val totalAmount: String,
    val perPersonAmount: String,
    val date: String,
    @PrimaryKey(autoGenerate = true)
    val id: Int? = null
)