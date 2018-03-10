package com.android.handy;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentManager.OnBackStackChangedListener;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;
import android.widget.Toast;

import com.android.handy.fragment.MyHiringsFragment_new;
import com.android.handy.fragment.PushnotificationCustomerCompleteFragment;
import com.android.handy.fragment.SlidingMenuFragment;
import com.android.handy.logger.Logger;
import com.android.handy.service.GPSTracker;
import com.android.handy.service.Utils;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu.OnOpenListener;

@SuppressLint("NewApi") public class MainActivity extends BaseActivity implements OnClickListener{
	
	private String From = "";
	
	public MainActivity() 
	{
		super(R.string.app_name);
	}
	
	private static String TAG = "MainActivity";
	SharedPreferences sharedPreferences;
	TextView title;
	Fragment fr = null;
	boolean locPres=false;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		getActionBar().hide();
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
//		stopService(new Intent(MainActivity.this, ServiceTest.class));
		startService(new Intent(MainActivity.this, ServiceTest.class));
		
		GPSTracker gpsTracker = Utils.getCurrentLocation(this);
		if (gpsTracker != null) {
			SplashActivity.latitude = gpsTracker.getLatitude();
			SplashActivity.longitude = gpsTracker.getLongitude();
			Logger.e(TAG, "GPSTracker -- "+ SplashActivity.latitude + " & " + SplashActivity.longitude);
			
		} else {
			Toast.makeText(getApplicationContext(), "Please On GPS" , Toast.LENGTH_SHORT).show();
		}
		
		initview();
		drawermenuFunction();
	}

	private void initview() {
		sharedPreferences = getSharedPreferences(Utils.PREF_NAME, MODE_PRIVATE);
		getSlidingMenu().setMode(SlidingMenu.LEFT);
		getSlidingMenu().setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);

		findViewById(R.id.title).setVisibility(View.VISIBLE);
//		findViewById(R.id.search_bar).setVisibility(View.VISIBLE);
		findViewById(R.id.iconBtn).setVisibility(View.VISIBLE);
		title = (TextView)findViewById(R.id.title);
		//title.setText(R.string.profile_txt);

		findViewById(R.id.iconBtn).setOnClickListener(this);
		findViewById(R.id.menuBtn).setOnClickListener(this);
		findViewById(R.id.backBtn).setOnClickListener(this);
		
		Bundle bundle = getIntent().getExtras();
		if(bundle != null){
			From = bundle.getString("FROM");
			if(From.equalsIgnoreCase("SplashActivity")){
					getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, new MyHiringsFragment_new()).commit();
			} else if(From.equalsIgnoreCase("LOGIN")){
					getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, new MyHiringsFragment_new()).commit();
			}else if(From.equalsIgnoreCase("GCMIntentService")){
//				getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, new  MyHiringsFragment_new()).addToBackStack(TAG).commit();
				
				PushnotificationCustomerCompleteFragment pushnotificationCustomerCompleteFragment = new PushnotificationCustomerCompleteFragment();
				getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, pushnotificationCustomerCompleteFragment).commit();
			}
		}

	}
	
	public void setTitleText(String title,String title_back, int backbtn, int iconbtn, int menubtn) {
		findViewById(R.id.titleLayout).setVisibility(View.VISIBLE);
		((TextView) findViewById(R.id.title)).setText(title);	
		((TextView) findViewById(R.id.title_back)).setText(title_back);	
		findViewById(R.id.title).setVisibility(View.VISIBLE);
		findViewById(R.id.title_back).setVisibility(View.VISIBLE);
		findViewById(R.id.backBtn).setVisibility(backbtn);
		findViewById(R.id.iconBtn).setVisibility(iconbtn);
		findViewById(R.id.menuBtn).setVisibility(menubtn);
	}

	public void hideTitleLayout() {
		findViewById(R.id.backBtn).setVisibility(View.GONE);
		findViewById(R.id.iconBtn).setVisibility(View.GONE);
		findViewById(R.id.title).setVisibility(View.GONE);
		findViewById(R.id.title_back).setVisibility(View.GONE);
		findViewById(R.id.menuBtn).setVisibility(View.GONE);
	}
	
	public void hideTitleRelativeLayout() {
		findViewById(R.id.titleLayout).setVisibility(View.GONE);
	}

//	public void setActionbarTitle(String title, int Visibilty) {
//		((TextView) getActionBar().getCustomView().findViewById(R.id.title)).setText(title);
//		((ImageView) getActionBar().getCustomView().findViewById(R.id.iconBtn)).setVisibility(Visibilty);
//		((ImageView) getActionBar().getCustomView().findViewById(R.id.menuBtn)).setVisibility(Visibilty);
//	}
	
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.iconBtn:
			showMenu();
			
			Utils.storeString(sharedPreferences, Utils.FROM_DATE, "");
			Utils.storeString(sharedPreferences, Utils.TO_DATE, "");
			break;
			
		case R.id.backBtn:
			onBackPressed();
			break;
			
		case R.id.menuBtn:
			break;
//		
		}
	}
	
	private void drawermenuFunction()
	{
		getSlidingMenu().setOnOpenListener(new OnOpenListener() {
			@Override
			public void onOpen() {
				SlidingMenuFragment.viewSelectedPosition();
			}
		});
	}
	
//	@Override
//	public boolean onCreateOptionsMenu(Menu menu) {
//	    return super.onCreateOptionsMenu(menu);
//	}
//	
//	@Override
//	public boolean onMenuItemSelected(int featureId, MenuItem item) {
//
//		int itemId = item.getItemId();
//		switch (itemId) {
//		case android.R.id.home:
//			Logger.e("", "clicked");
//			showMenu();
//			break;
//		}
//		return true;
//	}


	@Override
	public void onBackPressed() {
		hideSoftKeyboard();
		getSupportFragmentManager().addOnBackStackChangedListener(getListener());
		if (getSlidingMenu().isMenuShowing()) {
			getSlidingMenu().toggle();
		}
		else {
			super.onBackPressed();
		}
	}

	private OnBackStackChangedListener getListener() {
		hideSoftKeyboard();
		OnBackStackChangedListener result = new OnBackStackChangedListener()
		{
			public void onBackStackChanged() 
			{                   
				FragmentManager fm = MainActivity.this.getSupportFragmentManager();
				Fragment fragment = null;

				if (fm != null)
				{
					fragment = (Fragment)fm.findFragmentById(R.id.content_frame);
					fragment.onResume();
				}                   
			}
		};

		return result;
	}
	
	public void hideSoftKeyboard() {
		if (getCurrentFocus() != null) {
			InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
			inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
		}
	}

}
