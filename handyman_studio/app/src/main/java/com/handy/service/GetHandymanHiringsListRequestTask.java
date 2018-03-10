package com.handy.service;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONObject;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.widget.Toast;

import com.handy.R;
import com.handy.logger.Logger;
import com.handy.model.DataModel;
import com.handy.model.MyHiringsModel;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

public class GetHandymanHiringsListRequestTask extends AsyncTask<String, Integer, Object> {

	public static final String TAG = "GetHandymanHiringsListRequestTask";
	public Context mContext;
//	private ProgressDialog mProgressDialog;
	private AsyncCallListener mAsyncCallListener;
	private String mErrorMessage;
//	private SharedPreferences mSharedPreferences;
	private boolean mIsError = false;
//	View mFooterView;
//	boolean loadingMore = false;

	public GetHandymanHiringsListRequestTask(Context mContext) {
		this.mContext = mContext;
//		if(Utils.flag_hiring == false){
//			mProgressDialog = new ProgressDialog(mContext);
//			mProgressDialog.setMessage(mContext.getResources().getString(R.string.loading));
//			mProgressDialog.setCanceledOnTouchOutside(false);
//			mProgressDialog.setCancelable(false);	
//		} 
//		
//		mSharedPreferences = mContext.getSharedPreferences(Utils.PREF_NAME,	mContext.MODE_PRIVATE);
//		loadingMore = true;
//        mFooterView = LayoutInflater.from(mContext).inflate(R.layout.loadmore, null);
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
		return getMyHirings(params[0],params[1],params[2]);
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

	private Object getMyHirings(String handyman_id, String status, String page) {
		
		DataModel dataModel = new DataModel();
		ArrayList<MyHiringsModel> myHiringsModels = new ArrayList<MyHiringsModel>();
		String result = "";
		
		JSONObject jsonMain = new JSONObject();
		JSONObject jsonData = new JSONObject();
		JSONObject jsonUser = new JSONObject();
		String getServerPath = Utils.URL_SERVER_ADDRESS + Utils.GET_HANDYMAN_HIRINGS;
		try {

			jsonUser.accumulate("handyman_id", handyman_id);
			jsonUser.accumulate("status", status);
			jsonUser.accumulate("page", page);
			jsonData.accumulate("hire", jsonUser);
			jsonMain.accumulate("data", jsonData);

			result = Utils.POST(getServerPath, jsonMain);
			JSONObject jObj = new JSONObject(result.toString());
			
			dataModel.setSuccess(jObj.getString("success"));
			dataModel.setMessage(jObj.getString("message"));
			
			Gson gson = new Gson();
			Type listType = new TypeToken<List<MyHiringsModel>>() {}.getType();
			if(!jObj.isNull("data"))
			myHiringsModels = gson.fromJson(jObj.get("data").toString(), listType);
			
			if(myHiringsModels.size() > 0){
				dataModel.setMyOrderList(myHiringsModels);
			}
			

		} catch (Exception e) {
			e.printStackTrace();
		}
		return dataModel;
	}
}
