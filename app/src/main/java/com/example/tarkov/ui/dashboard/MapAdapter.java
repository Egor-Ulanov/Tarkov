package com.example.tarkov.ui.dashboard;

import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.tarkov.R;

import java.util.List;

public class MapAdapter extends RecyclerView.Adapter<MapAdapter.MapViewHolder> {

    private static final String TAG = "MapAdapter";
    private List<MapItem> mapItemList;

    public MapAdapter(List<MapItem> mapItemList) {
        this.mapItemList = mapItemList;
    }

    @NonNull
    @Override
    public MapViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Log.d(TAG, "onCreateViewHolder: создание view holder");

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_map, parent, false);
        return new MapViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MapViewHolder holder, int position) {
        Log.d(TAG, "onBindViewHolder: позиция = " + position);

        MapItem mapItem = mapItemList.get(position);

        Glide.with(holder.itemView.getContext())
                .load(mapItem.getImageUrl())
                .into(holder.mapImage);
        holder.mapTitle.setText(mapItem.getTitle());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: нажатие на элемент с позицией " + position);

                Intent intent = new Intent(holder.itemView.getContext(), mapItem.getActivityClass());
                // Добавьте дополнительные данные, если необходимо (например, передача параметров в новую активность)
                // intent.putExtra("key", "value");
                holder.itemView.getContext().startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mapItemList.size();
    }

    public static class MapViewHolder extends RecyclerView.ViewHolder {
        ImageView mapImage;
        TextView mapTitle;

        public MapViewHolder(@NonNull View itemView) {
            super(itemView);
            mapImage = itemView.findViewById(R.id.mapImage);
            mapTitle = itemView.findViewById(R.id.mapTitle);
        }
    }
}
