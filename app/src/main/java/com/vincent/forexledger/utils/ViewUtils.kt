package com.vincent.forexledger.utils

import android.view.View
import android.widget.EditText

object ViewUtils {
    fun setVisible(vararg views: View) =
            views.forEach { it.visibility = View.VISIBLE }

    fun setInvisible(vararg views: View) =
            views.forEach { it.visibility = View.INVISIBLE }

    fun clearText(vararg views: EditText?) =
            views.forEach { it?.text?.clear() }

    fun isEmpty(editText: EditText) = editText.text.isNullOrEmpty()

    fun toDouble(editText: EditText) = editText.text.toString().toDouble()
}