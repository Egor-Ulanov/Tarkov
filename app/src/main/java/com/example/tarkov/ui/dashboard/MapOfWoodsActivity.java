package com.example.tarkov.ui.dashboard;

import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.drawable.DrawableCompat;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.davemorrissey.labs.subscaleview.ImageSource;
import com.davemorrissey.labs.subscaleview.SubsamplingScaleImageView;
import com.example.tarkov.R;
import com.squareup.picasso.Target;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

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
    private Button activeButton = null;

    // Переменные для обработки загрузки изображений и отслеживания состояния интернет-соединения
    private List<Target> targets = new ArrayList<>();
    private boolean isCheckingInternet = false;
    private boolean internetWasLost = false;
    private int internetCheckAttempts = 0;
    private Handler handler = new Handler();
    private static final String TAG = "MapOfWoodsActivity";
    private Target loadImageTarget; // Поле для сильной ссылки на Target

    private Bitmap mapBitmap;

    private Map<String, Bitmap> loadedMarkers = new HashMap<>();

    private static final String BASE_URL = "http://213.171.14.43:8000/images/";

    private int markersToLoad = 0; // Счетчик маркеров, которые нужно загрузить

    // Runnable для периодической проверки состояния интернет-соединения
    /*private final Runnable internetCheckRunnable = new Runnable() {
        @Override
        public void run() {
            checkInternetConnection();
        }
    };*/
//    private List<ImageView> markers = new ArrayList<>();

    private List<Bitmap> markerBitmaps = new ArrayList<>();
    private List<String> markerUrls = new ArrayList<>();

    private Set<String> activeFilters = new HashSet<>();

    private LinearLayout buttonsContainer;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Скрыть ActionBar
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }
        super.onCreate(savedInstanceState);
        loadThemePreference();
        setContentView(R.layout.map_of_woods);
        expandButton = findViewById(R.id.expandButton);
        categoryList = findViewById(R.id.categoryList);
        Button IconGratitude = findViewById(R.id.IconGratitude);
        Button selectAllButton = findViewById(R.id.selectAllButton);
        Button clearAllButton = findViewById(R.id.clearAllButton);
        Button PMCExtractsButton = findViewById(R.id.PMCExtractsButton);
        Button scavExtractsButton = findViewById(R.id.scavExtractsButton);
        Button IconLegendBoss = findViewById(R.id.IconLegendBoss);
        Button IconLegendBTRStop = findViewById(R.id.IconLegendBTRStop);
        Button IconLegendCache = findViewById(R.id.IconLegendCache);
        Button IconLegendCorpse = findViewById(R.id.IconLegendCorpse);
        Button IconLegendCultists = findViewById(R.id.IconLegendCultists);
        Button IconLegendGoons = findViewById(R.id.IconLegendGoons);
        Button IconLegendMines = findViewById(R.id.IconLegendMines);
        Button IconLegendPMCSpawn = findViewById(R.id.IconLegendPMCSpawn);
        Button IconLegendRitual = findViewById(R.id.IconLegendRitual);
        Button IconLegendScavs = findViewById(R.id.IconLegendScavs);
        Button IconLegendScavSniper = findViewById(R.id.IconLegendScavSniper);
        Button IconLegendSniper = findViewById(R.id.IconLegendSniper);
        Button IconBloodOfWar3 = findViewById(R.id.IconBloodOfWar3);
        Button IconChumming = findViewById(R.id.IconChumming);
        Button IconHCarePriv3 = findViewById(R.id.IconHCarePriv3);
        Button IconInformedMeansArmed = findViewById(R.id.IconInformedMeansArmed);
        Button IconIntroduction = findViewById(R.id.IconIntroduction);
        Button IconLendLease1 = findViewById(R.id.IconLendLease1);
        Button IconSearchMission = findViewById(R.id.IconSearchMission);
        Button IconSupplyPlans = findViewById(R.id.IconSupplyPlans);
        Button IconThrifty = findViewById(R.id.IconThrifty);
        Button ScavSpots = findViewById(R.id.IconScavSpots);
        Button IconPMCSpots = findViewById(R.id.IconPMCSpots);
        Button IconNeutralSpots = findViewById(R.id.IconNeutralSpots);
        buttonsContainer  = findViewById(R.id.buttonsAll);
        ImageButton plusButton = findViewById(R.id.PlusMach);
        ImageButton minusButton = findViewById(R.id.MinusMach);


        //Metki/////////////////
        // Загрузка карты без меток при старте
        String mapImageUrl = "http://213.171.14.43:8000/images/Карта с границами.png";
        loadMapImage(mapImageUrl);

