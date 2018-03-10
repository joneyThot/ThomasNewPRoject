package com.handyman;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.ActivityCompat;
import android.widget.Toast;

import com.handyman.model.RegisterModel;
import com.handyman.service.AsyncCallListener;
import com.handyman.service.RealTimeLocationChangeRequestTask;
import com.handyman.service.Utils;


public class ServiceTest extends Service {

    private SharedPreferences mSharedPreferences;
    double latitude = 0;
    double longitude = 0;

    @Override
    public IBinder onBind(Intent intent) {
        // TODO Auto-generated method stub
        return null;
    }


    @Override
    public void onCreate() {
        super.onCreate();

        mSharedPreferences = getSharedPreferences(Utils.PREF_NAME, MODE_PRIVATE);

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

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
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
//						arg0.equals(LocationManager.GPS_PROVIDER);
            }

            @Override
            public void onProviderDisabled(String arg0) {
            }

            @Override
            public void onLocationChanged(Location location) {
                // currentLocation = location;
                SplashActivity.latitude = location.getLatitude();
                SplashActivity.longitude = location.getLongitude();

                if (Utils.checkInternetConnection(getApplicationContext())) {
                    LocationManager manager = (LocationManager) getApplicationContext().getSystemService(Context.LOCATION_SERVICE);
                    boolean statusOfGPS = manager.isProviderEnabled(LocationManager.GPS_PROVIDER);
                    if (statusOfGPS) {
                        if (!mSharedPreferences.getString(Utils.USER_ID, "").equalsIgnoreCase("") && !mSharedPreferences.getString(Utils.USER_ID, "").equalsIgnoreCase(null)) {
                            if (SplashActivity.latitude != 0 && SplashActivity.longitude != 0) {
                                onRealTimeLocation(mSharedPreferences.getString(Utils.USER_ID, ""), SplashActivity.latitude, SplashActivity.longitude);
                            }
                        }
                    }
                }

            }

        });

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        try {

        } catch (Exception e) {
            e.printStackTrace();
        }
        return super.onStartCommand(intent, flags, startId);
    }

    public void onDestroy() {
        Intent intent = new Intent("com.handyman.intent.action.LOCATION");
        intent.putExtra("yourvalue", "torestore");
        sendBroadcast(intent);
    }

    private void onRealTimeLocation(String id, Double lat, Double lng) {

        if (Utils.checkInternetConnection(this)) {
            RealTimeLocationChangeRequestTask realTimeLocationChangeRequestTask = new RealTimeLocationChangeRequestTask(this);
            realTimeLocationChangeRequestTask.setAsyncCallListener(new AsyncCallListener() {

                @Override
                public void onResponseReceived(Object response) {
                    RegisterModel registerModel = (RegisterModel) response;
                    if (registerModel.success.equalsIgnoreCase("1")) {

                    } else if (registerModel.success.equalsIgnoreCase("0")) {
                    }
                }

                @Override
                public void onErrorReceived(String error) {
                    Toast.makeText(getApplicationContext(), getString(R.string.try_again), Toast.LENGTH_SHORT).show();
                }
            });
            realTimeLocationChangeRequestTask.execute(id, String.valueOf(lat), String.valueOf(lng));
        }
    }


}
