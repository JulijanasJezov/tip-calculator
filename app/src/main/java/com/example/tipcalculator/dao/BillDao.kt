package com.example.tipcalculator.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.tipcalculator.model.Bill
import kotlinx.coroutines.flow.Flow

@Dao
interface BillDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun save(bill: Bill)

    @Query("SELECT * FROM bill")
    fun loadAllBillsFlow(): Flow<List<Bill>>

    @Query("DELETE FROM bill ")
    suspend fun deleteCache()
}