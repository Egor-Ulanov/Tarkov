package com.example.tarkov.ui.home;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

import com.example.tarkov.R;
import com.google.api.services.youtube.model.SearchResult;

import java.util.List;

public class ImageSliderAdapter extends PagerAdapter {

    private Context context;
    private List<SearchResult> videos;

    public ImageSliderAdapter(Context context, List<SearchResult> videos) {
        this.context = context;
        this.videos = videos;
    }

    @Override
    public int getCount() {
        return videos.size();
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == object;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View itemView = inflater.inflate(R.layout.video_item, container, false);

        WebView youtubeWebView = itemView.findViewById(R.id.youtubeWebView);

        // Получите videoId
        String videoId = videos.get(position).getId().getVideoId();

        // Создайте iframe-код для встраивания видеоплеера YouTube
        String iframeCode = "<html><body><iframe width=\"100%\" height=\"100%\" src=\"https://www.youtube.com/embed/"
                + videoId + "\" frameborder=\"0\" allowfullscreen></iframe></body></html>";

        // Настройте WebView для отображения iframe-кода
        WebSettings webSettings = youtubeWebView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        youtubeWebView.loadData(iframeCode, "text/html", "utf-8");

        youtubeWebView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                // Обработка нажатия на видеоплеер (открывается в приложении YouTube)
                return false;
            }
        });

        container.addView(itemView);

        return itemView;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((View) object);
    }
}