package com.handyman;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentManager.OnBackStackChangedListener;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.LocationListener;
import com.handyman.fragment.AdvertiseFragment;
import com.handyman.fragment.MyHiringsFragment;
import com.handyman.fragment.PushnotificationCustomerCompleteFragment;
import com.handyman.fragment.ServiceAtHomeFragment;
import com.handyman.fragment.SlidingMenuFragment;
import com.handyman.fragment.TermsAndConditionFragment;
import com.handyman.logger.Logger;
import com.handyman.model.AdvertiseListModel;
import com.handyman.model.DataModel;
import com.handyman.service.AsyncCallListener;
import com.handyman.service.GPSTracker;
import com.handyman.service.GetAdvertiseRequestTask;
import com.handyman.service.GetDominNameRequestTask;
import com.handyman.service.Utils;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu.OnOpenListener;

import java.util.ArrayList;

@SuppressLint("NewApi")
public class MainActivity extends BaseActivity implements OnClickListener, LocationListener {

    private String From = "";

    public MainActivity() {
        super(R.string.app_name);
    }

    private static String TAG = "MainActivity";
    SharedPreferences sharedPreferences;
    TextView title;
    Fragment fr = null;
    boolean locPres = false;
    public static boolean doubleBackToExitPressedOnce = false;
    View decorView;
    final private int REQUEST_CODE_ASK_PERMISSIONS = 123;
    ArrayList<AdvertiseListModel> advertiseListModels = new ArrayList<AdvertiseListModel>();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        int currentapiVersion = Build.VERSION.SDK_INT;
        if (currentapiVersion < Build.VERSION_CODES.LOLLIPOP) {
            getActionBar().hide();
        }

//        if (currentapiVersion > Build.VERSION_CODES.LOLLIPOP) {
//            // Do something for lollipop and above versions
////			if (!Utils.denyFlag) {
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//                if ((this.checkSelfPermission(android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) ||
//                        (this.checkSelfPermission(android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) ||
//                        (this.checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)) {
//
//                    requestPermissions(new String[]{android.Manifest.permission.ACCESS_COARSE_LOCATION, android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.WRITE_EXTERNAL_STORAGE},
//                            REQUEST_CODE_ASK_PERMISSIONS);
//                }
//            }
////			}
//        }
        setContentView(R.layout.activity_main);

        startService(new Intent(MainActivity.this, LocationUpdaterService.class));

        initview();
        drawermenuFunction();
//        getDomin();

    }


    private void initview() {
        sharedPreferences = getSharedPreferences(Utils.PREF_NAME, MODE_PRIVATE);
        getSlidingMenu().setMode(SlidingMenu.LEFT);
        getSlidingMenu().setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);
        decorView = getWindow().getDecorView();

        findViewById(R.id.title).setVisibility(View.VISIBLE);
