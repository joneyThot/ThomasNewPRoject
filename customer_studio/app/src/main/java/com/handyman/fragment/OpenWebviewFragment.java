package com.handyman.fragment;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.handyman.MainActivity;
import com.handyman.R;
import com.handyman.model.AdvertiseListModel;
import com.handyman.service.Utils;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;

public class OpenWebviewFragment extends BaseFragment implements OnClickListener {

    Fragment fr;
    View mRootView;
    WebView webView;
    ProgressBar prog;
    //    ImageView backBtn;
//    TextView title_back;
    String mUrl = "";
    private SharedPreferences mSharedPreferences;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mRootView = inflater.inflate(R.layout.row_termas_and_condition, container, false);
        initview();
        return mRootView;
    }

    private void initview() {

        if (getArguments() != null) {
            mUrl = getArguments().getString(Utils.URL);
        }

        mSharedPreferences = getActivity().getSharedPreferences(Utils.PREF_NAME, Context.MODE_PRIVATE);
        ((MainActivity) getActivity()).setTitleText("", "", mUrl, View.GONE, View.GONE, View.GONE, View.GONE, View.VISIBLE);
        getActivity().findViewById(R.id.title).setVisibility(View.GONE);
        getActivity().findViewById(R.id.title_back).setVisibility(View.GONE);
        ((TextView)getActivity().findViewById(R.id.txtUrl)).setPaintFlags(Paint.UNDERLINE_TEXT_FLAG);

        mRootView.findViewById(R.id.livTop).setVisibility(View.GONE);
        mActivity.getSlidingMenu().setMode(SlidingMenu.LEFT);
        mActivity.getSlidingMenu().setTouchModeAbove(SlidingMenu.TOUCHMODE_NONE);

//        backBtn = (ImageView) mRootView.findViewById(R.id.backBtn);
//        backBtn.setOnClickListener(this);
//        title_back = (TextView) mRootView.findViewById(R.id.title_back);

        webView = (WebView) mRootView.findViewById(R.id.webview);
        prog = (ProgressBar) mRootView.findViewById(R.id.prog);

//        title_back.setText(mUrl);
//        title_back.setPaintFlags(Paint.UNDERLINE_TEXT_FLAG);
        prog.setVisibility(View.VISIBLE);

        webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                mUrl = url;
                prog.setVisibility(View.GONE);
            }
        });

        webView.setWebChromeClient(new WebChromeClient());
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setLoadWithOverviewMode(true);
        webView.getSettings().setUseWideViewPort(true);
        webView.getSettings().setBuiltInZoomControls(true);
        webView.getSettings().setSupportZoom(true);
        webView.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);

        webView.loadUrl(mUrl);
    }

    @Override
    public void onClick(View v) {
        // TODO Auto-generated method stub
        switch (v.getId()) {
            case R.id.backBtn:
//                FragmentManager fm = getActivity().getSupportFragmentManager();
//                for (int i = 0; i < fm.getBackStackEntryCount(); ++i) {
//                    fm.popBackStack();
//                }
                break;
        }
    }

}
