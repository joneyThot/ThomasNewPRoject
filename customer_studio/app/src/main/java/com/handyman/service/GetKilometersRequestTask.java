package com.handyman.service;

import android.content.Context;
import android.os.AsyncTask;

import com.handyman.logger.Logger;
import com.handyman.model.AdvertiseListModel;

import org.json.JSONObject;

public class GetKilometersRequestTask extends AsyncTask<String, Integer, Object> {

	public Context mContext;
	private AsyncCallListener mAsyncCallListener;
	private String mErrorMessage;
	private boolean mIsError = false;

	public GetKilometersRequestTask(Context mContext) {
		this.mContext = mContext;
	}

	@Override
	protected void onPreExecute() {
		super.onPreExecute();
	}

	@Override
	protected Object doInBackground(String... params) {
		return getKilometers();
	}

	@Override
	protected void onPostExecute(Object result) {
		super.onPostExecute(result);
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

	private Object getKilometers() {

		AdvertiseListModel advertiseModel = new AdvertiseListModel();
		String result = "";
		
		String getServerPath = Utils.URL_SERVER_ADDRESS + Utils.GET_KILO_METERS;
		try {

			result = Utils.postRequest(getServerPath);
			JSONObject jObj = new JSONObject(result.toString());

			advertiseModel.setSuccess(jObj.getString("success"));
			advertiseModel.setMessage(jObj.getString("message"));
			if(!jObj.isNull("data")) {
				JSONObject dataObj = jObj.getJSONObject("data");
				advertiseModel.setOption_value(dataObj.getString("option_value"));
			}

			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return advertiseModel;
	}

}
