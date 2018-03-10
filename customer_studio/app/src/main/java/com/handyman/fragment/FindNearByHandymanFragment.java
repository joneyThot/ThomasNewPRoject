package com.handyman.fragment;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Point;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Display;
import android.view.InflateException;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.InfoWindowAdapter;
import com.google.android.gms.maps.GoogleMap.OnInfoWindowClickListener;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.handyman.MainActivity;
import com.handyman.R;
import com.handyman.SplashActivity;
import com.handyman.logger.Logger;
import com.handyman.model.AdvertiseListModel;
import com.handyman.model.DataModel;
import com.handyman.model.HandymanModel;
import com.handyman.service.AsyncCallListener;
import com.handyman.service.GPSTracker;
import com.handyman.service.GetAllHandymanListRequestTask;
import com.handyman.service.GetKilometersRequestTask;
import com.handyman.service.SearchByNameHandymanListRequestTask;
import com.handyman.service.Utils;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;

import java.util.ArrayList;
import java.util.Collections;

public class FindNearByHandymanFragment extends BaseFragment implements OnClickListener, TextWatcher, OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, LocationListener {

    private static String TAG = "FindNearByHandymanFragment";
    private SharedPreferences mSharedPreferences;

    private ArrayList<HandymanModel> mHandymanModelsList = new ArrayList<HandymanModel>();
    private ArrayList<HandymanModel> mSearchHandyman = new ArrayList<HandymanModel>();

    GoogleMap googleMap;
    SupportMapFragment myMAPF;
    GPSTracker gps;
    //	double curLatitude, curLongitude;
    View rootView;
    ArrayList<Marker> marraylst_marker = new ArrayList<Marker>();
    LatLngBounds.Builder builder;
    ArrayList<LatLng> markerPoints;
    LatLng point;
    Marker marker;
    Circle mCircle;

    Fragment fr;
    View mRootView;
    EditText mSearchEditText;
    ImageView mSearchImg, mRefreshImg;
    String category_id = "", sub_category_id = "", category_name = "", buzy = "";
    boolean showingFirst = true, mapFlag = true;
    private int mDeviceWidth = 480;
    float zoomlevel;

    GoogleApiClient mGoogleApiClient;
    LocationRequest mLocationRequest;
    Location mLastLocation;
    Marker mCurrLocationMarker;
    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;
    private ProgressDialog mProgressDialog;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (mRootView != null) {
            ViewGroup parent = (ViewGroup) mRootView.getParent();
            if (parent != null)
                parent.removeView(mRootView);
        }
        try {
            mRootView = inflater.inflate(R.layout.fragment_find_near_by_handyman, container, false);
        } catch (InflateException e) {
            /* map is already there, just return view as it is */
        }

        WindowManager w = ((Activity) getActivity()).getWindowManager();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            Point size = new Point();
            w.getDefaultDisplay().getSize(size);
            mDeviceWidth = size.x;
        } else {
            Display d = w.getDefaultDisplay();
            mDeviceWidth = d.getWidth();
        }

