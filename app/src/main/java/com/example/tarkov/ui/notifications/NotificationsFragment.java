package com.example.tarkov.ui.notifications;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.tarkov.databinding.FragmentNotificationsBinding;

import android.widget.Switch;

import com.example.tarkov.R;
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
            // ... ваш код
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

    private void applyTheme() {
        int textColorResId = isDarkTheme ? R.color.ForTextNewsDark : R.color.ForTextNewsLight;
        int backgroundColorResId = isDarkTheme ? R.color.ForBackgroundNewsDark : R.color.ForBackgroundNewsLight;

        // Применение текста в зависимости от выбранной темы
        String themeText = isDarkTheme ? "Темная" : "Светлая";
        binding.activeThemeLabel.setText(themeText);

        // Применение цвета текста из ресурсов
        int textColor = ContextCompat.getColor(requireContext(), textColorResId);
        binding.activeThemeLabel.setTextColor(textColor);

        // Применение фона из ресурсов
        int backgroundColor = ContextCompat.getColor(requireContext(), backgroundColorResId);
        binding.getRoot().setBackgroundColor(backgroundColor);

        // Применение выбранного стиля к активности
        getActivity().setTheme(isDarkTheme ? R.style.AppTheme_Dark : R.style.AppTheme_Light);



        // Установка изображения при изменении темы
        updateSwitchThumb();
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