package com.example.tipcalculator.ui.saved

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.*
import com.example.tipcalculator.dao.BillsRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

class SavedViewModel @ViewModelInject constructor(
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
}