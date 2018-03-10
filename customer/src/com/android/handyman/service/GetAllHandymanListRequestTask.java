package com.android.handyman.service;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;

import com.android.handyman.R;
import com.android.handyman.logger.Logger;
import com.android.handyman.model.CategoryListModel;
import com.android.handyman.model.DataModel;
import com.android.handyman.model.HandymanModel;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

public class GetAllHandymanListRequestTask extends AsyncTask<String, Integer, Object> {

	public static final String TAG = "GetAllHandymanListRequestTask";
	public Context mContext;
	private ProgressDialog mProgressDialog;
	private AsyncCallListener mAsyncCallListener;
	private String mErrorMessage;
	private SharedPreferences mSharedPreferences;
	private boolean mIsError = false;

	public GetAllHandymanListRequestTask(Context mContext) {
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
		return getAllHandyman(params[0],params[1],params[2],params[3]);
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

	private Object getAllHandyman(String lat,String lng, String category, String sub_category) {

		if(lat.equalsIgnoreCase("0.0"))
			lat = "0";
		if(lng.equalsIgnoreCase("0.0"))
			lng = "0";
		
//		ArrayList<HandymanModel> allHandymanList = new ArrayList<HandymanModel>();
		
		DataModel dataModel = new DataModel();
		ArrayList<HandymanModel> handymanListModels = new ArrayList<HandymanModel>();
		
		String result = "";
		
		JSONObject jsonMain = new JSONObject();
		JSONObject jsonData = new JSONObject();
		JSONObject jsonUser = new JSONObject();
		String getServerPath = Utils.URL_SERVER_ADDRESS + Utils.GET_ALL_HANDYMAN_LIST;
		try {

			jsonUser.accumulate("lat", lat);
			jsonUser.accumulate("lng", lng);
			jsonUser.accumulate("category", category);
			jsonUser.accumulate("sub_category", sub_category);
			jsonData.accumulate("users", jsonUser);
			jsonMain.accumulate("data", jsonData);	

			result = Utils.POST(getServerPath, jsonMain);
			JSONObject jObj = new JSONObject(result.toString());
			
			dataModel.setSuccess(jObj.getString("success"));
			dataModel.setMessage(jObj.getString("message"));
//			if(!jObj.isNull("data")){
//				JSONArray jArray = jObj.getJSONArray("data");
//				handymanListModels = new ArrayList<HandymanModel>();
//				
//				for (int i = 0; i < jArray.length(); i++) {
//					JSONObject issue = jArray.getJSONObject(i);
//					
//					HandymanModel handymanModel = new HandymanModel();
//					
//					handymanModel.setId(issue.getString("id"));
//					handymanModel.setMobile(issue.getString("mobile"));
//					handymanModel.setFirstname(issue.getString("firstname"));
//					handymanModel.setLastname(issue.getString("lastname"));
//					handymanModel.setGender(issue.getString("gender"));
//					handymanModel.setUser_type(issue.getString("user_type"));
//					handymanModel.setDob(issue.getString("dob"));
//					handymanModel.setEmail(issue.getString("email"));
//					handymanModel.setWhatsapp_id(issue.getString("whatsapp_id"));
//					handymanModel.setImg(issue.getString("img"));
//					handymanModel.setImg_path(issue.getString("img_path"));
//					handymanModel.setLat(issue.getString("lat"));
//					handymanModel.setLng(issue.getString("lng"));
//					handymanModel.setDistance(issue.getString("distance"));
//					handymanModel.setRating(issue.getString("rating"));
//					
//					handymanListModels.add(handymanModel);
//				}
				
				Gson gson = new Gson();
				Type listType = new TypeToken<List<HandymanModel>>() {}.getType();
				handymanListModels = gson.fromJson(jObj.get("data").toString(), listType);
			
				if(handymanListModels.size() > 0){
					dataModel.setHandymanModels(handymanListModels);
				}
//			}
			
//	        Gson gson = new Gson();
//	    	Type listType = new TypeToken<List<HandymanModel>>() {}.getType();
//	    	allHandymanList = gson.fromJson(jObj.get("data").toString(), listType);
	    	
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return dataModel;
	}
}
