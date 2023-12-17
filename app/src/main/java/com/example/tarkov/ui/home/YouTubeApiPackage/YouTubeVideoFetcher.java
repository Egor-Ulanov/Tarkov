package com.example.tarkov.ui.home.YouTubeApiPackage;

import android.os.AsyncTask;

import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.model.SearchListResponse;
import com.google.api.services.youtube.model.SearchResult;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class YouTubeVideoFetcher extends AsyncTask<Void, Void, List<SearchResult>> {

    private static final String API_KEY = "ВАШ_КЛЮЧ_API"; // Замените на свой ключ API

    @Override
    protected List<SearchResult> doInBackground(Void... voids) {
        List<SearchResult> videoList = new ArrayList<>();

        try {
            YouTube youtube = new YouTube.Builder(new NetHttpTransport(), new JacksonFactory(), null)
                    .setApplicationName("YourAppName")
                    .build();

            YouTube.Search.List search = youtube.search().list("id,snippet");
            search.setKey(API_KEY);
            search.setChannelId("UCj1J76BVRWb1G8RrVLh7T3w"); // ID канала @Battlestategames
            search.setMaxResults(5L); // Получить последние 5 видеороликов
            search.setType("video");

            SearchListResponse searchResponse = search.execute();
            videoList = searchResponse.getItems();
        } catch (GoogleJsonResponseException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return videoList;
    }
}