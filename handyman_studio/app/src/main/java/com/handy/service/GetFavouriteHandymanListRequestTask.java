package com.handy.service;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONObject;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;

import com.handy.R;
import com.handy.logger.Logger;
import com.handy.model.FavouriteHandymanModel;
import com.handy.model.SubCategoryModel;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

public class GetFavouriteHandymanListRequestTask extends AsyncTask<String, Integer, Object> {

	public static final String TAG = "GetFavouriteHandymanListRequestTask";
	public Context mContext;
	private ProgressDialog mProgressDialog;
	private AsyncCallListener mAsyncCallListener;
	private String mErrorMessage;
	private SharedPreferences mSharedPreferences;
	private boolean mIsError = false;

	public GetFavouriteHandymanListRequestTask(Context mContext) {
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
		return favorite_handyman_list(params[0]);
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

	private Object favorite_handyman_list(String client_id) {

		ArrayList<FavouriteHandymanModel> favouriteHandymanModel = new ArrayList<FavouriteHandymanModel>();
		ArrayList<SubCategoryModel> subCategoryModelsList = null;
		String result = "";
		
		JSONObject jsonMain = new JSONObject();
		JSONObject jsonData = new JSONObject();
		JSONObject jsonUser = new JSONObject();
		String getServerPath = Utils.URL_SERVER_ADDRESS + Utils.GET_FAVOURITE_HANDYMAN_LIST;
		try {

			jsonUser.accumulate("client_id", client_id);
			jsonData.accumulate("favourite", jsonUser);
			jsonMain.accumulate("data", jsonData);

			result = Utils.POST(getServerPath, jsonMain);
			JSONObject jObj = new JSONObject(result.toString());
			
			Gson gson = new Gson();
			Type listType = new TypeToken<List<FavouriteHandymanModel>>() {}.getType();
			favouriteHandymanModel = gson.fromJson(jObj.get("data").toString(), listType);
			
			/*JSONArray subcategory_jArray = jObj.getJSONArray("sub_category_id");
			subCategoryModelsList = new ArrayList<SubCategoryModel>();
            for (int k = 0; k < subcategory_jArray.length(); k++) {
            	JSONObject subcategory_jobj = subcategory_jArray.getJSONObject(k);
                SubCategoryModel subCategoryModel = new SubCategoryModel();
                if (!subcategory_jobj.isNull("sub_category_id")) {
                	subCategoryModel.setSub_category_id(subcategory_jobj.getString("sub_category_id"));
                }
                if (!subcategory_jobj.isNull("sub_category_name")) {
                	subCategoryModel.setSub_category_name(subcategory_jobj.getString("sub_category_name"));
                }
                subCategoryModelsList.add(subCategoryModel);
            }*/
            
            

		} catch (Exception e) {
			e.printStackTrace();
		}
		return favouriteHandymanModel;
	}
}
