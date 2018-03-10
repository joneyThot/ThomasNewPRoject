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
import com.handyman.adapter.HandymanHiringAdapter;
import com.handyman.adapter.MyHiringAdapter;
import com.handyman.logger.Logger;
import com.handyman.model.DataModel;
import com.handyman.model.MyHiringsModel;
import com.handyman.service.AsyncCallListener;
import com.handyman.service.GetMyHiringsListRequestTask;
import com.handyman.service.Utils;

public class RejectedFragment extends BaseFragment implements OnClickListener, AbsListView.OnScrollListener, SwipeRefreshLayout.OnRefreshListener {

    private static String TAG = "RejectedFragment";
    private SharedPreferences mSharedPreferences;
    private ArrayList<MyHiringsModel> mRejectedHiringsList = new ArrayList<MyHiringsModel>();
    //	ArrayList<MyHiringsModel> mTempHiringslist = new ArrayList<MyHiringsModel>();
    ArrayList<MyHiringsModel> mTempCustomerList = new ArrayList<MyHiringsModel>();
    HandymanHiringAdapter handymanHiringAdapter = null;
    MyHiringAdapter myHiringAdapter = null;
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

    public static Fragment newInstance(int page, String title) {
        RejectedFragment fragmentFirst = new RejectedFragment();
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
        
       /* if(mSharedPreferences.getString("HandymanCustomerHireProfileFragment", "").equalsIgnoreCase("HandymanCustomerHireProfileFragment")){
            mRejectedHiringsList.clear();
        	mTempCustomerList.clear();
        	mTempHiringslist.clear();
        	handymanHiringAdapter = null;
        	myHiringAdapter = null;
        	currentPage = 1;
        	Utils.storeString(mSharedPreferences, "HandymanCustomerHireProfileFragment", "");
        }*/

//        if(mSharedPreferences.getString(Utils.USER_TYPE, "").equalsIgnoreCase("customer")){
        if (mTempCustomerList.size() > 0) {
            Logger.d(TAG, "SAVE Rejected Customer size :: " + mTempCustomerList.size());
            myHiringAdapter = new MyHiringAdapter(getActivity(), mTempCustomerList, onAcceptedClickListener);
            myhiringlist.setAdapter(myHiringAdapter);
            myHiringAdapter.notifyDataSetChanged();
        } else {
            if (flag == false) {
                bar.setVisibility(View.VISIBLE);
                flag = true;
            }

        }
//       } else {
//	        if(mTempHiringslist.size() > 0){
//	        	Logger.d(TAG, "SAVE Rejected Handyman size :: " + mTempHiringslist.size());
//	        	handymanHiringAdapter = new HandymanHiringAdapter(getActivity(), mTempHiringslist, onAcceptedClickListener);
//				myhiringlist.setAdapter(handymanHiringAdapter);
//				handymanHiringAdapter.notifyDataSetChanged();
//	    	}
//       }


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
//				if(mRejectedHiringsList.size() == 0){
//	    			getRejectHirings(mSharedPreferences.getString(Utils.USER_ID, ""),"rejected",String.valueOf(currentPage));
//	         }
//			}
//		});

        if (mRejectedHiringsList.size() == 0) {
            getRejectHirings(mSharedPreferences.getString(Utils.USER_ID, ""), "rejected", String.valueOf(currentPage));
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

    private void getRejectHirings(String id, String status, String page) {
//		if(Utils.checkInternetConnection(getActivity())){
        GetMyHiringsListRequestTask myHiringsListRequestTask = new GetMyHiringsListRequestTask(getActivity());
        myHiringsListRequestTask.setAsyncCallListener(new AsyncCallListener() {

            @SuppressWarnings("unchecked")
            @Override
            public void onResponseReceived(Object response) {
                mRejectedHiringsList.clear();
                DataModel dataModel = (DataModel) response;
                if (dataModel.getSuccess().equalsIgnoreCase("1")) {
                    if (isRefreshing) {
                        mRejectedHiringsList.clear();
                        mTempCustomerList.clear();
                        myHiringAdapter = null;
                    }
//						mRejectedHiringsList = (ArrayList<MyHiringsModel>) response;
                    mRejectedHiringsList.addAll(dataModel.getMyHiringsModels());
                    Logger.e(TAG, "mRejectedHiringsList SIZE -- " + mRejectedHiringsList.size());
                    if (mRejectedHiringsList.size() > 0) {

                        myhiringlist.setVisibility(View.VISIBLE);
                        mRootView.findViewById(R.id.not_found_text).setVisibility(View.GONE);
                        bar.setVisibility(View.GONE);

                        setViewCustomer(mRejectedHiringsList);
                        isLoading = false;
                        setProgressVisibility(View.GONE);
                        removeFooter();

                    } /*else {
							
						}*/
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
                        mRejectedHiringsList.clear();
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


    OnClickListener onAcceptedClickListener = new OnClickListener() {

        @Override
        public void onClick(View v) {
            if (v.getTag() != null) {
                int index = (Integer) v.getTag();
                Logger.e(TAG, "HIRE REJECT CLICK INDEX ::" + index);
                HandymanCustomerHireProfileFragment handymanCustomerHireProfileFragment = new HandymanCustomerHireProfileFragment();
                Bundle bundle = new Bundle();
                bundle.putSerializable(Utils.HANDYMAN_HIRE_REJECT_DETAILS, mTempCustomerList.get(index));
                handymanCustomerHireProfileFragment.setArguments(bundle);
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, handymanCustomerHireProfileFragment).addToBackStack(TAG).commit();
            }
        }
    };

    private void setViewCustomer(ArrayList<MyHiringsModel> hiringAllList) {
        if (isAdded()) {
            mTempCustomerList.addAll(hiringAllList);
            Logger.d(TAG, "SIZE of setview Reject Customer ::" + mTempCustomerList.size());
            if (hiringAllList.size() > 0) {

                if (myHiringAdapter == null) {
                    myHiringAdapter = new MyHiringAdapter(getActivity(), mTempCustomerList, onAcceptedClickListener);
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
    public void onScroll(AbsListView view, int firstVisibleItem,
                         int visibleItemCount, int totalItemCount) {
        swipeRefreshLayout.setRefreshing(false);
        int lastInScreen = firstVisibleItem + visibleItemCount;
        if (lastInScreen == totalItemCount && totalItemCount != 0 && !isLoading && !isDataFinished) {

            if (mRejectedHiringsList.size() >= 10) {
                isLoading = true;
                isRefreshing = false;
                currentPage++;
                setProgressVisibility(View.VISIBLE);
                addFooter();
                getRejectHirings(mSharedPreferences.getString(Utils.USER_ID, ""), "rejected", String.valueOf(currentPage));
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
//                        if (mRejectedHiringsList.size() == 0) {
                            getRejectHirings(mSharedPreferences.getString(Utils.USER_ID, ""), "rejected", String.valueOf(currentPage));
//                        }
                    }

                }
            }, 5000);
        } else {
            swipeRefreshLayout.setRefreshing(false);
        }

    }

}
