package ru.alex9127.app.database;

import android.content.*;
import android.database.Cursor;
import android.database.sqlite.*;

import androidx.annotation.NonNull;

import java.util.ArrayList;

public class HistoryDataBase {
    public static final String DATABASE_NAME = "history.db";
    public static final int DATABASE_VERSION = 1;
    public static final String TABLE_NAME = "timeSpent";

    public static final String COLUMN_ID = "id";
    public static final String COLUMN_TIME_SPENT = "timeSpent";

    public static final int NUM_COLUMN_ID = 0;
    public static final int NUM_COLUMN_TIME_SPENT = 1;

    private final SQLiteDatabase database;

    private static class DatabaseOpener extends SQLiteOpenHelper {
        DatabaseOpener(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            String query = "CREATE TABLE IF NOT EXISTS " + TABLE_NAME + " (" +
                    COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COLUMN_TIME_SPENT + " INTEGER);";
            db.execSQL(query);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        }
    }

    public static class Item {
        final long id;
        final int timeSpent;

        Item(long id, int t) {
            this.id = id;
            timeSpent = t;
        }

        @NonNull
        @Override
        public String toString() {
            return id + "|" + timeSpent + "\n";
        }
    }

    public HistoryDataBase(Context context) {
        DatabaseOpener opener = new DatabaseOpener(context);
        database = opener.getWritableDatabase();
    }

    public long insert(int timeSpent) {
        ContentValues cv = new ContentValues();
        cv.put(COLUMN_TIME_SPENT, timeSpent);
        return database.insert(TABLE_NAME, null, cv);
    }

    public Item select(long id) {
        Cursor cursor = database.query(TABLE_NAME, null, COLUMN_ID + " = ?", new String[]{String.valueOf(id)}, null, null, null);
        boolean found = cursor.moveToFirst();
        if (!found) return null;
        int timeSpent = cursor.getInt(NUM_COLUMN_TIME_SPENT);
        cursor.close();
        return new Item(id, timeSpent);
    }

    public ArrayList<Item> selectAll() {
        Cursor cursor = database.query(TABLE_NAME, null, null, null, null, null, null);
        ArrayList<Item> arr = new ArrayList<>();
        cursor.moveToFirst();
        if (!cursor.isAfterLast()) {
            do {
                long id = cursor.getLong(NUM_COLUMN_ID);
                int timeSpent = cursor.getInt(NUM_COLUMN_TIME_SPENT);
                arr.add(new Item(id, timeSpent));
            } while (cursor.moveToNext());
        }
        cursor.close();
        return arr;
    }

    public int sum() {
        int sum = 0;
        for (Item item:selectAll()) {
            sum += item.timeSpent;
        }
        return sum;
    }
}
