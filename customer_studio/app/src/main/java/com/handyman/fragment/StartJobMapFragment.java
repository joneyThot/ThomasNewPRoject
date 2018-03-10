package com.handyman.fragment;

import android.Manifest;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.OnMapReadyCallback;
import com.handyman.MainActivity;
import com.handyman.R;
import com.handyman.logger.Logger;
import com.handyman.model.DataModel;
import com.handyman.model.ProfileHandymanModel;
import com.handyman.model.TermsAndConditionModel;
import com.handyman.service.AsyncCallListener;
import com.handyman.service.GetHandymanLatLngRequestTask;
import com.handyman.service.Utils;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;

import java.util.ArrayList;
import java.util.List;

public class StartJobMapFragment extends BaseFragment implements OnClickListener, OnMapReadyCallback {

    private static String TAG = "StartJobMapFragment";
    private SharedPreferences mSharedPreferences;

    GoogleMap googleMap;
    SupportMapFragment myMAPF;
    LatLng point;
    double latitude = 0, longitude = 0, customer_lat = 0, customer_lng = 0;
    Circle mCircle;

    Fragment fr;
    View mRootView;
    String handyman_id = "", handyman_name = "", contact_person_name = "";
    Handler mHandler = new Handler();
    Runnable mRunnable;
    String category_name = "";
    Bitmap smallMarker;
    LatLng customerLatLng, handymanLatLng;
//	Runnable refresh;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (mRootView != null) {
            ViewGroup parent = (ViewGroup) mRootView.getParent();
            if (parent != null)
                parent.removeView(mRootView);
        }
        try {
            mRootView = inflater.inflate(R.layout.fragment_startjob_map, container, false);
        } catch (InflateException e) {
            /* map is already there, just return view as it is */
        }

        initview();
        return mRootView;
    }

    private void initview() {
        mSharedPreferences = getActivity().getSharedPreferences(Utils.PREF_NAME, Context.MODE_PRIVATE);
        if (getArguments() != null) {
            handyman_id = getArguments().getString(Utils.HIRE_HANDYMAN_ID);
            handyman_name = getArguments().getString(Utils.HIRE_HANDYMAN_NAME);
            customer_lat = Double.parseDouble(getArguments().getString(Utils.latitude));
            customer_lng = Double.parseDouble(getArguments().getString(Utils.longitude));
            contact_person_name = getArguments().getString(Utils.CONTACT_PERSON_NAME);
        }

        ((MainActivity) getActivity()).setTitleText("", handyman_name, "", View.VISIBLE, View.GONE, View.GONE, View.GONE, View.GONE);
        getActivity().findViewById(R.id.title).setVisibility(View.GONE);
        getActivity().findViewById(R.id.txtUrl).setVisibility(View.GONE);

        mActivity.getSlidingMenu().setMode(SlidingMenu.LEFT);
        mActivity.getSlidingMenu().setTouchModeAbove(SlidingMenu.TOUCHMODE_NONE);

        mRootView.findViewById(R.id.refresh).setOnClickListener(this);

        customerLatLng = new LatLng(customer_lat, customer_lng);

        if (googleMap == null) {
            FragmentManager fm = getChildFragmentManager();
            myMAPF = (SupportMapFragment) fm.findFragmentById(R.id.map);
            if (myMAPF == null) {
                myMAPF = SupportMapFragment.newInstance();
                fm.beginTransaction().replace(R.id.map, myMAPF).commit();
            }

            myMAPF.getMapAsync(this);

            if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }

        }
        getHandymanLatLng(handyman_id);
        doTheAutoRefresh();

    }

    private void doTheAutoRefresh() {
        mRunnable = new Runnable() {

            @Override
            public void run() {
                getHandymanLatLng(handyman_id);
                doTheAutoRefresh();
            }
        };
        mHandler.postDelayed(mRunnable, 20000);

    }

    @Override
    public void onDestroy() {

        mHandler.removeCallbacks(mRunnable);
        super.onDestroy();

    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.refresh:
                break;

        }
    }

    private void getHandymanLatLng(String handyman_id) {
        GetHandymanLatLngRequestTask getHandymanLatLngRequestTask = new GetHandymanLatLngRequestTask(getActivity());
        getHandymanLatLngRequestTask.setAsyncCallListener(new AsyncCallListener() {

            @SuppressWarnings("unchecked")
            @Override
            public void onResponseReceived(Object response) {
                TermsAndConditionModel termsAndConditionModel = (TermsAndConditionModel) response;
                if (termsAndConditionModel.getSuccess().equalsIgnoreCase("1")) {

                    latitude = Double.parseDouble(termsAndConditionModel.lat);
                    longitude = Double.parseDouble(termsAndConditionModel.lng);
                    handymanLatLng = new LatLng(latitude, longitude);
                    category_name = termsAndConditionModel.category;
                    openMap();

                } else if (termsAndConditionModel.getSuccess().equalsIgnoreCase("0")) {

                    Toast.makeText(getActivity(), termsAndConditionModel.message, Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void onErrorReceived(String error) {
                Toast.makeText(getActivity(), getString(R.string.try_again), Toast.LENGTH_SHORT).show();
            }
        });
        getHandymanLatLngRequestTask.execute(handyman_id);
    }

    private void openMap() {

        if (googleMap != null) {
            googleMap.clear();

            MarkerOptions markerOptions1 = new MarkerOptions()
                    .title(contact_person_name)
                    .snippet("Customer")
                    .icon(BitmapDescriptorFactory.fromBitmap(smallMarker))
                    .position(customerLatLng);

            googleMap.addMarker(markerOptions1);
//			googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(markerOptions1.getPosition(), 13f));

            if (category_name.equalsIgnoreCase("Electrical")) {

                MarkerOptions markerOptions = new MarkerOptions()
                        .title(handyman_name)
                        .snippet("Handyman")
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.handyman_electrical_xxxhdpi))
                        .position(handymanLatLng);

                googleMap.addMarker(markerOptions);
//				googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(markerOptions.getPosition(), 13f));

            } else if (category_name.equalsIgnoreCase("Plumbing")) {
                MarkerOptions markerOptions = new MarkerOptions()
                        .title(handyman_name)
                        .snippet("Handyman")
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.handyman_plumbing_xxxhdpi))
                        .position(handymanLatLng);

                googleMap.addMarker(markerOptions);
//				googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(markerOptions.getPosition(), 13f));

            } else if (category_name.equalsIgnoreCase("Cleaner")) {
                MarkerOptions markerOptions = new MarkerOptions()
                        .title(handyman_name)
                        .snippet("Handyman")
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.handyman_cleaning_xxxhdpi))
                        .position(handymanLatLng);

                googleMap.addMarker(markerOptions);
