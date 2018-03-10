package com.handyman.fragment;

import java.util.ArrayList;

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

import com.handyman.MainActivity;
import com.handyman.R;
import com.handyman.adapter.MyHiringFragmentPagerAdapter;
import com.handyman.model.MyHiringsModel;
import com.handyman.service.Utils;
import com.handyman.view.TitlePageIndicator;
import com.handyman.view.TitlePageIndicator.OnCenterItemClickListener;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;

public class MyHiringsFragment extends BaseFragment implements OnClickListener, OnCenterItemClickListener {

    private static String TAG = "MyHiringsFragment";
    private ArrayList<MyHiringsModel> mMyHiringsList = new ArrayList<MyHiringsModel>();
    private SharedPreferences mSharedPreferences;

    Fragment fr;
    View mRootView;
    ViewPager mPager;
    TitlePageIndicator mIndicator;
    //	MyHiringPagerAdapter_new myHiringPagerAdapter_new;
    MyHiringFragmentPagerAdapter myHiringFragmentPagerAdapter;
    String complete = "", pending = "", cancel = "", start = "", active = "",declined = "";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        this.myHiringFragmentPagerAdapter = new MyHiringFragmentPagerAdapter(getChildFragmentManager());
    }

//	@Override
//	public void onStart() {
//		// TODO Auto-generated method stub
//		super.onStart();
//		IntentFilter intentFilter = new IntentFilter(Utils.DISPLAY_MESSAGE_ACTION);
//		getActivity().registerReceiver(receiver, intentFilter);
//	}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mRootView = inflater.inflate(R.layout.fragment_my_hirings, container, false);

        return mRootView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        initview();
        super.onViewCreated(view, savedInstanceState);

    }

    @SuppressWarnings("deprecation")
    private void initview() {
        mSharedPreferences = getActivity().getSharedPreferences(Utils.PREF_NAME, Context.MODE_PRIVATE);
        ((MainActivity) getActivity()).setTitleText(getString(R.string.menu_my_hirings), "", "", View.GONE, View.VISIBLE, View.GONE,View.GONE, View.GONE);
        getActivity().findViewById(R.id.title_back).setVisibility(View.GONE);
        getActivity().findViewById(R.id.txtUrl).setVisibility(View.GONE);

        mActivity.getSlidingMenu().setMode(SlidingMenu.LEFT);
        mActivity.getSlidingMenu().setTouchModeAbove(SlidingMenu.TOUCHMODE_NONE);

        mPager = (ViewPager) mRootView.findViewById(R.id.pager);
        mIndicator = (TitlePageIndicator) mRootView.findViewById(R.id.indicator);

//		mPager.setAdapter(new MyHiringPagerAdapter_new(getActivity()));
//		mIndicator.setViewPager(mPager);
//		mIndicator.setCurrentItem(0);

        SlidingMenuFragment.selectMenu(2);

        if (getArguments() != null) {
            pending = getArguments().getString("Pending");
            active = getArguments().getString("Active");
            start = getArguments().getString("Start");
            cancel = getArguments().getString("Cancel");
            complete = getArguments().getString("Complete");
            declined = getArguments().getString("declined");
        }

        if (Utils.validateString(pending)) {
            mPager.setAdapter(myHiringFragmentPagerAdapter);
            mIndicator.setViewPager(mPager);
            mIndicator.setCurrentItem(0);
        } else if (Utils.validateString(active)) {
            mPager.setAdapter(myHiringFragmentPagerAdapter);
            mIndicator.setViewPager(mPager);
            mIndicator.setCurrentItem(1);
        } else if (Utils.validateString(start)) {
            mPager.setAdapter(myHiringFragmentPagerAdapter);
            mIndicator.setViewPager(mPager);
            mIndicator.setCurrentItem(2);
        } else if (Utils.validateString(cancel)) {
            mPager.setAdapter(myHiringFragmentPagerAdapter);
            mIndicator.setViewPager(mPager);
            mIndicator.setCurrentItem(3);
        } else if (Utils.validateString(complete)) {
            mPager.setAdapter(myHiringFragmentPagerAdapter);
            mIndicator.setViewPager(mPager);
            mIndicator.setCurrentItem(4);
        } else if(Utils.validateString(declined)){
            mPager.setAdapter(myHiringFragmentPagerAdapter);
            mIndicator.setViewPager(mPager);
            mIndicator.setCurrentItem(5);
        } else {
            mPager.setAdapter(myHiringFragmentPagerAdapter);
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