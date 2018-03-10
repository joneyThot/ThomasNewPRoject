package com.handy.fragment;

import java.util.ArrayList;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStates;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.handy.MainActivity;
import com.handy.R;
import com.handy.SplashActivity;
import com.handy.adapter.MyHiringFragmentPagerAdapter;
import com.handy.logger.Logger;
import com.handy.model.MyHiringsModel;
import com.handy.service.Utils;
import com.handy.view.TitlePageIndicator;
import com.handy.view.TitlePageIndicator.OnCenterItemClickListener;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;

public class MyHiringsFragment_new extends BaseFragment implements OnClickListener, OnCenterItemClickListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {
	
	private static String TAG = "MyHiringsFragment";
	private ArrayList<MyHiringsModel> mMyHiringsList = new ArrayList<MyHiringsModel>();
	private SharedPreferences mSharedPreferences;

	Fragment fr;
	View mRootView;
	ViewPager mPager;
	TitlePageIndicator mIndicator;
//	MyHiringPagerAdapter_new myHiringPagerAdapter_new;
	MyHiringFragmentPagerAdapter myHiringFragmentPagerAdapter;
	String complete = "", pending = "", cancel = "", start = "", active = "", declined = "";
	private static final int DEFAULT_OFFSCREEN_PAGES = 1;
	private GoogleApiClient googleApiClient;
	private LocationRequest locationRequest;
	final static int REQUEST_LOCATION = 199;
	boolean firstTime = true, isDialogOpen = false;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setRetainInstance(true);
	    this.myHiringFragmentPagerAdapter = new MyHiringFragmentPagerAdapter(getChildFragmentManager());
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		int currentapiVersion = Build.VERSION.SDK_INT;

		if (currentapiVersion > Build.VERSION_CODES.LOLLIPOP) {
			// Do something for lollipop and above versions
//			if (!Utils.denyFlag) {
			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
				if ((getActivity().checkSelfPermission(android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) ||
						(getActivity().checkSelfPermission(android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) ||
						(getActivity().checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)) {

					requestPermissions(new String[]{android.Manifest.permission.ACCESS_COARSE_LOCATION, android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.WRITE_EXTERNAL_STORAGE},
							0);
				}
			}
//			}
		}
		mRootView = inflater.inflate(R.layout.fragment_my_hirings, container, false);
		return mRootView;
	}
	
	@Override
	public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		initview();
		super.onViewCreated(view, savedInstanceState);
		
	}
	
	@SuppressWarnings("deprecation")
	private void initview() {
		mSharedPreferences = getActivity().getSharedPreferences(Utils.PREF_NAME, Context.MODE_PRIVATE);
		((MainActivity) getActivity()).setTitleText(getString(R.string.menu_my_hirings), "", View.GONE,	View.VISIBLE, View.GONE);
		getActivity().findViewById(R.id.title_back).setVisibility(View.GONE);

		mActivity.getSlidingMenu().setMode(SlidingMenu.LEFT);
		mActivity.getSlidingMenu().setTouchModeAbove(SlidingMenu.TOUCHMODE_NONE);
		
		mPager = (ViewPager) mRootView.findViewById(R.id.pager);
		mIndicator = (TitlePageIndicator) mRootView.findViewById(R.id.indicator);

		LocationManager manager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE );
		boolean statusOfGPS = manager.isProviderEnabled(LocationManager.GPS_PROVIDER);
		if(!statusOfGPS) {
			enableLoc();
		}
		
