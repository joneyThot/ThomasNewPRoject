package com.handy.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.handy.fragment.AcceptFragment;
import com.handy.fragment.AllFragment;
import com.handy.fragment.CancelledFragment;
import com.handy.fragment.CompletedFragment;
import com.handy.fragment.CustomlyFragment;
import com.handy.fragment.DayFragment;
import com.handy.fragment.MonthlyFragment;
import com.handy.fragment.PendingFragment;
import com.handy.fragment.RejectedFragment;
import com.handy.fragment.StartJobFragment;
import com.handy.fragment.WeeklyFragment;

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
