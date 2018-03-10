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
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.handy.MainActivity;
import com.handy.R;
import com.handy.adapter.HandymanCollectionAdapter;
import com.handy.logger.Logger;
import com.handy.model.MyCollectionModel;
import com.handy.model.MyCollectionModelList;
import com.handy.service.AsyncCallListener;
import com.handy.service.MyCollectionRequestTask;
import com.handy.service.Utils;

public class DayFragment extends BaseFragment implements OnClickListener, AbsListView.OnScrollListener, SwipeRefreshLayout.OnRefreshListener {

    private static String TAG = "DayFragment";
    private SharedPreferences mSharedPreferences;

    ArrayList<MyCollectionModel> collectionModelsList = new ArrayList<MyCollectionModel>();
    ArrayList<MyCollectionModel> mTempCollectionlist = new ArrayList<MyCollectionModel>();
    HandymanCollectionAdapter handymanCollectionAdapter = null;

    private String title;
    private int page;

    ListView mCollactionListView;
    Fragment fr;
    View mRootView, footerView = null;

    SwipeRefreshLayout swipeRefreshLayout;

    private Context context;

    int currentPage = 1;
    boolean isLoading = false, isDataFinished = false,isRefreshing = false;
    private ProgressBar progressBar = null;
    boolean flag = false;
    ProgressBar bar = null;
    //    int count = 0;
    String lastDate = "";

