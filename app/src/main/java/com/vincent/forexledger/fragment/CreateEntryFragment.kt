package com.vincent.forexledger.fragment

import android.app.DatePickerDialog
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.vincent.forexledger.Constants
import com.vincent.forexledger.R
import com.vincent.forexledger.model.book.BookListVO
import com.vincent.forexledger.model.entry.CreateEntryRequest
import com.vincent.forexledger.model.entry.TransactionType
import com.vincent.forexledger.network.ResponseEntity
import com.vincent.forexledger.service.BookService
import com.vincent.forexledger.service.EntryService
import com.vincent.forexledger.utils.FormatUtils
import com.vincent.forexledger.utils.ResponseCallback
import com.vincent.forexledger.utils.ViewUtils
import io.reactivex.rxjava3.disposables.CompositeDisposable
import kotlinx.android.synthetic.main.content_progress_bar.*
import kotlinx.android.synthetic.main.fragment_create_entry.*
import java.util.*

class CreateEntryFragment : Fragment() {
    private var entryTypeSelectingDialog: AlertDialog? = null
    private var relatedBookSelectingDialog: AlertDialog? = null
    private var transactionDatePickerDialog: DatePickerDialog? = null

    private lateinit var bookId: String
    private var balance: Double = Double.MIN_VALUE
    private lateinit var myBooks: List<BookListVO>

    private var selectedEntryType: TransactionType? = null
    private var selectedTransactionDate: Date? = null
    private var selectedRelatedBook: BookListVO? = null

    private val disposables = CompositeDisposable()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_create_entry, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        super.setHasOptionsMenu(true)
        val args = CreateEntryFragmentArgs.fromBundle(requireArguments())

        bookId = args.bookId
        balance = args.balance.toDouble()

        initToolbar()
        ViewUtils.setInvisible(layoutForm)
        ViewUtils.setGone(
                inputTwdAmount, checkSyncToRelatedBook,
                inputRelatedBookName, inputRelatedForeignAmount)
        ViewUtils.setVisible(progressBar)

        editTransactionType.setOnClickListener {
            (entryTypeSelectingDialog ?: initEntryTypeSelectingDialog()).show()
        }

        editTransactionDate.setOnClickListener {
            (transactionDatePickerDialog ?: initTransactionDateSelectingDialog()).show()
        }

        checkSyncToRelatedBook.setOnCheckedChangeListener { compoundButton, isChecked ->
            selectedRelatedBook = null
            resetWidgetByRelatingBook(isChecked)
        }

        editRelatedBookName.setOnClickListener {
            (relatedBookSelectingDialog ?: initRelatedBookSelectingDialog()).show()
        }

