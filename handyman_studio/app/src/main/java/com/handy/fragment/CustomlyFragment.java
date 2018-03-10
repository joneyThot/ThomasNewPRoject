package com.handy.fragment;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
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

import com.handy.MainActivity;
import com.handy.R;
import com.handy.adapter.HandymanCollectionAdapter;
import com.handy.logger.Logger;
import com.handy.model.MyCollectionModel;
import com.handy.model.MyCollectionModelList;
import com.handy.service.AsyncCallListener;
import com.handy.service.MyCollectionCustomRequestTask;
import com.handy.service.Utils;

public class CustomlyFragment extends BaseFragment implements OnClickListener, AbsListView.OnScrollListener, SwipeRefreshLayout.OnRefreshListener {

    private static String TAG = "CustomlyFragment";
    private SharedPreferences mSharedPreferences;

    private String title;
    private int page;
    private Calendar c;


    ArrayList<MyCollectionModel> collectionModelsList = new ArrayList<MyCollectionModel>();
    ArrayList<MyCollectionModel> mTempCollectionlist = new ArrayList<MyCollectionModel>();
    HandymanCollectionAdapter handymanCollectionAdapter = null;

    ListView mCollactionListView;
    Fragment fr;
    View mRootView, footerView = null;

    TextView mFromDateView, mToDateView;
    private Context context;
    SimpleDateFormat dfDate;

    SwipeRefreshLayout swipeRefreshLayout;

    int currentPage = 1;
    boolean isLoading = false, isDataFinished = false;
    private ProgressBar progressBar = null;
    boolean flag = false, isDateSelect = false;
    //    int count = 0;
    String lastDate = "";


    public static Fragment newInstance(int page, String title) {
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
        super.onAttach(activity);
        context = activity;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mRootView = inflater.inflate(R.layout.row_my_collection_list, container, false);
        initview();
        return mRootView;
    }

    @SuppressLint("SimpleDateFormat")
    private void initview() {
        mSharedPreferences = getActivity().getSharedPreferences(Utils.PREF_NAME, Context.MODE_PRIVATE);

        dfDate = new SimpleDateFormat("dd/MM/yyyy");
        c = Calendar.getInstance();

        ((LinearLayout) mRootView.findViewById(R.id.from_to_layout)).setVisibility(View.VISIBLE);
        mFromDateView = (TextView) mRootView.findViewById(R.id.from_date);
        mToDateView = (TextView) mRootView.findViewById(R.id.to_date);

        mFromDateView.setOnClickListener(this);
        mToDateView.setOnClickListener(this);
        mCollactionListView = (ListView) mRootView.findViewById(R.id.collaction_list);

        swipeRefreshLayout = (SwipeRefreshLayout) mRootView.findViewById(R.id.swipe_refresh_hiring_layout);
        swipeRefreshLayout.setOnRefreshListener(this);

        footerView = ((MainActivity) context).getLayoutInflater().inflate(R.layout.loadmore, null);
        progressBar = (ProgressBar) footerView.findViewById(R.id.progress_bar);

        mCollactionListView.setOnScrollListener(this);
        mCollactionListView.addFooterView(footerView);

        if (mTempCollectionlist.size() > 0) {
            Logger.d(TAG, "SAVE Custom Collection size :: " + mTempCollectionlist.size());
            handymanCollectionAdapter = new HandymanCollectionAdapter(getActivity(), mTempCollectionlist, onAcceptedClickListener);
            mCollactionListView.setAdapter(handymanCollectionAdapter);
            handymanCollectionAdapter.notifyDataSetChanged();

            if ((!mSharedPreferences.getString("custom_total", "").isEmpty())) {
                mRootView.findViewById(R.id.total).setVisibility(View.VISIBLE);
                ((TextView) mRootView.findViewById(R.id.total)).setText("TOTAL : " + mSharedPreferences.getString("custom_total", ""));
            }

        } else {
            if (flag == true) {
                mRootView.findViewById(R.id.not_found_text).setVisibility(View.VISIBLE);
                ((TextView) mRootView.findViewById(R.id.not_found_text)).setText("no collections available.");
            }
        }

        if ((!mSharedPreferences.getString(Utils.FROM_DATE, "").isEmpty()) && (!mSharedPreferences.getString(Utils.TO_DATE, "").isEmpty())) {
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

//			DialogFragment fromDateFragment = new SelectFromDateFragment();
//			fromDateFragment.show(getFragmentManager(), "From DatePicker");

                int year = 0, month = 0, day = 0;
                if (mFromDateView.getText().toString().trim().equalsIgnoreCase("")) {
                    year = c.get(Calendar.YEAR);
                    month = c.get(Calendar.MONTH);
                    day = c.get(Calendar.DAY_OF_MONTH);
                } else {
                    String d = mFromDateView.getText().toString();
                    year = Integer.parseInt(d.substring(6, 10));
                    month = Integer.parseInt(d.substring(3, 5)) - 1;
                    day = Integer.parseInt(d.substring(0, 2));
                }

//                long minDate = c.getTime().getTime();
                DatePickerDialog dpdFrom = new DatePickerDialog(getActivity(), R.style.DialogTheme, fromDate, year, month, day);
                dpdFrom.getDatePicker().setMaxDate(new Date().getTime());
                dpdFrom.show();
                break;

            case R.id.to_date:

                if (!mFromDateView.getText().toString().equalsIgnoreCase("") && !mFromDateView.getText().toString().equalsIgnoreCase(null)) {
//				DialogFragment toDateFragment = new SelectToDateFragment();
//				toDateFragment.show(getFragmentManager(), "To DatePicker");
                    int year1 = 0, month1 = 0, day1 = 0;
                    if (mToDateView.getText().toString().trim().equalsIgnoreCase("")) {
                        year1 = c.get(Calendar.YEAR);
                        month1 = c.get(Calendar.MONTH);
                        day1 = c.get(Calendar.DAY_OF_MONTH);
                    } else {
                        String d = mToDateView.getText().toString();
                        year1 = Integer.parseInt(d.substring(6, 10));
                        month1 = Integer.parseInt(d.substring(3, 5)) - 1;
                        day1 = Integer.parseInt(d.substring(0, 2));
                    }

                    DatePickerDialog dpdTo = new DatePickerDialog(getActivity(), R.style.DialogTheme, toDate, year1, month1, day1);
                    dpdTo.getDatePicker().setMaxDate(new Date().getTime());
                    dpdTo.show();
                } else {
                    Utils.showMessageDialog(getActivity(), getResources().getString(R.string.alert), "From date should not be blank");
                }

                break;
        }
    }

