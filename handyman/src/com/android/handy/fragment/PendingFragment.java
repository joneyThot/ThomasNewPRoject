package com.android.handy.fragment;

import java.util.ArrayList;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.handy.MainActivity;
import com.android.handy.R;
import com.android.handy.SplashActivity;
import com.android.handy.adapter.HandymanHiringAdapter;
import com.android.handy.logger.Logger;
import com.android.handy.model.DataModel;
import com.android.handy.model.MyHiringsModel;
import com.android.handy.service.AsyncCallListener;
import com.android.handy.service.GetHandymanHiringsListRequestTask;
import com.android.handy.service.Utils;

public class PendingFragment extends BaseFragment implements OnClickListener, AbsListView.OnScrollListener,SwipeRefreshLayout.OnRefreshListener{
	
	private static String TAG = "PendingFragment";
	private SharedPreferences mSharedPreferences;
	private ArrayList<MyHiringsModel> mPendingHiringsList = new ArrayList<MyHiringsModel>();
	ArrayList<MyHiringsModel> mTempHiringslist = new ArrayList<MyHiringsModel>();
	
	HandymanHiringAdapter handymanHiringAdapter = null;
	ListView myhiringlist;
	
	private String title;
    private int page;

    Fragment fr;
	View mRootView,footerView = null;
	
	SwipeRefreshLayout swipeRefreshLayout;
	
    int currentPage = 1;
    boolean isLoading = false,isDataFinished = false;
    private Context context;
    private ProgressBar progressBar = null;
    boolean flag = false;
    ProgressBar bar = null;
	
