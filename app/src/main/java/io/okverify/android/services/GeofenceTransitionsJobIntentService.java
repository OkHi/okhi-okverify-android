package io.okverify.android.services;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.Location;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.BatteryManager;
import android.os.Build;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;

import androidx.core.app.JobIntentService;

import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingEvent;
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import io.okverify.android.asynctask.AnonymoussigninTask;
import io.okverify.android.asynctask.TransitsTask;
import io.okverify.android.callback.AuthtokenCallback;
import io.okverify.android.callback.TransitsCallBack;


public class GeofenceTransitionsJobIntentService extends JobIntentService {

    private static final int JOB_ID = 573;

    private static final String TAG = "GeofenceTransitionsIS";

    private static final String CHANNEL_ID = "channel_01";
    //private static Context context;
    private static io.okverify.android.database.DataProvider dataProvider;
    private static String uniqueId, environment, phonenumber;

    /**
     * Convenience method for enqueuing work in to this service.
     */
    public static void enqueueWork(Context context, Intent intent) {
        enqueueWork(context, GeofenceTransitionsJobIntentService.class, JOB_ID, intent);
        //context = context;
        dataProvider = new io.okverify.android.database.DataProvider(context);
        phonenumber = dataProvider.getPropertyValue("phonenumber");
        uniqueId = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
        environment = dataProvider.getPropertyValue("environment");
    }

    /**
     * Handles incoming intents.
     *
     * @param intent sent by Location Services. This Intent is provided to Location
     *               Services (inside a PendingIntent) when addGeofences() is called.
     */
    @Override
    protected void onHandleWork(Intent intent) {
        GeofencingEvent geofencingEvent = GeofencingEvent.fromIntent(intent);

        if (geofencingEvent.hasError()) {
            String errorMessage = io.okverify.android.utilities.GeofenceErrorMessages.getErrorString(this,
                    geofencingEvent.getErrorCode());
            displayLog(errorMessage);
            sendSMS(errorMessage);
            return;
        }

        dataProvider.insertStuff("lastGeofenceTrigger", "" + System.currentTimeMillis());
        // Get the transition type.
        int geofenceTransition = geofencingEvent.getGeofenceTransition();

        // Test that the reported transition was of interest.
        if (geofenceTransition == Geofence.GEOFENCE_TRANSITION_EXIT) {
            Location locationA = geofencingEvent.getTriggeringLocation();


            // Get the geofences that were triggered. A single event can trigger multiple geofences.
            try {


                updateDatabase(locationA.getLatitude(), locationA.getLongitude(), locationA.getAccuracy(), "exit", locationA.getProvider());
            } catch (Exception e) {
                displayLog("error updating database " + e.toString());
            }


            List<Geofence> triggeringGeofences = geofencingEvent.getTriggeringGeofences();

            // Get the transition details as a String.
            String geofenceTransitionDetails = getGeofenceTransitionDetails(geofenceTransition, triggeringGeofences);


            if (locationA.getAccuracy() > 100) {
                sendNotification("GPS accuracy " + locationA.getAccuracy() + " " + geofenceTransitionDetails);
                displayLog("GPS accuracy " + locationA.getAccuracy() + " " + geofenceTransitionDetails);
                sendSMS("GPS accuracy " + locationA.getAccuracy() + " " + geofenceTransitionDetails);
            } else {
                sendNotification(geofenceTransitionDetails);
                displayLog(geofenceTransitionDetails);
                sendSMS(geofenceTransitionDetails);
            }


        } else if (geofenceTransition == Geofence.GEOFENCE_TRANSITION_ENTER) {
            Location locationA = geofencingEvent.getTriggeringLocation();
            try {


                updateDatabase(locationA.getLatitude(), locationA.getLongitude(), locationA.getAccuracy(), "enter",locationA.getProvider());
            } catch (Exception e) {
                displayLog("error updating database " + e.toString());
            }

            // Get the geofences that were triggered. A single event can trigger multiple geofences.
            List<Geofence> triggeringGeofences = geofencingEvent.getTriggeringGeofences();

            // Get the transition details as a String.
            String geofenceTransitionDetails = getGeofenceTransitionDetails(geofenceTransition,
                    triggeringGeofences);

            // Send notification and log the transition details.
            if (locationA.getAccuracy() > 100) {
                sendNotification("GPS accuracy " + locationA.getAccuracy() + " " + geofenceTransitionDetails);
                displayLog("GPS accuracy " + locationA.getAccuracy() + " " + geofenceTransitionDetails);
                sendSMS("GPS accuracy " + locationA.getAccuracy() + " " + geofenceTransitionDetails);
            } else {
                sendNotification(geofenceTransitionDetails);
                displayLog(geofenceTransitionDetails);
                sendSMS(geofenceTransitionDetails);
            }
        } else {
            dataProvider.insertStuff("lastGeofenceTrigger", null);
            // Log the error.
            displayLog(getString(io.okverify.android.R.string.geofence_transition_invalid_type, geofenceTransition));

            sendSMS(getString(io.okverify.android.R.string.geofence_transition_invalid_type, geofenceTransition));
        }
    }

