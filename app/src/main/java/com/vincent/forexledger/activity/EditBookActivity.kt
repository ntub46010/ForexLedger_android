package com.vincent.forexledger.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.vincent.forexledger.R
import kotlinx.android.synthetic.main.content_toolbar.*

class EditBookActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_book)

        initToolbar()
    }

    private fun initToolbar() {
        setSupportActionBar(toolbar)
        toolbar.setNavigationOnClickListener { finish() }
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }
}