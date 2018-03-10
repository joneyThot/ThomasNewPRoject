package com.android.handyman.adapter;

import java.util.ArrayList;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.android.handyman.MainActivity;
import com.android.handyman.R;
import com.android.handyman.SplashActivity;
import com.android.handyman.fragment.HandymanCustomerHireProfileFragment;
import com.android.handyman.fragment.TestFragmentPage;
import com.android.handyman.logger.Logger;
import com.android.handyman.model.MyHiringsModel;
import com.android.handyman.service.AsyncCallListener;
import com.android.handyman.service.GetHandymanHiringsListRequestTask;
import com.android.handyman.service.Utils;

public class MyHiringPagerAdapter_new extends PagerAdapter {
	protected static final String[] CONTENT = new String[] {"     Upcomming Hirings     ", "     Accepted Hirings     ","     Cancelled Hirings     ", "     Compeleted Hirings     ",	"     Rejected Hirings     ", "     All Hirings     "};
	private Context mContext;
	private static String TAG = "MyHiringPagerAdapter";
	private LayoutInflater mInflater;
	private SharedPreferences mSharedPreferences;
//	private ArrayList<MyHiringsModel> mMyHiringsModelsList;
	private ArrayList<MyHiringsModel> mPendingHiringsList;
	private ArrayList<MyHiringsModel> mCancelHiringsList;
	private ArrayList<MyHiringsModel> mCompleteHiringsList;
	private ArrayList<MyHiringsModel> mRejectedHiringsList;
	private ArrayList<MyHiringsModel> mAcceptedHiringsList;
	private ArrayList<MyHiringsModel> mAllHiringsList;

	MyHiringAdapter myHiringAdapter;
	HandymanHiringAdapter handymanHiringAdapter;
	ListView myhiringlist;

	public MyHiringPagerAdapter_new(Context context) {
		this.mContext = context;
		mSharedPreferences = mContext.getSharedPreferences(Utils.PREF_NAME, Context.MODE_PRIVATE);

	}

	@Override
	public int getCount() {
		return 6;
	}

	@Override
	public boolean isViewFromObject(View view, Object object) {
		return view == ((LinearLayout) object);
	}

	@Override
	public void destroyItem(ViewGroup container, int position, Object object) {
		((ViewPager) container).removeView((LinearLayout) object);
	}

	@Override
	public int getItemPosition(Object object) {
		// TODO Auto-generated method stub
		return 6;
	}
	
	@Override
	public CharSequence getPageTitle(int position) {
		return MyHiringPagerAdapter_new.CONTENT[position % CONTENT.length];
	}
	
	@Override
	public Object instantiateItem(ViewGroup container, final int position) {

		View vi = new View(mContext.getApplicationContext());
		mInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		vi = mInflater.inflate(R.layout.row_my_hirings_list, container, false);
		myhiringlist = (ListView) vi.findViewById(R.id.hirings_list);
		switch (position) {
		
		case 0:
			getHMPendingHirings(mSharedPreferences.getString(Utils.USER_ID, ""),"pending",String.valueOf(SplashActivity.latitude),String.valueOf(SplashActivity.longitude));
			break;
			
		case 1:
			getHMAcceptedHirings(mSharedPreferences.getString(Utils.USER_ID, ""),"active",String.valueOf(SplashActivity.latitude),String.valueOf(SplashActivity.longitude));
			break;

		case 2:
			getHMCancelHirings(mSharedPreferences.getString(Utils.USER_ID, ""),"cancelled",String.valueOf(SplashActivity.latitude),String.valueOf(SplashActivity.longitude));
			break;

		case 3:
			getHMCompleteHirings(mSharedPreferences.getString(Utils.USER_ID, ""),"completed",String.valueOf(SplashActivity.latitude),String.valueOf(SplashActivity.longitude));
			Logger.e(TAG, "Position ::" + "completed - " + position);
			break;

		case 4:
			getHMRejectHirings(mSharedPreferences.getString(Utils.USER_ID, ""),"rejected",String.valueOf(SplashActivity.latitude),String.valueOf(SplashActivity.longitude));
			break;

		case 5:
			getHMAllHirings(mSharedPreferences.getString(Utils.USER_ID, ""),"all",String.valueOf(SplashActivity.latitude),String.valueOf(SplashActivity.longitude));
			break;
		}
		
		((ViewPager) container).addView(vi);
		return vi;
	}

