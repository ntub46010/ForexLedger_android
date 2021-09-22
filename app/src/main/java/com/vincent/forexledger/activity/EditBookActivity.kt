package com.vincent.forexledger.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.vincent.forexledger.R
import com.vincent.forexledger.model.bank.BankType
import com.vincent.forexledger.model.book.CreateBookRequest
import com.vincent.forexledger.model.exchangerate.CurrencyType
import kotlinx.android.synthetic.main.content_toolbar.toolbar
import kotlinx.android.synthetic.main.fragment_edit_book.*

class EditBookActivity : AppCompatActivity() {

    private var bankSelectingDialog: AlertDialog? = null
    private var currencySelectingDialog: AlertDialog? = null

    private var selectedBank: BankType? = null
    private var selectedCurrencyType: CurrencyType? = null

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
            if (currencySelectingDialog == null) {
                initCurrencyDialog()
            }
            currencySelectingDialog!!.show()
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
        }
        bankSelectingDialog = builder.create()
    }

    private fun initCurrencyDialog() {
        val currencyTypes = CurrencyType.values()
        val currencyLocalNames = currencyTypes.map {
            "${getString(it.localNameResource)} ${it.name}"
        }.toTypedArray()

        val builder = AlertDialog.Builder(this).setItems(currencyLocalNames) { dialog, position ->
            selectedCurrencyType = currencyTypes[position]
            editCurrencyType.setText(currencyLocalNames[position])
        }
        currencySelectingDialog = builder.create()
    }

    private fun createBook() {
        inputBookName.error = null
        inputBank.error = null
        inputCurrencyType.error = null

        if (validateData()) {
            val bookName = editBookName.text.toString()
            val request = CreateBookRequest(bookName, selectedBank!!, selectedCurrencyType!!)
            Toast.makeText(this, "SUBMIT", Toast.LENGTH_SHORT).show()
        }
    }

    private fun validateData(): Boolean {
        var isValid = true

        if (editBookName.text.toString().isEmpty()) {
            inputBookName.error = getString(R.string.error_should_not_be_empty)
            isValid = false
        }

        if (selectedBank == null) {
            inputBank.error = getString(R.string.error_should_select_one)
            isValid = false
        }

        if (selectedCurrencyType == null) {
            inputCurrencyType.error = getString(R.string.error_should_select_one)
            isValid = false
        }

        return isValid
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