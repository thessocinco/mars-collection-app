//package com.marsIT.collection_app.ReceiverSMS;
//
//import android.content.BroadcastReceiver;
//import android.content.Context;
//import android.content.Intent;
//import android.os.Bundle;
//import android.telephony.SmsMessage;
//
//public class SMSDataReceiver extends BroadcastReceiver {
//    @Override
//    public void onReceive(Context context, Intent intent) {
//        Bundle bundle = intent.getExtras();
//        SmsMessage[] msgs;
//        String str = "";
//        String cellnum = "";
//        if (bundle != null) {
//            Object[] pdus = (Object[]) bundle.get("pdus");
//            msgs = new SmsMessage[pdus.length];
//            for (int i = 0; i < msgs.length; i++) {
//                msgs[i] = SmsMessage.createFromPdu((byte[]) pdus[i]);
//                cellnum += msgs[i].getOriginatingAddress();
//                str += msgs[i].getMessageBody().toString();
//            }
//
//            Intent broadcastIntent = new Intent();
//            broadcastIntent.setAction("SMS_RECEIVED_ACTION");
//            broadcastIntent.putExtra("sms", str);
//            broadcastIntent.putExtra("cellnumber", cellnum);
//            context.sendBroadcast(broadcastIntent);
//        }
//    }
//}