package com.handyman.service;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;

import com.google.gson.Gson;
import com.handyman.R;
import com.handyman.logger.Logger;
import com.handyman.model.HireHandymanModel;

import org.json.JSONObject;

public class AddCoinsForPaymentRequestTask extends AsyncTask<String, Integer, Object> {

	public static final String TAG = "AddCoinsForPaymentRequestTask";
	public Context mContext;
	private ProgressDialog mProgressDialog;
	private AsyncCallListener mAsyncCallListener;
	private String mErrorMessage;
	private SharedPreferences mSharedPreferences;
	private boolean mIsError = false;

	public AddCoinsForPaymentRequestTask(Context mContext) {
		this.mContext = mContext;
		mProgressDialog = new ProgressDialog(mContext);
		mProgressDialog.setMessage(mContext.getResources().getString(R.string.loading));
		mProgressDialog.setCanceledOnTouchOutside(false);
		mProgressDialog.setCancelable(false);
//		mSharedPreferences = mContext.getSharedPreferences(Utils.PREF_NAME, mContext.MODE_PRIVATE);
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
		return customer_add_coins(params[0], params[1], params[2]);
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

	private Object customer_add_coins(String id, String client_id, String coins) {

		HireHandymanModel hireHandymanModel = new HireHandymanModel();
		String result = "";
		
		JSONObject jsonMain = new JSONObject();
		JSONObject jsonData = new JSONObject();
		JSONObject jsonUser = new JSONObject();
		String getServerPath = Utils.URL_SERVER_ADDRESS + Utils.CUSTOMER_EDIT_COINS;
		try {

			jsonUser.accumulate("id", id);
			jsonUser.accumulate("client_id", client_id);
			jsonUser.accumulate("coins", coins);
			jsonUser.accumulate("is_coin_note_sent", "1");
			jsonData.accumulate("hire", jsonUser);
			jsonMain.accumulate("data", jsonData);

			result = Utils.POST(getServerPath, jsonMain);
			JSONObject jObj = new JSONObject(result);
			Gson gson = new Gson();
			hireHandymanModel = gson.fromJson(jObj.toString(), HireHandymanModel.class);
			

		} catch (Exception e) {
			e.printStackTrace();
		}
		return hireHandymanModel;
	}
}
