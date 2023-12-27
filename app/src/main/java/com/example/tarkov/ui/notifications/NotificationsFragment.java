package com.example.tarkov.ui.notifications;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.tarkov.R;
import com.example.tarkov.databinding.FragmentNotificationsBinding;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.switchmaterial.SwitchMaterial;

public class NotificationsFragment extends Fragment {

    private FragmentNotificationsBinding binding;
    // Добавьте поле класса
    private boolean isDarkTheme = false;

    // Внутри onCreateView или других методов
    private boolean isDarkTheme() {
        return isDarkTheme;
    }
    private boolean isRecreating = false;
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        NotificationsViewModel notificationsViewModel =
                new ViewModelProvider(this).get(NotificationsViewModel.class);

        binding = FragmentNotificationsBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        /*final TextView textView = binding.textNotifications;
        notificationsViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);*/

        // Восстановление состояния темы, если оно было сохранено
        if (savedInstanceState != null) {
            isDarkTheme = savedInstanceState.getBoolean("isDarkTheme", false);
        }
        // Установка изображения при изменении темы
        updateSwitchThumb();

        TextView activeThemeLabel = binding.activeThemeLabel;

        SwitchMaterial themeSwitch = binding.themeSwitch;
        themeSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            isDarkTheme = isChecked;
            // Применение выбранной темы
            applyTheme();
        });

        // Добавление контактной информации
        TextView contactsText = binding.contactsText;
        TextView websiteText = binding.websiteText;
        TextView emailText = binding.emailText;
        TextView phoneText = binding.phoneText;

        websiteText.setOnClickListener(v -> openLink("https://www.escapefromtarkov.com/support"));
        emailText.setOnClickListener(v -> openLink("mailto:gaijinpass@gaijindistribution.com"));
        phoneText.setOnClickListener(v -> openLink("tel:+3614719244"));
        applyTheme();
        return root;
    }

    public void applyTheme() {
        int textColorResId = isDarkTheme ? R.color.ForTextNewsDark : R.color.ForTextNewsLight;
        int backgroundColorResId = isDarkTheme ? R.color.ForBackgroundNewsDark : R.color.ForBackgroundNewsLight;
        int navBackgroundColorResId = isDarkTheme ? R.color.nav_background_dark : R.color.nav_background_light;
        int navIconColorResId = isDarkTheme ? R.color.nav_icon_light : R.color.nav_icon_dark;

        // Обновление цветов фрагмента
        int textColor = ContextCompat.getColor(requireContext(), textColorResId);
        int backgroundColor = ContextCompat.getColor(requireContext(), backgroundColorResId);
        binding.activeThemeLabel.setTextColor(textColor);
        binding.getRoot().setBackgroundColor(backgroundColor);

        // Обновление цветов нижнего навигационного меню
        BottomNavigationView navView = getActivity().findViewById(R.id.nav_view);
        navView.setBackgroundColor(ContextCompat.getColor(requireContext(), navBackgroundColorResId));
        navView.setItemIconTintList(ContextCompat.getColorStateList(requireContext(), navIconColorResId));

        // Установка стиля активности
        getActivity().setTheme(isDarkTheme ? R.style.AppTheme_Dark : R.style.AppTheme_Light);
        updateSwitchThumb();

        AppCompatActivity activity = (AppCompatActivity) getActivity();
        if (activity != null) {
            if (isDarkTheme) {
                activity.getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.ForItemNewsDark)));
            } else {
                activity.getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.ForItemNewsLight)));
            }
        }

        SharedPreferences sharedPreferences = getActivity().getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("isDarkTheme", isDarkTheme);
        editor.apply();


    }

    private void openLink(String url) {
        // Обработка нажатия на ссылку
    }
    private void updateSwitchThumb() {
        SwitchMaterial themeSwitch = binding.themeSwitch;
        int thumbImageResId = isDarkTheme() ? R.drawable.moon : R.drawable.sun;
        themeSwitch.setThumbDrawable(ContextCompat.getDrawable(requireContext(), thumbImageResId));
    }
    // Сохранение состояния темы при уничтожении фрагмента
    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean("isDarkTheme", isDarkTheme);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}