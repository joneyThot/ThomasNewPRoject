package com.android.handy.fragment;

import java.util.ArrayList;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;

import com.android.handy.MainActivity;
import com.android.handy.R;
import com.android.handy.adapter.MyHiringFragmentPagerAdapter;
import com.android.handy.model.MyHiringsModel;
import com.android.handy.service.Utils;
import com.android.handy.view.TitlePageIndicator;
import com.android.handy.view.TitlePageIndicator.OnCenterItemClickListener;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;

public class MyHiringsFragment_new extends BaseFragment implements OnClickListener, OnCenterItemClickListener{
	
	private static String TAG = "MyHiringsFragment";
	private ArrayList<MyHiringsModel> mMyHiringsList = new ArrayList<MyHiringsModel>();
	private SharedPreferences mSharedPreferences;

	Fragment fr;
	View mRootView;
	ViewPager mPager;
	TitlePageIndicator mIndicator;
//	MyHiringPagerAdapter_new myHiringPagerAdapter_new;
	MyHiringFragmentPagerAdapter myHiringFragmentPagerAdapter;
	String complete = "", pending = "", cancel = "", start = "", active = "";
	private static final int DEFAULT_OFFSCREEN_PAGES = 1;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setRetainInstance(true);
	    this.myHiringFragmentPagerAdapter = new MyHiringFragmentPagerAdapter(getChildFragmentManager());
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		mRootView = inflater.inflate(R.layout.fragment_my_hirings, container, false);
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
		((MainActivity) getActivity()).setTitleText(getString(R.string.menu_my_hirings), "", View.GONE,	View.VISIBLE, View.GONE);
		getActivity().findViewById(R.id.title_back).setVisibility(View.GONE);

		mActivity.getSlidingMenu().setMode(SlidingMenu.LEFT);
		mActivity.getSlidingMenu().setTouchModeAbove(SlidingMenu.TOUCHMODE_NONE);
		
		mPager = (ViewPager) mRootView.findViewById(R.id.pager);
		mIndicator = (TitlePageIndicator) mRootView.findViewById(R.id.indicator);
		
//		mPager.setAdapter(new MyHiringPagerAdapter_new(getActivity()));
//		mIndicator.setViewPager(mPager);
//		mIndicator.setCurrentItem(0);
		
		if(getArguments() != null){
			pending = getArguments().getString("Pending");
			active = getArguments().getString("Active");
			start = getArguments().getString("Start");
			cancel = getArguments().getString("Cancel");
			complete = getArguments().getString("Complete");
		} 
		
		if(Utils.validateString(pending)){
			mPager.setAdapter(myHiringFragmentPagerAdapter);
			mIndicator.setViewPager(mPager);
			mIndicator.setCurrentItem(0);
		} else if(Utils.validateString(active)){
			mPager.setAdapter(myHiringFragmentPagerAdapter);
			mIndicator.setViewPager(mPager);
			mIndicator.setCurrentItem(1);
		} else if(Utils.validateString(start)){
			mPager.setAdapter(myHiringFragmentPagerAdapter);
			mIndicator.setViewPager(mPager);
			mIndicator.setCurrentItem(2);
		} else if(Utils.validateString(cancel)){
			mPager.setAdapter(myHiringFragmentPagerAdapter);
			mIndicator.setViewPager(mPager);
			mIndicator.setCurrentItem(5);
		} else if(Utils.validateString(complete)){
			mPager.setAdapter(myHiringFragmentPagerAdapter);
			mIndicator.setViewPager(mPager);
			mIndicator.setCurrentItem(4);
		} else {
			mPager.setAdapter(myHiringFragmentPagerAdapter);
//			mPager.setOffscreenPageLimit(7);
			mIndicator.setViewPager(mPager);
		}
		
		/*mPager.setOnPageChangeListener(new OnPageChangeListener() {
			
			@Override
			public void onPageSelected(int arg0) {
				switch (arg0) {
				case 0:	PendingFragment.newInstance(0, "Page # 1");
					break;
				case 1: AcceptFragment.newInstance(1, "Page # 2");
					break;
				case 2: StartJobFragment.newInstance(2, "Page # 3");
					break;
				case 3: CancelledFragment.newInstance(3, "Page # 4");
					break;
				case 4: CompletedFragment.newInstance(4, "Page # 5");
					break;
				case 5: RejectedFragment.newInstance(5, "Page # 6");
					break;
				case 6: AllFragment.newInstance(6, "Page # 7");
					break;
								
				}
				
			}
			
			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onPageScrollStateChanged(int arg0) {
				// TODO Auto-generated method stub
				
			}
		});*/
		
	}
	
	
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		}
	}
	
	@Override
	public void onCenterItemClick(int position) {
//		Toast.makeText(getActivity(), "You clicked :: " + position, Toast.LENGTH_SHORT).show();
		
	}
	
}