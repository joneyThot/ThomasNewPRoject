package com.handyman.fragment;

import android.Manifest;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.handyman.MainActivity;
import com.handyman.R;
import com.handyman.SplashActivity;
import com.handyman.logger.Logger;
import com.handyman.service.DirectionsJSONParser;
import com.handyman.service.GPSTracker;
import com.handyman.service.Utils;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MapHandymanFragment extends BaseFragment {

	private static String TAG = "MapHandymanFragment";
	private SharedPreferences mSharedPreferences;

	GoogleMap map;
	SupportMapFragment myMAPF;
	GPSTracker gps;
	double curLatitude, curLongitude;
	View rootView;
	ArrayList<Marker> marraylst_marker = new ArrayList<Marker>();
	LatLngBounds.Builder builder;
	ArrayList<LatLng> markerPoints;
	LatLng mCurrentLng;
	LatLng mDestinationLatLng;

//	Marker marker;
//	Circle mCircle;

	Fragment fr;
	View mRootView;
	boolean showingFirst = true;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		if (mRootView != null) {
			ViewGroup parent = (ViewGroup) mRootView.getParent();
			if (parent != null)
				parent.removeView(mRootView);
		}
		try {
			mRootView = inflater.inflate(R.layout.fragment_handyman_map, container, false);
		} catch (InflateException e) {
			/* map is already there, just return view as it is */
		}

		initview();
		return mRootView;
	}

	private void initview() {
		mSharedPreferences = getActivity().getSharedPreferences(Utils.PREF_NAME, Context.MODE_PRIVATE);
		((MainActivity) getActivity()).setTitleText("", "HANDYMAN MAP", "",View.VISIBLE, View.GONE, View.GONE, View.GONE,View.GONE);
		getActivity().findViewById(R.id.title).setVisibility(View.GONE);
		getActivity().findViewById(R.id.txtUrl).setVisibility(View.GONE);

		mActivity.getSlidingMenu().setMode(SlidingMenu.LEFT);
		mActivity.getSlidingMenu().setTouchModeAbove(SlidingMenu.TOUCHMODE_NONE);

		String lat = "0.0", lng = "0.0";

		if (getArguments() != null) {
			lat = getArguments().getString(Utils.HANDYMAN_LATITUDE);
			lng = getArguments().getString(Utils.HANDYMAN_LONGITUDE);
		}

		double hmLat = 0.0f, hmLng = 0.0f;

		if (Utils.validateString(lat)) {
			hmLat = Double.parseDouble(lat);
		}
		if (Utils.validateString(lng)) {
			hmLng = Double.parseDouble(lng);
		}

		markerPoints = new ArrayList<LatLng>();
		if (map == null) {
			FragmentManager fm = getChildFragmentManager();
			myMAPF = (SupportMapFragment) fm.findFragmentById(R.id.handyman_map);
			if (myMAPF == null) {
				myMAPF = SupportMapFragment.newInstance();
				fm.beginTransaction().replace(R.id.map, myMAPF).commit();
			}

//			map = myMAPF.getMap();
			map.clear();

			map.setMapType(GoogleMap.MAP_TYPE_NORMAL);
			map.setMyLocationEnabled(true);
			map.setBuildingsEnabled(true);
			map.getUiSettings().setZoomControlsEnabled(false);

			View locationButton = ((View) mRootView.findViewById(1).getParent()).findViewById(2);
			RelativeLayout.LayoutParams rlp = (RelativeLayout.LayoutParams) locationButton.getLayoutParams();
			rlp.addRule(RelativeLayout.ALIGN_PARENT_TOP, 0);
			rlp.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE);
			rlp.setMargins(0, 0, 60, 40);

		}

		if (map != null) {
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
			// locationManager.getBestProvider(criteria, true);

			if (provider != null) {
				// Getting Current Location
				// Location currentLocation = locationManager.getLastKnownLocation(provider);
				Location currentLocation = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
				if (currentLocation != null) {
					SplashActivity.latitude = currentLocation.getLatitude();
					SplashActivity.longitude = currentLocation.getLongitude();

					Logger.e(TAG, "currentLocation -- " + SplashActivity.latitude + " & " + SplashActivity.longitude);
				}

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
				locationManager.requestLocationUpdates(provider, 100, 0, new LocationListener() {

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
						Logger.e(TAG, "onLocationChanged -- " + SplashActivity.latitude + " & " + SplashActivity.longitude);
					}
				});

				if(markerPoints.size()>1){
					markerPoints.clear();
                    map.clear();
                }

				final LatLngBounds.Builder builder = new LatLngBounds.Builder();

				mCurrentLng = new LatLng(SplashActivity.latitude, SplashActivity.longitude);
				mDestinationLatLng = new LatLng(hmLat, hmLng);
				markerPoints.add(mCurrentLng);
				markerPoints.add(mDestinationLatLng);

				MarkerOptions markerOptions = new MarkerOptions();
//				.icon(BitmapDescriptorFactory.fromResource(R.drawable.handyman_mascot))
//				.anchor(0.0f, 1.0f) // Anchors the marker on the bottom left
//				.position(new LatLng(curLatitude, curLongitude));

				markerOptions.position(mCurrentLng);
				markerOptions.position(mDestinationLatLng);

				 /**
                 * For the start location, the color of marker is GREEN and
                 * for the end location, the color of marker is RED.
                 */
				 if(markerPoints.size()==1){
					 markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));

                 }else if(markerPoints.size()==2){
                	 markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED))
                	 /*.anchor(0.0f, 1.0f)
                	 .position(mDestinationLatLng)*/;
                 }

                 // Add new marker to the Google Map Android API V2

				map.addMarker(markerOptions);
				map.animateCamera(CameraUpdateFactory.newLatLngZoom(mCurrentLng, 10));
