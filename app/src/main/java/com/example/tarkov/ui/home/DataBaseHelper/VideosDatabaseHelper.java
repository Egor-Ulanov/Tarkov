package com.example.tarkov.ui.home.DataBaseHelper;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class VideosDatabaseHelper extends SQLiteOpenHelper {
    private static final String DB_NAME = "tarkov.db";
    private static final int DB_VERSION = 1; // Версия вашей базы данных

    // Названия таблицы и столбцов
    private static final String TABLE_NAME = "videos";
    private static final String COLUMN_VIDEO_ID = "video_id";
    private static final String COLUMN_TITLE = "title";
    private static final String COLUMN_TIMESTAMP = "timestamp";
    private static final String COLUMN_LAST_FETCH_TIME = "last_fetch_time";
    private final Context context;

    public VideosDatabaseHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
        this.context = context;
        copyDataBase();
    }

    // Метод для копирования базы данных из assets
    private void copyDataBase() {
        File dbFile = context.getDatabasePath(DB_NAME);
        if (dbFile.exists()) {
            Log.d("DatabaseHelper", "Database already exists");
            return;
        }

        try {
            InputStream myInput = context.getAssets().open(DB_NAME);
            OutputStream myOutput = new FileOutputStream(dbFile);

            byte[] buffer = new byte[1024];
            int length;
            while ((length = myInput.read(buffer)) > 0) {
                myOutput.write(buffer, 0, length);
            }

            myOutput.flush();
            myOutput.close();
            myInput.close();

            Log.d("DatabaseHelper", "Database copied successfully");
        } catch (IOException e) {
            Log.e("DatabaseHelper", "Error copying database", e);
        }
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
//        String createTableQuery = "CREATE TABLE " + TABLE_NAME + " (" +
//                COLUMN_VIDEO_ID + " TEXT PRIMARY KEY," +
//                COLUMN_TITLE + " TEXT," +
//                COLUMN_TIMESTAMP + " INTEGER," +
//                COLUMN_LAST_FETCH_TIME + " INTEGER DEFAULT 0)";
//        db.execSQL(createTableQuery);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion < 2) {
            db.execSQL("ALTER TABLE " + TABLE_NAME + " ADD COLUMN " + COLUMN_LAST_FETCH_TIME + " INTEGER DEFAULT 0");
        }
    }

    // Добавление видеоролика в базу данных
    public void addVideo(String videoId, String title, long timestamp) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_VIDEO_ID, videoId);
        values.put(COLUMN_TITLE, title);
        values.put(COLUMN_TIMESTAMP, timestamp);

        int updated = db.update(TABLE_NAME, values, COLUMN_VIDEO_ID + " = ?", new String[]{videoId});
        if (updated == 0) {
            long rowId = db.insert(TABLE_NAME, null, values);
            if (rowId == -1) {
                Log.e("DatabaseHelper", "Failed to insert video: " + videoId);
            } else {
                Log.d("DatabaseHelper", "Video inserted: " + videoId);
            }
        } else {
            Log.d("DatabaseHelper", "Video updated: " + videoId);
        }
        db.close();
    }



    // Получение информации о видеороликах из базы данных
    public Cursor getVideos() {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.query(TABLE_NAME, null, null, null, null, null, null);
    }

    // Установка времени последнего запроса к API
    public void setLastFetchTime(long timestamp) {
        Log.d("DatabaseHelper", "Setting last fetch time: " + timestamp);
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_LAST_FETCH_TIME, timestamp);
        db.update(TABLE_NAME, values, null, null);
        db.close();
    }

    // Получение времени последнего запроса к API
    @SuppressLint("Range")
    public long getLastFetchTime() {
        long lastFetchTime = 0;
        Log.d("DatabaseHelper", "Last fetch time: " + lastFetchTime);
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_NAME, new String[]{COLUMN_LAST_FETCH_TIME}, null, null, null, null, null);
        lastFetchTime = 0;
        if (cursor != null && cursor.moveToFirst()) {
            lastFetchTime = cursor.getLong(cursor.getColumnIndex(COLUMN_LAST_FETCH_TIME));
            Log.d("DatabaseHelper", "Last fetch time: " + lastFetchTime);
            cursor.close();
        }
        return lastFetchTime;
    }




}