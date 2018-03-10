package com.handy;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.GooglePlayServicesUtil;
import com.handy.logger.Logger;
import com.handy.model.RegisterModel;
import com.handy.service.AsyncCallListener;
import com.handy.service.CheckIsLoginRequestTask;
import com.handy.service.GetDominNameRequestTask;
import com.handy.service.Utils;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStates;
import com.google.android.gms.location.LocationSettingsStatusCodes;

public class SplashActivity extends Activity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, com.google.android.gms.location.LocationListener {

    public static double latitude = 0, longitude = 0;
    private static String TAG = "SplashActivity";

    boolean firstTime = true, isDialogOpen = false, nextCall = false;
    private GoogleApiClient googleApiClient;
    private LocationRequest locationRequest;
    final static int REQUEST_LOCATION = 199;
    TextView txtCheckInternet;
    Location lastLocation;
    final private int REQUEST_CODE_ASK_PERMISSIONS = 123;
    private final static int PLAY_SERVICES_RESOLUTION_REQUEST = 1000;
    private NetworkChangeReceiver receiver;
    private boolean isConnected = false, isNerverAskmeAgain = false;

    private SharedPreferences mSharedPreferences;
    private Handler mHandler = new Handler();
    private Runnable mRunnable = new Runnable() {

        @Override
        public void run() {
//			Utils.turnGPSOn(SplashActivity.this);
//			if(Utils.checkInternetConnection(getApplicationContext())){
            if (isConnected) {
                if ((!mSharedPreferences.getString(Utils.MOBILE_NO, "").isEmpty()) && (!mSharedPreferences.getString(Utils.PASSWORD, "").isEmpty())) {
                    if (!getIntent().hasExtra("FROM")) {
                        Intent intent = new Intent(SplashActivity.this, MainActivity.class);
                        intent.putExtra("FROM", "SplashActivity");
                        startActivity(intent);
                        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_left);
                        finish();
                    } else {
                        Intent intent = new Intent(SplashActivity.this, MainActivity.class);
                        intent.putExtra("FROM", getIntent().getStringExtra("FROM"));
                        intent.putExtra("response", getIntent().getStringExtra("response"));
                        startActivity(intent);
                        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_left);
                        finish();
                    }
                } else {
                    if (mSharedPreferences.getString("SplashActivityTwo", "").isEmpty()) {
                        startActivity(new Intent(SplashActivity.this, SplashActivityTwo.class));
                        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_left);
                        finish();
                    } else {
                        startActivity(new Intent(SplashActivity.this, LoginActivity.class));
                        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_left);
                        finish();
                    }
                }

            } else {
//				Toast.makeText(getApplicationContext(), getResources().getString(R.string.connection) , Toast.LENGTH_SHORT).show();
                txtCheckInternet.setVisibility(View.VISIBLE);
            }

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);

        int currentapiVersion = Build.VERSION.SDK_INT;
        if (currentapiVersion < Build.VERSION_CODES.LOLLIPOP) {
            getActionBar().hide();
        }

        setContentView(R.layout.activity_splash);

        if (currentapiVersion > Build.VERSION_CODES.LOLLIPOP) {
            // Do something for lollipop and above versions
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if ((this.checkSelfPermission(android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) ||
                        (this.checkSelfPermission(android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) ||
                        (this.checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) ||
                        (this.checkSelfPermission(android.Manifest.permission.RECEIVE_SMS) != PackageManager.PERMISSION_GRANTED) ||
                        (this.checkSelfPermission(android.Manifest.permission.READ_SMS) != PackageManager.PERMISSION_GRANTED)) {

                    requestPermissions(new String[]{android.Manifest.permission.ACCESS_COARSE_LOCATION,
                                    android.Manifest.permission.ACCESS_FINE_LOCATION,
                                    android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
                                    android.Manifest.permission.RECEIVE_SMS,
                                    android.Manifest.permission.READ_SMS},
                            REQUEST_CODE_ASK_PERMISSIONS);
                }
            }
        }

        mSharedPreferences = getSharedPreferences(Utils.PREF_NAME, MODE_PRIVATE);
        txtCheckInternet = (TextView) findViewById(R.id.txtCheckInternet);
        txtCheckInternet.setVisibility(View.GONE);

        IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        receiver = new NetworkChangeReceiver();
        registerReceiver(receiver, filter);

        mSharedPreferences = getSharedPreferences(Utils.PREF_NAME, MODE_PRIVATE);
        if ((!mSharedPreferences.getString(Utils.USER_ID, "").isEmpty())) {
            isLogin(mSharedPreferences.getString(Utils.USER_ID, ""), "handyman");
        }

    }

    private void enableLoc() {

        if (googleApiClient == null) {
            googleApiClient = new GoogleApiClient.Builder(this)
                    .addApi(LocationServices.API)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .build();

            googleApiClient.connect();

            locationRequest = LocationRequest.create();
            locationRequest.setInterval(10000);
            locationRequest.setFastestInterval(5000);
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
                                status.startResolutionForResult(SplashActivity.this, REQUEST_LOCATION);
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
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUEST_LOCATION:
                switch (resultCode) {
                    case Activity.RESULT_CANCELED:
                        isDialogOpen = false;
//						finish();
                        if (!nextCall) {
                            nextCall = true;
                            mHandler.postDelayed(mRunnable, 1000);
                        }
                        break;
                    case Activity.RESULT_OK:
                        startLocationUpdates();
                        if (!isConnected) {
                            txtCheckInternet.setVisibility(View.VISIBLE);
                        } else {
                            txtCheckInternet.setVisibility(View.GONE);
                        }
                        break;
                }
                break;
        }

    }

    private void startLocationUpdates() {

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            return;
        }
        if (googleApiClient != null) {
            if (googleApiClient.isConnected()) {
                LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, locationRequest, this);
            }
        }

    }

    @Override
    protected void onStart() {
        if (googleApiClient != null) {
            googleApiClient.connect();
            Logger.e(TAG, "onStart()");
        } else {
            // First we need to check availability of play services
            if (checkPlayServices()) {
                enableLoc();
            }
        }
        super.onStart();
    }


    @Override
    protected void onResume() {
        // TODO Auto-generated method stub
        super.onResume();
//		if (Utils.checkInternetConnection(getApplicationContext())) {
        if (googleApiClient != null) {
            if (googleApiClient.isConnected()) {
                startLocationUpdates();
                Logger.e(TAG, "onResume()");
            }
        }
//		} else {
//			txtCheckInternet.setVisibility(View.VISIBLE);
//		}
    }


    @Override
    protected void onStop() {
        if (googleApiClient != null) {
            googleApiClient.disconnect();
            Logger.e(TAG, "onStop()");
        }
        super.onStop();
    }

