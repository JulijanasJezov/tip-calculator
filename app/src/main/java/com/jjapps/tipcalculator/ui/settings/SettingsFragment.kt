package com.jjapps.tipcalculator.ui.settings

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import com.jjapps.tipcalculator.R
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SettingsFragment : PreferenceFragmentCompat() {

    private val viewModel: ThemesViewModel by viewModels()

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.preferences, rootKey)

        val theme: Preference? = findPreference(getString(R.string.theme))

        theme?.setOnPreferenceClickListener {
            findNavController().navigate(R.id.action_SettingsFragment_to_ThemesFragment)
            true
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val theme: Preference? = findPreference(getString(R.string.theme))
        viewModel.theme.observe(viewLifecycleOwner) {
            theme?.summary = it
        }
    }
}