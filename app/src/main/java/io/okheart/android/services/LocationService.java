package io.okheart.android.services;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.IBinder;
import android.provider.Settings;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;
import androidx.core.text.HtmlCompat;
import androidx.work.BackoffPolicy;
import androidx.work.Constraints;
import androidx.work.Data;
import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.NetworkType;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;

import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;

import io.okheart.android.R;
import io.okheart.android.activity.SettingsActivity;
import io.okheart.android.datamodel.AddressItem;
import io.okheart.android.utilities.MyWorker;

public class LocationService extends Service {
    public static final String TAG = "LocationService";
    private NotificationManager notificationManager;
    private String environment;
    private io.okheart.android.database.DataProvider dataProvider;
    private String uniqueId;

    private static void displayLog(String log) {
        Log.i(TAG, log);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        displayLog("oncreate");
        notificationManager = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);
        uniqueId = Settings.Secure.getString(this.getContentResolver(), Settings.Secure.ANDROID_ID);
        dataProvider = new io.okheart.android.database.DataProvider(io.okheart.android.services.LocationService.this);
        environment = dataProvider.getPropertyValue("environment");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        displayLog("onStartCommand");
        //String input = intent.getStringExtra("inputExtra");
        Bitmap largeIconBitmap = BitmapFactory.decodeResource(this.getResources(), io.okheart.android.R.drawable.ic_launcher_foreground);

        String channelId = this.getString(io.okheart.android.R.string.default_notification_channel_id);
        Intent playIntent = new Intent(this, SettingsActivity.class);

        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, playIntent, 0);
        NotificationCompat.Action playAction = new NotificationCompat.Action(android.R.drawable.ic_media_play,
                HtmlCompat.fromHtml("<font color=\"" + ContextCompat.getColor(this, R.color.colorPrimary) +
                        "\">HIDE</font>", HtmlCompat.FROM_HTML_MODE_LEGACY), pendingIntent);


        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder =
                new NotificationCompat.Builder(this, channelId)
                        .setSmallIcon(io.okheart.android.R.drawable.ic_stat_ic_notification)
                        .setContentTitle("OkVerify")
                        .setContentText("Location verification in progress")
                        .setAutoCancel(false)
                        .setOngoing(true)
                        .setLargeIcon(largeIconBitmap)
                        .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                        .setCategory(NotificationCompat.CATEGORY_MESSAGE)
                        .setWhen(System.currentTimeMillis())
                        //.setSound(defaultSoundUri)
                        .addAction(playAction)
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

        notificationManager.notify(1, notification);

        startForeground(1, notification);
        startVerification();
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
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
        List<AddressItem> addressItemList = dataProvider.getAllAddressList();
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
            HashMap<String, String> parameters = new HashMap<>();
            parameters.put("eventName", "Data Collection Service");
            parameters.put("subtype", "stopPeriodicPing");
            parameters.put("type", "doWork");
            parameters.put("onObject", "app");
            parameters.put("view", "OkHi");
            sendEvent(parameters, loans);
        } catch (Exception e1) {
            displayLog("error attaching afl to ual " + e1.toString());
        }
        WorkManager.getInstance().cancelUniqueWork("locationservice");

    }

    private void startKeepPeriodicPing(Integer pingTime, String uniqueId) {
        displayLog("workmanager startKeepPeriodicPing " + pingTime);
        try {

            try {
                HashMap<String, String> loans = new HashMap<>();
                loans.put("uniqueId", uniqueId);
                HashMap<String, String> parameters = new HashMap<>();
                parameters.put("eventName", "Data Collection Service");
                parameters.put("subtype", "startKeepPeriodicPing");
                parameters.put("type", "doWork");
                parameters.put("onObject", "app");
                parameters.put("view", "OkHi");
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
                    new PeriodicWorkRequest.Builder(MyWorker.class, pingTime, TimeUnit.MILLISECONDS)
                            .setInputData(inputData)
                            .setConstraints(constraints)
                            .setBackoffCriteria(BackoffPolicy.LINEAR, 30, TimeUnit.SECONDS)
                            .build();

            WorkManager.getInstance().enqueueUniquePeriodicWork("locationservice", ExistingPeriodicWorkPolicy.KEEP, request);

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
                HashMap<String, String> parameters = new HashMap<>();
                parameters.put("eventName", "Data Collection Service");
                parameters.put("subtype", "startReplacePeriodicPing");
                parameters.put("type", "doWork");
                parameters.put("onObject", "app");
                parameters.put("view", "OkHi");
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
                    new PeriodicWorkRequest.Builder(MyWorker.class, pingTime, TimeUnit.MILLISECONDS)
                            .setInputData(inputData)
                            .setConstraints(constraints)
                            .setBackoffCriteria(BackoffPolicy.LINEAR, 30, TimeUnit.SECONDS)
                            .build();

            WorkManager.getInstance().enqueueUniquePeriodicWork("locationservice", ExistingPeriodicWorkPolicy.REPLACE, request);


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

            io.okheart.android.utilities.OkAnalytics okAnalytics = new io.okheart.android.utilities.OkAnalytics(LocationService.this, environment);
            okAnalytics.sendToAnalytics(parameters, loans, environment);
        } catch (Exception e) {
            displayLog("error sending photoexpanded analytics event " + e.toString());
        }
    }

}
