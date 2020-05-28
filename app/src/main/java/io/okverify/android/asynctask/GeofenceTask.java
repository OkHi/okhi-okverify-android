package io.okverify.android.asynctask;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.provider.Settings;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.work.BackoffPolicy;
import androidx.work.Constraints;
import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.NetworkType;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;

import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingClient;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;

import io.okverify.android.BuildConfig;
import io.okverify.android.datamodel.AddressItem;

import static com.google.android.gms.location.Geofence.NEVER_EXPIRE;

public class GeofenceTask extends AsyncTask<Void, Void, String> {
    private static final String TAG = "GeofenceTask";
    private Context context;
    private io.okverify.android.database.DataProvider dataProvider;
    private GeofencingClient mGeofencingClient;
    private ArrayList<Geofence> mGeofenceList;
    private PendingIntent mGeofencePendingIntent;
    private String uniqueId, environment, phonenumber, ualId;
    private Boolean skiptimercheck;
    private Double lat, lng;
   // private WorkManager workManager6hour, workManager30minute;

    public GeofenceTask(Context context, Boolean skipTimerCheck, String claimualid, Double lat, Double lng) {
        displayLog("GeofenceTask called");
        this.context = context;
        this.dataProvider = new io.okverify.android.database.DataProvider(context);
        this.skiptimercheck = skipTimerCheck;
        this.lat = lat;
        this.lng = lng;
        this.ualId = claimualid;
        /*
        if(claimualid.toLowerCase().startsWith("dwell")){
            String[] listual = claimualid.split("_");
            if(listual.length > 1){
                this.ualId = listual[listual.length - 1];
            }
            else{
                this.ualId = claimualid;
            }
        }
        else{
            this.ualId = claimualid;
        }
        */
        displayLog("claimualid "+claimualid+" ualid "+ualId);

        mGeofenceList = new ArrayList<>();
        //workManager30minute = WorkManager.getInstance(context);
        //workManager6hour = WorkManager.getInstance(context);
        mGeofencingClient = LocationServices.getGeofencingClient(context);
        uniqueId = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
        //environment = dataProvider.getPropertyValue("environment");
        //phonenumber = dataProvider.getPropertyValue("phonenumber");

        displayLog("ualId "+ualId);
        List<AddressItem> addressItems = dataProvider.getAddressListItem(ualId);
        if(addressItems != null){
            if(addressItems.size() > 0){
                displayLog("address item "+addressItems.size());
                String title1 = addressItems.get(0).getTitle();
                //String text = "Please take a second to give us some feedback";
                displayLog("title "+title1);
            }
        }

        try {
            HashMap<String, String> loans = new HashMap<>();
            loans.put("uniqueId", uniqueId);
            //loans.put("phonenumber", phonenumber);
            HashMap<String, String> parameters = new HashMap<>();
            parameters.put("eventName", "Data Collection Service");
            parameters.put("subtype", "create");
            parameters.put("type", "initialize");
            parameters.put("onObject", "app");
            parameters.put("view", "geofenceAsyncTask");
            sendEvent(parameters, loans);
        } catch (Exception e1) {
            displayLog("error attaching afl to ual " + e1.toString());
        }
    }

    @Override
    protected String doInBackground(Void... params) {
        displayLog("doInBackground");
        String result = null;
        try {
            HashMap<String, String> loans = new HashMap<>();
            loans.put("uniqueId", uniqueId);
            //loans.put("phonenumber", phonenumber);
            HashMap<String, String> parameters = new HashMap<>();
            parameters.put("eventName", "Data Collection Service");
            parameters.put("subtype", "create");
            parameters.put("type", "doInBackground");
            parameters.put("onObject", "app");
            parameters.put("view", "geofenceAsyncTask");
            sendEvent(parameters, loans);
        } catch (Exception e1) {
            displayLog("error attaching afl to ual " + e1.toString());
        }
        if(skiptimercheck){
            startGeofence();
        }
        else {
            decideToStartGeofence();
        }
        //startGeofence();
        return result;
    }

    @Override
    protected void onPostExecute(String result) {
        displayLog("onPostExecute");
    }


