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
import com.android.handyman.fragment.HandymanCustomerHireProfileFragment;
import com.android.handyman.logger.Logger;
import com.android.handyman.model.MyHiringsModel;
import com.android.handyman.service.AsyncCallListener;
import com.android.handyman.service.GetHandymanHiringsListRequestTask;
import com.android.handyman.service.GetMyHiringsListRequestTask;
import com.android.handyman.service.Utils;

/**
 * Created by User20 on 6/10/2015.
 */
public class MyHiringPagerAdapter extends PagerAdapter{
	protected static final String[] CONTENT = new String[] {
			"     Upcomming Hirings     ", "     Accepted Hirings     ","     Cancelled Hirings     ", "     Compeleted Hirings     ",	"     Rejected Hirings     ", "     All Hirings     ", };
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
	private int mCount = CONTENT.length;
	
	MyHiringAdapter myHiringAdapter;
	HandymanHiringAdapter handymanHiringAdapter;
	ListView myhiringlist;

	public MyHiringPagerAdapter(Context context, ArrayList<MyHiringsModel> mPendingHiringsList, ArrayList<MyHiringsModel> mAcceptedHiringsList, ArrayList<MyHiringsModel> mCancelHiringsList, ArrayList<MyHiringsModel> mCompleteHiringsList, ArrayList<MyHiringsModel> mRejectedHiringsList, ArrayList<MyHiringsModel> mAllHiringsList) {
		this.mContext = context;
		this.mPendingHiringsList = mPendingHiringsList;
		this.mCancelHiringsList = mCancelHiringsList;
		this.mCompleteHiringsList = mCompleteHiringsList;
		this.mRejectedHiringsList = mRejectedHiringsList;
		this.mAcceptedHiringsList = mAcceptedHiringsList;
		this.mAllHiringsList = mAllHiringsList;

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

	/*public void setCount(int count) {
		if (count >= 1  && count <= 5) {
			mCount = count;
			notifyDataSetChanged();
		}
	}*/
	
	@Override
	public int getItemPosition(Object object) {
		// TODO Auto-generated method stub
		return 6;
	}
	
	@Override
	public CharSequence getPageTitle(int position) {
		return MyHiringPagerAdapter.CONTENT[position % CONTENT.length];
	}

	@Override
	public Object instantiateItem(ViewGroup container, final int position) {

		View vi = new View(mContext.getApplicationContext());
		mInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		vi = mInflater.inflate(R.layout.row_my_hirings_list, container, false);
		myhiringlist = (ListView) vi.findViewById(R.id.hirings_list);

//		if(mSharedPreferences.getString(Utils.USER_TYPE, "").equalsIgnoreCase("customer")){
			/*switch (position) {
			case 0:
				if (mPendingHiringsList.size() > 0) {
					for (int i = 0; i < mPendingHiringsList.size(); i++) {
						myhiringlist.setVisibility(View.VISIBLE);
						vi.findViewById(R.id.not_found_text).setVisibility(View.GONE);
						myHiringAdapter = new MyHiringAdapter(mContext, mPendingHiringsList);
						myhiringlist.setAdapter(myHiringAdapter);
						myHiringAdapter.notifyDataSetChanged();
					}
				} else {
					myhiringlist.setVisibility(View.GONE);
					vi.findViewById(R.id.not_found_text).setVisibility(View.VISIBLE);
				}
				break;
				
			case 1:
				if (mAcceptedHiringsList.size() > 0) {
					for (int i = 0; i < mAcceptedHiringsList.size(); i++) {
						myhiringlist.setVisibility(View.VISIBLE);
						vi.findViewById(R.id.not_found_text).setVisibility(View.GONE);
						myHiringAdapter = new MyHiringAdapter(mContext, mAcceptedHiringsList);
						myhiringlist.setAdapter(myHiringAdapter);
						myHiringAdapter.notifyDataSetChanged();
					}
				} else {
					myhiringlist.setVisibility(View.GONE);
					vi.findViewById(R.id.not_found_text).setVisibility(View.VISIBLE);
				}
				break;
				
			case 2:
				if (mCancelHiringsList.size() > 0) {
					for (int i = 0; i < mCancelHiringsList.size(); i++) {
						myhiringlist.setVisibility(View.VISIBLE);
						vi.findViewById(R.id.not_found_text).setVisibility(View.GONE);
						myHiringAdapter = new MyHiringAdapter(mContext, mCancelHiringsList);
						myhiringlist.setAdapter(myHiringAdapter);
						myHiringAdapter.notifyDataSetChanged();
					}
				} else {
					myhiringlist.setVisibility(View.GONE);
					vi.findViewById(R.id.not_found_text).setVisibility(View.VISIBLE);
				}
				break;
			case 3:
				if (mCompleteHiringsList.size() > 0) {
					for (int i = 0; i < mCompleteHiringsList.size(); i++) {
						myhiringlist.setVisibility(View.VISIBLE);
						vi.findViewById(R.id.not_found_text).setVisibility(View.GONE);
						myHiringAdapter = new MyHiringAdapter(mContext, mCompleteHiringsList);
						myhiringlist.setAdapter(myHiringAdapter);
						myHiringAdapter.notifyDataSetChanged();
					}
				} else {
					myhiringlist.setVisibility(View.GONE);
					vi.findViewById(R.id.not_found_text).setVisibility(View.VISIBLE);
				}
				break;
			case 4:
				if (mRejectedHiringsList.size() > 0) {
					for (int i = 0; i < mRejectedHiringsList.size(); i++) {
						myhiringlist.setVisibility(View.VISIBLE);
						vi.findViewById(R.id.not_found_text).setVisibility(View.GONE);
						myHiringAdapter = new MyHiringAdapter(mContext, mRejectedHiringsList);
						myhiringlist.setAdapter(myHiringAdapter);
						myHiringAdapter.notifyDataSetChanged();
					}
				} else {
					myhiringlist.setVisibility(View.GONE);
					vi.findViewById(R.id.not_found_text).setVisibility(View.VISIBLE);
				}
				break;
			case 5:
				if (mAllHiringsList.size() > 0) {
					for (int i = 0; i < mAllHiringsList.size(); i++) {
						myhiringlist.setVisibility(View.VISIBLE);
						vi.findViewById(R.id.not_found_text).setVisibility(View.GONE);
						myHiringAdapter = new MyHiringAdapter(mContext, mAllHiringsList);
						myhiringlist.setAdapter(myHiringAdapter);
						myHiringAdapter.notifyDataSetChanged();
					}
				} else {
					myhiringlist.setVisibility(View.GONE);
					vi.findViewById(R.id.not_found_text).setVisibility(View.VISIBLE);
				}
				break;
			}*/
		/*} else {
			switch (position) {
			case 0:
				if (mPendingHiringsList.size() > 0) {
					for (int i = 0; i < mPendingHiringsList.size(); i++) {
						myhiringlist.setVisibility(View.VISIBLE);
						vi.findViewById(R.id.not_found_text).setVisibility(View.GONE);
						handymanHiringAdapter = new HandymanHiringAdapter(mContext, mPendingHiringsList, onPendingClickListener);
						myhiringlist.setAdapter(handymanHiringAdapter);
						handymanHiringAdapter.notifyDataSetChanged();
					}
				} else {
					myhiringlist.setVisibility(View.GONE);
					vi.findViewById(R.id.not_found_text).setVisibility(View.VISIBLE);
				}
				break;
				
			case 1:
				if (mAcceptedHiringsList.size() > 0) {
					for (int i = 0; i < mAcceptedHiringsList.size(); i++) {
						myhiringlist.setVisibility(View.VISIBLE);
						vi.findViewById(R.id.not_found_text).setVisibility(View.GONE);
						handymanHiringAdapter = new HandymanHiringAdapter(mContext, mAcceptedHiringsList, onAcceptedClickListener);
						myhiringlist.setAdapter(handymanHiringAdapter);
						handymanHiringAdapter.notifyDataSetChanged();
					}
				} else {
					myhiringlist.setVisibility(View.GONE);
					vi.findViewById(R.id.not_found_text).setVisibility(View.VISIBLE);
				}
				break;
			case 2:
				if (mCancelHiringsList.size() > 0) {
					for (int i = 0; i < mCancelHiringsList.size(); i++) {
						myhiringlist.setVisibility(View.VISIBLE);
						vi.findViewById(R.id.not_found_text).setVisibility(View.GONE);
						handymanHiringAdapter = new HandymanHiringAdapter(mContext, mCancelHiringsList, onCancelClickListener);
						myhiringlist.setAdapter(handymanHiringAdapter);
						handymanHiringAdapter.notifyDataSetChanged();
					}
				} else {
					myhiringlist.setVisibility(View.GONE);
					vi.findViewById(R.id.not_found_text).setVisibility(View.VISIBLE);
				}
				break;
			case 3:
				if (mCompleteHiringsList.size() > 0) {
					for (int i = 0; i < mCompleteHiringsList.size(); i++) {
						myhiringlist.setVisibility(View.VISIBLE);
						vi.findViewById(R.id.not_found_text).setVisibility(View.GONE);
						handymanHiringAdapter = new HandymanHiringAdapter(mContext, mCompleteHiringsList, onCompleteClickListener);
						myhiringlist.setAdapter(handymanHiringAdapter);
						handymanHiringAdapter.notifyDataSetChanged();
					}
				} else {
					myhiringlist.setVisibility(View.GONE);
					vi.findViewById(R.id.not_found_text).setVisibility(View.VISIBLE);
				}
				break;
			case 4:
				if (mRejectedHiringsList.size() > 0) {
					for (int i = 0; i < mRejectedHiringsList.size(); i++) {
						myhiringlist.setVisibility(View.VISIBLE);
						vi.findViewById(R.id.not_found_text).setVisibility(View.GONE);
						handymanHiringAdapter = new HandymanHiringAdapter(mContext, mRejectedHiringsList, onRejectClickListener);
						myhiringlist.setAdapter(handymanHiringAdapter);
						handymanHiringAdapter.notifyDataSetChanged();
					}
				} else {
					myhiringlist.setVisibility(View.GONE);
					vi.findViewById(R.id.not_found_text).setVisibility(View.VISIBLE);
				}
				break;
			case 5:
				if (mAllHiringsList.size() > 0) {
					for (int i = 0; i < mAllHiringsList.size(); i++) {
						myhiringlist.setVisibility(View.VISIBLE);
						vi.findViewById(R.id.not_found_text).setVisibility(View.GONE);
						handymanHiringAdapter = new HandymanHiringAdapter(mContext, mAllHiringsList, onAllClickListener);
						myhiringlist.setAdapter(handymanHiringAdapter);
						handymanHiringAdapter.notifyDataSetChanged();
					}
				} else {
					myhiringlist.setVisibility(View.GONE);
					vi.findViewById(R.id.not_found_text).setVisibility(View.VISIBLE);
				}
				break;
			}*/
//		}

		((ViewPager) container).addView(vi);

		return vi;
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
