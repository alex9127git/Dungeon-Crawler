package ru.alex9127.app.database;

import android.content.*;
import android.database.Cursor;
import android.database.sqlite.*;

import androidx.annotation.NonNull;

import java.util.ArrayList;

public class TerrainDataBase {
    public static final String DATABASE_NAME = "saves.db";
    public static final int DATABASE_VERSION = 1;
    public static final String TABLE_NAME = "terrain";

    public static final String COLUMN_ID = "id";
    public static final String COLUMN_TERRAIN_DATA = "terrainData";
    public static final String COLUMN_TERRAIN_PATH = "terrainPath";

    public static final int NUM_COLUMN_ID = 0;
    public static final int NUM_COLUMN_TERRAIN_DATA = 1;
    public static final int NUM_COLUMN_TERRAIN_PATH = 2;

    private final SQLiteDatabase database;

    private static class DatabaseOpener extends SQLiteOpenHelper {
        DatabaseOpener(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            String query = "CREATE TABLE IF NOT EXISTS " + TABLE_NAME + " (" +
                    COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COLUMN_TERRAIN_DATA + " TEXT, " +
                    COLUMN_TERRAIN_PATH + " TEXT);";
            db.execSQL(query);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        }
    }

    public static class Item {
        final long id;
        final String terrainData;
        final int terrainPath;

        Item(long id, String t, int p) {
            this.id = id;
            terrainData = t;
            terrainPath = p;
        }

        @NonNull
        @Override
        public String toString() {
            return id + "|" + terrainData + "|" + terrainPath + "\n";
        }
    }

    public TerrainDataBase(Context context) {
        DatabaseOpener opener = new DatabaseOpener(context);
        database = opener.getWritableDatabase();
    }

    public long insert(String terrainData, int terrainPath) {
        ContentValues cv = new ContentValues();
        cv.put(COLUMN_TERRAIN_DATA, terrainData);
        cv.put(COLUMN_TERRAIN_PATH, terrainPath);
        return database.insert(TABLE_NAME, null, cv);
    }

    public Item select(long id) {
        Cursor cursor = database.query(TABLE_NAME, null, COLUMN_ID + " = ?", new String[]{String.valueOf(id)}, null, null, null);
        boolean found = cursor.moveToFirst();
        if (!found) return null;
        String terrainData = cursor.getString(NUM_COLUMN_TERRAIN_DATA);
        int terrainPath = cursor.getInt(NUM_COLUMN_TERRAIN_PATH);
        cursor.close();
        return new Item(id, terrainData, terrainPath);
    }

    public ArrayList<Item> selectAll() {
        Cursor cursor = database.query(TABLE_NAME, null, null, null, null, null, null);
        ArrayList<Item> arr = new ArrayList<>();
        cursor.moveToFirst();
        if (!cursor.isAfterLast()) {
            do {
                long id = cursor.getLong(NUM_COLUMN_ID);
                String terrainData = cursor.getString(NUM_COLUMN_TERRAIN_DATA);
                int terrainPath = cursor.getInt(NUM_COLUMN_TERRAIN_PATH);
                arr.add(new Item(id, terrainData, terrainPath));
            } while (cursor.moveToNext());
        }
        cursor.close();
        return arr;
    }
}
