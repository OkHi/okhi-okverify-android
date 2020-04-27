package io.okverify.android;

import android.Manifest;
import android.app.Activity;
import android.app.NotificationManager;
import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.provider.Settings;
import android.util.Log;
import android.view.Gravity;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;

import com.segment.analytics.Analytics;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.List;


public final class OkHi extends ContentProvider {

    private static final String TAG = "OkHi";
    private static final int THREAD_WAIT_TIMEOUT_IN_MS = 100;
    protected static Integer resume_ping_frequency = null;
    protected static Integer ping_frequency = null;
    protected static Integer background_frequency = null;
    protected static String sms_template = null;
    protected static Double gps_accuracy = null;
    protected static Boolean kill_switch = null;
    protected static io.okverify.android.database.DataProvider dataProvider;
    private static String firstname, lastname, phonenumber, requestSource;
    private static Context mContext;
    private static io.okverify.android.callback.OkHiCallback callback;
    private static String appkey;
    private static String uniqueId;
    //private static String remoteSmsTemplate;
    private static Analytics analytics;
    private static NotificationManager notificationManager;

    public OkHi() {
    }

    public static void initialize(@NonNull final String applicationKey, @NonNull String branchid, @NonNull final String environment) throws RuntimeException {

        displayLog("initialize");
        //dataProvider.insertStuff("enableverify", ""+verify);

        startInitialization(applicationKey, branchid, environment, true);
        /*
        if (applicationKey != null) {
            if (applicationKey.length() > 0) {
                if (applicationKey.startsWith("r:")) {
                    startInitialization(applicationKey, branchid, environment, true);
                } else {
                    throw new RuntimeException("Initialization error", new Throwable("Confirm your application key is correct"));

                }
            } else {
                throw new RuntimeException("Initialization error", new Throwable("Confirm your application key is correct"));
            }
        } else {
            throw new RuntimeException("Initialization error", new Throwable("Confirm your application key is not null"));
        }
        */

    }
/*
    public static void displayClient(@NonNull io.okverify.android.callback.OkHiCallback okHiCallback, @NonNull JSONObject jsonObject) throws RuntimeException {

        displayLog("display client " + jsonObject.toString());

        if (jsonObject != null) {
            if (jsonObject.length() > 0) {
                if (okHiCallback != null) {

                    if (checkPermission()) {
                        startActivity(okHiCallback, jsonObject);
                    } else {
                        String cause = checkPermissionCause();
                        if ((cause.equalsIgnoreCase("Manifest.permission.ACCESS_FINE_LOCATION granted")) ||
                                (cause.equalsIgnoreCase("Manifest.permission.ACCESS_BACKGROUND_LOCATION granted"))) {
                            startActivity(okHiCallback, jsonObject);
                        } else {
                            String verify = "false";
                            File filesDir = new File(mContext.getFilesDir() + "/verify.txt");
                            if (filesDir.exists()) {
                                displayLog("filesdir exists");
                                try {
                                    verify = getStringFromFile(filesDir.getAbsolutePath());
                                    displayLog("verify " + verify);
                                } catch (Exception e) {
                                    // Hmm, the applicationId file was malformed or something. Assume it
                                    // doesn't match.
                                    displayLog("error " + e.toString());
                                }
                            } else {
                                displayLog("filesdir does not exist");
                            }
                            if (verify.equalsIgnoreCase("true")) {
                                try {
                                    JSONObject responseJson = new JSONObject();
                                    responseJson.put("message", "fatal_exit");
                                    JSONObject payloadJson = new JSONObject();
                                    payloadJson.put("errorCode", -1);
                                    payloadJson.put("error", "Location permission not granted");
                                    payloadJson.put("message", cause);
                                    responseJson.put("payload", payloadJson);
                                    displayLog(responseJson.toString());
                                    okHiCallback.querycomplete(responseJson);
                                } catch (JSONException jse) {

                                }
                            } else {
                                startActivity(okHiCallback, jsonObject);
                            }
                        }
                    }
                } else {
                    throw new RuntimeException("DisplayClient error", new Throwable("Confirm OkHiCallback is not null"));
                }
            } else {
                throw new RuntimeException("DisplayClient error", new Throwable("Confirm your JSONObject is not null"));
            }
        } else {
            throw new RuntimeException("DisplayClient error", new Throwable("Confirm your JSONObject is not null"));
        }
        //fetchAddresses(jsonObject);


    }
*/
    private static void startInitialization(final String applicationKey, final String branchid, final String environment, final Boolean verify) {
        displayLog("workmanager startInitialization " + verify);
        try {
            HashMap<String, String> loans = new HashMap<>();
            loans.put("uniqueId", uniqueId);
            HashMap<String, String> parameters = new HashMap<>();
            parameters.put("eventName", "Android SDK");
            parameters.put("type", "initialize");
            parameters.put("subtype", "initialize");
            parameters.put("onObject", "okHeartAndroidSDK");
            parameters.put("view", "app");
            parameters.put("appKey", "" + applicationKey);
            parameters.put("branchid", branchid);
            parameters.put("environment", environment);
            sendEvent(parameters, loans);
        } catch (Exception e1) {
            displayLog("error attaching afl to ual " + e1.toString());
        }
        try {
            dataProvider.insertStuff("verify", "" + verify);
            appkey = applicationKey;
            dataProvider.insertStuff("applicationKey", applicationKey);
            dataProvider.insertStuff("branchid", branchid);
            dataProvider.insertStuff("environment", environment);
        } catch (Exception io) {

        } finally {

        }
        try {
            writeToFile(applicationKey);
        } catch (Exception io) {

        } finally {

        }



        try {

            JSONObject identifyjson = new JSONObject();
            //final String environment = dataProvider.getPropertyValue("environment");
            //identifyjson.put("userId", userId);
            try {
                io.okverify.android.callback.SegmentIdentifyCallBack segmentIdentifyCallBack = new io.okverify.android.callback.SegmentIdentifyCallBack() {
                    @Override
                    public void querycomplete(String response, boolean status) {
                        if (status) {
                            displayLog("things went ok with send to omtm identify");

                            try {
                                io.okverify.android.callback.SegmentTrackCallBack segmentTrackCallBack = new io.okverify.android.callback.SegmentTrackCallBack() {
                                    @Override
                                    public void querycomplete(String response, boolean status) {
                                        if (status) {
                                            displayLog("things went ok with send to omtm track");
                                        } else {
                                            displayLog("something went wrong with send to omtm track");
                                        }
                                    }
                                };
                                JSONObject eventjson = new JSONObject();
                                //eventjson.put("userId", userId);
                                eventjson.put("event", "SDK Initialization");

                                JSONObject trackjson = new JSONObject();
                                trackjson.put("environment", environment);
                                trackjson.put("event", "SDK Initialization");
                                trackjson.put("action", "initialization");
                                trackjson.put("actionSubtype", "initialization");
                                trackjson.put("clientProduct", "okHeartAndroidSDK");
                                trackjson.put("clientProductVersion", BuildConfig.VERSION_NAME);
                                trackjson.put("clientKey", applicationKey);
                                trackjson.put("uniqueId", uniqueId);
                                trackjson.put("appLayer", "client");
                                trackjson.put("onObject", "sdk");
                                trackjson.put("product", "okHeartAndroidSDK");
                                eventjson.put("properties", trackjson);
                                io.okverify.android.asynctask.SegmentTrackTask segmentTrackTask = new io.okverify.android.asynctask.SegmentTrackTask(segmentTrackCallBack, eventjson, environment);
                                segmentTrackTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                            } catch (JSONException e) {
                                displayLog("track error omtm error " + e.toString());
                            }
                        } else {
                            displayLog("something went wrong with send to omtm identify");
                        }

                    }
                };
                io.okverify.android.asynctask.SegmentIdentifyTask segmentIdentifyTask = new io.okverify.android.asynctask.SegmentIdentifyTask(segmentIdentifyCallBack, identifyjson, environment);
                segmentIdentifyTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

            } catch (Exception e) {
                displayLog("Error initializing analytics_omtm " + e.toString());
            }
        } catch (Exception jse) {
            displayLog("jsonexception jse " + jse.toString());
        }
        io.okverify.android.utilities.ConfigurationFile configurationFile = new io.okverify.android.utilities.ConfigurationFile(mContext, environment);

    }

