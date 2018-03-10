package com.android.handy.fragment;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.DatePicker;
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
import com.android.handy.service.MyCollectionCustomRequestTask;
import com.android.handy.service.Utils;

public class CustomlyFragment extends BaseFragment implements OnClickListener, AbsListView.OnScrollListener{
	
	private static String TAG = "CustomlyFragment";
	private SharedPreferences mSharedPreferences;
	
	private String title;
    private int page;
    
    ArrayList<MyCollectionModel> collectionModelsList = new ArrayList<MyCollectionModel>();
    ArrayList<MyCollectionModel> mTempCollectionlist = new ArrayList<MyCollectionModel>();
    HandymanCollectionAdapter handymanCollectionAdapter = null;

    ListView mCollactionListView;
    Fragment fr;
	View mRootView,footerView = null;
	
	TextView mFromDateView,mToDateView;
    private Context context;
    SimpleDateFormat dfDate;
    
    int currentPage = 1;
    boolean isLoading = false,isDataFinished = false;
    private ProgressBar progressBar = null;
    boolean flag = false;
	
	public static Fragment newInstance(int page , String title) {
		CustomlyFragment fragmentFirst = new CustomlyFragment();
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

	@SuppressLint("SimpleDateFormat") private void initview() {
		mSharedPreferences = getActivity().getSharedPreferences(Utils.PREF_NAME, Context.MODE_PRIVATE); 
		
		dfDate  = new SimpleDateFormat("dd/MM/yyyy");
		
		((LinearLayout) mRootView.findViewById(R.id.from_to_layout)).setVisibility(View.VISIBLE);
		mFromDateView = (TextView) mRootView.findViewById(R.id.from_date);
		mToDateView  = (TextView) mRootView.findViewById(R.id.to_date);
		
		mFromDateView.setOnClickListener(this);
		mToDateView.setOnClickListener(this);
		mCollactionListView = (ListView) mRootView.findViewById(R.id.collaction_list);
		
		footerView = ((MainActivity) context).getLayoutInflater().inflate(R.layout.loadmore, null);
        progressBar = (ProgressBar) footerView.findViewById(R.id.progress_bar);
        
        mCollactionListView.setOnScrollListener(this);
        mCollactionListView.addFooterView(footerView);
		
		 if(mTempCollectionlist.size() > 0){
	        	Logger.d(TAG, "SAVE Custom Collection size :: " + mTempCollectionlist.size());
	        	handymanCollectionAdapter = new HandymanCollectionAdapter(getActivity(), mTempCollectionlist,onAcceptedClickListener);
				mCollactionListView.setAdapter(handymanCollectionAdapter);
				handymanCollectionAdapter.notifyDataSetChanged();
				
				if((!mSharedPreferences.getString("custom_total", "").isEmpty())){
					mRootView.findViewById(R.id.total).setVisibility(View.VISIBLE);
					((TextView)mRootView.findViewById(R.id.total)).setText("TOTAL : " + mSharedPreferences.getString("custom_total", ""));
				}
				
	    	} else {
	    		if(flag == true){
	    			mRootView.findViewById(R.id.not_found_text).setVisibility(View.VISIBLE);
					((TextView)mRootView.findViewById(R.id.not_found_text)).setText("no collections available.");
	    		}
	    	}
		 
		if((!mSharedPreferences.getString(Utils.FROM_DATE, "").isEmpty()) && (!mSharedPreferences.getString(Utils.TO_DATE, "").isEmpty())){
				mFromDateView.setText(mSharedPreferences.getString(Utils.FROM_DATE, ""));
				mToDateView.setText(mSharedPreferences.getString(Utils.TO_DATE, ""));
			} else {
				mTempCollectionlist.clear();
				collectionModelsList.clear();
			}
		
		 if (progressBar != null) {
	            if (progressBar.getVisibility() == View.VISIBLE) {
	                setProgressVisibility(View.GONE);
	                removeFooter();
	             }
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
		
		case R.id.from_date:
			
			DialogFragment fromDateFragment = new SelectFromDateFragment();
			fromDateFragment.show(getFragmentManager(), "From DatePicker");
			break;
			
		case R.id.to_date:
			
			if(!mFromDateView.getText().toString().equalsIgnoreCase("") && !mFromDateView.getText().toString().equalsIgnoreCase(null)){
				DialogFragment toDateFragment = new SelectToDateFragment();
				toDateFragment.show(getFragmentManager(), "To DatePicker");	
			} else {
				Utils.showMessageDialog(getActivity(), getResources().getString(R.string.alert), "From date should not be blank");
			}
			
			break;
		}
	}
	
	
	private void getMyCollection(String handyman_id, String mode, String start_date, String end_date,String page) {
		if(Utils.checkInternetConnection(getActivity())){
			MyCollectionCustomRequestTask myCollectionCustomRequestTask = new MyCollectionCustomRequestTask(getActivity());
			myCollectionCustomRequestTask.setAsyncCallListener(new AsyncCallListener() {

				@SuppressWarnings("unchecked")
				@Override
				public void onResponseReceived(Object response) {
					collectionModelsList.clear();
					MyCollectionModelList myCollectionModel = (MyCollectionModelList) response;
					if(myCollectionModel.getSuccess().equalsIgnoreCase("1")){
						
						Utils.storeString(mSharedPreferences, "custom_total", myCollectionModel.getTotalamount());
						
						collectionModelsList.addAll(myCollectionModel.getCollectionModelsList());
						Logger.e(TAG, "collectionModelsList SIZE -- " + collectionModelsList.size());
						
							if(collectionModelsList.size() > 0){
								mCollactionListView.setVisibility(View.VISIBLE);
								mRootView.findViewById(R.id.not_found_text).setVisibility(View.GONE);
								
								setView(collectionModelsList);
								isLoading = false;
			                    setProgressVisibility(View.GONE);
			                    removeFooter();
							}
						
						
					}else if(myCollectionModel.getSuccess().equalsIgnoreCase("0")){
						flag = true;
						mCollactionListView.setVisibility(View.GONE);
						mRootView.findViewById(R.id.not_found_text).setVisibility(View.VISIBLE);
						((TextView)mRootView.findViewById(R.id.not_found_text)).setText(myCollectionModel.getMessage());
						
						if(mTempCollectionlist.isEmpty() == true){
							Utils.storeString(mSharedPreferences, "custom_total", "");
							mRootView.findViewById(R.id.total).setVisibility(View.GONE);
//							mCollactionListView.setAdapter(null);
						}
						
						isDataFinished = true;
						new Handler().postDelayed(new Runnable() {

			    			@Override
			    			public void run() {
			    				 setProgressVisibility(View.GONE);
			    		         removeFooter();
			    			}
			    		}, 2000);
						
					}
					
				}
				@Override
				public void onErrorReceived(String error) {
					Toast.makeText(getActivity(), getString(R.string.try_again), Toast.LENGTH_SHORT).show();
				}
			});
			myCollectionCustomRequestTask.execute(handyman_id,mode, start_date, end_date, page);
		}else{
			Utils.showMessageDialog(getActivity(), getResources().getString(R.string.alert), 
					getResources().getString(R.string.connection));
		}
	}
	
	private void setView(ArrayList<MyCollectionModel> hiringAllList) {
		if(isAdded()) {
//			mTempCollectionlist.clear();
			mTempCollectionlist.addAll(hiringAllList);
			Logger.d(TAG, "SIZE of setview Day Collection ::" + mTempCollectionlist.size());
			if(hiringAllList.size() > 0) {
				
				if(handymanCollectionAdapter == null){
					handymanCollectionAdapter = new HandymanCollectionAdapter(getActivity(), mTempCollectionlist,onAcceptedClickListener);
					mCollactionListView.setAdapter(handymanCollectionAdapter);
					handymanCollectionAdapter.notifyDataSetChanged();
				} else {
					handymanCollectionAdapter.setList(mTempCollectionlist);
					handymanCollectionAdapter.notifyDataSetChanged();
				}
				
				if((!mSharedPreferences.getString("custom_total", "").isEmpty())){
					mRootView.findViewById(R.id.total).setVisibility(View.VISIBLE);
					((TextView)mRootView.findViewById(R.id.total)).setText("TOTAL : " + mSharedPreferences.getString("custom_total", ""));
				}
			}
		}
	}
	
	OnClickListener onAcceptedClickListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			if(v.getTag() != null){
				int index = (Integer) v.getTag();
				Logger.e(TAG, "COLLECTION CUSTOM CLICK INDEX ::" + index);
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
            isLoading = true;
            currentPage++;
            setProgressVisibility(View.VISIBLE);
	        addFooter();
            if(collectionModelsList.size() > 0 && collectionModelsList.size() <= 20){
            	getMyCollection(mSharedPreferences.getString(Utils.USER_ID, ""),"custom",mSharedPreferences.getString(Utils.FROM_DATE, ""),mSharedPreferences.getString(Utils.TO_DATE, ""),String.valueOf(currentPage));
        	} 

        } 	
	}
	
