package com.example.tipcalculator.ui.home

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tipcalculator.dao.BillsRepository
import com.example.tipcalculator.model.Bill
import com.example.tipcalculator.util.ValueWrapper
import com.example.tipcalculator.util.format
import kotlinx.coroutines.launch
import java.lang.Math.round
import java.math.BigDecimal
import java.math.RoundingMode
import java.util.*

class HomeViewModel @ViewModelInject constructor(
    private val billsRepository: BillsRepository
) : ViewModel() {

    val totalAmount: LiveData<String>
        get() = _totalAmount

    private val _totalAmount = MutableLiveData<String>()

    val tipAmount: LiveData<String>
        get() = _tipAmount

    private val _tipAmount = MutableLiveData<String>()

    val perPersonAmount: LiveData<String>
        get() = _perPersonAmount

    private val _perPersonAmount = MutableLiveData<String>()

    val isBillSaved: LiveData<ValueWrapper<Unit>>
        get() = _isBillSaved

    private val _isBillSaved = MutableLiveData<ValueWrapper<Unit>>()

    private var perPersonFloat = BigDecimal.ZERO
    private var totalAmountFloat = BigDecimal.ZERO
    private var billAmount = BigDecimal.ZERO
    private var tip = 0
    private var partySize = 1

    fun saveBill(tip: String, partySize: String, tipAmount: String, totalAmount: String, perPersonAmount: String) {
        viewModelScope.launch {
            val tipToSave = if (tip.isEmpty()) "0" else tip
            val partySizeToSave = if (partySize.isEmpty()) "1" else partySize
            val bill = Bill(tipToSave, partySizeToSave, tipAmount, totalAmount, perPersonAmount)
            billsRepository.saveBill(bill)
            _isBillSaved.value = ValueWrapper(Unit)
        }
    }

    fun roundUpTotalAmount() {
        var roundedUp = totalAmountFloat.setScale(0, RoundingMode.UP)
        if (roundedUp.compareTo(totalAmountFloat) == 0) roundedUp = roundedUp.add(BigDecimal.ONE)
        calculateTotalAmount(roundedUp)
    }

    fun roundUpPerPerson() {
        var roundedUp = perPersonFloat.setScale(0, RoundingMode.UP)
        if (roundedUp.compareTo(perPersonFloat) == 0) roundedUp = roundedUp.add(BigDecimal.ONE)
        totalAmountFloat = roundedUp.times(BigDecimal(partySize)).setScale(2, RoundingMode.UP)
        perPersonFloat = roundedUp
        _totalAmount.value = totalAmountFloat.toString()
        _perPersonAmount.value = perPersonFloat.setScale(2, RoundingMode.UP).toString()
    }

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

    private fun calculateTotalAmount(roundedUpAmount: BigDecimal? = null) {
        totalAmountFloat = roundedUpAmount?.also { calculateTipAmount() } ?: billAmount.add(calculateTipAmount())
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
        perPersonFloat = totalAmountFloat.divide(BigDecimal(partySize), 2, RoundingMode.UP)
        _perPersonAmount.value = perPersonFloat.toString()
    }

    companion object {
        private val ONE_HUNDRED = BigDecimal(100)
    }
}