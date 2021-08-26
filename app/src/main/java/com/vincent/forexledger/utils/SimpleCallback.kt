package com.vincent.forexledger.utils

class SimpleCallback<S, F>(val onSuccessListener: (S) -> Unit,
                           val onFailureListener: (F) -> Unit) {
}