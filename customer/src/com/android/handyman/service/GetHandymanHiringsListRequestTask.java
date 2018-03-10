package com.android.handyman.service;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONObject;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;

import com.android.handyman.R;
import com.android.handyman.logger.Logger;
import com.android.handyman.model.HandymanModel;
import com.android.handyman.model.MyHiringsModel;
import com.android.handyman.model.ProfileHandymanModel;
import com.android.handyman.model.RegisterModel;
import com.android.handyman.model.StateModel;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

public class GetHandymanHiringsListRequestTask extends AsyncTask<String, Integer, Object> {

	public static final String TAG = "GetHandymanHiringsListRequestTask";
	public Context mContext;
//	private ProgressDialog mProgressDialog;
	private AsyncCallListener mAsyncCallListener;
	private String mErrorMessage;
	private SharedPreferences mSharedPreferences;
	private boolean mIsError = false;
//	View mFooterView;
//	boolean loadingMore = false;

	public GetHandymanHiringsListRequestTask(Context mContext) {
		this.mContext = mContext;
//		mProgressDialog = new ProgressDialog(mContext);
//		mProgressDialog.setMessage(mContext.getResources().getString(R.string.loading));
//		mProgressDialog.setCanceledOnTouchOutside(false);
//		mProgressDialog.setCancelable(false);
		mSharedPreferences = mContext.getSharedPreferences(Utils.PREF_NAME,	mContext.MODE_PRIVATE);
//		loadingMore = true;
//        mFooterView = LayoutInflater.from(mContext).inflate(R.layout.loadmore, null);
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
		return getMyHirings(params[0],params[1],params[2],params[3],params[4]);
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

	private Object getMyHirings(String handyman_id, String status, String lat, String lng , String page) {
		
		if(lat.equalsIgnoreCase("0.0"))
			lat = "0";
		if(lng.equalsIgnoreCase("0.0"))
			lng = "0";

		ArrayList<MyHiringsModel> myHiringsModels = new ArrayList<MyHiringsModel>();
		String result = "";
		
		JSONObject jsonMain = new JSONObject();
		JSONObject jsonData = new JSONObject();
		JSONObject jsonUser = new JSONObject();
		String getServerPath = Utils.URL_SERVER_ADDRESS + Utils.GET_HANDYMAN_HIRINGS;
		try {

			jsonUser.accumulate("handyman_id", handyman_id);
			jsonUser.accumulate("status", status);
			jsonUser.accumulate("lat", lat);
			jsonUser.accumulate("lng", lng);
			jsonUser.accumulate("page", page);
			jsonData.accumulate("hire", jsonUser);
			jsonMain.accumulate("data", jsonData);

			result = Utils.POST(getServerPath, jsonMain);
			JSONObject jObj = new JSONObject(result.toString());
			
			Gson gson = new Gson();
			Type listType = new TypeToken<List<MyHiringsModel>>() {}.getType();
			myHiringsModels = gson.fromJson(jObj.get("data").toString(), listType);
			
			

		} catch (Exception e) {
			e.printStackTrace();
		}
		return myHiringsModels;
	}
}
