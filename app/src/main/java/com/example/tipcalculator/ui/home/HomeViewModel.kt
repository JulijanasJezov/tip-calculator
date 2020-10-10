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
        get() = _tipAmount

    private val _tipAmount = MutableLiveData<String>()

    val perPersonAmount: LiveData<String>
        get() = _perPersonAmount

    private val _perPersonAmount = MutableLiveData<String>()

    private var totalAmountFloat = BigDecimal(0)
    private var billAmount = BigDecimal(0)
    private var tip = 0
    private var partySize = 1

    fun updateBillAmount(amount: String?) {
        billAmount = BigDecimal(if (!amount.isNullOrBlank()) amount.toDouble() else 0.0)
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
        totalAmountFloat = billAmount.add(calculateTipAmount())
        _totalAmount.value = totalAmountFloat.setScale(2, RoundingMode.UP).toString()
        calculatePerPersonAmount()
    }

    private fun calculateTipAmount(): BigDecimal {
        val tempTip = if (tip == 0) BigDecimal.ZERO else billAmount.divide(ONE_HUNDRED)
            .times(BigDecimal.valueOf(tip.toDouble()))

        _tipAmount.value = tempTip.setScale(2, RoundingMode.UP).toString()
        return tempTip
    }

    private fun calculatePerPersonAmount() {
        val tempPerPerson = totalAmountFloat.divide(BigDecimal(partySize), 2, RoundingMode.UP)
        _perPersonAmount.value = tempPerPerson.toString()
    }

    companion object {
        private val ONE_HUNDRED = BigDecimal(100)
    }
}