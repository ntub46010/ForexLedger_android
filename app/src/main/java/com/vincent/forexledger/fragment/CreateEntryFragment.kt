package com.vincent.forexledger.fragment

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
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
    private var relatedBookSelectingDialog: AlertDialog? = null
    private var transactionDatePickerDialog: DatePickerDialog? = null

    private lateinit var bookId: String
    private var balance: Double = Double.MIN_VALUE

    private var selectedEntryType: TransactionType? = null
    private var selectedTransactionDate: Date? = null
    private var selectedRelatedBookId: String? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_create_entry, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        super.setHasOptionsMenu(true)
        val args = CreateEntryFragmentArgs.fromBundle(requireArguments())

        bookId = args.bookId
        balance = args.balance

        initToolbar()
        ViewUtils.setInvisible(checkSyncToRelatedBook, inputRelatedBookName)

        editTransactionType.setOnClickListener {
            (entryTypeSelectingDialog ?: initEntryTypeSelectingDialog()).show()
        }

        editTransactionDate.setOnClickListener {
            (transactionDatePickerDialog ?: initTransactionDateSelectingDialog()).show()
        }

        checkSyncToRelatedBook.setOnCheckedChangeListener { compoundButton, isChecked ->
            selectedRelatedBookId = null
            resetWidgetByRelatingBook(isChecked)
        }

        editRelatedBookName.setOnClickListener {
            (relatedBookSelectingDialog ?: initRelatedBookSelectingDialog()).show()
        }
    }

    private fun createEntry() {
        if (!validateData()) {
            return
        }
        
        val request = CreateEntryRequest(
                bookId,
                selectedEntryType!!,
                selectedTransactionDate!!,
                editForeignAmount.text.toString().toDouble(),
                editTwdAmount.text.toString().toIntOrNull(),
                selectedRelatedBookId,
                editRelatedForeignAmount.text.toString().toDoubleOrNull()
        )

        Toast.makeText(requireContext(), request.toString(), Toast.LENGTH_SHORT).show()
    }

    private fun validateData(): Boolean {
        inputTransactionType.error = null
        inputTransactionDate.error = null
        inputForeignAmount.error = null
        inputTwdAmount.error = null
        inputRelatedBookName.error = null
        inputRelatedForeignAmount.error = null
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
        } else if (selectedEntryType == TransactionType.TRANSFER_OUT_TO_TWD
                || selectedEntryType == TransactionType.TRANSFER_OUT_TO_FOREIGN
                || selectedEntryType == TransactionType.TRANSFER_OUT_TO_OTHER) {
            val foreignAmount = editForeignAmount.text.toString().toDouble()
            if (foreignAmount < balance) {
                inputForeignAmount.error = requireContext().getString(R.string.error_this_book_is_insufficient)
                isValid = false
            }
        }

        if (selectedEntryType == TransactionType.TRANSFER_IN_FROM_TWD
                || selectedEntryType == TransactionType.TRANSFER_OUT_TO_TWD) {
            if (editTwdAmount.text.isNullOrEmpty()) {
                inputTwdAmount.error = requireContext().getString(R.string.error_should_not_be_empty)
                isValid = false
            }
        }

        if (selectedEntryType?.isRelatedToAnotherBook == true
                && checkSyncToRelatedBook.isChecked) {
            if (editRelatedBookName.text.isNullOrEmpty()) {
                inputRelatedBookName.error = requireContext().getString(R.string.error_should_select_one)
                isValid = false
            }
            if (editRelatedForeignAmount.text.isNullOrEmpty()) {
                inputRelatedForeignAmount.error = requireContext().getString(R.string.error_should_not_be_empty)
                isValid = false
            }
        }

        return isValid
    }

    private fun initEntryTypeSelectingDialog(): AlertDialog {
        val entryTypes = TransactionType.values();
        val entryTypeLocalNames = entryTypes.map { requireContext().getString(it.localNameResource) }.toTypedArray()
        val builder = AlertDialog.Builder(requireContext())
            .setItems(entryTypeLocalNames) { dialog, position ->
                selectedEntryType = entryTypes[position]
                resetWidgetByTransactionType(selectedEntryType!!)
                editTransactionType.setText(entryTypeLocalNames[position])
            }

        return builder.create()
                .also { entryTypeSelectingDialog = it };
    }

    private fun initRelatedBookSelectingDialog(): AlertDialog {
        val bookNames = listOf("AAA", "BBB", "CCC").toTypedArray()
        val builder = AlertDialog.Builder(requireContext()).setItems(bookNames) { dialog, position ->
            // TODO: record selected book
            selectedRelatedBookId = bookNames[position] + " id"
            editRelatedBookName.setText(bookNames[position])
        }

        return builder.create()
            .also { relatedBookSelectingDialog = it }
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

    private fun initToolbar() {
        val actionBar = (requireActivity() as AppCompatActivity).supportActionBar
        actionBar?.title = requireContext().getString(R.string.create_transaction)
    }

    private fun resetWidgetByRelatingBook(isRelatedToAnotherBook: Boolean) {
        if (isRelatedToAnotherBook) {
            ViewUtils.setVisible(inputRelatedBookName, inputRelatedForeignAmount)
        } else {
            ViewUtils.setInvisible(inputRelatedBookName, inputRelatedForeignAmount)
            ViewUtils.clearText(editRelatedBookName, editRelatedForeignAmount)
        }
    }

    private fun resetWidgetByTransactionType(type: TransactionType) {
        when (type) {
            TransactionType.TRANSFER_IN_FROM_TWD,
            TransactionType.TRANSFER_OUT_TO_TWD -> {
                ViewUtils.setVisible(inputTwdAmount)
                ViewUtils.setInvisible(
                        checkSyncToRelatedBook, inputRelatedBookName,
                        inputRelatedForeignAmount)
                ViewUtils.clearText(editRelatedBookName, editRelatedForeignAmount)
                checkSyncToRelatedBook.isChecked = false
            }
            TransactionType.TRANSFER_IN_FROM_FOREIGN,
            TransactionType.TRANSFER_OUT_TO_FOREIGN -> {
                ViewUtils.setVisible(
                        checkSyncToRelatedBook, inputRelatedBookName,
                        inputRelatedForeignAmount)
                ViewUtils.setInvisible(inputTwdAmount)
                ViewUtils.clearText(editTwdAmount)
            }
            TransactionType.TRANSFER_IN_FROM_INTEREST,
            TransactionType.TRANSFER_IN_FROM_OTHER,
            TransactionType.TRANSFER_OUT_TO_OTHER -> {
                ViewUtils.setInvisible(
                        inputTwdAmount, checkSyncToRelatedBook,
                        inputRelatedBookName, inputRelatedForeignAmount)
                ViewUtils.clearText(editTwdAmount, editRelatedBookName, editRelatedForeignAmount)
                checkSyncToRelatedBook.isChecked = false
            }
        }
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