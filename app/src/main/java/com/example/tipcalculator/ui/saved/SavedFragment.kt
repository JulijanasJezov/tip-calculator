package com.example.tipcalculator.ui.saved

import android.content.Intent
import android.os.Bundle
import android.view.ActionMode
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.DividerItemDecoration
import com.example.tipcalculator.R
import com.google.android.material.transition.MaterialFadeThrough
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_home.*
import kotlinx.android.synthetic.main.fragment_saved.*
import kotlinx.coroutines.ExperimentalCoroutinesApi

@AndroidEntryPoint
@ExperimentalCoroutinesApi
class SavedFragment() : Fragment(R.layout.fragment_saved), SavedItemsAdapterClickListener {

    private val savedViewModel: SavedViewModel by viewModels()
    private val adapter by lazy(LazyThreadSafetyMode.NONE) {
        SavedItemsAdapter(
            requireContext(),
            this
        )
    }
    private var actionMode: ActionMode? = null
    private var selectedItems: List<Int> = ArrayList()

    private val callback = object : ActionMode.Callback {

        override fun onCreateActionMode(mode: ActionMode?, menu: Menu?): Boolean {
            activity?.menuInflater?.inflate(R.menu.saved_menu, menu)
            return true
        }

        override fun onPrepareActionMode(mode: ActionMode?, menu: Menu?): Boolean {
            return false
        }

        override fun onActionItemClicked(mode: ActionMode?, item: MenuItem?): Boolean {
            return when (item?.itemId) {
                R.id.share -> {
                    shareBills()
                    actionMode?.finish()
                    true
                }
                R.id.delete -> {
                    savedViewModel.deleteBills(selectedItems)
                    actionMode?.finish()
                    true
                }
                else -> false
            }
        }

        override fun onDestroyActionMode(mode: ActionMode?) {
            actionMode = null
            adapter.removeSelectedItems()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enterTransition = MaterialFadeThrough().apply {
            duration = resources.getInteger(R.integer.config_navAnimTime).toLong()
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        saved_items_view.addItemDecoration(
            DividerItemDecoration(
                this.context,
                DividerItemDecoration.VERTICAL
            )
        )
        savedViewModel.billsLiveData.observe(viewLifecycleOwner, {
            adapter.submitList(it)
        })

        saved_items_view.adapter = adapter
    }

    override fun notifySelected(selectedIds: List<Int>) {
        selectedItems = selectedIds.toMutableList()
        if (actionMode == null) actionMode = requireActivity().startActionMode(callback)
        if (selectedIds.isNotEmpty()) actionMode?.title = ("${selectedIds.size} selected")
        else actionMode?.finish()
    }

    private fun shareBills() {
        val sendIntent = Intent()
        var textToShare = ""

        for (id in selectedItems) {
            val bill = savedViewModel.billsLiveData.value?.filter { it.id == id }?.first()

            if (!bill?.tip.isNullOrEmpty()) textToShare += "Tip: ${bill?.tip}%\n"
            if (!bill?.partySize.isNullOrEmpty()) textToShare += "People: ${bill?.partySize}\n"

            textToShare += "Tip amount: ${bill?.tipAmount}\n" +
                    "Per person: ${bill?.perPersonAmount}\n" +
                    "Total: ${bill?.totalAmount}\n\n"
        }

        sendIntent.apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, textToShare)
            type = "text/plain"
        }

        val shareIntent = Intent.createChooser(sendIntent, null)
        startActivity(shareIntent)
    }
}