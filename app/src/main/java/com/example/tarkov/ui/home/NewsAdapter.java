package com.example.tarkov.ui.home;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.toolbox.NetworkImageView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.example.tarkov.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;
import com.example.tarkov.ui.Parser.ParserFix;


public class NewsAdapter extends RecyclerView.Adapter<NewsAdapter.NewsViewHolder> {

    private static List<ParserFix.NewsItem> newsList = new ArrayList<>();


    public void setNewsList(List<ParserFix.NewsItem> newsList) {
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

        holder.itemView.setOnClickListener(v -> {
            // Открывает новую активность при нажатии на элемент списка
            Intent intent = new Intent(v.getContext(), NewsDetailActivity.class);
            // Здесь вы можете передавать дополнительные данные в новую активность
            v.getContext().startActivity(intent);
        });

        ParserFix.NewsItem currentNews = newsList.get(position);
        holder.bind(currentNews);
//        holder.bind(currentNews.getTitle(), currentNews.getPartialContent(), currentNews.getDate());

        Picasso.get()
                .load(currentNews.getImageUrl())
                .placeholder(R.drawable.image1)
                .into(holder.newsImage);
        // Загрузите изображение с использованием Glide или Picasso
//        Glide.with(holder.itemView.getContext())
//                .load(currentNews.getImageUrl())
//                .placeholder(R.drawable.image1)
//                .into(holder.newsImage);
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


public void bind(ParserFix.NewsItem currentNews) {
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
