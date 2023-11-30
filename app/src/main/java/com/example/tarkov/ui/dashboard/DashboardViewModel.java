package com.example.tarkov.ui.dashboard;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class DashboardViewModel extends ViewModel {

    private final MutableLiveData<String> mText;

    public DashboardViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("Здесь будет интерактивная карта Содержимое (сам текст) в папке com.example.tarkov.ui.dashboard");
    }

    public LiveData<String> getText() {
        return mText;
    }
}