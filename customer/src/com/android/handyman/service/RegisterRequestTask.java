package com.android.handyman.service;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.media.Image;
import android.os.AsyncTask;

import com.android.handyman.R;
import com.android.handyman.logger.Logger;
import com.android.handyman.model.RegisterModel;
import com.google.gson.Gson;

public class RegisterRequestTask extends AsyncTask<String, Integer, Object> {

	public static final String TAG = "RegisterRequestTask";
	public Context mContext;
	private ProgressDialog mProgressDialog;
	private AsyncCallListener mAsyncCallListener;
	private String mErrorMessage;
	private SharedPreferences mSharedPreferences;
	private boolean mIsError = false;

	public RegisterRequestTask(Context mContext) {
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
		return register(params[0], params[1], params[2], params[3], params[4],
				params[5], params[6], params[7], params[8], params[9],
				params[10], params[11], params[12], params[13], params[14],
				params[15], params[16], params[17], params[18],params[19]);
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

	private Object register(String mobile, String firstname, String lastname,
			String gender, String dob, String email, String whatsapp_id,
			String password, String address, String street, String landmark,
			String city, String pincode, String state, String website,
			String lat, String lng, String device_id, String device_token, String img) {

		if(state.equalsIgnoreCase("State")) 
			state = "";
		if(city.equalsIgnoreCase("City")) 
			city = "";
		if(lat.equalsIgnoreCase("0.0"))
			lat = "0";
		if(lng.equalsIgnoreCase("0.0"))
			lng = "0";
		
		RegisterModel registerModel = new RegisterModel();
		String result = "";
		
		JSONObject jsonMain = new JSONObject();
		JSONObject jsonData = new JSONObject();
		JSONObject jsonUser = new JSONObject();
		String getServerPath = Utils.URL_SERVER_ADDRESS + Utils.REGISTER;
		try {

//			jsonUser.accumulate("mobile", mobile);
//			jsonUser.accumulate("firstname", firstname);
//			jsonUser.accumulate("lastname", lastname);
//			jsonUser.accumulate("gender", gender);
//			jsonUser.accumulate("dob", dob);
//			jsonUser.accumulate("email", email);
//			jsonUser.accumulate("whatsapp_id", whatsapp_id);
//			jsonUser.accumulate("password", password);
//			jsonUser.accumulate("address", address);
//			jsonUser.accumulate("street", street);
//			jsonUser.accumulate("landmark", landmark);
//			jsonUser.accumulate("city", city);
//			jsonUser.accumulate("pincode", pincode);
//			jsonUser.accumulate("state", state);
//			jsonUser.accumulate("website", website);
//			jsonUser.accumulate("lat", lat);
//			jsonUser.accumulate("lng", lng);
//			jsonUser.accumulate("img", img);
//			jsonData.accumulate("users", jsonUser);
//			jsonMain.accumulate("data", jsonData);
//
//			result = Utils.POST(getServerPath, jsonMain);
//			result = jsonMain.toString();
//			JSONObject jObj = new JSONObject(result);
//			Gson gson = new Gson();
//			registerModel = gson.fromJson(jObj.toString(), RegisterModel.class);
			
			List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(18);
			nameValuePairs.add(new BasicNameValuePair("mobile", mobile));
			nameValuePairs.add(new BasicNameValuePair("firstname", firstname));
			nameValuePairs.add(new BasicNameValuePair("lastname", lastname));
			nameValuePairs.add(new BasicNameValuePair("gender", gender));
			nameValuePairs.add(new BasicNameValuePair("dob", dob));
			nameValuePairs.add(new BasicNameValuePair("email", email));
			nameValuePairs.add(new BasicNameValuePair("whatsapp_id", whatsapp_id));
			nameValuePairs.add(new BasicNameValuePair("password", password));
			nameValuePairs.add(new BasicNameValuePair("address", address));
			nameValuePairs.add(new BasicNameValuePair("street", street));
			nameValuePairs.add(new BasicNameValuePair("landmark", landmark));
			nameValuePairs.add(new BasicNameValuePair("city", city));
			nameValuePairs.add(new BasicNameValuePair("pincode", pincode));
			nameValuePairs.add(new BasicNameValuePair("state", state));
			nameValuePairs.add(new BasicNameValuePair("website", website));
			nameValuePairs.add(new BasicNameValuePair("lat", lat));
			nameValuePairs.add(new BasicNameValuePair("lng", lng));
			nameValuePairs.add(new BasicNameValuePair("device_id", device_id));
			nameValuePairs.add(new BasicNameValuePair("device_token", device_token));
			if(!img.equalsIgnoreCase("")){
				nameValuePairs.add(new BasicNameValuePair("img", img));
			}
			
			result = Utils.postMultipart(getServerPath, nameValuePairs);
			
			JSONObject jObj1 = new JSONObject(result.toString());
			Gson gson1 = new Gson();
			registerModel = gson1.fromJson(jObj1.toString(), RegisterModel.class);
			Utils.storeString(mSharedPreferences, Utils.USER_ID, registerModel.user.id);
			Utils.storeString(mSharedPreferences, Utils.OTP_CODE, registerModel.user.otp);
			Utils.storeString(mSharedPreferences, Utils.MOBILE_NO, registerModel.user.mobile);
			

		} catch (Exception e) {
			e.printStackTrace();
		}
		return registerModel;
	}
}
