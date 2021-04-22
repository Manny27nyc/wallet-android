package com.mycelium.wapi.wallet.btcvault

import com.mycelium.wapi.wallet.*
import com.mycelium.wapi.wallet.coins.CryptoCurrency
import com.mycelium.wapi.wallet.coins.Value


class BTCVTransactionSummary(type: CryptoCurrency?, id: ByteArray?, hash: ByteArray?,
                             transferred: Value?, timestamp: Long, height: Int, confirmations: Int,
                             isQueuedOutgoing: Boolean, inputs: List<InputViewModel>?,
                             outputs: List<OutputViewModel>?,
                             destinationAddresses: List<Address>?,
                             risk: ConfirmationRiskProfileLocal?, rawSize: Int, fee: Value?)
    : TransactionSummary(type, id, hash, transferred, timestamp, height, confirmations,
        isQueuedOutgoing, inputs, outputs, destinationAddresses, risk, rawSize, fee) {
}