package com.android.handyman.fragment;

import java.util.ArrayList;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

import com.android.handyman.MainActivity;
import com.android.handyman.R;
import com.android.handyman.SplashActivity;
import com.android.handyman.adapter.CategoryAdapter;
import com.android.handyman.adapter.SubCategoryAdapter;
import com.android.handyman.logger.Logger;
import com.android.handyman.model.CategoryListModel;
import com.android.handyman.model.DataModel;
import com.android.handyman.model.StateModel;
import com.android.handyman.service.AsyncCallListener;
import com.android.handyman.service.GetCategoryListRequestTask;
import com.android.handyman.service.Utils;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;

public class ServiceAtHomeFragment extends BaseFragment implements OnClickListener{
	
	private static String TAG = "ServiceAtHomeFragment";
	
	private SharedPreferences mSharedPreferences;
//	private ArrayList<StateModel> mCategoryArrayList = new ArrayList<StateModel>();
	private ArrayList<CategoryListModel> mCategoryArrayList = new ArrayList<CategoryListModel>();
	
	SubCategoryAdapter subCategoryAdapter;
	CategoryAdapter categoryAdapter;
	
	Fragment fr;
	View mRootView;
	ListView mCategoryList/*, mSubCategoryList*/;
	String mElectricalID,mPlumbinID,mCleaningID,mCarpenterID;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,	Bundle savedInstanceState) {
		mRootView = inflater.inflate(R.layout.fragment_home, container, false);
		initview();
		return mRootView;
	}

	private void initview() {
		mSharedPreferences = getActivity().getSharedPreferences(Utils.PREF_NAME, Context.MODE_PRIVATE); 
		((MainActivity) getActivity()).setTitleText(getString(R.string.home_searvice_at_home), "", View.GONE, View.VISIBLE, View.GONE,View.GONE);
		getActivity().findViewById(R.id.title_back).setVisibility(View.GONE);	
		
		mActivity.getSlidingMenu().setMode(SlidingMenu.LEFT);
		mActivity.getSlidingMenu().setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);
		
		mCategoryList = (ListView) mRootView.findViewById(R.id.category_list);
		getCategory(String.valueOf(SplashActivity.latitude),String.valueOf(SplashActivity.longitude));
//		getCategory("22.3072","73.1812");
		
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		}
	}
	
	private void getCategory(String lat,String lng) {
		if(Utils.checkInternetConnection(getActivity())){
			GetCategoryListRequestTask getCategoryListTask = new GetCategoryListRequestTask(getActivity());
			getCategoryListTask.setAsyncCallListener(new AsyncCallListener() {

				@SuppressWarnings("unchecked")
				@Override
				public void onResponseReceived(Object response) {
						mCategoryArrayList.clear();
						DataModel dataModel = (DataModel) response;
						if(dataModel.getSuccess().equalsIgnoreCase("1")){
//							Toast.makeText(getActivity(), dataModel.getMessage(), Toast.LENGTH_SHORT).show();

//							mCategoryArrayList = (ArrayList<CategoryListModel>) response;
							mCategoryArrayList.addAll(dataModel.getCategoryListModels());
							
							Logger.e(TAG, "mCategoryArrayList SIZE -- " + mCategoryArrayList.size());
							if (mCategoryArrayList.size() > 0) {
								categoryAdapter = new CategoryAdapter(getActivity(), mCategoryArrayList, onCategoryClickListener);
								mCategoryList.setAdapter(categoryAdapter);
								categoryAdapter.notifyDataSetChanged();
								
							}
						} else if(dataModel.getSuccess().equalsIgnoreCase("0")){
							Utils.showMessageDialog(getActivity(), getResources().getString(R.string.alert), dataModel.getMessage());
						}
				}
				@Override
				public void onErrorReceived(String error) {
					Toast.makeText(getActivity(), getString(R.string.try_again), Toast.LENGTH_SHORT).show();
				}
			});
			getCategoryListTask.execute(lat,lng);
		}else{
			Utils.showMessageDialog(getActivity(), getResources().getString(R.string.alert), 
					getResources().getString(R.string.connection));
		}
	}
	
	OnClickListener onCategoryClickListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			if(v.getTag() != null){
				int index = (Integer) v.getTag();
				Logger.e(TAG, "CATEGORY CLICK INDEX ::" + index);
				Utils.storeString(mSharedPreferences, Utils.CATEGORY_ID, mCategoryArrayList.get(index).getId());				
				ServiceAtHomeSubCategoryFragment serviceAtHomeSubCategoryFragment = new ServiceAtHomeSubCategoryFragment();
				Bundle bundle = new Bundle();
				bundle.putSerializable(Utils.CATEGORY_ITEM_DETAILS, mCategoryArrayList.get(index));
//				bundle.putString("ID", String.valueOf(index++));
				Utils.storeString(mSharedPreferences, Utils.CATE_NAME, mCategoryArrayList.get(index).getName());
				serviceAtHomeSubCategoryFragment.setArguments(bundle);
				getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, serviceAtHomeSubCategoryFragment).addToBackStack(null).commit();
			}
		}
	};
	
	
}
