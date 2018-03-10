//package com.handy;
//
//import org.json.JSONException;
//import org.json.JSONObject;
//
//import com.handy.logger.Logger;
//import com.handy.model.RegisterModel;
//import com.handy.service.Utils;
//import com.google.android.gcm.GCMBaseIntentService;
//import com.google.gson.Gson;
//
//import android.app.Notification;
//import android.app.NotificationManager;
//import android.app.PendingIntent;
//import android.content.Context;
//import android.content.Intent;
//import android.content.SharedPreferences;
//import android.graphics.Bitmap;
//import android.graphics.BitmapFactory;
//import android.graphics.Color;
//import android.location.LocationManager;
//import android.media.RingtoneManager;
//import android.os.Build;
//import android.support.v4.app.NotificationCompat;
//
//import java.io.IOException;
//import java.io.InputStream;
//import java.net.URL;
//
//public class GCMIntentService extends GCMBaseIntentService {
//
//    private static final String TAG = "GCMIntentService";
//    private Intent notificationIntent;
//    private SharedPreferences mSharedPreferences;
//    private RegisterModel registerModel;
//
//    public GCMIntentService() {
//        super(Utils.SENDER_ID);
//    }
//
//    @Override
//    protected void onRegistered(Context context, String registrationId) {
//        Logger.e(TAG, "Device registered: regId = " + registrationId);
//        mSharedPreferences = getSharedPreferences(Utils.PREF_NAME, MODE_PRIVATE);
//
//        Utils.storeString(mSharedPreferences, Utils.REG_ID, registrationId);
//        Utils.displayMessage(context, "Your device registred with GCM");
//
//    }
//
//    @Override
//    protected void onUnregistered(Context context, String registrationId) {
//        Logger.i(TAG, "Device unregistered" + registrationId);
//
//    }
//
//    @Override
//    protected void onError(Context context, String errorId) {
//        Logger.i(TAG, "Received error: " + errorId);
//
//    }
//
//    @Override
//    protected void onMessage(Context context, Intent intent) {
//        Logger.i(TAG, "Received message");
//        String message = intent.getExtras().getString("message");
//        Utils.displayMessage(context, message);
//        // notifies user
//        generateNotification(context, message);
//    }
//
//    @Override
//    protected void onDeletedMessages(Context context, int total) {
//        Logger.i(TAG, "Received deleted messages notification");
//        String message = getString(R.string.gcm_deleted, total);
//        Utils.displayMessage(context, message);
//        // notifies user
//        generateNotification(context, message);
//    }
//
//    @Override
//    protected boolean onRecoverableError(Context context, String errorId) {
//        Logger.i(TAG, "Received recoverable error: " + errorId);
//        return super.onRecoverableError(context, errorId);
//    }
//
//    @SuppressWarnings("deprecation")
//    private void generateNotification(Context context, String message) {
//        int icon = R.drawable.logo_partner_xxxhdpi;
//        long when = System.currentTimeMillis();
//        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
//        String msg = "", hire_status = "", is_requested = "";
//        int unique_id = 0;
//        JSONObject jObj = null;
//        try {
//            jObj = new JSONObject(message.toString());
//            msg = jObj.getString("message");
//            if (!jObj.isNull("data")) {
//                JSONObject data_jobj = jObj.getJSONObject("data");
//                unique_id = Integer.parseInt(data_jobj.getString("id"));
//                if (!data_jobj.has("is_requested")) {
//                    hire_status = data_jobj.getString("hire_status");
//                }
//
//            }
//        } catch (JSONException e1) {
//            // TODO Auto-generated catch block
//            e1.printStackTrace();
//        }
//        Notification notification = new Notification(icon, msg, when);
//
//        String title = context.getString(R.string.app_name);
//
//        try {
//            Gson gson = new Gson();
//            mSharedPreferences = getSharedPreferences(Utils.PREF_NAME, MODE_PRIVATE);
//            if (Utils.validateString(mSharedPreferences.getString(Utils.USER_PROFILE, ""))) {
//                registerModel = gson.fromJson(mSharedPreferences.getString(Utils.USER_PROFILE, ""), RegisterModel.class);
//            }
//
//            if (registerModel != null) {
//                if (registerModel.user.user_type.equalsIgnoreCase("handyman")) {
//                    LocationManager manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
//                    boolean statusOfGPS = manager.isProviderEnabled(LocationManager.GPS_PROVIDER);
//                    if (statusOfGPS) {
//                        notificationIntent = new Intent(context, MainActivity.class);
//                    } else {
//                        notificationIntent = new Intent(context, SplashActivity.class);
//                    }
//                    notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
//                    notificationIntent.setAction("com.handyman" + unique_id);
//                    notificationIntent.putExtra("FROM", "GCMIntentService");
//                    notificationIntent.putExtra("response", jObj.toString());
//
//                    Utils.storeString(mSharedPreferences, Utils.NOTI_HIRE_STATUS, hire_status);
//                }
//            } else if (Utils.validateString(hire_status) && hire_status.equalsIgnoreCase("declined")) {
//
//                notificationIntent = new Intent(context, SplashActivity.class);
//
//                notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
//                notificationIntent.setAction("com.handyman" + unique_id);
//                notificationIntent.putExtra("FROM", "GCMIntentService");
//                notificationIntent.putExtra("response", jObj.toString());
//
//                Utils.storeString(mSharedPreferences, Utils.NOTI_HIRE_STATUS, hire_status);
//            }
//
//            // set intent so it does not start a new activity
//            PendingIntent intent = PendingIntent.getActivity(context, 0, notificationIntent, PendingIntent.FLAG_CANCEL_CURRENT);
//
//            NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context);
//
//            if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//                mBuilder.setSmallIcon(R.drawable.small_logo);
//                mBuilder.setColor(Color.parseColor("#D74F4F"));
//            } else {
//                mBuilder.setSmallIcon(icon);
//            }
//            mBuilder.setTicker(title).setWhen(when);
//            mBuilder.setAutoCancel(true);
//            mBuilder.setContentTitle(title);
//            mBuilder.setStyle(new NotificationCompat.BigTextStyle().bigText(msg));
//            mBuilder.setContentIntent(intent);
//            mBuilder.setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION));
//            mBuilder.setLargeIcon(BitmapFactory.decodeResource(context.getResources(), icon));
//            mBuilder.setContentText(msg);
//            mBuilder.setPriority(Notification.PRIORITY_MAX);
//
//            Notification noti = mBuilder.build();
//            notificationManager.notify(0, noti);
//
//        } catch (NullPointerException e) {
//            e.printStackTrace();
//        }
//
//    }
//
//}
//
