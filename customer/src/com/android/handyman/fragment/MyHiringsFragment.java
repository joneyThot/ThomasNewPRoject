package com.android.handyman.fragment;

import java.util.ArrayList;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Toast;

import com.android.handyman.MainActivity;
import com.android.handyman.R;
import com.android.handyman.SplashActivity;
import com.android.handyman.adapter.MyHiringPagerAdapter;
import com.android.handyman.logger.Logger;
import com.android.handyman.model.MyHiringsModel;
import com.android.handyman.service.AsyncCallListener;
import com.android.handyman.service.GetHandymanHiringsListRequestTask;
import com.android.handyman.service.GetMyHiringsListRequestTask;
import com.android.handyman.service.Utils;
import com.android.handyman.view.TitlePageIndicator;
import com.android.handyman.view.TitlePageIndicator.OnCenterItemClickListener;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;

public class MyHiringsFragment extends BaseFragment implements OnClickListener, OnCenterItemClickListener, OnPageChangeListener{
	
	private static String TAG = "MyHiringsFragment";
//	private ArrayList<MyHiringsModel> mMyHiringsList = new ArrayList<MyHiringsModel>();
	private ArrayList<MyHiringsModel> mPendingHiringsList = new ArrayList<MyHiringsModel>();
	private ArrayList<MyHiringsModel> mCancelHiringsList = new ArrayList<MyHiringsModel>();
	private ArrayList<MyHiringsModel> mCompleteHiringsList = new ArrayList<MyHiringsModel>();
	private ArrayList<MyHiringsModel> mRejectedHiringsList = new ArrayList<MyHiringsModel>();
	private ArrayList<MyHiringsModel> mAcceptedHiringsList = new ArrayList<MyHiringsModel>();
	private ArrayList<MyHiringsModel> mAllHiringsList = new ArrayList<MyHiringsModel>();
	private SharedPreferences mSharedPreferences;

