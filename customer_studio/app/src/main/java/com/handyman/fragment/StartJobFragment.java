package com.handyman.fragment;

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

import com.handyman.MainActivity;
import com.handyman.R;
import com.handyman.SplashActivity;
import com.handyman.adapter.HandymanHiringAdapter;
import com.handyman.adapter.MyHiringAdapter;
import com.handyman.logger.Logger;
import com.handyman.model.DataModel;
import com.handyman.model.MyHiringsModel;
import com.handyman.service.AsyncCallListener;
import com.handyman.service.GetHandymanHiringsListRequestTask;
import com.handyman.service.GetMyHiringsListRequestTask;
import com.handyman.service.Utils;

public class StartJobFragment extends BaseFragment implements OnClickListener, AbsListView.OnScrollListener, SwipeRefreshLayout.OnRefreshListener {

    private static String TAG = "StartJobFragment";
    private SharedPreferences mSharedPreferences;
    private ArrayList<MyHiringsModel> mStartedHiringsList = new ArrayList<MyHiringsModel>();
    ArrayList<MyHiringsModel> mTempCustomerList = new ArrayList<MyHiringsModel>();
    MyHiringAdapter myHiringAdapter = null;
    ListView myhiringlist;

    private String title;
    private int page;

    SwipeRefreshLayout swipeRefreshLayout;

    Fragment fr;
    View mRootView, footerView = null;

    int currentPage = 1;
    boolean isLoading = false, isDataFinished = false, isRefreshing = false;
    private Context context;
    private ProgressBar progressBar = null;

    boolean flag = false;
    ProgressBar bar = null;

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
            Logger.e(TAG, "CANCEL_A");
            mStartedHiringsList.clear();
            mTempCustomerList.clear();
            myHiringAdapter = null;
            currentPage = 1;
            Utils.storeString(mSharedPreferences, Utils.CANCEL_S, "");
            Utils.storeString(mSharedPreferences, Utils.START_CANCEL, "START_CANCEL");

        }

        if (mTempCustomerList.size() > 0) {
            Logger.d(TAG, "SAVE Start Customer size :: " + mTempCustomerList.size());
            myHiringAdapter = new MyHiringAdapter(getActivity(), mTempCustomerList, onStartedClickListener);
            myhiringlist.setAdapter(myHiringAdapter);
            myHiringAdapter.notifyDataSetChanged();
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

//        swipeRefreshLayout.post(new Runnable() {
//			@Override
//			public void run() {
//				swipeRefreshLayout.setRefreshing(true);
//
//				if(mStartedHiringsList.size() == 0){
//		        	getStartedHirings(mSharedPreferences.getString(Utils.USER_ID, ""),"start",String.valueOf(currentPage));
//		        }
//			}
//		});

        if (mStartedHiringsList.size() == 0) {
            getStartedHirings(mSharedPreferences.getString(Utils.USER_ID, ""), "start", String.valueOf(currentPage));
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

    private void getStartedHirings(String id, String status, String page) {
//		if(Utils.checkInternetConnection(getActivity())){
        GetMyHiringsListRequestTask myHiringsListRequestTask = new GetMyHiringsListRequestTask(getActivity());
        myHiringsListRequestTask.setAsyncCallListener(new AsyncCallListener() {

            @SuppressWarnings("unchecked")
            @Override
            public void onResponseReceived(Object response) {
                mStartedHiringsList.clear();
                DataModel dataModel = (DataModel) response;
                if (dataModel.getSuccess().equalsIgnoreCase("1")) {
                    if (isRefreshing) {
                        mStartedHiringsList.clear();
                        mTempCustomerList.clear();
                        myHiringAdapter = null;
                    }
//						mStartedHiringsList = (ArrayList<MyHiringsModel>) response;
                    mStartedHiringsList.addAll(dataModel.getMyHiringsModels());
                    Logger.e(TAG, "mStartedHiringsList SIZE -- " + mStartedHiringsList.size());
                    if (mStartedHiringsList.size() > 0) {

                        myhiringlist.setVisibility(View.VISIBLE);
                        mRootView.findViewById(R.id.not_found_text).setVisibility(View.GONE);
                        bar.setVisibility(View.GONE);

                        setViewCustomer(mStartedHiringsList);
                        isLoading = false;
                        setProgressVisibility(View.GONE);
                        removeFooter();

                    }
//						else {
//							
//						}
                } else if (dataModel.getSuccess().equalsIgnoreCase("0")) {

                    isDataFinished = true;
                    if (mTempCustomerList.isEmpty() == true) {
                        myhiringlist.setAdapter(null);
                        myhiringlist.setVisibility(View.GONE);
                        bar.setVisibility(View.GONE);
                        mRootView.findViewById(R.id.not_found_text).setVisibility(View.VISIBLE);
                        ((TextView) mRootView.findViewById(R.id.not_found_text)).setText(dataModel.getMessage());

                    }

                    if(isRefreshing){
                        mStartedHiringsList.clear();
                        mTempCustomerList.clear();
                        myHiringAdapter = null;
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
        myHiringsListRequestTask.execute(id, status, page);
//		}else{
//			Utils.showMessageDialog(getActivity(), getResources().getString(R.string.alert), 
//					getResources().getString(R.string.connection));
//		}
    }


    OnClickListener onStartedClickListener = new OnClickListener() {

        @Override
        public void onClick(View v) {
            if (v.getTag() != null) {
                int index = (Integer) v.getTag();
                Logger.e(TAG, "HIRE STARTED CLICK INDEX ::" + index);
                HandymanCustomerHireProfileFragment handymanCustomerHireProfileFragment = new HandymanCustomerHireProfileFragment();
                Bundle bundle = new Bundle();
                bundle.putSerializable(Utils.HANDYMAN_HIRE_START_DETAILS, mTempCustomerList.get(index));
                handymanCustomerHireProfileFragment.setArguments(bundle);
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, handymanCustomerHireProfileFragment).addToBackStack(TAG).commit();
            }
        }
    };

    private void setViewCustomer(ArrayList<MyHiringsModel> hiringAllList) {

        if (isAdded()) {
            mTempCustomerList.addAll(hiringAllList);
            Logger.d(TAG, "SIZE of setview start Customer ::" + mTempCustomerList.size());
            if (hiringAllList.size() > 0) {

                if (myHiringAdapter == null) {
                    myHiringAdapter = new MyHiringAdapter(getActivity(), mTempCustomerList, onStartedClickListener);
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

    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        swipeRefreshLayout.setRefreshing(false);
        int lastInScreen = firstVisibleItem + visibleItemCount;
        if (lastInScreen == totalItemCount && totalItemCount != 0 && !isLoading && !isDataFinished) {

            if (mStartedHiringsList.size() >= 10) {
                isLoading = true;
                isRefreshing = false;
                currentPage++;
                setProgressVisibility(View.VISIBLE);
                addFooter();
                getStartedHirings(mSharedPreferences.getString(Utils.USER_ID, ""), "start", String.valueOf(currentPage));
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
                        isRefreshing = true;
                        isDataFinished = false;
//                        if (mStartedHiringsList.size() == 0) {
                            getStartedHirings(mSharedPreferences.getString(Utils.USER_ID, ""), "start", String.valueOf(currentPage));
//                        }
                    }

                }
            }, 5000);
        } else {
            swipeRefreshLayout.setRefreshing(false);
        }
    }

}
