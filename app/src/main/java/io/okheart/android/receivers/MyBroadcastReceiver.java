package io.okheart.android.receivers;


public class MyBroadcastReceiver {/*extends BroadcastReceiver {
    private static final String TAG = "MyBroadcastReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        displayLog("alarm onreceive");


        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                context.startForegroundService(new Intent(context, io.okheart.android.services.ForegroundService.class));
            } else {
                context.startService(new Intent(context, io.okheart.android.services.ForegroundService.class));
            }

        } catch (Exception jse) {
            displayLog("jsonexception jse " + jse.toString());
        }

    }

    private void displayLog(String me) {
        Log.i(TAG, me);
    }
    */
}