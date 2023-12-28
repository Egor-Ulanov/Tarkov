    package com.example.tarkov;

    import android.content.pm.ActivityInfo;
    import android.content.res.ColorStateList;
    import android.graphics.drawable.ColorDrawable;
    import android.os.Bundle;
    import android.text.Spannable;
    import android.text.SpannableString;
    import android.text.style.ForegroundColorSpan;
    import android.view.Window;
    import android.view.WindowManager;

    import androidx.appcompat.app.ActionBar;
    import androidx.appcompat.app.AppCompatActivity;
    import androidx.core.content.ContextCompat;
    import androidx.navigation.NavController;
    import androidx.navigation.Navigation;
    import androidx.navigation.ui.AppBarConfiguration;
    import androidx.navigation.ui.NavigationUI;

    import com.example.tarkov.databinding.ActivityMainBinding;
    import com.google.android.material.bottomnavigation.BottomNavigationView;

    public class MainActivity extends AppCompatActivity {

        private ActivityMainBinding binding;

        @Override
        protected void onCreate(Bundle savedInstanceState) {

            // Установка светлой темы перед setContentView
           // setTheme(R.style.AppTheme_Light);

            super.onCreate(savedInstanceState);
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            requestWindowFeature(Window.FEATURE_NO_TITLE);
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

            binding = ActivityMainBinding.inflate(getLayoutInflater());
            setContentView(binding.getRoot());

            BottomNavigationView navView = findViewById(R.id.nav_view);
            int navBackgroundColor = ContextCompat.getColor(this, R.color.light_nav_background);
            ColorStateList navIconColor = ContextCompat.getColorStateList(this, R.color.light_nav_icon_selector);

            navView.setBackgroundColor(navBackgroundColor);
            navView.setItemIconTintList(navIconColor);
            navView.setItemTextColor(navIconColor);


            AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                    R.id.navigation_home, R.id.navigation_dashboard, R.id.navigation_notifications)
                    .build();
            NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_activity_main);
            NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
            NavigationUI.setupWithNavController(binding.navView, navController);


        }

        public void updateActionBarStyle(boolean isDarkTheme) {
            ActionBar actionBar = getSupportActionBar();
            if (actionBar != null) {
                // Установка цвета фона ActionBar
                int backgroundColorId = isDarkTheme ? R.color.dark_nav_background : R.color.light_nav_background;
                int backgroundColor = ContextCompat.getColor(this, backgroundColorId);
                actionBar.setBackgroundDrawable(new ColorDrawable(backgroundColor));

                // Установка цвета текста заголовка
                int textColorId = isDarkTheme ? R.color.dark_text : R.color.light_text;
                int textColor = ContextCompat.getColor(this, textColorId);

                // Создание нового Spannable для изменения цвета текста заголовка
                Spannable text = new SpannableString(actionBar.getTitle());
                text.setSpan(new ForegroundColorSpan(textColor), 0, text.length(), Spannable.SPAN_INCLUSIVE_INCLUSIVE);
                actionBar.setTitle(text);
            }
        }



    }