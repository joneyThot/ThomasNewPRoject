package com.handy.service;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;

import com.handy.R;
import com.handy.logger.Logger;
import com.handy.model.JobCompeletedModel;
import com.google.gson.Gson;

public class JobCompletedRequestTask extends AsyncTask<String, Integer, Object> {

	public static final String TAG = "JobCompletedRequestTask";
	public Context mContext;
	private ProgressDialog mProgressDialog;
	private AsyncCallListener mAsyncCallListener;
	private String mErrorMessage;
	private SharedPreferences mSharedPreferences;
	private boolean mIsError = false;

	public JobCompletedRequestTask(Context mContext) {
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
		return job_compelete(params[0], params[1], params[2], params[3], params[4],params[5], params[6], params[7]);
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

	private Object job_compelete(String job_description, String receiver_name, String amount,
			String id, String img, String TOTAL, String DISCOUNT, String CREDITS) {
		
		JobCompeletedModel jobCompeletedModel = new JobCompeletedModel();
		String result = "";

		String getServerPath = Utils.URL_SERVER_ADDRESS + Utils.JOB_COMPELETED;
		try {

			List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(5);
			nameValuePairs.add(new BasicNameValuePair("job_description", job_description));
			nameValuePairs.add(new BasicNameValuePair("receiver_name", receiver_name));
			nameValuePairs.add(new BasicNameValuePair("amount", amount));
			nameValuePairs.add(new BasicNameValuePair("id", id));
			nameValuePairs.add(new BasicNameValuePair("img", img));
			nameValuePairs.add(new BasicNameValuePair("total", TOTAL));
			nameValuePairs.add(new BasicNameValuePair("discount", DISCOUNT));
			nameValuePairs.add(new BasicNameValuePair("credit", CREDITS));
			
			result = Utils.postMultipartPNG(getServerPath, nameValuePairs);
			
			JSONObject jObj1 = new JSONObject(result.toString());
			Gson gson1 = new Gson();
			jobCompeletedModel = gson1.fromJson(jObj1.toString(), JobCompeletedModel.class);

		} catch (Exception e) {
			e.printStackTrace();
		}
		return jobCompeletedModel;
	}
}
