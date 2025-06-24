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
package com.mycelium.wapi.wallet.eth

//import org.web3j.compat.Compat

//class EthSyncChecker(private val web3j: Web3j, var syncThreshold: Long = DEFAULT_SYNC_THRESHOLD) {
//
//    @get:Throws(Exception::class)
//    val isSynced: Boolean
//        get() {
//            val ethSyncing = web3j.ethSyncing().send()
//            return if (ethSyncing.isSyncing) {
//                false
//            } else {
//                val ethBlock = web3j.ethGetBlockByNumber(DefaultBlockParameterName.LATEST, false).send()
////                val timestamp = Compat.longValueExact(ethBlock.block.timestamp) * 1000
////                System.currentTimeMillis() - syncThreshold < timestamp
//            }
//        }

//    companion object {
//        const val DEFAULT_SYNC_THRESHOLD = 1000 * 60 * 3.toLong()
//    }
//
//}