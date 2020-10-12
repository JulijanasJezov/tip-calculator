package com.example.tipcalculator.dao

import com.example.tipcalculator.model.Bill
import javax.inject.Inject

class BillsRepository @Inject constructor(private val billDao: BillDao) {

    suspend fun saveBill(bill: Bill) = billDao.save(bill)

    internal val savedItemsFlow = billDao.loadAllBillsFlow()
}