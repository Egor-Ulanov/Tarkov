package com.example.tarkov.ui.home;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tarkov.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;
import com.example.tarkov.ui.Parser.ParserNewsList;


public class NewsAdapter extends RecyclerView.Adapter<NewsAdapter.NewsViewHolder> {

    private static List<ParserNewsList.NewsItem> newsList = new ArrayList<>();


    public void setNewsList(List<ParserNewsList.NewsItem> newsList) {
        this.newsList = newsList;
        notifyDataSetChanged();
    }
    @NonNull
    @Override
    public NewsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.news_item, parent, false);
        return new NewsViewHolder(view);
    }

    @Override

    public void onBindViewHolder(@NonNull NewsViewHolder holder, int position) {
        // Привязать данные элемента новости к держателю представления
        ParserNewsList.NewsItem currentNews = newsList.get(position);
        holder.bind(currentNews);

        holder.itemView.setOnClickListener(v -> {
            // Переход к Activity подробной информации о новостях, когда элемент новости щелкивается
            Intent intent = new Intent(v.getContext(), NewsDetailActivity.class);
            intent.putExtra("fullNewsLink", currentNews.getFullNewsLink()); // Передать полную ссылку на активность
            v.getContext().startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        // Замените на реальное количество новостей
        return newsList.size();
        //return 5;
    }

    static class NewsViewHolder extends RecyclerView.ViewHolder {

        private final ImageView newsImage;
        private final TextView newsTitle;
        private final TextView newsContent;
        private final TextView newsDate;

        public NewsViewHolder(@NonNull View itemView) {
            super(itemView);
            newsImage = itemView.findViewById(R.id.newsImage);
            newsTitle = itemView.findViewById(R.id.newsTitle);
            newsContent = itemView.findViewById(R.id.newsContent);
            newsDate = itemView.findViewById(R.id.newsDate);
        }


public void bind(ParserNewsList.NewsItem currentNews) {
    Picasso.get()
            .load(currentNews.getImageUrl())
            .placeholder(R.drawable.image1)
            .into(newsImage);

    newsTitle.setText(currentNews.getTitle());
    newsContent.setText(currentNews.getPartialContent());
    newsDate.setText(currentNews.getDate());
}


    }
}
