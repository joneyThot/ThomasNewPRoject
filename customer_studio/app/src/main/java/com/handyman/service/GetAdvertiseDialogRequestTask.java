package com.handyman.service;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.handyman.R;
import com.handyman.logger.Logger;
import com.handyman.model.AdvertiseListModel;
import com.handyman.model.DataModel;

import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class GetAdvertiseDialogRequestTask extends AsyncTask<String, Integer, Object> {

	public Context mContext;
	private ProgressDialog mProgressDialog;
	private AsyncCallListener mAsyncCallListener;
	private String mErrorMessage;
	private boolean mIsError = false;

	public GetAdvertiseDialogRequestTask(Context mContext) {
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
		return getadvertise(params[0]);
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

	/*private Object getadvertise() {
		
		ArrayList<AdvertiseModel> advertiseModel = new ArrayList<AdvertiseModel>();
		String result = "";
		
		String getServerPath = Utils.URL_SERVER_ADDRESS + Utils.GET_ADVERTISE_LIST;
		try {

			result = Utils.postRequest(getServerPath);
			JSONObject jObj = new JSONObject(result.toString());

			Gson gson = new Gson();
			Type listType = new TypeToken<List<AdvertiseModel>>() {}.getType();
			advertiseModel = gson.fromJson(jObj.get("data").toString(), listType);
			
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return advertiseModel;
	}*/
	
	private Object getadvertise(String users_id) {
		
		DataModel advertiseModel = new DataModel();
		ArrayList<AdvertiseListModel> advertiseListModels = null;
		
		String result = "";

		JSONObject jsonMain = new JSONObject();
		JSONObject jsonData = new JSONObject();
		JSONObject jsonUser = new JSONObject();
		String getServerPath = Utils.URL_SERVER_ADDRESS + Utils.GET_ADVERTISE_LIST;
		try {

			jsonUser.accumulate("users_id", users_id);
			jsonData.accumulate("banner", jsonUser);
			jsonMain.accumulate("data", jsonData);

			result = Utils.POST(getServerPath, jsonMain);
			JSONObject jObj = new JSONObject(result.toString());

			advertiseModel.setSuccess(jObj.getString("success"));
			advertiseModel.setMessage(jObj.getString("message"));
			if(!jObj.isNull("data")) {
				Gson gson = new Gson();
				Type listType = new TypeToken<List<AdvertiseListModel>>() {}.getType();
				advertiseListModels = gson.fromJson(jObj.get("data").toString(), listType);
			}
			/*JSONArray jArray = jObj.getJSONArray("data");
			advertiseListModels = new ArrayList<AdvertiseListModel>();
			
			for (int i = 0; i < jArray.length(); i++) {
				JSONObject data_jobj = jArray.getJSONObject(i);
				AdvertiseListModel advertiseListModel = new AdvertiseListModel();
				
                 advertiseListModel.setId(data_jobj.getString("id"));
                 advertiseListModel.setTitle(data_jobj.getString("title"));
                 advertiseListModel.setBanner_name(data_jobj.getString("banner_name"));
                 advertiseListModel.setBanner_path(data_jobj.getString("banner_path"));
                 advertiseListModel.setDescription(data_jobj.getString("description"));
                 advertiseListModel.setIs_deleted(data_jobj.getString("is_deleted"));
                 advertiseListModel.setStatus(data_jobj.getString("status"));
                 advertiseListModel.setCreated_date(data_jobj.getString("created_date"));
                 advertiseListModel.setModified_date(data_jobj.getString("modified_date"));
                 advertiseListModel.setCount(data_jobj.getString("count"));

                 advertiseListModels.add(advertiseListModel);
			}*/
			
			if(advertiseListModels != null){
				advertiseModel.setAdvertiseListModels(advertiseListModels);
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return advertiseModel;
	}
}
