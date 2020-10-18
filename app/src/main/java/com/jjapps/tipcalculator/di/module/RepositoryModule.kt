package com.jjapps.tipcalculator.di.module

import com.jjapps.tipcalculator.dao.BillDao
import com.jjapps.tipcalculator.dao.BillsRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityRetainedComponent
import dagger.hilt.android.scopes.ActivityRetainedScoped

@Module
@InstallIn(ActivityRetainedComponent::class)
object RepositoryModule {

    @Provides
    @ActivityRetainedScoped
    fun provideBillsRepository(billDao: BillDao): BillsRepository = BillsRepository(billDao)

}