	public class SelectFromDateFragment extends DialogFragment implements DatePickerDialog.OnDateSetListener {

		@Override
		public Dialog onCreateDialog(Bundle savedInstanceState) {
			final Calendar c = Calendar.getInstance();
//		    c.add(Calendar.YEAR, -18);
		   
			int year = 0,month = 0,day = 0;
		    if(mFromDateView.getText().toString().trim().equalsIgnoreCase("")){
		    	 year = c.get(Calendar.YEAR);
				 month = c.get(Calendar.MONTH);
				 day = c.get(Calendar.DAY_OF_MONTH);
		    } else {
		    	String d = mFromDateView.getText().toString();
		    	year = Integer.parseInt(d.substring(6, 10)); 
		    	month = Integer.parseInt(d.substring(3, 5)) - 1; 
		    	day = Integer.parseInt(d.substring(0, 2));
		    }
		    
		    long maxDate = c.getTime().getTime();
			
			DatePickerDialog dpd = new DatePickerDialog(getActivity(), this,year,month,day);
	        dpd.getDatePicker().setMaxDate(maxDate);
            
	        return  dpd;
		}

		public void onDateSet(DatePicker view, int yy, int mm, int dd) {
			String day = String.valueOf(dd);
			String month = String.valueOf(mm + 1);
			if(day.trim().length() == 1 )
				day = "0" + day;
			if(month.trim().length() == 1)
				month = "0"+ month;
			String from = day + "/" + month + "/" + yy;
			String to = mToDateView.getText().toString();
			if(Utils.validateString(to)){
				boolean flag = CheckDates(from, to);
				
				if(flag == true){
					mFromDateView.setText(new StringBuilder().append(day).append("/").append(month).append("/").append(yy).append(" "));
					
					Utils.storeString(mSharedPreferences, Utils.FROM_DATE, mFromDateView.getText().toString());
					
					if(Utils.validateString(mFromDateView.getText().toString()) && Utils.validateString(mToDateView.getText().toString())){
						String fromString = mFromDateView.getText().toString();
						String toString = mToDateView.getText().toString();
						String from_date = fromString.substring(6, 10) + "-" + fromString.substring(3, 5) + "-" + fromString.substring(0, 2);
						String to_date = toString.substring(6, 10) + "-" + toString.substring(3, 5) + "-" + toString.substring(0, 2);
						currentPage = 1;
						mTempCollectionlist.clear();
						getMyCollection(mSharedPreferences.getString(Utils.USER_ID, ""),"custom",from_date,to_date,String.valueOf(currentPage));
						
					}
				} else {
					Utils.showMessageDialog(getActivity(), getResources().getString(R.string.alert), "Please Select Before To Date");
				}
			} else {
				mFromDateView.setText(new StringBuilder().append(day).append("/").append(month).append("/").append(yy).append(" "));
				
				Utils.storeString(mSharedPreferences, Utils.FROM_DATE, mFromDateView.getText().toString());
				
				if(Utils.validateString(mFromDateView.getText().toString()) && Utils.validateString(mToDateView.getText().toString())){
					String fromString = mFromDateView.getText().toString();
					String toString = mToDateView.getText().toString();
					String from_date = fromString.substring(6, 10) + "-" + fromString.substring(3, 5) + "-" + fromString.substring(0, 2);
					String to_date = toString.substring(6, 10) + "-" + toString.substring(3, 5) + "-" + toString.substring(0, 2);
					currentPage = 1;
					mTempCollectionlist.clear();
					getMyCollection(mSharedPreferences.getString(Utils.USER_ID, ""),"custom",from_date,to_date,String.valueOf(currentPage));
					
				}
			}
			
			
		}
	}
	
