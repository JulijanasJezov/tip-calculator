package com.example.tipcalculator.ui.saved

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.example.tipcalculator.dao.BillsRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi

class SavedViewModel @ViewModelInject constructor(
    private val billsRepository: BillsRepository
) : ViewModel() {

    @ExperimentalCoroutinesApi
    val billsLiveData = billsRepository.savedItemsFlow.asLiveData()
}