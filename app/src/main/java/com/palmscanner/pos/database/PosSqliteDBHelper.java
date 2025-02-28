package com.palmscanner.pos.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import com.palmscanner.pos.database.model.User;

public class PosSqliteDBHelper extends SQLiteOpenHelper {
    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "pos_user_db.db";

    private static final String SQL_CREATE_ENTRIES =
            "CREATE TABLE " + User.UserEntry.TABLE_NAME + " (" +
                    User.UserEntry._ID + " INTEGER PRIMARY KEY," +
                    User.UserEntry.COLUMN_NAME_UUID + " TEXT," +
                    User.UserEntry.COLUMN_NAME_CARD_NUMBER + " TEXT," +
                    User.UserEntry.COLUMN_NAME_CARD_CVV + " TEXT," +
                    User.UserEntry.COLUMN_NAME_CARD_EXPIRATION_DATE + " TEXT," +
                    User.UserEntry.COLUMN_NAME_CARD_HOLDER_NAME + " TEXT," +
                    User.UserEntry.COLUMN_NAME_CARD_TYPE + " TEXT," +
                    User.UserEntry.COLUMN_NAME_PALM_TEMPLATE + " TEXT)";

    private static final String SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS " + User.UserEntry.TABLE_NAME;
    public PosSqliteDBHelper(@Nullable Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_ENTRIES);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(SQL_DELETE_ENTRIES);
        onCreate(db);
    }

    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        super.onDowngrade(db, oldVersion, newVersion);
    }
}
