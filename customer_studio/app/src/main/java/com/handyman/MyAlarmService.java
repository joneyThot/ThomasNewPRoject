package com.handyman;


import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v7.app.NotificationCompat;


public class MyAlarmService extends Service

{
    private NotificationManager mManager;

    @Override
    public IBinder onBind(Intent arg0) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();

    }

    @SuppressWarnings({"deprecation", "static-access"})
    @Override
    public void onStart(Intent intent, int startId) {
        super.onStart(intent, startId);

//		if(intent != null){
//			Bundle bundle = intent.getExtras();
//			if(bundle != null){
//				String mDoc_name = bundle.getString("DOC_NAME");
//				String mAppointmentTime = bundle.getString("TIME");

        mManager = (NotificationManager) this.getApplicationContext().
                getSystemService(this.getApplicationContext().NOTIFICATION_SERVICE);

        Intent intent1 = new Intent(this.getApplicationContext(), LoginActivity.class);

        String msg = getResources().getString(R.string.reminder);/* + " (" + mDoc_name + ") " + getResources().getString(R.string.at) + " " + mAppointmentTime;*/
        long when = System.currentTimeMillis();
        Notification notification = new Notification();
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this.getApplicationContext());

        intent1.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);

        PendingIntent pendingNotificationIntent = PendingIntent.getActivity(this.getApplicationContext(), 0, intent1, PendingIntent.FLAG_UPDATE_CURRENT);

//				notification = builder
//						.setSmallIcon(R.drawable.)
//						.setTicker("Service@Home").setWhen(0)
//						.setAutoCancel(true)
//						.setContentIntent(pendingNotificationIntent)
//						.setContentTitle("Service@Home")
//						.setContentText(msg)
//						.build();
//				notification.flags |= Notification.FLAG_AUTO_CANCEL;
//				//notification.setLatestEventInfo(this.getApplicationContext(), , msg, pendingNotificationIntent);
//				mManager.notify(0, notification);

        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            mBuilder.setSmallIcon(R.drawable.small_logo);
            mBuilder.setColor(Color.parseColor("#D74F4F"));
        } else {
            mBuilder.setSmallIcon(R.drawable.splash_logo_xxxhdpi);
        }

        mBuilder.setTicker("Service@Home").setWhen(when);
        mBuilder.setAutoCancel(true);
        mBuilder.setContentTitle("Service@Home");
        mBuilder.setStyle(new android.support.v4.app.NotificationCompat.BigTextStyle().bigText(msg));
        mBuilder.setContentIntent(pendingNotificationIntent);
        mBuilder.setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION));
        mBuilder.setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.splash_logo_xxxhdpi));
        mBuilder.setContentText(msg);

        notification = mBuilder.build();
        //notification.setLatestEventInfo(this.getApplicationContext(), , msg, pendingNotificationIntent);
        mManager.notify(0, notification);
//			}
//		}
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

}