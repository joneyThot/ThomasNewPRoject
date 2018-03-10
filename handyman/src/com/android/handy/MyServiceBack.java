package com.android.handy;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.IBinder;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.widget.Toast;

import com.android.handy.logger.Logger;
import com.android.handy.model.RegisterModel;
import com.android.handy.service.AsyncCallListener;
import com.android.handy.service.RealTimeLocationChangeRequestTask;
import com.android.handy.service.Utils;

public class MyServiceBack extends Service{
	
	private LocationManager locationManager;
	private SharedPreferences mSharedPreferences;
	double latitude = 0;
	double longitude = 0;
	WakeLock wakeLock;
	
	public MyServiceBack() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public void onCreate() {
		super.onCreate();
		mSharedPreferences = getSharedPreferences(Utils.PREF_NAME, MODE_PRIVATE);
		PowerManager pm = (PowerManager) getSystemService(this.POWER_SERVICE);
		wakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "DoNotSleep");
		Logger.e("Google", "Service Created");
	}
	
	@Override
	@Deprecated
	public void onStart(Intent intent, int startId) {
		// TODO Auto-generated method stub
		super.onStart(intent, startId);
//		new ToggleGPS(getApplicationContext()).turnGPSOn();
		Logger.e("Google", "Service Started");

		locationManager = (LocationManager) getApplicationContext().getSystemService(Context.LOCATION_SERVICE);
		locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 5, listener);
	}
	
	private LocationListener listener = new LocationListener() {

		@Override
		public void onLocationChanged(Location location) {
			Logger.e("Google", "Location Changed");

			if (location == null)
				return;

			if (isConnectingToInternet(getApplicationContext())) {
				try {
					
					latitude = location.getLatitude();
					longitude = location.getLongitude();
					
					if(!mSharedPreferences.getString(Utils.USER_ID, "").equalsIgnoreCase("") && !mSharedPreferences.getString(Utils.USER_ID, "").equalsIgnoreCase(null)){
						if(latitude != 0 && longitude != 0){
							onRealTimeLocation(mSharedPreferences.getString(Utils.USER_ID, ""),latitude, longitude);
						}
					
					}
				} catch (Exception e) {
					e.printStackTrace();
				}

			}
			
		}

		@Override
		public void onStatusChanged(String provider, int status, Bundle extras) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void onProviderEnabled(String provider) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void onProviderDisabled(String provider) {
			// TODO Auto-generated method stub
			
		}
		
	};
	
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();

//		new ToggleGPS(getApplicationContext()).turnGPSOff();
		wakeLock.release();
		
		Intent intent = new Intent("com.handy.intent.action.LOCATION");
		intent.putExtra("yourvalue", "torestore");
		sendBroadcast(intent);
	};
	
	public static boolean isConnectingToInternet(Context _context) {
		ConnectivityManager connectivity = (ConnectivityManager) _context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		if (connectivity != null) {
			NetworkInfo[] info = connectivity.getAllNetworkInfo();
			if (info != null)
				for (int i = 0; i < info.length; i++)
					if (info[i].getState() == NetworkInfo.State.CONNECTED) {
						return true;
					}

		}
		return false;
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
            Toast.makeText(getApplicationContext(), getString(R.string.connection), Toast.LENGTH_SHORT).show();
        }
    }
	
}
