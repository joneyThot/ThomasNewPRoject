package com.handyman;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Toast;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.handyman.adapter.SliderPagerAdapter;
import com.handyman.service.Utils;
import com.handyman.view.CirclePageIndicator;

public class SplashActivityTwo extends Activity implements OnClickListener {

    SharedPreferences mSharedPreferences;
    private static String TAG = "SplashActivityTwo";
    ViewPager mViewPager;
    CirclePageIndicator mCirclePageIndicator;
    SliderPagerAdapter sliderPagerAdapter;
    final private int REQUEST_CODE_ASK_PERMISSIONS = 123;
    private boolean isNerverAskmeAgain = false, denyPermission = false;
    int currentapiVersion;
    private GoogleApiClient googleApiClient;
    private LocationRequest locationRequest;
    final static int REQUEST_LOCATION = 199;
//	public static double latitude = 0, longitude = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub

        super.onCreate(savedInstanceState);

        currentapiVersion = Build.VERSION.SDK_INT;
        if (currentapiVersion < Build.VERSION_CODES.LOLLIPOP) {
            getActionBar().hide();
        }
        setContentView(R.layout.splash_two);

        mSharedPreferences = getSharedPreferences(Utils.PREF_NAME, MODE_PRIVATE);
        initView();

    }

    private void initView() {

        findViewById(R.id.loginbtn).setOnClickListener(this);
        findViewById(R.id.signupbtn).setOnClickListener(this);
        findViewById(R.id.loginbtn).setVisibility(View.GONE);
        findViewById(R.id.signupbtn).setVisibility(View.GONE);
        findViewById(R.id.skip_text).setOnClickListener(this);
        mViewPager = (ViewPager) findViewById(R.id.viewpager);
        mCirclePageIndicator = (CirclePageIndicator) findViewById(R.id.circle_pager_Indicator);

        mViewPager.setAdapter(new SliderPagerAdapter(this));
        mCirclePageIndicator.setViewPager(mViewPager);
        mCirclePageIndicator.setCurrentItem(0);

        mCirclePageIndicator.setOnPageChangeListener(new OnPageChangeListener() {

            @Override
            public void onPageSelected(int position) {

                if (position == 5) {
                    findViewById(R.id.loginbtn).setVisibility(View.VISIBLE);
                    findViewById(R.id.signupbtn).setVisibility(View.VISIBLE);
                    findViewById(R.id.rl1).setBackgroundResource(R.color.white);
                } else {
                    findViewById(R.id.loginbtn).setVisibility(View.GONE);
                    findViewById(R.id.signupbtn).setVisibility(View.GONE);
                    findViewById(R.id.rl1).setBackgroundResource(android.R.color.transparent);
                }
            }

            @Override
            public void onPageScrolled(int arg0, float arg1, int arg2) {
                // TODO Auto-generated method stub

            }

            @Override
            public void onPageScrollStateChanged(int arg0) {
                // TODO Auto-generated method stub

            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.loginbtn:
                Utils.storeString(mSharedPreferences, TAG, TAG);
                startActivity(new Intent(SplashActivityTwo.this, LoginActivity.class));
                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_left);
                finish();
                break;

            case R.id.signupbtn:
                Utils.storeString(mSharedPreferences, TAG, TAG);
                startActivity(new Intent(SplashActivityTwo.this, RegisterActivity.class));
                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_left);
                break;

            case R.id.skip_text:
                Utils.storeString(mSharedPreferences, TAG, TAG);
                startActivity(new Intent(SplashActivityTwo.this, LoginActivity.class));
                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_left);
                finish();
                break;

            default:
                break;
        }
    }



}