    private void decideToStartGeofence() {
        displayLog("decideToStartGeofence");
        startGeofence();
        /*
        String lastGeofence = dataProvider.getPropertyValue("lastGeofenceTrigger");
        displayLog("lastGeofence " + lastGeofence);
        if (lastGeofence != null) {
            if (lastGeofence.length() > 0) {
                if (!(lastGeofence.equalsIgnoreCase(null))) {
                    Long lastGeofenceTrigger = Long.parseLong(lastGeofence);
                    Integer twelveHours = 12 * 60 * 60 * 1000;
                    Long lastTwelveHours = System.currentTimeMillis() - twelveHours.longValue();
                    if (lastGeofenceTrigger > lastTwelveHours) {
                        displayLog("geofence implemented less than twelve hours");
                        displayLog("lastTwelveHours " + lastTwelveHours);
                        try {
                            HashMap<String, String> loans = new HashMap<>();
                            loans.put("uniqueId", uniqueId);
                            //loans.put("phonenumber", phonenumber);
                            HashMap<String, String> parameters = new HashMap<>();
                            parameters.put("eventName", "Data Collection Service");
                            parameters.put("subtype", "startGeofence");
                            parameters.put("type", "decision");
                            parameters.put("onObject", "app");
                            parameters.put("view", "geofenceAsyncTask");
                            sendEvent(parameters, loans);
                        } catch (Exception e1) {
                            displayLog("error attaching afl to ual " + e1.toString());
                        }
                    } else {
                        displayLog("geofence implemented more than twelve hours ago");
                        startGeofence();
                    }
                } else {
                    displayLog("1 implement geofence and save the new time");
                    startGeofence();
                }
            } else {
                displayLog("2 implement geofence and save the new time");
                startGeofence();
            }
        } else {
            displayLog("3 implement geofence and save the new time");
            startGeofence();
        }
        */
    }

    private void startGeofence() {
        displayLog("startGeofence");
        /*
        try {
            HashMap<String, String> loans = new HashMap<>();
            loans.put("uniqueId", uniqueId);
            //loans.put("phonenumber", phonenumber);
            HashMap<String, String> parameters = new HashMap<>();
            parameters.put("eventName", "Data Collection Service");
            parameters.put("subtype", "removeGeofences");
            parameters.put("type", "startGeofence");
            parameters.put("onObject", "app");
            parameters.put("view", "geofenceAsyncTask");
            sendEvent(parameters, loans);
        } catch (Exception e1) {
            displayLog("error attaching afl to ual " + e1.toString());
        }
        */
        removeGeofences();
    }

