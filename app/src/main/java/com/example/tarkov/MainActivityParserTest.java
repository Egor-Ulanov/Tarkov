package com.example.tarkov;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivityParserTest extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.test_activity);

//        new FetchNewsTask().execute("https://www.escapefromtarkov.com/");
    }

//    private class FetchNewsTask extends AsyncTask<String, Void, List<ParserForNews.NewsItem>> {
//        @Override
//        protected List<ParserForNews.NewsItem> doInBackground(String... urls) {
//            if (urls.length < 1 || urls[0] == null) {
//                return null;
//            }
//            return ParserForNews.parseNews(urls[0]);
//        }
//
//        @Override
//        protected void onPostExecute(List<ParserForNews.NewsItem> newsItems) {
//            super.onPostExecute(newsItems);
//            if (newsItems != null && !newsItems.isEmpty()) {
//                for (ParserForNews.NewsItem item : newsItems) {
//                    Log.i("NewsParser", item.toString());
//                }
//            }
//        }
//    }
}