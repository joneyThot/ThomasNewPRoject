package com.android.handyman.service;

import org.json.JSONObject;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;

import com.android.handyman.R;
import com.android.handyman.logger.Logger;
import com.android.handyman.model.MyHiringsModel;
import com.google.gson.Gson;

public class GetHandymanChangeStatusRequestTask extends AsyncTask<String, Integer, Object> {

	public static final String TAG = "HandymanChangeStatusRequestTask";
	public Context mContext;
	private ProgressDialog mProgressDialog;
	private AsyncCallListener mAsyncCallListener;
	private String mErrorMessage;
	private SharedPreferences mSharedPreferences;
	private boolean mIsError = false;

	public GetHandymanChangeStatusRequestTask(Context mContext) {
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
		return getChangeStatus(params[0],params[1],params[2]);
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

	private Object getChangeStatus(String id, String hire_status, String service_updated_by) {

		MyHiringsModel myHiringsModels = new MyHiringsModel();
		String result = "";
		
		JSONObject jsonMain = new JSONObject();
		JSONObject jsonData = new JSONObject();
		JSONObject jsonUser = new JSONObject();
		String getServerPath = Utils.URL_SERVER_ADDRESS + Utils.GET_HANDYMAN_CHANGE_STATUS;
		try {

			jsonUser.accumulate("id", id);
			jsonUser.accumulate("hire_status", hire_status);
			jsonUser.accumulate("service_updated_by", service_updated_by);
			jsonData.accumulate("hire", jsonUser);
			jsonMain.accumulate("data", jsonData);

			result = Utils.POST(getServerPath, jsonMain);
			JSONObject jObj1 = new JSONObject(result.toString());
			
			Gson gson1 = new Gson();
			myHiringsModels = gson1.fromJson(jObj1.toString(), MyHiringsModel.class);
			
			

		} catch (Exception e) {
			e.printStackTrace();
		}
		return myHiringsModels;
	}
}
