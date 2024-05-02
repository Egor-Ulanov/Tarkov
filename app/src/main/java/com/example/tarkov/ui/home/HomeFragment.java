package com.example.tarkov.ui.home;

import static android.content.Context.MODE_PRIVATE;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
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
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.tarkov.R;
import com.example.tarkov.databinding.FragmentHomeBinding;
import com.example.tarkov.ui.Parser.ParserCookie.NewsViewModel;
import com.example.tarkov.ui.Parser.ParserNewsList;
import com.google.api.services.youtube.model.ResourceId;
import com.google.api.services.youtube.model.SearchResult;
import com.google.api.services.youtube.model.SearchResultSnippet;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

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

    private boolean isFetchingVideos = false; // Флаг для отслеживания состояния загрузки

    private static final String VIDEOS_URL = "http://213.171.14.43:8000/videos/"; // URL вашего сервера

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        Log.d("HomeFragment", "onCreateView");
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        progressBar = root.findViewById(R.id.progressBar); // Инициализация ProgressBar

        viewPager = root.findViewById(R.id.viewPager);

        // Инициализация ViewPagerAdapter
        sliderAdapter = new ImageSliderAdapter(this, latestVideos, new ImageSliderAdapter.OnPageLoadListener() {
            @Override
            public void onPageLoad(int position) {
                updateSlideIndicator(sliderIndicator, position);
            }
        });
        sliderAdapter.setShowErrorLayout(!isNetworkConnected());




//        // Инфляция макета для этого фрагмента
//        View view = inflater.inflate(R.layout.fragment_home, container, false);


        // Проверка состояния сети и установка состояния ошибки для адаптера
        if (!isNetworkConnected()) {
            sliderAdapter.setShowErrorLayout(true);
            Log.d("HomeFragment", "Нет интернета при загрузке");
        } else {
            Log.d("HomeFragment", "Загрузка данных, интернет есть");
            loadYouTubeData(); // Загрузка данных, если есть интернет
//            sliderIndicator = root.findViewById(R.id.sliderIndicator);
//            setupSlideIndicator(sliderIndicator); 3333
        }

        viewPager.setAdapter(sliderAdapter);
        viewPager.setOffscreenPageLimit(latestVideos.size());

//        BottomNavigationView navView = getActivity().findViewById(R.id.nav_view);
//        boolean isDarkTheme = false;
//        ThemeUtils.applyTheme(getActivity(), isDarkTheme, navView);