//		findViewById(R.id.search_bar).setVisibility(View.VISIBLE);
        findViewById(R.id.iconBtn).setVisibility(View.VISIBLE);
        title = (TextView) findViewById(R.id.title);
        //title.setText(R.string.profile_txt);

        findViewById(R.id.iconBtn).setOnClickListener(this);
        findViewById(R.id.menuBtn).setOnClickListener(this);
        findViewById(R.id.backBtn).setOnClickListener(this);
        findViewById(R.id.home_txt).setOnClickListener(this);
        findViewById(R.id.btnClose).setOnClickListener(this);

        if ((sharedPreferences.getString(Utils.USER_ID, "").isEmpty())) {
            startActivity(new Intent(MainActivity.this, LoginActivity.class));
            finish();

        } else {
            Bundle bundle = getIntent().getExtras();
            if (bundle != null) {
                From = bundle.getString("FROM");
                if (From.equalsIgnoreCase("SplashActivity")) {
                    getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, new ServiceAtHomeFragment()).commit();
                    doubleBackToExitPressedOnce = true;
                } else if (From.equalsIgnoreCase("LOGIN")) {
                    getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, new ServiceAtHomeFragment()).commit();
                    doubleBackToExitPressedOnce = true;
                } else if (From.equalsIgnoreCase("GCMIntentService")) {
                    doubleBackToExitPressedOnce = false;
                    if ((sharedPreferences.getString(Utils.NOTI_HIRE_STATUS, "").isEmpty()) && (sharedPreferences.getString(Utils.NOTI_ADVERTISE, "").isEmpty()) && (sharedPreferences.getString(Utils.NOTI_COINS, "").isEmpty())) {
                        getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, new TermsAndConditionFragment()).commit();
                    } else {
                        Utils.notificationFlag = true;
                        PushnotificationCustomerCompleteFragment pushnotificationCustomerCompleteFragment = new PushnotificationCustomerCompleteFragment();
                        Bundle pushBundle = new Bundle();
                        pushBundle.putString("resposne", bundle.getString("response"));
                        pushnotificationCustomerCompleteFragment.setArguments(pushBundle);
                        getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, pushnotificationCustomerCompleteFragment).commit();
                    }
                }
            }
        }

    }

    public void setTitleText(String title, String title_back, String txtUrl, int backbtn, int iconbtn, int menubtn, int hometxt, int btnClose) {
        findViewById(R.id.titleLayout).setVisibility(View.VISIBLE);
        ((TextView) findViewById(R.id.title)).setText(title);
        ((TextView) findViewById(R.id.title_back)).setText(title_back);
        ((TextView) findViewById(R.id.txtUrl)).setText(txtUrl);
        findViewById(R.id.title).setVisibility(View.VISIBLE);
        findViewById(R.id.title_back).setVisibility(View.VISIBLE);
        findViewById(R.id.txtUrl).setVisibility(View.VISIBLE);
        findViewById(R.id.backBtn).setVisibility(backbtn);
        findViewById(R.id.iconBtn).setVisibility(iconbtn);
        findViewById(R.id.menuBtn).setVisibility(menubtn);
        findViewById(R.id.home_txt).setVisibility(hometxt);
        findViewById(R.id.btnClose).setVisibility(btnClose);
    }

    public void hideTitleLayout() {
        findViewById(R.id.backBtn).setVisibility(View.GONE);
        findViewById(R.id.iconBtn).setVisibility(View.GONE);
        findViewById(R.id.title).setVisibility(View.GONE);
        findViewById(R.id.title_back).setVisibility(View.GONE);
        findViewById(R.id.menuBtn).setVisibility(View.GONE);
        findViewById(R.id.home_txt).setVisibility(View.GONE);
        findViewById(R.id.txtUrl).setVisibility(View.GONE);
        findViewById(R.id.btnClose).setVisibility(View.GONE);
    }

    public void hideTitleRelativeLayout() {
        findViewById(R.id.titleLayout).setVisibility(View.GONE);
    }

//	public void setActionbarTitle(String title, int Visibilty) {
//		((TextView) getActionBar().getCustomView().findViewById(R.id.title)).setText(title);
//		((ImageView) getActionBar().getCustomView().findViewById(R.id.iconBtn)).setVisibility(Visibilty);
//		((ImageView) getActionBar().getCustomView().findViewById(R.id.menuBtn)).setVisibility(Visibilty);
//	}

    @Override
    public void onClick(View v) {
        // TODO Auto-generated method stub
        switch (v.getId()) {
            case R.id.iconBtn:
                showMenu();
                if (!sharedPreferences.getString(Utils.EDIT_PROFILE_IMAGE, "").isEmpty()) {
                    SlidingMenuFragment slidingMenuFragment = new SlidingMenuFragment();
                    slidingMenuFragment.setImage(MainActivity.this);
                    Utils.storeString(sharedPreferences, Utils.EDIT_PROFILE_IMAGE, "");
                }

                if ((!sharedPreferences.getString(Utils.USER_ID, "").isEmpty())) {
                    getAdvertise(sharedPreferences.getString(Utils.USER_ID, ""));
                }

                GPSTracker gpsTracker = Utils.getCurrentLocation(MainActivity.this);
                if (gpsTracker != null) {
                    SplashActivity.latitude = gpsTracker.getLatitude();
                    SplashActivity.longitude = gpsTracker.getLongitude();
                }

                break;

            case R.id.backBtn:
                onBackPressed();
                break;

            case R.id.btnClose:
                onBackPressed();
                break;

            case R.id.menuBtn:
                break;

            case R.id.home_txt:
                Utils.onCategoryClickFlag = false;
                getSupportFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
                break;

        }
    }

    private void drawermenuFunction() {
        getSlidingMenu().setOnOpenListener(new OnOpenListener() {
            @Override
            public void onOpen() {
                SlidingMenuFragment.viewSelectedPosition();
            }
        });
    }

