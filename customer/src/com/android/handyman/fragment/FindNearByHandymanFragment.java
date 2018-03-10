package com.android.handyman.fragment;

import java.util.ArrayList;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.InflateException;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import android.widget.Toast;

import com.android.handyman.MainActivity;
import com.android.handyman.R;
import com.android.handyman.SplashActivity;
import com.android.handyman.logger.Logger;
import com.android.handyman.model.DataModel;
import com.android.handyman.model.HandymanModel;
import com.android.handyman.service.AsyncCallListener;
import com.android.handyman.service.GPSTracker;
import com.android.handyman.service.GetAllHandymanListRequestTask;
import com.android.handyman.service.SearchByNameHandymanListRequestTask;
import com.android.handyman.service.Utils;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.InfoWindowAdapter;
import com.google.android.gms.maps.GoogleMap.OnInfoWindowClickListener;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;

public class FindNearByHandymanFragment extends BaseFragment implements OnClickListener, TextWatcher{
	
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
	ImageView mSearchImg;
	String category_id = "" ,sub_category_id = "", category_name = "", buzy = ""; 
	boolean showingFirst = true;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,	Bundle savedInstanceState) {
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
		
		initview();
		return mRootView;
	}

	private void initview() {
		mSharedPreferences = getActivity().getSharedPreferences(Utils.PREF_NAME, Context.MODE_PRIVATE); 
		if(getArguments() != null){
			((MainActivity) getActivity()).setTitleText("", getString(R.string.menu_find_near_by_handyman), View.VISIBLE, View.GONE, View.GONE, View.VISIBLE);
			getActivity().findViewById(R.id.title).setVisibility(View.GONE);
		} else {
			((MainActivity) getActivity()).setTitleText(getString(R.string.menu_find_near_by_handyman), "", View.GONE, View.VISIBLE, View.GONE, View.VISIBLE);
			getActivity().findViewById(R.id.title_back).setVisibility(View.GONE);
		}
		
		mActivity.getSlidingMenu().setMode(SlidingMenu.LEFT);
		mActivity.getSlidingMenu().setTouchModeAbove(SlidingMenu.TOUCHMODE_NONE);
		
		category_id = mSharedPreferences.getString(Utils.CATEGORY_ID, "");
		category_name = mSharedPreferences.getString(Utils.CATE_NAME, "");
		sub_category_id = mSharedPreferences.getString(Utils.SUB_CATEGORY_ID, "");
		
		mRootView.findViewById(R.id.refresh).setOnClickListener(this);
		mSearchEditText = (EditText) mRootView.findViewById(R.id.handyman_id_edittext);
		mSearchEditText.addTextChangedListener(this);
		mSearchEditText.setOnEditorActionListener(new OnEditorActionListener() {

			@Override
			public boolean onEditorAction(TextView arg0, int arg1, KeyEvent arg2) {
				if (arg1 == EditorInfo.IME_ACTION_SEARCH) {

					InputMethodManager imm = (InputMethodManager) mActivity.getSystemService(Context.INPUT_METHOD_SERVICE);
					imm.hideSoftInputFromWindow(mSearchEditText.getWindowToken(), 0);
					searchHandyman(mSearchEditText.getText().toString(),category_id,sub_category_id);
//					searchHandyman();
				}
				return false;
			}
		});
		mSearchImg = (ImageView)mRootView.findViewById(R.id.searchBtn);
		mSearchImg.setOnClickListener(this);
		
		if(googleMap == null){
			FragmentManager fm = getChildFragmentManager();
			myMAPF = (SupportMapFragment) fm.findFragmentById(R.id.map);
			if (myMAPF == null) {
				myMAPF = SupportMapFragment.newInstance();
				fm.beginTransaction().replace(R.id.map, myMAPF).commit();
			}
			
			googleMap = myMAPF.getMap();
			googleMap.clear();
			
			googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
	        googleMap.setMyLocationEnabled(true);
	        googleMap.setBuildingsEnabled(true);
	        googleMap.getUiSettings().setZoomControlsEnabled(false);
	        
			View locationButton = ((View) mRootView.findViewById(1).getParent()).findViewById(2);
	        RelativeLayout.LayoutParams rlp = (RelativeLayout.LayoutParams) locationButton.getLayoutParams();
		    rlp.addRule(RelativeLayout.ALIGN_PARENT_TOP, 0);
		    rlp.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE);
		    rlp.setMargins(0, 0, 60, 40);
		    
		} 
		
		if(googleMap != null){
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
				// Getting Current Location
				// Location currentLocation = locationManager.getLastKnownLocation(provider);
				Location currentLocation = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
				if (currentLocation != null) {
					SplashActivity.latitude = currentLocation.getLatitude();
					SplashActivity.longitude = currentLocation.getLongitude();

					Logger.e(TAG, "currentLocation -- " + SplashActivity.latitude	+ " & " + SplashActivity.longitude);
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
//								LatLng latLng = new LatLng(SplashActivity.latitude, SplashActivity.longitude);
//						        googleMap.addMarker(new MarkerOptions().position(latLng));
//						        googleMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
//						        googleMap.animateCamera(CameraUpdateFactory.zoomTo(10));
//								Logger.e(TAG, "onLocationChanged -- "+ SplashActivity.latitude + " & " + SplashActivity.longitude);
							}
						});
		}
			
			googleMap.setInfoWindowAdapter(new InfoWindowAdapter() {

				@Override
				public View getInfoWindow(Marker arg0) {
					mSearchEditText.setVisibility(View.INVISIBLE);
		        	mSearchImg.setBackgroundResource(R.color.white);
		            showingFirst = true;
		            
					View v = mActivity.getLayoutInflater().inflate(R.layout.row_map_title_rating, null);
					((TextView) v.findViewById(R.id.handyman_name_textview)).setText(arg0.getTitle());

					final String str[] =  arg0.getSnippet().split("&#");
					if(str.length > 1) {
						String rnumber[] = str[1].split(" ");
						if(!(rnumber[0].equals("0"))) {
							v.findViewById(R.id.handyman_details_textview).setVisibility(View.VISIBLE);
							((TextView) v.findViewById(R.id.handyman_details_textview)).setText(str[1]);
						} else {
							v.findViewById(R.id.handyman_details_textview).setVisibility(View.GONE);
						}
						
						((RatingBar) v.findViewById(R.id.ratingCount)).setRating(Float.parseFloat(str[2]));
//						buzy = str[3];
						
						if(str[3].equalsIgnoreCase("0")){
							v.findViewById(R.id.busy_layout).setVisibility(View.GONE);
//							v.findViewById(R.id.handyman_buzy_textview).setVisibility(View.GONE);
//							v.findViewById(R.id.read_buzy_textview).setVisibility(View.GONE);
							
						} else {
							v.findViewById(R.id.busy_layout).setVisibility(View.VISIBLE);
							v.findViewById(R.id.handyman_buzy_textview).setVisibility(View.VISIBLE);
							v.findViewById(R.id.read_buzy_textview).setVisibility(View.VISIBLE);
					}
						
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
					 
					final String str[] =  marker.getSnippet().split("&#");
					
					
						if(Utils.validateString(str[0])) {
							
//							if(str[3].equalsIgnoreCase("0")){
								Utils.storeString(mSharedPreferences, Utils.HANDYMAN_ID, str[0]);
								Utils.storeString(mSharedPreferences, Utils.HANDYMAN_RATING, str[2]);
								getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, new HandymanProfileFragment()).addToBackStack(null).commit();
							
//							} 
//							else {
//							Utils.showMessageDialog(getActivity(), getResources().getString(R.string.alert), "Handyman is buzy");
//						}
					} 
					
					
				}
			});
		}
		
		new Handler().postDelayed(new Runnable() {

			@Override
			public void run() {
				/*if(mHandymanModelsList.size() > 0) {
					Utils.showMessageDialog(getActivity(), getResources().getString(R.string.alert), "Not Subcategory Data");
				} else {*/
//					curLatitude = SplashActivity.latitude;
//					curLongitude = SplashActivity.longitude;
					
					if(Utils.validateString(sub_category_id)){
						getAllHandyman(SplashActivity.latitude, SplashActivity.longitude, category_id, sub_category_id);
					}
//					getAllHandyman(Double.parseDouble("19.2290"), Double.parseDouble("72.8573"), mSharedPreferences.getString(Utils.SUB_CATEGORY_ID, ""));
//				}
				
			}
		}, 10);
		
	}
	
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.searchBtn:
			if(showingFirst == true){
				mSearchEditText.setVisibility(View.VISIBLE);
				mSearchImg.setBackgroundResource(R.color.title_color);
//				if(mSearchEditText.getText().toString().trim().length() > 0 && mHandymanModelsList.size() > 0) {
//					setView(mSearchHandyman);
//				} else {
//					setView(mHandymanModelsList);
//				}
	            showingFirst = false;
	            mSearchEditText.setText("");
	        }else{
	        	mSearchEditText.setVisibility(View.INVISIBLE);
	        	mSearchImg.setBackgroundResource(R.color.white);
	            showingFirst = true;
	        }
			
			break;
			
		case R.id.refresh:
			if(Utils.validateString(sub_category_id)){
				getAllHandyman(SplashActivity.latitude, SplashActivity.longitude, category_id, sub_category_id);
			}
			break;
			
	}
}

	@Override
	public void beforeTextChanged(CharSequence s, int start, int count,	int after) {
	}

	@Override
	public void onTextChanged(CharSequence s, int start, int before, int count) {
		searchHandyman(mSearchEditText.getText().toString(),category_id,sub_category_id);
//		searchHandyman();
	}

	@Override
	public void afterTextChanged(Editable s) {
	}
	
	private void getAllHandyman(double lat,double lng, String category, String sub_category) {
		if(Utils.checkInternetConnection(getActivity())){
			GetAllHandymanListRequestTask getAllHandymanListRequestTask = new GetAllHandymanListRequestTask(getActivity());
			getAllHandymanListRequestTask.setAsyncCallListener(new AsyncCallListener() {

				@SuppressWarnings("unchecked")
				@Override
				public void onResponseReceived(Object response) {
						mHandymanModelsList.clear();
						mSearchHandyman.clear();
						
						DataModel dataModel = (DataModel) response;
						
						if(dataModel.getSuccess().equalsIgnoreCase("1")) {
//							Toast.makeText(getActivity(), dataModel.getMessage(), Toast.LENGTH_SHORT).show();
//							mHandymanModelsList.addAll((ArrayList<HandymanModel>) response);
							mHandymanModelsList.addAll(dataModel.getHandymanModels());
							
							Logger.e(TAG, "mHandymanModelsList SIZE -- "	+ mHandymanModelsList.size());
							if (mHandymanModelsList.size() > 0) {
								for (int i = 0; i < mHandymanModelsList.size(); i++) {
									setView(mHandymanModelsList);
								}
							} 
							
						} else if(dataModel.getSuccess().equalsIgnoreCase("0")) {
//							Utils.showMessageDialog(getActivity(), getResources().getString(R.string.alert), "All the Handyman are busy in " + mSharedPreferences.getString(Utils.CATE_NAME, "")+ " - " + mSharedPreferences.getString(Utils.SUB_CATE_NAME, "")  +", Please try after some time.");
							Utils.showMessageDialog(getActivity(), getResources().getString(R.string.alert), dataModel.getMessage());
							LatLng latLng = new LatLng(SplashActivity.latitude, SplashActivity.longitude);
					        googleMap.addMarker(new MarkerOptions().position(latLng));
					        googleMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
					        googleMap.animateCamera(CameraUpdateFactory.zoomTo(10));
					        
					        googleMap.clear();
						}
	                }
				
				@Override
				public void onErrorReceived(String error) {
					Toast.makeText(getActivity(), getString(R.string.try_again), Toast.LENGTH_SHORT).show();
				}
			});
			getAllHandymanListRequestTask.execute(String.valueOf(lat),String.valueOf(lng), category, sub_category);
		}else{
			Utils.showMessageDialog(getActivity(), getResources().getString(R.string.alert), 
					getResources().getString(R.string.connection));
		}
	}
	
	
	private void searchHandyman(String name, String category, String sub_category) {
		if(Utils.checkInternetConnection(getActivity())){
			SearchByNameHandymanListRequestTask searchByNameHandymanListRequestTask = new SearchByNameHandymanListRequestTask(getActivity());
			searchByNameHandymanListRequestTask.setAsyncCallListener(new AsyncCallListener() {

				@SuppressWarnings("unchecked")
				@Override
				public void onResponseReceived(Object response) {
					mHandymanModelsList.clear();
					mSearchHandyman.clear();
					DataModel dataModel = (DataModel) response;
					if(dataModel.getSuccess().equalsIgnoreCase("1")) {
//						mSearchHandyman =  (ArrayList<HandymanModel>) response;
						mSearchHandyman.addAll(dataModel.getSearchHandymanModels());
						Logger.e(TAG, "mSearchHandyman SIZE -- "	+ mSearchHandyman.size());
						if (mSearchHandyman.size() > 0) {
							for (int i = 0; i < mSearchHandyman.size(); i++) {
								setView(mSearchHandyman);
							}
						} 
					} else if(dataModel.getSuccess().equalsIgnoreCase("0")) {
						Toast.makeText(getActivity(), dataModel.getMessage(), Toast.LENGTH_SHORT).show();
					}
					
					
				}
				@Override
				public void onErrorReceived(String error) {
					Toast.makeText(getActivity(), getString(R.string.try_again), Toast.LENGTH_SHORT).show();
				}
			});
			searchByNameHandymanListRequestTask.execute(name,category,sub_category);
		}else{
			Utils.showMessageDialog(getActivity(), getResources().getString(R.string.alert), 
					getResources().getString(R.string.connection));
		}
	}
	
