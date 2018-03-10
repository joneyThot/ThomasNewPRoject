package com.handy.service;

import android.content.Context;
import android.os.AsyncTask;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.handy.logger.Logger;
import com.handy.model.CreditsModel;
import com.handy.model.HandymanCreditsModel;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class GetCustomerAddedCoinsRequestTask extends AsyncTask<String, Integer, Object> {

    public static final String TAG = "GetCustomerAddedCoinsRequestTask";
    public Context mContext;
    //	private ProgressDialog mProgressDialog;
    private AsyncCallListener mAsyncCallListener;
    private String mErrorMessage;
    private boolean mIsError = false;

    public GetCustomerAddedCoinsRequestTask(Context mContext) {
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
        return getCustomerCoins(params[0], params[1], params[2]);
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

    private Object getCustomerCoins(String id, String client_id, String handyman_id) {

        HandymanCreditsModel handymanCreditsModel = new HandymanCreditsModel();
        String result = "";

        JSONObject jsonMain = new JSONObject();
        JSONObject jsonData = new JSONObject();
        JSONObject jsonUser = new JSONObject();
        String getServerPath = Utils.URL_SERVER_ADDRESS + Utils.GET_CUSTOMER_COINS;
        try {

            jsonUser.accumulate("id", id);
            jsonUser.accumulate("client_id", client_id);
            jsonUser.accumulate("handyman_id", handyman_id);
            jsonData.accumulate("hire", jsonUser);
            jsonMain.accumulate("data", jsonData);

            result = Utils.POST(getServerPath, jsonMain);
            JSONObject jObj = new JSONObject(result.toString());
            handymanCreditsModel.setSuccess(jObj.getString("success"));
            handymanCreditsModel.setMessage(jObj.getString("message"));
            if (!jObj.isNull("coins")) {
                handymanCreditsModel.setCoins(jObj.getString("coins"));
            }


        } catch (Exception e) {
            e.printStackTrace();
        }

        return handymanCreditsModel;
    }
}