//        // Загрузка изображений меток с сервера
//        ImageView pmcExtractsMarker = findViewById(R.id.pmc_extracts_marker);
//
//        ImageView scav_extracts_marker = findViewById(R.id.pmc_extracts_marker);
//
//        ImageView boss_marker = findViewById(R.id.boss_marker);
//
//        ImageView cache_marker = findViewById(R.id.cache_marker);
//
//        ImageView corpse_marker = findViewById(R.id.corpse_marker);
//
//        ImageView cultists_marker = findViewById(R.id.cultists_marker);
//
//        ImageView goons_marker = findViewById(R.id.goons_marker);
//
//        ImageView btr_stop_marker = findViewById(R.id.btr_stop_marker);
//
//        ImageView mines_marker = findViewById(R.id.mines_marker);
//
//        ImageView ritual_spot_marker = findViewById(R.id.ritual_spot_marker);
//
//        ImageView ai_scav_marker = findViewById(R.id.ai_scav_marker);
//
//        ImageView scav_sniper_marker = findViewById(R.id.scav_sniper_marker);
//
//        ImageView sniper_marker = findViewById(R.id.sniper_marker);
//
//        ImageView pmc_spawn_marker = findViewById(R.id.pmc_spawn_marker);
//
//        ImageView BloodOfWar3_marker = findViewById(R.id.BloodOfWar3_marker);
//
//        ImageView chumming_marker = findViewById(R.id.chumming_marker);
//
//        ImageView gratitude_marker = findViewById(R.id.gratitude_marker);
//
//        ImageView HCarePriv3_marker = findViewById(R.id.HCarePriv3_marker);
//
//        ImageView InformedMeansArmed_marker = findViewById(R.id.InformedMeansArmed_marker);
//
//        ImageView introduction_marker = findViewById(R.id.introduction_marker);
//
//        ImageView lend_lease_marker = findViewById(R.id.lend_lease_marker);
//
//        ImageView search_mission_marker = findViewById(R.id.search_mission_marker);
//
//        ImageView supply_plans_marker = findViewById(R.id.supply_plans_marker);
//
//        ImageView TheSurvivalistPath_marker = findViewById(R.id.TheSurvivalistPath_marker);
//
//        ImageView scav_spots_marker = findViewById(R.id.scav_spots_marker);
//
//        ImageView pmc_spots_marker = findViewById(R.id.pmc_spots_marker);
//
//        ImageView neutral_spots_marker = findViewById(R.id.neutral_spots_marker);
//        markers.addAll(Arrays.asList(pmcExtractsMarker, scav_extracts_marker, boss_marker, cache_marker, corpse_marker, cultists_marker, goons_marker,
//                btr_stop_marker, mines_marker, ritual_spot_marker, ai_scav_marker,scav_sniper_marker, sniper_marker, pmc_spawn_marker,
//                BloodOfWar3_marker, chumming_marker, gratitude_marker, HCarePriv3_marker, InformedMeansArmed_marker, introduction_marker,
//                lend_lease_marker, search_mission_marker, supply_plans_marker, TheSurvivalistPath_marker, scav_spots_marker,
//                pmc_spots_marker, neutral_spots_marker));



