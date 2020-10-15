package com.jjapps.tipcalculator.util

import android.text.InputFilter
import android.text.Spanned


class DigitsRangeInputFilter(private val min: Int, private val max: Int) : InputFilter {

    override fun filter(source: CharSequence, start: Int, end: Int, dest: Spanned, dstart: Int, dend: Int): CharSequence? {
        return try {
            if ("$dest$source".toInt() in min..max) null else ""
        } catch (e: Exception) {
            ""
        }
    }
}