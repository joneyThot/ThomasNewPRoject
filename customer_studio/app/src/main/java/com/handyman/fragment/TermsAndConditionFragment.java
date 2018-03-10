package com.handyman.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

import com.handyman.MainActivity;
import com.handyman.R;
import com.handyman.service.Utils;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;

public class TermsAndConditionFragment extends BaseFragment implements OnClickListener{
	
	Fragment fr;
	View mRootView;
	WebView webView;
	ProgressBar prog;
	String mUrl = "";

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,	Bundle savedInstanceState) {
		mRootView = inflater.inflate(R.layout.row_termas_and_condition, container, false);
		initview();
		return mRootView;
	}

	private void initview() {
		((MainActivity) getActivity()).setTitleText(getString(R.string.menu_terms_and_conditions), "", "", View.GONE, View.VISIBLE, View.GONE,View.GONE, View.GONE);
		getActivity().findViewById(R.id.title_back).setVisibility(View.GONE);
		getActivity().findViewById(R.id.txtUrl).setVisibility(View.GONE);
		
		mRootView.findViewById(R.id.livTop).setVisibility(View.GONE);
		
		mActivity.getSlidingMenu().setMode(SlidingMenu.LEFT);
		mActivity.getSlidingMenu().setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);

		SlidingMenuFragment.selectMenu(13);

		webView = (WebView) mRootView.findViewById(R.id.webview);
		prog = (ProgressBar) mRootView.findViewById(R.id.prog);
		prog.setVisibility(View.VISIBLE);
		mUrl = Utils.URL_SERVER_ADDRESS + Utils.TERMS_AND_CONDITION;
		webView.setWebViewClient(new WebViewClient(){
			@Override
			public void onPageFinished(WebView view, String url) {
				super.onPageFinished(view, url);
				mUrl = url;
				prog.setVisibility(View.GONE);
			}
		});

		webView.loadUrl(mUrl);
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
	}
	
	
	
	
}
