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
package com.mycelium.wallet;

import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Handler;
import java.util.logging.LogRecord;

class AndroidLogHandler extends Handler {
    private static final String ANDROID_LOG_TAG = "AndroidLogHandler";
    private static final int SEVERE_LOG = 1000; // Level.SEVERE
    private static final int WARNING_LOG = 900; // Level.WARNING
    private static final int INFO_LOG = 800; // Level.INFO
    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");

    @Override
    public void publish(LogRecord record) {
        Date date = new Date(record.getMillis());
        String dateAsString = dateFormat.format(date);
        String message = dateAsString + ":" + record.getLevel().getName() + ": " + record.getMessage();
        switch (record.getLevel().intValue()) {
            case SEVERE_LOG: Log.e(ANDROID_LOG_TAG, message);break;
            case WARNING_LOG: Log.w(ANDROID_LOG_TAG, message);break;
            case INFO_LOG: Log.i(ANDROID_LOG_TAG, message);break;
            default: Log.wtf(ANDROID_LOG_TAG, message);
        }
    }

    @Override
    public void flush() {}

    @Override
    public void close() {}
}
