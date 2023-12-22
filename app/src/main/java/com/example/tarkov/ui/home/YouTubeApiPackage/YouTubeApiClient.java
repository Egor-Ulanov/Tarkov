package com.example.tarkov.ui.home.YouTubeApiPackage;

import static com.example.tarkov.ui.home.YouTubeApiPackage.CachedYouTubeVideos.EXPIRATION_TIME_IN_MILLISECONDS;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.Cursor;
import android.os.AsyncTask;
import android.util.Log;

import com.example.tarkov.R;
import com.example.tarkov.ui.home.DataBaseHelper.VideosDatabaseHelper;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.model.SearchListResponse;
import com.google.api.services.youtube.model.SearchResult;
import com.google.api.services.youtube.model.SearchResultSnippet;
import com.google.api.services.youtube.model.ResourceId;


import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class YouTubeApiClient {

    private static final String TAG = "YouTubeApiClient";
    private static final String API_KEY = "AIzaSyD8WMuLtNjdTHv9KwL4W3AdyhKb_AIjeKg"; // Замените на свой API ключ
    private static VideosDatabaseHelper databaseHelper;

    public interface OnVideosFetchedListener {
        void onVideosFetched(List<SearchResult> videos);
    }

    public static List<SearchResult> fetchLatestVideos(Context context, OnVideosFetchedListener listener) {
        if (databaseHelper == null) {
            databaseHelper = new VideosDatabaseHelper(context);
        }

        long lastFetchTime = databaseHelper.getLastFetchTime();
        long currentTime = System.currentTimeMillis();

        if (currentTime - lastFetchTime > EXPIRATION_TIME_IN_MILLISECONDS) {
            new FetchVideosTask(context, listener).execute();
        } else {
            List<SearchResult> cachedVideos = getCachedVideosFromDatabase(context);
            if (listener != null) {
                listener.onVideosFetched(cachedVideos);
            }
        }

        return null;
    }




    public static void fetchLatestVideosAsync(Context context, OnVideosFetchedListener listener) {
        Log.d(TAG, "fetchLatestVideosAsync called");
        new FetchVideosTask(context, listener).execute();
    }

    private static class FetchVideosTask extends AsyncTask<Void, Void, List<SearchResult>> {

        private final Context context;
        private final OnVideosFetchedListener listener;

        private final VideosDatabaseHelper databaseHelper;

        public FetchVideosTask(Context context, OnVideosFetchedListener listener) {
            this.context = context;
            this.listener = listener;
            this.databaseHelper = new VideosDatabaseHelper(context);
        }

        @Override
        protected List<SearchResult> doInBackground(Void... voids) {
            try {
                Log.d(TAG, "Fetching latest videos...");

                final JsonFactory jsonFactory = JacksonFactory.getDefaultInstance();
                final YouTube youtube = new YouTube.Builder(GoogleNetHttpTransport.newTrustedTransport(), jsonFactory, null)
                        .setApplicationName(context.getString(R.string.app_name))
                        .build();

                YouTube.Search.List search = youtube.search().list(Collections.singletonList("id,snippet"));
                search.setKey(API_KEY);
                search.setChannelId("UC5QGploHhl9_XaxDiHZKamg");
                search.setType(Collections.singletonList("video"));
                search.setOrder("date");
                search.setMaxResults(5L);

                SearchListResponse searchResponse = search.execute();
                List<SearchResult> searchResults = searchResponse.getItems();

                Log.d(TAG, "Fetched " + (searchResults != null ? searchResults.size() : 0) + " videos.");

                return searchResults;
            } catch (IOException | GeneralSecurityException e) {
                Log.e(TAG, "Error fetching videos: " + e.getMessage());
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(List<SearchResult> searchResults) {
            super.onPostExecute(searchResults);
            if (searchResults != null) {
                saveVideosToDatabase(searchResults);
                databaseHelper.setLastFetchTime(System.currentTimeMillis());
            }
            if (listener != null) {
                listener.onVideosFetched(searchResults);
            }
        }


        private void saveVideosToDatabase(List<SearchResult> videos) {
            for (SearchResult video : videos) {
                String videoId = video.getId().getVideoId();
                String title = video.getSnippet().getTitle();
                long timestamp = System.currentTimeMillis();

                databaseHelper.addVideo(videoId,
                        title,
                        timestamp);
            }
        }
    }

    private static List<SearchResult> getCachedVideosFromDatabase(Context context) {
        VideosDatabaseHelper databaseHelper = new VideosDatabaseHelper(context);
        List<SearchResult> cachedVideos = new ArrayList<>();

        Cursor cursor = databaseHelper.getVideos();
        if (cursor.moveToFirst()) {
            do {
                @SuppressLint("Range") String videoId = cursor.getString(cursor.getColumnIndex("video_id"));
                @SuppressLint("Range") String title = cursor.getString(cursor.getColumnIndex("title"));

                SearchResult searchResult = new SearchResult();
                SearchResultSnippet snippet = new SearchResultSnippet();
                snippet.setTitle(title);

                // Создать новый объект ResourceId для идентификатора видео
                ResourceId resourceId = new ResourceId();
                resourceId.setVideoId(videoId);

                searchResult.setId(resourceId);
                searchResult.setSnippet(snippet);

                cachedVideos.add(searchResult);
            } while (cursor.moveToNext());
        }

        cursor.close();
        return cachedVideos;
    }

}