	public class SelectToDateFragment extends DialogFragment implements DatePickerDialog.OnDateSetListener {

		@Override
		public Dialog onCreateDialog(Bundle savedInstanceState) {
			final Calendar c = Calendar.getInstance();
//		    c.add(Calendar.YEAR, -18);
			int year = 0,month = 0,day = 0;
		    if(mToDateView.getText().toString().trim().equalsIgnoreCase("")){
		    	 year = c.get(Calendar.YEAR);
				 month = c.get(Calendar.MONTH);
				 day = c.get(Calendar.DAY_OF_MONTH);
		    } else {
		    	String d = mToDateView.getText().toString();
		    	year = Integer.parseInt(d.substring(6, 10)); 
		    	month = Integer.parseInt(d.substring(3, 5)) - 1; 
		    	day = Integer.parseInt(d.substring(0, 2));
		    }
		    
			long maxDate = c.getTime().getTime();
			
			DatePickerDialog dpd = new DatePickerDialog(getActivity(), this,year,month,day);
	        dpd.getDatePicker().setMaxDate(maxDate);
            
	        return  dpd;
		}

		public void onDateSet(DatePicker view, int yy, int mm, int dd) {
			String day = String.valueOf(dd);
			String month = String.valueOf(mm + 1);
			if(day.trim().length() == 1 )
				day = "0" + day;
			if(month.trim().length() == 1)
				month = "0"+ month;
			String to = day + "/" + month + "/" + yy;
			String from = mFromDateView.getText().toString();
			boolean flag = CheckDates(from, to);
			
			if(flag == true){
				mToDateView.setText(new StringBuilder().append(day).append("/").append(month).append("/").append(yy).append(" "));
				
				Utils.storeString(mSharedPreferences, Utils.TO_DATE, mToDateView.getText().toString());
				
				if(Utils.validateString(mFromDateView.getText().toString()) && Utils.validateString(mToDateView.getText().toString())){
					String fromString = mFromDateView.getText().toString();
					String toString = mToDateView.getText().toString();
					String from_date = fromString.substring(6, 10) + "-" + fromString.substring(3, 5) + "-" + fromString.substring(0, 2);
					String to_date = toString.substring(6, 10) + "-" + toString.substring(3, 5) + "-" + toString.substring(0, 2);
					currentPage = 1;
					mTempCollectionlist.clear();
					getMyCollection(mSharedPreferences.getString(Utils.USER_ID, ""),"custom",from_date,to_date,String.valueOf(currentPage));	
				}
			} else {
				Utils.showMessageDialog(getActivity(), getResources().getString(R.string.alert), "Please Select After From Date");
			}
			
		}
	}
	
	public boolean CheckDates(String d1, String d2)    {
	    boolean b = false;
	    try {
	        if(dfDate.parse(d1).before(dfDate.parse(d2)))
	        {
	            b = true;//If start date is before end date
	        }
	        else if(dfDate.parse(d1).equals(dfDate.parse(d2)))
	        {
	            b = true;//If two dates are equal
	        }
	        else
	        {
	            b = false; //If start date is after the end date
	        }
	    } catch (ParseException e) {
	        // TODO Auto-generated catch block
	        e.printStackTrace();
	    }
	    return b;
	}
	
	
}
