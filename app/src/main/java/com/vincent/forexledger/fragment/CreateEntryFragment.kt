package com.vincent.forexledger.fragment

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import com.vincent.forexledger.R
import com.vincent.forexledger.model.entry.TransactionType
import com.vincent.forexledger.utils.FormatUtils
import com.vincent.forexledger.utils.ViewUtils
import kotlinx.android.synthetic.main.fragment_create_entry.*
import java.util.*

class CreateEntryFragment : Fragment() {
    private var entryTypeSelectingDialog: AlertDialog? = null
    private var anotherBookSelectingDialog: AlertDialog? = null

    private var selectedEntryType: TransactionType? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_create_entry, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        editTransactionType.setOnClickListener {
            (entryTypeSelectingDialog ?: initEntryTypeSelectingDialog()).show()
        }

        editTransactionDate.setOnClickListener(editTransactionDateOnClickListener())

        checkSyncToAnotherBook.setOnCheckedChangeListener { compoundButton, isChecked ->
            if (isChecked) {
                ViewUtils.setVisible(editBookName)
            } else {
                ViewUtils.setInvisible(editBookName)
            }
        }

        editBookName.setOnClickListener {
            (anotherBookSelectingDialog ?: initAnotherBookSelectingDialog()).show()
        }
    }

    private fun initEntryTypeSelectingDialog(): AlertDialog {
        val entryTypes = TransactionType.values();
        val entryTypeLocalNames = entryTypes.map { requireContext().getString(it.localNameResource) }.toTypedArray()
        val builder = AlertDialog.Builder(requireContext())
            .setItems(entryTypeLocalNames) { dialog, position ->
                selectedEntryType = entryTypes[position]
                editTransactionType.setText(entryTypeLocalNames[position])

                if (selectedEntryType!!.isRelatedToAnotherBook) {
                    ViewUtils.setVisible(checkSyncToAnotherBook)
                } else {
                    ViewUtils.setInvisible(checkSyncToAnotherBook, editBookName)
                }
            }

        return builder.create()
                .also { entryTypeSelectingDialog = it };
    }

    private fun initAnotherBookSelectingDialog(): AlertDialog {
        val bookNames = listOf("AAA", "BBB", "CCC").toTypedArray()
        val builder = AlertDialog.Builder(requireContext()).setItems(bookNames) { dialog, position ->
            // TODO: record selected book
            editBookName.setText(bookNames[position])
        }

        return builder.create()
            .also { anotherBookSelectingDialog = it }
    }

    private fun editTransactionDateOnClickListener(): View.OnClickListener {
        return View.OnClickListener {
            val calendar = Calendar.getInstance()
            DatePickerDialog(
                    requireContext(), transactionDateSelectedListener(),
                    calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)
            ).show()
        }
    }

    private fun transactionDateSelectedListener(): DatePickerDialog.OnDateSetListener {
        return DatePickerDialog.OnDateSetListener { datePicker, year, month, dayOfMonth ->
            editTransactionDate.setText(FormatUtils.formatDateStr(year, month, dayOfMonth))
        }
    }
}