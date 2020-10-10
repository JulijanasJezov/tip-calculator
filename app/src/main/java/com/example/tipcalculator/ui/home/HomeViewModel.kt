package com.example.tipcalculator.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.tipcalculator.util.format
import java.lang.Math.round
import java.math.BigDecimal
import java.math.RoundingMode

class HomeViewModel : ViewModel() {

    val totalAmount: LiveData<String>
        get() = _totalAmount

    private val _totalAmount = MutableLiveData<String>()

    val tipAmount: LiveData<String>
        get() = _tipAmountgi

    private val _tipAmount = MutableLiveData<String>()

    val perPersonAmount: LiveData<String>
        get() = _perPersonAmount

    private val _perPersonAmount = MutableLiveData<String>()

    private var totalAmountFloat = 0f
    private var billAmount = 0f
    private var tip = 0
    private var partySize = 1

    fun updateBillAmount(amount: String?) {
        billAmount = if (!amount.isNullOrBlank()) amount.toFloat() else 0f
        calculateTotalAmount()
    }

    fun updateTipPercentage(tipString: String?) {
        tip = if (!tipString.isNullOrBlank()) tipString.toInt() else 0
        calculateTotalAmount()
    }

    fun updatePartySize(partySizeString: String?) {
        partySize = if (!partySizeString.isNullOrBlank()) partySizeString.toInt() else 1
        calculatePerPersonAmount()
    }

    private fun calculateTotalAmount() {
        totalAmountFloat = billAmount + calculateTipAmount()

        _totalAmount.value = totalAmountFloat.format(2)
        calculatePerPersonAmount()
    }

    private fun calculateTipAmount(): Float {
        val tempTip = if (tip == 0) 0f else billAmount / 100 * tip
        _tipAmount.value = tempTip.format(2)
        return tempTip
    }

    private fun calculatePerPersonAmount() {
        val tempPerPerson = totalAmountFloat / partySize
        _perPersonAmount.value = tempPerPerson.format(2)
    }
}