	Fragment fr;
	View mRootView;
	ViewPager mPager;
	TitlePageIndicator mIndicator;
	MyHiringPagerAdapter myHiringPagerAdapter;
	boolean flag = false;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		mRootView = inflater.inflate(R.layout.fragment_my_hirings, container, false);
		initview();
		return mRootView;
	}

	private void initview() {
		mSharedPreferences = getActivity().getSharedPreferences(Utils.PREF_NAME, Context.MODE_PRIVATE);
		((MainActivity) getActivity()).setTitleText(getString(R.string.menu_my_hirings), "", View.GONE,	View.VISIBLE, View.GONE,View.GONE);
		getActivity().findViewById(R.id.title_back).setVisibility(View.GONE);

		mActivity.getSlidingMenu().setMode(SlidingMenu.LEFT);
		mActivity.getSlidingMenu().setTouchModeAbove(SlidingMenu.TOUCHMODE_NONE);
		
		mPager = (ViewPager) mRootView.findViewById(R.id.pager);
		mIndicator = (TitlePageIndicator) mRootView.findViewById(R.id.indicator);
		mIndicator.setOnPageChangeListener(this);
		
//		mPager.setOnPageChangeListener(this);
//			if(mSharedPreferences.getString(Utils.USER_TYPE, "").equalsIgnoreCase("customer")){
//				getPendingHirings(mSharedPreferences.getString(Utils.USER_ID, ""),"pending");
//				getCancelHirings(mSharedPreferences.getString(Utils.USER_ID, ""),"cancelled");
//				getCompleteHirings(mSharedPreferences.getString(Utils.USER_ID, ""),"completed");
//				getRejectHirings(mSharedPreferences.getString(Utils.USER_ID, ""),"rejected");
//				getAllHirings(mSharedPreferences.getString(Utils.USER_ID, ""),"all");
//				getAcceptedHirings(mSharedPreferences.getString(Utils.USER_ID, ""),"active");
				
//			} else {
//				getHMPendingHirings(mSharedPreferences.getString(Utils.USER_ID, ""),"pending",String.valueOf(SplashActivity.latitude),String.valueOf(SplashActivity.longitude));
//				getHMCancelHirings(mSharedPreferences.getString(Utils.USER_ID, ""),"cancelled",String.valueOf(SplashActivity.latitude),String.valueOf(SplashActivity.longitude));
//				getHMCompleteHirings(mSharedPreferences.getString(Utils.USER_ID, ""),"completed",String.valueOf(SplashActivity.latitude),String.valueOf(SplashActivity.longitude));
//				getHMRejectHirings(mSharedPreferences.getString(Utils.USER_ID, ""),"rejected",String.valueOf(SplashActivity.latitude),String.valueOf(SplashActivity.longitude));
//				getHMAllHirings(mSharedPreferences.getString(Utils.USER_ID, ""),"all",String.valueOf(SplashActivity.latitude),String.valueOf(SplashActivity.longitude));
//				getHMAcceptedHirings(mSharedPreferences.getString(Utils.USER_ID, ""),"active",String.valueOf(SplashActivity.latitude),String.valueOf(SplashActivity.longitude));
//			}
//		}
		
		
	}
	
	
	@Override
	public void onPageSelected(int position) {
		 Toast.makeText(getActivity(), "Changed to page " + position, Toast.LENGTH_SHORT).show();
		 Logger.e(TAG, "onPageSelected :: "+ position);
		// TODO Auto-generated method stub
//		if(mSharedPreferences.getString(Utils.USER_TYPE, "").equalsIgnoreCase("customer")){
//			switch (position) {
//			case 0:
//				getPendingHirings(mSharedPreferences.getString(Utils.USER_ID, ""),"pending");
//				break;
//			case 1 :
//				getAcceptedHirings(mSharedPreferences.getString(Utils.USER_ID, ""),"active");
//				break;
//			case 2 :
//				getCancelHirings(mSharedPreferences.getString(Utils.USER_ID, ""),"cancelled");
//				break;
//			case 3:
//				getCompleteHirings(mSharedPreferences.getString(Utils.USER_ID, ""),"completed");
//				break;
//			case 4:
//				getRejectHirings(mSharedPreferences.getString(Utils.USER_ID, ""),"rejected");
//				break;
//			case 5:
//				getAllHirings(mSharedPreferences.getString(Utils.USER_ID, ""),"all");
//				break;
//	
//			}
//		} else {
//			switch (position) {
//			
//			case 0:
//				getHMPendingHirings(mSharedPreferences.getString(Utils.USER_ID, ""),"pending",String.valueOf(SplashActivity.latitude),String.valueOf(SplashActivity.longitude));
//				break;
//			case 1 :
//				getHMAcceptedHirings(mSharedPreferences.getString(Utils.USER_ID, ""),"active",String.valueOf(SplashActivity.latitude),String.valueOf(SplashActivity.longitude));
//				break;
//			case 2 :
//				getHMCancelHirings(mSharedPreferences.getString(Utils.USER_ID, ""),"cancelled",String.valueOf(SplashActivity.latitude),String.valueOf(SplashActivity.longitude));
//				break;
//			case 3:
//				getHMCompleteHirings(mSharedPreferences.getString(Utils.USER_ID, ""),"completed",String.valueOf(SplashActivity.latitude),String.valueOf(SplashActivity.longitude));
//				break;
//			case 4:
//				getHMRejectHirings(mSharedPreferences.getString(Utils.USER_ID, ""),"rejected",String.valueOf(SplashActivity.latitude),String.valueOf(SplashActivity.longitude));
//				break;
//			case 5:
//				getHMAllHirings(mSharedPreferences.getString(Utils.USER_ID, ""),"all",String.valueOf(SplashActivity.latitude),String.valueOf(SplashActivity.longitude));
//				break;
//			}
//			
//		}
		
	}
	
	@Override
	public void onPageScrollStateChanged(int arg0) {
		// TODO Auto-generated method stub
//		 Logger.e(TAG, "onPageScrollStateChanged :: "+ arg0);
	}

	@Override
	public void onPageScrolled(int arg0, float arg1, int arg2) {
		// TODO Auto-generated method stub
//		 Logger.e(TAG, "onPageScrolled :: "+ arg0 + "::" + arg1 + "::" +  arg2);
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
	
	private void getPendingHirings(String id, String status) {
		if(Utils.checkInternetConnection(getActivity())){
			GetMyHiringsListRequestTask myHiringsListRequestTask = new GetMyHiringsListRequestTask(getActivity());
			myHiringsListRequestTask.setAsyncCallListener(new AsyncCallListener() {

				@SuppressWarnings("unchecked")
				@Override
				public void onResponseReceived(Object response) {
					mPendingHiringsList = (ArrayList<MyHiringsModel>) response;
						Logger.e(TAG, "mPendingHiringsList SIZE -- " + mPendingHiringsList.size());
						if (mPendingHiringsList != null) {
							for (int i = 0; i < mPendingHiringsList.size(); i++) {
								mPager.setAdapter(new MyHiringPagerAdapter(getActivity(),mPendingHiringsList,mAcceptedHiringsList,mCancelHiringsList,mCompleteHiringsList,mRejectedHiringsList,mAllHiringsList));
								mIndicator.setViewPager(mPager);
							}
							
						} else {
							Toast.makeText(getActivity(), "Data not available. ", Toast.LENGTH_SHORT).show();
						}
				}
				@Override
				public void onErrorReceived(String error) {
					Toast.makeText(getActivity(), getString(R.string.try_again), Toast.LENGTH_SHORT).show();
				}
			});
			myHiringsListRequestTask.execute(id,status);
		}else{
			Utils.showMessageDialog(getActivity(), getResources().getString(R.string.alert), 
					getResources().getString(R.string.connection));
		}
	}
	
	private void getCancelHirings(String id, String status) {
		if(Utils.checkInternetConnection(getActivity())){
			GetMyHiringsListRequestTask myHiringsListRequestTask = new GetMyHiringsListRequestTask(getActivity());
			myHiringsListRequestTask.setAsyncCallListener(new AsyncCallListener() {

				@SuppressWarnings("unchecked")
				@Override
				public void onResponseReceived(Object response) {
					mCancelHiringsList = (ArrayList<MyHiringsModel>) response;
						Logger.e(TAG, "mCancelHiringsList SIZE -- " + mCancelHiringsList.size());
						if (mCancelHiringsList != null) {
							for (int i = 0; i < mCancelHiringsList.size(); i++) {
								mPager.setAdapter(new MyHiringPagerAdapter(getActivity(),mPendingHiringsList,mAcceptedHiringsList,mCancelHiringsList,mCompleteHiringsList,mRejectedHiringsList,mAllHiringsList));
								mIndicator.setViewPager(mPager);
							}
							
						} else {
							Toast.makeText(getActivity(), "Data not available. ", Toast.LENGTH_SHORT).show();
						}
				}
				@Override
				public void onErrorReceived(String error) {
					Toast.makeText(getActivity(), getString(R.string.try_again), Toast.LENGTH_SHORT).show();
				}
			});
			myHiringsListRequestTask.execute(id,status);
		}else{
			Utils.showMessageDialog(getActivity(), getResources().getString(R.string.alert), 
					getResources().getString(R.string.connection));
		}
	}
	
	private void getCompleteHirings(String id, String status) {
		if(Utils.checkInternetConnection(getActivity())){
			GetMyHiringsListRequestTask myHiringsListRequestTask = new GetMyHiringsListRequestTask(getActivity());
			myHiringsListRequestTask.setAsyncCallListener(new AsyncCallListener() {

				@SuppressWarnings("unchecked")
				@Override
				public void onResponseReceived(Object response) {
					mCompleteHiringsList = (ArrayList<MyHiringsModel>) response;
						Logger.e(TAG, "mCompleteHiringsList SIZE -- " + mCompleteHiringsList.size());
						if (mCompleteHiringsList != null) {
							for (int i = 0; i < mCompleteHiringsList.size(); i++) {
								mPager.setAdapter(new MyHiringPagerAdapter(getActivity(),mPendingHiringsList,mAcceptedHiringsList,mCancelHiringsList,mCompleteHiringsList,mRejectedHiringsList,mAllHiringsList));
								mIndicator.setViewPager(mPager);
							}
							
						} else {
							Toast.makeText(getActivity(), "Data not available. ", Toast.LENGTH_SHORT).show();
						}
				}
				@Override
				public void onErrorReceived(String error) {
					Toast.makeText(getActivity(), getString(R.string.try_again), Toast.LENGTH_SHORT).show();
				}
			});
			myHiringsListRequestTask.execute(id,status);
		}else{
			Utils.showMessageDialog(getActivity(), getResources().getString(R.string.alert), 
					getResources().getString(R.string.connection));
		}
	}
	
	private void getRejectHirings(String id, String status) {
		if(Utils.checkInternetConnection(getActivity())){
			GetMyHiringsListRequestTask myHiringsListRequestTask = new GetMyHiringsListRequestTask(getActivity());
			myHiringsListRequestTask.setAsyncCallListener(new AsyncCallListener() {

				@SuppressWarnings("unchecked")
				@Override
				public void onResponseReceived(Object response) {
					mRejectedHiringsList = (ArrayList<MyHiringsModel>) response;
						Logger.e(TAG, "mRejectedHiringsList SIZE -- " + mRejectedHiringsList.size());
						if (mRejectedHiringsList != null) {
							for (int i = 0; i < mRejectedHiringsList.size(); i++) {
								mPager.setAdapter(new MyHiringPagerAdapter(getActivity(),mPendingHiringsList,mAcceptedHiringsList,mCancelHiringsList,mCompleteHiringsList,mRejectedHiringsList,mAllHiringsList));
								mIndicator.setViewPager(mPager);
							}
							
						} else {
							Toast.makeText(getActivity(), "Data not available. ", Toast.LENGTH_SHORT).show();
						}
				}
				@Override
				public void onErrorReceived(String error) {
					Toast.makeText(getActivity(), getString(R.string.try_again), Toast.LENGTH_SHORT).show();
				}
			});
			myHiringsListRequestTask.execute(id,status);
		}else{
			Utils.showMessageDialog(getActivity(), getResources().getString(R.string.alert), 
					getResources().getString(R.string.connection));
		}
	}
	
	private void getAllHirings(String id, String status) {
		if(Utils.checkInternetConnection(getActivity())){
			GetMyHiringsListRequestTask myHiringsListRequestTask = new GetMyHiringsListRequestTask(getActivity());
			myHiringsListRequestTask.setAsyncCallListener(new AsyncCallListener() {

				@SuppressWarnings("unchecked")
				@Override
				public void onResponseReceived(Object response) {
					mAllHiringsList = (ArrayList<MyHiringsModel>) response;
						Logger.e(TAG, "mAllHiringsList SIZE -- " + mAllHiringsList.size());
						if (mAllHiringsList != null) {
							for (int i = 0; i < mAllHiringsList.size(); i++) {
								mPager.setAdapter(new MyHiringPagerAdapter(getActivity(),mPendingHiringsList,mAcceptedHiringsList,mCancelHiringsList,mCompleteHiringsList,mRejectedHiringsList,mAllHiringsList));
								mIndicator.setViewPager(mPager);
							}
							
						} else {
							Toast.makeText(getActivity(), "Data not available. ", Toast.LENGTH_SHORT).show();
						}
				}
				@Override
				public void onErrorReceived(String error) {
					Toast.makeText(getActivity(), getString(R.string.try_again), Toast.LENGTH_SHORT).show();
				}
			});
			myHiringsListRequestTask.execute(id,status);
		}else{
			Utils.showMessageDialog(getActivity(), getResources().getString(R.string.alert), 
					getResources().getString(R.string.connection));
		}
	}
	
	private void getAcceptedHirings(String id, String status) {
		if(Utils.checkInternetConnection(getActivity())){
			GetMyHiringsListRequestTask myHiringsListRequestTask = new GetMyHiringsListRequestTask(getActivity());
			myHiringsListRequestTask.setAsyncCallListener(new AsyncCallListener() {

				@SuppressWarnings("unchecked")
				@Override
				public void onResponseReceived(Object response) {
					mAcceptedHiringsList = (ArrayList<MyHiringsModel>) response;
						Logger.e(TAG, "mAcceptedHiringsList SIZE -- " + mAcceptedHiringsList.size());
						if (mAllHiringsList != null) {
							for (int i = 0; i < mAcceptedHiringsList.size(); i++) {
								mPager.setAdapter(new MyHiringPagerAdapter(getActivity(),mPendingHiringsList,mAcceptedHiringsList,mCancelHiringsList,mCompleteHiringsList,mRejectedHiringsList,mAllHiringsList));
								mIndicator.setViewPager(mPager);
							}
							
						} else {
							Toast.makeText(getActivity(), "Data not available. ", Toast.LENGTH_SHORT).show();
						}
				}
				@Override
				public void onErrorReceived(String error) {
					Toast.makeText(getActivity(), getString(R.string.try_again), Toast.LENGTH_SHORT).show();
				}
			});
			myHiringsListRequestTask.execute(id,status);
		}else{
			Utils.showMessageDialog(getActivity(), getResources().getString(R.string.alert), 
					getResources().getString(R.string.connection));
		}
	}
	
	private void getHMPendingHirings(String id, String status, String lat, String lng) {
		if(Utils.checkInternetConnection(getActivity())){
			GetHandymanHiringsListRequestTask getHandymanHiringsListRequestTask = new GetHandymanHiringsListRequestTask(getActivity());
			getHandymanHiringsListRequestTask.setAsyncCallListener(new AsyncCallListener() {

				@SuppressWarnings("unchecked")
				@Override
				public void onResponseReceived(Object response) {
					mPendingHiringsList = (ArrayList<MyHiringsModel>) response;
						Logger.e(TAG, "mPendingHiringsList SIZE -- " + mPendingHiringsList.size());
						if (mPendingHiringsList != null) {
							for (int i = 0; i < mPendingHiringsList.size(); i++) {
								mPager.setAdapter(new MyHiringPagerAdapter(getActivity(),mPendingHiringsList,mAcceptedHiringsList,mCancelHiringsList,mCompleteHiringsList,mRejectedHiringsList,mAllHiringsList));
								mIndicator.setViewPager(mPager);
//								mIndicator.setCurrentItem(0);
							}
							
						} else {
							Toast.makeText(getActivity(), "Data not available. ", Toast.LENGTH_SHORT).show();
						}
				}
				@Override
				public void onErrorReceived(String error) {
					Toast.makeText(getActivity(), getString(R.string.try_again), Toast.LENGTH_SHORT).show();
				}
			});
			getHandymanHiringsListRequestTask.execute(id,status,lat,lng);
		}else{
			Utils.showMessageDialog(getActivity(), getResources().getString(R.string.alert), 
					getResources().getString(R.string.connection));
		}
	}
	
	private void getHMCancelHirings(String id, String status,String lat, String lng) {
		if(Utils.checkInternetConnection(getActivity())){
			GetHandymanHiringsListRequestTask getHandymanHiringsListRequestTask = new GetHandymanHiringsListRequestTask(getActivity());
			getHandymanHiringsListRequestTask.setAsyncCallListener(new AsyncCallListener() {

				@SuppressWarnings("unchecked")
				@Override
				public void onResponseReceived(Object response) {
					mCancelHiringsList = (ArrayList<MyHiringsModel>) response;
						Logger.e(TAG, "mCancelHiringsList SIZE -- " + mCancelHiringsList.size());
						if (mCancelHiringsList != null) {
							for (int i = 0; i < mCancelHiringsList.size(); i++) {
								mPager.setAdapter(new MyHiringPagerAdapter(getActivity(),mPendingHiringsList,mAcceptedHiringsList,mCancelHiringsList,mCompleteHiringsList,mRejectedHiringsList,mAllHiringsList));
								mIndicator.setViewPager(mPager);
//								mIndicator.setCurrentItem(2);
							}
							
						} else {
							Toast.makeText(getActivity(), "Data not available. ", Toast.LENGTH_SHORT).show();
						}
				}
				@Override
				public void onErrorReceived(String error) {
					Toast.makeText(getActivity(), getString(R.string.try_again), Toast.LENGTH_SHORT).show();
				}
			});
			getHandymanHiringsListRequestTask.execute(id,status,lat,lng);
		}else{
			Utils.showMessageDialog(getActivity(), getResources().getString(R.string.alert), 
					getResources().getString(R.string.connection));
		}
	}
	
	private void getHMCompleteHirings(String id, String status,String lat, String lng) {
		if(Utils.checkInternetConnection(getActivity())){
			GetHandymanHiringsListRequestTask getHandymanHiringsListRequestTask = new GetHandymanHiringsListRequestTask(getActivity());
			getHandymanHiringsListRequestTask.setAsyncCallListener(new AsyncCallListener() {

				@SuppressWarnings("unchecked")
				@Override
				public void onResponseReceived(Object response) {
					mCompleteHiringsList = (ArrayList<MyHiringsModel>) response;
						Logger.e(TAG, "mCompleteHiringsList SIZE -- " + mCompleteHiringsList.size());
						if (mCompleteHiringsList != null) {
							for (int i = 0; i < mCompleteHiringsList.size(); i++) {
								mPager.setAdapter(new MyHiringPagerAdapter(getActivity(),mPendingHiringsList,mAcceptedHiringsList,mCancelHiringsList,mCompleteHiringsList,mRejectedHiringsList,mAllHiringsList));
								mIndicator.setViewPager(mPager);
//								mIndicator.setCurrentItem(3);
							}
							
						} else {
							Toast.makeText(getActivity(), "Data not available. ", Toast.LENGTH_SHORT).show();
						}
				}
				@Override
				public void onErrorReceived(String error) {
					Toast.makeText(getActivity(), getString(R.string.try_again), Toast.LENGTH_SHORT).show();
				}
			});
			getHandymanHiringsListRequestTask.execute(id,status,lat,lng);
		}else{
			Utils.showMessageDialog(getActivity(), getResources().getString(R.string.alert), 
					getResources().getString(R.string.connection));
		}
	}
	
	private void getHMRejectHirings(String id, String status,String lat, String lng) {
		if(Utils.checkInternetConnection(getActivity())){
			GetHandymanHiringsListRequestTask getHandymanHiringsListRequestTask = new GetHandymanHiringsListRequestTask(getActivity());
			getHandymanHiringsListRequestTask.setAsyncCallListener(new AsyncCallListener() {

				@SuppressWarnings("unchecked")
				@Override
				public void onResponseReceived(Object response) {
					mRejectedHiringsList = (ArrayList<MyHiringsModel>) response;
						Logger.e(TAG, "mRejectedHiringsList SIZE -- " + mRejectedHiringsList.size());
						if (mRejectedHiringsList != null) {
							for (int i = 0; i < mRejectedHiringsList.size(); i++) {
								mPager.setAdapter(new MyHiringPagerAdapter(getActivity(),mPendingHiringsList,mAcceptedHiringsList,mCancelHiringsList,mCompleteHiringsList,mRejectedHiringsList,mAllHiringsList));
								mIndicator.setViewPager(mPager);
//								mIndicator.setCurrentItem(4);
							}
							
						} else {
							Toast.makeText(getActivity(), "Data not available. ", Toast.LENGTH_SHORT).show();
						}
				}
				@Override
				public void onErrorReceived(String error) {
					Toast.makeText(getActivity(), getString(R.string.try_again), Toast.LENGTH_SHORT).show();
				}
			});
			getHandymanHiringsListRequestTask.execute(id,status,lat,lng);
		}else{
			Utils.showMessageDialog(getActivity(), getResources().getString(R.string.alert), 
					getResources().getString(R.string.connection));
		}
	}
	
	private void getHMAllHirings(String id, String status,String lat, String lng) {
		if(Utils.checkInternetConnection(getActivity())){
			GetHandymanHiringsListRequestTask getHandymanHiringsListRequestTask = new GetHandymanHiringsListRequestTask(getActivity());
			getHandymanHiringsListRequestTask.setAsyncCallListener(new AsyncCallListener() {

				@SuppressWarnings("unchecked")
				@Override
				public void onResponseReceived(Object response) {
					mAllHiringsList = (ArrayList<MyHiringsModel>) response;
						Logger.e(TAG, "mAllHiringsList SIZE -- " + mAllHiringsList.size());
						if (mAllHiringsList != null) {
							for (int i = 0; i < mAllHiringsList.size(); i++) {
								mPager.setAdapter(new MyHiringPagerAdapter(getActivity(),mPendingHiringsList,mAcceptedHiringsList,mCancelHiringsList,mCompleteHiringsList,mRejectedHiringsList,mAllHiringsList));
								mIndicator.setViewPager(mPager);
//								mIndicator.setCurrentItem(5);
							}
							
						} else {
							Toast.makeText(getActivity(), "Data not available. ", Toast.LENGTH_SHORT).show();
						}
				}
				@Override
				public void onErrorReceived(String error) {
					Toast.makeText(getActivity(), getString(R.string.try_again), Toast.LENGTH_SHORT).show();
				}
			});
			getHandymanHiringsListRequestTask.execute(id,status,lat,lng);
		}else{
			Utils.showMessageDialog(getActivity(), getResources().getString(R.string.alert), 
					getResources().getString(R.string.connection));
		}
	}
	
	private void getHMAcceptedHirings(String id, String status,String lat, String lng) {
		if(Utils.checkInternetConnection(getActivity())){
			GetHandymanHiringsListRequestTask getHandymanHiringsListRequestTask = new GetHandymanHiringsListRequestTask(getActivity());
			getHandymanHiringsListRequestTask.setAsyncCallListener(new AsyncCallListener() {

				@SuppressWarnings("unchecked")
				@Override
				public void onResponseReceived(Object response) {
					mAcceptedHiringsList = (ArrayList<MyHiringsModel>) response;
						Logger.e(TAG, "mAcceptedHiringsList SIZE -- " + mAcceptedHiringsList.size());
						if (mAllHiringsList != null) {
							for (int i = 0; i < mAcceptedHiringsList.size(); i++) {
								mPager.setAdapter(new MyHiringPagerAdapter(getActivity(),mPendingHiringsList,mAcceptedHiringsList,mCancelHiringsList,mCompleteHiringsList,mRejectedHiringsList,mAllHiringsList));
								mIndicator.setViewPager(mPager);
//								mIndicator.setCurrentItem(1);
							}
							
						} else {
							Toast.makeText(getActivity(), "Data not available. ", Toast.LENGTH_SHORT).show();
						}
				}
				@Override
				public void onErrorReceived(String error) {
					Toast.makeText(getActivity(), getString(R.string.try_again), Toast.LENGTH_SHORT).show();
				}
			});
			getHandymanHiringsListRequestTask.execute(id,status,lat,lng);
		}else{
			Utils.showMessageDialog(getActivity(), getResources().getString(R.string.alert), 
					getResources().getString(R.string.connection));
		}
	}


}
