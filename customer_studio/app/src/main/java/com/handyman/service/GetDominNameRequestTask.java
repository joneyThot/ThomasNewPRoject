package com.handyman.service;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;

import com.google.gson.Gson;
import com.handyman.crop.Util;
import com.handyman.logger.Logger;
import com.handyman.model.AdvertiseListModel;
import com.handyman.model.RegisterModel;

import org.json.JSONObject;

public class GetDominNameRequestTask extends AsyncTask<String, Integer, Object> {

	public static final String TAG = "GetDominNameRequestTask";
	public Context mContext;
//	private ProgressDialog mProgressDialog;
	private AsyncCallListener mAsyncCallListener;
	private String mErrorMessage;
	private SharedPreferences mSharedPreferences;
	private boolean mIsError = false;

	public GetDominNameRequestTask(Context mContext) {
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
		return getDomin();
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

	private Object getDomin() {

		AdvertiseListModel advertiseModel = new AdvertiseListModel();
		String result = "";
		
		String getServerPath = Utils.DOMIN_URL;
		try {

			result = Utils.postRequest(getServerPath);
			JSONObject jObj = new JSONObject(result.toString());

			advertiseModel.setSuccess(jObj.getString("success"));
			advertiseModel.setMessage(jObj.getString("message"));
			if(!jObj.isNull("data")) {
				JSONObject dataObj = jObj.getJSONObject("data");
				advertiseModel.setDomain_name(dataObj.getString("domain_name"));
				Utils.storeString(mSharedPreferences,Utils.URL_ADDRESS,dataObj.getString("domain_name"));
				Utils.URL_SERVER_ADDRESS = mSharedPreferences.getString(Utils.URL_ADDRESS,"");

				advertiseModel.setImage_url(dataObj.getString("image_url"));
				Utils.storeString(mSharedPreferences,Utils.IMAGE_URL_ADDRESS,dataObj.getString("image_url"));
				Utils.IMAGE_URL = mSharedPreferences.getString(Utils.IMAGE_URL_ADDRESS,"");
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return advertiseModel;
	}
}
