package com.handy;

import com.handy.logger.Logger;
import com.handy.service.GPSTracker;
import com.handy.service.Utils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

public class ReceiverCall extends BroadcastReceiver {

    SharedPreferences sharedPreferences;
    private static String TAG = "ReceiverCall";

    @Override
    public void onReceive(Context context, Intent intent) {

        sharedPreferences = context.getSharedPreferences(Utils.PREF_NAME, Context.MODE_PRIVATE);

        if (intent.getAction().equals("android.intent.action.BOOT_COMPLETED")) {
            if (Utils.checkInternetConnection(context)) {
                if (sharedPreferences.getString(Utils.LOGOUT_STATUS, "").isEmpty()) {
//                    context.startService(new Intent(context.getApplicationContext(), LocationUpdaterService.class));
                    Intent i = new Intent(context, LocationUpdaterService.class);
//                    i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startService(i);
                } else {
                    Utils.storeString(sharedPreferences, Utils.LOGOUT_STATUS, "");
                    Logger.i("Service Stops", "Ohhhhhhh");
                }

            }
        }

    }

}
