package com.handyman;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.handyman.model.TermsAndConditionModel;
import com.handyman.service.Utils;

public class PrivacyPolicyActivity extends Activity implements OnClickListener {

	private static String TAG = "PrivacyPolicyActivity";
	private SharedPreferences mSharedPreferences;
//	TermsAndConditionModel termsAndConditionModel = new TermsAndConditionModel();
	WebView webView;
	ProgressBar prog;
	String mUrl = "";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		int currentapiVersion = Build.VERSION.SDK_INT;
		if(currentapiVersion < Build.VERSION_CODES.LOLLIPOP) {
			getActionBar().hide();
		}
		setContentView(R.layout.row_termas_and_condition);

		mSharedPreferences = getSharedPreferences(Utils.PREF_NAME, MODE_PRIVATE);
		initView();

//		onTermsAndCondition(String.valueOf(6));
	}

	private void initView() {

		((ImageView) findViewById(R.id.backBtn)).setVisibility(View.VISIBLE);
		((ImageView) findViewById(R.id.backBtn)).setOnClickListener(this);
		findViewById(R.id.title_back).setVisibility(View.VISIBLE);
		((TextView) findViewById(R.id.title_back)).setText(getString(R.string.menu_privacy_policy));
		webView = (WebView)findViewById(R.id.webview);

		prog = (ProgressBar) findViewById(R.id.prog);
		prog.setVisibility(View.VISIBLE);
		mUrl = Utils.URL_SERVER_ADDRESS + Utils.PRIVACY_POLICY;
		webView.setWebViewClient(new WebViewClient(){
			@Override
			public void onPageFinished(WebView view, String url) {
				super.onPageFinished(view, url);
				mUrl = url;
				prog.setVisibility(View.GONE);
			}
		});

		webView.loadUrl(mUrl);
//		webView.loadUrl(Utils.URL_SERVER_ADDRESS + Utils.PRIVACY_POLICY);
	}

	@Override
	public void onClick(View v) {
		
		switch (v.getId()) {
		case R.id.backBtn:
			finish();
			break;
			
		}
	}
	
/*private void onTermsAndCondition(String id) {
		
        if (Utils.checkInternetConnection(this)) {
        	GetTermsAndConditionRequestTask termsAndConditionRequestTask = new GetTermsAndConditionRequestTask(this);
        	termsAndConditionRequestTask.setAsyncCallListener(new AsyncCallListener() {

                @SuppressWarnings("unchecked")
				@Override
                public void onResponseReceived(Object response) {
                	TermsAndConditionModel termsAndConditionModels = (TermsAndConditionModel) response;
                	String yourData = termsAndConditionModels.id;
                	webView.loadData(yourData, "text/html", "UTF-8");
                }
                @Override
                public void onErrorReceived(String error) {
                    Toast.makeText(TermsAndConditionActivity.this, getString(R.string.try_again), Toast.LENGTH_SHORT).show();
                }
            });
        	termsAndConditionRequestTask.execute(id);
        } else {
            Toast.makeText(TermsAndConditionActivity.this, getString(R.string.connection), Toast.LENGTH_SHORT).show();
        }
    }

	public String stripHtml(String html) {
		return Html.fromHtml(html).toString();
	}*/
	
	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		super.onBackPressed();
		finish();
	}
}
