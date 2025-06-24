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
package se.grunka.fortuna;

import Rijndael.Rijndael;

public class Encryption {
    private final Rijndael rijndael = new Rijndael();

    public void setKey(byte[] key) {
        rijndael.makeKey(key, key.length * 8, Rijndael.DIR_ENCRYPT);
    }

    public byte[] encrypt(byte[] data) {
        byte[] result = new byte[data.length];
        rijndael.encrypt(data, result);
        return result;
    }
}
