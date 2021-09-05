package com.vincent.forexledger.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.vincent.forexledger.R
import com.vincent.forexledger.model.exchangerate.ExchangeRateVO

class ExchangeRateListAdapter(var exchangeRates: List<ExchangeRateVO>)
    : RecyclerView.Adapter<ExchangeRateListAdapter.ExchangeRateItemViewHolder>() {

    override fun getItemCount() = exchangeRates.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ExchangeRateItemViewHolder {
        val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_exchange_rate, parent, false)
        return ExchangeRateItemViewHolder(view)
    }

    override fun onBindViewHolder(holder: ExchangeRateItemViewHolder, position: Int) {
        val itemView = holder.itemView
        val exchangeRate = exchangeRates[position]
        val currencyType = exchangeRate.currencyType

        itemView.findViewById<ImageView>(R.id.imgCurrency).setImageResource(currencyType.imageResource)
        itemView.findViewById<TextView>(R.id.textCurrencyName).setText(currencyType.localNameResource)
        itemView.findViewById<TextView>(R.id.textSellingRate).text = exchangeRate.sellingRate.toString()
        itemView.findViewById<TextView>(R.id.textBuyingRate).text = exchangeRate.buyingRate.toString()
    }

    fun refreshData(data: List<ExchangeRateVO>) {
        exchangeRates = data
        notifyDataSetChanged()
    }

    inner class ExchangeRateItemViewHolder(view: View): RecyclerView.ViewHolder(view)
}