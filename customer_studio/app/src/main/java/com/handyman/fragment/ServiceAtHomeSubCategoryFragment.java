package com.handyman.fragment;

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
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.handyman.MainActivity;
import com.handyman.R;
import com.handyman.SplashActivity;
import com.handyman.adapter.CategoryAdapter;
import com.handyman.adapter.SubCategoryAdapter;
import com.handyman.logger.Logger;
import com.handyman.model.AdvertiseListModel;
import com.handyman.model.CategoryListModel;
import com.handyman.model.DataModel;
import com.handyman.model.StateModel;
import com.handyman.service.AsyncCallListener;
import com.handyman.service.GetKilometersRequestTask;
import com.handyman.service.GetSubCategoryListRequestTask;
import com.handyman.service.Utils;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;
import com.squareup.picasso.Picasso;

public class ServiceAtHomeSubCategoryFragment extends BaseFragment implements OnClickListener{
	
	private static String TAG = "ServiceAtHomeSubCategoryFragment";
	
	private SharedPreferences mSharedPreferences;
	
//	private ArrayList<StateModel> mCategoryArrayList = new ArrayList<StateModel>();
//	StateModel mCategoryList = new StateModel(); 
//	private ArrayList<StateModel> mSubCategoryArrayList = new ArrayList<StateModel>();

	CategoryListModel mCategoryList = new CategoryListModel(); 
	private ArrayList<CategoryListModel> mSubCategoryArrayList = new ArrayList<CategoryListModel>();
	
	SubCategoryAdapter subCategoryAdapter;
	CategoryAdapter categoryAdapter;
	String category_id;
	
	Fragment fr;
	View mRootView;
	ListView  mSubCategoryList;
	private int mDeviceWidth = 480;
	float zoomlevel = 1.0f;
//	String mElectricalID,mPlumbinID,mCleaningID,mCarpenterID;

