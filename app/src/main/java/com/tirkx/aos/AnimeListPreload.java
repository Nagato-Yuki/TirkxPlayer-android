package com.tirkx.aos;

import android.content.Context;

import com.readystatesoftware.sqliteasset.SQLiteAssetHelper;

/**
 * Created by Pakkapon on 21/5/2558.
 */

// Use by SQLiteAssetHelper for access preloaded data

public class AnimeListPreload extends SQLiteAssetHelper {
    private static final String DATABASE_NAME = "animelist.db";
    private static final int DATABASE_VERSION = 1;

    public AnimeListPreload(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }
}
