package com.example.tarkov.ui.dashboard;

import android.os.Bundle;
import android.widget.RadioGroup;

import androidx.appcompat.app.AppCompatActivity;

import com.davemorrissey.labs.subscaleview.ImageSource;
import com.davemorrissey.labs.subscaleview.SubsamplingScaleImageView;
import com.example.tarkov.R;

public class MapOfBeregActivity extends AppCompatActivity {

    private SubsamplingScaleImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.map_of_bereg);

        imageView = findViewById(R.id.mapImageView);

        RadioGroup radioGroup = findViewById(R.id.RadioGroup); // Assuming you have a RadioGroup in your layout
        imageView.setImage(ImageSource.resource(R.drawable.bereg)); // Set initial image

        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {

                    if (checkedId == R.id.radioButton1) {
                        imageView.setImage(ImageSource.resource(R.drawable.bereg_plus_all));
                    } else if (checkedId == R.id.radioButton2) { // Assuming the second radio button exists
                        imageView.setImage(ImageSource.resource(R.drawable.bereg_plus_exs));
                    } else if (checkedId == R.id.radioButton3) { // Assuming the third radio button exists
                        imageView.setImage(ImageSource.resource(R.drawable.bereg_plus_icon));
                    } else if (checkedId == R.id.radioButton4) {
                        imageView.setImage(ImageSource.resource(R.drawable.bereg));
                    } else {
                        // Handle any other radio buttons if needed
                    }
            }
        });
    }
}
