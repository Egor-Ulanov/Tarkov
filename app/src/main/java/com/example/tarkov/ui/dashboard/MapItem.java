package com.example.tarkov.ui.dashboard;

public class MapItem {
    private String imageUrl;
    private Class<?> activityClass;
    private String title;  // Добавлено поле для заголовка

    public MapItem(String imageUrl, Class<?> activityClass, String title) {
        this.imageUrl = imageUrl;
        this.activityClass = activityClass;
        this.title = title;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public Class<?> getActivityClass() {
        return activityClass;
    }

    public String getTitle() {  // Добавлен метод getTitle()
        return title;
    }
}

