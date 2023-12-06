package com.example.tarkov.ui.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.tarkov.databinding.FragmentHomeBinding;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;
import com.example.tarkov.R;

import android.widget.LinearLayout;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;

    //2 строки внизу добавил Volegov
    private ViewPager viewPager;
    private ImageSliderAdapter sliderAdapter;
    private LinearLayout sliderIndicator;
    private RecyclerView recyclerView;
    private NewsAdapter newsAdapter;
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        HomeViewModel homeViewModel =
                new ViewModelProvider(this).get(HomeViewModel.class);

        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        final TextView textView = binding.textHome;
        homeViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);

        // Initialize ViewPager, добавил volegov
        viewPager = root.findViewById(R.id.viewPager);
        sliderAdapter = new ImageSliderAdapter(requireContext());
        viewPager.setAdapter(sliderAdapter);

        // Инициализация индикатора слайдов
        sliderIndicator = root.findViewById(R.id.sliderIndicator);
        setupSlideIndicator();

        // Инициализация RecyclerView
        recyclerView = root.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        newsAdapter = new NewsAdapter();
        recyclerView.setAdapter(newsAdapter);

        // Передайте данные в адаптер (ваш список новостей)
        List<String> newsList = new ArrayList<>(); // Замените этот список на ваш
        newsAdapter.setNewsList(newsList);

        // Обработчик смены слайда
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                // Обновление индикатора слайдов при смене слайда
                sliderAdapter.updateSlideIndicator(sliderIndicator, position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });

        return root;
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


}