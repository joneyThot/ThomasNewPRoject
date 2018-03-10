package com.android.handyman;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import android.widget.Toast;

import com.android.handyman.fragment.ServiceAtHomeFragment;
import com.android.handyman.model.RegisterModel;
import com.android.handyman.service.AsyncCallListener;
import com.android.handyman.service.OTPRegisterRequestTask;
import com.android.handyman.service.OTPRequestTask;
import com.android.handyman.service.Utils;

public class OtpPasswordActivity extends Activity implements OnClickListener {

	private static String TAG = "OtpPasswordActivity";
	private SharedPreferences mSharedPreferences;
	EditText mOTPNo;
	String mobile_no,otp_code,from_ragister = null, from_forgot = null;
	private String From = "";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		getActionBar().hide();
		setContentView(R.layout.activity_otp_password);

		mSharedPreferences = getSharedPreferences(Utils.PREF_NAME, MODE_PRIVATE);
		initView();

	}

	private void initView() {

		Bundle bundle = getIntent().getExtras();
		From = bundle.getString("FROM");
		if(!bundle.getString(Utils.MOBILE_NO).equalsIgnoreCase(null))
			mobile_no = bundle.getString(Utils.MOBILE_NO);
		if(!bundle.getString(Utils.OTP_CODE).equalsIgnoreCase(null))
			otp_code = bundle.getString(Utils.OTP_CODE);
		
		
		Utils.showMessageDialog(this, getResources().getString(R.string.alert), "Your OTP Code is " + mSharedPreferences.getString(Utils.OTP_CODE, ""));
		
		((ImageView) findViewById(R.id.backBtn)).setVisibility(View.VISIBLE);
		((ImageView) findViewById(R.id.backBtn)).setOnClickListener(this);
		findViewById(R.id.title_back).setVisibility(View.VISIBLE);
		((TextView) findViewById(R.id.title_back)).setText(getString(R.string.otp));
		
		mOTPNo = (EditText) findViewById(R.id.otp_edittxt);
		findViewById(R.id.otpSubmitButton).setOnClickListener(this);
		
		mOTPNo.setOnEditorActionListener(new OnEditorActionListener(){
            @Override
            public boolean onEditorAction(TextView arg0, int arg1, KeyEvent arg2) {
                if (arg1 == EditorInfo.IME_ACTION_GO) {
                    findViewById(R.id.otpSubmitButton).performClick();
                }
                return false;
            }
        });
		
	}

	@Override
	public void onClick(View v) {
		
		switch (v.getId()) {
		case R.id.backBtn:
			finish();
			break;
			
		case R.id.otpSubmitButton:
			if(fieldValidation()){
//				if(mOTPNo.getText().toString().equalsIgnoreCase(mSharedPreferences.getString(Utils.OTP_CODE, ""))){
//					Toast.makeText(OtpPasswordActivity.this, "PLEASE CHECK OTP MATCHED" , Toast.LENGTH_SHORT).show();
//					startActivity(new Intent(OtpPasswordActivity.this, ChangePasswordActivity.class));
//        			overridePendingTransition(R.anim.slide_in_left,R.anim.slide_out_left);
//        			finish();
//				} else {
//					Toast.makeText(OtpPasswordActivity.this, "PLEASE CHECK OTP NOT MATCHED" , Toast.LENGTH_SHORT).show();
//				}
				
				if(From.equalsIgnoreCase("REGISTER")){
					onOTPPasswordRegister(mobile_no, mOTPNo.getText().toString());
				}  else if(From.equalsIgnoreCase("FORGOT")) {
					onOTPPassword(mobile_no, mOTPNo.getText().toString());	
				}

				
			}
			break;
		}

	}
	
private void onOTPPassword(String mobile, String otp) {
		
        if (Utils.checkInternetConnection(this)) {
            OTPRequestTask otpRequestTask = new OTPRequestTask(this);
            otpRequestTask.setAsyncCallListener(new AsyncCallListener() {

                @Override
                public void onResponseReceived(Object response) {
                    RegisterModel registerModel = (RegisterModel) response;
                    if(registerModel.success.equalsIgnoreCase("1")) {
                            Toast.makeText(OtpPasswordActivity.this, registerModel.message , Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(OtpPasswordActivity.this, ChangePasswordActivity.class));
                			overridePendingTransition(R.anim.slide_in_left,R.anim.slide_out_left);
                			finish();
                        } else if (registerModel.success.equalsIgnoreCase("0")) {
//                            Toast.makeText(OtpPasswordActivity.this, registerModel.message , Toast.LENGTH_SHORT).show();
                        	Utils.showMessageDialog(OtpPasswordActivity.this, getResources().getString(R.string.alert), registerModel.message);
                        }
                    }

                @Override
                public void onErrorReceived(String error) {
                    Toast.makeText(OtpPasswordActivity.this, getString(R.string.try_again), Toast.LENGTH_SHORT).show();
                }
            });
            otpRequestTask.execute(mobile,otp);
        } else {
            Toast.makeText(OtpPasswordActivity.this, getString(R.string.connection), Toast.LENGTH_SHORT).show();
        }
    }

private void onOTPPasswordRegister(String mobile, String otp) {
	
    if (Utils.checkInternetConnection(this)) {
    	OTPRegisterRequestTask otpRegisterRequestTask = new OTPRegisterRequestTask(this);
    	otpRegisterRequestTask.setAsyncCallListener(new AsyncCallListener() {

            @Override
            public void onResponseReceived(Object response) {
                RegisterModel registerModel = (RegisterModel) response;
                if(registerModel.success.equalsIgnoreCase("1")) {
                        Toast.makeText(OtpPasswordActivity.this, registerModel.message , Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(OtpPasswordActivity.this, LoginActivity.class));
                        overridePendingTransition(R.anim.slide_in_left,R.anim.slide_out_left);
            			finish();
                    } else if (registerModel.success.equalsIgnoreCase("0")) {
//                        Toast.makeText(OtpPasswordActivity.this, registerModel.message , Toast.LENGTH_SHORT).show();
                    	Utils.showMessageDialog(OtpPasswordActivity.this, getResources().getString(R.string.alert), registerModel.message);
                    }
                }

            @Override
            public void onErrorReceived(String error) {
                Toast.makeText(OtpPasswordActivity.this, getString(R.string.try_again), Toast.LENGTH_SHORT).show();
            }
        });
    	otpRegisterRequestTask.execute(mobile,otp);
    } else {
        Toast.makeText(OtpPasswordActivity.this, getString(R.string.connection), Toast.LENGTH_SHORT).show();
    }
}
	
	public boolean fieldValidation() {
		boolean flag = true;
		if (!Utils.validateString(mOTPNo.getText().toString())) {
			flag = false;
			Utils.showMessageDialog(this, getResources().getString(R.string.alert), getResources().getString(R.string.enter_mobile_no));
		} else if(mOTPNo.getText().toString().trim().length() < 4 ){
			flag = false;
			Utils.showMessageDialog(this, getResources().getString(R.string.alert), "OTP Length minimum 4.");
		}
		return flag;
	}

	
	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		super.onBackPressed();
		finish();
	}
}
