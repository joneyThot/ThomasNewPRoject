package com.handy.service;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;

import com.handy.R;
import com.handy.logger.Logger;
import com.handy.model.DataModel;
import com.handy.model.HandymanCreditsModel;
import com.handy.model.MyHiringsModel;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class GetHandymanCreditListRequestTask extends AsyncTask<String, Integer, Object> {

	public Context mContext;
//	private ProgressDialog mProgressDialog;
	private AsyncCallListener mAsyncCallListener;
	private String mErrorMessage;
	private boolean mIsError = false;

	public GetHandymanCreditListRequestTask(Context mContext) {
		this.mContext = mContext;
//		mProgressDialog = new ProgressDialog(mContext);
//		mProgressDialog.setMessage(mContext.getResources().getString(R.string.loading));
//		mProgressDialog.setCanceledOnTouchOutside(false);
//		mProgressDialog.setCancelable(false);
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
		return getHandymanCreditList(params[0],params[1],params[2]);
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

	private Object getHandymanCreditList(String handyman_id, String is_requested, String page) {

		DataModel dataModel = new DataModel();
		ArrayList<HandymanCreditsModel> handymanCreditsModelList = new ArrayList<HandymanCreditsModel>();
		String result = "";

		JSONObject jsonMain = new JSONObject();
		JSONObject jsonData = new JSONObject();
		JSONObject jsonUser = new JSONObject();
		String getServerPath = Utils.URL_SERVER_ADDRESS + Utils.GET_REQUEST_STATUS_LIST;
		try {

			jsonUser.accumulate("handyman_id", handyman_id);
			jsonUser.accumulate("is_requested", is_requested);
			jsonUser.accumulate("page", page);
			jsonData.accumulate("credit_to_cash", jsonUser);
			jsonMain.accumulate("data", jsonData);

			result = Utils.POST(getServerPath, jsonMain);
			JSONObject jObj = new JSONObject(result.toString());

			dataModel.setSuccess(jObj.getString("success"));
			dataModel.setMessage(jObj.getString("message"));

			Gson gson = new Gson();
			Type listType = new TypeToken<List<HandymanCreditsModel>>() {}.getType();
			if(!jObj.isNull("data"))
				handymanCreditsModelList = gson.fromJson(jObj.get("data").toString(), listType);

			if(handymanCreditsModelList.size() > 0){
				dataModel.setHandymanCreditsModels(handymanCreditsModelList);
			}


		} catch (Exception e) {
			e.printStackTrace();
		}
		return dataModel;
	}
}