//	@Override
//	public boolean onCreateOptionsMenu(Menu menu) {
//	    return super.onCreateOptionsMenu(menu);
//	}
//	
//	@Override
//	public boolean onMenuItemSelected(int featureId, MenuItem item) {
//
//		int itemId = item.getItemId();
//		switch (itemId) {
//		case android.R.id.home:
//			Logger.e("", "clicked");
//			showMenu();
//			break;
//		}
//		return true;
//	}


    @Override
    public void onBackPressed() {
        hideSoftKeyboard();
        Utils.onCategoryClickFlag = false;
        if (!Utils.notificationFlag) {
            if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
                getSupportFragmentManager().addOnBackStackChangedListener(getListener());
                if (getSlidingMenu().isMenuShowing()) {
                    getSlidingMenu().toggle();
                } else {
                    super.onBackPressed();
                    return;
                }
            } else if (!doubleBackToExitPressedOnce) {
                this.doubleBackToExitPressedOnce = true;
                getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, new ServiceAtHomeFragment()).commit();

            } else {
                super.onBackPressed();
                return;
            }
        } else {
            Utils.notificationFlag = false;
            String hire_status = sharedPreferences.getString(Utils.NOTI_HIRE_STATUS, "");
            String coin_text = sharedPreferences.getString(Utils.NOTI_COINS, "");

            if (Utils.validateString(hire_status)) {

                // remove fragment
                removePushNotifocationFragment();

                if (hire_status.equalsIgnoreCase("pending")) {
                    MyHiringsFragment myHiringsFragment = new MyHiringsFragment();
                    Bundle bundle1 = new Bundle();
                    bundle1.putString("Pending", "Pending");
                    myHiringsFragment.setArguments(bundle1);
                    getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, myHiringsFragment).commit();

                } else if (hire_status.equalsIgnoreCase("active")) {
                    MyHiringsFragment myHiringsFragment = new MyHiringsFragment();
                    Bundle bundle1 = new Bundle();
                    bundle1.putString("Active", "Active");
                    myHiringsFragment.setArguments(bundle1);
                    getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, myHiringsFragment).commit();

                } else if (hire_status.equalsIgnoreCase("start")) {
                    MyHiringsFragment myHiringsFragment = new MyHiringsFragment();
                    Bundle bundle1 = new Bundle();
                    bundle1.putString("Start", "Start");
                    myHiringsFragment.setArguments(bundle1);
                    getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, myHiringsFragment).commit();

                } else if (hire_status.equalsIgnoreCase("cancelled")) {
                    MyHiringsFragment myHiringsFragment = new MyHiringsFragment();
                    Bundle bundle1 = new Bundle();
                    bundle1.putString("Cancel", "Cancel");
                    myHiringsFragment.setArguments(bundle1);
                    getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, myHiringsFragment).commit();

                } else if (hire_status.equalsIgnoreCase("completed")) {
                    MyHiringsFragment myHiringsFragment = new MyHiringsFragment();
                    Bundle bundle1 = new Bundle();
                    bundle1.putString("Complete", "Complete");
                    myHiringsFragment.setArguments(bundle1);
                    getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, myHiringsFragment).commit();

                } else if (hire_status.equalsIgnoreCase("declined")) {
                    MyHiringsFragment myHiringsFragment = new MyHiringsFragment();
                    Bundle bundle1 = new Bundle();
                    bundle1.putString("declined", "declined");
                    myHiringsFragment.setArguments(bundle1);
                    getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, myHiringsFragment).commit();

                }
                Utils.storeString(sharedPreferences, Utils.NOTI_HIRE_STATUS, "");
            } else if (Utils.validateString(coin_text)) {

//                Utils.storeString(sharedPreferences, Utils.NOTI_COINS, "");
                doubleBackToExitPressedOnce = false;
                SlidingMenuFragment.selectMenu(2);
                MyHiringsFragment myHiringsFragment = new MyHiringsFragment();
                Bundle bundle1 = new Bundle();
                bundle1.putString("Start", "Start");
                myHiringsFragment.setArguments(bundle1);
                getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, myHiringsFragment).commit();
            } else {
                doubleBackToExitPressedOnce = false;
                AdvertiseFragment mAdvertiseFragment = new AdvertiseFragment();
//              Bundle bundle1 = new Bundle();
//              bundle1.putString("Complete", "Complete");
//              mAdvertiseFragment.setArguments(bundle1);
                getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, mAdvertiseFragment).commit();
            }

        }

        /*if (!Utils.notificationFlag) {
                getSupportFragmentManager().addOnBackStackChangedListener(getListener());
                if (getSlidingMenu().isMenuShowing()) {
                    getSlidingMenu().toggle();
                } else {
                    super.onBackPressed();
                }
            } else {
                Utils.notificationFlag = false;
                String hire_status = sharedPreferences.getString(Utils.NOTI_HIRE_STATUS, "");
                String coin_text = sharedPreferences.getString(Utils.NOTI_COINS, "");
                if (Utils.validateString(hire_status)) {
                    if (hire_status.equalsIgnoreCase("pending")) {
                        MyHiringsFragment myHiringsFragment = new MyHiringsFragment();
                        Bundle bundle1 = new Bundle();
                        bundle1.putString("Pending", "Pending");
                        myHiringsFragment.setArguments(bundle1);
                        getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, myHiringsFragment).commit();

                    } else if (hire_status.equalsIgnoreCase("active")) {
                        MyHiringsFragment myHiringsFragment = new MyHiringsFragment();
                        Bundle bundle1 = new Bundle();
                        bundle1.putString("Active", "Active");
                        myHiringsFragment.setArguments(bundle1);
                        getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, myHiringsFragment).commit();

                    } else if (hire_status.equalsIgnoreCase("start")) {
                        MyHiringsFragment myHiringsFragment = new MyHiringsFragment();
                        Bundle bundle1 = new Bundle();
                        bundle1.putString("Start", "Start");
                        myHiringsFragment.setArguments(bundle1);
                        getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, myHiringsFragment).commit();

                    } else if (hire_status.equalsIgnoreCase("cancelled")) {
                        MyHiringsFragment myHiringsFragment = new MyHiringsFragment();
                        Bundle bundle1 = new Bundle();
                        bundle1.putString("Cancel", "Cancel");
                        myHiringsFragment.setArguments(bundle1);
                        getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, myHiringsFragment).commit();

                    } else if (hire_status.equalsIgnoreCase("completed")) {
                        MyHiringsFragment myHiringsFragment = new MyHiringsFragment();
                        Bundle bundle1 = new Bundle();
                        bundle1.putString("Complete", "Complete");
                        myHiringsFragment.setArguments(bundle1);
                        getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, myHiringsFragment).commit();

                    } else if (hire_status.equalsIgnoreCase("declined")) {
                        MyHiringsFragment myHiringsFragment = new MyHiringsFragment();
                        Bundle bundle1 = new Bundle();
                        bundle1.putString("declined", "declined");
                        myHiringsFragment.setArguments(bundle1);
                        getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, myHiringsFragment).commit();

                    }
                    Utils.storeString(sharedPreferences, Utils.NOTI_HIRE_STATUS, "");
                } else if (Utils.validateString(coin_text)) {

//                Utils.storeString(sharedPreferences, Utils.NOTI_COINS, "");
                    SlidingMenuFragment.selectMenu(2);
                    MyHiringsFragment myHiringsFragment = new MyHiringsFragment();
                    Bundle bundle1 = new Bundle();
                    bundle1.putString("Start", "Start");
                    myHiringsFragment.setArguments(bundle1);
                    getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, myHiringsFragment).commit();
                } else {
                    AdvertiseFragment mAdvertiseFragment = new AdvertiseFragment();
//              Bundle bundle1 = new Bundle();
//              bundle1.putString("Complete", "Complete");
//              mAdvertiseFragment.setArguments(bundle1);
                    getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, mAdvertiseFragment).commit();
                }
            }*/

    }

    private OnBackStackChangedListener getListener() {
        hideSoftKeyboard();
        OnBackStackChangedListener result = new OnBackStackChangedListener() {
            public void onBackStackChanged() {
                FragmentManager fm = MainActivity.this.getSupportFragmentManager();
                Fragment fragment = null;

                if (fm != null) {
                    fragment = (Fragment) fm.findFragmentById(R.id.content_frame);
                    fragment.onResume();
                }
            }
        };

        return result;
    }

    public void hideSoftKeyboard() {
        if (getCurrentFocus() != null) {
            InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        }
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            decorView.setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        }
    }

    public void removePushNotifocationFragment(){
        FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction trans = manager.beginTransaction();
        trans.remove(new PushnotificationCustomerCompleteFragment());
        trans.commit();
        manager.popBackStack();
    }

