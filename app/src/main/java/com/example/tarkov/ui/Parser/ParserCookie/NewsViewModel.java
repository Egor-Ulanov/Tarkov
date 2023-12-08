package com.example.tarkov.ui.Parser.ParserCookie;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import com.example.tarkov.ui.Parser.ParserFix.NewsItem;
import java.util.List;

public class NewsViewModel extends ViewModel {

    private MutableLiveData<List<NewsItem>> newsListLiveData = new MutableLiveData<>();

    public MutableLiveData<List<NewsItem>> getNewsListLiveData() {
        return newsListLiveData;
    }

    public void setNewsList(List<NewsItem> newsList) {
        newsListLiveData.setValue(newsList);
    }
}