//        SplashActivity.latitude = 19.0760;
//        SplashActivity.longitude = 72.8777;
        initview();
        return mRootView;
    }

    private void initview() {
        mSharedPreferences = getActivity().getSharedPreferences(Utils.PREF_NAME, Context.MODE_PRIVATE);
        if (getArguments() != null) {
            ((MainActivity) getActivity()).setTitleText("", getString(R.string.menu_find_near_by_handyman), "", View.VISIBLE, View.GONE, View.GONE, View.VISIBLE, View.GONE);
            getActivity().findViewById(R.id.title).setVisibility(View.GONE);
            getActivity().findViewById(R.id.txtUrl).setVisibility(View.GONE);
        } else {
            ((MainActivity) getActivity()).setTitleText(getString(R.string.menu_find_near_by_handyman), "", "", View.GONE, View.VISIBLE, View.GONE, View.VISIBLE, View.GONE);
            getActivity().findViewById(R.id.title_back).setVisibility(View.GONE);
            getActivity().findViewById(R.id.txtUrl).setVisibility(View.GONE);
        }

        String mapZoom = mSharedPreferences.getString(Utils.ZOOMLEVEL, "");
        zoomlevel = Float.parseFloat(mapZoom);

        mActivity.getSlidingMenu().setMode(SlidingMenu.LEFT);
        mActivity.getSlidingMenu().setTouchModeAbove(SlidingMenu.TOUCHMODE_NONE);

        category_id = mSharedPreferences.getString(Utils.CATEGORY_ID, "");
        category_name = mSharedPreferences.getString(Utils.CATE_NAME, "");
        sub_category_id = mSharedPreferences.getString(Utils.SUB_CATEGORY_ID, "");

        mRefreshImg = (ImageView) mRootView.findViewById(R.id.refresh);
        mRefreshImg.setOnClickListener(this);
        mRefreshImg.setVisibility(View.GONE);

        mSearchEditText = (EditText) mRootView.findViewById(R.id.handyman_id_edittext);
        mSearchEditText.addTextChangedListener(this);
        mSearchEditText.setOnEditorActionListener(new OnEditorActionListener() {

            @Override
            public boolean onEditorAction(TextView arg0, int arg1, KeyEvent arg2) {
                if (arg1 == EditorInfo.IME_ACTION_SEARCH) {

                    searchHandyman(mSearchEditText.getText().toString(), category_id, sub_category_id, SplashActivity.latitude, SplashActivity.longitude);
                    return true;
                }
                return false;
            }
        });
        mSearchImg = (ImageView) mRootView.findViewById(R.id.searchBtn);
        mSearchImg.setOnClickListener(this);
        mSearchImg.setVisibility(View.GONE);

        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            checkLocationPermission();
        }

        if (googleMap == null) {
            FragmentManager fm = getChildFragmentManager();
            myMAPF = (SupportMapFragment) fm.findFragmentById(R.id.map);
            if (myMAPF == null) {
                myMAPF = SupportMapFragment.newInstance();
                fm.beginTransaction().replace(R.id.map, myMAPF).commit();
            }

            myMAPF.getMapAsync(this);

        }

        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        mProgressDialog = new ProgressDialog(getActivity());
        mProgressDialog.setMessage(getActivity().getResources().getString(R.string.loading));
