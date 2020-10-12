package com.example.tipcalculator.ui.saved

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.DividerItemDecoration
import com.example.tipcalculator.R
import com.google.android.material.transition.MaterialFadeThrough
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_saved.*

@AndroidEntryPoint
class SavedFragment() : Fragment(R.layout.fragment_saved) {

    private val savedViewModel: SavedViewModel by viewModels()
    private val adapter by lazy(LazyThreadSafetyMode.NONE) { SavedItemsAdapter() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enterTransition = MaterialFadeThrough().apply {
            duration = 500
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        saved_items_view.addItemDecoration(DividerItemDecoration(this.context, DividerItemDecoration.VERTICAL))
        savedViewModel.billsLiveData.observe(viewLifecycleOwner,  {
            adapter.submitList(it)
        })

        saved_items_view.adapter = adapter
    }
}