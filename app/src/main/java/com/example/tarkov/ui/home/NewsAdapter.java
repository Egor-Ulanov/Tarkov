package com.example.tarkov.ui.home;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.toolbox.NetworkImageView;
import com.bumptech.glide.Glide;
import com.example.tarkov.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class NewsAdapter extends RecyclerView.Adapter<NewsAdapter.NewsViewHolder> {

    private List<String> newsList = new ArrayList<>();

    public void setNewsList(List<String> newsList) {
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
        // Подставьте реальные данные для каждой новости
        /*holder.bind("Заголовок новости", "Содержимое новости", "01.01.2023");
        String imageUrl = "https://www.escapefromtarkov.com/uploads/content/news/thumb_71d9c96b35e48336e85138ee5395d1a3.png?ru";
        Glide.with(holder.itemView.getContext())
                .load(imageUrl)
                .placeholder(R.drawable.image1)
                .into(holder.newsImage);*/

        NewsItem currentNews = newsList.get(position);

        holder.bind(currentNews.getTitle(), currentNews.getContent(), currentNews.getDate());

        // Загрузите изображение с использованием Glide или Picasso
        Glide.with(holder.itemView.getContext())
                .load(currentNews.getImageUrl())
                .placeholder(R.drawable.image1)
                .into(holder.newsImage);
    }

    @Override
    public int getItemCount() {
        // Замените на реальное количество новостей
        return newsList.size();
        //return 5;
    }

    static class NewsViewHolder extends RecyclerView.ViewHolder {

        private final NetworkImageView newsImage;
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

        public void bind(String title, String content, String date) {
            //Замените следующие строки на загрузку изображения из вашего URL-адреса
            Glide.with(itemView.getContext())
                    .load(newsImage)
                    .placeholder(R.drawable.image1)
                    .into(newsImage);

            newsTitle.setText(title);
            newsContent.setText(content);
            newsDate.setText(date);
        }
    }
}
