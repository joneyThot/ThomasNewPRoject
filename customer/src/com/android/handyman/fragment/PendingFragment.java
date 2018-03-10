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
import com.android.handyman.adapter.MyHiringAdapter;
import com.android.handyman.logger.Logger;
import com.android.handyman.model.DataModel;
import com.android.handyman.model.MyHiringsModel;
import com.android.handyman.service.AsyncCallListener;
import com.android.handyman.service.GetMyHiringsListRequestTask;
import com.android.handyman.service.Utils;

public class PendingFragment extends BaseFragment implements OnClickListener, AbsListView.OnScrollListener,SwipeRefreshLayout.OnRefreshListener{
	
	private static String TAG = "PendingFragment";
	private SharedPreferences mSharedPreferences;
	private ArrayList<MyHiringsModel> mPendingHiringsList = new ArrayList<MyHiringsModel>();
//	ArrayList<MyHiringsModel> mTempHiringslist = new ArrayList<MyHiringsModel>();
	ArrayList<MyHiringsModel> mTempCustomerList = new ArrayList<MyHiringsModel>();
	
	MyHiringAdapter myHiringAdapter = null ;
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
        
        if(mSharedPreferences.getString(Utils.CANCEL, "").equalsIgnoreCase("CANCEL")){
        	Logger.e(TAG, "CANCEL");
        	mPendingHiringsList.clear();
        	mTempCustomerList.clear();
        	myHiringAdapter = null;
        	currentPage = 1;
        	Utils.storeString(mSharedPreferences, Utils.CANCEL, "");
        	Utils.storeString(mSharedPreferences, Utils.PENDING_CANCEL, "PENDING_CANCEL");
        	
        }

        
        	 if(mTempCustomerList.size() > 0) {
        		Logger.d(TAG, "SAVE Pending Customer size :: " + mTempCustomerList.size());
     			myHiringAdapter = new MyHiringAdapter(getActivity(), mTempCustomerList,onPendingClickListener);
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
        
        /**
         * Showing Swipe Refresh animation on activity create
         * As animation won't start on onCreate, post runnable is used
         */
//		swipeRefreshLayout.post(new Runnable() {
//			@Override
//			public void run() {
//				swipeRefreshLayout.setRefreshing(true);
//
//				if (mPendingHiringsList.size() == 0) {
//					getPendingHirings(mSharedPreferences.getString(Utils.USER_ID, ""),"pending", String.valueOf(currentPage));
//				}
//			}
//		});
        
		if (mPendingHiringsList.size() == 0) {
			getPendingHirings(mSharedPreferences.getString(Utils.USER_ID, ""),"pending", String.valueOf(currentPage));
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
	
	private void getPendingHirings(String id, String status,String page) {
//		if(Utils.checkInternetConnection(getActivity())){
			GetMyHiringsListRequestTask myHiringsListRequestTask = new GetMyHiringsListRequestTask(getActivity());
			myHiringsListRequestTask.setAsyncCallListener(new AsyncCallListener() {

				@SuppressWarnings("unchecked")
				@Override
				public void onResponseReceived(Object response) {
					mPendingHiringsList.clear();
					DataModel dataModel = (DataModel) response;
					if(dataModel.getSuccess().equalsIgnoreCase("1")){
//						mPendingHiringsList.addAll((ArrayList<MyHiringsModel>) response);
						mPendingHiringsList.addAll(dataModel.getMyHiringsModels());
						Logger.e(TAG, "mPendingHiringsList SIZE -- " + mPendingHiringsList.size());
						if (mPendingHiringsList.size() > 0) {
							
							myhiringlist.setVisibility(View.VISIBLE);
							mRootView.findViewById(R.id.not_found_text).setVisibility(View.GONE);
							bar.setVisibility(View.GONE);
							
							setViewCustomer(mPendingHiringsList);
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
	
	
	OnClickListener onPendingClickListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			if(v.getTag() != null){
				int index = (Integer) v.getTag();
				Logger.e(TAG, "HIRE PENDING CLICK INDEX ::" + index);
				HandymanCustomerHireProfileFragment handymanCustomerHireProfileFragment = new HandymanCustomerHireProfileFragment();
				Bundle bundle = new Bundle();
				bundle.putSerializable(Utils.HANDYMAN_HIRE_PENDING_DETAILS, mTempCustomerList.get(index));
				handymanCustomerHireProfileFragment.setArguments(bundle);
				getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, handymanCustomerHireProfileFragment).addToBackStack(TAG).commit();
			}
		} 
	};

	private void setViewCustomer(ArrayList<MyHiringsModel> hiringAllList) {
		if(isAdded()) {
			mTempCustomerList.addAll(hiringAllList);
			Logger.d(TAG, "SIZE of setview Pending Customer ::" + mTempCustomerList.size());
			if(hiringAllList.size() > 0) {
				if(myHiringAdapter == null){
					myHiringAdapter = new MyHiringAdapter(getActivity(), mTempCustomerList,onPendingClickListener);
					myhiringlist.setAdapter(myHiringAdapter);
					myHiringAdapter.notifyDataSetChanged();
				} else {
					myHiringAdapter.setList(mTempCustomerList);
					myHiringAdapter.notifyDataSetChanged();
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
            	 isLoading = true;
                 currentPage++;
                 setProgressVisibility(View.VISIBLE);
     	        addFooter();
            	
            	getPendingHirings(mSharedPreferences.getString(Utils.USER_ID, ""),"pending",String.valueOf(currentPage));
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
	        	mTempCustomerList.clear();
	        	myHiringAdapter = null;
	        	currentPage = 1;
	    		isLoading = true;
	    		isDataFinished = false;
	    		
	    		if (mPendingHiringsList.size() == 0) {
	    			getPendingHirings(mSharedPreferences.getString(Utils.USER_ID, ""),"pending", String.valueOf(currentPage));
	    		}
	    		
	        }
	    }, 5000);
		} else {
			swipeRefreshLayout.setRefreshing(false);
		}
		
		
	}

	
	
}
