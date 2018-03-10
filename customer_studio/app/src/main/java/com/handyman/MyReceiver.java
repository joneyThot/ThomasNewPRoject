package com.handyman;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

public class MyReceiver extends BroadcastReceiver
{

	@Override
	public void onReceive(Context context, Intent intent)
	{
//		Bundle bundle = intent.getExtras();
//		String mDoc_name = bundle.getString("DOC_NAME");
//		String mAppointmentTime = bundle.getString("TIME");
		Intent service1 = new Intent(context, MyAlarmService.class);
//		service1.putExtra("DOC_NAME", mDoc_name);
//		service1.putExtra("TIME", mAppointmentTime);
		context.startService(service1);

		
	}

}
