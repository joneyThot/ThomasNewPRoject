package com.handy.service;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;

import com.handy.R;
import com.handy.logger.Logger;
import com.handy.model.RegisterModel;
import com.google.gson.Gson;

public class EditProfileRequestTask extends AsyncTask<String, Integer, Object> {

	public static final String TAG = "EditProfileRequestTask";
	public Context mContext;
	private ProgressDialog mProgressDialog;
	private AsyncCallListener mAsyncCallListener;
	private String mErrorMessage;
	private SharedPreferences mSharedPreferences;
	private boolean mIsError = false;

	public EditProfileRequestTask(Context mContext) {
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
		return editProfile(params[0], params[1], params[2], params[3], params[4],
				params[5], params[6], params[7], params[8], params[9],
				params[10], params[11], params[12], params[13], params[14],
				params[15]);
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

	private Object editProfile(String id, String firstname, String lastname,
			String gender, String dob, String email, String whatsapp_id,
			String address, String street, String landmark,
			String city, String pincode, String state, String lat, String lng, String img) {
		
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

		String getServerPath = Utils.URL_SERVER_ADDRESS + Utils.EDIT_PROFILE;
		try {

			List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(18);
			nameValuePairs.add(new BasicNameValuePair("id", id));
			nameValuePairs.add(new BasicNameValuePair("firstname", firstname));
			nameValuePairs.add(new BasicNameValuePair("lastname", lastname));
			nameValuePairs.add(new BasicNameValuePair("gender", gender));
			nameValuePairs.add(new BasicNameValuePair("dob", dob));
			nameValuePairs.add(new BasicNameValuePair("email", email));
			nameValuePairs.add(new BasicNameValuePair("whatsapp_id", whatsapp_id));
			nameValuePairs.add(new BasicNameValuePair("address", address));
			nameValuePairs.add(new BasicNameValuePair("street", street));
			nameValuePairs.add(new BasicNameValuePair("landmark", landmark));
			nameValuePairs.add(new BasicNameValuePair("city", city));
			nameValuePairs.add(new BasicNameValuePair("pincode", pincode));
			nameValuePairs.add(new BasicNameValuePair("state", state));
			nameValuePairs.add(new BasicNameValuePair("lat", lat));
			nameValuePairs.add(new BasicNameValuePair("lng", lng));
			if(!img.equalsIgnoreCase(""))
			nameValuePairs.add(new BasicNameValuePair("img", img));
			
			result = Utils.postMultipart(getServerPath, nameValuePairs);
			
			JSONObject jObj1 = new JSONObject(result.toString());
			Gson gson1 = new Gson();
			registerModel = gson1.fromJson(jObj1.toString(), RegisterModel.class);
			Utils.storeString(mSharedPreferences, Utils.USER_ID, registerModel.user.id);
			Utils.storeString(mSharedPreferences, Utils.IMAGEPATH, registerModel.user.img_path);

		} catch (Exception e) {
			e.printStackTrace();
		}
		return registerModel;
	}
}
