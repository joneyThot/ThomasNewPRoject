package com.handyman.service;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;

import com.google.gson.Gson;
import com.handyman.R;
import com.handyman.logger.Logger;
import com.handyman.model.RegisterModel;

import org.json.JSONObject;

public class HireResendOTPRequestTask extends AsyncTask<String, Integer, Object> {

    public static final String TAG = "HireResendOTPRequestTask";
    public Context mContext;
    private ProgressDialog mProgressDialog;
    private AsyncCallListener mAsyncCallListener;
    private String mErrorMessage;
    private SharedPreferences mSharedPreferences;
    private boolean mIsError = false;

    public HireResendOTPRequestTask(Context mContext) {
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
        return otpResendCode(params[0],params[1]);
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

    private Object otpResendCode(String contact_no,String order_id) {

        RegisterModel registerModel = new RegisterModel();
        String result = "";

        JSONObject jsonMain = new JSONObject();
        JSONObject jsonData = new JSONObject();
        JSONObject jsonUser = new JSONObject();
        String getServerPath = Utils.URL_SERVER_ADDRESS + Utils.HIRE_RESEND_OTP;
        try {

            jsonUser.accumulate("contact_no", contact_no);
            jsonUser.accumulate("order_id", order_id);
            jsonData.accumulate("hire", jsonUser);
            jsonMain.accumulate("data", jsonData);

            result = Utils.POST(getServerPath, jsonMain);
            JSONObject jObj = new JSONObject(result.toString());
            Gson gson = new Gson();
            registerModel = gson.fromJson(jObj.toString(), RegisterModel.class);


        } catch (Exception e) {
            e.printStackTrace();
        }
        return registerModel;
    }
}
