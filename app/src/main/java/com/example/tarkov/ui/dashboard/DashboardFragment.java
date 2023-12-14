package com.example.tarkov.ui.dashboard;

import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.tarkov.databinding.FragmentDashboardBinding;
import com.example.tarkov.databinding.MapOfBeregBinding;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.toolbox.NetworkImageView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.example.tarkov.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;
import com.example.tarkov.ui.Parser.ParserFix;

public class DashboardFragment extends Fragment {

    private FragmentDashboardBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        DashboardViewModel dashboardViewModel =
                new ViewModelProvider(this).get(DashboardViewModel.class);

        binding = FragmentDashboardBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        /*final TextView textView = binding.textDashboard;
        dashboardViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);*/

        // Пример картинок из интернета. Замените ссылками на ваши изображения.
        String imageUrl1 = "https://tarkov.team/wp-content/uploads/elementor/thumbs/Карта-Берег-q7fd7nczf1xy7xbfhqiox7m7sltorv62vq1l5ozu6w.jpg";
        String imageUrl2 = "https://tarkov.team/wp-content/uploads/elementor/thumbs/Карта-Завод-q7fd9ubfd4y1b64qooncqnoxoz0iqfvj6kudgxqro8.jpg";

        Glide.with(this).load(imageUrl1).into(binding.mapImage1);
        Glide.with(this).load(imageUrl2).into(binding.mapImage2);

        binding.mapBlock1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
// Создайте намерение для открытия новой активности (замените MapOfBeregActivity.class на ваш класс активности)
                Intent intent = new Intent(getActivity(), MapOfBeregActivity.class);

// Добавьте дополнительные данные, если необходимо (например, передача параметров в новую активность)
                //intent.putExtra("key", "value");

// Запустите новую активность
                startActivity(intent);
            }
        });

        binding.mapBlock2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
// Создайте намерение для открытия новой активности (замените MapOfBeregActivity.class на ваш класс активности)
                Intent intent = new Intent(getActivity(), MapOfZavodActivity.class);

// Добавьте дополнительные данные, если необходимо (например, передача параметров в новую активность)
                //intent.putExtra("key", "value");

// Запустите новую активность
                startActivity(intent);
            }
        });
        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}