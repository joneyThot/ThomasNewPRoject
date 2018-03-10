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
import com.android.handyman.model.DataModel;
import com.android.handyman.model.HandymanModel;
import com.android.handyman.model.MyHiringsModel;
import com.android.handyman.model.ProfileHandymanModel;
import com.android.handyman.model.RegisterModel;
import com.android.handyman.model.StateModel;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

public class GetMyHiringsListRequestTask extends AsyncTask<String, Integer, Object> {

	public static final String TAG = "GetMyHiringsListRequestTask";
	public Context mContext;
//	private ProgressDialog mProgressDialog;
	private AsyncCallListener mAsyncCallListener;
	private String mErrorMessage;
//	private SharedPreferences mSharedPreferences;
	private boolean mIsError = false;

	public GetMyHiringsListRequestTask(Context mContext) {
		this.mContext = mContext;
		/*mProgressDialog = new ProgressDialog(mContext);
		mProgressDialog.setMessage(mContext.getResources().getString(R.string.loading));
		mProgressDialog.setCanceledOnTouchOutside(false);
		mProgressDialog.setCancelable(false);*/
//		mSharedPreferences = mContext.getSharedPreferences(Utils.PREF_NAME,	mContext.MODE_PRIVATE);
	}

	@Override
	protected void onPreExecute() {
		super.onPreExecute();
		/*if (mProgressDialog != null && !mProgressDialog.isShowing()) {
			mProgressDialog.show();
		}*/
	}

	@Override
	protected Object doInBackground(String... params) {
		return getMyHirings(params[0],params[1],params[2]);
	}

	@Override
	protected void onPostExecute(Object result) {
		super.onPostExecute(result);
		/*if (mProgressDialog != null && mProgressDialog.isShowing()) {
			mProgressDialog.dismiss();
		}*/
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

	private Object getMyHirings(String id, String status, String page) {

		DataModel dataModel = new DataModel();
		ArrayList<MyHiringsModel> myHiringsModels = new ArrayList<MyHiringsModel>();
		String result = "";
		
		JSONObject jsonMain = new JSONObject();
		JSONObject jsonData = new JSONObject();
		JSONObject jsonUser = new JSONObject();
		String getServerPath = Utils.URL_SERVER_ADDRESS + Utils.GET_MY_HIRINGS;
		try {

			jsonUser.accumulate("id", id);
			jsonUser.accumulate("status", status);
			jsonUser.accumulate("page", page);
			jsonData.accumulate("hire", jsonUser);
			jsonMain.accumulate("data", jsonData);

			result = Utils.POST(getServerPath, jsonMain);
			JSONObject jObj = new JSONObject(result.toString());
			
			dataModel.setSuccess(jObj.getString("success"));
			dataModel.setMessage(jObj.getString("message"));
			
			Gson gson = new Gson();
			Type listType = new TypeToken<List<MyHiringsModel>>() {}.getType();
			if(!jObj.isNull("data"))
			myHiringsModels = gson.fromJson(jObj.get("data").toString(), listType);
			
			if(myHiringsModels.size() > 0){
				dataModel.setMyHiringsModels(myHiringsModels);
			}
			

		} catch (Exception e) {
			e.printStackTrace();
		}
		return dataModel;
	}
}
