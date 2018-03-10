package com.android.handyman.service;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONObject;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;

import com.android.handyman.R;
import com.android.handyman.logger.Logger;
import com.android.handyman.model.CreditsModel;
import com.android.handyman.model.DataModel;
import com.android.handyman.model.HandymanModel;
import com.android.handyman.model.MyProfileModel;
import com.android.handyman.model.ProfileHandymanModel;
import com.android.handyman.model.RegisterModel;
import com.android.handyman.model.StateModel;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

public class GetMyProfileRequestListTask extends AsyncTask<String, Integer, Object> {

	public static final String TAG = "GetProfileRequestTask";
	public Context mContext;
	private ProgressDialog mProgressDialog;
	private AsyncCallListener mAsyncCallListener;
	private String mErrorMessage;
	private SharedPreferences mSharedPreferences;
	private boolean mIsError = false;

	public GetMyProfileRequestListTask(Context mContext) {
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
		return getMyProfile(params[0]);
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

	private Object getMyProfile(String id) {

		DataModel dataModel = new DataModel();
		ArrayList<MyProfileModel> myProfileModelsList = new ArrayList<MyProfileModel>();
		String result = "";
		
		JSONObject jsonMain = new JSONObject();
		JSONObject jsonData = new JSONObject();
		JSONObject jsonUser = new JSONObject();
		String getServerPath = Utils.URL_SERVER_ADDRESS + Utils.GET_PROFILE;
		try {

			jsonUser.accumulate("id", id);
			jsonData.accumulate("users", jsonUser);
			jsonMain.accumulate("data", jsonData);

			result = Utils.POST(getServerPath, jsonMain);
			JSONObject jObj = new JSONObject(result.toString());
			
			dataModel.setSuccess(jObj.getString("success"));
			dataModel.setMessage(jObj.getString("message"));
			
			Gson gson = new Gson();
			Type listType = new TypeToken<List<MyProfileModel>>() {}.getType();
			myProfileModelsList = gson.fromJson(jObj.get("data").toString(), listType);
			
			if(myProfileModelsList.size() > 0){
				dataModel.setMyProfileModels(myProfileModelsList);
			}
			

		} catch (Exception e) {
			e.printStackTrace();
		}
		return dataModel;
	}
}
