package com.android.handy.service;

import org.json.JSONObject;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;

import com.android.handy.R;
import com.android.handy.logger.Logger;
import com.android.handy.model.RegisterModel;
import com.google.gson.Gson;

public class LoginRequestTask extends AsyncTask<String, Integer, Object> {

	public static final String TAG = "LoginRequestTask";
	public Context mContext;
	private ProgressDialog mProgressDialog;
	private AsyncCallListener mAsyncCallListener;
	private String mErrorMessage;
	private SharedPreferences mSharedPreferences;
	private boolean mIsError = false;

	public LoginRequestTask(Context mContext) {
		this.mContext = mContext;
		mProgressDialog = new ProgressDialog(mContext);
		mProgressDialog.setMessage(mContext.getResources().getString(
				R.string.loading));
		mProgressDialog.setCanceledOnTouchOutside(false);
		mProgressDialog.setCancelable(false);
		mSharedPreferences = mContext.getSharedPreferences(Utils.PREF_NAME,
				mContext.MODE_PRIVATE);
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
		return login(params[0], params[1],params[2],params[3],params[4],params[5],params[6]);
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

	private Object login(String mobile,	String password,String user_type, String device_id, String device_token, String lat, String lng) {

		
		if(lat.equalsIgnoreCase("0.0"))
			lat = "0";
		if(lng.equalsIgnoreCase("0.0"))
			lng = "0";
		
		RegisterModel registerModel = new RegisterModel();
		String result = "";
		
		JSONObject jsonMain = new JSONObject();
		JSONObject jsonData = new JSONObject();
		JSONObject jsonUser = new JSONObject();
		String getServerPath = Utils.URL_SERVER_ADDRESS + Utils.LOGIN;
		try {

			jsonUser.accumulate("mobile", mobile);
			jsonUser.accumulate("password", password);
			jsonUser.accumulate("user_type", user_type);
			jsonUser.accumulate("device_id", device_id);
			jsonUser.accumulate("device_token", device_token);
			jsonUser.accumulate("lat", lat);
			jsonUser.accumulate("lng", lng);
			jsonData.accumulate("user", jsonUser);
			jsonMain.accumulate("data", jsonData);

			result = Utils.POST(getServerPath, jsonMain);
//			result = jsonMain.toString();
			JSONObject jObj1 = new JSONObject(result.toString());
			Gson gson1 = new Gson();
			registerModel = gson1.fromJson(jObj1.toString(), RegisterModel.class);
			
			Utils.storeString(mSharedPreferences, Utils.USER_PROFILE, jObj1.toString());
			Utils.storeString(mSharedPreferences, Utils.USER_ID, registerModel.user.id);
			Utils.storeString(mSharedPreferences, Utils.FIRSTNAME, registerModel.user.firstname);
			Utils.storeString(mSharedPreferences, Utils.LASTNAME, registerModel.user.lastname);
			Utils.storeString(mSharedPreferences, Utils.EMAIL, registerModel.user.email);
			Utils.storeString(mSharedPreferences, Utils.IMAGEPATH, registerModel.user.img_path);
//			Utils.storeString(mSharedPreferences, Utils.USER_TYPE, registerModel.user.user_type);
			
			/*if (!jObj1.isNull("data")) {
                JSONObject login_jobj = jObj1.getJSONObject("data");
                User user = new User();
                if (!login_jobj.isNull("id"))
                    user.setId(login_jobj.getString("id"));
                
                if (!login_jobj.isNull("firstname"))
                    user.setId(login_jobj.getString("firstname"));
                
                if (!login_jobj.isNull("lastname"))
                    user.setId(login_jobj.getString("lastname"));
                
                if (!login_jobj.isNull("img_path"))
                    user.setId(login_jobj.getString("img_path"));
                
			}
                
			Utils.storeString(mSharedPreferences, Utils.USER_ID, registerModel.user.getId());
			Utils.storeString(mSharedPreferences, Utils.FIRSTNAME, registerModel.user.getFirstname());
			Utils.storeString(mSharedPreferences, Utils.LASTNAME, registerModel.user.getLastname());
			Utils.storeString(mSharedPreferences, Utils.IMAGEPATH, registerModel.user.getImg_path());*/

		} catch (Exception e) {
			e.printStackTrace();
		}
		return registerModel;
	}
}
