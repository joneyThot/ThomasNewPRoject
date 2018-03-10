package com.handy.service;

import java.util.ArrayList;

import org.json.JSONObject;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;

import com.handy.R;
import com.handy.logger.Logger;
import com.handy.model.CheckHandymanRatingInnerModel;
import com.handy.model.CheckHandymanRatingModel;

public class CheckRateHandymanRequestTask extends
		AsyncTask<String, Integer, Object> {

	public static final String TAG = "CheckRateHandymanRequestTask";
	public Context mContext;
	private ProgressDialog mProgressDialog;
	private AsyncCallListener mAsyncCallListener;
	private String mErrorMessage;
	private SharedPreferences mSharedPreferences;
	private boolean mIsError = false;

	public CheckRateHandymanRequestTask(Context mContext) {
		this.mContext = mContext;
		mProgressDialog = new ProgressDialog(mContext);
		mProgressDialog.setMessage(mContext.getResources().getString(
				R.string.loading));
		mProgressDialog.setCanceledOnTouchOutside(false);
		mProgressDialog.setCancelable(false);
		mSharedPreferences = mContext.getSharedPreferences(Utils.PREF_NAME,
				mContext.MODE_PRIVATE);
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
		return rate_check_handyman(params[0], params[1]);
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

	private Object rate_check_handyman(String handyman_id, String client_id) {

		ArrayList<CheckHandymanRatingModel> checkHandymanRatingModel = new ArrayList<CheckHandymanRatingModel>();
		CheckHandymanRatingModel handymanRatingModel = new CheckHandymanRatingModel();
		String result = "";

		JSONObject jsonMain = new JSONObject();
		JSONObject jsonData = new JSONObject();
		JSONObject jsonUser = new JSONObject();
		String getServerPath = Utils.URL_SERVER_ADDRESS
				+ Utils.RATE_CHECK_HANDYMAN;
		try {

			jsonUser.accumulate("handyman_id", handyman_id);
			jsonUser.accumulate("client_id", client_id);
			jsonData.accumulate("rating", jsonUser);
			jsonMain.accumulate("data", jsonData);

			result = Utils.POST(getServerPath, jsonMain);
			JSONObject jObj = new JSONObject(result.toString());
			if (!jObj.isNull("success"))
				handymanRatingModel.setSuccess(jObj.getString("success"));

			if (!jObj.isNull("message"))
				handymanRatingModel.setMessage(jObj.getString("message"));

			if (!jObj.isNull("data")) {
				JSONObject login_jobj = jObj.getJSONObject("data");
				CheckHandymanRatingInnerModel user = new CheckHandymanRatingInnerModel();
				if (!login_jobj.isNull("id"))
					user.setId(login_jobj.getString("id"));

				if (!login_jobj.isNull("handyman_id"))
					user.setHandyman_id(login_jobj.getString("handyman_id"));

				if (!login_jobj.isNull("client_id"))
					user.setClient_id(login_jobj.getString("client_id"));

				if (!login_jobj.isNull("rate"))
					user.setRate(login_jobj.getString("rate"));

				if (!login_jobj.isNull("description"))
					user.setDescription(login_jobj.getString("description"));

				if (!login_jobj.isNull("is_deleted"))
					user.setIs_deleted(login_jobj.getString("is_deleted"));

				if (!login_jobj.isNull("status"))
					user.setStatus(login_jobj.getString("status"));

				if (!login_jobj.isNull("created_date"))
					user.setCreated_date(login_jobj.getString("created_date"));

				if (!login_jobj.isNull("modified_date"))
					user.setModified_date(login_jobj.getString("modified_date"));
				
				Utils.storeString(mSharedPreferences, "rate", user.getRate());
				Utils.storeString(mSharedPreferences, "des", user.getDescription());

			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return handymanRatingModel;
	}
}