	private void getHMPendingHirings(String id, String status,String lat, String lng) {
		if(Utils.checkInternetConnection(mContext)){
			GetHandymanHiringsListRequestTask getHandymanHiringsListRequestTask = new GetHandymanHiringsListRequestTask(mContext);
			getHandymanHiringsListRequestTask.setAsyncCallListener(new AsyncCallListener() {

				@SuppressWarnings("unchecked")
				@Override
				public void onResponseReceived(Object response) {
					mPendingHiringsList = (ArrayList<MyHiringsModel>) response;
						Logger.e(TAG, "mPendingHiringsList SIZE -- " + mPendingHiringsList.size());
						if (mPendingHiringsList != null) {
							for (int i = 0; i < mPendingHiringsList.size(); i++) {
								handymanHiringAdapter = new HandymanHiringAdapter(mContext, mPendingHiringsList, onPendingClickListener);
								myhiringlist.setAdapter(handymanHiringAdapter);
								handymanHiringAdapter.notifyDataSetChanged();
							}
							
						} else {
							Toast.makeText(mContext, "Data not available. ", Toast.LENGTH_SHORT).show();
						}
				}
				@Override
				public void onErrorReceived(String error) {
					Toast.makeText(mContext, mContext.getString(R.string.try_again), Toast.LENGTH_SHORT).show();
				}
			});
			getHandymanHiringsListRequestTask.execute(id,status,lat,lng);
		}else{
			Utils.showMessageDialog(mContext, mContext.getResources().getString(R.string.alert), 
					mContext.getResources().getString(R.string.connection));
		}
	}
	
