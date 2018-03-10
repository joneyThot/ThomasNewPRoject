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
import com.android.handyman.adapter.CategoryAdapter;
import com.android.handyman.adapter.FavouriteAdapter;
import com.android.handyman.logger.Logger;
import com.android.handyman.model.DataModel;
import com.android.handyman.model.FavouriteHandymanModel;
import com.android.handyman.model.StateModel;
import com.android.handyman.service.AsyncCallListener;
import com.android.handyman.service.GetCategoryListRequestTask;
import com.android.handyman.service.GetFavouriteHandymanListRequestTask;
import com.android.handyman.service.Utils;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;

public class FavouriteHandymanFragment extends BaseFragment implements OnClickListener{
	
	private static String TAG = "FavouriteHandymanFragment";
	private ArrayList<FavouriteHandymanModel> mFavouriteHandymanModel = new ArrayList<FavouriteHandymanModel>();
	private SharedPreferences mSharedPreferences;
	
	FavouriteAdapter favouriteAdapter;
	Fragment fr;
	View mRootView;
	ListView mFavouriteList;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,	Bundle savedInstanceState) {
		mRootView = inflater.inflate(R.layout.fragment_favourite, container, false);
		initview();
		return mRootView;
	}

	private void initview() {
		mSharedPreferences = getActivity().getSharedPreferences(Utils.PREF_NAME, Context.MODE_PRIVATE); 
		((MainActivity) getActivity()).setTitleText(getString(R.string.menu_favourite_handyman), "", View.GONE, View.VISIBLE, View.GONE, View.GONE);
		getActivity().findViewById(R.id.title_back).setVisibility(View.GONE);	
		
		mActivity.getSlidingMenu().setMode(SlidingMenu.LEFT);
		mActivity.getSlidingMenu().setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);
		
		mFavouriteList = (ListView) mRootView.findViewById(R.id.favourite_list);
		getFavouriteHandymanList(mSharedPreferences.getString(Utils.USER_ID, ""));
		
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		}
	}
	
	private void getFavouriteHandymanList(String client_id) {
		if(Utils.checkInternetConnection(getActivity())){
			GetFavouriteHandymanListRequestTask getFavouriteHandymanListRequestTask = new GetFavouriteHandymanListRequestTask(getActivity());
			getFavouriteHandymanListRequestTask.setAsyncCallListener(new AsyncCallListener() {

				@SuppressWarnings("unchecked")
				@Override
				public void onResponseReceived(Object response) {
					mFavouriteHandymanModel.clear();
					
					DataModel dataModel = (DataModel) response;
					if(dataModel.getSuccess().equalsIgnoreCase("1")) {
						
//						mFavouriteHandymanModel = (ArrayList<FavouriteHandymanModel>) response;
						mFavouriteHandymanModel.addAll(dataModel.getFavouriteHandymanModel());
						Logger.e(TAG, "mFavouriteHandymanModel SIZE -- " + mFavouriteHandymanModel.size());
						if (mFavouriteHandymanModel.size() > 0) {
							favouriteAdapter = new FavouriteAdapter(getActivity(), mFavouriteHandymanModel, onClickListener);
							mFavouriteList.setAdapter(favouriteAdapter);
							favouriteAdapter.notifyDataSetChanged();
							
						} 
						
					} else if(dataModel.getSuccess().equalsIgnoreCase("0")) {
						Utils.showMessageDialog(getActivity(), getResources().getString(R.string.alert), dataModel.getMessage());
					}
					
				}
				@Override
				public void onErrorReceived(String error) {
					Toast.makeText(getActivity(), getString(R.string.try_again), Toast.LENGTH_SHORT).show();
				}
			});
			getFavouriteHandymanListRequestTask.execute(client_id);
		}else{
			Utils.showMessageDialog(getActivity(), getResources().getString(R.string.alert), getResources().getString(R.string.connection));
		}
	}
	
	OnClickListener onClickListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			if(v.getTag() != null){
				int index = (Integer) v.getTag();
				Logger.e(TAG, "CATEGORY CLICK INDEX ::" + index);
//				Utils.storeString(mSharedPreferences, Utils.CATEGORY_ID, mCategoryArrayList.get(index).getId());				
				HandymanProfileFragment handymanProfileFragment = new HandymanProfileFragment();
				Bundle bundle = new Bundle();
//				bundle.putSerializable(Utils.CATEGORY_ITEM_DETAILS, mCategoryArrayList.get(index));
				bundle.putString("FavouriteHandymanFragment", "FavouriteHandymanFragment");
				handymanProfileFragment.setArguments(bundle);
				Utils.storeString(mSharedPreferences, Utils.HANDYMAN_ID, mFavouriteHandymanModel.get(index).handyman_id);
				getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, handymanProfileFragment).addToBackStack(null).commit();
			}
		}
	};
	
	
}
