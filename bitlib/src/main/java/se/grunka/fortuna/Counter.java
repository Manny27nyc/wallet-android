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

public class Counter {
    private final byte[] state;

    public Counter(int bits) {
        if (bits < 8) {
            throw new IllegalArgumentException("Too few bits");
        }
        if (bits % 8 != 0) {
            throw new IllegalArgumentException("Only even bytes allowed");
        }
        state = new byte[bits / 8];
    }

    public void increment() {
        int position = 0;
        byte newValue;
        do {
            newValue = ++state[position++];
        }
        while (newValue == 0 && position < state.length);
    }

    public byte[] getState() {
        return state;
    }

    public boolean isZero() {
        for (byte b : state) {
            if (b != 0) {
                return false;
            }
        }
        return true;
    }
}
