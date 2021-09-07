package com.vincent.forexledger.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import com.vincent.forexledger.R
import com.vincent.forexledger.adapter.ExchangeRateListAdapter
import com.vincent.forexledger.model.bank.BankType
import com.vincent.forexledger.model.exchangerate.CurrencyType
import com.vincent.forexledger.model.exchangerate.ExchangeRateVO
import kotlinx.android.synthetic.main.fragment_exchange_rate.*

class ExchangeRateFragment : Fragment() {

    private var bankSelectingDialog: AlertDialog? = null
    private var selectedBank: BankType = BankType.FUBON

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_exchange_rate, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        layoutBankSelector.setOnClickListener {
            if (bankSelectingDialog == null) {
                createBankSelectingDialog()
            }
            bankSelectingDialog!!.show()
        }

        swipeRefreshLayout.setColorSchemeColors(requireContext().getColor(R.color.brown1))
        swipeRefreshLayout.setOnRefreshListener { loadExchangeRates(selectedBank) }

        listExchangeRate.layoutManager = LinearLayoutManager(requireContext())

        // get preference
        loadExchangeRates(selectedBank)
    }

    private fun loadExchangeRates(bankType: BankType) {
        // TODO: load data from server
        Toast.makeText(requireContext(), "Load ${bankType.name} exchange rate.", Toast.LENGTH_SHORT).show()
        displayExchangeRate(getFakeExRateData())
    }

    private fun displayExchangeRate(data: List<ExchangeRateVO>) {
        swipeRefreshLayout.isRefreshing = false

        val adapter = listExchangeRate.adapter
        if (adapter == null) {
            listExchangeRate.adapter = ExchangeRateListAdapter(data)
        } else {
            (adapter as ExchangeRateListAdapter).refreshData(data)
        }
    }

    private fun createBankSelectingDialog() {
        val bankArray = BankType.values()
        val bankNames = bankArray.map { getString(it.localNameResource) }.toTypedArray()
        val builder = AlertDialog.Builder(requireContext()).setItems(bankNames) { dialog, position ->
            selectedBank = bankArray[position]
            textSelectedBank.text = getString(selectedBank.localNameResource)
            loadExchangeRates(selectedBank)
        }
        bankSelectingDialog = builder.create()
    }

    private fun getFakeExRateData(): List<ExchangeRateVO> {
        return listOf(
                ExchangeRateVO(CurrencyType.USD, 27.7435, 27.6435),
                ExchangeRateVO(CurrencyType.CNY, 4.3169, 4.2669),
                ExchangeRateVO(CurrencyType.JPY, 0.2535, 0.2499),
                ExchangeRateVO(CurrencyType.EUR, 33.0861, 32.6861),
                ExchangeRateVO(CurrencyType.HKD, 3.5906, 3.5366),
                ExchangeRateVO(CurrencyType.AUD, 20.7235, 20.4235),
                ExchangeRateVO(CurrencyType.ZAR, 1.9727, 1.8627),
                ExchangeRateVO(CurrencyType.CAD, 22.2227, 21.9227),
                ExchangeRateVO(CurrencyType.GBP, 38.5277, 38.0477),
                ExchangeRateVO(CurrencyType.SGD, 20.7453, 20.5053),
                ExchangeRateVO(CurrencyType.CHF, 30.4526, 20.1326),
                ExchangeRateVO(CurrencyType.NZD, 19.8794, 19.5894),
                ExchangeRateVO(CurrencyType.SEK, 3.2633, 3.2033),
                ExchangeRateVO(CurrencyType.THB, 0.8685, 0.8285)
        )
    }

    companion object {
        fun newInstance() = ExchangeRateFragment()
    }
}