//		mPager.setAdapter(new MyHiringPagerAdapter_new(getActivity()));
//		mIndicator.setViewPager(mPager);
//		mIndicator.setCurrentItem(0);
		
		if(getArguments() != null){
			pending = getArguments().getString("Pending");
			active = getArguments().getString("Active");
			start = getArguments().getString("Start");
			cancel = getArguments().getString("Cancel");
			complete = getArguments().getString("Complete");
			declined = getArguments().getString("declined");
		} 
		
		if(Utils.validateString(pending)){
			mPager.setAdapter(myHiringFragmentPagerAdapter);
			mIndicator.setViewPager(mPager);
			mIndicator.setCurrentItem(0);
		} else if(Utils.validateString(active)){
			mPager.setAdapter(myHiringFragmentPagerAdapter);
			mIndicator.setViewPager(mPager);
			mIndicator.setCurrentItem(1);
		} else if(Utils.validateString(start)){
			mPager.setAdapter(myHiringFragmentPagerAdapter);
			mIndicator.setViewPager(mPager);
			mIndicator.setCurrentItem(2);
		} else if(Utils.validateString(cancel)){
			mPager.setAdapter(myHiringFragmentPagerAdapter);
			mIndicator.setViewPager(mPager);
			mIndicator.setCurrentItem(3);
		} else if(Utils.validateString(complete)){
			mPager.setAdapter(myHiringFragmentPagerAdapter);
			mIndicator.setViewPager(mPager);
			mIndicator.setCurrentItem(4);
		} else if(Utils.validateString(declined)){
			mPager.setAdapter(myHiringFragmentPagerAdapter);
			mIndicator.setViewPager(mPager);
			mIndicator.setCurrentItem(5);
		} else {
			mPager.setAdapter(myHiringFragmentPagerAdapter);
			mIndicator.setViewPager(mPager);
		}

	}
	
	
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		}
	}
	
	@Override
	public void onCenterItemClick(int position) {

	}

	private void enableLoc() {

		if (googleApiClient == null) {
			googleApiClient = new GoogleApiClient.Builder(getActivity())
					.addApi(LocationServices.API)
					.addConnectionCallbacks(this)
					.addOnConnectionFailedListener(this)
					.build();

			googleApiClient.connect();

			locationRequest = LocationRequest.create();
			locationRequest.setInterval(30 * 1000);
			locationRequest.setFastestInterval(5 * 1000);
			locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

			LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder().addLocationRequest(locationRequest);

			builder.setAlwaysShow(true);

			PendingResult<LocationSettingsResult> result = LocationServices.SettingsApi.checkLocationSettings(googleApiClient, builder.build());
			result.setResultCallback(new ResultCallback<LocationSettingsResult>() {
				@Override
				public void onResult(LocationSettingsResult result) {
					final Status status = result.getStatus();
					final LocationSettingsStates state = result.getLocationSettingsStates();
					switch (status.getStatusCode()) {
						case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
							try {
								isDialogOpen = true;
								status.startResolutionForResult(getActivity(), REQUEST_LOCATION);
							} catch (IntentSender.SendIntentException e) {
								// Ignore the error.
							}
							break;

						case LocationSettingsStatusCodes.SUCCESS:

							break;
						case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
							// Location settings are not satisfied. However, we have no way to fix the
							// settings so we won't show the dialog.
							break;
					}
				}
			});
		}

	}


	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (requestCode) {
			case REQUEST_LOCATION:
				switch (resultCode) {
					case Activity.RESULT_CANCELED:
						isDialogOpen = false;
//						finish();
						break;
					case Activity.RESULT_OK:
						startLocationUpdates();
						break;
				}
				break;
		}

	}

	private void startLocationUpdates() {

		if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
			// TODO: Consider calling
			return;
		}
		if(googleApiClient != null) {
			if (googleApiClient.isConnected()) {
				LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, locationRequest, this);
			}
		}

	}

	@Override
	public void onStart() {
		if(googleApiClient != null) {
			googleApiClient.connect();
			Logger.e(TAG,"onStart()");
		}
		super.onStart();
	}


	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		if (Utils.checkInternetConnection(getActivity())) {
			if (googleApiClient != null) {
				if (googleApiClient.isConnected()) {
					startLocationUpdates();
					Logger.e(TAG, "onResume()");
				}
			}
		} else {
//			Toast.makeText(getActivity(), getResources().getString(R.string.connection), Toast.LENGTH_SHORT).show();
		}
	}


	@Override
	public void onStop() {
		if(googleApiClient != null) {
			googleApiClient.disconnect();
			Logger.e(TAG,"onStop()");
		}
		super.onStop();
	}

	@Override
	public void onPause() {
		super.onPause();
		stopLocationUpdates();
		Logger.e(TAG,"onPause()");
	}

	protected void stopLocationUpdates() {
		if(googleApiClient != null && googleApiClient.isConnected()) {
			LocationServices.FusedLocationApi.removeLocationUpdates(googleApiClient, this);
			Logger.e(TAG,"stopLocationUpdates()");
		}
	}

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		Logger.d(TAG, "onDestroy");
	}


	@Override
	public void onConnected(@Nullable Bundle bundle) {
		if (!isDialogOpen) {

			if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
				// TODO: Consider calling
				return;
			}
			if (googleApiClient != null) {
				if (googleApiClient.isConnected()) {
					LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, locationRequest, this);
				}
			}
		}
	}

	@Override
	public void onConnectionSuspended(int i) {
		Logger.e(TAG, "Connection suspended");
		if(googleApiClient != null) {
			googleApiClient.connect();
		}
	}

	@Override
	public void onLocationChanged(Location location) {
		if(firstTime) {
			if (location != null) {
				UpdateLatLang(location);
				Logger.e(TAG, "onLocationChanged()");
				firstTime = false;
				isDialogOpen = false;
			}
		}
	}

	private void UpdateLatLang(Location location){
		if (location != null) {
			SplashActivity.latitude = location.getLatitude();
			SplashActivity.longitude = location.getLongitude();
			Logger.e(TAG,"UpdateLatLang()" + "LATITUDE :: " + SplashActivity.latitude + "LONGITUDE :: " + SplashActivity.longitude);
		} else {
			Logger.e(TAG,"UpdateLatLang()" + "LATITUDE :: " + SplashActivity.latitude + "LONGITUDE :: " + SplashActivity.longitude);
		}

	}

	@Override
	public void onConnectionFailed(ConnectionResult connectionResult) {

	}
}