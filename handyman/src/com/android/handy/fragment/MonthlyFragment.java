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
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.handy.MainActivity;
import com.android.handy.R;
import com.android.handy.adapter.HandymanCollectionAdapter;
import com.android.handy.logger.Logger;
import com.android.handy.model.MyCollectionModel;
import com.android.handy.model.MyCollectionModelList;
import com.android.handy.service.AsyncCallListener;
import com.android.handy.service.MyCollectionRequestTask;
import com.android.handy.service.Utils;

public class MonthlyFragment extends BaseFragment implements OnClickListener, AbsListView.OnScrollListener,SwipeRefreshLayout.OnRefreshListener{
	
	private static String TAG = "MonthFragment";
	private SharedPreferences mSharedPreferences;
	
	private String title;
    private int page;
    
    ArrayList<MyCollectionModel> collectionModelsList = new ArrayList<MyCollectionModel>();
    ArrayList<MyCollectionModel> mTempCollectionlist = new ArrayList<MyCollectionModel>();
    HandymanCollectionAdapter handymanCollectionAdapter = null;

    ListView mCollactionListView;
    Fragment fr;
	View mRootView,footerView = null;
	
    private Context context;
    SwipeRefreshLayout swipeRefreshLayout;
    
    int currentPage = 1;
    boolean isLoading = false,isDataFinished = false;
    private ProgressBar progressBar = null;
    boolean flag = false;
    ProgressBar bar = null;
	
	public static Fragment newInstance(int page , String title) {
		MonthlyFragment fragmentFirst = new MonthlyFragment();
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
		mRootView = inflater.inflate(R.layout.row_my_collection_list, container, false);
		initview();
		return mRootView;
	}

