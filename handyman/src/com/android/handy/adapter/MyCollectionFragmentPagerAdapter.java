package com.android.handy.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.android.handy.fragment.AcceptFragment;
import com.android.handy.fragment.AllFragment;
import com.android.handy.fragment.CancelledFragment;
import com.android.handy.fragment.CompletedFragment;
import com.android.handy.fragment.CustomlyFragment;
import com.android.handy.fragment.DayFragment;
import com.android.handy.fragment.MonthlyFragment;
import com.android.handy.fragment.PendingFragment;
import com.android.handy.fragment.RejectedFragment;
import com.android.handy.fragment.StartJobFragment;
import com.android.handy.fragment.WeeklyFragment;

public class MyCollectionFragmentPagerAdapter extends FragmentPagerAdapter {
	private static String TAG = "MyCollectionFragmentPagerAdapter";

	protected static final String[] CONTENT = new String[] {"Day", "Weekly", "Monthly", "Custom",};
	 private int mCount = CONTENT.length;

	public MyCollectionFragmentPagerAdapter(FragmentManager fm) {
		super(fm);
	}

	@Override
	public Fragment getItem(int position) {
		switch (position) {
        case 0: 
            return DayFragment.newInstance(0, "Page # 1");
        case 1: 
        	 return WeeklyFragment.newInstance(1, "Page # 2");
        case 2: 
        	 return MonthlyFragment.newInstance(2, "Page # 3");
        case 3: 
        	 return CustomlyFragment.newInstance(3, "Page # 4");
        default:
            return null;
        }
	}

	@Override
	public int getCount() {
		return mCount;
	}

	@Override
	public CharSequence getPageTitle(int position) {
		return MyCollectionFragmentPagerAdapter.CONTENT[position % CONTENT.length];
	}

	public void setCount(int count) {
        if (count > 0 && count <= 10) {
            mCount = count;
            notifyDataSetChanged();
        }
    }
}
