package com.android.handyman;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.handyman.model.TermsAndConditionModel;
import com.android.handyman.service.Utils;

public class TermsAndConditionActivity extends Activity implements OnClickListener {

	private static String TAG = "TermsAndConditionActivity";
	private SharedPreferences mSharedPreferences;
	TermsAndConditionModel termsAndConditionModel = new TermsAndConditionModel();
	WebView webView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		getActionBar().hide();
		setContentView(R.layout.row_termas_and_condition);

		mSharedPreferences = getSharedPreferences(Utils.PREF_NAME, MODE_PRIVATE);
		initView();

//		onTermsAndCondition(String.valueOf(6));
	}

	private void initView() {

		((ImageView) findViewById(R.id.backBtn)).setVisibility(View.VISIBLE);
		((ImageView) findViewById(R.id.backBtn)).setOnClickListener(this);
		findViewById(R.id.title_back).setVisibility(View.VISIBLE);
		((TextView) findViewById(R.id.title_back)).setText(getString(R.string.menu_terms_and_conditions));
		webView = (WebView)findViewById(R.id.webview);
		webView.loadUrl(Utils.URL_SERVER_ADDRESS + Utils.TERMS_AND_CONDITION);
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
