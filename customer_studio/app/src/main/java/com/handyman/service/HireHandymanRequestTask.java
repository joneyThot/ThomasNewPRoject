package com.handyman.service;

import org.json.JSONObject;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;

import com.handyman.R;
import com.handyman.logger.Logger;
import com.handyman.model.HireHandymanModel;
import com.handyman.model.RegisterModel;
import com.google.gson.Gson;

public class HireHandymanRequestTask extends AsyncTask<String, Integer, Object> {

	public static final String TAG = "HireHandymanRequestTask";
	public Context mContext;
	private ProgressDialog mProgressDialog;
	private AsyncCallListener mAsyncCallListener;
	private String mErrorMessage;
	private SharedPreferences mSharedPreferences;
	private boolean mIsError = false;

	public HireHandymanRequestTask(Context mContext) {
		this.mContext = mContext;
		mProgressDialog = new ProgressDialog(mContext);
		mProgressDialog.setMessage(mContext.getResources().getString(R.string.loading));
		mProgressDialog.setCanceledOnTouchOutside(false);
		mProgressDialog.setCancelable(false);
		mSharedPreferences = mContext.getSharedPreferences(Utils.PREF_NAME, mContext.MODE_PRIVATE);
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
		return hire_handyman(params[0], params[1], params[2], params[3], params[4],
				params[5], params[6], params[7], params[8], params[9],
				params[10], params[11], params[12], params[13], params[14],params[15]);
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

	/*{"data":{"hire":{"handyman_id":"65","client_id":"142","job_description":"electronics",
			"appointment_date":"2016-11-20","appointment_time":"2:34:10",
			"contact_person":"Rahul","contact_no":"1234567890",
			"comment":"test","hire_status":"pending",
			"floor":"42/5th floor","address":"Shajanand college, Panjarapole, L-colony",
			"landmark":"null",
			"category":"1","sub_category":"3","lat":"23.4758","lng":"72.5648"}}}*/

	private Object hire_handyman(String handyman_id, String client_id, String job_description,
			String appointment_date, String appointment_time, String contact_person, String contact_no,
			String comment, String hire_status,String address, String floor, String apartment,
			String category,String sub_category, String lat,String lng) {

//		if(state.equalsIgnoreCase("State"))
//			state = "";
//		if(city.equalsIgnoreCase("City"))
//			city = "";
		if(lat.equalsIgnoreCase("0.0"))
			lat = "0";
		if(lng.equalsIgnoreCase("0.0"))
			lng = "0";
		
		HireHandymanModel hireHandymanModel = new HireHandymanModel();
		String result = "";
		
		JSONObject jsonMain = new JSONObject();
		JSONObject jsonData = new JSONObject();
		JSONObject jsonUser = new JSONObject();
		String getServerPath = Utils.URL_SERVER_ADDRESS + Utils.HIRE_HANDYMAN;
		try {

			jsonUser.accumulate("handyman_id", handyman_id);
			jsonUser.accumulate("client_id", client_id);
			jsonUser.accumulate("job_description", job_description);
			jsonUser.accumulate("appointment_date", appointment_date);
			jsonUser.accumulate("appointment_time", appointment_time);
			jsonUser.accumulate("contact_person", contact_person);
			jsonUser.accumulate("contact_no", contact_no);
			jsonUser.accumulate("comment", comment);
			jsonUser.accumulate("hire_status", hire_status);
			jsonUser.accumulate("address", address);
			jsonUser.accumulate("floor", floor);
			jsonUser.accumulate("apartment", apartment);
//			jsonUser.accumulate("landmark", landmark);
//			jsonUser.accumulate("city", city);
//			jsonUser.accumulate("pincode", pincode);
//			jsonUser.accumulate("state", state);
			jsonUser.accumulate("category", category);
			jsonUser.accumulate("sub_category", sub_category);
			jsonUser.accumulate("lat", lat);
			jsonUser.accumulate("lng", lng);
			jsonData.accumulate("hire", jsonUser);
			jsonMain.accumulate("data", jsonData);

			result = Utils.POST(getServerPath, jsonMain);
//			result = jsonMain.toString();
			JSONObject jObj = new JSONObject(result);
			Gson gson = new Gson();
			hireHandymanModel = gson.fromJson(jObj.toString(), HireHandymanModel.class);
			

		} catch (Exception e) {
			e.printStackTrace();
		}
		return hireHandymanModel;
	}
}
