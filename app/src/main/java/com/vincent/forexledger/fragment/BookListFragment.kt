package com.vincent.forexledger.fragment

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.GridLayoutManager
import com.vincent.forexledger.Constants
import com.vincent.forexledger.R
import com.vincent.forexledger.activity.BookDetailActivity
import com.vincent.forexledger.activity.EditBookActivity
import com.vincent.forexledger.adapter.BookListAdapter
import com.vincent.forexledger.model.book.BookListVO
import com.vincent.forexledger.network.ResponseEntity
import com.vincent.forexledger.service.BookService
import com.vincent.forexledger.utils.ResponseCallback
import com.vincent.forexledger.utils.ViewUtils
import io.reactivex.rxjava3.disposables.CompositeDisposable
import kotlinx.android.synthetic.main.content_progress_bar.*
import kotlinx.android.synthetic.main.fragment_book_list.*
import kotlinx.android.synthetic.main.fragment_exchange_rate.swipeRefreshLayout

class BookListFragment : Fragment() {

    private val disposables = CompositeDisposable()

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
            //startActivity(Intent(requireContext(), EditBookActivity::class.java))

            // FIXME: should implement in on click listener
            val bundle = Bundle().apply {
                putString(Constants.KEY_BOOK_ID, "test_book_id")
                putString(Constants.KEY_BOOK_NAME, "test_book_name")
            }
            val intent = Intent(requireContext(), BookDetailActivity::class.java)
            intent.putExtras(bundle)
            startActivity(intent)
        }

        getBooks()
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
        swipeRefreshLayout.isRefreshing = false

        if (response.getStatusCode() == 200) {
            displayBooks(response.getBody() ?: emptyList())
        } else {
            Toast.makeText(requireContext(), response.getStatusCode().toString(), Toast.LENGTH_SHORT).show()
        }

        response.disposables
                .filterNotNull()
                .forEach { disposables.add(it) }
    }

    private fun displayBooks(books: List<BookListVO>) {
        val adapter = listBook.adapter

        if (adapter == null) {
            listBook.adapter = BookListAdapter(books, onBookClickListener())
        } else {
            (adapter as BookListAdapter).refreshData(books)
        }
    }

    private fun onBookClickListener() = object : BookListAdapter.OnItemClickListener {
        override fun onItemClick(book: BookListVO) {
            Toast.makeText(requireContext(), book.id, Toast.LENGTH_SHORT).show()
        }
    }

    override fun onDestroy() {
        disposables.dispose()
        super.onDestroy()
    }

    companion object {
        fun newInstance() = BookListFragment()
    }
}