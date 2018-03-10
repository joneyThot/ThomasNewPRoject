package com.android.handy;

import com.android.handy.logger.Logger;
import com.android.handy.service.GPSTracker;
import com.android.handy.service.Utils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class ReceiverCall extends BroadcastReceiver{

	@Override
	public void onReceive(Context context, Intent intent) {
		
		if(Utils.checkInternetConnection(context)){
			Logger.i("Service Stops", "Ohhhhhhh");
			context.startService(new Intent(context, ServiceTest.class));
		} 
	}

}
