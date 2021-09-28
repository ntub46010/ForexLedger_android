package com.vincent.forexledger.fragment

object FragmentProvider {
    private var exchangeRateFragment: ExchangeRateFragment? = null
    private var bookListFragment: BookListFragment? = null

    fun exchangeRate(): ExchangeRateFragment {
        if (exchangeRateFragment == null) {
            exchangeRateFragment = ExchangeRateFragment.newInstance()
        }
        return exchangeRateFragment!!
    }

    fun bookList(): BookListFragment {
        if (bookListFragment == null) {
            bookListFragment = BookListFragment.newInstance()
        }
        return bookListFragment!!
    }

    fun editBook() = EditBookFragment.newInstance()
    fun bookDetail(bookId: String) = BookDetailFragment.newInstance(bookId)
}