package com.vincent.forexledger.utils

import android.util.Log

object CallbackUtils {

    fun createLogThrowableCallback(): (Throwable) -> Unit {
        return {
            val message = it.message
            if (message == null) {
                it.printStackTrace()
            } else {
                Log.e("APPLICATION", message)
            }
        }
    }
}