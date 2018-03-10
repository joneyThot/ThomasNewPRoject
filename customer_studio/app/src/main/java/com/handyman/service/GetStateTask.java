package com.handyman.service;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;

import com.handyman.R;
import com.handyman.logger.Logger;
import com.handyman.model.StateModel;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

public class GetStateTask extends AsyncTask<String, Integer, Object> {

	public Context mContext;
	private ProgressDialog mProgressDialog;
	private AsyncCallListener mAsyncCallListener;
	private String mErrorMessage;
	private boolean mIsError = false;

	public GetStateTask(Context mContext) {
		this.mContext = mContext;
		mProgressDialog = new ProgressDialog(mContext);
		mProgressDialog.setMessage(mContext.getResources().getString(
				R.string.loading));
		mProgressDialog.setCanceledOnTouchOutside(false);
		mProgressDialog.setCancelable(false);
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
		return getState();
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

	private Object getState() {
		ArrayList<StateModel> states = new ArrayList<StateModel>();
		String result = "";
		String getServerPath = Utils.URL_SERVER_ADDRESS + Utils.GET_STATE;
		try {
			List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(0);
			result = Utils.postRequest(getServerPath);

			JSONObject jObj = new JSONObject(result.toString());
			if (!jObj.isNull("data")) {
				Gson gson = new Gson();
				Type listType = new TypeToken<List<StateModel>>() {
				}.getType();
				states = gson.fromJson(jObj.get("data").toString(), listType);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return states;
	}
}
