package com.example.tarkov.ui.home;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.util.Log;
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

    public interface OnPageLoadListener {
        void onPageLoad(int position);
    }

    private OnPageLoadListener onPageLoadListener;

    private HomeFragment fragment;
    private List<SearchResult> videos;
    private boolean showErrorLayout = false; // Флаг для отображения ошибки

    public ImageSliderAdapter(HomeFragment fragment, List<SearchResult> videos, OnPageLoadListener onPageLoadListener) {
        this.fragment = fragment;
        this.videos = videos;
        this.onPageLoadListener = onPageLoadListener;
    }

    public void setShowErrorLayout(boolean showError) {
        this.showErrorLayout = showError;
        notifyDataSetChanged();
    }

    public boolean isShowErrorLayout() {
        return showErrorLayout;
    }

    @Override
    public int getCount() {
        return videos != null ? videos.size() : 0;
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == object;
    }



    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {

        LayoutInflater inflater = (LayoutInflater) fragment.getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        if (showErrorLayout) {
            View errorView = inflater.inflate(R.layout.layout_no_internet, container, false);
            container.addView(errorView);
            return errorView;
        } else {
            View itemView = inflater.inflate(R.layout.video_item, container, false);
            WebView youtubeWebView = itemView.findViewById(R.id.youtubeWebView);



            // Настройка WebView
            WebSettings webSettings = youtubeWebView.getSettings();
            webSettings.setJavaScriptEnabled(true); // Включаем JavaScript
            webSettings.setDomStorageEnabled(true); // Включаем DOM storage API
            webSettings.setJavaScriptEnabled(true); // Включаем JavaScript

            webSettings.setCacheMode(WebSettings.LOAD_DEFAULT); // Включаем кэширование

            String videoId = videos.get(position).getId().getVideoId();
            String iframeCode = "<html><body><iframe width=\"100%\" height=\"100%\" src=\"https://www.youtube.com/embed/" + videoId + "\" frameborder=\"0\" allowfullscreen></iframe></body></html>";
            youtubeWebView.loadData(iframeCode, "text/html", "utf-8");



            youtubeWebView.setWebViewClient(new WebViewClient() {
                @Override
                public boolean shouldOverrideUrlLoading(WebView view, String url) {
                    if (url != null && (url.startsWith("https://www.youtube.com/") || url.startsWith("https://youtu.be/"))) {
                        // Запуск YouTube приложения для просмотра видео
                        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                        view.getContext().startActivity(intent);
                        return true;
                    }
                    // Для всех остальных ссылок используется обычное поведение WebView
                    return false;
                }

                @Override
                public void onPageStarted(WebView view, String url, Bitmap favicon) {
                    super.onPageStarted(view, url, favicon);
                    Log.d("WebView", "Page started loading: " + url);
                    // Включаем ProgressBar здесь
                }

                @Override
                public void onPageFinished(WebView view, String url) {
                    Log.d("WebView", "Page finished loading: " + url);
                    super.onPageFinished(view, url);
                }

            });

            container.addView(itemView);
            return itemView;
        }
    }





    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((View) object);
    }



}
