package io.okheart.android;

import android.Manifest;
import android.app.Activity;
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

import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.work.BackoffPolicy;
import androidx.work.Constraints;
import androidx.work.Data;
import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.NetworkType;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;

import com.parse.Parse;
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
import java.util.concurrent.TimeUnit;

import io.okheart.android.asynctask.SendCustomLinkSmsTask;
import io.okheart.android.callback.SendCustomLinkSmsCallBack;
import io.okheart.android.utilities.MyWorker;


public final class OkHi extends ContentProvider {

    private static final String TAG = "OkHi";
    protected static Integer resume_ping_frequency = null;
    protected static Integer ping_frequency = null;
    protected static Integer background_frequency = null;
    protected static String sms_template = null;
    protected static Double gps_accuracy = null;
    protected static Boolean kill_switch = null;
    protected static io.okheart.android.database.DataProvider dataProvider;
    private static String firstname, lastname, phonenumber, color, name, logo, requestSource;
    private static Context mContext;
    private static io.okheart.android.callback.OkHiCallback callback;
    private static String appkey;
    private static String uniqueId;
    //private static String remoteSmsTemplate;
    private static Analytics analytics;

    public OkHi() {
    }

    public static void initialize(final String applicationKey, final Boolean verify) throws RuntimeException {

        displayLog("initialize");

        if (applicationKey != null) {
            if (applicationKey.length() > 0) {
                if (applicationKey.startsWith("r:")) {
                    startInitialization(applicationKey, verify);
                } else {
                    throw new RuntimeException("Initialization error", new Throwable("Confirm your application key is correct"));

                }
            } else {
                throw new RuntimeException("Initialization error", new Throwable("Confirm your application key is correct"));
            }
        } else {
            throw new RuntimeException("Initialization error", new Throwable("Confirm your application key is not null"));
        }

    }

    private static void startInitialization(final String applicationKey, final Boolean verify) {
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
            parameters.put("verify", "" + verify);
            sendEvent(parameters, loans);
        } catch (Exception e1) {
            displayLog("error attaching afl to ual " + e1.toString());
        }
        try {
            writeToFile(applicationKey);
        } catch (Exception io) {

        } finally {

        }

        dataProvider.insertStuff("verify", "" + verify);
        try {
            if (verify != null) {
                String tempVerify = "" + verify;
                if (tempVerify.length() > 0) {
                    if ((tempVerify.equalsIgnoreCase("false")) || ((tempVerify.equalsIgnoreCase("true")))) {
                        writeToFileVerify(tempVerify, "six");
                        if (verify) {
                            displayLog("decideWhatToStart");
                            decideWhatToStart();
                        } else {
                            displayLog("stopPeriodicPing");
                            stopPeriodicPing();
                        }


                    } else {
                        writeToFileVerify("false", "five");
                        stopPeriodicPing();
                    }

                } else {
                    writeToFileVerify("false", "four");
                    stopPeriodicPing();
                }

            } else {
                writeToFileVerify("false", "three");
                stopPeriodicPing();
            }
        } catch (Exception io) {
            writeToFileVerify("false", "two");
            stopPeriodicPing();
        } finally {
            // writeToFileVerify("false", "one");
        }

        appkey = applicationKey;
        dataProvider.insertStuff("applicationKey", applicationKey);


