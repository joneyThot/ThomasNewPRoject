package com.android.handyman;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.provider.Settings.Secure;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import android.widget.Toast;

import com.android.handyman.logger.Logger;
import com.android.handyman.model.RegisterModel;
import com.android.handyman.service.AsyncCallListener;
import com.android.handyman.service.GPSTracker;
import com.android.handyman.service.LoginRequestTask;
import com.android.handyman.service.Utils;
import com.google.android.gcm.GCMRegistrar;

public class LoginActivity extends Activity implements OnClickListener {
	
//	String GcmProjectID = "1085788808372";
	private static String TAG = "LoginActivity";
	private SharedPreferences mSharedPreferences;
	EditText mMobileNo, mPassword;
	String device_Id = "";
	String regId = "";
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		getActionBar().hide();
		setContentView(R.layout.activity_login);

		mSharedPreferences = getSharedPreferences(Utils.PREF_NAME, MODE_PRIVATE);
		device_Id = Secure.getString(this.getContentResolver(), Secure.ANDROID_ID);
//		deviceid();
		initView();
		
	}

	private void initView() {

		mMobileNo = (EditText) findViewById(R.id.mobileEditText);
		mPassword = (EditText) findViewById(R.id.passwordEditText);
		
		findViewById(R.id.loginButton).setOnClickListener(this);
		findViewById(R.id.registerbtn).setOnClickListener(this);
		findViewById(R.id.forgotTxt).setOnClickListener(this);
		
		boolean isNetAvailable = Utils.checkInternetConnection(this);
		if (!isNetAvailable) {
			Utils.showMessageDialog(this,getResources().getString(R.string.Error),getResources().getString(R.string.connection));
		} else {
//			if (Utils.checkPlayServices(this)) {
//				GCMRegistrar.checkDevice(this);
//				GCMRegistrar.checkManifest(this);
//				regId = GCMRegistrar.getRegistrationId(this);
//				Utils.storeString(mSharedPreferences, Utils.REG_ID, regId);
//
//				registerReceiver(mHandleMessageReceiver, new IntentFilter(Utils.DISPLAY_MESSAGE_ACTION));
//				
//				
//				if(!GCMRegistrar.isRegistered(this)){
//					GCMRegistrar.register(this, Utils.SENDER_ID);
//				} else {
//					String GCMRegistrationId = mSharedPreferences.getString(Utils.REG_ID, "");
//					Logger.e("GCMRegistrationId", "GCMRegistrationId : " + GCMRegistrationId);
//				}
//				
//			} else {
//				Toast.makeText(this, getResources().getString(R.string.install_googleplay), Toast.LENGTH_LONG).show();
//			}
			
			if (Utils.checkPlayServices(this)) {
				GCMRegistrar.checkDevice(this);
				GCMRegistrar.checkManifest(this);

				registerReceiver(mHandleMessageReceiver, new IntentFilter(Utils.DISPLAY_MESSAGE_ACTION));

				if (!GCMRegistrar.isRegistered(this)) {
					GCMRegistrar.register(this, Utils.SENDER_ID);
				} else {
					String GCMRegistrationId = mSharedPreferences.getString(Utils.REG_ID, "");
					Logger.e("GCMRegistrationId", "GCMRegistrationId : " + GCMRegistrationId);
				}
			} else {
				Toast.makeText(this, getResources().getString(R.string.install_googleplay), Toast.LENGTH_LONG).show();
			}
		} 
		
		mPassword.setOnEditorActionListener(new OnEditorActionListener(){
            @Override
            public boolean onEditorAction(TextView arg0, int arg1, KeyEvent arg2) {
                if (arg1 == EditorInfo.IME_ACTION_GO) {
                    findViewById(R.id.loginButton).performClick();
                }
                return false;
            }
        });
		
		
		
	}
	
	/*public String deviceid() {

        checkNotNull(Utils.SENDER_ID, "SENDER_ID");
        GCMRegistrar.checkDevice(this);
        GCMRegistrar.checkManifest(this);
        String regId = GCMRegistrar.getRegistrationId(this);

        Logger.d("Registration Info", "registration id: " + regId);
        System.out.println("Registration Id 1--->:" + regId);

        if (regId.equals("")) {
            GCMRegistrar.register(this, Utils.SENDER_ID);
            System.out.println("Registration Id 2--->:" + regId);

        } else {
            Logger.v("Registration Info", "Already registered");
        }
        return regId;

    }

	
	private void checkNotNull(Object reference, String name) {
        if (reference == null) {
            throw new NullPointerException();
        }
    }*/
	
	/**
	 * Receiving push messages
	 * */
	private final BroadcastReceiver mHandleMessageReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			// Waking up mobile if it is sleeping
			WakeLocker.acquire(getApplicationContext());
			// Releasing wake lock
			WakeLocker.release();
		}
	};
	
	@Override
	protected void onDestroy() {
		try {
			unregisterReceiver(mHandleMessageReceiver);
			GCMRegistrar.onDestroy(this);
		} catch (Exception e) {
			Logger.e("UnRegister Receiver Error", "> " + e.getMessage());
		}
		super.onDestroy();
	}


	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.loginButton:
			GPSTracker gpsTracker = Utils.getCurrentLocation(this);
			if (gpsTracker != null) {
				SplashActivity.latitude = gpsTracker.getLatitude();
				SplashActivity.longitude = gpsTracker.getLongitude();
				
				if (SplashActivity.latitude != 0 && SplashActivity.longitude != 0) {
					if(fieldValidation()) {
						onLogin(mMobileNo.getText().toString(),mPassword.getText().toString(),"customer",device_Id, mSharedPreferences.getString(Utils.REG_ID, "") ,String.valueOf(SplashActivity.latitude), String.valueOf(SplashActivity.longitude));
					}
				}
				
			} else {
				Toast.makeText(getApplicationContext(), "Please On GPS" , Toast.LENGTH_SHORT).show();
			}
			
			break;

		case R.id.forgotTxt:
			startActivity(new Intent(LoginActivity.this,ForgotPasswordActivity.class));
			overridePendingTransition(R.anim.slide_in_left,R.anim.slide_out_left);
			
			break;

		case R.id.registerbtn:
			startActivity(new Intent(LoginActivity.this,RegisterActivity.class));
			overridePendingTransition(R.anim.slide_in_left,R.anim.slide_out_left);