//	@Override
//	protected void onPause() {
//		super.onPause();
//		stopLocationUpdates();
//		Logger.e(TAG,"onPause()");
//	}
//
//	protected void stopLocationUpdates() {
//		if(googleApiClient != null) {
//			LocationServices.FusedLocationApi.removeLocationUpdates(googleApiClient, this);
//			Logger.e(TAG,"stopLocationUpdates()");
//		}
//	}

    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
        unregisterReceiver(receiver);
        Logger.d(TAG, "onDestroy");
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        isDialogOpen = false;
        mHandler.removeCallbacks(mRunnable);
    }


    @Override
    public void onConnected(Bundle bundle) {
        if (!isDialogOpen) {
            if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                return;
            }
            if (googleApiClient != null) {
                if (googleApiClient != null) {
                    if (googleApiClient.isConnected()) {
                        LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, locationRequest, this);
                    }
//			lastLocation = LocationServices.FusedLocationApi.getLastLocation(googleApiClient);
//			if (lastLocation != null) {
//				UpdateLatLang(lastLocation);
//				Logger.e(TAG,"onConnected()");
                }
            }
        }
    }

    @Override
    public void onConnectionSuspended(int i) {
        Logger.e(TAG, "Connection suspended");
        if (googleApiClient != null) {
            googleApiClient.connect();
        }
    }

    @Override
    public void onConnectionFailed(ConnectionResult result) {
//		if (result.getErrorCode() == ConnectionResult.SERVICE_VERSION_UPDATE_REQUIRED) {
//			updateGoogleplay();
//
//		}
//			if(!result.isSuccess()){
//				updateGoogleplay();
//			}
    }

    @Override
    public void onLocationChanged(Location location) {
        LocationManager manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        boolean statusOfGPS = manager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        if (statusOfGPS) {
            if (firstTime) {
                if (location != null) {
                    UpdateLatLang(location);
                    Logger.e(TAG, "onLocationChanged()");
                    firstTime = false;
                    isDialogOpen = false;
                }
            }
        }
    }

    private void UpdateLatLang(Location location) {
        if (location != null) {
            latitude = location.getLatitude();
            longitude = location.getLongitude();
            Logger.e(TAG, "UpdateLatLang()" + "LATITUDE :: " + latitude + "LONGITUDE :: " + longitude);
            mHandler.postDelayed(mRunnable, 1000);
        } else {
            Logger.e(TAG, "UpdateLatLang()" + "LATITUDE :: " + latitude + "LONGITUDE :: " + longitude);
        }

    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        isDialogOpen = false;
        return true;
    }

    public void updateGoogleplay() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(SplashActivity.this);
        alertDialogBuilder.setTitle("Update Google Play Services");
        alertDialogBuilder
                .setMessage("This Application Want To Update You Google Play Services App")
                .setCancelable(false)
                .setPositiveButton("Update",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                callMarketPlace();
                                finish();
                            }
                        });
        alertDialogBuilder.show();
    }

    public void callMarketPlace() {
        try {
            startActivityForResult(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + "com.google.android.gms")), 1);
        } catch (android.content.ActivityNotFoundException anfe) {
            startActivityForResult(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + "com.google.android.gms")), 1);
        }
    }

    @TargetApi(Build.VERSION_CODES.M)
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CODE_ASK_PERMISSIONS:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // Permission Granted
                } else {
                    // Permission Denied
                    if (!nextCall) {
                        nextCall = true;
                        mHandler.postDelayed(mRunnable, 1000);
                    }
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
        /*if (requestCode == REQUEST_CODE_ASK_PERMISSIONS) {
            // for each permission check if the user grantet/denied them
			// you may want to group the rationale in a single dialog,
			// this is just an example
			for (int i = 0, len = permissions.length; i < len; i++) {
				String permission = permissions[i];
				if (grantResults[i] == PackageManager.PERMISSION_DENIED) {
					boolean showRationale = shouldShowRequestPermissionRationale( permission );
					if (! showRationale) {
						// user denied flagging NEVER ASK AGAIN
						// you can either enable some fall back,
						// disable features of your app
						// or open another dialog explaining
						// again the permission and directing to
						// the app setting
						if(!isNerverAskmeAgain) {
							Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
							Uri uri = Uri.fromParts("package", getPackageName(), null);
							intent.setData(uri);
							startActivityForResult(intent, REQUEST_CODE_ASK_PERMISSIONS);
							isNerverAskmeAgain = true;
						}

					} else if (Manifest.permission.WRITE_EXTERNAL_STORAGE.equals(permission) || Manifest.permission.ACCESS_COARSE_LOCATION.equals(permission) ||
							Manifest.permission.ACCESS_FINE_LOCATION.equals(permission)) {
//                        Utils.showMessageDialog(SplashActivity.this,"Permission denied",getString(R.string.photo_deny_permission));
						if(!isNerverAskmeAgain) {
							AlertDialog.Builder builder = new AlertDialog.Builder(SplashActivity.this);
							builder.setTitle("Permission denied");
							builder.setCancelable(false);
							builder.setMessage(getString(R.string.photo_deny_permission));
							builder.setPositiveButton("I'M SURE",
									new DialogInterface.OnClickListener() {
										public void onClick(DialogInterface dialog, int id) {
											Utils.denyFlag = true;
											mHandler.postDelayed(mRunnable, 1000);
										}
									});
							builder.setNegativeButton("RE-TRY", new DialogInterface.OnClickListener() {

								@Override
								public void onClick(DialogInterface dialog, int which) {
									dialog.dismiss();
									finish();
//                                    int currentapiVersion = Build.VERSION.SDK_INT;
//                                    if (currentapiVersion > Build.VERSION_CODES.LOLLIPOP) {
//                                        // Do something for lollipop and above versions
//                                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//                                            if ((checkSelfPermission(android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) ||
//                                                    (checkSelfPermission(android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) ||
//                                                    (checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)) {
//
//                                                requestPermissions(new String[]{android.Manifest.permission.ACCESS_COARSE_LOCATION, android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_CODE_ASK_PERMISSIONS);
//                                            }
//                                        }
//                                    }

								}

							});
							AlertDialog alertDialog = builder.create();
							alertDialog.show();
							isNerverAskmeAgain = true;
						}
						// user denied WITHOUT never ask again
						// this is a good place to explain the user
						// why you need the permission and ask if he want
						// to accept it (the rationale)
					} *//*else if (Manifest.permission.ACCESS_COARSE_LOCATION.equals(permission) &&
                            Manifest.permission.ACCESS_FINE_LOCATION.equals(permission)) {
                        Utils.showMessageDialog(SplashActivity.this,"Permission denied",getString(R.string.location_deny_permission));
                    }*//*
                }
			}
		}*/
    }

    private boolean checkPlayServices() {
        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
                GooglePlayServicesUtil.getErrorDialog(resultCode, this, PLAY_SERVICES_RESOLUTION_REQUEST).show();
            } else {
                Toast.makeText(getApplicationContext(), "This device is not supported.", Toast.LENGTH_LONG).show();
//				finish();
            }
            return false;
        }
        return true;
    }

    private void isLogin(String id, String user_type) {

        if (Utils.checkInternetConnection(SplashActivity.this)) {
            CheckIsLoginRequestTask checkIsLoginRequestTask = new CheckIsLoginRequestTask(SplashActivity.this);
            checkIsLoginRequestTask.setAsyncCallListener(new AsyncCallListener() {

                @Override
                public void onResponseReceived(Object response) {
                    RegisterModel registerModel = (RegisterModel) response;

                    if (registerModel.success.equalsIgnoreCase("0")) {

                        if ((!mSharedPreferences.getString(Utils.USER_ID, "").isEmpty())) {
                            Utils.storeString(mSharedPreferences, Utils.MOBILE_NO, "");
                            Utils.storeString(mSharedPreferences, Utils.PASSWORD, "");
                            Utils.storeString(mSharedPreferences, Utils.USER_ID, "");
                            Utils.storeString(mSharedPreferences, Utils.FIRSTNAME, "");
                            Utils.storeString(mSharedPreferences, Utils.LASTNAME, "");
                            Utils.storeString(mSharedPreferences, Utils.IMAGEPATH, "");
                            Utils.storeString(mSharedPreferences, Utils.EMAIL, "");
                            Utils.storeString(mSharedPreferences, Utils.USER_PROFILE, "");
                            Utils.storeString(mSharedPreferences, Utils.NOTI_HIRE_STATUS, "");

                            Utils.storeString(mSharedPreferences, Utils.LOGOUT_STATUS, "logout");
                            stopService(new Intent(SplashActivity.this, LocationUpdaterService.class));
                        }
                    } else {
                        Utils.storeString(mSharedPreferences, Utils.LOGOUT_STATUS, "");
                    }
                }

                @Override
                public void onErrorReceived(String error) {
                    Toast.makeText(SplashActivity.this, getString(R.string.try_again), Toast.LENGTH_SHORT).show();
                }
            });
            checkIsLoginRequestTask.execute(id, user_type);
        } /*else {
            Toast.makeText(SplashActivity.this, getString(R.string.connection), Toast.LENGTH_SHORT).show();
        }*/
    }

    private void getDomin() {

        if (Utils.checkInternetConnection(SplashActivity.this)) {
            GetDominNameRequestTask getDominNameRequestTask = new GetDominNameRequestTask(SplashActivity.this);
            getDominNameRequestTask.setAsyncCallListener(new AsyncCallListener() {

                @Override
                public void onResponseReceived(Object response) {
                    RegisterModel registerModel = (RegisterModel) response;

                    if (registerModel.success.equalsIgnoreCase("1")) {

//                        if (Utils.validateString(registerModel.getDomain_name())) {
//                            Utils.URL_SERVER_ADDRESS = registerModel.getDomain_name();
//                        }
//
//                        if (Utils.validateString(registerModel.getImage_url())) {
//                            Utils.IMAGE_URL = registerModel.getImage_url();
//                        }
                        Utils.URL_SERVER_ADDRESS = mSharedPreferences.getString(Utils.URL_ADDRESS,"");
                        Utils.IMAGE_URL = mSharedPreferences.getString(Utils.IMAGE_URL_ADDRESS,"");
                    }
                }

                @Override
                public void onErrorReceived(String error) {
                    Toast.makeText(SplashActivity.this, getString(R.string.try_again), Toast.LENGTH_SHORT).show();
                }
            });
            getDominNameRequestTask.execute();
        } /*else {
            Toast.makeText(SplashActivity.this, getString(R.string.connection), Toast.LENGTH_SHORT).show();
        }*/
    }

    public class NetworkChangeReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(final Context context, final Intent intent) {

            Logger.v(TAG, "Receieved notification about network status");
            isNetworkAvailable(context);

        }

        private boolean isNetworkAvailable(Context context) {
            ConnectivityManager connectivity = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            if (connectivity != null) {
                NetworkInfo[] info = connectivity.getAllNetworkInfo();
                if (info != null) {
                    for (int i = 0; i < info.length; i++) {
                        if (info[i].getState() == NetworkInfo.State.CONNECTED) {
                            if (!isConnected) {
//                                getDomin();
                                Logger.v(TAG, "Now you are connected to Internet!");
                                isConnected = true;
                                firstTime = true;
                                txtCheckInternet.setVisibility(View.GONE);

                            }
                            return true;
                        }
                    }
                }
            }
            Logger.v(TAG, "You are not connected to Internet!");
            isConnected = false;
            return false;
        }
    }

}