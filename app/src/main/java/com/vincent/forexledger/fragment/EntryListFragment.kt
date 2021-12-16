package com.vincent.forexledger.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.vincent.forexledger.R
import com.vincent.forexledger.adapter.EntryListAdapter
import com.vincent.forexledger.model.entry.EntryListVO
import com.vincent.forexledger.model.entry.TransactionType
import com.vincent.forexledger.model.exchangerate.CurrencyType
import com.vincent.forexledger.utils.ViewUtils
import kotlinx.android.synthetic.main.content_progress_bar.*
import kotlinx.android.synthetic.main.fragment_entry_list.*
import java.util.*

class EntryListFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_entry_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        ViewUtils.setInvisible(listEntry)
        ViewUtils.setVisible(progressBar)

        val args = EntryListFragmentArgs.fromBundle(requireArguments())

        initToolbar(args.bookName)

        listEntry.layoutManager = LinearLayoutManager(requireContext())
        getEntries(args.bookId)
    }

    private fun initToolbar(bookName: String) {
        (requireActivity() as AppCompatActivity).supportActionBar?.title =
                "$bookName${requireContext().getString(R.string.transaction_records_of)}"
    }

    private fun getEntries(bookId: String) {
        displayEntries(fakeEntries())
    }

    private fun displayEntries(entries: List<EntryListVO>) {
        // TODO: maybe move to callback
        ViewUtils.setInvisible(progressBar)
        ViewUtils.setVisible(listEntry)
        
        val adapter = listEntry.adapter

        if (adapter == null) {
            listEntry.adapter = EntryListAdapter(entries)
        }
    }

    private fun fakeEntries(): List<EntryListVO> {
        return listOf(
                EntryListVO("1", Date(), TransactionType.TRANSFER_IN_FROM_TWD, 100.0, CurrencyType.USD, 2785.0, CurrencyType.THB, "匯率27.85"),
                EntryListVO("2", Date(), TransactionType.TRANSFER_OUT_TO_TWD, 95.0, CurrencyType.USD, 2945.0, CurrencyType.THB, "匯率31；淨利299TWD"),
                EntryListVO("3", Date(), TransactionType.TRANSFER_IN_FROM_FOREIGN, 100.0, CurrencyType.USD, 133.89, CurrencyType.GBP, "匯率0.7469"),
                EntryListVO("4", Date(), TransactionType.TRANSFER_OUT_TO_FOREIGN, 133.89, CurrencyType.GBP, 100.0, CurrencyType.USD, "匯率0.7469"),
                EntryListVO("5", Date(), TransactionType.TRANSFER_IN_FROM_INTEREST, 126.93, CurrencyType.ZAR, null, null, "利息收入"),
                EntryListVO("6", Date(), TransactionType.TRANSFER_OUT_TO_OTHER, 2000.0, CurrencyType.USD, null, null, "轉至奈米投"),
                EntryListVO("7", Date(), TransactionType.TRANSFER_IN_FROM_OTHER, 2050.0, CurrencyType.USD, null, null, null)
        )
    }
}