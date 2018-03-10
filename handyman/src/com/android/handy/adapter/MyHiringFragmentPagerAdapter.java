package com.android.handy.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.android.handy.fragment.AcceptFragment;
import com.android.handy.fragment.AllFragment;
import com.android.handy.fragment.CancelledFragment;
import com.android.handy.fragment.CompletedFragment;
import com.android.handy.fragment.PendingFragment;
import com.android.handy.fragment.RejectedFragment;
import com.android.handy.fragment.StartJobFragment;

public class MyHiringFragmentPagerAdapter extends FragmentPagerAdapter {
	private static String TAG = "MyHiringFragmentPagerAdapter";

//	protected static final String[] CONTENT = new String[] {
//			"     Upcomming Orders     ", "     Accepted Orders     ", "     Start Orders     ", "     Cancelled Orders    ",
//			"    Compeleted Orders    ", "    Rejected Orders    ", "    All Orders    ", };
	
	protected static final String[] CONTENT = new String[] {"Upcoming", "Accepted", "Start", "Cancelled", "Completed","Rejected","All",};
	public int mCount = CONTENT.length;

	public MyHiringFragmentPagerAdapter(FragmentManager fm) {
		super(fm);
	}

	@Override
	public Fragment getItem(int position) {
		switch (position) {
        case 0: 
            return PendingFragment.newInstance(0, "Page # 1");
        case 1: 
            return AcceptFragment.newInstance(1, "Page # 2");
        case 2: 
            return StartJobFragment.newInstance(2, "Page # 3");
        case 3: 
            return CancelledFragment.newInstance(3, "Page # 4");
        case 4: 
            return CompletedFragment.newInstance(4, "Page # 5");
        case 5: 
            return RejectedFragment.newInstance(5, "Page # 6");
        case 6: 
            return AllFragment.newInstance(6, "Page # 7");
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
		return MyHiringFragmentPagerAdapter.CONTENT[position % CONTENT.length];
	}

	public void setCount(int count) {
        if (count > 0 && count <= 10) {
            mCount = count;
            notifyDataSetChanged();
        }
    }
}
