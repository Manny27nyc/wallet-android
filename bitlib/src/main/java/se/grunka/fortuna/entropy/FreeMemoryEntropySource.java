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
package se.grunka.fortuna.entropy;

import java.util.concurrent.TimeUnit;

import se.grunka.fortuna.accumulator.EntropySource;
import se.grunka.fortuna.accumulator.EventAdder;
import se.grunka.fortuna.accumulator.EventScheduler;
import se.grunka.fortuna.Util;

public class FreeMemoryEntropySource implements EntropySource {
    @Override
    public void event(EventScheduler scheduler, EventAdder adder) {
        long freeMemory = Runtime.getRuntime().freeMemory();
        adder.add(Util.twoLeastSignificantBytes(freeMemory));
        scheduler.schedule(100, TimeUnit.MILLISECONDS);
    }
}
