package com.tirkx.aos;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Pakkapon on 19/5/2558.
 */

// Cahcing Database for Temporary Data

public class CacheSQLiteHelper extends SQLiteOpenHelper {
    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "prepare.db";
    public CacheSQLiteHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE THREADLIST (ID INTEGER PRIMARY KEY NOT NULL,AID INTEGER,FANSUB CHAR(2048),FILENAME CHAR(2048),FILELANG CHAR(16),FILELINK CHAR(2048))");
        db.execSQL("CREATE TABLE NEWANIME (ID INTEGER PRIMARY KEY NOT NULL,AID INTEGER,FILENAME CHAR(2048),CHECKSUM CHAR(32),FILELANG CHAR(16))");
        db.execSQL("CREATE TABLE LASTFETCH (ID INTEGER PRIMARY KEY NOT NULL,AID INTEGER,FETCHTIME DATETIME)");
    }
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS THREADLIST");
        db.execSQL("DROP TABLE IF EXISTS LASTFETCH");
        db.execSQL("DROP TABLE IF EXISTS NEWANIME");
        onCreate(db);
    }
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }
}
