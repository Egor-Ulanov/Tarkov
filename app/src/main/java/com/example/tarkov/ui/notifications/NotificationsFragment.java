package com.example.tarkov.ui.notifications;

import android.content.res.ColorStateList;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.tarkov.MainActivity;
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
        TextView themeText = binding.themeText;
        TextView interfaceText = binding.interfacePril;
        View liniaText = binding.liniaText;

        websiteText.setOnClickListener(v -> openLink("https://www.escapefromtarkov.com/support"));
        emailText.setOnClickListener(v -> openLink("mailto:gaijinpass@gaijindistribution.com"));
        phoneText.setOnClickListener(v -> openLink("tel:+3614719244"));
        applyTheme();
        return root;
    }

    private void applyTheme() {
        MainActivity mainActivity = (MainActivity) getActivity();
        if (mainActivity != null) {
            mainActivity.updateActionBarStyle(isDarkTheme);
        }

        updateUIElements();
        updateSwitchThumb();


        // Напиши установку цветов элементов здес

    }



    @Override
    public void onResume() {
        super.onResume();
        // Проверка, была ли активность пересоздана из-за смены темы
        if(isRecreating) {
            // Обновление цветов элементов

            updateActionBar();
            updateUIElements();
            isRecreating = false;
        }

//        MainActivity mainActivity = (MainActivity) getActivity();
//        if (mainActivity != null) {
//            mainActivity.updateActionBarStyle(isDarkTheme);
//        }
    }

    private void updateUIElements() {
//        // Получение цветов из ресурсов
//        int textColor = ContextCompat.getColor(requireContext(), isDarkTheme ? R.color.dark_text : R.color.light_text);
//        int backgroundColor = ContextCompat.getColor(requireContext(), isDarkTheme ? R.color.dark_background : R.color.light_background);
        int navBackgroundColor = ContextCompat.getColor(requireContext(), isDarkTheme ? R.color.dark_nav_background : R.color.light_nav_background);
//
//        // Установка цветов элементов
//        binding.activeThemeLabel.setTextColor(textColor);
//        binding.getRoot().setBackgroundColor(backgroundColor);

        // Обновление цветов фрагмента
        int textColorResId = isDarkTheme ? R.color.dark_text : R.color.light_text;
        int backgroundColorResId = isDarkTheme ? R.color.dark_background : R.color.light_background;
        int interfaceTextColorID = isDarkTheme ? R.color.lighter_interface_text_color : R.color.interface_text_color;

        int textColor = ContextCompat.getColor(requireContext(), textColorResId);
        int backgroundColor = ContextCompat.getColor(requireContext(), backgroundColorResId);
        binding.activeThemeLabel.setTextColor(textColor);
        binding.contactsText.setTextColor(textColor);
        binding.phoneText.setTextColor(textColor);
        binding.getRoot().setBackgroundColor(backgroundColor);

        // Обновление цветов и фона нижней навигационной панели
        BottomNavigationView navView = getActivity().findViewById(R.id.nav_view);
        navView.setBackgroundColor(navBackgroundColor);

        // Установка селекторов для цвета иконок и текста
        int navIconColorResId = isDarkTheme ? R.color.dark_nav_icon_selector : R.color.light_nav_icon_selector;
        ColorStateList navIconColor = ContextCompat.getColorStateList(requireContext(), navIconColorResId);
        navView.setItemIconTintList(navIconColor);
        navView.setItemTextColor(navIconColor);


        // Обновление цветов "Интерфейс приложения"
        int interfaceTextColor = ContextCompat.getColor(requireContext(), interfaceTextColorID);
        binding.interfacePril.setTextColor(interfaceTextColor);

        // Обновление цветов "Тема приложения"
        int themeTextColor = ContextCompat.getColor(requireContext(), textColorResId);
        binding.themeText.setTextColor(themeTextColor);

        // Обновление цвета полоски
        int dividerColor = ContextCompat.getColor(requireContext(), interfaceTextColorID);
        binding.liniaText.setBackgroundColor(dividerColor);


// Применение текста в зависимости от выбранной темы
        String themeText = isDarkTheme ? "Темная" : "Светлая";
        binding.activeThemeLabel.setText(themeText);
// Применение цвета текста из ресурсов

        binding.activeThemeLabel.setTextColor(textColor);
// Применение фона из ресурсов

        binding.getRoot().setBackgroundColor(backgroundColor);
// Применение выбранного стиля к активности
        getActivity().setTheme(isDarkTheme ? R.style.AppTheme_Dark : R.style.AppTheme_Light);
// Установка изображения при изменении темыupdateSwitchThumb();
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

    private void updateActionBar() {
        if (getActivity() != null) {
            ActionBar actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
            if (actionBar != null) {
                // Установка цвета фона ActionBar
                int backgroundColorId = isDarkTheme ? R.color.dark_nav_background : R.color.light_nav_background;
                int backgroundColor = ContextCompat.getColor(getContext(), backgroundColorId);
                actionBar.setBackgroundDrawable(new ColorDrawable(backgroundColor));

                // Установка цвета текста заголовка
                int textColorId = isDarkTheme ? R.color.dark_text : R.color.light_text;
                int textColor = ContextCompat.getColor(getContext(), textColorId);

                // Создание нового Spannable для изменения цвета текста заголовка
                Spannable text = new SpannableString(actionBar.getTitle());
                text.setSpan(new ForegroundColorSpan(textColor), 0, text.length(), Spannable.SPAN_INCLUSIVE_INCLUSIVE);

                actionBar.setTitle(text);
            }
        }
    }
}