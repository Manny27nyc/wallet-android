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

import java.lang.management.ManagementFactory;
import java.lang.management.OperatingSystemMXBean;
import java.math.BigDecimal;
import java.util.concurrent.TimeUnit;

import se.grunka.fortuna.accumulator.EntropySource;
import se.grunka.fortuna.accumulator.EventAdder;
import se.grunka.fortuna.accumulator.EventScheduler;
import se.grunka.fortuna.Util;

public class LoadAverageEntropySource implements EntropySource {

    private final OperatingSystemMXBean operatingSystemMXBean = ManagementFactory.getOperatingSystemMXBean();

    @Override
    public void event(EventScheduler scheduler, EventAdder adder) {
        double systemLoadAverage = operatingSystemMXBean.getSystemLoadAverage();
        BigDecimal value = BigDecimal.valueOf(systemLoadAverage);
        long convertedValue = value.movePointRight(value.scale()).longValue();
        adder.add(Util.twoLeastSignificantBytes(convertedValue));
        scheduler.schedule(1000, TimeUnit.MILLISECONDS);
    }
}
