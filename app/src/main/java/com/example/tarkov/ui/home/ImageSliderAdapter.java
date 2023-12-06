package com.example.tarkov.ui.home;

import androidx.viewpager.widget.PagerAdapter;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.example.tarkov.R;
import android.widget.LinearLayout;
public class ImageSliderAdapter extends PagerAdapter {

    private Context context;
    private int[] images = {R.drawable.image1, R.drawable.image2, R.drawable.image3};

    public ImageSliderAdapter(Context context) {
        this.context = context;
    }

    @Override
    public int getCount() {
        return images.length;
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == object;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View itemView = inflater.inflate(R.layout.slider_item, container, false);

        // 03.12.2023/16:42)
        TextView textOverlay = itemView.findViewById(R.id.textOverlay);
        ImageView imageView = itemView.findViewById(R.id.imageView);
        imageView.setImageResource(images[position]);
        imageView.setScaleType(ImageView.ScaleType.CENTER_CROP); // Установите ScaleType (03.12.2023/16:30)

        // Установите текст для textOverlay (здесь вы можете использовать данные, связанные с изображением/03.12.2023/16:42)
        textOverlay.setText("Текст наложенный на изображение для большей реалистичности тест тест");

        container.addView(itemView);

        return itemView;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((View) object);
    }

    // Добавьте метод для обновления индикатора слайдов(03.12.2023/16:49)
    public void updateSlideIndicator(LinearLayout sliderIndicator, int position) {
        for (int i = 0; i < sliderIndicator.getChildCount(); i++) {
            ImageView indicator = (ImageView) sliderIndicator.getChildAt(i);
            indicator.setImageResource(i == position ? R.drawable.truedot1 : R.drawable.truedot2);
        }
    }
}
