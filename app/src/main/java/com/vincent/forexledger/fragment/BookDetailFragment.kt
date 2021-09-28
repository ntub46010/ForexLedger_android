package com.vincent.forexledger.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.vincent.forexledger.Constants
import com.vincent.forexledger.R
import kotlinx.android.synthetic.main.fragment_book_detail.*

class BookDetailFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_book_detail, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        textMsg.text = requireArguments().getString(Constants.KEY_BOOK_ID)
    }

    companion object {
        fun newInstance(bookId: String): BookDetailFragment {
            val bundle = Bundle().apply { putString(Constants.KEY_BOOK_ID, bookId) }
            return BookDetailFragment().apply { arguments = bundle }
        }
    }
}