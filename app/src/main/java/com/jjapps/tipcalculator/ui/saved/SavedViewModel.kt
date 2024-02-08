package com.jjapps.tipcalculator.ui.saved

import androidx.lifecycle.*
import com.jjapps.tipcalculator.dao.BillsRepository
import com.jjapps.tipcalculator.model.Bill
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SavedViewModel @Inject constructor(
    private val billsRepository: BillsRepository
) : ViewModel() {

    @ExperimentalCoroutinesApi
    val billsLiveData = billsRepository.savedBillsFlow.map { it.reversed() }.asLiveData()

    fun deleteBills(selectedItems: List<Long>) = viewModelScope.launch {
        billsRepository.deleteBills(selectedItems)
    }

    fun deleteBill(itemId: Long) = viewModelScope.launch {
        billsRepository.deleteBill(itemId)
    }

    fun updateBill(bill: Bill) = viewModelScope.launch {
        billsRepository.updateBill(bill)
    }
}