    DatePickerDialog.OnDateSetListener fromDate = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int yy, int mm, int dd) {
            String day = String.valueOf(dd);
            String month = String.valueOf(mm + 1);
            if (day.trim().length() == 1)
                day = "0" + day;
            if (month.trim().length() == 1)
                month = "0" + month;
            String from = day + "/" + month + "/" + yy;
            String to = mToDateView.getText().toString();
            if (Utils.validateString(to)) {
                boolean flag = CheckDates(from, to);

                if (flag == true) {
                    mFromDateView.setText(new StringBuilder().append(day).append("/").append(month).append("/").append(yy).append(" "));

                    Utils.storeString(mSharedPreferences, Utils.FROM_DATE, mFromDateView.getText().toString());

                    if (Utils.validateString(mFromDateView.getText().toString()) && Utils.validateString(mToDateView.getText().toString())) {
                        String fromString = mFromDateView.getText().toString();
                        String toString = mToDateView.getText().toString();
                        String from_date = fromString.substring(6, 10) + "-" + fromString.substring(3, 5) + "-" + fromString.substring(0, 2);
                        String to_date = toString.substring(6, 10) + "-" + toString.substring(3, 5) + "-" + toString.substring(0, 2);
                        currentPage = 1;
//						isDataFinished = false;
//						mTempCollectionlist.clear();
                        lastDate = "";
                        isDateSelect = true;
                        getMyCollection(mSharedPreferences.getString(Utils.USER_ID, ""), "custom", from_date, to_date, String.valueOf(currentPage), lastDate);

                    }
                } else {
                    Utils.showMessageDialog(getActivity(), getResources().getString(R.string.alert), "Please Select Before To Date");
                }
            } else {
                mFromDateView.setText(new StringBuilder().append(day).append("/").append(month).append("/").append(yy).append(" "));

                Utils.storeString(mSharedPreferences, Utils.FROM_DATE, mFromDateView.getText().toString());

                if (Utils.validateString(mFromDateView.getText().toString()) && Utils.validateString(mToDateView.getText().toString())) {
                    String fromString = mFromDateView.getText().toString();
                    String toString = mToDateView.getText().toString();
                    String from_date = fromString.substring(6, 10) + "-" + fromString.substring(3, 5) + "-" + fromString.substring(0, 2);
                    String to_date = toString.substring(6, 10) + "-" + toString.substring(3, 5) + "-" + toString.substring(0, 2);
                    currentPage = 1;
//					isDataFinished = false;
//					mTempCollectionlist.clear();
                    lastDate = "";
                    isDateSelect = true;
                    getMyCollection(mSharedPreferences.getString(Utils.USER_ID, ""), "custom", from_date, to_date, String.valueOf(currentPage), lastDate);

                }
            }
        }
    };

    DatePickerDialog.OnDateSetListener toDate = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int yy, int mm, int dd) {
            String day = String.valueOf(dd);
            String month = String.valueOf(mm + 1);
            if (day.trim().length() == 1)
                day = "0" + day;
            if (month.trim().length() == 1)
                month = "0" + month;
            String to = day + "/" + month + "/" + yy;
            String from = mFromDateView.getText().toString();
            boolean flag = CheckDates(from, to);

            if (flag == true) {
                mToDateView.setText(new StringBuilder().append(day).append("/").append(month).append("/").append(yy).append(" "));

                Utils.storeString(mSharedPreferences, Utils.TO_DATE, mToDateView.getText().toString());

                if (Utils.validateString(mFromDateView.getText().toString()) && Utils.validateString(mToDateView.getText().toString())) {
                    String fromString = mFromDateView.getText().toString();
                    String toString = mToDateView.getText().toString();
                    String from_date = fromString.substring(6, 10) + "-" + fromString.substring(3, 5) + "-" + fromString.substring(0, 2);
                    String to_date = toString.substring(6, 10) + "-" + toString.substring(3, 5) + "-" + toString.substring(0, 2);
                    currentPage = 1;
//					isDataFinished = false;
//					mTempCollectionlist.clear();
                    lastDate = "";
                    isDateSelect = true;
                    getMyCollection(mSharedPreferences.getString(Utils.USER_ID, ""), "custom", from_date, to_date, String.valueOf(currentPage), lastDate);
                }
            } else {
                Utils.showMessageDialog(getActivity(), getResources().getString(R.string.alert), "Please Select After From Date");
            }
        }
    };


    private void getMyCollection(String handyman_id, String mode, String start_date, String end_date, String page, String lastdate) {
        if (Utils.checkInternetConnection(getActivity())) {
            MyCollectionCustomRequestTask myCollectionCustomRequestTask = new MyCollectionCustomRequestTask(getActivity());
            myCollectionCustomRequestTask.setAsyncCallListener(new AsyncCallListener() {

                @SuppressWarnings("unchecked")
                @Override
                public void onResponseReceived(Object response) {
                    collectionModelsList.clear();

                    MyCollectionModelList myCollectionModel = (MyCollectionModelList) response;
                    if (myCollectionModel.getSuccess().equalsIgnoreCase("1")) {

                        Utils.storeString(mSharedPreferences, "custom_total", myCollectionModel.getTotalamount());

                        collectionModelsList.addAll(myCollectionModel.getCollectionModelsList());
                        Logger.e(TAG, "collectionModelsList SIZE -- " + collectionModelsList.size());

                        if (collectionModelsList.size() > 0) {
                            mCollactionListView.setVisibility(View.VISIBLE);
                            mRootView.findViewById(R.id.not_found_text).setVisibility(View.GONE);

//								for (int i = 0; i < collectionModelsList.size(); i++) {
//									count = collectionModelsList.get(i).getCount();
//								}
//								
//								Logger.e(TAG, "Count Custom Date Title : " + count);
                            if (isDateSelect) {
                                isDateSelect = false;
                                mTempCollectionlist.clear();
                            }
                            setView(collectionModelsList);
                            isLoading = false;
                            isDataFinished = false;
                            setProgressVisibility(View.GONE);
                            removeFooter();
                        }

                    } else if (myCollectionModel.getSuccess().equalsIgnoreCase("0")) {
                        flag = true;
                        isDataFinished = true;

                        if(currentPage == 1){
                            mTempCollectionlist.clear();
                        }

                        if (mTempCollectionlist.isEmpty() == true) {
                            Utils.storeString(mSharedPreferences, "custom_total", "");
                            mRootView.findViewById(R.id.total).setVisibility(View.GONE);
                            mCollactionListView.setVisibility(View.GONE);
                            mRootView.findViewById(R.id.not_found_text).setVisibility(View.VISIBLE);
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

                }

                @Override
                public void onErrorReceived(String error) {
                    Toast.makeText(getActivity(), getString(R.string.try_again), Toast.LENGTH_SHORT).show();
                }
            });
            myCollectionCustomRequestTask.execute(handyman_id, mode, start_date, end_date, page, lastdate);
        } else {
//            Utils.showMessageDialog(getActivity(), getResources().getString(R.string.alert),
//                    getResources().getString(R.string.connection));
        }
    }

    private void setView(ArrayList<MyCollectionModel> hiringAllList) {
        if (isAdded()) {
//			mTempCollectionlist.clear();
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

                if ((!mSharedPreferences.getString("custom_total", "").isEmpty())) {
                    mRootView.findViewById(R.id.total).setVisibility(View.VISIBLE);
                    ((TextView) mRootView.findViewById(R.id.total)).setText("TOTAL : " + mSharedPreferences.getString("custom_total", ""));
                }
            }
        }
    }

    OnClickListener onAcceptedClickListener = new OnClickListener() {

        @Override
        public void onClick(View v) {
            if (v.getTag() != null) {
                int index = (Integer) v.getTag();
                Logger.e(TAG, "COLLECTION CUSTOM CLICK INDEX ::" + index);
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
        int lastInScreen = firstVisibleItem + visibleItemCount;
        if (lastInScreen == totalItemCount && totalItemCount != 0 && !isLoading && !isDataFinished) {

            if (collectionModelsList.size() >= 10 && Utils.validateString(mFromDateView.getText().toString()) && Utils.validateString(mToDateView.getText().toString())) {
                isLoading = true;
                currentPage++;
                setProgressVisibility(View.VISIBLE);
                addFooter();
                lastDate = getLastDate();

                String fromString = mFromDateView.getText().toString();
                String toString = mToDateView.getText().toString();
                String from_date = fromString.substring(6, 10) + "-" + fromString.substring(3, 5) + "-" + fromString.substring(0, 2);
                String to_date = toString.substring(6, 10) + "-" + toString.substring(3, 5) + "-" + toString.substring(0, 2);

                getMyCollection(mSharedPreferences.getString(Utils.USER_ID, ""), "custom", from_date, to_date, String.valueOf(currentPage), lastDate);
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

	/*public class SelectFromDateFragment extends DialogFragment implements DatePickerDialog.OnDateSetListener {

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

//		    long maxDate = c.getTime().getTime();

			DatePickerDialog dpd = new DatePickerDialog(getActivity(),R.style.DialogTheme, this,year,month,day);
	        dpd.getDatePicker().setMaxDate(c.getTime().getTime());

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
//						mTempCollectionlist.clear();
						lastDate = "";
						isDateSelect = true;
						getMyCollection(mSharedPreferences.getString(Utils.USER_ID, ""),"custom",from_date,to_date,String.valueOf(currentPage),lastDate);

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
//					mTempCollectionlist.clear();
					lastDate = "";
					isDateSelect = true;
					getMyCollection(mSharedPreferences.getString(Utils.USER_ID, ""),"custom",from_date,to_date,String.valueOf(currentPage),lastDate);

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

//			long maxDate = c.getTime().getTime();

			DatePickerDialog dpd = new DatePickerDialog(getActivity(),R.style.DialogTheme, this,year,month,day);
	        dpd.getDatePicker().setMaxDate(c.getTime().getTime());

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
//					mTempCollectionlist.clear();
					lastDate = "";
					isDateSelect = true;
					getMyCollection(mSharedPreferences.getString(Utils.USER_ID, ""),"custom",from_date,to_date,String.valueOf(currentPage),lastDate);
				}
			} else {
				Utils.showMessageDialog(getActivity(), getResources().getString(R.string.alert), "Please Select After From Date");
			}

		}
	}*/

    public boolean CheckDates(String d1, String d2) {
        boolean b = false;
        try {
            if (dfDate.parse(d1).before(dfDate.parse(d2))) {
                b = true;//If start date is before end date
            } else if (dfDate.parse(d1).equals(dfDate.parse(d2))) {
                b = true;//If two dates are equal
            } else {
                b = false; //If start date is after the end date
            }
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return b;
    }


    @Override
    public void onRefresh() {
        swipeRefreshLayout.setRefreshing(false);
    }


}