    public static void customize(@NonNull String appColorTheme, @NonNull String appName, @NonNull String appLogo,
                                 @NonNull String appBarColor, @NonNull Boolean appBarVisibility,
                                 @NonNull Boolean enableStreetView) {

        try {
            displayLog("okhi customized");
            JSONObject jsonObject = new JSONObject();
            if (appColorTheme != null) {
                if (appColorTheme.length() > 0) {
                    jsonObject.put("color", appColorTheme);
                }
            }
            if (appName != null) {
                if (appName.length() > 0) {
                    jsonObject.put("name", appName);
                }
            }

            if (appLogo != null) {
                if (appLogo.length() > 0) {
                    jsonObject.put("logo", appLogo);
                }
            }
            if (appBarColor != null) {
                if (appBarColor.length() > 0) {
                    jsonObject.put("appbarcolor", appBarColor);
                }
            }
            if (appBarVisibility != null) {
                jsonObject.put("appbarvisibility", appBarVisibility);
            }

            if (enableStreetView != null) {
                jsonObject.put("enablestreetview", enableStreetView);
            }
            String customString = jsonObject.toString();
            //displayLog("logo "+jsonObject.get("logo"));
            String testString = "{\"color\":\"" + appBarColor + "\", \"name\": \"" + appName + "\",\"logo\": \"" + appLogo + "\"}";
            displayLog("custom string " + customString);
            writeToFileCustomize(customString);
            try {
                HashMap<String, String> loans = new HashMap<>();
                loans.put("uniqueId", uniqueId);
                HashMap<String, String> parameters = new HashMap<>();
                parameters.put("eventName", "Android SDK");
                parameters.put("type", "initialize");
                parameters.put("subtype", "customize");
                parameters.put("onObject", "okHeartAndroidSDK");
                parameters.put("view", "app");
                parameters.put("customString ", customString);
                parameters.put("appKey", "" + appkey);
                sendEvent(parameters, loans);
            } catch (Exception e1) {
                displayLog("error attaching afl to ual " + e1.toString());
            }
        } catch (Exception io) {

        } finally {

        }


        try {
            /*
            Boolean production = false;
            if (appkey != null) {
                if (appkey.equalsIgnoreCase("r:b59a93ba7d80a95d89dff8e4c52e259a")) {

                } else {
                    production = true;
                }
            } else {
                production = true;
            }

            final Boolean productionVersion = production;
            displayLog("things went ok with send to omtm identify");

            */
            try {
                io.okverify.android.callback.SegmentTrackCallBack segmentTrackCallBack = new io.okverify.android.callback.SegmentTrackCallBack() {
                    @Override
                    public void querycomplete(String response, boolean status) {
                        if (status) {
                            displayLog("things went ok with send to omtm track");
                        } else {
                            displayLog("something went wrong with send to omtm track");
                        }
                    }
                };
                JSONObject eventjson = new JSONObject();
                //eventjson.put("userId", userId);
                eventjson.put("event", "SDK Initialization");

                JSONObject trackjson = new JSONObject();
                String environment = dataProvider.getPropertyValue("environment");
                trackjson.put("environment", environment);
                /*
                if (environment != null) {
                    if (environment.length() > 0) {
                        if (environment.equalsIgnoreCase("PROD")) {
                            trackjson.put("environment", "PROD");
                        } else if (environment.equalsIgnoreCase("DEVMASTER")) {
                            trackjson.put("environment", "DEVMASTER");
                        } else if (environment.equalsIgnoreCase("SANDBOX")) {
                            trackjson.put("environment", "SANDBOX");
                        } else {
                            trackjson.put("environment", "PROD");
                        }
                    } else {
                        trackjson.put("environment", "PROD");
                    }
                } else {
                    trackjson.put("environment", "PROD");
                }
                */
                /*
                if(productionVersion){
                    trackjson.put("environment", "PROD");
                }
                else if(DEVMASTER){
                    trackjson.put("environment", "DEVMASTER");
                }else if(SANDBOX){
                    trackjson.put("environment", "SANDBOX");
                }
                else{
                    trackjson.put("environment", "PROD");
                }
                */
                trackjson.put("event", "SDK Customize");

                trackjson.put("action", "customization");
                trackjson.put("actionSubtype", "customization");
                trackjson.put("clientProduct", "okHeartAndroidSDK");
                trackjson.put("clientProductVersion", BuildConfig.VERSION_NAME);
                trackjson.put("clientKey", appkey);
                trackjson.put("appLayer", "client");
                trackjson.put("onObject", "sdk");
                trackjson.put("product", "okHeartAndroidSDK");
                trackjson.put("uniqueId", uniqueId);


                eventjson.put("properties", trackjson);
                io.okverify.android.asynctask.SegmentTrackTask segmentTrackTask = new io.okverify.android.asynctask.SegmentTrackTask(segmentTrackCallBack, eventjson, environment);
                segmentTrackTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            } catch (JSONException e) {
                displayLog("track error omtm error " + e.toString());
            }
        } catch (Exception jse) {
            displayLog("jsonexception jse " + jse.toString());
        }


    }

    /*
    public static void displayClient(OkHiCallback okHiCallback, JSONObject jsonObject) {

        displayLog("display client " + jsonObject.toString());

        if(checkPermission()) {
            startActivity(okHiCallback, jsonObject);
        }
        else{
            String cause = checkPermissionCause();
            if((cause.equalsIgnoreCase("Manifest.permission.ACCESS_FINE_LOCATION granted")) ||
                    (cause.equalsIgnoreCase("Manifest.permission.ACCESS_BACKGROUND_LOCATION granted"))){
                startActivity(okHiCallback, jsonObject);
            }
            else{
                if(verify != null){
                    if(verify){
                        try {
                            JSONObject responseJson = new JSONObject();
                            responseJson.put("message", "fatal_exit");
                            JSONObject payloadJson = new JSONObject();
                            payloadJson.put("Error","Location permission not granted");
                            payloadJson.put("message", cause);
                            responseJson.put("payload", payloadJson);
                            displayLog(responseJson.toString());
                            okHiCallback.querycomplete(responseJson);
                        }
                        catch (JSONException jse){

                        }
                    }
                    else{
                        startActivity(okHiCallback, jsonObject);
                    }
                }
                else{
                    startActivity(okHiCallback, jsonObject);
                }
            }
        }
    }
*/

