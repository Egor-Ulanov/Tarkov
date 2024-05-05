package com.example.tarkov.ui.dashboard;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioGroup;

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
import com.google.android.material.snackbar.Snackbar;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.NetworkPolicy;
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
    private Button activeButton = null;

    // Переменные для обработки загрузки изображений и отслеживания состояния интернет-соединения
    private List<Target> targets = new ArrayList<>();
    private boolean isCheckingInternet = false;
    private boolean internetWasLost = false;
    private int internetCheckAttempts = 0;
    private Handler handler = new Handler();
    private static final String TAG = "MapOfWoodsActivity";
    private Target loadImageTarget; // Поле для сильной ссылки на Target

    // Runnable для периодической проверки состояния интернет-соединения
    /*private final Runnable internetCheckRunnable = new Runnable() {
        @Override
        public void run() {
            checkInternetConnection();
        }
    };*/

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
        ImageButton showMapButton = findViewById(R.id.ShowMap);

        //Metki/////////////////
        String mapImageUrl = "http://213.171.14.43:8000/images/Карта с границами.png";
        loadMapImage(mapImageUrl);

        // Загрузка изображений меток с сервера
        ImageView pmcExtractsMarker = findViewById(R.id.pmc_extracts_marker);
        Picasso.get().load("http://213.171.14.43:8000/images/pmc extracts.png").into(pmcExtractsMarker);
        ImageView scav_extracts_marker = findViewById(R.id.pmc_extracts_marker);
        Picasso.get().load("http://213.171.14.43:8000/images/scav extracts.png").into(scav_extracts_marker);
        ImageView boss_marker = findViewById(R.id.boss_marker);
        Picasso.get().load("http://213.171.14.43:8000/images/icon_legend_Boss.png ").into(boss_marker);
        ImageView cache_marker = findViewById(R.id.cache_marker);
        Picasso.get().load("http://213.171.14.43:8000/images/icon_legend_Cashe.png").into(cache_marker);
        ImageView corpse_marker = findViewById(R.id.corpse_marker);
        Picasso.get().load("http://213.171.14.43:8000/images/icon_legend_Corpess.png").into(corpse_marker);
        ImageView cultists_marker = findViewById(R.id.cultists_marker);
        Picasso.get().load("http://213.171.14.43:8000/images/icon_legend_Cultists.png ").into(cultists_marker);
        ImageView goons_marker = findViewById(R.id.goons_marker);
        Picasso.get().load("http://213.171.14.43:8000/images/icon_legend_Goons.png").into(goons_marker);
        ImageView btr_stop_marker = findViewById(R.id.btr_stop_marker);
        Picasso.get().load("http://213.171.14.43:8000/images/icon_legend_Btrstop.png ").into(btr_stop_marker);
        ImageView mines_marker = findViewById(R.id.mines_marker);
        Picasso.get().load("http://213.171.14.43:8000/images/icon_legend_Mines.png").into(mines_marker);
        ImageView ritual_spot_marker = findViewById(R.id.ritual_spot_marker);
        Picasso.get().load("http://213.171.14.43:8000/images/icon_legend_Ritual.png").into(ritual_spot_marker);
        ImageView ai_scav_marker = findViewById(R.id.ai_scav_marker);
        Picasso.get().load("http://213.171.14.43:8000/images/icon_legend_Scavs.png").into(ai_scav_marker);
        ImageView scav_sniper_marker = findViewById(R.id.scav_sniper_marker);
        Picasso.get().load("http://213.171.14.43:8000/images/icon_legend_ScavSniper.png").into(scav_sniper_marker);
        ImageView sniper_marker = findViewById(R.id.sniper_marker);
        Picasso.get().load("http://213.171.14.43:8000/images/icon_legend_Sniper.png").into(sniper_marker);
        ImageView pmc_spawn_marker = findViewById(R.id.pmc_spawn_marker);
        Picasso.get().load("http://213.171.14.43:8000/images/icon_legend_PMCSpawn.png").into(pmc_spawn_marker);
        ImageView BloodOfWar3_marker = findViewById(R.id.BloodOfWar3_marker);
        Picasso.get().load("http://213.171.14.43:8000/images/icon_BloodOfWar3(Quests).png").into(BloodOfWar3_marker);
        ImageView chumming_marker = findViewById(R.id.chumming_marker);
        Picasso.get().load("http://213.171.14.43:8000/images/icon_chumming(Quests).png").into(chumming_marker);
        ImageView gratitude_marker = findViewById(R.id.gratitude_marker);
        Picasso.get().load("http://213.171.14.43:8000/images/icon_Gratitude(Quests).png").into(gratitude_marker);
        ImageView HCarePriv3_marker = findViewById(R.id.HCarePriv3_marker);
        Picasso.get().load("http://213.171.14.43:8000/images/icon_HCarePt3(Quests).png").into(HCarePriv3_marker);
        ImageView InformedMeansArmed_marker = findViewById(R.id.InformedMeansArmed_marker);
        Picasso.get().load("http://213.171.14.43:8000/images/icon_IntormedMeansArmed(Quests).png").into(InformedMeansArmed_marker);
        ImageView introduction_marker = findViewById(R.id.introduction_marker);
        Picasso.get().load("http://213.171.14.43:8000/images/icon_Introduction(Quests).png").into(introduction_marker);
        ImageView lend_lease_marker = findViewById(R.id.lend_lease_marker);
        Picasso.get().load("http://213.171.14.43:8000/images/icon_LendLeasePt1(Quests).png").into(lend_lease_marker);
        ImageView search_mission_marker = findViewById(R.id.search_mission_marker);
        Picasso.get().load("http://213.171.14.43:8000/images/icon_SerchMession(Quests).png").into(search_mission_marker);
        ImageView supply_plans_marker = findViewById(R.id.supply_plans_marker);
        Picasso.get().load("http://213.171.14.43:8000/images/icon_SupplyPlans(Quests).png").into(supply_plans_marker);
        ImageView TheSurvivalistPath_marker = findViewById(R.id.TheSurvivalistPath_marker);
        Picasso.get().load("http://213.171.14.43:8000/images/icon_Thrifty(Quests).png").into(TheSurvivalistPath_marker);
        ImageView scav_spots_marker = findViewById(R.id.scav_spots_marker);
        Picasso.get().load("http://213.171.14.43:8000/images/scav точки(Another).png").into(scav_spots_marker);
        ImageView pmc_spots_marker = findViewById(R.id.pmc_spots_marker);
        Picasso.get().load("http://213.171.14.43:8000/images/pmc точки(Another).png").into(pmc_spots_marker);
        ImageView neutral_spots_marker = findViewById(R.id.neutral_spots_marker);
        Picasso.get().load("http://213.171.14.43:8000/images/netral точки(Another).png").into(neutral_spots_marker);
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
                // Логика для загрузки всех изображений
                setActiveButton(PMCExtractsButton);
                setActiveButton(scavExtractsButton);
                setActiveButton(IconLegendBoss);
                setActiveButton(IconLegendBTRStop);
                setActiveButton(IconLegendCache);
                setActiveButton(IconLegendCorpse);
                pmcExtractsMarker.setVisibility(View.VISIBLE);
                scav_extracts_marker.setVisibility(View.VISIBLE);
                boss_marker.setVisibility(View.VISIBLE);
                btr_stop_marker.setVisibility(View.VISIBLE);
                cache_marker.setVisibility(View.VISIBLE);
                corpse_marker.setVisibility(View.VISIBLE);
                setActiveButton(IconLegendCultists);
                cultists_marker.setVisibility(View.VISIBLE);
                setActiveButton(IconLegendGoons);
                goons_marker.setVisibility(View.VISIBLE);
                setActiveButton(IconLegendMines);
                mines_marker.setVisibility(View.VISIBLE);
                setActiveButton(IconLegendPMCSpawn);
                pmc_spawn_marker.setVisibility(View.VISIBLE);
                setActiveButton(IconLegendRitual);
                ritual_spot_marker.setVisibility(View.VISIBLE);
                setActiveButton(IconLegendScavs);
                scav_spots_marker.setVisibility(View.VISIBLE);
                setActiveButton(IconLegendScavSniper);
                scav_sniper_marker.setVisibility(View.VISIBLE);
                setActiveButton(IconLegendSniper);
                sniper_marker.setVisibility(View.VISIBLE);
                setActiveButton(IconBloodOfWar3);
                BloodOfWar3_marker.setVisibility(View.VISIBLE);
                setActiveButton(IconChumming);
                chumming_marker.setVisibility(View.VISIBLE);
                setActiveButton(IconGratitude);
                gratitude_marker.setVisibility(View.VISIBLE);
                setActiveButton(IconHCarePriv3);
                HCarePriv3_marker.setVisibility(View.VISIBLE);
                setActiveButton(IconInformedMeansArmed);
                InformedMeansArmed_marker.setVisibility(View.VISIBLE);
                setActiveButton(IconIntroduction);
                introduction_marker.setVisibility(View.VISIBLE);
                setActiveButton(IconLendLease1);
                lend_lease_marker.setVisibility(View.VISIBLE);
                setActiveButton(IconSearchMission);
                search_mission_marker.setVisibility(View.VISIBLE);
                setActiveButton(IconSupplyPlans);
                supply_plans_marker.setVisibility(View.VISIBLE);
                setActiveButton(IconThrifty);
                TheSurvivalistPath_marker.setVisibility(View.VISIBLE);
                setActiveButton(ScavSpots);
                scav_spots_marker.setVisibility(View.VISIBLE);
                setActiveButton(IconPMCSpots);
                pmc_spots_marker.setVisibility(View.VISIBLE);
                setActiveButton(IconNeutralSpots);
                neutral_spots_marker.setVisibility(View.VISIBLE);
            }
        });

            clearAllButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Логика для очистки всех изображений
                    setActiveButton(PMCExtractsButton);
                    setActiveButton(scavExtractsButton);
                    setActiveButton(IconLegendBoss);
                    setActiveButton(IconLegendBTRStop);
                    setActiveButton(IconLegendCache);
                    setActiveButton(IconLegendCorpse);
                    pmcExtractsMarker.setVisibility(View.GONE);
                    scav_extracts_marker.setVisibility(View.GONE);
                    boss_marker.setVisibility(View.GONE);
                    btr_stop_marker.setVisibility(View.GONE);
                    cache_marker.setVisibility(View.GONE);
                    corpse_marker.setVisibility(View.GONE);
                    setActiveButton(IconLegendCultists);
                    cultists_marker.setVisibility(View.GONE);
                    setActiveButton(IconLegendGoons);
                    goons_marker.setVisibility(View.GONE);
                    setActiveButton(IconLegendMines);
                    mines_marker.setVisibility(View.GONE);
                    setActiveButton(IconLegendPMCSpawn);
                    pmc_spawn_marker.setVisibility(View.GONE);
                    setActiveButton(IconLegendRitual);
                    ritual_spot_marker.setVisibility(View.GONE);
                    setActiveButton(IconLegendScavs);
                    scav_spots_marker.setVisibility(View.GONE);
                    setActiveButton(IconLegendScavSniper);
                    scav_sniper_marker.setVisibility(View.GONE);
                    setActiveButton(IconLegendSniper);
                    sniper_marker.setVisibility(View.GONE);
                    setActiveButton(IconBloodOfWar3);
                    BloodOfWar3_marker.setVisibility(View.GONE);
                    setActiveButton(IconChumming);
                    chumming_marker.setVisibility(View.GONE);
                    setActiveButton(IconGratitude);
                    gratitude_marker.setVisibility(View.GONE);
                    setActiveButton(IconHCarePriv3);
                    HCarePriv3_marker.setVisibility(View.GONE);
                    setActiveButton(IconInformedMeansArmed);
                    InformedMeansArmed_marker.setVisibility(View.GONE);
                    setActiveButton(IconIntroduction);
                    introduction_marker.setVisibility(View.GONE);
                    setActiveButton(IconLendLease1);
                    lend_lease_marker.setVisibility(View.GONE);
                    setActiveButton(IconSearchMission);
                    search_mission_marker.setVisibility(View.GONE);
                    setActiveButton(IconSupplyPlans);
                    supply_plans_marker.setVisibility(View.GONE);
                    setActiveButton(IconThrifty);
                    TheSurvivalistPath_marker.setVisibility(View.GONE);
                    setActiveButton(ScavSpots);
                    scav_spots_marker.setVisibility(View.GONE);
                    setActiveButton(IconPMCSpots);
                    pmc_spots_marker.setVisibility(View.GONE);
                    setActiveButton(IconNeutralSpots);
                    neutral_spots_marker.setVisibility(View.GONE);
                }
            });

        PMCExtractsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Логика для загрузки изображений PMC
                setActiveButton(PMCExtractsButton);
                pmcExtractsMarker.setVisibility(View.VISIBLE);
            }
        });

        scavExtractsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Логика для загрузки изображений SCAV
                setActiveButton(scavExtractsButton);
                scav_extracts_marker.setVisibility(View.VISIBLE);
            }
        });
        IconLegendBoss.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Логика для загрузки изображений SCAV
                setActiveButton(IconLegendBoss);
                boss_marker.setVisibility(View.VISIBLE);
            }
        });
        IconLegendBTRStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Логика для загрузки изображений SCAV
                setActiveButton(IconLegendBTRStop);
                btr_stop_marker.setVisibility(View.VISIBLE);
            }
        });
        IconLegendCache.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Логика для загрузки изображений SCAV
                setActiveButton(IconLegendCache);
                cache_marker.setVisibility(View.VISIBLE);
            }
        });
        IconLegendCorpse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Логика для загрузки изображений SCAV
                setActiveButton(IconLegendCorpse);
                corpse_marker.setVisibility(View.VISIBLE);
            }
        });
        IconLegendCultists.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Логика для загрузки изображений SCAV
                setActiveButton(IconLegendCultists);
                cultists_marker.setVisibility(View.VISIBLE);
            }
        });
        IconLegendGoons.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Логика для загрузки изображений SCAV
                setActiveButton(IconLegendGoons);
                goons_marker.setVisibility(View.VISIBLE);
            }
        });
        IconLegendMines.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Логика для загрузки изображений SCAV
                setActiveButton(IconLegendMines);
                mines_marker.setVisibility(View.VISIBLE);
            }
        });
        IconLegendPMCSpawn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Логика для загрузки изображений SCAV
                setActiveButton(IconLegendPMCSpawn);
                pmc_spawn_marker.setVisibility(View.VISIBLE);
            }
        });
        IconLegendRitual.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Логика для загрузки изображений SCAV
                setActiveButton(IconLegendRitual);
                ritual_spot_marker.setVisibility(View.VISIBLE);
            }
        });
        IconLegendScavs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Логика для загрузки изображений SCAV
                setActiveButton(IconLegendScavs);
                scav_spots_marker.setVisibility(View.VISIBLE);
            }
        });
        IconLegendScavSniper.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Логика для загрузки изображений SCAV
                setActiveButton(IconLegendScavSniper);
                scav_sniper_marker.setVisibility(View.VISIBLE);
            }
        });
        IconLegendSniper.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Логика для загрузки изображений SCAV
                setActiveButton(IconLegendSniper);
                sniper_marker.setVisibility(View.VISIBLE);
            }
        });
        IconBloodOfWar3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Логика для загрузки изображений SCAV
                setActiveButton(IconBloodOfWar3);
                BloodOfWar3_marker.setVisibility(View.VISIBLE);
            }
        });
        IconChumming.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Логика для загрузки изображений SCAV
                setActiveButton(IconChumming);
                chumming_marker.setVisibility(View.VISIBLE);
            }
        });
        IconGratitude.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Логика для загрузки изображений SCAV
                setActiveButton(IconGratitude);
                gratitude_marker.setVisibility(View.VISIBLE);
            }
        });
        IconHCarePriv3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Логика для загрузки изображений SCAV
                setActiveButton(IconHCarePriv3);
                HCarePriv3_marker.setVisibility(View.VISIBLE);
            }
        });
        IconInformedMeansArmed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Логика для загрузки изображений SCAV
                setActiveButton(IconInformedMeansArmed);
                InformedMeansArmed_marker.setVisibility(View.VISIBLE);
            }
        });
        IconIntroduction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Логика для загрузки изображений SCAV
                setActiveButton(IconIntroduction);
                introduction_marker.setVisibility(View.VISIBLE);
            }
        });
        IconLendLease1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Логика для загрузки изображений SCAV
                setActiveButton(IconLendLease1);
                lend_lease_marker.setVisibility(View.VISIBLE);
            }
        });
        IconSearchMission.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Логика для загрузки изображений SCAV
                setActiveButton(IconSearchMission);
                search_mission_marker.setVisibility(View.VISIBLE);
            }
        });
        IconSupplyPlans.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Логика для загрузки изображений SCAV
                setActiveButton(IconSupplyPlans);
                supply_plans_marker.setVisibility(View.VISIBLE);
            }
        });
        IconThrifty.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Логика для загрузки изображений SCAV
                setActiveButton(IconThrifty);
                TheSurvivalistPath_marker.setVisibility(View.VISIBLE);
            }
        });
        ScavSpots.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Логика для загрузки изображений SCAV
                setActiveButton(ScavSpots);
                scav_spots_marker.setVisibility(View.VISIBLE);
            }
        });
        IconPMCSpots.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Логика для загрузки изображений SCAV
                setActiveButton(IconPMCSpots);
                pmc_spots_marker.setVisibility(View.VISIBLE);
            }
        });
        IconNeutralSpots.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Логика для загрузки изображений SCAV
                setActiveButton(IconNeutralSpots);
                neutral_spots_marker.setVisibility(View.VISIBLE);
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
        // Установка слушателя кликов на кнопку
        showMapButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Проверяем текущую видимость карты
                if (imageView.getVisibility() == View.VISIBLE) {
                    // Если карта видима, скрываем её
                    imageView.setVisibility(View.GONE);
                } else {
                    // Если карта скрыта, показываем её
                    imageView.setVisibility(View.VISIBLE);
                }
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
        if (activeButton != null) {
            activeButton.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#FFC165")));
        }
        activeButton = button;
        activeButton.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#cec7a6")));
    }
    // Метод для загрузки и склеивания изображений


    // Интерфейс для обратного вызова при завершении загрузки изображений


    // Метод для загрузки массива изображений


    // Метод для склеивания и отображения изображений в одно большое

    //New code//
    /*private void loadMapImage(String imageUrl, ImageView imageView) {
        Log.d(TAG, "loadMapImage: попытка загрузить изображение из " + imageUrl);
        // Создание Target и сохранение сильной ссылки
        this.loadImageTarget = new Target() {
            @Override
            public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                imageView.setImageBitmap(bitmap);
                runOnUiThread(() -> progressBar.setVisibility(View.GONE));  // Скрыть ProgressBar
            }

            @Override
            public void onBitmapFailed(Exception e, Drawable errorDrawable) {
                Log.e(TAG, "Ошибка загрузки изображения", e);
                runOnUiThread(() -> progressBar.setVisibility(View.GONE));  // Скрыть ProgressBar
                // Показать запасное изображение или сообщение об ошибке
                // Например, imageView.setImage(ImageSource.resource(R.drawable.placeholder));
            }

            @Override
            public void onPrepareLoad(Drawable placeHolderDrawable) {
                // Подготовка к загрузке
            }
        };

        // Использование Picasso для загрузки изображения
        Picasso.get().load(imageUrl).networkPolicy(NetworkPolicy.NO_CACHE)
                .memoryPolicy(MemoryPolicy.NO_CACHE).into(this.loadImageTarget);
    }*/


    private void loadMapImage(String imageUrl) {
        Log.d(TAG, "loadMapImage: попытка загрузить изображение из " + imageUrl);

        Glide.with(this)
                .asBitmap() // Загружаем как Bitmap
                .load(imageUrl)
                .into(new CustomTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                        imageView.setImage(ImageSource.bitmap(resource));  // Устанавливаем изображение в SubsamplingScaleImageView
                        runOnUiThread(() -> progressBar.setVisibility(View.GONE));  // Скрыть ProgressBar
                    }

                    @Override
                    public void onLoadCleared(@Nullable Drawable placeholder) {
                        // Этот метод вызывается, когда Glide освобождает ресурсы
                    }
                });
    }

    // Метод для возврата на предыдущую страницу
    private void goBack() {
        Log.d(TAG, "goBack: возврат на предыдущую страницу");
        // Возврат на предыдущую страницу
        onBackPressed();
    }

    // Метод для переключения ориентации экрана

    // Метод для проверки интернет-соединения
    /*private void checkInternetConnection() {
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
    }*/

}