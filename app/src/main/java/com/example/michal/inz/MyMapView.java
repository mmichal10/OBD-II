package com.example.michal.inz;

import android.content.Context;
import android.support.v4.app.FragmentActivity;
import android.util.AttributeSet;
import android.view.MotionEvent;

import com.example.michal.inz.fragments.MapsFragment;

import org.mapsforge.map.android.view.MapView;

public class MyMapView extends MapView {

    public boolean centerLock = false;
    MapsFragment parentFragment;

    public void setParentFragment(MapsFragment parentFragment) {
        this.parentFragment = parentFragment;
    }

    public MyMapView(Context context) {
        super(context);
    }

    public MyMapView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_MOVE){
            centerLock = true;
            parentFragment.updateLocation();
        }

        return super.onTouchEvent(event);
    }
}
