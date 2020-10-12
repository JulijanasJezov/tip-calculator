package com.example.tipcalculator.ui.saved

import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.tipcalculator.R
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SavedFragment() : Fragment(R.layout.fragment_saved) {

    private val savedViewModel: SavedViewModel by viewModels()


}