//				googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(markerOptions.getPosition(), 13f));

            } else if (category_name.equalsIgnoreCase("Carpenter")) {
                MarkerOptions markerOptions = new MarkerOptions()
                        .title(handyman_name)
                        .snippet("Handyman")
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.handyman_carpenter_xxxhdpi))
                        .position(handymanLatLng);

                googleMap.addMarker(markerOptions);
//				googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(markerOptions.getPosition(), 13f));


            } else {
                // default
                MarkerOptions markerOptions = new MarkerOptions()
                        .title(handyman_name)
                        .snippet("Handyman")
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.handyman_default_xxxhdpi))
                        .position(handymanLatLng);

                googleMap.addMarker(markerOptions);
//				googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(markerOptions.getPosition(), 13f));
            }

            LatLngBounds.Builder builder = new LatLngBounds.Builder();
            builder.include(handymanLatLng);
            builder.include(customerLatLng);
            googleMap.animateCamera(CameraUpdateFactory.newLatLngBounds(builder.build(), 200));
//			makeURL(customer_lat,customer_lng,latitude,longitude);
        }

    }

    @Override
    public void onMapReady(GoogleMap mMap) {
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

        if (googleMap != null) {
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
                // Getting Current Location
                // Location currentLocation = locationManager.getLastKnownLocation(provider);
                if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
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
                }


                locationManager.requestLocationUpdates(provider, 10000, 0, new LocationListener() {

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
                    }
                });
            }

        }


        int height = 100;
        int width = 100;
        BitmapDrawable bitmapdraw = (BitmapDrawable) getResources().getDrawable(R.drawable.menu_icon_home);
        Bitmap b = bitmapdraw.getBitmap();
        smallMarker = Bitmap.createScaledBitmap(b, width, height, false);

//		if(googleMap != null) {
//			googleMap.clear();
//			MarkerOptions markerOptions = new MarkerOptions()
//					.title(contact_person_name)
//					.snippet("Customer")
//					.icon(BitmapDescriptorFactory.fromBitmap(smallMarker))
//					.position(new LatLng(customer_lat, customer_lng));
//
//			googleMap.addMarker(markerOptions);
//			googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(markerOptions.getPosition(), 13f));
//		}

        googleMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {

            @Override
            public View getInfoWindow(Marker arg0) {
                View v = mActivity.getLayoutInflater().inflate(R.layout.row_map_title_rating, null);
                ((TextView) v.findViewById(R.id.handyman_name_textview)).setText(arg0.getTitle());

                v.findViewById(R.id.handyman_details_textview).setVisibility(View.VISIBLE);
                ((TextView) v.findViewById(R.id.handyman_details_textview)).setText(arg0.getSnippet());

                v.findViewById(R.id.ratingCount).setVisibility(View.GONE);
                v.findViewById(R.id.busy_layout).setVisibility(View.GONE);

                return v;
            }

            @Override
            public View getInfoContents(Marker arg0) {
                return null;

            }
        });
    }

	/*public String makeURL (double sourcelat, double sourcelog, double destlat, double destlog ){
		StringBuilder urlString = new StringBuilder();
		urlString.append("http://maps.googleapis.com/maps/api/directions/json");
		urlString.append("?origin=");// from
		urlString.append(Double.toString(sourcelat));
		urlString.append(",");
		urlString
				.append(Double.toString( sourcelog));
		urlString.append("&destination=");// to
		urlString
				.append(Double.toString( destlat));
		urlString.append(",");
		urlString.append(Double.toString( destlog));
		urlString.append("&sensor=false&mode=driving&alternatives=true");
		urlString.append("&key=" + getResources().getString(R.string.server_api_key));
		return urlString.toString();
	}*/

}
