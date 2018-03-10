package com.android.handyman;


import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;


public class MyAlarmService extends Service 

{
	private NotificationManager mManager;

	@Override
	public IBinder onBind(Intent arg0)
	{
		return null;
	}

	@Override
	public void onCreate() 
	{
		super.onCreate();

	}

	@SuppressWarnings({ "deprecation", "static-access" })
	@Override
	public void onStart(Intent intent, int startId)
	{
		super.onStart(intent, startId);

//		if(intent != null){
//			Bundle bundle = intent.getExtras();
//			if(bundle != null){
//				String mDoc_name = bundle.getString("DOC_NAME");
//				String mAppointmentTime = bundle.getString("TIME");

				mManager = (NotificationManager) this.getApplicationContext().
						getSystemService(this.getApplicationContext().NOTIFICATION_SERVICE);

				Intent intent1 = new Intent(this.getApplicationContext(),LoginActivity.class);

				String msg = getResources().getString(R.string.reminder);/* + " (" + mDoc_name + ") " + getResources().getString(R.string.at) + " " + mAppointmentTime;*/

				Notification notification = new Notification(R.drawable.ic_launcher, msg , System.currentTimeMillis());
				intent1.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP| Intent.FLAG_ACTIVITY_CLEAR_TOP);

				PendingIntent pendingNotificationIntent = PendingIntent.getActivity(this.getApplicationContext(),0, intent1,PendingIntent.FLAG_UPDATE_CURRENT);
				notification.flags |= Notification.FLAG_AUTO_CANCEL;
				notification.setLatestEventInfo(this.getApplicationContext(), "Service@Home", msg, pendingNotificationIntent);
				mManager.notify(0, notification);
//			}
//		}
	}

	@Override
	public void onDestroy() 
	{
		super.onDestroy();
	}

}