	@TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2) @Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,	Bundle savedInstanceState) {
		mRootView = inflater.inflate(R.layout.fragment_home_sub_category, container, false);
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
		((MainActivity) getActivity()).setTitleText(getString(R.string.home_searvice_at_home), "", "", View.VISIBLE, View.GONE, View.GONE,View.GONE, View.GONE);
		getActivity().findViewById(R.id.title_back).setVisibility(View.GONE);
		getActivity().findViewById(R.id.txtUrl).setVisibility(View.GONE);
		
		mActivity.getSlidingMenu().setMode(SlidingMenu.LEFT);
		mActivity.getSlidingMenu().setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);
		
		mSubCategoryList = (ListView) mRootView.findViewById(R.id.sub_category_list);
		getKoilometer();
		
		if(getArguments() != null) {
			mCategoryList =  (CategoryListModel) getArguments().get(Utils.CATEGORY_ITEM_DETAILS);
//			category_id = getArguments().getString("ID");
			Logger.e("Primium", "Image Name ::" + mCategoryList.img_path);
			Picasso.with(getActivity()).load(Utils.IMAGE_URL + mCategoryList.img_path).resize(mDeviceWidth, (int) (mDeviceWidth * 0.32)).centerCrop().into(((ImageView) mRootView.findViewById(R.id.subcategory_img)));
			
			if(Utils.validateString(mCategoryList.name)){
				((TextView) mRootView.findViewById(R.id.category_name_txt)).setText(mCategoryList.name);
			} 
			
			Utils.storeString(mSharedPreferences, Utils.CATEGORY_ID, mCategoryList.getId());
			getSubCategory(mCategoryList.getId());
		}
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		
//		
		}
	}
	
	private void getSubCategory(String id) {
		try{
			if(Utils.checkInternetConnection(getActivity())){
				GetSubCategoryListRequestTask getSubCategoryListRequestTask = new GetSubCategoryListRequestTask(getActivity());
				getSubCategoryListRequestTask.setAsyncCallListener(new AsyncCallListener() {

					@SuppressWarnings("unchecked")
					@Override
					public void onResponseReceived(Object response) {
						mSubCategoryArrayList.clear();
						DataModel dataModel = (DataModel) response;

						if(dataModel.getSuccess().equalsIgnoreCase("1")){

//						Toast.makeText(getActivity(), dataModel.getMessage(), Toast.LENGTH_SHORT).show();
							mSubCategoryArrayList.addAll(dataModel.getCategoryListModels());
//						mSubCategoryArrayList =  (ArrayList<StateModel>) response;
							Logger.e(TAG, "mSubCategoryArrayList SIZE -- "	+ mSubCategoryArrayList.size());
							if (mSubCategoryArrayList.size() > 0 ) {
								subCategoryAdapter = new SubCategoryAdapter(getActivity(), mSubCategoryArrayList, onSubCategoryClickListener);
								mSubCategoryList.setAdapter(subCategoryAdapter);
								subCategoryAdapter.notifyDataSetChanged();
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
				getSubCategoryListRequestTask.execute(id);
			}else{
				Utils.showMessageDialog(getActivity(), getResources().getString(R.string.alert),
						getResources().getString(R.string.connection));
			}
		} catch (NullPointerException e){
			e.printStackTrace();
		}

	}
	
	OnClickListener onSubCategoryClickListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			if(v.getTag() != null){
				int index = (Integer) v.getTag();
				Logger.e(TAG, "SUB CATEGORY CLICK INDEX ::" + index);
				Utils.storeString(mSharedPreferences, Utils.SUB_CATEGORY_ID, mSubCategoryArrayList.get(index).getId());
				FindNearByHandymanFragment findNearByHandymanFragment = new FindNearByHandymanFragment();
				Bundle bundle = new Bundle();
				bundle.putString("ServiceAtHomeSubCategoryFragment", "ServiceAtHomeSubCategoryFragment");
//				bundle.putString("zoomlavel", String.valueOf(zoomlevel));
				Utils.storeString(mSharedPreferences, Utils.SUB_CATE_NAME, mSubCategoryArrayList.get(index).getName());
				findNearByHandymanFragment.setArguments(bundle);
//				getActivity().getSupportFragmentManager().popBackStack("ServiceAtHomeFragment", FragmentManager.POP_BACK_STACK_INCLUSIVE);
				getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, findNearByHandymanFragment).addToBackStack(null).commit();
			}
		}
	};

	private void getKoilometer() {
		if(Utils.checkInternetConnection(getActivity())){
			GetKilometersRequestTask getKilometersRequestTask = new GetKilometersRequestTask(getActivity());
			getKilometersRequestTask.setAsyncCallListener(new AsyncCallListener() {

				@SuppressWarnings("unchecked")
				@Override
				public void onResponseReceived(Object response) {
					AdvertiseListModel kilomerterModel = (AdvertiseListModel) response;
					if(kilomerterModel.getSuccess().equalsIgnoreCase("1")) {

						if(Utils.validateString(kilomerterModel.getOption_value())) {

							float km = Float.parseFloat(kilomerterModel.getOption_value());
							double r = km * 10000;
//							zoomlevel = calculateZoomLevel(mDeviceWidth, r);
							double zoom = 19 - Math.log((km * 15) * 5.508);
							zoomlevel = ((float) zoom);
							Logger.e(TAG, "Zoom_level :" + zoomlevel);
							Utils.storeString(mSharedPreferences,Utils.ZOOMLEVEL, String.valueOf(zoomlevel));

						} else {
							Toast.makeText(getActivity(), "Kilometer range not get", Toast.LENGTH_SHORT).show();
						}

					} else if(kilomerterModel.getSuccess().equalsIgnoreCase("0")) {
						Utils.showMessageDialog(getActivity(), getResources().getString(R.string.alert), kilomerterModel.getMessage());
					}

				}
				@Override
				public void onErrorReceived(String error) {
					Toast.makeText(getActivity(), getString(R.string.try_again), Toast.LENGTH_SHORT).show();
				}
			});
			getKilometersRequestTask.execute();
		}
	}

	private int calculateZoomLevel(int screenWidth, double range) {
		double equatorLength = 40075004; // in meters
		double widthInPixels = screenWidth;
		double metersPerPixel = equatorLength / 256;
		int zoomLevel = 1;
		while ((metersPerPixel * widthInPixels) > range) {
			metersPerPixel /= 2;
			++zoomLevel;
		}
		Logger.i(TAG, "zoom level = "+zoomLevel);
		return zoomLevel;
	}
	
}