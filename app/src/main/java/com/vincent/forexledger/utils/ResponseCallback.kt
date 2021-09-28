package com.vincent.forexledger.utils

import com.vincent.forexledger.network.ResponseEntity

class ResponseCallback<T, EX>(val onSuccessListener: (ResponseEntity<T>) -> Unit,
                              val onFailureListener: (EX) -> Unit)