//	private void searchHandyman() {
//		if(mSearchEditText.getText().toString().trim().length() > 0 && mHandymanModelsList.size() > 0) {
//			mSearchHandyman.clear();
//			for (int i = 0; i < mHandymanModelsList.size(); i++) {
//				if(Utils.validateString(mHandymanModelsList.get(i).getFirstname()) && mHandymanModelsList.get(i).getFirstname().toLowerCase().contains(mSearchEditText.getText().toString().trim().toLowerCase())) {
//					mSearchHandyman.add(mHandymanModelsList.get(i));
//				}
//				if(Utils.validateString(mHandymanModelsList.get(i).getLastname()) && mHandymanModelsList.get(i).getLastname().toLowerCase().contains(mSearchEditText.getText().toString().trim().toLowerCase())) {
//					mSearchHandyman.add(mHandymanModelsList.get(i));
//				}
//			}
//			setView(mSearchHandyman);
//		} else {
//			setView(mHandymanModelsList);
//		}
//	}
	
	private void setView(ArrayList<HandymanModel> hadymanlist) {
		if(isAdded()) {
				//ADD MARKERS TO GOOGLEMAP
				try {
					googleMap.clear();
				} catch (Exception e) {
					e.printStackTrace();
				}
//				final LatLngBounds.Builder builder = new LatLngBounds.Builder();
//				int counter = 0;
				for (int i = 0; i < hadymanlist.size(); i++) {
					
					if(Utils.validateString(hadymanlist.get(i).lat) && Utils.validateString(hadymanlist.get(i).lng)) {
						double latitude = Double.parseDouble(hadymanlist.get(i).lat);
						double longitude = Double.parseDouble(hadymanlist.get(i).lng);
						String first_name = "";
						if(Utils.validateString(hadymanlist.get(i).getFirstname())) {
							first_name = hadymanlist.get(i).getFirstname().toString();
						}
						if(Utils.validateString(hadymanlist.get(i).getLastname())) {
							first_name = first_name + " " + hadymanlist.get(i).getLastname();
						}
						
						String email = "";
						if(Utils.validateString(hadymanlist.get(i).email)) {
							email = hadymanlist.get(i).email;
						}
						
//						String rating = "";
//						if(Utils.validateString(mHandymanModelsList.get(i).rating)) {
//							rating = mHandymanModelsList.get(i).rating;
//						}
						
						String distance = hadymanlist.get(i).distance;
						if(distance.contains(".")) {
//							String temp[] = mVenues.get(position).distance.split(".");
							int index = distance.indexOf(".");
							if(distance.length() > (index + 3)) {
								distance = distance.substring(0, index+3);
							}
						}
						
//						if(Utils.validateString(hadymanlist.get(i).busy)){
//							buzy = hadymanlist.get(i).busy;
//						}
						
						
//						MarkerOptions markerOptions = null ;
						if(category_name.equalsIgnoreCase("Electrical")){

							MarkerOptions markerOptions = new MarkerOptions()
							.title(first_name)
							.snippet(hadymanlist.get(i).id + "&#" + email + "&#" + hadymanlist.get(i).rating + "&#" + hadymanlist.get(i).busy)
							.icon(BitmapDescriptorFactory.fromResource(R.drawable.handyman_electrical_xxxhdpi))
							.anchor(0.0f, 1.0f) // Anchors the marker on the bottom left
							.position(new LatLng(latitude, longitude));
							
							googleMap.addMarker(markerOptions);
//							counter++;
//							builder.include(markerOptions.getPosition());
							googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(markerOptions.getPosition(), 15f));
							
						} else if(category_name.equalsIgnoreCase("Plumbing")){
							MarkerOptions markerOptions = new MarkerOptions()
								.title(first_name)
								.snippet(hadymanlist.get(i).id + "&#" + email + "&#" + hadymanlist.get(i).rating + "&#" + hadymanlist.get(i).busy)
								.icon(BitmapDescriptorFactory.fromResource(R.drawable.handyman_plumbing_xxxhdpi))
								.anchor(0.0f, 1.0f) // Anchors the marker on the bottom left
								.position(new LatLng(latitude, longitude));
							
							googleMap.addMarker(markerOptions);
//							counter++;
//							builder.include(markerOptions.getPosition());
//							googleMap.animateCamera(CameraUpdateFactory.newLatLngBounds(builder.build(), 10));
							googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(markerOptions.getPosition(), 15f));
							
						} else if(category_name.equalsIgnoreCase("Cleaner")){
							MarkerOptions markerOptions = new MarkerOptions()
								.title(first_name)
								.snippet(hadymanlist.get(i).id + "&#" + email + "&#" + hadymanlist.get(i).rating + "&#" + hadymanlist.get(i).busy)
								.icon(BitmapDescriptorFactory.fromResource(R.drawable.handyman_cleaning_xxxhdpi))
								.anchor(0.0f, 1.0f) // Anchors the marker on the bottom left
								.position(new LatLng(latitude, longitude));
							
							googleMap.addMarker(markerOptions);
//							counter++;
//							builder.include(markerOptions.getPosition());
//							googleMap.animateCamera(CameraUpdateFactory.newLatLngBounds(builder.build(), 10));
							googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(markerOptions.getPosition(), 15f));
							
						} else if(category_name.equalsIgnoreCase("Carpenter")){
							MarkerOptions markerOptions = new MarkerOptions()
								.title(first_name)
								.snippet(hadymanlist.get(i).id + "&#" + email + "&#" + hadymanlist.get(i).rating + "&#" + hadymanlist.get(i).busy)
								.icon(BitmapDescriptorFactory.fromResource(R.drawable.handyman_carpenter_xxxhdpi))
								.anchor(0.0f, 1.0f) // Anchors the marker on the bottom left
								.position(new LatLng(latitude, longitude));
							
							googleMap.addMarker(markerOptions);
//							counter++;
//							builder.include(markerOptions.getPosition());
//							googleMap.animateCamera(CameraUpdateFactory.newLatLngBounds(builder.build(), 10));
							googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(markerOptions.getPosition(), 15f));
							
							
						} else {
							// default
							MarkerOptions markerOptions = new MarkerOptions()
							.title(first_name)
							.snippet(hadymanlist.get(i).id + "&#" + email + "&#" + hadymanlist.get(i).rating + "&#" + hadymanlist.get(i).busy)
							.icon(BitmapDescriptorFactory.fromResource(R.drawable.handyman_default_xxxhdpi))
							.anchor(0.0f, 1.0f) // Anchors the marker on the bottom left
							.position(new LatLng(latitude, longitude));
							
							googleMap.addMarker(markerOptions);
//							counter++;
//							builder.include(markerOptions.getPosition());
//							googleMap.animateCamera(CameraUpdateFactory.newLatLngBounds(builder.build(), 10));
							googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(markerOptions.getPosition(), 15f));
						}

						
					
					}
				}
			}
		
		}
	
}
