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
import android.widget.TextView;
import android.widget.Toast;

import com.android.handyman.MainActivity;
import com.android.handyman.R;
import com.android.handyman.adapter.CustomerCraditsAdapter;
import com.android.handyman.logger.Logger;
import com.android.handyman.model.CreditsModel;
import com.android.handyman.model.DataModel;
import com.android.handyman.service.AsyncCallListener;
import com.android.handyman.service.GetCustomerCreditsListRequestTask;
import com.android.handyman.service.RemoveCustomerCreditsRequestTask;
import com.android.handyman.service.Utils;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;

public class MyCreditsFragment extends BaseFragment implements OnClickListener{
	
	private static String TAG = "MyCreditsFragment";
	
	private SharedPreferences mSharedPreferences;
	private ArrayList<CreditsModel> mCreditsModelArrayList = new ArrayList<CreditsModel>();
	
	CustomerCraditsAdapter customerCraditsAdapter;
	Fragment fr;
	View mRootView;
	ListView mCreditsList;
	TextView mTotalCraditsValue;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,	Bundle savedInstanceState) {
		mRootView = inflater.inflate(R.layout.fragment_my_credits, container, false);
		initview();
		return mRootView;
	}

	private void initview() {
		mSharedPreferences = getActivity().getSharedPreferences(Utils.PREF_NAME, Context.MODE_PRIVATE); 
		((MainActivity) getActivity()).setTitleText(getString(R.string.menu_my_cradits), "", View.GONE, View.VISIBLE, View.GONE,View.GONE);
		getActivity().findViewById(R.id.title_back).setVisibility(View.GONE);	
		
		mActivity.getSlidingMenu().setMode(SlidingMenu.LEFT);
		mActivity.getSlidingMenu().setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);
		
		mTotalCraditsValue = (TextView) mRootView.findViewById(R.id.cradits_value_text);
		mCreditsList = (ListView) mRootView.findViewById(R.id.credits_list);
		
		mRootView.findViewById(R.id.request_more_credit).setOnClickListener(this);
		
		getCustomerCredits(mSharedPreferences.getString(Utils.USER_ID, ""));
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		
		case R.id.request_more_credit:
			getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, new PurchaseCreditsFragment()).addToBackStack(null).commit();
			break;
		}
	}
	
	private void getCustomerCredits(String id) {
		if(Utils.checkInternetConnection(getActivity())){
			GetCustomerCreditsListRequestTask getCustomerCreditsListRequestTask = new GetCustomerCreditsListRequestTask(getActivity());
			getCustomerCreditsListRequestTask.setAsyncCallListener(new AsyncCallListener() {

				@SuppressWarnings("unchecked")
				@Override
				public void onResponseReceived(Object response) {
					mCreditsModelArrayList.clear();
					DataModel dataModel = (DataModel) response;
					if(dataModel.getSuccess().equalsIgnoreCase("1")){
//						mCreditsModelArrayList = (ArrayList<CreditsModel>) response;
						mCreditsModelArrayList.addAll(dataModel.getCreditsModelList());
						Logger.e(TAG, "mCreditsModelArrayList SIZE -- " + mCreditsModelArrayList.size());
						if (mCreditsModelArrayList.size() > 0) {
							
							mTotalCraditsValue.setText(mCreditsModelArrayList.get(0).total_amount + " CREDITS");
							
							if(mCreditsModelArrayList.get(0).status.equalsIgnoreCase("1")){
								customerCraditsAdapter = new CustomerCraditsAdapter(getActivity(), mCreditsModelArrayList, onCreditRemoveClickListener);
								mCreditsList.setAdapter(customerCraditsAdapter);
								customerCraditsAdapter.notifyDataSetChanged();
							} 
							
						} 
					} 
					
						
						
				}
				@Override
				public void onErrorReceived(String error) {
					Toast.makeText(getActivity(), getString(R.string.try_again), Toast.LENGTH_SHORT).show();
				}
			});
			getCustomerCreditsListRequestTask.execute(id);
		}else{
			Utils.showMessageDialog(getActivity(), getResources().getString(R.string.alert), 
					getResources().getString(R.string.connection));
		}
	}
	
	OnClickListener onCreditRemoveClickListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			if(v.getTag() != null){
				int index = (Integer) v.getTag();
				deleteListItem(mCreditsModelArrayList.get(index).getId());
				mCreditsModelArrayList.remove(index);
				customerCraditsAdapter.notifyDataSetChanged();
			}
		}
	};
	
	private void deleteListItem(String id) {
		if (Utils.checkInternetConnection(getActivity())) {
			RemoveCustomerCreditsRequestTask removeCustomerCreditsRequestTask = new RemoveCustomerCreditsRequestTask(getActivity());
			removeCustomerCreditsRequestTask.setAsyncCallListener(new AsyncCallListener() {
				@Override
				public void onResponseReceived(Object response) {
					
					Toast.makeText(getActivity(), "Delete Successfully", Toast.LENGTH_SHORT).show();
				}
				@Override
				public void onErrorReceived(String error) {
					Toast.makeText(getActivity(), "Try Again", Toast.LENGTH_SHORT).show();
				}
			});
			removeCustomerCreditsRequestTask.execute(id);
		} else {
			Toast.makeText(getActivity(), "Internet Connection Not Available", Toast.LENGTH_SHORT).show();
		}

	}
	
	
}
