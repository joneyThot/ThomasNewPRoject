package com.handy.service;

import android.content.Context;
import android.os.AsyncTask;


import com.handy.logger.Logger;
import com.handy.model.CoinAmountModel;

import org.json.JSONObject;

public class GetCoinsRequestTask extends AsyncTask<String, Integer, Object> {

	public Context mContext;
	private AsyncCallListener mAsyncCallListener;
	private String mErrorMessage;
	private boolean mIsError = false;

	public GetCoinsRequestTask(Context mContext) {
		this.mContext = mContext;
	}

	@Override
	protected void onPreExecute() {
		super.onPreExecute();
	}

	@Override
	protected Object doInBackground(String... params) {
		return getCoins();
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

	private Object getCoins() {

		CoinAmountModel advertiseModel = new CoinAmountModel();
		String result = "";
		
		String getServerPath = Utils.URL_SERVER_ADDRESS + Utils.GET_COINS;
		try {

			result = Utils.postRequest(getServerPath);
			JSONObject jObj = new JSONObject(result.toString());

			advertiseModel.setSuccess(jObj.getString("success"));
			advertiseModel.setMessage(jObj.getString("message"));
			if(!jObj.isNull("data")) {
				JSONObject jsonObject = jObj.getJSONObject("data");
				advertiseModel.setOption_value(jsonObject.getString("option_value"));
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return advertiseModel;
	}

}
