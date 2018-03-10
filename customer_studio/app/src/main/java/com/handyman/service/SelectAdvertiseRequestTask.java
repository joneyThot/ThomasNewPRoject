package com.handyman.service;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.handyman.R;
import com.handyman.logger.Logger;
import com.handyman.model.AdvertiseListModel;
import com.handyman.model.DataModel;

import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class SelectAdvertiseRequestTask extends AsyncTask<String, Integer, Object> {

	public Context mContext;
//	private ProgressDialog mProgressDialog;
	private AsyncCallListener mAsyncCallListener;
	private String mErrorMessage;
	private boolean mIsError = false;

	public SelectAdvertiseRequestTask(Context mContext) {
		this.mContext = mContext;
//		mProgressDialog = new ProgressDialog(mContext);
//		mProgressDialog.setMessage(mContext.getResources().getString(
//				R.string.loading));
//		mProgressDialog.setCanceledOnTouchOutside(false);
//		mProgressDialog.setCancelable(false);
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
		return selectAdvertise(params[0],params[1],params[2]);
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

	private Object selectAdvertise(String users_id,String banner_id, String is_read) {

		AdvertiseListModel advertiseModel = new AdvertiseListModel();

		String result = "";

		JSONObject jsonMain = new JSONObject();
		JSONObject jsonData = new JSONObject();
		JSONObject jsonUser = new JSONObject();
		String getServerPath = Utils.URL_SERVER_ADDRESS + Utils.SELECTED_BANNER;
		try {

//			jsonUser.accumulate("id", id);
			jsonUser.accumulate("users_id", users_id);
			jsonUser.accumulate("banner_id", banner_id);
			jsonUser.accumulate("is_read", is_read);
			jsonData.accumulate("banner", jsonUser);
			jsonMain.accumulate("data", jsonData);

			result = Utils.POST(getServerPath, jsonMain);
			JSONObject jObj = new JSONObject(result.toString());

			advertiseModel.setSuccess(jObj.getString("success"));
			advertiseModel.setMessage(jObj.getString("message"));

		} catch (Exception e) {
			e.printStackTrace();
		}
		return advertiseModel;
	}
}