    private static void startActivity(io.okverify.android.callback.OkHiCallback okHiCallback, JSONObject jsonObject) {
        callback = okHiCallback;
        firstname = jsonObject.optString("firstName");
        lastname = jsonObject.optString("lastName");
        phonenumber = jsonObject.optString("phone");
        requestSource = jsonObject.optString("requestSource");

        try {
            HashMap<String, String> loans = new HashMap<>();
            loans.put("phonenumber", phonenumber);
            loans.put("firstname", firstname);
            loans.put("lastname", lastname);
            loans.put("uniqueId", uniqueId);
            HashMap<String, String> parameters = new HashMap<>();
            parameters.put("eventName", "Android SDK");
            parameters.put("type", "initialize");
            parameters.put("subtype", "displayClient");
            parameters.put("onObject", "okHeartAndroidSDK");
            parameters.put("view", "app");
            parameters.put("appKey", "" + appkey);
            sendEvent(parameters, loans);
        } catch (Exception e1) {
            displayLog("error attaching afl to ual " + e1.toString());
        }

        dataProvider.insertStuff("phonenumber", phonenumber);
        dataProvider.insertStuff("requestSource", requestSource);


        try {
            Intent intent = new Intent(mContext, io.okverify.android.activity.OkHeartActivity.class);
            intent.putExtra("firstname", firstname);
            intent.putExtra("lastname", lastname);
            intent.putExtra("phone", phonenumber);
            intent.putExtra("uniqueId", uniqueId);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            mContext.startActivity(intent);
        } catch (Exception e) {
            displayLog("error calling receiveActivity activity " + e.toString());
        }

    }

    private static String checkPermissionCause() {

        String environment = dataProvider.getPropertyValue("environment");
/*
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

*/
        String permission;

        boolean permissionAccessFineLocationApproved =
                ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_FINE_LOCATION)
                        == PackageManager.PERMISSION_GRANTED;

