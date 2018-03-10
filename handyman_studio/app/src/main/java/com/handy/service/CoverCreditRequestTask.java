package com.handy.service;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;

import com.handy.R;
import com.handy.logger.Logger;
import com.handy.model.HandymanCreditsModel;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class CoverCreditRequestTask extends AsyncTask<String, Integer, Object> {

	public static final String TAG = "CoverCreditRequestTask";
	public Context mContext;
//	private ProgressDialog mProgressDialog;
	private AsyncCallListener mAsyncCallListener;
	private String mErrorMessage;
	private boolean mIsError = false;

	public CoverCreditRequestTask(Context mContext) {
		this.mContext = mContext;
//		mProgressDialog = new ProgressDialog(mContext);
//		mProgressDialog.setMessage(mContext.getResources().getString(R.string.loading));
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
		return convert_credit(params[0], params[1]);
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

	private Object convert_credit(String id, String credit) {

		HandymanCreditsModel handymanCreditsModel = new HandymanCreditsModel();
		String result = "";

		String getServerPath = Utils.URL_SERVER_ADDRESS + Utils.CONVERT_CREDIT_CALCULATION;
		try {

			List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(5);
			nameValuePairs.add(new BasicNameValuePair("id", id));
			nameValuePairs.add(new BasicNameValuePair("credit", credit));

			result = Utils.postRequest(getServerPath, nameValuePairs);
			
			JSONObject jObj1 = new JSONObject(result.toString());
			handymanCreditsModel.setSuccess(jObj1.getString("success"));
			handymanCreditsModel.setMessage(jObj1.getString("message"));
			if(!jObj1.isNull("total")) {
				handymanCreditsModel.setTotal(jObj1.getString("total"));
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return handymanCreditsModel;
	}
}
