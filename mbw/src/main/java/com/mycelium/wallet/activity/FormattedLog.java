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
package com.mycelium.wallet.activity;

import android.annotation.SuppressLint;

import com.mycelium.generated.logger.database.Logs;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class FormattedLog {
        @SuppressLint("SimpleDateFormat")
        private static final DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss.SSS");
        private Logs log;

        public FormattedLog(Logs log) {
            this.log = log;
        }

        @Override
        public String toString() {
            return dateFormat.format(new Date(log.getDateMillis())) + ":" + log.getLevel() + ":" + log.getMessage();
        }
    }