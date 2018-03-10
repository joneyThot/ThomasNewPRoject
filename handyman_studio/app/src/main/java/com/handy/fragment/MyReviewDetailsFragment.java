package com.handy.fragment;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.RatingBar;
import android.widget.TextView;

import com.handy.MainActivity;
import com.handy.R;
import com.handy.model.MyHiringsModel;
import com.handy.model.ReviewRateModel;
import com.handy.service.Utils;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;

public class MyReviewDetailsFragment extends BaseFragment implements OnClickListener{
	
	private static String TAG = "MyReviewDetailsFragment";
	
	private SharedPreferences mSharedPreferences;
	ReviewRateModel mReviewRate = new ReviewRateModel();
	
	Fragment fr;
	View mRootView;
	
	String description = "",date = "", rate = "";
    

	@SuppressLint("NewApi") @Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,	Bundle savedInstanceState) {
		mRootView = inflater.inflate(R.layout.fragment_review_details, container, false);
		
		initview();
		return mRootView;
	}

	private void initview() {
		mSharedPreferences = getActivity().getSharedPreferences(Utils.PREF_NAME, Context.MODE_PRIVATE); 
		((MainActivity) getActivity()).setTitleText("", getString(R.string.menu_my_review_details), View.VISIBLE, View.GONE, View.GONE);
		getActivity().findViewById(R.id.title).setVisibility(View.GONE);
		getActivity().findViewById(R.id.menuBtn).setOnClickListener(this);	
		
		mActivity.getSlidingMenu().setMode(SlidingMenu.LEFT);
		mActivity.getSlidingMenu().setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);
		
		
		if(getArguments() != null) {
			mReviewRate =  (ReviewRateModel) getArguments().get(Utils.HANDYMAN_REVIEW_DETAILS);
		}
		
		if(mReviewRate != null){
			
			if(Utils.validateString(mReviewRate.description)){
//				description = mReviewRate.description;
				((TextView) mRootView.findViewById(R.id.review_details_description)).setText(mReviewRate.description);
				((TextView) mRootView.findViewById(R.id.review_details_more_desc)).setText(mReviewRate.description);
				
			}
			
			if(Utils.validateString(mReviewRate.created_date)){
				((TextView) mRootView.findViewById(R.id.review_details_date)).setText(mReviewRate.created_date);
			}
			
			if(Utils.validateString(mReviewRate.rate)){
				float rate = Float.parseFloat(mReviewRate.rate);
				((RatingBar) mRootView.findViewById(R.id.review_details_rating)).setRating(rate);
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
