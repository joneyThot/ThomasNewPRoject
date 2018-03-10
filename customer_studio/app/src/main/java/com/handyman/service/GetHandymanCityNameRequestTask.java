package com.handyman.service;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;

import com.handyman.R;
import com.handyman.logger.Logger;
import com.handyman.model.TermsAndConditionModel;

import org.json.JSONObject;

public class GetHandymanCityNameRequestTask extends AsyncTask<String, Integer, Object> {

	public static final String TAG = "GetHandymanCityNameRequestTask";
	public Context mContext;
	private ProgressDialog mProgressDialog;
	private AsyncCallListener mAsyncCallListener;
	private String mErrorMessage;
//	private SharedPreferences mSharedPreferences;
	private boolean mIsError = false;

	public GetHandymanCityNameRequestTask(Context mContext) {
		this.mContext = mContext;
		mProgressDialog = new ProgressDialog(mContext);
		mProgressDialog.setMessage(mContext.getResources().getString(R.string.loading));
		mProgressDialog.setCanceledOnTouchOutside(false);
		mProgressDialog.setCancelable(false);
//		mSharedPreferences = mContext.getSharedPreferences(Utils.PREF_NAME,	mContext.MODE_PRIVATE);
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
		return getHandymanCity(params[0]);
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

	private Object getHandymanCity(String handyman_id) {
		TermsAndConditionModel termsAndConditionModel = new TermsAndConditionModel();
		String result = "";

		JSONObject jsonMain = new JSONObject();
		JSONObject jsonData = new JSONObject();
		JSONObject jsonUser = new JSONObject();
		String getServerPath = Utils.URL_SERVER_ADDRESS + Utils.GET_CITY_OF_HANDYMAN;
		try {

			jsonUser.accumulate("handyman_id", handyman_id);
			jsonData.accumulate("users", jsonUser);
			jsonMain.accumulate("data", jsonData);

			result = Utils.POST(getServerPath, jsonMain);

			JSONObject jObj = new JSONObject(result.toString());
			termsAndConditionModel.setSuccess(jObj.getString("success"));
			termsAndConditionModel.setMessage(jObj.getString("message"));

			if(!jObj.isNull("data")){
				JSONObject dataObj = jObj.getJSONObject("data");
				termsAndConditionModel.setCityname(dataObj.getString("cityname"));
			}


		} catch (Exception e) {
			e.printStackTrace();
		}
		return termsAndConditionModel;
	}
}
