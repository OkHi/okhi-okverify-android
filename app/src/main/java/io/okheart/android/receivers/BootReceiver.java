package io.okheart.android.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import io.okheart.android.services.ForegroundService;

public class BootReceiver extends BroadcastReceiver {
    private static final String TAG = "BootReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        // displayLog("alarm bootreceiver");
        //sendSMS("boot receiver");
        if (intent.getAction().equals("android.intent.action.BOOT_COMPLETED") || (intent.getAction().equals("android.intent.action.QUICKBOOT_POWERON"))) {
            // Set the alarm here.
            // displayLog("alarm boot completed");
            //startAlert(context);
            //sendSMS("boot completed");

            try {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    context.startForegroundService(new Intent(context, ForegroundService.class));
                } else {
                    context.startService(new Intent(context, ForegroundService.class));
                }

            } catch (Exception jse) {
                // displayLog("jsonexception jse " + jse.toString());
            }
        }
    }

/*
    public void startAlert(Context context) {
        //EditText text = findViewById(R.id.time);
        int i = Integer.parseInt("3600");
        Intent intent = new Intent(context, MyBroadcastReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context.getApplicationContext(), 234324243, intent, 0);

        AlarmManager alarmManager = (AlarmManager) context.getSystemService(ALARM_SERVICE);
        //alarmManager.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + (i * 1000), pendingIntent);
        alarmManager.setExact(AlarmManager.RTC_WAKEUP,
                System.currentTimeMillis() + (i * 1000),
                pendingIntent);

        displayLog("alarm boot completed");


    }


    private void sendSMS(String who){
        try {
            //String message = "https://hypertrack-996a0.firebaseapp.com/?id="+OkVerifyApplication.getUniqueId();
            //String message = remoteSmsTemplate + OkVerifyApplication.getUniqueId();
            final HashMap<String, String> jsonObject = new HashMap<>();
            jsonObject.put("userId", "GrlaR3LHUP");
            jsonObject.put("sessionToken", "r:3af107bf99e4c6f2a91e6fec046f5fc7");
            jsonObject.put(COLUMN_BRANCH, "hq_okhi");
            jsonObject.put(COLUMN_AFFILIATION, "okhi");
            jsonObject.put("customName", "test");
            //jsonObject.put("ualId", verifyDataItem.getUalId());
            jsonObject.put("phoneNumber", "+254713567907");
            jsonObject.put("phone", "+254713567907");
            jsonObject.put("message", "we have received "+getDeviceModelAndBrand()+" status "+who);
            SendCustomLinkSmsCallBack sendCustomLinkSmsCallBack = new SendCustomLinkSmsCallBack() {
                @Override
                public void querycomplete(String response, boolean status) {
                    if (status) {
                        displayLog("send sms success " + response);
                        //displayToast("SMS sent", true);
                    } else {
                        displayLog("send sms failure " + response);
                        //displayToast("Error " + response, true);
                    }
                }
            };
            SendCustomLinkSmsTask sendCustomLinkSmsTask = new SendCustomLinkSmsTask(sendCustomLinkSmsCallBack, jsonObject);
            sendCustomLinkSmsTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        } catch (Exception jse) {
            displayLog("jsonexception " + jse.toString());
        }
    }


    private String getDeviceModelAndBrand() {

        String manufacturer = Build.MANUFACTURER;
        String model = Build.MODEL;
        if (model.contains(manufacturer)) {
            return model;
        } else {
            return manufacturer + " " + model;
        }

    }


    private void displayLog(String me){
       // Log.i(TAG, me);
    }
    */
}
