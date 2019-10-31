package io.okheart.android.services;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ComponentName;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.media.RingtoneManager;
import android.net.Uri;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.BatteryManager;
import android.os.Build;
import android.os.IBinder;
import android.provider.Settings;
import android.util.Log;
import android.view.Gravity;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.parse.ParseException;
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;
import com.parse.SaveCallback;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.okheart.android.BuildConfig;
import io.okheart.android.R;
import io.okheart.android.database.DataProvider;
import io.okheart.android.datamodel.VerifyDataItem;
import io.okheart.android.receivers.BootReceiver;
import io.okheart.android.receivers.MyBroadcastReceiver;
import io.okheart.android.utilities.OkAnalytics;
import io.okheart.android.utilities.geohash.GeoHash;

import static android.app.NotificationManager.IMPORTANCE_LOW;
import static io.okheart.android.utilities.Constants.COLUMN_BRANCH;
import static io.okheart.android.utilities.Constants.COLUMN_CLAIMUALID;
import static io.okheart.android.utilities.Constants.COLUMN_CUSTOMERNAME;
import static io.okheart.android.utilities.Constants.COLUMN_DIRECTION;
import static io.okheart.android.utilities.Constants.COLUMN_IMAGEURL;
import static io.okheart.android.utilities.Constants.COLUMN_LAT;
import static io.okheart.android.utilities.Constants.COLUMN_LNG;
import static io.okheart.android.utilities.Constants.COLUMN_LOCATIONNAME;
import static io.okheart.android.utilities.Constants.COLUMN_LOCATIONNICKNAME;
import static io.okheart.android.utilities.Constants.COLUMN_PHONECUSTOMER;
import static io.okheart.android.utilities.Constants.COLUMN_PROPERTYNAME;
import static io.okheart.android.utilities.Constants.COLUMN_STREETNAME;
import static io.okheart.android.utilities.Constants.REMOTE_ADDRESS_FREQUENCY_THRESHOLD;
import static io.okheart.android.utilities.Constants.REMOTE_AUTO_STOP;
import static io.okheart.android.utilities.Constants.REMOTE_BACKGROUND_LOCATION_FREQUENCY;
import static io.okheart.android.utilities.Constants.REMOTE_GEOSEARCH_RADIUS;
import static io.okheart.android.utilities.Constants.REMOTE_GPS_ACCURACY;
import static io.okheart.android.utilities.Constants.REMOTE_KILL_SWITCH;

public class ForegroundService extends Service {

    //private String status;
    protected static final int REQUEST_CHECK_SETTINGS = 0x1;
    private static final String TAG = "ForegroundService";
    //public Location alocation;
    //private static LocationManager locationManager;
    //private static final long UPDATE_INTERVAL_IN_MILLISECONDS = 300000;
    //private static final long FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS = UPDATE_INTERVAL_IN_MILLISECONDS / 2;
    private static final int MY_PERMISSIONS_ACCESS_FINE_LOCATION = 7;
    private SettingsClient mSettingsClient;
    private LocationSettingsRequest mLocationSettingsRequest;
    private FirebaseFirestore mFirestore;
    private LocationCallback mLocationCallback;
    private FusedLocationProviderClient mFusedLocationClient;
    private LocationRequest mLocationRequest;
    private Location mCurrentLocation;
    private Double lat, lng;
    private Float acc;
    private DataProvider dataProvider;
    private Query query, queryAlarm;
    private List<Map<String, Object>> addresses;
    private FirebaseRemoteConfig mFirebaseRemoteConfig;
    private Integer remotelocationfrequency, remoteaddressfrequency;
    private Double remotegeosearchradius;
    private Double remotegpsaccuracy;
    private Boolean remotekillswitch;
    private Boolean remoteautostop;
    private NotificationManager notificationManager;
    //private MainActivity mainActivity;
    private Boolean firestore;
    private Boolean parsedb;
    private AlarmManager alarmManager;
    private String uniqueId;


    public ForegroundService() {

    }

    private static void displayLog(String me) {
        Log.i(TAG, me);
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        //OkVerifyApplication.setForegroundServiceStatus(false);
        displayLog("foreground service destroyed");

        try {
            HashMap<String, String> loans = new HashMap<>();
            //loans.put("phonenumber",postDataParams.get("phone"));
            //loans.put("ualId", model.getUalId());
            HashMap<String, String> parameters = new HashMap<>();
            parameters.put("eventName", "Data Collection Service");
            parameters.put("subtype", "destroy");
            parameters.put("type", "onDestroy");
            parameters.put("onObject", "app");
            parameters.put("view", "foregroundService");
            parameters.put("firestore", "" + firestore);
            parameters.put("parse", "" + parsedb);
            sendEvent(parameters, loans);
        } catch (Exception e1) {
            displayLog("error attaching afl to ual " + e1.toString());
        }

        if (notificationManager != null) {
            notificationManager.cancelAll();
        }

        stopLocationUpdates();

        startAlert();

        /*
        if(status.equalsIgnoreCase("false")) {
            displayLog("alarm status is false");
            startAlert();
        }
        else{
            displayLog("alarm status is true");
        }
        */
    }

    public void startAlert() {
        //EditText text = findViewById(R.id.time);
        int i = Integer.parseInt("3600");
        Intent intent = new Intent(this, MyBroadcastReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this.getApplicationContext(), 987623224, intent, 0);

        alarmManager.setExact(AlarmManager.RTC_WAKEUP,
                System.currentTimeMillis() + (i * 1000),
                pendingIntent);


        try {
            ComponentName receiver = new ComponentName(this, BootReceiver.class);
            PackageManager pm = this.getPackageManager();

            pm.setComponentEnabledSetting(receiver,
                    PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
                    PackageManager.DONT_KILL_APP);
        } catch (Exception e) {

        }

        /*
        Map<String, Object> city = new HashMap<>();
        city.put("status", "true");
        mFirestore.collection("alarm").document(OkVerifyApplication.getUniqueId())
                .set(city)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        displayLog("DocumentSnapshot successfully written!");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        displayLog("Error writing document"+ e);
                    }
                });
        */
        displayLog("Alarm set in " + i + " seconds");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        displayLog("My foreground service onCreate().");
        firestore = false;
        parsedb = false;
        //Constants.scheduleJob(ForegroundService.this, "Foreground service");
        alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        uniqueId = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);

