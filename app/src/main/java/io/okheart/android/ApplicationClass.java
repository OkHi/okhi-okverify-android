package io.okheart.android;

import android.app.Application;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import com.parse.Parse;
import com.segment.analytics.Analytics;

import io.okheart.android.services.ForegroundService;

import static io.okheart.android.utilities.Constants.ANALYTICS_WRITE_KEY;
import static io.okheart.android.utilities.Constants.DEVMASTER_APPLICATION_ID;
import static io.okheart.android.utilities.Constants.DEVMASTER_CLIENT_ID;

public class ApplicationClass extends Application {
    private static final String TAG = "ApplicationClass";
    //public static Context context;
    //private static String uniqueId;
    private static Analytics analytics;
    //private FirebaseRemoteConfig mFirebaseRemoteConfig;

    public static String getTAG() {
        return TAG;
    }

    /*
    public static Context getContext() {
        return context;
    }

    public static void setContext(Context context) {
        ApplicationClass.context = context;
    }
    */
/*
    public static String getUniqueId() {
        return uniqueId;
    }

    public static void setUniqueId(String uniqueId) {
        ApplicationClass.uniqueId = uniqueId;
    }
*/
    public static Analytics getAnalytics() {
        return analytics;
    }

    public static void setAnalytics(Analytics analytics) {
        ApplicationClass.analytics = analytics;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        try {
            Parse.initialize(new Parse.Configuration.Builder(this)
                    .applicationId(DEVMASTER_APPLICATION_ID)
                    .clientKey(DEVMASTER_CLIENT_ID)
                    .server("https://parseapi.back4app.com/").enableLocalDataStore().build());
        } catch (Exception e) {
            displayLog("parse initialize error " + e.toString());
        }
        /*
        try {

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
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    displayLog("Remote config params fetch failed " + e.getMessage());
                }
            });
        } catch (Exception e) {
            displayLog("firebase remote config initialize error " + e.toString());
        }
        */

        try {
            //context = getApplicationContext();
        } catch (Exception e) {
            displayLog("Error resolver " + e.toString());
        }

        try {
            analytics = new Analytics.Builder(this, ANALYTICS_WRITE_KEY).build();
            Analytics.setSingletonInstance(analytics);
        } catch (Exception e) {
            displayLog("Error initializing analytics " + e.toString());
        }

        //uniqueId = Settings.Secure.getString(getApplicationContext().getContentResolver(), Settings.Secure.ANDROID_ID);
        //setUniqueId(uniqueId);
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                startForegroundService(new Intent(this.getApplicationContext(), ForegroundService.class));
            } else {
                startService(new Intent(this.getApplicationContext(), ForegroundService.class));
            }

        } catch (Exception jse) {
            displayLog("jsonexception jse " + jse.toString());
        }

    }
/*
    public FirebaseRemoteConfig getmFirebaseRemoteConfig() {
        return mFirebaseRemoteConfig;
    }

    public void setmFirebaseRemoteConfig(FirebaseRemoteConfig mFirebaseRemoteConfig) {
        this.mFirebaseRemoteConfig = mFirebaseRemoteConfig;
    }
    */

    private void displayLog(String me) {
        Log.i(TAG, me);
    }
}
