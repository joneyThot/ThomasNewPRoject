package com.handyman.fragment;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import com.handyman.MainActivity;
import com.handyman.R;
import com.handyman.adapter.AutoCompleteAdapter;
import com.handyman.logger.Logger;
import com.handyman.model.PlacePredictions;
import com.handyman.service.AppUtils;
import com.handyman.service.Utils;
/*import com.handyman.service.VolleyJSONRequest;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;*/
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.gson.Gson;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class AddressMapSearchFragment extends BaseFragment  implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, com.google.android.gms.location.LocationListener {

	private static String TAG = "AddressMapSearchFragment";

	private SharedPreferences mSharedPreferences;

	Fragment fr;
	View mRootView;

	private GoogleMap mMap;
	private GoogleApiClient mGoogleApiClient;
	private final static int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
	Context mContext;
	TextView mLocationMarkerText;
	private LatLng mCenterLatLong;


	/**
	 * Receiver registered with this activity to get the response from FetchAddressIntentService.
	 */
	private AddressResultReceiver mResultReceiver;
	/**
	 * The formatted location address.
	 */
	protected String mAddressOutput;
	protected String mAreaOutput;
	protected String mCityOutput;
	protected String mStateOutput;
	EditText mLocationAddress;
	TextView mLocationText;
	private static final int REQUEST_CODE_AUTOCOMPLETE = 1;
	Toolbar mToolbar;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		mRootView = inflater.inflate(R.layout.activity_maps, container, false);
		initview();
		return mRootView;
	}

	@Override
	public void onAttach(Context context) {
		super.onAttach(context);
		mContext = getActivity();
	}


	private void initview() {
		mSharedPreferences = getActivity().getSharedPreferences(Utils.PREF_NAME, Context.MODE_PRIVATE);
//		((MainActivity) getActivity()).setTitleText("", "Search", View.VISIBLE, View.GONE, View.GONE, View.VISIBLE);
//		getActivity().findViewById(R.id.title).setVisibility(View.GONE);

		mActivity.getSlidingMenu().setMode(SlidingMenu.LEFT);
		mActivity.getSlidingMenu().setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);

		init();

	}

	private void init() {
		SupportMapFragment mapFragment = (SupportMapFragment) getActivity().getSupportFragmentManager().findFragmentById(R.id.map);

		mLocationMarkerText = (TextView) mRootView.findViewById(R.id.locationMarkertext);
		mLocationAddress = (EditText) mRootView.findViewById(R.id.Address);
		mLocationText = (TextView) mRootView.findViewById(R.id.Locality);
		mToolbar = (Toolbar) mRootView.findViewById(R.id.toolbar);
//		mContext.setSupportActionBar(mToolbar);
//		getSupportActionBar().setDisplayShowHomeEnabled(true);
//
//		getSupportActionBar().setTitle(getResources().getString(R.string.app_name));
	}


	@Override
	public void onConnected(@Nullable Bundle bundle) {

	}

	@Override
	public void onConnectionSuspended(int i) {

	}

	@Override
	public void onLocationChanged(Location location) {

	}

	@Override
	public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

	}

	@Override
	public void onMapReady(GoogleMap googleMap) {

	}

	class AddressResultReceiver extends ResultReceiver {
		public AddressResultReceiver(Handler handler) {
			super(handler);
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
			mStateOutput = resultData.getString(AppUtils.LocationConstants.LOCATION_DATA_STREET);

			displayAddressOutput();

			// Show a toast message if an address was found.
			if (resultCode == AppUtils.LocationConstants.SUCCESS_RESULT) {
				//  showToast(getString(R.string.address_found));


			}


		}

	}

	protected void displayAddressOutput() {
		//  mLocationAddressTextView.setText(mAddressOutput);
		try {
			if (mAreaOutput != null)
				// mLocationText.setText(mAreaOutput+ "");

				mLocationAddress.setText(mAddressOutput);
			//mLocationText.setText(mAreaOutput);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