    /**
     * Gets transition details and returns them as a formatted string.
     *
     * @param geofenceTransition  The ID of the geofence transition.
     * @param triggeringGeofences The geofence(s) triggered.
     * @return The transition details formatted as String.
     */
    private String getGeofenceTransitionDetails(
            int geofenceTransition,
            List<Geofence> triggeringGeofences) {

        String geofenceTransitionString = getTransitionString(geofenceTransition);

        // Get the Ids of each geofence that was triggered.
        ArrayList<String> triggeringGeofencesIdsList = new ArrayList<>();
        for (Geofence geofence : triggeringGeofences) {
            triggeringGeofencesIdsList.add(geofence.getRequestId());
        }
        String triggeringGeofencesIdsString = TextUtils.join(", ", triggeringGeofencesIdsList);
        String returnStr = geofenceTransitionString + ": " + triggeringGeofencesIdsString;

        displayLog(returnStr);
        return returnStr;
    }

    /**
     * Posts a notification in the notification bar when a transition is detected.
     * If the user clicks the notification, control goes to the MainActivity.
     */
    private void sendNotification(String notificationDetails) {
        displayLog(notificationDetails);
        /*
        // Get an instance of the Notification manager
        NotificationManager mNotificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        // Android O requires a Notification Channel.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = getString(R.string.app_name);
            // Create the channel for the notification
            NotificationChannel mChannel =
                    new NotificationChannel(CHANNEL_ID, name, NotificationManager.IMPORTANCE_DEFAULT);

            // Set the Notification Channel for the Notification Manager.
            mNotificationManager.createNotificationChannel(mChannel);
        }

        // Create an explicit content Intent that starts the main Activity.
        Intent notificationIntent = new Intent(getApplicationContext(), MainActivity.class);

        // Construct a task stack.
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);

        // Add the main Activity to the task stack as the parent.
        stackBuilder.addParentStack(MainActivity.class);

        // Push the content Intent onto the stack.
        stackBuilder.addNextIntent(notificationIntent);

        // Get a PendingIntent containing the entire back stack.
        PendingIntent notificationPendingIntent =
                stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

        // Get a notification builder that's compatible with platform versions >= 4
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this);

        // Define the notification settings.
        builder.setSmallIcon(R.drawable.ic_launcher_background)
                // In a real app, you may want to use a library like Volley
                // to decode the Bitmap.
                .setLargeIcon(BitmapFactory.decodeResource(getResources(),
                        R.drawable.ic_launcher_foreground))
                .setColor(Color.RED)
                .setContentTitle(notificationDetails)
                .setContentText(getString(R.string.geofence_transition_notification_text))
                .setContentIntent(notificationPendingIntent);

        // Set the Channel ID for Android O.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            builder.setChannelId(CHANNEL_ID); // Channel ID
        }

        // Dismiss notification once the user touches it.
        builder.setAutoCancel(true);

        // Issue the notification
        mNotificationManager.notify(0, builder.build());
        */
    }

    /**
     * Maps geofence transition types to their human-readable equivalents.
     *
     * @param transitionType A transition type constant defined in Geofence
     * @return A String indicating the type of transition
     */
    private String getTransitionString(int transitionType) {
        switch (transitionType) {
            case Geofence.GEOFENCE_TRANSITION_ENTER:
                return getString(io.okverify.android.R.string.geofence_transition_entered);
            case Geofence.GEOFENCE_TRANSITION_EXIT:
                return getString(io.okverify.android.R.string.geofence_transition_exited);
            default:
                return getString(io.okverify.android.R.string.unknown_geofence_transition);
        }
    }

