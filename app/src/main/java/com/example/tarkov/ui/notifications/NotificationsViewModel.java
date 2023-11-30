package com.example.tarkov.ui.notifications;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class NotificationsViewModel extends ViewModel {

    private final MutableLiveData<String> mText;

    public NotificationsViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("Здесь возможно будут настройки.Содержимое (сам текст) в папке com.example.tarkov.ui.notifications");
    }

    public LiveData<String> getText() {
        return mText;
    }
}