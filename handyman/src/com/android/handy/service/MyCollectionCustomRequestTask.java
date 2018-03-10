package com.android.handy.service;

import java.util.ArrayList;
import java.util.Iterator;

import org.json.JSONArray;
import org.json.JSONObject;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;

import com.android.handy.logger.Logger;
import com.android.handy.model.ApplyDiscountModel;
import com.android.handy.model.MyCollectionModel;
import com.android.handy.model.MyCollectionModelList;

public class MyCollectionCustomRequestTask extends AsyncTask<String, Integer, Object> {

	public static final String TAG = "MyCollectionCustomRequestTask";
	public Context mContext;
//	private ProgressDialog mProgressDialog;
	private AsyncCallListener mAsyncCallListener;
	private String mErrorMessage;
	private SharedPreferences mSharedPreferences;
	private boolean mIsError = false;

	public MyCollectionCustomRequestTask(Context mContext) {
		this.mContext = mContext;
//		mProgressDialog = new ProgressDialog(mContext);
//		mProgressDialog.setMessage(mContext.getResources().getString(R.string.loading));
//		mProgressDialog.setCanceledOnTouchOutside(false);
//		mProgressDialog.setCancelable(false);
		mSharedPreferences = mContext.getSharedPreferences(Utils.PREF_NAME,	mContext.MODE_PRIVATE);
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
		return my_collection(params[0], params[1],params[2],params[3],params[4]);
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

	@SuppressWarnings("rawtypes")
	private Object my_collection(String handyman_id, String mode, String start_date, String end_date, String page) {
		
		MyCollectionModelList myCollectionModelList = new MyCollectionModelList();
		ArrayList<MyCollectionModel> collectionModels = null;
		String temp_date = "";
		String result = "";

		JSONObject jsonMain = new JSONObject();
		JSONObject jsonData = new JSONObject();
		JSONObject jsonUser = new JSONObject();
		
		String getServerPath = Utils.URL_SERVER_ADDRESS + Utils.GET_HM_COLLECTION;
		try {

			jsonUser.accumulate("handyman_id", handyman_id);
			jsonUser.accumulate("mode", mode);
			jsonUser.accumulate("start_date", start_date);
			jsonUser.accumulate("end_date", end_date);
			jsonUser.accumulate("page", page);
			jsonData.accumulate("hire", jsonUser);
			jsonMain.accumulate("data", jsonData);

			result = Utils.POST(getServerPath, jsonMain);
			JSONObject jObj = new JSONObject(result.toString());
			
			myCollectionModelList.setSuccess(jObj.getString("success"));
			myCollectionModelList.setMessage(jObj.getString("message"));
			
			if(!jObj.isNull("data")){
				JSONObject data_jObj = jObj.getJSONObject("data");
				collectionModels = new ArrayList<MyCollectionModel>();
				Iterator iterator_data = data_jObj.keys();
				while (iterator_data.hasNext()) {
					String key = (String) iterator_data.next();
					if (Utils.validateString(key) && key.equalsIgnoreCase("totalamount")) {
                        JSONArray jsonArray = data_jObj.getJSONArray(key);
                        	 for (int k = 0; k < jsonArray.length(); k++) {
 	                        	JSONObject totalamount_jobj = jsonArray.getJSONObject(k);
 	                        	myCollectionModelList.setTotalamount(totalamount_jobj.getString("totalamount"));
                        	 }
            				
                        } else {
//	                        for (int k = 0; k < jsonArray.length(); k++) {
	                        	
	                        	MyCollectionModel collectionModel = new MyCollectionModel();	                        	
//	                        	JSONObject all_jobj = jsonArray.getJSONObject(k);
	                        	JSONObject all_jobj = data_jObj.getJSONObject(key);
	                        	
	                        	collectionModel.setId(all_jobj.getString("id"));
	                        	collectionModel.setHandyman_id(all_jobj.getString("handyman_id"));
	                        	collectionModel.setClient_id(all_jobj.getString("client_id"));
	                        	collectionModel.setOrder_id(all_jobj.getString("order_id"));
	                        	collectionModel.setJob_description(all_jobj.getString("job_description"));
	                        	collectionModel.setCategory(all_jobj.getString("category"));
	                        	collectionModel.setSub_category(all_jobj.getString("sub_category"));
	                        	collectionModel.setAppointment_date(all_jobj.getString("appointment_date"));
	                        	collectionModel.setAppointment_time(all_jobj.getString("appointment_time"));
	                        	collectionModel.setContact_person(all_jobj.getString("contact_person"));
	                        	collectionModel.setContact_no(all_jobj.getString("contact_no"));
	                        	collectionModel.setComment(all_jobj.getString("comment"));
	                        	collectionModel.setHire_status(all_jobj.getString("hire_status"));
	                        	collectionModel.setService_updated_by(all_jobj.getString("service_updated_by"));
	                        	collectionModel.setIs_outdated(all_jobj.getString("is_outdated"));
	                        	collectionModel.setAddress(all_jobj.getString("address"));
	                        	collectionModel.setStreet(all_jobj.getString("street"));
	                        	collectionModel.setLandmark(all_jobj.getString("landmark"));
	                        	collectionModel.setCity(all_jobj.getString("city"));
	                        	collectionModel.setPincode(all_jobj.getString("pincode"));
	                        	collectionModel.setState(all_jobj.getString("state"));
	                        	collectionModel.setLat(all_jobj.getString("lat"));
	                        	collectionModel.setLng(all_jobj.getString("lng"));
	                        	collectionModel.setReceiver_name(all_jobj.getString("receiver_name"));
	                        	collectionModel.setAmount(all_jobj.getString("amount"));
	                        	collectionModel.setDiscount(all_jobj.getString("discount"));
	                        	collectionModel.setCredit(all_jobj.getString("credit"));
	                        	collectionModel.setTotal(all_jobj.getString("total"));
	                        	collectionModel.setIs_deleted(all_jobj.getString("is_deleted"));
	                        	collectionModel.setStatus(all_jobj.getString("status"));
	                        	collectionModel.setCreated_date(all_jobj.getString("created_date"));
	                        	collectionModel.setModified_date(all_jobj.getString("modified_date"));
	                        	collectionModel.setClient_name(all_jobj.getString("client_name"));
	                        	collectionModel.setUser_img(all_jobj.getString("user_img"));
	                        	collectionModel.setUser_img_path(all_jobj.getString("user_img_path"));
	                        	collectionModel.setCategory_name(all_jobj.getString("category_name"));
	                        	collectionModel.setSubcategory_name(all_jobj.getString("subcategory_name"));
	                        	collectionModel.setCity_name(all_jobj.getString("city_name"));
	                        	
	                        	String main_date = all_jobj.getString("date_format");
	                        	
//	                        	if (key.equalsIgnoreCase("0")) {
//	                        		MyCollectionModel date = new MyCollectionModel();
//	                        		date.setDateMain(all_jobj.getString("appointment_date"));
//	                                temp_date = main_date;
//	                                collectionModels.add(date);
//	                            }
	                        	
	                        	 if (temp_date.equals(main_date)) {
	                                 Logger.d("no display-->", "");
	                             } else {
	                            	 MyCollectionModel date = new MyCollectionModel();
		                        	 date.setDateMain(all_jobj.getString("date_format"));
		                             temp_date = main_date;
		                             collectionModels.add(date);
	                             }
	                        	
	                        	collectionModels.add(collectionModel);
//	                        }
                        }
                        
                        if(collectionModels.size() > 0){
                        	myCollectionModelList.setCollectionModelsList(collectionModels);
                        }
                        
					}
//				}
				
			}			

		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return myCollectionModelList;
	}
}

/*if(!jObj.isNull("data")){
				JSONObject data_jObj = jObj.getJSONObject("data");
				collectionModels = new ArrayList<MyCollectionModel>();
				Iterator iterator_data = data_jObj.keys();
				while (iterator_data.hasNext()) {
					String key = (String) iterator_data.next();
					if (Utils.validateString(key)) {
                        JSONArray jsonArray = data_jObj.getJSONArray(key);
                        if(key.equalsIgnoreCase("totalamount")){
                        	 for (int k = 0; k < jsonArray.length(); k++) {
 	                        	JSONObject totalamount_jobj = jsonArray.getJSONObject(k);
 	                        	myCollectionModelList.setTotalamount(totalamount_jobj.getString("totalamount"));
                        	 }
            				
                        } else {
//                        	MyCollectionModel collectionModel = new MyCollectionModel();
//                        	collectionModel.setDateMain(key);
                        	myCollectionModelList.setDate(key);
	                        for (int k = 0; k < jsonArray.length(); k++) {
	                        	JSONObject all_jobj = jsonArray.getJSONObject(k);
	                        	MyCollectionModel collectionModel = new MyCollectionModel();
	                        	
	                        	collectionModel.setId(all_jobj.getString("id"));
	                        	collectionModel.setHandyman_id(all_jobj.getString("handyman_id"));
	                        	collectionModel.setClient_id(all_jobj.getString("client_id"));
	                        	collectionModel.setOrder_id(all_jobj.getString("order_id"));
	                        	collectionModel.setJob_description(all_jobj.getString("job_description"));
	                        	collectionModel.setCategory(all_jobj.getString("category"));
	                        	collectionModel.setSub_category(all_jobj.getString("sub_category"));
	                        	collectionModel.setAppointment_date(all_jobj.getString("appointment_date"));
	                        	collectionModel.setAppointment_time(all_jobj.getString("appointment_time"));
	                        	collectionModel.setContact_person(all_jobj.getString("contact_person"));
	                        	collectionModel.setContact_no(all_jobj.getString("contact_no"));
	                        	collectionModel.setComment(all_jobj.getString("comment"));
	                        	collectionModel.setHire_status(all_jobj.getString("hire_status"));
	                        	collectionModel.setService_updated_by(all_jobj.getString("service_updated_by"));
	                        	collectionModel.setIs_outdated(all_jobj.getString("is_outdated"));
	                        	collectionModel.setAddress(all_jobj.getString("address"));
	                        	collectionModel.setStreet(all_jobj.getString("street"));
	                        	collectionModel.setLandmark(all_jobj.getString("landmark"));
	                        	collectionModel.setCity(all_jobj.getString("city"));
	                        	collectionModel.setPincode(all_jobj.getString("pincode"));
	                        	collectionModel.setState(all_jobj.getString("state"));
	                        	collectionModel.setLat(all_jobj.getString("lat"));
	                        	collectionModel.setLng(all_jobj.getString("lng"));
	                        	collectionModel.setReceiver_name(all_jobj.getString("receiver_name"));
	                        	collectionModel.setAmount(all_jobj.getString("amount"));
	                        	collectionModel.setDiscount(all_jobj.getString("discount"));
	                        	collectionModel.setCredit(all_jobj.getString("credit"));
	                        	collectionModel.setTotal(all_jobj.getString("total"));
	                        	collectionModel.setIs_deleted(all_jobj.getString("is_deleted"));
	                        	collectionModel.setStatus(all_jobj.getString("status"));
	                        	collectionModel.setCreated_date(all_jobj.getString("created_date"));
	                        	collectionModel.setModified_date(all_jobj.getString("modified_date"));
	                        	collectionModel.setClient_name(all_jobj.getString("client_name"));
	                        	collectionModel.setUser_img(all_jobj.getString("user_img"));
	                        	collectionModel.setUser_img_path(all_jobj.getString("user_img_path"));
	                        	collectionModel.setCategory(all_jobj.getString("category_name"));
	                        	collectionModel.setSub_category(all_jobj.getString("subcategory_name"));
	                        	
	                        	String main_date = all_jobj.getString("appointment_date");
	                        	
	                        	if (k == 0) {
	                        		MyCollectionModel date = new MyCollectionModel();
	                        		date.setDateMain(all_jobj.getString("appointment_date"));
	                                temp_date = main_date;
	                                collectionModels.add(date);
	                            }
	                        	
	                        	 if (temp_date.equals(main_date)) {
	                                 Logger.d("no display-->", "");
	                             } else {
	                            	 MyCollectionModel date = new MyCollectionModel();
		                        	 date.setDateMain(all_jobj.getString("appointment_date"));
		                             temp_date = main_date;
		                             collectionModels.add(date);
	                             }
	                        	 
	                        	collectionModels.add(collectionModel);
	                        }
                        }
                        
                        if(collectionModels.size() > 0){
                        	myCollectionModelList.setCollectionModelsList(collectionModels);
                        }
                        
					}
				}*/
