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
package se.grunka.fortuna.accumulator;

import se.grunka.fortuna.Pool;

public class EventAdderImpl implements EventAdder {
    private int pool;
    private final int sourceId;
    private final Pool[] pools;

    public EventAdderImpl(int sourceId, Pool[] pools) {
        this.sourceId = sourceId;
        this.pools = pools;
        pool = 0;
    }

    @Override
    public void add(byte[] event) {
        pool = (pool + 1) % pools.length;
        pools[pool].add(sourceId, event);
    }
}
