package com.example.tarkov.ui.home;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
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
import com.example.tarkov.ui.home.YouTubeApiPackage.CachedYouTubeVideos;
import com.example.tarkov.ui.home.YouTubeApiPackage.YouTubeApiClient;
import com.google.api.services.youtube.model.SearchResult;

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

    private List<SearchResult> latestVideos = new ArrayList<>();

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        Log.d("HomeFragment", "onCreateView");
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        progressBar = root.findViewById(R.id.progressBar); // Инициализация ProgressBar

        viewPager = root.findViewById(R.id.viewPager);
        sliderAdapter = new ImageSliderAdapter(requireContext(), latestVideos);
        viewPager.setAdapter(sliderAdapter);

        sliderIndicator = root.findViewById(R.id.sliderIndicator);
        setupSlideIndicator(sliderIndicator);

        recyclerView = root.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        newsAdapter = new NewsAdapter();
        recyclerView.setAdapter(newsAdapter);


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
                // Ignore scrolling events
            }

            @Override
            public void onPageSelected(int position) {
                // Update the slide indicator
                updateSlideIndicator(sliderIndicator, viewPager);
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                // Ignore scrolling state change events
            }
        });

        // Проверка и загрузка данных из YouTube API
        if (CachedYouTubeVideos.isExpired() || CachedYouTubeVideos.getCachedVideos() == null) {
            loadYouTubeData();
        } else {
            // Используем кэшированные данные
            processFetchedVideos(CachedYouTubeVideos.getCachedVideos());
        }


        // Загрузка последних 5 видео с канала Battlestategames
        YouTubeApiClient.fetchLatestVideos(requireContext(), new YouTubeApiClient.OnVideosFetchedListener() {
            @Override
            public void onVideosFetched(List<SearchResult> videos) {
                // Фильтровать только первые 5 видео
                List<SearchResult> filteredVideos = new ArrayList<>();
                for (int i = 0; i < 5; i++) {
                    if (i < videos.size()) {
                        filteredVideos.add(videos.get(i));
                    }
                }

                latestVideos.clear();
                latestVideos.addAll(filteredVideos);
                sliderAdapter.notifyDataSetChanged();
            }
        });

        return root;
    }



    private void setupSlideIndicator(LinearLayout sliderIndicator) {
        sliderIndicator.removeAllViews(); // Удалить все дочерние элементы

        if (latestVideos != null) {
            int count = latestVideos.size();
            if (count > 0) {
                for (int i = 0; i < count; i++) {
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
        } else {
            Log.e("HomeFragment", "latestVideos is null in setupSlideIndicator");
        }
    }



    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState)

    {
        super.onViewCreated(view, savedInstanceState);

        // Инициализация ProgressBar
        progressBar = view.findViewById(R.id.progressBar);

        // Инициализация ViewPager
        viewPager = view.findViewById(R.id.viewPager);
        sliderAdapter = new ImageSliderAdapter(requireContext(), latestVideos);
        viewPager.setAdapter(sliderAdapter);

        // Инициализация индикатора слайдера
        LinearLayout sliderIndicator = view.findViewById(R.id.sliderIndicator);
        setupSlideIndicator(sliderIndicator);
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

    private void updateSlideIndicator(LinearLayout sliderIndicator, ViewPager viewPager) {
        for (int i = 0; i < sliderIndicator.getChildCount(); i++) {
            ImageView indicator = (ImageView) sliderIndicator.getChildAt(i);
            indicator.setImageResource(i == viewPager.getCurrentItem() ? R.drawable.truedot1 : R.drawable.truedot2);
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

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
        Log.d("HomeFragment", "onDestroy");
    }

    private void loadYouTubeData() {
        YouTubeApiClient.fetchLatestVideosAsync(requireContext(), new YouTubeApiClient.OnVideosFetchedListener() {
            @Override
            public void onVideosFetched(List<SearchResult> videos) {
                // Обновляем кэш и UI
                CachedYouTubeVideos.setCachedVideos(videos);
                processFetchedVideos(videos);
            }
        });
    }


    private void processFetchedVideos(List<SearchResult> videos) {
        if (videos != null && !videos.isEmpty()) {
            latestVideos.clear();
            latestVideos.addAll(videos);
            sliderAdapter.notifyDataSetChanged();
            setupSlideIndicator(sliderIndicator);
        }
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        Log.d("HomeFragment", "onAttach");
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("HomeFragment", "onCreate");
    }
    @Override
    public void onDetach() {
        super.onDetach();
        Log.d("HomeFragment", "onDetach");
    }

}