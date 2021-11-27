package com.vincent.forexledger.fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.vincent.forexledger.Constants
import com.vincent.forexledger.R
import com.vincent.forexledger.model.book.BookDetailVO
import com.vincent.forexledger.network.ResponseEntity
import com.vincent.forexledger.service.BookService
import com.vincent.forexledger.utils.ResponseCallback
import com.vincent.forexledger.utils.ViewUtils
import io.reactivex.rxjava3.disposables.CompositeDisposable
import kotlinx.android.synthetic.main.fragment_book_detail.*
import java.math.BigDecimal

class BookDetailFragment : Fragment() {
    private val disposables = CompositeDisposable()

    private lateinit var bookId: String
    private lateinit var book: BookDetailVO

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_book_detail, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val args = BookDetailFragmentArgs.fromBundle(requireArguments())

        bookId = args.bookId
        initToolbar(args.bookName)

        btnCreateEntry.setOnClickListener {
            findNavController().navigate(BookDetailFragmentDirections.toCreateEntry(bookId, book.balance.toFloat()))
        }
        ViewUtils.setInvisible(btnCreateEntry)

        getBook()
    }

    private fun initToolbar(bookName: String?) {
        bookName?.let {
            (requireActivity() as AppCompatActivity).supportActionBar?.title = it
        }
    }

    private fun getBook() {
        val callback = ResponseCallback<BookDetailVO, String>(
                { onBookReturned(it) },
                {  Log.e(Constants.TAG_APPLICATION, it) }
        )
        BookService.loadBookDetail(bookId, callback)
    }

    private fun onBookReturned(response: ResponseEntity<BookDetailVO>) {
        if (response.getStatusCode() == 200) {
            book = response.getBody()!!
            displayCurrentValueCard(book)
            displayProfitCard(book)
            displayLastInvestCard(book)

            ViewUtils.setVisible(btnCreateEntry)
        } else {
            Toast.makeText(requireContext(), response.getStatusCode().toString(), Toast.LENGTH_SHORT).show()
        }

        response.disposables
                .filterNotNull()
                .forEach { disposables.add(it) }
    }

    private fun displayCurrentValueCard(book: BookDetailVO) {
        with (layoutCurrentValue) {
            findViewById<TextView>(R.id.textBalance).text = book.balance.toString()
            findViewById<TextView>(R.id.textCurrencyType).text = book.currencyType.name
            findViewById<TextView>(R.id.textTWDCurrentValue).text = book.twdCurrentValue.toString()
            findViewById<TextView>(R.id.textBuyingRate).text = book.bankBuyingRate.toString()
        }
    }

    private fun displayProfitCard(book: BookDetailVO) {
        with (layoutProfit) {
            findViewById<TextView>(R.id.textTWDProfit).text = book.twdProfit?.toString() ?: "-"

            if (book.twdProfitRate == null) {
                findViewById<TextView>(R.id.textProfitRate).text = "-"
            } else {
                findViewById<TextView>(R.id.textProfitRate).text = BigDecimal.valueOf(book.twdProfitRate!!)
                        .multiply(BigDecimal.valueOf(100))
                        .toDouble()
                        .toString()
            }

            findViewById<TextView>(R.id.textBreakEvenPointValue).text = book.breakEvenPoint?.toString() ?: "-"
        }
    }

    private fun displayLastInvestCard(book: BookDetailVO) {
        with (layoutLastInvest) {
            findViewById<TextView>(R.id.textForeignInvest).text = book.lastForeignInvest?.toString() ?: "-"
            findViewById<TextView>(R.id.textCurrencyType).text = book.currencyType.name
            findViewById<TextView>(R.id.textTWDInvest).text = book.lastTwdInvest?.toString() ?: "-"
            findViewById<TextView>(R.id.textSellingRate).text = book.lastSellingRate?.toString() ?: "-"
        }
    }

}