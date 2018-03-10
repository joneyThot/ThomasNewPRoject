package com.handyman.fragment;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ListView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NoConnectionError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
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
import com.handyman.MainActivity;
import com.handyman.R;
import com.handyman.SplashActivity;
import com.handyman.adapter.CategoryAdapter;
import com.handyman.adapter.SubCategoryAdapter;
import com.handyman.logger.Logger;
import com.handyman.model.CategoryListModel;
import com.handyman.service.Utils;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class ServiceAtHomeFragment extends BaseFragment implements OnClickListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {

    private static String TAG = "ServiceAtHomeFragment";

    private SharedPreferences mSharedPreferences;
    private ArrayList<CategoryListModel> mCategoryArrayList = new ArrayList<CategoryListModel>();

    SubCategoryAdapter subCategoryAdapter;
    CategoryAdapter categoryAdapter;

    Fragment fr;
    View mRootView;
    ListView mCategoryList/*, mSubCategoryList*/;
    private GoogleApiClient googleApiClient;
    private LocationRequest locationRequest;
    final static int REQUEST_LOCATION = 199;
    boolean firstTime = true, isDialogOpen = false/*, nextCall = false*/;
    final private int REQUEST_CODE_ASK_PERMISSIONS = 123;
    private ProgressDialog mProgressDialog;
    RequestQueue requestQueue;

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
        mRootView = inflater.inflate(R.layout.fragment_home, container, false);
        Utils.onCategoryClickFlag = false;
        initview();
        return mRootView;
    }

    private void initview() {
        requestQueue = Volley.newRequestQueue(getActivity());
        mSharedPreferences = getActivity().getSharedPreferences(Utils.PREF_NAME, Context.MODE_PRIVATE);
        ((MainActivity) getActivity()).setTitleText(getString(R.string.home_searvice_at_home), "", "", View.GONE, View.VISIBLE, View.GONE, View.GONE, View.GONE);
        getActivity().findViewById(R.id.title_back).setVisibility(View.GONE);
        getActivity().findViewById(R.id.txtUrl).setVisibility(View.GONE);

        mActivity.getSlidingMenu().setMode(SlidingMenu.LEFT);
        mActivity.getSlidingMenu().setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);

        mCategoryList = (ListView) mRootView.findViewById(R.id.category_list);

        SlidingMenuFragment.selectMenu(1);

        LocationManager manager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
        boolean statusOfGPS = manager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        if (!statusOfGPS) {
            enableLoc();
        } else {
            buildGoogleApiClient();
        }
