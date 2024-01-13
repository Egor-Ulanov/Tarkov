package com.example.tarkov.ui.dashboard;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.example.tarkov.R;

import com.davemorrissey.labs.subscaleview.ImageSource;
import com.davemorrissey.labs.subscaleview.SubsamplingScaleImageView;

public class MapOfWoodsActivity extends AppCompatActivity{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.map_of_zavod);

        SubsamplingScaleImageView imageView = findViewById(R.id.mapImageView);
        imageView.setImage(ImageSource.resource(R.drawable.map_of_zavod));
    }
}
