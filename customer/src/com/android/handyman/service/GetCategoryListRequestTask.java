package com.android.handyman.service;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;

import com.android.handyman.R;
import com.android.handyman.logger.Logger;
import com.android.handyman.model.CategoryListModel;
import com.android.handyman.model.DataModel;
import com.android.handyman.model.StateModel;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

public class GetCategoryListRequestTask extends AsyncTask<String, Integer, Object> {

	public Context mContext;
	private ProgressDialog mProgressDialog;
	private AsyncCallListener mAsyncCallListener;
	private String mErrorMessage;
	private boolean mIsError = false;

	public GetCategoryListRequestTask(Context mContext) {
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
		return getCategory(params[0],params[1]);
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

	private Object getCategory(String lat,String lng) {
		if(lat.equalsIgnoreCase("0.0"))
			lat = "0";
		if(lng.equalsIgnoreCase("0.0"))
			lng = "0";
		
//		ArrayList<StateModel> categoryList = new ArrayList<StateModel>();
		DataModel dataModel = new DataModel();
		ArrayList<CategoryListModel> categoryListModels = new ArrayList<CategoryListModel>();
		String result = "";
		
		JSONObject jsonMain = new JSONObject();
		JSONObject jsonData = new JSONObject();
		JSONObject jsonUser = new JSONObject();
		
		String getServerPath = Utils.URL_SERVER_ADDRESS + Utils.GET_CATEGORY_LIST;
		try {
			jsonUser.accumulate("lat", lat);
			jsonUser.accumulate("lng", lng);
			jsonData.accumulate("category", jsonUser);
			jsonMain.accumulate("data", jsonData);

			result = Utils.POST(getServerPath, jsonMain);

			JSONObject jObj = new JSONObject(result.toString());
//			Gson gson = new Gson();
//			Type listType = new TypeToken<List<StateModel>>() {}.getType();
//			categoryList = gson.fromJson(jObj.get("data").toString(), listType);
			
			dataModel.setSuccess(jObj.getString("success"));
			dataModel.setMessage(jObj.getString("message"));
//			if(!jObj.isNull("data")){
//				JSONArray jArray = jObj.getJSONArray("data");
//				categoryListModels = new ArrayList<CategoryListModel>();
//				
//				for (int i = 0; i < jArray.length(); i++) {
//					JSONObject data_jobj = jArray.getJSONObject(i);
//					
//					CategoryListModel categoryModel = new CategoryListModel();
//					categoryModel.setId(data_jobj.getString("id"));
//					categoryModel.setName(data_jobj.getString("name"));
//					categoryModel.setImg(data_jobj.getString("img"));
//					categoryModel.setImg_path(data_jobj.getString("img_path"));
//					categoryModel.setIs_deleted(data_jobj.getString("is_deleted"));
//					categoryModel.setStatus(data_jobj.getString("status"));
//					categoryModel.setCreated_date(data_jobj.getString("created_date"));
//					categoryModel.setModified_date(data_jobj.getString("modified_date"));
//					
//					categoryListModels.add(categoryModel);
//				}
			
			Gson gson = new Gson();
			Type listType = new TypeToken<List<CategoryListModel>>() {}.getType();
			categoryListModels = gson.fromJson(jObj.get("data").toString(), listType);
				
				if(categoryListModels.size() > 0){
					dataModel.setCategoryListModels(categoryListModels);
				}
//			}
			
			
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return dataModel;
	}
}
