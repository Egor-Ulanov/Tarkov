package com.example.tarkov.ui.dashboard;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.app.Activity;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.davemorrissey.labs.subscaleview.ImageSource;
import com.davemorrissey.labs.subscaleview.SubsamplingScaleImageView;
import com.example.tarkov.MainActivity;
import com.example.tarkov.R;
import com.example.tarkov.databinding.FragmentDashboardBinding;

public class DashboardFragment extends Fragment {

    private FragmentDashboardBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        DashboardViewModel dashboardViewModel =
                new ViewModelProvider(this).get(DashboardViewModel.class);

        binding = FragmentDashboardBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        final TextView textView = binding.textDashboard;
        dashboardViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);

        return root;



    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    public void startNewActivity(View v) {
        //setContentView(R.layout.fragment_blank);
        //bottomNavigationView.setSelectedItemId(R.id.navigation_home);
        //Intent intent = new Intent(getActivity(), DashboardFragment1.class);
        //startActivity(intent);


    }

}