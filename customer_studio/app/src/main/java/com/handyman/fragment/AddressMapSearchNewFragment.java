package com.handyman.fragment;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceBuffer;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.handyman.MainActivity;
import com.handyman.R;
import com.handyman.adapter.PlaceArrayListviewAdapter;
import com.handyman.logger.Logger;
import com.handyman.service.Utils;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;


public class AddressMapSearchNewFragment extends BaseFragment implements
        GoogleApiClient.OnConnectionFailedListener,
        GoogleApiClient.ConnectionCallbacks {

    private static String TAG = "AddressMapSearchNewFragment";
    private SharedPreferences mSharedPreferences;
    Fragment fr;
    View mRootView;

    private static final int GOOGLE_API_CLIENT_ID = 0;
    private EditText mAutocompleteTextView;
    private ListView ltvPlace;
    private GoogleApiClient mGoogleApiClient;
    //    private PlaceArrayAdapter mPlaceArrayAdapter;
    private PlaceArrayListviewAdapter mPlaceArrayListviewAdapter;
    private static final LatLngBounds BOUNDS_MOUNTAIN_VIEW = new LatLngBounds(
            new LatLng(37.398160, -122.180831), new LatLng(37.430610, -121.972090));

//    String searchAddress = "";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mRootView = inflater.inflate(R.layout.fragment_map_search, container, false);

        mGoogleApiClient = new GoogleApiClient.Builder(getActivity())
                .addApi(Places.GEO_DATA_API)
                .enableAutoManage(getActivity(), GOOGLE_API_CLIENT_ID, this)
                .addConnectionCallbacks(this)
                .build();
        initview();
        return mRootView;
    }


    private void initview() {
        mSharedPreferences = getActivity().getSharedPreferences(Utils.PREF_NAME, Context.MODE_PRIVATE);
        ((MainActivity) getActivity()).setTitleText("", "Search", "", View.VISIBLE, View.GONE, View.GONE, View.GONE, View.GONE);
        getActivity().findViewById(R.id.title).setVisibility(View.GONE);
        getActivity().findViewById(R.id.txtUrl).setVisibility(View.GONE);

        mActivity.getSlidingMenu().setMode(SlidingMenu.LEFT);
        mActivity.getSlidingMenu().setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);

        Utils.storeString(mSharedPreferences, Utils.currentAddress, "");
        Utils.storeString(mSharedPreferences, Utils.latitude, "");
        Utils.storeString(mSharedPreferences, Utils.longitude, "");

        ltvPlace = (ListView) mRootView.findViewById(R.id.ltvPlace);
        ltvPlace.setOnItemClickListener(mAutocompleteClickListener);

        mPlaceArrayListviewAdapter = new PlaceArrayListviewAdapter(getActivity(), R.layout.row_map_addresses, BOUNDS_MOUNTAIN_VIEW, null);
        ltvPlace.setAdapter(mPlaceArrayListviewAdapter);

        mAutocompleteTextView = (EditText) mRootView.findViewById(R.id.autoCompleteTextView);
        mAutocompleteTextView.requestFocus();
        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
        mAutocompleteTextView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
//                if (mAutocompleteTextView.getText().length() > 2) {
                mPlaceArrayListviewAdapter.getFilter().filter(s);

//                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        /*mAutocompleteTextView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
//                    Toast.makeText(getContext(), "Error: " + "Search Ready", Toast.LENGTH_SHORT).show();
                    v = (TextView) getActivity().getCurrentFocus();
                    if (v != null) {
                        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                    }
                    mPlaceArrayListviewAdapter.getFilter().filter(mAutocompleteTextView.getText().toString().trim());
                    return true;
                }
                return false;
            }
        });*/

    }


    private AdapterView.OnItemClickListener mAutocompleteClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//            final PlaceArrayAdapter.PlaceAutocomplete item = mPlaceArrayAdapter.getItem(position);
            final PlaceArrayListviewAdapter.PlaceAutocomplete item = mPlaceArrayListviewAdapter.getItem(position);
            final String placeId = String.valueOf(item.placeId);
            Logger.i(TAG, "Selected: " + item.description);
//            searchAddress = String.valueOf(item.description);
            if (view != null) {
                InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(), 0);
            }
            PendingResult<PlaceBuffer> placeResult = Places.GeoDataApi.getPlaceById(mGoogleApiClient, placeId);
            placeResult.setResultCallback(mUpdatePlaceDetailsCallback);
            Logger.i(TAG, "Fetching details for ID: " + item.placeId);
        }
    };

    private ResultCallback<PlaceBuffer> mUpdatePlaceDetailsCallback = new ResultCallback<PlaceBuffer>() {
        @Override
        public void onResult(PlaceBuffer places) {
            try {
                if (!places.getStatus().isSuccess()) {
                    Logger.e(TAG, "Place query did not complete. Error: " + places.getStatus().toString());
                    return;
                }
                // Selecting the first object buffer.
                final Place place = places.get(0);

                CharSequence attributions = places.getAttributions();
                LatLng latlng = place.getLatLng();

//            place.
                String place_name = String.valueOf(place.getName());
                String place_address = String.valueOf(place.getAddress());
                String adds = place_address.contains(place_name) ? place_address : (place_name + ", " + place_address);


                if (Utils.validateString(adds)) {
//                Utils.storeString(mSharedPreferences,Utils.ADDRESS_MAP_SEARCH_NEW_FRAGMENT,TAG);
                    Utils.storeString(mSharedPreferences, Utils.currentAddress, adds);
                    String lat = String.valueOf(latlng.latitude);
                    String lng = String.valueOf(latlng.longitude);
                    Utils.storeString(mSharedPreferences, Utils.latitude, lat.substring(0, 6));
                    Utils.storeString(mSharedPreferences, Utils.longitude, lng.substring(0, 6));

                    getActivity().getSupportFragmentManager().popBackStack();

                    Logger.e(TAG, "Current Address :: " + mSharedPreferences.getString(Utils.currentAddress, ""));
                    Logger.e(TAG, "Latitiude :: " + mSharedPreferences.getString(Utils.latitude, ""));
                    Logger.e(TAG, "Longitude :: " + mSharedPreferences.getString(Utils.longitude, ""));
                }
            } catch (NullPointerException e) {
                e.printStackTrace();
            }


        }
    };

    @Override
    public void onConnected(Bundle bundle) {
//        mPlaceArrayAdapter.setGoogleApiClient(mGoogleApiClient);
        mPlaceArrayListviewAdapter.setGoogleApiClient(mGoogleApiClient);
        Logger.i(TAG, "Google Places API connected.");
    }

    @Override
    public void onConnectionSuspended(int i) {
//        mPlaceArrayAdapter.setGoogleApiClient(null);
        mPlaceArrayListviewAdapter.setGoogleApiClient(null);
        Logger.e(TAG, "Google Places API connection suspended.");
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Logger.e(TAG, "Google Places API connection failed with error code: " + connectionResult.getErrorCode());

        Toast.makeText(getActivity(), "Google Places API connection failed with error code:" + connectionResult.getErrorCode(), Toast.LENGTH_LONG).show();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mGoogleApiClient.stopAutoManage(getActivity());
        mGoogleApiClient.disconnect();
    }

}