        try {
            Boolean production = false;
            if (applicationKey != null) {
                if (applicationKey.equalsIgnoreCase("r:b59a93ba7d80a95d89dff8e4c52e259a")) {

                } else {
                    production = true;
                }
            } else {
                production = true;
            }

            final Boolean productionVersion = production;

            JSONObject identifyjson = new JSONObject();
            //identifyjson.put("userId", userId);
            try {
                io.okheart.android.callback.SegmentIdentifyCallBack segmentIdentifyCallBack = new io.okheart.android.callback.SegmentIdentifyCallBack() {
                    @Override
                    public void querycomplete(String response, boolean status) {
                        if (status) {
                            displayLog("things went ok with send to omtm identify");

                            try {
                                io.okheart.android.callback.SegmentTrackCallBack segmentTrackCallBack = new io.okheart.android.callback.SegmentTrackCallBack() {
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

                                if (productionVersion) {
                                    trackjson.put("environment", "PROD");
                                } else {
                                    trackjson.put("environment", "DEVMASTER");

                                }
                                trackjson.put("event", "SDK Initialization");

                                trackjson.put("action", "initialization");
                                trackjson.put("actionSubtype", "initialization");
                                trackjson.put("clientProduct", "okHeartAndroidSDK");
                                trackjson.put("clientProductVersion", BuildConfig.VERSION_NAME);
                                trackjson.put("clientKey", applicationKey);
                                trackjson.put("uniqueId", uniqueId);
                                //trackjson.put("actionSubtype", "directionsUpdated/okhiGatePhotoUpdated/mapPinUpdated/customNameUpdated/locationInformationUpdated/");
                                //trackjson.put("crudOp", "create/update");
                                //trackjson.put("action", "viewUserAddres/updateUserAddress");
                                //trackjson.put("userId", userId);
                                //trackjson.put("phone", phoneNumber);
                                //trackjson.put("addressType", "G");
                                //trackjson.put("previousAddressType", "T");

                                /*
                                trackjson.put("hasCustomLink", "");
                                trackjson.put("hasGatePhoto", false);
                                trackjson.put("hasGPS", false);
                                trackjson.put("hasStruturedAddress", true);
                                trackjson.put("hasDescription",true);
                                trackjson.put("hasBreadcrumbs", "");
                                trackjson.put("addressDesignation", addressType);
                                trackjson.put("streetName", street_name2);
                                trackjson.put("propertyName",location_name2);
                                trackjson.put("propertyNumber", location_number2);
                                trackjson.put("unit", unit2);
                                trackjson.put("floor", floor2);
                                trackjson.put("businessName", businessnamestring2);
                                trackjson.put("neighbourhood", "");
                                trackjson.put("aflId", "");
                                trackjson.put("ualId", claimUalId);
                                trackjson.put("optedIn", "");
                                trackjson.put("addressSourceAffiliation","");
                                trackjson.put("addressSourceBrand", "");
                                trackjson.put("addressSourceBranch", "");
                                trackjson.put("activeAffiliation", loginaffiliation);
                                trackjson.put("activeBrand", branch);
                                trackjson.put("activeBranch", branch);
                                trackjson.put("photoSource", "");
                                trackjson.put("cookieToken", OkDriverApplication.getDeviceid());
                                trackjson.put("daysSinceActivation", "");
                                trackjson.put("daysSinceActivationPlus", "");
                                trackjson.put("gpsAccuracy", "");
                                trackjson.put("isHistoryAddress", "");
                                trackjson.put("customLink", "");
                                trackjson.put("amount", "");
                                trackjson.put("otherId", "");
                                trackjson.put("paymentMethod", "");
                                trackjson.put("type", "usage");
                                trackjson.put("isNewUser", "");
                                trackjson.put("isOkAppUser", "");
                                trackjson.put("isOptedIn", "");
                                trackjson.put("okAppActiveUser", "");
                                */
                                trackjson.put("appLayer", "client");
                                trackjson.put("onObject", "sdk");
                                trackjson.put("product", "okHeartAndroidSDK");


                                eventjson.put("properties", trackjson);
                                io.okheart.android.asynctask.SegmentTrackTask segmentTrackTask = new io.okheart.android.asynctask.SegmentTrackTask(segmentTrackCallBack, eventjson, productionVersion);
                                segmentTrackTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                            } catch (JSONException e) {
                                displayLog("track error omtm error " + e.toString());
                            }
                        } else {
                            displayLog("something went wrong with send to omtm identify");
                        }

                    }
                };
                io.okheart.android.asynctask.SegmentIdentifyTask segmentIdentifyTask = new io.okheart.android.asynctask.SegmentIdentifyTask(segmentIdentifyCallBack, identifyjson, productionVersion);
                segmentIdentifyTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

            } catch (Exception e) {
                displayLog("Error initializing analytics_omtm " + e.toString());
            }
        } catch (Exception jse) {
            displayLog("jsonexception jse " + jse.toString());
        }

    }

