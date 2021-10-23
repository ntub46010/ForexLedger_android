package com.vincent.forexledger.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.vincent.forexledger.R
import kotlinx.android.synthetic.main.fragment_book_detail.*

class BookDetailFragment : Fragment() {
    private lateinit var bookId: String

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_book_detail, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val args = BookDetailFragmentArgs.fromBundle(requireArguments())
        bookId = args.bookId

        displayCurrentValueCard()
        displayProfitCard()
        displayLastInvestCard()
    }

    private fun displayCurrentValueCard() {
        with (layoutCurrentValue) {
            findViewById<TextView>(R.id.textBalance).text = 321.77.toString()
            findViewById<TextView>(R.id.textCurrencyType).text = "GBP"
            findViewById<TextView>(R.id.textTWDCurrentValue).text = "23,877"
            findViewById<TextView>(R.id.textBuyingRate).text = 37.8428.toString()
        }
    }

    private fun displayProfitCard() {
        with (layoutProfit) {
            findViewById<TextView>(R.id.textTWDProfit).text = (-359).toString()
            findViewById<TextView>(R.id.textProfitRate).text = (-1.51).toString()
            findViewById<TextView>(R.id.textBreakEvenPointValue).text = 38.4210.toString()
        }
    }

    private fun displayLastInvestCard() {
        with (layoutLastInvest) {
            findViewById<TextView>(R.id.textForeignInvest).text = 78.44.toString()
            findViewById<TextView>(R.id.textCurrencyType).text = "GBP"
            findViewById<TextView>(R.id.textTWDInvest).text = "3,000"
            findViewById<TextView>(R.id.textSellingRate).text = 38.2457.toString()
        }
    }

}