//				builder.include(markerOptions.getPosition());
//				map.animateCamera(CameraUpdateFactory.newLatLngBounds(builder.build(), 10));

//				map.setOnCameraChangeListener(new OnCameraChangeListener() {
//					@Override
//					public void onCameraChange(CameraPosition arg0) {
//						// TODO Auto-generated method stub
//						map.animateCamera(CameraUpdateFactory.newLatLngBounds(builder.build(), 5));
//						map.setOnCameraChangeListener(null);
//					}
//				});

				 if(markerPoints.size() >= 2){
                     LatLng origin = markerPoints.get(0);
                     LatLng dest = markerPoints.get(1);

                     // Getting URL to the Google Directions API
                     String url = getDirectionsUrl(origin, dest);

                     DownloadTask downloadTask = new DownloadTask();

                     // Start downloading json data from Google Directions API
                     downloadTask.execute(url);
                 }
			}

		}

	}

	private String getDirectionsUrl(LatLng origin,LatLng dest){

        // Origin of route
        String str_origin = "origin="+origin.latitude+","+origin.longitude;

        // Destination of route
        String str_dest = "destination="+dest.latitude+","+dest.longitude;

        // Sensor enabled
        String sensor = "sensor=false";

        // Building the parameters to the web service
        String parameters = str_origin+"&"+str_dest+"&"+sensor;

        // Output format
        String output = "json";

        // Building the url to the web service
        String url = "https://maps.googleapis.com/maps/api/directions/"+output+"?"+parameters;

        return url;
    }

	 private String downloadUrl(String strUrl) throws IOException{
	        String data = "";
	        InputStream iStream = null;
	        HttpURLConnection urlConnection = null;
	        try{
	            URL url = new URL(strUrl);

	            // Creating an http connection to communicate with url
	            urlConnection = (HttpURLConnection) url.openConnection();

	            // Connecting to url
	            urlConnection.connect();

	            // Reading data from url
	            iStream = urlConnection.getInputStream();

	            BufferedReader br = new BufferedReader(new InputStreamReader(iStream));

	            StringBuffer sb = new StringBuffer();

	            String line = "";
	            while( ( line = br.readLine()) != null){
	                sb.append(line);
	            }

	            data = sb.toString();

	            br.close();

	        }catch(Exception e){
	            Logger.d("Exception while downloading url", e.toString());
	        }finally{
	            iStream.close();
	            urlConnection.disconnect();
	        }
	        return data;
	    }

	 // Fetches data from url passed
	    private class DownloadTask extends AsyncTask<String, Void, String>{

	        // Downloading data in non-ui thread
	        @Override
	        protected String doInBackground(String... url) {

	            // For storing data from web service
	            String data = "";

	            try{
	                // Fetching the data from web service
	                data = downloadUrl(url[0]);
	            }catch(Exception e){
	                Logger.d("Background Task",e.toString());
	            }
	            return data;
	        }

	        // Executes in UI thread, after the execution of
	        // doInBackground()
	        @Override
	        protected void onPostExecute(String result) {
	            super.onPostExecute(result);

	            ParserTask parserTask = new ParserTask();

	            // Invokes the thread for parsing the JSON data
	            parserTask.execute(result);
	        }
	    }

	    private class ParserTask extends AsyncTask<String, Integer, List<List<HashMap<String,String>>> >{

	        // Parsing the data in non-ui thread
	        @Override
	        protected List<List<HashMap<String, String>>> doInBackground(String... jsonData) {

	            JSONObject jObject;
	            List<List<HashMap<String, String>>> routes = null;

	            try{
	                jObject = new JSONObject(jsonData[0]);
	                DirectionsJSONParser parser = new DirectionsJSONParser();

	                // Starts parsing data
	                routes = parser.parse(jObject);
	            }catch(Exception e){
	                e.printStackTrace();
	            }
	            return routes;
	        }

	        // Executes in UI thread, after the parsing process
	        @Override
	        protected void onPostExecute(List<List<HashMap<String, String>>> result) {
	            ArrayList<LatLng> points = null;
	            PolylineOptions lineOptions = null;
	            MarkerOptions markerOptions = new MarkerOptions();

	            // Traversing through all the routes
	            for(int i=0;i<result.size();i++){
	                points = new ArrayList<LatLng>();
	                lineOptions = new PolylineOptions();

	                // Fetching i-th route
	                List<HashMap<String, String>> path = result.get(i);

	                // Fetching all the points in i-th route
	                for(int j=0;j<path.size();j++){
	                    HashMap<String,String> point = path.get(j);

	                    double lat = Double.parseDouble(point.get("lat"));
	                    double lng = Double.parseDouble(point.get("lng"));
	                    LatLng position = new LatLng(lat, lng);

	                    points.add(position);
	                }

	                // Adding all the points in the route to LineOptions
	                lineOptions.addAll(points);
	                lineOptions.width(10);
	                lineOptions.color(Color.RED);
	            }

	            // Drawing polyline in the Google Map for the i-th route
	            map.addPolyline(lineOptions);
	        }
	    }
}
