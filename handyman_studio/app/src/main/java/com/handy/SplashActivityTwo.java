package com.handy;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.View;
import android.view.View.OnClickListener;

import com.handy.adapter.SliderPagerAdapter;
import com.handy.service.Utils;
import com.handy.view.CirclePageIndicator;

public class SplashActivityTwo extends Activity implements OnClickListener {

	SharedPreferences mSharedPreferences;
	private static String TAG = "SplashActivityTwo";
	ViewPager mViewPager;
	CirclePageIndicator mCirclePageIndicator;
	SliderPagerAdapter sliderPagerAdapter;
//	public static double latitude = 0, longitude = 0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		int currentapiVersion = Build.VERSION.SDK_INT;
		if(currentapiVersion < Build.VERSION_CODES.LOLLIPOP) {
			getActionBar().hide();
		}
		setContentView(R.layout.splash_two);

//		if (currentapiVersion > Build.VERSION_CODES.LOLLIPOP){
//			// Do something for lollipop and above versions
//			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//				if ((this.checkSelfPermission(android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) ||
//						(this.checkSelfPermission(android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) ||
//						(this.checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)) {
//
//					requestPermissions(new String[]{android.Manifest.permission.ACCESS_COARSE_LOCATION, android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.WRITE_EXTERNAL_STORAGE},
//							0);
//				}
//			}
//		}
		
//		GPSTracker gpsTracker = Utils.getCurrentLocation(this);
//		if (gpsTracker != null) {
//			latitude = gpsTracker.getLatitude();
//			longitude = gpsTracker.getLongitude();
//			Logger.e("", "GPSTracker -- "+ latitude + " & " +longitude);
//		}
		
	/*	SingleShotLocationProvider.requestSingleUpdate(this, new SingleShotLocationProvider.LocationCallback() {
			@Override public void onNewLocationAvailable(GPSCoordinates location) {
				//				currentLocation = new Location("");
				//				currentLocation.setLatitude(location.latitude);
				//				currentLocation.setLongitude(location.longitude);

				latitude = location.latitude;
				longitude = location.longitude;

				Logger.e("", "SingleShotLocationProvider -- "+ latitude + " & " +longitude);
			}
		});
		
		LocationResult locationResult = new LocationResult(){
			@Override
			public void gotLocation(Location location){
				//Got the location!
				//		    	currentLocation = location;
				if(location != null) {
					latitude = location.getLatitude();
					longitude = location.getLongitude();
					Logger.e("", "MyLocation -- "+ latitude + " & " +longitude);
				}
			}
		};
		MyLocation myLocation = new MyLocation();
		myLocation.getLocation(this, locationResult);*/


		mSharedPreferences = getSharedPreferences(Utils.PREF_NAME, MODE_PRIVATE);
//		startAnimations();
		initView();
	}

	private void initView() {

		findViewById(R.id.loginbtn).setOnClickListener(this);
		findViewById(R.id.loginbtn).setVisibility(View.GONE);
		findViewById(R.id.skip_text).setOnClickListener(this);
		mViewPager = (ViewPager) findViewById(R.id.viewpager);
		mCirclePageIndicator = (CirclePageIndicator)findViewById(R.id.circle_pager_Indicator);
		
		mViewPager.setAdapter(new SliderPagerAdapter(this));
		mCirclePageIndicator.setViewPager(mViewPager);
		mCirclePageIndicator.setCurrentItem(0);
	
		mCirclePageIndicator.setOnPageChangeListener(new OnPageChangeListener() {
			
			@Override
			public void onPageSelected(int position) {
				
				if(position == 4){
					findViewById(R.id.loginbtn).setVisibility(View.VISIBLE);
					findViewById(R.id.rl1).setBackgroundResource(R.color.white);
				} else {
					findViewById(R.id.loginbtn).setVisibility(View.GONE);
					findViewById(R.id.rl1).setBackgroundResource(android.R.color.transparent);
				}
			}
			
			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onPageScrollStateChanged(int arg0) {
				// TODO Auto-generated method stub
				
			}
		});
	}

	/*private void startAnimations() {
		Animation anim = AnimationUtils.loadAnimation(this, R.anim.alpha);
		anim.reset();
		LinearLayout l = (LinearLayout) findViewById(R.id.splash_layout);
		l.clearAnimation();
		l.startAnimation(anim);

		anim = AnimationUtils.loadAnimation(this, R.anim.translate_splash);
		anim.reset();
		LinearLayout layout = (LinearLayout) findViewById(R.id.animation_layout);
		layout.clearAnimation();
		layout.startAnimation(anim);

	}*/

	@Override
	public void onClick(View v) {

		switch (v.getId()) {
		case R.id.loginbtn:
			Utils.storeString(mSharedPreferences, TAG, TAG);
			startActivity(new Intent(SplashActivityTwo.this, LoginActivity.class));
			overridePendingTransition(R.anim.slide_in_left,R.anim.slide_out_left);
			finish();
			break;

		case R.id.skip_text:
			Utils.storeString(mSharedPreferences, TAG, TAG);
			startActivity(new Intent(SplashActivityTwo.this, LoginActivity.class));
			overridePendingTransition(R.anim.slide_in_left,R.anim.slide_out_left);
			finish();
			break;
		
		default:
			break;
		}
	}

}