        if (permissionAccessFineLocationApproved) {
            try {
                io.okverify.android.utilities.OkAnalytics okAnalytics = new io.okverify.android.utilities.OkAnalytics(mContext, environment);
                HashMap<String, String> loans = new HashMap<>();
                loans.put("uniqueId", uniqueId);
                loans.put("type", "Manifest.permission.ACCESS_FINE_LOCATION");
                okAnalytics.initializeDynamicParameters("app", "permissionAccessFineLocationApproved",
                        "permission", "mainActivityView", null, loans);
                okAnalytics.sendToAnalytics("app_interswitch", null, null, "interswitch", environment);
            } catch (Exception e) {
                displayLog("event.submit okanalytics error " + e.toString());
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                boolean backgroundLocationPermissionApproved =
                        ActivityCompat.checkSelfPermission(mContext,
                                Manifest.permission.ACCESS_BACKGROUND_LOCATION)
                                == PackageManager.PERMISSION_GRANTED;

                if (backgroundLocationPermissionApproved) {
                    // App can access location both in the foreground and in the background.
                    // Start your service that doesn't have a foreground service type
                    // defined.
                    try {
                        io.okverify.android.utilities.OkAnalytics okAnalytics = new io.okverify.android.utilities.OkAnalytics(mContext, environment);
                        HashMap<String, String> loans = new HashMap<>();
                        loans.put("uniqueId", uniqueId);
                        loans.put("type", " Manifest.permission.ACCESS_BACKGROUND_LOCATION");
                        okAnalytics.initializeDynamicParameters("app", "backgroundLocationPermissionApproved",
                                "permission", "mainActivityView", null, loans);
                        okAnalytics.sendToAnalytics("app_interswitch", null, null, "interswitch", environment);
                    } catch (Exception e) {
                        displayLog("event.submit okanalytics error " + e.toString());
                    }
                    permission = "Manifest.permission.ACCESS_BACKGROUND_LOCATION granted";

                } else {
                    // App can only access location in the foreground. Display a dialog
                    // warning the user that your app must have all-the-time access to
                    // location in order to function properly. Then, request background
                    // location.
                    try {
                        io.okverify.android.utilities.OkAnalytics okAnalytics = new io.okverify.android.utilities.OkAnalytics(mContext, environment);
                        HashMap<String, String> loans = new HashMap<>();
                        loans.put("uniqueId", uniqueId);
                        loans.put("type", " Manifest.permission.ACCESS_BACKGROUND_LOCATION");
                        okAnalytics.initializeDynamicParameters("app", "backgroundLocationPermissionNotApproved",
                                "permission", "mainActivityView", null, loans);
                        okAnalytics.sendToAnalytics("app_interswitch", null, null, "interswitch", environment);
                    } catch (Exception e) {
                        displayLog("event.submit okanalytics error " + e.toString());
                    }
                    permission = "Manifest.permission.ACCESS_BACKGROUND_LOCATION not granted";
                }
            } else {
                permission = "Manifest.permission.ACCESS_FINE_LOCATION granted";
            }
        } else {
            // App doesn't have access to the device's location at all. Make full request
            // for permission.
            try {
                io.okverify.android.utilities.OkAnalytics okAnalytics = new io.okverify.android.utilities.OkAnalytics(mContext, environment);
                HashMap<String, String> loans = new HashMap<>();
                loans.put("uniqueId", uniqueId);
                loans.put("type", "Manifest.permission.ACCESS_FINE_LOCATION");
                okAnalytics.initializeDynamicParameters("app", "permissionAccessFineLocationNotApproved",
                        "permission", "mainActivityView", null, loans);
                okAnalytics.sendToAnalytics("app_interswitch", null, null, "interswitch", environment);
            } catch (Exception e) {
                displayLog("event.submit okanalytics error " + e.toString());
            }
            permission = "Manifest.permission.ACCESS_FINE_LOCATION not granted";
        }
        return permission;
    }

    public static boolean checkPermission() {
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

        Boolean permission;

        boolean permissionAccessFineLocationApproved =
                ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_FINE_LOCATION)
                        == PackageManager.PERMISSION_GRANTED;

        if (permissionAccessFineLocationApproved) {
            try {
                io.okverify.android.utilities.OkAnalytics okAnalytics = new io.okverify.android.utilities.OkAnalytics(mContext, environment);
                HashMap<String, String> loans = new HashMap<>();
                loans.put("uniqueId", uniqueId);
                loans.put("type", "Manifest.permission.ACCESS_FINE_LOCATION");
                okAnalytics.initializeDynamicParameters("app", "permissionAccessFineLocationApproved",
                        "permission", "mainActivityView", null, loans);
                okAnalytics.sendToAnalytics("app_interswitch", null, null, "interswitch", environment);
            } catch (Exception e) {
                displayLog("event.submit okanalytics error " + e.toString());
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                boolean backgroundLocationPermissionApproved =
                        ActivityCompat.checkSelfPermission(mContext,
                                Manifest.permission.ACCESS_BACKGROUND_LOCATION)
                                == PackageManager.PERMISSION_GRANTED;

                if (backgroundLocationPermissionApproved) {
                    // App can access location both in the foreground and in the background.
                    // Start your service that doesn't have a foreground service type
                    // defined.
                    try {
                        io.okverify.android.utilities.OkAnalytics okAnalytics = new io.okverify.android.utilities.OkAnalytics(mContext, environment);
                        HashMap<String, String> loans = new HashMap<>();
                        loans.put("uniqueId", uniqueId);
                        loans.put("type", "Manifest.permission.ACCESS_FINE_LOCATION");
                        okAnalytics.initializeDynamicParameters("app", "backgroundLocationPermissionApproved",
                                "permission", "mainActivityView", null, loans);
                        okAnalytics.sendToAnalytics("app_interswitch", null, null, "interswitch", environment);
                    } catch (Exception e) {
                        displayLog("event.submit okanalytics error " + e.toString());
                    }
                    permission = true;

                } else {
                    // App can only access location in the foreground. Display a dialog
                    // warning the user that your app must have all-the-time access to
                    // location in order to function properly. Then, request background
                    // location.
                    try {
                        io.okverify.android.utilities.OkAnalytics okAnalytics = new io.okverify.android.utilities.OkAnalytics(mContext, environment);
                        HashMap<String, String> loans = new HashMap<>();
                        loans.put("uniqueId", uniqueId);
                        loans.put("type", "Manifest.permission.ACCESS_FINE_LOCATION");
                        okAnalytics.initializeDynamicParameters("app", "backgroundLocationPermissionNotApproved",
                                "permission", "mainActivityView", null, loans);
                        okAnalytics.sendToAnalytics("app_interswitch", null, null, "interswitch", environment);
                    } catch (Exception e) {
                        displayLog("event.submit okanalytics error " + e.toString());
                    }
                    permission = false;
                }
            } else {
                permission = true;
            }
        } else {
            // App doesn't have access to the device's location at all. Make full request
            // for permission.
            try {
                io.okverify.android.utilities.OkAnalytics okAnalytics = new io.okverify.android.utilities.OkAnalytics(mContext, environment);
                HashMap<String, String> loans = new HashMap<>();
                loans.put("uniqueId", uniqueId);
                loans.put("type", "Manifest.permission.ACCESS_FINE_LOCATION");
                okAnalytics.initializeDynamicParameters("app", "permissionAccessFineLocationNotApproved",
                        "permission", "mainActivityView", null, loans);
                okAnalytics.sendToAnalytics("app_interswitch", null, null, "interswitch", environment);
            } catch (Exception e) {
                displayLog("event.submit okanalytics error " + e.toString());
            }
            permission = false;
        }
        return permission;
    }
    /*
        public static void manualPing(@NonNull io.okverify.android.callback.OkHiCallback okHiCallback, @NonNull JSONObject jsonObject) {

            displayLog("display client " + jsonObject.toString());


            callback = okHiCallback;
            firstname = jsonObject.optString("firstName");
            lastname = jsonObject.optString("lastName");
            phonenumber = jsonObject.optString("phone");

            try {
                HashMap<String, String> loans = new HashMap<>();
                loans.put("phonenumber", phonenumber);
                loans.put("firstname", firstname);
                loans.put("lastname", lastname);
                loans.put("uniqueId", uniqueId);
                HashMap<String, String> parameters = new HashMap<>();
                parameters.put("eventName", "Android SDK");
                parameters.put("type", "initialize");
                parameters.put("subtype", "manualPing");
                parameters.put("onObject", "okHeartAndroidSDK");
                parameters.put("view", "app");
                parameters.put("appKey", "" + appkey);
                sendEvent(parameters, loans);
            } catch (Exception e1) {
                displayLog("error attaching afl to ual " + e1.toString());
            }

            String tempPhonenumber = null;
            if (phonenumber != null) {
                if (phonenumber.length() > 0) {
                    if (phonenumber.startsWith("0")) {
                        tempPhonenumber = "+254" + phonenumber.substring(1);
                    } else {
                        tempPhonenumber = phonenumber;
                    }
                    List<io.okverify.android.datamodel.AddressItem> addressItemList = dataProvider.getAllAddressList();
                    String environment = dataProvider.getPropertyValue("environment");
                    if (addressItemList.size() > 0) {
                        sendPingSMS(okHiCallback, tempPhonenumber, environment);
                    } else {
                        try {
                            JSONObject responseJson = new JSONObject();
                            responseJson.put("message", "sms_failure");
                            JSONObject payloadJson = new JSONObject();
                            payloadJson.put("errorCode", -1);
                            payloadJson.put("error", "Missing addresses");
                            payloadJson.put("message", "Please create at least one address");
                            responseJson.put("payload", payloadJson);
                            displayLog(responseJson.toString());
                            okHiCallback.querycomplete(responseJson);
                        } catch (JSONException jse) {

                        }
                    }
                } else {
                    try {
                        JSONObject responseJson = new JSONObject();
                        responseJson.put("message", "fatal_exit");
                        JSONObject payloadJson = new JSONObject();
                        payloadJson.put("errorCode", -1);
                        payloadJson.put("error", "Missing parameter");
                        payloadJson.put("message", "Phone number cannot be an empty string");
                        responseJson.put("payload", payloadJson);
                        displayLog(responseJson.toString());
                        okHiCallback.querycomplete(responseJson);
                    } catch (JSONException jse) {

                    }
                }
            } else {
                try {
                    JSONObject responseJson = new JSONObject();
                    responseJson.put("message", "fatal_exit");
                    JSONObject payloadJson = new JSONObject();
                    payloadJson.put("errorCode", -1);
                    payloadJson.put("error", "Missing parameter");
                    payloadJson.put("message", "Phone number cannot be null");
                    responseJson.put("payload", payloadJson);
                    displayLog(responseJson.toString());
                    okHiCallback.querycomplete(responseJson);
                } catch (JSONException jse) {

                }
            }
        }

        private static void sendPingSMS(final io.okverify.android.callback.OkHiCallback okHiCallback, String phonenumber, String environment) {
            try {
                String remoteSmsTemplate = dataProvider.getPropertyValue("sms_template");
                String message = remoteSmsTemplate + uniqueId;
                final HashMap<String, String> jsonObject = new HashMap<>();
                jsonObject.put("userId", "GrlaR3LHUP");
                jsonObject.put("sessionToken", "r:3af107bf99e4c6f2a91e6fec046f5fc7");
                jsonObject.put("customName", "test");
                //jsonObject.put("ualId", verifyDataItem.getUalId());

                jsonObject.put("phoneNumber", phonenumber);
                jsonObject.put("phone", phonenumber);
                jsonObject.put("message", message);
                jsonObject.put("uniqueId", uniqueId);
                io.okverify.android.callback.SendCustomLinkSmsCallBack sendCustomLinkSmsCallBack = new io.okverify.android.callback.SendCustomLinkSmsCallBack() {
                    @Override
                    public void querycomplete(String response, boolean status) {
                        if (status) {
                            //displayLog("send sms success " + response);
                            try {
                                JSONObject responseJson = new JSONObject();
                                responseJson.put("message", "sendSMS");
                                JSONObject payloadJson = new JSONObject();
                                payloadJson.put("errorCode", 0);
                                payloadJson.put("error", "SMS sent");
                                payloadJson.put("message", "SMS sent");
                                responseJson.put("payload", payloadJson);
                                displayLog(responseJson.toString());
                                okHiCallback.querycomplete(responseJson);
                            } catch (JSONException jse) {

                            }
                        } else {
                            displayLog("Error! " + response);
                            try {
                                JSONObject responseJson = new JSONObject();
                                responseJson.put("message", "sendSMS");
                                JSONObject payloadJson = new JSONObject();
                                payloadJson.put("errorCode", 0);
                                payloadJson.put("error", "SMS not sent");
                                payloadJson.put("message", response);
                                responseJson.put("payload", payloadJson);
                                displayLog(responseJson.toString());
                                okHiCallback.querycomplete(responseJson);
                            } catch (JSONException jse) {

                            }
                        }
                    }
                };
                io.okverify.android.asynctask.SendCustomLinkSmsTask sendCustomLinkSmsTask = new io.okverify.android.asynctask.SendCustomLinkSmsTask(mContext, sendCustomLinkSmsCallBack, jsonObject, environment);
                sendCustomLinkSmsTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            } catch (Exception jse) {
                displayLog("jsonexception " + jse.toString());
            }
        }
    */
    private static void displayLog(String log) {
        Log.i(TAG, log);
    }

    private static void writeToFile(String customString) {
        try {
            File path = mContext.getFilesDir();
            File file = new File(path, "okverify.txt");
            if (!file.exists()) {
                FileOutputStream stream = new FileOutputStream(file);
                try {

                    stream.write(customString.getBytes());
                } catch (Exception e) {
                    displayLog("filestream error " + e.toString());
                } finally {
                    stream.close();
                }
            } else {
                file.delete();
                FileOutputStream stream = new FileOutputStream(file);
                try {

                    stream.write(customString.getBytes());
                } catch (Exception e) {
                    displayLog("filestream error " + e.toString());
                } finally {
                    stream.close();
                }
            }

        } catch (Exception e) {
            displayLog("write to file error " + e.toString());

        }

    }

    private static void writeToFileCustomize(String apiKey) {
        try {
            File path = mContext.getFilesDir();
            File file = new File(path, "custom.txt");
            if (!file.exists()) {
                FileOutputStream stream = new FileOutputStream(file);
                try {

                    stream.write(apiKey.getBytes());
                } catch (Exception e) {
                    displayLog("filestream error " + e.toString());
                } finally {
                    stream.close();
                }
            } else {
                file.delete();
                FileOutputStream stream = new FileOutputStream(file);
                try {

                    stream.write(apiKey.getBytes());
                } catch (Exception e) {
                    displayLog("filestream error " + e.toString());
                } finally {
                    stream.close();
                }
            }

        } catch (Exception e) {
            displayLog("write to file error " + e.toString());

        }

    }

    private static void writeToFileVerify(String apiKey, String who) {
        try {
            File path = mContext.getFilesDir();
            File file = new File(path, "verify.txt");
            if (!file.exists()) {
                FileOutputStream stream = new FileOutputStream(file);
                try {

                    stream.write(apiKey.getBytes());
                } catch (Exception e) {
                    displayLog("filestream error " + e.toString());
                } finally {
                    stream.close();
                }
            } else {
                file.delete();
                FileOutputStream stream = new FileOutputStream(file);
                try {

                    stream.write(apiKey.getBytes());
                } catch (Exception e) {
                    displayLog("filestream error " + e.toString());
                } finally {
                    stream.close();
                }
            }

            dataProvider.insertStuff("verify", "" + apiKey);

            displayLog(who + "okhi done writing verify " + apiKey);
        } catch (Exception e) {
            displayLog("write to file error " + e.toString());

        }

    }

    public static io.okverify.android.callback.OkHiCallback getCallback() {
        return callback;
    }

    public static void setCallback(io.okverify.android.callback.OkHiCallback callback) {
        OkHi.callback = callback;
    }

    public static void requestPermission(@NonNull Activity activity, @NonNull int MY_PERMISSIONS_ACCESS_FINE_LOCATION) {
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


        Boolean permission = false;

        boolean permissionAccessFineLocationApproved =
                ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_FINE_LOCATION)
                        == PackageManager.PERMISSION_GRANTED;

        if (permissionAccessFineLocationApproved) {
            try {
                io.okverify.android.utilities.OkAnalytics okAnalytics = new io.okverify.android.utilities.OkAnalytics(mContext, environment);
                HashMap<String, String> loans = new HashMap<>();
                loans.put("uniqueId", uniqueId);
                loans.put("type", "Manifest.permission.ACCESS_FINE_LOCATION");
                okAnalytics.initializeDynamicParameters("app", "permissionAccessFineLocationApproved",
                        "requestPermission", activity.getLocalClassName(), null, loans);
                okAnalytics.sendToAnalytics("app_interswitch", null, null, "interswitch", environment);
            } catch (Exception e) {
                displayLog("event.submit okanalytics error " + e.toString());
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                boolean backgroundLocationPermissionApproved =
                        ActivityCompat.checkSelfPermission(mContext,
                                Manifest.permission.ACCESS_BACKGROUND_LOCATION)
                                == PackageManager.PERMISSION_GRANTED;

                if (backgroundLocationPermissionApproved) {
                    // App can access location both in the foreground and in the background.
                    // Start your service that doesn't have a foreground service type
                    // defined.
                    try {
                        io.okverify.android.utilities.OkAnalytics okAnalytics = new io.okverify.android.utilities.OkAnalytics(mContext, environment);
                        HashMap<String, String> loans = new HashMap<>();
                        loans.put("uniqueId", uniqueId);
                        loans.put("type", "Manifest.permission.ACCESS_FINE_LOCATION");
                        okAnalytics.initializeDynamicParameters("app", "backgroundLocationPermissionApproved",
                                "requestPermission", activity.getLocalClassName(), null, loans);
                        okAnalytics.sendToAnalytics("app_interswitch", null, null, "interswitch", environment);
                    } catch (Exception e) {
                        displayLog("event.submit okanalytics error " + e.toString());
                    }
                    //Constants.scheduleJob(MainActivity.this);
                    //checkLocationSettings();
                } else {
                    // App can only access location in the foreground. Display a dialog
                    // warning the user that your app must have all-the-time access to
                    // location in order to function properly. Then, request background
                    // location.
                    try {
                        io.okverify.android.utilities.OkAnalytics okAnalytics = new io.okverify.android.utilities.OkAnalytics(mContext, environment);
                        HashMap<String, String> loans = new HashMap<>();
                        loans.put("uniqueId", uniqueId);
                        loans.put("type", "Manifest.permission.ACCESS_FINE_LOCATION");
                        okAnalytics.initializeDynamicParameters("app", "backgroundLocationPermissionNotApproved",
                                "permission", "mainActivityView", null, loans);
                        okAnalytics.sendToAnalytics("app_interswitch", null, null, "interswitch", environment);
                    } catch (Exception e) {
                        displayLog("event.submit okanalytics error " + e.toString());
                    }
                    ActivityCompat.requestPermissions(activity, new String[]{
                                    Manifest.permission.ACCESS_BACKGROUND_LOCATION},
                            MY_PERMISSIONS_ACCESS_FINE_LOCATION);
                }
            } else {
                permission = true;

            }
        } else {
            // App doesn't have access to the device's location at all. Make full request
            // for permission.
            try {
                io.okverify.android.utilities.OkAnalytics okAnalytics = new io.okverify.android.utilities.OkAnalytics(mContext, environment);
                HashMap<String, String> loans = new HashMap<>();
                loans.put("uniqueId", uniqueId);
                loans.put("type", "Manifest.permission.ACCESS_FINE_LOCATION");
                okAnalytics.initializeDynamicParameters("app", "permissionAccessFineLocationNotApproved",
                        "requestPermission", activity.getLocalClassName(), null, loans);
                okAnalytics.sendToAnalytics("app_interswitch", null, null, "interswitch", environment);
            } catch (Exception e) {
                displayLog("event.submit okanalytics error " + e.toString());
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                ActivityCompat.requestPermissions(activity, new String[]{
                                Manifest.permission.ACCESS_FINE_LOCATION,
                                Manifest.permission.ACCESS_BACKGROUND_LOCATION
                        },
                        MY_PERMISSIONS_ACCESS_FINE_LOCATION);
            } else {
                ActivityCompat.requestPermissions(activity, new String[]{
                                Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_ACCESS_FINE_LOCATION);
            }

        }
    }

    private static void sendEvent(HashMap<String, String> parameters, HashMap<String, String> loans) {
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

            io.okverify.android.utilities.OkAnalytics okAnalytics = new io.okverify.android.utilities.OkAnalytics(mContext, environment);
            okAnalytics.sendToAnalytics(parameters, loans, environment);
        } catch (Exception e) {
            displayLog("error sending photoexpanded analytics event " + e.toString());
        }
    }
    /*

    private static void decideWhatToStart() {
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
    */

    private static void displayToast(String msg, boolean show) {
        if (show) {
            try {
                Toast toast = Toast.makeText(mContext,
                        msg, Toast.LENGTH_LONG);
                toast.setGravity(Gravity.CENTER_HORIZONTAL, 0, 0);
                toast.show();
            } catch (Exception e) {
                displayLog("Enable data toast error " + e.toString());
            }
        }
    }

    private static String convertStreamToString(InputStream is) throws IOException {
        displayLog("convertStreamToString1");
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();
        String line = null;
        Boolean firstLine = true;
        while ((line = reader.readLine()) != null) {
            if (firstLine) {
                sb.append(line);
                firstLine = false;
            } else {
                sb.append("\n").append(line);
            }
        }
        reader.close();
        displayLog("convertStreamToString2");
        return sb.toString();
    }

    private static String getStringFromFile(String filePath) throws IOException {
        displayLog("getStringFromFile1");
        File fl = new File(filePath);
        FileInputStream fin = new FileInputStream(fl);
        String ret = convertStreamToString(fin);
        //Make sure you close all streams.
        fin.close();
        displayLog("getStringFromFile2");
        return ret;
    }

    private void triggerSpecial() {

    }

  /*
    private static void stopPeriodicPing() {

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
        WorkManager.getInstance().cancelUniqueWork("ramogi");

    }

    private static void startKeepPeriodicPing(Integer pingTime, String uniqueId) {
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

            WorkManager.getInstance().enqueueUniquePeriodicWork("ramogi", ExistingPeriodicWorkPolicy.KEEP, request);

        } catch (Exception e) {
            displayLog("my worker error " + e.toString());
        }
    }

    private static void startReplacePeriodicPing(Integer pingTime, String uniqueId) {
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

            WorkManager.getInstance().enqueueUniquePeriodicWork("ramogi", ExistingPeriodicWorkPolicy.REPLACE, request);


        } catch (Exception e) {
            displayLog("my worker error " + e.toString());
        }
    }
*/

