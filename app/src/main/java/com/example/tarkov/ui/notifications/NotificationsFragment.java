package com.example.tarkov.ui.notifications;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.tarkov.R;
import com.example.tarkov.databinding.FragmentNotificationsBinding;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.switchmaterial.SwitchMaterial;

public class NotificationsFragment extends Fragment {

    private FragmentNotificationsBinding binding;
    private boolean isDarkTheme = false;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        NotificationsViewModel notificationsViewModel = new ViewModelProvider(this).get(NotificationsViewModel.class);
        binding = FragmentNotificationsBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        if (savedInstanceState != null) {
            isDarkTheme = savedInstanceState.getBoolean("isDarkTheme", false);
        }

        SwitchMaterial themeSwitch = binding.themeSwitch;
        themeSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            isDarkTheme = isChecked;
            applyTheme();
        });

        applyTheme();
        return root;
    }

    public void applyTheme() {
        int textColorResId = isDarkTheme ? R.color.dark_text : R.color.light_text;
        int backgroundColorResId = isDarkTheme ? R.color.dark_background : R.color.light_background;
        int navBackgroundColorResId = isDarkTheme ? R.color.dark_nav_background : R.color.light_nav_background;
        int navIconColorResId = isDarkTheme ? R.color.dark_nav_icon_selector : R.color.light_nav_icon_selector;

        int textColor = ContextCompat.getColor(requireContext(), textColorResId);
        int backgroundColor = ContextCompat.getColor(requireContext(), backgroundColorResId);
        binding.activeThemeLabel.setTextColor(textColor);
        binding.getRoot().setBackgroundColor(backgroundColor);

        BottomNavigationView navView = getActivity().findViewById(R.id.nav_view);
        navView.setBackgroundColor(ContextCompat.getColor(requireContext(), navBackgroundColorResId));
        navView.setItemIconTintList(ContextCompat.getColorStateList(requireContext(), navIconColorResId));
        navView.setItemTextColor(ContextCompat.getColorStateList(requireContext(), navIconColorResId));

//        AppCompatActivity activity = (AppCompatActivity) getActivity();
//        if (activity instanceof MainActivity) {
//            ((MainActivity) activity).applyTheme();
//        }
    }

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
