package io.okheart.android.utilities;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.Location;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.BatteryManager;
import android.os.Build;
import android.os.Looper;
import android.provider.Settings;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.work.Data;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.parse.ParseException;
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;
import com.parse.SaveCallback;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static android.content.Context.BATTERY_SERVICE;

public class MyWorker extends Worker {

    private static final String TAG = "MyWorker";
    private Context context;
    private LocationCallback mLocationCallback;
    private LocationSettingsRequest mLocationSettingsRequest;
    private FusedLocationProviderClient mFusedLocationClient;
    private LocationRequest mLocationRequest;
    private Location mCurrentLocation;
    private Double lat, lng;
    private Float acc;
    private String uniqueId;
    private io.okheart.android.database.DataProvider dataProvider;
    private String environment;

    public MyWorker(@NonNull Context appContext, @NonNull WorkerParameters workerParams) {
        super(appContext, workerParams);
        context = appContext;
    }

    @NonNull
    @Override
    public Result doWork() {
        displayLog("doWork ");

        /*
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                context.startForegroundService(new Intent(context, io.okheart.android.services.ForegroundService.class));
            } else {
                context.startService(new Intent(context, io.okheart.android.services.ForegroundService.class));
            }

        } catch (Exception jse) {
            displayLog("jsonexception jse " + jse.toString());
        }
        */

        //OkHi.startForegroundNotification();

        uniqueId = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
        dataProvider = new io.okheart.android.database.DataProvider(context);
        environment = dataProvider.getPropertyValue("environment");

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


        try {
            HashMap<String, String> loans = new HashMap<>();
            loans.put("uniqueId", uniqueId);
            HashMap<String, String> parameters = new HashMap<>();
            parameters.put("eventName", "Data Collection Service");
            parameters.put("subtype", "create");
            parameters.put("type", "doWork");
            parameters.put("onObject", "app");
            parameters.put("view", "worker");
            sendEvent(parameters, loans);
        } catch (Exception e1) {
            displayLog("error attaching afl to ual " + e1.toString());
        }

        try {
            mFusedLocationClient = LocationServices.getFusedLocationProviderClient(context);
        } catch (Exception e) {
            displayLog("mfusedlocationclient error " + e.toString());
        }

        String tempGps_accuracy = dataProvider.getPropertyValue("gps_accuracy");
        if (tempGps_accuracy != null) {
            if (tempGps_accuracy.length() > 0) {
                Double gps_accuracy = Double.parseDouble(tempGps_accuracy);
                createLocationCallback(gps_accuracy);
            } else {
                createLocationCallback(50.0);
            }
        } else {
            createLocationCallback(50.0);
        }

        String tempBackground_frequency = dataProvider.getPropertyValue("background_frequency");
        if (tempBackground_frequency != null) {
            if (tempBackground_frequency.length() > 0) {
                Integer background_frequency = Integer.parseInt(tempBackground_frequency);
                createLocationRequest(background_frequency);
            } else {
                createLocationRequest(30000);
            }
        } else {
            createLocationRequest(30000);
        }

        buildLocationSettingsRequest();

        startLocationUpdates();

        Data outputData = new Data.Builder().putString("work_result", "Foreground service started").build();
        //sendSMS("dowork");

        return Result.success(outputData);
    }


    private void createLocationRequest(Integer remotelocationfrequency) {

        displayLog("createLocationRequest start frequency " + remotelocationfrequency);
        try {
            HashMap<String, String> loans = new HashMap<>();
            loans.put("uniqueId", uniqueId);
            HashMap<String, String> parameters = new HashMap<>();
            parameters.put("eventName", "Data Collection Service");
            parameters.put("subtype", "create");
            parameters.put("type", "createLocationRequest");
            parameters.put("onObject", "app");
            parameters.put("view", "worker");
            sendEvent(parameters, loans);
        } catch (Exception e1) {
            displayLog("error attaching afl to ual " + e1.toString());
        }
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(remotelocationfrequency);
        mLocationRequest.setFastestInterval(remotelocationfrequency / 2);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        displayLog("createLocationRequest end");
    }