//        sliderIndicator = root.findViewById(R.id.sliderIndicator);
//        setupSlideIndicator(sliderIndicator);

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

            }

            @Override
            public void onPageSelected(int position) {
                // Обновление индикатора слайдера при изменении выбранной страницы
                updateSlideIndicator(sliderIndicator, position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }

            // Игнорировать другие методы (onPageScrolled и onPageScrollStateChanged)
        });






        // Установка цвета фона в соответствии с темой
        updateBackgroundColor();

    return root;
    }

    private void updateBackgroundColor() {
        SharedPreferences prefs = getActivity().getSharedPreferences("AppThemePrefs", MODE_PRIVATE);
        boolean isDarkTheme = prefs.getBoolean("isDarkTheme", false);

        int backgroundColorResId = isDarkTheme ? R.color.dark_background : R.color.light_background;
        int backgroundColor = ContextCompat.getColor(requireContext(), backgroundColorResId);
        binding.getRoot().setBackgroundColor(backgroundColor);
    }



    private void setupSlideIndicator(LinearLayout sliderIndicator) {
        Log.d("HomeFragment", "Настройка индикатора слайдера");
        if (sliderIndicator == null) {
            Log.e("HomeFragment", "SliderIndicator is null");
            return;
        }

        if (!sliderAdapter.isShowErrorLayout() && latestVideos != null && !latestVideos.isEmpty()) {
            sliderIndicator.removeAllViews(); // Удалить все дочерние элементы

            if (latestVideos != null) {
                int count = latestVideos.size();
                if (count > 0) {
                    for (int i = 0; i < count; i++) {
                        ImageView indicator = new ImageView(requireContext());
                        indicator.setImageResource(i == 0 ? R.drawable.circle_fill : R.drawable.circle_fill2);

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
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Инициализация ProgressBar
        progressBar = view.findViewById(R.id.progressBar);

        // Инициализация ViewPager
        viewPager = view.findViewById(R.id.viewPager);

        // Установка адаптера с учетом состояния сети
        sliderAdapter = new ImageSliderAdapter(this, latestVideos, position -> {
            updateIndicators();
        });
        viewPager.setAdapter(sliderAdapter);

        // Инициализация индикатора слайдера
        sliderIndicator = view.findViewById(R.id.sliderIndicator);

        if (!isNetworkConnected()) {
            // Если нет интернета, установить флаг ошибки в адаптере
            sliderAdapter.setShowErrorLayout(true);
            // Нет необходимости инициализировать индикаторы, так как нет видео
        } else {
            // Если есть интернет, загружаем данные и инициализируем индикаторы
            loadYouTubeData();
            setupSlideIndicator(sliderIndicator);
        }
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

    private void updateSlideIndicator(LinearLayout sliderIndicator, int currentPosition) {
        for (int i = 0; i < sliderIndicator.getChildCount(); i++) {
            ImageView indicator = (ImageView) sliderIndicator.getChildAt(i);
            indicator.setImageResource(i == currentPosition ? R.drawable.circle_fill : R.drawable.circle_fill2);
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

//    private void loadYouTubeData() {
//        YouTubeApiClient.fetchLatestVideosAsync(requireContext(), new YouTubeApiClient.OnVideosFetchedListener() {
//            @Override
//            public void onVideosFetched(List<SearchResult> videos) {
//                // Обновляем кэш и UI
//                CachedYouTubeVideos.setCachedVideos(videos);
//                processFetchedVideos(videos);
//            }
//        });
//    }

    private void loadYouTubeData() {
        Log.d("HomeFragment", "Начало загрузки данных YouTube");
        if (!isNetworkConnected()) {
            Log.d("HomeFragment", "Нет интернета, показываем ошибку");
            sliderAdapter.setShowErrorLayout(true);
        } else if (!isFetchingVideos) {
            isFetchingVideos = true;
            // Выполняем HTTP-запрос для получения видео
            fetchVideosFromServer();
        }
    }







    public boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);

        if (cm != null) {
            NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
            return activeNetwork != null && activeNetwork.isConnectedOrConnecting();
        }

        return false;
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


    public void updateIndicators() {
        setupSlideIndicator(sliderIndicator);
    }

    private void fetchVideosFromServer() {
        RequestQueue queue = Volley.newRequestQueue(requireContext());
        StringRequest stringRequest = new StringRequest(Request.Method.GET, VIDEOS_URL,
                response -> {
                    isFetchingVideos = false;
                    try {
                        JSONObject jsonResponse = new JSONObject(response);
                        JSONArray videosArray = jsonResponse.getJSONArray("videos");
                        List<SearchResult> searchResults = new ArrayList<>();
                        for (int i = 0; i < videosArray.length(); i++) {
                            JSONObject videoObject = videosArray.getJSONObject(i);
                            String videoId = videoObject.getString("video_id");
                            String title = videoObject.getString("title");
                            // Создаем объект SearchResult
                            SearchResult searchResult = new SearchResult();
                            SearchResultSnippet snippet = new SearchResultSnippet();
                            snippet.setTitle(title);
                            ResourceId resourceId = new ResourceId();
                            resourceId.setVideoId(videoId);
                            searchResult.setId(resourceId);
                            searchResult.setSnippet(snippet);
                            searchResults.add(searchResult);
                        }
                        // Обновляем адаптер с полученными данными
                        latestVideos.clear();
                        latestVideos.addAll(searchResults);
                        sliderAdapter.notifyDataSetChanged();
                        setupSlideIndicator(sliderIndicator);
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Log.e("HomeFragment", "Ошибка парсинга JSON: " + e.getMessage());
                        sliderAdapter.setShowErrorLayout(true);
                    }
                }, error -> {
            isFetchingVideos = false;
            Log.e("HomeFragment", "Ошибка HTTP-запроса: " + error.getMessage());
            sliderAdapter.setShowErrorLayout(true);
        });
        queue.add(stringRequest);
    }


}