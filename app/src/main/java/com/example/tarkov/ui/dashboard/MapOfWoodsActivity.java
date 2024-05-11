package com.example.tarkov.ui.dashboard;

import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
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
import java.util.Arrays;
import java.util.List;

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
    private List<ImageView> markers = new ArrayList<>();

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
        String mapImageUrl = "http://213.171.14.43:8000/images/Карта с границами.png";
        loadMapImage(mapImageUrl);

        // Загрузка изображений меток с сервера
        ImageView pmcExtractsMarker = findViewById(R.id.pmc_extracts_marker);

        ImageView scav_extracts_marker = findViewById(R.id.pmc_extracts_marker);

        ImageView boss_marker = findViewById(R.id.boss_marker);

        ImageView cache_marker = findViewById(R.id.cache_marker);

        ImageView corpse_marker = findViewById(R.id.corpse_marker);

        ImageView cultists_marker = findViewById(R.id.cultists_marker);

        ImageView goons_marker = findViewById(R.id.goons_marker);

        ImageView btr_stop_marker = findViewById(R.id.btr_stop_marker);

        ImageView mines_marker = findViewById(R.id.mines_marker);

        ImageView ritual_spot_marker = findViewById(R.id.ritual_spot_marker);

        ImageView ai_scav_marker = findViewById(R.id.ai_scav_marker);

        ImageView scav_sniper_marker = findViewById(R.id.scav_sniper_marker);

        ImageView sniper_marker = findViewById(R.id.sniper_marker);

        ImageView pmc_spawn_marker = findViewById(R.id.pmc_spawn_marker);

        ImageView BloodOfWar3_marker = findViewById(R.id.BloodOfWar3_marker);

        ImageView chumming_marker = findViewById(R.id.chumming_marker);

        ImageView gratitude_marker = findViewById(R.id.gratitude_marker);

        ImageView HCarePriv3_marker = findViewById(R.id.HCarePriv3_marker);

        ImageView InformedMeansArmed_marker = findViewById(R.id.InformedMeansArmed_marker);

        ImageView introduction_marker = findViewById(R.id.introduction_marker);

        ImageView lend_lease_marker = findViewById(R.id.lend_lease_marker);

        ImageView search_mission_marker = findViewById(R.id.search_mission_marker);

        ImageView supply_plans_marker = findViewById(R.id.supply_plans_marker);

        ImageView TheSurvivalistPath_marker = findViewById(R.id.TheSurvivalistPath_marker);

        ImageView scav_spots_marker = findViewById(R.id.scav_spots_marker);

        ImageView pmc_spots_marker = findViewById(R.id.pmc_spots_marker);

        ImageView neutral_spots_marker = findViewById(R.id.neutral_spots_marker);
        markers.addAll(Arrays.asList(pmcExtractsMarker, scav_extracts_marker, boss_marker, cache_marker, corpse_marker, cultists_marker, goons_marker,
                btr_stop_marker, mines_marker, ritual_spot_marker, ai_scav_marker,scav_sniper_marker, sniper_marker, pmc_spawn_marker,
                BloodOfWar3_marker, chumming_marker, gratitude_marker, HCarePriv3_marker, InformedMeansArmed_marker, introduction_marker,
                lend_lease_marker, search_mission_marker, supply_plans_marker, TheSurvivalistPath_marker, scav_spots_marker,
                pmc_spots_marker, neutral_spots_marker));



        // Скрыть все метки по умолчанию
        pmcExtractsMarker.setVisibility(View.GONE);
        scav_extracts_marker.setVisibility(View.GONE);

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
                setActiveButton(PMCExtractsButton);
                setActiveButton(scavExtractsButton);
                setActiveButton(IconLegendBoss);
                setActiveButton(IconLegendBTRStop);
                setActiveButton(IconLegendCache);
                setActiveButton(IconLegendCorpse);
                setActiveButton(IconLegendCultists);
                setActiveButton(IconLegendGoons);
                setActiveButton(IconLegendMines);
                setActiveButton(IconLegendPMCSpawn);
                setActiveButton(IconLegendRitual);
                setActiveButton(IconLegendScavs);
                setActiveButton(IconLegendScavSniper);
                setActiveButton(IconLegendSniper);
                setActiveButton(IconBloodOfWar3);
                setActiveButton(IconChumming);
                setActiveButton(IconGratitude);
                setActiveButton(IconHCarePriv3);
                setActiveButton(IconInformedMeansArmed);
                setActiveButton(IconIntroduction);
                setActiveButton(IconLendLease1);
                setActiveButton(IconSearchMission);
                setActiveButton(IconSupplyPlans);
                setActiveButton(IconThrifty);
                setActiveButton(ScavSpots);
                setActiveButton(IconPMCSpots);
                setActiveButton(IconNeutralSpots);

                // Загрузить и показать все метки
                loadMarkerImage("http://213.171.14.43:8000/images/pmc%20extracts.png", pmcExtractsMarker);
                pmcExtractsMarker.setVisibility(View.VISIBLE);
                loadMarkerImage("http://213.171.14.43:8000/images/scav%20extracts.png", scav_extracts_marker);
                scav_extracts_marker.setVisibility(View.VISIBLE);
                loadMarkerImage("http://213.171.14.43:8000/images/icon_legend_Boss.png", boss_marker);
                boss_marker.setVisibility(View.VISIBLE);
                loadMarkerImage("http://213.171.14.43:8000/images/icon_legend_Btrstop.png", btr_stop_marker);
                btr_stop_marker.setVisibility(View.VISIBLE);
                loadMarkerImage("http://213.171.14.43:8000/images/icon_legend_Cashe.png", cache_marker);
                cache_marker.setVisibility(View.VISIBLE);
                loadMarkerImage("http://213.171.14.43:8000/images/icon_legend_Corpess.png", corpse_marker);
                corpse_marker.setVisibility(View.VISIBLE);
                loadMarkerImage("http://213.171.14.43:8000/images/icon_legend_Cultists.png", cultists_marker);
                cultists_marker.setVisibility(View.VISIBLE);
                loadMarkerImage("http://213.171.14.43:8000/images/icon_legend_Goons.png", goons_marker);
                goons_marker.setVisibility(View.VISIBLE);
                loadMarkerImage("http://213.171.14.43:8000/images/icon_legend_Mines.png", mines_marker);
                mines_marker.setVisibility(View.VISIBLE);
                loadMarkerImage("http://213.171.14.43:8000/images/icon_legend_PMCSpawn.png", pmc_spawn_marker);
                pmc_spawn_marker.setVisibility(View.VISIBLE);
                loadMarkerImage("http://213.171.14.43:8000/images/icon_legend_Ritual.png", ritual_spot_marker);
                ritual_spot_marker.setVisibility(View.VISIBLE);
                loadMarkerImage("http://213.171.14.43:8000/images/icon_legend_Scavs.png", scav_spots_marker);
                scav_spots_marker.setVisibility(View.VISIBLE);
                loadMarkerImage("http://213.171.14.43:8000/images/icon_legend_ScavSniper.png", scav_sniper_marker);
                scav_sniper_marker.setVisibility(View.VISIBLE);
                loadMarkerImage("http://213.171.14.43:8000/images/icon_legend_Sniper.png", sniper_marker);
                sniper_marker.setVisibility(View.VISIBLE);
                loadMarkerImage("http://213.171.14.43:8000/images/icon_BloodOfWar3(Quests).png", BloodOfWar3_marker);
                BloodOfWar3_marker.setVisibility(View.VISIBLE);
                loadMarkerImage("http://213.171.14.43:8000/images/icon_chumming(Quests).png", chumming_marker);
                chumming_marker.setVisibility(View.VISIBLE);
                loadMarkerImage("http://213.171.14.43:8000/images/icon_Gratitude(Quests).png", gratitude_marker);
                gratitude_marker.setVisibility(View.VISIBLE);
                loadMarkerImage("http://213.171.14.43:8000/images/icon_HCarePt3(Quests).png", HCarePriv3_marker);
                HCarePriv3_marker.setVisibility(View.VISIBLE);
                loadMarkerImage("http://213.171.14.43:8000/images/icon_IntormedMeansArmed(Quests).png", InformedMeansArmed_marker);
                InformedMeansArmed_marker.setVisibility(View.VISIBLE);
                loadMarkerImage("http://213.171.14.43:8000/images/icon_Introduction(Quests).png", introduction_marker);
                introduction_marker.setVisibility(View.VISIBLE);
                loadMarkerImage("http://213.171.14.43:8000/images/icon_LendLeasePt1(Quests).png", lend_lease_marker);
                lend_lease_marker.setVisibility(View.VISIBLE);
                loadMarkerImage("http://213.171.14.43:8000/images/icon_SerchMession(Quests).png", search_mission_marker);
                search_mission_marker.setVisibility(View.VISIBLE);
                loadMarkerImage("http://213.171.14.43:8000/images/icon_SupplyPlans(Quests).png", supply_plans_marker);
                supply_plans_marker.setVisibility(View.VISIBLE);
                loadMarkerImage("http://213.171.14.43:8000/images/icon_Thrifty(Quests).png", TheSurvivalistPath_marker);
                TheSurvivalistPath_marker.setVisibility(View.VISIBLE);
                loadMarkerImage("http://213.171.14.43:8000/images/scav%20точки(Another).png", scav_spots_marker);
                scav_spots_marker.setVisibility(View.VISIBLE);
                loadMarkerImage("http://213.171.14.43:8000/images/pmc%20точки(Another).png", pmc_spots_marker);
                pmc_spots_marker.setVisibility(View.VISIBLE);
                loadMarkerImage("http://213.171.14.43:8000/images/netral%20точки(Another).png", neutral_spots_marker);
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

        // Пример обработчика клика для чекбокса "PMC Extracts"
        PMCExtractsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setActiveButton(PMCExtractsButton);
                if (PMCExtractsButton.getVisibility() == View.GONE) {
                    loadMarkerImage("http://213.171.14.43:8000/images/pmc%20extracts.png", pmcExtractsMarker);
                    pmcExtractsMarker.setVisibility(View.VISIBLE);
                } else {
                    pmcExtractsMarker.setVisibility(View.GONE);
                }
            }
        });

        // Обработчики кликов для кнопок:
        scavExtractsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setActiveButton(scavExtractsButton);
                if (scav_extracts_marker.getVisibility() == View.GONE) {
                    loadMarkerImage("http://213.171.14.43:8000/images/scav%20extracts.png", scav_extracts_marker);
                    scav_extracts_marker.setVisibility(View.VISIBLE);
                } else {
                    scav_extracts_marker.setVisibility(View.GONE);
                }
            }
        });

        IconLegendBoss.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setActiveButton(IconLegendBoss);
                if (boss_marker.getVisibility() == View.GONE) {
                    loadMarkerImage("http://213.171.14.43:8000/images/icon_legend_Boss.png", boss_marker);
                    boss_marker.setVisibility(View.VISIBLE);
                } else {
                    boss_marker.setVisibility(View.GONE);
                }
            }
        });

        IconLegendBTRStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setActiveButton(IconLegendBTRStop);
                if (btr_stop_marker.getVisibility() == View.GONE) {
                    loadMarkerImage("http://213.171.14.43:8000/images/icon_legend_Btrstop.png", btr_stop_marker);
                    btr_stop_marker.setVisibility(View.VISIBLE);
                } else {
                    btr_stop_marker.setVisibility(View.GONE);
                }
            }
        });

        IconLegendCache.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setActiveButton(IconLegendCache);
                if (cache_marker.getVisibility() == View.GONE) {
                    loadMarkerImage("http://213.171.14.43:8000/images/icon_legend_Cashe.png", cache_marker);
                    cache_marker.setVisibility(View.VISIBLE);
                } else {
                    cache_marker.setVisibility(View.GONE);
                }
            }
        });

        IconLegendCorpse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setActiveButton(IconLegendCorpse);
                if (corpse_marker.getVisibility() == View.GONE) {
                    loadMarkerImage("http://213.171.14.43:8000/images/icon_legend_Corpess.png", corpse_marker);
                    corpse_marker.setVisibility(View.VISIBLE);
                } else {
                    corpse_marker.setVisibility(View.GONE);
                }
            }
        });

        IconLegendCultists.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setActiveButton(IconLegendCultists);
                if (cultists_marker.getVisibility() == View.GONE) {
                    loadMarkerImage("http://213.171.14.43:8000/images/icon_legend_Cultists.png", cultists_marker);
                    cultists_marker.setVisibility(View.VISIBLE);
                } else {
                    cultists_marker.setVisibility(View.GONE);
                }
            }
        });

        IconLegendGoons.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setActiveButton(IconLegendGoons);
                if (goons_marker.getVisibility() == View.GONE) {
                    loadMarkerImage("http://213.171.14.43:8000/images/icon_legend_Goons.png", goons_marker);
                    goons_marker.setVisibility(View.VISIBLE);
                } else {
                    goons_marker.setVisibility(View.GONE);
                }
            }
        });

        IconLegendMines.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setActiveButton(IconLegendMines);
                if (mines_marker.getVisibility() == View.GONE) {
                    loadMarkerImage("http://213.171.14.43:8000/images/icon_legend_Mines.png", mines_marker);
                    mines_marker.setVisibility(View.VISIBLE);
                } else {
                    mines_marker.setVisibility(View.GONE);
                }
            }
        });

        IconLegendPMCSpawn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setActiveButton(IconLegendPMCSpawn);
                if (pmc_spawn_marker.getVisibility() == View.GONE) {
                    loadMarkerImage("http://213.171.14.43:8000/images/icon_legend_PMCSpawn.png", pmc_spawn_marker);
                    pmc_spawn_marker.setVisibility(View.VISIBLE);
                } else {
                    pmc_spawn_marker.setVisibility(View.GONE);
                }
            }
        });

        IconLegendRitual.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setActiveButton(IconLegendRitual);
                if (ritual_spot_marker.getVisibility() == View.GONE) {
                    loadMarkerImage("http://213.171.14.43:8000/images/icon_legend_Ritual.png", ritual_spot_marker);
                    ritual_spot_marker.setVisibility(View.VISIBLE);
                } else {
                    ritual_spot_marker.setVisibility(View.GONE);
                }
            }
        });

        IconLegendScavs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setActiveButton(IconLegendScavs);
                if (scav_spots_marker.getVisibility() == View.GONE) {
                    loadMarkerImage("http://213.171.14.43:8000/images/icon_legend_Scavs.png", scav_spots_marker);
                    scav_spots_marker.setVisibility(View.VISIBLE);
                } else {
                    scav_spots_marker.setVisibility(View.GONE);
                }
            }
        });

        IconLegendScavSniper.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setActiveButton(IconLegendScavSniper);
                if (scav_sniper_marker.getVisibility() == View.GONE) {
                    loadMarkerImage("http://213.171.14.43:8000/images/icon_legend_ScavSniper.png", scav_sniper_marker);
                    scav_sniper_marker.setVisibility(View.VISIBLE);
                } else {
                    scav_sniper_marker.setVisibility(View.GONE);
                }
            }
        });

        IconLegendSniper.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setActiveButton(IconLegendSniper);
                if (sniper_marker.getVisibility() == View.GONE) {
                    loadMarkerImage("http://213.171.14.43:8000/images/icon_legend_Sniper.png", sniper_marker);
                    sniper_marker.setVisibility(View.VISIBLE);
                } else {
                    sniper_marker.setVisibility(View.GONE);
                }
            }
        });

        IconBloodOfWar3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setActiveButton(IconBloodOfWar3);
                if (BloodOfWar3_marker.getVisibility() == View.GONE) {
                    loadMarkerImage("http://213.171.14.43:8000/images/icon_BloodOfWar3(Quests).png", BloodOfWar3_marker);
                    BloodOfWar3_marker.setVisibility(View.VISIBLE);
                } else {
                    BloodOfWar3_marker.setVisibility(View.GONE);
                }
            }
        });

        IconChumming.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setActiveButton(IconChumming);
                if (chumming_marker.getVisibility() == View.GONE) {
                    loadMarkerImage("http://213.171.14.43:8000/images/icon_chumming(Quests).png", chumming_marker);
                    chumming_marker.setVisibility(View.VISIBLE);
                } else {
                    chumming_marker.setVisibility(View.GONE);
                }
            }
        });

        IconGratitude.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setActiveButton(IconGratitude);
                if (gratitude_marker.getVisibility() == View.GONE) {
                    loadMarkerImage("http://213.171.14.43:8000/images/icon_Gratitude(Quests).png", gratitude_marker);
                    gratitude_marker.setVisibility(View.VISIBLE);
                } else {
                    gratitude_marker.setVisibility(View.GONE);
                }
            }
        });

        IconHCarePriv3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setActiveButton(IconHCarePriv3);
                if (HCarePriv3_marker.getVisibility() == View.GONE) {
                    loadMarkerImage("http://213.171.14.43:8000/images/icon_HCarePt3(Quests).png", HCarePriv3_marker);
                    HCarePriv3_marker.setVisibility(View.VISIBLE);
                } else {
                    HCarePriv3_marker.setVisibility(View.GONE);
                }
            }
        });

        IconInformedMeansArmed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setActiveButton(IconInformedMeansArmed);
                if (InformedMeansArmed_marker.getVisibility() == View.GONE) {
                    loadMarkerImage("http://213.171.14.43:8000/images/icon_IntormedMeansArmed(Quests).png", InformedMeansArmed_marker);
                    InformedMeansArmed_marker.setVisibility(View.VISIBLE);
                } else {
                    InformedMeansArmed_marker.setVisibility(View.GONE);
                }
            }
        });

        IconIntroduction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setActiveButton(IconIntroduction);
                if (introduction_marker.getVisibility() == View.GONE) {
                    loadMarkerImage("http://213.171.14.43:8000/images/icon_Introduction(Quests).png", introduction_marker);
                    introduction_marker.setVisibility(View.VISIBLE);
                } else {
                    introduction_marker.setVisibility(View.GONE);
                }
            }
        });

        IconLendLease1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setActiveButton(IconLendLease1);
                if (lend_lease_marker.getVisibility() == View.GONE) {
                    loadMarkerImage("http://213.171.14.43:8000/images/icon_LendLeasePt1(Quests).png", lend_lease_marker);
                    lend_lease_marker.setVisibility(View.VISIBLE);
                } else {
                    lend_lease_marker.setVisibility(View.GONE);
                }
            }
        });

        IconSearchMission.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setActiveButton(IconSearchMission);
                if (search_mission_marker.getVisibility() == View.GONE) {
                    loadMarkerImage("http://213.171.14.43:8000/images/icon_SerchMession(Quests).png", search_mission_marker);
                    search_mission_marker.setVisibility(View.VISIBLE);
                } else {
                    search_mission_marker.setVisibility(View.GONE);
                }
            }
        });

        IconSupplyPlans.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setActiveButton(IconSupplyPlans);
                if (supply_plans_marker.getVisibility() == View.GONE) {
                    loadMarkerImage("http://213.171.14.43:8000/images/icon_SupplyPlans(Quests).png", supply_plans_marker);
                    supply_plans_marker.setVisibility(View.VISIBLE);
                } else {
                    supply_plans_marker.setVisibility(View.GONE);
                }
            }
        });

        IconThrifty.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setActiveButton(IconThrifty);
                if (TheSurvivalistPath_marker.getVisibility() == View.GONE) {
                    loadMarkerImage("http://213.171.14.43:8000/images/icon_Thrifty(Quests).png", TheSurvivalistPath_marker);
                    TheSurvivalistPath_marker.setVisibility(View.VISIBLE);
                } else {
                    TheSurvivalistPath_marker.setVisibility(View.GONE);
                }
            }
        });

        ScavSpots.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setActiveButton(ScavSpots);
                if (scav_spots_marker.getVisibility() == View.GONE) {
                    loadMarkerImage("http://213.171.14.43:8000/images/scav%20точки(Another).png", scav_spots_marker);
                    scav_spots_marker.setVisibility(View.VISIBLE);
                } else {
                    scav_spots_marker.setVisibility(View.GONE);
                }
            }
        });

        IconPMCSpots.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setActiveButton(IconPMCSpots);
                if (pmc_spots_marker.getVisibility() == View.GONE) {
                    loadMarkerImage("http://213.171.14.43:8000/images/pmc%20точки(Another).png", pmc_spots_marker);
                    pmc_spots_marker.setVisibility(View.VISIBLE);
                } else {
                    pmc_spots_marker.setVisibility(View.GONE);
                }
            }
        });

        IconNeutralSpots.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setActiveButton(IconNeutralSpots);
                if (neutral_spots_marker.getVisibility() == View.GONE) {
                    loadMarkerImage("http://213.171.14.43:8000/images/netral%20точки(Another).png", neutral_spots_marker);
                    neutral_spots_marker.setVisibility(View.VISIBLE);
                } else {
                    neutral_spots_marker.setVisibility(View.GONE);
                }
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

    private void loadMarkerImage(String imageUrl, ImageView imageView) {
        Glide.with(this)
                .load(imageUrl)
                .into(imageView);
    }

    // Метод для возврата на предыдущую страницу
    private void goBack() {
        Log.d(TAG, "goBack: возврат на предыдущую страницу");
        // Возврат на предыдущую страницу
        onBackPressed();
    }
}