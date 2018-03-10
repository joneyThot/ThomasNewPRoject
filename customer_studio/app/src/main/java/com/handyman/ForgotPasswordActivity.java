package com.handyman;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import android.widget.Toast;

import com.handyman.logger.Logger;
import com.handyman.model.RegisterModel;
import com.handyman.service.AsyncCallListener;
import com.handyman.service.ForgotPasswordRequestTask;
import com.handyman.service.Utils;

public class ForgotPasswordActivity extends Activity implements OnClickListener {

    private static String TAG = "ForgotPasswordActivity";
    private SharedPreferences mSharedPreferences;
    EditText mMobileNo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        int currentapiVersion = Build.VERSION.SDK_INT;
        if (currentapiVersion < Build.VERSION_CODES.LOLLIPOP) {
            getActionBar().hide();
        }
        setContentView(R.layout.activity_forgot_password);

        mSharedPreferences = getSharedPreferences(Utils.PREF_NAME, MODE_PRIVATE);
        initView();

    }

    private void initView() {

        ((ImageView) findViewById(R.id.backBtn)).setVisibility(View.VISIBLE);
        ((ImageView) findViewById(R.id.backBtn)).setOnClickListener(this);
        findViewById(R.id.title_back).setVisibility(View.VISIBLE);
        ((TextView) findViewById(R.id.title_back)).setText(getString(R.string.forgot_pass));

        mMobileNo = (EditText) findViewById(R.id.forgot_mobile_edittxt);
        findViewById(R.id.forgotSubmitButton).setOnClickListener(this);

        mMobileNo.setOnEditorActionListener(new OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView arg0, int arg1, KeyEvent arg2) {
                if (arg1 == EditorInfo.IME_ACTION_GO) {
                    findViewById(R.id.forgotSubmitButton).performClick();
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

            case R.id.forgotSubmitButton:
                if (fieldValidation()) {
                    if(getCurrentFocus()!=null) {
                        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                        inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
                    }

                    Utils.storeString(mSharedPreferences, Utils.MOBILE_NO, mMobileNo.getText().toString());
                    onForgotPassword(mMobileNo.getText().toString());
                }
                break;
        }

    }

    private void onForgotPassword(String mobile) {

        if (Utils.checkInternetConnection(this)) {
            ForgotPasswordRequestTask forgotPasswordRequestTask = new ForgotPasswordRequestTask(this);
            forgotPasswordRequestTask.setAsyncCallListener(new AsyncCallListener() {

                @Override
                public void onResponseReceived(Object response) {
                    RegisterModel registerModel = (RegisterModel) response;
                    if (registerModel.success.equalsIgnoreCase("1")) {
//                        Toast.makeText(ForgotPasswordActivity.this, registerModel.message, Toast.LENGTH_SHORT).show();
//    						Utils.showMessageDialog(ForgotPasswordActivity.this, getResources().getString(R.string.alert), registerModel.message);
                        Logger.i(TAG, "Registered Mobile No :: " + mSharedPreferences.getString(Utils.MOBILE_NO, ""));
//                        Logger.i(TAG, "OTP CODE :: " + registerModel.user.otp);

                        Bundle bundle = new Bundle();
                        bundle.putString(Utils.MOBILE_NO, mSharedPreferences.getString(Utils.MOBILE_NO, ""));
                        bundle.putString(Utils.OTP_CODE, registerModel.user.otp);
                        bundle.putString("FROM", "FORGOT");
                        Intent i = new Intent(ForgotPasswordActivity.this, OtpPasswordActivity.class);
                        i.putExtras(bundle);
                        startActivity(i);
                        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_left);
                        finish();

                    } else if (registerModel.success.equalsIgnoreCase("0")) {
//                            Toast.makeText(ForgotPasswordActivity.this, registerModel.message , Toast.LENGTH_SHORT).show();
                        Utils.showMessageDialog(ForgotPasswordActivity.this, getResources().getString(R.string.alert), registerModel.message);
                    }
                }

                @Override
                public void onErrorReceived(String error) {
                    Toast.makeText(ForgotPasswordActivity.this, getString(R.string.try_again), Toast.LENGTH_SHORT).show();
                }
            });
            forgotPasswordRequestTask.execute(mobile);
        } else {
            Toast.makeText(ForgotPasswordActivity.this, getString(R.string.connection), Toast.LENGTH_SHORT).show();
        }
    }

    public boolean fieldValidation() {
        boolean flag = true;
        if (!Utils.validateString(mMobileNo.getText().toString())) {
            flag = false;
            Utils.showMessageDialog(this, getResources().getString(R.string.alert), getResources().getString(R.string.enter_mobile_no));
        } else if (mMobileNo.getText().toString().trim().length() < 10) {
            flag = false;
            Utils.showMessageDialog(this, getResources().getString(R.string.alert), getResources().getString(R.string.enter_mobile_no_length));
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