        //status = "false";


        //OneTimeWorkRequest oneTimeWorkRequest = new OneTimeWorkRequest.Builder(MyWorker.class).setInitialDelay(3600, TimeUnit.SECONDS).build();
        //WorkManager.getInstance(this).enqueueUniqueWork("ramogi", ExistingWorkPolicy.REPLACE, oneTimeWorkRequest);


        try {
            ComponentName receiver = new ComponentName(this, BootReceiver.class);
            PackageManager pm = this.getPackageManager();

            pm.setComponentEnabledSetting(receiver,
                    PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
                    PackageManager.DONT_KILL_APP);
        } catch (Exception e) {

        }

        try {
            HashMap<String, String> loans = new HashMap<>();
            //loans.put("phonenumber",postDataParams.get("phone"));
            //loans.put("ualId", model.getUalId());
            HashMap<String, String> parameters = new HashMap<>();
            parameters.put("eventName", "Data Collection Service");
            parameters.put("subtype", "create");
            parameters.put("type", "onCreate");
            parameters.put("onObject", "app");
            parameters.put("view", "foregroundService");
            //parameters.put("branch", "branch");
            //parameters.put("ualId", model.getUalId());
            sendEvent(parameters, loans);
        } catch (Exception e1) {
            displayLog("error attaching afl to ual " + e1.toString());
        }
        try {
            //mainActivity = OkVerifyApplication.getMainActivity();
        } catch (Exception e) {
            displayLog("mainactivity is null");
        }
        mFirebaseRemoteConfig = FirebaseRemoteConfig.getInstance();
        try {
            Double tempFrequency = mFirebaseRemoteConfig.getDouble(REMOTE_BACKGROUND_LOCATION_FREQUENCY);
            remotelocationfrequency = tempFrequency.intValue();
        } catch (Exception e) {
            displayLog("error getting remotelocationfrequency " + e.toString());
        }
        try {
            remoteautostop = mFirebaseRemoteConfig.getBoolean(REMOTE_AUTO_STOP);
        } catch (Exception e) {
            displayLog("error getting remoteautostop " + e.toString());
        }
        try {
            remotegpsaccuracy = mFirebaseRemoteConfig.getDouble(REMOTE_GPS_ACCURACY);
        } catch (Exception e) {
            displayLog("error getting remotegpsaccuracy " + e.toString());
        }
        try {
            remotekillswitch = mFirebaseRemoteConfig.getBoolean(REMOTE_KILL_SWITCH);
        } catch (Exception e) {
            displayLog("error getting remotekillswitch " + e.toString());
        }
        try {
            Double tempFrequency = mFirebaseRemoteConfig.getDouble(REMOTE_ADDRESS_FREQUENCY_THRESHOLD);
            remoteaddressfrequency = tempFrequency.intValue();
        } catch (Exception e) {
            displayLog("error getting frequency " + e.toString());
        }
        try {
            remotegeosearchradius = mFirebaseRemoteConfig.getDouble(REMOTE_GEOSEARCH_RADIUS);
        } catch (Exception e) {
            displayLog("error getting frequency " + e.toString());
        }
        notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        dataProvider = new DataProvider(ForegroundService.this);
        mFirestore = FirebaseFirestore.getInstance();
        query = mFirestore.collection("addresses").document(uniqueId)
                .collection("addresses")
                .orderBy("timestamp", Query.Direction.DESCENDING);

        try {
            mFusedLocationClient = LocationServices.getFusedLocationProviderClient(ForegroundService.this);
            mSettingsClient = LocationServices.getSettingsClient(ForegroundService.this);
        } catch (Exception e) {
            displayLog("mfusedlocationclient error " + e.toString());
        }
        /*
        try {
            DocumentReference docRef = mFirestore.collection("alarms").document(OkVerifyApplication.getUniqueId());
            docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {
                            displayLog("DocumentSnapshot data: " + document.getData());
                            status = (String) document.get("status");
                        } else {
                            displayLog( "No such document");

                        }
                    } else {
                        displayLog("get failed with "+ task.getException());
                    }
                }
            });
        }
        catch (Exception e){
            displayLog("error querying firestore for alarm state "+e.toString());
        }
        */


