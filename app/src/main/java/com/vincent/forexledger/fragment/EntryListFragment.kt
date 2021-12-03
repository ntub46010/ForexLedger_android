package com.vincent.forexledger.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.vincent.forexledger.R
import com.vincent.forexledger.adapter.EntryListAdapter
import com.vincent.forexledger.model.entry.EntryListVO
import com.vincent.forexledger.utils.ViewUtils
import kotlinx.android.synthetic.main.content_progress_bar.*
import kotlinx.android.synthetic.main.fragment_entry_list.*

class EntryListFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_entry_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }

    private fun displayEntries(entries: List<EntryListVO>) {
        // TODO: maybe move to callback
        ViewUtils.setInvisible(progressBar)
        
        val adapter = listEntry.adapter

        if (adapter == null) {
            listEntry.adapter = EntryListAdapter(entries)
        }
    }
}