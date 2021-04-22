package com.mycelium.wapi.wallet.btcvault

import com.mrd.bitlib.model.NetworkParameters
import com.mrd.bitlib.util.HexUtils
import com.mycelium.wapi.wallet.CommonNetworkParameters


class BTCVNetworkParameters(networkType: NetworkType) : NetworkParameters(networkType), CommonNetworkParameters {
    companion object {
        private val TESTNET_GENESIS_BLOCK = HexUtils.toBytes("0100000043497fd7f826957108f4a30fd9cec3aeba79972084e90ead01ea3309"
                + "00000000bac8b0fa927c0ac8234287e33c5f74d38d354820e24756ad709d7038"
                + "fc5f31f020e7494dffff001d03e4b67201010000000100000000000000000000"
                + "00000000000000000000000000000000000000000000ffffffff0e0420e7494d"
                + "017f062f503253482fffffffff0100f2052a010000002321021aeaf2f8638a12"
                + "9a3156fbe7e5ef635226b0bafd495ff03afe2c843d7e3a4b51ac00000000")
        private val PRODNET_GENESIS_BLOCK = HexUtils.toBytes("0100000000000000000000000000000000000000000000000000000000000000"
                + "000000003ba3edfd7a7b12b27ac72c3e67768f617fc81bc3888a51323a9fb8aa"
                + "4b1e5e4a29ab5f49ffff001d1dac2b7c01010000000100000000000000000000"
                + "00000000000000000000000000000000000000000000ffffffff4d04ffff001d"
                + "0104455468652054696d65732030332f4a616e2f32303039204368616e63656c"
                + "6c6f72206f6e206272696e6b206f66207365636f6e64206261696c6f75742066"
                + "6f722062616e6b73ffffffff0100f2052a01000000434104678afdb0fe554827"
                + "1967f1a67130b7105cd6a828e03909a67962e0ea1f61deb649f6bc3f4cef38c4"
                + "f35504e51ec112de5c384df7ba0b8d578a4c702b6bf11d5fac00000000")
        private val REGTEST_GENESIS_BLOCK = HexUtils.toBytes("010000000000000000000000000000000000000000000000000000000000000000" +
                "0000003ba3edfd7a7b12b27ac72c3e67768f617fc81bc3888a51323a9fb8aa4b1e5e4adae5494dffff7f20020000000101000" +
                "000010000000000000000000000000000000000000000000000000000000000000000ffffffff4d04ffff001d010445546865" +
                "2054696d65732030332f4a616e2f32303039204368616e63656c6c6f72206f6e206272696e6b206f66207365636f6e6420626" +
                "1696c6f757420666f722062616e6b73ffffffff0100f2052a01000000434104678afdb0fe5548271967f1a67130b7105cd6a8" +
                "28e03909a67962e0ea1f61deb649f6bc3f4cef38c4f35504e51ec112de5c384df7ba0b8d578a4c702b6bf11d5fac00000000")

        @JvmStatic
        val testNetwork = BTCVNetworkParameters(NetworkType.TESTNET)

        @JvmStatic
        val productionNetwork = BTCVNetworkParameters(NetworkType.PRODNET)

        @JvmStatic
        val regtestNetwork = BTCVNetworkParameters(NetworkType.REGTEST)
    }

    private val bip44CoinType: Int
    /**
     * The first byte of a base58 encoded bitcoin standard address.
     */
    private val standardAddressHeader: Int
    /**
     * The first byte of a base58 encoded bitcoin multisig address.
     */
    private val multisigAddressHeader: Int
    private val genesisBlock: ByteArray
    private val port: Int
    private val packetMagic: Int
    private val packetMagicBytes: ByteArray

    init {
        when (networkType) {
            NetworkType.PRODNET -> {
                standardAddressHeader = 0x4E
                multisigAddressHeader = 0x3C
                genesisBlock = PRODNET_GENESIS_BLOCK
                port = 8333
                packetMagic = -0x6414b27
                packetMagicBytes = byteArrayOf(0xf9.toByte(), 0xbe.toByte(), 0xb4.toByte(), 0xd9.toByte())
                bip44CoinType = 440
            }
            NetworkType.TESTNET -> {
                standardAddressHeader = 0x6F
                multisigAddressHeader = 0xC4
                genesisBlock = TESTNET_GENESIS_BLOCK
                port = 18333
                packetMagic = 0x0b110907
                packetMagicBytes = byteArrayOf(0x0b.toByte(), 0x11.toByte(), 0x09.toByte(), 0x07.toByte())
                bip44CoinType = 441
            }
            NetworkType.REGTEST -> {
                standardAddressHeader = 0x6F
                multisigAddressHeader = 0xC4
                genesisBlock = REGTEST_GENESIS_BLOCK
                port = 18444
                packetMagic = -0x5404a26
                packetMagicBytes = byteArrayOf(0xfa.toByte(), 0xbf.toByte(), 0xb5.toByte(), 0xda.toByte())
                bip44CoinType = 1
            }
            else -> throw IllegalArgumentException()
        }
    }

    /**
     * Get the first byte of a base58 encoded bitcoin address as an integer.
     *
     * @return The first byte of a base58 encoded bitcoin address as an integer.
     */
    override fun getStandardAddressHeader(): Int = standardAddressHeader

    /**
     * Get the first byte of a base58 encoded bitcoin multisig address as an
     * integer.
     *
     * @return The first byte of a base58 encoded bitcoin multisig address as an
     * integer.
     */
    override fun getMultisigAddressHeader(): Int = multisigAddressHeader

    override fun getGenesisBlock(): ByteArray? = genesisBlock

    override fun getPort(): Int = port

    override fun getPacketMagic(): Int = packetMagic

    override fun getPacketMagicBytes(): ByteArray? = packetMagicBytes

    override fun hashCode(): Int = standardAddressHeader

    override fun isProdnet(): Boolean = this == productionNetwork

    override fun isRegTest(): Boolean = this == regtestNetwork

    override fun isTestnet(): Boolean = this == testNetwork

    // used for Trezor coin_name
    override fun getCoinName(): String? {
        return if (isProdnet()) "BitcoinVault" else "TestnetVault"
    }

    override fun equals(other: Any?): Boolean {
        if (other !is BTCVNetworkParameters) {
            return false
        }
        return other.standardAddressHeader == standardAddressHeader
    }

    override fun toString(): String = when {
        isProdnet -> "prodnet"
        networkType == NetworkType.REGTEST -> "regtest"
        else -> "testnet"
    }

    override fun getBip44CoinType(): Int = bip44CoinType
}