        /*
        try {
            boolean permissionAccessFineLocationApproved =
                    ActivityCompat.checkSelfPermission(mainActivity, Manifest.permission.ACCESS_FINE_LOCATION)
                            == PackageManager.PERMISSION_GRANTED;

            if (permissionAccessFineLocationApproved) {
                try {
                    OkAnalytics okAnalytics = new OkAnalytics();
                    HashMap<String, String> loans = new HashMap<>();
                    //loans.put(PROP_ACTORPHONENUMBER, loginphonenumber);
                    //loans.put(PROP_ACTORNAME, loginname);
                    //loans.put("phonenumber", null);
                    loans.put("type", "Manifest.permission.ACCESS_FINE_LOCATION");
                    okAnalytics.initializeDynamicParameters("app", "permissionAccessFineLocationApproved",
                            "permission", "mainActivityView", null, loans);
                    okAnalytics.sendToAnalytics("hq_okhi", null, null, "okhi");
                } catch (Exception e) {
                    displayLog("event.submit okanalytics error " + e.toString());
                }
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    boolean backgroundLocationPermissionApproved =
                            ActivityCompat.checkSelfPermission(ForegroundService.this,
                                    Manifest.permission.ACCESS_BACKGROUND_LOCATION)
                                    == PackageManager.PERMISSION_GRANTED;

                    if (backgroundLocationPermissionApproved) {
                        // App can access location both in the foreground and in the background.
                        // Start your service that doesn't have a foreground service type
                        // defined.
                        try {
                            OkAnalytics okAnalytics = new OkAnalytics();
                            HashMap<String, String> loans = new HashMap<>();
                            //loans.put(PROP_ACTORPHONENUMBER, loginphonenumber);
                            //loans.put(PROP_ACTORNAME, loginname);
                            //loans.put("phonenumber", null);
                            loans.put("type", "Manifest.permission.ACCESS_FINE_LOCATION");
                            okAnalytics.initializeDynamicParameters("app", "backgroundLocationPermissionApproved",
                                    "permission", "mainActivityView", null, loans);
                            okAnalytics.sendToAnalytics("hq_okhi", null, null, "okhi");
                        } catch (Exception e) {
                            displayLog("event.submit okanalytics error " + e.toString());
                        }
                        //Constants.scheduleJob(MainActivity.this);
                    } else {
                        // App can only access location in the foreground. Display a dialog
                        // warning the user that your app must have all-the-time access to
                        // location in order to function properly. Then, request background
                        // location.
                        try {
                            OkAnalytics okAnalytics = new OkAnalytics();
                            HashMap<String, String> loans = new HashMap<>();
                            //loans.put(PROP_ACTORPHONENUMBER, loginphonenumber);
                            //loans.put(PROP_ACTORNAME, loginname);
                            //loans.put("phonenumber", null);
                            loans.put("type", "Manifest.permission.ACCESS_FINE_LOCATION");
                            okAnalytics.initializeDynamicParameters("app", "backgroundLocationPermissionNotApproved",
                                    "permission", "mainActivityView", null, loans);
                            okAnalytics.sendToAnalytics("hq_okhi", null, null, "okhi");
                        } catch (Exception e) {
                            displayLog("event.submit okanalytics error " + e.toString());
                        }
                        ActivityCompat.requestPermissions(mainActivity, new String[]{
                                        Manifest.permission.ACCESS_BACKGROUND_LOCATION},
                                MY_PERMISSIONS_ACCESS_FINE_LOCATION);
                    }
                } else {

                }
            } else {
                // App doesn't have access to the device's location at all. Make full request
                // for permission.
                try {
                    OkAnalytics okAnalytics = new OkAnalytics();
                    HashMap<String, String> loans = new HashMap<>();
                    //loans.put(PROP_ACTORPHONENUMBER, loginphonenumber);
                    //loans.put(PROP_ACTORNAME, loginname);
                    //loans.put("phonenumber", null);
                    loans.put("type", "Manifest.permission.ACCESS_FINE_LOCATION");
                    okAnalytics.initializeDynamicParameters("app", "permissionAccessFineLocationNotApproved",
                            "permission", "mainActivityView", null, loans);
                    okAnalytics.sendToAnalytics("hq_okhi", null, null, "okhi");
                } catch (Exception e) {
                    displayLog("event.submit okanalytics error " + e.toString());
                }
                try {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                        ActivityCompat.requestPermissions(mainActivity, new String[]{
                                        Manifest.permission.ACCESS_FINE_LOCATION,
                                        Manifest.permission.ACCESS_BACKGROUND_LOCATION
                                },
                                MY_PERMISSIONS_ACCESS_FINE_LOCATION);
                    } else {
                        ActivityCompat.requestPermissions(mainActivity, new String[]{
                                        Manifest.permission.ACCESS_FINE_LOCATION},
                                MY_PERMISSIONS_ACCESS_FINE_LOCATION);
                    }
                } catch (Exception e) {
                    displayLog("Error getting permissions " + e.toString());
                }

            }
        } catch (Exception e) {
            displayLog("error getting permission " + e.toString());
        }
        */

        // Kick off the process of building the LocationCallback, LocationRequest, and
        // LocationSettingsRequest objects.
        createLocationCallback();
        createLocationRequest();
        buildLocationSettingsRequest();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        /*
        if (remotekillswitch) {
            displayLog("don't collect data");
            try {
                HashMap<String, String> loans = new HashMap<>();
                //loans.put("phonenumber",postDataParams.get("phone"));
                //loans.put("ualId", model.getUalId());
                HashMap<String, String> parameters = new HashMap<>();
                parameters.put("eventName", "Data Collection Service");
                parameters.put("subtype", "start");
                parameters.put("type", "onStartCommand");
                parameters.put("onObject", "app");
                parameters.put("view", "foregroundService");
                parameters.put("killswitch", "" + remotekillswitch);
                //parameters.put("branch", "branch");
                //parameters.put("ualId", model.getUalId());
                sendEvent(parameters, loans);
            } catch (Exception e1) {
                displayLog("error attaching afl to ual " + e1.toString());
            }
        } else {
            displayLog("please collect data");
            try {
                HashMap<String, String> loans = new HashMap<>();
                //loans.put("phonenumber",postDataParams.get("phone"));
                //loans.put("ualId", model.getUalId());
                HashMap<String, String> parameters = new HashMap<>();
                parameters.put("eventName", "Data Collection Service");
                parameters.put("subtype", "start");
                parameters.put("type", "onStartCommand");
                parameters.put("onObject", "app");
                parameters.put("view", "foregroundService");
                parameters.put("killswitch", "" + remotekillswitch);
                //parameters.put("ualId", model.getUalId());
                sendEvent(parameters, loans);
            } catch (Exception e1) {
                displayLog("error attaching afl to ual " + e1.toString());
            }
            startForegroundService();
        }
        */

        try {
            HashMap<String, String> loans = new HashMap<>();
            //loans.put("phonenumber",postDataParams.get("phone"));
            //loans.put("ualId", model.getUalId());
            HashMap<String, String> parameters = new HashMap<>();
            parameters.put("eventName", "Data Collection Service");
            parameters.put("subtype", "start");
            parameters.put("type", "onStartCommand");
            parameters.put("onObject", "app");
            parameters.put("view", "foregroundService");
            //parameters.put("killswitch", "" + remotekillswitch);
            //parameters.put("ualId", model.getUalId());
            sendEvent(parameters, loans);
        } catch (Exception e1) {
            displayLog("error attaching afl to ual " + e1.toString());
        }
        startForegroundService();