//        // Скрыть все метки по умолчанию
//        pmcExtractsMarker.setVisibility(View.GONE);
//        scav_extracts_marker.setVisibility(View.GONE);

        //Metki/////////////////

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
        //handler.postDelayed(internetCheckRunnable, 1000);
        // Ссылки на части карты берег

        // Установка карты берег


        selectAllButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Показать все метки
                showAllMarkers();
            }
        });

        clearAllButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Скрыть все метки
                hideAllMarkers();
            }
        });



        PMCExtractsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setActiveButton(PMCExtractsButton);

                loadMarker("pmc_extracts", BASE_URL + "pmc%20extracts.png");
            }
        });

        scavExtractsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setActiveButton(scavExtractsButton);
                loadMarker("scav_extracts", BASE_URL + "scav%20extracts.png");
            }
        });

        IconLegendBoss.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setActiveButton(IconLegendBoss);
                loadMarker("boss", BASE_URL + "icon_legend_Boss.png");
            }
        });

        IconLegendBTRStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setActiveButton(IconLegendBTRStop);
                loadMarker("btr_stop", BASE_URL + "icon_legend_Btrstop.png");
            }
        });

        IconLegendCache.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setActiveButton(IconLegendCache);
                loadMarker("cache", BASE_URL + "icon_legend_Cashe.png");
            }
        });

        IconLegendCorpse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setActiveButton(IconLegendCorpse);
                loadMarker("corpse", BASE_URL + "icon_legend_Corpess.png");
            }
        });

        IconLegendCultists.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setActiveButton(IconLegendCultists);
                loadMarker("cultists", BASE_URL + "icon_legend_Cultists.png");
            }
        });

        IconLegendGoons.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setActiveButton(IconLegendGoons);
                loadMarker("goons", BASE_URL + "icon_legend_Goons.png");
            }
        });

        IconLegendMines.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setActiveButton(IconLegendMines);
                loadMarker("mines", BASE_URL + "icon_legend_Mines.png");
            }
        });

        IconLegendPMCSpawn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setActiveButton(IconLegendPMCSpawn);
                loadMarker("pmc_spawn", BASE_URL + "icon_legend_PMCSpawn.png");
            }
        });

        IconLegendRitual.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setActiveButton(IconLegendRitual);
                loadMarker("ritual", BASE_URL + "icon_legend_Ritual.png");
            }
        });

        IconLegendScavs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setActiveButton(IconLegendScavs);
                loadMarker("scavs", BASE_URL + "icon_legend_Scavs.png");
            }
        });

        IconLegendScavSniper.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setActiveButton(IconLegendScavSniper);
                loadMarker("scav_sniper", BASE_URL + "icon_legend_ScavSniper.png");
            }
        });

        IconLegendSniper.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setActiveButton(IconLegendSniper);
                loadMarker("sniper", BASE_URL + "icon_legend_Sniper.png");
            }
        });

        IconBloodOfWar3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setActiveButton(IconBloodOfWar3);
                loadMarker("blood_of_war3", BASE_URL + "icon_BloodOfWar3(Quests).png");
            }
        });

        IconChumming.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setActiveButton(IconChumming);
                loadMarker("chumming", BASE_URL + "icon_chumming(Quests).png");
            }
        });

        IconGratitude.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setActiveButton(IconGratitude);
                loadMarker("gratitude", BASE_URL + "icon_Gratitude(Quests).png");
            }
        });

        IconHCarePriv3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setActiveButton(IconHCarePriv3);
                loadMarker("hcare_priv3", BASE_URL + "icon_HCarePt3(Quests).png");
            }
        });

        IconInformedMeansArmed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setActiveButton(IconInformedMeansArmed);
                loadMarker("informed_means_armed", BASE_URL + "icon_IntormedMeansArmed(Quests).png");
            }
        });

        IconIntroduction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setActiveButton(IconIntroduction);
                loadMarker("introduction", BASE_URL + "icon_Introduction(Quests).png");
            }
        });

        IconLendLease1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setActiveButton(IconLendLease1);
                loadMarker("lend_lease1", BASE_URL + "icon_LendLeasePt1(Quests).png");
            }
        });

        IconSearchMission.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setActiveButton(IconSearchMission);
                loadMarker("search_mission", BASE_URL + "icon_SerchMession(Quests).png");
            }
        });

        IconSupplyPlans.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setActiveButton(IconSupplyPlans);
                loadMarker("supply_plans", BASE_URL + "icon_SupplyPlans(Quests).png");
            }
        });

        IconThrifty.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setActiveButton(IconThrifty);
                loadMarker("thrifty", BASE_URL + "icon_Thrifty(Quests).png");
            }
        });

        ScavSpots.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setActiveButton(ScavSpots);
                loadMarker("scav_spots", BASE_URL + "scav%20точки(Another).png");
            }
        });

        IconPMCSpots.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setActiveButton(IconPMCSpots);
                loadMarker("pmc_spots", BASE_URL + "pmc%20точки(Another).png");
            }
        });

        IconNeutralSpots.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setActiveButton(IconNeutralSpots);
                loadMarker("neutral_spots", BASE_URL + "netral%20точки(Another).png");
            }
        });

        plusButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Увеличить масштаб карты
                zoomIn();
            }
        });
        minusButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Уменьшить масштаб карты
                zoomOut();
            }
        });
    }

    private void zoomIn() {
        // Получаем текущий масштаб карты
        float currentZoom = imageView.getScale();

        // Увеличиваем масштаб карты на 0.5
        float newZoom = currentZoom + 0.5f;

        // Ограничиваем максимальный масштаб карты
        if (newZoom > imageView.getMaxScale()) {
            newZoom = imageView.getMaxScale();
        }

        // Устанавливаем новый масштаб карты
        imageView.setScaleAndCenter(newZoom, imageView.getCenter());
    }

    private void zoomOut() {
        // Получаем текущий масштаб карты
        float currentZoom = imageView.getScale();

        // Уменьшаем масштаб карты на 0.5
        float newZoom = currentZoom - 0.5f;

        // Ограничиваем минимальный масштаб карты
        if (newZoom < imageView.getMinScale()) {
            newZoom = imageView.getMinScale();
        }

        // Устанавливаем новый масштаб карты
        imageView.setScaleAndCenter(newZoom, imageView.getCenter());
    }


    // Метод для открытия/закрытия списка
    private void toggleList() {
        // Получаем максимальную высоту списка
        int maxHeight = 100; // Измените это значение на желаемое

        if (isListExpanded) {
            // Если список уже открыт, закрываем его и меняем изображение кнопки на стрелку вниз
            categoryList.setVisibility(View.GONE);
            buttonsContainer.setVisibility(View.GONE);
            expandButton.setImageResource(R.drawable.icon_up);
        } else {
            // Если список закрыт, устанавливаем максимальную высоту списка и открываем его
            categoryList.getLayoutParams().height = maxHeight;
            categoryList.requestLayout();
            categoryList.setVisibility(View.VISIBLE);
            buttonsContainer.setVisibility(View.VISIBLE);
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
    private void setActiveButton(Button button) {
        Boolean isSelected = (Boolean) button.getTag(); // Проверяем, активна ли кнопка

        if (isSelected == null || !isSelected) {
            // Если кнопка не была выбрана, делаем ее активной
            button.setTag(true);
            button.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#FFC165"))); // Устанавливаем цвет активности
        } else {
            // Если кнопка уже была выбрана, делаем ее неактивной
            button.setTag(false);
            button.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#cec7a6"))); // Устанавливаем цвет неактивности
        }
    }

//    private void loadMapImage(String imageUrl) {
//        Log.d(TAG, "loadMapImage: Загрузка основной карты началась...");
//        Glide.with(this)
//                .asBitmap()
//                .load(imageUrl)
//                .into(new CustomTarget<Bitmap>() {
//                    @Override
//                    public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
//                        Log.d(TAG, "loadMapImage: Основная карта успешно загружена!");
//                        mapBitmap = resource; // Сохраняем изображение карты
//                        imageView.setImage(ImageSource.bitmap(resource));
//                        runOnUiThread(() -> progressBar.setVisibility(View.GONE));
//                    }
//
//                    @Override
//                    public void onLoadCleared(@Nullable Drawable placeholder) {
//                        Log.d(TAG, "loadMapImage: Основная карта очищена...");
//                    }
//                });
//    }

//    private void loadMarker(String markerName, String markerUrl) {
//        if (loadedMarkers.containsKey(markerName)) {
//            // Маркер уже загружен, используем его
//            updateMapWithMarkers();
//        } else {
//            progressBar.setVisibility(View.VISIBLE);
//            Glide.with(this)
//                    .asBitmap()
//                    .load(markerUrl)
//                    .into(new CustomTarget<Bitmap>() {
//                        @Override
//                        public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
//                            loadedMarkers.put(markerName, resource); // Сохраняем маркер
//                            updateMapWithMarkers();
//                            progressBar.setVisibility(View.GONE);
//                        }
//
//                        @Override
//                        public void onLoadCleared(@Nullable Drawable placeholder) {
//                            progressBar.setVisibility(View.GONE);
//                        }
//                    });
//        }
//    }

//    private void loadMarker(String markerName, String markerUrl) {
//        if (activeFilters.contains(markerName)) {
//            activeFilters.remove(markerName);
//            loadedMarkers.remove(markerName);
//            updateMapWithMarkers();
//        } else {
//            activeFilters.add(markerName);
//            if (!loadedMarkers.containsKey(markerName)) {
//                markersToLoad++;
//                progressBar.setVisibility(View.VISIBLE);
//                Glide.with(this)
//                        .asBitmap()
//                        .load(markerUrl)
//                        .into(new CustomTarget<Bitmap>() {
//                            @Override
//                            public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
//                                Bitmap markerBitmapCopy = resource.copy(resource.getConfig(), true);
//                                loadedMarkers.put(markerName, markerBitmapCopy);
//                                markersToLoad--;
//                                if (markersToLoad == 0) {
//                                    updateMapWithMarkers();
//                                    progressBar.setVisibility(View.GONE);
//                                }
//                            }
//
//                            @Override
//                            public void onLoadCleared(@Nullable Drawable placeholder) {
//                                // Ничего не делаем здесь
//                            }
//                        });
//            } else {
//                // Маркер уже загружен, просто обновляем карту
//                updateMapWithMarkers();
//            }
//        }
//    }

//    private void loadMarker(String markerName, String markerUrl) {
//
//        Log.d(TAG, "loadMarker: Запрос на загрузку маркера " + markerName + " с URL " + markerUrl);
//        if (activeFilters.contains(markerName)) {
//            Log.d(TAG, "loadMarker: Маркер " + markerName + " уже был активен, удаляем его.");
//            activeFilters.remove(markerName);
//            loadedMarkers.remove(markerName);
//            updateMapWithMarkers();
//        } else {
//            Log.d(TAG, "loadMarker: Маркер " + markerName + " не активен, добавляем его.");
//            activeFilters.add(markerName);
//            if (!loadedMarkers.containsKey(markerName)) {
//                Log.d(TAG, "loadMarker: Маркер " + markerName + " не загружен, начинаем загрузку.");
//                markersToLoad++;
//                progressBar.setVisibility(View.VISIBLE);
//                Glide.with(this)
//                        .asBitmap()
//                        .load(markerUrl)
//                        .into(new CustomTarget<Bitmap>() {
//                            @Override
//                            public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
//                                Log.d(TAG, "loadMarker: Маркер " + markerName + " успешно загружен!");
//                                Bitmap markerBitmapCopy = resource.copy(resource.getConfig(), true);
//                                loadedMarkers.put(markerName, markerBitmapCopy);
//                                markersToLoad--;
//                                if (markersToLoad == 0) {
//                                    Log.d(TAG, "loadMarker: Все метки загружены, обновляем карту.");
//                                    updateMapWithMarkers();
//                                    progressBar.setVisibility(View.GONE);
//                                }
//                            }
//
//                            @Override
//                            public void onLoadCleared(@Nullable Drawable placeholder) {
//                                // Ничего не делаем здесь.
//                                Log.d(TAG, "loadMarker: Маркер " + markerName + " очищен.");
//                            }
//                        });
//            } else {
//                Log.d(TAG, "loadMarker: Маркер " + markerName + " уже загружен, обновляем карту.");
//                updateMapWithMarkers();
//            }
//        }
//    }





    private void loadMarkerImage(String imageUrl, ImageView imageView) {
        Glide.with(this)
                .load(imageUrl)
                .into(imageView);
    }
//
//    private void updateMapWithMarkers() {
//        Log.d(TAG, "updateMapWithMarkers: Обновление карты началось...");
//        if (mapBitmap != null) {
//            Log.d(TAG, "updateMapWithMarkers: Основная карта доступна.");
//            Bitmap combinedBitmap = Bitmap.createBitmap(mapBitmap.getWidth(), mapBitmap.getHeight(), mapBitmap.getConfig());
//            Canvas canvas = new Canvas(combinedBitmap);
//
//            canvas.drawBitmap(mapBitmap, 0, 0, null); // Рисуем карту
//
//            Log.d(TAG, "updateMapWithMarkers: Начинаем рисовать метки...");
//            for (Map.Entry<String, Bitmap> entry : loadedMarkers.entrySet()) {
//                String markerName = entry.getKey();
//                Bitmap markerBitmap = entry.getValue();
//
//                if (activeFilters.contains(markerName)) { // Проверяем активность фильтра
//                    Log.d(TAG, "updateMapWithMarkers: Рисуем метку " + markerName);
//                    canvas.drawBitmap(markerBitmap, 0, 0, null);
//                }
//            }
//            Log.d(TAG, "updateMapWithMarkers: Рисование меток завершено.");
//
//            runOnUiThread(() -> {
//                Log.d(TAG, "updateMapWithMarkers: Установка изображения карты на ImageView...");
//                imageView.setImage(ImageSource.bitmap(combinedBitmap));
//            });
//        }
//    }

//    private void setActiveButton(Button button) {
//        boolean isSelected = button.getTag() != null && (boolean) button.getTag();
//        if (isSelected) {
//            button.setTag(false);
//            button.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#cec7a6")));
//        } else {
//            button.setTag(true);
//            button.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#FFC165")));
//        }
//    }

    private void loadMapImage(String imageUrl) {
        Log.d(TAG, "loadMapImage: Загрузка основной карты началась...");
        Glide.with(this)
                .asBitmap()
                .load(imageUrl)
                .into(new CustomTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                        Log.d(TAG, "loadMapImage: Основная карта успешно загружена!");
                        mapBitmap = resource.copy(resource.getConfig(), true);
                        imageView.setImage(ImageSource.bitmap(mapBitmap));
                        runOnUiThread(() -> progressBar.setVisibility(View.GONE));
                    }

                    @Override
                    public void onLoadCleared(@Nullable Drawable placeholder) {
                        Log.d(TAG, "loadMapImage: Основная карта очищена...");
                    }
                });
    }

    private void loadMarker(String markerName, String markerUrl) {
        Log.d(TAG, "loadMarker: Запрос на загрузку маркера " + markerName + " с URL " + markerUrl);
        if (activeFilters.contains(markerName)) {
            Log.d(TAG, "loadMarker: Маркер " + markerName + " уже был активен, удаляем его.");
            activeFilters.remove(markerName);
            loadedMarkers.remove(markerName);
            reloadMapAndMarkers(true);
        } else {
            Log.d(TAG, "loadMarker: Маркер " + markerName + " не активен, добавляем его.");
            activeFilters.add(markerName);
            if (!loadedMarkers.containsKey(markerName)) {
                Log.d(TAG, "loadMarker: Маркер " + markerName + " не загружен, начинаем загрузку.");
                markersToLoad++;
                progressBar.setVisibility(View.VISIBLE);
                Glide.with(this)
                        .asBitmap()
                        .load(markerUrl)
                        .into(new CustomTarget<Bitmap>() {
                            @Override
                            public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                                Log.d(TAG, "loadMarker: Маркер " + markerName + " успешно загружен!");
                                Bitmap markerBitmapCopy = resource.copy(resource.getConfig(), true);
                                loadedMarkers.put(markerName, markerBitmapCopy);
                                markersToLoad--;
                                if (markersToLoad == 0) {
                                    Log.d(TAG, "loadMarker: Все метки загружены, обновляем карту.");
                                    reloadMapAndMarkers(true);
                                    progressBar.setVisibility(View.GONE);
                                }
                            }

                            @Override
                            public void onLoadCleared(@Nullable Drawable placeholder) {
                                Log.d(TAG, "loadMarker: Маркер " + markerName + " очищен.");
                            }
                        });
            } else {
                Log.d(TAG, "loadMarker: Маркер " + markerName + " уже загружен, обновляем карту.");
                reloadMapAndMarkers(true);
            }
        }
    }

    private void reloadMapAndMarkers(boolean updateMarkers) {
        Log.d(TAG, "reloadMapAndMarkers: Перезагрузка карты...");
        loadMapImage(BASE_URL + "Карта с границами.png", updateMarkers ? this::updateMapWithMarkers : null);
    }

    private void loadMapImage(String imageUrl, @Nullable Runnable onMapLoaded) {
        Log.d(TAG, "loadMapImage: Загрузка основной карты началась...");
        Glide.with(this)
                .asBitmap()
                .load(imageUrl)
                .into(new CustomTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                        Log.d(TAG, "loadMapImage: Основная карта успешно загружена!");
                        mapBitmap = resource.copy(resource.getConfig(), true);
                        imageView.setImage(ImageSource.bitmap(mapBitmap));
                        runOnUiThread(() -> progressBar.setVisibility(View.GONE));
                        if (onMapLoaded != null) {
                            onMapLoaded.run();
                        }
                    }

                    @Override
                    public void onLoadCleared(@Nullable Drawable placeholder) {
                        Log.d(TAG, "loadMapImage: Основная карта очищена...");
                    }
                });
    }

    private void updateMapWithMarkers() {
        Log.d(TAG, "updateMapWithMarkers: Обновление карты началось...");
        if (mapBitmap != null && !mapBitmap.isRecycled()) {
            Log.d(TAG, "updateMapWithMarkers: Основная карта доступна.");
            Bitmap combinedBitmap = Bitmap.createBitmap(mapBitmap.getWidth(), mapBitmap.getHeight(), mapBitmap.getConfig());
            Canvas canvas = new Canvas(combinedBitmap);

            canvas.drawBitmap(mapBitmap, 0, 0, null);

            Log.d(TAG, "updateMapWithMarkers: Начинаем рисовать метки...");
            for (Map.Entry<String, Bitmap> entry : loadedMarkers.entrySet()) {
                String markerName = entry.getKey();
                Bitmap markerBitmap = entry.getValue();

                if (activeFilters.contains(markerName) && markerBitmap != null && !markerBitmap.isRecycled()) {
                    Log.d(TAG, "updateMapWithMarkers: Рисуем метку " + markerName);
                    canvas.drawBitmap(markerBitmap, 0, 0, null);
                }
            }
            Log.d(TAG, "updateMapWithMarkers: Рисование меток завершено.");

            runOnUiThread(() -> {
                Log.d(TAG, "updateMapWithMarkers: Установка изображения карты на ImageView...");
                imageView.setImage(ImageSource.bitmap(combinedBitmap));
            });
        }
    }

    private void showAllMarkers() {
        // Список всех кнопок и их соответствующих URL меток
        Map<Button, String[]> allButtonsWithUrls = new HashMap<>();
        allButtonsWithUrls.put(findViewById(R.id.PMCExtractsButton), new String[]{"pmc_extracts", BASE_URL + "pmc%20extracts.png"});
        allButtonsWithUrls.put(findViewById(R.id.scavExtractsButton), new String[]{"scav_extracts", BASE_URL + "scav%20extracts.png"});
        allButtonsWithUrls.put(findViewById(R.id.IconLegendBoss), new String[]{"boss", BASE_URL + "icon_legend_Boss.png"});
        allButtonsWithUrls.put(findViewById(R.id.IconLegendBTRStop), new String[]{"btr_stop", BASE_URL + "icon_legend_Btrstop.png"});
        allButtonsWithUrls.put(findViewById(R.id.IconLegendCache), new String[]{"cache", BASE_URL + "icon_legend_Cashe.png"});
        allButtonsWithUrls.put(findViewById(R.id.IconLegendCorpse), new String[]{"corpse", BASE_URL + "icon_legend_Corpess.png"});
        allButtonsWithUrls.put(findViewById(R.id.IconLegendCultists), new String[]{"cultists", BASE_URL + "icon_legend_Cultists.png"});
        allButtonsWithUrls.put(findViewById(R.id.IconLegendGoons), new String[]{"goons", BASE_URL + "icon_legend_Goons.png"});
        allButtonsWithUrls.put(findViewById(R.id.IconLegendMines), new String[]{"mines", BASE_URL + "icon_legend_Mines.png"});
        allButtonsWithUrls.put(findViewById(R.id.IconLegendPMCSpawn), new String[]{"pmc_spawn", BASE_URL + "icon_legend_PMCSpawn.png"});
        allButtonsWithUrls.put(findViewById(R.id.IconLegendRitual), new String[]{"ritual", BASE_URL + "icon_legend_Ritual.png"});
        allButtonsWithUrls.put(findViewById(R.id.IconLegendScavs), new String[]{"scavs", BASE_URL + "icon_legend_Scavs.png"});
        allButtonsWithUrls.put(findViewById(R.id.IconLegendScavSniper), new String[]{"scav_sniper", BASE_URL + "icon_legend_ScavSniper.png"});
        allButtonsWithUrls.put(findViewById(R.id.IconLegendSniper), new String[]{"sniper", BASE_URL + "icon_legend_Sniper.png"});
        allButtonsWithUrls.put(findViewById(R.id.IconBloodOfWar3), new String[]{"blood_of_war3", BASE_URL + "icon_BloodOfWar3(Quests).png"});
        allButtonsWithUrls.put(findViewById(R.id.IconChumming), new String[]{"chumming", BASE_URL + "icon_chumming(Quests).png"});
        allButtonsWithUrls.put(findViewById(R.id.IconGratitude), new String[]{"gratitude", BASE_URL + "icon_Gratitude(Quests).png"});
        allButtonsWithUrls.put(findViewById(R.id.IconHCarePriv3), new String[]{"hcare_priv3", BASE_URL + "icon_HCarePt3(Quests).png"});
        allButtonsWithUrls.put(findViewById(R.id.IconInformedMeansArmed), new String[]{"informed_means_armed", BASE_URL + "icon_IntormedMeansArmed(Quests).png"});
        allButtonsWithUrls.put(findViewById(R.id.IconIntroduction), new String[]{"introduction", BASE_URL + "icon_Introduction(Quests).png"});
        allButtonsWithUrls.put(findViewById(R.id.IconLendLease1), new String[]{"lend_lease1", BASE_URL + "icon_LendLeasePt1(Quests).png"});
        allButtonsWithUrls.put(findViewById(R.id.IconSearchMission), new String[]{"search_mission", BASE_URL + "icon_SerchMession(Quests).png"});
        allButtonsWithUrls.put(findViewById(R.id.IconSupplyPlans), new String[]{"supply_plans", BASE_URL + "icon_SupplyPlans(Quests).png"});
        allButtonsWithUrls.put(findViewById(R.id.IconThrifty), new String[]{"thrifty", BASE_URL + "icon_Thrifty(Quests).png"});
        allButtonsWithUrls.put(findViewById(R.id.IconScavSpots), new String[]{"scav_spots", BASE_URL + "scav%20точки(Another).png"});
        allButtonsWithUrls.put(findViewById(R.id.IconPMCSpots), new String[]{"pmc_spots", BASE_URL + "pmc%20точки(Another).png"});
        allButtonsWithUrls.put(findViewById(R.id.IconNeutralSpots), new String[]{"neutral_spots", BASE_URL + "netral%20точки(Another).png"});

        // Сначала скрываем все метки
        hideAllMarkers();

        // Активируем все кнопки и загружаем все метки
        for (Map.Entry<Button, String[]> entry : allButtonsWithUrls.entrySet()) {
            Button button = entry.getKey();
            String[] markerData = entry.getValue();
            String markerName = markerData[0];
            String markerUrl = markerData[1];
            setButtonActive(button, true); // Активируем кнопку
            loadMarker(markerName, markerUrl); // Загружаем метку
        }
    }

    private void hideAllMarkers() {
        // Список всех кнопок
        Button[] allButtons = {
                findViewById(R.id.PMCExtractsButton), findViewById(R.id.scavExtractsButton), findViewById(R.id.IconLegendBoss),
                findViewById(R.id.IconLegendBTRStop), findViewById(R.id.IconLegendCache), findViewById(R.id.IconLegendCorpse),
                findViewById(R.id.IconLegendCultists), findViewById(R.id.IconLegendGoons), findViewById(R.id.IconLegendMines),
                findViewById(R.id.IconLegendPMCSpawn), findViewById(R.id.IconLegendRitual), findViewById(R.id.IconLegendScavs),
                findViewById(R.id.IconLegendScavSniper), findViewById(R.id.IconLegendSniper), findViewById(R.id.IconBloodOfWar3),
                findViewById(R.id.IconChumming), findViewById(R.id.IconGratitude), findViewById(R.id.IconHCarePriv3),
                findViewById(R.id.IconInformedMeansArmed), findViewById(R.id.IconIntroduction), findViewById(R.id.IconLendLease1),
                findViewById(R.id.IconSearchMission), findViewById(R.id.IconSupplyPlans), findViewById(R.id.IconThrifty),
                findViewById(R.id.IconScavSpots), findViewById(R.id.IconPMCSpots), findViewById(R.id.IconNeutralSpots)
        };

        // Деактивировать все кнопки и очистить активные фильтры и маркеры
        for (Button button : allButtons) {
            setButtonActive(button, false); // Деактивируем кнопку
        }

        activeFilters.clear();
        loadedMarkers.clear();

        // Загрузка карты без меток
        String mapImageUrl = BASE_URL + "Карта с границами.png";
        loadMapImage(mapImageUrl);
    }

    private void setButtonActive(Button button, boolean isActive) {
        button.setTag(isActive);
        if (isActive) {
            button.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#FFC165"))); // Устанавливаем цвет активности
        } else {
            button.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#cec7a6"))); // Устанавливаем цвет неактивности
        }
    }


    // Метод для возврата на предыдущую страницу
    private void goBack() {
        Log.d(TAG, "goBack: возврат на предыдущую страницу");
        // Возврат на предыдущую страницу
        onBackPressed();
    }


}