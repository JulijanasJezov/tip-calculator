package com.jjapps.tipcalculator.ui.settings

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject


@HiltViewModel
class SettingsViewModel @Inject constructor(
    dataStore: DataStore<Preferences>
) : ViewModel() {

    val theme: LiveData<String> = dataStore.data
        .map { preferences ->
            preferences[THEME_PREF] ?: "Default"
        }.asLiveData()
}

