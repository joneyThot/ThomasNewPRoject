package com.handy.fragment;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Toast;

import com.handy.MainActivity;
import com.handy.R;
import com.handy.adapter.MyCollectionFragmentPagerAdapter;
import com.handy.adapter.RequestStatusPagerAdapter;
import com.handy.service.Utils;
import com.handy.view.TitlePageIndicator;
import com.handy.view.TitlePageIndicator.OnCenterItemClickListener;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;

import org.json.JSONException;
import org.json.JSONObject;

public class RequestStatusFragment extends BaseFragment implements OnClickListener, OnCenterItemClickListener {

    private static String TAG = "RequestStatusFragment";
    private SharedPreferences mSharedPreferences;

    Fragment fr;
    View mRootView;
    ViewPager mPager;
    TitlePageIndicator mIndicator;
    RequestStatusPagerAdapter mRequestStatusPagerAdapter;
    String is_requested = "";


    @Override
    public void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        this.mRequestStatusPagerAdapter = new RequestStatusPagerAdapter(getChildFragmentManager());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mRootView = inflater.inflate(R.layout.fragment_my_collection, container, false);

        return mRootView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        initview();
        super.onViewCreated(view, savedInstanceState);

    }

    private void initview() {
        mSharedPreferences = getActivity().getSharedPreferences(Utils.PREF_NAME, Context.MODE_PRIVATE);
        ((MainActivity) getActivity()).setTitleText(getString(R.string.menu_request_status), "", View.GONE, View.VISIBLE, View.GONE);
        getActivity().findViewById(R.id.title_back).setVisibility(View.GONE);

        mActivity.getSlidingMenu().setMode(SlidingMenu.LEFT);
        mActivity.getSlidingMenu().setTouchModeAbove(SlidingMenu.TOUCHMODE_NONE);

        mPager = (ViewPager) mRootView.findViewById(R.id.pager_collection);
        mIndicator = (TitlePageIndicator) mRootView.findViewById(R.id.indicator_collection);


        if (getArguments() != null) {
            String result = getArguments().getString("resposne");
            JSONObject jObj = null;
            try {
                jObj = new JSONObject(result.toString());
                if (!jObj.isNull("data")) {
                    JSONObject data_jobj = jObj.getJSONObject("data");
                    is_requested = data_jobj.getString("is_requested");
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }

//        Utils.storeString(mSharedPreferences,TAG,TAG);
        SlidingMenuFragment.selectMenu(5);

        if (is_requested.equalsIgnoreCase("pending")) {
            mPager.setAdapter(mRequestStatusPagerAdapter);
            mIndicator.setViewPager(mPager);
            mIndicator.setCurrentItem(0);
        } else if (is_requested.equalsIgnoreCase("accepted")) {
            mPager.setAdapter(mRequestStatusPagerAdapter);
            mIndicator.setViewPager(mPager);
            mIndicator.setCurrentItem(1);
        } else if (is_requested.equalsIgnoreCase("completed")) {
            mPager.setAdapter(mRequestStatusPagerAdapter);
            mIndicator.setViewPager(mPager);
            mIndicator.setCurrentItem(2);
        } else if (is_requested.equalsIgnoreCase("cancelled")) {
            mPager.setAdapter(mRequestStatusPagerAdapter);
            mIndicator.setViewPager(mPager);
            mIndicator.setCurrentItem(3);
        } else {
            mPager.setAdapter(mRequestStatusPagerAdapter);
            mIndicator.setViewPager(mPager);
        }

    }


    @Override
    public void onClick(View v) {
        // TODO Auto-generated method stub
        switch (v.getId()) {
        }
    }

    @Override
    public void onCenterItemClick(int position) {
        Toast.makeText(getActivity(), "You clicked :: " + position, Toast.LENGTH_SHORT).show();
    }

}