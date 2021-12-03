package com.vincent.forexledger.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.navigation.findNavController
import com.vincent.forexledger.Constants
import com.vincent.forexledger.R
import kotlinx.android.synthetic.main.content_toolbar.*

class BookDetailActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_book_detail)

        val bookName = intent.getStringExtra(Constants.KEY_BOOK_NAME)!!
        initToolbar(bookName)
    }

    override fun onStart() {
        super.onStart()
        
        val bookId = intent.getStringExtra(Constants.KEY_BOOK_ID)!!
        val bookName = intent.getStringExtra(Constants.KEY_BOOK_NAME)!!

        val bundle = Bundle().apply {
            putString(Constants.KEY_BOOK_ID, bookId)
            putString(Constants.KEY_BOOK_NAME, bookName)
        }
        findNavController(R.id.layout_navigation_container)
            .setGraph(R.navigation.browsing_book, bundle)
    }

    private fun initToolbar(bookName: String) {
        setSupportActionBar(toolbar)
        supportActionBar?.title = bookName
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        toolbar.setNavigationOnClickListener {
            if (!findNavController(R.id.layout_navigation_container).popBackStack()) {
                finish()
            }
        }
    }

    override fun onBackPressed() {
        if (!findNavController(R.id.layout_navigation_container).popBackStack()) {
            super.onBackPressed()
        }
    }
}