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
/*
 * Copyright 2013, 2014 Megion Research & Development GmbH
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.mycelium.wapi.wallet.btcvault

import com.google.common.base.Optional
import com.google.common.collect.Lists
import com.mrd.bitlib.FeeEstimatorBuilder
import com.mrd.bitlib.StandardTransactionBuilder
import com.mrd.bitlib.TransactionUtils
import com.mrd.bitlib.UnsignedTransaction
import com.mrd.bitlib.crypto.*
import com.mrd.bitlib.model.*
import com.mrd.bitlib.util.ByteReader
import com.mrd.bitlib.util.HashUtils
import com.mrd.bitlib.util.HexUtils
import com.mrd.bitlib.util.Sha256Hash
import com.mycelium.wapi.SyncStatus
import com.mycelium.wapi.SyncStatusInfo
import com.mycelium.wapi.api.Wapi
import com.mycelium.wapi.api.WapiException
import com.mycelium.wapi.api.WapiResponse
import com.mycelium.wapi.api.request.BroadcastTransactionRequest
import com.mycelium.wapi.api.request.CheckTransactionsRequest
import com.mycelium.wapi.api.request.GetTransactionsRequest
import com.mycelium.wapi.api.request.QueryUnspentOutputsRequest
import com.mycelium.wapi.api.response.CheckTransactionsResponse
import com.mycelium.wapi.api.response.GetTransactionsResponse
import com.mycelium.wapi.model.*
import com.mycelium.wapi.model.TransactionSummary
import com.mycelium.wapi.wallet.*
import com.mycelium.wapi.wallet.btc.*
import com.mycelium.wapi.wallet.btcvault.coins.BitcoinVaultMain
import com.mycelium.wapi.wallet.btcvault.coins.BitcoinVaultTest
import com.mycelium.wapi.wallet.coins.Balance
import com.mycelium.wapi.wallet.coins.CryptoCurrency
import com.mycelium.wapi.wallet.coins.Value
import com.mycelium.wapi.wallet.coins.Value.Companion.valueOf
import com.mycelium.wapi.wallet.coins.Value.Companion.zeroValue
import com.mycelium.wapi.wallet.currency.ExactBitcoinValue
import com.mycelium.wapi.wallet.exceptions.BuildTransactionException
import com.mycelium.wapi.wallet.exceptions.InsufficientFundsException
import com.mycelium.wapi.wallet.exceptions.OutputTooSmallException
import java.util.*
import java.util.concurrent.TimeUnit
import java.util.logging.Level
import java.util.logging.Logger
import javax.annotation.Nonnull
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min

abstract class AbstractBtcvAccount protected constructor(val accountBacking: BtcAccountBacking,
                                                         val network: NetworkParameters,
                                                         protected val wapi: Wapi,
                                                         val accountListener: AccountListener?)
    : SynchronizeAbleWalletAccount<BtcvAddress>(), AddressContainer, PrivateKeyProvider {
    interface EventHandler {
        fun onEvent(accountId: UUID?, event: WalletManager.Event?)
    }

    protected val logger: Logger = Logger.getLogger(AbstractBtcvAccount::class.java.simpleName)
    private var allowZeroConfSpending = true //on per default, we warn users if they use it
    protected var cachedBalance: BalanceSatoshis? = null
    private var eventHandler: EventHandler? = null
    protected var syncTotalRetrievedTxs = 0

    override fun setAllowZeroConfSpending(allowZeroConfSpending: Boolean) {
        this.allowZeroConfSpending = allowZeroConfSpending
    }

    @Throws(BuildTransactionException::class, InsufficientFundsException::class, OutputTooSmallException::class)
    override fun createTx(address: Address, amount: Value, fee: Fee, data: TransactionData?): Transaction {
        val btcFee = fee as FeePerKbFee
        return try {
            BtcvTransaction(coinType, address as BtcvAddress, amount, btcFee.feePerKb).apply {
                val receivers = listOf(BtcvReceiver(destination!!, amount.valueAsLong))
                unsignedTx = createUnsignedTransaction(receivers, feePerKb!!.valueAsLong)
            }
        } catch (ex: StandardTransactionBuilder.BtcOutputTooSmallException) {
            throw OutputTooSmallException(ex)
        } catch (ex: StandardTransactionBuilder.InsufficientBtcException) {
            throw InsufficientFundsException(ex)
        } catch (ex: StandardTransactionBuilder.UnableToBuildTransactionException) {
            throw BuildTransactionException(ex)
        }
    }

    @Throws(KeyCipher.InvalidKeyCipher::class)
    override fun signTx(request: Transaction, keyCipher: KeyCipher) {
        val btcSendRequest = request as BtcvTransaction
        btcSendRequest.setTransaction(signTransaction(btcSendRequest.unsignedTx!!, AesKeyCipher.defaultKeyCipher()))
    }

    override fun isExchangeable(): Boolean {
        return true
    }

    override fun getTx(transactionId: ByteArray): Transaction? {
        val tex = accountBacking.getTransaction(Sha256Hash.of(transactionId))
        val tx = TransactionEx.toTransaction(tex) ?: return null
        return BtcvTransaction(coinType, tx)
    }

    /**
     * set the event handler for this account
     *
     * @param eventHandler the event handler for this account
     */
    fun setEventHandler(eventHandler: EventHandler?) {
        this.eventHandler = eventHandler
    }

    protected fun postEvent(event: WalletManager.Event?) {
        eventHandler?.onEvent(this.id, event)
    }

    /**
     * Determine whether a transaction was sent from one of our own addresses.
     *
     *
     * This is a costly operation as we first have to lookup the transaction and
     * then it's funding outputs
     *
     * @param txid the ID of the transaction to investigate
     * @return true if one of the funding outputs were sent from one of our own
     * addresses
     */
    protected fun isFromMe(txid: Sha256Hash?): Boolean {
        val t = TransactionEx.toTransaction(accountBacking.getTransaction(txid))
        return t != null && isFromMe(t)
    }

    /**
     * Determine whether a transaction was sent from one of our own addresses.
     *
     *
     * This is a costly operation as we have to lookup funding outputs of the
     * transaction
     *
     * @param t the transaction to investigate
     * @return true iff one of the funding outputs were sent from one of our own
     * addresses
     */
    protected fun isFromMe(t: BitcoinTransaction): Boolean {
        for (input in t.inputs) {
            val funding = accountBacking.getParentTransactionOutput(input.outPoint)
            if (funding == null || funding.isCoinBase) {
                continue
            }
            val fundingScript = ScriptOutput.fromScriptBytes(funding.script)
            val fundingAddress = toBtcvAddress(fundingScript.getAddress(network))
            if (isMineAddress(fundingAddress)) {
                return true;
            }
        }
        return false
    }

    /**
     * Determine whether a transaction output was sent from one of our own
     * addresses
     *
     * @param output the output to investigate
     * @return true iff the putput was sent from one of our own addresses
     */
    protected fun isMine(output: TransactionOutputEx): Boolean {
        return isMine(ScriptOutput.fromScriptBytes(output.script))
    }

    /**
     * Determine whether an output script was created by one of our own addresses
     *
     * @param script the script to investigate
     * @return true iff the script was created by one of our own addresses
     */
    protected fun isMine(script: ScriptOutput): Boolean {
        return isMineAddress(toBtcvAddress(script.getAddress(network)))
    }
    //    public boolean isMineAddress(String address) {
    //        Address addr = AddressUtils.from(_network.isProdnet() ? BitcoinMain : BitcoinTest, address);
    //        return isMineAddress(addr);
    //    }
    //    protected static UUID addressToUUID(BitcoinAddress address) {
    //        return new UUID(BitUtils.uint64ToLong(address.getAllAddressBytes(), 0), BitUtils.uint64ToLong(
    //                address.getAllAddressBytes(), 8));
    //    }
    /**
     * Checks for all UTXO of the requested addresses and deletes or adds them locally if necessary
     * returns -1 if something went wrong or otherwise the number of new UTXOs added to the local
     * database
     */
    protected suspend fun synchronizeUnspentOutputs(addresses: Collection<BtcvAddress?>): Int {
        // Get the current unspent outputs as dictated by the block chain
        val unspentOutputResponse = try {
            wapi.queryUnspentOutputs(QueryUnspentOutputsRequest(Wapi.VERSION, addresses)).result
        } catch (e: WapiException) {
            logger.log(Level.SEVERE, "Server connection failed with error code: " + e.errorCode, e)
            lastSyncInfo = SyncStatusInfo(SyncStatus.ERROR)
            postEvent(WalletManager.Event.SERVER_CONNECTION_ERROR)
            return -1
        }
        val remoteUnspent = unspentOutputResponse.unspent
        // Store the current block height
        setBlockChainHeight(unspentOutputResponse.height)
        // Make a map for fast lookup
        val remoteMap = toMap(remoteUnspent)

        // Get the current unspent outputs as it is believed to be locally
        val localUnspent = accountBacking.allUnspentOutputs
        // Make a map for fast lookup
        val localMap = toMap(localUnspent)
        val transactionsToAddOrUpdate: MutableSet<Sha256Hash> = HashSet()
        val addressesToDiscover: MutableSet<BtcvAddress> = HashSet()

        // Find remotely removed unspent outputs
        for (l in localUnspent) {
            val r = remoteMap[l.outPoint]
            if (r == null) {
                // An output has gone. Maybe it was spent in another wallet, or
                // never confirmed due to missing fees, double spend, or mutated.

                // we need to fetch associated transactions, to see the outgoing tx in the history
                val scriptOutput = ScriptOutput.fromScriptBytes(l.script)
                var removeLocally = true

                // Start of the hack to prevent actual local data removal if server still didn't process just sent tx
                youngTransactions@ for (transactionEx in accountBacking.getTransactionsSince(System.currentTimeMillis() -
                        TimeUnit.SECONDS.toMillis(15))) {
                    var output: TransactionOutputEx? = null
                    var i = 0
                    while (TransactionEx.getTransactionOutput(transactionEx, i++)?.also { output = it } != null) {
                        if (output == l && !accountBacking.hasParentTransactionOutput(l.outPoint)) {
                            removeLocally = false
                            break@youngTransactions
                        }
                    }
                }
                // End of hack
                if (scriptOutput != null && removeLocally) {
                    val address = scriptOutput.getAddress(network)
                    if (addresses.contains(address)) {
                        // the output was associated with an address we were scanning for
                        // we should have got back that output from the servers
                        // this means it got probably spent via another wallet
                        // scan this address for all associated transaction to keep the history in sync
                        if (address != dummyAddress) {
                            addressesToDiscover.add(toBtcvAddress(address))
                        }
                    } else {
                        removeLocally = false
                    }
                }
                if (removeLocally) {
                    // delete the UTXO locally
                    accountBacking.deleteUnspentOutput(l.outPoint)
                }
            }
        }
        var newUtxos = 0

        // Find remotely added unspent outputs
        val unspentOutputsToAddOrUpdate: MutableList<TransactionOutputEx> = LinkedList()
        for (r in remoteUnspent) {
            var l = localMap[r.outPoint]
            if (l == null) {
                // We might have already spent transaction, but if getUnspent used connection to different server
                // it would not know that output is already spent.
                l = accountBacking.getParentTransactionOutput(r.outPoint)
            }
            if (l == null || l.height != r.height) {
                // New remote output or new height (Maybe it confirmed or we
                // might even have had a reorg). Either way we just update it
                unspentOutputsToAddOrUpdate.add(r)
                transactionsToAddOrUpdate.add(r.outPoint.txid)
                // Note: We are not adding the unspent output to the DB just yet. We
                // first want to verify the full set of funding transactions of the
                // transaction that this unspent output belongs to
                if (l == null) {
                    // this is a new UTXO. it might be necessary to sync older addresses too
                    // as this might be a change utxo from spending smth from a address we own
                    // too but did not sync here
                    newUtxos++
                }
            }
        }

        // Fetch updated or added transactions
        if (transactionsToAddOrUpdate.isNotEmpty()) {
            val response: GetTransactionsResponse
            try {
                response = getTransactionsBatched(transactionsToAddOrUpdate).result
                val txs: List<TransactionEx> = Lists.newLinkedList<TransactionEx>(response.transactions)
                handleNewExternalTransactions(txs)
            } catch (e: WapiException) {
                logger.log(Level.SEVERE, "Server connection failed with error code: " + e.errorCode, e)
                lastSyncInfo = SyncStatusInfo(SyncStatus.ERROR)
                postEvent(WalletManager.Event.SERVER_CONNECTION_ERROR)
                return -1
            }
            try {
                accountBacking.beginTransaction()
                // Finally update out list of unspent outputs with added or updated
                // outputs
                for (output in unspentOutputsToAddOrUpdate) {
                    // check if the output really belongs to one of our addresses
                    // prevent getting out local cache into a undefined state, if the server screws up
                    if (isMine(output)) {
                        accountBacking.putUnspentOutput(output)
                    } else {
                        logger.log(Level.SEVERE, "We got an UTXO that does not belong to us: $output")
                    }
                }
                accountBacking.setTransactionSuccessful()
            } finally {
                accountBacking.endTransaction()
            }
        }

        // if we removed some UTXO because of a sync, it means that there are transactions
        // we don't yet know about. Run a discover for all addresses related to the UTXOs we removed
        if (addressesToDiscover.isNotEmpty()) {
            try {
                doDiscoveryForAddresses(Lists.newArrayList(addressesToDiscover))
            } catch (ignore: WapiException) {
            }
        }
        return newUtxos
    }

    protected suspend fun getTransactionsBatched(txids: Collection<Sha256Hash>?): WapiResponse<GetTransactionsResponse> =
            wapi.getTransactions(GetTransactionsRequest(Wapi.VERSION, txids))

    @Throws(WapiException::class)
    protected abstract suspend fun doDiscoveryForAddresses(lookAhead: List<BtcvAddress>): Set<BipDerivationType>

    // HACK: skipping local handling of known transactions breaks the sync process. This should
    // be fixed somewhere else to make allKnown obsolete.
    @Throws(WapiException::class)
    protected suspend fun handleNewExternalTransactions(transactions: Collection<TransactionEx>?, allKnown: Boolean = false) {
        val all = ArrayList(transactions)
        var i = 0
        while (i < all.size) {
            val endIndex = min(all.size, i + MAX_TRANSACTIONS_TO_HANDLE_SIMULTANEOUSLY)
            val sub = all.subList(i, endIndex)
            handleNewExternalTransactionsInt(sub, allKnown)
            updateSyncProgress()
            i += MAX_TRANSACTIONS_TO_HANDLE_SIMULTANEOUSLY
        }
    }

    @Throws(WapiException::class)
    private suspend fun handleNewExternalTransactionsInt(@Nonnull transactions: Collection<TransactionEx>, allKnown: Boolean) {
        // Transform and put into two arrays with matching indexes
        val txArray: MutableList<BitcoinTransaction> = ArrayList(transactions.size)
        for (tex in transactions) {
            try {
                txArray.add(BitcoinTransaction.fromByteReader(ByteReader(tex.binary)))
            } catch (e: BitcoinTransaction.TransactionParsingException) {
                // We hit a transaction that we cannot parse. Log but otherwise ignore it
                logger.log(Level.SEVERE, "Received transaction that we cannot parse: " + tex.txid.toString())
            }
        }

        // Grab and handle parent transactions
        fetchStoreAndValidateParentOutputs(txArray, false)

        // Store transaction locally
        if (!allKnown) {
            accountBacking.putTransactions(transactions)
            syncTotalRetrievedTxs += transactions.size
        }
        for (t in txArray) {
            onNewTransaction(t)
        }
    }

    @Throws(WapiException::class)
    suspend fun fetchStoreAndValidateParentOutputs(transactions: List<BitcoinTransaction>, doRemoteFetching: Boolean) {
        val parentTransactions = hashMapOf<Sha256Hash, TransactionEx>()
        val parentOutputs = hashMapOf<OutPoint, TransactionOutputEx>()

        // Find list of parent outputs to fetch
        val toFetch = hashSetOf<Sha256Hash>()
        for (t in transactions) {
            for (`in` in t.inputs) {
                if (`in`.outPoint.txid == OutPoint.COINBASE_OUTPOINT.txid) {
                    // Coinbase input, so no parent
                    continue
                }
                val parentOutput = accountBacking.getParentTransactionOutput(`in`.outPoint)
                if (parentOutput != null) {
                    // We already have the parent output, no need to fetch the entire
                    // parent transaction
                    parentOutputs[parentOutput.outPoint] = parentOutput
                    continue
                }
                var parentTransaction = accountBacking.getTransaction(`in`.outPoint.txid)
                if (parentTransaction == null) {
                    // Check current transactions list for parents
                    for (transaction in transactions) {
                        if (transaction.id == `in`.outPoint.txid) {
                            parentTransaction = TransactionEx.fromUnconfirmedTransaction(transaction)
                            break
                        }
                    }
                }
                if (parentTransaction != null) {
                    // We had the parent transaction in our own transactions, no need to
                    // fetch it remotely
                    parentTransactions[parentTransaction.txid] = parentTransaction
                } else if (doRemoteFetching) {
                    // Need to fetch it
                    toFetch.add(`in`.outPoint.txid)
                }
            }
        }

        // Fetch missing parent transactions
        if (toFetch.isNotEmpty()) {
            val result = getTransactionsBatched(toFetch).result
            for (tx in result.transactions) {
                // Verify transaction hash. This is important as we don't want to
                // have a transaction output associated with an outpoint that
                // doesn't match.
                // This is the end users protection against a rogue server that lies
                // about the value of an output and makes you pay a large fee.
                val hash = HashUtils.doubleSha256(tx.binary).reverse()
                if (hash == tx.hash) {
                    parentTransactions[tx.txid] = tx
                } else {
                    //TODO: Document what's happening here.
                    //Question: Crash and burn? Really? How about user feedback? Here, wapi returned a transaction that doesn't hash to the txid it is supposed to txhash to, right?
                    throw RuntimeException("Failed to validate transaction hash from server. Expected: " + tx.txid
                            + " Calculated: " + hash)
                }
            }
        }

        // We should now have all parent transactions or parent outputs. There is
        // a slight probability that one of them was not found due to double
        // spends and/or malleability and network latency etc.

        // Now figure out which parent outputs we need to persist
        val toPersist: MutableList<TransactionOutputEx?> = LinkedList()
        for (t in transactions) {
            for (`in` in t.inputs) {
                if (`in`.outPoint.txid == OutPoint.COINBASE_OUTPOINT.txid) {
                    // coinbase input, so no parent
                    continue
                }
                var parentOutput = parentOutputs[`in`.outPoint]
                if (parentOutput != null) {
                    // We had it all along
                    continue
                }
                val parentTex = parentTransactions[`in`.outPoint.txid]
                if (parentTex != null) {
                    // Parent output not found, maybe we already have it
                    parentOutput = TransactionEx.getTransactionOutput(parentTex, `in`.outPoint.index)
                    toPersist.add(parentOutput)
                }
            }
        }

        // Persist
        if (toPersist.size <= MAX_TRANSACTIONS_TO_HANDLE_SIMULTANEOUSLY) {
            accountBacking.putParentTransactionOuputs(toPersist)
        } else {
            // We have quite a list of transactions to handle, do it in batches
            val all = ArrayList(toPersist)
            var i = 0
            while (i < all.size) {
                val endIndex = Math.min(all.size, i + MAX_TRANSACTIONS_TO_HANDLE_SIMULTANEOUSLY)
                val sub: List<TransactionOutputEx?> = all.subList(i, endIndex)
                accountBacking.putParentTransactionOuputs(sub)
                i += MAX_TRANSACTIONS_TO_HANDLE_SIMULTANEOUSLY
            }
        }
    }

    protected fun calculateLocalBalance(): BalanceSatoshis {
        val unspentOutputs = accountBacking.allUnspentOutputs
        var confirmed: Long = 0
        var pendingChange: Long = 0
        var pendingSending: Long = 0
        var pendingReceiving: Long = 0

        //
        // Determine the value we are receiving and create a set of outpoints for fast lookup
        //
        val unspentOutPoints = hashSetOf<OutPoint>()
        for (output in unspentOutputs) {
            if (output.height == -1) {
                if (isFromMe(output.outPoint.txid)) {
                    pendingChange += output.value
                } else {
                    pendingReceiving += output.value
                }
            } else {
                confirmed += output.value
            }
            unspentOutPoints.add(output.outPoint)
        }

        //
        // Determine the value we are sending
        //

        // Get the current set of unconfirmed transactions
        val unconfirmed: MutableList<BitcoinTransaction> = ArrayList()
        for (tex in accountBacking.unconfirmedTransactions) {
            try {
                val t = BitcoinTransaction.fromByteReader(ByteReader(tex.binary))
                unconfirmed.add(t)
            } catch (e: BitcoinTransaction.TransactionParsingException) {
                // never happens, we have parsed it before
            }
        }
        for (t in unconfirmed) {
            // For each input figure out if WE are sending it by fetching the
            // parent transaction and looking at the address
            var weSend = false
            for (input in t.inputs) {
                // Find the parent transaction
                if (input.outPoint.txid == Sha256Hash.ZERO_HASH) {
                    continue
                }
                val parent = accountBacking.getParentTransactionOutput(input.outPoint)
                if (parent == null) {
                    logger.log(Level.SEVERE, "Unable to find parent transaction output: " + input.outPoint)
                    continue
                }
                val parentOutput = transform(parent)
                val fundingAddress = parentOutput.script.getAddress(network)
                if (isMineAddress(toBtcvAddress(fundingAddress))) {
                    // One of our addresses are sending coins
                    pendingSending += parentOutput.value
                    weSend = true
                }
            }

            // Now look at the outputs and if it contains change for us, then subtract that from the sending amount
            // if it is already spent in another transaction
            for (i in t.outputs.indices) {
                val output = t.outputs[i]
                val destination = output.script.getAddress(network)
                if (weSend && isMineAddress(toBtcvAddress(destination))) {
                    // The funds are sent from us to us
                    val outPoint = OutPoint(t.id, i)
                    if (!unspentOutPoints.contains(outPoint)) {
                        // This output has been spent, subtract it from the amount sent
                        pendingSending -= output.value
                    }
                }
            }
        }
        return BalanceSatoshis(confirmed, pendingReceiving, pendingSending, pendingChange, System.currentTimeMillis(),
                               getBlockChainHeight(), true, allowZeroConfSpending)
    }

    abstract fun toBtcvAddress(bitcoinAddress: BitcoinAddress): BtcvAddress

    private fun transform(parent: TransactionOutputEx): TransactionOutput {
        val script = ScriptOutput.fromScriptBytes(parent.script)
        return TransactionOutput(parent.value, script)
    }

    /**
     * Broadcast outgoing transactions.
     *
     *
     * This method should only be called from the wallet manager
     *
     * @return false if synchronization failed due to failed blockchain
     * connection
     */
    @Synchronized
    override fun broadcastOutgoingTransactions(): Boolean {
        checkNotArchived()
        val broadcastedIds: MutableList<Sha256Hash> = LinkedList()
        val transactions = accountBacking.outgoingTransactions
        var malformedTransactionsCount = 0
        for (rawTransaction in transactions.values) {
            var transaction: BitcoinTransaction
            transaction = try {
                BitcoinTransaction.fromBytes(rawTransaction)
            } catch (e: BitcoinTransaction.TransactionParsingException) {
                logger.log(Level.SEVERE, "Unable to parse transaction from bytes: " + HexUtils.toHex(rawTransaction), e)
                return false
            }
            val result = broadcastTransaction(transaction)
            if (result.resultType == BroadcastResultType.SUCCESS) {
                broadcastedIds.add(transaction.id)
                accountBacking.removeOutgoingTransaction(transaction.id)
            } else if (result.resultType == BroadcastResultType.REJECT_MALFORMED) {
                malformedTransactionsCount++
            }
        }
        if (malformedTransactionsCount > 0) {
            postEvent(WalletManager.Event.MALFORMED_OUTGOING_TRANSACTIONS_FOUND)
        }
        if (broadcastedIds.isNotEmpty()) {
            onTransactionsBroadcasted(broadcastedIds)
        }
        return true
    }

    fun getTransaction(txid: Sha256Hash): TransactionEx = accountBacking.getTransaction(txid)

    @Synchronized
    fun broadcastTransaction(transaction: BitcoinTransaction): BroadcastResult {
        checkNotArchived()
        return try {
            val response = wapi.broadcastTransaction(
                    BroadcastTransactionRequest(Wapi.VERSION, transaction.toBytes()))
            val errorCode = response.errorCode
            if (errorCode == Wapi.ERROR_CODE_SUCCESS) {
                if (response.result.success) {
                    markTransactionAsSpent(TransactionEx.fromUnconfirmedTransaction(transaction))
                    postEvent(WalletManager.Event.BROADCASTED_TRANSACTION_ACCEPTED)
                    BroadcastResult(BroadcastResultType.SUCCESS)
                } else {
                    // This transaction was rejected must be double spend or
                    // malleability, delete it locally.
                    logger.log(Level.SEVERE, "Failed to broadcast transaction due to a double spend or malleability issue")
                    postEvent(WalletManager.Event.BROADCASTED_TRANSACTION_DENIED)
                    BroadcastResult(BroadcastResultType.REJECT_DUPLICATE)
                }
            } else if (errorCode == Wapi.ERROR_CODE_NO_SERVER_CONNECTION) {
                lastSyncInfo = SyncStatusInfo(SyncStatus.ERROR)
                postEvent(WalletManager.Event.SERVER_CONNECTION_ERROR)
                logger.log(Level.SEVERE, "Server connection failed with ERROR_CODE_NO_SERVER_CONNECTION")
                BroadcastResult(BroadcastResultType.NO_SERVER_CONNECTION)
            } else if (errorCode == Wapi.ElectrumxError.REJECT_MALFORMED.errorCode) {
                BroadcastResult(response.errorMessage, BroadcastResultType.REJECT_MALFORMED)
            } else if (errorCode == Wapi.ElectrumxError.REJECT_DUPLICATE.errorCode) {
                BroadcastResult(response.errorMessage, BroadcastResultType.REJECT_DUPLICATE)
            } else if (errorCode == Wapi.ElectrumxError.REJECT_NONSTANDARD.errorCode) {
                BroadcastResult(response.errorMessage, BroadcastResultType.REJECT_NONSTANDARD)
            } else if (errorCode == Wapi.ElectrumxError.REJECT_INSUFFICIENT_FEE.errorCode) {
                BroadcastResult(response.errorMessage, BroadcastResultType.REJECT_INSUFFICIENT_FEE)
            } else {
                postEvent(WalletManager.Event.BROADCASTED_TRANSACTION_DENIED)
                logger.log(Level.SEVERE, "Server connection failed with error: $errorCode")
                BroadcastResult(BroadcastResultType.REJECTED)
            }
        } catch (e: WapiException) {
            lastSyncInfo = SyncStatusInfo(SyncStatus.ERROR)
            postEvent(WalletManager.Event.SERVER_CONNECTION_ERROR)
            logger.log(Level.SEVERE, "Server connection failed with error code: " + e.errorCode, e)
            BroadcastResult(BroadcastResultType.NO_SERVER_CONNECTION)
        }
    }

    protected fun checkNotArchived() {
        val usingArchivedAccount = "Using archived account"
        if (isArchived) {
            logger.log(Level.SEVERE, usingArchivedAccount)
            throw RuntimeException(usingArchivedAccount)
        }
    }

    protected abstract fun onNewTransaction(t: BitcoinTransaction)
    protected abstract fun onTransactionsBroadcasted(txids: List<Sha256Hash>)

    fun getTransactionHistory(offset: Int, limit: Int): List<TransactionSummary> {
        // Note that this method is not synchronized, and we might fetch the transaction history while synchronizing
        // accounts. That should be ok as we write to the DB in a sane order.
        val history: MutableList<TransactionSummary> = ArrayList()
        checkNotArchived()
        val list = accountBacking.getTransactionHistory(offset, limit)
        for (tex in list) {
            val item = transform(tex, getBlockChainHeight())
            if (item != null) {
                history.add(item)
            }
        }
        return history
    }

    protected abstract fun setBlockChainHeight(blockHeight: Int)

    @Throws(KeyCipher.InvalidKeyCipher::class)
    fun signTransaction(unsigned: UnsignedTransaction, cipher: KeyCipher): BitcoinTransaction {
        checkNotArchived()
        if (!isValidEncryptionKey(cipher)) {
            throw KeyCipher.InvalidKeyCipher()
        }
        // Make all signatures, this is the CPU intensive part
        val signatures = StandardTransactionBuilder.generateSignatures(
                unsigned.signingRequests.filterNotNull().toTypedArray(),
                PrivateKeyRing(cipher)
        )

        // Apply signatures and finalize transaction
        return StandardTransactionBuilder.finalizeTransaction(unsigned, signatures)
    }

    @Synchronized
    private fun queueTransaction(transaction: TransactionEx) {
        // Store transaction in outgoing buffer, so we can broadcast it
        // later
        val rawTransaction = transaction.binary
        accountBacking.putOutgoingTransaction(transaction.txid, rawTransaction)
        markTransactionAsSpent(transaction)
    }

    @Synchronized
    fun deleteTransaction(transactionId: Sha256Hash): Boolean {
        val tex = accountBacking.getTransaction(transactionId) ?: return false
        val tx = TransactionEx.toTransaction(tex)
        accountBacking.beginTransaction()
        try {
            // See if any of the outputs are stored locally and remove them
            for (i in tx.outputs.indices) {
                val outPoint = OutPoint(tx.id, i)
                val utxo = accountBacking.getUnspentOutput(outPoint)
                if (utxo != null) {
                    accountBacking.deleteUnspentOutput(outPoint)
                }
            }
            // remove it from the accountBacking
            accountBacking.deleteTransaction(transactionId)
            accountBacking.setTransactionSuccessful()
        } finally {
            accountBacking.endTransaction()
        }
        updateLocalBalance() //will still need a new sync besides re-calculating
        return true
    }

    override fun removeAllQueuedTransactions() {
        val outgoingTransactions = accountBacking.outgoingTransactions
        for (key in outgoingTransactions.keys) {
            cancelQueuedTransaction(key)
        }
    }

    @Synchronized
    fun cancelQueuedTransaction(transaction: Sha256Hash?): Boolean {
        val outgoingTransactions = accountBacking.outgoingTransactions
        if (!outgoingTransactions.containsKey(transaction)) {
            return false
        }
        val tx: BitcoinTransaction
        tx = try {
            BitcoinTransaction.fromBytes(outgoingTransactions[transaction])
        } catch (e: BitcoinTransaction.TransactionParsingException) {
            return false
        }
        accountBacking.beginTransaction()
        try {

            // See if any of the outputs are stored locally and remove them
            for (i in tx.outputs.indices) {
                val outPoint = OutPoint(tx.id, i)
                val utxo = accountBacking.getUnspentOutput(outPoint)
                if (utxo != null) {
                    accountBacking.deleteUnspentOutput(outPoint)
                }
            }

            // Remove a queued transaction from our outgoing buffer
            accountBacking.removeOutgoingTransaction(transaction)

            // remove it from the accountBacking
            accountBacking.deleteTransaction(transaction)
            accountBacking.setTransactionSuccessful()
        } finally {
            accountBacking.endTransaction()
        }

        // calc the new balance to remove the outgoing amount
        // the total balance will still be wrong, as we already deleted some UTXOs to build the queued transaction
        // these will get restored after the next sync
        updateLocalBalance()

        //markTransactionAsSpent(transaction);
        return true
    }

    override fun queueTransaction(transaction: Transaction) {
        val txBytes = transaction.txBytes()
        val now = (System.currentTimeMillis() / 1000).toInt()
        try {
            val btcTx = BitcoinTransaction.fromBytes(txBytes)
            val tex = TransactionEx(btcTx.id, btcTx.hash, -1, now, txBytes)
            queueTransaction(tex)
        } catch (e: BitcoinTransaction.TransactionParsingException) {
            logger.log(Level.INFO, String.format("Unable to parse transaction %s: %s", HexUtils.toHex(transaction.id), e.message))
        }
    }

    private fun markTransactionAsSpent(transaction: TransactionEx) {
        accountBacking.beginTransaction()
        val parsedTransaction: BitcoinTransaction
        parsedTransaction = try {
            BitcoinTransaction.fromBytes(transaction.binary)
        } catch (e: BitcoinTransaction.TransactionParsingException) {
            logger.log(Level.INFO, String.format("Unable to parse transaction %s: %s", transaction.txid, e.message))
            return
        }
        try {
            // Remove inputs from unspent, marking them as spent
            for (input in parsedTransaction.inputs) {
                val parentOutput = accountBacking.getUnspentOutput(input.outPoint)
                if (parentOutput != null) {
                    accountBacking.deleteUnspentOutput(input.outPoint)
                    accountBacking.putParentTransactionOutput(parentOutput)
                }
            }

            // See if any of the outputs are for ourselves and store them as
            // unspent
            for (i in parsedTransaction.outputs.indices) {
                val output = parsedTransaction.outputs[i]
                if (isMine(output.script)) {
                    accountBacking.putUnspentOutput(TransactionOutputEx(OutPoint(parsedTransaction.id, i), -1,
                            output.value, output.script.scriptBytes, false))
                }
            }

            // Store transaction locally, so we have it in our history and don't
            // need to fetch it in a minute
            accountBacking.putTransaction(transaction)
            accountBacking.setTransactionSuccessful()
        } finally {
            accountBacking.endTransaction()
        }

        // Tell account that we have a new transaction
        onNewTransaction(parsedTransaction)

        // Calculate local balance cache. It has changed because we have done
        // some spending
        updateLocalBalance()
        persistContextIfNecessary()
    }

    protected abstract fun persistContextIfNecessary()

    protected fun getSpendableOutputs(minerFeePerKbToUse: Long): Collection<TransactionOutputEx> =
            getSpendableOutputs(minerFeePerKbToUse, false)


    /**
     * @param minerFeePerKbToUse Determines the dust level, at which including a UTXO costs more than it is worth.
     * @return all UTXOs that are spendable now, as they are neither locked coinbase outputs nor unconfirmed received coins if _allowZeroConfSpending is not set nor dust.
     */
    private fun getSpendableOutputs(minerFeePerKbToUse: Long, skipDustCheck: Boolean): Collection<TransactionOutputEx> {
        val satDustOutput = StandardTransactionBuilder.MAX_INPUT_SIZE * minerFeePerKbToUse / 1000
        val allUnspentOutputs = accountBacking.allUnspentOutputs

        // Prune confirmed outputs for coinbase outputs that are not old enough
        // for spending. Also prune unconfirmed receiving coins except for change
        val it = allUnspentOutputs.iterator()
        while (it.hasNext()) {
            val output = it.next()
            // we remove all outputs that don't cover their costs (dust)
            // coinbase outputs are not spendable and this should not be overridden
            // Unless we allow zero confirmation spending we prune all unconfirmed outputs sent from foreign addresses
            if (!skipDustCheck && output.value < satDustOutput || output.isCoinBase && getBlockChainHeight() - output.height < COINBASE_MIN_CONFIRMATIONS
                    || !allowZeroConfSpending && output.height == -1 && !isFromMe(output.outPoint.txid)) {
                it.remove()
            }
        }
        return allUnspentOutputs
    }

    abstract val changeAddress: BtcvAddress

    protected abstract fun getChangeAddress(vararg destinationAddresses: BtcvAddress): BtcvAddress

    override fun calculateMaxSpendableAmount(minerFeePerKbToUse: Value, destinationAddress: BtcvAddress?, txData: TransactionData?): Value {
        checkNotArchived()
        val spendableOutputs = transform(getSpendableOutputs(minerFeePerKbToUse.valueAsLong))
        var satoshis: Long = 0

        // sum up the maximal available number of satoshis (i.e. sum of all spendable outputs)
        for (output in spendableOutputs) {
            satoshis += output.value
        }

        // TODO: 25.06.17 the following comment was justifying to assume two outputs, which might wrongly lead to no spendable funds or am I reading the wrongly? I assume one output only for the max.
        // we will use all of the available inputs and it will be only one output
        // but we use "2" here, because the tx-estimation in StandardTransactionBuilder always includes an
        // output into its estimate - so add one here too to arrive at the same tx fee
        val estimatorBuilder = FeeEstimatorBuilder().setArrayOfInputs(spendableOutputs)
                .setMinerFeePerKb(minerFeePerKbToUse.valueAsLong)
        addOutputToEstimation(destinationAddress, estimatorBuilder)
        val estimator = estimatorBuilder.createFeeEstimator()
        val feeToUse = estimator.estimateFee()
        satoshis -= feeToUse
        if (satoshis <= 0) {
            return zeroValue(coinType)
        }

        // Create transaction builder
        val stb = StandardTransactionBuilder(network)
        val destinationAddressType: AddressType = destinationAddress?.type ?: AddressType.P2PKH
        // Try and add the output
        try {
            // Note, null address used here, we just use it for measuring the transaction size
            stb.addOutput(getDummyAddress(destinationAddressType.toString()), satoshis)
        } catch (e1: StandardTransactionBuilder.BtcOutputTooSmallException) {
            // The amount we try to send is lower than what the network allows
            return zeroValue(coinType)
        }

        // Try to create an unsigned transaction
        return try {
            stb.createUnsignedTransaction(spendableOutputs,
                    getChangeAddress(getDummyAddress(destinationAddressType.toString())),
                    PublicKeyRing(), network, minerFeePerKbToUse.valueAsLong)
            // We have enough to pay the fees, return the amount as the maximum
            valueOf(coinType, satoshis)
        } catch (e: StandardTransactionBuilder.InsufficientBtcException) {
            zeroValue(coinType)
        } catch (e: StandardTransactionBuilder.UnableToBuildTransactionException) {
            zeroValue(coinType)
        }
    }

    private fun addOutputToEstimation(outputAddress: BitcoinAddress?, estimatorBuilder: FeeEstimatorBuilder) {
        val type = if (outputAddress != null) outputAddress.type else AddressType.P2PKH
        estimatorBuilder.addOutput(type)
    }

    @Throws(KeyCipher.InvalidKeyCipher::class)
    protected abstract fun getPrivateKey(publicKey: PublicKey, cipher: KeyCipher): InMemoryPrivateKey?

    protected abstract fun getPublicKeyForAddress(address: BitcoinAddress): PublicKey?

    @Throws(StandardTransactionBuilder.BtcOutputTooSmallException::class, StandardTransactionBuilder.InsufficientBtcException::class, StandardTransactionBuilder.UnableToBuildTransactionException::class)
    fun createUnsignedTransaction(receivers: List<BtcvReceiver>, minerFeeToUse: Long): UnsignedTransaction {
        checkNotArchived()

        // Determine the list of spendable outputs
        val spendable = transform(getSpendableOutputs(minerFeeToUse))

        // Create the unsigned transaction
        val stb = StandardTransactionBuilder(network)
        val addressList: MutableList<BtcvAddress> = mutableListOf()
        for (receiver in receivers) {
            stb.addOutput(receiver.address, receiver.amount)
            addressList.add(receiver.address)
        }
        val changeAddress = getChangeAddress(*addressList.toTypedArray())
        return stb.createUnsignedTransaction(spendable, changeAddress, PublicKeyRing(),
                network, minerFeeToUse)
    }

    @Throws(StandardTransactionBuilder.BtcOutputTooSmallException::class, StandardTransactionBuilder.InsufficientBtcException::class, StandardTransactionBuilder.UnableToBuildTransactionException::class)
    fun createUnsignedTransaction(outputs: OutputList, minerFeeToUse: Long): UnsignedTransaction {
        checkNotArchived()

        // Determine the list of spendable outputs
        val spendable = transform(getSpendableOutputs(minerFeeToUse))

        // Create the unsigned transaction
        val stb = StandardTransactionBuilder(network)
        stb.addOutputs(outputs)
        val changeAddress = changeAddress
        return stb.createUnsignedTransaction(spendable, changeAddress, PublicKeyRing(),
                network, minerFeeToUse)
    }

    /**
     * Create a new, unsigned transaction that spends from a UTXO of the provided transaction.
     *
     * @param txid          transaction to spend from
     * @param minerFeeToUse fee to use to pay up for txid and the new transaction
     * @param satoshisPaid  amount already paid by parent transaction
     * @see WalletBtcAccount.createUnsignedTransaction
     */
    @Throws(StandardTransactionBuilder.InsufficientBtcException::class, StandardTransactionBuilder.UnableToBuildTransactionException::class)
    fun createUnsignedCPFPTransaction(txid: Sha256Hash, minerFeeToUse: Long, satoshisPaid: Long): UnsignedTransaction {
        checkNotArchived()
        val utxos: MutableList<UnspentTransactionOutput> = ArrayList(transform(getSpendableOutputs(minerFeeToUse, true)))
        val parent = getTransactionDetails(txid)
        var totalSpendableSatoshis: Long = 0
        var haveOutputToBump = false
        val utxosToSpend: MutableList<UnspentTransactionOutput> = ArrayList()
        for (utxo in utxos) {
            if (utxo.outPoint.txid == txid) {
                // moving the bumpable UTXO to the beginning for the transaction to be built:
                utxos.remove(utxo)
                utxos.add(0, utxo)
                haveOutputToBump = true
                break
            }
        }
        if (!haveOutputToBump) {
            throw StandardTransactionBuilder.UnableToBuildTransactionException("We have no UTXO")
        }
        val changeAddress = changeAddress
        var parentChildFeeSat: Long
        var builder = FeeEstimatorBuilder().setArrayOfInputs(utxosToSpend)
        addOutputToEstimation(changeAddress, builder)
        var childSize = builder.createFeeEstimator().estimateTransactionSize().toLong()
        var parentChildSize = parent.rawSize + childSize
        parentChildFeeSat = parentChildSize * minerFeeToUse / 1000 - satoshisPaid
        if (parentChildFeeSat < childSize * minerFeeToUse / 1000) {
            // if child doesn't get itself to target priority, it's not needed to boost a parent to it.
            throw StandardTransactionBuilder.UnableToBuildTransactionException("parent needs no boosting")
        }
        do {
            val utxo = utxos.removeAt(0)
            utxosToSpend.add(utxo)
            totalSpendableSatoshis += utxo.value
            builder = FeeEstimatorBuilder().setArrayOfInputs(utxosToSpend)
            addOutputToEstimation(changeAddress, builder)
            childSize = builder.createFeeEstimator().estimateTransactionSize().toLong()
            parentChildSize = parent.rawSize + childSize
            parentChildFeeSat = parentChildSize * minerFeeToUse / 1000 - satoshisPaid
            val value = totalSpendableSatoshis - parentChildFeeSat
            if (value >= TransactionUtils.MINIMUM_OUTPUT_VALUE) {
                val outputs = listOf(StandardTransactionBuilder.createOutput(changeAddress, value, network))
                return UnsignedTransaction(outputs, utxosToSpend, PublicKeyRing(), network, 0, UnsignedTransaction.NO_SEQUENCE)
            }
        } while (!utxos.isEmpty())
        throw StandardTransactionBuilder.InsufficientBtcException(0, parentChildFeeSat)
    }

    // public method that needs no synchronization
    // We make a copy of the reference for a reason. Otherwise the balance
    // might change right when we make a copy
    val balance: BalanceSatoshis
        get() {
            // public method that needs no synchronization
            checkNotArchived()
            // We make a copy of the reference for a reason. Otherwise the balance
            // might change right when we make a copy
            val b = cachedBalance
            return if (b != null) BalanceSatoshis(b.confirmed, b.pendingReceiving, b.pendingSending, b.pendingChange, b.updateTime,
                    b.blockHeight, isSyncing(), b.allowsZeroConfSpending) else BalanceSatoshis(0, 0, 0, 0, 0, 0, isSyncing(), false)
        }

    /**
     * Update the balance by summing up the unspent outputs in local persistence.
     *
     * @return true if the balance changed, false otherwise
     */
    protected fun updateLocalBalance(): Boolean {
        val balance = calculateLocalBalance()
        if (balance != cachedBalance) {
            cachedBalance = balance
            accountListener?.balanceUpdated(this)
            return true
        }
        return false
    }

    private fun transform(tex: TransactionEx, blockChainHeight: Int): TransactionSummary? {
        val tx: BitcoinTransaction = try {
            BitcoinTransaction.fromByteReader(ByteReader(tex.binary))
        } catch (e: BitcoinTransaction.TransactionParsingException) {
            // Should not happen as we have parsed the transaction earlier
            logger.log(Level.SEVERE, "Unable to parse ")
            return null
        }

        // Outputs
        var satoshis: Long = 0
        val toAddresses: MutableList<BitcoinAddress> = ArrayList()
        var destAddress: BitcoinAddress? = null
        for (output in tx.outputs) {
            val address = output.script.getAddress(network)
            if (isMine(output.script)) {
                satoshis += output.value
            } else {
                destAddress = address
            }
            if (address != null && address != dummyAddress) {
                toAddresses.add(address)
            }
        }

        // Inputs
        if (!tx.isCoinbase) {
            for (input in tx.inputs) {
                // find parent output
                var funding = accountBacking.getParentTransactionOutput(input.outPoint)
                if (funding == null) {
                    funding = accountBacking.getUnspentOutput(input.outPoint)
                }
                if (funding == null) {
                    continue
                }
                if (isMine(funding)) {
                    satoshis -= funding.value
                }
            }
        }
        // else {
        //    For coinbase transactions there is nothing to subtract
        // }
        val confirmations: Int = if (tex.height == -1) {
            0
        } else {
            max(0, blockChainHeight - tex.height + 1)
        }

        // only track a destinationAddress if it is an outgoing transaction (i.e. send money to someone)
        // to prevent the user that he tries to return money to an address he got bitcoin from.
        if (satoshis >= 0) {
            destAddress = null
        }
        val isQueuedOutgoing = accountBacking.isOutgoingTransaction(tx.id)

        // see if we have a riskAssessment for this tx available in memory (i.e. valid for last sync)
        val risk = riskAssessmentForUnconfirmedTx[tx.id]
        return TransactionSummary(
                tx.id,
                ExactBitcoinValue.from(Math.abs(satoshis)),
                satoshis >= 0,
                tex.time.toLong(),
                tex.height,
                confirmations,
                isQueuedOutgoing,
                risk,
                Optional.fromNullable(destAddress),
                toAddresses)
    }// This never happens as we have parsed this script before
    // Sort & return
    // Note that this method is not synchronized, and we might fetch the transaction history while synchronizing
    // accounts. That should be ok as we write to the DB in a sane order.

    // Get all unspent outputs for this account
    val unspentTransactionOutputSummary: List<TransactionOutputSummary>
        // Transform it to a list of summaries
        // Note that this method is not synchronized, and we might fetch the transaction history while synchronizing
        // accounts. That should be ok as we write to the DB in a sane order.
        // Get all unspent outputs for this account
        get() = accountBacking.allUnspentOutputs
                .map {
                    val script = ScriptOutput.fromScriptBytes(it.script)
                    TransactionOutputSummary(it.outPoint, it.value, it.height,
                            if (it.height == -1) {
                                0
                            } else {
                                max(0, getBlockChainHeight() - it.height + 1)
                            },
                            script?.getAddress(network) ?: dummyAddress)
                }.sorted()

    protected suspend fun monitorYoungTransactions(): Boolean {
        val list = accountBacking.getYoungTransactions(5, getBlockChainHeight())
        if (list.isEmpty()) {
            return true
        }
        val txids: MutableList<Sha256Hash> = ArrayList(list.size)
        for (tex in list) {
            txids.add(tex.txid)
        }
        val result: CheckTransactionsResponse = try {
            wapi.checkTransactions(CheckTransactionsRequest(txids)).result
        } catch (e: WapiException) {
            lastSyncInfo = SyncStatusInfo(SyncStatus.ERROR)
            postEvent(WalletManager.Event.SERVER_CONNECTION_ERROR)
            logger.log(Level.SEVERE, "Server connection failed with error code: " + e.errorCode, e)
            // We failed to check transactions
            return false
        }
        for (t in result.transactions) {
            val localTransactionEx = accountBacking.getTransaction(t.txid)
            val parsedTransaction: BitcoinTransaction? = if (localTransactionEx != null) {
                try {
                    BitcoinTransaction.fromBytes(localTransactionEx.binary)
                } catch (ignore: BitcoinTransaction.TransactionParsingException) {
                    null
                }
            } else {
                null
            }

            // check if this transaction is unconfirmed and spends any inputs that got already spend
            // by any other transaction we know
            var isDoubleSpend = false
            if (parsedTransaction != null && localTransactionEx!!.height == -1) {
                for (input in parsedTransaction.inputs) {
                    val otherTx = accountBacking.getTransactionsReferencingOutPoint(input.outPoint)
                    // remove myself
                    otherTx.remove(parsedTransaction.id)
                    if (!otherTx.isEmpty()) {
                        isDoubleSpend = true
                    }
                }
            }

            // if this transaction summary has a risk assessment set, remember it
            if (t.rbfRisk || t.unconfirmedChainLength > 0 || isDoubleSpend) {
                riskAssessmentForUnconfirmedTx[t.txid] = ConfirmationRiskProfileLocal(t.unconfirmedChainLength, t.rbfRisk, isDoubleSpend)
            } else {
                // otherwise just remove it if we ever got one
                riskAssessmentForUnconfirmedTx.remove(t.txid)
            }

            // does the server know anything about this tx?
            if (!t.found) {
                if (localTransactionEx != null) {
                    // We have a transaction locally that did not get reported back by the server
                    // put it into the outgoing queue and mark it as "not transmitted" (even as it might be an incoming tx)
                    queueTransaction(localTransactionEx)
                } else {
                    // we haven't found it locally (shouldn't happen here) - so delete it to be sure
                    accountBacking.deleteTransaction(t.txid)
                }
                continue
            } else {
                // we got it back from the server and it got confirmations - remove it from out outgoing queue
                if (t.height > -1 || accountBacking.isOutgoingTransaction(t.txid)) {
                    accountBacking.removeOutgoingTransaction(t.txid)
                }
            }

            // update the local transaction
            if (localTransactionEx != null && localTransactionEx.height != t.height) {
                // The transaction got a new height. There could be
                // several reasons for that. It confirmed, or might also be a reorg.
                val newTex = TransactionEx(localTransactionEx.txid, localTransactionEx.hash, t.height, localTransactionEx.time, localTransactionEx.binary)
                logger.log(Level.INFO, String.format("Replacing: %s With: %s", localTransactionEx.toString(), newTex.toString()))
                accountBacking.putTransaction(newTex)
                postEvent(WalletManager.Event.TRANSACTION_HISTORY_CHANGED)
            }
        }
        return true
    }

    // local cache for received risk assessments for unconfirmed transactions - does not get persisted in the db
    protected var riskAssessmentForUnconfirmedTx = HashMap<Sha256Hash, ConfirmationRiskProfileLocal>()

    open inner class PublicKeyRing : IPublicKeyRing {
        override fun findPublicKeyByAddress(address: BitcoinAddress): PublicKey {
            val btcvAddress = toBtcvAddress(address)
            val publicKey = getPublicKeyForAddress(btcvAddress)
            if (publicKey != null) {
                return if (btcvAddress.type === AddressType.P2SH_P2WPKH
                        || btcvAddress.type === AddressType.P2WPKH) {
                    PublicKey(publicKey.pubKeyCompressed)
                } else publicKey
            }
            // something unexpected happened - the account might be in a undefined state
            // drop local cached data (transaction history, addresses - metadata will be kept)
            dropCachedData()
            throw RuntimeException(String.format("Unable to find public key for address %s acc:%s", btcvAddress.toString(), this@AbstractBtcvAccount.javaClass.toString()))
        }
    }

    inner class PrivateKeyRing(private var cipher: KeyCipher) : PublicKeyRing(), IPublicKeyRing, IPrivateKeyRing {
        override fun findSignerByPublicKey(publicKey: PublicKey): BitcoinSigner {
            val privateKey: InMemoryPrivateKey? = try {
                getPrivateKey(publicKey, cipher)
            } catch (e: KeyCipher.InvalidKeyCipher) {
                throw RuntimeException("Unable to decrypt private key for public key $publicKey")
            }
            return privateKey ?: throw RuntimeException("Unable to find private key for public key $publicKey")
        }
    }

    fun getTransactionSummary(txid: Sha256Hash): TransactionSummary? {
        val tx = accountBacking.getTransaction(txid)
        return transform(tx, tx.height)
    }

    fun getTransactionDetails(txid: Sha256Hash): TransactionDetails {
        // Note that this method is not synchronized, and we might fetch the transaction history while synchronizing
        // accounts. That should be ok as we write to the DB in a sane order.
        val tex = accountBacking.getTransaction(txid)
        val tx = TransactionEx.toTransaction(tex)
                ?: throw RuntimeException()
        val inputs: MutableList<TransactionDetails.Item> = ArrayList(tx.inputs.size)
        if (tx.isCoinbase) {
            // We have a coinbase transaction. Create one input with the sum of the outputs as its value,
            // and make the address the null address
            var value: Long = 0
            for (out in tx.outputs) {
                value += out.value
            }
            inputs.add(TransactionDetails.Item(BitcoinAddress.getNullAddress(network), value, true))
        } else {
            // Populate the inputs
            for (input in tx.inputs) {
                // Get the parent transaction
                val parentOutput = accountBacking.getParentTransactionOutput(input.outPoint)
                        ?: // We never heard about the parent, skip
                        continue
                // Determine the parent address
                var parentAddress: BitcoinAddress?
                val parentScript = ScriptOutput.fromScriptBytes(parentOutput.script)
                parentAddress = if (parentScript == null) {
                    // Null address means we couldn't figure out the address, strange script
                    BitcoinAddress.getNullAddress(network)
                } else {
                    parentScript.getAddress(network)
                }
                inputs.add(TransactionDetails.Item(parentAddress, parentOutput.value, false))
            }
        }
        // Populate the outputs
        val outputs = arrayOfNulls<TransactionDetails.Item>(tx.outputs.size)
        for (i in tx.outputs.indices) {
            val address = tx.outputs[i].script.getAddress(network)
            outputs[i] = TransactionDetails.Item(address, tx.outputs[i].value, false)
        }
        return TransactionDetails(
                txid, tex.height, tex.time,
                inputs.toTypedArray(), outputs,
                tx.vsize()
        )
    }

    override fun getTransactionsSince(receivingSince: Long): List<com.mycelium.wapi.wallet.TransactionSummary> {
        checkNotArchived()
        return accountBacking.getTransactionsSince(receivingSince)
                .mapNotNull {
                    getTxSummary(it.txid.bytes)
                }
    }

    override fun getTransactionSummaries(offset: Int, limit: Int): List<com.mycelium.wapi.wallet.TransactionSummary> {
        // Note that this method is not synchronized, and we might fetch the transaction history while synchronizing
        // accounts. That should be ok as we write to the DB in a sane order.
        checkNotArchived()
        return accountBacking.getTransactionHistory(offset, limit)
                .mapNotNull {
                    getTxSummary(it.txid.bytes)
                }
    }

    override fun getTxSummary(transactionId: ByteArray): com.mycelium.wapi.wallet.TransactionSummary? {
        checkNotArchived()
        val tex = accountBacking.getTransaction(Sha256Hash.of(transactionId))
        val tx = TransactionEx.toTransaction(tex) ?: return null
        var satoshisReceived: Long = 0
        var satoshisSent: Long = 0
        var satoshisTransferred: Long = 0
        val destinationAddresses: MutableList<Address> = ArrayList()
        val outputs = ArrayList<OutputViewModel>()
        for (output in tx.outputs) {
            val address = toBtcvAddress(output.script.getAddress(network))
            if (isMine(output.script)) {
                satoshisTransferred += output.value
            } else {
                destinationAddresses.add(address)
            }
            satoshisReceived += output.value
            if (address != null && address != BitcoinAddress.getNullAddress(network)) {
                outputs.add(OutputViewModel(address, valueOf(coinType, output.value), false))
            }
        }
        val inputs = ArrayList<InputViewModel>() //need to create list of outputs

        // Inputs
        if (tx.isCoinbase) {
            // We have a coinbase transaction. Create one input with the sum of the outputs as its value,
            // and make the address the null address
            var value: Long = 0
            for (out in tx.outputs) {
                value += out.value
            }
            inputs.add(InputViewModel(dummyAddress, valueOf(coinType, value), true))
        } else {
            for (input in tx.inputs) {
                // find parent output
                val funding = accountBacking.getParentTransactionOutput(input.outPoint) ?: continue
                if (isMine(funding)) {
                    satoshisTransferred -= funding.value
                }
                satoshisSent += funding.value
                val address = toBtcvAddress(ScriptOutput.fromScriptBytes(funding.script).getAddress(network))
                inputs.add(InputViewModel(address, valueOf(coinType, funding.value), false))
            }
        }
        val confirmations: Int
        confirmations = if (tex.height == -1) {
            0
        } else {
            max(0, getBlockChainHeight() - tex.height + 1)
        }
        val isQueuedOutgoing = accountBacking.isOutgoingTransaction(tx.id)
        return com.mycelium.wapi.wallet.TransactionSummary(coinType, tx.id.bytes, tx.hash.bytes, valueOf(coinType, satoshisTransferred), tex.time.toLong(), tex.height,
                confirmations, isQueuedOutgoing, inputs, outputs, destinationAddresses, riskAssessmentForUnconfirmedTx[tx.id],
                tx.vsize(), valueOf(coinType, abs(satoshisReceived - satoshisSent)))
    }

    override val coinType: CryptoCurrency
        get() = if (network.isProdnet) BitcoinVaultMain else BitcoinVaultTest

    override val basedOnCoinType: CryptoCurrency
        get() = coinType

    override val accountBalance: Balance
        get() = cachedBalance?.let { balance ->
            Balance(
                valueOf(coinType, balance.confirmed),
                valueOf(coinType, balance.pendingReceiving),
                valueOf(coinType, balance.pendingSending),
                valueOf(coinType, balance.pendingChange)
            )
        } ?: Balance.getZeroBalance(coinType)
    override val syncTotalRetrievedTransactions: Int
        get() = syncTotalRetrievedTxs

    fun updateSyncProgress() {
        postEvent(WalletManager.Event.SYNC_PROGRESS_UPDATED)
    }

    override val typicalEstimatedTransactionSize: Int
        get() = FeeEstimatorBuilder().setLegacyInputs(1)
                    .setLegacyOutputs(2)
                    .createFeeEstimator().estimateTransactionSize()


    override fun getUnspentOutputViewModels(): List<OutputViewModel> =
            unspentTransactionOutputSummary.map {
                OutputViewModel(BtcAddress(coinType, it.address), valueOf(coinType, it.value), false)
            }

    override fun isSpendingUnconfirmed(tx: Transaction): Boolean {
        val btcTx = tx as BtcvTransaction
        val unsignedTransaction = btcTx.unsignedTx
        for (out in unsignedTransaction!!.fundingOutputs) {
            val address = out.script.getAddress(network)
            if (out.height == -1 && isOwnExternalAddress(toBtcvAddress(address))) {
                // this is an unconfirmed output from an external address -> we want to warn the user
                // we allow unconfirmed spending of internal (=change addresses) without warning
                return true
            }
        }
        //no unconfirmed outputs are used as inputs, we are fine
        return false
    }

    @Throws(WapiException::class)
    suspend fun updateParentOutputs(txid: ByteArray?) {
        val transactionEx = getTransaction(Sha256Hash.of(txid))
        val transaction = TransactionEx.toTransaction(transactionEx)
        fetchStoreAndValidateParentOutputs(listOf(transaction), true)
    }

    abstract fun isOwnExternalAddress(address: BtcvAddress): Boolean
    protected abstract fun isValidEncryptionKey(cipher: KeyCipher?): Boolean

    companion object {
        private const val COINBASE_MIN_CONFIRMATIONS = 100
        private const val MAX_TRANSACTIONS_TO_HANDLE_SIMULTANEOUSLY = 199
        private fun toMap(list: Collection<TransactionOutputEx>): Map<OutPoint, TransactionOutputEx> {
            val map: MutableMap<OutPoint, TransactionOutputEx> = HashMap()
            for (t in list) {
                map[t.outPoint] = t
            }
            return map
        }

        private fun transform(source: Collection<TransactionOutputEx>): Collection<UnspentTransactionOutput> {
            val outputs: MutableList<UnspentTransactionOutput> = ArrayList()
            for (s in source) {
                val script = ScriptOutput.fromScriptBytes(s.script)
                outputs.add(UnspentTransactionOutput(s.outPoint, s.height, s.value, script))
            }
            return outputs
        }
    }
}
