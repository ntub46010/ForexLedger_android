package com.vincent.forexledger.fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.vincent.forexledger.Constants
import com.vincent.forexledger.R
import com.vincent.forexledger.adapter.EntryListAdapter
import com.vincent.forexledger.model.entry.EntryListVO
import com.vincent.forexledger.network.ResponseEntity
import com.vincent.forexledger.service.EntryService
import com.vincent.forexledger.utils.ResponseCallback
import com.vincent.forexledger.utils.ViewUtils
import io.reactivex.rxjava3.disposables.CompositeDisposable
import kotlinx.android.synthetic.main.content_progress_bar.*
import kotlinx.android.synthetic.main.fragment_entry_list.*

class EntryListFragment : Fragment() {

    private val disposables = CompositeDisposable()

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
        val callback = ResponseCallback<List<EntryListVO>, String>(
                { onEntriesReturned(it) },
                { Log.e(Constants.TAG_APPLICATION, it) }
        )

        EntryService.loadEntries(bookId, callback)
    }

    private fun onEntriesReturned(response: ResponseEntity<List<EntryListVO>>) {
        ViewUtils.setInvisible(progressBar)
        ViewUtils.setVisible(listEntry)

        if (response.getStatusCode() == 200) {
            displayEntries(response.getBody() ?: emptyList())
        } else {
            Toast.makeText(requireContext(), response.getStatusCode().toString(), Toast.LENGTH_SHORT).show()
        }

        response.disposables
                .filterNotNull()
                .forEach { disposables.add(it) }
    }

    private fun displayEntries(entries: List<EntryListVO>) {
        ViewUtils.setInvisible(progressBar)
        ViewUtils.setVisible(listEntry)
        
        val adapter = listEntry.adapter

        if (adapter == null) {
            listEntry.adapter = EntryListAdapter(entries)
        }
    }

    override fun onDestroy() {
        disposables.dispose()
        super.onDestroy()
    }
}