package com.android.handy;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.widget.Toast;

import com.android.handy.model.RegisterModel;
import com.android.handy.service.AsyncCallListener;
import com.android.handy.service.RealTimeLocationChangeRequestTask;
import com.android.handy.service.Utils;

public class ServiceTest extends Service{
	
	private SharedPreferences mSharedPreferences;
	static double latitude = 0;
	static double longitude = 0;
	
	/*static double latitude = 22.3000;
	static double longitude = 70.7833;*/

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}
	
	
	@Override
	public void onCreate() {
		super.onCreate();
		
		mSharedPreferences = getSharedPreferences(Utils.PREF_NAME, MODE_PRIVATE);
		
//		GPSTracker gpsTracker = Utils.getCurrentLocation(this);
//		if (gpsTracker != null) {
//			latitude = gpsTracker.getLatitude();
//			longitude = gpsTracker.getLongitude();
//			//			currentLocation = new Location("");
//			//			currentLocation.setLatitude(latitude);
//			//			currentLocation.setLongitude(longitude);
//			Logger.e("", "GPSTracker -- "+ latitude + " & " +longitude);
//		}
		
		LocationManager locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);

		// Creating a criteria object to retrieve provider
		Criteria criteria = new Criteria();
		criteria.setAccuracy(Criteria.ACCURACY_FINE);
		criteria.setAltitudeRequired(false);
		criteria.setBearingRequired(false);
		criteria.setCostAllowed(true);
		criteria.setPowerRequirement(Criteria.POWER_MEDIUM);
		// Getting the name of the best provider
		String provider = LocationManager.GPS_PROVIDER;
		 locationManager.getBestProvider(criteria, true);
		
		Location currentLocation = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
		if (currentLocation != null) {
			SplashActivity.latitude = currentLocation.getLatitude();
			SplashActivity.longitude = currentLocation.getLongitude();

//			Logger.e(TAG, "currentLocation -- " + SplashActivity.latitude	+ " & " + SplashActivity.longitude);
		}

		locationManager.requestLocationUpdates(provider, 10000, 0, new LocationListener() {

					@Override
					public void onStatusChanged(String arg0, int arg1,
							Bundle arg2) {
					}

					@Override
					public void onProviderEnabled(String arg0) {
					}

					@Override
					public void onProviderDisabled(String arg0) {
					}

					@Override
					public void onLocationChanged(Location location) {
						// currentLocation = location;
						SplashActivity.latitude = location.getLatitude();
						SplashActivity.longitude = location.getLongitude();
						
						if(!mSharedPreferences.getString(Utils.USER_ID, "").equalsIgnoreCase("") && !mSharedPreferences.getString(Utils.USER_ID, "").equalsIgnoreCase(null)){
							if(SplashActivity.latitude != 0 && SplashActivity.longitude != 0){
								onRealTimeLocation(mSharedPreferences.getString(Utils.USER_ID, ""),SplashActivity.latitude, SplashActivity.longitude);
							}
						}
					}
					
				});

//		mTimer = new Timer();
//		mTimer.schedule(timerTask, 10000, 10 * 1000);
	}
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		try {
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return super.onStartCommand(intent, flags, startId);
	}
	
//	private Timer mTimer;
//
//	TimerTask timerTask = new TimerTask() {
//
//		@Override
//		public void run() {
////			Logger.e("Running", "Running");
//			try {
//				if(Utils.checkInternetConnection(getApplicationContext())){
//					
//					if(!mSharedPreferences.getString(Utils.USER_ID, "").equalsIgnoreCase("") && !mSharedPreferences.getString(Utils.USER_ID, "").equalsIgnoreCase(null)){
//						if(latitude != 0 && longitude != 0){
//							onRealTimeLocation(mSharedPreferences.getString(Utils.USER_ID, ""),latitude, longitude);
//						}
//					
//					}
//					
//				} else {
////					Toast.makeText(getApplicationContext(), getResources().getString(R.string.connection) , Toast.LENGTH_SHORT).show();
//				}
//			} catch (Exception e) {
//				e.printStackTrace();
//			}
//		}
//	};
	
	
	public void onDestroy() {
//		try {
//			mTimer.cancel();
//			timerTask.cancel();
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
		Intent intent = new Intent("com.handy.intent.action.LOCATION");
		intent.putExtra("yourvalue", "torestore");
		sendBroadcast(intent);
	}
	
	private void onRealTimeLocation(String id, Double lat,Double lng) {
		
        if (Utils.checkInternetConnection(this)) {
        	RealTimeLocationChangeRequestTask realTimeLocationChangeRequestTask = new RealTimeLocationChangeRequestTask(this);
        	realTimeLocationChangeRequestTask.setAsyncCallListener(new AsyncCallListener() {

                @Override
                public void onResponseReceived(Object response) {
                    RegisterModel registerModel = (RegisterModel) response;
                    if(registerModel.success.equalsIgnoreCase("1")) {

                        } else if (registerModel.success.equalsIgnoreCase("0")) {
                        }
                    }

                @Override
                public void onErrorReceived(String error) {
                    Toast.makeText(getApplicationContext(), getString(R.string.try_again), Toast.LENGTH_SHORT).show();
                }
            });
        	realTimeLocationChangeRequestTask.execute(id, String.valueOf(lat), String.valueOf(lng));
        } else {
//            Toast.makeText(getApplicationContext(), getString(R.string.connection), Toast.LENGTH_SHORT).show();
        }
    }
	
	
}
