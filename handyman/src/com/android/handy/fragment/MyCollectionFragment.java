package com.android.handy.fragment;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Toast;

import com.android.handy.MainActivity;
import com.android.handy.R;
import com.android.handy.adapter.MyCollectionFragmentPagerAdapter;
import com.android.handy.service.Utils;
import com.android.handy.view.TitlePageIndicator;
import com.android.handy.view.TitlePageIndicator.OnCenterItemClickListener;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;

public class MyCollectionFragment extends BaseFragment implements OnClickListener, OnCenterItemClickListener{
	
	private static String TAG = "MyCollectionFragment";
//	private ArrayList<MyHiringsModel> mMyHiringsList = new ArrayList<MyHiringsModel>();
	private SharedPreferences mSharedPreferences;

	Fragment fr;
	View mRootView;
	ViewPager mPager;
	TitlePageIndicator mIndicator;
	MyCollectionFragmentPagerAdapter myCollectionFragmentPagerAdapter;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setRetainInstance(true);
	    this.myCollectionFragmentPagerAdapter = new MyCollectionFragmentPagerAdapter(getChildFragmentManager());
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		mRootView = inflater.inflate(R.layout.fragment_my_collection, container, false);
		
		return mRootView;
	}
	
	@Override
	public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		initview();
		super.onViewCreated(view, savedInstanceState);
		
	}
	
	@SuppressWarnings("deprecation")
	private void initview() {
		mSharedPreferences = getActivity().getSharedPreferences(Utils.PREF_NAME, Context.MODE_PRIVATE);
		((MainActivity) getActivity()).setTitleText(getString(R.string.menu_my_collection), "", View.GONE,	View.VISIBLE, View.GONE);
		getActivity().findViewById(R.id.title_back).setVisibility(View.GONE);

		mActivity.getSlidingMenu().setMode(SlidingMenu.LEFT);
		mActivity.getSlidingMenu().setTouchModeAbove(SlidingMenu.TOUCHMODE_NONE);
		
		mPager = (ViewPager) mRootView.findViewById(R.id.pager_collection);
		mIndicator = (TitlePageIndicator) mRootView.findViewById(R.id.indicator_collection);
		
		mPager.setAdapter(myCollectionFragmentPagerAdapter);
		mIndicator.setViewPager(mPager);
		
	}
	

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		}
	}
	
	@Override
	public void onCenterItemClick(int position) {
		Toast.makeText(getActivity(), "You clicked :: " + position, Toast.LENGTH_SHORT).show();
		
	}

}