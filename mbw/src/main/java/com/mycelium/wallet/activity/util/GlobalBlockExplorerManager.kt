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
package com.mycelium.wallet.activity.util

import com.mycelium.wallet.MbwManager

/**
 * Manages BlockExplorerManagers for different currencies
 */
class GlobalBlockExplorerManager(mbwManager: MbwManager, private val blockExplorers: Map<String, List<BlockExplorer>>,
                                 private val currentBlockExplorers: Map<String, String>) {
    /**
     * Map of BlockExplorerManagers by currency
     */
    private var bems = HashMap<String, BlockExplorerManager>()

    init {
        blockExplorers.forEach { entry ->
            val defaultBlockExplorer = if (currentBlockExplorers[entry.key].isNullOrEmpty())
                blockExplorers.getValue(entry.key)[0].identifier else currentBlockExplorers[entry.key]
            bems[entry.key] = BlockExplorerManager(mbwManager, entry.value, defaultBlockExplorer)
        }
    }

    /**
     * Returns BlockExplorerManager by currency name
     */
    fun getBEMByCurrency(coinName: String): BlockExplorerManager? = bems[coinName]

    /**
     * Returns current BlockExplorer identifiers for all currencies
     */
    fun getCurrentBlockExplorersMap(): HashMap<String, String> {
        val result = HashMap<String, String>()

        bems.forEach { entry ->
            result[entry.key] = bems[entry.key]!!.blockExplorer.identifier
        }
        return result
    }
}