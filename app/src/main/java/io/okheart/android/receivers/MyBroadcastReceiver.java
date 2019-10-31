package io.okheart.android.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import io.okheart.android.services.ForegroundService;


public class MyBroadcastReceiver extends BroadcastReceiver {
    private static final String TAG = "MyBroadcastReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        displayLog("alarm onreceive");

        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                context.startForegroundService(new Intent(context, ForegroundService.class));
            } else {
                context.startService(new Intent(context, ForegroundService.class));
            }

        } catch (Exception jse) {
            displayLog("jsonexception jse " + jse.toString());
        }
    }

    private void displayLog(String me) {
        Log.i(TAG, me);
    }
}