package com.handyman.fragment;

import android.Manifest;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
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
import com.handyman.MainActivity;
import com.handyman.R;
import com.handyman.SplashActivity;
import com.handyman.crop.Util;
import com.handyman.logger.Logger;
import com.handyman.model.DataModel;
import com.handyman.model.HireHandymanModel;
import com.handyman.model.ProfileHandymanModel;
import com.handyman.service.AsyncCallListener;
import com.handyman.service.FavouriteHandymanRequestTask;
import com.handyman.service.GPSTracker;
import com.handyman.service.GetProfileHandymanListRequestTask;
import com.handyman.service.Utils;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Transformation;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class HandymanProfileFragment extends BaseFragment implements OnClickListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {

    private static String TAG = "HandymanProfileFragment";
    private SharedPreferences mSharedPreferences;
    //	private HandymanModel mHandymanModels = new HandymanModel();
//	private ArrayList<HandymanModel> mHandymanModelsList = new ArrayList<HandymanModel>(); 
    private int mDeviceWidth = 480;
    private ArrayList<ProfileHandymanModel> mProfileHandymanModelsList = new ArrayList<ProfileHandymanModel>();
    private GoogleApiClient googleApiClient;
    private LocationRequest locationRequest;
    //    private ProgressBar mProgressbar;
    private ProgressDialog mProgressDialog;

    final static int REQUEST_LOCATION = 199;
    boolean firstTime = true, isDialogOpen = false/*, nextCall = false*/;

    Fragment fr;
    View mRootView;
    TextView mAgetxt;
    ImageView mHanfymanprofileImg, mFaveImg;
    String handymanCity = "", customerCity = "", category_id = "", sub_category_id = "", firstname = "", lastname = "",
            isBusy = "0", is_login = "1", handymanBusy = "0", handyman_id, handyman_name, like_value, url = "",
            FavouriteHandymanFragment = "", mobile_no = "";
    boolean showingFirst = false;
    double h_lat = 0, h_long = 0;


    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        getPermisson();
        mRootView = inflater.inflate(R.layout.fragment_handyman_profile, container, false);
        WindowManager w = ((Activity) getActivity()).getWindowManager();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            Point size = new Point();
            w.getDefaultDisplay().getSize(size);
            mDeviceWidth = size.x;
        } else {
            Display d = w.getDefaultDisplay();
            mDeviceWidth = d.getWidth();
        }

        LocationManager manager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
        boolean statusOfGPS = manager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        if (!statusOfGPS) {
            enableLoc();
        }

        initview();
        return mRootView;
    }

    private void getPermisson() {
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
    }

    @SuppressWarnings("unchecked")
    private void initview() {
        mSharedPreferences = getActivity().getSharedPreferences(Utils.PREF_NAME, Context.MODE_PRIVATE);

        mActivity.getSlidingMenu().setMode(SlidingMenu.LEFT);
        mActivity.getSlidingMenu().setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);

        mHanfymanprofileImg = (ImageView) mRootView.findViewById(R.id.hanfyman_profile_img);
        mHanfymanprofileImg.setOnClickListener(this);
        mRootView.findViewById(R.id.hireme_Button).setOnClickListener(this);
//		rating = mSharedPreferences.getString(Utils.HANDYMAN_RATING, "");

        mRootView.findViewById(R.id.chat_img).setOnClickListener(this);
        mRootView.findViewById(R.id.call_img).setOnClickListener(this);
        mRootView.findViewById(R.id.rating_img).setOnClickListener(this);
        mRootView.findViewById(R.id.report_img).setOnClickListener(this);
        mRootView.findViewById(R.id.hire_website_txt).setOnClickListener(this);
        mFaveImg = (ImageView) mRootView.findViewById(R.id.fave_img);
        mFaveImg.setOnClickListener(this);
//        mProgressbar = (ProgressBar) mRootView.findViewById(R.id.mProgressbar);
//        mProgressbar.setVisibility(View.GONE);

//		if(getArguments() != null) {
        handyman_id = mSharedPreferences.getString(Utils.HANDYMAN_ID, "");
