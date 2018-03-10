package com.handyman.service;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.handyman.R;
import com.handyman.logger.Logger;
import com.handyman.model.CityModel;
import com.handyman.model.DataModel;

import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class GetAllCityNameTask extends AsyncTask<String, Integer, Object> {

    public Context mContext;
    private ProgressDialog mProgressDialog;
    private AsyncCallListener mAsyncCallListener;
    private String mErrorMessage;
    private boolean mIsError = false;

    public GetAllCityNameTask(Context mContext) {
        this.mContext = mContext;
        mProgressDialog = new ProgressDialog(mContext);
        mProgressDialog.setMessage(mContext.getResources().getString(
                R.string.loading));
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
        return getCity(params[0], params[1], params[2]);
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

    private Object getCity(String cityName, String categoryId, String subCategiryId) {
        DataModel dataModel = new DataModel();
        ArrayList<CityModel> cityModels = new ArrayList<CityModel>();
        String result = "";

        JSONObject jsonMain = new JSONObject();
        JSONObject jsonData = new JSONObject();
        JSONObject jsonUser = new JSONObject();
        String getServerPath = Utils.URL_SERVER_ADDRESS + Utils.GET_CITY_NAME;

        try {
            jsonUser.accumulate("name", cityName);
            jsonUser.accumulate("category", categoryId);
            jsonUser.accumulate("sub_category", subCategiryId);
            jsonData.accumulate("city", jsonUser);
            jsonMain.accumulate("data", jsonData);

            result = Utils.POST(getServerPath, jsonMain);

            JSONObject jObj = new JSONObject(result.toString());

            dataModel.setSuccess(jObj.getString("success"));
            dataModel.setMessage(jObj.getString("message"));

            Gson gson = new Gson();
            Type listType = new TypeToken<List<CityModel>>() {}.getType();
            if (!jObj.isNull("data")) {
                cityModels = gson.fromJson(jObj.get("data").toString(), listType);

                if (cityModels.size() > 0) {
                    dataModel.setCityModels(cityModels);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return dataModel;
    }
}

