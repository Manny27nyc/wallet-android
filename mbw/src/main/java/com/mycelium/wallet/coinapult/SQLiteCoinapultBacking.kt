package com.mycelium.wallet.coinapult

import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteStatement
import com.google.common.base.Splitter
import com.mrd.bitlib.model.Address
import com.mrd.bitlib.model.NetworkParameters
import com.mrd.bitlib.util.HexUtils
import com.mycelium.wallet.persistence.MetadataStorage
import com.mycelium.wallet.persistence.SQLiteQueryWithBlobs
import com.mycelium.wallet.persistence.SQLiteQueryWithBlobs.uuidToBytes
import com.mycelium.wapi.wallet.CommonAccountBacking
import com.mycelium.wapi.wallet.WalletBacking
import com.mycelium.wapi.wallet.btc.BtcAddress
import com.mycelium.wapi.wallet.coinapult.CoinapultAccountContext
import com.mycelium.wapi.wallet.coinapult.CoinapultUtils
import com.mycelium.wapi.wallet.coinapult.Currency
import java.io.IOException
import java.util.*


class SQLiteCoinapultBacking(val context: Context
                             , val metadataStorage: MetadataStorage
                             , addressByteArray: ByteArray
                             , val networkParameters: NetworkParameters)
    : WalletBacking<CoinapultAccountContext> {
    val database: SQLiteDatabase

    private val insertOrReplaceAccount: SQLiteStatement
    private val updateAccount: SQLiteStatement

    private var backings = mutableMapOf<UUID, SQLiteCoinapultAccountBacking>()

    init {
        val helper = CoinapultSQLiteHelper(context)
        database = helper.writableDatabase

        insertOrReplaceAccount = database.compileStatement("INSERT OR REPLACE INTO coinapultcontext VALUES (?,?,?,?)")
        updateAccount = database.compileStatement("UPDATE coinapultcontext SET archived=?,address=? WHERE id=?")
        /**
         * import accounts from old place
         */
        val currencies = Splitter.on(",").split(metadataStorage.coinapultCurrencies)
        currencies.forEach {
            Currency.all[it]?.let { currency ->
                val id = CoinapultUtils.getGuidForAsset(currency, addressByteArray)
                val address = BtcAddress(currency, metadataStorage.getCoinapultAddress(currency.name).get())
                createAccountContext(CoinapultAccountContext(id, address, metadataStorage.getArchived(id), currency))
            }
        }
    }

    override fun createAccountContext(context: CoinapultAccountContext) {
        database.beginTransaction()
        try {

            // Create accountBacking tables
            var backing = backings[context.id]
            if (backing == null) {
                createAccountBackingTables(context.id, database)
                backing = SQLiteCoinapultAccountBacking(context.id, database)
                backings[context.id] = backing
            }

            // Create context
            insertOrReplaceAccount.bindBlob(1, uuidToBytes(context.id))
            insertOrReplaceAccount.bindBlob(2, context.address.getBytes())
            insertOrReplaceAccount.bindLong(3, (if (context.isArchived()) 1 else 0).toLong())
            insertOrReplaceAccount.bindString(4, context.currency.name)
            insertOrReplaceAccount.executeInsert()
            database.setTransactionSuccessful()
        } finally {
            database.endTransaction()
        }
    }

    override fun updateAccountContext(context: CoinapultAccountContext) {
        database.beginTransaction()
        try {
            // "UPDATE single SET archived=?,blockheight=? WHERE id=?"
            updateAccount.bindLong(1, (if (context.isArchived()) 1 else 0).toLong())
            updateAccount.bindBlob(2, context.address.getBytes())
            updateAccount.bindBlob(3, uuidToBytes(context.id))
            updateAccount.execute()
            database.setTransactionSuccessful()
        } catch (ignore: IOException) {
            // ignore
        } finally {
            database.endTransaction()
        }
    }

    private fun createAccountBackingTables(id: UUID, db: SQLiteDatabase) {
        val tableSuffix = HexUtils.toHex(uuidToBytes(id))
        db.execSQL("CREATE TABLE IF NOT EXISTS tx$tableSuffix"
                + " (id BLOB PRIMARY KEY, hash BLOB, height INTEGER, time INTEGER, binary BLOB);")
    }

    override fun loadAccountContexts(): List<CoinapultAccountContext> {
        val result = mutableListOf<CoinapultAccountContext>()

        var cursor: Cursor? = null
        try {
            val blobQuery = SQLiteQueryWithBlobs(database)
            cursor = blobQuery.query(false, "coinapultcontext"
                    , arrayOf("id", "address", "archived", "currency"), null
                    , null, null, null, null, null)
            while (cursor.moveToNext()) {
                val id = SQLiteQueryWithBlobs.uuidFromBytes(cursor.getBlob(0))
                val addressBytes = cursor.getBlob(1)
                val isArchived = cursor.getInt(2) == 1
                val currency = Currency.all[cursor.getString(3)]!!
                backings[id] = SQLiteCoinapultAccountBacking(id, database)
                result.add(CoinapultAccountContext(id, BtcAddress(currency, Address(addressBytes)), isArchived, currency))
            }
        } finally {
            cursor?.close()
        }
        return result
    }

    override fun getAccountBacking(accountId: UUID?): CommonAccountBacking? = backings[accountId]

    override fun deleteAccountContext(uuid: UUID?) {

    }
}