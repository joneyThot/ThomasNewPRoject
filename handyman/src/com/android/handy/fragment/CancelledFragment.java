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

public class CancelledFragment extends BaseFragment implements OnClickListener, AbsListView.OnScrollListener,SwipeRefreshLayout.OnRefreshListener{
	
	private static String TAG = "CancelledFragment";
	private SharedPreferences mSharedPreferences;
	private ArrayList<MyHiringsModel> mCancelHiringsList = new ArrayList<MyHiringsModel>();
	ArrayList<MyHiringsModel> mTempHiringslist = new ArrayList<MyHiringsModel>();
	HandymanHiringAdapter handymanHiringAdapter = null;
	ListView myhiringlist;
	
	private String title;
    private int page;
    
    SwipeRefreshLayout swipeRefreshLayout;

    Fragment fr;
	View mRootView,footerView = null;
	
    int currentPage = 1;
    boolean isLoading = false,isDataFinished = false;
    private Context context;
    private ProgressBar progressBar = null;
    boolean flag = false;
    ProgressBar bar = null;
	
	public static Fragment newInstance(int page , String title) {
		CancelledFragment fragmentFirst = new CancelledFragment();
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
        
        if(mSharedPreferences.getString(Utils.PENDING_CANCEL, "").equalsIgnoreCase("PENDING_CANCEL")){
        	Logger.e(TAG, "PENDING_CANCEL");
        	mCancelHiringsList.clear();
        	mTempHiringslist.clear();
        	handymanHiringAdapter = null;
        	currentPage = 1;
        	Utils.storeString(mSharedPreferences, Utils.PENDING_CANCEL, "");
        	Utils.storeString(mSharedPreferences, Utils.PENDING_CANCEL_ALL, "PENDING_CANCEL_ALL");
        	
        } else if(mSharedPreferences.getString(Utils.ACCEPT_CANCEL, "").equalsIgnoreCase("ACCEPT_CANCEL")){
        	Logger.e(TAG, "ACCEPT_CANCEL");
        	mCancelHiringsList.clear();
        	mTempHiringslist.clear();
        	handymanHiringAdapter = null;
        	currentPage = 1;
        	Utils.storeString(mSharedPreferences, Utils.ACCEPT_CANCEL, "");
        	Utils.storeString(mSharedPreferences, Utils.ACCEPT_CANCEL_ALL, "ACCEPT_CANCEL_ALL");
        	
        } else if(mSharedPreferences.getString(Utils.START_CANCEL, "").equalsIgnoreCase("START_CANCEL")){
        	Logger.e(TAG, "START_CANCEL");
        	mCancelHiringsList.clear();
        	mTempHiringslist.clear();
        	handymanHiringAdapter = null;
        	currentPage = 1;
        	Utils.storeString(mSharedPreferences, Utils.START_CANCEL, "");
        	Utils.storeString(mSharedPreferences, Utils.START_CANCEL_ALL, "START_CANCEL_ALL");
        } 
        
	        if(mTempHiringslist.size() > 0){
	        	Logger.d(TAG, "SAVE Cancel Handyman size :: " + mTempHiringslist.size());
	        	handymanHiringAdapter = new HandymanHiringAdapter(getActivity(), mTempHiringslist, onAcceptedClickListener);
				myhiringlist.setAdapter(handymanHiringAdapter);
				handymanHiringAdapter.notifyDataSetChanged();
	    	} else {
	    		if(flag == false){
	    			bar.setVisibility(View.VISIBLE);
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
//				 if(mCancelHiringsList.size() == 0){
//		    			getHMPendingHirings(mSharedPreferences.getString(Utils.USER_ID, ""),"cancelled",String.valueOf(currentPage));
//		    		}
//			}
//		});
        
		 if(mCancelHiringsList.size() == 0){
			 
   			getHMPendingHirings(mSharedPreferences.getString(Utils.USER_ID, ""),"cancelled",String.valueOf(currentPage));
   		}
        
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
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
	
	
	private void getHMPendingHirings(String id, String status,String page) {
//		if(Utils.checkInternetConnection(getActivity())){
			GetHandymanHiringsListRequestTask getHandymanHiringsListRequestTask = new GetHandymanHiringsListRequestTask(getActivity());
			getHandymanHiringsListRequestTask.setAsyncCallListener(new AsyncCallListener() {

				@SuppressWarnings("unchecked")
				@Override
				public void onResponseReceived(Object response) {
					mCancelHiringsList.clear();
					DataModel dataModel = (DataModel) response;
					if(dataModel.getSuccess().equalsIgnoreCase("1")){
						
//						mCancelHiringsList = (ArrayList<MyHiringsModel>) response;
						mCancelHiringsList.addAll(dataModel.getMyOrderList());
						Logger.e(TAG, "mCancelHiringsList SIZE -- " + mCancelHiringsList.size());
						if (mCancelHiringsList.size() > 0) {
							
							myhiringlist.setVisibility(View.VISIBLE);
							mRootView.findViewById(R.id.not_found_text).setVisibility(View.GONE);
							bar.setVisibility(View.GONE);
							
							setView(mCancelHiringsList);
							isLoading = false;
		                    setProgressVisibility(View.GONE);
		                    removeFooter();
		                    
							/*for (int i = 0; i < mCancelHiringsList.size(); i++) {
								myhiringlist.setVisibility(View.VISIBLE);
								mRootView.findViewById(R.id.not_found_text).setVisibility(View.GONE);
								handymanHiringAdapter = new HandymanHiringAdapter(getActivity(), mCancelHiringsList, onAcceptedClickListener);
								myhiringlist.setAdapter(handymanHiringAdapter);
								handymanHiringAdapter.notifyDataSetChanged();
							}*/
							
						} /*else {
//							
							
						}*/
						
					} else if(dataModel.getSuccess().equalsIgnoreCase("0")){
						myhiringlist.setVisibility(View.GONE);
						bar.setVisibility(View.GONE);
	    				mRootView.findViewById(R.id.not_found_text).setVisibility(View.VISIBLE);
	    				((TextView)mRootView.findViewById(R.id.not_found_text)).setText(dataModel.getMessage());
	    				
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
	
	OnClickListener onAcceptedClickListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			if(v.getTag() != null){
				int index = (Integer) v.getTag();
				Logger.e(TAG, "HIRE CANCELCLICK INDEX ::" + index);
				HandymanCustomerHireProfileFragment handymanCustomerHireProfileFragment = new HandymanCustomerHireProfileFragment();
				Bundle bundle = new Bundle();
				bundle.putSerializable(Utils.HANDYMAN_HIRE_CANCEL_DETAILS, mTempHiringslist.get(index));
				handymanCustomerHireProfileFragment.setArguments(bundle);
				getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, handymanCustomerHireProfileFragment).addToBackStack(TAG).commit();
			}
		}
	};
	
	
	private void setView(ArrayList<MyHiringsModel> hiringAllList) {
		if(isAdded()) {
			mTempHiringslist.addAll(hiringAllList);
			Logger.d(TAG, "SIZE of setview Cancel Handyman ::" + mTempHiringslist.size());
			if(hiringAllList.size() > 0) {
				
				if(handymanHiringAdapter == null){
					handymanHiringAdapter = new HandymanHiringAdapter(getActivity(), mTempHiringslist, onAcceptedClickListener);
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
	public void onScroll(AbsListView view, int firstVisibleItem,
			int visibleItemCount, int totalItemCount) {
		int lastInScreen = firstVisibleItem + visibleItemCount;
        if (lastInScreen == totalItemCount && totalItemCount != 0 && !isLoading && !isDataFinished) {
            
            if(mCancelHiringsList.size() >= 10){
            	Utils.flag_hiring = true;
            	isLoading = true;
                currentPage++;
                setProgressVisibility(View.VISIBLE);
    	        addFooter();
        		getHMPendingHirings(mSharedPreferences.getString(Utils.USER_ID, ""),"cancelled",String.valueOf(currentPage));
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
		        	
		        	mCancelHiringsList.clear();
		        	mTempHiringslist.clear();
		        	handymanHiringAdapter = null;
		        	currentPage = 1;
		    		isLoading = true;
		    		isDataFinished = false;
		    		 if(mCancelHiringsList.size() == 0){
		    			 Utils.flag_hiring = true;
		     			getHMPendingHirings(mSharedPreferences.getString(Utils.USER_ID, ""),"cancelled",String.valueOf(currentPage));
		     		}
		        }
		    }, 5000);
		}else{
			swipeRefreshLayout.setRefreshing(false);
		}
		
	}
	
}
