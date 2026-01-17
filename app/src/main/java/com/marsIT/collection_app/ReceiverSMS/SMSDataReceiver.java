package com.marsIT.collection_app.ReceiverSMS;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.telephony.SmsMessage;

public class SMSDataReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {

        Bundle bundle = intent.getExtras();
        if (bundle == null) return;

        Object[] pdus = (Object[]) bundle.get("pdus");
        if (pdus == null || pdus.length == 0) return;

        // Android M+ provides PDU format
        String format = bundle.getString("format");

        StringBuilder smsBody = new StringBuilder();
        String senderNumber = "";

        for (Object pdu : pdus) {
            SmsMessage sms;

            // --------- Android 6–15+ PDU parsing ---------
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                sms = SmsMessage.createFromPdu((byte[]) pdu, format);
            } else {
                sms = SmsMessage.createFromPdu((byte[]) pdu);
            }

            if (sms != null) {
                if (senderNumber.isEmpty()) {
                    senderNumber = sms.getOriginatingAddress();
                }
                smsBody.append(sms.getMessageBody());
            }
        }

        // --------- Send to your internal broadcast ---------
        Intent broadcastIntent = new Intent("SMS_RECEIVED_ACTION");
        broadcastIntent.putExtra("sms", smsBody.toString());
        broadcastIntent.putExtra("cellnumber", senderNumber);
        context.sendBroadcast(broadcastIntent);
    }
}
