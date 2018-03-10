package com.handy;

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
import com.handy.fragment.MyHiringsFragment_new;
import com.handy.fragment.PushnotificationCustomerCompleteFragment;
import com.handy.fragment.RequestStatusFragment;
import com.handy.fragment.SlidingMenuFragment;
import com.handy.logger.Logger;
import com.handy.model.RegisterModel;
import com.handy.service.AsyncCallListener;
import com.handy.service.GPSTracker;
import com.handy.service.GetDominNameRequestTask;
import com.handy.service.Utils;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu.OnOpenListener;

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
    final private int REQUEST_CODE_ASK_PERMISSIONS = 123;
    public static boolean doubleBackToExitPressedOnce = false;
    //	boolean notificationFlag = false;
    View decorView;
//	String is_requested  = "";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        int currentapiVersion = Build.VERSION.SDK_INT;
        if (currentapiVersion < Build.VERSION_CODES.LOLLIPOP) {
            getActionBar().hide();
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

//        if (currentapiVersion > Build.VERSION_CODES.LOLLIPOP){
//            // Do something for lollipop and above versions
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//                if ((this.checkSelfPermission(android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) ||
//                        (this.checkSelfPermission(android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) ||
//                        (this.checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)) {
//
//                    requestPermissions(new String[]{android.Manifest.permission.ACCESS_COARSE_LOCATION, android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, 0);
//                }
//            }
//        }

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

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            From = bundle.getString("FROM");
            if (From.equalsIgnoreCase("SplashActivity")) {
                doubleBackToExitPressedOnce = true;
                getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, new MyHiringsFragment_new()).commit();
            } else if (From.equalsIgnoreCase("LOGIN")) {
                doubleBackToExitPressedOnce = true;
                getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, new MyHiringsFragment_new()).commit();
            } else if (From.equalsIgnoreCase("GCMIntentService")) {
                if ((sharedPreferences.getString(Utils.NOTI_HIRE_STATUS, "").isEmpty())) {
                    doubleBackToExitPressedOnce = false;
                    RequestStatusFragment requestStatusFragment = new RequestStatusFragment();
                    Bundle pushBundle = new Bundle();
                    pushBundle.putString("resposne", bundle.getString("response"));
                    requestStatusFragment.setArguments(pushBundle);
                    getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, requestStatusFragment).commit();
                } else {
//                    if(sharedPreferences.getString(Utils.NOTI_HIRE_STATUS, "").equalsIgnoreCase("pending")){
//                        Utils.notificationFlag = false;
//                    } else {
//                        Utils.notificationFlag = true;
//                    }
                    doubleBackToExitPressedOnce = true;
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

    public void setTitleText(String title, String title_back, int backbtn, int iconbtn, int menubtn) {
        findViewById(R.id.titleLayout).setVisibility(View.VISIBLE);
        ((TextView) findViewById(R.id.title)).setText(title);
        ((TextView) findViewById(R.id.title_back)).setText(title_back);
        findViewById(R.id.title).setVisibility(View.VISIBLE);
        findViewById(R.id.title_back).setVisibility(View.VISIBLE);
        findViewById(R.id.backBtn).setVisibility(backbtn);
        findViewById(R.id.iconBtn).setVisibility(iconbtn);
        findViewById(R.id.menuBtn).setVisibility(menubtn);
    }

    public void hideTitleLayout() {
        findViewById(R.id.backBtn).setVisibility(View.GONE);
        findViewById(R.id.iconBtn).setVisibility(View.GONE);
        findViewById(R.id.title).setVisibility(View.GONE);
        findViewById(R.id.title_back).setVisibility(View.GONE);
        findViewById(R.id.menuBtn).setVisibility(View.GONE);
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
                hideSoftKeyboard();
                showMenu();

                if (!sharedPreferences.getString(Utils.IMAGEPATH, "").isEmpty()) {
                    SlidingMenuFragment slidingMenuFragment = new SlidingMenuFragment();
                    slidingMenuFragment.setImage(MainActivity.this);
                }

//                if(!sharedPreferences.getString("RequestStatusFragment", "").isEmpty()){
//                    SlidingMenuFragment.selectMenu(5);
//                    Utils.storeString(sharedPreferences, "RequestStatusFragment", "");
//                }

                GPSTracker gpsTracker = Utils.getCurrentLocation(MainActivity.this);
                if (gpsTracker != null) {
                    SplashActivity.latitude = gpsTracker.getLatitude();
                    SplashActivity.longitude = gpsTracker.getLongitude();
                }

                Utils.storeString(sharedPreferences, Utils.FROM_DATE, "");
                Utils.storeString(sharedPreferences, Utils.TO_DATE, "");
                break;

            case R.id.backBtn:
                onBackPressed();
                break;

            case R.id.menuBtn:
                break;
//		
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
                getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, new MyHiringsFragment_new()).commit();

            } else {
                super.onBackPressed();
                return;
            }
        } else {
            Utils.notificationFlag = false;
            String hire_status = sharedPreferences.getString(Utils.NOTI_HIRE_STATUS, "");

            if (Utils.validateString(hire_status)) {

                // remove fragment
                removePushNotifocationFragment();

                if (hire_status.equalsIgnoreCase("pending")) {
                    MyHiringsFragment_new myHiringsFragment_new = new MyHiringsFragment_new();
                    Bundle bundle1 = new Bundle();
                    bundle1.putString("Pending", "Pending");
                    myHiringsFragment_new.setArguments(bundle1);
                    getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, myHiringsFragment_new).commit();

                } else if (hire_status.equalsIgnoreCase("active")) {
                    MyHiringsFragment_new myHiringsFragment_new = new MyHiringsFragment_new();
                    Bundle bundle1 = new Bundle();
                    bundle1.putString("Active", "Active");
                    myHiringsFragment_new.setArguments(bundle1);
                    getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, myHiringsFragment_new).commit();

                } else if (hire_status.equalsIgnoreCase("start")) {
                    MyHiringsFragment_new myHiringsFragment_new = new MyHiringsFragment_new();
                    Bundle bundle1 = new Bundle();
                    bundle1.putString("Start", "Start");
                    myHiringsFragment_new.setArguments(bundle1);
                    getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, myHiringsFragment_new).commit();

                } else if (hire_status.equalsIgnoreCase("cancelled")) {
                    MyHiringsFragment_new myHiringsFragment_new = new MyHiringsFragment_new();
                    Bundle bundle1 = new Bundle();
                    bundle1.putString("Cancel", "Cancel");
                    myHiringsFragment_new.setArguments(bundle1);
                    getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, myHiringsFragment_new).commit();

                } else if (hire_status.equalsIgnoreCase("completed")) {
                    MyHiringsFragment_new myHiringsFragment_new = new MyHiringsFragment_new();
                    Bundle bundle1 = new Bundle();
                    bundle1.putString("Complete", "Complete");
                    myHiringsFragment_new.setArguments(bundle1);
                    getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, myHiringsFragment_new).commit();

                } else if (hire_status.equalsIgnoreCase("declined")) {
                    MyHiringsFragment_new myHiringsFragment_new = new MyHiringsFragment_new();
                    Bundle bundle1 = new Bundle();
                    bundle1.putString("declined", "declined");
                    myHiringsFragment_new.setArguments(bundle1);
                    getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, myHiringsFragment_new).commit();
                }
            }
        }
       /* if (!Utils.notificationFlag) {
            getSupportFragmentManager().addOnBackStackChangedListener(getListener());
            if (getSlidingMenu().isMenuShowing()) {
                getSlidingMenu().toggle();
            } else {
                super.onBackPressed();
            }
        } else {
            Utils.notificationFlag = false;
            String hire_status = sharedPreferences.getString(Utils.NOTI_HIRE_STATUS, "");

            if (Utils.validateString(hire_status)) {
                if (hire_status.equalsIgnoreCase("pending")) {
                    MyHiringsFragment_new myHiringsFragment_new = new MyHiringsFragment_new();
                    Bundle bundle1 = new Bundle();
                    bundle1.putString("Pending", "Pending");
                    myHiringsFragment_new.setArguments(bundle1);
                    getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, myHiringsFragment_new).commit();

                } else if (hire_status.equalsIgnoreCase("active")) {
                    MyHiringsFragment_new myHiringsFragment_new = new MyHiringsFragment_new();
                    Bundle bundle1 = new Bundle();
                    bundle1.putString("Active", "Active");
                    myHiringsFragment_new.setArguments(bundle1);
                    getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, myHiringsFragment_new).commit();

                } else if (hire_status.equalsIgnoreCase("start")) {
                    MyHiringsFragment_new myHiringsFragment_new = new MyHiringsFragment_new();
                    Bundle bundle1 = new Bundle();
                    bundle1.putString("Start", "Start");
                    myHiringsFragment_new.setArguments(bundle1);
                    getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, myHiringsFragment_new).commit();

                } else if (hire_status.equalsIgnoreCase("cancelled")) {
                    MyHiringsFragment_new myHiringsFragment_new = new MyHiringsFragment_new();
                    Bundle bundle1 = new Bundle();
                    bundle1.putString("Cancel", "Cancel");
                    myHiringsFragment_new.setArguments(bundle1);
                    getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, myHiringsFragment_new).commit();

                } else if (hire_status.equalsIgnoreCase("completed")) {
                    MyHiringsFragment_new myHiringsFragment_new = new MyHiringsFragment_new();
                    Bundle bundle1 = new Bundle();
                    bundle1.putString("Complete", "Complete");
                    myHiringsFragment_new.setArguments(bundle1);
                    getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, myHiringsFragment_new).commit();

                } else if (hire_status.equalsIgnoreCase("declined")) {
                    MyHiringsFragment_new myHiringsFragment_new = new MyHiringsFragment_new();
                    Bundle bundle1 = new Bundle();
                    bundle1.putString("declined", "declined");
                    myHiringsFragment_new.setArguments(bundle1);
                    getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, myHiringsFragment_new).commit();
                }
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

    public void removePushNotifocationFragment(){
        FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction trans = manager.beginTransaction();
        trans.remove(new PushnotificationCustomerCompleteFragment());
        trans.commit();
        manager.popBackStack();
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
                    RegisterModel registerModel = (RegisterModel) response;

                    if (registerModel.success.equalsIgnoreCase("1")) {

//                        if (Utils.validateString(registerModel.getDomain_name())) {
//                            Utils.URL_SERVER_ADDRESS = registerModel.getDomain_name();
//
//                        }
//
//                        if (Utils.validateString(registerModel.getImage_url())) {
//                            Utils.IMAGE_URL = registerModel.getImage_url();
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
