package com.vincent.forexledger.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.vincent.forexledger.R
import com.vincent.forexledger.model.entry.EntryListVO
import com.vincent.forexledger.model.entry.TransactionType
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

            val transactionType = entry.transactionType

            findViewById<TextView>(R.id.textDescription).text =
                    if (entry.description.isNullOrEmpty()) context.getString(transactionType.localNameResource)
                    else entry.description

            findViewById<TextView>(R.id.textPrimaryDirection).text =
                    if (transactionType.isTransferIn) context.getString(R.string.transfer_in)
                    else context.getString(R.string.transfer_out)

            if (transactionType != TransactionType.TRANSFER_IN_FROM_INTEREST &&
                    transactionType != TransactionType.TRANSFER_IN_FROM_OTHER &&
                    transactionType != TransactionType.TRANSFER_OUT_TO_OTHER) {
                findViewById<TextView>(R.id.textRelatedDirection).text =
                        if (entry.transactionType.isTransferIn) context.getString(R.string.transfer_out)
                        else context.getString(R.string.transfer_in)
            }

            entry.relatedAmount?.let {
                findViewById<TextView>(R.id.textRelatedAmount).text = FormatUtils.formatMoney(it)
            }

            if (transactionType == TransactionType.TRANSFER_IN_FROM_TWD ||
                    transactionType == TransactionType.TRANSFER_OUT_TO_TWD) {
                findViewById<TextView>(R.id.textRelatedCurrencyType).text = context.getString(R.string.symbol_twd)
            } else {
                entry.relatedCurrencyType?.let {
                    findViewById<TextView>(R.id.textRelatedCurrencyType).text = it.name
                }
            }
        }
    }

    inner class EntryListViewHolder(view: View) : RecyclerView.ViewHolder(view)

}