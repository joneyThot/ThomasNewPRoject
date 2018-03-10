package com.android.handyman;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Toast;

import com.android.handyman.logger.Logger;
import com.android.handyman.service.GPSTracker;
import com.android.handyman.service.Utils;

public class SplashActivity extends Activity{
	
//	public static double latitude = 19.2290, longitude = 72.8573;
	public static double latitude = 0, longitude = 0;
	private static String TAG = "SplashActivity";
	boolean firstTime = true;
	
	private SharedPreferences mSharedPreferences;
	private Handler mHandler = new Handler();
	private Runnable mRunnable = new Runnable() {

		@Override
		public void run() {
			Utils.turnGPSOn(SplashActivity.this);
			if(Utils.checkInternetConnection(getApplicationContext())){
					if((!mSharedPreferences.getString(Utils.MOBILE_NO, "").isEmpty())&&(!mSharedPreferences.getString(Utils.PASSWORD, "").isEmpty()))
					{
						Intent intent = new Intent(SplashActivity.this, MainActivity.class);
						intent.putExtra("FROM", "SplashActivity");
						startActivity(intent);
						overridePendingTransition(R.anim.slide_in_left,R.anim.slide_out_left);
						finish();
					} else {
						if(mSharedPreferences.getString("SplashActivityTwo", "").isEmpty()){
							startActivity(new Intent(SplashActivity.this, SplashActivityTwo.class));
							overridePendingTransition(R.anim.slide_in_left,R.anim.slide_out_left);
							finish();
						} else {
							startActivity(new Intent(SplashActivity.this, LoginActivity.class));
							overridePendingTransition(R.anim.slide_in_left,R.anim.slide_out_left);
							finish();
						}
					}
			} else {
				Toast.makeText(getApplicationContext(), getResources().getString(R.string.connection) , Toast.LENGTH_SHORT).show();
			}
			
		}
	};
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_splash);
		GPSTracker gpsTracker = Utils.getCurrentLocation(this);
		if (gpsTracker != null) {
			latitude = gpsTracker.getLatitude();
			longitude = gpsTracker.getLongitude();
			mHandler.postDelayed(mRunnable, 3000);
			Logger.e("", "GPSTracker onCreate -- "+ latitude + " & " +longitude);
		} else {
			Toast.makeText(getApplicationContext(), "Please On GPS" , Toast.LENGTH_SHORT).show();
		}
		mSharedPreferences = getSharedPreferences(Utils.PREF_NAME, MODE_PRIVATE);	
		
	}
	
	@Override
		protected void onStart() {
			// TODO Auto-generated method stub
			super.onStart();
			
//			if((!mSharedPreferences.getString("onRestart", "").isEmpty())){
//				GPSTracker gpsTracker = Utils.getCurrentLocation(this);
//				if (gpsTracker != null) {
//					
//					latitude = gpsTracker.getLatitude();
//					longitude = gpsTracker.getLongitude();
//					
//					if (latitude != 0 && longitude != 0) {
//						mHandler.postDelayed(mRunnable, 2000);
//						Logger.e("", "GPSTracker onStart -- " + latitude + " & " + longitude);
//
//					} else {
////							Toast.makeText(getApplicationContext(),	latitude + " & " + longitude, Toast.LENGTH_SHORT).show();
//							Utils.storeString(mSharedPreferences, "onStart", "onStart");
//						}
//					Utils.storeString(mSharedPreferences, "onRestart", "");
//				}
//			}
			Logger.d(TAG, "onStart");
		}
	
	@Override
		protected void onPause() {
			// TODO Auto-generated method stub
			super.onPause();
			Logger.d(TAG, "onPause");
		}
	
	@Override
		protected void onResume() {
			// TODO Auto-generated method stub
			super.onResume();
//			if((!mSharedPreferences.getString("onStart", "").isEmpty())){
//				GPSTracker gpsTracker = Utils.getCurrentLocation(this);
//				if (gpsTracker != null) {
//					
//					latitude = gpsTracker.getLatitude();
//					longitude = gpsTracker.getLongitude();
//					
//					if (latitude != 0 && longitude != 0) {
//						mHandler.postDelayed(mRunnable, 2000);
//						Logger.e("", "GPSTracker onResume -- " + latitude + " & " + longitude);
//
//					} else {
//							Toast.makeText(getApplicationContext(),	latitude + " & " + longitude, Toast.LENGTH_SHORT).show();
//						}
//					Utils.storeString(mSharedPreferences, "onStart", "");
//				}
//			}
			
			Logger.i(TAG, "onResume");
		}
	
	@Override
		protected void onRestart() {
			// TODO Auto-generated method stub
			super.onRestart();
			
			GPSTracker gpsTracker = Utils.getCurrentLocation(this);
			if (gpsTracker != null) {
				
				latitude = gpsTracker.getLatitude();
				longitude = gpsTracker.getLongitude();
				
				if (latitude != 0 && longitude != 0) {
					mHandler.postDelayed(mRunnable, 2000);
					Logger.e("", "GPSTracker onRestart -- " + latitude + " & " + longitude);

				} else {
					Toast.makeText(getApplicationContext(),	latitude + " & " + longitude, Toast.LENGTH_SHORT).show();
//					Utils.storeString(mSharedPreferences, "onRestart", "onRestart");
				}
				
			} else {
				Toast.makeText(getApplicationContext(), "Please On GPS" , Toast.LENGTH_SHORT).show();
			}
			Logger.d(TAG, "onRestart");
		}
	
	@Override
		protected void onStop() {
			// TODO Auto-generated method stub
			super.onStop();
			Logger.d(TAG, "onStop");
		}
	
	@Override
		protected void onDestroy() {
			// TODO Auto-generated method stub
			super.onDestroy();
			Logger.d(TAG, "onDestroy");
		}
	
	@Override
	public void onBackPressed() {
		super.onBackPressed();
		mHandler.removeCallbacks(mRunnable);
	}
	
	
}