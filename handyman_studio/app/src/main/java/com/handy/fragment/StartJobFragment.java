package com.handy.fragment;

import java.util.ArrayList;

import android.app.ProgressDialog;
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

import com.handy.MainActivity;
import com.handy.R;
import com.handy.adapter.HandymanHiringAdapter;
import com.handy.logger.Logger;
import com.handy.model.DataModel;
import com.handy.model.MyHiringsModel;
import com.handy.service.AsyncCallListener;
import com.handy.service.GetHandymanHiringsListRequestTask;
import com.handy.service.Utils;

public class StartJobFragment extends BaseFragment implements OnClickListener, AbsListView.OnScrollListener, SwipeRefreshLayout.OnRefreshListener {

    private static String TAG = "StartJobFragment";
    private SharedPreferences mSharedPreferences;
    private ArrayList<MyHiringsModel> mStartedHiringsList = new ArrayList<MyHiringsModel>();
    ArrayList<MyHiringsModel> mTempHiringslist = new ArrayList<MyHiringsModel>();
    HandymanHiringAdapter handymanHiringAdapter = null;
    ListView myhiringlist;

    private String title;
    private int page;

    Fragment fr;
    View mRootView, footerView = null;

    SwipeRefreshLayout swipeRefreshLayout;

    int currentPage = 1;
    boolean isLoading = false, isDataFinished = false, isRefreshing = false;
    private Context context;
    private ProgressBar progressBar = null;
    boolean flag = false;
    ProgressBar bar = null;
//    private ProgressDialog mProgressDialog;

