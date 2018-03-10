package com.handy.fragment;

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
import com.handy.adapter.HandymanCreditAdapter;
import com.handy.logger.Logger;
import com.handy.model.DataModel;
import com.handy.model.HandymanCreditsModel;
import com.handy.service.AsyncCallListener;
import com.handy.service.GetHandymanCreditListRequestTask;
import com.handy.service.Utils;

import java.util.ArrayList;


public class CancelRequestStatusFragment extends BaseFragment implements OnClickListener, AbsListView.OnScrollListener, SwipeRefreshLayout.OnRefreshListener{

	private static String TAG = "CancelRequestStatusFragment";
	private SharedPreferences mSharedPreferences;
	private ArrayList<HandymanCreditsModel> mHandymanCreditsList = new ArrayList<HandymanCreditsModel>();
	ArrayList<HandymanCreditsModel> mTempHandymanCreditslist = new ArrayList<HandymanCreditsModel>();
	HandymanCreditAdapter handymanCreditAdapter = null;
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
		CancelRequestStatusFragment fragmentFirst = new CancelRequestStatusFragment();
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

		if (mTempHandymanCreditslist.size() > 0) {
			Logger.d(TAG, "SAVE Handyman Credit size :: " + mTempHandymanCreditslist.size());
			handymanCreditAdapter = new HandymanCreditAdapter(getActivity(), mTempHandymanCreditslist);
			myhiringlist.setAdapter(handymanCreditAdapter);
			handymanCreditAdapter.notifyDataSetChanged();
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

		if (mHandymanCreditsList.size() == 0) {
			getHandymanCredit(mSharedPreferences.getString(Utils.USER_ID, ""), "cancelled", String.valueOf(currentPage));
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

	private void getHandymanCredit(String handyman_id, String is_requested, String page) {
//		if(Utils.checkInternetConnection(getActivity())){
		GetHandymanCreditListRequestTask getHandymanHiringsListRequestTask = new GetHandymanCreditListRequestTask(getActivity());
		getHandymanHiringsListRequestTask.setAsyncCallListener(new AsyncCallListener() {

			@SuppressWarnings("unchecked")
			@Override
			public void onResponseReceived(Object response) {
				mHandymanCreditsList.clear();
				DataModel dataModel = (DataModel) response;
				if (dataModel.getSuccess().equalsIgnoreCase("1")) {
					if(isRefreshing){
						mHandymanCreditsList.clear();
						mTempHandymanCreditslist.clear();
						handymanCreditAdapter = null;
					}
//						mAcceptedHiringsList = (ArrayList<MyHiringsModel>) response;
					mHandymanCreditsList.addAll(dataModel.getHandymanCreditsModels());
					Logger.e(TAG, "mHandymanCreditsList SIZE -- " + mHandymanCreditsList.size());
					if (mHandymanCreditsList.size() > 0) {

						myhiringlist.setVisibility(View.VISIBLE);
						mRootView.findViewById(R.id.not_found_text).setVisibility(View.GONE);
						bar.setVisibility(View.GONE);

						setView(mHandymanCreditsList);
						isLoading = false;
						setProgressVisibility(View.GONE);
						removeFooter();

					} /*else {
//
						}*/

				} else if (dataModel.getSuccess().equalsIgnoreCase("0")) {

//						bar.setVisibility(View.VISIBLE);
					isDataFinished = true;
					if (mTempHandymanCreditslist.isEmpty() == true) {
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
		getHandymanHiringsListRequestTask.execute(handyman_id, is_requested, page);
//		}else{
//			Utils.showMessageDialog(getActivity(), getResources().getString(R.string.alert),
//					getResources().getString(R.string.connection));
//		}
	}

	private void setView(ArrayList<HandymanCreditsModel> hiringAllList) {
		if (isAdded()) {
			mTempHandymanCreditslist.addAll(hiringAllList);
			Logger.d(TAG, "SIZE of setview Pending Handyman Credits ::" + mTempHandymanCreditslist.size());
			if (hiringAllList.size() > 0) {

				if (handymanCreditAdapter == null) {
					handymanCreditAdapter = new HandymanCreditAdapter(getActivity(), mTempHandymanCreditslist);
					myhiringlist.setAdapter(handymanCreditAdapter);
					handymanCreditAdapter.notifyDataSetChanged();
				} else {
					handymanCreditAdapter.setList(mTempHandymanCreditslist);
					handymanCreditAdapter.notifyDataSetChanged();
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

			if (mHandymanCreditsList.size() >= 10) {
				Utils.flag_hiring = true;
				isLoading = true;
				currentPage++;
				isRefreshing = false;
				setProgressVisibility(View.VISIBLE);
				addFooter();
				getHandymanCredit(mSharedPreferences.getString(Utils.USER_ID, ""), "cancelled", String.valueOf(currentPage));
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
						Utils.flag_hiring = true;
						getHandymanCredit(mSharedPreferences.getString(Utils.USER_ID, ""), "cancelled", String.valueOf(currentPage));
					}

				}
			}, 5000);
		} else {
			swipeRefreshLayout.setRefreshing(false);
		}
	}
}
