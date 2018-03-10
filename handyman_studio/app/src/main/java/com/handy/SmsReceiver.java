package com.handy;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.text.TextUtils;

import com.handy.logger.Logger;


/**
 * Created by jackey on 19-04-2016.
 */
public class SmsReceiver extends BroadcastReceiver {

    private static SmsListener mListener;
    private String TAG = "SmsReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        Bundle data = intent.getExtras();

        Object[] pdus = (Object[]) data.get("pdus");

        for (int i = 0; i < pdus.length; i++) {

            SmsMessage currentMessage = SmsMessage.createFromPdu((byte[]) pdus[i]);
            String senderNum = currentMessage.getDisplayOriginatingAddress();
            String message = currentMessage.getDisplayMessageBody();
            Logger.d(TAG, "senderNum: " + senderNum + " message: " + message);
            try {
                if (!TextUtils.isEmpty("DM-HomSer") && senderNum.equals("DM-HomSer")) {
                    if (mListener != null) {
                        mListener.messageReceived(message);
                    }
                }
            } catch (Exception e) {
            }

        }

    }

    public static void bindListener(SmsListener listener) {
        mListener = listener;
    }
}
