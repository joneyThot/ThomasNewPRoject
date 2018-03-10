package com.android.handyman.fragment;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Point;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.android.handyman.MainActivity;
import com.android.handyman.R;
import com.android.handyman.model.AdvertiseListModel;
import com.android.handyman.model.ReviewRateModel;
import com.android.handyman.service.Utils;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;
import com.squareup.picasso.Picasso;

public class AdvertiseDetailsFragment extends BaseFragment implements OnClickListener{
	
	private static String TAG = "AdvertiseDetailsFragment";
	
	private SharedPreferences mSharedPreferences;
	AdvertiseListModel mAdvertiseListModel = new AdvertiseListModel();
	
	Fragment fr;
	View mRootView;
	private int mDeviceWidth = 480;
	ImageView imageView;
    

	@SuppressLint("NewApi") @Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,	Bundle savedInstanceState) {
		mRootView = inflater.inflate(R.layout.fragment_advertise_details, container, false);
		
		
		WindowManager w = ((Activity) getActivity()).getWindowManager();

		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
			Point size = new Point();
			w.getDefaultDisplay().getSize(size);
			mDeviceWidth = size.x;
		} else {
			Display d = w.getDefaultDisplay();
			mDeviceWidth = d.getWidth();
		}
		
		initview();
		return mRootView;
	}

	private void initview() {
		mSharedPreferences = getActivity().getSharedPreferences(Utils.PREF_NAME, Context.MODE_PRIVATE); 
		((MainActivity) getActivity()).setTitleText("", "advertise details", View.VISIBLE, View.GONE, View.GONE, View.GONE);
		getActivity().findViewById(R.id.title).setVisibility(View.GONE);
		getActivity().findViewById(R.id.menuBtn).setOnClickListener(this);	
		
		mActivity.getSlidingMenu().setMode(SlidingMenu.LEFT);
		mActivity.getSlidingMenu().setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);
		
		imageView = (ImageView) mRootView.findViewById(R.id.adve_details_img);
		
		if(getArguments() != null) {
			mAdvertiseListModel =  (AdvertiseListModel) getArguments().get(Utils.HANDYMAN_ADVERTISE);
		}
		
		if(mAdvertiseListModel != null){
			
			if(Utils.validateString(mAdvertiseListModel.banner_path)) {
				
				Picasso.with(getActivity()).load(Utils.IMAGE_URL + mAdvertiseListModel.banner_path).resize(mDeviceWidth, (int) (mDeviceWidth)).centerCrop().error(R.drawable.placeholder).into(imageView);
			}
			
			if(Utils.validateString(mAdvertiseListModel.description)){
				((TextView) mRootView.findViewById(R.id.advr_details_more_desc)).setText(mAdvertiseListModel.description);
				
			}
			
		}
		
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		}
	}
	
	
}
