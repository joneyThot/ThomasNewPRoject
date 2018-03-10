package com.handy.service;

import java.util.ArrayList;

import org.json.JSONObject;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;

import com.handy.R;
import com.handy.logger.Logger;
import com.handy.model.ApplyDiscountCreditsModel;
import com.handy.model.ApplyDiscountModel;
import com.handy.model.DisocuntCreditsModel;

public class ApplyDiscountRequestTask extends AsyncTask<String, Integer, Object> {

	public static final String TAG = "ApplyDiscountRequestTask";
	public Context mContext;
//	private ProgressDialog mProgressDialog;
	private AsyncCallListener mAsyncCallListener;
	private String mErrorMessage;
	private SharedPreferences mSharedPreferences;
	private boolean mIsError = false;

	public ApplyDiscountRequestTask(Context mContext) {
		this.mContext = mContext;
//		mProgressDialog = new ProgressDialog(mContext);
//		mProgressDialog.setMessage(mContext.getResources().getString(R.string.loading));
//		mProgressDialog.setCanceledOnTouchOutside(false);
//		mProgressDialog.setCancelable(false);
//		mSharedPreferences = mContext.getSharedPreferences(Utils.PREF_NAME,	mContext.MODE_PRIVATE);
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
		return apply_notification(params[0], params[1], params[2], params[3]);
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

	private Object apply_notification(String client_id, String amount, String discount,	String credit) {
		
		ApplyDiscountModel applyDiscountModel = new ApplyDiscountModel();
		ArrayList<ApplyDiscountCreditsModel> mDiscountCreditList = null;
		ArrayList<DisocuntCreditsModel> mDiscountList = null;
		ArrayList<DisocuntCreditsModel> mCreditsList = null;
		String result = "";

		JSONObject jsonMain = new JSONObject();
		JSONObject jsonData = new JSONObject();
		JSONObject jsonUser = new JSONObject();
		
		String getServerPath = Utils.URL_SERVER_ADDRESS + Utils.APPLY_DISCOUNT;
		try {

			jsonUser.accumulate("client_id", client_id);
			jsonUser.accumulate("amount", amount);
			jsonUser.accumulate("discount", discount);
			jsonUser.accumulate("credit", credit);
			jsonData.accumulate("hire", jsonUser);
			jsonMain.accumulate("data", jsonData);

			result = Utils.POST(getServerPath, jsonMain);
			JSONObject jObj = new JSONObject(result.toString());
			
			applyDiscountModel.setSuccess(jObj.getString("success"));
			applyDiscountModel.setMessage(jObj.getString("message"));
			
			if(!jObj.isNull("data")){
				JSONObject data_jObj = jObj.getJSONObject("data");
				mDiscountCreditList = new ArrayList<ApplyDiscountCreditsModel>();
				
				ApplyDiscountCreditsModel applyDiscountCreditsModel = new ApplyDiscountCreditsModel();
				
				if(!data_jObj.isNull("discount")){
					JSONObject discount_jObj = data_jObj.getJSONObject("discount");
	                mDiscountList = new ArrayList<DisocuntCreditsModel>();
	                
	                DisocuntCreditsModel disocuntCreditsModel = new DisocuntCreditsModel();
	                disocuntCreditsModel.setSuccess(discount_jObj.getString("success"));
	                disocuntCreditsModel.setMessage(discount_jObj.getString("message"));
	                if(!discount_jObj.isNull("total"))
	                disocuntCreditsModel.setTotal(discount_jObj.getString("total"));
	                if(!discount_jObj.isNull("discount"))
	                disocuntCreditsModel.setDiscount(discount_jObj.getString("discount"));

	                mDiscountList.add(disocuntCreditsModel);
	                
	                if(mDiscountList.size() > 0){
						applyDiscountCreditsModel.setmDiscountModel(mDiscountList);
						mDiscountCreditList.add(applyDiscountCreditsModel);
					}
	                
				}
				
				if(!data_jObj.isNull("credit")){
					JSONObject credit_jObj = data_jObj.getJSONObject("credit");
	                mCreditsList = new ArrayList<DisocuntCreditsModel>();
	                
	                DisocuntCreditsModel disocuntCreditsModel = new DisocuntCreditsModel();
	                disocuntCreditsModel.setSuccess(credit_jObj.getString("success"));
	                disocuntCreditsModel.setMessage(credit_jObj.getString("message"));
	                if(!credit_jObj.isNull("total"))
	                disocuntCreditsModel.setTotal(credit_jObj.getString("total"));
	                if(!credit_jObj.isNull("credit"))
	                disocuntCreditsModel.setCredit(credit_jObj.getString("credit"));
	                
	                mCreditsList.add(disocuntCreditsModel);
	                
	                if(mCreditsList.size() > 0){
						applyDiscountCreditsModel.setmCreditModel(mCreditsList);
						mDiscountCreditList.add(applyDiscountCreditsModel);
					}
				}
				
				
				if(mDiscountCreditList.size() > 0){
					applyDiscountModel.setApplyDiscountCreditsModels(mDiscountCreditList);
				}
				
				
			}
			

		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return applyDiscountModel;
	}
}
