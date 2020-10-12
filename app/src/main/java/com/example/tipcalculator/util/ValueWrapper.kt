package com.example.tipcalculator.util

class ValueWrapper<T>(private val value: T) {

    private var isConsumed = false

    fun get(): T? =
        if (isConsumed) {
            null
        } else {
            isConsumed = true
            value
        }
}