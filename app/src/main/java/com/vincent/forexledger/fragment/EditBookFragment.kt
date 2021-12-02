package com.vincent.forexledger.fragment

import android.app.Dialog
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
import com.vincent.forexledger.model.bank.BankType
import com.vincent.forexledger.model.book.CreateBookRequest
import com.vincent.forexledger.model.exchangerate.CurrencyType
import com.vincent.forexledger.network.ResponseEntity
import com.vincent.forexledger.service.BookService
import com.vincent.forexledger.utils.ResponseCallback
import io.reactivex.rxjava3.disposables.CompositeDisposable
import kotlinx.android.synthetic.main.fragment_edit_book.*

class EditBookFragment : Fragment() {
    private var bankSelectingDialog: AlertDialog? = null
    private var currencySelectingDialogMap: MutableMap<BankType, AlertDialog> = mutableMapOf()
    private var waitingDialog: Dialog? = null

    private var selectedBank: BankType? = null
    private var selectedCurrencyType: CurrencyType? = null

    private val disposables = CompositeDisposable()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_edit_book, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        super.setHasOptionsMenu(true)
        initToolbar()

        editBank.setOnClickListener {
            if (bankSelectingDialog == null) {
                initBankSelectingDialog()
            }
            bankSelectingDialog!!.show()
        }

        editCurrencyType.setOnClickListener {
            if (selectedBank != null) {
                val dialog = currencySelectingDialogMap[selectedBank]
                        ?: initCurrencyDialog(selectedBank!!)
                dialog.show()
            } else {
                Toast.makeText(requireContext(), getString(R.string.select_bank_first), Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.action_submit) {
            createBook()
        }
        return super.onOptionsItemSelected(item)
    }

    private fun initToolbar() {
        val activity = requireActivity() as AppCompatActivity
        activity.supportActionBar?.title = getString(R.string.create_book)
    }

    private fun initBankSelectingDialog() {
        val bankTypes = BankType.values()
        val bankLocalNames = bankTypes.map { getString(it.localNameResource) }.toTypedArray()
        val builder = AlertDialog.Builder(requireContext()).setItems(bankLocalNames) { dialog, position ->
            selectedBank = bankTypes[position]
            editBank.setText(bankLocalNames[position])
            inputBank.error = null

            selectedCurrencyType?.let {
                if (!selectedBank!!.isSupportedCurrency(it)) {
                    inputCurrencyType.error = getString(R.string.error_unsupported_bank_currency)
                }
            }

        }
        bankSelectingDialog = builder.create()
    }

    private fun initCurrencyDialog(bank: BankType): AlertDialog {
        val currencyTypes = bank.supportedCurrency
        val currencyLocalNames = currencyTypes.map {
            "${getString(it.localNameResource)} ${it.name}"
        }.toTypedArray()

        val builder = AlertDialog.Builder(requireContext()).setItems(currencyLocalNames) { dialog, position ->
            selectedCurrencyType = currencyTypes[position]
            editCurrencyType.setText(currencyLocalNames[position])
            inputCurrencyType.error = null
        }

        return builder.create().also {
            currencySelectingDialogMap[bank] = it
        }
    }

    private fun initWaitingDialog() {
        waitingDialog = Dialog(requireContext()).also {
            it.setContentView(R.layout.dialog_waiting)
            it.setCancelable(false)
        }
    }

    private fun createBook() {
        inputBookName.error = null
        inputBank.error = null
        inputCurrencyType.error = null

        if (!validateData()) {
            return
        }

        val bookName = editBookName.text.toString()
        val request = CreateBookRequest(bookName, selectedBank!!, selectedCurrencyType!!)

        if (waitingDialog == null) {
            initWaitingDialog()
        }
        waitingDialog?.show()

        val callback = ResponseCallback<Unit, String>(
                { onBookCreated(it, bookName) },
                { Log.e(Constants.TAG_APPLICATION, it) }
        )
        BookService.createBook(request, callback)
    }

    private fun validateData(): Boolean {
        var isValid = true

        if (editBookName.text.toString().isEmpty()) {
            inputBookName.error = getString(R.string.error_should_not_be_empty)
            isValid = false
        }

        val bank = selectedBank
        val currencyType = selectedCurrencyType

        if (bank == null) {
            inputBank.error = getString(R.string.error_should_select_one)
            isValid = false
        }

        if (currencyType == null) {
            inputCurrencyType.error = getString(R.string.error_should_select_one)
            isValid = false
        }

        if (bank != null && currencyType != null && !bank.isSupportedCurrency(currencyType)) {
            inputCurrencyType.error = getString(R.string.error_unsupported_bank_currency)
            isValid = false
        }

        return isValid
    }

    private fun onBookCreated(response: ResponseEntity<Unit>, bookName: String) {
        waitingDialog?.hide()
        if (response.getStatusCode() == 201) {
            val bookId = response.getHeader(Constants.HEADER_LOCATION)!!.substringAfterLast("/")
            findNavController().navigate(EditBookFragmentDirections.toBookDetail(bookId, bookName))
        } else {
            Toast.makeText(requireContext(), response.getStatusCode().toString(), Toast.LENGTH_SHORT).show()
        }

        response.disposables
                .filterNotNull()
                .forEach { disposables.add(it) }
    }

    override fun onDestroy() {
        disposables.dispose()
        super.onDestroy()
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.edit_page_actions, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    companion object {
        fun newInstance() = EditBookFragment()
    }
}
