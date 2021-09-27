package com.vincent.forexledger.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.vincent.forexledger.R
import com.vincent.forexledger.model.bank.BankType
import com.vincent.forexledger.model.book.CreateBookRequest
import com.vincent.forexledger.model.exchangerate.CurrencyType
import com.vincent.forexledger.network.ResponseEntity
import com.vincent.forexledger.service.BookService
import com.vincent.forexledger.utils.ResponseCallback
import io.reactivex.rxjava3.disposables.CompositeDisposable
import kotlinx.android.synthetic.main.content_toolbar.toolbar
import kotlinx.android.synthetic.main.fragment_edit_book.*

class EditBookActivity : AppCompatActivity() {

    private var bankSelectingDialog: AlertDialog? = null
    private var currencySelectingDialogMap: MutableMap<BankType, AlertDialog> = mutableMapOf()

    private var selectedBank: BankType? = null
    private var selectedCurrencyType: CurrencyType? = null

    private val disposables = CompositeDisposable()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.fragment_edit_book)
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
                Toast.makeText(this, getString(R.string.select_bank_first), Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun initToolbar() {
        toolbar.title = getString(R.string.create_book)
        setSupportActionBar(toolbar)
        toolbar.setNavigationOnClickListener { finish() }
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    private fun initBankSelectingDialog() {
        val bankTypes = BankType.values()
        val bankLocalNames = bankTypes.map { getString(it.localNameResource) }.toTypedArray()
        val builder = AlertDialog.Builder(this).setItems(bankLocalNames) { dialog, position ->
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

        val builder = AlertDialog.Builder(this).setItems(currencyLocalNames) { dialog, position ->
            selectedCurrencyType = currencyTypes[position]
            editCurrencyType.setText(currencyLocalNames[position])
            inputCurrencyType.error = null
        }

        return builder.create().also {
            currencySelectingDialogMap[bank] = it
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

        // TODO: Show waiting dialog
        val callback = ResponseCallback<Unit, String>(
                { onBookCreated(it) },
                { Log.e("APPLICATION", it) }
        );
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

    private fun onBookCreated(response: ResponseEntity<Unit>) {
        // TODO: Hide waiting dialog
        if (response.getStatusCode() == 201) {
            // TODO: Go to detail page
            Toast.makeText(this, response.getHeader("Location"), Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(this, response.getStatusCode().toString(), Toast.LENGTH_SHORT).show()
        }
        response.disposables
                .filterNotNull()
                .forEach { disposables.add(it) }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.edit_page_actions, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.action_submit) {
            createBook()
        }
        return super.onOptionsItemSelected(item)
    }
}