package com.example.tipcalculator.dao

import com.example.tipcalculator.model.Bill
import javax.inject.Inject

class BillsRepository @Inject constructor(private val billDao: BillDao) {

    internal val savedBillsFlow = billDao.loadAllBillsFlow()

    suspend fun saveBill(bill: Bill) = billDao.save(bill)

    suspend fun deleteBills(billIds: List<Long>) = billDao.deleteBillsFromDb(billIds)

    suspend fun deleteBill(billId: Long) = billDao.deleteBillFromDb(billId)
}