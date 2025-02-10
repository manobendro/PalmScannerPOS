package com.palmscanner.pos.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.Cursor;

import java.util.ArrayList;

import com.palmscanner.pos.database.model.User;
import com.palmscanner.pos.database.model.User.UserEntry;

public class PosSqliteDB {

    private final SQLiteDatabase db;
    private final PosSqliteDBHelper dbHelper;

    public PosSqliteDB(Context mContext) {
        dbHelper = new PosSqliteDBHelper(mContext);
        db = dbHelper.getWritableDatabase();
    }

    public ArrayList<User> queryAllUser() {
        ArrayList<User> users = new ArrayList<>();
        String[] projection = {
                UserEntry.COLUMN_NAME_UUID,
                UserEntry.COLUMN_NAME_PALM_TEMPLATE,
                UserEntry.COLUMN_NAME_CARD_HOLDER_NAME,
                UserEntry.COLUMN_NAME_CARD_NUMBER,
                UserEntry.COLUMN_NAME_CARD_CVV,
                UserEntry.COLUMN_NAME_CARD_EXPIRATION_DATE,
                UserEntry.COLUMN_NAME_CARD_TYPE
        };
        Cursor cursor = db.query(UserEntry.TABLE_NAME, projection, null, null, null, null, null);
        while (cursor.moveToNext()) {
            User user = new User();
            user.setUuid(cursor.getString(cursor.getColumnIndexOrThrow(UserEntry.COLUMN_NAME_UUID)));
            user.setPalmTemplate(cursor.getString(cursor.getColumnIndexOrThrow(UserEntry.COLUMN_NAME_PALM_TEMPLATE)));
            user.setCardHolderName(cursor.getString(cursor.getColumnIndexOrThrow(UserEntry.COLUMN_NAME_CARD_HOLDER_NAME)));
            user.setCardNumber(cursor.getString(cursor.getColumnIndexOrThrow(UserEntry.COLUMN_NAME_CARD_NUMBER)));
            user.setCardCvv(cursor.getString(cursor.getColumnIndexOrThrow(UserEntry.COLUMN_NAME_CARD_CVV)));
            user.setCardExpirationDate(cursor.getString(cursor.getColumnIndexOrThrow(UserEntry.COLUMN_NAME_CARD_EXPIRATION_DATE)));
            user.setCardType(cursor.getString(cursor.getColumnIndexOrThrow(UserEntry.COLUMN_NAME_CARD_TYPE)));

            users.add(user);
        }
        cursor.close();
        return users;
    }

    public void addUser(User user) {
        ContentValues values = new ContentValues();
        values.put(UserEntry.COLUMN_NAME_UUID, user.getUuid());
        values.put(UserEntry.COLUMN_NAME_PALM_TEMPLATE, user.getPalmTemplate());
        values.put(UserEntry.COLUMN_NAME_CARD_HOLDER_NAME, user.getCardHolderName());
        values.put(UserEntry.COLUMN_NAME_CARD_NUMBER, user.getCardNumber());
        values.put(UserEntry.COLUMN_NAME_CARD_CVV, user.getCardCvv());
        values.put(UserEntry.COLUMN_NAME_CARD_EXPIRATION_DATE, user.getCardExpirationDate());
        values.put(UserEntry.COLUMN_NAME_CARD_TYPE, user.getCardType());
        db.insert(UserEntry.TABLE_NAME, null, values);
    }

    public User getUserByUuid(String uuid) {
        String[] projection = {
            UserEntry.COLUMN_NAME_UUID,
            UserEntry.COLUMN_NAME_PALM_TEMPLATE,
            UserEntry.COLUMN_NAME_CARD_HOLDER_NAME,
            UserEntry.COLUMN_NAME_CARD_NUMBER,
            UserEntry.COLUMN_NAME_CARD_CVV,
            UserEntry.COLUMN_NAME_CARD_EXPIRATION_DATE,
            UserEntry.COLUMN_NAME_CARD_TYPE
        };
        String selection = UserEntry.COLUMN_NAME_UUID + " = ?";
        String[] selectionArgs = {uuid};
        Cursor cursor = db.query(UserEntry.TABLE_NAME, projection, selection, selectionArgs, null, null, null);
        User user = new User();
        if (cursor.moveToFirst()) {
            user.setUuid(cursor.getString(cursor.getColumnIndexOrThrow(UserEntry.COLUMN_NAME_UUID)));
            user.setPalmTemplate(cursor.getString(cursor.getColumnIndexOrThrow(UserEntry.COLUMN_NAME_PALM_TEMPLATE)));
            user.setCardHolderName(cursor.getString(cursor.getColumnIndexOrThrow(UserEntry.COLUMN_NAME_CARD_HOLDER_NAME)));
            user.setCardNumber(cursor.getString(cursor.getColumnIndexOrThrow(UserEntry.COLUMN_NAME_CARD_NUMBER)));
            user.setCardCvv(cursor.getString(cursor.getColumnIndexOrThrow(UserEntry.COLUMN_NAME_CARD_CVV)));
            user.setCardExpirationDate(cursor.getString(cursor.getColumnIndexOrThrow(UserEntry.COLUMN_NAME_CARD_EXPIRATION_DATE)));
            user.setCardType(cursor.getString(cursor.getColumnIndexOrThrow(UserEntry.COLUMN_NAME_CARD_TYPE)));
        }
        cursor.close();
        return user;
    }

    public void deleteUser(String uuid) {
        String selection = UserEntry.COLUMN_NAME_UUID + " = ?";
        String[] selectionArgs = {uuid};
        db.delete(UserEntry.TABLE_NAME, selection, selectionArgs);
    }

    public void updateUserPalmTemplate(String uuid, String palmTemplate) {
        ContentValues values = new ContentValues();
        values.put(UserEntry.COLUMN_NAME_PALM_TEMPLATE, palmTemplate);
        String selection = UserEntry.COLUMN_NAME_UUID + " = ?";
        db.update(UserEntry.TABLE_NAME, values, selection, new String[]{uuid});
    }


    public void closeDatabase() {
        if (this.dbHelper != null) {
            this.dbHelper.close();
        }
    }
}
