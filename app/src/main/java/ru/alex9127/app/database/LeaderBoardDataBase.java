package ru.alex9127.app.database;

import android.content.*;
import android.database.Cursor;
import android.database.sqlite.*;

import androidx.annotation.NonNull;

import java.util.ArrayList;

public class LeaderBoardDataBase {
    public static final String DATABASE_NAME = "leaderboards.db";
    public static final int DATABASE_VERSION = 1;
    public static final String TABLE_NAME = "stats";

    public static final String COLUMN_ID = "id";
    public static final String COLUMN_NICKNAME = "name";
    public static final String COLUMN_FLOORS_CLEARED = "floorsCleared";
    public static final String COLUMN_UNIT_LEVEL = "unitLevel";
    public static final String COLUMN_ENEMIES_KILLED = "enemiesKilled";

    public static final int NUM_COLUMN_ID = 0;
    public static final int NUM_COLUMN_NICKNAME = 1;
    public static final int NUM_COLUMN_FLOORS_CLEARED = 2;
    public static final int NUM_COLUMN_UNIT_LEVEL = 3;
    public static final int NUM_COLUMN_ENEMIES_KILLED = 4;

    private final SQLiteDatabase database;

    private static class DatabaseOpener extends SQLiteOpenHelper {
        DatabaseOpener(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            String query = "CREATE TABLE IF NOT EXISTS " + TABLE_NAME + " (" +
                    COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COLUMN_NICKNAME + " TEXT, " +
                    COLUMN_FLOORS_CLEARED + " INTEGER, " +
                    COLUMN_UNIT_LEVEL + " INTEGER, "+
                    COLUMN_ENEMIES_KILLED + " INTEGER);";
            db.execSQL(query);
            db.close();
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        }
    }

    public static class Item {
        long id;
        String nickname;
        int floorsCleared;
        int unitLevel;
        int enemiesKilled;

        Item(long id, String n, int f, int l, int e) {
            this.id = id;
            nickname = n;
            floorsCleared = f;
            unitLevel = l;
            enemiesKilled = e;
        }

        @NonNull
        @Override
        public String toString() {
            return id + "|" + nickname + "|" + floorsCleared + "|" + unitLevel + "|" + enemiesKilled + "\n";
        }
    }

    public LeaderBoardDataBase(Context context) {
        DatabaseOpener opener = new DatabaseOpener(context);
        database = opener.getWritableDatabase();
    }

    public long insert(String name, int floors, int level, int enemies) {
        ContentValues cv = new ContentValues();
        cv.put(COLUMN_NICKNAME, name);
        cv.put(COLUMN_FLOORS_CLEARED, floors);
        cv.put(COLUMN_UNIT_LEVEL, level);
        cv.put(COLUMN_ENEMIES_KILLED, enemies);
        return database.insert(TABLE_NAME, null, cv);
    }

    public Item select(long id) {
        Cursor cursor = database.query(TABLE_NAME, null, COLUMN_ID + " = ?", new String[]{String.valueOf(id)}, null, null, null);
        boolean found = cursor.moveToFirst();
        if (!found) return null;
        String nickname = cursor.getString(NUM_COLUMN_NICKNAME);
        int floorsCleared = cursor.getInt(NUM_COLUMN_FLOORS_CLEARED);
        int unitLevel = cursor.getInt(NUM_COLUMN_UNIT_LEVEL);
        int enemiesKilled = cursor.getInt(NUM_COLUMN_ENEMIES_KILLED);
        cursor.close();
        return new Item(id, nickname, floorsCleared, unitLevel, enemiesKilled);
    }

    public ArrayList<Item> selectAll() {
        Cursor cursor = database.query(TABLE_NAME, null, null, null, null, null, null);

        ArrayList<Item> arr = new ArrayList<Item>();
        cursor.moveToFirst();
        if (!cursor.isAfterLast()) {
            do {
                long id = cursor.getLong(NUM_COLUMN_ID);
                String name = cursor.getString(NUM_COLUMN_NICKNAME);
                int floors = cursor.getInt(NUM_COLUMN_FLOORS_CLEARED);
                int level = cursor.getInt(NUM_COLUMN_UNIT_LEVEL);
                int enemies = cursor.getInt(NUM_COLUMN_ENEMIES_KILLED);
                arr.add(new Item(id, name, floors, level, enemies));
            } while (cursor.moveToNext());
        }
        cursor.close();
        return arr;
    }
}
