package com.jjapps.tipcalculator.dao

import androidx.room.*
import com.jjapps.tipcalculator.model.Bill
import kotlinx.coroutines.flow.Flow

@Dao
interface BillDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun save(bill: Bill)

    @Update
    suspend fun update(bill: Bill)

    @Query("SELECT * FROM bill")
    fun loadAllBillsFlow(): Flow<List<Bill>>

    @Query("delete from bill where id in (:idList)")
    suspend fun deleteBillsFromDb(idList: List<Long>)

    @Query("delete from bill where id is :billId")
    suspend fun deleteBillFromDb(billId: Long)
}