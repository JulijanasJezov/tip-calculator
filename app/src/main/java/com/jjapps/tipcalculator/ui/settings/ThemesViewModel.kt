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

val THEME_PREF = stringPreferencesKey("theme_pref")

@HiltViewModel
class ThemesViewModel @Inject constructor(
    private val dataStore: DataStore<Preferences>
) : ViewModel() {

    val theme: LiveData<String> = dataStore.data
        .map { preferences ->
            preferences[THEME_PREF] ?: "Default"
        }.asLiveData()

    suspend fun setThemePreference(pref: String) {
        dataStore.edit { settings ->
            settings[THEME_PREF] = pref
        }
    }
}