        getBooks()
    }

    private fun createEntry() {
        if (!validateData()) {
            return
        }

        val descriptionStr = editDescription.text.let {
            if (it.isNullOrEmpty()) null
            else it.toString()
        }

        val request = CreateEntryRequest(
                bookId,
                selectedEntryType!!,
                selectedTransactionDate!!.time,
                descriptionStr,
                editForeignAmount.text.toString().toDouble(),
                editTwdAmount.text.toString().toIntOrNull(),
                selectedRelatedBook?.id,
                editRelatedForeignAmount.text.toString().toDoubleOrNull()
        )

        val callback = ResponseCallback<Unit, String>(
                { onEntryCreated(it) },
                { Log.e(Constants.TAG_APPLICATION, it) }
        )
        EntryService.createEntry(request, callback)
    }

    private fun onEntryCreated(response: ResponseEntity<Unit>) {
        if (response.getStatusCode() == 201) {
            findNavController().popBackStack()
            Toast.makeText(requireContext(), R.string.message_create_successfully, Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(requireContext(), response.getStatusCode().toString(), Toast.LENGTH_SHORT).show()
        }

        response.disposables
                .filterNotNull()
                .forEach { disposables.add(it) }
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

        if (ViewUtils.isEmpty(editTransactionDate)) {
            inputTransactionDate.error = requireContext().getString(R.string.error_should_not_be_empty)
            isValid = false
        }

        if (ViewUtils.isEmpty(editForeignAmount)) {
            inputForeignAmount.error = requireContext().getString(R.string.error_should_not_be_empty)
            isValid = false
        } else if (selectedEntryType?.isTransferOut() == true) {
            val foreignAmount = ViewUtils.toDouble(editForeignAmount)
            if (foreignAmount <= 0) {
                inputForeignAmount.error = requireContext().getString(R.string.error_should_be_positive)
                isValid = false
            }
            if (foreignAmount > balance) {
                inputForeignAmount.error = requireContext().getString(R.string.error_this_book_is_insufficient)
                isValid = false
            }
        }

        if (selectedEntryType == TransactionType.TRANSFER_IN_FROM_TWD
                || selectedEntryType == TransactionType.TRANSFER_OUT_TO_TWD
                || selectedEntryType == TransactionType.TRANSFER_IN_FROM_OTHER) {
            if (ViewUtils.isEmpty(editTwdAmount)) {
                inputTwdAmount.error = requireContext().getString(R.string.error_should_not_be_empty)
                isValid = false
            }
        } else if (selectedEntryType == TransactionType.TRANSFER_IN_FROM_FOREIGN
            || selectedEntryType == TransactionType.TRANSFER_OUT_TO_FOREIGN) {
            if (!checkSyncToRelatedBook.isChecked && ViewUtils.isEmpty(editTwdAmount)) {
                inputTwdAmount.error = requireContext().getString(R.string.error_should_not_be_empty)
                isValid = false
            }
        }

        if (checkSyncToRelatedBook.isChecked) {
            if (ViewUtils.isEmpty(editRelatedBookName)) {
                inputRelatedBookName.error = requireContext().getString(R.string.error_should_select_one)
                isValid = false
            }
            if (ViewUtils.isEmpty(editRelatedForeignAmount)) {
                inputRelatedForeignAmount.error = requireContext().getString(R.string.error_should_not_be_empty)
                isValid = false
            } else if (selectedEntryType == TransactionType.TRANSFER_IN_FROM_FOREIGN) {
                val relatedForeignAmount = ViewUtils.toDouble(editRelatedForeignAmount)
                if (relatedForeignAmount <= 0) {
                    inputForeignAmount.error = requireContext().getString(R.string.error_should_be_positive)
                    isValid = false
                }

                val selectedBookBalance = selectedRelatedBook?.balance ?: 0.0
                if (selectedBookBalance < relatedForeignAmount) {
                    inputRelatedForeignAmount.error = requireContext().getString(R.string.error_related_book_is_insufficient)
                    isValid = false
                }
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
        val bookLabels = myBooks
                .map { "${it.name} (${FormatUtils.formatMoney(it.balance)} ${it.currencyType.name})" }
                .toTypedArray()
        val builder = AlertDialog.Builder(requireContext()).setItems(bookLabels) { dialog, position ->
            selectedRelatedBook = myBooks[position]
            editRelatedBookName.setText(myBooks[position].name)
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
            ViewUtils.setGone(inputTwdAmount)
            ViewUtils.clearText(editTwdAmount)
            return
        }

        ViewUtils.setGone(inputRelatedBookName, inputRelatedForeignAmount)
        ViewUtils.clearText(editRelatedBookName, editRelatedForeignAmount)

        when (selectedEntryType) {
            TransactionType.TRANSFER_IN_FROM_OTHER,
            TransactionType.TRANSFER_IN_FROM_FOREIGN,
            TransactionType.TRANSFER_OUT_TO_FOREIGN -> {
                ViewUtils.setVisible(inputTwdAmount)
            }
            TransactionType.TRANSFER_OUT_TO_OTHER -> {
                ViewUtils.setGone(inputTwdAmount)
            }
            else -> {}
        }
    }

    private fun resetWidgetByTransactionType(type: TransactionType) {
        ViewUtils.clearText(editTwdAmount, editRelatedBookName, editRelatedForeignAmount)

        when (type) {
            TransactionType.TRANSFER_IN_FROM_TWD,
            TransactionType.TRANSFER_OUT_TO_TWD -> {
                ViewUtils.setVisible(inputTwdAmount)
                ViewUtils.setGone(
                        checkSyncToRelatedBook, inputRelatedBookName,
                        inputRelatedForeignAmount)
                ViewUtils.clearText(editRelatedBookName, editRelatedForeignAmount)
                checkSyncToRelatedBook.isChecked = false
            }
            TransactionType.TRANSFER_IN_FROM_FOREIGN,
            TransactionType.TRANSFER_OUT_TO_FOREIGN -> {
                ViewUtils.setGone(inputTwdAmount)
                ViewUtils.setVisible(
                        checkSyncToRelatedBook, inputRelatedBookName,
                        inputRelatedForeignAmount)
                checkSyncToRelatedBook.isChecked = true
                ViewUtils.clearText(editTwdAmount)
            }
            TransactionType.TRANSFER_IN_FROM_INTEREST -> {
                ViewUtils.setGone(
                        inputTwdAmount, checkSyncToRelatedBook,
                        inputRelatedBookName, inputRelatedForeignAmount)
                ViewUtils.clearText(editTwdAmount, editRelatedBookName, editRelatedForeignAmount)
                checkSyncToRelatedBook.isChecked = false
            }
            TransactionType.TRANSFER_IN_FROM_OTHER -> {
                ViewUtils.setVisible(inputTwdAmount)
                ViewUtils.setGone(
                        checkSyncToRelatedBook, inputRelatedBookName,
                        inputRelatedForeignAmount)
                ViewUtils.clearText(editTwdAmount, editRelatedBookName, editRelatedForeignAmount)
                checkSyncToRelatedBook.isChecked = false
            }
            TransactionType.TRANSFER_OUT_TO_OTHER -> {
                ViewUtils.setGone(
                        inputTwdAmount, checkSyncToRelatedBook,
                        inputRelatedBookName, inputRelatedForeignAmount)
                checkSyncToRelatedBook.isChecked = false
            }
        }
    }

    private fun getBooks() {
        val callback = ResponseCallback<List<BookListVO>, String>(
                { onBookListReturned(it) },
                { Log.e(Constants.TAG_APPLICATION, it) }
        )

        BookService.loadMyBooks(callback)
    }

    private fun onBookListReturned(response: ResponseEntity<List<BookListVO>>) {
        ViewUtils.setInvisible(progressBar)
        ViewUtils.setVisible(layoutForm)

        if (response.getStatusCode() == 200) {
            myBooks = response.getBody()
                    ?.filter { it.id != bookId }
                    ?: emptyList()
            /*
            if (myBooks.isEmpty()) {
                ViewUtils.setGone(checkSyncToRelatedBook)
            }
            */
        } else {
            Toast.makeText(requireContext(), response.getStatusCode().toString(), Toast.LENGTH_SHORT).show()
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

    override fun onDestroy() {
        disposables.dispose()
        super.onDestroy()
    }
}