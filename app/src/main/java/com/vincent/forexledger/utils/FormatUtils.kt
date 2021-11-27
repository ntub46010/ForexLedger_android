package com.vincent.forexledger.utils

import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.*

object FormatUtils {
    private val moneyFormat = NumberFormat.getNumberInstance(Locale.US)
    private val dateFormat = SimpleDateFormat("yyyy/MM/dd", Locale.TAIWAN)

    fun formatDecimalPlaces(number: Double, place: Int): String =
            String.format(Locale.US, "%.${place}f", number)

    fun formatMoney(amount: Int): String = moneyFormat.format(amount)

    fun formatMoney(amount: Double): String = moneyFormat.format(amount)

    fun formatDateStr(date: Date): String = dateFormat.format(date)
}