/*
    private void updateDatabase() {

        List<io.okverify.android.datamodel.AddressItem> addressItemList = dataProvider.getAllAddressList();

        if (addressItemList.size() > 0) {
            List<Map<String, Object>> addresses = new ArrayList<>();


            HashMap<String, String> loans = new HashMap<>();
            loans.put("uniqueId", uniqueId);
            HashMap<String, String> parameters = new HashMap<>();

            final Long timemilliseconds = System.currentTimeMillis();

            final ParseObject parseObject = new ParseObject("UserVerificationData");

            /*
            parseObject.put("latitude", lat);
            parseObject.put("longitude", lng);
            parseObject.put("gpsAccuracy", acc);
            ParseGeoPoint parseGeoPoint = new ParseGeoPoint(lat, lng);
            parseObject.put("geoPoint", parseGeoPoint);
            */
/*
            parseObject.put("geoPointSource", "webClientBackgroundGPS");
            parseObject.put("timemilliseconds", timemilliseconds);
            parseObject.put("device", getDeviceModelAndBrand());
            parseObject.put("model", Build.MODEL);
            parseObject.put("brand", Build.MANUFACTURER);
            parseObject.put("OSVersion", Build.VERSION.SDK_INT);
            parseObject.put("OSName", "Android");
            parseObject.put("appVersionCode", io.okverify.android.BuildConfig.VERSION_CODE);
            parseObject.put("appVersionName", io.okverify.android.BuildConfig.VERSION_NAME);

            try {
                WifiManager wifiManager = (WifiManager) mContext.getSystemService(Context.WIFI_SERVICE);
                WifiInfo info = wifiManager.getConnectionInfo();
                String ssid = info.getSSID();
                displayLog("ssid " + ssid);
                if (ssid.contains("unknown")) {

                } else {
                    if (ssid.length() > 0) {
                        displayLog("ssid " + ssid.substring(1, ssid.length() - 1));
                        parameters.put("ssid", ssid);
                        parseObject.put("ssid", ssid);
                    } else {

                    }
                }


                try {
                    List<String> configuredSSIDList = new ArrayList<>();
                    //List<String> scannedSSIDList = new ArrayList<>();
                    List<WifiConfiguration> configuredList = wifiManager.getConfiguredNetworks();
                    //List<ScanResult> scanResultList = wifiManager.getScanResults();
                    //displayLog("configured list size "+configuredList.size());
                    //displayLog("scanned list size "+scanResultList.size());
                    for (WifiConfiguration config : configuredList) {
                        //displayLog("configured list "+config.SSID);
                        configuredSSIDList.add(config.SSID);
                    }
                    if (configuredSSIDList != null) {
                        if (configuredSSIDList.size() > 0) {
                            parameters.put("configuredSSIDs", configuredSSIDList.toString());
                        }
                    }
                    parseObject.put("configuredSSIDs", configuredSSIDList);

                } catch (Exception e) {
                    displayLog("error gettign scanned list " + e.toString());
                }


            } catch (Exception e) {
                displayLog(" error getting wifi info " + e.toString());
            }

            try{
                String phonenumber = dataProvider.getPropertyValue("phonenumber");
                if(phonenumber != null){
                    if(phonenumber.length() > 0){
                        parseObject.put("phonenumber", phonenumber);
                    }
                }
            }
            catch (Exception e){
                displayLog("error getting phonenumber "+e.toString());
            }


            try {
                boolean isPlugged = false;
                BatteryManager bm = (BatteryManager) mContext.getSystemService(BATTERY_SERVICE);
                int batLevel = bm.getIntProperty(BatteryManager.BATTERY_PROPERTY_CAPACITY);
                parameters.put("batteryLevel", "" + batLevel);
                parseObject.put("batteryLevel", batLevel);

                Intent intent = mContext.registerReceiver(null, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
                int plugged = intent.getIntExtra(BatteryManager.EXTRA_PLUGGED, -1);
                isPlugged = plugged == BatteryManager.BATTERY_PLUGGED_AC || plugged == BatteryManager.BATTERY_PLUGGED_USB;
                if (Build.VERSION.SDK_INT > Build.VERSION_CODES.JELLY_BEAN) {
                    isPlugged = isPlugged || plugged == BatteryManager.BATTERY_PLUGGED_WIRELESS;
                }
                parameters.put("isPlugged", "" + isPlugged);
                parseObject.put("isPlugged", isPlugged);

                // Are we charging / charged?
                int status = intent.getIntExtra(BatteryManager.EXTRA_STATUS, -1);
                boolean isCharging = status == BatteryManager.BATTERY_STATUS_CHARGING ||
                        status == BatteryManager.BATTERY_STATUS_FULL;
                parameters.put("isCharging", "" + isCharging);
                parseObject.put("isCharging", isCharging);

                // How are we charging?
                int chargePlug = intent.getIntExtra(BatteryManager.EXTRA_PLUGGED, -1);
                boolean usbCharge = chargePlug == BatteryManager.BATTERY_PLUGGED_USB;
                parameters.put("usbCharge", "" + usbCharge);
                parseObject.put("usbCharge", usbCharge);
                boolean acCharge = chargePlug == BatteryManager.BATTERY_PLUGGED_AC;
                parameters.put("acCharge", "" + acCharge);
                parseObject.put("acCharge", acCharge);

            } catch (Exception e) {
                displayLog(" error getting battery status " + e.toString());
            }

            parseObject.put("uniqueId", uniqueId);
            parameters.put("uniqueId", uniqueId);

            try {

                parameters.put("uniqueId", uniqueId);
                parameters.put("cookieToken", uniqueId);
                parameters.put("eventName", "Data collection Service");
                parameters.put("subtype", "saveBackgroundData");
                parameters.put("type", "saveData");
                parameters.put("onObject", "backgroundService");
                parameters.put("view", "worker");
                parameters.put("branch", "app_interswitch");
                //parameters.put("deliveryId", null);
                //parameters.put("ualId", addressParseObject.getClaimUalId());
                parameters.put("userAffiliation", "interswitch");

                /*
                parameters.put("latitude", "" + lat);
                parameters.put("longitude", "" + lng);
                parameters.put("gpsAccuracy", "" + acc);
                try {
                    Location location2 = new Location("geohash");
                    location2.setLatitude(lat);
                    location2.setLongitude(lng);

                    io.okverify.android.utilities.geohash.GeoHash hash = io.okverify.android.utilities.geohash.GeoHash.fromLocation(location2, 12);
                    parameters.put("location", hash.toString());
                } catch (Exception e) {
                    displayLog("geomap error " + e.toString());
                }
                */
