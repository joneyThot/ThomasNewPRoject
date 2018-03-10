package com.handy.service;

import android.content.Context;
import android.os.AsyncTask;

import com.handy.logger.Logger;
import com.handy.model.HandymanCreditsModel;

import org.json.JSONObject;

public class HandymanAddCustomerCoinsRequestTask extends AsyncTask<String, Integer, Object> {

    public static final String TAG = "HandymanAddCustomerCoinsRequestTask";
    public Context mContext;
    //	private ProgressDialog mProgressDialog;
    private AsyncCallListener mAsyncCallListener;
    private String mErrorMessage;
    private boolean mIsError = false;

    public HandymanAddCustomerCoinsRequestTask(Context mContext) {
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
        return addCoinsNotification(params[0], params[1]);
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

    private Object addCoinsNotification(String id, String client_id) {

        HandymanCreditsModel handymanCreditsModel = new HandymanCreditsModel();
        String result = "";

        JSONObject jsonMain = new JSONObject();
        JSONObject jsonData = new JSONObject();
        JSONObject jsonUser = new JSONObject();
        String getServerPath = Utils.URL_SERVER_ADDRESS + Utils.HANDYMAN_SEND_COMPLETE_NOTIFICATION;

        try {

            jsonUser.accumulate("id", id);
            jsonUser.accumulate("client_id", client_id);
            jsonData.accumulate("hire", jsonUser);
            jsonMain.accumulate("data", jsonData);

            result = Utils.POST(getServerPath, jsonMain);
            JSONObject jObj = new JSONObject(result.toString());

        } catch (Exception e) {
            e.printStackTrace();
        }

        return handymanCreditsModel;
    }
}
