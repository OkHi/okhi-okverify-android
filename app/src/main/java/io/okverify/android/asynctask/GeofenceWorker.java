package io.okverify.android.asynctask;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.work.ListenableWorker;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingClient;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import java.util.ArrayList;
import java.util.List;

import io.okverify.android.datamodel.OrderItem;

import static com.google.android.gms.location.Geofence.NEVER_EXPIRE;


public class GeofenceWorker extends Worker {
    private io.okverify.android.database.DataProvider dataProvider;
    private Context context;
    private ArrayList<Geofence> mGeofenceList;
    private PendingIntent mGeofencePendingIntent;
    private GeofencingClient mGeofencingClient;
    private List<String> geofenceList;
    private List<io.okverify.android.datamodel.OrderItem> orderItems;

    public GeofenceWorker(
            @NonNull Context context,
            @NonNull WorkerParameters params) {
        super(context, params);
        this.context = context;
        try {
            this.dataProvider = new io.okverify.android.database.DataProvider(context);
            this.orderItems = dataProvider.getOrderListItem();
            this.mGeofencingClient = LocationServices.getGeofencingClient(context);
        }
        catch (Exception e){
            displayLog("most likely context is null "+e.toString());
        }
        this.geofenceList = new ArrayList<>();

        this.mGeofenceList = new ArrayList<>();
    }

    @Override
    public ListenableWorker.Result doWork() {
        // Do the work here--in this case, upload the images.
        startGeofenceTask();
        // Indicate whether the task finished successfully with the Result
        return ListenableWorker.Result.success();
    }

    public void startGeofenceTask(){

        if(orderItems != null){
            if(orderItems.size() > 0){
                for(OrderItem orderItem : orderItems) {
                    geofenceList.add(orderItem.getClaimualid());
                }
                removegeofence();
            }
        }
    }

    private void removegeofence() {
        try {
            mGeofencingClient.removeGeofences(geofenceList)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                displayLog("geofences removed successfully ");
                                populateGeofenceList();
                            } else {

                                populateGeofenceList();
                            }

                        }
                    }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    displayLog("error removing geofence " + e.toString());
                    //String errorMessage = io.okverify.android.utilities.GeofenceErrorMessages.getErrorString(context, e.toString());
                    displayLog(" error removing geofence " + e.toString());
                }
            });
        }
        catch (Exception e){
            displayLog("mGeofencingClient error "+e.toString());
        }

    }


    private void populateGeofenceList() {
        displayLog("populateGeofenceList");

        for(OrderItem orderItem : orderItems){
            try {
                mGeofenceList.add(new Geofence.Builder()
                        // Set the request ID of the geofence. This is a string to identify this
                        // geofence.
                        .setRequestId(orderItem.getClaimualid())
                        //.setNotificationResponsiveness(12000)
                        // Set the circular region of this geofence.
                        .setCircularRegion(orderItem.getLat(), orderItem.getLng(), 500)
                        .setNotificationResponsiveness(60000)
                        .setLoiteringDelay(3600000)
                        // Set the expiration duration of the geofence. This geofence gets automatically
                        // removed after this period of time.
                        .setExpirationDuration(NEVER_EXPIRE)

                        // Set the transition types of interest. Alerts are only generated for these
                        // transition. We track entry and exit transitions in this sample.
                        .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER |
                                Geofence.GEOFENCE_TRANSITION_EXIT | Geofence.GEOFENCE_TRANSITION_DWELL)

                        // Create the geofence.
                        .build());
            } catch (Exception e){
                displayLog("for loop error "+e.toString());
            }
        }
        addGeofences();

    }


    private void addGeofences() {
        displayLog("addgeofences");
        try {
            mGeofencingClient.addGeofences(getGeofencingRequest(), getGeofencePendingIntent())
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                displayLog("addgeofences is successful ");
                                //int messageId = getGeofencesAdded() ? R.string.geofences_added : R.string.geofences_removed;
                                //Toast.makeText(this, getString(messageId), Toast.LENGTH_SHORT).show();

                            } else {
                                //displayLog("addgeofences is not successful ");
                                // Get the status code for the error and log it using a user-friendly message.
                                try {
                                    String errorMessage = io.okverify.android.utilities.GeofenceErrorMessages.getErrorString(context, task.getException());
                                    //Log.w(TAG, errorMessage);
                                    displayLog("addgeofence error " + errorMessage);
                                } catch (Exception e){

                                }
                            }

                        }
                    }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    displayLog("add geofence failure " + e.toString());

                }
            }).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {

                }
            });
        }
        catch (Exception e){
            displayLog("add geofence error "+e.toString());
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
        builder.setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_DWELL);
        builder.addGeofences(mGeofenceList);
        return builder.build();
    }

    private void displayLog(String log){
        Log.i("GeofenceWorker", log);
    }

}