/*
                parameters.put("geoPointSource", "clientBackgroundGPS");
                parameters.put("timemilliseconds", "" + timemilliseconds);
                parameters.put("device", getDeviceModelAndBrand());
                parameters.put("model", Build.MODEL);
                parameters.put("brand", Build.MANUFACTURER);
                parameters.put("OSVersion", "" + Build.VERSION.SDK_INT);
                parameters.put("OSName", "Android");
                parameters.put("appVersionCode", "" + io.okverify.android.BuildConfig.VERSION_CODE);
                parameters.put("appVersionName", "" + io.okverify.android.BuildConfig.VERSION_NAME);
                sendEvent(parameters, loans);
            } catch (Exception e1) {
                displayLog("error attaching afl to ual " + e1.toString());
            }


            for (int i = 0; i < addressItemList.size(); i++) {
                try {
                    io.okverify.android.datamodel.AddressItem addressItem = addressItemList.get(i);
                    //Float distance = getDistance(lat, lng, addressItem.getLat(), addressItem.getLng());
                    Map<String, Object> nestedData = new HashMap<>();
                    nestedData.put("ualId", addressItem.getUalid());
                    nestedData.put("latitude", addressItem.getLat());
                    nestedData.put("longitude", addressItem.getLng());
                    if (distance < 100.0) {
                        nestedData.put("verified", true);
                    } else {
                        nestedData.put("verified", false);
                    }

                    nestedData.put("distance", distance);
                    HashMap<String, String> paramText = getTitleText(addressItem);
                    nestedData.put("title", paramText.get("header"));
                    nestedData.put("text", paramText.get("text"));
                    addresses.add(nestedData);
                } catch (Exception e) {

                }
            }
            parseObject.put("addresses", addresses);
            saveData(parseObject);
        } else {
            //add an event here saying we don't have addresses
            //saveData(parseObject,  timemilliseconds);
            //stopSelf();
        }

    }
*/

