package com.handy.FCM;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.location.LocationManager;
import android.media.RingtoneManager;
import android.os.Build;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.google.gson.Gson;
import com.handy.MainActivity;
import com.handy.R;
import com.handy.SplashActivity;
import com.handy.logger.Logger;
import com.handy.model.RegisterModel;
import com.handy.service.Utils;

import org.json.JSONException;
import org.json.JSONObject;


public class MyFirebaseMessagingService extends FirebaseMessagingService {

    private static final String TAG = MyFirebaseMessagingService.class.getSimpleName();
    private Intent notificationIntent;
    private SharedPreferences mSharedPreferences;
    private RegisterModel registerModel;

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        Log.e(TAG, "From: " + remoteMessage.getFrom());
        mSharedPreferences = getSharedPreferences(Utils.PREF_NAME, MODE_PRIVATE);

        if (remoteMessage == null)
            return;

        String message = remoteMessage.getData().toString();
        // Check if message contains a notification payload.
        if (remoteMessage.getNotification() != null) {
            Logger.e(TAG, "Notification Body: " + remoteMessage.getNotification().getBody());
            if (!Utils.isAppIsInBackground(getApplicationContext())) {
                // app is in foreground, broadcast the push message
                Intent pushNotification = new Intent(Utils.PUSH_NOTIFICATION);
                pushNotification.putExtra("message", message);
                LocalBroadcastManager.getInstance(this).sendBroadcast(pushNotification);

            } else {
                // If the app is in background, firebase itself handles the notification
            }
        }

        // Check if message contains a data payload.
        if (remoteMessage.getData().size() > 0) {
            Logger.e(TAG, "Data Payload: " + remoteMessage.getData().toString());

            try {
                JSONObject json = new JSONObject(remoteMessage.getData().toString());
                handleDataMessage(getApplicationContext(),json);
            } catch (Exception e) {
                Log.e(TAG, "Exception: " + e.getMessage());
            }

//            handleDataMessage(getApplicationContext(), message);
        }

       /* if (remoteMessage.getNotification() != null) {
            Logger.e(TAG, "Notification Body: " + remoteMessage.getNotification().getBody());
            String message = remoteMessage.getData().toString();
//            Utils.displayMessage(getApplicationContext(), message);

            Intent pushNotification = new Intent(Utils.PUSH_NOTIFICATION);
            pushNotification.putExtra("message", message);
            LocalBroadcastManager.getInstance(this).sendBroadcast(pushNotification);

            handleDataMessage(getApplicationContext(), message);
        }*/

    }

    @Override
    public void onDeletedMessages() {
        super.onDeletedMessages();
        Logger.i(TAG, "Received deleted messages notification");
    }

    @Override
    public void onMessageSent(String s) {
        super.onMessageSent(s);
    }

    @Override
    public void onSendError(String s, Exception e) {
        super.onSendError(s, e);
        Logger.i(TAG, "Received recoverable error: " + s);
    }

    private void handleDataMessage(Context context, JSONObject json) {
        int icon = R.drawable.logo_partner_xxxhdpi;
        long when = System.currentTimeMillis();
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        String msg = "", hire_status = "", is_requested = "";
        int unique_id = 0;
        JSONObject jObj = null;
        try {
            jObj = json.getJSONObject("message");
            msg = jObj.getString("message");
            if (!jObj.isNull("data")) {
                JSONObject data_jobj = jObj.getJSONObject("data");
                unique_id = Integer.parseInt(data_jobj.getString("id"));
                if (!data_jobj.has("is_requested")) {
                    hire_status = data_jobj.getString("hire_status");
                }

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
            if (Utils.validateString(mSharedPreferences.getString(Utils.USER_PROFILE, ""))) {
                registerModel = gson.fromJson(mSharedPreferences.getString(Utils.USER_PROFILE, ""), RegisterModel.class);
            }

            if (registerModel != null) {
                if (registerModel.user.user_type.equalsIgnoreCase("handyman")) {
                    LocationManager manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
                    boolean statusOfGPS = manager.isProviderEnabled(LocationManager.GPS_PROVIDER);
                    if (statusOfGPS) {
                        notificationIntent = new Intent(context, MainActivity.class);
                    } else {
                        notificationIntent = new Intent(context, SplashActivity.class);
                    }
                    notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                    notificationIntent.setAction("com.handyman" + unique_id);
                    notificationIntent.putExtra("FROM", "GCMIntentService");
                    notificationIntent.putExtra("response", jObj.toString());

                    Utils.storeString(mSharedPreferences, Utils.NOTI_HIRE_STATUS, hire_status);
                }
            } else if (Utils.validateString(hire_status) && hire_status.equalsIgnoreCase("declined")) {

                notificationIntent = new Intent(context, SplashActivity.class);

                notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                notificationIntent.setAction("com.handyman" + unique_id);
                notificationIntent.putExtra("FROM", "GCMIntentService");
                notificationIntent.putExtra("response", jObj.toString());

                Utils.storeString(mSharedPreferences, Utils.NOTI_HIRE_STATUS, hire_status);
            }

            // set intent so it does not start a new activity
            PendingIntent intent = PendingIntent.getActivity(context, 0, notificationIntent, PendingIntent.FLAG_CANCEL_CURRENT);

            android.support.v4.app.NotificationCompat.Builder mBuilder = new android.support.v4.app.NotificationCompat.Builder(context);

            if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                mBuilder.setSmallIcon(R.drawable.small_logo);
                mBuilder.setColor(Color.parseColor("#D74F4F"));
            } else {
                mBuilder.setSmallIcon(icon);
            }
            mBuilder.setTicker(title).setWhen(when);
            mBuilder.setAutoCancel(true);
            mBuilder.setContentTitle(title);
            mBuilder.setStyle(new android.support.v4.app.NotificationCompat.BigTextStyle().bigText(msg));
            mBuilder.setContentIntent(intent);
            mBuilder.setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION));
            mBuilder.setLargeIcon(BitmapFactory.decodeResource(context.getResources(), icon));
            mBuilder.setContentText(msg);
            mBuilder.setPriority(Notification.PRIORITY_MAX);

            Notification noti = mBuilder.build();
            notificationManager.notify(0, noti);

        } catch (NullPointerException e) {
            e.printStackTrace();
        }
    }
}