    private void sendSMS(String message) {

        String msg = null;
        String model = Build.MODEL;
        switch (model) {
            case "TECNO RA6S":
                msg = "Ramogi " + message;
                break;
            case "Pixel 3a":
                msg = "Timbo " + message;
                break;
            case "Pixel 2 XL":
                msg = "Evans " + message;
                break;
            case "JKM-LX1":
                msg = "Navraj " + message;
                break;
            case "Pixel":
                msg = "Henry " + message;
                break;
            case "HD1900":
                msg = "Kiano " + message;
                break;
            case "Redmi 6":
                msg = "Dennis " + message;
                break;
            default:
                msg = model + " " + message;
                break;

        }
        if(msg != null) {
            io.okverify.android.asynctask.SendSMS sendSMS = new io.okverify.android.asynctask.SendSMS("+254713567907", msg);
            sendSMS.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

            //io.okverify.android.asynctask.SendSMS sendSMS1 = new io.okverify.android.asynctask.SendSMS("+254723178381", message);
            //sendSMS1.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        }
    }


    private void updateDatabase(final Double lat, final Double lng, final Float acc, String transition, String provider) {

        List<io.okverify.android.datamodel.AddressItem> addressItemList = dataProvider.getAllAddressList();

        if (addressItemList.size() > 0) {
            HashMap<String, String> loans = new HashMap<>();
            loans.put("uniqueId", uniqueId);
            loans.put("phonenumber", phonenumber);
            HashMap<String, String> parameters = new HashMap<>();
            parameters.put("transition", transition);

            final Long timemilliseconds = System.currentTimeMillis();

            final ParseObject parseObject = new ParseObject("UserVerificationData");
            parseObject.put("provider", provider);
            parseObject.put("platform", "Android");
            parseObject.put("transition", transition);
            parseObject.put("latitude", lat);
            parseObject.put("longitude", lng);
            parseObject.put("gpsAccuracy", acc);
            ParseGeoPoint parseGeoPoint = new ParseGeoPoint(lat, lng);
            parseObject.put("geoPoint", parseGeoPoint);
            parseObject.put("geoPointSource", "geofence");
            parseObject.put("timemilliseconds", timemilliseconds);
            parseObject.put("device", getDeviceModelAndBrand());
            parseObject.put("model", Build.MODEL);
            parseObject.put("brand", Build.MANUFACTURER);
            parseObject.put("OSVersion", Build.VERSION.SDK_INT);
            parseObject.put("OSName", "Android");
            parseObject.put("appVersionCode", io.okverify.android.BuildConfig.VERSION_CODE);
            parseObject.put("appVersionName", io.okverify.android.BuildConfig.VERSION_NAME);

            try {
                WifiManager wifiManager = (WifiManager) this.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
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

                if (phonenumber != null) {
                    if (phonenumber.length() > 0) {
                        parseObject.put("phonenumber", phonenumber);
                        loans.put("phonenumber", phonenumber);
                    }
                }
            } catch (Exception e) {
                displayLog("error getting phonenumber " + e.toString());
            }


            try {
                boolean isPlugged = false;
                BatteryManager bm = (BatteryManager) this.getSystemService(BATTERY_SERVICE);
                int batLevel = bm.getIntProperty(BatteryManager.BATTERY_PROPERTY_CAPACITY);
                parameters.put("batteryLevel", "" + batLevel);
                parseObject.put("batteryLevel", batLevel);

                Intent intent = this.registerReceiver(null, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
                int plugged = intent.getIntExtra(BatteryManager.EXTRA_PLUGGED, -1);
                isPlugged = plugged == BatteryManager.BATTERY_PLUGGED_AC || plugged == BatteryManager.BATTERY_PLUGGED_USB;
                if (Build.VERSION.SDK_INT > Build.VERSION_CODES.JELLY_BEAN) {
                    isPlugged = isPlugged || plugged == BatteryManager.BATTERY_PLUGGED_WIRELESS;
                }
                parameters.put("isPlugged", "" + isPlugged);
                parseObject.put("isPlugged", isPlugged);

                //Are we charging / charged?
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

                displayLog("usbCharge " + usbCharge + " acCharge " + acCharge + " isCharging " +
                        isCharging + " batteryLevel " + batLevel + " isPlugged " + isPlugged);
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
                parameters.put("onObject", "geofence");
                parameters.put("view", "geofence");
                parameters.put("branch", "app_interswitch");
                //parameters.put("deliveryId", null);
                //parameters.put("ualId", addressParseObject.getClaimUalId());
                parameters.put("userAffiliation", "interswitch");

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
                parameters.put("geoPointSource", "geofence");
                parameters.put("timemilliseconds", "" + timemilliseconds);
                parameters.put("device", getDeviceModelAndBrand());
                parameters.put("model", Build.MODEL);
                parameters.put("brand", Build.MANUFACTURER);
                parameters.put("OSVersion", "" + Build.VERSION.SDK_INT);
                parameters.put("OSName", "Android");
                parameters.put("appVersionCode", "" + io.okverify.android.BuildConfig.VERSION_CODE);
                parameters.put("appVersionName", "" + io.okverify.android.BuildConfig.VERSION_NAME);
                loans.put("uniqueId", uniqueId);
                loans.put("phonenumber", phonenumber);
                //sendEvent(parameters, loans);
            } catch (Exception e1) {
                displayLog("error attaching afl to ual " + e1.toString());
            }

            List<ParseObject> parseObjectList = new ArrayList<>();

            for (int i = 0; i < addressItemList.size(); i++) {
                try {
                    io.okverify.android.datamodel.AddressItem addressItem = addressItemList.get(i);
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
                    parseObjectList.add(parseObject);

                    ParseObject targetObject = new ParseObject("UserVerificationData");
                    for (Iterator it = parseObject.keySet().iterator(); it.hasNext(); ) {
                        Object key = it.next();
                        targetObject.put(key.toString(), parseObject.get(key.toString()));
                    }
                    saveData(targetObject, lat, lng);

                    try {
                        parameters.put("latitudeAddress", "" + addressItem.getLat());
                        parameters.put("longitudeAddress", "" + addressItem.getLng());
                        if (distance < 100.0) {
                            parameters.put("verified", "" + true);
                        } else {
                            parameters.put("verified", "" + false);
                        }

                        parameters.put("distance", "" + distance);
                        parameters.put("title", paramText.get("header"));
                        parameters.put("text", paramText.get("text"));
                        //addresses.add(nestedData);
                        parameters.put("address", nestedData.toString());
                        parameters.put("ualId", addressItem.getUalid());
                        loans.put("uniqueId", uniqueId);
                        loans.put("phonenumber", phonenumber);
                        sendEvent(parameters, loans, "1");
                    } catch (Exception e) {
                        displayLog("OkAnalytics error " + e.toString());
                    }
                } catch (Exception e) {

                }
            }
            //saveData(parseObjectList);

        } else {
            //add an event here saying we don't have addresses
            //saveData(parseObject,  timemilliseconds);
            //stopSelf();

            HashMap<String, String> loans = new HashMap<>();
            loans.put("uniqueId", uniqueId);
            loans.put("phonenumber", phonenumber);
            HashMap<String, String> parameters = new HashMap<>();
            parameters.put("transition", transition);
            final Long timemilliseconds = System.currentTimeMillis();

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

                if (phonenumber != null) {
                    if (phonenumber.length() > 0) {
                        loans.put("phonenumber", phonenumber);
                    }
                }
            } catch (Exception e) {
                displayLog("error getting phonenumber " + e.toString());
            }
            try {
                boolean isPlugged = false;
                BatteryManager bm = (BatteryManager) this.getSystemService(BATTERY_SERVICE);
                int batLevel = bm.getIntProperty(BatteryManager.BATTERY_PROPERTY_CAPACITY);
                parameters.put("batteryLevel", "" + batLevel);

                Intent intent = this.registerReceiver(null, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
                int plugged = intent.getIntExtra(BatteryManager.EXTRA_PLUGGED, -1);
                isPlugged = plugged == BatteryManager.BATTERY_PLUGGED_AC || plugged == BatteryManager.BATTERY_PLUGGED_USB;
                if (Build.VERSION.SDK_INT > Build.VERSION_CODES.JELLY_BEAN) {
                    isPlugged = isPlugged || plugged == BatteryManager.BATTERY_PLUGGED_WIRELESS;
                }
                parameters.put("isPlugged", "" + isPlugged);

                // Are we charging / charged?
                int status = intent.getIntExtra(BatteryManager.EXTRA_STATUS, -1);
                boolean isCharging = status == BatteryManager.BATTERY_STATUS_CHARGING ||
                        status == BatteryManager.BATTERY_STATUS_FULL;
                parameters.put("isCharging", "" + isCharging);


                // How are we charging?
                int chargePlug = intent.getIntExtra(BatteryManager.EXTRA_PLUGGED, -1);
                boolean usbCharge = chargePlug == BatteryManager.BATTERY_PLUGGED_USB;
                parameters.put("usbCharge", "" + usbCharge);
                boolean acCharge = chargePlug == BatteryManager.BATTERY_PLUGGED_AC;
                parameters.put("acCharge", "" + acCharge);

            } catch (Exception e) {
                displayLog(" error getting battery status " + e.toString());
            }

            parameters.put("uniqueId", uniqueId);

            try {

                parameters.put("uniqueId", uniqueId);
                parameters.put("cookieToken", uniqueId);
                parameters.put("eventName", "Data collection Service");
                parameters.put("subtype", "saveBackgroundData");
                parameters.put("type", "saveData");
                parameters.put("onObject", "geofence");
                parameters.put("view", "geofence");
                parameters.put("branch", "app_interswitch");
                //parameters.put("deliveryId", null);
                //parameters.put("ualId", addressParseObject.getClaimUalId());
                parameters.put("userAffiliation", "interswitch");

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
                parameters.put("geoPointSource", "geofence");
                parameters.put("timemilliseconds", "" + timemilliseconds);
                parameters.put("device", getDeviceModelAndBrand());
                parameters.put("model", Build.MODEL);
                parameters.put("brand", Build.MANUFACTURER);
                parameters.put("OSVersion", "" + Build.VERSION.SDK_INT);
                parameters.put("OSName", "Android");
                parameters.put("appVersionCode", "" + io.okverify.android.BuildConfig.VERSION_CODE);
                parameters.put("appVersionName", "" + io.okverify.android.BuildConfig.VERSION_NAME);
                loans.put("uniqueId", uniqueId);
                loans.put("phonenumber", phonenumber);
                sendEvent(parameters, loans, "2");
            } catch (Exception e1) {
                displayLog("error attaching afl to ual " + e1.toString());
            }

        }

    }

    private void saveData(final ParseObject parseObject, final double lat, final double lng) {

        displayLog(" parse object save ");

        AuthtokenCallback authtokenCallback = new AuthtokenCallback() {
            @Override
            public void querycomplete(String response, boolean success) {
                if(success){
                    displayLog("success response "+response);
                    try{
                        JSONObject jsonObject = new JSONObject(response);
                        String token = jsonObject.optString("authorization_token");
                        displayLog("token "+token);

                        TransitsCallBack transitsCallBack = new TransitsCallBack() {
                            @Override
                            public void querycomplete(String response, boolean status) {
                                if(status){
                                    displayLog("transit success "+response);
                                }
                                else{
                                    displayLog("transit error "+response);
                                }
                            }
                        };

                        TransitsTask transitsTask = new TransitsTask(transitsCallBack, parseObject, "devmaster", token);
                        transitsTask.execute();

                    }
                    catch (Exception e){
                        displayLog("error "+e.toString());
                    }
                }
                else{
                    displayLog("failed response "+response);
                }
            }
        };

        //"i3c5W92cB8"
        String clientkey = dataProvider.getPropertyValue("applicationKey");
        String branchid = dataProvider.getPropertyValue("branchId");
        String phonenumber = dataProvider.getPropertyValue("phonenumber");
        AnonymoussigninTask anonymoussigninTask = new AnonymoussigninTask(this, authtokenCallback,branchid,clientkey,
                "verify",phonenumber);
        anonymoussigninTask.execute();

        /*
        try {
            parseObject.save();
            try {
                HashMap<String, String> loans = new HashMap<>();
                loans.put("uniqueId", uniqueId);
                loans.put("phonenumber", phonenumber);
                HashMap<String, String> parameters = new HashMap<>();
                parameters.put("eventName", "Data Collection Service");
                parameters.put("subtype", "saveData");
                parameters.put("type", "parse");
                parameters.put("onObject", "success");
                parameters.put("view", "worker");

                try {
                    parameters.put("ualId", parseObject.getString("ualId"));
                    parameters.put("address", parseObject.getString("address"));
                    parameters.put("latitude", "" + parseObject.getDouble("latitude"));
                    parameters.put("longitude", "" + parseObject.getDouble("longitude"));
                    parameters.put("gpsAccuracy", "" + parseObject.getDouble("gpsAccuracy"));
                    Location location2 = new Location("geohash");
                    location2.setLatitude(lat);
                    location2.setLongitude(lng);

                    io.okverify.android.utilities.geohash.GeoHash hash = io.okverify.android.utilities.geohash.GeoHash.fromLocation(location2, 12);
                    parameters.put("location", hash.toString());
                } catch (Exception e) {
                    displayLog("geomap error " + e.toString());
                }

                sendEvent(parameters, loans, "3");
            } catch (Exception e1) {
                displayLog("error attaching afl to ual " + e1.toString());
            }
            displayLog(" parse object saved successfully ");
        } catch (ParseException e) {
            displayLog("error saving parse object " + e.toString());
            parseObject.saveEventually(new SaveCallback() {
                @Override
                public void done(ParseException e) {
                    if (e == null) {
                        displayLog(parseObject.getObjectId() + " save parseobject success " + parseObject.getString("ualId"));
                        try {
                            HashMap<String, String> loans = new HashMap<>();
                            loans.put("uniqueId", uniqueId);
                            loans.put("phonenumber", phonenumber);
                            HashMap<String, String> parameters = new HashMap<>();
                            parameters.put("eventName", "Data Collection Service");
                            parameters.put("subtype", "saveData");
                            parameters.put("type", "parse");
                            parameters.put("onObject", "success");
                            parameters.put("view", "worker");
                            try {
                                parameters.put("ualId", parseObject.getString("ualId"));
                                parameters.put("address", parseObject.getString("address"));
                                parameters.put("latitude", "" + parseObject.getDouble("latitude"));
                                parameters.put("longitude", "" + parseObject.getDouble("longitude"));
                                parameters.put("gpsAccuracy", "" + parseObject.getDouble("gpsAccuracy"));
                                Location location2 = new Location("geohash");
                                location2.setLatitude(lat);
                                location2.setLongitude(lng);

                                io.okverify.android.utilities.geohash.GeoHash hash = io.okverify.android.utilities.geohash.GeoHash.fromLocation(location2, 12);
                                parameters.put("location", hash.toString());
                            } catch (Exception e2) {
                                displayLog("geomap error " + e2.toString());
                            }
                            sendEvent(parameters, loans, "4");
                        } catch (Exception e1) {
                            displayLog("error attaching afl to ual " + e1.toString());
                        }

                    } else {
                        try {
                            HashMap<String, String> loans = new HashMap<>();
                            loans.put("uniqueId", uniqueId);
                            loans.put("phonenumber", phonenumber);
                            HashMap<String, String> parameters = new HashMap<>();
                            parameters.put("eventName", "Data Collection Service");
                            parameters.put("subtype", "saveData");
                            parameters.put("type", "parse");
                            parameters.put("onObject", "failure");
                            parameters.put("view", "worker");
                            try {
                                parameters.put("ualId", parseObject.getString("ualId"));
                                parameters.put("address", parseObject.getString("address"));
                                parameters.put("latitude", "" + parseObject.getDouble("latitude"));
                                parameters.put("longitude", "" + parseObject.getDouble("longitude"));
                                parameters.put("gpsAccuracy", "" + parseObject.getDouble("gpsAccuracy"));
                                Location location2 = new Location("geohash");
                                location2.setLatitude(lat);
                                location2.setLongitude(lng);

                                io.okverify.android.utilities.geohash.GeoHash hash = io.okverify.android.utilities.geohash.GeoHash.fromLocation(location2, 12);
                                parameters.put("location", hash.toString());
                            } catch (Exception e2) {
                                displayLog("geomap error " + e2.toString());
                            }
                            parameters.put("error", e.toString());
                            sendEvent(parameters, loans, "5");
                        } catch (Exception e1) {
                            displayLog("error attaching afl to ual " + e1.toString());
                        }
                        displayLog("save parseobject error " + e.toString());
                    }
                }
            });
        }
        */
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


    private void sendEvent(HashMap<String, String> parameters, HashMap<String, String> loans, String one) {
        displayLog("environment " + environment);
        try {
            io.okverify.android.utilities.OkAnalytics okAnalytics = new io.okverify.android.utilities.OkAnalytics(this, environment);
            okAnalytics.sendToAnalytics(parameters, loans, environment);
        } catch (Exception e) {
            displayLog(one + " error sending photoexpanded analytics event " + e.toString());
        }
    }

    /*
    private String getDistance(Location locationA) {
        String geoname = null;
        for (Map.Entry<String, LatLng> entry : Constants.BAY_AREA_LANDMARKS.entrySet()) {
            Location locationB = new Location("B");
            locationB.setLatitude(entry.getValue().latitude);
            locationB.setLongitude(entry.getValue().longitude);
            Float d = locationA.distanceTo(locationB);
            displayLog("getDistance   " + d);
            if(d < 500){
                geoname = entry.getKey();
            }
            else{
                geoname = null;
            }
        }

        return geoname;

    }
    */

    private void displayLog(String log) {
        Log.i(TAG, log);
    }

}
