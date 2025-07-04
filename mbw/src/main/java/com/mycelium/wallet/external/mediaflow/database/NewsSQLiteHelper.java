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
package com.mycelium.wallet.external.mediaflow.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class NewsSQLiteHelper extends SQLiteOpenHelper {
    public static final String NEWS = "news";
    private static final String DATABASE_NAME = NEWS + ".db";
    private static final int DATABASE_VERSION = 5;

    public NewsSQLiteHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE IF NOT EXISTS " + NEWS + " (id INTEGER PRIMARY KEY" +
                ", title TEXT" +
                ", content TEXT" +
                ", date INTEGER" +
                ", author TEXT" +
                ", short_URL TEXT" +
                ", category TEXT" +
                ", categories TEXT" +
                ", image TEXT" +
                ", read INTEGER" +
                ", excerpt TEXT" +
                ", isfull INTEGER" +
                ", tags TEXT" +
                ");");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion < 3) {
            db.execSQL("ALTER TABLE " + NEWS + " ADD COLUMN image TEXT");
            db.execSQL("ALTER TABLE " + NEWS + " ADD COLUMN read TEXT");
            db.execSQL("ALTER TABLE " + NEWS + " ADD COLUMN excerpt TEXT");
        }
        if(oldVersion < 4) {
            db.execSQL("ALTER TABLE " + NEWS + " ADD COLUMN isfull INTEGER");
        }
        if (oldVersion < 5) {
            db.execSQL("ALTER TABLE " + NEWS + " ADD COLUMN tags TEXT");
        }
    }
}