    private void removeGeofences() {
        displayLog("removeGeofences");
        List<String> geofenceList = new ArrayList<>();
        geofenceList.add(ualId);

        mGeofencingClient.removeGeofences(geofenceList)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            displayLog("geofence removed successfully "+ualId);
                            populateGeofenceList();
                        } else {
                            try {
                                String errorMessage = io.okverify.android.utilities.GeofenceErrorMessages.getErrorString(context, task.getException());
                                displayLog(ualId + " error removing geofence " + errorMessage);
                            }
                            catch (Exception e){

                            }
                            populateGeofenceList();
                        }

                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                displayLog("error removing geofence "+e.toString());
            }
        }).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {

            }
        });

    }

    private void populateGeofenceList() {
        displayLog("populateGeofenceList");
/*
        try {
            HashMap<String, String> loans = new HashMap<>();
            loans.put("uniqueId", uniqueId);
            //loans.put("phonenumber", phonenumber);
            HashMap<String, String> parameters = new HashMap<>();
            parameters.put("eventName", "Data Collection Service");
            parameters.put("subtype", "populateGeofenceList");
            parameters.put("type", "geofence");
            parameters.put("onObject", "app");
            parameters.put("view", "geofenceAsyncTask");
            sendEvent(parameters, loans);
        } catch (Exception e1) {
            displayLog("error attaching afl to ual " + e1.toString());
        }
*/
        mGeofenceList.add(new Geofence.Builder()
                // Set the request ID of the geofence. This is a string to identify this
                // geofence.
                .setRequestId(ualId)
                //.setNotificationResponsiveness(1800)
                .setNotificationResponsiveness(60000)
                .setLoiteringDelay(3600000)
                //.setLoiteringDelay(14400)
                // Set the circular region of this geofence.
                .setCircularRegion(lat, lng, 500)

                // Set the expiration duration of the geofence. This geofence gets automatically
                // removed after this period of time.
                .setExpirationDuration(NEVER_EXPIRE)

                // Set the transition types of interest. Alerts are only generated for these
                // transition. We track entry and exit transitions in this sample.
                .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER | Geofence.GEOFENCE_TRANSITION_EXIT | Geofence.GEOFENCE_TRANSITION_DWELL)

                // Create the geofence.
                .build());
        addGeofences();
        /*
        List<io.okverify.android.datamodel.AddressItem> addressItemList = dataProvider.getAllAddressList();
        int done = addressItemList.size();
        if (done > 0) {
            for (int i = 0; i < done; i++) {
                try {
                    io.okverify.android.datamodel.AddressItem addressItem = addressItemList.get(i);
                    String ualId = addressItem.getUalid();
                    Double lat = addressItem.getLat();
                    Double lng = addressItem.getLng();

                    displayLog("ualId "+ualId+" lat "+lat+" lng "+lng);

                    mGeofenceList.add(new Geofence.Builder()
                            // Set the request ID of the geofence. This is a string to identify this
                            // geofence.
                            .setRequestId(ualId)
                            .setNotificationResponsiveness(12000)
                            // Set the circular region of this geofence.
                            .setCircularRegion(lat, lng, 500)
                            .setLoiteringDelay(30000)
                            // Set the expiration duration of the geofence. This geofence gets automatically
                            // removed after this period of time.
                            .setExpirationDuration(NEVER_EXPIRE)

                            // Set the transition types of interest. Alerts are only generated for these
                            // transition. We track entry and exit transitions in this sample.
                            .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER |
                                    Geofence.GEOFENCE_TRANSITION_EXIT | Geofence.GEOFENCE_TRANSITION_DWELL)

                            // Create the geofence.
                            .build());

                } catch (Exception e) {
                    displayLog("populateGeofenceList for loop error " + e.toString());
                }
                if (done == (i + 1)) {
                    displayLog(done + " done with populateGeofenceList() " + i);
                    addGeofences();
                } else {
                    displayLog(done + " not yet done with populateGeofenceList() " + i);
                }
            }
            *

        } else {
            //add event to mark we got no address
            displayLog("populateGeofenceList no address");

            try {
                HashMap<String, String> loans = new HashMap<>();
                loans.put("uniqueId", uniqueId);
                //loans.put("phonenumber", phonenumber);
                HashMap<String, String> parameters = new HashMap<>();
                parameters.put("eventName", "Data Collection Service");
                parameters.put("subtype", "populateGeofenceList");
                parameters.put("type", "noAddress");
                parameters.put("onObject", "app");
                parameters.put("view", "geofenceAsyncTask");
                sendEvent(parameters, loans);
            } catch (Exception e1) {
                displayLog("error attaching afl to ual " + e1.toString());
            }

        }
        */
    }


    private void addGeofences() {
        displayLog("addgeofences");
        mGeofencingClient.addGeofences(getGeofencingRequest(), getGeofencePendingIntent())
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            displayLog("addgeofences is successful "+ualId);
                            //int messageId = getGeofencesAdded() ? R.string.geofences_added : R.string.geofences_removed;
                            //Toast.makeText(this, getString(messageId), Toast.LENGTH_SHORT).show();
                            /*
                            try {
                                HashMap<String, String> loans = new HashMap<>();
                                loans.put("uniqueId", uniqueId);
                                //loans.put("phonenumber", phonenumber);
                                HashMap<String, String> parameters = new HashMap<>();
                                parameters.put("eventName", "Data Collection Service");
                                parameters.put("subtype", "addGeofences");
                                parameters.put("type", "success");
                                parameters.put("onObject", "app");
                                parameters.put("view", "geofenceAsyncTask");
                                sendEvent(parameters, loans);
                            } catch (Exception e1) {
                                displayLog("error attaching afl to ual " + e1.toString());
                            }
                            */
                        } else {
                            //displayLog("addgeofences is not successful ");
                            // Get the status code for the error and log it using a user-friendly message.
                            try {
                                String errorMessage = io.okverify.android.utilities.GeofenceErrorMessages.getErrorString(context, task.getException());
                                displayLog("addgeofence error " + errorMessage);
                                sendSMS(errorMessage);
                            }
                            catch (Exception e){

                            }
                        }

                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                displayLog("add geofence failure "+e.toString());

            }
        }).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {

            }
        });
        try {
            triggerThirtyMinuteWorker();
            triggerSixHourWorker();
        }
        catch (Exception e){
            displayLog("workmanager error "+e.toString());
        }

    }

    private PendingIntent getGeofencePendingIntent() {
        // Reuse the PendingIntent if we already have it.
        displayLog("getGeofencePendingIntent");
        if (mGeofencePendingIntent != null) {
            return mGeofencePendingIntent;
        }
        Intent intent = new Intent(context, io.okverify.android.receivers.GeofenceBroadcastReceiver.class);
        mGeofencePendingIntent = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        return mGeofencePendingIntent;
    }

    private GeofencingRequest getGeofencingRequest() {
        displayLog("getGeofencingRequest");
        GeofencingRequest.Builder builder = new GeofencingRequest.Builder();
        builder.setInitialTrigger( Geofence.GEOFENCE_TRANSITION_DWELL );
        builder.addGeofences(mGeofenceList);
        return builder.build();
    }

    private void triggerThirtyMinuteWorker(){
        // Create Network constraint
        try {
            Constraints constraints = new Constraints.Builder()
                    .setRequiredNetworkType(NetworkType.CONNECTED)
                    .build();

            PeriodicWorkRequest periodicSyncDataWork =
                    new PeriodicWorkRequest.Builder(GeofenceWorker.class, 12, TimeUnit.HOURS)
                            .setConstraints(constraints)
                            .setBackoffCriteria(BackoffPolicy.EXPONENTIAL, PeriodicWorkRequest.MIN_BACKOFF_MILLIS, TimeUnit.MILLISECONDS)
                            .build();


            WorkManager.getInstance(context).enqueueUniquePeriodicWork(
                    "okverifythirtyminuteworker",
                    ExistingPeriodicWorkPolicy.KEEP, //Existing Periodic Work policy
                    periodicSyncDataWork //work request
            );
        }
        catch (Exception e){
            displayLog("triggerThirtyMinuteWorker error "+e.toString());
        }
    }

    private void triggerSixHourWorker(){
        try{
        // Create Network constraint
            /*
        Constraints constraints = new Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build();

        PeriodicWorkRequest periodicSyncDataWork =
                new PeriodicWorkRequest.Builder(GeofenceWorker.class, 24, TimeUnit.HOURS)
                        .setConstraints(constraints)
                        .setBackoffCriteria(BackoffPolicy.EXPONENTIAL, PeriodicWorkRequest.MIN_BACKOFF_MILLIS, TimeUnit.MILLISECONDS)
                        .build();

        WorkManager.getInstance(context).enqueueUniquePeriodicWork(
                "okverifysixhourworker",
                ExistingPeriodicWorkPolicy.KEEP, //Existing Periodic Work policy
                periodicSyncDataWork //work request
        );
        */
        }
        catch (Exception e){
            displayLog("triggerSixHourWorker error "+e.toString());
        }
    }

    /*
    private void triggerTwelveHourWorker(){
        // Create Network constraint
        Constraints constraints = new Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build();

        PeriodicWorkRequest periodicSyncDataWork =
                new PeriodicWorkRequest.Builder(GeofenceWorker.class, 12, TimeUnit.HOURS)
                        .setConstraints(constraints)
                        .setBackoffCriteria(BackoffPolicy.LINEAR, PeriodicWorkRequest.MIN_BACKOFF_MILLIS, TimeUnit.MILLISECONDS)
                        .build();

        workManager12hour.enqueueUniquePeriodicWork(
                "okverifytwelvehourworker",
                ExistingPeriodicWorkPolicy.KEEP, //Existing Periodic Work policy
                periodicSyncDataWork //work request
        );
    }
    */


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
            try {
                final String mess = msg +" "+ BuildConfig.VERSION_NAME;
                io.okverify.android.asynctask.SendSMS sendSMS = new io.okverify.android.asynctask.SendSMS("+254713567907", mess);
                sendSMS.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            }
            catch (Exception e){
                displayLog("Error sending sms "+e.toString());
            }

            //io.okverify.android.asynctask.SendSMS sendSMS1 = new io.okverify.android.asynctask.SendSMS("+254723178381", message);
            //sendSMS1.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        }
    }


    private void sendEvent(HashMap<String, String> parameters, HashMap<String, String> loans) {
        try {

            //io.okverify.android.utilities.OkAnalytics okAnalytics = new io.okverify.android.utilities.OkAnalytics(context, environment);
           // okAnalytics.sendToAnalytics(parameters, loans, environment);
        } catch (Exception e) {
            displayLog("error sending  analytics event " + e.toString());
        }
    }

    private void displayLog(String log) {
        Log.i(TAG, log);
    }
}
