package com.handy.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.handy.fragment.ApprovedRequestStatusFragment;
import com.handy.fragment.CancelRequestStatusFragment;
import com.handy.fragment.CompleteRequestStatusFragment;
import com.handy.fragment.CustomlyFragment;
import com.handy.fragment.DayFragment;
import com.handy.fragment.MonthlyFragment;
import com.handy.fragment.PendingRequestStatusFragment;
import com.handy.fragment.WeeklyFragment;

public class RequestStatusPagerAdapter extends FragmentPagerAdapter {
    private static String TAG = "RequestStatusPagerAdapter";

    protected static final String[] CONTENT = new String[]{"Pending", "Accepted", "Completed", "Cancelled",};
    private int mCount = CONTENT.length;

    public RequestStatusPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return PendingRequestStatusFragment.newInstance(0, "Page # 1");
            case 1:
                return PendingRequestStatusFragment.newInstance(1, "Page # 2");
            case 2:
                return PendingRequestStatusFragment.newInstance(2, "Page # 3");
            case 3:
                return PendingRequestStatusFragment.newInstance(3, "Page # 4");
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
        return RequestStatusPagerAdapter.CONTENT[position % CONTENT.length];
    }

    public void setCount(int count) {
        if (count > 0 && count <= 10) {
            mCount = count;
            notifyDataSetChanged();
        }
    }
}