    private void createLocationCallback(final Double remotegpsaccuracy) {
        displayLog("create location callback start gpsaccuracy " + remotegpsaccuracy);

        try {
            HashMap<String, String> loans = new HashMap<>();
            loans.put("uniqueId", uniqueId);
            HashMap<String, String> parameters = new HashMap<>();
            parameters.put("eventName", "Data Collection Service");
            parameters.put("subtype", "create");
            parameters.put("type", "createLocationCallback");
            parameters.put("onObject", "app");
            parameters.put("view", "worker");
            sendEvent(parameters, loans);
        } catch (Exception e1) {
            displayLog("error attaching afl to ual " + e1.toString());
        }

        mLocationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                super.onLocationResult(locationResult);

                if (locationResult == null) {
                    displayLog("lat cannot get location");
                    //sendSMS("with null location");
                    try {
                        HashMap<String, String> loans = new HashMap<>();
                        loans.put("uniqueId", uniqueId);
                        HashMap<String, String> parameters = new HashMap<>();
                        parameters.put("eventName", "Data Collection Service");
                        parameters.put("subtype", "result");
                        parameters.put("type", "onLocationResult");
                        parameters.put("onObject", "null");
                        parameters.put("view", "worker");
                        sendEvent(parameters, loans);
                    } catch (Exception e1) {
                        displayLog("error attaching afl to ual " + e1.toString());
                    }
                    displayLog("create location callback end");
                    return;
                } else {
                    mCurrentLocation = locationResult.getLastLocation();
                    lat = mCurrentLocation.getLatitude();
                    lng = mCurrentLocation.getLongitude();
                    acc = mCurrentLocation.getAccuracy();

                    displayLog("lat " + lat + " lng " + lng + " acc " + acc);

                    try {
                        HashMap<String, String> loans = new HashMap<>();
                        loans.put("uniqueId", uniqueId);
                        HashMap<String, String> parameters = new HashMap<>();
                        parameters.put("eventName", "Data Collection Service");
                        parameters.put("subtype", "result");
                        parameters.put("type", "onLocationResult");
                        parameters.put("onObject", "app");
                        parameters.put("view", "worker");
                        parameters.put("latitude", "" + lat);
                        parameters.put("longitude", "" + lng);
                        parameters.put("gpsAccuracy", "" + acc);
                        parameters.put("remoteGPSAccuracy", "" + remotegpsaccuracy);
                        sendEvent(parameters, loans);
                    } catch (Exception e1) {
                        displayLog("error attaching afl to ual " + e1.toString());
                    }

                    if (acc > remotegpsaccuracy) {
                        displayLog("lat quick");
                        //Constants.scheduleQuickJob(ForegroundService.this);
                        //sendSMS("without acc location " + acc);
                    } else {
                        displayLog("lat updatedatabase");
                        //sendSMS("with acc location");
                        updateDatabase(lat, lng, acc);
                        //stopSelf();
                    }
                    displayLog("create location callback end");
                }
            }
        };

    }

    private void buildLocationSettingsRequest() {
        displayLog("buildLocationSettingsRequest start");
        try {
            HashMap<String, String> loans = new HashMap<>();
            loans.put("uniqueId", uniqueId);
            HashMap<String, String> parameters = new HashMap<>();
            parameters.put("eventName", "Data Collection Service");
            parameters.put("subtype", "create");
            parameters.put("type", "buildLocationSettingsRequest");
            parameters.put("onObject", "app");
            parameters.put("view", "worker");
            sendEvent(parameters, loans);
        } catch (Exception e1) {
            displayLog("error attaching afl to ual " + e1.toString());
        }
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder();
        builder.addLocationRequest(mLocationRequest);
        builder.setNeedBle(true);
        mLocationSettingsRequest = builder.build();
        displayLog("buildLocationSettingsRequest end");
    }


    private void updateDatabase(final Double lat, final Double lng, final Float acc) {

        List<io.okheart.android.datamodel.AddressItem> addressItemList = dataProvider.getAllAddressList();

        if (addressItemList.size() > 0) {
            //List<Map<String, Object>> addresses = new ArrayList<>();

            try {
                stopLocationUpdates();
            } catch (Exception e) {
                displayLog("Error stopping location update " + e.toString());
            }


            HashMap<String, String> loans = new HashMap<>();
            loans.put("uniqueId", uniqueId);
            HashMap<String, String> parameters = new HashMap<>();

            final Long timemilliseconds = System.currentTimeMillis();

            final ParseObject parseObject = new ParseObject("UserVerificationData");

            parseObject.put("latitude", lat);
            parseObject.put("longitude", lng);
            parseObject.put("gpsAccuracy", acc);
            ParseGeoPoint parseGeoPoint = new ParseGeoPoint(lat, lng);
            parseObject.put("geoPoint", parseGeoPoint);
            parseObject.put("geoPointSource", "clientBackgroundGPS");
            parseObject.put("timemilliseconds", timemilliseconds);
            parseObject.put("device", getDeviceModelAndBrand());
            parseObject.put("model", Build.MODEL);
            parseObject.put("brand", Build.MANUFACTURER);
            parseObject.put("OSVersion", Build.VERSION.SDK_INT);
            parseObject.put("OSName", "Android");
            parseObject.put("appVersionCode", io.okheart.android.BuildConfig.VERSION_CODE);
            parseObject.put("appVersionName", io.okheart.android.BuildConfig.VERSION_NAME);

            try {
                WifiManager wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
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
                /*
                for(ScanResult config : scanResultList) {
                    displayLog("scanned list "+config.SSID);
                    scannedSSIDList.add(config.SSID);
                }
                */
                } catch (Exception e) {
                    displayLog("error gettign scanned list " + e.toString());
                }


            } catch (Exception e) {
                displayLog(" error getting wifi info " + e.toString());
            }

            try {
                String phonenumber = dataProvider.getPropertyValue("phonenumber");
                if (phonenumber != null) {
                    if (phonenumber.length() > 0) {
                        parseObject.put("phonenumber", phonenumber);
                    }
                }
            } catch (Exception e) {
                displayLog("error getting phonenumber " + e.toString());
            }


            try {
                boolean isPlugged = false;
                BatteryManager bm = (BatteryManager) context.getSystemService(BATTERY_SERVICE);
                int batLevel = bm.getIntProperty(BatteryManager.BATTERY_PROPERTY_CAPACITY);
                parameters.put("batteryLevel", "" + batLevel);
                parseObject.put("batteryLevel", batLevel);

                Intent intent = context.registerReceiver(null, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
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
                parameters.put("branch", "hq_okhi");
                //parameters.put("deliveryId", null);
                //parameters.put("ualId", addressParseObject.getClaimUalId());
                parameters.put("userAffiliation", "okhi");

                parameters.put("latitude", "" + lat);
                parameters.put("longitude", "" + lng);
                parameters.put("gpsAccuracy", "" + acc);
                try {
                    Location location2 = new Location("geohash");
                    location2.setLatitude(lat);
                    location2.setLongitude(lng);

                    io.okheart.android.utilities.geohash.GeoHash hash = io.okheart.android.utilities.geohash.GeoHash.fromLocation(location2, 12);
                    parameters.put("location", hash.toString());
                } catch (Exception e) {
                    displayLog("geomap error " + e.toString());
                }
                parameters.put("geoPointSource", "clientBackgroundGPS");
                parameters.put("timemilliseconds", "" + timemilliseconds);
                parameters.put("device", getDeviceModelAndBrand());
                parameters.put("model", Build.MODEL);
                parameters.put("brand", Build.MANUFACTURER);
                parameters.put("OSVersion", "" + Build.VERSION.SDK_INT);
                parameters.put("OSName", "Android");
                parameters.put("appVersionCode", "" + io.okheart.android.BuildConfig.VERSION_CODE);
                parameters.put("appVersionName", "" + io.okheart.android.BuildConfig.VERSION_NAME);
                sendEvent(parameters, loans);
            } catch (Exception e1) {
                displayLog("error attaching afl to ual " + e1.toString());
            }


            for (int i = 0; i < addressItemList.size(); i++) {
                try {
                    io.okheart.android.datamodel.AddressItem addressItem = addressItemList.get(i);
                    Float distance = getDistance(lat, lng, addressItem.getLat(), addressItem.getLng());
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
                    //addresses.add(nestedData);
                    parseObject.put("address", nestedData.toString());
                    parseObject.put("ualId", addressItem.getUalid());
                    saveData(parseObject);
                } catch (Exception e) {

                }
            }

        } else {
            //add an event here saying we don't have addresses
            //saveData(parseObject,  timemilliseconds);
            //stopSelf();
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


    private void stopLocationUpdates() {
        try {
            HashMap<String, String> loans = new HashMap<>();
            loans.put("uniqueId", uniqueId);
            HashMap<String, String> parameters = new HashMap<>();
            parameters.put("eventName", "Data Collection Service");
            parameters.put("subtype", "stop");
            parameters.put("type", "stopLocationUpdates");
            parameters.put("onObject", "app");
            parameters.put("view", "worker");
            sendEvent(parameters, loans);
        } catch (Exception e1) {
            displayLog("error attaching afl to ual " + e1.toString());
        }
        try {
            mFusedLocationClient.removeLocationUpdates(mLocationCallback);
        } catch (Exception e) {

        }
    }

    private void startLocationUpdates() {
        displayLog("startLocationUpdates");
        try {
            HashMap<String, String> loans = new HashMap<>();
            loans.put("uniqueId", uniqueId);
            HashMap<String, String> parameters = new HashMap<>();
            parameters.put("eventName", "Data Collection Service");
            parameters.put("subtype", "start");
            parameters.put("type", "startLocationUpdates");
            parameters.put("onObject", "app");
            parameters.put("view", "worker");
            //parameters.put("killswitch", "" + remotekillswitch);
            //parameters.put("ualId", model.getUalId());
            sendEvent(parameters, loans);
        } catch (Exception e1) {
            displayLog("error attaching afl to ual " + e1.toString());
        }
        try {
            mFusedLocationClient.requestLocationUpdates(mLocationRequest,
                    mLocationCallback, Looper.getMainLooper())
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            displayLog("startLocationUpdates addOnCompleteListener 1 ");
                            try {
                                HashMap<String, String> loans = new HashMap<>();
                                loans.put("uniqueId", uniqueId);
                                HashMap<String, String> parameters = new HashMap<>();
                                parameters.put("eventName", "Data Collection Service");
                                parameters.put("subtype", "complete");
                                parameters.put("type", "startLocationUpdates");
                                parameters.put("onObject", "app");
                                parameters.put("view", "worker");
                                //parameters.put("killswitch", "" + remotekillswitch);
                                //parameters.put("ualId", model.getUalId());
                                sendEvent(parameters, loans);
                            } catch (Exception e1) {
                                displayLog("error attaching afl to ual " + e1.toString());
                            }
                            //sendSMS("location callback complete");
                        }
                    }).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    displayLog("startLocationUpdates addOnSuccessListener 3");
                    try {
                        HashMap<String, String> loans = new HashMap<>();
                        loans.put("uniqueId", uniqueId);
                        HashMap<String, String> parameters = new HashMap<>();
                        parameters.put("eventName", "Data Collection Service");
                        parameters.put("subtype", "success");
                        parameters.put("type", "startLocationUpdates");
                        parameters.put("onObject", "app");
                        parameters.put("view", "worker");
                        //parameters.put("killswitch", "" + remotekillswitch);
                        //parameters.put("ualId", model.getUalId());
                        sendEvent(parameters, loans);
                    } catch (Exception e1) {
                        displayLog("error attaching afl to ual " + e1.toString());
                    }
                    //sendSMS("location callback success");
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    displayLog("startLocationUpdates addOnFailureListener 2 " + e.getMessage());
                    try {
                        HashMap<String, String> loans = new HashMap<>();
                        loans.put("uniqueId", uniqueId);
                        HashMap<String, String> parameters = new HashMap<>();
                        parameters.put("eventName", "Data Collection Service");
                        parameters.put("subtype", "failure");
                        parameters.put("type", "startLocationUpdates");
                        parameters.put("onObject", "app");
                        parameters.put("view", "worker");
                        //parameters.put("killswitch", "" + remotekillswitch);
                        parameters.put("error", e.getMessage());
                        sendEvent(parameters, loans);
                    } catch (Exception e1) {
                        displayLog("error attaching afl to ual " + e1.toString());
                    }
                    //sendSMS("location callback failure");
                    //startAlert(remotePingFrequency);

                }
            });
        } catch (SecurityException e) {
            displayLog("startLocationUpdates requestLocationUpdates error " + e.toString());
            //displayToast("Please enable GPS location", true);
            //sendSMS("location callback security exception");
        }


    }


    /*
    private void sendSMS(String who) {

        try {
            //String message = "https://hypertrack-996a0.firebaseapp.com/?id="+OkVerifyApplication.getUniqueId();
            //String message = remoteSmsTemplate + OkVerifyApplication.getUniqueId();
            final HashMap<String, String> jsonObject = new HashMap<>();
            jsonObject.put("userId", "GrlaR3LHUP");
            jsonObject.put("sessionToken", "r:3af107bf99e4c6f2a91e6fec046f5fc7");
            jsonObject.put(io.okheart.android.utilities.Constants.COLUMN_BRANCH, "hq_okhi");
            jsonObject.put(io.okheart.android.utilities.Constants.COLUMN_AFFILIATION, "okhi");
            jsonObject.put("customName", "test");
            jsonObject.put("phoneNumber", "+254713567907");
            jsonObject.put("phone", "+254713567907");
            jsonObject.put("message", "we have received " + getDeviceModelAndBrand() + " status " + who);
            io.okheart.android.callback.SendCustomLinkSmsCallBack sendCustomLinkSmsCallBack = new io.okheart.android.callback.SendCustomLinkSmsCallBack() {
                @Override
                public void querycomplete(String response, boolean status) {
                    if (status) {
                        displayLog("send sms success " + response);
                        //displayToast("SMS sent", true);
                    } else {
                        displayLog("send sms failure " + response);
                        //displayToast("Error " + response, true);
                    }
                }
            };
            io.okheart.android.asynctask.SendCustomLinkSmsTask sendCustomLinkSmsTask = new io.okheart.android.asynctask.SendCustomLinkSmsTask(context, sendCustomLinkSmsCallBack, jsonObject);
            sendCustomLinkSmsTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        } catch (Exception jse) {
            displayLog("jsonexception " + jse.toString());
        }

    }
    */


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

    private String getDeviceModelAndBrand() {

        String manufacturer = Build.MANUFACTURER;
        String model = Build.MODEL;
        if (model.contains(manufacturer)) {
            return model;
        } else {
            return manufacturer + " " + model;
        }

    }


    private HashMap<String, String> getTitleText(io.okheart.android.datamodel.AddressItem model) {

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

    private HashMap<String, String> getTitleText(io.okheart.android.datamodel.VerifyDataItem model) {

        String streetName = model.getStreetName();
        String propertyName = model.getPropertyName();
        String directions = model.getDirections();
        String title = model.getTitle();

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

    private void sendEvent(HashMap<String, String> parameters, HashMap<String, String> loans) {
        try {
            io.okheart.android.utilities.OkAnalytics okAnalytics = new io.okheart.android.utilities.OkAnalytics(context, environment);
            okAnalytics.sendToAnalytics(parameters, loans, environment);
        } catch (Exception e) {
            displayLog("error sending photoexpanded analytics event " + e.toString());
        }
    }

    private void displayLog(String log) {
        Log.i(TAG, log);
    }
}