package io.okverify.android.services;

import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import android.provider.Settings;

import androidx.annotation.Nullable;
import androidx.work.BackoffPolicy;
import androidx.work.Constraints;
import androidx.work.Data;
import androidx.work.NetworkType;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;

import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class LocationService extends Service {
    public static final String TAG = "LocationService";
    private NotificationManager notificationManager;
    private String environment;
    private io.okverify.android.database.DataProvider dataProvider;
    private String uniqueId, phonenumber;

    private static void displayLog(String log) {
        //Log.i(TAG, log);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        displayLog("oncreate");
        notificationManager = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);
        uniqueId = Settings.Secure.getString(this.getContentResolver(), Settings.Secure.ANDROID_ID);
        dataProvider = new io.okverify.android.database.DataProvider(io.okverify.android.services.LocationService.this);
        environment = dataProvider.getPropertyValue("environment");
        phonenumber = dataProvider.getPropertyValue("phonenumber");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        displayLog("onStartCommand");
        startNotification();

        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

    }

    @Override
    public void onLowMemory() {
        //Send broadcast to the Activity to kill this service and restart it.
        super.onLowMemory();
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                startForegroundService(new Intent(this, io.okverify.android.services.LocationService.class));
            } else {
                startService(new Intent(this, io.okverify.android.services.LocationService.class));
            }

        } catch (Exception jse) {
            displayLog("jsonexception jse " + jse.toString());
        }
    }


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private void startNotification() {
/*
        Bitmap largeIconBitmap = BitmapFactory.decodeResource(this.getResources(), io.okverify.android.R.drawable.ic_launcher_foreground);

        String channelId = this.getString(io.okverify.android.R.string.default_notification_channel_id);
        Intent playIntent = new Intent(this, io.okverify.android.activity.M.class);

        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, playIntent, 0);



        //Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder =
                new NotificationCompat.Builder(this, channelId)
                        .setSmallIcon(io.okverify.android.R.drawable.ic_stat_ic_notification)
                        .setContentTitle("OkVerify")
                        .setColor(this.getColor(io.okverify.android.R.color.newdarkgreen))
                        .setContentText("Your address is being verified")
                        .setAutoCancel(false)
                        .setOngoing(true)
                        .setLargeIcon(largeIconBitmap)
                        .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                        .setCategory(NotificationCompat.CATEGORY_MESSAGE)
                        .setWhen(System.currentTimeMillis())
                        //.setSound(defaultSoundUri)

                        .addAction(android.R.drawable.ic_menu_view, "HIDE", pendingIntent)
                        .setFullScreenIntent(pendingIntent, true)
                        //.setStyle(bigTextStyle)
                        .setContentIntent(pendingIntent);


        // Since android Oreo notification channel is needed.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(channelId,
                    "Notification",
                    NotificationManager.IMPORTANCE_DEFAULT);
            notificationManager.createNotificationChannel(channel);

        }
        Notification notification = notificationBuilder.build();

        notificationManager.notify(32, notification);

        startForeground(32, notification);
        startLoop();
        */


    }

    private void startLoop() {
        displayLog("1 startLoop");
        new Thread() {
            public void run() {
                displayLog("2 startLoop");
                while (true) {
                    displayLog("3 startLoop");
                    try {
                        startVerification();
                        displayLog("4 startLoop");
                        sleep(1800000);
                        displayLog("6 startLoop");
                    } catch (Exception e) {
                        displayLog("while loop error " + e.toString());
                    }
                    displayLog("7 startLoop");
                }
            }

        }.start();
        displayLog("8 startLoop");
    }

    private void startVerification() {
        String verify = "true";

        try {
            if (verify != null) {
                String tempVerify = "" + verify;
                if (tempVerify.length() > 0) {
                    if ((tempVerify.equalsIgnoreCase("false")) || ((tempVerify.equalsIgnoreCase("true")))) {
                        if (verify.equalsIgnoreCase("true")) {
                            displayLog("decideWhatToStart");
                            decideWhatToStart();
                        } else {
                            displayLog("stopPeriodicPing");
                            stopPeriodicPing();
                        }


                    } else {
                        stopPeriodicPing();
                    }

                } else {

                    stopPeriodicPing();
                }

            } else {

                stopPeriodicPing();
            }
        } catch (Exception io) {
            stopPeriodicPing();
        } finally {
            // writeToFileVerify("false", "one");
        }
    }

    private void decideWhatToStart() {
        //io.okverify.android.asynctask.GeofenceTask geofenceTask = new io.okverify.android.asynctask.GeofenceTask(this, false);
        //geofenceTask.execute();
        List<io.okverify.android.datamodel.AddressItem> addressItemList = dataProvider.getAllAddressList();
        displayLog("addressItemList " + addressItemList.size());
        if (addressItemList.size() > 0) {

            String tempKill = dataProvider.getPropertyValue("kill_switch");
            if (tempKill != null) {
                if (tempKill.length() > 0) {
                    if (tempKill.equalsIgnoreCase("true")) {
                        String tempResume_ping_frequency = dataProvider.getPropertyValue("resume_ping_frequency");
                        if (tempResume_ping_frequency != null) {
                            if (tempResume_ping_frequency.length() > 0) {
                                Integer pingTime = Integer.parseInt(tempResume_ping_frequency);
                                startReplacePeriodicPing(pingTime, uniqueId);
                            } else {
                                startReplacePeriodicPing(360000000, uniqueId);
                            }
                        } else {
                            startReplacePeriodicPing(360000000, uniqueId);
                        }
                    } else {
                        String tempPing_frequency = dataProvider.getPropertyValue("ping_frequency");
                        if (tempPing_frequency != null) {
                            if (tempPing_frequency.length() > 0) {
                                Integer pingTime = Integer.parseInt(tempPing_frequency);
                                startKeepPeriodicPing(pingTime, uniqueId);
                            } else {
                                startKeepPeriodicPing(3600000, uniqueId);
                            }
                        } else {
                            startKeepPeriodicPing(3600000, uniqueId);
                        }
                    }
                } else {
                    String tempPing_frequency = dataProvider.getPropertyValue("ping_frequency");
                    if (tempPing_frequency != null) {
                        if (tempPing_frequency.length() > 0) {
                            Integer pingTime = Integer.parseInt(tempPing_frequency);
                            startKeepPeriodicPing(pingTime, uniqueId);
                        } else {
                            startKeepPeriodicPing(3600000, uniqueId);
                        }
                    } else {
                        startKeepPeriodicPing(3600000, uniqueId);
                    }
                }
            } else {
                String tempPing_frequency = dataProvider.getPropertyValue("ping_frequency");
                if (tempPing_frequency != null) {
                    if (tempPing_frequency.length() > 0) {
                        Integer pingTime = Integer.parseInt(tempPing_frequency);
                        startKeepPeriodicPing(pingTime, uniqueId);
                    } else {
                        startKeepPeriodicPing(3600000, uniqueId);
                    }
                } else {
                    startKeepPeriodicPing(3600000, uniqueId);
                }
            }
        } else {
            stopPeriodicPing();
        }
    }

    ///Please enable this after testing
    private void stopPeriodicPing() {

        try {
            HashMap<String, String> loans = new HashMap<>();
            loans.put("uniqueId", uniqueId);
            loans.put("phonenumber", phonenumber);
            HashMap<String, String> parameters = new HashMap<>();
            parameters.put("eventName", "Data Collection Service");
            parameters.put("subtype", "stopPeriodicPing");
            parameters.put("type", "doWork");
            parameters.put("onObject", "app");
            parameters.put("view", "LocationService");
            sendEvent(parameters, loans);
        } catch (Exception e1) {
            displayLog("error attaching afl to ual " + e1.toString());
        }
        WorkManager.getInstance().cancelUniqueWork("ramogi");

    }

    private void startKeepPeriodicPing(Integer pingTime, String uniqueId) {
        displayLog("workmanager startKeepPeriodicPing " + pingTime);
        try {

            try {
                HashMap<String, String> loans = new HashMap<>();
                loans.put("uniqueId", uniqueId);
                loans.put("phonenumber", phonenumber);
                HashMap<String, String> parameters = new HashMap<>();
                parameters.put("eventName", "Data Collection Service");
                parameters.put("subtype", "startKeepPeriodicPing");
                parameters.put("type", "doWork");
                parameters.put("onObject", "app");
                parameters.put("view", "LocationService");
                sendEvent(parameters, loans);
            } catch (Exception e1) {
                displayLog("error attaching afl to ual " + e1.toString());
            }

            Constraints constraints = new Constraints.Builder()
                    .setRequiredNetworkType(NetworkType.CONNECTED)
                    .build();

            Data inputData = new Data.Builder()
                    .putString("uniqueId", uniqueId)
                    .build();

            PeriodicWorkRequest request =
                    new PeriodicWorkRequest.Builder(io.okverify.android.utilities.MyWorker.class, pingTime, TimeUnit.MILLISECONDS)
                            .setInputData(inputData)
                            .setConstraints(constraints)
                            .setBackoffCriteria(BackoffPolicy.LINEAR, 30, TimeUnit.SECONDS)
                            .build();

            //WorkManager.getInstance().enqueueUniquePeriodicWork(uniqueId, ExistingPeriodicWorkPolicy.KEEP, request);

        } catch (Exception e) {
            displayLog("my worker error " + e.toString());
        }
    }

    private void startReplacePeriodicPing(Integer pingTime, String uniqueId) {
        displayLog("workmanager startReplacePeriodicPing");
        try {
            try {
                HashMap<String, String> loans = new HashMap<>();
                loans.put("uniqueId", uniqueId);
                loans.put("phonenumber", phonenumber);
                HashMap<String, String> parameters = new HashMap<>();
                parameters.put("eventName", "Data Collection Service");
                parameters.put("subtype", "startReplacePeriodicPing");
                parameters.put("type", "doWork");
                parameters.put("onObject", "app");
                parameters.put("view", "LocationService");
                sendEvent(parameters, loans);
            } catch (Exception e1) {
                displayLog("error attaching afl to ual " + e1.toString());
            }

            Constraints constraints = new Constraints.Builder()
                    .setRequiredNetworkType(NetworkType.CONNECTED)
                    .build();

            Data inputData = new Data.Builder()
                    .putString("uniqueId", uniqueId)
                    .build();

            PeriodicWorkRequest request =
                    new PeriodicWorkRequest.Builder(io.okverify.android.utilities.MyWorker.class, pingTime, TimeUnit.MILLISECONDS)
                            .setInputData(inputData)
                            .setConstraints(constraints)
                            .setBackoffCriteria(BackoffPolicy.LINEAR, 30, TimeUnit.SECONDS)
                            .build();

            //WorkManager.getInstance().enqueueUniquePeriodicWork(uniqueId, ExistingPeriodicWorkPolicy.REPLACE, request);


        } catch (Exception e) {
            displayLog("my worker error " + e.toString());
        }
    }

    private void sendEvent(HashMap<String, String> parameters, HashMap<String, String> loans) {
        try {
            String environment = dataProvider.getPropertyValue("environment");

            if (environment != null) {
                if (environment.length() > 0) {
                    if (environment.equalsIgnoreCase("PROD")) {

                    } else if (environment.equalsIgnoreCase("DEVMASTER")) {

                    } else if (environment.equalsIgnoreCase("SANDBOX")) {

                    } else {

                    }
                } else {
                    environment = "PROD";
                }
            } else {
                environment = "PROD";
            }

            io.okverify.android.utilities.OkAnalytics okAnalytics = new io.okverify.android.utilities.OkAnalytics(LocationService.this, environment);
            okAnalytics.sendToAnalytics(parameters, loans, environment);
        } catch (Exception e) {
            displayLog("error sending photoexpanded analytics event " + e.toString());
        }
    }

}
