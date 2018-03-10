package com.android.handy.fragment;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;

import com.android.handy.MainActivity;
import com.android.handy.R;
import com.android.handy.service.Utils;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;

public class FeedbackFragment extends BaseFragment implements OnClickListener{
	
	private static String TAG = "FeedbackFragment";
	
	private SharedPreferences mSharedPreferences;
	
	Fragment fr;
	View mRootView;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,	Bundle savedInstanceState) {
		mRootView = inflater.inflate(R.layout.fragment_feedback, container, false);
		initview();
		return mRootView;
	}

	private void initview() {
		mSharedPreferences = getActivity().getSharedPreferences(Utils.PREF_NAME, Context.MODE_PRIVATE); 
		((MainActivity) getActivity()).setTitleText(getString(R.string.menu_feedback_suggestions), "", View.GONE, View.VISIBLE, View.GONE);
		getActivity().findViewById(R.id.title_back).setVisibility(View.GONE);	
		
		mActivity.getSlidingMenu().setMode(SlidingMenu.LEFT);
		mActivity.getSlidingMenu().setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);
		
		
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		}
	}
	
}
