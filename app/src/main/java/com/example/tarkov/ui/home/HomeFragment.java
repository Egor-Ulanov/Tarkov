package com.example.tarkov.ui.home;

import android.content.Context;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;
import com.example.tarkov.R;
import com.example.tarkov.databinding.FragmentHomeBinding;
import com.example.tarkov.ui.Parser.ParserCookie.NewsViewModel;
import com.example.tarkov.ui.Parser.ParserNewsList;
import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;
    private ViewPager viewPager;
    private ImageSliderAdapter sliderAdapter;
    private LinearLayout sliderIndicator;
    private RecyclerView recyclerView;
    private static NewsAdapter newsAdapter;
    private ProgressBar progressBar;
    private static final int WRITE_EXTERNAL_STORAGE_REQUEST_CODE = 1;
    private NewsViewModel newsViewModel;
    private ParserTask parserTask;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
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

        List<ParserNewsList.NewsItem> newsList = new ArrayList<>();
        newsAdapter.setNewsList(newsList);

        // Инициализация ViewModel
        newsViewModel = new ViewModelProvider(requireActivity()).get(NewsViewModel.class);

        // Наблюдатель за изменением данных в ViewModel
        newsViewModel.getNewsListLiveData().observe(getViewLifecycleOwner(), new Observer<List<ParserNewsList.NewsItem>>() {
            @Override
            public void onChanged(List<ParserNewsList.NewsItem> newsItems) {
                if (newsItems != null) {
                    newsAdapter.setNewsList(newsItems);
                }
            }
        });

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

        // Если данные уже загружены, обновите RecyclerView
        List<ParserNewsList.NewsItem> cachedNews = newsViewModel.getNewsListLiveData().getValue();
        if (cachedNews != null && !cachedNews.isEmpty()) {
            newsAdapter.setNewsList(cachedNews);
        } else {
            // Иначе, если данные еще не загружены, выполните парсинг
            parserTask = new ParserTask();
            parserTask.execute(getContext());
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        // Остановите выполнение парсинга при остановке фрагмента
        if (parserTask != null && !parserTask.isCancelled()) {
            parserTask.cancel(true);
        }
    }

    public class ParserTask extends AsyncTask<Context, Void, List<ParserNewsList.NewsItem>> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Показываем ProgressBar перед началом парсинга
            progressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected List<ParserNewsList.NewsItem> doInBackground(Context... contexts) {
            if (contexts != null && contexts.length > 0) {
                return ParserNewsList.parseEftNews(contexts[0]);
            }
            return null;
        }

        @Override
        protected void onPostExecute(List<ParserNewsList.NewsItem> newsItems) {
            super.onPostExecute(newsItems);
            if (newsItems != null) {
                // Сохранение данных в ViewModel
                newsViewModel.setNewsList(newsItems);
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
                // Если разрешение на запись предоставлено, выполните парсинг
                ParserTask parserTask = new ParserTask();
                parserTask.execute(getContext());
            } else {
                // Обработка случая, когда разрешение не предоставлено
            }
        }
    }
}
