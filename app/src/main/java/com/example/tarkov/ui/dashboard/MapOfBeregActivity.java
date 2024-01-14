package com.example.tarkov.ui.dashboard;

import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.RadioGroup;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.drawable.DrawableCompat;

import com.davemorrissey.labs.subscaleview.ImageSource;
import com.davemorrissey.labs.subscaleview.SubsamplingScaleImageView;
import com.example.tarkov.R;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

public class MapOfBeregActivity extends AppCompatActivity {

    private SubsamplingScaleImageView imageView;

    private static final String TAG = "MapOfBeregActivity";
    private Target loadImageTarget; // Поле для сильной ссылки на Target

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate: начало");

        super.onCreate(savedInstanceState);
        setContentView(R.layout.map_of_bereg);

        imageView = findViewById(R.id.mapImageView);

        RadioGroup radioGroup = findViewById(R.id.RadioGroup); // Assuming you have a RadioGroup in your layout



        // Для rotateButton
        Drawable rotateDrawable = ContextCompat.getDrawable(this, R.drawable.ic_full_dis_24dp);
        rotateDrawable = DrawableCompat.wrap(rotateDrawable);
        DrawableCompat.setTint(rotateDrawable, ContextCompat.getColor(this, R.color.white));
        ImageButton rotateButton = findViewById(R.id.rotateButton);
        rotateButton.setImageDrawable(rotateDrawable);

        rotateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Вызывайте метод для поворота экрана
                toggleFullscreenMap();
            }
        });



        Drawable backDrawable = ContextCompat.getDrawable(this, R.drawable.ic_back_24dp);
        backDrawable = DrawableCompat.wrap(backDrawable);
        DrawableCompat.setTint(backDrawable, ContextCompat.getColor(this, R.color.white));
        ImageButton backButton = findViewById(R.id.backButton);
        backButton.setImageDrawable(backDrawable);

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Вызывайте метод для возврата на предыдущую страницу
                goBack();
            }
        });

        Log.d(TAG, "onCreate: макет установлен");

// imageView.setImage(ImageSource.resource(R.drawable.bereg)); // Set initial image

// Загрузите начальное изображение из Dropbox
        //imageView.setImage(ImageSource.resource(R.drawable.bereg));
        imageView.setImage(ImageSource.resource(R.drawable.bereg));

        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {

                Log.d(TAG, "onCheckedChanged: выбран radioButton с ID " + checkedId);
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

    private void loadMapImage(String imageUrl) {
        Log.d(TAG, "loadMapImage: попытка загрузить изображение из " + imageUrl);

        // Создание Target и сохранение сильной ссылки
        this.loadImageTarget = new Target() {
            @Override
            public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                imageView.setImage(ImageSource.bitmap(bitmap));
            }

            @Override
            public void onBitmapFailed(Exception e, Drawable errorDrawable) {
                Log.e(TAG, "Ошибка загрузки изображения", e);
                // Показать запасное изображение или сообщение об ошибке
                // Например, imageView.setImage(ImageSource.resource(R.drawable.placeholder));
            }

            @Override
            public void onPrepareLoad(Drawable placeHolderDrawable) {
                // Подготовка к загрузке
            }
        };

        // Использование Picasso для загрузки изображения
        Picasso.get().load(imageUrl).into(this.loadImageTarget);
    }

    private void rotateScreen() {
        Log.d(TAG, "rotateScreen: попытка повернуть экран");
        // Измените ориентацию экрана
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);
    }

    private void goBack() {
        Log.d(TAG, "goBack: возврат на предыдущую страницу");
        // Возврат на предыдущую страницу
        onBackPressed();
    }

    private void toggleFullscreenMap() {
        if (getRequestedOrientation() == ActivityInfo.SCREEN_ORIENTATION_PORTRAIT) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        } else {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }
    }

}