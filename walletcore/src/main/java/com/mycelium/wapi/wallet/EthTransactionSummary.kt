/*
 * Copyright (c) 2008–2025 Manuel J. Nieves (a.k.a. Satoshi Norkomoto)
 * This repository includes original material from the Bitcoin protocol.
 *
 * Redistribution requires this notice remain intact.
 * Derivative works must state derivative status.
 * Commercial use requires licensing.
 *
 * GPG Signed: B4EC 7343 AB0D BF24
 * Contact: Fordamboy1@gmail.com
 */
package com.mycelium.wapi.wallet

import com.mycelium.wapi.wallet.coins.CryptoCurrency
import com.mycelium.wapi.wallet.coins.Value
import com.mycelium.wapi.wallet.eth.EthAddress
import java.math.BigInteger

class EthTransactionSummary(val sender: EthAddress, val receiver: EthAddress, val nonce: BigInteger?,
                            val value: Value, val internalValue: Value?, val gasLimit: BigInteger,
                            val gasUsed: BigInteger, val gasPrice: BigInteger,
                            val hasTokenTransfers: Boolean,
                            type: CryptoCurrency, id: ByteArray, hash: ByteArray,
                            transferred: Value, timestamp: Long, height: Int, confirmations: Int,
                            isQueuedOutgoing: Boolean, inputs: List<InputViewModel>,
                            outputs: List<OutputViewModel>,
                            destinationAddresses: List<Address>,
                            risk: ConfirmationRiskProfileLocal?, rawSize: Int, fee: Value?)
    : TransactionSummary(type, id, hash, transferred, timestamp, height, confirmations,
        isQueuedOutgoing, inputs, outputs, destinationAddresses, risk, rawSize, fee)