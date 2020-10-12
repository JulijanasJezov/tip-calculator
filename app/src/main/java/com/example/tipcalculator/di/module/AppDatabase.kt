package com.example.tipcalculator.di.module

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.tipcalculator.dao.BillDao
import com.example.tipcalculator.model.Bill

@Database(entities = [Bill::class], version = 1)
abstract class AppDatabase: RoomDatabase() {

    abstract fun getBillDao(): BillDao

    companion object {
        const val DB_NAME = "app_db"
    }
}