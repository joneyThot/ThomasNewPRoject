package com.android.handyman.fragment;

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

import com.android.handyman.MainActivity;
import com.android.handyman.R;
import com.android.handyman.SplashActivity;
import com.android.handyman.adapter.HandymanHiringAdapter;
import com.android.handyman.adapter.MyHiringAdapter;
import com.android.handyman.logger.Logger;
import com.android.handyman.model.DataModel;
import com.android.handyman.model.MyHiringsModel;
import com.android.handyman.service.AsyncCallListener;
import com.android.handyman.service.GetHandymanHiringsListRequestTask;
import com.android.handyman.service.GetMyHiringsListRequestTask;
import com.android.handyman.service.Utils;

public class CancelledFragment extends BaseFragment implements OnClickListener, AbsListView.OnScrollListener,SwipeRefreshLayout.OnRefreshListener{
	
	private static String TAG = "CancelledFragment";
	private SharedPreferences mSharedPreferences;
	private ArrayList<MyHiringsModel> mCancelHiringsList = new ArrayList<MyHiringsModel>();
//	ArrayList<MyHiringsModel> mTempHiringslist = new ArrayList<MyHiringsModel>();
	ArrayList<MyHiringsModel> mTempCustomerList = new ArrayList<MyHiringsModel>();
//	HandymanHiringAdapter handymanHiringAdapter = null;
	MyHiringAdapter myHiringAdapter = null;
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
        	mTempCustomerList.clear();
        	myHiringAdapter = null;
        	currentPage = 1;
        	Utils.storeString(mSharedPreferences, Utils.PENDING_CANCEL, "");
        	Utils.storeString(mSharedPreferences, Utils.PENDING_CANCEL_ALL, "PENDING_CANCEL_ALL");
        	
        } else if(mSharedPreferences.getString(Utils.ACCEPT_CANCEL, "").equalsIgnoreCase("ACCEPT_CANCEL")){
        	Logger.e(TAG, "ACCEPT_CANCEL");
        	mCancelHiringsList.clear();
        	mTempCustomerList.clear();
        	myHiringAdapter = null;
        	currentPage = 1;
        	Utils.storeString(mSharedPreferences, Utils.ACCEPT_CANCEL, "");
        	Utils.storeString(mSharedPreferences, Utils.ACCEPT_CANCEL_ALL, "ACCEPT_CANCEL_ALL");
        	
        } else if(mSharedPreferences.getString(Utils.START_CANCEL, "").equalsIgnoreCase("START_CANCEL")){
        	Logger.e(TAG, "START_CANCEL");
        	mCancelHiringsList.clear();
        	mTempCustomerList.clear();
        	myHiringAdapter = null;
        	currentPage = 1;
        	Utils.storeString(mSharedPreferences, Utils.START_CANCEL, "");
        	Utils.storeString(mSharedPreferences, Utils.START_CANCEL_ALL, "START_CANCEL_ALL");
        } 
        
        
       	 if(mTempCustomerList.size() > 0) {
       		Logger.d(TAG, "SAVE Pending Customer size :: " + mTempCustomerList.size());
    			myHiringAdapter = new MyHiringAdapter(getActivity(), mTempCustomerList,onAcceptedClickListener);
    			myhiringlist.setAdapter(myHiringAdapter);
    			myHiringAdapter.notifyDataSetChanged();
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
//				if(mCancelHiringsList.size() == 0){
//		    			getCancelHirings(mSharedPreferences.getString(Utils.USER_ID, ""),"cancelled",String.valueOf(currentPage));
//		        }
//			}
//		});
        

		if(mCancelHiringsList.size() == 0){
    			getCancelHirings(mSharedPreferences.getString(Utils.USER_ID, ""),"cancelled",String.valueOf(currentPage));
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
	
	private void getCancelHirings(String id, String status,String page) {
//		if(Utils.checkInternetConnection(getActivity())){
			GetMyHiringsListRequestTask myHiringsListRequestTask = new GetMyHiringsListRequestTask(getActivity());
			myHiringsListRequestTask.setAsyncCallListener(new AsyncCallListener() {

				@SuppressWarnings("unchecked")
				@Override
				public void onResponseReceived(Object response) {
					mCancelHiringsList.clear();
					DataModel dataModel = (DataModel) response;
					if(dataModel.getSuccess().equalsIgnoreCase("1")){
//						mCancelHiringsList = (ArrayList<MyHiringsModel>) response;
						mCancelHiringsList.addAll(dataModel.getMyHiringsModels());
						Logger.e(TAG, "mCancelHiringsList SIZE -- " + mCancelHiringsList.size());
						if (mCancelHiringsList.size() > 0) {
							myhiringlist.setVisibility(View.VISIBLE);
							mRootView.findViewById(R.id.not_found_text).setVisibility(View.GONE);
							bar.setVisibility(View.GONE);
							
							setViewCustomer(mCancelHiringsList);
							isLoading = false;
		                    setProgressVisibility(View.GONE);
		                    removeFooter();
							
						} /*else {
							
						}*/
					} else if(dataModel.getSuccess().equalsIgnoreCase("0")){
						myhiringlist.setVisibility(View.GONE);
						bar.setVisibility(View.GONE);
	    				mRootView.findViewById(R.id.not_found_text).setVisibility(View.VISIBLE);
	    				((TextView)mRootView.findViewById(R.id.not_found_text)).setText(dataModel.getMessage());
						
	    				isDataFinished = true;
						if(mTempCustomerList.isEmpty() == true){
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
			myHiringsListRequestTask.execute(id,status,page);
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
				bundle.putSerializable(Utils.HANDYMAN_HIRE_CANCEL_DETAILS, mTempCustomerList.get(index));
				handymanCustomerHireProfileFragment.setArguments(bundle);
				getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, handymanCustomerHireProfileFragment).addToBackStack(TAG).commit();
			}
		}
	};
	
	private void setViewCustomer(ArrayList<MyHiringsModel> hiringAllList) {
		if(isAdded()) {
			mTempCustomerList.addAll(hiringAllList);
			Logger.d(TAG, "SIZE of setview Cancel Customer ::" + mTempCustomerList.size());
			if(hiringAllList.size() > 0) {
				
				if(myHiringAdapter == null){
					myHiringAdapter = new MyHiringAdapter(getActivity(), mTempCustomerList,onAcceptedClickListener);
					myhiringlist.setAdapter(myHiringAdapter);
					myHiringAdapter.notifyDataSetChanged();
				} else {
					myHiringAdapter.setList(mTempCustomerList);
					myHiringAdapter.notifyDataSetChanged();
				}
			}
		}
	}
	
//	private void setView(ArrayList<MyHiringsModel> hiringAllList) {
//		if(isAdded()) {
//			mTempHiringslist.addAll(hiringAllList);
//			Logger.d(TAG, "SIZE of setview Cancel Handyman ::" + mTempHiringslist.size());
//			if(hiringAllList.size() > 0) {
//				
//				if(handymanHiringAdapter == null){
//					handymanHiringAdapter = new HandymanHiringAdapter(getActivity(), mTempHiringslist, onAcceptedClickListener);
//					myhiringlist.setAdapter(handymanHiringAdapter);
//					handymanHiringAdapter.notifyDataSetChanged();
//				} else {
//					handymanHiringAdapter.setList(mTempHiringslist);
//					handymanHiringAdapter.notifyDataSetChanged();
//				}
//			}
//		}
//	}

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
            	isLoading = true;
                currentPage++;
                setProgressVisibility(View.VISIBLE);
    	        addFooter();
        			getCancelHirings(mSharedPreferences.getString(Utils.USER_ID, ""),"cancelled",String.valueOf(currentPage));
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
		        	mTempCustomerList.clear();
		        	myHiringAdapter = null;
		        	currentPage = 1;
		    		isLoading = true;
		    		isDataFinished = false;
		    		 if(mCancelHiringsList.size() == 0){
		     			getCancelHirings(mSharedPreferences.getString(Utils.USER_ID, ""),"cancelled",String.valueOf(currentPage));
		          }
		        }
		    }, 5000);
		} else {
			swipeRefreshLayout.setRefreshing(false);
		}
		
		
	}
	
}