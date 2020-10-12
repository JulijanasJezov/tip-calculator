package com.example.tipcalculator.ui.saved

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.tipcalculator.R
import com.example.tipcalculator.model.Bill
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.item_bill.view.*

class SavedItemsAdapter() :
    ListAdapter<Bill, SavedItemsAdapter.ViewHolder>(
        DiffCallback()
    ) {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        val itemLayout = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_bill, parent, false)

        return ViewHolder(itemLayout)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class ViewHolder(override val containerView: View)
        : RecyclerView.ViewHolder(containerView), LayoutContainer {

        fun bind(item: Bill) {
            if (!item.partySize.isNullOrEmpty()) {
                containerView.party_label.visibility = View.VISIBLE
                containerView.people_size.text = item.partySize
            }

            if (!item.tip.isNullOrEmpty()) {
                containerView.tip_label.visibility = View.VISIBLE
                containerView.tip_size.text = item.tip
            }

            containerView.date_title.text = item.date
            containerView.per_person_value.text = item.perPersonAmount
            containerView.tip_amount_value.text = item.tipAmount
            containerView.total_amount_value.text = item.totalAmount
        }
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