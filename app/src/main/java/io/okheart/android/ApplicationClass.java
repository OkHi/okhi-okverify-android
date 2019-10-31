package io.okheart.android;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.provider.Settings;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings;
import com.parse.Parse;
import com.segment.analytics.Analytics;

import io.okheart.android.services.ForegroundService;

import static io.okheart.android.utilities.Constants.ANALYTICS_WRITE_KEY;

public class ApplicationClass extends Application {
    private static final String TAG = "ApplicationClass";
    public static Context context;
    private static String uniqueId;
    private static Analytics analytics;
    private FirebaseRemoteConfig mFirebaseRemoteConfig;

    public static String getTAG() {
        return TAG;
    }

    public static Context getContext() {
        return context;
    }

    public static void setContext(Context context) {
        ApplicationClass.context = context;
    }

    public static String getUniqueId() {
        return uniqueId;
    }

    public static void setUniqueId(String uniqueId) {
        ApplicationClass.uniqueId = uniqueId;
    }

    public static Analytics getAnalytics() {
        return analytics;
    }

    public static void setAnalytics(Analytics analytics) {
        ApplicationClass.analytics = analytics;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Parse.initialize(new Parse.Configuration.Builder(this)
                .applicationId("39qx0cn6q1IZM7XCA0H2uYstqwLazABGupEUTMg0")
                .clientKey("2s23x3Bsqhattvq2VzPNJwnNCbpoja2DkoN2v4OR")
                .server("https://parseapi.back4app.com/").enableLocalDataStore().build());

        mFirebaseRemoteConfig = FirebaseRemoteConfig.getInstance();
        FirebaseRemoteConfigSettings configSettings = new FirebaseRemoteConfigSettings.Builder()
                .setFetchTimeoutInSeconds(10)
                .setMinimumFetchIntervalInSeconds(3600)
                .build();

        mFirebaseRemoteConfig.setConfigSettingsAsync(configSettings).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    displayLog("setConfigSettingsAsync updated: ");

                } else {
                    displayLog("setConfigSettingsAsync failed ");
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        });

        mFirebaseRemoteConfig.setDefaultsAsync(R.xml.remote_config_defaults);
        mFirebaseRemoteConfig.fetchAndActivate().addOnCompleteListener(new OnCompleteListener<Boolean>() {
            @Override
            public void onComplete(@NonNull Task<Boolean> task) {
                if (task.isSuccessful()) {
                    boolean updated = task.getResult();
                    displayLog("Remote config params updated: " + updated);

                } else {
                    displayLog("Remote config params fetch failed ");
                }
                //Double frequency = mFirebaseRemoteConfig.getDouble(BACKGROUND_FREQUENCY);
                //displayLog("2 frequency " + frequency);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                displayLog("Remote config params fetch failed " + e.getMessage());
            }
        });
        try {
            context = getApplicationContext();
        } catch (Exception e) {
            displayLog("Error resolver " + e.toString());
        }


        try {
            analytics = new Analytics.Builder(this, ANALYTICS_WRITE_KEY).build();
            Analytics.setSingletonInstance(analytics);
        } catch (Exception e) {
            displayLog("Error initializing analytics " + e.toString());
        }

        uniqueId = Settings.Secure.getString(getApplicationContext().getContentResolver(), Settings.Secure.ANDROID_ID);
        setUniqueId(uniqueId);

        /*
        try {
            WindowManager wm = (WindowManager) getSystemService(WINDOW_SERVICE);
            DisplayMetrics dm = new DisplayMetrics();
            wm.getDefaultDisplay().getMetrics(dm);
            setScreenwidth(dm.widthPixels);
            setScreenheight(dm.heightPixels);
        } catch (Exception e) {
            displayLog("Error initializing stuff " + e.toString());
        }
        try {
            setVersion(getBuildGradleVersionName());
        } catch (Exception e) {
            displayLog("error setting version " + e.toString());
        }
        */
        try {
            //checkJobSchedule(getContext(), "okverifyapplication");
            //Constants.periodicSyncService(this, "app");
        } catch (Exception e) {
            displayLog("error check job schedule " + e.toString());
        }

        try {
            //FirebaseMessaging.getInstance().setAutoInitEnabled(true);
            FirebaseInstanceId.getInstance().getInstanceId()
                    .addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                        @Override
                        public void onComplete(@NonNull Task<InstanceIdResult> task) {
                            if (!task.isSuccessful()) {
                                displayLog("getInstanceId failed " + task.getException());
                                return;
                            }

                            // Get new Instance ID token
                            String token = task.getResult().getToken();

                            // Log and toast
                            //String msg = getString(R.string.msg_token_fmt, token);
                            displayLog("token " + token);
                            //Toast.makeText(MainActivity.this, msg, Toast.LENGTH_SHORT).show();
                            //sendRegistrationToServer(token);
                        }
                    });


        } catch (Exception e) {
            displayLog("error check job schedule " + e.toString());
        }


        try {


/*
            Constraints constraints = new Constraints.Builder()
                    // The Worker needs Network connectivity
                    //.setRequiredNetworkType(NetworkType.CONNECTED)
                    // Needs the device to be charging
                    //.setRequiresCharging(true)
                    .build();

            Data inputData = new Data.Builder()
                    .putString("key", "some_value")
                    .build();

            PeriodicWorkRequest request =
                    // Executes MyWorker every 15 minutes
                    new PeriodicWorkRequest.Builder(MyWorker.class, 1800, TimeUnit.SECONDS)
                            // Sets the input data for the ListenableWorker
                            .setInputData(inputData)
                            .setInitialDelay(60, TimeUnit.SECONDS)
                            // other setters (as above)
                            .setConstraints(constraints)
                            .setBackoffCriteria(BackoffPolicy.LINEAR, 30, TimeUnit.SECONDS)
                            .build();

            WorkManager.getInstance(this).enqueueUniquePeriodicWork("ramogi", ExistingPeriodicWorkPolicy.KEEP, request);
*/


        } catch (Exception e) {
            displayLog("my worker error " + e.toString());
        }


        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                startForegroundService(new Intent(this, ForegroundService.class));
            } else {
                startService(new Intent(this, ForegroundService.class));
            }

        } catch (Exception jse) {
            displayLog("jsonexception jse " + jse.toString());
        }


    }

    public FirebaseRemoteConfig getmFirebaseRemoteConfig() {
        return mFirebaseRemoteConfig;
    }

    public void setmFirebaseRemoteConfig(FirebaseRemoteConfig mFirebaseRemoteConfig) {
        this.mFirebaseRemoteConfig = mFirebaseRemoteConfig;
    }

    private void displayLog(String me) {
        Log.i(TAG, me);
    }
}
