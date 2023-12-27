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

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;



public class VideosDatabaseHelper extends SQLiteOpenHelper {
    private static final String DB_NAME = "tarkov.db";
    private static final int DB_VERSION = 5;
    private final Context context;

    // Названия таблиц и столбцов
    private static final String TABLE_REQUEST_TIME = "request_time_manager";
    private static final String COLUMN_REQUEST_TIME = "request_time";

    private static final String TABLE_VIDEOS = "videos";
    private static final String COLUMN_VIDEO_ID = "id";
    public static final String COLUMN_VIDEO_IDENTIFIER = "video_identificator";
    public static final String COLUMN_TITLE = "title";
    private static final String COLUMN_PUBLICATION_DATE = "publication_date";

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



//    @Override
//    public void onCreate(SQLiteDatabase db) {
//        // Создание таблиц в базе данных
//        db.execSQL("CREATE TABLE " + TABLE_REQUEST_TIME + " (" +
//                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
//                "request_time TEXT)");
//        db.execSQL("CREATE TABLE " + TABLE_VIDEOS + " (" +
//                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
//                "video_identificator TEXT," +
//                "title TEXT," +
//                "publication_date TEXT)");
//    }


    @Override
    public void onCreate(SQLiteDatabase db) {
//        // Создание таблицы для времени запросов
//        db.execSQL("CREATE TABLE IF NOT EXISTS " + TABLE_REQUEST_TIME + " (" +
//                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
//                COLUMN_REQUEST_TIME + " TEXT)");
//
//        // Создание таблицы для видео
//        db.execSQL("CREATE TABLE IF NOT EXISTS " + TABLE_VIDEOS + " (" +
//                COLUMN_VIDEO_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
//                COLUMN_VIDEO_IDENTIFIER + " TEXT," +
//                COLUMN_TITLE + " TEXT," +
//                COLUMN_PUBLICATION_DATE + " TEXT)");
    }


    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
//        if (oldVersion < 2) {
//            db.execSQL("ALTER TABLE " + TABLE_NAME + " ADD COLUMN " + COLUMN_LAST_FETCH_TIME + " INTEGER DEFAULT 0");
//        }
    }


    // Добавление записи о видео в базу данных
    public void addVideo(String videoIdentificator, String title, String publicationDate) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_VIDEO_IDENTIFIER, videoIdentificator);
        values.put(COLUMN_TITLE, title);
        values.put(COLUMN_PUBLICATION_DATE, publicationDate); // Добавляем дату публикации
        db.insert(TABLE_VIDEOS, null, values);
        db.close();
    }




    // Получение информации о видеороликах из базы данных
    public Cursor getVideos() {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.query(TABLE_VIDEOS, null, null, null, null, null, null);
    }

    // Метод для форматирования текущего времени в читаемый формат
    public static String getCurrentFormattedTime() {
        SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss", Locale.getDefault());
        return sdf.format(new Date());
    }

    // Метод для сохранения времени запроса в базу данных
    public void addRequestTime(long currentTimeMillis) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        currentTimeMillis = System.currentTimeMillis();
        values.put(COLUMN_REQUEST_TIME, currentTimeMillis);
        db.insert(TABLE_REQUEST_TIME, null, values);
        db.close();
    }


    // Получение последнего времени запроса к API
    public String getLastRequestTime() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_REQUEST_TIME, new String[]{COLUMN_REQUEST_TIME}, null, null, null, null, "id DESC", "1");
        if (cursor != null && cursor.moveToFirst()) {
            @SuppressLint("Range") String lastRequestTime = cursor.getString(cursor.getColumnIndex(COLUMN_REQUEST_TIME));
            cursor.close();
            return lastRequestTime;
        }
        return null;
    }

    public void clearVideos() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_VIDEOS, null, null);
        db.close();
    }





}