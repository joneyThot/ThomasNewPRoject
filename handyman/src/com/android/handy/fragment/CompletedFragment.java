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
import com.android.handy.service.GetMyHiringsListRequestTask;
import com.android.handy.service.Utils;

public class CompletedFragment extends BaseFragment implements OnClickListener, AbsListView.OnScrollListener,SwipeRefreshLayout.OnRefreshListener{
	
	private static String TAG = "CompletedFragment";
	private SharedPreferences mSharedPreferences;
	private ArrayList<MyHiringsModel> mCompleteHiringsList = new ArrayList<MyHiringsModel>();
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
		CompletedFragment fragmentFirst = new CompletedFragment();
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
        
        if(mSharedPreferences.getString(Utils.START_COMPELETE, "").equalsIgnoreCase("START_COMPELETE")){
        	Logger.e(TAG, "PENDING_COMPELETE");
        	mCompleteHiringsList.clear();
        	mTempHiringslist.clear();
        	handymanHiringAdapter = null;
        	currentPage = 1;
        	Utils.storeString(mSharedPreferences, Utils.START_COMPELETE, "");
        	Utils.storeString(mSharedPreferences, Utils.START_COMPELETE_ALL, "START_COMPELETE_ALL");
        	
        } 
        
        /*else if(mSharedPreferences.getString(Utils.ACCEPT_COMPELETE, "").equalsIgnoreCase("ACCEPT_COMPELETE")){
        	Logger.e(TAG, "ACCEPT_COMPELETE");
        	mCompleteHiringsList.clear();
        	mTempHiringslist.clear();
        	handymanHiringAdapter = null;
        	currentPage = 1;
        	Utils.storeString(mSharedPreferences, Utils.ACCEPT_COMPELETE, "");
        	Utils.storeString(mSharedPreferences, Utils.ACCEPT_COMPELETE_ALL, "ACCEPT_COMPELETE_ALL");
        	
        }*/ 
        
        
	        if(mTempHiringslist.size() > 0){
	        	Logger.d(TAG, "SAVE complete Handyman size :: " + mTempHiringslist.size());
	        	handymanHiringAdapter = new HandymanHiringAdapter(getActivity(), mTempHiringslist, onAcceptedClickListener);
				myhiringlist.setAdapter(handymanHiringAdapter);
				handymanHiringAdapter.notifyDataSetChanged();
	    	} else {
	    		if(flag == false){
	    			bar.setVisibility(View.VISIBLE);
					 flag = true;
				 } else {
					 mTempHiringslist.clear();
					 getHMPendingHirings(mSharedPreferences.getString(Utils.USER_ID, ""), "completed", String.valueOf(currentPage));
				 }
	    	}
        
        if (progressBar != null) {
             if (progressBar.getVisibility() == View.VISIBLE) {
                 setProgressVisibility(View.GONE);
                 removeFooter();
              }
        }
        

		 if(mCompleteHiringsList.size() == 0){
			 if(flag == false){
				 Utils.flag_hiring = false;
				 flag = true;
			 } else {
				 Utils.flag_hiring = true;
			 }
			 
			 
   			getHMPendingHirings(mSharedPreferences.getString(Utils.USER_ID, ""),"completed",String.valueOf(currentPage));
   		}
        
//        swipeRefreshLayout.post(new Runnable() {
//			@Override
//			public void run() {
//				swipeRefreshLayout.setRefreshing(true);
//
//				 if(mCompleteHiringsList.size() == 0){
//		    			getHMPendingHirings(mSharedPreferences.getString(Utils.USER_ID, ""),"completed",String.valueOf(currentPage));
//		    		}
//			}
//		});
        
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
					mCompleteHiringsList.clear();
					DataModel dataModel = (DataModel) response;
					if(dataModel.getSuccess().equalsIgnoreCase("1")){
//						mCompleteHiringsList = (ArrayList<MyHiringsModel>) response;
						mCompleteHiringsList.addAll(dataModel.getMyOrderList());
						Logger.e(TAG, "mCompleteHiringsList SIZE -- " + mCompleteHiringsList.size());
						if (mCompleteHiringsList.size() > 0) {
							/*for (int i = 0; i < mCompleteHiringsList.size(); i++) {
								myhiringlist.setVisibility(View.VISIBLE);
								mRootView.findViewById(R.id.not_found_text).setVisibility(View.GONE);
								handymanHiringAdapter = new HandymanHiringAdapter(getActivity(), mCompleteHiringsList, onAcceptedClickListener);
								myhiringlist.setAdapter(handymanHiringAdapter);
								handymanHiringAdapter.notifyDataSetChanged();
							}*/
							myhiringlist.setVisibility(View.VISIBLE);
							mRootView.findViewById(R.id.not_found_text).setVisibility(View.GONE);
							bar.setVisibility(View.GONE);
							
							setView(mCompleteHiringsList);
							isLoading = false;
		                    setProgressVisibility(View.GONE);
		                    removeFooter();
							
						} /*else {
//							
						}*/
					} else if(dataModel.getSuccess().equalsIgnoreCase("0")){
						myhiringlist.setVisibility(View.GONE);
						mRootView.findViewById(R.id.not_found_text).setVisibility(View.VISIBLE);
						bar.setVisibility(View.GONE);
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
				Logger.e(TAG, "HIRE COMPELETE CLICK INDEX ::" + index);
				HandymanCustomerHireProfileFragment handymanCustomerHireProfileFragment = new HandymanCustomerHireProfileFragment();
				Bundle bundle = new Bundle();
				bundle.putSerializable(Utils.HANDYMAN_HIRE_COMPLETE_DETAILS, mTempHiringslist.get(index));
				handymanCustomerHireProfileFragment.setArguments(bundle);
				getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, handymanCustomerHireProfileFragment).addToBackStack(TAG).commit();
			}
		}
	};
	
	private void setView(ArrayList<MyHiringsModel> hiringAllList) {
		if(isAdded()) {
			mTempHiringslist.addAll(hiringAllList);
			Logger.d(TAG, "SIZE of setview complete Handyman ::" + mTempHiringslist.size());
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
	public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
		int lastInScreen = firstVisibleItem + visibleItemCount;
        if (lastInScreen == totalItemCount && totalItemCount != 0 && !isLoading && !isDataFinished) {
           
            if(mCompleteHiringsList.size() >= 10){
            	Utils.flag_hiring = true;
            	 isLoading = true;
                 currentPage++;
                 setProgressVisibility(View.VISIBLE);
     	        addFooter();
        		getHMPendingHirings(mSharedPreferences.getString(Utils.USER_ID, ""),"completed",String.valueOf(currentPage));
        	}
        } 
	}


	@Override
	public void onRefresh() {
		if(mTempHiringslist.isEmpty() == false){
			swipeRefreshLayout.setRefreshing(true);
			
			new Handler().postDelayed(new Runnable() {
		        @Override
		        public void run() {
		        	mCompleteHiringsList.clear();
		        	mTempHiringslist.clear();
		        	handymanHiringAdapter = null;
		        	currentPage = 1;
		    		isLoading = true;
		    		isDataFinished = false;
		    		 if(mCompleteHiringsList.size() == 0){
		    			 Utils.flag_hiring = true;
		     			getHMPendingHirings(mSharedPreferences.getString(Utils.USER_ID, ""),"completed",String.valueOf(currentPage));
		     		}
		        }
		    }, 5000);
		} else {
			swipeRefreshLayout.setRefreshing(false);
		}
		
	}
	
}
