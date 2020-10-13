package com.example.tipcalculator.ui.saved

import android.content.Intent
import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.example.tipcalculator.R
import com.example.tipcalculator.model.Bill
import com.example.tipcalculator.util.formatMedium
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.transition.MaterialFadeThrough
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.dialog_bill.view.*
import kotlinx.android.synthetic.main.fragment_home.*
import kotlinx.android.synthetic.main.fragment_saved.*
import kotlinx.coroutines.ExperimentalCoroutinesApi

@AndroidEntryPoint
@ExperimentalCoroutinesApi
class SavedFragment() : Fragment(R.layout.fragment_saved), SavedItemsAdapterClickListener {

    private val savedViewModel: SavedViewModel by viewModels()
    private val adapter by lazy(LazyThreadSafetyMode.NONE) { SavedItemsAdapter(requireContext(), this) }
    private var actionMode: ActionMode? = null
    private var selectedItems: List<Long> = ArrayList()
    private lateinit var billAlertDialogView : View
    private lateinit var materialAlertDialogBuilder: MaterialAlertDialogBuilder

    private val swipeHandler = object : SwipeToDeleteCallback() {
        override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
            savedViewModel.deleteBill(adapter.getItemId(viewHolder.adapterPosition))
            adapter.removeSelectedId(viewHolder.adapterPosition)
        }
    }

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
        enterTransition = MaterialFadeThrough().apply { duration = resources.getInteger(R.integer.config_navAnimTime).toLong() }
        materialAlertDialogBuilder = MaterialAlertDialogBuilder(requireContext())
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        billAlertDialogView = inflater.inflate(R.layout.dialog_bill, container, false)
        return super.onCreateView(inflater, container, savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        savedViewModel.billsLiveData.observe(viewLifecycleOwner, {
            if (it.isEmpty()) {
                saved_items_view.visibility = View.GONE;
                empty_view.visibility = View.VISIBLE;
            } else {
                saved_items_view.visibility = View.VISIBLE;
                empty_view.visibility = View.GONE;
            }
            adapter.submitList(it)
        })

        saved_items_view.adapter = adapter
        ItemTouchHelper(swipeHandler).attachToRecyclerView(saved_items_view)
    }

    override fun onStop() {
        super.onStop()
        actionMode?.finish()
    }

    override fun notifySelected(selectedIds: List<Long>) {
        selectedItems = selectedIds.toMutableList()
        if (actionMode == null) actionMode = requireActivity().startActionMode(callback)
        if (selectedIds.isNotEmpty()) actionMode?.title = ("${selectedIds.size} selected")
        else actionMode?.finish()
    }

    override fun onItemTap(bill: Bill) {
        billAlertDialogView.parent?.let {
            (billAlertDialogView.parent as ViewGroup).removeView(billAlertDialogView)
        }

        billAlertDialogView.name_input.setText(bill.name)
        billAlertDialogView.date_text.text = bill.creationDate.formatMedium()
        billAlertDialogView.tip_text.text = resources.getString(R.string.tip_placeholder, bill.tip)
        billAlertDialogView.people_text.text = resources.getString(R.string.people_placeholder, bill.partySize)
        billAlertDialogView.per_person_text.text = resources.getString(R.string.per_person_placeholder, bill.perPersonAmount)
        billAlertDialogView.tip_amount_text.text = resources.getString(R.string.tip_amount_placeholder, bill.tipAmount)
        billAlertDialogView.total_amount_text.text = resources.getString(R.string.total_placeholder, bill.totalAmount)

        materialAlertDialogBuilder.setView(billAlertDialogView)
            .setTitle(R.string.bill_details)
            .setPositiveButton(R.string.save_text) { dialog, _ ->
                bill.name = billAlertDialogView.name_input.text.toString()
                savedViewModel.updateBill(bill)
                dialog.dismiss()
            }
            .setNegativeButton(R.string.cancel_text) { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }

    private fun shareBills() {
        val sendIntent = Intent()
        var textToShare = ""

        for (id in selectedItems) {
            val bill = savedViewModel.billsLiveData.value?.first { it.id == id }

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