    public static Fragment newInstance(int page, String title) {
        DayFragment fragmentFirst = new DayFragment();
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

        if (mTempCollectionlist.size() > 0) {
            Logger.d(TAG, "SAVE Dayly Collection size :: " + mTempCollectionlist.size());
            handymanCollectionAdapter = new HandymanCollectionAdapter(getActivity(), mTempCollectionlist, onAcceptedClickListener);
            mCollactionListView.setAdapter(handymanCollectionAdapter);
            handymanCollectionAdapter.notifyDataSetChanged();

            if ((!mSharedPreferences.getString("day_total", "").isEmpty())) {
                mRootView.findViewById(R.id.total).setVisibility(View.VISIBLE);
                ((TextView) mRootView.findViewById(R.id.total)).setText("TOTAL : " + mSharedPreferences.getString("day_total", ""));
            }
        } else {

            if (flag == false) {
                bar.setVisibility(View.VISIBLE);
                flag = true;
            }  /*else {
                     mRootView.findViewById(R.id.not_found_text).setVisibility(View.VISIBLE);
					 ((TextView)mRootView.findViewById(R.id.not_found_text)).setText("no collections available.");
				 }*/
        }

        if (progressBar != null) {
            if (progressBar.getVisibility() == View.VISIBLE) {
                setProgressVisibility(View.GONE);
                removeFooter();
            }
        }

        if (collectionModelsList.size() == 0) {
            getMyCollection(mSharedPreferences.getString(Utils.USER_ID, ""), "day", String.valueOf(currentPage), lastDate);
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

    private void getMyCollection(String handyman_id, String mode, String page, String lastdate) {
//		if(Utils.checkInternetConnection(getActivity())){
        MyCollectionRequestTask myCollectionRequestTask = new MyCollectionRequestTask(getActivity());
        myCollectionRequestTask.setAsyncCallListener(new AsyncCallListener() {

            @SuppressWarnings("unchecked")
            @Override
            public void onResponseReceived(Object response) {
                collectionModelsList.clear();
                MyCollectionModelList myCollectionModel = (MyCollectionModelList) response;
                if (myCollectionModel.getSuccess().equalsIgnoreCase("1")) {
                    if(isRefreshing){
                        collectionModelsList.clear();
                        mTempCollectionlist.clear();
                        handymanCollectionAdapter = null;
                    }
                    Utils.storeString(mSharedPreferences, "day_total", myCollectionModel.getTotalamount());

                    collectionModelsList.addAll(myCollectionModel.getCollectionModelsList());
                    Logger.e(TAG, "collectionModelsList SIZE -- " + collectionModelsList.size());

                    if (collectionModelsList.size() > 0) {
                        mCollactionListView.setVisibility(View.VISIBLE);
                        mRootView.findViewById(R.id.not_found_text).setVisibility(View.GONE);
                        bar.setVisibility(View.GONE);

//								for (int i = 0; i < collectionModelsList.size(); i++) {
//									count = collectionModelsList.get(i).getCount();
//								}

                        setView(collectionModelsList);
                        isLoading = false;
                        setProgressVisibility(View.GONE);
                        removeFooter();
                    }


                } else if (myCollectionModel.getSuccess().equalsIgnoreCase("0")) {
                    flag = true;
                    isDataFinished = true;

                    if (mTempCollectionlist.isEmpty() == true) {
                        mCollactionListView.setVisibility(View.GONE);
                        mRootView.findViewById(R.id.not_found_text).setVisibility(View.VISIBLE);
                        bar.setVisibility(View.GONE);
                        ((TextView) mRootView.findViewById(R.id.not_found_text)).setText(myCollectionModel.getMessage());
                    }

                    new Handler().postDelayed(new Runnable() {

                        @Override
                        public void run() {
                            setProgressVisibility(View.GONE);
                            removeFooter();
                        }
                    }, 2000);
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
        myCollectionRequestTask.execute(handyman_id, mode, page, lastdate);
//		}else{
//			Utils.showMessageDialog(getActivity(), getResources().getString(R.string.alert), 
//					getResources().getString(R.string.connection));
//		}
    }

    private void setView(ArrayList<MyCollectionModel> hiringAllList) {
        if (isAdded()) {
            mTempCollectionlist.addAll(hiringAllList);
            Logger.d(TAG, "SIZE of setview Day Collection ::" + mTempCollectionlist.size());
            if (hiringAllList.size() > 0) {

                if (handymanCollectionAdapter == null) {
                    handymanCollectionAdapter = new HandymanCollectionAdapter(getActivity(), mTempCollectionlist, onAcceptedClickListener);
                    mCollactionListView.setAdapter(handymanCollectionAdapter);
                    handymanCollectionAdapter.notifyDataSetChanged();
                } else {
                    handymanCollectionAdapter.setList(mTempCollectionlist);
                    handymanCollectionAdapter.notifyDataSetChanged();
                }

                if ((!mSharedPreferences.getString("day_total", "").isEmpty())) {
                    mRootView.findViewById(R.id.total).setVisibility(View.VISIBLE);
                    ((TextView) mRootView.findViewById(R.id.total)).setText("TOTAL : " + mSharedPreferences.getString("day_total", ""));
                }
            }
        }
    }

    OnClickListener onAcceptedClickListener = new OnClickListener() {

        @Override
        public void onClick(View v) {
            if (v.getTag() != null) {
                int index = (Integer) v.getTag();
                Logger.e(TAG, "COLLECTION DAY CLICK INDEX ::" + index);
                if (Utils.validateString(mTempCollectionlist.get(index).getDateMain())) {

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
        swipeRefreshLayout.setRefreshing(false);
        int lastInScreen = firstVisibleItem + visibleItemCount;
        if (lastInScreen == totalItemCount && totalItemCount != 0 && !isLoading && !isDataFinished) {

            if (collectionModelsList.size() >= 10) {
                isLoading = true;
                isRefreshing = false;
                currentPage++;
                setProgressVisibility(View.VISIBLE);
                addFooter();
                lastDate = getLastDate();
                getMyCollection(mSharedPreferences.getString(Utils.USER_ID, ""), "day", String.valueOf(currentPage), lastDate);
            }

        }
    }

    public String getLastDate() {
        String lastdate = "";
        for (int i = mTempCollectionlist.size() - 1; i >= 0; i--) {
            if (Utils.validateString(mTempCollectionlist.get(i).getDateMain())) {
                lastdate = mTempCollectionlist.get(i).getDateMain();
                break;
            }
        }
        return lastdate;
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
                        lastDate = "";
//                        if (collectionModelsList.size() == 0) {
                            getMyCollection(mSharedPreferences.getString(Utils.USER_ID, ""), "day", String.valueOf(currentPage), lastDate);
//                        }
                    }

                }
            }, 5000);
        } else {
            swipeRefreshLayout.setRefreshing(false);
        }

    }

}
