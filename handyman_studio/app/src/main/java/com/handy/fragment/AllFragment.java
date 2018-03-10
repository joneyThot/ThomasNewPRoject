package com.handy.fragment;

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

import com.handy.MainActivity;
import com.handy.R;
import com.handy.SplashActivity;
import com.handy.adapter.HandymanHiringAdapter;
import com.handy.logger.Logger;
import com.handy.model.DataModel;
import com.handy.model.MyHiringsModel;
import com.handy.service.AsyncCallListener;
import com.handy.service.GetHandymanHiringsListRequestTask;
import com.handy.service.Utils;

public class AllFragment extends BaseFragment implements OnClickListener, AbsListView.OnScrollListener, SwipeRefreshLayout.OnRefreshListener {

    private static String TAG = "AllFragment";
    private SharedPreferences mSharedPreferences;
    private ArrayList<MyHiringsModel> mAllHiringsList = new ArrayList<MyHiringsModel>();
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

    public static Fragment newInstance(int page, String title) {
        AllFragment fragmentFirst = new AllFragment();
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

        if (mSharedPreferences.getString(Utils.PENDING_ACCEPT_ALL, "").equalsIgnoreCase("PENDING_ACCEPT_ALL")) {
            Logger.e(TAG, "PENDING_ACCEPT_ALL");
            mAllHiringsList.clear();
            mTempHiringslist.clear();
            handymanHiringAdapter = null;
            currentPage = 1;
            Utils.storeString(mSharedPreferences, Utils.PENDING_ACCEPT_ALL, "");

        } else if (mSharedPreferences.getString(Utils.PENDING_CANCEL_ALL, "").equalsIgnoreCase("PENDING_CANCEL_ALL")) {
            Logger.e(TAG, "PENDING_CANCEL_ALL");
            mAllHiringsList.clear();
            mTempHiringslist.clear();
            handymanHiringAdapter = null;
            currentPage = 1;
            Utils.storeString(mSharedPreferences, Utils.PENDING_CANCEL_ALL, "");

        } else if (mSharedPreferences.getString(Utils.ACCEPT_START_ALL, "").equalsIgnoreCase("ACCEPT_START_ALL")) {
            Logger.e(TAG, "ACCEPT_START_ALL");
            mAllHiringsList.clear();
            mTempHiringslist.clear();
            handymanHiringAdapter = null;
            currentPage = 1;
            Utils.storeString(mSharedPreferences, Utils.ACCEPT_START_ALL, "");

        } else if (mSharedPreferences.getString(Utils.ACCEPT_CANCEL_ALL, "").equalsIgnoreCase("ACCEPT_CANCEL_ALL")) {
            Logger.e(TAG, "ACCEPT_CANCEL_ALL");
            mAllHiringsList.clear();
            mTempHiringslist.clear();
            handymanHiringAdapter = null;
            currentPage = 1;
            Utils.storeString(mSharedPreferences, Utils.ACCEPT_CANCEL_ALL, "");

        } else if (mSharedPreferences.getString(Utils.START_CANCEL_ALL, "").equalsIgnoreCase("START_CANCEL_ALL")) {
            Logger.e(TAG, "START_CANCEL_ALL");
            mAllHiringsList.clear();
            mTempHiringslist.clear();
            handymanHiringAdapter = null;
            currentPage = 1;
            Utils.storeString(mSharedPreferences, Utils.START_CANCEL_ALL, "");

        } else if (mSharedPreferences.getString(Utils.START_COMPELETE_ALL, "").equalsIgnoreCase("START_COMPELETE_ALL")) {
            Logger.e(TAG, "START_COMPELETE_ALL");
            mAllHiringsList.clear();
            mTempHiringslist.clear();
            handymanHiringAdapter = null;
            currentPage = 1;
            Utils.storeString(mSharedPreferences, Utils.START_COMPELETE_ALL, "");
        }

        if (mTempHiringslist.size() > 0) {
            Logger.d(TAG, "SAVE All Handyman size :: " + mTempHiringslist.size());
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

//        swipeRefreshLayout.post(new Runnable() {
//			@Override
//			public void run() {
//				swipeRefreshLayout.setRefreshing(true);
//
//				 if(mAllHiringsList.size() == 0){
//		    	    	getHMPendingHirings(mSharedPreferences.getString(Utils.USER_ID, ""), "all", String.valueOf(currentPage));
//		    	    }	
//			}
//		});


        if (mAllHiringsList.size() == 0) {
            getHMPendingHirings(mSharedPreferences.getString(Utils.USER_ID, ""), "all", String.valueOf(currentPage));
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
                mAllHiringsList.clear();
                DataModel dataModel = (DataModel) response;
                if (dataModel.getSuccess().equalsIgnoreCase("1")) {
                    if (isRefreshing) {
                        mAllHiringsList.clear();
                        mTempHiringslist.clear();
                        handymanHiringAdapter = null;
                    }
//						mAllHiringsList.addAll((ArrayList<MyHiringsModel>) response);
                    mAllHiringsList.addAll(dataModel.getMyOrderList());
                    Logger.e(TAG, "mAllHiringsList SIZE -- " + mAllHiringsList.size());
                    if (mAllHiringsList.size() > 0) {

                        myhiringlist.setVisibility(View.VISIBLE);
                        mRootView.findViewById(R.id.not_found_text).setVisibility(View.GONE);
                        bar.setVisibility(View.GONE);

                        setView(mAllHiringsList);
                        isLoading = false;
                        setProgressVisibility(View.GONE);
                        removeFooter();
                            /*for (int i = 0; i < mAllHiringsList.size(); i++) {
                                myhiringlist.setVisibility(View.VISIBLE);
								mRootView.findViewById(R.id.not_found_text).setVisibility(View.GONE);
								
								isLoading = false;
			                    setProgressVisibility(View.GONE);
			                    removeFooter();
							}*/

                    } /*else {
//							
//							myhiringlist.setVisibility(View.GONE);
//							mRootView.findViewById(R.id.not_found_text).setVisibility(View.VISIBLE);
						}*/

                } else if (dataModel.getSuccess().equalsIgnoreCase("0")) {
                    isDataFinished = true;
                    if (mTempHiringslist.isEmpty() == true) {
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
            swipeRefreshLayout.setRefreshing(false);

            if (v.getTag() != null) {
                int index = (Integer) v.getTag();
                Logger.e(TAG, "HIRE ACCEPT CLICK INDEX ::" + index);
                HandymanCustomerHireProfileFragment handymanCustomerHireProfileFragment = new HandymanCustomerHireProfileFragment();
                Bundle bundle = new Bundle();
                bundle.putSerializable(Utils.HANDYMAN_HIRE_ALL_DETAILS, mTempHiringslist.get(index));
                handymanCustomerHireProfileFragment.setArguments(bundle);
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, handymanCustomerHireProfileFragment).addToBackStack(TAG).commit();
            }
        }
    };


    private void setView(ArrayList<MyHiringsModel> hiringAllList) {
        if (isAdded()) {
            mTempHiringslist.addAll(hiringAllList);
            Logger.d(TAG, "SIZE of setview All Handyman ::" + mTempHiringslist.size());
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
		/*switch (scrollState){
		case SCROLL_STATE_TOUCH_SCROLL:
			swipeRefreshLayout.setRefreshing(false);
			break;
		}*/
    }


    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        swipeRefreshLayout.setRefreshing(false);
        int lastInScreen = firstVisibleItem + visibleItemCount;
        if (lastInScreen == totalItemCount && totalItemCount != 0 && !isLoading && !isDataFinished) {

            if (mAllHiringsList.size() >= 10) {
                Utils.flag_hiring = true;
                isLoading = true;
                currentPage++;
                setProgressVisibility(View.VISIBLE);
                addFooter();
                isRefreshing = false;
                getHMPendingHirings(mSharedPreferences.getString(Utils.USER_ID, ""), "all", String.valueOf(currentPage));
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
                    Logger.e(TAG, "onRefresh");
                    if (swipeRefreshLayout.isRefreshing()) {
                        Utils.flag_hiring = true;

//						handymanHiringAdapter.notifyDataSetChanged();
                        currentPage = 1;
                        isLoading = true;
                        isDataFinished = false;
                        isRefreshing = true;
//						if (mAllHiringsList.size() == 0) {
//							Utils.flag_hiring = true;
                        getHMPendingHirings(mSharedPreferences.getString(Utils.USER_ID, ""), "all", String.valueOf(currentPage));
//						}
                    }
                }
            }, 5000);
        } else {
            swipeRefreshLayout.setRefreshing(false);
        }

    }

}