/*

    private String getDeviceModelAndBrand() {

        String manufacturer = Build.MANUFACTURER;
        String model = Build.MODEL;
        if (model.contains(manufacturer)) {
            return model;
        } else {
            return manufacturer + " " + model;
        }

    }

    private void saveData(ParseObject parseObject) {

        displayLog("parse object save");
        parseObject.saveEventually(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e == null) {
                    displayLog("save parseobject success ");
                    try {
                        HashMap<String, String> loans = new HashMap<>();
                        loans.put("uniqueId", uniqueId);
                        HashMap<String, String> parameters = new HashMap<>();
                        parameters.put("eventName", "Data Collection Service");
                        parameters.put("subtype", "saveData");
                        parameters.put("type", "parse");
                        parameters.put("onObject", "success");
                        parameters.put("view", "worker");
                        sendEvent(parameters, loans);
                    } catch (Exception e1) {
                        displayLog("error attaching afl to ual " + e1.toString());
                    }

                } else {
                    try {
                        HashMap<String, String> loans = new HashMap<>();
                        loans.put("uniqueId", uniqueId);
                        HashMap<String, String> parameters = new HashMap<>();
                        parameters.put("eventName", "Data Collection Service");
                        parameters.put("subtype", "saveData");
                        parameters.put("type", "parse");
                        parameters.put("onObject", "failure");
                        parameters.put("view", "worker");
                        parameters.put("error", e.toString());
                        sendEvent(parameters, loans);
                    } catch (Exception e1) {
                        displayLog("error attaching afl to ual " + e1.toString());
                    }
                    displayLog("save parseobject error " + e.toString());
                }
            }
        });
    }


    private Float getDistance(Double latA, Double lngA, Double latB, Double lngB) {

        Location locationA = new Location("point A");

        locationA.setLatitude(latA);
        locationA.setLongitude(lngA);

        Location locationB = new Location("point B");

        locationB.setLatitude(latB);
        locationB.setLongitude(lngB);

        Float me = locationA.distanceTo(locationB);
        displayLog("getDistance " + latA + " " + lngA + " " + latB + " " + lngB + " distance " + me);
        return me;

    }


    private HashMap<String, String> getTitleText(io.okverify.android.datamodel.AddressItem model) {

        String streetName = model.getStreetName();
        String propertyName = model.getPropname();
        String directions = model.getDirection();
        String title = model.getLocationName();

        displayLog(streetName + " " + propertyName + " " + directions + " " + title);

        HashMap<String, String> titleText = new HashMap<>();

        String header = "";
        String text = "";
        if (streetName != null) {
            if (streetName.length() > 0) {
                if (!(streetName.equalsIgnoreCase("null"))) {
                    text = streetName;
                } else {

                    if (directions != null) {
                        if (directions.length() > 0) {
                            if (!(directions.equalsIgnoreCase("null"))) {
                                text = directions;
                            }
                        }
                    }
                }
            } else {
                if (directions != null) {
                    if (directions.length() > 0) {
                        if (!(directions.equalsIgnoreCase("null"))) {
                            text = directions;
                        }
                    }
                }
            }
        } else {
            if (directions != null) {
                if (directions.length() > 0) {
                    if (!(directions.equalsIgnoreCase("null"))) {
                        text = directions;
                    }
                }
            }
        }

        if (title != null) {
            if (title.length() > 0) {
                if (!(title.equalsIgnoreCase("null"))) {
                    header = title;
                } else {

                    if (propertyName != null) {
                        if (propertyName.length() > 0) {
                            if (!(propertyName.equalsIgnoreCase("null"))) {
                                header = propertyName;
                            }
                        }
                    }
                }
            } else {
                if (propertyName != null) {
                    if (propertyName.length() > 0) {
                        if (!(propertyName.equalsIgnoreCase("null"))) {
                            header = propertyName;
                        }
                    }
                }
            }
        } else {
            if (propertyName != null) {
                if (propertyName.length() > 0) {
                    if (!(propertyName.equalsIgnoreCase("null"))) {
                        header = propertyName;
                    }
                }
            }
        }
        titleText.put("header", header);
        titleText.put("text", text);
        displayLog("titletext " + titleText.get("header") + " " + titleText.get("text"));
        return titleText;
    }

    public static Integer getResume_ping_frequency() {
        return resume_ping_frequency;
    }


    public static void setResume_ping_frequency(Integer resume_ping_frequency) {
        OkHi.resume_ping_frequency = resume_ping_frequency;
    }

    public static Integer getPing_frequency() {
        return ping_frequency;
    }

    public static void setPing_frequency(Integer ping_frequency) {
        OkHi.ping_frequency = ping_frequency;
    }

    public static Integer getBackground_frequency() {
        return background_frequency;
    }

    public static void setBackground_frequency(Integer background_frequency) {
        OkHi.background_frequency = background_frequency;
    }

    public static String getSms_template() {
        return sms_template;
    }

    public static void setSms_template(String sms_template) {
        OkHi.sms_template = sms_template;
    }

    public static Double getGps_accuracy() {
        return gps_accuracy;
    }

    public static void setGps_accuracy(Double gps_accuracy) {
        OkHi.gps_accuracy = gps_accuracy;
    }

    public static Boolean getKill_switch() {
        return kill_switch;
    }

    public static void setKill_switch(Boolean kill_switch) {
        OkHi.kill_switch = kill_switch;
    }

    */

    @Nullable
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        return null;
    }

    @Nullable
    @Override
    public String getType(Uri uri) {
        return null;
    }

    @Nullable
    @Override
    public Uri insert(Uri uri, ContentValues values) {
        return null;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        return 0;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        return 0;
    }

    @Override
    public boolean onCreate() {
        // get the context (Application context)
        mContext = getContext();
        dataProvider = new io.okverify.android.database.DataProvider(mContext);

        try {
            analytics = new Analytics.Builder(mContext, io.okverify.android.utilities.Constants.ANALYTICS_WRITE_KEY).build();
            Analytics.setSingletonInstance(analytics);
        } catch (Exception e) {
            displayLog("Error initializing analytics " + e.toString());
        }

        uniqueId = Settings.Secure.getString(mContext.getContentResolver(), Settings.Secure.ANDROID_ID);



        /*
        List<io.okverify.android.datamodel.AddressItem> addressItemList = dataProvider.getAllAddressList();
        displayLog("foreground addressItemList " + addressItemList.size());
        if (addressItemList.size() > 0) {
            try {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    mContext.startForegroundService(new Intent(mContext, io.okverify.android.services.ForegroundService.class));
                } else {
                    mContext.startService(new Intent(mContext, io.okverify.android.services.ForegroundService.class));
                }

            } catch (Exception jse) {
                displayLog("jsonexception jse " + jse.toString());
            }
        } else {
            //we have no addresses to start foreground
            displayLog("we have no addresses to start foreground");
        }
        */


        try {
            /*
            Constraints constraints = new Constraints.Builder()
                    .setRequiredNetworkType(NetworkType.CONNECTED)
                    .build();

            Data inputData = new Data.Builder()
                    .putString("uniqueId", uniqueId)
                    .build();

            PeriodicWorkRequest request =
                    new PeriodicWorkRequest.Builder(MyWorker.class, 1800000, TimeUnit.MILLISECONDS)
                            .setInputData(inputData)
                            .setConstraints(constraints)
                            .setBackoffCriteria(BackoffPolicy.LINEAR, 30, TimeUnit.SECONDS)
                            .build();

            WorkManager.getInstance().enqueueUniquePeriodicWork("ramogi", ExistingPeriodicWorkPolicy.KEEP, request);
            */

            /*
            Constraints constraints = new Constraints.Builder()
                    .build();

            Data inputData = new Data.Builder()
                    .putString("uniqueId", uniqueId)
                    .build();

            PeriodicWorkRequest request =
                    new PeriodicWorkRequest.Builder(MyWorker.class, 900, TimeUnit.SECONDS)
                            .setInputData(inputData)
                            .setConstraints(constraints)
                            .setBackoffCriteria(BackoffPolicy.LINEAR, 900, TimeUnit.SECONDS)
                            .build();

            WorkManager.getInstance().enqueueUniquePeriodicWork("ramogi", ExistingPeriodicWorkPolicy.KEEP, request);
            */


        } catch (Exception e) {
            displayLog("my worker error " + e.toString());
        }

        return true;
    }

    /*
    private static void startForegroundNotification2() {

        displayLog("startForegroundNotification");


        Bitmap largeIconBitmap = BitmapFactory.decodeResource(mContext.getResources(), io.okverify.android.R.drawable.ic_launcher_foreground);

        String channelId = mContext.getString(io.okverify.android.R.string.default_notification_channel_id);
        Intent playIntent = new Intent(mContext, SettingsActivity.class);
        //playIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

        PendingIntent pendingIntent = PendingIntent.getActivity(mContext, 0, playIntent, 0);
        NotificationCompat.Action playAction = new NotificationCompat.Action(android.R.drawable.ic_media_play,
                HtmlCompat.fromHtml("<font color=\"" + ContextCompat.getColor(mContext, R.color.colorPrimary) +
                        "\">HIDE</font>", HtmlCompat.FROM_HTML_MODE_LEGACY), pendingIntent);


        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder =
                new NotificationCompat.Builder(mContext, channelId)
                        .setSmallIcon(io.okverify.android.R.drawable.ic_stat_ic_notification)
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


    }
    */

