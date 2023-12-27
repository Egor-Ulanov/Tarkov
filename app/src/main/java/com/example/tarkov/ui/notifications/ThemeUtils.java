package com.example.tarkov.ui.notifications;

import android.content.Context;

import androidx.core.content.ContextCompat;

import com.example.tarkov.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class ThemeUtils {

    public static void applyTheme(Context context, boolean isDarkTheme, BottomNavigationView navView) {
        int textColorResId = isDarkTheme ? R.color.ForTextNewsDark : R.color.ForTextNewsLight;
        int backgroundColorResId = isDarkTheme ? R.color.ForBackgroundNewsDark : R.color.ForBackgroundNewsLight;
        int navBackgroundColorResId = isDarkTheme ? R.color.nav_background_dark : R.color.nav_background_light;
        int navIconColorResId = isDarkTheme ? R.color.nav_icon_light : R.color.nav_icon_dark;

        // Обновление цветов нижнего навигационного меню
        navView.setBackgroundColor(ContextCompat.getColor(context, navBackgroundColorResId));
        navView.setItemIconTintList(ContextCompat.getColorStateList(context, navIconColorResId));

        // Применение выбранного стиля к активности
        context.setTheme(isDarkTheme ? R.style.AppTheme_Dark : R.style.AppTheme_Light);
    }
}
