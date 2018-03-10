package com.handyman.service;

import android.content.Context;
import android.os.AsyncTask;

import com.handyman.logger.Logger;
import com.handyman.model.DataModel;
import com.handyman.model.ProfileHandymanModel;
import com.handyman.model.TermsAndConditionModel;
import com.handyman.model.TimeIntervalModel;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class GetHandymanLatLngRequestTask extends AsyncTask<String, Integer, Object> {

	public static final String TAG = "GetHandymanLatLngRequestTask";
	public Context mContext;
//	private ProgressDialog mProgressDialog;
	private AsyncCallListener mAsyncCallListener;
	private String mErrorMessage;
//	private SharedPreferences mSharedPreferences;
	private boolean mIsError = false;

	public GetHandymanLatLngRequestTask(Context mContext) {
		this.mContext = mContext;
//		mProgressDialog = new ProgressDialog(mContext);
//		mProgressDialog.setMessage(mContext.getResources().getString(R.string.loading));
//		mProgressDialog.setCanceledOnTouchOutside(false);
//		mProgressDialog.setCancelable(false);
//		mSharedPreferences = mContext.getSharedPreferences(Utils.PREF_NAME,	mContext.MODE_PRIVATE);
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
		return getHandymanLatLng(params[0]);
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

	private Object getHandymanLatLng(String id) {
		TermsAndConditionModel termsAndConditionModel = new TermsAndConditionModel();
		String result = "";

		JSONObject jsonMain = new JSONObject();
		JSONObject jsonData = new JSONObject();
		JSONObject jsonUser = new JSONObject();
		String getServerPath = Utils.URL_SERVER_ADDRESS + Utils.GET_HANDYMAN_LAT_LNG;
		try {

			jsonUser.accumulate("id", id);
			jsonData.accumulate("users", jsonUser);
			jsonMain.accumulate("data", jsonData);

			result = Utils.POST(getServerPath, jsonMain);

			JSONObject jObj = new JSONObject(result.toString());
			termsAndConditionModel.setSuccess(jObj.getString("success"));
			termsAndConditionModel.setMessage(jObj.getString("message"));

			if(!jObj.isNull("data")){
				JSONObject dataObj = jObj.getJSONObject("data");
				termsAndConditionModel.setLat(dataObj.getString("lat"));
				termsAndConditionModel.setLng(dataObj.getString("lng"));
				termsAndConditionModel.setCategory(dataObj.getString("category"));
				termsAndConditionModel.setSub_category(dataObj.getString("sub_category"));
			}


		} catch (Exception e) {
			e.printStackTrace();
		}
		return termsAndConditionModel;
	}
}
