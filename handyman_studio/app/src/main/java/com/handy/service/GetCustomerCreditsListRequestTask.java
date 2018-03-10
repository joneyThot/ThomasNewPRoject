package com.handy.service;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONObject;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;

import com.handy.R;
import com.handy.logger.Logger;
import com.handy.model.CreditsModel;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

public class GetCustomerCreditsListRequestTask extends AsyncTask<String, Integer, Object> {

	public static final String TAG = "GetProfileHandymanListRequestTask";
	public Context mContext;
	private ProgressDialog mProgressDialog;
	private AsyncCallListener mAsyncCallListener;
	private String mErrorMessage;
	private SharedPreferences mSharedPreferences;
	private boolean mIsError = false;

	public GetCustomerCreditsListRequestTask(Context mContext) {
		this.mContext = mContext;
		mProgressDialog = new ProgressDialog(mContext);
		mProgressDialog.setMessage(mContext.getResources().getString(R.string.loading));
		mProgressDialog.setCanceledOnTouchOutside(false);
		mProgressDialog.setCancelable(false);
		mSharedPreferences = mContext.getSharedPreferences(Utils.PREF_NAME,	mContext.MODE_PRIVATE);
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
		return getCustomerCredits(params[0]);
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

	private Object getCustomerCredits(String id) {

		ArrayList<CreditsModel> creditsModelList = new ArrayList<CreditsModel>();
		String result = "";
		
		JSONObject jsonMain = new JSONObject();
		JSONObject jsonData = new JSONObject();
		JSONObject jsonUser = new JSONObject();
		String getServerPath = Utils.URL_SERVER_ADDRESS + Utils.GET_CUSTOMER_CREDIT;
		try {

			jsonUser.accumulate("id", id);
			jsonData.accumulate("user", jsonUser);
			jsonMain.accumulate("data", jsonData);

			result = Utils.POST(getServerPath, jsonMain);
			JSONObject jObj = new JSONObject(result.toString());
			Gson gson = new Gson();
			Type listType = new TypeToken<List<CreditsModel>>() {}.getType();
			creditsModelList = gson.fromJson(jObj.get("data").toString(), listType);
			

		} catch (Exception e) {
			e.printStackTrace();
		}
		return creditsModelList;
	}
}
