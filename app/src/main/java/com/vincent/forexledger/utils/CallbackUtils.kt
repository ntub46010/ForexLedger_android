package com.vincent.forexledger.utils

import android.content.Context
import android.widget.Toast

object CallbackUtils {
    fun createShowToastCallback(context: Context): (Throwable) -> Unit {
        return {
            Toast.makeText(context, it.message, Toast.LENGTH_SHORT).show()
        }
    }
}