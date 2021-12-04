package com.vincent.forexledger.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.vincent.forexledger.R
import com.vincent.forexledger.model.entry.EntryListVO
import com.vincent.forexledger.utils.FormatUtils

class EntryListAdapter(private val entries: List<EntryListVO>)
    : RecyclerView.Adapter<EntryListAdapter.EntryListViewHolder>() {

    override fun getItemCount() = entries.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EntryListViewHolder {
        val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_entry, parent, false)
        return EntryListViewHolder(view)
    }

    override fun onBindViewHolder(holder: EntryListViewHolder, position: Int) {
        val entry = entries[position]

        with (holder.itemView) {
            findViewById<TextView>(R.id.textDate).text = FormatUtils.formatDateStr(entry.transactionDate)
            findViewById<TextView>(R.id.textPrimaryAmount).text = FormatUtils.formatMoney(entry.primaryAmount)
            findViewById<TextView>(R.id.textPrimaryCurrencyType).text = entry.primaryCurrencyType.name

            entry.relatedAmount?.let {
                findViewById<TextView>(R.id.textRelatedAmount).text = FormatUtils.formatMoney(it)
            }
            entry.relatedCurrencyType?.let {
                findViewById<TextView>(R.id.textRelatedCurrencyType).text = it.name
            }
            entry.description?.let {
                findViewById<TextView>(R.id.textDescription).text = it
            }
        }
    }

    class EntryListViewHolder(view: View) : RecyclerView.ViewHolder(view)

}