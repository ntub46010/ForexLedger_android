package com.vincent.forexledger.fragment

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import com.vincent.forexledger.R
import com.vincent.forexledger.activity.EditBookActivity
import com.vincent.forexledger.adapter.BookListAdapter
import com.vincent.forexledger.model.book.BookListVO
import com.vincent.forexledger.model.exchangerate.CurrencyType
import kotlinx.android.synthetic.main.fragment_book_list.*
import kotlinx.android.synthetic.main.fragment_exchange_rate.*
import kotlinx.android.synthetic.main.fragment_exchange_rate.swipeRefreshLayout

class BookListFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_book_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        swipeRefreshLayout.setColorSchemeColors(requireContext().getColor(R.color.brown1))
        swipeRefreshLayout.setOnRefreshListener { getBooks() }

        listBook.layoutManager = GridLayoutManager(requireContext(), 2, GridLayoutManager.VERTICAL, false)

        btnCreateBook.setOnClickListener {
            startActivity(Intent(requireContext(), EditBookActivity::class.java))
        }

        getBooks()
    }

    private fun getBooks() {
        // TODO: get data from server
        displayBooks(genFakeData())
    }

    private fun displayBooks(books: List<BookListVO>) {
        val adapter = listBook.adapter

        if (adapter == null) {
            listBook.adapter = BookListAdapter(books)
        } else {
            (adapter as BookListAdapter).refreshData(books)
        }
    }

    private fun genFakeData(): List<BookListVO> {
        return listOf(
            BookListVO("1", "富邦南非幣", CurrencyType.ZAR, 51344.72, 2042, 0.021),
            BookListVO("2", "富邦瑞士法郎", CurrencyType.CHF, 645.49, -554, -0.028),
            BookListVO("3", "富邦歐元", CurrencyType.EUR, 700.51, -538, -0.023),
            BookListVO("4", "富邦英鎊", CurrencyType.GBP, 543.33, -351, -0.017),
            BookListVO("5", "Richart 澳幣", CurrencyType.AUD, 1492.13, -1165, -0.036)
        )
    }

    companion object {
        fun newInstance() = BookListFragment()
    }
}