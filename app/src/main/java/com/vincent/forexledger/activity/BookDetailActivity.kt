package com.vincent.forexledger.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.vincent.forexledger.Constants
import com.vincent.forexledger.R
import kotlinx.android.synthetic.main.content_toolbar.*

class BookDetailActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_book_detail)

        val bookId = intent.getStringExtra(Constants.KEY_BOOK_ID)!!
        val bookName = intent.getStringExtra(Constants.KEY_BOOK_NAME) ?: ""

        initToolbar(bookName)
    }

    private fun initToolbar(bookName: String) {
        setSupportActionBar(toolbar)
        toolbar.setNavigationOnClickListener { finish() }
        supportActionBar?.title = bookName
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }
}