	public static Fragment newInstance(int page , String title) {
		PendingFragment fragmentFirst = new PendingFragment();
		Bundle args = new Bundle();
		args.putInt("someInt", page);
		args.putString("someTitle", title);
		fragmentFirst.setArguments(args);
		return fragmentFirst;
	}

	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		page = getArguments().getInt("someInt", 0);
        title = getArguments().getString("someTitle");
	}
	
	@Override
	public void onAttach(Context activity) {
		super.onAttach(	activity);
		context = activity;
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,	Bundle savedInstanceState) {
		mRootView = inflater.inflate(R.layout.row_my_hirings_list, container, false);
		initview();
		return mRootView;
	}

	private void initview() {
		mSharedPreferences = getActivity().getSharedPreferences(Utils.PREF_NAME, Context.MODE_PRIVATE); 
		
		myhiringlist = (ListView) mRootView.findViewById(R.id.hirings_list);
		swipeRefreshLayout = (SwipeRefreshLayout) mRootView.findViewById(R.id.swipe_refresh_hiring_layout);
		footerView = ((MainActivity) context).getLayoutInflater().inflate(R.layout.loadmore, null);
        progressBar = (ProgressBar) footerView.findViewById(R.id.progress_bar);
        
        myhiringlist.setOnScrollListener(this);
        swipeRefreshLayout.setOnRefreshListener(this);
        myhiringlist.addFooterView(footerView);
        
        swipeRefreshLayout.setColorSchemeColors(Color.RED, Color.GREEN, Color.BLUE, Color.CYAN);
        bar = (ProgressBar) mRootView.findViewById(R.id.prog);
        
        if(mSharedPreferences.getString(Utils.ACCEPT, "").equalsIgnoreCase("ACCEPT")){
        	Logger.e(TAG, "ACCEPT");
        	mPendingHiringsList.clear();
        	mTempHiringslist.clear();
        	handymanHiringAdapter = null;
        	currentPage = 1;
        	Utils.storeString(mSharedPreferences, Utils.ACCEPT, "");
        	Utils.storeString(mSharedPreferences, Utils.PENDING_ACCEPT, "PENDING_ACCEPT");
        	
        	
        } else if(mSharedPreferences.getString(Utils.CANCEL, "").equalsIgnoreCase("CANCEL")){
        	Logger.e(TAG, "CANCEL");
        	mPendingHiringsList.clear();
        	mTempHiringslist.clear();
        	handymanHiringAdapter = null;
        	currentPage = 1;
        	Utils.storeString(mSharedPreferences, Utils.CANCEL, "");
        	Utils.storeString(mSharedPreferences, Utils.PENDING_CANCEL, "PENDING_CANCEL");
        	
        } 
        
	        if(mTempHiringslist.size() > 0){
	        	Logger.d(TAG, "SAVE Pending Handyman size :: " + mTempHiringslist.size());
	    		handymanHiringAdapter = new HandymanHiringAdapter(getActivity(), mTempHiringslist, onPendingClickListener);
				myhiringlist.setAdapter(handymanHiringAdapter);
				handymanHiringAdapter.notifyDataSetChanged();
	    	} else {
	    		if(flag == false){
	    			bar.setVisibility(View.VISIBLE);
	    			bar.setClickable(flag);
	    			flag = true;
				 } 
	    		
	    	}
        
        
        if (progressBar != null) {
            if (progressBar.getVisibility() == View.VISIBLE) {
                setProgressVisibility(View.GONE);
                removeFooter();
             }
       }
        
//        swipeRefreshLayout.post(new Runnable() {
//			@Override
//			public void run() {
//				swipeRefreshLayout.setRefreshing(true);
//
//				 if(mPendingHiringsList.size() == 0){
//		    			getHMPendingHirings(mSharedPreferences.getString(Utils.USER_ID, ""),"pending",String.valueOf(currentPage));
//		        }
//			}
//		});
        
		 if(mPendingHiringsList.size() == 0){
			
   			getHMPendingHirings(mSharedPreferences.getString(Utils.USER_ID, ""),"pending",String.valueOf(currentPage));
       }
		
	}
	
	private void setProgressVisibility(int visibility) {
        progressBar.setVisibility(visibility);
    }
 
	 protected void removeFooter() {
		 myhiringlist.removeFooterView(footerView);
	    }
	 
	 protected void addFooter() {
	        if (myhiringlist.getFooterViewsCount() == 0)
	        	myhiringlist.addFooterView(footerView);
	    }

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		}
	}
	
	
	private void getHMPendingHirings(String id, String status, String page) {
//		if(Utils.checkInternetConnection(getActivity())){
			GetHandymanHiringsListRequestTask getHandymanHiringsListRequestTask = new GetHandymanHiringsListRequestTask(getActivity());
			getHandymanHiringsListRequestTask.setAsyncCallListener(new AsyncCallListener() {

				@SuppressWarnings("unchecked")
				@Override
				public void onResponseReceived(Object response) {
					mPendingHiringsList.clear();
					DataModel dataModel = (DataModel) response;
					if(dataModel.getSuccess().equalsIgnoreCase("1")){
//						mPendingHiringsList.addAll((ArrayList<MyHiringsModel>) response);
						mPendingHiringsList.addAll(dataModel.getMyOrderList());
						Logger.e(TAG, "mPendingHiringsList SIZE -- " + mPendingHiringsList.size());
						if (mPendingHiringsList.size() > 0) {
//							for (int i = 0; i < mPendingHiringsList.size(); i++) {
//								loadingMore = false;
								myhiringlist.setVisibility(View.VISIBLE);
								mRootView.findViewById(R.id.not_found_text).setVisibility(View.GONE);
								bar.setVisibility(View.GONE);
//								handymanHiringAdapter = new HandymanHiringAdapter(getActivity(), mPendingHiringsList, onPendingClickListener);
//								myhiringlist.setAdapter(handymanHiringAdapter);
//								handymanHiringAdapter.notifyDataSetChanged();
								
								setView(mPendingHiringsList);
								isLoading = false;
			                    setProgressVisibility(View.GONE);
			                    removeFooter();
//							}
							
						} /*else {
//							
						}*/
						
					} else if(dataModel.getSuccess().equalsIgnoreCase("0")){
						myhiringlist.setVisibility(View.GONE);
						bar.setVisibility(View.GONE);
	    				 mRootView.findViewById(R.id.not_found_text).setVisibility(View.VISIBLE);
	    				 ((TextView)mRootView.findViewById(R.id.not_found_text)).setText(dataModel.getMessage());
	    				 
//						bar.setVisibility(View.VISIBLE);
						isDataFinished = true;
						if(mTempHiringslist.isEmpty() == true){
							myhiringlist.setAdapter(null);
								
						}
						new Handler().postDelayed(new Runnable() {

			    			@Override
			    			public void run() {
			    				 setProgressVisibility(View.GONE);
			    		         removeFooter();
			    			}
			    		}, 3000);
					}
					
						
						swipeRefreshLayout.setRefreshing(false);
					
				}
				@Override
				public void onErrorReceived(String error) {
					Toast.makeText(getActivity(), getString(R.string.try_again), Toast.LENGTH_SHORT).show();
					swipeRefreshLayout.setRefreshing(false);
				}
			});
			getHandymanHiringsListRequestTask.execute(id,status,page);
//		}else{
//			Utils.showMessageDialog(getActivity(), getResources().getString(R.string.alert), 
//					getResources().getString(R.string.connection));
//		}
	}
	
	OnClickListener onPendingClickListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			if(v.getTag() != null){
				int index = (Integer) v.getTag();
				Logger.e(TAG, "HIRE PENDING CLICK INDEX ::" + index);
				HandymanCustomerHireProfileFragment handymanCustomerHireProfileFragment = new HandymanCustomerHireProfileFragment();
				Bundle bundle = new Bundle();
				bundle.putSerializable(Utils.HANDYMAN_HIRE_PENDING_DETAILS, mTempHiringslist.get(index));
				handymanCustomerHireProfileFragment.setArguments(bundle);
				getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, handymanCustomerHireProfileFragment).addToBackStack(TAG).commit();
			}
		}
	};

	
	private void setView(ArrayList<MyHiringsModel> hiringAllList) {
		if(isAdded()) {
			mTempHiringslist.addAll(hiringAllList);
			Logger.d(TAG, "SIZE of setview  Pending Handyman ::" + mTempHiringslist.size());
			if(hiringAllList.size() > 0) {
				
				if(handymanHiringAdapter == null){
					handymanHiringAdapter = new HandymanHiringAdapter(getActivity(), mTempHiringslist, onPendingClickListener);
					myhiringlist.setAdapter(handymanHiringAdapter);
					handymanHiringAdapter.notifyDataSetChanged();
				} else {
					handymanHiringAdapter.setList(mTempHiringslist);
					handymanHiringAdapter.notifyDataSetChanged();
				}
			}
		}
	}
	
	@Override
	public void onScrollStateChanged(AbsListView view, int scrollState) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
		int lastInScreen = firstVisibleItem + visibleItemCount;
        if (lastInScreen == totalItemCount && totalItemCount != 0 && !isLoading && !isDataFinished) {
            
            if(mPendingHiringsList.size() >= 10){
            	Utils.flag_hiring = true;
            	isLoading = true;
                currentPage++;
                setProgressVisibility(View.VISIBLE);
    	        addFooter();
        		getHMPendingHirings(mSharedPreferences.getString(Utils.USER_ID, ""),"pending",String.valueOf(currentPage));
        	}
        } 
		
	}


	@Override
	public void onRefresh() {
		if(bar.getVisibility() == View.GONE){
			swipeRefreshLayout.setRefreshing(true);
			
			new Handler().postDelayed(new Runnable() {
		        @Override
		        public void run() {
		        	mPendingHiringsList.clear();
		        	mTempHiringslist.clear();
		        	handymanHiringAdapter = null;
		    		currentPage = 1;
		    		isLoading = true;
		    		isDataFinished = false;
		    		 if(mPendingHiringsList.size() == 0){
		    			 Utils.flag_hiring = true;
		     			getHMPendingHirings(mSharedPreferences.getString(Utils.USER_ID, ""),"pending",String.valueOf(currentPage));
		         }
		        }
		    }, 5000);
		} else {
			swipeRefreshLayout.setRefreshing(false);
		}
		
	}

	
	
}
