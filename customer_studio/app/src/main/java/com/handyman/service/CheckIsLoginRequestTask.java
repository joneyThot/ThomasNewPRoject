package com.handyman.service;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;

import com.google.gson.Gson;
import com.handyman.logger.Logger;
import com.handyman.model.RegisterModel;

import org.json.JSONObject;

public class CheckIsLoginRequestTask extends AsyncTask<String, Integer, Object> {

	public static final String TAG = "CheckIsLoginRequestTask";
	public Context mContext;
//	private ProgressDialog mProgressDialog;
	private AsyncCallListener mAsyncCallListener;
	private String mErrorMessage;
	private SharedPreferences mSharedPreferences;
	private boolean mIsError = false;

	public CheckIsLoginRequestTask(Context mContext) {
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
		return logout(params[0], params[1]);
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

	private Object logout(String id, String user_type) {

		RegisterModel registerModel = new RegisterModel();
		String result = "";
		
		JSONObject jsonMain = new JSONObject();
		JSONObject jsonData = new JSONObject();
		JSONObject jsonUser = new JSONObject();
//		String getServerPath = /*Utils.URL_SERVER_ADDRESS*/mSharedPreferences.getString(Utils.URL_ADDRESS,"") + Utils.ISLOGIN;
		String getServerPath = Utils.URL_SERVER_ADDRESS + Utils.ISLOGIN;
		try {

			jsonUser.accumulate("id", id);
			jsonUser.accumulate("user_type", user_type);
			jsonData.accumulate("users", jsonUser);
			jsonMain.accumulate("data", jsonData);

			result = Utils.POST(getServerPath, jsonMain);
			JSONObject jObj1 = new JSONObject(result.toString());
			Gson gson1 = new Gson();
			registerModel = gson1.fromJson(jObj1.toString(), RegisterModel.class);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return registerModel;
	}
}
