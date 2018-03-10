package com.android.handyman.service;

import org.json.JSONObject;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;

import com.android.handyman.R;
import com.android.handyman.logger.Logger;
import com.android.handyman.model.HireHandymanModel;
import com.android.handyman.model.RegisterModel;
import com.google.gson.Gson;

public class RateHandymanRequestTask extends AsyncTask<String, Integer, Object> {

	public static final String TAG = "RateHandymanRequestTask";
	public Context mContext;
	private ProgressDialog mProgressDialog;
	private AsyncCallListener mAsyncCallListener;
	private String mErrorMessage;
	private SharedPreferences mSharedPreferences;
	private boolean mIsError = false;

	public RateHandymanRequestTask(Context mContext) {
		this.mContext = mContext;
		mProgressDialog = new ProgressDialog(mContext);
		mProgressDialog.setMessage(mContext.getResources().getString(R.string.loading));
		mProgressDialog.setCanceledOnTouchOutside(false);
		mProgressDialog.setCancelable(false);
		mSharedPreferences = mContext.getSharedPreferences(Utils.PREF_NAME, mContext.MODE_PRIVATE);
	}

	@Override
	protected void onPreExecute() {
		super.onPreExecute();
		if (mProgressDialog != null && !mProgressDialog.isShowing()) {
			mProgressDialog.show();
		}
	}

	@Override
	protected Object doInBackground(String... params) {
		return rate_handyman(params[0], params[1], params[2],params[3],params[4]);
	}

	@Override
	protected void onPostExecute(Object result) {
		super.onPostExecute(result);
		if (mProgressDialog != null && mProgressDialog.isShowing()) {
			mProgressDialog.dismiss();
		}
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

	private Object rate_handyman(String handyman_id, String client_id, String hire_id, String rate, String description) {

		HireHandymanModel hireHandymanModel = new HireHandymanModel();
		String result = "";
		
		JSONObject jsonMain = new JSONObject();
		JSONObject jsonData = new JSONObject();
		JSONObject jsonUser = new JSONObject();
		String getServerPath = Utils.URL_SERVER_ADDRESS + Utils.RATE_HANDYMAN_ORDER_WISE;
		try {

			jsonUser.accumulate("handyman_id", handyman_id);
			jsonUser.accumulate("client_id", client_id);
			jsonUser.accumulate("hire_id", hire_id);
			jsonUser.accumulate("rate", rate);
			jsonUser.accumulate("description", description);
			jsonData.accumulate("rating", jsonUser);
			jsonMain.accumulate("data", jsonData);

			result = Utils.POST(getServerPath, jsonMain);
//			result = jsonMain.toString();
			JSONObject jObj = new JSONObject(result);
			Gson gson = new Gson();
			hireHandymanModel = gson.fromJson(jObj.toString(), HireHandymanModel.class);
			

		} catch (Exception e) {
			e.printStackTrace();
		}
		return hireHandymanModel;
	}
}
