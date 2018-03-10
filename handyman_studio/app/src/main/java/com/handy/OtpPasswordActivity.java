package com.handy;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import android.widget.Toast;

import com.handy.logger.Logger;
import com.handy.model.RegisterModel;
import com.handy.service.AsyncCallListener;
import com.handy.service.OTPForgotResendRequestTask;
import com.handy.service.OTPRegisterRequestTask;
import com.handy.service.OTPRequestTask;
import com.handy.service.Utils;

public class OtpPasswordActivity extends Activity implements OnClickListener {

	private static String TAG = "OtpPasswordActivity";
	private SharedPreferences mSharedPreferences;
	EditText mOTPNo;
	String mobile_no,otp_code,from_ragister = null, from_forgot = null;
	private String From = "", number = "";
	Dialog otpDialog;
	private ProgressDialog mProgressDialog;
	Handler mHandler = new Handler();
	Runnable mRunnable;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		int currentapiVersion = Build.VERSION.SDK_INT;
		if(currentapiVersion < Build.VERSION_CODES.LOLLIPOP) {
			getActionBar().hide();
		}
		setContentView(R.layout.activity_otp_password);

		mSharedPreferences = getSharedPreferences(Utils.PREF_NAME, MODE_PRIVATE);
		initView();

		SmsReceiver.bindListener(new SmsListener() {
			@Override
			public void messageReceived(String messageText) {
				if (mProgressDialog != null && mProgressDialog.isShowing()) {
					mProgressDialog.dismiss();
				}
				if (otpDialog != null && otpDialog.isShowing()) {
					otpDialog.dismiss();
				}
				mHandler.removeCallbacks(mRunnable);
				Logger.d("Text", messageText);
				String text = messageText.replaceAll("[^0-9]", "");
				number = text.substring(0, 4);
				openOTPDialog(number);
			}
		});

	}

	private void initView() {

		mProgressDialog = new ProgressDialog(this);
		mProgressDialog.setMessage(getResources().getString(R.string.otp_msg));
		mProgressDialog.setCanceledOnTouchOutside(false);
		mProgressDialog.setCancelable(false);
		if (mProgressDialog != null && !mProgressDialog.isShowing()) {
			mProgressDialog.show();
		}

		Bundle bundle = getIntent().getExtras();
		From = bundle.getString("FROM");
		if(!bundle.getString(Utils.MOBILE_NO).equalsIgnoreCase(null))
			mobile_no = bundle.getString(Utils.MOBILE_NO);
//		if(!bundle.getString(Utils.OTP_CODE).equalsIgnoreCase(null))
//			otp_code = bundle.getString(Utils.OTP_CODE);
		
		
//		Utils.showMessageDialog(this, getResources().getString(R.string.alert), "Your OTP Code is " + mSharedPreferences.getString(Utils.OTP_CODE, ""));
		
		((ImageView) findViewById(R.id.backBtn)).setVisibility(View.VISIBLE);
		((ImageView) findViewById(R.id.backBtn)).setOnClickListener(this);
		findViewById(R.id.title_back).setVisibility(View.VISIBLE);
		((TextView) findViewById(R.id.title_back)).setText(getString(R.string.otp));
		findViewById(R.id.txtResendOtp).setOnClickListener(this);


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

		doTheAutoRefresh();
		
	}

	private void openOTPDialog(String otp) {
		otpDialog = new Dialog(this);
		otpDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		otpDialog.setContentView(R.layout.otp_approve_dialog);

		((TextView) otpDialog.findViewById(R.id.txtOtp)).setText(otp);
		otpDialog.findViewById(R.id.txtApprove).setOnClickListener(this);

		otpDialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
		otpDialog.getWindow().setGravity(Gravity.BOTTOM);
		otpDialog.show();
	}

	private void doTheAutoRefresh() {
		mRunnable = new Runnable() {

			@Override
			public void run() {
				if (mProgressDialog != null && mProgressDialog.isShowing()) {
					mProgressDialog.dismiss();
				}
			}
		};
		mHandler.postDelayed(mRunnable, 15000);

	}

	@Override
	public void onClick(View v) {
		
		switch (v.getId()) {
		case R.id.backBtn:
			finish();
			break;
			
		case R.id.otpSubmitButton:
			if(fieldValidation()){

				if(From.equalsIgnoreCase("REGISTER")){
//					onOTPPasswordRegister(mobile_no, mOTPNo.getText().toString());
				}  else if(From.equalsIgnoreCase("FORGOT")) {
					onOTPPassword(mSharedPreferences.getString(Utils.USER_ID, ""), mobile_no, mOTPNo.getText().toString());
				}

				
			}
			break;

			case R.id.txtResendOtp:
				if (getCurrentFocus() != null) {
					InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
					inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
				}

				if (From.equalsIgnoreCase("REGISTER")) {
//					onOTPResendRegister(mSharedPreferences.getString(Utils.USER_ID, ""), mobile_no);
				} else if (From.equalsIgnoreCase("FORGOT")) {
					onOTPResendForgot(mSharedPreferences.getString(Utils.USER_ID, ""), mobile_no);
				}
				break;

			case R.id.txtApprove:

				mOTPNo.setText("" + number);
				mOTPNo.setSelection(mOTPNo.getText().length());
				otpDialog.dismiss();
//				findViewById(R.id.otpSubmitButton).performClick();

				break;
		}

	}
	
	private void onOTPPassword(String userId, String mobile, String otp) {
		
        if (Utils.checkInternetConnection(this)) {
            OTPRequestTask otpRequestTask = new OTPRequestTask(this);
            otpRequestTask.setAsyncCallListener(new AsyncCallListener() {

                @Override
                public void onResponseReceived(Object response) {
                    RegisterModel registerModel = (RegisterModel) response;
                    if(registerModel.success.equalsIgnoreCase("1")) {
//                            Toast.makeText(OtpPasswordActivity.this, registerModel.message , Toast.LENGTH_SHORT).show();
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
            otpRequestTask.execute(userId, mobile,otp);
        } else {
            Toast.makeText(OtpPasswordActivity.this, getString(R.string.connection), Toast.LENGTH_SHORT).show();
        }
    }

	private void onOTPResendForgot(String id, String mobile) {

		if (Utils.checkInternetConnection(this)) {
			OTPForgotResendRequestTask otpForgotResendRequestTask = new OTPForgotResendRequestTask(this);
			otpForgotResendRequestTask.setAsyncCallListener(new AsyncCallListener() {

				@Override
				public void onResponseReceived(Object response) {
					RegisterModel registerModel = (RegisterModel) response;
					if (registerModel.success.equalsIgnoreCase("1")) {
						if (mProgressDialog != null && !mProgressDialog.isShowing()) {
							mProgressDialog.show();
						}

						doTheAutoRefresh();

					} else if (registerModel.success.equalsIgnoreCase("0")) {
						Utils.showMessageDialog(OtpPasswordActivity.this, getResources().getString(R.string.alert), registerModel.message);

					}
				}

				@Override
				public void onErrorReceived(String error) {
					Toast.makeText(OtpPasswordActivity.this, getString(R.string.try_again), Toast.LENGTH_SHORT).show();
				}
			});
			otpForgotResendRequestTask.execute(id, mobile);
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
