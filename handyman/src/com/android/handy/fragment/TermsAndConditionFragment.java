package com.android.handy.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.webkit.WebView;

import com.android.handy.MainActivity;
import com.android.handy.R;
import com.android.handy.service.Utils;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;

public class TermsAndConditionFragment extends BaseFragment implements OnClickListener{
	
	Fragment fr;
	View mRootView;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,	Bundle savedInstanceState) {
		mRootView = inflater.inflate(R.layout.row_termas_and_condition, container, false);
		initview();
		return mRootView;
	}

	private void initview() {
		((MainActivity) getActivity()).setTitleText(getString(R.string.menu_terms_and_conditions), "", View.GONE, View.VISIBLE, View.GONE);
		getActivity().findViewById(R.id.title_back).setVisibility(View.GONE);	
		
		mRootView.findViewById(R.id.titleLayout).setVisibility(View.GONE);
		
		mActivity.getSlidingMenu().setMode(SlidingMenu.LEFT);
		mActivity.getSlidingMenu().setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);
		
		((WebView) mRootView.findViewById(R.id.webview)).loadUrl(Utils.URL_SERVER_ADDRESS + Utils.TERMS_AND_CONDITION);
		
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
	}
	
	
	
	
}
