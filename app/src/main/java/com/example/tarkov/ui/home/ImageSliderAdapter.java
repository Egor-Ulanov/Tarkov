package com.example.tarkov.ui.home;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

import com.bumptech.glide.Glide;
import com.example.tarkov.R;
import com.google.api.services.youtube.model.SearchResult;

import java.util.List;

public class ImageSliderAdapter extends PagerAdapter {

    private Context context;
    private List<SearchResult> videos;

    public ImageSliderAdapter(Context context, List<SearchResult> videos) {
        this.context = context;
        this.videos = videos;
    }

    @Override
    public int getCount() {
        return videos.size();
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == object;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View itemView = inflater.inflate(R.layout.video_item, container, false);

        ImageView imageView = itemView.findViewById(R.id.videoThumbnail);
        TextView videoTitle = itemView.findViewById(R.id.videoTitle);
        ImageView playButton = itemView.findViewById(R.id.playButton);

        // Получить URL превью видеоролика
        String videoId = videos.get(position).getId().getVideoId();
        String thumbnailUrl = "https://img.youtube.com/vi/" + videoId + "/0.jpg";

        // Загрузить превью видеоролика
        Glide.with(context)
                .load(thumbnailUrl)
                .into(imageView);

        // Установить заголовок видеоролика (примените нужную логику для получения заголовка)
        String title = "Заголовок видео";
        videoTitle.setText(title);

        // Добавьте обработчик щелчка кнопки воспроизведения (примените нужную логику)
        playButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Обработка нажатия на кнопку воспроизведения
                Log.d("ImageSliderAdapter", "Кнопка воспроизведения нажата для позиции: " + position);
            }
        });

        container.addView(itemView);

        return itemView;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((View) object);
    }
}
