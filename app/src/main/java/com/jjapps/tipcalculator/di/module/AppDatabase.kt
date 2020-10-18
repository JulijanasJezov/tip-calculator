package com.jjapps.tipcalculator.di.module

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.jjapps.tipcalculator.dao.BillDao
import com.jjapps.tipcalculator.model.Bill
import com.jjapps.tipcalculator.util.DbConverters

@Database(entities = [Bill::class], version = 1)
@TypeConverters(DbConverters::class)
abstract class AppDatabase: RoomDatabase() {

    abstract fun getBillDao(): BillDao

    companion object {
        const val DB_NAME = "app_db"
    }
}