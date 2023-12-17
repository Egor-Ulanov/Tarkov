package com.example.tarkov.ui.home.YouTubeApiPackage;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.example.tarkov.R;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.model.SearchListResponse;
import com.google.api.services.youtube.model.SearchResult;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.List;

public class YouTubeApiClient {

    private static final String TAG = "YouTubeApiClient";
    private static final String API_KEY = "AIzaSyD8WMuLtNjdTHv9KwL4W3AdyhKb_AIjeKg"; // Замените на свой API ключ

    public interface OnVideosFetchedListener {
        void onVideosFetched(List<SearchResult> videos);
    }

    public static void fetchLatestVideos(Context context, OnVideosFetchedListener listener) {
        new FetchVideosTask(context, listener).execute();
    }

    private static class FetchVideosTask extends AsyncTask<Void, Void, List<SearchResult>> {

        private final Context context;
        private final OnVideosFetchedListener listener;

        public FetchVideosTask(Context context, OnVideosFetchedListener listener) {
            this.context = context;
            this.listener = listener;
        }

        @Override
        protected List<SearchResult> doInBackground(Void... voids) {
            try {
                Log.d(TAG, "Fetching latest videos...");

                final JsonFactory jsonFactory = JacksonFactory.getDefaultInstance();
                final YouTube youtube = new YouTube.Builder(GoogleNetHttpTransport.newTrustedTransport(), jsonFactory, null)
                        .setApplicationName(context.getString(R.string.app_name))
                        .build();

                YouTube.Search.List search = youtube.search().list("id,snippet");
                search.setKey(API_KEY);
                search.setChannelId("UC5QGploHhl9_XaxDiHZKamg"); // Канал Battlestategames
                search.setType("video");
                search.setOrder("date");
                search.setMaxResults(5L); // Получить последние 5 видеороликов

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
            if (listener != null) {
                listener.onVideosFetched(searchResults);
            }
        }
    }
}
