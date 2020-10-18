package com.jjapps.tipcalculator.util

import android.text.InputFilter
import android.text.Spanned
import androidx.core.text.isDigitsOnly


class DecimalDigitsInputFilter(
    private val digitsBeforeZero: Int, private val digitsAfterZero: Int
) : InputFilter {

    override fun filter(
        source: CharSequence, start: Int, end: Int, dest: Spanned, dstart: Int, dend: Int
    ): CharSequence? {
        val text: String = dest.toString()

        return if (dstart == 0 && source.isNotEmpty() && !source.isDigitsOnly()) ""
        else if (source == "." && dstart <= text.length - 3) ""
        else if (text.contains(".")) {
            val index = text.indexOf(".")
            if (dstart > index && index + digitsAfterZero + 1 <= text.length) {
                ""
            } else if (index >= digitsBeforeZero && dstart <= index) ""
            else null
        } else if (text.length >= digitsBeforeZero && source != ".") ""
        else null
    }
}