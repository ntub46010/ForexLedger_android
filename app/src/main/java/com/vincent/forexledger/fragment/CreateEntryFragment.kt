package com.vincent.forexledger.fragment

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import com.vincent.forexledger.R
import com.vincent.forexledger.model.entry.CreateEntryRequest
import com.vincent.forexledger.model.entry.TransactionType
import com.vincent.forexledger.utils.FormatUtils
import com.vincent.forexledger.utils.ViewUtils
import kotlinx.android.synthetic.main.fragment_create_entry.*
import java.util.*

class CreateEntryFragment : Fragment() {
    private var entryTypeSelectingDialog: AlertDialog? = null
    private var anotherBookSelectingDialog: AlertDialog? = null
    private var transactionDatePickerDialog: DatePickerDialog? = null

    private lateinit var bookId: String
    private var selectedEntryType: TransactionType? = null
    private var selectedTransactionDate: Date? = null
    private var selectedAnotherBookId: String? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_create_entry, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        editTransactionType.setOnClickListener {
            (entryTypeSelectingDialog ?: initEntryTypeSelectingDialog()).show()
        }

        editTransactionDate.setOnClickListener {
            (transactionDatePickerDialog ?: initTransactionDateSelectingDialog()).show()
        }

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

    private fun createEntry() {
        if (!validateData()) {
            return
        }

        val request = CreateEntryRequest(
                bookId, // TODO
                selectedEntryType!!,
                selectedTransactionDate!!,
                editForeignAmount.text.toString().toDouble(),
                editTwdAmount.text.toString().toDoubleOrNull(),
                selectedAnotherBookId // TODO
        )

        Toast.makeText(requireContext(), request.toString(), Toast.LENGTH_SHORT).show()
    }

    private fun validateData(): Boolean {
        inputTransactionType.error = null
        inputTransactionDate.error = null
        inputForeignAmount.error = null
        inputTwdAmount.error = null
        inputBookName.error = null
        var isValid = true

        if (selectedEntryType == null) {
            inputTransactionType.error = requireContext().getString(R.string.error_should_select_one)
            isValid = false
        }

        if (editTransactionDate.text.isNullOrEmpty()) {
            inputTransactionDate.error = requireContext().getString(R.string.error_should_not_be_empty)
            isValid = false
        }

        if (editForeignAmount.text.isNullOrEmpty()) {
            inputForeignAmount.error = requireContext().getString(R.string.error_should_not_be_empty)
            isValid = false
        }

        if (selectedEntryType != TransactionType.TRANSFER_IN_FROM_INTEREST
                && editTwdAmount.text.isNullOrEmpty()) {
            inputTwdAmount.error = requireContext().getString(R.string.error_should_not_be_empty)
            isValid = false
        }

        if (checkSyncToAnotherBook.isChecked && editBookName.text.isNullOrEmpty()) {
            inputBookName.error = requireContext().getString(R.string.error_should_select_one)
            isValid = false
        }

        return isValid
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

    private fun initTransactionDateSelectingDialog(): DatePickerDialog {
        val calendar = Calendar.getInstance()

        val onSelectedListener = DatePickerDialog.OnDateSetListener { datePicker, year, month, dayOfMonth ->
            calendar.set(Calendar.YEAR, year)
            calendar.set(Calendar.MONTH, month)
            calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)
            selectedTransactionDate = calendar.time
            editTransactionDate.setText(FormatUtils.formatDateStr(selectedTransactionDate!!))
        }

        return DatePickerDialog(
                requireContext(), onSelectedListener,
                calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)
        ).also { transactionDatePickerDialog = it }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.action_submit) {
            createEntry()
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.edit_page_actions, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }
}