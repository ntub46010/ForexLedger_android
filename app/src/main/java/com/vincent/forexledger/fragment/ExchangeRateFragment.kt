package com.vincent.forexledger.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.vincent.forexledger.R

class ExchangeRateFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_exchange_rate, container, false)
    }

    companion object {
        fun newInstance() = ExchangeRateFragment()
    }
}