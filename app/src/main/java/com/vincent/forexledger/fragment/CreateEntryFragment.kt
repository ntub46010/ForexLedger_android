package com.vincent.forexledger.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import com.vincent.forexledger.R
import com.vincent.forexledger.model.entry.TransactionType

class CreateEntryFragment : Fragment() {
    private var entryTypeSelectingDialog: AlertDialog? = null

    private var selectedEntryType: TransactionType? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_create_entry, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }

    private fun initEntryTypeSelectingDialog() {
        val entryTypes = TransactionType.values();
        val entryTypeLocalNames = entryTypes.map { requireContext().getString(it.localNameResource) }.toTypedArray()
        val builder = AlertDialog.Builder(requireContext())
            .setItems(entryTypeLocalNames) { dialog, position ->
                selectedEntryType = entryTypes[position]
            }
        entryTypeSelectingDialog = builder.create();
    }
}