package com.example.tarkov.ui.home.DataBaseHelper;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class VideosDatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "videos_database";
    private static final int DATABASE_VERSION = 2;

    private static final String TABLE_NAME = "videos";
    private static final String COLUMN_VIDEO_ID = "video_id";
    private static final String COLUMN_TITLE = "title";
    private static final String COLUMN_TIMESTAMP = "timestamp";
    private static final String COLUMN_LAST_FETCH_TIME = "last_fetch_time";

    public VideosDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTableQuery = "CREATE TABLE " + TABLE_NAME + " (" +
                COLUMN_VIDEO_ID + " TEXT PRIMARY KEY," +
                COLUMN_TITLE + " TEXT," +
                COLUMN_TIMESTAMP + " INTEGER," +
                COLUMN_LAST_FETCH_TIME + " INTEGER DEFAULT 0)";
        db.execSQL(createTableQuery);
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
        db.insert(TABLE_NAME, null, values);
        db.close();
    }

    // Получение информации о видеороликах из базы данных
    public Cursor getVideos() {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.query(TABLE_NAME, null, null, null, null, null, null);
    }

    // Установка времени последнего запроса к API
    public void setLastFetchTime(long timestamp) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_LAST_FETCH_TIME, timestamp);
        db.update(TABLE_NAME, values, null, null);
        db.close();
    }

    // Получение времени последнего запроса к API
    public long getLastFetchTime() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_NAME, new String[]{COLUMN_LAST_FETCH_TIME}, null, null, null, null, null);
        long lastFetchTime = 0;
        if (cursor != null && cursor.moveToFirst()) {
            lastFetchTime = cursor.getLong(cursor.getColumnIndex(COLUMN_LAST_FETCH_TIME));
            cursor.close();
        }
        return lastFetchTime;
    }
}
