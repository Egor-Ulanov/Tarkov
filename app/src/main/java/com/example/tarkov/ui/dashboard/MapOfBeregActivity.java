package com.example.tarkov.ui.dashboard;

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.widget.RadioGroup;

import androidx.appcompat.app.AppCompatActivity;

import com.davemorrissey.labs.subscaleview.ImageSource;
import com.davemorrissey.labs.subscaleview.SubsamplingScaleImageView;
import com.example.tarkov.R;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

public class MapOfBeregActivity extends AppCompatActivity {

    private SubsamplingScaleImageView imageView;

//    @Override
//            protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.map_of_bereg);
//
//        imageView = findViewById(R.id.mapImageView);
//
//        RadioGroup radioGroup = findViewById(R.id.RadioGroup); // Assuming you have a RadioGroup in your layout
//
//
//         imageView.setImage(ImageSource.resource(R.drawable.bereg)); // Set initial image
//
//        // Загрузите начальное изображение из Dropbox
////        loadMapImage("https://www.dropbox.com/scl/fi/nn7f1d4y7wey7jh73ur4a/bereg.png?rlkey=w499vba0n3g7t40m2i10u7al6&dl=1");
//        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
//            @Override
//            public void onCheckedChanged(RadioGroup group, int checkedId) {
//
//                    if (checkedId == R.id.radioButton1) {
//                        imageView.setImage(ImageSource.resource(R.drawable.bereg_plus_all));
//                    } else if (checkedId == R.id.radioButton2) { // Assuming the second radio button exists
//                        imageView.setImage(ImageSource.resource(R.drawable.bereg_plus_exs));
//                    } else if (checkedId == R.id.radioButton3) { // Assuming the third radio button exists
//                        imageView.setImage(ImageSource.resource(R.drawable.bereg_plus_icon));
//                    } else if (checkedId == R.id.radioButton4) {
//                        imageView.setImage(ImageSource.resource(R.drawable.bereg));
//                    } else {
//                        // Handle any other radio buttons if needed
//                    }
//            }
//        });
//    }

    private void loadMapImage(String imageUrl) {
        Picasso.get()
                .load(imageUrl)
                .into(new Target() {
                    @Override
                    public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                        imageView.setImage(ImageSource.bitmap(bitmap));
                    }

                    @Override
                    public void onBitmapFailed(Exception e, Drawable errorDrawable) {
                        // Обработка ошибок загрузки
                    }

                    @Override
                    public void onPrepareLoad(Drawable placeHolderDrawable) {
                        // Подготовка к загрузке
                    }
                });

        Log.d("MapOfBeregActivity", "Image loaded: " + imageUrl);
    }

}
