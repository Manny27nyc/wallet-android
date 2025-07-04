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
package com.mycelium.wapi.wallet.btc;

import com.mrd.bitlib.model.AddressType;
import com.mrd.bitlib.model.BitcoinAddress;
import com.mrd.bitlib.model.hdpath.HdKeyPath;
import com.mycelium.wapi.wallet.Address;
import com.mycelium.wapi.wallet.coins.CryptoCurrency;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

public class BtcAddress implements Address {
    private BitcoinAddress address;
    private CryptoCurrency currencyType;

    public BtcAddress(CryptoCurrency currencyType, BitcoinAddress address) {
        this.address = address;
        this.currencyType = currencyType;
    }

    @Override
    public String toString() {
        return address.toString();
    }

    @NotNull
    public BitcoinAddress getAddress() {
        return address;
    }

    @NotNull
    @Override
    public CryptoCurrency getCoinType() {
        return currencyType;
    }

    @NotNull
    @Override
    public byte[] getBytes() {
        return address.getAllAddressBytes();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BtcAddress that = (BtcAddress) o;
        return Objects.equals(address, that.address);
    }

    @Override
    public int hashCode() {
        return Objects.hash(address);
    }

    @NotNull
    @Override
    public String getSubType() {
        return address.getType().name();
    }

    public AddressType getType()
    {
        return address.getType();
    }

    private HdKeyPath bip32Path;

    @Nullable
    @Override
    public HdKeyPath getBip32Path() {
        return bip32Path;
    }

    @Override
    public void setBip32Path(@Nullable HdKeyPath bip32Path) {
        this.bip32Path = bip32Path;
    }
}
