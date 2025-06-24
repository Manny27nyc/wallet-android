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
package com.mycelium.wallet.activity.rmc;

import java.util.Calendar;

public class Keys {
    public static Calendar getActiveStartDay() {
        Calendar calendarStart = Calendar.getInstance();
        calendarStart.set(2017, 6, 12);
        return calendarStart;
    }

    public static Calendar getActiveEndDay() {
        Calendar calendarEnd = Calendar.getInstance();
        calendarEnd.set(2018, 10, 30);
        return calendarEnd;
    }
}
