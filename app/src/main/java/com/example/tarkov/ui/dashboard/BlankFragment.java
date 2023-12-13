package com.example.tarkov.ui.dashboard;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.davemorrissey.labs.subscaleview.ImageSource;
import com.davemorrissey.labs.subscaleview.SubsamplingScaleImageView;
import com.example.tarkov.R;
import com.example.tarkov.databinding.FragmentDashboardBinding;

public class BlankFragment extends Fragment {

    private BlankViewModel mViewModel;
    private FragmentDashboardBinding binding;

    private Switch switchImage;
    private SubsamplingScaleImageView imageView;
    private int imageIndex = 0; // 0 for woods, 1 for woods2

    public static BlankFragment newInstance() {
        return new BlankFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_blank, container, false);

        imageView = view.findViewById(R.id.imageView);
        setImageBasedOnIndex();

        switchImage = view.findViewById(R.id.switch_image);
        switchImage.setOnCheckedChangeListener((compoundButton, isChecked) -> {
            imageIndex = isChecked ? 1 : 0;
            setImageBasedOnIndex();
        });

        return view;
    }

    private void setImageBasedOnIndex() {
        switch (imageIndex) {
            case 0:
                imageView.setImage(ImageSource.resource(R.drawable.woods));
                break;
            case 1:
                imageView.setImage(ImageSource.resource(R.drawable.woods2));
                break;
        }
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(BlankViewModel.class);
        // TODO: Use the ViewModel
    }
}