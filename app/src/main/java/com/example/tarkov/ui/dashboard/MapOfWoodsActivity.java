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
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioGroup;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.drawable.DrawableCompat;

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

        // Для rotateButton
        /*Drawable rotateDrawable = ContextCompat.getDrawable(this, R.drawable.ic_full_dis_24dp);
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
        String[] WoodsPlusBossimageUrls = {
                "https://drive.google.com/uc?export=view&id=13MtM0fWaIdoBOFFIfmtUmnMMTs9DCOAh",
                "https://drive.google.com/uc?export=view&id=1fKNjctswZHKAWvwpIl9R4pgAPHP2mrkS",
                "https://drive.google.com/uc?export=view&id=1R6zvnRDVVw0i2DghiKVtYyoc8FXztO97",
                "https://drive.google.com/uc?export=view&id=1BT-EwYAH1VEdovHQbca84Cfz8xSo1IAo",
                "https://drive.google.com/uc?export=view&id=1LVvHGsr6dUH0scXJvGWpnYZ_DWf5BLSK",
                "https://drive.google.com/uc?export=view&id=1OThID64kp7h1xXJ7ysYoPoyBU1JvVMA8",
                "https://drive.google.com/uc?export=view&id=1Kci2OlrG3wpelLGGtjGJtPqWkcwCoAYL",
                "https://drive.google.com/uc?export=view&id=1v9Uh5SGXiDWUykzXuQZ3_e9jagO4e0V_",
                "https://drive.google.com/uc?export=view&id=1d2zna06KNVl2PxibUHn_zPOIU4L0ezHO",
        };
        String[] WoodsPlusBTRStopimageUrls = {
                "https://drive.google.com/uc?export=view&id=1YwiGex-Fifvm4B3l46n8qdNc7DgcBoj9",
                "https://drive.google.com/uc?export=view&id=1DTks60PT4ofHaRLkmaR42EdYTG5O2WCB",
                "https://drive.google.com/uc?export=view&id=15xmBfrjBCublJLl5h7T8BhmeVWpsfqp-",
                "https://drive.google.com/uc?export=view&id=16ORQ3yrg9bqXrdMytCr2V9H4P_R7-kB5",
                "https://drive.google.com/uc?export=view&id=1J-siV9Y4JwpbmdlChJBgpyDQrYymKktH",
                "https://drive.google.com/uc?export=view&id=1QOtQeFSopBmSSqGfDWVfHiGTAF9pgsuC",
                "https://drive.google.com/uc?export=view&id=1SMFpHWRVZHB1pQzSI9DguWjfHSm4OuVT",
                "https://drive.google.com/uc?export=view&id=1s0xtHg3o9Dgq2fFvFLA18zHfOGuFbeGu",
                "https://drive.google.com/uc?export=view&id=15i71AdnpaeaPxLhQH78MLglItvCdIsaH",
        };
        String[] WoodsPlusCasheimageUrls = {
                "https://drive.google.com/uc?export=view&id=1VwAyNTCn0Briy5ZP3_6BHZEcRngz4D01",
                "https://drive.google.com/uc?export=view&id=1s1Ki8ZpvSHKCvy4n7L5pxE4eKBRe37ce",
                "https://drive.google.com/uc?export=view&id=13okj_IuQGOm4tfmmiKS7XjZXXOBmrzsy",
                "https://drive.google.com/uc?export=view&id=1Df0Du_0NrZl3srZcARx-GFqB_4i2hlas",
                "https://drive.google.com/uc?export=view&id=1jcZEUmTGiokwH7tdx2ylrhTeHqvMErYp",
                "https://drive.google.com/uc?export=view&id=1nWNNaCgAwc-9WnyWHszk6rwLBDY-su5L",
                "https://drive.google.com/uc?export=view&id=1547i4vvRikpdJ4HYGN-IiP1MLIXaIFwy",
                "https://drive.google.com/uc?export=view&id=1UyAuEsKWL8tB5-XfgTmzrGALGiVpR2Et",
                "https://drive.google.com/uc?export=view&id=1l0aW-T9aT65QPpB1FZPXZgDLOvMcKTHN",
        };
        String[] WoodsPlusCorpseimageUrls = {
                "https://drive.google.com/uc?export=view&id=1cf_hEkuzivpjjaAW77luY_VEXnj1P1XK",
                "https://drive.google.com/uc?export=view&id=13_HR-YM6-EwlLjtipnaUSW8a39Igs0Zr",
                "https://drive.google.com/uc?export=view&id=1tVWYp3EDp1mQeiIiFow6g2HiMZulovKY",
                "https://drive.google.com/uc?export=view&id=1lQ5sO01AqaCCURFeAW3LwPRIeJqsn_2x",
                "https://drive.google.com/uc?export=view&id=1NmE-H6f-16ZvB9UeXPk_O8CKfQTljiKB",
                "https://drive.google.com/uc?export=view&id=1d7jkd5s_VH3WH_bY0kJtcey161kKhoE7",
                "https://drive.google.com/uc?export=view&id=1pjTPMBIuAB1hBnSv58jEowco-dZ1STaj",
                "https://drive.google.com/uc?export=view&id=1etmOhMsZPLKk9z9hzW8_9MhaZDltC7eY",
                "https://drive.google.com/uc?export=view&id=1mKrKny_eAod1x6naLom5yYT6cK6drHB7",
        };
        String[] WoodsPlusCultistsimageUrls = {
                "https://drive.google.com/uc?export=view&id=1zgk5HJETKwufN6rLKzh95d4q9s4jPaFb",
                "https://drive.google.com/uc?export=view&id=1dyr4zOn7lqaO_cqArzaHm5inyets-vVF",
                "https://drive.google.com/uc?export=view&id=1Jw9iCDOJxVbRqlaRq7c_6msGFisCrt3e",
                "https://drive.google.com/uc?export=view&id=1aLOT-Al3TXIn-BPZkA2I1-KvE-T4ZeoG",
                "https://drive.google.com/uc?export=view&id=10Buu_C3LM3YKvCb0a19dO_oXFSrU0aUX",
                "https://drive.google.com/uc?export=view&id=1IJkJQj7OgSnccfh7e1ltSdsE_9KwzafJ",
                "https://drive.google.com/uc?export=view&id=1w8s0KhuBdmsWuHFKgTEaIBYgd4MF1-oH",
                "https://drive.google.com/uc?export=view&id=1zbQmLF-t2Ri1FVSN4htreZf36Uony7NW",
                "https://drive.google.com/uc?export=view&id=1r9CmMZTWkvFrGYFcxxVuhk-YrBNoazY8",
        };
        String[] WoodsPlusGoonsimageUrls = {
                "https://drive.google.com/uc?export=view&id=1QkAcSlTD5KxF0-9I-mlT4Fsf0_1N8khy",
                "https://drive.google.com/uc?export=view&id=1VKtTj5a8vOmHXYgS_X9ip1SedU9JR6RO",
                "https://drive.google.com/uc?export=view&id=1rJOE6-QUMhyWKwMrkclMCeOeZPUzrZ5a",
                "https://drive.google.com/uc?export=view&id=14Em7pPtoeOisX59hYH9kvcU2vRE0CsyK",
                "https://drive.google.com/uc?export=view&id=1g-6SIwVq5tBGMnNE1L3KXdcUj2m6iIOb",
                "https://drive.google.com/uc?export=view&id=1qMCbnnwghUt8W3DaEto1WZ5bYRw8q_W_",
                "https://drive.google.com/uc?export=view&id=1jbu7Uag3f59ZQKbidFTpf5cEFAOBhYP2",
                "https://drive.google.com/uc?export=view&id=1CL7mV0MliMANzS7VvvyQhuilB7a2neVR",
                "https://drive.google.com/uc?export=view&id=1qGuSUK4R7Vqy4z2NZuwNGYZxuimUnWxk",
        };
        String[] WoodsPlusMinesimageUrls = {
                "https://drive.google.com/uc?export=view&id=1ecHfv-JbvqTEEyOFS-dnaqoJNGiR-Qw3",
                "https://drive.google.com/uc?export=view&id=1itxCY9YonfNclB4Qbj5lU-xmtdDZonk3",
                "https://drive.google.com/uc?export=view&id=1yQJewxdiro0zI6iIWJGr5sWN2OW1tPfR",
                "https://drive.google.com/uc?export=view&id=1pvhCydGGZ5RXPSAsP1txPopeRN8zXNPK",
                "https://drive.google.com/uc?export=view&id=1b8oZrXaOUiQ_1sGx6oZLpEMXU_V1tuxz",
                "https://drive.google.com/uc?export=view&id=17flrsh_RC8OMeep-6LQ7axx-SUxIdmPF",
                "https://drive.google.com/uc?export=view&id=18xVPLr0DS4lxtnLe2AGaUY3I20iln7CC",
                "https://drive.google.com/uc?export=view&id=1nYW6UJsuiweVnmVh15sKG242SWtY754y",
                "https://drive.google.com/uc?export=view&id=1aluRXDyPvr8awiRsU_7R05Tpp2zFsi4b",
        };
        String[] WoodsPlusPMCSpawnimageUrls = {
                "https://drive.google.com/uc?export=view&id=1xI00iCSrjsy8F7QzN9Du3XHd56aCTYH0",
                "https://drive.google.com/uc?export=view&id=1cyX4QjympwBv6OQVS3vEKiLQaTlSxZ-S",
                "https://drive.google.com/uc?export=view&id=1qW31Uhe345Wzvv3I4YALLFYLuLS8ZMT5",
                "https://drive.google.com/uc?export=view&id=17vb43l3NS5zIuxH6kxy8S4CFgQLeri_Q",
                "https://drive.google.com/uc?export=view&id=1FoYTpHbdcj6De6SkoLf1sLYYQe1FuSRY",
                "https://drive.google.com/uc?export=view&id=1lZDPKl_Z1f9Aymb7NNu5HblZi9Nk05EC",
                "https://drive.google.com/uc?export=view&id=1SlFNlNY_cu0q8uL_5xudCPH1H9ypdUr_",
                "https://drive.google.com/uc?export=view&id=1TydJ0WhePoSqRkO6O1ByYW1lpddUN_sg",
                "https://drive.google.com/uc?export=view&id=18sgHnS9KJRvz7AAK24cd8BqzNHPRXW51",
        };
        String[] WoodsPlusRitualimageUrls = {
                "https://drive.google.com/uc?export=view&id=1pYScd3jvomWX0R5S3I08HoJIVcLHSstm",
                "https://drive.google.com/uc?export=view&id=1HHzuiMc8lZPI3GaH9bUdQolT6BSiNW1d",
                "https://drive.google.com/uc?export=view&id=13KubU4ow0XE1-XeJnsNTI6so5cASITUP",
                "https://drive.google.com/uc?export=view&id=1lKCxHHEnI8_TNJPdbNYaGigr6ezzWD1p",
                "https://drive.google.com/uc?export=view&id=1DDRRhRwb09IRdPzPM2J8Ajlpfuclf2vC",
                "https://drive.google.com/uc?export=view&id=125xk2ftKCUrbNx5pM_iRCl-JBK2xdUtR",
                "https://drive.google.com/uc?export=view&id=1PxdwI6HSb8hDVSISaOi1ib1gu0u8eN2H",
                "https://drive.google.com/uc?export=view&id=1VR06z39Mjx3fc_8M0M7Sa4wX2NKyVBOe",
                "https://drive.google.com/uc?export=view&id=1HNvIHN3XbZFXmpFUcB6V_i6h8aKqnSsp",
        };
        String[] WoodsPlusScavsimageUrls = {
                "https://drive.google.com/uc?export=view&id=1wcbt_1ma6HutTp4CTMnBAZ0RVj4Y-qXl",
                "https://drive.google.com/uc?export=view&id=1Sbm9UEFwLgWxb7pgTuBCxz-rZq2g46fo",
                "https://drive.google.com/uc?export=view&id=1ewT1uDsu-3MkSLC4NOZzXM8fgWTwVBxj",
                "https://drive.google.com/uc?export=view&id=1b-uG4RylpWE4PNo1bYqzYeWhmDrBBwwN",
                "https://drive.google.com/uc?export=view&id=1IveNPhFrYYlw9fl70ukisP0NJJ_xGOKI",
                "https://drive.google.com/uc?export=view&id=1xh3dUteyXAnxWp0jeB4BolJmqqdJ2uSh",
                "https://drive.google.com/uc?export=view&id=1lvLevHCLICs4IvdjBGWV_FAzE-NaG7sS",
                "https://drive.google.com/uc?export=view&id=1MJumt-B6EUI7-L4FdYxDWgPUOY-2NCHj",
                "https://drive.google.com/uc?export=view&id=1-eCuJ4h6RgF7lQaG2n0vH1jMCvjbZthY",
        };
        String[] WoodsPlusScavSniperimageUrls = {
                "https://drive.google.com/uc?export=view&id=1atOUJJ2OImFCwetlrQXIuf40-nUxoB0e",
                "https://drive.google.com/uc?export=view&id=1AS9qEs96_GbFm_NiVM8iLl24SAaEhUe5",
                "https://drive.google.com/uc?export=view&id=1ZzVdF9JEsELaml4X7r8lEQVPQyk7rEBZ",
                "https://drive.google.com/uc?export=view&id=1wb96v8ZOsxIxma__Hynf5VtULAB8tJst",
                "https://drive.google.com/uc?export=view&id=180pR9GBuLCAEbO8lx7BwbJgxtCxNxsWd",
                "https://drive.google.com/uc?export=view&id=1h32sE3lamzp4GpkQ5pVI05yjzxF8ET5r",
                "https://drive.google.com/uc?export=view&id=1iC6Igf_zeNsVgOHAR8up0JYPSvvX0xK2",
                "https://drive.google.com/uc?export=view&id=1L_jbvkdzCIrVuDKXzHwtIMhQHAuSRb4w",
                "https://drive.google.com/uc?export=view&id=12GzCl29yrbQAjMGKNnPfZMwoghF39yhw",
        };
        String[] WoodsPlusSniperimageUrls = {
                "https://drive.google.com/uc?export=view&id=1I0Z-0e6b9RdppC1cKCQSug-1qIU2Tflb",
                "https://drive.google.com/uc?export=view&id=1jSXQ-ITMNZHLgB3RhdGBGRp8K0QnyqIH",
                "https://drive.google.com/uc?export=view&id=1cMFU1NuI8VQJfzKTNsPdOnhwn4EK1MpI",
                "https://drive.google.com/uc?export=view&id=1vCptF_2IQOrpem0NTBAoxep3niEr_79_",
                "https://drive.google.com/uc?export=view&id=11xHloUQpN6eUi43OGiPBb0MjypNbd1e5",
                "https://drive.google.com/uc?export=view&id=1I8D_dQAfqCBWPMlfe2VJEYfc90rHSSkm",
                "https://drive.google.com/uc?export=view&id=190-IjaHS-qQ6Cjf-nf_JDvIs3EhrYa9i",
                "https://drive.google.com/uc?export=view&id=1jASmjT45OtaFl37UTZhuImEGiVBT5L9a",
                "https://drive.google.com/uc?export=view&id=1GNY6UQY3y-WBfNxCMtTtLheojQJNEoBg",
        };
        String[] WoodsPlusBloodOfWar3imageUrls = {
                "https://drive.google.com/uc?export=view&id=1abxFGlQ5-lo096hgLWsr2UokonBnR4Uw",
                "https://drive.google.com/uc?export=view&id=1gfZEKDz3FfMX8jhR0FuJbv4f4Z4EfQb-",
                "https://drive.google.com/uc?export=view&id=1L6XsEdV5tPjnWBKBoZqIM8wygoxkLUkR",
                "https://drive.google.com/uc?export=view&id=12tEdxc_raM5CzC0Hewk7BhC-I2uwtpcE",
                "https://drive.google.com/uc?export=view&id=1m9ZJFBN40OZSqOT91yVS9WS_jd_onZDj",
                "https://drive.google.com/uc?export=view&id=11TZNn6CklHYUIJc5m4yMOlqldJLmsvA4",
                "https://drive.google.com/uc?export=view&id=1E5c0vAJxo6FkMOyXn-mYP9APWlhWfSkL",
                "https://drive.google.com/uc?export=view&id=1FtkWMxmMwqZ8QU39FQYW87JjiPH2hp5P",
                "https://drive.google.com/uc?export=view&id=1AH0Vp4hz7ceTUll5xOwntPWKEkY3jklA",
        };
        String[] WoodsPlusChummingimageUrls = {
                "https://drive.google.com/uc?export=view&id=1XHOJQqty1P83ZRrzgYv6zQl4FhKZnkG_",
                "https://drive.google.com/uc?export=view&id=1SuLfM0cRfkhP1YNMwxmTHHYHmxZO7CVX",
                "https://drive.google.com/uc?export=view&id=1Pv-NAOrJvWg3J-ntxhtSIJjh-upCo5ny",
                "https://drive.google.com/uc?export=view&id=1fT5f4mI86aNfVY9bKfiJnVvWQxtOxcPV",
                "https://drive.google.com/uc?export=view&id=1UPF9Vw2KleF8yEDuVdX3_bqlzHSegVFC",
                "https://drive.google.com/uc?export=view&id=16eH4zp-BaDq47oEBh8053ZyZ4uqrtB_a",
                "https://drive.google.com/uc?export=view&id=1GBWYZlkZZ58h9V83IfOfagGyrPDbfn7w",
                "https://drive.google.com/uc?export=view&id=1wi6vm6JieLCLotbijDM_F6gfUui4ZJBQ",
                "https://drive.google.com/uc?export=view&id=1mkvVq_CjA4NafivNptDDWb9_TnL8_j79",
        };
        String[] WoodsPlusGratitudeimageUrls = {
                "https://drive.google.com/uc?export=view&id=1Ui-HtonyVGjjcg4Ap29v01rd24cK_5LG",
                "https://drive.google.com/uc?export=view&id=1KrsvekL6YDqqAAq4EeTGR7sZRYrX0rBp",
                "https://drive.google.com/uc?export=view&id=11OPJiH3zZ0CAcqDaJYBTTefAkQKc2-nC",
                "https://drive.google.com/uc?export=view&id=1bM4EnpMBsWyFhJlSix65k38S4VfTzHCF",
                "https://drive.google.com/uc?export=view&id=1shZj3cDWnFmb7VsoCwYJwTJ938cB6629",
                "https://drive.google.com/uc?export=view&id=1P5TY-3xQCLaJ6CUDrTtwlofvNy_j6bvY",
                "https://drive.google.com/uc?export=view&id=10W_3ZXlGO5kdePOso9clPwNX2RGF2rrB",
                "https://drive.google.com/uc?export=view&id=1qHhCsy97_22Gzu8yUxnhQlA9wi01Ye48",
                "https://drive.google.com/uc?export=view&id=1hCahqKmZBTDaAvW67tqK55gc1wpAGjMV",
        };
        String[] WoodsPlusHCarePriv3imageUrls = {
                "https://drive.google.com/uc?export=view&id=1W_nNR2oo9iCMF5iJgn_6kxo3UZs0Ndbf",
                "https://drive.google.com/uc?export=view&id=1nLUqCHLeTdUblSNQNKkqx7Z8Fi3dHVpM",
                "https://drive.google.com/uc?export=view&id=1pb1emNNUtvhz22teQSvX7hklwhlJETu7",
                "https://drive.google.com/uc?export=view&id=1p5itYj4wmdiLcrpkiiM8tD-NfLpU78jo",
                "https://drive.google.com/uc?export=view&id=1MKrsYEqHZxvX-X0g0qRFeYyt4L4jS3PI",
                "https://drive.google.com/uc?export=view&id=1495LAkQIKsOrALbVtdNTRmFloIq-4pmi",
                "https://drive.google.com/uc?export=view&id=1AdZoxVb0ncIrxSqntzQmMAbEHnnKck4w",
                "https://drive.google.com/uc?export=view&id=1H4gIMyhx3glWNJZZWIY37AIbO97XxgGz",
                "https://drive.google.com/uc?export=view&id=1vEFG-xMoRuAMaKvIq4mcqAhZ-4xgBPB1",
        };
        String[] WoodsPlusInformedMeansArmedimageUrls = {
                "https://drive.google.com/uc?export=view&id=1GwuIcg0TmZvNvTsCoLmsQB_1mfmMkkIa",
                "https://drive.google.com/uc?export=view&id=11enaFt46ZLOZuETOBB5U-5ySOn_My6c3",
                "https://drive.google.com/uc?export=view&id=17YgNZNdrXFK3bDNSCq0KPiZjuJ0vjSsC",
                "https://drive.google.com/uc?export=view&id=1-26cqpV0dpMJKTeCVUOGlzp7f3aozPhn",
                "https://drive.google.com/uc?export=view&id=1V5vVgmhAwAc4eCEluocNNWyuZGKIyJoH",
                "https://drive.google.com/uc?export=view&id=16QKeTNmwe1kCSCi_KapFV8VdnmtRnYOb",
                "https://drive.google.com/uc?export=view&id=1Lr9IqNL7n9T5awVwCNQS1B7ZXet2LHp-",
                "https://drive.google.com/uc?export=view&id=1Oq09wNC28FUqIPSoKDDgQDQZmLXAKjU6",
                "https://drive.google.com/uc?export=view&id=12k0BxwPh7i_PRMipU67sZ9VNZ4qPBgMD",
        };
        String[] WoodsPlusIntroductionimageUrls = {
                "https://drive.google.com/uc?export=view&id=1RUoOFM-OjTXFsj-2FF5JisoSpC35Zkd9",
                "https://drive.google.com/uc?export=view&id=1foiWmBz6d0bDXVaBYT3A4x2UWY9nMnZw",
                "https://drive.google.com/uc?export=view&id=1TSpEWiwXoX2WTtIMMlyGDSTBmAgdxTQh",
                "https://drive.google.com/uc?export=view&id=1753bGFmPTROoMcy-IKTyr2frT7oYU4Qs",
                "https://drive.google.com/uc?export=view&id=1DaJ7fZ6364-4AlQ2XSVsGVIcPU4N7Ws0",
                "https://drive.google.com/uc?export=view&id=1Q470hOUf2705elMZfQ0mTdgzQ9Sis1uW",
                "https://drive.google.com/uc?export=view&id=1s-JpLUah1uswRAjJgmQQYXfYVDEoxlpY",
                "https://drive.google.com/uc?export=view&id=1cBgbf8mmfwYWb-9bbMCKoDNLadY9-6Zy",
                "https://drive.google.com/uc?export=view&id=1TP1CwOnJQ687XCzQOfSL02IlPTaEJvEu",
        };
        String[] WoodsPlusLendLease1imageUrls = {
                "https://drive.google.com/uc?export=view&id=1tmSGEXa8R-aHu5YyIA3awPTyNeyOi4xu",
                "https://drive.google.com/uc?export=view&id=1edWKN9mONqIJ3SqG8PfhuuRBdK0uytzT",
                "https://drive.google.com/uc?export=view&id=1GzQpGUv4gx3zm4t8h8M_dBkd_zl3qu6X",
                "https://drive.google.com/uc?export=view&id=1C2hw1uf_hbW3jeeRMNTM4Ub0UB8XOo_t",
                "https://drive.google.com/uc?export=view&id=1CzAOyWGgMNE3pf9RgoGInB7ciwWbaTQj",
                "https://drive.google.com/uc?export=view&id=1rSMfKQwHhTjE6G780UVkldj1hltEydqP",
                "https://drive.google.com/uc?export=view&id=16h8IOBbLTRb5EuLd1umPzmcaJT600O5Q",
                "https://drive.google.com/uc?export=view&id=1wscG9TIsnKXLjs_68oMh_hyaKPxI2hQb",
                "https://drive.google.com/uc?export=view&id=1nKtgZQHm1-bcsr-ptNVmSat2B0lJphTU",
        };
        String[] WoodsPlusSearchMissionimageUrls = {
                "https://drive.google.com/uc?export=view&id=1NEyyq1odd1sL0uWIWKhot4DWNjGygfU7",
                "https://drive.google.com/uc?export=view&id=1ksId_Xn3IbUUxH5gIfOI4LirEtE-Ndww",
                "https://drive.google.com/uc?export=view&id=1jnIgJqbFmq94lozICIMHJJ6V4oY0oBYu",
                "https://drive.google.com/uc?export=view&id=1IBhL9H4LTgdUF22NZp84O_-6_ZGvNdqE",
                "https://drive.google.com/uc?export=view&id=1-NrA5UA9q4m99JD5liGHd9iVMIqhhVvg",
                "https://drive.google.com/uc?export=view&id=1qvck1DvSQJ3BnHwWF1n9nW4RLslgESWO",
                "https://drive.google.com/uc?export=view&id=1UcqglaHJ8ejOcfdKvk2ZuZPB0mEtvxLh",
                "https://drive.google.com/uc?export=view&id=1MZn-1qr3QscGrBY2mEgW6mc_7q4F04oD",
                "https://drive.google.com/uc?export=view&id=1Kqhixxfhlfb6hYLOnd9J2TWr-_wBGhfF",
        };
        String[] WoodsPlusSupplyPlansimageUrls = {
                "https://drive.google.com/uc?export=view&id=1TH6NAQx7pBqn8EsFw1TSw1gG3lokCAMA",
                "https://drive.google.com/uc?export=view&id=1UHMIosf8hJjmas4I8tLLoqId3lthvAp0",
                "https://drive.google.com/uc?export=view&id=17wlM9lmoU0vJs1X3wO-irkKJdL8c3q7g",
                "https://drive.google.com/uc?export=view&id=1auFrBYhFlOC1y0kK8Hr6RK-XWSToW1l3",
                "https://drive.google.com/uc?export=view&id=1Uxy7GqpTiHwxldZMg64on5XV9jl6o54q",
                "https://drive.google.com/uc?export=view&id=1DKBglkk7sMsWCfOGXBNq1PHrrM6Fyi0Q",
                "https://drive.google.com/uc?export=view&id=1cJcdASgxuwfWW6iyUcQt8o02E4tO-jw7",
                "https://drive.google.com/uc?export=view&id=18SmLlXH_aNUKfXWAQTrjx9M_w1Zy8z8z",
                "https://drive.google.com/uc?export=view&id=17GnrqjL1jDN4bYs5DrK8whyIzG6JVl3B",
        };
        String[] WoodsPlusThriftyimageUrls = {
                "https://drive.google.com/uc?export=view&id=1VwjxXMPKJjDD4JJkpIuyNd2ldOTaC9Ey",
                "https://drive.google.com/uc?export=view&id=1NA7oYV27iJ5KpG9uBvJnShiaV-eXSsrv",
                "https://drive.google.com/uc?export=view&id=1IYhlrA1L5kbJw95zGfTkn4fhhn7zJpiu",
                "https://drive.google.com/uc?export=view&id=1K5uieLvNrgqHtizXPnFyMXXFuoD7w8gx",
                "https://drive.google.com/uc?export=view&id=1RXz9AhdsMxRllYVUGjH6evFFy7cH2scK",
                "https://drive.google.com/uc?export=view&id=11LrBTkp2v39rhlKllrmQ1zp_Pwli6BtP",
                "https://drive.google.com/uc?export=view&id=1ORvEm1AzoRm2IssVV4DomiTCgIA8QJv1",
                "https://drive.google.com/uc?export=view&id=1XSR5R3AFRz55Rh8zYsWx3zody8sUYihH",
                "https://drive.google.com/uc?export=view&id=1tj-QlG8e_6RACrye67ad13OYR6cSGJs4",
        };
        String[] WoodsPlusSCAVSpotsimageUrls = {
                "https://drive.google.com/uc?export=view&id=1r8I5QKo-Ihwq0KGnSu7lf6EHsYWzn-f7",
                "https://drive.google.com/uc?export=view&id=17K6PWGK6MTwS2HDxI77FDt3e1MZaPd_4",
                "https://drive.google.com/uc?export=view&id=16SUPtClrV4KyK3mKNu_ku-5W2lomYi1X",
                "https://drive.google.com/uc?export=view&id=1RqfUX0uISlvbeIr_EMwQqufZmWxPNDvd",
                "https://drive.google.com/uc?export=view&id=1ARux1uqDQ1MRVuN6RgOUCeFLqOAy6VnY",
                "https://drive.google.com/uc?export=view&id=1WIfA6s869Z1_DZU57ZWAIIdFlvACN2rw",
                "https://drive.google.com/uc?export=view&id=16CeRbm9aOesPgvNfhe9NwjxsLdh_aqH1",
                "https://drive.google.com/uc?export=view&id=1JLzTQUCR0HlLouHRN8azdcq9O1PfPgeh",
                "https://drive.google.com/uc?export=view&id=1Qi9pYwBAaEVNX2ZrscYn8_OtPROobbN9",
        };
        String[] WoodsPlusPMCSpotsimageUrls = {
                "https://drive.google.com/uc?export=view&id=1K58ugzEnqdsx21qYmZZO7rkD9OnsyH8f",
                "https://drive.google.com/uc?export=view&id=1vsEZYtzKp1Zv-Qzd_UJ6ihN2nr-yJYZZ",
                "https://drive.google.com/uc?export=view&id=1rN1-3aJCj32Fxr0vxuN2Z2aHhspvtnEg",
                "https://drive.google.com/uc?export=view&id=1yVlV7H_QgcgZTnrKEjv_xTrLwnioaHy5",
                "https://drive.google.com/uc?export=view&id=1DbYYew_oxSzVkHUx4_AtDeJTsRdhUiUh",
                "https://drive.google.com/uc?export=view&id=1k6c9EoJmojcbkModWkcLr770pTjzUvyj",
                "https://drive.google.com/uc?export=view&id=1lghnzI6TcLFyNW3l7sjbcUNqFMF6hCwO",
                "https://drive.google.com/uc?export=view&id=17S1XtsHFPd4OlfxX2CGKwqGojbdLCcM6",
                "https://drive.google.com/uc?export=view&id=1vjnZwt7lTNkp7DrleGrEX44ioj0Enl9X",
        };
        String[] WoodsPlusNeutralSpotsimageUrls = {
                "https://drive.google.com/uc?export=view&id=1DyvfM2xKKowZVCxkrHydgG_Uoso25QiI",
                "https://drive.google.com/uc?export=view&id=1NBJGhir8nU5FldMsOhCqPhW0bL_jpj8_",
                "https://drive.google.com/uc?export=view&id=1T8g1kzLWAtgVUyRzC9FUGLSw6Q1J2TTc",
                "https://drive.google.com/uc?export=view&id=1dl5hrl27YTYGdp5wMUnz-Q-MonCQK76Y",
                "https://drive.google.com/uc?export=view&id=19WO2532fIzXnko2snrA46cVLaMc_Ip89",
                "https://drive.google.com/uc?export=view&id=1v6cfzQevOGK5XStMgohfb3x9n92jyYGT",
                "https://drive.google.com/uc?export=view&id=1r3tBRsDlraqsbk9fIR85hIpkhG60Nd14",
                "https://drive.google.com/uc?export=view&id=1jiOC1Bx-dhXTsu7R7kLCz3hb9aajL1FF",
                "https://drive.google.com/uc?export=view&id=1FmgeHQkwj2Stxoq7y7D-G-uy8jShMNo3",
        };
        String[] WoodsPlusCompassimageUrls = {
                "https://drive.google.com/uc?export=view&id=1P0Yuu3__s0_d-mfUdJDJ4X0DRu2r_Co1",
                "https://drive.google.com/uc?export=view&id=14Rxx2ahL2D-aGWBgZjgbLzsrVzSeqQI8",
                "https://drive.google.com/uc?export=view&id=1R62rvOFF1rFU9coMd-0a5c5Bxwkb8Icx",
                "https://drive.google.com/uc?export=view&id=1y-bL6DjTcrIYAb8m3RDCjmdIQ754FqPw",
                "https://drive.google.com/uc?export=view&id=1TPLVfhz6PqU_XdhJJzJvOD2pHBee_XiP",
                "https://drive.google.com/uc?export=view&id=1azZvqjSSJY2JHjKfY1ragiWvPO2h9IRP",
                "https://drive.google.com/uc?export=view&id=1A5PX5tAq-3tyaZoEJkJX0UMutXUJlDtO",
                "https://drive.google.com/uc?export=view&id=1f-698xfAetHEeingBxTYfyexITnO1aFQ",
                "https://drive.google.com/uc?export=view&id=1mpFd79J8OVYd_cG_SDGUn7ys9BsI5ZvG",
        };

        // Установка карты берег
        loadAndMergeImages(WoodsimageUrls, WoodsPlusCompassimageUrls);
        selectAllButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Логика для загрузки всех изображений
                loadAndMergeImages(WoodsimageUrls,WoodsPlusAllimageUrls);
            }
        });

            clearAllButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Логика для очистки всех изображений
                    loadAndMergeImages(WoodsimageUrls,WoodsPlusCompassimageUrls);
                }
            });

        PMCExtractsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Логика для загрузки изображений PMC
                setActiveButton(PMCExtractsButton);
                loadAndMergeImages(WoodsimageUrls, WoodsPlusPMCimageUrls);
            }
        });

        scavExtractsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Логика для загрузки изображений SCAV
                setActiveButton(scavExtractsButton);
                loadAndMergeImages(WoodsimageUrls, WoodsPlusSCAVimageUrls);// Установите фон активной кнопки
            }
        });
        IconLegendBoss.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Логика для загрузки изображений SCAV
                setActiveButton(IconLegendBoss);
                loadAndMergeImages(WoodsimageUrls, WoodsPlusBossimageUrls);
            }
        });
        IconLegendBTRStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Логика для загрузки изображений SCAV
                setActiveButton(IconLegendBTRStop);
                loadAndMergeImages(WoodsimageUrls, WoodsPlusBTRStopimageUrls);
            }
        });
        IconLegendCache.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Логика для загрузки изображений SCAV
                setActiveButton(IconLegendCache);
                loadAndMergeImages(WoodsimageUrls, WoodsPlusCasheimageUrls);
            }
        });
        IconLegendCorpse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Логика для загрузки изображений SCAV
                setActiveButton(IconLegendCorpse);
                loadAndMergeImages(WoodsimageUrls, WoodsPlusCorpseimageUrls);
            }
        });
        IconLegendCultists.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Логика для загрузки изображений SCAV
                setActiveButton(IconLegendCultists);
                loadAndMergeImages(WoodsimageUrls, WoodsPlusCultistsimageUrls);
            }
        });
        IconLegendGoons.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Логика для загрузки изображений SCAV
                setActiveButton(IconLegendGoons);
                loadAndMergeImages(WoodsimageUrls, WoodsPlusGoonsimageUrls);
            }
        });
        IconLegendMines.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Логика для загрузки изображений SCAV
                setActiveButton(IconLegendMines);
                loadAndMergeImages(WoodsimageUrls, WoodsPlusMinesimageUrls);
            }
        });
        IconLegendPMCSpawn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Логика для загрузки изображений SCAV
                setActiveButton(IconLegendPMCSpawn);
                loadAndMergeImages(WoodsimageUrls, WoodsPlusPMCSpawnimageUrls);
            }
        });
        IconLegendRitual.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Логика для загрузки изображений SCAV
                setActiveButton(IconLegendRitual);
                loadAndMergeImages(WoodsimageUrls, WoodsPlusRitualimageUrls);
            }
        });
        IconLegendScavs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Логика для загрузки изображений SCAV
                setActiveButton(IconLegendScavs);
                loadAndMergeImages(WoodsimageUrls, WoodsPlusScavsimageUrls);
            }
        });
        IconLegendScavSniper.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Логика для загрузки изображений SCAV
                setActiveButton(IconLegendScavSniper);
                loadAndMergeImages(WoodsimageUrls, WoodsPlusScavSniperimageUrls);
            }
        });
        IconLegendSniper.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Логика для загрузки изображений SCAV
                setActiveButton(IconLegendSniper);
                loadAndMergeImages(WoodsimageUrls, WoodsPlusSniperimageUrls);
            }
        });
        IconBloodOfWar3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Логика для загрузки изображений SCAV
                setActiveButton(IconBloodOfWar3);
                loadAndMergeImages(WoodsimageUrls, WoodsPlusBloodOfWar3imageUrls);
            }
        });
        IconChumming.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Логика для загрузки изображений SCAV
                setActiveButton(IconChumming);
                loadAndMergeImages(WoodsimageUrls, WoodsPlusChummingimageUrls);
            }
        });
        IconGratitude.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Логика для загрузки изображений SCAV
                setActiveButton(IconGratitude);
                loadAndMergeImages(WoodsimageUrls, WoodsPlusGratitudeimageUrls);
            }
        });
        IconHCarePriv3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Логика для загрузки изображений SCAV
                setActiveButton(IconHCarePriv3);
                loadAndMergeImages(WoodsimageUrls, WoodsPlusHCarePriv3imageUrls);
            }
        });
        IconInformedMeansArmed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Логика для загрузки изображений SCAV
                setActiveButton(IconInformedMeansArmed);
                loadAndMergeImages(WoodsimageUrls, WoodsPlusInformedMeansArmedimageUrls);
            }
        });
        IconIntroduction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Логика для загрузки изображений SCAV
                setActiveButton(IconIntroduction);
                loadAndMergeImages(WoodsimageUrls, WoodsPlusIntroductionimageUrls);
            }
        });
        IconLendLease1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Логика для загрузки изображений SCAV
                setActiveButton(IconLendLease1);
                loadAndMergeImages(WoodsimageUrls, WoodsPlusLendLease1imageUrls);
            }
        });
        IconSearchMission.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Логика для загрузки изображений SCAV
                setActiveButton(IconSearchMission);
                loadAndMergeImages(WoodsimageUrls, WoodsPlusSearchMissionimageUrls);
            }
        });
        IconSupplyPlans.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Логика для загрузки изображений SCAV
                setActiveButton(IconSupplyPlans);
                loadAndMergeImages(WoodsimageUrls, WoodsPlusSupplyPlansimageUrls);
            }
        });
        IconThrifty.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Логика для загрузки изображений SCAV
                setActiveButton(IconThrifty);
                loadAndMergeImages(WoodsimageUrls, WoodsPlusThriftyimageUrls);
            }
        });
        ScavSpots.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Логика для загрузки изображений SCAV
                setActiveButton(ScavSpots);
                loadAndMergeImages(WoodsimageUrls, WoodsPlusSCAVSpotsimageUrls);
            }
        });
        IconPMCSpots.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Логика для загрузки изображений SCAV
                setActiveButton(IconPMCSpots);
                loadAndMergeImages(WoodsimageUrls, WoodsPlusPMCSpotsimageUrls);
            }
        });
        IconNeutralSpots.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Логика для загрузки изображений SCAV
                setActiveButton(IconNeutralSpots);
                loadAndMergeImages(WoodsimageUrls, WoodsPlusNeutralSpotsimageUrls);
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
                // Загрузка накладываемых и зображений
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