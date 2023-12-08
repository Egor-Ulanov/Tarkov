package com.example.tarkov.ui.home;

import android.Manifest;
import android.content.pm.PackageManager;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import com.example.tarkov.R;
import com.example.tarkov.databinding.FragmentHomeBinding;
import com.example.tarkov.ui.Parser.ParserFix;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;

    private ViewPager viewPager;
    private ImageSliderAdapter sliderAdapter;
    private LinearLayout sliderIndicator;
    private RecyclerView recyclerView;
    private static NewsAdapter newsAdapter;

    private ParserTask parserTask;
    private static ProgressBar progressBar;

    private static final int WRITE_EXTERNAL_STORAGE_REQUEST_CODE = 1;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        HomeViewModel homeViewModel =
                new ViewModelProvider(this).get(HomeViewModel.class);

        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        progressBar = root.findViewById(R.id.progressBar); // Инициализация ProgressBar

        // Инициализация остальных элементов фрагмента
        viewPager = root.findViewById(R.id.viewPager);
        sliderAdapter = new ImageSliderAdapter(requireContext());
        viewPager.setAdapter(sliderAdapter);

        sliderIndicator = root.findViewById(R.id.sliderIndicator);
        setupSlideIndicator();

        recyclerView = root.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        newsAdapter = new NewsAdapter();
        recyclerView.setAdapter(newsAdapter);

        List<ParserFix.NewsItem> newsList = new ArrayList<>();
        newsAdapter.setNewsList(newsList);

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                sliderAdapter.updateSlideIndicator(sliderIndicator, position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });

        return root;
    }

    @Override
    public void onStart() {
        super.onStart();
        parserTask = new ParserTask();
        parserTask.execute(getContext());
    }

    @Override
    public void onStop() {
        super.onStop();
        if (parserTask != null && !parserTask.isCancelled()) {
            parserTask.cancel(true);
        }
    }

    public static class ParserTask extends AsyncTask<Context, Void, List<ParserFix.NewsItem>> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Показываем ProgressBar перед началом парсинга
            progressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected List<ParserFix.NewsItem> doInBackground(Context... contexts) {
            if (contexts != null && contexts.length > 0) {
                return ParserFix.parseEftNews(contexts[0]);
            }
            return null;
        }

        @Override
        protected void onPostExecute(List<ParserFix.NewsItem> newsItems) {
            super.onPostExecute(newsItems);
            if (newsItems != null) {
                newsAdapter.setNewsList(newsItems);
            }
            // Скрываем ProgressBar после завершения парсинга
            progressBar.setVisibility(View.GONE);
        }
    }

    private void setupSlideIndicator() {
        for (int i = 0; i < sliderAdapter.getCount(); i++) {
            ImageView indicator = new ImageView(requireContext());
            indicator.setImageResource(i == 0 ? R.drawable.truedot1 : R.drawable.truedot2);

            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            );
            layoutParams.setMargins(8, 8, 8, 0);
            sliderIndicator.addView(indicator, layoutParams);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == WRITE_EXTERNAL_STORAGE_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                parserTask = new ParserTask();
                parserTask.execute(getContext());
            } else {
                // Обработка случая, когда разрешение не предоставлено
            }
        }
    }
}
