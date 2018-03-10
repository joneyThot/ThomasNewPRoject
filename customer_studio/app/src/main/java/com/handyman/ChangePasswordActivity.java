package com.handyman;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Build;
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

import com.handyman.logger.Logger;
import com.handyman.model.RegisterModel;
import com.handyman.service.AsyncCallListener;
import com.handyman.service.ChangePasswordRequestTask;
import com.handyman.service.Utils;

public class ChangePasswordActivity extends Activity implements OnClickListener {

	private static String TAG = "ChangePasswordActivity";
	private SharedPreferences mSharedPreferences;
	EditText mNewPass,mConfirmPass;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		int currentapiVersion = Build.VERSION.SDK_INT;
		if(currentapiVersion < Build.VERSION_CODES.LOLLIPOP) {
			getActionBar().hide();
		}
		setContentView(R.layout.activity_change_password);

		mSharedPreferences = getSharedPreferences(Utils.PREF_NAME, MODE_PRIVATE);
		initView();

	}

	private void initView() {

		((ImageView) findViewById(R.id.backBtn)).setVisibility(View.VISIBLE);
		((ImageView) findViewById(R.id.backBtn)).setOnClickListener(this);
		findViewById(R.id.title_back).setVisibility(View.VISIBLE);
		((TextView) findViewById(R.id.title_back)).setText(getString(R.string.change_password));
		
		mNewPass = (EditText) findViewById(R.id.cahnge_new_pass_edittxt);
		mConfirmPass = (EditText) findViewById(R.id.change_confirm_pass_edittxt);
		findViewById(R.id.updatePassButton).setOnClickListener(this);
		
		mConfirmPass.setOnEditorActionListener(new OnEditorActionListener(){
            @Override
            public boolean onEditorAction(TextView arg0, int arg1, KeyEvent arg2) {
                if (arg1 == EditorInfo.IME_ACTION_GO) {
                    findViewById(R.id.updatePassButton).performClick();
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
			
		case R.id.updatePassButton:
			if(fieldValidation()){
				onChangePassword(mSharedPreferences.getString(Utils.USER_ID, ""),mNewPass.getText().toString());
			}
			break;
		}

	}
	
private void onChangePassword(String id, String password) {
		
        if (Utils.checkInternetConnection(this)) {
            ChangePasswordRequestTask changePasswordRequestTask = new ChangePasswordRequestTask(this);
            changePasswordRequestTask.setAsyncCallListener(new AsyncCallListener() {

                @Override
                public void onResponseReceived(Object response) {
                    RegisterModel registerModel = (RegisterModel) response;
                    if(registerModel.success.equalsIgnoreCase("1")) {
                            Toast.makeText(ChangePasswordActivity.this, registerModel.message , Toast.LENGTH_SHORT).show();
                            finish();

                        } else if (registerModel.success.equalsIgnoreCase("0")) {
//                            Toast.makeText(ChangePasswordActivity.this, registerModel.message , Toast.LENGTH_SHORT).show();
                            Utils.showMessageDialog(ChangePasswordActivity.this, getResources().getString(R.string.alert), registerModel.message);
                        }
                    }

                @Override
                public void onErrorReceived(String error) {
                    Toast.makeText(ChangePasswordActivity.this, getString(R.string.try_again), Toast.LENGTH_SHORT).show();
                }
            });
            changePasswordRequestTask.execute(id, password);
        } else {
            Toast.makeText(ChangePasswordActivity.this, getString(R.string.connection), Toast.LENGTH_SHORT).show();
        }
    }
	
	public boolean fieldValidation() {
		boolean flag = true;
		 if (!Utils.validateString(mNewPass.getText().toString())) {
			flag = false;
			Utils.showMessageDialog(this, getResources().getString(R.string.alert), getResources().getString(R.string.enter_password));
			mNewPass.requestFocus();
		} else if (mNewPass.getText().toString().trim().length() < 8) {
			flag = false;
			Utils.showMessageDialog(this, getResources().getString(R.string.alert), getResources().getString(R.string.check_password_length));
			mNewPass.requestFocus();
		}
		else if (!mNewPass.getText().toString().equalsIgnoreCase(mConfirmPass.getText().toString())) {
			flag = false;
			Utils.showMessageDialog(this, getResources().getString(R.string.alert), getResources().getString(R.string.password_not_match));
			mConfirmPass.requestFocus();
		}
		return flag;
	}
	
}
