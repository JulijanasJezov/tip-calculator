package com.jjapps.tipcalculator.util

import com.google.android.material.textfield.TextInputLayout

fun TextInputLayout.addDecimalLimiter(digitsBeforeZero: Int = 7, maxLimit: Int = 2) {
    editText?.filters = arrayOf(DecimalDigitsInputFilter(digitsBeforeZero, maxLimit))
}

fun TextInputLayout.addDigitsRangeLimit(min: Int = 0, max: Int = 100) {
    editText?.filters = arrayOf(DigitsRangeInputFilter(min, max))
}

fun Float.format(digits: Int) = "%.${digits}f".format(this)