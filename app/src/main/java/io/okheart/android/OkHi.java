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

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.util.HashMap;

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
    private static String firstname, lastname, phonenumber, color, name, logo, requestSource;
    private static Context mContext;
    private static OkHiCallback callback;
    private static String appkey;
    private static String uniqueId;
    private static String remoteSmsTemplate;

    public OkHi() {
    }

    public static void initialize(final String applicationKey) {


        /*
        Map<String, Object> users = new HashMap<>();
        users.put("appKey", applicationKey);

        mFirestore.collection("affiliations").document(applicationKey).set(users, SetOptions.merge())
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
        */
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
                trackjson.put("uniqueId", uniqueId);


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
            Intent intent = new Intent(mContext, OkHeartActivity.class);
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

    /*

      public static void manualPing(OkHiCallback okHiCallback, JSONObject jsonObject) {

          //displayLog("display client " + jsonObject.toString());


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

      }
  */
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

    public static void checkLocationPermission(Activity activity, int MY_PERMISSIONS_ACCESS_FINE_LOCATION) {

        boolean permissionAccessFineLocationApproved =
                ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_FINE_LOCATION)
                        == PackageManager.PERMISSION_GRANTED;

        if (permissionAccessFineLocationApproved) {
            try {
                OkAnalytics okAnalytics = new OkAnalytics(mContext);
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
                        OkAnalytics okAnalytics = new OkAnalytics(mContext);
                        HashMap<String, String> loans = new HashMap<>();
                        loans.put("uniqueId", uniqueId);
                        loans.put("type", "Manifest.permission.ACCESS_FINE_LOCATION");
                        okAnalytics.initializeDynamicParameters("app", "backgroundLocationPermissionApproved",
                                "permission", "mainActivityView", null, loans);
                        okAnalytics.sendToAnalytics("hq_okhi", null, null, "okhi");
                    } catch (Exception e) {
                        displayLog("event.submit okanalytics error " + e.toString());
                    }
                    //Constants.scheduleJob(MainActivity.this);
                    checkLocationSettings();
                } else {
                    // App can only access location in the foreground. Display a dialog
                    // warning the user that your app must have all-the-time access to
                    // location in order to function properly. Then, request background
                    // location.
                    try {
                        OkAnalytics okAnalytics = new OkAnalytics(mContext);
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
                checkLocationSettings();
            }
        } else {
            // App doesn't have access to the device's location at all. Make full request
            // for permission.
            try {
                OkAnalytics okAnalytics = new OkAnalytics(mContext);
                HashMap<String, String> loans = new HashMap<>();
                loans.put("uniqueId", uniqueId);
                loans.put("type", "Manifest.permission.ACCESS_FINE_LOCATION");
                okAnalytics.initializeDynamicParameters("app", "permissionAccessFineLocationNotApproved",
                        "permission", "mainActivityView", null, loans);
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

    private static void checkLocationSettings() {

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

    @Override
    public boolean onCreate() {
        // get the context (Application context)
        mContext = getContext();

        uniqueId = Settings.Secure.getString(mContext.getContentResolver(), Settings.Secure.ANDROID_ID);
        /*
        mFirestore = FirebaseFirestore.getInstance();
        query = mFirestore.collection("addresses").document(uniqueId)
                .collection("addresses")
                .orderBy("timestamp", Query.Direction.DESCENDING);
        mFirebaseRemoteConfig = FirebaseRemoteConfig.getInstance();
        try {
            remoteSmsTemplate = mFirebaseRemoteConfig.getString(REMOTE_SMS_TEMPLATE);
            //displayLog("remotesmstemplate " + remoteSmsTemplate);
        } catch (Exception e) {
            displayLog("error getting frequency " + e.toString());
        }
        */
        return true;

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

}
