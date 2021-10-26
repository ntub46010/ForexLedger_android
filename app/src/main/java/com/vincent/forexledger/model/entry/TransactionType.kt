package com.vincent.forexledger.model.entry

import com.vincent.forexledger.R

enum class TransactionType(val localNameResource: Int) {
    TRANSFER_IN_FROM_TWD(R.string.transaction_type_transfer_in_from_twd),
    TRANSFER_IN_FROM_FOREIGN(R.string.transaction_type_transfer_in_from_foreign),
    TRANSFER_IN_FROM_INTEREST(R.string.transaction_type_transfer_in_from_interest),
    TRANSFER_IN_FROM_OTHER(R.string.transaction_type_transfer_in_from_other),
    TRANSFER_OUT_TO_TWD(R.string.transaction_type_transfer_out_to_twd),
    TRANSFER_OUT_TO_FOREIGN(R.string.transaction_type_transfer_out_to_foreign),
    TRANSFER_OUT_TO_OTHER(R.string.transaction_type_transfer_out_to_other)
}