	private void initview() {
		mSharedPreferences = getActivity().getSharedPreferences(Utils.PREF_NAME, Context.MODE_PRIVATE); 
		
		((LinearLayout) mRootView.findViewById(R.id.from_to_layout)).setVisibility(View.GONE);
		mCollactionListView = (ListView) mRootView.findViewById(R.id.collaction_list);
		swipeRefreshLayout = (SwipeRefreshLayout) mRootView.findViewById(R.id.swipe_refresh_hiring_layout);
		footerView = ((MainActivity) context).getLayoutInflater().inflate(R.layout.loadmore, null);
        progressBar = (ProgressBar) footerView.findViewById(R.id.progress_bar);
        bar = (ProgressBar) mRootView.findViewById(R.id.prog);
        
        mCollactionListView.setOnScrollListener(this);
        mCollactionListView.addFooterView(footerView);
        swipeRefreshLayout.setOnRefreshListener(this);
        
        swipeRefreshLayout.setColorSchemeColors(Color.RED, Color.GREEN, Color.BLUE, Color.CYAN);
        
		if(mTempCollectionlist.size() > 0){
        	Logger.d(TAG, "SAVE Monthly Collection size :: " + mTempCollectionlist.size());
        	handymanCollectionAdapter = new HandymanCollectionAdapter(getActivity(), mTempCollectionlist,onAcceptedClickListener);
			mCollactionListView.setAdapter(handymanCollectionAdapter);
			handymanCollectionAdapter.notifyDataSetChanged();
			
			if((!mSharedPreferences.getString("month_total", "").isEmpty())){
				mRootView.findViewById(R.id.total).setVisibility(View.VISIBLE);
				((TextView)mRootView.findViewById(R.id.total)).setText("TOTAL : " + mSharedPreferences.getString("month_total", ""));
			}
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
		
		if(collectionModelsList.size() == 0){
			getMyCollection(mSharedPreferences.getString(Utils.USER_ID, ""),"month", String.valueOf(currentPage));
		}
		
	}
	
	private void setProgressVisibility(int visibility) {
        progressBar.setVisibility(visibility);
    }
 
	 protected void removeFooter() {
		 mCollactionListView.removeFooterView(footerView);
	    }
	 
	 protected void addFooter() {
	        if (mCollactionListView.getFooterViewsCount() == 0)
	        	mCollactionListView.addFooterView(footerView);
	    }
	

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		}
	}
	
	private void getMyCollection(String handyman_id, String mode, String page) {
//		if(Utils.checkInternetConnection(getActivity())){
			MyCollectionRequestTask myCollectionRequestTask = new MyCollectionRequestTask(getActivity());
			myCollectionRequestTask.setAsyncCallListener(new AsyncCallListener() {

				@SuppressWarnings("unchecked")
				@Override
				public void onResponseReceived(Object response) {
					collectionModelsList.clear();
					MyCollectionModelList myCollectionModel = (MyCollectionModelList) response;
					if(myCollectionModel.getSuccess().equalsIgnoreCase("1")){
						
						Utils.storeString(mSharedPreferences, "month_total", myCollectionModel.getTotalamount());
						
						collectionModelsList.addAll(myCollectionModel.getCollectionModelsList());
						Logger.e(TAG, "collectionModelsList SIZE -- " + collectionModelsList.size());
						
							if(collectionModelsList.size() > 0){
								mCollactionListView.setVisibility(View.VISIBLE);
								mRootView.findViewById(R.id.not_found_text).setVisibility(View.GONE);
								bar.setVisibility(View.GONE);
								
								setView(collectionModelsList);
								isLoading = false;
			                    setProgressVisibility(View.GONE);
			                    removeFooter();
							}
						
					}else if(myCollectionModel.getSuccess().equalsIgnoreCase("0")){
						
						mCollactionListView.setVisibility(View.GONE);
						mRootView.findViewById(R.id.not_found_text).setVisibility(View.VISIBLE);
						bar.setVisibility(View.GONE);
						((TextView)mRootView.findViewById(R.id.not_found_text)).setText(myCollectionModel.getMessage());
						
						isDataFinished = true;
						new Handler().postDelayed(new Runnable() {

			    			@Override
			    			public void run() {
			    				 setProgressVisibility(View.GONE);
			    		         removeFooter();
			    			}
			    		}, 2000);
					}
					swipeRefreshLayout.setRefreshing(false);
				}
				@Override
				public void onErrorReceived(String error) {
					Toast.makeText(getActivity(), getString(R.string.try_again), Toast.LENGTH_SHORT).show();
					swipeRefreshLayout.setRefreshing(false);
				}
			});
			myCollectionRequestTask.execute(handyman_id,mode,page);
//		}else{
//			Utils.showMessageDialog(getActivity(), getResources().getString(R.string.alert), 
//					getResources().getString(R.string.connection));
//		}
	}
	
	private void setView(ArrayList<MyCollectionModel> hiringAllList) {
		if(isAdded()) {
			mTempCollectionlist.addAll(hiringAllList);
			Logger.d(TAG, "SIZE of setview Monthly Collection ::" + mTempCollectionlist.size());
			if(hiringAllList.size() > 0) {
				
				if(handymanCollectionAdapter == null){
					handymanCollectionAdapter = new HandymanCollectionAdapter(getActivity(), mTempCollectionlist,onAcceptedClickListener);
					mCollactionListView.setAdapter(handymanCollectionAdapter);
					handymanCollectionAdapter.notifyDataSetChanged();
				} else {
					handymanCollectionAdapter.setList(mTempCollectionlist);
					handymanCollectionAdapter.notifyDataSetChanged();
				}
				
				if((!mSharedPreferences.getString("month_total", "").isEmpty())){
					mRootView.findViewById(R.id.total).setVisibility(View.VISIBLE);
					((TextView)mRootView.findViewById(R.id.total)).setText("TOTAL : " + mSharedPreferences.getString("month_total", ""));
				}
			}
		}
	}
	
	OnClickListener onAcceptedClickListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			if(v.getTag() != null){
				int index = (Integer) v.getTag();
				Logger.e(TAG, "COLLECTION MONTH CLICK INDEX ::" + index);
				if(Utils.validateString(mTempCollectionlist.get(index).getDateMain())){
					
				} else {
					MyColleactionDetailsFragment myColleactionDetailsFragment = new MyColleactionDetailsFragment();
					Bundle bundle = new Bundle();
					bundle.putSerializable(Utils.MY_COLLECTION_WEEK_DETAILS, mTempCollectionlist.get(index));
					myColleactionDetailsFragment.setArguments(bundle);
					getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, myColleactionDetailsFragment).addToBackStack(TAG).commit();	
				}
			}
		}
	};
	
	
	@Override
	public void onScrollStateChanged(AbsListView view, int scrollState) {
		
	}


	@Override
	public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
		int lastInScreen = firstVisibleItem + visibleItemCount;
        if (lastInScreen == totalItemCount && totalItemCount != 0 && !isLoading && !isDataFinished) {
            
            if(collectionModelsList.size() >= 20){
            	isLoading = true;
                currentPage++;
                setProgressVisibility(View.VISIBLE);
    	        addFooter();
            	getMyCollection(mSharedPreferences.getString(Utils.USER_ID, ""),"month",String.valueOf(currentPage));
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
		        	collectionModelsList.clear();
		        	mTempCollectionlist.clear();
		        	handymanCollectionAdapter = null;
		    		currentPage = 1;
		    		isLoading = true;
		    		isDataFinished = false;
		    		
		    		if(collectionModelsList.size() == 0){
		   			 getMyCollection(mSharedPreferences.getString(Utils.USER_ID, ""),"month",String.valueOf(currentPage));
		   		 }
		        }
		    }, 5000);
		} else {
			swipeRefreshLayout.setRefreshing(false);
		}
		
	}
	
}