        return super.onStartCommand(intent, flags, startId);
    }

    /* Used to build and start foreground service. */
    private void startForegroundService() {

        if (notificationManager != null) {
            displayLog("notification is not null");
            notificationManager.cancelAll();
        } else {
            displayLog("notification is null");
        }

        displayLog("Start foreground service.");
        Bitmap largeIconBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.ic_launcher_foreground);
        /*
        NotificationCompat.BigTextStyle bigTextStyle = new NotificationCompat.BigTextStyle();
        bigTextStyle.setBigContentTitle("Address verification service.");
        bigTextStyle.bigText("Proof of address powered by OkHi");
        */

        Intent playIntent = new Intent(this, ForegroundService.class);
        playIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getService(this, 0, playIntent, PendingIntent.FLAG_ONE_SHOT);
        //NotificationCompat.Action playAction = new NotificationCompat.Action(android.R.drawable.ic_media_play, "Play", pendingIntent);

        String channelId = getString(R.string.default_notification_channel_id);
        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder =
                new NotificationCompat.Builder(this, channelId)
                        .setSmallIcon(R.drawable.ic_stat_ic_notification)
                        .setContentTitle("OkVerify")
                        .setContentText("Location verification in progress")
                        .setAutoCancel(true)
                        .setLargeIcon(largeIconBitmap)
                        .setPriority(IMPORTANCE_LOW)
                        .setCategory(NotificationCompat.CATEGORY_MESSAGE)
                        .setWhen(System.currentTimeMillis())
                        //.setSound(defaultSoundUri)
                        //.addAction(playAction)
                        .setFullScreenIntent(pendingIntent, false)
                        //.setStyle(bigTextStyle)
                        .setContentIntent(pendingIntent);


        // Since android Oreo notification channel is needed.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(channelId,
                    "Channel human readable title",
                    NotificationManager.IMPORTANCE_DEFAULT);
            notificationManager.createNotificationChannel(channel);

        }
        Notification notification = notificationBuilder.build();


        notificationManager.notify(1, notification);

        // Start foreground service.
        startForeground(1, notification);

        startLocationUpdates();

    }

    private void stopLocationUpdates() {
        try {
            HashMap<String, String> loans = new HashMap<>();
            //loans.put("phonenumber",postDataParams.get("phone"));
            //loans.put("ualId", model.getUalId());
            HashMap<String, String> parameters = new HashMap<>();
            parameters.put("eventName", "Data Collection Service");
            parameters.put("subtype", "stop");
            parameters.put("type", "stopLocationUpdates");
            parameters.put("onObject", "app");
            parameters.put("view", "foregroundService");
            sendEvent(parameters, loans);
        } catch (Exception e1) {
            displayLog("error attaching afl to ual " + e1.toString());
        }
        mFusedLocationClient.removeLocationUpdates(mLocationCallback);
    }

    private void startLocationUpdates() {
        displayLog("startLocationUpdates");
        try {
            HashMap<String, String> loans = new HashMap<>();
            //loans.put("phonenumber",postDataParams.get("phone"));
            //loans.put("ualId", model.getUalId());
            HashMap<String, String> parameters = new HashMap<>();
            parameters.put("eventName", "Data Collection Service");
            parameters.put("subtype", "start");
            parameters.put("type", "startLocationUpdates");
            parameters.put("onObject", "app");
            parameters.put("view", "foregroundService");
            parameters.put("killswitch", "" + remotekillswitch);
            //parameters.put("ualId", model.getUalId());
            sendEvent(parameters, loans);
        } catch (Exception e1) {
            displayLog("error attaching afl to ual " + e1.toString());
        }
        try {
            mFusedLocationClient.requestLocationUpdates(mLocationRequest,
                    mLocationCallback, null)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            displayLog("addOnCompleteListener 1 ");
                            try {
                                HashMap<String, String> loans = new HashMap<>();
                                //loans.put("phonenumber",postDataParams.get("phone"));
                                //loans.put("ualId", model.getUalId());
                                HashMap<String, String> parameters = new HashMap<>();
                                parameters.put("eventName", "Data Collection Service");
                                parameters.put("subtype", "complete");
                                parameters.put("type", "startLocationUpdates");
                                parameters.put("onObject", "app");
                                parameters.put("view", "foregroundService");
                                parameters.put("killswitch", "" + remotekillswitch);
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
                    displayLog("addOnSuccessListener 3");
                    try {
                        HashMap<String, String> loans = new HashMap<>();
                        //loans.put("phonenumber",postDataParams.get("phone"));
                        //loans.put("ualId", model.getUalId());
                        HashMap<String, String> parameters = new HashMap<>();
                        parameters.put("eventName", "Data Collection Service");
                        parameters.put("subtype", "success");
                        parameters.put("type", "startLocationUpdates");
                        parameters.put("onObject", "app");
                        parameters.put("view", "foregroundService");
                        parameters.put("killswitch", "" + remotekillswitch);
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
                    displayLog("addOnFailureListener 2 " + e.getMessage());
                    try {
                        HashMap<String, String> loans = new HashMap<>();
                        //loans.put("phonenumber",postDataParams.get("phone"));
                        //loans.put("ualId", model.getUalId());
                        HashMap<String, String> parameters = new HashMap<>();
                        parameters.put("eventName", "Data Collection Service");
                        parameters.put("subtype", "failure");
                        parameters.put("type", "startLocationUpdates");
                        parameters.put("onObject", "app");
                        parameters.put("view", "foregroundService");
                        parameters.put("killswitch", "" + remotekillswitch);
                        parameters.put("error", e.getMessage());
                        sendEvent(parameters, loans);
                    } catch (Exception e1) {
                        displayLog("error attaching afl to ual " + e1.toString());
                    }
                    sendSMS("location callback failure");
                    startAlert();

                }
            });
        } catch (SecurityException e) {
            displayLog("requestLocationUpdates error " + e.toString());
            //displayToast("Please enable GPS location", true);
            sendSMS("location callback security exception");
        }


    }

    private void createLocationRequest() {

        displayLog("frequency " + remotelocationfrequency);
        try {
            HashMap<String, String> loans = new HashMap<>();
            //loans.put("phonenumber",postDataParams.get("phone"));
            //loans.put("ualId", model.getUalId());
            HashMap<String, String> parameters = new HashMap<>();
            parameters.put("eventName", "Data Collection Service");
            parameters.put("subtype", "create");
            parameters.put("type", "createLocationRequest");
            parameters.put("onObject", "app");
            parameters.put("view", "foregroundService");
            sendEvent(parameters, loans);
        } catch (Exception e1) {
            displayLog("error attaching afl to ual " + e1.toString());
        }
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(remotelocationfrequency);
        mLocationRequest.setFastestInterval(remotelocationfrequency / 2);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    private void createLocationCallback() {
        displayLog("create location callback");

        try {
            HashMap<String, String> loans = new HashMap<>();
            //loans.put("phonenumber",postDataParams.get("phone"));
            //loans.put("ualId", model.getUalId());
            HashMap<String, String> parameters = new HashMap<>();
            parameters.put("eventName", "Data Collection Service");
            parameters.put("subtype", "create");
            parameters.put("type", "createLocationCallback");
            parameters.put("onObject", "app");
            parameters.put("view", "foregroundService");
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
                    sendSMS("with null location");
                    try {
                        HashMap<String, String> loans = new HashMap<>();
                        //loans.put("phonenumber",postDataParams.get("phone"));
                        //loans.put("ualId", model.getUalId());
                        HashMap<String, String> parameters = new HashMap<>();
                        parameters.put("eventName", "Data Collection Service");
                        parameters.put("subtype", "result");
                        parameters.put("type", "onLocationResult");
                        parameters.put("onObject", "null");
                        parameters.put("view", "foregroundService");
                        sendEvent(parameters, loans);
                    } catch (Exception e1) {
                        displayLog("error attaching afl to ual " + e1.toString());
                    }
                    return;
                } else {
                    mCurrentLocation = locationResult.getLastLocation();
                    lat = mCurrentLocation.getLatitude();
                    lng = mCurrentLocation.getLongitude();
                    acc = mCurrentLocation.getAccuracy();

                    displayLog("lat " + lat + " lng " + lng + " acc " + acc);

                    try {
                        HashMap<String, String> loans = new HashMap<>();
                        //loans.put("phonenumber",postDataParams.get("phone"));
                        //loans.put("ualId", model.getUalId());
                        HashMap<String, String> parameters = new HashMap<>();
                        parameters.put("eventName", "Data Collection Service");
                        parameters.put("subtype", "result");
                        parameters.put("type", "onLocationResult");
                        parameters.put("onObject", "app");
                        parameters.put("view", "foregroundService");
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
                        sendSMS("without acc location " + acc);
                    } else {
                        displayLog("lat updatedatabase");
                        sendSMS("with acc location");
                        updateDatabase(lat, lng, acc);
                        //stopSelf();
                    }
                }
            }
        };

    }

    private void buildLocationSettingsRequest() {
        displayLog("buildLocationSettingsRequest");
        try {
            HashMap<String, String> loans = new HashMap<>();
            //loans.put("phonenumber",postDataParams.get("phone"));
            //loans.put("ualId", model.getUalId());
            HashMap<String, String> parameters = new HashMap<>();
            parameters.put("eventName", "Data Collection Service");
            parameters.put("subtype", "create");
            parameters.put("type", "buildLocationSettingsRequest");
            parameters.put("onObject", "app");
            parameters.put("view", "foregroundService");
            sendEvent(parameters, loans);
        } catch (Exception e1) {
            displayLog("error attaching afl to ual " + e1.toString());
        }
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder();
        builder.addLocationRequest(mLocationRequest);
        builder.setNeedBle(true);
        mLocationSettingsRequest = builder.build();

        try {
            //OkVerifyApplication.getMainActivity().checkLocationSettings();
        } catch (Exception e) {
            displayLog("error checking location settings " + e.toString());
        }

    }

    private void sendSMS(String who) {
        /*
        try {
            //String message = "https://hypertrack-996a0.firebaseapp.com/?id="+OkVerifyApplication.getUniqueId();
            //String message = remoteSmsTemplate + OkVerifyApplication.getUniqueId();
            final HashMap<String, String> jsonObject = new HashMap<>();
            jsonObject.put("userId", "GrlaR3LHUP");
            jsonObject.put("sessionToken", "r:3af107bf99e4c6f2a91e6fec046f5fc7");
            jsonObject.put(COLUMN_BRANCH, "hq_okhi");
            jsonObject.put(COLUMN_AFFILIATION, "okhi");
            jsonObject.put("customName", "test");
            //jsonObject.put("ualId", verifyDataItem.getUalId());
            jsonObject.put("phoneNumber", "+254713567907");
            jsonObject.put("phone", "+254713567907");
            jsonObject.put("message", "we have received "+getDeviceModelAndBrand()+" status "+who);
            SendCustomLinkSmsCallBack sendCustomLinkSmsCallBack = new SendCustomLinkSmsCallBack() {
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
            SendCustomLinkSmsTask sendCustomLinkSmsTask = new SendCustomLinkSmsTask(sendCustomLinkSmsCallBack, jsonObject);
            sendCustomLinkSmsTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        } catch (Exception jse) {
            displayLog("jsonexception " + jse.toString());
        }
        */
    }

    private void firebase(String who) {

        final Long timemilliseconds = System.currentTimeMillis();
        final Map<String, Object> user = new HashMap<>();
        user.put("state", who);
        user.put("timestamp", new Timestamp(new Date()));
        user.put("geoPointSource", "clientBackgroundGPS");
        user.put("timemilliseconds", timemilliseconds);
        user.put("device", getDeviceModelAndBrand());
        user.put("model", Build.MODEL);
        user.put("brand", Build.MANUFACTURER);
        user.put("OSVersion", Build.VERSION.SDK_INT);
        user.put("OSName", "Android");
        user.put("appVersionCode", BuildConfig.VERSION_CODE);
        user.put("appVersionName", BuildConfig.VERSION_NAME);
        mFirestore.collection("verifydata").document("data")
                .collection(uniqueId).document("" + timemilliseconds)
                .set(user)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        displayLog("Documentsnapshot successfully written!");

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        displayLog("Error writing document " + e.toString());

                    }
                });

    }


    private void updateDatabase(final Double lat, final Double lng, final Float acc) {

        try {
            stopLocationUpdates();
        } catch (Exception e) {
            displayLog("Error stopping location update " + e.toString());
        }


        HashMap<String, String> loans = new HashMap<>();
        //loans.put(PROP_ACTORNAME, loginname);
        //loans.put(PROP_ACTORPHONENUMBER, loginphonenumber);
        //loans.put("error", "Location settings are not satisfied. Attempting to upgrade location settings");
        HashMap<String, String> parameters = new HashMap<>();

        final Long timemilliseconds = System.currentTimeMillis();
        final Map<String, Object> user = new HashMap<>();
        user.put("latitude", lat);
        user.put("longitude", lng);
        user.put("gpsAccuracy", acc);
        user.put("timestamp", new Timestamp(new Date()));
        GeoPoint geoPoint = new GeoPoint(lat, lng);
        user.put("geoPoint", geoPoint);
        user.put("geoPointSource", "clientBackgroundGPS");
        user.put("timemilliseconds", timemilliseconds);
        user.put("device", getDeviceModelAndBrand());
        user.put("model", Build.MODEL);
        user.put("brand", Build.MANUFACTURER);
        user.put("OSVersion", Build.VERSION.SDK_INT);
        user.put("OSName", "Android");
        user.put("appVersionCode", BuildConfig.VERSION_CODE);
        user.put("appVersionName", BuildConfig.VERSION_NAME);

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
        parseObject.put("appVersionCode", BuildConfig.VERSION_CODE);
        parseObject.put("appVersionName", BuildConfig.VERSION_NAME);

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
                    user.put("ssid", ssid);
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
                user.put("configuredSSIDs", configuredSSIDList);
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
            boolean isPlugged = false;
            BatteryManager bm = (BatteryManager) getSystemService(BATTERY_SERVICE);
            int batLevel = bm.getIntProperty(BatteryManager.BATTERY_PROPERTY_CAPACITY);
            parameters.put("batteryLevel", "" + batLevel);
            user.put("batteryLevel", batLevel);
            parseObject.put("batteryLevel", batLevel);

            Intent intent = ForegroundService.this.registerReceiver(null, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
            int plugged = intent.getIntExtra(BatteryManager.EXTRA_PLUGGED, -1);
            isPlugged = plugged == BatteryManager.BATTERY_PLUGGED_AC || plugged == BatteryManager.BATTERY_PLUGGED_USB;
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.JELLY_BEAN) {
                isPlugged = isPlugged || plugged == BatteryManager.BATTERY_PLUGGED_WIRELESS;
            }
            parameters.put("isPlugged", "" + isPlugged);
            user.put("isPlugged", isPlugged);
            parseObject.put("isPlugged", isPlugged);

            // Are we charging / charged?
            int status = intent.getIntExtra(BatteryManager.EXTRA_STATUS, -1);
            boolean isCharging = status == BatteryManager.BATTERY_STATUS_CHARGING ||
                    status == BatteryManager.BATTERY_STATUS_FULL;
            parameters.put("isCharging", "" + isCharging);
            user.put("isCharging", isCharging);
            parseObject.put("isCharging", isCharging);

            // How are we charging?
            int chargePlug = intent.getIntExtra(BatteryManager.EXTRA_PLUGGED, -1);
            boolean usbCharge = chargePlug == BatteryManager.BATTERY_PLUGGED_USB;
            parameters.put("usbCharge", "" + usbCharge);
            user.put("usbCharge", usbCharge);
            parseObject.put("usbCharge", usbCharge);
            boolean acCharge = chargePlug == BatteryManager.BATTERY_PLUGGED_AC;
            parameters.put("acCharge", "" + acCharge);
            user.put("acCharge", acCharge);
            parseObject.put("acCharge", acCharge);

        } catch (Exception e) {
            displayLog(" error getting battery status " + e.toString());
        }

        //String uniqueId = OkVerifyApplication.getUniqueId();
        user.put("uniqueId", uniqueId);
        parseObject.put("uniqueId", uniqueId);
        parameters.put("uniqueId", uniqueId);

        try {

            parameters.put("uniqueId", uniqueId);
            parameters.put("cookieToken", uniqueId);
            parameters.put("eventName", "Data collection Service");
            parameters.put("subtype", "saveBackgroundData");
            parameters.put("type", "saveData");
            parameters.put("onObject", "backgroundService");
            parameters.put("view", "foregroundService");
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

                GeoHash hash = GeoHash.fromLocation(location2, 12);
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
            parameters.put("appVersionCode", "" + BuildConfig.VERSION_CODE);
            parameters.put("appVersionName", "" + BuildConfig.VERSION_NAME);
            sendEvent(parameters, loans);
        } catch (Exception e1) {
            displayLog("error attaching afl to ual " + e1.toString());
        }


        query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    dataProvider.deleteAllAddresseses();
                    displayLog("gotten results ");
                    List<DocumentSnapshot> documentSnapshotList = task.getResult().getDocuments();
                    if (documentSnapshotList.size() > 0) {
                        displayLog("result size " + documentSnapshotList.size());
                        addresses = new ArrayList<>();
                        for (DocumentSnapshot address : documentSnapshotList) {
                            VerifyDataItem verifyDataItem = address.toObject(VerifyDataItem.class);
                            displayLog("unique id " + uniqueId + " ualid " + verifyDataItem.getUalId() + "lat " + verifyDataItem.getLatitude() + " lng " + verifyDataItem.getLongitude());
                            Float distance = getDistance(lat, lng, verifyDataItem.getLatitude(), verifyDataItem.getLongitude());
                            Map<String, Object> nestedData = new HashMap<>();
                            nestedData.put("ualId", verifyDataItem.getUalId());
                            nestedData.put("latitude", verifyDataItem.getLatitude());
                            nestedData.put("longitude", verifyDataItem.getLongitude());
                            if (distance < 100.0) {
                                nestedData.put("verified", true);
                            } else {
                                nestedData.put("verified", false);
                            }

                            nestedData.put("distance", distance);
                            HashMap<String, String> parameters = getTitleText(verifyDataItem);
                            nestedData.put("title", parameters.get("header"));
                            nestedData.put("text", parameters.get("text"));

                            ContentValues contentValues = new ContentValues();
                            contentValues.put(COLUMN_CUSTOMERNAME, verifyDataItem.getFirstName());
                            contentValues.put(COLUMN_PHONECUSTOMER, verifyDataItem.getPhone());
                            contentValues.put(COLUMN_STREETNAME, verifyDataItem.getStreetName());
                            contentValues.put(COLUMN_PROPERTYNAME, verifyDataItem.getPropertyName());
                            contentValues.put(COLUMN_DIRECTION, verifyDataItem.getDirections());
                            contentValues.put(COLUMN_LOCATIONNICKNAME, verifyDataItem.getPlaceId());
                            contentValues.put(COLUMN_CLAIMUALID, verifyDataItem.getUalId());
                            contentValues.put(COLUMN_IMAGEURL, verifyDataItem.getUrl());
                            contentValues.put(COLUMN_LOCATIONNAME, verifyDataItem.getTitle());
                            contentValues.put(COLUMN_BRANCH, "okhi");
                            contentValues.put(COLUMN_LAT, verifyDataItem.getLatitude());
                            contentValues.put(COLUMN_LNG, verifyDataItem.getLongitude());

                            Long i = dataProvider.insertAddressList(contentValues);
                            user.put("phone", verifyDataItem.getPhone());
                            parseObject.put("phone", verifyDataItem.getPhone());
                            addresses.add(nestedData);
                        }
                        user.put("addresses", addresses);
                        parseObject.put("addresses", addresses);
                        saveData(parseObject, user, timemilliseconds);
                    } else {
                        saveData(parseObject, user, timemilliseconds);
                    }
                } else {
                    displayLog("couldn't get addresses list ");
                    saveData(parseObject, user, timemilliseconds);
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                displayLog("address query failed " + e.toString());
                saveData(parseObject, user, timemilliseconds);
            }
        });

    }

    private void saveData(ParseObject parseObject, Map<String, Object> user, Long timemilliseconds) {
        displayLog("about to saveData");


        mFirestore.collection("verifydata").document("data")
                .collection(uniqueId).document("" + timemilliseconds)
                .set(user)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        displayLog("Documentsnapshot successfully written!");
                        try {
                            HashMap<String, String> loans = new HashMap<>();
                            //loans.put("phonenumber",postDataParams.get("phone"));
                            //loans.put("ualId", model.getUalId());
                            HashMap<String, String> parameters = new HashMap<>();
                            parameters.put("eventName", "Data Collection Service");
                            parameters.put("subtype", "saveData");
                            parameters.put("type", "firestore");
                            parameters.put("onObject", "success");
                            parameters.put("view", "foregroundService");
                            sendEvent(parameters, loans);
                        } catch (Exception e1) {
                            displayLog("error attaching afl to ual " + e1.toString());
                        }
                        /*
                        if (notificationManager != null) {
                            notificationManager.cancelAll();
                        }
                        */
                        firestore = true;
                        stopSelf(firestore, parsedb);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        displayLog("Error writing document " + e.toString());
                        try {
                            HashMap<String, String> loans = new HashMap<>();
                            //loans.put("phonenumber",postDataParams.get("phone"));
                            //loans.put("ualId", model.getUalId());
                            HashMap<String, String> parameters = new HashMap<>();
                            parameters.put("eventName", "Data Collection Service");
                            parameters.put("subtype", "saveData");
                            parameters.put("type", "firestore");
                            parameters.put("onObject", "failed");
                            parameters.put("view", "foregroundService");
                            sendEvent(parameters, loans);
                        } catch (Exception e1) {
                            displayLog("error attaching afl to ual " + e1.toString());
                        }
                        /*
                        if (notificationManager != null) {
                            notificationManager.cancelAll();
                        }
                        */
                        firestore = true;
                        stopSelf(firestore, parsedb);
                    }
                });


        displayLog("parse object save");
        parseObject.saveEventually(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e == null) {
                    displayLog("save parseobject success ");
                    try {
                        HashMap<String, String> loans = new HashMap<>();
                        //loans.put("phonenumber",postDataParams.get("phone"));
                        //loans.put("ualId", model.getUalId());
                        HashMap<String, String> parameters = new HashMap<>();
                        parameters.put("eventName", "Data Collection Service");
                        parameters.put("subtype", "saveData");
                        parameters.put("type", "parse");
                        parameters.put("onObject", "success");
                        parameters.put("view", "foregroundService");
                        sendEvent(parameters, loans);
                    } catch (Exception e1) {
                        displayLog("error attaching afl to ual " + e1.toString());
                    }
                    /*
                    if (notificationManager != null) {
                        notificationManager.cancelAll();
                    }
                    */
                    parsedb = true;
                    stopSelf(firestore, parsedb);

                } else {
                    try {
                        HashMap<String, String> loans = new HashMap<>();
                        //loans.put("phonenumber",postDataParams.get("phone"));
                        //loans.put("ualId", model.getUalId());
                        HashMap<String, String> parameters = new HashMap<>();
                        parameters.put("eventName", "Data Collection Service");
                        parameters.put("subtype", "saveData");
                        parameters.put("type", "parse");
                        parameters.put("onObject", "failure");
                        parameters.put("view", "foregroundService");
                        parameters.put("error", e.toString());
                        sendEvent(parameters, loans);
                    } catch (Exception e1) {
                        displayLog("error attaching afl to ual " + e1.toString());
                    }
                    displayLog("save parseobject error " + e.toString());
                    /*
                    if (notificationManager != null) {
                        notificationManager.cancelAll();
                    }
                    */
                    parsedb = true;
                    stopSelf(firestore, parsedb);
                }
            }
        });


    }

    /*
    private Boolean addressVerified(VerifyDataItem verifyDataItem) {
        displayLog("geosearch radius " + remotegeosearchradius + " address frequency " + remoteaddressfrequency);
        displayLog("ualid " + verifyDataItem.getUalId() + " lat " + verifyDataItem.getLatitude() + " lng " + verifyDataItem.getLongitude());
        Boolean results = false;
        if (verifyDataItem != null) {
            Double lat = verifyDataItem.getLatitude();
            Double lng = verifyDataItem.getLongitude();
            if (lat != null) {
                CollectionReference collectionReference = mFirestore.collection("verifydata")
                        .document("data")
                        .collection(OkVerifyApplication.getUniqueId());
                GeoFirestore geoFirestore = new GeoFirestore(collectionReference);
                GeoQuery geoQuery = geoFirestore.queryAtLocation(new GeoPoint(lat, lng), remotegeosearchradius);
                ArrayList<Query> queryArrayList = geoQuery.getQueries();
                displayLog("geo query size " + queryArrayList.size());
                if (queryArrayList != null) {
                    if (queryArrayList.size() >= remoteaddressfrequency) {
                        results = true;
                    }
                }
            } else {
                displayLog("verify data lat is null");
            }
        } else {
            displayLog("verify data item is null");
        }
        displayLog("returning " + results);
        return results;
    }
    */

    private void stopSelf(Boolean firestore, Boolean parsedb) {
        displayLog("stopself firestore " + firestore + " parse " + parsedb);

        if (firestore && parsedb) {
            displayLog("if stop self");
            try {
                HashMap<String, String> loans = new HashMap<>();
                //loans.put("phonenumber",postDataParams.get("phone"));
                //loans.put("ualId", model.getUalId());
                HashMap<String, String> parameters = new HashMap<>();
                parameters.put("eventName", "Data Collection Service");
                parameters.put("subtype", "stop");
                parameters.put("type", "stopSelf");
                parameters.put("onObject", "stopped");
                parameters.put("view", "foregroundService");
                parameters.put("firestore", "" + firestore);
                parameters.put("parse", "" + parsedb);
                sendEvent(parameters, loans);
            } catch (Exception e1) {
                displayLog("error attaching afl to ual " + e1.toString());
            }
            stopSelf();
        } else {
            displayLog("else stop self");
            try {
                HashMap<String, String> loans = new HashMap<>();
                //loans.put("phonenumber",postDataParams.get("phone"));
                //loans.put("ualId", model.getUalId());
                HashMap<String, String> parameters = new HashMap<>();
                parameters.put("eventName", "Data Collection Service");
                parameters.put("subtype", "stop");
                parameters.put("type", "stopSelf");
                parameters.put("onObject", "notStopped");
                parameters.put("view", "foregroundService");
                parameters.put("firestore", "" + firestore);
                parameters.put("parse", "" + parsedb);
                sendEvent(parameters, loans);
            } catch (Exception e1) {
                displayLog("error attaching afl to ual " + e1.toString());
            }
        }

        /*
        if (remoteautostop) {
            if (firestore && parsedb) {
                displayLog("if stop self");
                try {
                    HashMap<String, String> loans = new HashMap<>();
                    //loans.put("phonenumber",postDataParams.get("phone"));
                    //loans.put("ualId", model.getUalId());
                    HashMap<String, String> parameters = new HashMap<>();
                    parameters.put("eventName", "Data Collection Service");
                    parameters.put("subtype", "stop");
                    parameters.put("type", "stopSelf");
                    parameters.put("onObject", "stopped");
                    parameters.put("view", "foregroundService");
                    parameters.put("firestore", "" + firestore);
                    parameters.put("parse", "" + parsedb);
                    sendEvent(parameters, loans);
                } catch (Exception e1) {
                    displayLog("error attaching afl to ual " + e1.toString());
                }
                stopSelf();
            } else {
                displayLog("else stop self");
                try {
                    HashMap<String, String> loans = new HashMap<>();
                    //loans.put("phonenumber",postDataParams.get("phone"));
                    //loans.put("ualId", model.getUalId());
                    HashMap<String, String> parameters = new HashMap<>();
                    parameters.put("eventName", "Data Collection Service");
                    parameters.put("subtype", "stop");
                    parameters.put("type", "stopSelf");
                    parameters.put("onObject", "notStopped");
                    parameters.put("view", "foregroundService");
                    parameters.put("firestore", "" + firestore);
                    parameters.put("parse", "" + parsedb);
                    sendEvent(parameters, loans);
                } catch (Exception e1) {
                    displayLog("error attaching afl to ual " + e1.toString());
                }
            }
        } else {

        }
        */


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

    private String getDeviceModelAndBrand() {

        String manufacturer = Build.MANUFACTURER;
        String model = Build.MODEL;
        if (model.contains(manufacturer)) {
            return model;
        } else {
            return manufacturer + " " + model;
        }

    }

    private HashMap<String, String> getTitleText(VerifyDataItem model) {

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
            OkAnalytics okAnalytics = new OkAnalytics(ForegroundService.this);
            okAnalytics.sendToAnalytics(parameters, loans);
        } catch (Exception e) {
            displayLog("error sending photoexpanded analytics event " + e.toString());
        }
    }

    private void displayToast(String msg, boolean show) {
        if (show) {
            try {
                Toast toast = Toast.makeText(ForegroundService.this,
                        msg, Toast.LENGTH_LONG);
                toast.setGravity(Gravity.CENTER_HORIZONTAL, 0, 0);
                toast.show();
            } catch (Exception e) {
                displayLog("Enable data toast error " + e.toString());
            }
        }
    }
}
