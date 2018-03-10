package com.android.handyman;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentManager.OnBackStackChangedListener;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;
import android.widget.Toast;

import com.android.handyman.fragment.PushnotificationCustomerCompleteFragment;
import com.android.handyman.fragment.ServiceAtHomeFragment;
import com.android.handyman.fragment.SlidingMenuFragment;
import com.android.handyman.service.GPSTracker;
import com.android.handyman.service.Utils;
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
	boolean doubleBackToExitPressedOnce = false;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		getActionBar().hide();
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		GPSTracker gpsTracker = Utils.getCurrentLocation(this);
		if (gpsTracker != null) {
			SplashActivity.latitude = gpsTracker.getLatitude();
			SplashActivity.longitude = gpsTracker.getLongitude();
//			Logger.e(TAG, "GPSTracker -- "+ SplashActivity.latitude + " & " + SplashActivity.longitude);
			
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
		findViewById(R.id.home_txt).setOnClickListener(this);
		
		Bundle bundle = getIntent().getExtras();
		if(bundle != null){
			From = bundle.getString("FROM");
			if(From.equalsIgnoreCase("SplashActivity")){
					getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, new ServiceAtHomeFragment()).commit();
			} else if(From.equalsIgnoreCase("LOGIN")){
					getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, new ServiceAtHomeFragment()).commit();
			} else if(From.equalsIgnoreCase("GCMIntentService")){
//				getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, new  MyHiringsFragment_new()).addToBackStack(null).commit();
				
				PushnotificationCustomerCompleteFragment pushnotificationCustomerCompleteFragment = new PushnotificationCustomerCompleteFragment();
				getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, pushnotificationCustomerCompleteFragment).commit();
			}
		}

	}
	
	public void setTitleText(String title,String title_back, int backbtn, int iconbtn, int menubtn, int hometxt) {
		findViewById(R.id.titleLayout).setVisibility(View.VISIBLE);
		((TextView) findViewById(R.id.title)).setText(title);	
		((TextView) findViewById(R.id.title_back)).setText(title_back);	
		findViewById(R.id.title).setVisibility(View.VISIBLE);
		findViewById(R.id.title_back).setVisibility(View.VISIBLE);
		findViewById(R.id.backBtn).setVisibility(backbtn);
		findViewById(R.id.iconBtn).setVisibility(iconbtn);
		findViewById(R.id.menuBtn).setVisibility(menubtn);
		findViewById(R.id.home_txt).setVisibility(hometxt);
	}

	public void hideTitleLayout() {
		findViewById(R.id.backBtn).setVisibility(View.GONE);
		findViewById(R.id.iconBtn).setVisibility(View.GONE);
		findViewById(R.id.title).setVisibility(View.GONE);
		findViewById(R.id.title_back).setVisibility(View.GONE);
		findViewById(R.id.menuBtn).setVisibility(View.GONE);
		findViewById(R.id.home_txt).setVisibility(View.GONE);
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
			break;
			
		case R.id.backBtn:
			onBackPressed();
			break;
			
		case R.id.menuBtn:
			break;
			
		case R.id.home_txt:
			getSupportFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
//            getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, new ServiceAtHomeFragment()).commit();
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
//		 if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
			 getSupportFragmentManager().addOnBackStackChangedListener(getListener());
				if (getSlidingMenu().isMenuShowing()) {
					getSlidingMenu().toggle();
				} else {
					super.onBackPressed();
				}
//		    } else if (!doubleBackToExitPressedOnce) {
//		        this.doubleBackToExitPressedOnce = true;
//		        Toast.makeText(this,"Please click BACK again to exit.", Toast.LENGTH_SHORT).show();
//
//		        new Handler().postDelayed(new Runnable() {
//
//		            @Override
//		            public void run() {
//		                doubleBackToExitPressedOnce = false;
//		            }
//		        }, 2000);
//		    } else {
//		        super.onBackPressed();
//		        return;
//		    }

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
