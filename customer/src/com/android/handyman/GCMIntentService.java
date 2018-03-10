package com.android.handyman;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import com.android.handyman.logger.Logger;
import com.android.handyman.model.RegisterModel;
import com.android.handyman.service.Utils;
import com.google.android.gcm.GCMBaseIntentService;
import com.google.gson.Gson;

public class GCMIntentService extends GCMBaseIntentService {
	
	private static final String TAG = "GCMIntentService";
	private Intent notificationIntent;
	private SharedPreferences mSharedPreferences;
	private RegisterModel registerModel;
	
    public GCMIntentService() {
        super(Utils.SENDER_ID);
    }
    
    @Override
	protected void onRegistered(Context context, String registrationId) {
    	Logger.e(TAG, "Device registered: regId = " + registrationId);
    	mSharedPreferences = getSharedPreferences(Utils.PREF_NAME, MODE_PRIVATE);
    	
    	Utils.storeString(mSharedPreferences,Utils.REG_ID, registrationId);
		Utils.displayMessage(context, "Your device registred with GCM");
		
	}

	@Override
	protected void onUnregistered(Context context, String registrationId) {
		Logger.i(TAG, "Device unregistered" + registrationId);
		
	}

	@Override
	protected void onError(Context context, String errorId) {
		 Logger.i(TAG, "Received error: " + errorId);
		
	}

	@Override
	protected void onMessage(Context context, Intent intent) {
		Logger.i(TAG, "Received message");
		String message = intent.getExtras().getString("message");
		Utils.displayMessage(context, message);
		// notifies user
		generateNotification(context, message);
	}
	
	@Override
	protected void onDeletedMessages(Context context, int total) {
		Logger.i(TAG, "Received deleted messages notification");
		String message = getString(R.string.gcm_deleted, total);
		Utils.displayMessage(context, message);
		// notifies user
		generateNotification(context, message);
	}

	@Override
	protected boolean onRecoverableError(Context context, String errorId) {
		Logger.i(TAG, "Received recoverable error: " + errorId);
        return super.onRecoverableError(context, errorId);
	}
	
