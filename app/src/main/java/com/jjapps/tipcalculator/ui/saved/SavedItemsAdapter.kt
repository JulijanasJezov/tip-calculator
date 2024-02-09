package com.jjapps.tipcalculator.ui.saved

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.jjapps.tipcalculator.databinding.ItemBillBinding
import com.jjapps.tipcalculator.model.Bill
import com.jjapps.tipcalculator.util.formatMedium
import kotlinx.coroutines.ExperimentalCoroutinesApi

@ExperimentalCoroutinesApi
class SavedItemsAdapter(
    private val savedItemsAdapterClickListener: SavedItemsAdapterClickListener
) : ListAdapter<Bill, SavedItemsAdapter.ViewHolder>(DiffCallback()) {

    val selectedIds: MutableList<Long> = ArrayList()

    fun onLongItemClick(position: Int) = addRemoveSelectedId(position)

    fun onItemClick(position: Int) {
        if (isMultiSelectOn) addRemoveSelectedId(position) else savedItemsAdapterClickListener.onItemTap(getItem(position).copy())
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        return ViewHolder(ItemBillBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) = holder.bind(getItem(position))

    override fun getItemId(position: Int) = getItem(position).id

    fun removeSelectedItems() {
        selectedIds.clear()
        isMultiSelectOn = false
        notifyDataSetChanged()
    }

    fun removeSelectedId(position: Int) {
        val id = getItemId(position)
        if (selectedIds.contains(id)) {
            selectedIds.remove(id)
            savedItemsAdapterClickListener.notifySelected(selectedIds)
        }
    }

    private fun addRemoveSelectedId(position: Int) {
        val id = getItemId(position)
        if (selectedIds.contains(id)) selectedIds.remove(id)
        else selectedIds.add(id)

        notifyItemChanged(position)
        savedItemsAdapterClickListener.notifySelected(selectedIds)
        isMultiSelectOn = selectedIds.size >= 1
    }

    inner class ViewHolder(private val binding: ItemBillBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: Bill) {
            binding.nameTitle.text = if (!item.name.isNullOrEmpty()) item.name else "#${item.id}"
            binding.dateTitle.text = item.creationDate.formatMedium()
            binding.totalAmountValue.text = item.totalAmount
            binding.root.isChecked = selectedIds.contains(item.id)

            binding.root.setOnLongClickListener {
                onLongItemClick(adapterPosition)
                true
            }

            binding.root.setOnClickListener {
                onItemClick(adapterPosition)
            }
        }
    }

    companion object {
        private var isMultiSelectOn = false
    }
}

class DiffCallback : DiffUtil.ItemCallback<Bill>() {
    override fun areItemsTheSame(oldItem: Bill, newItem: Bill): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: Bill, newItem: Bill): Boolean {
        return oldItem == newItem
    }
}

interface SavedItemsAdapterClickListener {
    fun notifySelected(selectedIds: List<Long>)
    fun onItemTap(bill: Bill)
}