	OnClickListener onPendingClickListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			if(v.getTag() != null){
				int index = (Integer) v.getTag();
				Logger.e(TAG, "HIRE PENDING CLICK INDEX ::" + index);
				HandymanCustomerHireProfileFragment handymanCustomerHireProfileFragment = new HandymanCustomerHireProfileFragment();
				Bundle bundle = new Bundle();
				bundle.putSerializable(Utils.HANDYMAN_HIRE_PENDING_DETAILS, mPendingHiringsList.get(index));
				handymanCustomerHireProfileFragment.setArguments(bundle);
				((MainActivity) mContext).getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, handymanCustomerHireProfileFragment).addToBackStack(TAG).commit();
			}
		}
	};
	
	private void getHMAcceptedHirings(String id, String status,String lat, String lng) {
		if(Utils.checkInternetConnection(mContext)){
			GetHandymanHiringsListRequestTask getHandymanHiringsListRequestTask = new GetHandymanHiringsListRequestTask(mContext);
			getHandymanHiringsListRequestTask.setAsyncCallListener(new AsyncCallListener() {

				@SuppressWarnings("unchecked")
				@Override
				public void onResponseReceived(Object response) {
					mAcceptedHiringsList = (ArrayList<MyHiringsModel>) response;
						Logger.e(TAG, "mAcceptedHiringsList SIZE -- " + mAcceptedHiringsList.size());
						if (mAcceptedHiringsList != null) {
							for (int i = 0; i < mAcceptedHiringsList.size(); i++) {
								handymanHiringAdapter = new HandymanHiringAdapter(mContext, mAcceptedHiringsList, onAcceptedClickListener);
								myhiringlist.setAdapter(handymanHiringAdapter);
								handymanHiringAdapter.notifyDataSetChanged();
							}
							
						} else {
							Toast.makeText(mContext, "Data not available. ", Toast.LENGTH_SHORT).show();
						}
				}
				@Override
				public void onErrorReceived(String error) {
					Toast.makeText(mContext, mContext.getString(R.string.try_again), Toast.LENGTH_SHORT).show();
				}
			});
			getHandymanHiringsListRequestTask.execute(id,status,lat,lng);
		}else{
			Utils.showMessageDialog(mContext, mContext.getResources().getString(R.string.alert), 
					mContext.getResources().getString(R.string.connection));
		}
	}
	
	OnClickListener onAcceptedClickListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			if(v.getTag() != null){
				int index = (Integer) v.getTag();
				Logger.e(TAG, "HIRE ACCEPTED CLICK INDEX ::" + index);
				HandymanCustomerHireProfileFragment handymanCustomerHireProfileFragment = new HandymanCustomerHireProfileFragment();
				Bundle bundle = new Bundle();
				bundle.putSerializable(Utils.HANDYMAN_HIRE_ACCEPT_DETAILS, mAcceptedHiringsList.get(index));
				handymanCustomerHireProfileFragment.setArguments(bundle);
				((MainActivity) mContext).getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, handymanCustomerHireProfileFragment).addToBackStack(TAG).commit();
			}
		}
	};
	
	private void getHMCancelHirings(String id, String status,String lat, String lng) {
		if(Utils.checkInternetConnection(mContext)){
			GetHandymanHiringsListRequestTask getHandymanHiringsListRequestTask = new GetHandymanHiringsListRequestTask(mContext);
			getHandymanHiringsListRequestTask.setAsyncCallListener(new AsyncCallListener() {

				@SuppressWarnings("unchecked")
				@Override
				public void onResponseReceived(Object response) {
					mCancelHiringsList = (ArrayList<MyHiringsModel>) response;
						Logger.e(TAG, "mCancelHiringsList SIZE -- " + mCancelHiringsList.size());
						if (mCancelHiringsList != null) {
							for (int i = 0; i < mCancelHiringsList.size(); i++) {
								handymanHiringAdapter = new HandymanHiringAdapter(mContext, mCancelHiringsList, onCancelClickListener);
								myhiringlist.setAdapter(handymanHiringAdapter);
								handymanHiringAdapter.notifyDataSetChanged();
							}
							
						} else {
							Toast.makeText(mContext, "Data not available. ", Toast.LENGTH_SHORT).show();
						}
				}
				@Override
				public void onErrorReceived(String error) {
					Toast.makeText(mContext, mContext.getString(R.string.try_again), Toast.LENGTH_SHORT).show();
				}
			});
			getHandymanHiringsListRequestTask.execute(id,status,lat,lng);
		}else{
			Utils.showMessageDialog(mContext, mContext.getResources().getString(R.string.alert), 
					mContext.getResources().getString(R.string.connection));
		}
	}
	
	OnClickListener onCancelClickListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			if(v.getTag() != null){
				int index = (Integer) v.getTag();
				Logger.e(TAG, "HIRE CANCELCLICK INDEX ::" + index);
				HandymanCustomerHireProfileFragment handymanCustomerHireProfileFragment = new HandymanCustomerHireProfileFragment();
				Bundle bundle = new Bundle();
				bundle.putSerializable(Utils.HANDYMAN_HIRE_CANCEL_DETAILS, mCancelHiringsList.get(index));
				handymanCustomerHireProfileFragment.setArguments(bundle);
				((MainActivity) mContext).getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, handymanCustomerHireProfileFragment).addToBackStack(null).commit();
			}
		}
	};
	
	
	private void getHMCompleteHirings(String id, String status,String lat, String lng) {
		if(Utils.checkInternetConnection(mContext)){
			GetHandymanHiringsListRequestTask getHandymanHiringsListRequestTask = new GetHandymanHiringsListRequestTask(mContext);
			getHandymanHiringsListRequestTask.setAsyncCallListener(new AsyncCallListener() {

				@SuppressWarnings("unchecked")
				@Override
				public void onResponseReceived(Object response) {
					mCompleteHiringsList = (ArrayList<MyHiringsModel>) response;
						Logger.e(TAG, "mCompleteHiringsList SIZE -- " + mCompleteHiringsList.size());
						if (mCompleteHiringsList != null) {
							for (int i = 0; i < mCompleteHiringsList.size(); i++) {
								handymanHiringAdapter = new HandymanHiringAdapter(mContext, mCompleteHiringsList, onCompleteClickListener);
								myhiringlist.setAdapter(handymanHiringAdapter);
								handymanHiringAdapter.notifyDataSetChanged();
							}
							
						} else {
							Toast.makeText(mContext, "Data not available. ", Toast.LENGTH_SHORT).show();
						}
				}
				@Override
				public void onErrorReceived(String error) {
					Toast.makeText(mContext, mContext.getString(R.string.try_again), Toast.LENGTH_SHORT).show();
				}
			});
			getHandymanHiringsListRequestTask.execute(id,status,lat,lng);
		}else{
			Utils.showMessageDialog(mContext, mContext.getResources().getString(R.string.alert), 
					mContext.getResources().getString(R.string.connection));
		}
	}
	
	OnClickListener onCompleteClickListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			if(v.getTag() != null){
				int index = (Integer) v.getTag();
				Logger.e(TAG, "HIRE COMPELETE CLICK INDEX ::" + index);
				HandymanCustomerHireProfileFragment handymanCustomerHireProfileFragment = new HandymanCustomerHireProfileFragment();
				Bundle bundle = new Bundle();
				bundle.putSerializable(Utils.HANDYMAN_HIRE_COMPLETE_DETAILS, mCompleteHiringsList.get(index));
				handymanCustomerHireProfileFragment.setArguments(bundle);
				((MainActivity) mContext).getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, handymanCustomerHireProfileFragment).addToBackStack(null).commit();
			}
		}
	};
	
	private void getHMRejectHirings(String id, String status,String lat, String lng) {
		if(Utils.checkInternetConnection(mContext)){
			GetHandymanHiringsListRequestTask getHandymanHiringsListRequestTask = new GetHandymanHiringsListRequestTask(mContext);
			getHandymanHiringsListRequestTask.setAsyncCallListener(new AsyncCallListener() {

				@SuppressWarnings("unchecked")
				@Override
				public void onResponseReceived(Object response) {
					mRejectedHiringsList = (ArrayList<MyHiringsModel>) response;
						Logger.e(TAG, "mRejectedHiringsList SIZE -- " + mRejectedHiringsList.size());
						if (mRejectedHiringsList != null) {
							for (int i = 0; i < mRejectedHiringsList.size(); i++) {
								handymanHiringAdapter = new HandymanHiringAdapter(mContext, mRejectedHiringsList, onRejectClickListener);
								myhiringlist.setAdapter(handymanHiringAdapter);
								handymanHiringAdapter.notifyDataSetChanged();
							}
							
						} else {
							Toast.makeText(mContext, "Data not available. ", Toast.LENGTH_SHORT).show();
						}
				}
				@Override
				public void onErrorReceived(String error) {
					Toast.makeText(mContext, mContext.getString(R.string.try_again), Toast.LENGTH_SHORT).show();
				}
			});
			getHandymanHiringsListRequestTask.execute(id,status,lat,lng);
		}else{
			Utils.showMessageDialog(mContext, mContext.getResources().getString(R.string.alert), 
					mContext.getResources().getString(R.string.connection));
		}
	}
	
	OnClickListener onRejectClickListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			if(v.getTag() != null){
				int index = (Integer) v.getTag();
				Logger.e(TAG, "HIRE REJECT CLICK INDEX ::" + index);
				HandymanCustomerHireProfileFragment handymanCustomerHireProfileFragment = new HandymanCustomerHireProfileFragment();
				Bundle bundle = new Bundle();
				bundle.putSerializable(Utils.HANDYMAN_HIRE_REJECT_DETAILS, mRejectedHiringsList.get(index));
				handymanCustomerHireProfileFragment.setArguments(bundle);
				((MainActivity) mContext).getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, handymanCustomerHireProfileFragment).addToBackStack(null).commit();
			}
		}
	};
	
	private void getHMAllHirings(String id, String status,String lat, String lng) {
		if(Utils.checkInternetConnection(mContext)){
			GetHandymanHiringsListRequestTask getHandymanHiringsListRequestTask = new GetHandymanHiringsListRequestTask(mContext);
			getHandymanHiringsListRequestTask.setAsyncCallListener(new AsyncCallListener() {

				@SuppressWarnings("unchecked")
				@Override
				public void onResponseReceived(Object response) {
					mAllHiringsList = (ArrayList<MyHiringsModel>) response;
						Logger.e(TAG, "mAllHiringsList SIZE -- " + mAllHiringsList.size());
						if (mAllHiringsList != null) {
							for (int i = 0; i < mAllHiringsList.size(); i++) {
								handymanHiringAdapter = new HandymanHiringAdapter(mContext, mAllHiringsList, onAllClickListener);
								myhiringlist.setAdapter(handymanHiringAdapter);
								handymanHiringAdapter.notifyDataSetChanged();
							}
							
						} else {
							Toast.makeText(mContext, "Data not available. ", Toast.LENGTH_SHORT).show();
						}
				}
				@Override
				public void onErrorReceived(String error) {
					Toast.makeText(mContext, mContext.getString(R.string.try_again), Toast.LENGTH_SHORT).show();
				}
			});
			getHandymanHiringsListRequestTask.execute(id,status,lat,lng);
		}else{
			Utils.showMessageDialog(mContext, mContext.getResources().getString(R.string.alert), 
					mContext.getResources().getString(R.string.connection));
		}
	}
	
	OnClickListener onAllClickListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			if(v.getTag() != null){
				int index = (Integer) v.getTag();
				Logger.e(TAG, "HIRE ALL CLICK INDEX ::" + index);
				HandymanCustomerHireProfileFragment handymanCustomerHireProfileFragment = new HandymanCustomerHireProfileFragment();
				Bundle bundle = new Bundle();
				bundle.putSerializable(Utils.HANDYMAN_HIRE_ALL_DETAILS, mAllHiringsList.get(index));
				handymanCustomerHireProfileFragment.setArguments(bundle);
				((MainActivity) mContext).getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, handymanCustomerHireProfileFragment).addToBackStack(null).commit();
			}
		}
	};


}