//			finish();
			break;

		}

	}
	
	private void onLogin(String mobile,	String password ,String user_type, String device_id, String device_token, String lat, String lng) {
		
        if (Utils.checkInternetConnection(this)) {
            LoginRequestTask loginRequestTask = new LoginRequestTask(this);
            loginRequestTask.setAsyncCallListener(new AsyncCallListener() {

                @Override
                public void onResponseReceived(Object response) {
                    RegisterModel registerModel = (RegisterModel) response;
                    if(registerModel.success.equalsIgnoreCase("1")) {
//						Toast.makeText(LoginActivity.this,registerModel.message, Toast.LENGTH_SHORT).show();
//						Utils.showMessageDialog(LoginActivity.this, getResources().getString(R.string.alert), registerModel.message);
						
							Utils.storeString(mSharedPreferences, Utils.MOBILE_NO, mMobileNo.getText().toString());
							Utils.storeString(mSharedPreferences, Utils.PASSWORD, mPassword.getText().toString());
							Utils.storeString(mSharedPreferences, Utils.USER_ID, registerModel.user.id);
							
							Intent intent = new Intent(LoginActivity.this, MainActivity.class);
							intent.putExtra("FROM", "LOGIN");
							startActivity(intent);
							overridePendingTransition(R.anim.slide_in_left,R.anim.slide_out_left);
							finish();
							
                        } else if (registerModel.success.equalsIgnoreCase("0")) {
//                            Toast.makeText(LoginActivity.this, registerModel.message , Toast.LENGTH_SHORT).show();
    						Utils.showMessageDialog(LoginActivity.this, getResources().getString(R.string.alert), registerModel.message);
                        }
                    }

                @Override
                public void onErrorReceived(String error) {
                    Toast.makeText(LoginActivity.this, getString(R.string.try_again), Toast.LENGTH_SHORT).show();
                }
            });
            loginRequestTask.execute(mobile, password,user_type, device_id, device_token,lat,lng);
        } else {
            Toast.makeText(LoginActivity.this, getString(R.string.connection), Toast.LENGTH_SHORT).show();
        }
    }
	
	public boolean fieldValidation() {
		boolean flag = true;
		if (!Utils.validateString(mMobileNo.getText().toString())) {
			flag = false;
			Utils.showMessageDialog(this, getResources().getString(R.string.alert), getResources().getString(R.string.enter_mobile_no));
			mMobileNo.requestFocus();
		} else if(mMobileNo.getText().toString().trim().length() < 10 ){
			flag = false;
			Utils.showMessageDialog(this, getResources().getString(R.string.alert), getResources().getString(R.string.enter_mobile_no_length));
			mMobileNo.requestFocus();
		}else if (!Utils.validateString(mPassword.getText().toString())) {
			flag = false;
			Utils.showMessageDialog(this, getResources().getString(R.string.alert), getResources().getString(R.string.enter_password));
			mPassword.requestFocus();
		} else if (mPassword.getText().toString().trim().length() < 8) {
			flag = false;
			Utils.showMessageDialog(this, getResources().getString(R.string.alert), getResources().getString(R.string.check_password_length));
			mPassword.requestFocus();
		}
		return flag;
	}
	

}
