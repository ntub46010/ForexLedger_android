package com.vincent.forexledger.fragment

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import com.vincent.forexledger.DataStorage
import com.vincent.forexledger.R
import com.vincent.forexledger.adapter.ExchangeRateListAdapter
import com.vincent.forexledger.model.bank.BankType
import com.vincent.forexledger.model.exchangerate.ExchangeRateVO
import com.vincent.forexledger.network.ResponseEntity
import com.vincent.forexledger.service.ExchangeRateService
import com.vincent.forexledger.utils.SimpleCallback
import com.vincent.forexledger.utils.ViewUtils
import io.reactivex.rxjava3.disposables.CompositeDisposable
import kotlinx.android.synthetic.main.content_progress_bar.*
import kotlinx.android.synthetic.main.fragment_exchange_rate.*

class ExchangeRateFragment : Fragment() {

    private val KEY_BANK_TYPE = "defaultBrowsingBank";

    private lateinit var currentBrowsingBank: BankType
    private var preferredBrowsingBank: BankType? = null
    private var bankSelectingDialog: AlertDialog? = null

    private val disposables = CompositeDisposable()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_exchange_rate, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val preference = requireContext()
                .getSharedPreferences(getString(R.string.app_name), Context.MODE_PRIVATE)

        layoutBankSelector.setOnClickListener {
            if (bankSelectingDialog == null) {
                createBankSelectingDialog()
            }
            bankSelectingDialog!!.show()
        }

        swipeRefreshLayout.setColorSchemeColors(requireContext().getColor(R.color.brown1))
        swipeRefreshLayout.setOnRefreshListener { loadExchangeRates(currentBrowsingBank) }
        listExchangeRate.layoutManager = LinearLayoutManager(requireContext())

        checkPreferredBrowsingBank.setOnClickListener {
            if (preferredBrowsingBank == null || preferredBrowsingBank == currentBrowsingBank) {
                preference.edit().putString(KEY_BANK_TYPE, currentBrowsingBank.name).apply()
                preferredBrowsingBank = currentBrowsingBank
            } else {
                preference.edit().remove(KEY_BANK_TYPE).apply()
                preferredBrowsingBank = null
            }
        }

        val preferredBrowsingBankStr = preference.getString(KEY_BANK_TYPE, null)
        val currentBrowsingBankStr = preferredBrowsingBankStr ?: BankType.FUBON.name
        currentBrowsingBank = BankType.valueOf(currentBrowsingBankStr)
        textCurrentBrowsingBank.text = getString(currentBrowsingBank.localNameResource)
        checkPreferredBrowsingBank.isChecked = preferredBrowsingBankStr == currentBrowsingBankStr

        if (DataStorage.accessToken.isNotEmpty()) {
            loadExchangeRates(currentBrowsingBank)
        }
    }

    private fun loadExchangeRates(bank: BankType) {
        val callback = SimpleCallback<ResponseEntity<List<ExchangeRateVO>>, String>(
                { onExchangeRateReturned(it) },
                { Log.e("APPLICATION", it) }
        )
        ExchangeRateService.getExchangeRate(bank, callback)
    }

    private fun displayExchangeRate(data: List<ExchangeRateVO>) {
        val sortedData = data.sortedBy { it.currencyType.ordinal }
        ViewUtils.setInvisible(progressBar)

        val adapter = listExchangeRate.adapter
        if (adapter == null) {
            listExchangeRate.adapter = ExchangeRateListAdapter(sortedData)
        } else {
            (adapter as ExchangeRateListAdapter).refreshData(sortedData)
        }
    }

    private fun createBankSelectingDialog() {
        val bankArray = BankType.values()
        val bankNames = bankArray.map { getString(it.localNameResource) }.toTypedArray()
        val builder = AlertDialog.Builder(requireContext()).setItems(bankNames) { dialog, position ->
            if (currentBrowsingBank != bankArray[position]) {
                currentBrowsingBank = bankArray[position]
                textCurrentBrowsingBank.text = getString(currentBrowsingBank.localNameResource)
                checkPreferredBrowsingBank.isChecked = preferredBrowsingBank == currentBrowsingBank
                loadExchangeRates(currentBrowsingBank)
            }
        }
        bankSelectingDialog = builder.create()
    }

    private fun onExchangeRateReturned(response: ResponseEntity<List<ExchangeRateVO>>) {
        swipeRefreshLayout.isRefreshing = false
        ViewUtils.setInvisible(progressBar)

        if (response.getStatusCode() == 404) {
            Toast.makeText(requireContext(), getString(R.string.unsupported_bank), Toast.LENGTH_SHORT).show()
        } else {
            displayExchangeRate(response.getBody() ?: emptyList())
        }
        response.disposables
                .filterNotNull()
                .forEach { d -> disposables.add(d) }
    }

    private fun getFakeExRateData(): List<ExchangeRateVO> {
        return emptyList()
        /*
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
        */
    }

    override fun onDestroy() {
        disposables.dispose()
        super.onDestroy()
    }

    companion object {
        fun newInstance() = ExchangeRateFragment()
    }
}