	 @SuppressWarnings("deprecation")
	private void generateNotification(Context context, String message) {
	        int icon = R.drawable.splash_logo_xxxhdpi;
	        long when = System.currentTimeMillis();
	        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
	        String msg = "";
	        int unique_id = 0;
	        String id = "", img = "", sub_category = "", service_updated_by = "", city = "", hire_status = "", appointment_date = "", discount = "", client_id = "", completed_date = "", total = "", is_deleted = "", contact_no = "", street = "", receiver_name = "", state = "", landmark = "", credit = "", lat = "", pincode = "", appointment_time = "", amount = "", address = "", lng = "", contact_person = "", is_outdated = "", job_description = "", img_path = "", comment = "", created_date = "", handyman_id = "", category = "", order_id = "", status = ""
	        		, client_name = "", client_image = "",client_image_path = "", handyman_name = "", handyman_image = "", handyman_image_path = "", handyman_email = "", handyman_rating  = "", handyman_mobile = "";
	        try {
				JSONObject jObj = new JSONObject(message.toString());
				msg = jObj.getString("message");
//				title = jObj.getString("title");
				if (!jObj.isNull("data")) {
	                JSONObject data_jobj = jObj.getJSONObject("data");
	                unique_id = Integer.parseInt(data_jobj.getString("id"));
	                
	                id = data_jobj.getString("id");
	                handyman_id = data_jobj.getString("handyman_id");
	                client_id = data_jobj.getString("client_id");
	                order_id = data_jobj.getString("order_id");
	                job_description = data_jobj.getString("job_description");
	                category = data_jobj.getString("category");
	                sub_category = data_jobj.getString("sub_category");
	                appointment_date = data_jobj.getString("appointment_date");
	                appointment_time = data_jobj.getString("appointment_time");
	                contact_person = data_jobj.getString("contact_person");
	                contact_no = data_jobj.getString("contact_no");
	                comment = data_jobj.getString("comment");
	                hire_status = data_jobj.getString("hire_status");
	                service_updated_by = data_jobj.getString("service_updated_by");
	                is_outdated = data_jobj.getString("is_outdated");
	                address = data_jobj.getString("address");
	                street = data_jobj.getString("street");
	                landmark = data_jobj.getString("landmark");
	                city = data_jobj.getString("city");
	                pincode = data_jobj.getString("pincode");
	                state = data_jobj.getString("state");
	                lat = data_jobj.getString("lat");
	                lng = data_jobj.getString("lng");
	                receiver_name = data_jobj.getString("receiver_name");
	                amount = data_jobj.getString("amount");
	                completed_date = data_jobj.getString("completed_date");
	                img = data_jobj.getString("img");
	                img_path = data_jobj.getString("img_path");
	                discount = data_jobj.getString("discount");
	                credit = data_jobj.getString("credit");
	                total = data_jobj.getString("total");
	                is_deleted = data_jobj.getString("is_deleted");
	                status = data_jobj.getString("status");
	                created_date = data_jobj.getString("created_date");
	                client_name = data_jobj.getString("client_name");
	                client_image = data_jobj.getString("client_image");
	                client_image_path = data_jobj.getString("client_image_path");
	                handyman_name = data_jobj.getString("handyman_name");
	                handyman_image = data_jobj.getString("handyman_image");
	                handyman_image_path = data_jobj.getString("handyman_image_path");
	                handyman_email = data_jobj.getString("handyman_email");
	                handyman_mobile = data_jobj.getString("handyman_mobile");
	                handyman_rating = data_jobj.getString("handyman_rating");
	                
				}
			} catch (JSONException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
	        Notification notification = new Notification(icon, msg, when);
	        
	        String title = context.getString(R.string.app_name);
	        
	        try {
	        	Gson gson = new Gson();	
	        	mSharedPreferences = getSharedPreferences(Utils.PREF_NAME, MODE_PRIVATE);
	        	if(Utils.validateString(mSharedPreferences.getString(Utils.USER_PROFILE, ""))){
	        		registerModel = gson.fromJson(mSharedPreferences.getString(Utils.USER_PROFILE, ""), RegisterModel.class);
				}
	        	
	        	if(registerModel != null){
					if(registerModel.user.user_type.equalsIgnoreCase("customer")){
						notificationIntent = new Intent(context, MainActivity.class);
						notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
						notificationIntent.setAction("com.android.handyman" + unique_id);
						notificationIntent.putExtra("FROM", "GCMIntentService");
						
						Utils.storeString(mSharedPreferences, Utils.NOTI_ID, id);
						Utils.storeString(mSharedPreferences, Utils.NOTI_HANDYMAN_ID, handyman_id);
						Utils.storeString(mSharedPreferences, Utils.NOTI_CLIENT_ID, client_id);
						Utils.storeString(mSharedPreferences, Utils.NOTI_ORDER_ID, order_id);
						Utils.storeString(mSharedPreferences, Utils.NOTI_JOB_DESCRIPTION, job_description);
						Utils.storeString(mSharedPreferences, Utils.NOTI_CATEGORY, category);
						Utils.storeString(mSharedPreferences, Utils.NOTI_SUB_CATEGORY, sub_category);
						Utils.storeString(mSharedPreferences, Utils.NOTI_APPOINTMENT_DATE, appointment_date);
						Utils.storeString(mSharedPreferences, Utils.NOTI_APPOINTMENT_TIME, appointment_time);
						Utils.storeString(mSharedPreferences, Utils.NOTI_CONTACT_PERSON, contact_person);
						Utils.storeString(mSharedPreferences, Utils.NOTI_CONTACT_NO, contact_no);
						Utils.storeString(mSharedPreferences, Utils.NOTI_COMMENT, comment);
						Utils.storeString(mSharedPreferences, Utils.NOTI_HIRE_STATUS, hire_status);
						Utils.storeString(mSharedPreferences, Utils.NOTI_SERVICE_UPDATED_BY, service_updated_by);
						Utils.storeString(mSharedPreferences, Utils.NOTI_IS_OUTDATED, is_outdated);
						Utils.storeString(mSharedPreferences, Utils.NOTI_ADDRESS, address);
						Utils.storeString(mSharedPreferences, Utils.NOTI_STREET, street);
						Utils.storeString(mSharedPreferences, Utils.NOTI_LANDMARK, landmark);
						Utils.storeString(mSharedPreferences, Utils.NOTI_CITY, city);
						Utils.storeString(mSharedPreferences, Utils.NOTI_PINCODE, pincode);
						Utils.storeString(mSharedPreferences, Utils.NOTI_STATE, state);
						Utils.storeString(mSharedPreferences, Utils.NOTI_LAT, lat);
						Utils.storeString(mSharedPreferences, Utils.NOTI_LNG, lng);
						Utils.storeString(mSharedPreferences, Utils.NOTI_RECEIVER_NAME, receiver_name);
						Utils.storeString(mSharedPreferences, Utils.NOTI_AMOUNT, amount);
						Utils.storeString(mSharedPreferences, Utils.NOTI_COMPLETED_DATE, completed_date);
						Utils.storeString(mSharedPreferences, Utils.NOTI_IMG, img_path);
						Utils.storeString(mSharedPreferences, Utils.NOTI_DISCOUNT, discount);
						Utils.storeString(mSharedPreferences, Utils.NOTI_CREDIT, credit);
						Utils.storeString(mSharedPreferences, Utils.NOTI_TOTAL, total);
						Utils.storeString(mSharedPreferences, Utils.NOTI_IS_DELETED, is_deleted);
						Utils.storeString(mSharedPreferences, Utils.NOTI_STATUS, status);
						Utils.storeString(mSharedPreferences, Utils.NOTI_CREATED_DATE, created_date);
						Utils.storeString(mSharedPreferences, Utils.NOTI_CLIENT_NAME, client_name);
						Utils.storeString(mSharedPreferences, Utils.NOTI_CLIENT_IMAGE, client_image);
						Utils.storeString(mSharedPreferences, Utils.NOTI_CLIENT_IMAGE_PATH, client_image_path);
						Utils.storeString(mSharedPreferences, Utils.NOTI_HANDYMAN_NAME, handyman_name);
						Utils.storeString(mSharedPreferences, Utils.NOTI_HANDYMAN_EMAIL, handyman_email);
						Utils.storeString(mSharedPreferences, Utils.NOTI_HANDYMAN_IMAGE, handyman_image);
						Utils.storeString(mSharedPreferences, Utils.NOTI_HANDYMAN_IMAGE_PATH, handyman_image_path);
						Utils.storeString(mSharedPreferences, Utils.NOTI_HANDYMAN_RATING, handyman_rating);
						Utils.storeString(mSharedPreferences, Utils.NOTI_HANDYMAN_MOBILE, handyman_mobile);
						
					}
				}
		        
	        	// set intent so it does not start a new activity
		        PendingIntent intent = PendingIntent.getActivity(context, 0, notificationIntent, 0);
		        notification.setLatestEventInfo(context, title, msg, intent);
		        notification.flags |= Notification.FLAG_AUTO_CANCEL;
		        // Play default notification sound
		        notification.defaults |= Notification.DEFAULT_SOUND;
		        //notification.sound = Uri.parse("android.resource://" + context.getPackageName() + "your_sound_file_name.mp3");
		        // Vibrate if vibrate is enabled
		        notification.defaults |= Notification.DEFAULT_VIBRATE;
		        notificationManager.notify(unique_id, notification);      
				
			} catch (NullPointerException e) {
				e.printStackTrace();
			}

	    }

}