    public static Fragment newInstance(int page, String title) {
        StartJobFragment fragmentFirst = new StartJobFragment();
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
        super.onAttach(activity);
        context = activity;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
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

        if (mSharedPreferences.getString(Utils.CANCEL_S, "").equalsIgnoreCase("CANCEL_S")) {
            Logger.e(TAG, "CANCEL_S");
            mStartedHiringsList.clear();
            mTempHiringslist.clear();
            handymanHiringAdapter = null;
            currentPage = 1;
            Utils.storeString(mSharedPreferences, Utils.CANCEL_S, "");
            Utils.storeString(mSharedPreferences, Utils.START_CANCEL, "START_CANCEL");

        } else if (mSharedPreferences.getString(Utils.COMPELETE, "").equalsIgnoreCase("COMPELETE")) {
            Logger.e(TAG, "COMPELETE");
            mStartedHiringsList.clear();
            mTempHiringslist.clear();
            handymanHiringAdapter = null;
            currentPage = 1;
            Utils.storeString(mSharedPreferences, Utils.COMPELETE, "");
            Utils.storeString(mSharedPreferences, Utils.START_COMPELETE, "START_COMPELETE");

        } else if (mSharedPreferences.getString(Utils.ACCEPT_START, "").equalsIgnoreCase("ACCEPT_START")) {
            Logger.e(TAG, "ACCEPT_START");
            mStartedHiringsList.clear();
            mTempHiringslist.clear();
            handymanHiringAdapter = null;
            currentPage = 1;
            Utils.storeString(mSharedPreferences, Utils.ACCEPT_START, "");
            Utils.storeString(mSharedPreferences, Utils.ACCEPT_START_ALL, "ACCEPT_START_ALL");

        }
//        else if(mSharedPreferences.getString(Utils.PENDING_ACCEPT, "").equalsIgnoreCase("PENDING_ACCEPT")){
//        	Logger.e(TAG, "PENDING_ACCEPT");
//        	mStartedHiringsList.clear();
//        	mTempHiringslist.clear();
//        	handymanHiringAdapter = null;
//        	currentPage = 1;
//        	Utils.storeString(mSharedPreferences, Utils.PENDING_ACCEPT, "");
//        	Utils.storeString(mSharedPreferences, Utils.PENDING_ACCEPT_ALL, "PENDING_ACCEPT_ALL");
//        	
//        }

        if (mTempHiringslist.size() > 0) {
            Logger.d(TAG, "SAVE Start Handyman size :: " + mTempHiringslist.size());
            handymanHiringAdapter = new HandymanHiringAdapter(getActivity(), mTempHiringslist, onAcceptedClickListener);
            myhiringlist.setAdapter(handymanHiringAdapter);
            handymanHiringAdapter.notifyDataSetChanged();
        } else {
            if (flag == false) {
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

        if (mStartedHiringsList.size() == 0) {
            getHMPendingHirings(mSharedPreferences.getString(Utils.USER_ID, ""), "start", String.valueOf(currentPage));
        }

//        swipeRefreshLayout.post(new Runnable() {
//			@Override
//			public void run() {
//				swipeRefreshLayout.setRefreshing(true);
//
//				if(mStartedHiringsList.size() == 0){
//	    			getHMPendingHirings(mSharedPreferences.getString(Utils.USER_ID, ""),"start",String.valueOf(currentPage));
//	    		}
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
//			swipeRefreshLayout.setRefreshing(true);
        GetHandymanHiringsListRequestTask getHandymanHiringsListRequestTask = new GetHandymanHiringsListRequestTask(getActivity());
        getHandymanHiringsListRequestTask.setAsyncCallListener(new AsyncCallListener() {

            @SuppressWarnings("unchecked")
            @Override
            public void onResponseReceived(Object response) {
                mStartedHiringsList.clear();
                DataModel dataModel = (DataModel) response;
                if (dataModel.getSuccess().equalsIgnoreCase("1")) {
                    if(isRefreshing){
                        mStartedHiringsList.clear();
                        mTempHiringslist.clear();
                        handymanHiringAdapter = null;
                    }
//						mStartedHiringsList = (ArrayList<MyHiringsModel>) response;
                    mStartedHiringsList.addAll(dataModel.getMyOrderList());
                    Logger.e(TAG, "mStartedHiringsList SIZE -- " + mStartedHiringsList.size());
                    if (mStartedHiringsList.size() > 0) {

                        myhiringlist.setVisibility(View.VISIBLE);
                        mRootView.findViewById(R.id.not_found_text).setVisibility(View.GONE);
                        bar.setVisibility(View.GONE);
//							if (mProgressDialog != null && mProgressDialog.isShowing()) {
//								mProgressDialog.dismiss();
//							}

                        setView(mStartedHiringsList);
                        isLoading = false;
                        setProgressVisibility(View.GONE);
                        removeFooter();

                    } else {
//							
                    }
                } else if (dataModel.getSuccess().equalsIgnoreCase("0")) {


                    isDataFinished = true;
                    if (mTempHiringslist.isEmpty() == true) {
                        myhiringlist.setAdapter(null);
                        myhiringlist.setVisibility(View.GONE);
                        bar.setVisibility(View.GONE);
                        mRootView.findViewById(R.id.not_found_text).setVisibility(View.VISIBLE);
                        ((TextView) mRootView.findViewById(R.id.not_found_text)).setText(dataModel.getMessage());
                    }

                    if(isRefreshing){
                        mStartedHiringsList.clear();
                        mTempHiringslist.clear();
                        handymanHiringAdapter = null;
                        myhiringlist.setAdapter(null);
                        myhiringlist.setVisibility(View.GONE);
                        bar.setVisibility(View.GONE);
                        mRootView.findViewById(R.id.not_found_text).setVisibility(View.VISIBLE);
                        ((TextView) mRootView.findViewById(R.id.not_found_text)).setText(dataModel.getMessage());
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
                isRefreshing = false;

            }

            @Override
            public void onErrorReceived(String error) {
                Toast.makeText(getActivity(), getString(R.string.try_again), Toast.LENGTH_SHORT).show();
                swipeRefreshLayout.setRefreshing(false);
                isRefreshing = false;
            }
        });
        getHandymanHiringsListRequestTask.execute(id, status, page);
//		}else{
//			Utils.showMessageDialog(getActivity(), getResources().getString(R.string.alert), 
//					getResources().getString(R.string.connection));
//		}
    }

    OnClickListener onAcceptedClickListener = new OnClickListener() {

        @Override
        public void onClick(View v) {
            if (v.getTag() != null) {
                int index = (Integer) v.getTag();
                Logger.e(TAG, "HIRE STARTED CLICK INDEX ::" + index);
                HandymanCustomerHireProfileFragment handymanCustomerHireProfileFragment = new HandymanCustomerHireProfileFragment();
                Bundle bundle = new Bundle();
                bundle.putSerializable(Utils.HANDYMAN_HIRE_START_DETAILS, mTempHiringslist.get(index));
                handymanCustomerHireProfileFragment.setArguments(bundle);
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, handymanCustomerHireProfileFragment).addToBackStack(TAG).commit();
            }
        }
    };


    private void setView(ArrayList<MyHiringsModel> hiringAllList) {
        if (isAdded()) {
            mTempHiringslist.addAll(hiringAllList);
            Logger.d(TAG, "SIZE of setview Start Handyman ::" + mTempHiringslist.size());
            if (hiringAllList.size() > 0) {

                if (handymanHiringAdapter == null) {
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

    }


    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        swipeRefreshLayout.setRefreshing(false);
        int lastInScreen = firstVisibleItem + visibleItemCount;
        if (lastInScreen == totalItemCount && totalItemCount != 0 && !isLoading && !isDataFinished) {
            if (mStartedHiringsList.size() >= 10) {
                Utils.flag_hiring = true;
                isLoading = true;
                isRefreshing = false;
                currentPage++;
                setProgressVisibility(View.VISIBLE);
                addFooter();
                getHMPendingHirings(mSharedPreferences.getString(Utils.USER_ID, ""), "start", String.valueOf(currentPage));
            }

        }
    }


    @Override
    public void onRefresh() {
        if (bar.getVisibility() == View.GONE) {
            swipeRefreshLayout.setRefreshing(true);

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (swipeRefreshLayout.isRefreshing()) {
                        currentPage = 1;
                        isLoading = true;
                        isDataFinished = false;
                        isRefreshing = true;
//                        if (mStartedHiringsList.size() == 0) {
                            Utils.flag_hiring = true;
                            getHMPendingHirings(mSharedPreferences.getString(Utils.USER_ID, ""), "start", String.valueOf(currentPage));
//                        }
                    }

                }
            }, 5000);
        } else {
            swipeRefreshLayout.setRefreshing(false);
        }

    }

}
