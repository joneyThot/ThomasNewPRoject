package com.handy.fragment;

import java.util.ArrayList;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Point;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AbsListView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.handy.MainActivity;
import com.handy.R;
import com.handy.adapter.HandymanHiringAdapter;
import com.handy.adapter.ReviewAdapter;
import com.handy.logger.Logger;
import com.handy.model.DataModel;
import com.handy.model.MyProfileModel;
import com.handy.model.ReviewRateModel;
import com.handy.service.AsyncCallListener;
import com.handy.service.GetMyProfileRequestListTask;
import com.handy.service.GetReviewListRequestTask;
import com.handy.service.Utils;
import com.handy.view.ExpandedListView;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Transformation;

public class MyReviewFragment extends BaseFragment implements OnClickListener, AbsListView.OnScrollListener, SwipeRefreshLayout.OnRefreshListener {

    private static String TAG = "MyReviewFragment";

    private SharedPreferences mSharedPreferences;
    private ArrayList<ReviewRateModel> mReviewRateList = new ArrayList<ReviewRateModel>();
    private ArrayList<ReviewRateModel> mTempReviewRateList = new ArrayList<ReviewRateModel>();
    ReviewAdapter reviewAdapter = null;
    ListView myreviewlist;
    private int mDeviceWidth = 480;

    SwipeRefreshLayout swipeRefreshLayout;
    Fragment fr;
    View mRootView, footerView = null;
    Context context;
    ProgressBar progressBar = null;
    int currentPage = 1;
    boolean isLoading = false, isDataFinished = false, isRefreshing = false;

    boolean flag = false;
    ProgressBar bar = null;

    @Override
    public void onAttach(Context activity) {
        super.onAttach(activity);
        context = activity;
    }

    @SuppressLint("NewApi")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mRootView = inflater.inflate(R.layout.fragment_review, container, false);

        WindowManager w = ((Activity) getActivity()).getWindowManager();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            Point size = new Point();
            w.getDefaultDisplay().getSize(size);
            mDeviceWidth = size.x;
        } else {
            Display d = w.getDefaultDisplay();
            mDeviceWidth = d.getWidth();
        }

        initview();
        return mRootView;
    }

    private void initview() {
        mSharedPreferences = getActivity().getSharedPreferences(Utils.PREF_NAME, Context.MODE_PRIVATE);
        ((MainActivity) getActivity()).setTitleText(getString(R.string.menu_my_review), "", View.GONE, View.VISIBLE, View.GONE);
        getActivity().findViewById(R.id.title_back).setVisibility(View.GONE);

        mActivity.getSlidingMenu().setMode(SlidingMenu.LEFT);
        mActivity.getSlidingMenu().setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);

        swipeRefreshLayout = (SwipeRefreshLayout) mRootView.findViewById(R.id.swipe_refresh_layout);
        myreviewlist = (ListView) mRootView.findViewById(R.id.review_list);
        footerView = ((MainActivity) context).getLayoutInflater().inflate(R.layout.loadmore, null);
        progressBar = (ProgressBar) footerView.findViewById(R.id.progress_bar);

        swipeRefreshLayout.setOnRefreshListener(this);
        myreviewlist.setOnScrollListener(this);
        myreviewlist.addFooterView(footerView);

        swipeRefreshLayout.setColorSchemeColors(Color.RED, Color.GREEN, Color.BLUE, Color.CYAN);
        bar = (ProgressBar) mRootView.findViewById(R.id.prog);

        if (mTempReviewRateList.size() > 0) {
            Logger.d(TAG, "mTempReviewRateList size :: " + mTempReviewRateList.size());
            reviewAdapter = new ReviewAdapter(getActivity(), mTempReviewRateList, onAcceptedClickListener);
            myreviewlist.setAdapter(reviewAdapter);
            reviewAdapter.notifyDataSetChanged();
        } else {
            if (flag == false) {
                bar.setVisibility(View.VISIBLE);
                bar.setClickable(flag);
                flag = true;
            }
        }

        if (progressBar != null) {
            if (progressBar.getVisibility() == View.VISIBLE) {
                setProgressVisibility(View.GONE);
                removeFooter();
            }
        }

