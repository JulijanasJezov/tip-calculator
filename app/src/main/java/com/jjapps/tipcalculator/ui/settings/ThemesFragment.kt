package com.jjapps.tipcalculator.ui.settings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.jjapps.tipcalculator.R
import com.jjapps.tipcalculator.databinding.FragmentThemesBinding
import com.jjapps.tipcalculator.ui.saved.SavedViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ThemesFragment : Fragment() {

    private lateinit var binding: FragmentThemesBinding
    private val viewModel: ThemesViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentThemesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.theme.observe(viewLifecycleOwner) {
            binding.radioGroup.check(when (it) {
                "Light" -> R.id.radio_light
                "Dark" -> R.id.radio_dark
                else -> R.id.radio_default
            })
        }

        binding.radioGroup.setOnCheckedChangeListener { group, checkedId ->
            viewLifecycleOwner.lifecycleScope.launch {
                viewModel.setThemePreference(
                    when (checkedId) {
                        R.id.radio_light -> "Light"
                        R.id.radio_dark -> "Dark"
                        else -> "Default"
                    }
                )
            }
        }
    }
}