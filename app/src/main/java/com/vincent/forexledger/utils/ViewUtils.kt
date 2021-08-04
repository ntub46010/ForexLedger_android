package com.vincent.forexledger.utils

import android.view.View

object ViewUtils {
    fun setVisible(vararg views: View) =
            views.forEach { it.visibility = View.VISIBLE }

    fun setInvisible(vararg views: View) =
            views.forEach { it.visibility = View.INVISIBLE }
}