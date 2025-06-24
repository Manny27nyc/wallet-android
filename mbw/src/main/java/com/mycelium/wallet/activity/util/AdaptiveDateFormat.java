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
package com.mycelium.wallet.activity.util;

import android.content.Context;

import java.text.DateFormat;
import java.text.FieldPosition;
import java.text.ParsePosition;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class AdaptiveDateFormat extends DateFormat {
    Date _midnight;
    DateFormat _dayFormat;
    DateFormat _hourFormat;

    public AdaptiveDateFormat(Context context) {
        // Get the time at last midnight
        Calendar midnight = Calendar.getInstance();
        midnight.set(midnight.get(Calendar.YEAR), midnight.get(Calendar.MONTH), midnight.get(Calendar.DAY_OF_MONTH),
                0, 0, 0);
        _midnight = midnight.getTime();
        // Create date formats for hourly and day format
        Locale locale = context.getResources().getConfiguration().locale;
        _dayFormat = DateFormat.getDateInstance(DateFormat.SHORT, locale);
        _hourFormat = android.text.format.DateFormat.getTimeFormat(context);
    }

    @Override
    public StringBuffer format(Date date, StringBuffer buffer, FieldPosition field) {
        DateFormat dateFormat = date.before(_midnight) ? _dayFormat : _hourFormat;
        return dateFormat.format(date, buffer, field);
    }

    @Override
    public Date parse(String string, ParsePosition position) {
        return null;
    }
}
