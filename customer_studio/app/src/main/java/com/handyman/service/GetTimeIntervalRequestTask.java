package com.handyman.service;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONObject;

import com.handyman.R;
import com.handyman.logger.Logger;
import com.handyman.model.DataModel;
import com.handyman.model.MyProfileModel;
import com.handyman.model.TimeIntervalModel;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;

public class GetTimeIntervalRequestTask extends AsyncTask<String, Integer, Object> {

	public static final String TAG = "GetTimeIntervalRequestTask";
	public Context mContext;
//	private ProgressDialog mProgressDialog;
	private AsyncCallListener mAsyncCallListener;
	private String mErrorMessage;
//	private SharedPreferences mSharedPreferences;
	private boolean mIsError = false;

	public GetTimeIntervalRequestTask(Context mContext) {
		this.mContext = mContext;
//		mProgressDialog = new ProgressDialog(mContext);
//		mProgressDialog.setMessage(mContext.getResources().getString(R.string.loading));
//		mProgressDialog.setCanceledOnTouchOutside(false);
//		mProgressDialog.setCancelable(false);
//		mSharedPreferences = mContext.getSharedPreferences(Utils.PREF_NAME,	mContext.MODE_PRIVATE);
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
		return getTimeInterval();
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

	private Object getTimeInterval() {

		DataModel dataModel = new DataModel();
		ArrayList<TimeIntervalModel> timeIntervalModels = new ArrayList<TimeIntervalModel>();
		String result = "";
		
		String getServerPath = Utils.URL_SERVER_ADDRESS + Utils.GET_TIME_INTERVAL;
		try {

			result = Utils.postRequest(getServerPath);
			Logger.e(TAG, result);

			JSONObject jObj = new JSONObject(result.toString());
			
			dataModel.setSuccess(jObj.getString("success"));
			dataModel.setMessage(jObj.getString("message"));
			
			Gson gson = new Gson();
			Type listType = new TypeToken<List<TimeIntervalModel>>() {}.getType();
			timeIntervalModels = gson.fromJson(jObj.get("data").toString(), listType);
			
			if(timeIntervalModels.size() > 0){
				dataModel.setTimeIntervalModels(timeIntervalModels);
			}
			
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return dataModel;
	}
}
