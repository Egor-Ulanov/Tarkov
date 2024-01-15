package com.example.tarkov.ui.dashboard;

import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
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

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class MapOfBeregActivity extends AppCompatActivity {

    private SubsamplingScaleImageView imageView;

    private static final String TAG = "MapOfBeregActivity";
    private Target loadImageTarget; // Поле для сильной ссылки на Target

    private List<Target> targets = new ArrayList<>();


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

        // Обработчик события, поворот экрана
        rotateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleFullscreenMap();
            }
        });



        Drawable backDrawable = ContextCompat.getDrawable(this, R.drawable.ic_back_24dp);
        backDrawable = DrawableCompat.wrap(backDrawable);
        DrawableCompat.setTint(backDrawable, ContextCompat.getColor(this, R.color.white));
        ImageButton backButton = findViewById(R.id.backButton);
        backButton.setImageDrawable(backDrawable);

        // Обработчик события - возвращение на предыдущюю страницу
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goBack();
            }
        });

        Log.d(TAG, "onCreate: макет установлен");

        // Ссылки на части карты берег
        String[] BeregimageUrls = {
                "https://drive.google.com/uc?export=view&id=1bYGCesXZEY7ZfhaVtoXDVO94j5573LdP",
                "https://drive.google.com/uc?export=view&id=1ZuxuT-E_pSOy1zmtq9letWWjtGDXjqyU",
                "https://drive.google.com/uc?export=view&id=1B2UNmO8-K28apau6HTAWN8wyElxVv84D",
                "https://drive.google.com/uc?export=view&id=1tBSIfqNGa2ohSXpAmpSbmWAGUCntNDv3",
                "https://drive.google.com/uc?export=view&id=158u9k6EM96MLgIFHsnbl0VVXYv9QNtMG",
                "https://drive.google.com/uc?export=view&id=1-FLj87lmqIbbNcsFMbzbMPbwUwmkQCcp",
                "https://drive.google.com/uc?export=view&id=1FNqYlfm6Me4uu6TRqb6pjTHP6x6NSexp",
                "https://drive.google.com/uc?export=view&id=1qejLWwVMcTuVv6dJteir6jPNlJrLELas",
                "https://drive.google.com/uc?export=view&id=1EMjOSw2oqBYod1eCGrXeyhgPo4EI6hR8",
        };

        // Ссылки на части карты БерегПлюс
        String[] BeregPlusAllimageUrls = {
                "https://drive.google.com/uc?export=view&id=1j7DlT1uC_kJbNdU9pEJUyG3nQ73GBGxy",
                "https://drive.google.com/uc?export=view&id=1SokgS0F4aiFZ35b_31Fq0d0i_twt9vTX",
                "https://drive.google.com/uc?export=view&id=1sm_Q7l3Zir44CZfAmW2YylJQX7yjCwxl",
                "https://drive.google.com/uc?export=view&id=1QZskBR5BlHt-VwzQrqdAqcF4ox2Kbiz6",
                "https://drive.google.com/uc?export=view&id=1X1zT2T79D1p7zDboh__FFcvNKraNXZGX",
                "https://drive.google.com/uc?export=view&id=1NnFLl_rl0vk3vYaLfJOmG0PJpndIHi8a",
                "https://drive.google.com/uc?export=view&id=1z-OjwekUHL0SOpqJBSDuC4Z0klEPpG-s",
                "https://drive.google.com/uc?export=view&id=16cJa4qubQpJif5etwXwIhqdeLLQmqWJT",
                "https://drive.google.com/uc?export=view&id=18c6-YrYuIZN1sPCbXOCkKgHd3MCBc0sI",
        };

        // Ссылки на части карты БерегПлюсВыходы
        String[] BeregPlusExsimageUrls = {
                "https://drive.google.com/uc?export=view&id=1tZ9GBMWg7cBbNKqAILR-GPeGe-yyP_gj",
                "https://drive.google.com/uc?export=view&id=1PS2HzHRrx9RjbNwiuN3xZem2BYmEAl5s",
                "https://drive.google.com/uc?export=view&id=1wugwW7R85uNuhxEitvpjUt0crRgYqC4R",
                "https://drive.google.com/uc?export=view&id=1Usl_9RGzH4JMay6pB4ZR2JN4Fic3loKt",
                "https://drive.google.com/uc?export=view&id=1ZllKr5Dp77rXkWtj0tvCvqlAjZrsvrhy",
                "https://drive.google.com/uc?export=view&id=1BVlda_i9ukRwRLkhBRPNaxelj_kXAvyb",
                "https://drive.google.com/uc?export=view&id=18KjNMuCLGtWI8rlg_ULxCtbE0_VlAUDw",
                "https://drive.google.com/uc?export=view&id=16hNGlcJ-AJ0Ukgf1tspnICYdPbQVdnOC",
                "https://drive.google.com/uc?export=view&id=1w_jNEDeNvXKfmMvA9GJsbo7NdRVI9jEp",
        };

        // Ссылки на части карты БерегПлюсВыходы
        String[] BeregPlusIconimageUrls = {
                "https://drive.google.com/uc?export=view&id=1ht_KiHrB_45jVUYhTmZTFwhcIb9GTJIG",
                "https://drive.google.com/uc?export=view&id=1mUmsswzfhFazCmMvGMnrohAIViVeCl11",
                "https://drive.google.com/uc?export=view&id=1xqdqHIbF_PA02M4YNk--mgUzm_ExaMWS",
                "https://drive.google.com/uc?export=view&id=1eI9c-3Lqea0CugwOrZfV2ax16Lq1HAzV",
                "https://drive.google.com/uc?export=view&id=15HCpvw6YdgyBuSgkG9L1ASgp-IlKCI--",
                "https://drive.google.com/uc?export=view&id=1sD99wGle3xG2V9SbHTtXBvSzJU1dHe9G",
                "https://drive.google.com/uc?export=view&id=1omcygok19wL0o8spq7N2693Bj-POOmKi",
                "https://drive.google.com/uc?export=view&id=1NsbkJCS6nShi9MtQHHnIGHz4StlXhtI8",
                "https://drive.google.com/uc?export=view&id=1JCm6B94BqRrgemBGcAD7BU6ImjNZPT8m",
        };


        // Установка карты берег
        loadAndMergeImages(BeregimageUrls);

        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {

                Log.d(TAG, "onCheckedChanged: выбран radioButton с ID " + checkedId);

                // Логика для переключения между разными картами
                if (checkedId == R.id.radioButton1) {
                    loadAndMergeImages(BeregPlusAllimageUrls);
                } else if (checkedId == R.id.radioButton2) {
                    loadAndMergeImages(BeregPlusExsimageUrls);
                } else if (checkedId == R.id.radioButton3) {
                    loadAndMergeImages(BeregPlusIconimageUrls);
                } else if (checkedId == R.id.radioButton4) {
                    loadAndMergeImages(BeregimageUrls);
                }
            }
        });
    }

    /*
    Сильные Ссылки на Target: Для каждого изображения создается отдельный объект Target, который сохраняется в списке targets.
    Это предотвращает утилизацию объектов Target сборщиком мусора до того, как изображение будет загружено, поскольку на них сохраняются сильные ссылки.
    Задержка в Загрузке: Используется Handler и метод postDelayed для введения задержки между запросами на загрузку изображений.
    Это снижает нагрузку на память и CPU, предотвращая одновременную загрузку всех изображений.
    Задержка в 100 миллисекунд между каждым запросом помогает равномерно распределить загрузку.
    */

    // Метод для загрузки и склеивания изображений
    private void loadAndMergeImages(String[] imageUrls) {
        final int parts = imageUrls.length; // Количество изображений для загрузки
        final Bitmap[] images = new Bitmap[parts]; // Массив, хранящий загруженные изображения в формате Bitmap
        final AtomicInteger counter = new AtomicInteger(0); // Счетчик успешно загруженных фотографий
        final Handler handler = new Handler(); // Handler используется для задержки загрузки каждого изображения

        for (int i = 0; i < parts; i++) {
            final int index = i;

            // Создание нового Target для каждого изображения
            Target imageTarget = new Target() {
                @Override
                public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                    images[index] = bitmap;
                    Log.d(TAG, "Изображение загружено: " + imageUrls[index]);
                    if (counter.incrementAndGet() == parts) {
                        mergeImages(images);
                    }
                }

                @Override
                public void onBitmapFailed(Exception e, Drawable errorDrawable) {
                    Log.e(TAG, "Ошибка загрузки изображения: " + imageUrls[index], e);
                }

                @Override
                public void onPrepareLoad(Drawable placeHolderDrawable) {
                    Log.d(TAG, "Подготовка к загрузке изображения: " + imageUrls[index]);
                }
            };
            targets.add(imageTarget); // Сохранение сильной ссылки на Target
            // Задержка для уменьшения нагрузки на память и CPU
            handler.postDelayed(() -> Picasso.get().load(imageUrls[index]).into(imageTarget), i * 100);
        }
    }

    // Метод для склеивания изображений в одно большое
    private void mergeImages(Bitmap[] images) {
        int width = images[0].getWidth();
        int height = images[0].getHeight();
        Bitmap result = Bitmap.createBitmap(width * 3, height * 3, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(result);

        for (int i = 0; i < images.length; i++) {
            int row = i / 3;
            int col = i % 3;
            canvas.drawBitmap(images[i], col * width, row * height, null);
        }

        // Установка склеенного изображения в ImageView на основном потоке
        runOnUiThread(() -> {
            imageView.setImage(ImageSource.bitmap(result));
            Log.d(TAG, "Изображения склеены и установлены в imageView");
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