//			Utils.storeString(mSharedPreferences, Utils.HANDYMAN_ID, handyman_id);
//		}
        if (getArguments() != null) {
            FavouriteHandymanFragment = getArguments().getString("FavouriteHandymanFragment");
            category_id = getArguments().getString(Utils.CATEGORY_ID);
            sub_category_id = getArguments().getString(Utils.SUB_CATEGORY_ID);

        }

        if (Utils.validateString(FavouriteHandymanFragment)) {
            String name = getArguments().getString(Utils.HANDYMAN_NAME);
            ((MainActivity) getActivity()).setTitleText("", name, "", View.VISIBLE, View.GONE, View.GONE, View.GONE, View.GONE);
            getActivity().findViewById(R.id.title).setVisibility(View.GONE);
            getActivity().findViewById(R.id.txtUrl).setVisibility(View.GONE);

            ((TextView) mRootView.findViewById(R.id.handyman_name_profile_txt)).setText(name);
        }

        Utils.storeString(mSharedPreferences, Utils.HANDYMAN_LATITUDE, "");
        Utils.storeString(mSharedPreferences, Utils.HANDYMAN_LONGITUDE, "");

        if (Utils.validateString(handyman_id))
            getProfileHandyman(handyman_id, mSharedPreferences.getString(Utils.USER_ID, ""), category_id, sub_category_id);

        Utils.storeString(mSharedPreferences, Utils.currentAddress, "");
        Utils.storeString(mSharedPreferences, Utils.latitude, "");
        Utils.storeString(mSharedPreferences, Utils.longitude, "");

    }

    @Override
    public void onClick(View v) {
        // TODO Auto-generated method stub
        switch (v.getId()) {
            case R.id.hanfyman_profile_img:
                break;

            case R.id.hireme_Button:

//                mProgressDialog = new ProgressDialog(getActivity());
//                mProgressDialog.setMessage(getActivity().getResources().getString(R.string.loading));
//                mProgressDialog.setCanceledOnTouchOutside(false);
//                mProgressDialog.setCancelable(false);
//                if (mProgressDialog != null && !mProgressDialog.isShowing()) {
//                    mProgressDialog.show();
//                }

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    getPermisson();
                }

                LocationManager manager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
                boolean statusOfGPS = manager.isProviderEnabled(LocationManager.GPS_PROVIDER);
//
                GPSTracker gpsTracker = Utils.getCurrentLocation(getActivity());
                if (gpsTracker != null) {
                    SplashActivity.latitude = gpsTracker.getLatitude();
                    SplashActivity.longitude = gpsTracker.getLongitude();
                }
//
//                if (h_lat != 0 && h_long != 0) {
//                    getHandymanCity(h_lat, h_long);
//                }
//
//                if (SplashActivity.latitude != 0 && SplashActivity.longitude != 0) {
//                    getCustomerCity(SplashActivity.latitude, SplashActivity.longitude);
//                }
//
//                if (statusOfGPS) {
//                    new Handler().postDelayed(new Runnable() {
//
//                        @Override
//                        public void run() {
//
//                            if (SplashActivity.latitude != 0 && SplashActivity.longitude != 0) {
//                                if (is_login.equalsIgnoreCase("1")) {
//                                    if (handymanBusy.equalsIgnoreCase("0")) {
//                                        if (isBusy.equalsIgnoreCase("0")) {
//                                            if (customerCity.equalsIgnoreCase(handymanCity)) {
//                                                if (mProgressDialog != null && mProgressDialog.isShowing()) {
//                                                    mProgressDialog.dismiss();
//                                                }
//                                                HireHandymanFragment hireHandymanFragment = new HireHandymanFragment();
//                                                Bundle bundle = new Bundle();
//                                                bundle.putString(Utils.HANDYMAN_NAME, mProfileHandymanModelsList.get(0).firstname + " " + mProfileHandymanModelsList.get(0).lastname);
//                                                bundle.putString(Utils.HANDYMAN_EMAIL, mProfileHandymanModelsList.get(0).email);
//                                                bundle.putString("FavouriteHandymanFragment", FavouriteHandymanFragment);
//                                                hireHandymanFragment.setArguments(bundle);
//                                                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, hireHandymanFragment).addToBackStack(TAG).commit();
//                                            } else {
//                                                if (mProgressDialog != null && mProgressDialog.isShowing()) {
//                                                    mProgressDialog.dismiss();
//                                                }
//                                                Utils.showMessageDialog(getActivity(), getResources().getString(R.string.alert), "Selected handyman is not available in " + customerCity + ".");
//                                            }
//                                        } else {
//                                            if (mProgressDialog != null && mProgressDialog.isShowing()) {
//                                                mProgressDialog.dismiss();
//                                            }
//                                            Utils.showMessageDialog(getActivity(), getResources().getString(R.string.alert), "Handyman is busy.");
//                                        }
//                                    } else {
//                                        if (mProgressDialog != null && mProgressDialog.isShowing()) {
//                                            mProgressDialog.dismiss();
//                                        }
//                                        Utils.showMessageDialog(getActivity(), getResources().getString(R.string.alert), "Handyman is busy.");
//                                    }
//                                } else {
//                                    if (mProgressDialog != null && mProgressDialog.isShowing()) {
//                                        mProgressDialog.dismiss();
//                                    }
//                                    Utils.showMessageDialog(getActivity(), getResources().getString(R.string.alert), "Selected handyman is currently not available, please select other one.");
//                                }
//                            } else {
//                                if (mProgressDialog != null && mProgressDialog.isShowing()) {
//                                    mProgressDialog.dismiss();
//                                }
//                            }
//
//                        }
//                    }, 2000);
//
//                } else {
//
//                    if (mProgressDialog != null && mProgressDialog.isShowing()) {
//                        mProgressDialog.dismiss();
//                    }
//
//                    if (googleApiClient != null) {
//                        googleApiClient.disconnect();
//                        googleApiClient = null;
//                    }
//                    enableLoc();
//
//                }
                if (Utils.checkInternetConnection(getActivity())) {

                    if (statusOfGPS) {
                        if (!mProfileHandymanModelsList.isEmpty()) {
                            if (is_login.equalsIgnoreCase("1")) {
                                if (handymanBusy.equalsIgnoreCase("0")) {
                                    if (isBusy.equalsIgnoreCase("0")) {
                                        HireHandymanFragment.setDate = "";
                                        HireHandymanFragment.setTime = "";
                                        HireHandymanFragment hireHandymanFragment = new HireHandymanFragment();
                                        Bundle bundle = new Bundle();
                                        bundle.putString(Utils.HANDYMAN_NAME, mProfileHandymanModelsList.get(0).firstname + " " + mProfileHandymanModelsList.get(0).lastname);
                                        bundle.putString(Utils.HANDYMAN_EMAIL, mProfileHandymanModelsList.get(0).email);
                                        bundle.putString("FavouriteHandymanFragment", FavouriteHandymanFragment);
                                        hireHandymanFragment.setArguments(bundle);
                                        getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, hireHandymanFragment).addToBackStack(TAG).commit();
                                    } else {
                                        Utils.showMessageDialog(getActivity(), getResources().getString(R.string.alert), "Handyman is busy.");
                                    }
                                } else {
                                    Utils.showMessageDialog(getActivity(), getResources().getString(R.string.alert), "Handyman is busy.");
                                }
                            } else {
                                Utils.showMessageDialog(getActivity(), getResources().getString(R.string.alert), "Selected handyman is currently not available, please select other one.");
                            }
                        } else {
                            Utils.showMessageDialog(getActivity(), getResources().getString(R.string.alert), "Selected handyman is currently not available, please select other one.");
                        }
                    } else {
                        if (googleApiClient != null) {
                            googleApiClient.disconnect();
                            googleApiClient = null;
                        }
                        enableLoc();
                    }
                } else {
                    Utils.showMessageDialog(getActivity(), getResources().getString(R.string.alert),
                            getResources().getString(R.string.connection));
                }
                break;

            case R.id.chat_img:
                PackageManager pm = getActivity().getPackageManager();
                try {

                    Intent waIntent = new Intent(Intent.ACTION_SEND);
                    waIntent.setType("text/plain");
                    String text = "Hi, this is your handyman";

                    PackageInfo info = pm.getPackageInfo("com.whatsapp", PackageManager.GET_META_DATA);
                    // Check if package exists or not. If not then code in catch block will be called
                    waIntent.setPackage("com.whatsapp");

                    waIntent.putExtra(Intent.EXTRA_TEXT, text);
                    startActivity(Intent.createChooser(waIntent, "Share with"));

                } catch (NameNotFoundException e) {
                    Toast.makeText(getActivity(), "WhatsApp not Installed", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=com.whatsapp&hl=en")));
                }
                break;

            case R.id.call_img:
                Uri number = Uri.parse("tel:" + mobile_no);
                Intent callIntent = new Intent(Intent.ACTION_DIAL, number);
                startActivity(callIntent);

                break;

            case R.id.rating_img:
//			getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, new RatingFragment()).addToBackStack(TAG).commit();
                break;

            case R.id.report_img:
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, new RegisterComplainFragment()).addToBackStack(TAG).commit();
                break;

            case R.id.fave_img:
                if (Utils.checkInternetConnection(getActivity())) {
                    if (like_value.equalsIgnoreCase("1")) {
                        mFaveImg.setImageResource(R.drawable.icon_fav);
                        FavoriteHandyman(handyman_id, mSharedPreferences.getString(Utils.USER_ID, ""), like_value);
                        like_value = "0";
                    } else {
                        mFaveImg.setImageResource(R.drawable.icon_nonfav);
                        FavoriteHandyman(handyman_id, mSharedPreferences.getString(Utils.USER_ID, ""), like_value);
                        like_value = "1";
                    }
                } else {
                    Utils.showMessageDialog(getActivity(), getResources().getString(R.string.alert),
                            getResources().getString(R.string.connection));
                }

                break;

            case R.id.hire_website_txt:
                if (Utils.validateString(url)) {
                    Intent i = new Intent(Intent.ACTION_VIEW);
                    i.setData(Uri.parse(url));
                    startActivity(i);
                }
                break;
        }
    }

    private void getProfileHandyman(String handyman_id, String client_id, String category_id, String sub_category_id) {
        try {
            if (Utils.checkInternetConnection(getActivity())) {
                GetProfileHandymanListRequestTask getProfileHandymanListRequestTask = new GetProfileHandymanListRequestTask(getActivity());
                getProfileHandymanListRequestTask.setAsyncCallListener(new AsyncCallListener() {

                    @SuppressWarnings("unchecked")
                    @Override
                    public void onResponseReceived(Object response) {
                        mProfileHandymanModelsList.clear();
                        DataModel dataModel = (DataModel) response;
                        if (dataModel.getSuccess().equalsIgnoreCase("1")) {

//						Toast.makeText(getActivity(), dataModel.getMessage(), Toast.LENGTH_SHORT).show();

//						mProfileHandymanModelsList =  (ArrayList<ProfileHandymanModel>) response;
                            mProfileHandymanModelsList.addAll(dataModel.getProfileHandymanModels());
                            Logger.e(TAG, "mProfileHandymanModelsList SIZE -- " + mProfileHandymanModelsList.size());
                            if (mProfileHandymanModelsList.size() > 0) {
//							for (int i = 0; i < mProfileHandymanModelsList.size(); i++) {


                                if (Utils.validateString(mProfileHandymanModelsList.get(0).getFirstname())) {
                                    firstname = mProfileHandymanModelsList.get(0).getFirstname();
                                }
                                if (Utils.validateString(mProfileHandymanModelsList.get(0).getLastname())) {
                                    lastname = mProfileHandymanModelsList.get(0).getLastname();
                                }

                                if (getActivity() != null) {
                                    if (!Utils.validateString(FavouriteHandymanFragment)) {
                                        ((MainActivity) getActivity()).setTitleText("", firstname + " " + lastname, "", View.VISIBLE, View.GONE, View.GONE, View.VISIBLE, View.GONE);
                                        getActivity().findViewById(R.id.title).setVisibility(View.GONE);
                                        getActivity().findViewById(R.id.txtUrl).setVisibility(View.GONE);

                                        ((TextView) mRootView.findViewById(R.id.handyman_name_profile_txt)).setText(firstname + " " + lastname);
                                    }
                                }

                                if (Utils.validateString(mProfileHandymanModelsList.get(0).img_path)) {
                                    //			Picasso.with(mActivity).load(mVenue.image).fit().centerCrop().into(mBannerImage);
                                    Transformation transformation = new Transformation() {

                                        @Override
                                        public Bitmap transform(Bitmap source) {
                                            int targetWidth = mDeviceWidth;

                                            double aspectRatio = (double) source.getHeight() / (double) source.getWidth();
                                            int targetHeight = (int) (targetWidth * aspectRatio);
                                            if (targetHeight > targetWidth) {
                                                targetHeight = targetWidth;
                                            }
                                            Bitmap result = Bitmap.createScaledBitmap(source, targetWidth, targetHeight, false);
                                            if (result != source) {
                                                // Same bitmap is returned if sizes are the same
                                                source.recycle();
                                            }
                                            return result;
                                        }

                                        @Override
                                        public String key() {
                                            return "transformation" + " desiredWidth";
                                        }
                                    };

                                    Picasso.with(mActivity)
                                            .load(Utils.IMAGE_URL + mProfileHandymanModelsList.get(0).img_path)
                                            .placeholder(R.drawable.avtar_images)
                                            .error(R.drawable.avtar_images)
                                            .transform(transformation)
                                            .centerCrop()
                                            .resize(mDeviceWidth, (int) (mDeviceWidth))
                                            .into(mHanfymanprofileImg);
                                }

                                if (Utils.validateString(mProfileHandymanModelsList.get(0).getLike())) {
                                    like_value = mProfileHandymanModelsList.get(0).getLike().toString();
                                    if (like_value.equalsIgnoreCase("1")) {
                                        mFaveImg.setImageResource(R.drawable.icon_fav);
                                        showingFirst = true;
                                        like_value = "0";
                                    } else {
                                        mFaveImg.setImageResource(R.drawable.icon_nonfav);
                                        showingFirst = false;
                                        like_value = "1";
                                    }
                                }

                                if (Utils.validateString(mProfileHandymanModelsList.get(0).getDob())) {
                                    mRootView.findViewById(R.id.age_textview).setVisibility(View.VISIBLE);
                                    mRootView.findViewById(R.id.hire_age_txt).setVisibility(View.VISIBLE);

                                    String birth_date = mProfileHandymanModelsList.get(0).getDob().toString();
                                    int day = Integer.parseInt(birth_date.substring(0, 2));
                                    int month = Integer.parseInt(birth_date.substring(3, 5));
                                    int year = Integer.parseInt(birth_date.substring(6, 10));
                                    String age = getAge(year, month, day);
                                    int int_age = Integer.parseInt(age);
                                    if (int_age > 0) {
                                        ((TextView) mRootView.findViewById(R.id.hire_age_txt)).setText(age + " " + "yrs");
                                    } else {
                                        ((TextView) mRootView.findViewById(R.id.hire_age_txt)).setText("0" + " " + "yrs");
                                    }
                                }

                                if (Utils.validateString(mProfileHandymanModelsList.get(0).getQualification())) {
                                    mRootView.findViewById(R.id.qualification_textview).setVisibility(View.VISIBLE);
                                    mRootView.findViewById(R.id.hire_qualification_txt).setVisibility(View.VISIBLE);
                                    ((TextView) mRootView.findViewById(R.id.hire_qualification_txt)).setText(mProfileHandymanModelsList.get(0).getQualification());
                                }

                                if (Utils.validateString(mProfileHandymanModelsList.get(0).getExperience())) {
                                    mRootView.findViewById(R.id.experience_textview).setVisibility(View.VISIBLE);
                                    mRootView.findViewById(R.id.hire_experience_txt).setVisibility(View.VISIBLE);
                                    ((TextView) mRootView.findViewById(R.id.hire_experience_txt)).setText(mProfileHandymanModelsList.get(0).getExperience());
                                }

                                if (Utils.validateString(mProfileHandymanModelsList.get(0).getSub_category_name())) {
                                    mRootView.findViewById(R.id.expertise_textview).setVisibility(View.VISIBLE);
                                    mRootView.findViewById(R.id.hire_expertise_txt).setVisibility(View.VISIBLE);
                                    ((TextView) mRootView.findViewById(R.id.hire_expertise_txt)).setText(mProfileHandymanModelsList.get(0).getSub_category_name());
                                }

                                if (Utils.validateString(mProfileHandymanModelsList.get(0).getMobile())) {
                                    mobile_no = mProfileHandymanModelsList.get(0).getMobile();
                                    mRootView.findViewById(R.id.mobile_number_textview).setVisibility(View.VISIBLE);
                                    mRootView.findViewById(R.id.hire_mobile_number_txt).setVisibility(View.VISIBLE);
                                    ((TextView) mRootView.findViewById(R.id.hire_mobile_number_txt)).setText(mProfileHandymanModelsList.get(0).getMobile());
                                }

                                if (Utils.validateString(mProfileHandymanModelsList.get(0).getWhatsapp_id())) {
                                    mRootView.findViewById(R.id.watsapp_id_textview).setVisibility(View.VISIBLE);
                                    mRootView.findViewById(R.id.hire_watsapp_id_txt).setVisibility(View.VISIBLE);
                                    ((TextView) mRootView.findViewById(R.id.hire_watsapp_id_txt)).setText(mProfileHandymanModelsList.get(0).getWhatsapp_id());
                                }

                                if (Utils.validateString(mProfileHandymanModelsList.get(0).getEmail())) {
                                    mRootView.findViewById(R.id.email_id_textview).setVisibility(View.VISIBLE);
                                    mRootView.findViewById(R.id.hire_email_id_txt).setVisibility(View.VISIBLE);
                                    ((TextView) mRootView.findViewById(R.id.hire_email_id_txt)).setText(mProfileHandymanModelsList.get(0).getEmail());
                                }

                                if (Utils.validateString(mProfileHandymanModelsList.get(0).getProvider())) {
                                    mRootView.findViewById(R.id.service_provider_textview).setVisibility(View.VISIBLE);
                                    mRootView.findViewById(R.id.hire_service_provider_txt).setVisibility(View.VISIBLE);
                                    ((TextView) mRootView.findViewById(R.id.hire_service_provider_txt)).setText(mProfileHandymanModelsList.get(0).getProvider());
                                }

                                if (Utils.validateString(mProfileHandymanModelsList.get(0).getAddress())) {
                                    mRootView.findViewById(R.id.address_textview).setVisibility(View.VISIBLE);
                                    mRootView.findViewById(R.id.hire_address_txt).setVisibility(View.VISIBLE);
                                    String address = "", street = "", landmark = "", city = "", state = "", pincode = "", floor = "";
                                    if (Utils.validateString(mProfileHandymanModelsList.get(0).getFloor().toString()))
                                        floor = mProfileHandymanModelsList.get(0).getFloor().toString() + ",";
                                    if (Utils.validateString(mProfileHandymanModelsList.get(0).getAddress().toString()))
                                        address = mProfileHandymanModelsList.get(0).getAddress().toString();
                                    if (Utils.validateString(mProfileHandymanModelsList.get(0).getStreet().toString()))
                                        street = mProfileHandymanModelsList.get(0).getStreet().toString();
                                    if (Utils.validateString(mProfileHandymanModelsList.get(0).getLandmark().toString()))
                                        landmark = mProfileHandymanModelsList.get(0).getLandmark().toString();
                                    if (Utils.validateString(mProfileHandymanModelsList.get(0).getCity_name().toString()))
                                        city = mProfileHandymanModelsList.get(0).getCity_name().toString();
                                    if (Utils.validateString(mProfileHandymanModelsList.get(0).getState_name().toString()))
                                        state = mProfileHandymanModelsList.get(0).getState_name().toString();
                                    if (Utils.validateString(mProfileHandymanModelsList.get(0).getPincode().toString()))
                                        pincode = mProfileHandymanModelsList.get(0).getPincode().toString();
                                    ((TextView) mRootView.findViewById(R.id.hire_address_txt)).setText(floor + address + "\n" + street + "\n" + landmark + "\n" + city + " - " + pincode);
//									((TextView) mRootView.findViewById(R.id.hire_address_txt)).setText(floor + address);
                                }

                                if (Utils.validateString(mProfileHandymanModelsList.get(0).getWebsite())) {
                                    mRootView.findViewById(R.id.website_textview).setVisibility(View.VISIBLE);
                                    mRootView.findViewById(R.id.hire_website_txt).setVisibility(View.VISIBLE);
                                    url = mProfileHandymanModelsList.get(0).getWebsite();
                                    ((TextView) mRootView.findViewById(R.id.hire_website_txt)).setText(url);
                                }
                                String rating;
                                if (Utils.validateString(mProfileHandymanModelsList.get(0).rating)) {
                                    rating = mProfileHandymanModelsList.get(0).rating.trim();

                                } else {
                                    rating = "0";
                                }
                                ((RatingBar) mRootView.findViewById(R.id.handyman_rating_star)).setRating(Float.parseFloat(rating));

                                Utils.storeString(mSharedPreferences, Utils.HANDYMAN_NAME, mProfileHandymanModelsList.get(0).firstname + " " + mProfileHandymanModelsList.get(0).lastname);
                                Utils.storeString(mSharedPreferences, Utils.HANDYMAN_EMAIL, mProfileHandymanModelsList.get(0).email);
                                Utils.storeString(mSharedPreferences, Utils.HANDYMAN_RATING, rating);
//							}

                                if (Utils.validateString(mProfileHandymanModelsList.get(0).getLat())) {
                                    Utils.storeString(mSharedPreferences, Utils.HANDYMAN_LATITUDE, mProfileHandymanModelsList.get(0).getLat());
                                    h_lat = Double.parseDouble(mProfileHandymanModelsList.get(0).getLat());
                                }

                                if (Utils.validateString(mProfileHandymanModelsList.get(0).getLng())) {
                                    Utils.storeString(mSharedPreferences, Utils.HANDYMAN_LONGITUDE, mProfileHandymanModelsList.get(0).getLng());
                                    h_long = Double.parseDouble(mProfileHandymanModelsList.get(0).getLng());
                                }

                                if (Utils.validateString(mProfileHandymanModelsList.get(0).getIs_busy())) {
                                    isBusy = mProfileHandymanModelsList.get(0).getIs_busy();
                                }

                                if (Utils.validateString(mProfileHandymanModelsList.get(0).getIs_login())) {
                                    is_login = mProfileHandymanModelsList.get(0).getIs_login();
                                }

                                if (Utils.validateString(mProfileHandymanModelsList.get(0).getHandyman_busy())) {
                                    handymanBusy = mProfileHandymanModelsList.get(0).getHandyman_busy();
                                }

                                if (Utils.validateString(mProfileHandymanModelsList.get(0).getRatingusers())) {
                                    if (!mProfileHandymanModelsList.get(0).getRatingusers().equalsIgnoreCase("0")) {
                                        mRootView.findViewById(R.id.txtHandymanRates).setVisibility(View.VISIBLE);
                                        if (mProfileHandymanModelsList.get(0).getRatingusers().equalsIgnoreCase("1"))
                                            ((TextView) mRootView.findViewById(R.id.txtHandymanRates)).setText(mProfileHandymanModelsList.get(0).getRatingusers() + " (Review)");
                                        else
                                            ((TextView) mRootView.findViewById(R.id.txtHandymanRates)).setText(mProfileHandymanModelsList.get(0).getRatingusers() + " (Reviews)");
                                    } else {
                                        mRootView.findViewById(R.id.txtHandymanRates).setVisibility(View.GONE);
                                    }
                                }

                            }

                        } else if (dataModel.getSuccess().equalsIgnoreCase("0")) {
                            Toast.makeText(getActivity(), dataModel.getMessage(), Toast.LENGTH_SHORT).show();
                            mFaveImg.setOnClickListener(null);
                            mRootView.findViewById(R.id.call_img).setOnClickListener(null);
                            mRootView.findViewById(R.id.report_img).setOnClickListener(null);
//							Utils.showMessageDialog(getActivity(), getResources().getString(R.string.alert), "User profile not found.");
                        }


                    }

                    @Override
                    public void onErrorReceived(String error) {
                        Toast.makeText(getActivity(), getString(R.string.try_again), Toast.LENGTH_SHORT).show();
                    }
                });
                getProfileHandymanListRequestTask.execute(handyman_id, client_id, category_id, sub_category_id);
            } else {
                Utils.showMessageDialog(getActivity(), getResources().getString(R.string.alert),
                        getResources().getString(R.string.connection));
            }
        } catch (NullPointerException e) {
            e.printStackTrace();
        }

    }

    private void FavoriteHandyman(String handyman_id, String client_id, final String user_like) {
        try {
            if (Utils.checkInternetConnection(getActivity())) {
                FavouriteHandymanRequestTask favouriteHandymanRequestTask = new FavouriteHandymanRequestTask(getActivity());
                favouriteHandymanRequestTask.setAsyncCallListener(new AsyncCallListener() {

                    @SuppressWarnings("unchecked")
                    @Override
                    public void onResponseReceived(Object response) {
                        HireHandymanModel hireHandymanModel = (HireHandymanModel) response;
                        if (hireHandymanModel.success.equalsIgnoreCase("1")) {
//	                            Toast.makeText(getActivity(), hireHandymanModel.message , Toast.LENGTH_SHORT).show();

                        } else if (hireHandymanModel.success.equalsIgnoreCase("0")) {
                            Toast.makeText(getActivity(), hireHandymanModel.message, Toast.LENGTH_SHORT).show();
                        }

                    }

                    @Override
                    public void onErrorReceived(String error) {
                        Toast.makeText(getActivity(), getString(R.string.try_again), Toast.LENGTH_SHORT).show();
                    }
                });
                favouriteHandymanRequestTask.execute(handyman_id, client_id, user_like);
            } else {
                Utils.showMessageDialog(getActivity(), getResources().getString(R.string.alert), getResources().getString(R.string.connection));
            }
        } catch (NullPointerException e) {
            e.printStackTrace();
        }

    }


    @SuppressLint("UseValueOf")
    private String getAge(int year, int month, int day) {
        Calendar dob = Calendar.getInstance();
        Calendar today = Calendar.getInstance();

        dob.set(year, month, day);

        int age = today.get(Calendar.YEAR) - dob.get(Calendar.YEAR);

        if (today.get(Calendar.DAY_OF_YEAR) < dob.get(Calendar.DAY_OF_YEAR)) {
            age--;
        }

        Integer ageInt = new Integer(age);
        String ageS = ageInt.toString();

        return ageS;
    }

    private void getHandymanCity(double latitude, double longitude) {
        try {
            Geocoder geocoder = new Geocoder(getActivity(), Locale.getDefault());
            List<Address> addressList = geocoder.getFromLocation(latitude, longitude, 1);
            handymanCity = addressList.get(0).getLocality();
            Logger.e(TAG, "Handyman City ::" + handymanCity);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void getCustomerCity(double latitude, double longitude) {
        try {
            Geocoder geocoder = new Geocoder(getActivity(), Locale.getDefault());
            List<Address> addressList = geocoder.getFromLocation(latitude, longitude, 1);
            customerCity = addressList.get(0).getLocality();
            Logger.e(TAG, "Customer City ::" + customerCity);
        } catch (IOException e) {
            e.printStackTrace();
        }
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
        if (Utils.checkInternetConnection(getActivity())) {
            if (googleApiClient != null) {
                if (googleApiClient.isConnected()) {
                    startLocationUpdates();
                    Logger.e(TAG, "onResume()");
                }
            }
        } else {
            Toast.makeText(getActivity(), getResources().getString(R.string.connection), Toast.LENGTH_SHORT).show();
        }
    }


    @Override
    public void onStop() {
        if (googleApiClient != null) {
            googleApiClient.disconnect();
            Logger.e(TAG, "onStop()");
        }
        super.onStop();
    }

    @Override
    public void onPause() {
        super.onPause();
//        stopLocationUpdates();
        Logger.e(TAG, "onPause()");
    }

    protected void stopLocationUpdates() {
        if (googleApiClient != null && googleApiClient.isConnected()) {
            LocationServices.FusedLocationApi.removeLocationUpdates(googleApiClient, this);
            Logger.e(TAG, "stopLocationUpdates()");
        }
    }

    @Override
    public void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
        Logger.d(TAG, "onDestroy");
    }


    @Override
    public void onConnected(Bundle bundle) {
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
        if (googleApiClient != null) {
            googleApiClient.connect();
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        LocationManager manager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
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
            SplashActivity.latitude = location.getLatitude();
            SplashActivity.longitude = location.getLongitude();
            Logger.e(TAG, "UpdateLatLang()" + "LATITUDE :: " + SplashActivity.latitude + "LONGITUDE :: " + SplashActivity.longitude);
        } else {
            Logger.e(TAG, "UpdateLatLang()" + "LATITUDE :: " + SplashActivity.latitude + "LONGITUDE :: " + SplashActivity.longitude);
        }

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }


}
