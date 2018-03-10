package com.handy.service;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;

import com.handy.R;
import com.handy.logger.Logger;
import com.handy.model.HandymanCreditsModel;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class HandymanCreditToCashRequestTask extends AsyncTask<String, Integer, Object> {

	public static final String TAG = "HandymanCreditToCashRequestTask";
	public Context mContext;
	private ProgressDialog mProgressDialog;
	private AsyncCallListener mAsyncCallListener;
	private String mErrorMessage;
	private boolean mIsError = false;

	public HandymanCreditToCashRequestTask(Context mContext) {
		this.mContext = mContext;
		mProgressDialog = new ProgressDialog(mContext);
		mProgressDialog.setMessage(mContext.getResources().getString(R.string.loading));
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
		return creditToCash(params[0], params[1],params[2]);
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

	private Object creditToCash(String handyman_id, String credit_used_for_convert, String cash) {

		HandymanCreditsModel handymanCreditsModel = new HandymanCreditsModel();
		String result = "";

		JSONObject jsonMain = new JSONObject();
		JSONObject jsonData = new JSONObject();
		JSONObject jsonUser = new JSONObject();
		String getServerPath = Utils.URL_SERVER_ADDRESS + Utils.HANDYMAN_REQUEST_CREDIT_TO_CASH;

		try {

			jsonUser.accumulate("handyman_id", handyman_id);
			jsonUser.accumulate("credit_used_for_convert", credit_used_for_convert);
			jsonUser.accumulate("cash", cash);
			jsonData.accumulate("credit_to_cash", jsonUser);
			jsonMain.accumulate("data", jsonData);

			result = Utils.POST(getServerPath, jsonMain);
			JSONObject jObj = new JSONObject(result.toString());
			handymanCreditsModel.setSuccess(jObj.getString("success"));
			handymanCreditsModel.setMessage(jObj.getString("message"));


		} catch (Exception e) {
			e.printStackTrace();
		}

		/*try {

			List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(5);
			nameValuePairs.add(new BasicNameValuePair("id", id));
			nameValuePairs.add(new BasicNameValuePair("credit", credit));

			result = Utils.postRequest(getServerPath, nameValuePairs);
			
			JSONObject jObj1 = new JSONObject(result.toString());
			handymanCreditsModel.setSuccess(jObj1.getString("success"));
			handymanCreditsModel.setMessage(jObj1.getString("message"));
			if(!jObj1.isNull("total")) {
				handymanCreditsModel.setTotal(jObj1.getString("total"));
			}
//			if(!jObj1.isNull("data")){
//				JSONObject object = jObj1.getJSONObject("data");
//				handymanCreditsModel.setHandyman_id(object.getString("handyman_id"));
//				handymanCreditsModel.setCredit_use_for_convert(object.getString("credit_use_for_convert"));
//				handymanCreditsModel.setTotal_cash(object.getString("total_cash"));
//				handymanCreditsModel.setIs_requested(object.getString("is_requested"));
//				handymanCreditsModel.setId(object.getString("id"));
//			}
//			if(!jObj1.isNull("total_hm_credit"))
//				handymanCreditsModel.setTotal_hm_credit(jObj1.getString("total_hm_credit"));


		} catch (Exception e) {
			e.printStackTrace();
		}*/
		return handymanCreditsModel;
	}
}
