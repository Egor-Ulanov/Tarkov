package com.example.tarkov.ui.dashboard;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.PointF;
import android.graphics.drawable.Drawable;

import androidx.core.content.ContextCompat;

import com.davemorrissey.labs.subscaleview.SubsamplingScaleImageView;

@SuppressLint("ViewConstructor")
public class MapMarker extends SubsamplingScaleImageView {

    private final PointF location;
    private final Drawable markerDrawable;

    public MapMarker(Context context, int resourceId, PointF location) {
        super(context);
        this.location = location;
        this.markerDrawable = ContextCompat.getDrawable(context, resourceId);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (markerDrawable != null && location != null && isReady()) {
            PointF markerCoords = sourceToViewCoord(location);
            float x = markerCoords.x - (markerDrawable.getIntrinsicWidth() / 2);
            float y = markerCoords.y - markerDrawable.getIntrinsicHeight();
            markerDrawable.setBounds((int) x, (int) y, (int) (x + markerDrawable.getIntrinsicWidth()), (int) (y + markerDrawable.getIntrinsicHeight()));
            markerDrawable.draw(canvas);
        }
    }
}
