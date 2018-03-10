package com.handyman.fragment;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.handyman.FetchAddressIntentService;
import com.handyman.MainActivity;
import com.handyman.R;
import com.handyman.logger.Logger;
import com.handyman.model.CityModel;
import com.handyman.model.DataModel;
import com.handyman.service.AppUtils;
import com.handyman.service.AsyncCallListener;
import com.handyman.service.GPSTracker;
import com.handyman.service.GetAllCityNameTask;
import com.handyman.service.Utils;
import com.handyman.view.MySupportMapFragment;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class AddressMapFragment extends BaseFragment implements OnClickListener, OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, com.google.android.gms.location.LocationListener, View.OnTouchListener {

    private static final int RESULT_OK = 1;
    private static final int RESULT_CANCELED = 2;
    private static String TAG = "AddressMapFragment";
    private SharedPreferences mSharedPreferences;
    public static boolean mMapIsTouched = false;

    GoogleMap googleMap;
    MySupportMapFragment myMAPF;
    GPSTracker gps;
    double curLatitude = 0.0f, curLongitude = 0.0f;
    View rootView;
    ArrayList<Marker> marraylst_marker = new ArrayList<Marker>();
    LatLngBounds.Builder builder;
    ArrayList<LatLng> markerPoints;
    LatLng point;
    Marker marker;
    Circle mCircle;

    LocationManager locationManager;
    String provider = "";
    //	PlacesTask placesTask;
//	ParserTask parserTask;
//
//	DownloadTask placesDownloadTask;
//	DownloadTask placeDetailsDownloadTask;
//	ParserTask placesParserTask;
//	ParserTask placeDetailsParserTask;
    final int PLACES = 0;
    final int PLACES_DETAILS = 1;
    Geocoder geocoder;
    List<Address> addresses;
    //	double map_latitude = 0.0f, map_longitude = 0.0f;
    MarkerOptions markerOptions;
    CameraPosition cameraPosition;

    Fragment fr;
    View mRootView;
    //    TextView atvPlaces;
    TextView btnConfirm;
    String addressString = "";
    public static TextView locationMarkertext;

    public static ProgressBar locationLoading;
    private AddressResultReceiver mResultReceiver;
    protected String mAddressOutput;
    protected String mAreaOutput;
    protected String mCityOutput;
    protected String mStateOutput;
    private static final int REQUEST_CODE_AUTOCOMPLETE = 1;
    private final static int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
    private GoogleApiClient mGoogleApiClient;
    private LinearLayout address_layout;
    //    public String cityHandyman = "";
//    private double handymanLatitude = 0, handymanLongitude = 0;
    boolean mIsScrolling = false;
    String handyman_id, category_id = "", sub_category_id = "";

    ArrayList<CityModel> mCityNameList = new ArrayList<CityModel>();


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (mRootView != null) {
            ViewGroup parent = (ViewGroup) mRootView.getParent();
            if (parent != null)
                parent.removeView(mRootView);
        }
        try {
            mRootView = inflater.inflate(R.layout.fragment_address_map, container, false);
        } catch (InflateException e) {
            /* map is already there, just return view as it is */
        }

        initview();
//        getHandymanCity(mSharedPreferences.getString(Utils.HANDYMAN_ID, ""));
        return mRootView;
    }

    private void initview() {
        mSharedPreferences = getActivity().getSharedPreferences(Utils.PREF_NAME, Context.MODE_PRIVATE);

        ((MainActivity) getActivity()).setTitleText("", getString(R.string.customer_address), "", View.VISIBLE, View.GONE, View.GONE, View.GONE, View.GONE);
        getActivity().findViewById(R.id.title).setVisibility(View.GONE);
        getActivity().findViewById(R.id.txtUrl).setVisibility(View.GONE);

        mActivity.getSlidingMenu().setMode(SlidingMenu.LEFT);
        mActivity.getSlidingMenu().setTouchModeAbove(SlidingMenu.TOUCHMODE_NONE);

//        handymanLatitude = Double.parseDouble(mSharedPreferences.getString(Utils.HANDYMAN_LATITUDE,""));
//        handymanLongitude = Double.parseDouble(mSharedPreferences.getString(Utils.HANDYMAN_LONGITUDE,""));
//        getHandymanCity(handymanLatitude,handymanLongitude);

        category_id = mSharedPreferences.getString(Utils.CATEGORY_ID, "");
        sub_category_id = mSharedPreferences.getString(Utils.SUB_CATEGORY_ID, "");

        btnConfirm = (TextView) mRootView.findViewById(R.id.btnConfirm);
        btnConfirm.setOnClickListener(this);
//        atvPlaces = (TextView) mRootView.findViewById(R.id.atvPlaces);
//        atvPlaces.setOnClickListener(this);
        address_layout = (LinearLayout) mRootView.findViewById(R.id.address_layout);
        address_layout.setOnClickListener(this);
        locationMarkertext = (TextView) mRootView.findViewById(R.id.locationMarkertext);
        locationLoading = (ProgressBar) mRootView.findViewById(R.id.locationLoading);

        if (googleMap == null) {
            FragmentManager fm = getChildFragmentManager();
            myMAPF = (MySupportMapFragment) fm.findFragmentById(R.id.map_address);
            if (myMAPF == null) {
                myMAPF = (MySupportMapFragment) SupportMapFragment.newInstance();
                fm.beginTransaction().replace(R.id.map_address, myMAPF).commit();
            }

            myMAPF.getMapAsync(this);

        }

        mResultReceiver = new AddressResultReceiver(new Handler());

        if (checkPlayServices()) {
            // If this check succeeds, proceed with normal processing.
            // Otherwise, prompt user to get valid Play Services APK.
            if (!AppUtils.isLocationEnabled(getActivity())) {
                // notify user
                AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity());
                dialog.setMessage("Location not enabled!");
                dialog.setPositiveButton("Open location settings", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                        Intent myIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        startActivity(myIntent);
                    }
                });
                dialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                        // TODO Auto-generated method stub

                    }
                });
                dialog.show();
            }
            buildGoogleApiClient();
        } else {
            Toast.makeText(getActivity(), "Location not supported in this device", Toast.LENGTH_SHORT).show();
        }