    public static void customize(String appColorTheme, String appName, String appLogo, String appBarColor,
                                 Boolean appBarVisibility, Boolean enableStreetView) {


        try {
            HashMap<String, String> loans = new HashMap<>();
            loans.put("uniqueId", uniqueId);
            HashMap<String, String> parameters = new HashMap<>();
            parameters.put("eventName", "Android SDK");
            parameters.put("type", "initialize");
            parameters.put("subtype", "customize");
            parameters.put("onObject", "okHeartAndroidSDK");
            parameters.put("view", "app");
            parameters.put("appKey", "" + appkey);
            sendEvent(parameters, loans);
        } catch (Exception e1) {
            displayLog("error attaching afl to ual " + e1.toString());
        }

        try {
            displayLog("okhi customized");
            JSONObject jsonObject = new JSONObject();
            if (color != null) {
                if (color.length() > 0) {
                    jsonObject.put("color", color);
                }
            }
            if (name != null) {
                if (name.length() > 0) {
                    jsonObject.put("name", name);
                }
            }

            if (logo != null) {
                if (logo.length() > 0) {
                    jsonObject.put("logo", logo);
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
            String testString = "{\"color\":\"" + color + "\", \"name\": \"" + name + "\",\"logo\": \"" + logo + "\"}";

            displayLog("custom string " + customString);
            displayLog(testString);
            writeToFileCustomize(testString);
        } catch (Exception io) {

        } finally {

        }


        try {
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

            try {
                io.okheart.android.callback.SegmentTrackCallBack segmentTrackCallBack = new io.okheart.android.callback.SegmentTrackCallBack() {
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

                if (productionVersion) {
                    trackjson.put("environment", "PROD");
                } else {
                    trackjson.put("environment", "DEVMASTER");

                }
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
                io.okheart.android.asynctask.SegmentTrackTask segmentTrackTask = new io.okheart.android.asynctask.SegmentTrackTask(segmentTrackCallBack, eventjson, productionVersion);
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

    public static void displayClient(io.okheart.android.callback.OkHiCallback okHiCallback, JSONObject jsonObject) throws RuntimeException {

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

    }


    private static void startActivity(io.okheart.android.callback.OkHiCallback okHiCallback, JSONObject jsonObject) {
        callback = okHiCallback;
        firstname = jsonObject.optString("firstName");
        lastname = jsonObject.optString("lastName");
        phonenumber = jsonObject.optString("phone");
        requestSource = jsonObject.optString("phone");

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

        try {
            Intent intent = new Intent(mContext, io.okheart.android.activity.OkHeartActivity.class);
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

        String permission;

        boolean permissionAccessFineLocationApproved =
                ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_FINE_LOCATION)
                        == PackageManager.PERMISSION_GRANTED;

        if (permissionAccessFineLocationApproved) {
            try {
                io.okheart.android.utilities.OkAnalytics okAnalytics = new io.okheart.android.utilities.OkAnalytics(mContext);
                HashMap<String, String> loans = new HashMap<>();
                loans.put("uniqueId", uniqueId);
                loans.put("type", "Manifest.permission.ACCESS_FINE_LOCATION");
                okAnalytics.initializeDynamicParameters("app", "permissionAccessFineLocationApproved",
                        "permission", "mainActivityView", null, loans);
                okAnalytics.sendToAnalytics("hq_okhi", null, null, "okhi");
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
                        io.okheart.android.utilities.OkAnalytics okAnalytics = new io.okheart.android.utilities.OkAnalytics(mContext);
                        HashMap<String, String> loans = new HashMap<>();
                        loans.put("uniqueId", uniqueId);
                        loans.put("type", " Manifest.permission.ACCESS_BACKGROUND_LOCATION");
                        okAnalytics.initializeDynamicParameters("app", "backgroundLocationPermissionApproved",
                                "permission", "mainActivityView", null, loans);
                        okAnalytics.sendToAnalytics("hq_okhi", null, null, "okhi");
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
                        io.okheart.android.utilities.OkAnalytics okAnalytics = new io.okheart.android.utilities.OkAnalytics(mContext);
                        HashMap<String, String> loans = new HashMap<>();
                        loans.put("uniqueId", uniqueId);
                        loans.put("type", " Manifest.permission.ACCESS_BACKGROUND_LOCATION");
                        okAnalytics.initializeDynamicParameters("app", "backgroundLocationPermissionNotApproved",
                                "permission", "mainActivityView", null, loans);
                        okAnalytics.sendToAnalytics("hq_okhi", null, null, "okhi");
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
                io.okheart.android.utilities.OkAnalytics okAnalytics = new io.okheart.android.utilities.OkAnalytics(mContext);
                HashMap<String, String> loans = new HashMap<>();
                loans.put("uniqueId", uniqueId);
                loans.put("type", "Manifest.permission.ACCESS_FINE_LOCATION");
                okAnalytics.initializeDynamicParameters("app", "permissionAccessFineLocationNotApproved",
                        "permission", "mainActivityView", null, loans);
                okAnalytics.sendToAnalytics("hq_okhi", null, null, "okhi");
            } catch (Exception e) {
                displayLog("event.submit okanalytics error " + e.toString());
            }
            permission = "Manifest.permission.ACCESS_FINE_LOCATION not granted";
        }
        return permission;
    }


    public static boolean checkPermission() {

        Boolean permission;

        boolean permissionAccessFineLocationApproved =
                ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_FINE_LOCATION)
                        == PackageManager.PERMISSION_GRANTED;

        if (permissionAccessFineLocationApproved) {
            try {
                io.okheart.android.utilities.OkAnalytics okAnalytics = new io.okheart.android.utilities.OkAnalytics(mContext);
                HashMap<String, String> loans = new HashMap<>();
                loans.put("uniqueId", uniqueId);
                loans.put("type", "Manifest.permission.ACCESS_FINE_LOCATION");
                okAnalytics.initializeDynamicParameters("app", "permissionAccessFineLocationApproved",
                        "permission", "mainActivityView", null, loans);
                okAnalytics.sendToAnalytics("hq_okhi", null, null, "okhi");
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
                        io.okheart.android.utilities.OkAnalytics okAnalytics = new io.okheart.android.utilities.OkAnalytics(mContext);
                        HashMap<String, String> loans = new HashMap<>();
                        loans.put("uniqueId", uniqueId);
                        loans.put("type", "Manifest.permission.ACCESS_FINE_LOCATION");
                        okAnalytics.initializeDynamicParameters("app", "backgroundLocationPermissionApproved",
                                "permission", "mainActivityView", null, loans);
                        okAnalytics.sendToAnalytics("hq_okhi", null, null, "okhi");
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
                        io.okheart.android.utilities.OkAnalytics okAnalytics = new io.okheart.android.utilities.OkAnalytics(mContext);
                        HashMap<String, String> loans = new HashMap<>();
                        loans.put("uniqueId", uniqueId);
                        loans.put("type", "Manifest.permission.ACCESS_FINE_LOCATION");
                        okAnalytics.initializeDynamicParameters("app", "backgroundLocationPermissionNotApproved",
                                "permission", "mainActivityView", null, loans);
                        okAnalytics.sendToAnalytics("hq_okhi", null, null, "okhi");
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
                io.okheart.android.utilities.OkAnalytics okAnalytics = new io.okheart.android.utilities.OkAnalytics(mContext);
                HashMap<String, String> loans = new HashMap<>();
                loans.put("uniqueId", uniqueId);
                loans.put("type", "Manifest.permission.ACCESS_FINE_LOCATION");
                okAnalytics.initializeDynamicParameters("app", "permissionAccessFineLocationNotApproved",
                        "permission", "mainActivityView", null, loans);
                okAnalytics.sendToAnalytics("hq_okhi", null, null, "okhi");
            } catch (Exception e) {
                displayLog("event.submit okanalytics error " + e.toString());
            }
            permission = false;
        }
        return permission;
    }


    public static void manualPing(io.okheart.android.callback.OkHiCallback okHiCallback, JSONObject jsonObject) {

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
              parameters.put("view", "worker");
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
                List<io.okheart.android.datamodel.AddressItem> addressItemList = dataProvider.getAllAddressList();
                if (addressItemList.size() > 0) {
                    sendPingSMS(okHiCallback, tempPhonenumber);
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



                  /*
          query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
              @Override
              public void onComplete(@NonNull Task<QuerySnapshot> task) {
                  if (task.isSuccessful()) {
                      if (task.getResult().size() > 0) {
                          VerifyDataItem verifyDataItem = task.getResult().getDocuments().get(0).toObject(VerifyDataItem.class);

                          try {
                              String message = remoteSmsTemplate + uniqueId;
                              final HashMap<String, String> jsonObject = new HashMap<>();
                              jsonObject.put("userId", "GrlaR3LHUP");
                              jsonObject.put("sessionToken", "r:3af107bf99e4c6f2a91e6fec046f5fc7");
                              jsonObject.put("customName", "test");
                              jsonObject.put("ualId", verifyDataItem.getUalId());
                              jsonObject.put("phoneNumber", verifyDataItem.getPhone());
                              jsonObject.put("phone", verifyDataItem.getPhone());
                              jsonObject.put("message", message);
                              jsonObject.put("uniqueId", uniqueId);
                              SendCustomLinkSmsCallBack sendCustomLinkSmsCallBack = new SendCustomLinkSmsCallBack() {
                                  @Override
                                  public void querycomplete(String response, boolean status) {
                                      if (status) {
                                          displayLog("send sms success " + response);
                                          displayToast("SMS sent", true);
                                      } else {
                                          displayToast("Error! " + response, true);
                                      }
                                  }
                              };
                              SendCustomLinkSmsTask sendCustomLinkSmsTask = new SendCustomLinkSmsTask(mContext, sendCustomLinkSmsCallBack, jsonObject);
                              sendCustomLinkSmsTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                          } catch (Exception jse) {
                              displayLog("jsonexception " + jse.toString());
                          }
                      } else {
                          displayToast("Create an address first", true);
                      }
                  } else {
                      displayToast("Create an address first", true);
                  }
              }
          });
*/
      }


    /*
    public static void checkInternet() {
        try {
            HeartBeatCallBack heartBeatCallBack = new HeartBeatCallBack() {
                @Override
                public void querycomplete(Boolean response) {
                    if (response) {

                    } else {
                        try {
                            JSONObject jsonObject = new JSONObject();
                            jsonObject.put("message", "fatal_exit");
                            JSONObject jsonObject1 = new JSONObject();
                            jsonObject1.put("Error", "Network error");
                            jsonObject.put("payload", jsonObject1);
                            displayLog(jsonObject.toString());
                            callback.querycomplete(jsonObject);
                        } catch (JSONException jse) {
                            displayLog("json error " + jse.toString());
                        }
                    }
                }
            };
            HeartBeatTask heartBeatTask = new HeartBeatTask(heartBeatCallBack);
            heartBeatTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        } catch (Exception e) {
            displayLog("check internet error " + e.toString());
        }
    }
*/

    private static void sendPingSMS(final io.okheart.android.callback.OkHiCallback okHiCallback, String phonenumber) {
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
            SendCustomLinkSmsCallBack sendCustomLinkSmsCallBack = new SendCustomLinkSmsCallBack() {
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
            SendCustomLinkSmsTask sendCustomLinkSmsTask = new SendCustomLinkSmsTask(mContext, sendCustomLinkSmsCallBack, jsonObject);
            sendCustomLinkSmsTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        } catch (Exception jse) {
            displayLog("jsonexception " + jse.toString());
        }
    }
    private static void displayLog(String log) {
        Log.i(TAG, log);
    }

    private static void writeToFile(String customString) {
        try {
            File path = mContext.getFilesDir();
            File file = new File(path, "okheart.txt");
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

    public static io.okheart.android.callback.OkHiCallback getCallback() {
        return callback;
    }

    public static void setCallback(io.okheart.android.callback.OkHiCallback callback) {
        OkHi.callback = callback;
    }

    public static void requestPermission(Activity activity, int MY_PERMISSIONS_ACCESS_FINE_LOCATION) {

        Boolean permission = false;

        boolean permissionAccessFineLocationApproved =
                ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_FINE_LOCATION)
                        == PackageManager.PERMISSION_GRANTED;

        if (permissionAccessFineLocationApproved) {
            try {
                io.okheart.android.utilities.OkAnalytics okAnalytics = new io.okheart.android.utilities.OkAnalytics(mContext);
                HashMap<String, String> loans = new HashMap<>();
                loans.put("uniqueId", uniqueId);
                loans.put("type", "Manifest.permission.ACCESS_FINE_LOCATION");
                okAnalytics.initializeDynamicParameters("app", "permissionAccessFineLocationApproved",
                        "requestPermission", activity.getLocalClassName(), null, loans);
                okAnalytics.sendToAnalytics("hq_okhi", null, null, "okhi");
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
                        io.okheart.android.utilities.OkAnalytics okAnalytics = new io.okheart.android.utilities.OkAnalytics(mContext);
                        HashMap<String, String> loans = new HashMap<>();
                        loans.put("uniqueId", uniqueId);
                        loans.put("type", "Manifest.permission.ACCESS_FINE_LOCATION");
                        okAnalytics.initializeDynamicParameters("app", "backgroundLocationPermissionApproved",
                                "requestPermission", activity.getLocalClassName(), null, loans);
                        okAnalytics.sendToAnalytics("hq_okhi", null, null, "okhi");
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
                        io.okheart.android.utilities.OkAnalytics okAnalytics = new io.okheart.android.utilities.OkAnalytics(mContext);
                        HashMap<String, String> loans = new HashMap<>();
                        loans.put("uniqueId", uniqueId);
                        loans.put("type", "Manifest.permission.ACCESS_FINE_LOCATION");
                        okAnalytics.initializeDynamicParameters("app", "backgroundLocationPermissionNotApproved",
                                "permission", "mainActivityView", null, loans);
                        okAnalytics.sendToAnalytics("hq_okhi", null, null, "okhi");
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
                io.okheart.android.utilities.OkAnalytics okAnalytics = new io.okheart.android.utilities.OkAnalytics(mContext);
                HashMap<String, String> loans = new HashMap<>();
                loans.put("uniqueId", uniqueId);
                loans.put("type", "Manifest.permission.ACCESS_FINE_LOCATION");
                okAnalytics.initializeDynamicParameters("app", "permissionAccessFineLocationNotApproved",
                        "requestPermission", activity.getLocalClassName(), null, loans);
                okAnalytics.sendToAnalytics("hq_okhi", null, null, "okhi");
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

    private static void decideWhatToStart() {
        List<io.okheart.android.datamodel.AddressItem> addressItemList = dataProvider.getAllAddressList();
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

    private static void sendEvent(HashMap<String, String> parameters, HashMap<String, String> loans) {
        try {
            io.okheart.android.utilities.OkAnalytics okAnalytics = new io.okheart.android.utilities.OkAnalytics(mContext);
            okAnalytics.sendToAnalytics(parameters, loans);
        } catch (Exception e) {
            displayLog("error sending photoexpanded analytics event " + e.toString());
        }
    }

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

    private static void stopPeriodicPing() {
        WorkManager.getInstance().cancelUniqueWork("ramogi");
    }

    private static void startKeepPeriodicPing(Integer pingTime, String uniqueId) {
        displayLog("workmanager startKeepPeriodicPing");
        try {
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

    public static Integer getResume_ping_frequency() {
        return resume_ping_frequency;
    }

    /*
    private void saveInfoToFirestore(JSONObject payload) {

        displayLog("saveAddressToFirestore");

        JSONObject location = payload.optJSONObject("location");
        JSONObject user = payload.optJSONObject("user");
        String firstName = user.optString("firstName");
        String lastName = user.optString("lastName");
        String phone = user.optString("phone");
        String streetName = location.optString("streetName");
        String propertyName = location.optString("propertyName");
        String directions = location.optString("directions");
        String placeId = location.optString("placeId");
        String ualId = location.optString("id");
        String url = location.optString("url");
        String title = location.optString("title");
        String plusCode = location.optString("plusCode");
        String branch = "okhi";
        Double lat = location.optDouble("lat");
        Double lng = location.optDouble("lng");


        Map<String, Object> data = new HashMap<>();
        data.put("latitude", lat);
        data.put("longitude", lng);
        data.put("timestamp", new Timestamp(new Date()));
        GeoPoint geoPoint = new GeoPoint(lat, lng);
        data.put("geoPoint", geoPoint);
        data.put("firstName", firstName);
        data.put("lastName", lastName);
        data.put("phone", phone);
        data.put("streetName", streetName);
        data.put("propertyName", propertyName);

        data.put("directions", directions);
        data.put("placeId", placeId);
        data.put("ualId", ualId);
        data.put("url", url);
        data.put("title", title);
        data.put("plusCode", plusCode);
        data.put("appKey", appkey);

        Map<String, Object> users = new HashMap<>();
        users.put("firstName", firstName);
        users.put("lastName", lastName);
        users.put("phone", phone);
        users.put("uniqueId", uniqueId);
        users.put("appKey", appkey);

        mFirestore.collection("users").document(uniqueId).set(users, SetOptions.merge())
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        displayLog("Document written successfully");
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                displayLog("Document write failure " + e.getMessage());
            }
        });

        mFirestore.collection("addresses").document(uniqueId).collection("addresses")
                .document(ualId).set(data, SetOptions.merge())
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        displayLog("Document written successfully");
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                displayLog("Document write failure " + e.getMessage());
            }
        });

    }
    */

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
        try {
            Parse.initialize(new Parse.Configuration.Builder(mContext)
                    .applicationId(io.okheart.android.utilities.Constants.DEVMASTER_APPLICATION_ID)
                    .clientKey(io.okheart.android.utilities.Constants.DEVMASTER_CLIENT_ID)
                    .server("https://parseapi.back4app.com/").enableLocalDataStore().build());
        } catch (Exception e) {
            displayLog("parse initialize error " + e.toString());
        }

        try {
            analytics = new Analytics.Builder(mContext, io.okheart.android.utilities.Constants.ANALYTICS_WRITE_KEY).build();
            Analytics.setSingletonInstance(analytics);
        } catch (Exception e) {
            displayLog("Error initializing analytics " + e.toString());
        }

        uniqueId = Settings.Secure.getString(mContext.getContentResolver(), Settings.Secure.ANDROID_ID);

        dataProvider = new io.okheart.android.database.DataProvider(mContext);

        io.okheart.android.utilities.ConfigurationFile configurationFile = new io.okheart.android.utilities.ConfigurationFile(mContext);
        /*
        List<io.okheart.android.datamodel.AddressItem> addressItemList = dataProvider.getAllAddressList();
        displayLog("foreground addressItemList " + addressItemList.size());
        if (addressItemList.size() > 0) {
            try {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    mContext.startForegroundService(new Intent(mContext, io.okheart.android.services.ForegroundService.class));
                } else {
                    mContext.startService(new Intent(mContext, io.okheart.android.services.ForegroundService.class));
                }

            } catch (Exception jse) {
                displayLog("jsonexception jse " + jse.toString());
            }
        } else {
            //we have no addresses to start foreground
            displayLog("we have no addresses to start foreground");
        }
        */

        displayLog("okhi create");


        return true;
    }
}
