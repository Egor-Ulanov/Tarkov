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

    private boolean isFetchingVideos = false; // Флаг для отслеживания состояния загрузки

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

        // Проверка и загрузка данных из YouTube API
        if (CachedYouTubeVideos.isExpired(requireContext()) || CachedYouTubeVideos.getCachedVideos(requireContext()) == null) {
            // Проверка состояния сети и установка состояния ошибки для адаптера
            if (!isNetworkConnected()) {
                Log.d("HomeFragment", "Нет интернета при загрузке и нет кэша");

            } else {
                Log.d("HomeFragment", "Загрузка данных, интернет есть и есть кэш");
                loadYouTubeData();
            }
        } else {
            // Используем кэшированные данные
            processFetchedVideos(CachedYouTubeVideos.getCachedVideos(requireContext()));
        }


        // Загрузка последних 5 видео с канала Battlestategames
        if (!isNetworkConnected()) {
            Log.d("HomeFragment", "Нет интернета, показываем ошибку");
            // Если нет интернета, показать сообщение об ошибке
            sliderAdapter.setShowErrorLayout(true);
        } else if (!isFetchingVideos) { // Добавьте проверку флага
            isFetchingVideos = true; // Устанавливаем флаг в true перед вызовом fetchVideos
            CachedYouTubeVideos.fetchVideos(requireContext(), new YouTubeApiClient.OnVideosFetchedListener() {
                @Override
                public void onVideosFetched(List<SearchResult> videos) {
                    isFetchingVideos = false; // Устанавливаем флаг в false после завершения загрузки
                    if (videos != null && !videos.isEmpty()) {
                        Log.d("HomeFragment", "Данные YouTube получены");
                        CachedYouTubeVideos.setCachedVideos(videos);
                        processFetchedVideos(videos);
                        setupSlideIndicator(sliderIndicator); // Инициализация индикаторов, когда есть данные
                    } else {
                        Log.d("HomeFragment", "Видео не найдены или ошибка");
                        sliderAdapter.setShowErrorLayout(true);
                    }
                }
            });
        }

        // Можешь исправить ошибку
        // Can you fix the error

        // Please provide more details about the error that needs to be fixed.
        // Non-static method 'applyTheme(androidx.fragment.app.FragmentActivity)' cannot be referenced from a static context
        //   public void applyTheme(FragmentActivity activity) {
        //        int textColorResId = isDarkTheme ? R.color.dark_text : R.color.light_text;
        //        int backgroundColorResId = isDarkTheme ? R.color.dark_background : R.color.light_background;
        //        int navBackgroundColorResId = isDarkTheme ? R.color.dark_nav_background : R.color.light_nav_background;
        //        int navIconColorResId = isDarkTheme ? R.color.dark_nav_icon_selector : R.color.light_nav_icon_selector;
        //
        //        int textColor = ContextCompat.getColor(requireContext(), textColorResId);
        //        int backgroundColor = ContextCompat.getColor(requireContext(), backgroundColorResId);
        //        binding.activeThemeLabel.setTextColor(textColor);
        //        binding.getRoot().setBackgroundColor(backgroundColor);
        //
        //        BottomNavigationView navView = getActivity().findViewById(R.id.nav_view);
        //        navView.setBackgroundColor(ContextCompat.getColor(requireContext(), navBackgroundColorResId));
        //        navView.setItemIconTintList(ContextCompat.getColorStateList(requireContext(), navIconColorResId));
        //        navView.setItemTextColor(ContextCompat.getColorStateList(requireContext(), navIconColorResId));
        //
        //        AppCompatActivity activity = (AppCompatActivity) getActivity();
        //        if (activity != null) {
        //            if (isDarkTheme) {
        //                activity.getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.dark_item_background)));
        //            } else {
        //                activity.getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.light_item_background)));
        //            }
        //        }
        //
        //        SharedPreferences sharedPreferences = getActivity().getPreferences(Context.MODE_PRIVATE);
        //        SharedPreferences.Editor editor = sharedPreferences.edit();
        //        editor.putBoolean("isDarkTheme", isDarkTheme);
        //        editor.apply();
        //        updateSwitchThumb();
        //    }

        // Предложи исправление  NotificationsFragment.applyTheme(getActivity());


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
            indicator.setImageResource(i == currentPosition ? R.drawable.truedot1 : R.drawable.truedot2);
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
            // Если нет интернета, показать сообщение об ошибке
            sliderAdapter.setShowErrorLayout(true);
        } else if (!isFetchingVideos) { // Добавьте проверку флага
            isFetchingVideos = true; // Устанавливаем флаг в true перед вызовом fetchVideos
            CachedYouTubeVideos.fetchVideos(requireContext(), new YouTubeApiClient.OnVideosFetchedListener() {
                @Override
                public void onVideosFetched(List<SearchResult> videos) {
                    isFetchingVideos = false; // Устанавливаем флаг в false после завершения загрузки
                    if (videos != null && !videos.isEmpty()) {
                        Log.d("HomeFragment", "Данные YouTube получены");
                        CachedYouTubeVideos.setCachedVideos(videos);
                        processFetchedVideos(videos);
                        setupSlideIndicator(sliderIndicator); // Инициализация индикаторов, когда есть данные
                    } else {
                        Log.d("HomeFragment", "Видео не найдены или ошибка");
                        sliderAdapter.setShowErrorLayout(true);
                    }
                }
            });
        }
    }





    private void processFetchedVideos(List<SearchResult> videos) {
        if (videos != null && !videos.isEmpty()) {
            latestVideos.clear();
            latestVideos.addAll(videos);
            sliderAdapter.notifyDataSetChanged(); // Обновить адаптер
            setupSlideIndicator(sliderIndicator); // Обновить индикатор слайдера
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


}