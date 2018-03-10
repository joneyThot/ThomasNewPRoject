package com.handy.service;

import org.json.JSONArray;
import org.json.JSONObject;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;

import com.handy.R;
import com.handy.logger.Logger;
import com.handy.model.ContactsSupportModel;
import com.handy.model.RegisterModel;
import com.google.gson.Gson;

import java.util.ArrayList;

public class GetContackSupportRequestTask extends AsyncTask<String, Integer, Object> {

	public Context mContext;
	private ProgressDialog mProgressDialog;
	private AsyncCallListener mAsyncCallListener;
	private String mErrorMessage;
	private boolean mIsError = false;

	public GetContackSupportRequestTask(Context mContext) {
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
		return getContact();
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

	private Object getContact() {

		ContactsSupportModel contactsSupportModel = new ContactsSupportModel();
		String result = "";
		
		String getServerPath = Utils.URL_SERVER_ADDRESS + Utils.GET_CONTACT_SUPPORT;
		try {

			result = Utils.postRequest(getServerPath);

			JSONObject jObj = new JSONObject(result.toString());
			contactsSupportModel.setSuccess(jObj.getString("success"));
			contactsSupportModel.setMessage(jObj.getString("message"));
			if(!jObj.isNull("data")){
				JSONObject jObjData = jObj.getJSONObject("data");
				if(!jObjData.isNull("email")){
					JSONArray jArrayEmail = jObjData.getJSONArray("email");
					ArrayList<String> emailList = new ArrayList<>();
					for (int i = 0; i < jArrayEmail.length(); i++) {
						String email = jArrayEmail.getString(i);
						emailList.add(email);
					}
					contactsSupportModel.setEmail(emailList);
				}
				if(!jObjData.isNull("phone")){
					JSONArray jArrayPhone = jObjData.getJSONArray("phone");
					ArrayList<String> phoneList = new ArrayList<>();
					for (int i = 0; i < jArrayPhone.length(); i++) {
						String email = jArrayPhone.getString(i);
						phoneList.add(email);
					}
					contactsSupportModel.setPhone(phoneList);
				}
			}
//			Gson gson1 = new Gson();
//			registerModel = gson1.fromJson(jObj.toString(), RegisterModel.class);
			
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return contactsSupportModel;
	}
}