//        if (Utils.validateString(mSharedPreferences.getString(Utils.CALL_CITY_NAME, ""))) {
//            getCityName();
//            Utils.storeString(mSharedPreferences, Utils.CALL_CITY_NAME, "");
//        }

        if (Utils.validateString(mSharedPreferences.getString(Utils.currentAddress, ""))) {

            addressString = mSharedPreferences.getString(Utils.currentAddress, "");
            curLatitude = Double.parseDouble(mSharedPreferences.getString(Utils.latitude, ""));
            curLongitude = Double.parseDouble(mSharedPreferences.getString(Utils.longitude, ""));

        } /*else {
            try {
                Geocoder geocoder = new Geocoder(getActivity(), Locale.getDefault());
                List<Address> addressList = geocoder.getFromLocation(curLatitude, curLongitude, 1);

                if (addressList != null && addressList.size() > 0) {
                    Address address = addressList.get(0);
                    StringBuilder sb = new StringBuilder();
                    for (int i = 0; i < address.getMaxAddressLineIndex(); i++) {
                        sb.append(address.getAddressLine(i)).append("\n");
                    }
                    addressString = sb.toString();
                    Log.e(TAG, "Address :: " + addressString);

                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }*/

//        atvPlaces.setText(addressString);
//        locationMarkertext.setText(addressString);

        if (googleMap != null) {
            googleMap.clear();

            point = new LatLng(curLatitude, curLongitude);
//            cameraPosition = new CameraPosition.Builder()
//                    .target(point)
//                    .zoom(15.2f)
//                    .tilt(70)
//                    .build();

//            googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
            googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(point, 14.0f));

        }

    }

    /*private void getHandymanCity(double lat,double lng){
        try {
//            lat = 21.1702;
//            lng = 72.8311;
            Geocoder geocoder = new Geocoder(getActivity(), Locale.getDefault());
            List<Address> addresses = geocoder.getFromLocation(lat, lng, 1);
            cityHandyman = addresses.get(0).getLocality();
            Logger.e(TAG,"HANDYMAN CITY :: " + cityHandyman);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }*/

    private void openAutocompleteActivity() {
        try {
            // The autocomplete activity requires Google Play Services to be available. The intent
            // builder checks this and throws an exception if it is not the case.
            Intent intent = new PlaceAutocomplete.IntentBuilder(PlaceAutocomplete.MODE_FULLSCREEN).build(getActivity());
            startActivityForResult(intent, REQUEST_CODE_AUTOCOMPLETE);
        } catch (GooglePlayServicesRepairableException e) {
            // Indicates that Google Play Services is either not installed or not up to date. Prompt
            // the user to correct the issue.
            GoogleApiAvailability.getInstance().getErrorDialog(getActivity(), e.getConnectionStatusCode(),
                    0 /* requestCode */).show();
        } catch (GooglePlayServicesNotAvailableException e) {
            // Indicates that Google Play Services is not available and the problem is not easily
            // resolvable.
            String message = "Google Play Services is not available: " +
                    GoogleApiAvailability.getInstance().getErrorString(e.errorCode);

            Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
        }
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnConfirm:
                if (Utils.validateString(addressString)) {
                    mCityOutput = mSharedPreferences.getString(Utils.CITY_NAME, "");
                    if (Utils.validateString(mCityOutput)) {
                        getCityName(mCityOutput, category_id, sub_category_id);
                    } else {
                        Utils.showMessageDialog(getActivity(), "Alert", "Service not available in selected city");
                    }
                    /*Utils.addressFlag = true;
                    Utils.storeString(mSharedPreferences, Utils.currentAddress, addressString);
                    Utils.storeString(mSharedPreferences, Utils.latitude, String.valueOf(curLatitude));
                    Utils.storeString(mSharedPreferences, Utils.longitude, String.valueOf(curLongitude));
                    getActivity().getSupportFragmentManager().popBackStack();
                    Logger.e(TAG, "Current Address :: " + mSharedPreferences.getString(Utils.currentAddress, ""));*/
                }

                    /*if ("Ahmedabad".equalsIgnoreCase(mCityOutput)) {
                        Utils.storeString(mSharedPreferences, Utils.currentAddress, addressString);

                        getActivity().getSupportFragmentManager().popBackStack();
                        Logger.e(TAG, "Current Address :: " + mSharedPreferences.getString(Utils.currentAddress, ""));
                    } else {
//                        if(Utils.validateString(mCityOutput)) {
//                            Utils.showMessageDialog(getActivity(), "Alert", "Service not available in " + mCityOutput);
//                        } else {
                        Utils.showMessageDialog(getActivity(), "Alert", "Service not available in selected city");
//                        }
                    }*/

                break;

            case R.id.address_layout:
//			if (v != null) {
//        	    InputMethodManager imm = (InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
//        	    imm.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(), 0);
//        	}
                mMapIsTouched = true;
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, new AddressMapSearchNewFragment()).setCustomAnimations(R.anim.slide_out_right, 0).addToBackStack(null).commit();
//                openAutocompleteActivity();
                break;
        }

    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_MOVE) {
            mIsScrolling = true;
            locationMarkertext.setText("Pin Location");
        }
        return false;
    }

    @Override
    public void onMapReady(GoogleMap map) {
        Log.d(TAG, "OnMapReady");
        googleMap = map;

        googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        googleMap.setMyLocationEnabled(true);
        googleMap.setBuildingsEnabled(true);
        googleMap.getUiSettings().setZoomControlsEnabled(false);
        googleMap.getUiSettings().setCompassEnabled(false);
        googleMap.getUiSettings().setRotateGesturesEnabled(true);
        googleMap.getUiSettings().setMyLocationButtonEnabled(true);
        googleMap.getUiSettings().setZoomGesturesEnabled(true);

        View locationButton = ((View) mRootView.findViewById(1).getParent()).findViewById(2);
        RelativeLayout.LayoutParams rlp = (RelativeLayout.LayoutParams) locationButton.getLayoutParams();
        rlp.addRule(RelativeLayout.ALIGN_PARENT_TOP, 0);
        rlp.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE);
        rlp.setMargins(0, 0, 60, 40);

        if (googleMap != null) {
            // Getting LocationManager object from System Service
            // LOCATION_SERVICE
            LocationManager locationManager = (LocationManager) mActivity.getSystemService(Context.LOCATION_SERVICE);

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

            if (provider != null) {

                if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    return;
                }
                Location currentLocation = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                if (currentLocation != null) {
                    curLatitude = currentLocation.getLatitude();
                    curLongitude = currentLocation.getLongitude();
                }

                locationManager.requestLocationUpdates(provider, 2000, 0, new LocationListener() {

                    @Override
                    public void onStatusChanged(String arg0, int arg1, Bundle arg2) {
                    }

                    @Override
                    public void onProviderEnabled(String arg0) {
                    }

                    @Override
                    public void onProviderDisabled(String arg0) {
                    }

                    @Override
                    public void onLocationChanged(Location location) {

                        curLatitude = location.getLatitude();
                        curLongitude = location.getLongitude();


                    }
                });
            }

        }

        if (Utils.validateString(mSharedPreferences.getString(Utils.currentAddress, ""))) {
//            atvPlaces.setText("");
            addressString = mSharedPreferences.getString(Utils.currentAddress, "");
//            atvPlaces.setText(addressString);
//            locationMarkertext.setText(addressString);
            curLatitude = Double.parseDouble(mSharedPreferences.getString(Utils.latitude, ""));
            curLongitude = Double.parseDouble(mSharedPreferences.getString(Utils.longitude, ""));
        }

        if (googleMap != null) {
            googleMap.clear();

            point = new LatLng(curLatitude, curLongitude);
//            cameraPosition = new CameraPosition.Builder()
//                    .target(point)
//                    .zoom(15.2f)
//                    .tilt(70)
//                    .build();

//            googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
            googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(point, 14.0f));

        }

        googleMap.setOnCameraChangeListener(new GoogleMap.OnCameraChangeListener() {
            @Override
            public void onCameraChange(CameraPosition cameraPosition) {
                if (!mMapIsTouched) {
                    Log.d("Camera postion change" + "", cameraPosition + "");
//                    point = cameraPosition.target;
                    point = googleMap.getCameraPosition().target;

                    googleMap.clear();

                    try {

                        Location mLocation = new Location("");
                        mLocation.setLatitude(point.latitude);
                        mLocation.setLongitude(point.longitude);

                        curLatitude = point.latitude;
                        curLongitude = point.longitude;

                        String lat = String.valueOf(curLatitude);
                        String lng = String.valueOf(curLongitude);


                        if (!lat.substring(0, 6).equalsIgnoreCase(mSharedPreferences.getString(Utils.latitude, ""))
                                && !lng.substring(0, 6).equalsIgnoreCase(mSharedPreferences.getString(Utils.longitude, ""))) {
                            Utils.storeString(mSharedPreferences, Utils.currentAddress, "");
                        }


                        Utils.storeString(mSharedPreferences, Utils.latitude, lat.substring(0, 6));
                        Utils.storeString(mSharedPreferences, Utils.longitude, lng.substring(0, 6));
                        Logger.e(TAG, "onMapReady Latitude ::" + curLatitude + " Longitude ::" + curLongitude);


                        startIntentService(mLocation);

                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                } else {

                    if (Utils.validateString(mSharedPreferences.getString(Utils.currentAddress, ""))) {
                        addressString = mSharedPreferences.getString(Utils.currentAddress, "");
                        curLatitude = Double.parseDouble(mSharedPreferences.getString(Utils.latitude, ""));
                        curLongitude = Double.parseDouble(mSharedPreferences.getString(Utils.longitude, ""));

                        Geocoder gcd = new Geocoder(getActivity(), Locale.getDefault());
                        List<Address> addresses = null;
                        try {
                            addresses = gcd.getFromLocation(curLatitude, curLongitude, 1);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        if (addresses != null && addresses.size() > 0) {
                            if (Utils.validateString(addresses.get(0).getLocality())) {
                                System.out.println(addresses.get(0).getLocality());
                                mCityOutput = addresses.get(0).getLocality();
                                Utils.storeString(mSharedPreferences, Utils.CITY_NAME, mCityOutput);
                            }
                        } else {
                            Utils.storeString(mSharedPreferences, Utils.CITY_NAME, "");
                        }

                    }
                    if (Utils.validateString(addressString)) {
                        displayAddressOutput();
                    } else {
                        Location mLocation = new Location("");
                        mLocation.setLatitude(curLatitude);
                        mLocation.setLongitude(curLongitude);

                        startIntentService(mLocation);
                    }
                }
            }
        });
        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
    }

    protected void startIntentService(Location mLocation) {
        // Create an intent for passing to the intent service responsible for fetching the address.
        Intent intent = new Intent(getActivity(), FetchAddressIntentService.class);

        // Pass the result receiver as an extra to the service.
        intent.putExtra(AppUtils.LocationConstants.RECEIVER, mResultReceiver);

        // Pass the location data as an extra to the service.
        intent.putExtra(AppUtils.LocationConstants.LOCATION_DATA_EXTRA, mLocation);

        // Start the service. If the service isn't already running, it is instantiated and started
        // (creating a process for it if needed); if it is running then it remains running. The
        // service kills itself automatically once all intents are processed.
        getActivity().startService(intent);
    }

    @Override
    public void onConnected(Bundle bundle) {

        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        Location mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        if (mLastLocation != null) {
            changeMap(mLastLocation);
            Log.d(TAG, "ON connected");

        } else
            try {
                LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);

            } catch (Exception e) {
                e.printStackTrace();
            }
        try {
            LocationRequest mLocationRequest = new LocationRequest();
            mLocationRequest.setInterval(10000);
            mLocationRequest.setFastestInterval(5000);
            mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void changeMap(Location location) {

        Log.d(TAG, "Reaching map" + googleMap);


        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        // check if map is created successfully or not
        if (googleMap != null) {
            googleMap.getUiSettings().setZoomControlsEnabled(false);
//            LatLng latLong;


            point = new LatLng(location.getLatitude(), location.getLongitude());

            curLatitude = location.getLatitude();
            curLongitude = location.getLongitude();

//            CameraPosition cameraPosition = new CameraPosition.Builder()
//                    .target(point).zoom(17f).tilt(30).build();
//
//            googleMap.setMyLocationEnabled(true);
//            googleMap.getUiSettings().setMyLocationButtonEnabled(true);
//            googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

//            mLocationMarkerText.setText("Lat : " + location.getLatitude() + "," + "Long : " + location.getLongitude());
            try {
                Geocoder geocoder = new Geocoder(getActivity(), Locale.getDefault());
                List<Address> addressList = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);

                if (addressList != null && addressList.size() > 0) {
                    Address address = addressList.get(0);
                    StringBuilder sb = new StringBuilder();
                    for (int i = 0; i < address.getMaxAddressLineIndex(); i++) {
                        sb.append(address.getAddressLine(i));
                    }
//                    if (Utils.validateString(mSharedPreferences.getString(Utils.currentAddress, ""))) {
//                        addressString = mSharedPreferences.getString(Utils.currentAddress, "");
//                    } else {
                    addressString = sb.toString();
//                    }

                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            Logger.e(TAG, "changeMap" + addressString);
//            atvPlaces.setText(addressString);
//            locationMarkertext.setText(addressString);

//            cameraPosition = new CameraPosition.Builder()
//                    .target(point)
//                    .zoom(16.0f)
//                    .tilt(70)
//                    .build();

            googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(point, 14.0f));
//            googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

            startIntentService(location);


        } else {
            Toast.makeText(getActivity(), "Sorry! unable to create maps", Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.i(TAG, "Connection suspended");
        mGoogleApiClient.connect();
    }

    @Override
    public void onLocationChanged(Location location) {
        try {
            if (location != null)
                changeMap(location);
            LocationServices.FusedLocationApi.removeLocationUpdates(
                    mGoogleApiClient, this);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }

    @SuppressLint("ParcelCreator")
    class AddressResultReceiver extends ResultReceiver {
        public AddressResultReceiver(Handler handler) {
            super(handler);
            locationLoading.setVisibility(View.VISIBLE);
        }

        /**
         * Receives data sent from FetchAddressIntentService and updates the UI in MainActivity.
         */
        @Override
        protected void onReceiveResult(int resultCode, Bundle resultData) {

            // Display the address string or an error message sent from the intent service.
            mAddressOutput = resultData.getString(AppUtils.LocationConstants.RESULT_DATA_KEY);
            mAreaOutput = resultData.getString(AppUtils.LocationConstants.LOCATION_DATA_AREA);
            mCityOutput = resultData.getString(AppUtils.LocationConstants.LOCATION_DATA_CITY);
            Utils.storeString(mSharedPreferences, Utils.CITY_NAME, mCityOutput);

            if (!Utils.validateString(mAddressOutput)) {
                Utils.storeString(mSharedPreferences, Utils.currentAddress, "");
                mAddressOutput = resultData.getString(AppUtils.LocationConstants.LOCATION_DATA_STREET);
            }

            if (!Utils.validateString(mAddressOutput))
                mAddressOutput = "no address found";

//            curLatitude = Double.parseDouble(resultData.getString(AppUtils.LocationConstants.LOCATION_DATA_LATITUDE));
//            curLongitude = Double.parseDouble(resultData.getString(AppUtils.LocationConstants.LOCATION_DATA_LONGITUDE));

            displayAddressOutput();


            // Show a toast message if an address was found.
            if (resultCode == AppUtils.LocationConstants.SUCCESS_RESULT) {
                //  showToast(getString(R.string.address_found));

            } else {
                locationMarkertext.setText(getString(R.string.no_address_found));
            }


        }

    }

    protected void displayAddressOutput() {
        //  mLocationAddressTextView.setText(mAddressOutput);
        try {
//            if (mAddressOutput != null && curLatitude != 0 && curLongitude != 0) {
            // mLocationText.setText(mAreaOutput+ "");
            locationLoading.setVisibility(View.GONE);

//            try {
//                Geocoder geocoder = new Geocoder(getActivity(), Locale.getDefault());
//                List<Address> addressList = geocoder.getFromLocation(point.latitude, point.longitude, 1);
//
//                if (addressList != null && addressList.size() > 0) {
//                    Address address = addressList.get(0);
//                    StringBuilder sb = new StringBuilder();
//                    for (int i = 0; i < address.getMaxAddressLineIndex(); i++) {
//                        sb.append(address.getAddressLine(i)).append(", ");
//                    }
//
//                    if ((!mSharedPreferences.getString(Utils.currentAddress, "").isEmpty())) {
//                        addressString = mSharedPreferences.getString(Utils.currentAddress, "");
//                    } else {
//                        addressString = sb.toString();
//                    }
//
//                }
//            } catch (IOException e) {
//                e.printStackTrace();
//            }

            if ((!mSharedPreferences.getString(Utils.currentAddress, "").isEmpty())) {
                addressString = mSharedPreferences.getString(Utils.currentAddress, "");
            } else {
                addressString = mAddressOutput;
            }

            locationMarkertext.setText(addressString);
//            }
        } catch (Exception e) {

            e.printStackTrace();
        }
    }

    private boolean checkPlayServices() {
        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(getActivity());
        if (resultCode != ConnectionResult.SUCCESS) {
            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
                GooglePlayServicesUtil.getErrorDialog(resultCode, getActivity(),
                        PLAY_SERVICES_RESOLUTION_REQUEST).show();
            } else {
                //finish();
            }
            return false;
        }
        return true;
    }

    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(getActivity())
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
    }

    private void getCityName(String cityName, String categoryId, String subCategoryId) {

        try {
            if (Utils.checkInternetConnection(getActivity())) {
                GetAllCityNameTask getAllCityNameTask = new GetAllCityNameTask(getActivity());
                getAllCityNameTask.setAsyncCallListener(new AsyncCallListener() {

                    @Override
                    public void onResponseReceived(Object response) {
                        DataModel dataModel = (DataModel) response;
                        if (dataModel.getSuccess().equalsIgnoreCase("1")) {
                            Utils.addressFlag = true;
                            if (Utils.validateString(addressString)) {
                                mCityOutput = mSharedPreferences.getString(Utils.CITY_NAME, "");
                                if (Utils.validateString(mCityOutput)) {
                                    Utils.storeString(mSharedPreferences, Utils.currentAddress, addressString);
                                    Utils.storeString(mSharedPreferences, Utils.latitude, String.valueOf(curLatitude));
                                    Utils.storeString(mSharedPreferences, Utils.longitude, String.valueOf(curLongitude));
                                    getActivity().getSupportFragmentManager().popBackStack();
                                    Logger.e(TAG, "Current Address :: " + mSharedPreferences.getString(Utils.currentAddress, ""));
                                } else {
                                    if (Utils.validateString(mCityOutput)) {
                                        Utils.showMessageDialog(getActivity(), "Alert", "Service not available in " + mCityOutput);
                                    } else {
                                        Utils.showMessageDialog(getActivity(), "Alert", "Service not available in selected city");
                                    }
                                }

                            } else {
                                if (Utils.validateString(mCityOutput)) {
                                    Utils.showMessageDialog(getActivity(), "Alert", "Service not available in " + mCityOutput);
                                } else {
                                    Utils.showMessageDialog(getActivity(), "Alert", "Service not available in selected city");
                                }
                            }

                        } else if (dataModel.getSuccess().equalsIgnoreCase("0")) {
                            Utils.addressFlag = false;
                            Utils.showMessageDialog(getActivity(), getResources().getString(R.string.alert), dataModel.getMessage());
                        }
                    }

                    @Override
                    public void onErrorReceived(String error) {
                        Toast.makeText(getActivity(), getString(R.string.try_again), Toast.LENGTH_SHORT).show();
                    }
                });
                getAllCityNameTask.execute(cityName, categoryId, subCategoryId);
            } else {
                Utils.showMessageDialog(getActivity(), getResources().getString(R.string.alert), getResources().getString(R.string.connection));
            }
        } catch (NullPointerException e) {
            e.printStackTrace();
        }

    }

//    private void getHandymanCity(String handyman_id) {
//        GetHandymanCityNameRequestTask cityNameRequestTask = new GetHandymanCityNameRequestTask(getActivity());
//        cityNameRequestTask.setAsyncCallListener(new AsyncCallListener() {
//
//            @SuppressWarnings("unchecked")
//            @Override
//            public void onResponseReceived(Object response) {
//                TermsAndConditionModel termsAndConditionModel = (TermsAndConditionModel) response;
//                if (termsAndConditionModel.getSuccess().equalsIgnoreCase("1")) {
//                    cityHandyman = termsAndConditionModel.getCityname().toString().trim();
//
//                } else if (termsAndConditionModel.getSuccess().equalsIgnoreCase("0")) {
//                    Toast.makeText(getActivity(), termsAndConditionModel.message, Toast.LENGTH_SHORT).show();
//                }
//
//            }
//
//            @Override
//            public void onErrorReceived(String error) {
//                Toast.makeText(getActivity(), getString(R.string.try_again), Toast.LENGTH_SHORT).show();
//            }
//        });
//        cityNameRequestTask.execute(handyman_id);
//    }


}
