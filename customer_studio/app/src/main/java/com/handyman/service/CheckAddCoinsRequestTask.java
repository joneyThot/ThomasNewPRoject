package com.handyman.service;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;

import com.google.gson.Gson;
import com.handyman.R;
import com.handyman.logger.Logger;
import com.handyman.model.HireHandymanModel;

import org.json.JSONObject;

public class CheckAddCoinsRequestTask extends AsyncTask<String, Integer, Object> {

    public static final String TAG = "CheckAddCoinsRequestTask";
    public Context mContext;
    private ProgressDialog mProgressDialog;
    private AsyncCallListener mAsyncCallListener;
    private String mErrorMessage;
    private SharedPreferences mSharedPreferences;
    private boolean mIsError = false;

    public CheckAddCoinsRequestTask(Context mContext) {
        this.mContext = mContext;
        mProgressDialog = new ProgressDialog(mContext);
        mProgressDialog.setMessage(mContext.getResources().getString(
                R.string.loading));
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
        return coinsCheckHandyman(params[0], params[1]);
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

    private Object coinsCheckHandyman(String hire_id, String client_id) {

        HireHandymanModel hireHandymanModel = new HireHandymanModel();
        String result = "";

        JSONObject jsonMain = new JSONObject();
        JSONObject jsonData = new JSONObject();
        JSONObject jsonUser = new JSONObject();
        String getServerPath = Utils.URL_SERVER_ADDRESS + Utils.CHECK_COINS;
        try {

            jsonUser.accumulate("hire_id", hire_id);
            jsonUser.accumulate("client_id", client_id);
            jsonData.accumulate("customercredit", jsonUser);
            jsonMain.accumulate("data", jsonData);

            result = Utils.POST(getServerPath, jsonMain);
            JSONObject jObj = new JSONObject(result);
            Gson gson = new Gson();
            hireHandymanModel = gson.fromJson(jObj.toString(), HireHandymanModel.class);


        } catch (Exception e) {
            e.printStackTrace();
        }
        return hireHandymanModel;
    }
}
