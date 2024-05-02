package com.example.tarkov.ui.dashboard;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tarkov.R;
import com.example.tarkov.databinding.FragmentDashboardBinding;

import java.util.ArrayList;
import java.util.List;

public class DashboardFragment extends Fragment {

    private static final String TAG = "DashboardFragment";
    private FragmentDashboardBinding binding;
    private RecyclerView recyclerView;
    private MapAdapter mapAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView: начало");

        View view =  inflater.inflate(R.layout.fragment_dashboard, container, false);

        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        // Пример картинок из интернета. Замените ссылками на ваши изображения.
        String imageUrl1 = "https://static.wikia.nocookie.net/escapefromtarkov_gamepedia/images/d/d5/Banner_shoreline.png/revision/latest/scale-to-width-down/320?cb=20171101223501";
        String imageUrl2 = "https://static.wikia.nocookie.net/escapefromtarkov_gamepedia/images/1/1a/Factory-Day_Banner.png/revision/latest/scale-to-width-down/320?cb=20200811153020";
        String imageUrl3 = "https://static.wikia.nocookie.net/escapefromtarkov_gamepedia/images/3/3e/Banner_woods.png/revision/latest/scale-to-width-down/320?cb=20171101223132";

        List<MapItem> mapItemList = new ArrayList<>();
        mapItemList.add(new MapItem(imageUrl1, MapOfBeregActivity.class, "Берег"));
        mapItemList.add(new MapItem(imageUrl2, MapOfZavodActivity.class, "Завод"));
        mapItemList.add(new MapItem(imageUrl3, MapOfWoodsActivity.class, "Лес"));

        mapAdapter = new MapAdapter(mapItemList);
        recyclerView.setAdapter(mapAdapter);

        Log.d(TAG, "onCreateView: recyclerView установлен");
        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
