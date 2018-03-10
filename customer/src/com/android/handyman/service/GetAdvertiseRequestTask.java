package com.android.handyman.service;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;

import com.android.handyman.R;
import com.android.handyman.logger.Logger;
import com.android.handyman.model.AdvertiseListModel;
import com.android.handyman.model.DataModel;

public class GetAdvertiseRequestTask extends AsyncTask<String, Integer, Object> {

	public Context mContext;
	private ProgressDialog mProgressDialog;
	private AsyncCallListener mAsyncCallListener;
	private String mErrorMessage;
	private boolean mIsError = false;

	public GetAdvertiseRequestTask(Context mContext) {
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
		return getadvertise();
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
	
private Object getadvertise() {
		
		DataModel advertiseModel = new DataModel();
		ArrayList<AdvertiseListModel> advertiseListModels = null;
		
		String result = "";
		
		String getServerPath = Utils.URL_SERVER_ADDRESS + Utils.GET_ADVERTISE_LIST;
		try {

			result = Utils.postRequest(getServerPath);
			JSONObject jObj = new JSONObject(result.toString());
//			Gson gson1 = new Gson();
//			advertiseModel = gson1.fromJson(jObj.toString(), AdvertiseModel.class);
			
			advertiseModel.setSuccess(jObj.getString("success"));
			advertiseModel.setMessage(jObj.getString("message"));
			JSONArray jArray = jObj.getJSONArray("data");
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
			}
			
			if(advertiseListModels.size() > 0){
				advertiseModel.setAdvertiseListModels(advertiseListModels);
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return advertiseModel;
	}
}