//		swipeRefreshLayout.post(new Runnable() {
//			@Override
//			public void run() {
//				swipeRefreshLayout.setRefreshing(true);
//
        if (mReviewRateList.size() == 0) {
            getReview(mSharedPreferences.getString(Utils.USER_ID, ""), String.valueOf(currentPage));
        }
//			}
//		});


    }

    @Override
    public void onClick(View v) {
        // TODO Auto-generated method stub

    }


    private void getReview(String handyman_id, String page) {
//		if(Utils.checkInternetConnection(getActivity())){
        GetReviewListRequestTask getReviewListRequestTask = new GetReviewListRequestTask(getActivity());
        getReviewListRequestTask.setAsyncCallListener(new AsyncCallListener() {

            @SuppressWarnings("unchecked")
            @Override
            public void onResponseReceived(Object response) {
                mReviewRateList.clear();

                DataModel dataModel = (DataModel) response;
                if (dataModel.getSuccess().equalsIgnoreCase("1")) {
                    if (isRefreshing) {
                        mReviewRateList.clear();
                        mTempReviewRateList.clear();
                        reviewAdapter = null;
                    }
//						mReviewRateList = (ArrayList<ReviewRateModel>) response;
                    mReviewRateList.addAll(dataModel.getReviewRateModels());
                    Logger.e(TAG, "mReviewRateList SIZE -- " + mReviewRateList.size());
                    if (mReviewRateList.size() > 0) {


                        myreviewlist.setVisibility(View.VISIBLE);
                        mRootView.findViewById(R.id.not_found_text).setVisibility(View.GONE);
                        bar.setVisibility(View.GONE);

                        setViewCustomer(mReviewRateList);
                        isLoading = false;
                        setProgressVisibility(View.GONE);
                        removeFooter();

                    } /*else {

						}*/
                } else if (dataModel.getSuccess().equalsIgnoreCase("0")) {

                    isDataFinished = true;
                    if (mTempReviewRateList.isEmpty() == true) {
                        myreviewlist.setAdapter(null);
                        myreviewlist.setVisibility(View.GONE);
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
        getReviewListRequestTask.execute(handyman_id, page);
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
                Logger.e(TAG, "REVIEW ITEM CLICK INDEX ::" + index);
                MyReviewDetailsFragment myReviewDetailsFragment = new MyReviewDetailsFragment();
                Bundle bundle = new Bundle();
                bundle.putSerializable(Utils.HANDYMAN_REVIEW_DETAILS, mTempReviewRateList.get(index));
                myReviewDetailsFragment.setArguments(bundle);
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, myReviewDetailsFragment).addToBackStack(null).commit();
            }
        }
    };

    private void setViewCustomer(ArrayList<ReviewRateModel> hiringAllList) {
        if (isAdded()) {
            mTempReviewRateList.addAll(hiringAllList);
            Logger.d(TAG, "SIZE of setview Review ::" + mTempReviewRateList.size());
            if (hiringAllList.size() > 0) {

                if (reviewAdapter == null) {
                    reviewAdapter = new ReviewAdapter(getActivity(), mTempReviewRateList, onAcceptedClickListener);
                    myreviewlist.setAdapter(reviewAdapter);
                    reviewAdapter.notifyDataSetChanged();
                } else {
                    reviewAdapter.setList(mTempReviewRateList);
                    reviewAdapter.notifyDataSetChanged();
                }
            }
        }
    }

    private void setProgressVisibility(int visibility) {
        progressBar.setVisibility(visibility);
    }

    protected void removeFooter() {
        myreviewlist.removeFooterView(footerView);
    }

    protected void addFooter() {
        if (myreviewlist.getFooterViewsCount() == 0)
            myreviewlist.addFooterView(footerView);
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

            if (mReviewRateList.size() >= 10) {
                isLoading = true;
                isRefreshing = false;
                currentPage++;
                setProgressVisibility(View.VISIBLE);
                addFooter();
                getReview(mSharedPreferences.getString(Utils.USER_ID, ""), String.valueOf(currentPage));
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
//                        if (mReviewRateList.size() == 0) {
                            getReview(mSharedPreferences.getString(Utils.USER_ID, ""), String.valueOf(currentPage));
//                        }
                    }

                }
            }, 5000);
        } else {
            swipeRefreshLayout.setRefreshing(false);
        }


    }

}
