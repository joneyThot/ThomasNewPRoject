package com.handyman.service;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;

import com.handyman.R;
import com.handyman.logger.Logger;
import com.handyman.model.CategoryListModel;
import com.handyman.model.DataModel;
import com.handyman.model.HandymanModel;
import com.handyman.model.ProfileHandymanModel;
import com.handyman.model.RegisterModel;
import com.handyman.model.StateModel;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

public class GetProfileHandymanListRequestTask extends AsyncTask<String, Integer, Object> {

    public static final String TAG = "GetProfileHandymanListRequestTask";
    public Context mContext;
    private ProgressDialog mProgressDialog;
    private AsyncCallListener mAsyncCallListener;
    private String mErrorMessage;
    private SharedPreferences mSharedPreferences;
    private boolean mIsError = false;

    public GetProfileHandymanListRequestTask(Context mContext) {
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
        return getProfileHandyman(params[0], params[1], params[2], params[3]);
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

    private Object getProfileHandyman(String handyman_id, String client_id, String category, String sub_category) {

//		ArrayList<ProfileHandymanModel> profileHandymanList = new ArrayList<ProfileHandymanModel>();
        DataModel dataModel = new DataModel();
        ArrayList<ProfileHandymanModel> profileHandymanList = new ArrayList<ProfileHandymanModel>();
        String result = "";

        JSONObject jsonMain = new JSONObject();
        JSONObject jsonData = new JSONObject();
        JSONObject jsonUser = new JSONObject();
        String getServerPath = Utils.URL_SERVER_ADDRESS + Utils.GET_PROFILE_HANDYMAN_LIST;
        try {

            jsonUser.accumulate("handyman_id", handyman_id);
            jsonUser.accumulate("client_id", client_id);
            jsonUser.accumulate("category", category);
            jsonUser.accumulate("sub_category", sub_category);
            jsonData.accumulate("users", jsonUser);
            jsonMain.accumulate("data", jsonData);

            result = Utils.POST(getServerPath, jsonMain);
            JSONObject jObj = new JSONObject(result.toString());

            dataModel.setSuccess(jObj.getString("success"));
            dataModel.setMessage(jObj.getString("message"));
            if (!jObj.isNull("data")) {
                Gson gson = new Gson();
                Type listType = new TypeToken<List<ProfileHandymanModel>>() {
                }.getType();
                profileHandymanList = gson.fromJson(jObj.get("data").toString(), listType);
            }
                /*JSONArray jArray = jObj.getJSONArray("data");
				profileHandymanList = new ArrayList<ProfileHandymanModel>();
				
				for (int i = 0; i < jArray.length(); i++) {
					JSONObject data_jobj = jArray.getJSONObject(i);
					ProfileHandymanModel profileHandymanModel = new ProfileHandymanModel();
					profileHandymanModel.setId(data_jobj.getString("id"));
					profileHandymanModel.setMobile(data_jobj.getString("mobile"));
					profileHandymanModel.setFirstname(data_jobj.getString("firstname"));
					profileHandymanModel.setLastname(data_jobj.getString("lastname"));
					profileHandymanModel.setGender(data_jobj.getString("gender"));
					profileHandymanModel.setUser_type(data_jobj.getString("user_type"));
					profileHandymanModel.setJob_list(data_jobj.getString("job_list"));
					profileHandymanModel.setDob(data_jobj.getString("dob"));
					profileHandymanModel.setEmail(data_jobj.getString("email"));
					profileHandymanModel.setWhatsapp_id(data_jobj.getString("whatsapp_id"));
					profileHandymanModel.setPassword(data_jobj.getString("password"));
					profileHandymanModel.setQualification(data_jobj.getString("qualification"));
					profileHandymanModel.setExperience(data_jobj.getString("experience"));
					profileHandymanModel.setProvider(data_jobj.getString("provider"));
					profileHandymanModel.setAddress(data_jobj.getString("address"));
					profileHandymanModel.setStreet(data_jobj.getString("street"));
					profileHandymanModel.setLandmark(data_jobj.getString("landmark"));
					profileHandymanModel.setCity(data_jobj.getString("city"));
					profileHandymanModel.setPincode(data_jobj.getString("pincode"));
					profileHandymanModel.setState(data_jobj.getString("state"));
					profileHandymanModel.setWebsite(data_jobj.getString("website"));
					profileHandymanModel.setLat(data_jobj.getString("lat"));
					profileHandymanModel.setLng(data_jobj.getString("lng"));
					profileHandymanModel.setImg(data_jobj.getString("img"));
					profileHandymanModel.setImg_path(data_jobj.getString("img_path"));
					profileHandymanModel.setIs_deleted(data_jobj.getString("is_deleted"));
					profileHandymanModel.setStatus(data_jobj.getString("status"));
					profileHandymanModel.setCreated_date(data_jobj.getString("created_date"));
					profileHandymanModel.setModified_date(data_jobj.getString("modified_date"));
					profileHandymanModel.setCity_name(data_jobj.getString("city_name"));
					profileHandymanModel.setState_name(data_jobj.getString("state_name"));
					profileHandymanModel.setRating(data_jobj.getString("rating"));
					profileHandymanModel.setLike(data_jobj.getString("like"));
					
					profileHandymanList.add(profileHandymanModel);
					
				}*/
            if (!profileHandymanList.isEmpty() && profileHandymanList.size() > 0) {
                dataModel.setProfileHandymanModels(profileHandymanList);
            }
//			}


        } catch (Exception e) {
            e.printStackTrace();
        }
        return dataModel;
    }
}
