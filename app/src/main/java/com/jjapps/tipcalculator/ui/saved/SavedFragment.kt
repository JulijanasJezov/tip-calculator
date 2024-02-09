package com.jjapps.tipcalculator.ui.saved

import android.content.Intent
import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.jjapps.tipcalculator.R
import com.jjapps.tipcalculator.model.Bill
import com.jjapps.tipcalculator.util.formatMedium
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.transition.MaterialFadeThrough
import com.jjapps.tipcalculator.databinding.DialogBillBinding
import com.jjapps.tipcalculator.databinding.FragmentSavedBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi

@AndroidEntryPoint
@ExperimentalCoroutinesApi
class SavedFragment : Fragment(), SavedItemsAdapterClickListener {

    private lateinit var binding: FragmentSavedBinding

    private val savedViewModel: SavedViewModel by viewModels()
    private val adapter by lazy(LazyThreadSafetyMode.NONE) { SavedItemsAdapter(this) }
    private var actionMode: ActionMode? = null
    private var selectedItems: List<Long> = ArrayList()
    private lateinit var billAlertDialogView: DialogBillBinding
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
        enterTransition = MaterialFadeThrough().apply { duration = resources.getInteger(R.integer.navAnimTime).toLong() }
        materialAlertDialogBuilder = MaterialAlertDialogBuilder(requireContext())
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentSavedBinding.inflate(inflater, container, false)
        billAlertDialogView = DialogBillBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        savedViewModel.billsLiveData.observe(viewLifecycleOwner) {
            if (it.isEmpty()) {
                binding.savedItemsView.visibility = View.GONE
                binding.emptyView.visibility = View.VISIBLE
            } else {
                binding.savedItemsView.visibility = View.VISIBLE
                binding.emptyView.visibility = View.GONE
            }
            adapter.submitList(it)
        }

        binding.savedItemsView.adapter = adapter
        ItemTouchHelper(swipeHandler).attachToRecyclerView(binding.savedItemsView)
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
        billAlertDialogView.root.parent?.let {
            (billAlertDialogView.root.parent as ViewGroup).removeView(billAlertDialogView.root)
        }

        billAlertDialogView.nameInput.setText(bill.name)
        billAlertDialogView.dateText.text = bill.creationDate.formatMedium()
        billAlertDialogView.tipText.text = resources.getString(R.string.tip_placeholder, bill.tip)
        billAlertDialogView.peopleText.text = resources.getString(R.string.people_placeholder, bill.partySize)
        billAlertDialogView.perPersonText.text = resources.getString(R.string.per_person_placeholder, bill.perPersonAmount)
        billAlertDialogView.tipAmountText.text = resources.getString(R.string.tip_amount_placeholder, bill.tipAmount)
        billAlertDialogView.totalAmountText.text = resources.getString(R.string.total_placeholder, bill.totalAmount)

        materialAlertDialogBuilder.setView(billAlertDialogView.root)
            .setTitle(R.string.bill_details)
            .setPositiveButton(R.string.save_text) { dialog, _ ->
                bill.name = billAlertDialogView.nameInput.text.toString()
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