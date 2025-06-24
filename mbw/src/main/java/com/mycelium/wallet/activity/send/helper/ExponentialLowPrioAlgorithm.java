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
package com.mycelium.wallet.activity.send.helper;

/**
 * Wrapper on ExponentialFeeItemsAlgorithm for Low Prio fees
 * values will not change depends from server value, instead we change count of steps
 */

public class ExponentialLowPrioAlgorithm implements FeeItemsAlgorithm {
    private int minPosition;
    private int maxPosition;
    private ExponentialFeeItemsAlgorithm algorithm;

    public ExponentialLowPrioAlgorithm(long minValue, long maxValue) {
        minPosition = 1;
        algorithm = new ExponentialFeeItemsAlgorithm(minValue, 1, maxValue, 15);
        maxPosition = minPosition;
        while (algorithm.computeValue(maxPosition + 1) < maxValue) {
            maxPosition++;
        }
    }

    @Override
    public long computeValue(int position) {
        return algorithm.computeValue(position);
    }

    @Override
    public int getMinPosition() {
        return minPosition;
    }

    @Override
    public int getMaxPosition() {
        return maxPosition;
    }
}