//        getCategory(String.valueOf(SplashActivity.latitude), String.valueOf(SplashActivity.longitude));

        mProgressDialog = new ProgressDialog(getActivity());
        mProgressDialog.setMessage(getActivity().getResources().getString(R.string.loading));
        if (mProgressDialog != null && !mProgressDialog.isShowing()) {
            mProgressDialog.show();
        }
    }

    @Override
    public void onClick(View v) {
        // TODO Auto-generated method stub
        switch (v.getId()) {
        }
    }

    /*private void getCategory(String lat, String lng) {
        try {
            if (Utils.checkInternetConnection(getActivity())) {
                GetCategoryListRequestTask getCategoryListTask = new GetCategoryListRequestTask(getActivity());
                getCategoryListTask.setAsyncCallListener(new AsyncCallListener() {

                    @SuppressWarnings("unchecked")
                    @Override
                    public void onResponseReceived(Object response) {
                        mCategoryArrayList.clear();
                        DataModel dataModel = (DataModel) response;
                        if (dataModel.getSuccess().equalsIgnoreCase("1")) {
                            mCategoryArrayList.addAll(dataModel.getCategoryListModels());

                            Logger.e(TAG, "mCategoryArrayList SIZE -- " + mCategoryArrayList.size());
                            if (mCategoryArrayList.size() > 0) {
                                categoryAdapter = new CategoryAdapter(getActivity(), mCategoryArrayList, onCategoryClickListener);
                                mCategoryList.setAdapter(categoryAdapter);
                                categoryAdapter.notifyDataSetChanged();

                            }
                        } else if (dataModel.getSuccess().equalsIgnoreCase("0")) {
                            if (Utils.validateString(dataModel.getMessage())) {
                                Utils.showMessageDialog(getActivity(), getResources().getString(R.string.alert), dataModel.getMessage());
                            }
                        }
                    }

                    @Override
                    public void onErrorReceived(String error) {
                        Toast.makeText(getActivity(), getString(R.string.try_again), Toast.LENGTH_SHORT).show();
                    }
                });
                getCategoryListTask.execute(lat, lng);
            } else {
                Utils.showMessageDialog(getActivity(), getResources().getString(R.string.alert),
                        getResources().getString(R.string.connection));
            }
        } catch (NullPointerException e) {
            e.printStackTrace();
        }

    }*/

    public void getCategoryVollay(String lat, String lng) {

        if (lat.equalsIgnoreCase("0.0"))
            lat = "0";
        if (lng.equalsIgnoreCase("0.0"))
            lng = "0";

        mProgressDialog = new ProgressDialog(getActivity());
        mProgressDialog.setMessage(getString(R.string.loading));
        mProgressDialog.setCanceledOnTouchOutside(false);
        mProgressDialog.setCancelable(false);
        mProgressDialog.show();

        JSONObject jsonMain = new JSONObject();
        JSONObject jsonData = new JSONObject();
        JSONObject jsonUser = new JSONObject();

        try {
            jsonUser.accumulate("lat", lat);
            jsonUser.accumulate("lng", lng);
            jsonData.accumulate("category", jsonUser);
            jsonMain.accumulate("data", jsonData);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Logger.e(TAG, "URL :: " + Utils.URL_SERVER_ADDRESS + Utils.GET_CATEGORY_LIST);
        Logger.e(TAG, "Request :: " + String.valueOf(jsonMain));
        JsonObjectRequest jsObjRequest = new JsonObjectRequest
                (Request.Method.POST, Utils.URL_SERVER_ADDRESS + Utils.GET_CATEGORY_LIST, jsonMain, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Logger.e(TAG, "Response :: " + String.valueOf(response));
                        mProgressDialog.dismiss();
                        mCategoryArrayList.clear();
                        try {
                            String success = response.getString("success");
                            String msg = response.getString("message");

                            if (success.equalsIgnoreCase("1")) {
                                if (!response.isNull("data")) {
                                    JSONArray jArray = response.getJSONArray("data");
                                    for (int i = 0; i < jArray.length(); i++) {
                                        JSONObject jObj = jArray.getJSONObject(i);
                                        CategoryListModel categoryListModel = new CategoryListModel();
                                        categoryListModel.setId(jObj.getString("id"));
                                        categoryListModel.setName(jObj.getString("name"));
                                        categoryListModel.setImg(jObj.getString("img"));
                                        categoryListModel.setImg_path(jObj.getString("img_path"));
                                        categoryListModel.setIs_deleted(jObj.getString("is_deleted"));
                                        categoryListModel.setStatus(jObj.getString("status"));
                                        categoryListModel.setCreated_date(jObj.getString("created_date"));
                                        categoryListModel.setModified_date(jObj.getString("modified_date"));
                                        mCategoryArrayList.add(categoryListModel);
                                    }

                                    Logger.e(TAG, "mCategoryArrayList SIZE -- " + mCategoryArrayList.size());
                                    if (mCategoryArrayList.size() > 0) {
                                        categoryAdapter = new CategoryAdapter(getActivity(), mCategoryArrayList, onCategoryClickListener);
                                        mCategoryList.setAdapter(categoryAdapter);
                                        categoryAdapter.notifyDataSetChanged();

                                    }

                                } else {
                                    Utils.showMessageDialog(getActivity(), getResources().getString(R.string.alert), "No Category Found.");
                                }

                            } else if (success.equalsIgnoreCase("0")) {
                                if (Utils.validateString(msg))
                                    Utils.showMessageDialog(getActivity(), getResources().getString(R.string.alert), msg);
                                else
                                    Utils.showMessageDialog(getActivity(), getResources().getString(R.string.alert),
                                            "Server is not responding right now, Please try again later..");
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                            mProgressDialog.dismiss();
                            Logger.e("ERR", e.getMessage());
                            Utils.showMessageDialog(getActivity(), getResources().getString(R.string.alert), e.getMessage());
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        if (error instanceof NoConnectionError) {
                            mProgressDialog.dismiss();
                            Utils.showMessageDialog(getActivity(), getResources().getString(R.string.alert),
                                    getResources().getString(R.string.connection));
                        } else {
                            mProgressDialog.dismiss();
//                            Logger.e(TAG, "Response error : " + error.getMessage());
//                            Utils.showMessageDialog(getActivity(), getResources().getString(R.string.alert),
//                                    "Server is not responding right now, Please try again later.");

                        }
                    }
                });

        int socketTimeout = 30000;//30 seconds - change to what you want
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        jsObjRequest.setRetryPolicy(policy);
        requestQueue.add(jsObjRequest);
    }

    public void getCategoryCityNameVollay(String cityName) {

        mProgressDialog = new ProgressDialog(getActivity());
        mProgressDialog.setMessage(getString(R.string.loading));
        mProgressDialog.setCanceledOnTouchOutside(false);
        mProgressDialog.setCancelable(false);
        mProgressDialog.show();

        JSONObject jsonMain = new JSONObject();
        JSONObject jsonData = new JSONObject();
        JSONObject jsonUser = new JSONObject();

        try {
            jsonUser.accumulate("city_name", cityName);
            jsonData.accumulate("category", jsonUser);
            jsonMain.accumulate("data", jsonData);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Logger.e(TAG, "URL :: " + Utils.URL_SERVER_ADDRESS + Utils.GET_CATEGORY_LIST);
        Logger.e(TAG, "Request :: " + String.valueOf(jsonMain));
        JsonObjectRequest jsObjRequest = new JsonObjectRequest
                (Request.Method.POST, Utils.URL_SERVER_ADDRESS + Utils.GET_CATEGORY_LIST, jsonMain, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Logger.e(TAG, "Response :: " + String.valueOf(response));
                        mProgressDialog.dismiss();
                        mCategoryArrayList.clear();
                        try {
                            String success = response.getString("success");
                            String msg = response.getString("message");

                            if (success.equalsIgnoreCase("1")) {
                                if (!response.isNull("data")) {
                                    JSONArray jArray = response.getJSONArray("data");
                                    for (int i = 0; i < jArray.length(); i++) {
                                        JSONObject jObj = jArray.getJSONObject(i);
                                        CategoryListModel categoryListModel = new CategoryListModel();
                                        categoryListModel.setId(jObj.getString("id"));
                                        categoryListModel.setName(jObj.getString("name"));
                                        categoryListModel.setImg(jObj.getString("img"));
                                        categoryListModel.setImg_path(jObj.getString("img_path"));
                                        categoryListModel.setIs_deleted(jObj.getString("is_deleted"));
                                        categoryListModel.setStatus(jObj.getString("status"));
                                        categoryListModel.setCreated_date(jObj.getString("created_date"));
                                        categoryListModel.setModified_date(jObj.getString("modified_date"));
                                        mCategoryArrayList.add(categoryListModel);
                                    }

                                    Logger.e(TAG, "mCategoryArrayList SIZE -- " + mCategoryArrayList.size());
                                    if (mCategoryArrayList.size() > 0) {
                                        categoryAdapter = new CategoryAdapter(getActivity(), mCategoryArrayList, onCategoryClickListener);
                                        mCategoryList.setAdapter(categoryAdapter);
                                        categoryAdapter.notifyDataSetChanged();

                                    }

                                } else {
                                    Utils.showMessageDialog(getActivity(), getResources().getString(R.string.alert), "No Category Found.");
                                }

                            } else if (success.equalsIgnoreCase("0")) {
                                if (Utils.validateString(msg))
                                    Utils.showMessageDialog(getActivity(), getResources().getString(R.string.alert), msg);
//                                else
//                                    Utils.showMessageDialog(getActivity(), getResources().getString(R.string.alert),
//                                            "Server is not responding right now, Please try again later..");
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                            mProgressDialog.dismiss();
                            Logger.e("ERR", e.getMessage());
                            Utils.showMessageDialog(getActivity(), getResources().getString(R.string.alert), e.getMessage());
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        if (error instanceof NoConnectionError) {
                            mProgressDialog.dismiss();
                            Utils.showMessageDialog(getActivity(), getResources().getString(R.string.alert),
                                    getResources().getString(R.string.connection));
                        } else {
                            mProgressDialog.dismiss();
//                            Logger.e(TAG, "Response error : " + error.getMessage());
                            Utils.showMessageDialog(getActivity(), getResources().getString(R.string.alert),
                                    "Server is not responding right now, Please try again later.");

                        }
                    }
                });
        int socketTimeout = 30000;//30 seconds - change to what you want
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        jsObjRequest.setRetryPolicy(policy);
        requestQueue.add(jsObjRequest);
    }


    OnClickListener onCategoryClickListener = new OnClickListener() {

        @Override
        public void onClick(View v) {
            try {
                if (v.getTag() != null) {
                    Utils.onCategoryClickFlag = true;
                    int index = (Integer) v.getTag();
                    Logger.e(TAG, "CATEGORY CLICK INDEX ::" + index);
                    Utils.storeString(mSharedPreferences, Utils.CATEGORY_ID, mCategoryArrayList.get(index).getId());
                    ServiceAtHomeSubCategoryFragment serviceAtHomeSubCategoryFragment = new ServiceAtHomeSubCategoryFragment();
                    Bundle bundle = new Bundle();
                    bundle.putSerializable(Utils.CATEGORY_ITEM_DETAILS, mCategoryArrayList.get(index));
                    Utils.storeString(mSharedPreferences, Utils.CATE_NAME, mCategoryArrayList.get(index).getName());
                    serviceAtHomeSubCategoryFragment.setArguments(bundle);
                    getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, serviceAtHomeSubCategoryFragment).addToBackStack(null).commit();
                }
            } catch (ArrayIndexOutOfBoundsException e) {
                e.printStackTrace();
            }
        }
    };

    private void enableLoc() {

        if (googleApiClient == null) {
            googleApiClient = new GoogleApiClient.Builder(getActivity())
                    .addApi(LocationServices.API)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .build();

            googleApiClient.connect();

            locationRequest = LocationRequest.create();
            locationRequest.setInterval(5000);
            locationRequest.setFastestInterval(1000);
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
//                        getCategory(String.valueOf(SplashActivity.latitude), String.valueOf(SplashActivity.longitude));
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
        if (googleApiClient != null) {
            if (googleApiClient.isConnected()) {
                LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, locationRequest, this);
            }
        }

    }

    @Override
    public void onStart() {
        if (googleApiClient != null) {
            googleApiClient.connect();
            Logger.e(TAG, "onStart()");
        }
        super.onStart();
    }


    @Override
    public void onResume() {
        // TODO Auto-generated method stub
        super.onResume();
        if (!Utils.onCategoryClickFlag) {
            if (Utils.checkInternetConnection(getActivity())) {
                if (googleApiClient != null) {
                    if (googleApiClient.isConnected()) {
                        startLocationUpdates();
                        Logger.e(TAG, "onResume()");
                    }
                }
            }
        }
    }


    @Override
    public void onStop() {
//        if (googleApiClient != null) {
//            googleApiClient.disconnect();
//            Logger.e(TAG, "onStop()");
//        }
        if (requestQueue != null) {
            requestQueue.cancelAll("all");
        }
        super.onStop();
    }

    @Override
    public void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
        Logger.d(TAG, "onDestroy");
    }


    @Override
    public void onConnected(@Nullable Bundle bundle) {

        try {
            if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }

            locationRequest = new LocationRequest();
            locationRequest.setInterval(1000);
            locationRequest.setFastestInterval(1000);
            locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
            LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, locationRequest, this);
        } catch (NullPointerException e) {
            e.printStackTrace();
            if (mProgressDialog != null && mProgressDialog.isShowing()) {
                mProgressDialog.dismiss();
            }
        }
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onLocationChanged(Location location) {
        try {
            if (location != null) {
                if (googleApiClient != null) {
                    SplashActivity.latitude = location.getLatitude();
                    SplashActivity.longitude = location.getLongitude();

                    Geocoder gcd = new Geocoder(getActivity(), Locale.getDefault());
                    List<Address> addresses = null;
                    try {
                        addresses = gcd.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    String cityName = "";
                    if (addresses != null && addresses.size() > 0) {
                        cityName = addresses.get(0).getLocality();
                        Logger.e(TAG, "City Name :" + cityName);
                    }
                    if (mProgressDialog != null && mProgressDialog.isShowing()) {
                        mProgressDialog.dismiss();
                    }
                    Logger.e(TAG, "UpdateLatLang()" + "LATITUDE :: " + location.getLatitude() + ", LONGITUDE :: " + location.getLongitude());

//                    getCategoryVollay(String.valueOf(location.getLatitude()), String.valueOf(location.getLongitude()));

                    getCategoryCityNameVollay(cityName);

                    LocationServices.FusedLocationApi.removeLocationUpdates(googleApiClient, this);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }


    protected synchronized void buildGoogleApiClient() {
        googleApiClient = new GoogleApiClient.Builder(getActivity())
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        googleApiClient.connect();
    }


}
