package com.example.tarkov.ui.dashboard;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioGroup;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.drawable.DrawableCompat;

import com.davemorrissey.labs.subscaleview.ImageSource;
import com.davemorrissey.labs.subscaleview.SubsamplingScaleImageView;
import com.example.tarkov.R;
import com.google.android.material.snackbar.Snackbar;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class MapOfWoodsActivity extends AppCompatActivity {

    private void loadThemePreference() {
        SharedPreferences prefs = getSharedPreferences("AppThemePrefs", MODE_PRIVATE);
        boolean isDarkTheme = prefs.getBoolean("isDarkTheme", false);
        // Устанавливайте тему в соответствии с предпочтением
        setAppTheme(isDarkTheme);
    }

    private void setAppTheme(boolean isDarkTheme) {
        // Устанавливайте тему приложения в зависимости от предпочтения
        setTheme(isDarkTheme ? R.style.AppTheme_Dark : R.style.AppTheme_Light);
        // После изменения темы пересоздайте активность

    }
    private boolean isListExpanded = false;
    private ImageButton expandButton;
    private LinearLayout categoryList;

    // Определение переменных для работы с изображением, прогресс-баром и URL карт
    private SubsamplingScaleImageView imageView;
    private ProgressBar progressBar;
    private String[] WoodsimageUrls = {
            "https://drive.google.com/uc?export=view&id=1sZSZBCO8P6aZezrB3uXW9Boifp5VYDhN",
            "https://drive.google.com/uc?export=view&id=1RV_2dYknZhr27k4I9CoEG0WiUHLkzKuQ",
            "https://drive.google.com/uc?export=view&id=1w9YssAB_BT-oT0Tz1MotCJi5z92F246k",
            "https://drive.google.com/uc?export=view&id=1caBPyTToCvKMz05XstPlHw_S3lctq3iY",
            "https://drive.google.com/uc?export=view&id=16aXnqKs9fI1X2cOcllAXbwaZx63Di_oq",
            "https://drive.google.com/uc?export=view&id=1PXjPSM9G3UvVAZWIu2nGXfyviEWoXYwT",
            "https://drive.google.com/uc?export=view&id=1Gj-liVAoOEvRK1x8VMMMls9JeiIBEQiy",
            "https://drive.google.com/uc?export=view&id=1TZ8ftRlCNhZcP_lCeklT_Cy8H9KwMK_Y",
            "https://drive.google.com/uc?export=view&id=1Hjb8cC2TyqvoX6gQpP9ayIeQUOOq8orY",

    };
    // Переменные для обработки загрузки изображений и отслеживания состояния интернет-соединения
    private List<Target> targets = new ArrayList<>();
    private boolean isCheckingInternet = false;
    private boolean internetWasLost = false;
    private int internetCheckAttempts = 0;
    private Handler handler = new Handler();

    private static final String TAG = "MapOfWoodsActivity";
    private Target loadImageTarget; // Поле для сильной ссылки на Target


    // Runnable для периодической проверки состояния интернет-соединения
    private final Runnable internetCheckRunnable = new Runnable() {
        @Override
        public void run() {
            checkInternetConnection();
        }
    };



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        loadThemePreference();
        setContentView(R.layout.map_of_woods);
        expandButton = findViewById(R.id.expandButton);
        categoryList = findViewById(R.id.categoryList);
        Button selectAllButton = findViewById(R.id.selectAllButton);
        Button clearAllButton = findViewById(R.id.clearAllButton);
        Button PMCExtractsButton = findViewById(R.id.PMCExtractsButton);
        Button scavExtractsButton = findViewById(R.id.scavExtractsButton);

        // Загрузка сохраненной темы перед установкой содержимого вида
        SharedPreferences prefs = getSharedPreferences("AppThemePrefs", MODE_PRIVATE);
        boolean isDarkTheme = prefs.getBoolean("isDarkTheme", false); // false - значение по умолчанию

        setTheme(isDarkTheme ? R.style.AppTheme_Dark : R.style.AppTheme_Light);
        // Устанавливаем слушатель кликов на кнопку
        expandButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int maxHeight = 100;
                categoryList.getLayoutParams().height = maxHeight;
                categoryList.requestLayout();
                toggleList();

            }
        });

        Log.d(TAG, "onCreate: начало");




        progressBar = findViewById(R.id.progressBar);

        imageView = findViewById(R.id.mapImageView);

        //RadioGroup radioGroup = findViewById(R.id.RadioGroup); // Assuming you have a RadioGroup in your layout
        // Для rotateButton
        Drawable rotateDrawable = ContextCompat.getDrawable(this, R.drawable.ic_full_dis_24dp);
        rotateDrawable = DrawableCompat.wrap(rotateDrawable);
        DrawableCompat.setTint(rotateDrawable, ContextCompat.getColor(this, R.color.white));
       // ImageButton rotateButton = findViewById(R.id.rotateButton);
       // rotateButton.setImageDrawable(rotateDrawable);

        // Обработчик события, поворот экрана
        /*rotateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleFullscreenMap();
            }
        });*/
        Drawable backDrawable = ContextCompat.getDrawable(this, R.drawable.circle_close_button2);
        backDrawable = DrawableCompat.wrap(backDrawable);

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

        // Запуск проверки подключения к интернету с задержкой
        handler.postDelayed(internetCheckRunnable, 1000);
        // Ссылки на части карты берег
        String[] WoodsimageUrls = {
                "https://drive.google.com/uc?export=view&id=1sZSZBCO8P6aZezrB3uXW9Boifp5VYDhN",
                "https://drive.google.com/uc?export=view&id=1RV_2dYknZhr27k4I9CoEG0WiUHLkzKuQ",
                "https://drive.google.com/uc?export=view&id=1w9YssAB_BT-oT0Tz1MotCJi5z92F246k",
                "https://drive.google.com/uc?export=view&id=1caBPyTToCvKMz05XstPlHw_S3lctq3iY",
                "https://drive.google.com/uc?export=view&id=16aXnqKs9fI1X2cOcllAXbwaZx63Di_oq",
                "https://drive.google.com/uc?export=view&id=1PXjPSM9G3UvVAZWIu2nGXfyviEWoXYwT",
                "https://drive.google.com/uc?export=view&id=1Gj-liVAoOEvRK1x8VMMMls9JeiIBEQiy",
                "https://drive.google.com/uc?export=view&id=1TZ8ftRlCNhZcP_lCeklT_Cy8H9KwMK_Y",
                "https://drive.google.com/uc?export=view&id=1Hjb8cC2TyqvoX6gQpP9ayIeQUOOq8orY",
        };

        // Ссылки на части карты БерегПлюс
        String[] WoodsPlusAllimageUrls = {
                "https://drive.google.com/uc?export=view&id=1kV917GgP0zwqeTdLy-zjQ7HKdIIBWlE1",
                "https://drive.google.com/uc?export=view&id=1xrIY82FMiX7gqnlGtuIuAELuHBOLRxPJ",
                "https://drive.google.com/uc?export=view&id=1X7tghMsv5l9knwEbiu8civbr4LG9eA37",
                "https://drive.google.com/uc?export=view&id=1y7wHqrpOmidMi3yx7vKxGdcLKhH9clxm",
                "https://drive.google.com/uc?export=view&id=1odrj54DYhULIkiNHrpBCB7e814Aq-GaX",
                "https://drive.google.com/uc?export=view&id=12DJEy1OapwjWmTHuiBxlwa_PoiMdHRAP",
                "https://drive.google.com/uc?export=view&id=1YS-uBgP_qKlJbS907OhcBzDesBUFOIdJ",
                "https://drive.google.com/uc?export=view&id=1XCvgNgmCXzMk67wFXvSSVG2sR1_Es9Ej",
                "https://drive.google.com/uc?export=view&id=16d1jgfvS7OnI8bAyvEqp5yTgjm5jIv_O",
        };

        // Ссылки на части карты БерегПлюсВыходы
        String[] WoodsPlusPMCimageUrls = {
                "https://drive.google.com/uc?export=view&id=13DNonHZD1BVqfhMgYy5gR2b14C1ImX4W",
                "https://drive.google.com/uc?export=view&id=1Cl-6cCj9kEcXh6a9pU7Ev2xlglmXNLCa",
                "https://drive.google.com/uc?export=view&id=1A7T3layQjzTukjkpUNUYT72NW-FPm5G1",
                "https://drive.google.com/uc?export=view&id=1F-6YDio6iVz7le3gy_igvutRxyFtUgtf",
                "https://drive.google.com/uc?export=view&id=1TK2ZYWqldkb0Cg2Q81KvJNEe685ucwbF",
                "https://drive.google.com/uc?export=view&id=1eQjJAMFxz8pXyvZkl_7VkrPTU9ZCR0Nb",
                "https://drive.google.com/uc?export=view&id=1N55hQ63c9U63PaVvFEQQ5WmAeyCjj14M",
                "https://drive.google.com/uc?export=view&id=1e8NfT--vqIAOM7M3l7wP2Aaik0kMbnbd",
                "https://drive.google.com/uc?export=view&id=1oZlQDx8E8oC7MU0Y3FwzwEhiHW0FNTde",
        };

        // Ссылки на части карты БерегПлюсВыходы
        String[] WoodsPlusSCAVimageUrls = {
                "https://drive.google.com/uc?export=view&id=10xOT6cLT-Z3tOx7IByB0IYEw2vfOj7FV",
                "https://drive.google.com/uc?export=view&id=1BG41hTfFDhdIhEPygNHhe2Pb3g-xVOcZ",
                "https://drive.google.com/uc?export=view&id=18c42WezAa80G6YJFfHILSS9r9PTiny5M",
                "https://drive.google.com/uc?export=view&id=1yqyr_04XHHJuaIgTmo8GwgV6Wp6Xrmq-",
                "https://drive.google.com/uc?export=view&id=1rlgL9vRHEdkU_DUarzjJ0mNXFYnpH8gA",
                "https://drive.google.com/uc?export=view&id=15hzZTIY0I5G1kvFEGYGJT_Dz0KTXNmWF",
                "https://drive.google.com/uc?export=view&id=15mhCMHPxqTfANxmjJi457c8tqshGMl-l",
                "https://drive.google.com/uc?export=view&id=1riFxnh8yOg0-nxVJdcmkGULH-1G6fRr7",
                "https://drive.google.com/uc?export=view&id=17Yez09Nmv0fsa4nDHumw_8GSWiHv97aC",
        };


        // Установка карты берег
        loadAndMergeImages(WoodsimageUrls, WoodsimageUrls);

        /*radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
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
        });*/

       /* selectAllButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Логика для загрузки всех изображений
                loadAndMergeImages(WoodsPlusAllimageUrls);
            }
        });

        clearAllButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Логика для очистки всех изображений
                loadAndMergeImages(WoodsimageUrls);
            }
        });*/

        PMCExtractsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Логика для загрузки изображений PMC
                loadAndMergeImages(WoodsimageUrls, WoodsPlusPMCimageUrls);
            }
        });

        scavExtractsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Логика для загрузки изображений SCAV
                loadAndMergeImages(WoodsimageUrls, WoodsPlusSCAVimageUrls);
            }
        });
    }
    // Метод для открытия/закрытия списка
    private void toggleList() {
        // Получаем максимальную высоту списка
        int maxHeight = 100; // Измените это значение на желаемое

        if (isListExpanded) {
            // Если список уже открыт, закрываем его и меняем изображение кнопки на стрелку вниз
            categoryList.setVisibility(View.GONE);
            expandButton.setImageResource(R.drawable.icon_up);
        } else {
            // Если список закрыт, устанавливаем максимальную высоту списка и открываем его
            categoryList.getLayoutParams().height = maxHeight;
            categoryList.requestLayout();
            categoryList.setVisibility(View.VISIBLE);
            expandButton.setImageResource(R.drawable.icon_down);
        }
        // Инвертируем состояние списка
        isListExpanded = !isListExpanded;
    }

    /*
    Сильные Ссылки на Target: Для каждого изображения создается отдельный объект Target, который сохраняется в списке targets.
    Это предотвращает утилизацию объектов Target сборщиком мусора до того, как изображение будет загружено, поскольку на них сохраняются сильные ссылки.
    Задержка в Загрузке: Используется Handler и метод postDelayed для введения задержки между запросами на загрузку изображений.
    Это снижает нагрузку на память и CPU, предотвращая одновременную загрузку всех изображений.
    Задержка в 100 миллисекунд между каждым запросом помогает равномерно распределить загрузку.
    */

    // Метод для загрузки и склеивания изображений
    private void loadAndMergeImages(String[] baseImageUrls, String[] overlayImageUrls) {
        // Показать ProgressBar
        runOnUiThread(() -> progressBar.setVisibility(View.VISIBLE));

        final int baseParts = baseImageUrls.length; // Количество изображений для загрузки из базового набора
        final int overlayParts = overlayImageUrls.length; // Количество изображений для загрузки из накладываемого набора
        final Bitmap[] baseImages = new Bitmap[baseParts]; // Массив для базовых изображений
        final Bitmap[] overlayImages = new Bitmap[overlayParts]; // Массив для накладываемых изображений

        // Общее количество строк и столбцов в склеенном изображении (может быть изменено по вашему усмотрению)
        int totalRows = 3;
        int totalCols = 3;

        final AtomicInteger baseCounter = new AtomicInteger(0); // Счетчик успешно загруженных базовых изображений
        final AtomicInteger overlayCounter = new AtomicInteger(0); // Счетчик успешно загруженных накладываемых изображений

        // Загрузка базовых изображений
        loadImages(baseImageUrls, baseImages, baseCounter, new OnLoadCompleteListener() {
            @Override
            public void onLoadComplete() {
                // Загрузка накладываемых изображений
                loadImages(overlayImageUrls, overlayImages, overlayCounter, new OnLoadCompleteListener() {
                    @Override
                    public void onLoadComplete() {
                        // Все изображения загружены, можно склеить и отобразить
                        mergeAndDisplayImages(baseImages, overlayImages, totalRows, totalCols);
                        // Скрыть ProgressBar
                        runOnUiThread(() -> progressBar.setVisibility(View.GONE));
                    }
                });
            }
        });
    }

    // Интерфейс для обратного вызова при завершении загрузки изображений
    interface OnLoadCompleteListener {
        void onLoadComplete();
    }

    // Метод для загрузки массива изображений
    private void loadImages(String[] imageUrls, Bitmap[] images, AtomicInteger counter, OnLoadCompleteListener listener) {
        final int parts = imageUrls.length;

        for (int i = 0; i < parts; i++) {
            final int index = i;

            Target imageTarget = new Target() {
                @Override
                public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                    images[index] = bitmap;
                    Log.d(TAG, "Изображение загружено: " + imageUrls[index]);
                    if (counter.incrementAndGet() == parts) {
                        listener.onLoadComplete(); // Все изображения загружены
                    }
                }

                @Override
                public void onBitmapFailed(Exception e, Drawable errorDrawable) {
                    Log.e(TAG, "Ошибка загрузки изображения: " + imageUrls[index], e);
                    // Обработка ошибки загрузки (если нужно)
                }

                @Override
                public void onPrepareLoad(Drawable placeHolderDrawable) {
                    Log.d(TAG, "Подготовка к загрузке изображения: " + imageUrls[index]);
                }
            };
            targets.add(imageTarget);
            Picasso.get().load(imageUrls[index]).into(imageTarget);
        }
    }

    // Метод для склеивания и отображения изображений в одно большое
    // Метод для склеивания и отображения изображений
    // Метод для склеивания и отображения изображений
    private void mergeAndDisplayImages(Bitmap[] baseImages, Bitmap[] overlayImages, int totalRows, int totalCols) {
        int width = baseImages[0].getWidth();
        int height = baseImages[0].getHeight();

        // Создание нового большого изображения, которое будет хранить склеенную карту
        Bitmap result = Bitmap.createBitmap(width * totalCols, height * totalRows, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(result);

        for (int row = 0; row < totalRows; row++) {
            for (int col = 0; col < totalCols; col++) {
                // Рассчитываем индекс изображения в базовом массиве
                int baseIndex = row * totalCols + col;
                // Проверяем, существует ли такой индекс в массиве базовых изображений
                if (baseIndex < baseImages.length) {
                    canvas.drawBitmap(baseImages[baseIndex], col * width, row * height, null);
                }
                // Рассчитываем индекс изображения в массиве накладываемых изображений
                int overlayIndex = row * totalCols + col;
                // Проверяем, существует ли такой индекс в массиве накладываемых изображений
                if (overlayIndex < overlayImages.length) {
                    canvas.drawBitmap(overlayImages[overlayIndex], col * width, row * height, null);
                }
            }
        }

        // Устанавливаем склеенное изображение в SubsamplingScaleImageView
        runOnUiThread(() -> {
            SubsamplingScaleImageView imageView = findViewById(R.id.mapImageView);
            imageView.setImage(ImageSource.bitmap(result));
            Log.d(TAG, "Изображения склеены и установлены в SubsamplingScaleImageView");
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
                runOnUiThread(() -> progressBar.setVisibility(View.GONE));
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

    // Метод для возврата на предыдущую страницу
    private void goBack() {
        Log.d(TAG, "goBack: возврат на предыдущую страницу");
        // Возврат на предыдущую страницу
        onBackPressed();
    }

    // Метод для переключения ориентации экрана
    private void toggleFullscreenMap() {
        if (getRequestedOrientation() == ActivityInfo.SCREEN_ORIENTATION_PORTRAIT) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        } else {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }
    }



    // Метод для проверки интернет-соединения
    private void checkInternetConnection() {
        if (isCheckingInternet) {
            return; // Если уже выполняется проверка, выходим из метода
        }
        isCheckingInternet = true;

        if (!isInternetAvailable()) {
            internetWasLost = true;
            if (internetCheckAttempts < 3) {
                showSnackbarWithCountdown();
                internetCheckAttempts++;
            } else {
                showFinalSnackbar();
            }
        } else {
            if (internetWasLost) {
                // Интернет был потерян, но теперь восстановлен
                Snackbar.make(findViewById(android.R.id.content), "Подключение восстановлено", Snackbar.LENGTH_SHORT).show();
                loadAndMergeImages(WoodsimageUrls,WoodsimageUrls);
                internetWasLost = false;
                internetCheckAttempts = 0;
            }
            isCheckingInternet = false;
        }
    }


    // Метод для отображения Snackbar с обратным отсчетом
    private void showSnackbarWithCountdown() {
        final int[] countdownSeconds = {5};
        final Handler handler = new Handler();

        final Snackbar snackbar = Snackbar.make(findViewById(android.R.id.content), "", Snackbar.LENGTH_INDEFINITE);

        final Runnable countdownRunnable = new Runnable() {
            @Override
            public void run() {
                if (countdownSeconds[0] > 0) {
                    snackbar.setText("Нет интернет-соединения. Повторная проверка через " + countdownSeconds[0] + " секунд...");
                    countdownSeconds[0]--;
                    handler.postDelayed(this, 1000);
                } else {
                    snackbar.dismiss();
                    isCheckingInternet = false;
                    checkInternetConnection(); // Повторная проверка
                }
            }
        };

        handler.post(countdownRunnable);
        snackbar.show();
    }


    // Метод для отображения Snackbar с предложением проверить соединение
    private void showFinalSnackbar() {
        Snackbar.make(findViewById(android.R.id.content), "Нет подключения к интернету. Проверить соединение", Snackbar.LENGTH_INDEFINITE)
                .setAction("Проверить", v -> {
                    isCheckingInternet = false;
                    internetCheckAttempts = 0; // Сброс счетчика попыток
                    checkInternetConnection();
                }).show();
    }

    // Метод для проверки доступности интернета
    private boolean isInternetAvailable() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        if (cm != null) {
            NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
            return activeNetwork != null && activeNetwork.isConnectedOrConnecting();
        }
        return false;
    }

    // BroadcastReceiver для обработки изменений состояния сети
    private BroadcastReceiver networkStateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (ConnectivityManager.CONNECTIVITY_ACTION.equals(intent.getAction())) {
                checkInternetConnection(); // Немедленно проверяем состояние сети
            }
        }
    };


    // Обработчики жизненного цикла активности для управления BroadcastReceiver
    @Override
    protected void onResume() {
        super.onResume();
        IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(networkStateReceiver, filter);
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(networkStateReceiver);
    }

}