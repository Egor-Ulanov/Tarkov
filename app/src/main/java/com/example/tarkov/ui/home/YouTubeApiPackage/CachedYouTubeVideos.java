    package com.example.tarkov.ui.home.YouTubeApiPackage;

    import static com.example.tarkov.ui.home.YouTubeApiPackage.YouTubeApiClient.fetchLatestVideosAsync;

    import android.annotation.SuppressLint;
    import android.content.Context;
    import android.database.Cursor;
    import android.util.Log;

    import com.example.tarkov.ui.home.DataBaseHelper.VideosDatabaseHelper;
    import com.google.api.services.youtube.model.ResourceId;
    import com.google.api.services.youtube.model.SearchResult;
    import com.google.api.services.youtube.model.SearchResultSnippet;

    import java.util.ArrayList;
    import java.util.List;



    public class CachedYouTubeVideos {
        public static final long EXPIRATION_TIME_IN_MILLISECONDS = 86400000; // 24 часа

        private static List<SearchResult> cachedVideos;
        private static long lastFetchedTimestamp;

        public static List<SearchResult> getCachedVideos(Context context) {
            VideosDatabaseHelper databaseHelper = new VideosDatabaseHelper(context);
            Cursor cursor = databaseHelper.getVideos();
            List<SearchResult> results = new ArrayList<>();

            if (cursor != null && cursor.moveToFirst()) {
                do {
                    @SuppressLint("Range") String videoId = cursor.getString(cursor.getColumnIndex(VideosDatabaseHelper.COLUMN_VIDEO_IDENTIFIER));
                    @SuppressLint("Range") String title = cursor.getString(cursor.getColumnIndex(VideosDatabaseHelper.COLUMN_TITLE));

                    SearchResult searchResult = new SearchResult();
                    SearchResultSnippet snippet = new SearchResultSnippet();
                    snippet.setTitle(title);


                    ResourceId resourceId = new ResourceId();
                    resourceId.setVideoId(videoId);

                    searchResult.setId(resourceId);

                    searchResult.setSnippet(snippet);

                    results.add(searchResult);
                } while (cursor.moveToNext());
                cursor.close();
            }

            return results;
        }



        public static void setCachedVideos(List<SearchResult> videos) {
            cachedVideos = videos;
            lastFetchedTimestamp = System.currentTimeMillis();
        }

        public static boolean isExpired(Context context) {
            Log.d("CachedYouTubeVideos", "Checking if cache is expired");
            VideosDatabaseHelper databaseHelper = new VideosDatabaseHelper(context);
            String lastFetchTimeStr = databaseHelper.getLastRequestTime();
            long lastFetchTime = lastFetchTimeStr != null ? Long.parseLong(lastFetchTimeStr) : 0;
            boolean isExpired = System.currentTimeMillis() - lastFetchTime > EXPIRATION_TIME_IN_MILLISECONDS;
            Log.d("CachedYouTubeVideos", "Cache expired: " + isExpired);
            return isExpired;
        }





        public static void fetchVideos(Context context, YouTubeApiClient.OnVideosFetchedListener listener) {
            List<SearchResult> cachedVideos = getCachedVideos(context);
            if (isExpired(context) || cachedVideos == null || cachedVideos.isEmpty()) {
                Log.d("CachedYouTubeVideos", "Cache expired or empty, fetching new videos");
                fetchLatestVideosAsync(context, listener);
            } else {
                Log.d("CachedYouTubeVideos", "Using cached videos");
                listener.onVideosFetched(cachedVideos);
            }
        }


    }




