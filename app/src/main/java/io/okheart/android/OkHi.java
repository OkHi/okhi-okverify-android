package io.okheart.android;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.Settings;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.SetOptions;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import io.okheart.android.activity.OkHeartActivity;
import io.okheart.android.asynctask.HeartBeatTask;
import io.okheart.android.asynctask.SegmentIdentifyTask;
import io.okheart.android.asynctask.SegmentTrackTask;
import io.okheart.android.callback.HeartBeatCallBack;
import io.okheart.android.callback.OkHiCallback;
import io.okheart.android.callback.SegmentIdentifyCallBack;
import io.okheart.android.callback.SegmentTrackCallBack;
import io.okheart.android.utilities.OkAnalytics;

public final class OkHi extends ContentProvider {

    private static final String TAG = "OkHi";
    private static String firstname, lastname, phonenumber, color, name, logo;
    private static Context mContext;
    private static OkHiCallback callback;
    private static String appkey;
    private static String uniqueId;
    private static FirebaseFirestore mFirestore;

    public OkHi() {
    }

    public static void initialize(final String applicationKey) {

        mFirestore = FirebaseFirestore.getInstance();
        uniqueId = Settings.Secure.getString(mContext.getContentResolver(), Settings.Secure.ANDROID_ID);
        Map<String, Object> users = new HashMap<>();
        users.put("appKey", applicationKey);

        mFirestore.collection("affiliations").document(uniqueId).set(users, SetOptions.merge())
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
        try {
            HashMap<String, String> loans = new HashMap<>();
            //loans.put("phonenumber",postDataParams.get("phone"));
            //loans.put("ualId", model.getUalId());
            HashMap<String, String> parameters = new HashMap<>();
            parameters.put("eventName", "Android SDK");
            parameters.put("type", "initialize");
            parameters.put("subtype", "initialize");
            parameters.put("onObject", "okHeartAndroidSDK");
            parameters.put("view", "app");
            parameters.put("appKey", "" + applicationKey);
            sendEvent(parameters, loans);
        } catch (Exception e1) {
            displayLog("error attaching afl to ual " + e1.toString());
        }
        try {
            displayLog("okhi initialized");
            writeToFile(applicationKey);
        } catch (Exception io) {

        } finally {

        }
        appkey = applicationKey;


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
                SegmentIdentifyCallBack segmentIdentifyCallBack = new SegmentIdentifyCallBack() {
                    @Override
                    public void querycomplete(String response, boolean status) {
                        if (status) {
                            displayLog("things went ok with send to omtm identify");

                            try {
                                SegmentTrackCallBack segmentTrackCallBack = new SegmentTrackCallBack() {
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
                                //trackjson.put("crudOp", "create");
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
                                SegmentTrackTask segmentTrackTask = new SegmentTrackTask(segmentTrackCallBack, eventjson, productionVersion);
                                segmentTrackTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                            } catch (JSONException e) {
                                displayLog("track error omtm error " + e.toString());
                            }
                        } else {
                            displayLog("something went wrong with send to omtm identify");
                        }

                    }
                };
                SegmentIdentifyTask segmentIdentifyTask = new SegmentIdentifyTask(segmentIdentifyCallBack, identifyjson, productionVersion);
                segmentIdentifyTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

            } catch (Exception e) {
                displayLog("Error initializing analytics_omtm " + e.toString());
            }
        } catch (Exception jse) {
            displayLog("jsonexception jse " + jse.toString());
        }


    }

    public static void customize(String color, String name, String logo, String appbarcolor, Boolean appbarvisibility, Boolean streetview) {


        try {
            HashMap<String, String> loans = new HashMap<>();
            //loans.put("phonenumber",postDataParams.get("phone"));
            //loans.put("ualId", model.getUalId());
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
            if (appbarcolor != null) {
                if (appbarcolor.length() > 0) {
                    jsonObject.put("appbarcolor", appbarcolor);
                }
            }
            if (appbarvisibility != null) {
                jsonObject.put("appbarvisibility", appbarvisibility);
            }

            if (streetview != null) {
                jsonObject.put("enablestreetview", streetview);
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
//
            /*
            JSONObject identifyjson = new JSONObject();
            //identifyjson.put("userId", userId);
            try {
                SegmentIdentifyCallBack segmentIdentifyCallBack = new SegmentIdentifyCallBack() {
                    @Override
                    public void querycomplete(String response, boolean status) {
                        if(status){
                            */

            ////
            displayLog("things went ok with send to omtm identify");

            try {
                SegmentTrackCallBack segmentTrackCallBack = new SegmentTrackCallBack() {
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


                eventjson.put("properties", trackjson);
                SegmentTrackTask segmentTrackTask = new SegmentTrackTask(segmentTrackCallBack, eventjson, productionVersion);
                segmentTrackTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            } catch (JSONException e) {
                displayLog("track error omtm error " + e.toString());
            }
            /////
                            /*
                        }
                        else{
                            displayLog("something went wrong with send to omtm identify");
                        }

                    }
                };
                SegmentIdentifyTask segmentIdentifyTask = new SegmentIdentifyTask(segmentIdentifyCallBack, identifyjson, productionVersion);
                segmentIdentifyTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

            } catch (Exception e) {
                displayLog("Error initializing analytics_omtm " + e.toString());
            }
            */
            ///
        } catch (Exception jse) {
            displayLog("jsonexception jse " + jse.toString());
        }


    }

    public static void displayClient(OkHiCallback okHiCallback, JSONObject jsonObject) {

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
            Intent intent = new Intent(mContext, OkHeartActivity.class);
            intent.putExtra("firstname", firstname);
            intent.putExtra("lastname", lastname);
            intent.putExtra("phone", phonenumber);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            mContext.startActivity(intent);
        } catch (Exception e) {
            displayLog("error calling receiveActivity activity " + e.toString());
        }
    }

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

    public static OkHiCallback getCallback() {
        return callback;
    }

    public static void setCallback(OkHiCallback callback) {
        OkHi.callback = callback;
    }

    @Override
    public boolean onCreate() {
        // get the context (Application context)
        mContext = getContext();
        //initialize whatever you need
        return true;

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

    private static void sendEvent(HashMap<String, String> parameters, HashMap<String, String> loans) {
        try {
            OkAnalytics okAnalytics = new OkAnalytics(mContext);
            okAnalytics.sendToAnalytics(parameters, loans);
        } catch (Exception e) {
            displayLog("error sending photoexpanded analytics event " + e.toString());
        }
    }

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
}
