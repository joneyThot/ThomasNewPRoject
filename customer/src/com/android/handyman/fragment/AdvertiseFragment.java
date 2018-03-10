package com.android.handyman.fragment;

import java.util.ArrayList;

import android.annotation.TargetApi;
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
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ListView;
import android.widget.Toast;

import com.android.handyman.MainActivity;
import com.android.handyman.R;
import com.android.handyman.adapter.AdvertiseAdapter;
import com.android.handyman.logger.Logger;
import com.android.handyman.model.AdvertiseListModel;
import com.android.handyman.model.DataModel;
import com.android.handyman.service.AsyncCallListener;
import com.android.handyman.service.GetAdvertiseRequestTask;
import com.android.handyman.service.Utils;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;

public class AdvertiseFragment extends BaseFragment implements OnClickListener{
	
	private static String TAG = "AdvertiseFragment";
	private SharedPreferences mSharedPreferences;
	
	ArrayList<AdvertiseListModel> advertiseListModels = new ArrayList<AdvertiseListModel>();
    ArrayList<AdvertiseListModel> mTempAdvertiselist = new ArrayList<AdvertiseListModel>();
    AdvertiseAdapter advertiseAdapter = null;
    
	Fragment fr;
	View mRootView;
	ListView  mAdvertiseList;
	private int mDeviceWidth = 480;

	@TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2) @Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,	Bundle savedInstanceState) {
		mRootView = inflater.inflate(R.layout.fragment_favourite, container, false);
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
		((MainActivity) getActivity()).setTitleText(getString(R.string.menu_advertise) + " with us", "", View.GONE, View.VISIBLE, View.GONE,View.GONE);
		getActivity().findViewById(R.id.title_back).setVisibility(View.GONE);		
		
		mActivity.getSlidingMenu().setMode(SlidingMenu.LEFT);
		mActivity.getSlidingMenu().setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);
		
		mAdvertiseList = (ListView) mRootView.findViewById(R.id.favourite_list);
		
		 if(mTempAdvertiselist.size() > 0){
	        	Logger.d(TAG, "SAVE advertise size :: " + mTempAdvertiselist.size());
	        	advertiseAdapter = new AdvertiseAdapter(getActivity(), mTempAdvertiselist,onAcceptedClickListener);
	        	mAdvertiseList.setAdapter(advertiseAdapter);
				advertiseAdapter.notifyDataSetChanged();
				
	    	}
		 
		 if(advertiseListModels.size() == 0){
			 getAdvertise();	 
		 }
		 
		
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		
//		
		}
	}
	
	
	private void getAdvertise() {
		if(Utils.checkInternetConnection(getActivity())){
			GetAdvertiseRequestTask getAdvertiseRequestTask = new GetAdvertiseRequestTask(getActivity());
			getAdvertiseRequestTask.setAsyncCallListener(new AsyncCallListener() {

				@Override
				public void onResponseReceived(Object response) {
					advertiseListModels.clear();
					DataModel advertiseModel = (DataModel) response;
					
					if(advertiseModel.success.equalsIgnoreCase("1")){
//						Toast.makeText(getActivity(), advertiseModel.getMessage(), Toast.LENGTH_SHORT).show();
						advertiseListModels.addAll(advertiseModel.getAdvertiseListModels());
					
						Logger.e(TAG, "advertiseListModels SIZE -- "	+ advertiseListModels.size());
						if (advertiseListModels.size() > 0 ) {
//										
										setView(advertiseListModels);
						} 
					} else if(advertiseModel.success.equalsIgnoreCase("0")){
						Utils.showMessageDialog(getActivity(), getResources().getString(R.string.alert), advertiseModel.message);
					}
					

					
				}
				@Override
				public void onErrorReceived(String error) {
					Toast.makeText(getActivity(), getString(R.string.try_again), Toast.LENGTH_SHORT).show();
				}
			});
			getAdvertiseRequestTask.execute();
		}else{
			Utils.showMessageDialog(getActivity(), getResources().getString(R.string.alert), 
					getResources().getString(R.string.connection));
		}
	}
	
	private void setView(ArrayList<AdvertiseListModel> hiringAllList) {
		if(isAdded()) {
			mTempAdvertiselist.addAll(hiringAllList);
			Logger.d(TAG, "SIZE of setview Advertise ::" + mTempAdvertiselist.size());
			if(hiringAllList.size() > 0) {
				
				if(advertiseAdapter == null){
					advertiseAdapter = new AdvertiseAdapter(getActivity(), mTempAdvertiselist,onAcceptedClickListener);
					mAdvertiseList.setAdapter(advertiseAdapter);
					advertiseAdapter.notifyDataSetChanged();
				} else {
					advertiseAdapter.setList(mTempAdvertiselist);
					advertiseAdapter.notifyDataSetChanged();
				}
				
			}
		}
	}
	
	OnClickListener onAcceptedClickListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			if(v.getTag() != null){
				int index = (Integer) v.getTag();
				Logger.e(TAG, "ADVERTISE ITEM CLICK INDEX ::" + index);
				AdvertiseDetailsFragment advertiseDetailsFragment = new AdvertiseDetailsFragment();
				Bundle bundle = new Bundle();
				bundle.putSerializable(Utils.HANDYMAN_ADVERTISE, mTempAdvertiselist.get(index));
				advertiseDetailsFragment.setArguments(bundle);
				getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, advertiseDetailsFragment).addToBackStack(null).commit();
			}
		}
	};
	
}
