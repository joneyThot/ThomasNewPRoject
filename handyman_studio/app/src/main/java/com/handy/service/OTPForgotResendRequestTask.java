package com.handy.service;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;

import com.google.gson.Gson;
import com.handy.logger.Logger;
import com.handy.model.RegisterModel;

import org.json.JSONObject;

public class OTPForgotResendRequestTask extends AsyncTask<String, Integer, Object> {

	public static final String TAG = "OTPForgotResendRequestTask";
	public Context mContext;
//	private ProgressDialog mProgressDialog;
	private AsyncCallListener mAsyncCallListener;
	private String mErrorMessage;
	private SharedPreferences mSharedPreferences;
	private boolean mIsError = false;

	public OTPForgotResendRequestTask(Context mContext) {
		this.mContext = mContext;
//		mProgressDialog = new ProgressDialog(mContext);
//		mProgressDialog.setMessage(mContext.getResources().getString(
//				R.string.loading));
//		mProgressDialog.setCanceledOnTouchOutside(false);
//		mProgressDialog.setCancelable(false);
		mSharedPreferences = mContext.getSharedPreferences(Utils.PREF_NAME, mContext.MODE_PRIVATE);
	}

	@Override
	protected void onPreExecute() {
		super.onPreExecute();
//		if (mProgressDialog != null && !mProgressDialog.isShowing()) {
//			mProgressDialog.show();
//		}
	}

	@Override
	protected Object doInBackground(String... params) {
		return otpCode(params[0],params[1]);
	}

	@Override
	protected void onPostExecute(Object result) {
		super.onPostExecute(result);
//		if (mProgressDialog != null && mProgressDialog.isShowing()) {
//			mProgressDialog.dismiss();
//		}
		if (mAsyncCallListener != null) {
			if (mIsError) {
				Logger.e("", "In Asnyc call->errorMessage:" + mErrorMessage);
				mAsyncCallListener.onErrorReceived(mErrorMessage);
			} else {
				mAsyncCallListener.onResponseReceived(result);
			}
		}
	}

	public void setAsyncCallListener(AsyncCallListener listener) {
		this.mAsyncCallListener = listener;
	}

	private Object otpCode(String id, String mobile) {

		RegisterModel registerModel = new RegisterModel();
		String result = "";
		
		JSONObject jsonMain = new JSONObject();
		JSONObject jsonData = new JSONObject();
		JSONObject jsonUser = new JSONObject();
		String getServerPath = Utils.URL_SERVER_ADDRESS + Utils.OTP_RESEND_FORGOT;
		try {

			jsonUser.accumulate("id", id);
			jsonUser.accumulate("mobile", mobile);
			jsonUser.accumulate("user_type", "handyman");
			jsonData.accumulate("users", jsonUser);
			jsonMain.accumulate("data", jsonData);

			result = Utils.POST(getServerPath, jsonMain);
//			result = jsonMain.toString();
			JSONObject jObj = new JSONObject(result.toString());
			Gson gson = new Gson();
			registerModel = gson.fromJson(jObj.toString(), RegisterModel.class);
//			Utils.storeString(mSharedPreferences, Utils.OTP_CODE, registerModel.user.otp);
			

		} catch (Exception e) {
			e.printStackTrace();
		}
		return registerModel;
	}
}