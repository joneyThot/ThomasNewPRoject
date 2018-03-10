package com.handyman.view;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.handyman.MainActivity;
import com.handyman.fragment.AddressMapFragment;
import com.google.android.gms.maps.SupportMapFragment;

/**
 * Created by adminz on 25/7/16.
 */
public class MySupportMapFragment extends SupportMapFragment {
    public View mOriginalContentView;
    public TouchableWrapper mTouchView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        mOriginalContentView = super.onCreateView(inflater, parent, savedInstanceState);
        mTouchView = new TouchableWrapper(getActivity());
        mTouchView.addView(mOriginalContentView);
        return mTouchView;
    }

    @Override
    public View getView() {
        return mOriginalContentView;
    }
}

class TouchableWrapper extends FrameLayout {

    public TouchableWrapper(Context context) {
        super(context);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {

        switch (event.getAction()) {

           case MotionEvent.ACTION_DOWN:
                AddressMapFragment.mMapIsTouched = false;
//                AddressMapFragment.locationMarkertext.setText("Set your location");
                break;
            case MotionEvent.ACTION_UP:
                AddressMapFragment.mMapIsTouched = false;
                break;
            case MotionEvent.ACTION_MOVE:
                AddressMapFragment.mMapIsTouched = false;
                AddressMapFragment.locationMarkertext.setText("Set nearby location");
                AddressMapFragment.locationLoading.setVisibility(VISIBLE);
                break;
        }
        return super.dispatchTouchEvent(event);
    }
}
