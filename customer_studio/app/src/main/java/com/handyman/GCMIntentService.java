//package com.handyman;
//
//import org.json.JSONException;
//import org.json.JSONObject;
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
//import android.support.v7.app.NotificationCompat;
//
//import com.handyman.crop.Util;
//import com.handyman.logger.Logger;
//import com.handyman.model.RegisterModel;
//import com.handyman.service.Utils;
//import com.google.android.gcm.GCMBaseIntentService;
//import com.google.gson.Gson;
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
//        int icon = R.drawable.splash_logo_xxxhdpi;
//        long when = System.currentTimeMillis();
//        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
//        String msg = "", hire_status = "", banner_path = "", user_type = "", banner_id = "", coin_text = "";
//        int unique_id = 0;
//        JSONObject jObj = null;
//        Bitmap banner_picture = null;
//        NotificationCompat.BigPictureStyle notiStyle = null;
//        try {
//            jObj = new JSONObject(message.toString());
//            msg = jObj.getString("message");
//            if (!jObj.isNull("data")) {
//                JSONObject data_jobj = jObj.getJSONObject("data");
//                unique_id = Integer.parseInt(data_jobj.getString("id"));
//
//                if(!data_jobj.isNull("banner_id")){
//                    banner_id = data_jobj.getString("banner_id");
//                }
//
//                if (!data_jobj.isNull("banner_path")) {
//                    banner_path = data_jobj.getString("banner_path");
//                }
//
//                if (!data_jobj.isNull("hire_status")) {
//                    hire_status = data_jobj.getString("hire_status");
//                }
//
//                if (!data_jobj.isNull("user_type")) {
//                    user_type = data_jobj.getString("user_type");
//                }
//
//                if (!data_jobj.isNull("coin_text")) {
//                    coin_text = data_jobj.getString("coin_text");
//                }
//            }
//        } catch (JSONException e1) {
//            // TODO Auto-generated catch block
//            e1.printStackTrace();
//        }
//
//        Notification notification = new Notification(icon, msg, when);
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
//                if (registerModel.user.user_type.equalsIgnoreCase("customer")) {
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
//                    if (!Utils.validateString(banner_path)) {
//                        Utils.storeString(mSharedPreferences, Utils.NOTI_HIRE_STATUS, hire_status);
//                        Utils.storeString(mSharedPreferences, Utils.NOTI_ADVERTISE, "");
//                    } else {
//                        Utils.storeString(mSharedPreferences, Utils.NOTI_ADVERTISE, Utils.NOTI_ADVERTISE);
//                    }
//
//                    if (!Utils.validateString(banner_id)) {
//                        Utils.storeString(mSharedPreferences, Utils.NOTI_HIRE_STATUS, hire_status);
//                        Utils.storeString(mSharedPreferences, Utils.NOTI_ADVERTISE, "");
//                    } else {
//                        Utils.storeString(mSharedPreferences, Utils.NOTI_ADVERTISE, Utils.NOTI_ADVERTISE);
//                    }
//
//                    if (Utils.validateString(user_type)) {
//                        Utils.storeString(mSharedPreferences, Utils.NOTI_ADVERTISE, "");
//                        Utils.storeString(mSharedPreferences, Utils.NOTI_HIRE_STATUS, "");
//                    }
//
//                    if (Utils.validateString(coin_text) && coin_text.equalsIgnoreCase("start_text")) {
//                        Utils.storeString(mSharedPreferences, Utils.NOTI_ADVERTISE, "");
//                        Utils.storeString(mSharedPreferences, Utils.NOTI_HIRE_STATUS, "");
//                        Utils.storeString(mSharedPreferences, Utils.NOTI_COINS, coin_text);
//                    } else {
//                        Utils.storeString(mSharedPreferences, Utils.NOTI_COINS, "");
//                    }
//
//                }
//            } else if (Utils.validateString(banner_id) || Utils.validateString(banner_path)) {
//
//                notificationIntent = new Intent(context, SplashActivity.class);
//
//                notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
//                notificationIntent.setAction("com.handyman" + unique_id);
//                notificationIntent.putExtra("FROM", "GCMIntentService");
//                notificationIntent.putExtra("response", jObj.toString());
//
//                Utils.storeString(mSharedPreferences, Utils.NOTI_HIRE_STATUS, "");
//                Utils.storeString(mSharedPreferences, Utils.NOTI_ADVERTISE, Utils.NOTI_ADVERTISE);
//                Utils.storeString(mSharedPreferences, Utils.NOTI_COINS, "");
//
//            } else if(Utils.validateString(hire_status) && hire_status.equalsIgnoreCase("declined")){
//
//                notificationIntent = new Intent(context, SplashActivity.class);
//
//                notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
//                notificationIntent.setAction("com.handyman" + unique_id);
//                notificationIntent.putExtra("FROM", "GCMIntentService");
//                notificationIntent.putExtra("response", jObj.toString());
//
//                Utils.storeString(mSharedPreferences, Utils.NOTI_HIRE_STATUS, hire_status);
//                Utils.storeString(mSharedPreferences, Utils.NOTI_ADVERTISE, "");
//                Utils.storeString(mSharedPreferences, Utils.NOTI_COINS, "");
//
//            }
//
//            if (Utils.validateString(banner_path)) {
//                notiStyle = new NotificationCompat.BigPictureStyle();
//                notiStyle.setSummaryText(msg);
//
//                try {
//                    banner_picture = BitmapFactory.decodeStream((InputStream) new URL(Utils.IMAGE_URL + banner_path).getContent());
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//                notiStyle.bigPicture(banner_picture);
//            }
//
//            PendingIntent intent = PendingIntent.getActivity(context, 0, notificationIntent, PendingIntent.FLAG_CANCEL_CURRENT);
//
//            NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context);
//            if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//                mBuilder.setSmallIcon(R.drawable.small_logo);
//                mBuilder.setColor(Color.parseColor("#D74F4F"));
//            } else {
//                mBuilder.setSmallIcon(icon);
//            }
//
//            mBuilder.setTicker(title).setWhen(when);
//            mBuilder.setAutoCancel(true);
//            mBuilder.setContentTitle(title);
//            mBuilder.setContentIntent(intent);
//            mBuilder.setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION));
//            mBuilder.setLargeIcon(BitmapFactory.decodeResource(context.getResources(), icon));
//            mBuilder.setContentText(msg);
//            mBuilder.setPriority(Notification.PRIORITY_MAX);
//            if (Utils.validateString(banner_path)) {
//                mBuilder.setStyle(notiStyle);
//            } else {
//                mBuilder.setStyle(new NotificationCompat.BigTextStyle().bigText(msg));
//            }
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
