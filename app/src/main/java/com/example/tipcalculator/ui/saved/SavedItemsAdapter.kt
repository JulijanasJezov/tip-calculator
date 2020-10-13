package com.example.tipcalculator.ui.saved

import android.content.Context
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.tipcalculator.R
import com.example.tipcalculator.model.Bill
import com.example.tipcalculator.util.formatMedium
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.item_bill.view.*
import kotlinx.coroutines.ExperimentalCoroutinesApi

@ExperimentalCoroutinesApi
class SavedItemsAdapter(
    private val context: Context,
    private val savedItemsAdapterClickListener: SavedItemsAdapterClickListener
) : ViewHolderClickListener,
    ListAdapter<Bill, SavedItemsAdapter.ViewHolder>(DiffCallback()) {

    val selectedIds: MutableList<Long> = ArrayList()

    override fun onLongItemClick(position: Int) = addSelectedId(position)

    override fun onItemClick(position: Int) {
        if (isMultiSelectOn) addSelectedId(position)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        val itemLayout = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_bill, parent, false)

        return ViewHolder(itemLayout)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) = holder.bind(getItem(position))

    override fun getItemId(position: Int) = getItem(position).id

    fun removeSelectedItems() {
        selectedIds.clear()
        isMultiSelectOn = false
        notifyDataSetChanged()
    }

    private fun addSelectedId(position: Int) {
        val id = getItemId(position)
        if (selectedIds.contains(id)) selectedIds.remove(id)
        else selectedIds.add(id)

        notifyItemChanged(position)
        savedItemsAdapterClickListener.notifySelected(selectedIds)
        isMultiSelectOn = selectedIds.size >= 1
    }

    inner class ViewHolder(override val containerView: View) :
        RecyclerView.ViewHolder(containerView), LayoutContainer {

        fun bind(item: Bill) {
            containerView.people_size.text =
                if (!item.partySize.isNullOrEmpty()) item.partySize else "1"
            containerView.tip_size.text = if (!item.tip.isNullOrEmpty()) item.tip else "0"
            containerView.date_title.text = item.creationDate.formatMedium()
            containerView.per_person_value.text = item.perPersonAmount
            containerView.tip_amount_value.text = item.tipAmount
            containerView.total_amount_value.text = item.totalAmount

            if (selectedIds.contains(item.id)) {
                containerView.item_layout.foreground =
                    ColorDrawable(ContextCompat.getColor(context, R.color.colorControlActivated))
            } else {
                containerView.item_layout.foreground =
                    ColorDrawable(ContextCompat.getColor(context, android.R.color.transparent))
            }

            containerView.setOnLongClickListener {
                onLongItemClick(adapterPosition)
                true
            }

            containerView.setOnClickListener {
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

interface ViewHolderClickListener {
    fun onLongItemClick(position: Int)
    fun onItemClick(position: Int)
}

interface SavedItemsAdapterClickListener {
    fun notifySelected(selectedIds: List<Long>)
}