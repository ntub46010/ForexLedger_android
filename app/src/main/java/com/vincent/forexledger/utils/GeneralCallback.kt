package com.vincent.forexledger.utils

class GeneralCallback<S, F>(val onSuccessListener: (S) -> Unit,
                            val onFailListener: (F) -> Unit) {
}