//    @Override
//    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
//        switch (requestCode) {
//            case REQUEST_CODE_ASK_PERMISSIONS:
//                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                    // Permission Granted
//                } else {
//                    // Permission Denied
//                    Utils.callFalg = true;
//                }
//                break;
//            default:
//                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
//        }
//    }

    private void getAdvertise(String user_id) {
        if (Utils.checkInternetConnection(MainActivity.this)) {
            GetAdvertiseRequestTask getAdvertiseRequestTask = new GetAdvertiseRequestTask(MainActivity.this);
            getAdvertiseRequestTask.setAsyncCallListener(new AsyncCallListener() {

                @Override
                public void onResponseReceived(Object response) {
                    advertiseListModels.clear();
                    DataModel advertiseModel = (DataModel) response;

                    Utils.storeString(sharedPreferences, Utils.NOTI_ADVER, "");

                    if (advertiseModel.success.equalsIgnoreCase("1")) {
                        advertiseListModels.addAll(advertiseModel.getAdvertiseListModels());

                        Logger.e(TAG, "advertiseListModels SIZE -- " + advertiseListModels.size());

                        if (advertiseListModels.size() > 0) {
                            for (int i = 0; i < advertiseListModels.size(); i++) {
                                if (Utils.validateString(advertiseListModels.get(i).getIs_read())) {
                                    if (advertiseListModels.get(i).getIs_read().equalsIgnoreCase("0")) {
                                        Utils.storeString(sharedPreferences, Utils.NOTI_ADVER, Utils.NOTI_ADVER);
                                    }
                                }
                            }
                        }

                    }

                    SlidingMenuFragment slidingMenuFragment = new SlidingMenuFragment();
                    slidingMenuFragment.setColorNoti(MainActivity.this);

                }

                @Override
                public void onErrorReceived(String error) {
                    Toast.makeText(MainActivity.this, getString(R.string.try_again), Toast.LENGTH_SHORT).show();
                }
            });
            getAdvertiseRequestTask.execute(user_id);
        } /*else {
            Utils.showMessageDialog(getActivity(), getResources().getString(R.string.alert),
                    getResources().getString(R.string.connection));
        }*/
    }

    @Override
    public void onLocationChanged(Location location) {
        LocationManager manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        boolean statusOfGPS = manager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        if (statusOfGPS) {
            if (location != null) {
                SplashActivity.longitude = location.getLatitude();
                SplashActivity.longitude = location.getLongitude();
            }
        }
        Logger.e(TAG, "onLocationChanged()");
    }

    private void getDomin() {

        if (Utils.checkInternetConnection(MainActivity.this)) {
            GetDominNameRequestTask getDominNameRequestTask = new GetDominNameRequestTask(MainActivity.this);
            getDominNameRequestTask.setAsyncCallListener(new AsyncCallListener() {

                @Override
                public void onResponseReceived(Object response) {
                    AdvertiseListModel advertiseListModel = (AdvertiseListModel) response;

                    if (advertiseListModel.success.equalsIgnoreCase("1")) {

//                        if (Utils.validateString(advertiseListModel.getDomain_name())) {
//                            Utils.URL_SERVER_ADDRESS = advertiseListModel.getDomain_name();
//                        }
//
//                        if (Utils.validateString(advertiseListModel.getImage_url())) {
//                            Utils.IMAGE_URL = advertiseListModel.getImage_url();
//                        }

                        Utils.URL_SERVER_ADDRESS = sharedPreferences.getString(Utils.URL_ADDRESS, "");
                        Utils.IMAGE_URL = sharedPreferences.getString(Utils.IMAGE_URL_ADDRESS, "");

                    }
                }

                @Override
                public void onErrorReceived(String error) {
                    Toast.makeText(MainActivity.this, getString(R.string.try_again), Toast.LENGTH_SHORT).show();
                }
            });
            getDominNameRequestTask.execute();
        } /*else {
            Toast.makeText(SplashActivity.this, getString(R.string.connection), Toast.LENGTH_SHORT).show();
        }*/
    }
}
