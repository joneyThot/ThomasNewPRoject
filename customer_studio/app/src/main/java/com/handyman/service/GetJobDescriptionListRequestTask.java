package com.handyman.service;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONObject;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;

import com.handyman.R;
import com.handyman.logger.Logger;
import com.handyman.model.HandymanModel;
import com.handyman.model.JobdescriptionModel;
import com.handyman.model.MyHiringsModel;
import com.handyman.model.ProfileHandymanModel;
import com.handyman.model.RegisterModel;
import com.handyman.model.StateModel;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

public class GetJobDescriptionListRequestTask extends AsyncTask<String, Integer, Object> {

	public static final String TAG = "GetJobDescriptionListRequestTask";
	public Context mContext;
	private ProgressDialog mProgressDialog;
	private AsyncCallListener mAsyncCallListener;
	private String mErrorMessage;
	private SharedPreferences mSharedPreferences;
	private boolean mIsError = false;

	public GetJobDescriptionListRequestTask(Context mContext) {
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
		return getJobDescription(params[0]);
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

	private Object getJobDescription(String handyman_id) {

		ArrayList<JobdescriptionModel> jobdescriptionModels = new ArrayList<JobdescriptionModel>();
		String result = "";
		
		JSONObject jsonMain = new JSONObject();
		JSONObject jsonData = new JSONObject();
		JSONObject jsonUser = new JSONObject();
		String getServerPath = Utils.URL_SERVER_ADDRESS + Utils.GET_CATEGORY_OF_HANDYMAN;
		try {

			jsonUser.accumulate("handyman_id", handyman_id);
			jsonData.accumulate("hire", jsonUser);
			jsonMain.accumulate("data", jsonData);	

			result = Utils.POST(getServerPath, jsonMain);
			JSONObject jObj = new JSONObject(result.toString());
			
			Gson gson = new Gson();
			Type listType = new TypeToken<List<JobdescriptionModel>>() {}.getType();
			jobdescriptionModels = gson.fromJson(jObj.get("data").toString(), listType);
			

		} catch (Exception e) {
			e.printStackTrace();
		}
		return jobdescriptionModels;
	}
}
