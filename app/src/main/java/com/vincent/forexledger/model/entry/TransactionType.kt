package com.vincent.forexledger.model.entry

import com.vincent.forexledger.R

enum class TransactionType(val localNameResource: Int,
                           val isTransferIn: Boolean) {
    TRANSFER_IN_FROM_TWD(R.string.transaction_type_transfer_in_from_twd, true),
    TRANSFER_OUT_TO_TWD(R.string.transaction_type_transfer_out_to_twd, false),
    TRANSFER_IN_FROM_FOREIGN(R.string.transaction_type_transfer_in_from_foreign, true),
    TRANSFER_OUT_TO_FOREIGN(R.string.transaction_type_transfer_out_to_foreign, false),
    TRANSFER_IN_FROM_INTEREST(R.string.transaction_type_transfer_in_from_interest, true),
    TRANSFER_IN_FROM_OTHER(R.string.transaction_type_transfer_in_from_other, true),
    TRANSFER_OUT_TO_OTHER(R.string.transaction_type_transfer_out_to_other, false)
}