package com.handy.service;

import org.json.JSONObject;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;

import com.handy.R;
import com.handy.logger.Logger;
import com.handy.model.RegisterModel;
import com.google.gson.Gson;

public class RealTimeLocationChangeRequestTask extends AsyncTask<String, Integer, Object> {

	public static final String TAG = "RealTimeLocationChangeRequestTask";
	public Context mContext;
//	private ProgressDialog mProgressDialog;
	private AsyncCallListener mAsyncCallListener;
	private String mErrorMessage;
	private SharedPreferences mSharedPreferences;
	private boolean mIsError = false;

	public RealTimeLocationChangeRequestTask(Context mContext) {
		this.mContext = mContext;
		/*mProgressDialog = new ProgressDialog(mContext);
		mProgressDialog.setMessage(mContext.getResources().getString(
				R.string.loading));
		mProgressDialog.setCanceledOnTouchOutside(false);
		mProgressDialog.setCancelable(false);*/
		mSharedPreferences = mContext.getSharedPreferences(Utils.PREF_NAME,
				mContext.MODE_PRIVATE);
	}

	@Override
	protected void onPreExecute() {
		super.onPreExecute();
		/*if (mProgressDialog != null && !mProgressDialog.isShowing()) {
			mProgressDialog.show();
		}*/
	}

	@Override
	protected Object doInBackground(String... params) {
		return realtime_location(params[0], params[1],params[2]);
	}

	@Override
	protected void onPostExecute(Object result) {
		super.onPostExecute(result);
		/*if (mProgressDialog != null && mProgressDialog.isShowing()) {
			mProgressDialog.dismiss();
		}*/
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

	private Object realtime_location(String id, String lat, String lng) {

		if(lat.equalsIgnoreCase("")){
			lat = "0.0";
		}
		
		if(lng.equalsIgnoreCase("")){
			lng = "0.0";
		}
		
		RegisterModel registerModel = new RegisterModel();
		String result = "";
		
		JSONObject jsonMain = new JSONObject();
		JSONObject jsonData = new JSONObject();
		JSONObject jsonUser = new JSONObject();
//		String getServerPath = /*Utils.URL_SERVER_ADDRESS*/mSharedPreferences.getString(Utils.URL_ADDRESS,"") + Utils.REALTIME_LOCATION_UPDATE;
		String getServerPath = Utils.URL_SERVER_ADDRESS+ Utils.REALTIME_LOCATION_UPDATE;
		try {

			jsonUser.accumulate("handyman_id", id);
			jsonUser.accumulate("lat", lat);
			jsonUser.accumulate("lng", lng);
			jsonData.accumulate("users", jsonUser);
			jsonMain.accumulate("data", jsonData);

			result = Utils.POST(getServerPath, jsonMain);
			JSONObject jObj = new JSONObject(result.toString());
			Gson gson = new Gson();
			registerModel = gson.fromJson(jObj.toString(), RegisterModel.class);
			

		} catch (Exception e) {
			e.printStackTrace();
		}
		return registerModel;
	}
}
