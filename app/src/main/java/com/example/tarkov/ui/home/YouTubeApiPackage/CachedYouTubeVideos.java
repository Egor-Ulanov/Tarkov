    package com.example.tarkov.ui.home.YouTubeApiPackage;

    import android.content.Context;

    import com.google.api.services.youtube.model.SearchResult;

    import java.util.List;



    public class CachedYouTubeVideos {
        public static final long EXPIRATION_TIME_IN_MILLISECONDS = 86400000; // 24 часа

        private static List<SearchResult> cachedVideos;
        private static long lastFetchedTimestamp = 0;

        public static List<SearchResult> getCachedVideos() {
            return cachedVideos;
        }

        public static void setCachedVideos(List<SearchResult> videos) {
            cachedVideos = videos;
            lastFetchedTimestamp = System.currentTimeMillis();
        }

        public static boolean isExpired() {
            return System.currentTimeMillis() - lastFetchedTimestamp > EXPIRATION_TIME_IN_MILLISECONDS;
        }

        public static List<SearchResult> fetchVideos(Context context, YouTubeApiClient.OnVideosFetchedListener listener) {
            // Проверяем, не истекло ли время действия кэша
            if (isExpired()) {
                // Кэш устарел, делаем запрос к API
                return YouTubeApiClient.fetchLatestVideos(context, listener);
            } else {
                // Кэш действителен, возвращаем кэшированные видео
                return getCachedVideos();
            }
        }
    }