package io.okverify.android.asynctask;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.provider.Settings;

import androidx.annotation.NonNull;

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

import static com.google.android.gms.location.Geofence.NEVER_EXPIRE;

public class GeofenceTaskCopy extends AsyncTask<Void, Void, String> {
    private static final String TAG = "GeofenceTask";
    private Context context;
    private io.okverify.android.database.DataProvider dataProvider;
    private GeofencingClient mGeofencingClient;
    private ArrayList<Geofence> mGeofenceList;
    private PendingIntent mGeofencePendingIntent;
    private String uniqueId, environment, phonenumber;
    private Boolean skiptimercheck;

    public GeofenceTaskCopy(Context context, Boolean skipTimerCheck) {
        displayLog("GeofenceTask called");
        this.context = context;
        this.dataProvider = new io.okverify.android.database.DataProvider(context);
        this.skiptimercheck = skipTimerCheck;
        mGeofenceList = new ArrayList<>();
        mGeofencingClient = LocationServices.getGeofencingClient(context);
        uniqueId = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
        environment = dataProvider.getPropertyValue("environment");
        phonenumber = dataProvider.getPropertyValue("phonenumber");

        try {
            HashMap<String, String> loans = new HashMap<>();
            loans.put("uniqueId", uniqueId);
            loans.put("phonenumber", phonenumber);
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
            loans.put("phonenumber", phonenumber);
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
                            loans.put("phonenumber", phonenumber);
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
        try {
            HashMap<String, String> loans = new HashMap<>();
            loans.put("uniqueId", uniqueId);
            loans.put("phonenumber", phonenumber);
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
        removeGeofences();
    }

    private void removeGeofences() {
        displayLog("removeGeofences");

        mGeofencingClient.removeGeofences(getGeofencePendingIntent())
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            //int messageId = getGeofencesAdded() ? R.string.geofences_added : R.string.geofences_removed;
                            //Toast.makeText(this, getString(messageId), Toast.LENGTH_SHORT).show();
                            try {
                                HashMap<String, String> loans = new HashMap<>();
                                loans.put("uniqueId", uniqueId);
                                loans.put("phonenumber", phonenumber);
                                HashMap<String, String> parameters = new HashMap<>();
                                parameters.put("eventName", "Data Collection Service");
                                parameters.put("subtype", "removeGeofences");
                                parameters.put("type", "success");
                                parameters.put("onObject", "app");
                                parameters.put("view", "geofenceAsyncTask");
                                sendEvent(parameters, loans);
                            } catch (Exception e1) {
                                displayLog("error attaching afl to ual " + e1.toString());
                            }
                            populateGeofenceList();
                        } else {
                            //Get the status code for the error and log it using a user-friendly message.
                            String errorMessage = io.okverify.android.utilities.GeofenceErrorMessages.getErrorString(context, task.getException());
                            //Log.w(TAG, errorMessage);
                            try {
                                HashMap<String, String> loans = new HashMap<>();
                                loans.put("uniqueId", uniqueId);
                                loans.put("phonenumber", phonenumber);
                                loans.put("error", "" + errorMessage);
                                HashMap<String, String> parameters = new HashMap<>();
                                parameters.put("eventName", "Data Collection Service");
                                parameters.put("subtype", "removeGeofences");
                                parameters.put("type", "failure");
                                parameters.put("onObject", "app");
                                parameters.put("view", "geofenceAsyncTask");
                                parameters.put("error", "" + errorMessage);
                                sendEvent(parameters, loans);
                            } catch (Exception e1) {
                                displayLog("error attaching afl to ual " + e1.toString());
                            }
                            populateGeofenceList();
                        }

                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        }).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {

            }
        });
    }

    private void populateGeofenceList() {
        displayLog("populateGeofenceList");

        try {
            HashMap<String, String> loans = new HashMap<>();
            loans.put("uniqueId", uniqueId);
            loans.put("phonenumber", phonenumber);
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

                            // Set the expiration duration of the geofence. This geofence gets automatically
                            // removed after this period of time.
                            .setExpirationDuration(NEVER_EXPIRE)

                            // Set the transition types of interest. Alerts are only generated for these
                            // transition. We track entry and exit transitions in this sample.
                            .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER |
                                    Geofence.GEOFENCE_TRANSITION_EXIT)

                            // Create the geofence.
                            .build());
                    /*
                    try {
                        HashMap<String, String> loans = new HashMap<>();
                        loans.put("uniqueId", uniqueId);
                        loans.put("phonenumber", phonenumber);
                        HashMap<String, String> parameters = new HashMap<>();
                        parameters.put("eventName", "Data Collection Service");
                        parameters.put("subtype", "populateGeofenceList");
                        parameters.put("type", "geofence");
                        parameters.put("onObject", "app");
                        parameters.put("view", "geofenceAsyncTask");
                        parameters.put("ualId", addressItem.getUalid());
                        sendEvent(parameters, loans);
                    } catch (Exception e1) {
                        displayLog("error attaching afl to ual " + e1.toString());
                    }
                    */
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

        } else {
            //add event to mark we got no address
            displayLog("populateGeofenceList no address");
            try {
                HashMap<String, String> loans = new HashMap<>();
                loans.put("uniqueId", uniqueId);
                loans.put("phonenumber", phonenumber);
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
    }


    private void addGeofences() {
        displayLog("addgeofences");
        mGeofencingClient.addGeofences(getGeofencingRequest(), getGeofencePendingIntent())
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            displayLog("addgeofences is successful");
                            //int messageId = getGeofencesAdded() ? R.string.geofences_added : R.string.geofences_removed;
                            //Toast.makeText(this, getString(messageId), Toast.LENGTH_SHORT).show();
                            try {
                                HashMap<String, String> loans = new HashMap<>();
                                loans.put("uniqueId", uniqueId);
                                loans.put("phonenumber", phonenumber);
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
                        } else {
                            displayLog("addgeofences is not successful");
                            // Get the status code for the error and log it using a user-friendly message.
                            String errorMessage = io.okverify.android.utilities.GeofenceErrorMessages.getErrorString(context, task.getException());
                            //Log.w(TAG, errorMessage);
                            try {
                                HashMap<String, String> loans = new HashMap<>();
                                loans.put("uniqueId", uniqueId);
                                loans.put("phonenumber", phonenumber);
                                loans.put("error", "" + errorMessage);
                                HashMap<String, String> parameters = new HashMap<>();
                                parameters.put("eventName", "Data Collection Service");
                                parameters.put("subtype", "addGeofences");
                                parameters.put("type", "failure");
                                parameters.put("onObject", "app");
                                parameters.put("view", "geofenceAsyncTask");
                                parameters.put("error", "" + errorMessage);
                                sendEvent(parameters, loans);
                            } catch (Exception e1) {
                                displayLog("error attaching afl to ual " + e1.toString());
                            }
                        }

                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        }).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {

            }
        });

    }

    private PendingIntent getGeofencePendingIntent() {
        // Reuse the PendingIntent if we already have it.
        displayLog("getGeofencePendingIntent");
        try {
            HashMap<String, String> loans = new HashMap<>();
            loans.put("uniqueId", uniqueId);
            loans.put("phonenumber", phonenumber);
            HashMap<String, String> parameters = new HashMap<>();
            parameters.put("eventName", "Data Collection Service");
            parameters.put("subtype", "getGeofencePendingIntent");
            parameters.put("type", "geofence");
            parameters.put("onObject", "app");
            parameters.put("view", "geofenceAsyncTask");
            sendEvent(parameters, loans);
        } catch (Exception e1) {
            displayLog("error attaching afl to ual " + e1.toString());
        }
        if (mGeofencePendingIntent != null) {
            return mGeofencePendingIntent;
        }
        Intent intent = new Intent(context, io.okverify.android.receivers.GeofenceBroadcastReceiver.class);
        mGeofencePendingIntent = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        return mGeofencePendingIntent;
    }

    private GeofencingRequest getGeofencingRequest() {
        displayLog("getGeofencingRequest");
        try {
            HashMap<String, String> loans = new HashMap<>();
            loans.put("uniqueId", uniqueId);
            loans.put("phonenumber", phonenumber);
            HashMap<String, String> parameters = new HashMap<>();
            parameters.put("eventName", "Data Collection Service");
            parameters.put("subtype", "getGeofencingRequest");
            parameters.put("type", "geofence");
            parameters.put("onObject", "app");
            parameters.put("view", "geofenceAsyncTask");
            sendEvent(parameters, loans);
        } catch (Exception e1) {
            displayLog("error attaching afl to ual " + e1.toString());
        }
        GeofencingRequest.Builder builder = new GeofencingRequest.Builder();
        builder.setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER);
        builder.addGeofences(mGeofenceList);
        return builder.build();
    }

    private void sendEvent(HashMap<String, String> parameters, HashMap<String, String> loans) {
        try {

            io.okverify.android.utilities.OkAnalytics okAnalytics = new io.okverify.android.utilities.OkAnalytics(context, environment);
            okAnalytics.sendToAnalytics(parameters, loans, environment);
        } catch (Exception e) {
            displayLog("error sending  analytics event " + e.toString());
        }
    }

    private void displayLog(String log) {
        //Log.i(TAG, log);
    }
}
