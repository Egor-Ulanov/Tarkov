package com.example.tarkov.ui.notifications;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.tarkov.databinding.FragmentNotificationsBinding;

import android.widget.Switch;

import com.example.tarkov.R;
import com.google.android.material.switchmaterial.SwitchMaterial;

public class NotificationsFragment extends Fragment {

    private FragmentNotificationsBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        NotificationsViewModel notificationsViewModel =
                new ViewModelProvider(this).get(NotificationsViewModel.class);

        binding = FragmentNotificationsBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        /*final TextView textView = binding.textNotifications;
        notificationsViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);*/

        TextView activeThemeLabel = binding.activeThemeLabel;

        SwitchMaterial themeSwitch = binding.themeSwitch;
        themeSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            int themeId = isChecked ? R.style.AppTheme_Dark : R.style.AppTheme_Light;

            // Применение текста в зависимости от выбранной темы
            activeThemeLabel.setText(isChecked ? "Тёмная" : "Светлая");

            // Изменение темы без пересоздания активности
            AppCompatDelegate.setDefaultNightMode(
                    isChecked ? AppCompatDelegate.MODE_NIGHT_YES : AppCompatDelegate.MODE_NIGHT_NO);

            // Применение выбранного стиля к активности
            getActivity().setTheme(themeId);

            // Применение анимации при изменении темы (если нужно)
            getActivity().overridePendingTransition(R.transition.fade_in, R.transition.fade_out);
        });
        return root;
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}