/*
    private static boolean restrictBackgroundData(boolean enable, int timeout) {
        Boolean result = false;
        try {
            final ConnectivityManager connectivityManager = (ConnectivityManager) mContext
                    .getSystemService(Context.CONNECTIVITY_SERVICE);
            Class connectivityManagerClass = Class.forName(connectivityManager.getClass().getName());

            final Method getNetworkPolicyManager = connectivityManagerClass.getDeclaredMethod(
                    "getNetworkPolicyManager");
            getNetworkPolicyManager.setAccessible(true);

            final Object iNetworkPolicyManager = getNetworkPolicyManager.invoke(connectivityManager);
            final Class iNetworkPolicyManagerClass = Class.forName(iNetworkPolicyManager.getClass()
                    .getName());
            final Method setRestrictBackground = iNetworkPolicyManagerClass.getDeclaredMethod("setRestrictBackground",
                    boolean.class);
            final Method getRestrictBackground = iNetworkPolicyManagerClass.getDeclaredMethod("getRestrictBackground");
            setRestrictBackground.setAccessible(true);
            getRestrictBackground.setAccessible(true);
            // Check if the state is already set
            result = (Boolean) getRestrictBackground.invoke(iNetworkPolicyManager);
            result = ((result && enable) || ((!result) && (!enable)));

            if (!result) {
                // Set state
                setRestrictBackground.invoke(iNetworkPolicyManager, enable);

                while (timeout > 0) {
                    // Check if the state is set
                    result = (Boolean) getRestrictBackground.invoke(iNetworkPolicyManager);
                    result = ((result && enable) || ((!result) && (!enable)));
                    if (result) {
                        break;
                    }
                    try {
                        Thread.sleep(THREAD_WAIT_TIMEOUT_IN_MS);
                    } catch (InterruptedException e) {
                    }
                    timeout -= THREAD_WAIT_TIMEOUT_IN_MS;
                }
            }
        } catch (Exception e) {
        }
        return result;
    }
    */
}