//        mProgressDialog.setCanceledOnTouchOutside(false);
//        mProgressDialog.setCancelable(false);

        if (mapFlag == false) {
            mSearchImg.setVisibility(View.VISIBLE);
            mRefreshImg.setVisibility(View.VISIBLE);
        }

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.searchBtn:
                if (Utils.checkInternetConnection(getActivity())) {
                    if (showingFirst == true) {

                        mSearchEditText.setVisibility(View.VISIBLE);
                        mSearchImg.setBackgroundResource(R.color.title_color);

                        if (mSearchEditText.getText().toString().trim().length() > 0) {
                            searchHandyman(mSearchEditText.getText().toString(), category_id, sub_category_id, SplashActivity.latitude, SplashActivity.longitude);
                            showingFirst = false;
                            mSearchEditText.setVisibility(View.VISIBLE);
                            mSearchImg.setBackgroundResource(R.color.title_color);
                        } else {

                            if (showingFirst == true) {
                                mSearchEditText.setVisibility(View.VISIBLE);
                                mSearchImg.setBackgroundResource(R.color.title_color);
                                showingFirst = false;
                            } else {
                                mSearchEditText.setVisibility(View.INVISIBLE);
                                mSearchImg.setBackgroundResource(R.color.white);
                                showingFirst = true;
                            }
                        }

                    } else {

                        if (mSearchEditText.getText().toString().trim().equalsIgnoreCase("")) {
                            mSearchEditText.setVisibility(View.INVISIBLE);
                            mSearchImg.setBackgroundResource(R.color.white);
                            showingFirst = true;
                        } else {
                            searchHandyman(mSearchEditText.getText().toString(), category_id, sub_category_id, SplashActivity.latitude, SplashActivity.longitude);
                        }
                    }


                    InputMethodManager imm = (InputMethodManager) mActivity.getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(mSearchEditText.getWindowToken(), 0);

                } else {
                    Utils.showMessageDialog(getActivity(), getResources().getString(R.string.alert),
                            getResources().getString(R.string.connection));
                }
                break;

            case R.id.refresh:
                if (Utils.validateString(sub_category_id)) {
                    getKoilometer();
                }
                break;

        }
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
    }

    @Override
    public void afterTextChanged(Editable s) {
    }

    private void getKoilometer() {
        try {
            if (Utils.checkInternetConnection(getActivity())) {
                GetKilometersRequestTask getKilometersRequestTask = new GetKilometersRequestTask(getActivity());
                getKilometersRequestTask.setAsyncCallListener(new AsyncCallListener() {

                    @SuppressWarnings("unchecked")
                    @Override
                    public void onResponseReceived(Object response) {
                        AdvertiseListModel kilomerterModel = (AdvertiseListModel) response;
                        if (kilomerterModel.getSuccess().equalsIgnoreCase("1")) {

                            if (Utils.validateString(kilomerterModel.getOption_value())) {

                                float km = Float.parseFloat(kilomerterModel.getOption_value());

//                            double r = km * 10000;
//                            zoomlevel = calculateZoomLevel(mDeviceWidth, r);
                                double zoom = 19 - Math.log((km * 15) * 5.508);

                                zoomlevel = ((float) zoom);
                                Logger.e(TAG, "Zoom_level :" + zoomlevel);
                                Utils.storeString(mSharedPreferences, Utils.ZOOMLEVEL, String.valueOf(zoomlevel));

                                if (Utils.validateString(sub_category_id)) {
                                    getAllHandyman(SplashActivity.latitude, SplashActivity.longitude, category_id, sub_category_id);
                                }
                            } else {
                                Toast.makeText(getActivity(), "Kilometer range not get", Toast.LENGTH_SHORT).show();
                            }

                        } else if (kilomerterModel.getSuccess().equalsIgnoreCase("0")) {
                            Utils.showMessageDialog(getActivity(), getResources().getString(R.string.alert), kilomerterModel.getMessage());
                        }

                    }

                    @Override
                    public void onErrorReceived(String error) {
                        Toast.makeText(getActivity(), getString(R.string.try_again), Toast.LENGTH_SHORT).show();
                    }
                });
                getKilometersRequestTask.execute();
            }
        } catch (NullPointerException e) {
            e.printStackTrace();
        }

    }

    private void getAllHandyman(double lat, double lng, String category, String sub_category) {
        try {
            if (Utils.checkInternetConnection(getActivity())) {
                GetAllHandymanListRequestTask getAllHandymanListRequestTask = new GetAllHandymanListRequestTask(getActivity());
                getAllHandymanListRequestTask.setAsyncCallListener(new AsyncCallListener() {

                    @SuppressWarnings("unchecked")
                    @Override
                    public void onResponseReceived(Object response) {
                        mHandymanModelsList.clear();
                        mSearchHandyman.clear();

                        DataModel dataModel = (DataModel) response;

                        if (dataModel.getSuccess().equalsIgnoreCase("1")) {
                            if (mProgressDialog != null && mProgressDialog.isShowing()) {
                                mProgressDialog.dismiss();
                            }
                            mHandymanModelsList.addAll(dataModel.getHandymanModels());

                            Logger.e(TAG, "mHandymanModelsList SIZE -- " + mHandymanModelsList.size());
                            Collections.reverse(mHandymanModelsList);
                            if (mHandymanModelsList.size() > 0) {
                                setView(mHandymanModelsList);
                            }


                        } else if (dataModel.getSuccess().equalsIgnoreCase("0")) {
                            if (mProgressDialog != null && mProgressDialog.isShowing()) {
                                mProgressDialog.dismiss();
                            }
                            Utils.showMessageDialog(getActivity(), getResources().getString(R.string.alert), dataModel.getMessage());
                            LatLng latLng = new LatLng(SplashActivity.latitude, SplashActivity.longitude);
                            googleMap.addMarker(new MarkerOptions().position(latLng));
                            googleMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
                            googleMap.animateCamera(CameraUpdateFactory.zoomTo(zoomlevel));

                            googleMap.clear();
                        }
                    }

                    @Override
                    public void onErrorReceived(String error) {
                        Toast.makeText(getActivity(), getString(R.string.try_again), Toast.LENGTH_SHORT).show();
                    }
                });
                getAllHandymanListRequestTask.execute(String.valueOf(lat), String.valueOf(lng), category, sub_category);
            } else {
                Utils.showMessageDialog(getActivity(), getResources().getString(R.string.alert),
                        getResources().getString(R.string.connection));
            }
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
    }


    private void searchHandyman(String name, String category, String sub_category, double lat, double lng) {
        try {
            if (Utils.checkInternetConnection(getActivity())) {
                SearchByNameHandymanListRequestTask searchByNameHandymanListRequestTask = new SearchByNameHandymanListRequestTask(getActivity());
                searchByNameHandymanListRequestTask.setAsyncCallListener(new AsyncCallListener() {

                    @SuppressWarnings("unchecked")
                    @Override
                    public void onResponseReceived(Object response) {

                        InputMethodManager imm = (InputMethodManager) mActivity.getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(mSearchEditText.getWindowToken(), 0);
                        mHandymanModelsList.clear();
                        mSearchHandyman.clear();
                        DataModel dataModel = (DataModel) response;
                        if (dataModel.getSuccess().equalsIgnoreCase("1")) {
                            mSearchHandyman.addAll(dataModel.getSearchHandymanModels());
                            Logger.e(TAG, "mSearchHandyman SIZE -- " + mSearchHandyman.size());
                            if (mSearchHandyman.size() > 0) {
                                setSearchView(mSearchHandyman);
                            }
                        } else if (dataModel.getSuccess().equalsIgnoreCase("0")) {
                            Toast.makeText(getActivity(), dataModel.getMessage(), Toast.LENGTH_SHORT).show();
                        }

                    }

                    @Override
                    public void onErrorReceived(String error) {
                        Toast.makeText(getActivity(), getString(R.string.try_again), Toast.LENGTH_SHORT).show();
                    }
                });
                searchByNameHandymanListRequestTask.execute(name, category, sub_category, String.valueOf(lat), String.valueOf(lng));
            } else {
                Utils.showMessageDialog(getActivity(), getResources().getString(R.string.alert),
                        getResources().getString(R.string.connection));
            }
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
    }

    private void setView(ArrayList<HandymanModel> handymanlist) {
        if (isAdded()) {
            try {
                googleMap.clear();
            } catch (Exception e) {
                e.printStackTrace();
            }

            for (int i = 0; i < handymanlist.size(); i++) {

                if (Utils.validateString(handymanlist.get(i).lat) && Utils.validateString(handymanlist.get(i).lng)) {
                    double latitude = Double.parseDouble(handymanlist.get(i).lat);
                    double longitude = Double.parseDouble(handymanlist.get(i).lng);
                    String first_name = "";
                    if (Utils.validateString(handymanlist.get(i).getFirstname())) {
                        first_name = handymanlist.get(i).getFirstname().toString();
                    }
                    if (Utils.validateString(handymanlist.get(i).getLastname())) {
                        first_name = first_name + " " + handymanlist.get(i).getLastname();
                    }

                    String email = "";
                    if (Utils.validateString(handymanlist.get(i).email)) {
                        email = handymanlist.get(i).email;
                    }

                    String distance = handymanlist.get(i).distance;
                    if (distance.contains(".")) {
                        int index = distance.indexOf(".");
                        if (distance.length() > (index + 3)) {
                            distance = distance.substring(0, index + 3);
                        }
                    }

                    CircleOptions circleOptions = new CircleOptions().center(new LatLng(latitude, longitude));
                    mCircle = googleMap.addCircle(circleOptions);


                    int handymanIcon;
                    if (category_name.equalsIgnoreCase("Electrical")) {
                        handymanIcon = R.drawable.handyman_electrical_xxxhdpi;
                    } else if (category_name.equalsIgnoreCase("Plumbing")) {
                        handymanIcon = R.drawable.handyman_plumbing_xxxhdpi;
                    } else if (category_name.equalsIgnoreCase("Cleaner")) {
                        handymanIcon = R.drawable.handyman_cleaning_xxxhdpi;
                    } else if (category_name.equalsIgnoreCase("Carpenter")) {
                        handymanIcon = R.drawable.handyman_carpenter_xxxhdpi;
                    } else {
                        handymanIcon = R.drawable.handyman_default_xxxhdpi;
                    }

                    MarkerOptions markerOptions = new MarkerOptions()
                            .title(first_name)
                            .snippet(handymanlist.get(i).id + "&#" + email + "&#" + handymanlist.get(i).rating /*+ "&#" + handymanlist.get(i).busy*/)
                            .icon(BitmapDescriptorFactory.fromResource(handymanIcon))
                            .anchor(0.0f, 1.0f) // Anchors the marker on the bottom left
                            .position(new LatLng(mCircle.getCenter().latitude, mCircle.getCenter().longitude));

                    googleMap.addMarker(markerOptions);
                    googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(markerOptions.getPosition(), zoomlevel));


                }
            }
        }

    }

    private void setSearchView(ArrayList<HandymanModel> handymanlist) {
        if (isAdded()) {
            try {
                googleMap.clear();
            } catch (Exception e) {
                e.printStackTrace();
            }

            LatLng handymanLatLng;
            for (int i = 0; i < handymanlist.size(); i++) {

                if (Utils.validateString(handymanlist.get(i).lat) && Utils.validateString(handymanlist.get(i).lng)) {
                    double latitude = Double.parseDouble(handymanlist.get(i).lat);
                    double longitude = Double.parseDouble(handymanlist.get(i).lng);
                    String first_name = "";
                    if (Utils.validateString(handymanlist.get(i).getFirstname())) {
                        first_name = handymanlist.get(i).getFirstname().toString();
                    }
                    if (Utils.validateString(handymanlist.get(i).getLastname())) {
                        first_name = first_name + " " + handymanlist.get(i).getLastname();
                    }

                    String email = "";
                    if (Utils.validateString(handymanlist.get(i).email)) {
                        email = handymanlist.get(i).email;
                    }

//                    String distance = hadymanlist.get(i).distance;
//                    if (distance.contains(".")) {
//                        int index = distance.indexOf(".");
//                        if (distance.length() > (index + 3)) {
//                            distance = distance.substring(0, index + 3);
//                        }
//                    }

                    CircleOptions circleOptions = new CircleOptions().center(new LatLng(latitude, longitude));
                    mCircle = googleMap.addCircle(circleOptions);


                    handymanLatLng = new LatLng(latitude, longitude);

                    int handymanIcon;
                    if (category_name.equalsIgnoreCase("Electrical")) {
                        handymanIcon = R.drawable.handyman_electrical_xxxhdpi;
                    } else if (category_name.equalsIgnoreCase("Plumbing")) {
                        handymanIcon = R.drawable.handyman_plumbing_xxxhdpi;
                    } else if (category_name.equalsIgnoreCase("Cleaner")) {
                        handymanIcon = R.drawable.handyman_cleaning_xxxhdpi;
                    } else if (category_name.equalsIgnoreCase("Carpenter")) {
                        handymanIcon = R.drawable.handyman_carpenter_xxxhdpi;
                    } else {
                        handymanIcon = R.drawable.handyman_default_xxxhdpi;
                    }

                    MarkerOptions markerOptions = new MarkerOptions()
                            .title(first_name)
                            .snippet(handymanlist.get(i).id + "&#" + email + "&#" + handymanlist.get(i).rating /*+ "&#" + handymanlist.get(i).busy*/)
                            .icon(BitmapDescriptorFactory.fromResource(handymanIcon))
                            .anchor(0.0f, 1.0f) // Anchors the marker on the bottom left
                            .position(new LatLng(mCircle.getCenter().latitude, mCircle.getCenter().longitude));

                    googleMap.addMarker(markerOptions);
//                    googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(markerOptions.getPosition(), zoomlevel));

                    LatLngBounds.Builder builder = new LatLngBounds.Builder();
                    builder.include(new LatLng(SplashActivity.latitude, SplashActivity.longitude));
                    builder.include(handymanLatLng);
//                    googleMap.setPadding(150, 150, 150, 150);
                    googleMap.animateCamera(CameraUpdateFactory.newLatLngBounds(builder.build(), 250));

                }
            }
        }

    }

    private int calculateZoomLevel(int screenWidth, double range) {
        double equatorLength = 40075004; // in meters
        double widthInPixels = screenWidth;
        double metersPerPixel = equatorLength / 256;
        int zoomLevel = 1;
        while ((metersPerPixel * widthInPixels) > range) {
            metersPerPixel /= 2;
            ++zoomLevel;
        }
        Logger.i(TAG, "zoom level = " + zoomLevel);
        return zoomLevel;
    }

    @Override
    public void onMapReady(GoogleMap mMap) {
        Logger.e(TAG, "onMapReady" + "onMapReady");
        googleMap = mMap;
        googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        googleMap.setMyLocationEnabled(true);
        googleMap.setBuildingsEnabled(true);
        googleMap.getUiSettings().setZoomControlsEnabled(false);
        googleMap.getUiSettings().setCompassEnabled(true);
        googleMap.getUiSettings().setRotateGesturesEnabled(true);
        googleMap.getUiSettings().setMyLocationButtonEnabled(true);
        googleMap.getUiSettings().setZoomGesturesEnabled(true);

        View locationButton = ((View) mRootView.findViewById(1).getParent()).findViewById(2);
        RelativeLayout.LayoutParams rlp = (RelativeLayout.LayoutParams) locationButton.getLayoutParams();
        rlp.addRule(RelativeLayout.ALIGN_PARENT_TOP, 0);
        rlp.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE);
        rlp.setMargins(0, 0, 60, 40);

        if (mProgressDialog != null && !mProgressDialog.isShowing()) {
            mProgressDialog.show();
        }

        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(12.9716, 77.5946), 6.0f));

        //Initialize Google Play Services
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                buildGoogleApiClient();
                mMap.setMyLocationEnabled(true);
            }
        } else {
            buildGoogleApiClient();
            mMap.setMyLocationEnabled(true);
        }

        if (googleMap != null) {

            googleMap.setInfoWindowAdapter(new InfoWindowAdapter() {

                @Override
                public View getInfoWindow(Marker arg0) {
                    mSearchEditText.setVisibility(View.INVISIBLE);
                    mSearchImg.setBackgroundResource(R.color.white);
                    showingFirst = true;

                    View v = mActivity.getLayoutInflater().inflate(R.layout.row_map_title_rating, null);
                    ((TextView) v.findViewById(R.id.handyman_name_textview)).setText(arg0.getTitle());

                    final String str[] = arg0.getSnippet().split("&#");
                    if (str.length > 1) {
                        String rnumber[] = str[1].split(" ");
                        if (!(rnumber[0].equals("0"))) {
                            v.findViewById(R.id.handyman_details_textview).setVisibility(View.VISIBLE);
                            ((TextView) v.findViewById(R.id.handyman_details_textview)).setText(str[1]);
                        } else {
                            v.findViewById(R.id.handyman_details_textview).setVisibility(View.GONE);
                        }

                        ((RatingBar) v.findViewById(R.id.ratingCount)).setRating(Float.parseFloat(str[2]));
//						buzy = str[3];

                        v.findViewById(R.id.busy_layout).setVisibility(View.GONE);
                        v.findViewById(R.id.handyman_buzy_textview).setVisibility(View.GONE);
                        v.findViewById(R.id.read_buzy_textview).setVisibility(View.GONE);

                       /* if (str[3].equalsIgnoreCase("0")) {
                            v.findViewById(R.id.busy_layout).setVisibility(View.GONE);
//							v.findViewById(R.id.handyman_buzy_textview).setVisibility(View.GONE);
//							v.findViewById(R.id.read_buzy_textview).setVisibility(View.GONE);

                        } else {
                            v.findViewById(R.id.busy_layout).setVisibility(View.VISIBLE);
                            v.findViewById(R.id.handyman_buzy_textview).setVisibility(View.VISIBLE);
                            v.findViewById(R.id.read_buzy_textview).setVisibility(View.VISIBLE);
                        }*/

                    }
                    return v;
                }

                @Override
                public View getInfoContents(Marker arg0) {
                    return null;

                }
            });

            googleMap.setOnInfoWindowClickListener(new OnInfoWindowClickListener() {
                @Override
                public void onInfoWindowClick(Marker marker) {
                    InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(getView().getWindowToken(), 0);

                    final String str[] = marker.getSnippet().split("&#");

                    if (Utils.validateString(str[0])) {

//                        if (str[3].equalsIgnoreCase("0")) {
                        Utils.storeString(mSharedPreferences, Utils.HANDYMAN_ID, str[0]);
                        Utils.storeString(mSharedPreferences, Utils.HANDYMAN_RATING, str[2]);
                        HandymanProfileFragment handymanProfileFragment = new HandymanProfileFragment();
                        Bundle bundle = new Bundle();
                        bundle.putString(Utils.CATEGORY_ID, category_id);
                        bundle.putString(Utils.SUB_CATEGORY_ID, sub_category_id);
                        handymanProfileFragment.setArguments(bundle);
                        getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, handymanProfileFragment).addToBackStack(null).commit();

//                        } else {
//                            Utils.showMessageDialog(getActivity(), getResources().getString(R.string.alert), "Handyman is busy.");
//                        }
                    }


                }
            });
        }
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {

        try {
            if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }

            mLocationRequest = new LocationRequest();
            mLocationRequest.setInterval(1000);
            mLocationRequest.setFastestInterval(1000);
            mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
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
                mRefreshImg.setVisibility(View.VISIBLE);
                mSearchImg.setVisibility(View.VISIBLE);
                mapFlag = false;

                SplashActivity.latitude = location.getLatitude();
                SplashActivity.longitude = location.getLongitude();
                getAllHandyman(SplashActivity.latitude, SplashActivity.longitude, category_id, sub_category_id);

                //stop location updates
                if (mGoogleApiClient != null) {
                    LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(getActivity())
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        mGoogleApiClient.connect();
    }

    public boolean checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION)) {
                ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, MY_PERMISSIONS_REQUEST_LOCATION);
            } else {
                ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, MY_PERMISSIONS_REQUEST_LOCATION);
            }
            return false;
        } else {
            return true;
        }
    }
}
