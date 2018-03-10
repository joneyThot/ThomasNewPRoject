package com.handyman;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import com.handyman.logger.Logger;
import com.handyman.service.Utils;


public class ReceiverCall extends BroadcastReceiver {

    SharedPreferences sharedPreferences;

    @Override
    public void onReceive(Context context, Intent intent) {

        sharedPreferences = context.getSharedPreferences(Utils.PREF_NAME, Context.MODE_PRIVATE);

        if (Utils.checkInternetConnection(context)) {
            if (sharedPreferences.getString(Utils.LOGOUT_STATUS, "").isEmpty()) {
                context.startService(new Intent(context, LocationUpdaterService.class));
            } else {
                Utils.storeString(sharedPreferences, Utils.LOGOUT_STATUS, "");
                Logger.i("Service Stops", "Ohhhhhhh");
            }

        }
    }

}
