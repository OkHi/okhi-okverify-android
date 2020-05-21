package io.okverify.android.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.app.RemoteInput;

import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.SaveCallback;

import io.okverify.android.BuildConfig;
import io.okverify.android.R;

/**
 * Receiver for geofence transition changes.
 * <p>
 * Receives geofence transition events from Location Services in the form of an Intent containing
 * the transition type and geofence id(s) that triggered the transition. Creates a JobIntentService
 * that will handle the intent in the background.
 */
public class ReplyBroadcastReceiver extends BroadcastReceiver {
    private static String KEY_GOOD_REPLY = "key_good_reply";
    private static String KEY_BAD_REPLY = "key_bad_reply";
    /**
     * Receives incoming intents.
     *
     * @param context the application context.
     * @param intent  sent by Location Services. This Intent is provided to Location
     *                Services (inside a PendingIntent) when addGeofences() is called.
     */
    @Override
    public void onReceive(Context context, Intent intent) {
        // Enqueues a JobIntentService passing the context and intent as parameters
        try {

            String claimualid = intent.getStringExtra("ualId");
            String tempualid;
            if(claimualid.toLowerCase().startsWith("dwell")){
                String[] listual = claimualid.split("_");
                if(listual.length > 1){
                    tempualid = listual[listual.length - 1];
                }
                else{
                    tempualid = claimualid;
                }
            }
            else{
                tempualid = claimualid;
            }
            String ualId = tempualid;
            //String title = intent.getStringExtra("title");
            //String subtitle = intent.getStringExtra("subtitle");
            displayLog("ualId "+ualId);
            //displayLog("title "+title);
            //displayLog("subtitle "+subtitle);
            getMessageText(intent, context, ualId);
        }
        catch (Exception e){
            displayLog("on receive error "+e.toString());
        }

    }

    private void getMessageText(Intent intent, Context context, String ualId) {
        try {
            displayLog("getMessageText");

            Bundle remoteInput = RemoteInput.getResultsFromIntent(intent);

            if (remoteInput != null) {
                displayLog("remoteInput != null");
                CharSequence charSequence = remoteInput.getCharSequence(KEY_BAD_REPLY);
                if(charSequence != null){
                    displayLog("charSequence bad != null");
                    if(charSequence.length() > 0) {
                        displayLog("charSequence bad (charSequence.length() > 0)");
                        sendsms(ualId+" Bad " + charSequence.toString());
                        savetobackend(false, charSequence.toString(), ualId, context);
                    }
                    else{
                        charSequence = remoteInput.getCharSequence(KEY_GOOD_REPLY);
                        if(charSequence != null){
                            if(charSequence.length() > 0){
                                displayLog("charSequence good (charSequence.length() > 0)");
                                //displayLog("getmessagetext "+ualId+" "+charSequence.toString());
                                //charSequence = remoteInput.getCharSequence(KEY_GOOD_REPLY);
                                sendsms(ualId+" Good " + charSequence.toString());
                                savetobackend(true, charSequence.toString(), ualId, context);
                            }
                            else{
                                sendsms(ualId+" Bad No comment");
                                savetobackend(false, "No comment", ualId, context);
                            }
                        }
                        else {
                            sendsms(ualId+" Bad No comment");
                            savetobackend(false, "No comment", ualId, context);
                        }
                    }
                }
                else{
                    charSequence = remoteInput.getCharSequence(KEY_GOOD_REPLY);
                    if(charSequence != null){
                        displayLog("charSequence good != null");
                        if(charSequence.length() > 0){
                            displayLog("charSequence good (charSequence.length() > 0)");
                            //displayLog("getmessagetext "+ualId+" "+charSequence.toString());
                            //charSequence = remoteInput.getCharSequence(KEY_GOOD_REPLY);
                            sendsms(ualId+" Good " + charSequence.toString());
                            savetobackend(true, charSequence.toString(), ualId, context);
                        }
                        else{
                            sendsms(ualId+" Good No comment");
                            savetobackend(true, "No comment", ualId, context);
                        }
                    }
                    else{
                        sendsms(ualId+" Good No comment");
                        savetobackend(true, "No comment", ualId, context);
                    }
                }
            }
        }
        catch (Exception e){
            displayLog("get message text error "+e.toString());
        }
    }

    private void sendnotification(Context context){
        try {
            String channelId = context.getString(R.string.default_notification_channel_id);

            NotificationCompat.Builder notificationBuilder =
                    new NotificationCompat.Builder(context, channelId)
                            .setSmallIcon(R.mipmap.ic_launcher_round)
                            .setContentText("Feedback received");

            NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
            notificationManager.notify(0, notificationBuilder.build());
        }
        catch (Exception e){
            displayLog("sendnotifiation error "+e.toString());
        }
    }

    private void sendsms(String message){
        try {
            io.okverify.android.asynctask.SendSMS sendSMS =
                    new io.okverify.android.asynctask.SendSMS("+254713567907", message);
            sendSMS.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        } catch (Exception e) {
            displayLog("Error sending sms " + e.toString());
        }
    }

    private void savetobackend(final Boolean isAccurate, final String userfeedback, final String ualId,
                               final Context context){
        displayLog(ualId+" isAccurate "+isAccurate+" feedback "+userfeedback);
        ParseQuery<ParseObject> query = ParseQuery.getQuery("AddressActivity");
        query.orderByDescending("createdAt");
        query.whereEqualTo("okhiId", ualId);
        query.getFirstInBackground(new GetCallback<ParseObject>() {
            @Override
            public void done(final ParseObject object, ParseException e) {
                if(e == null){
                    if(object != null){

                        object.put("isAccurate", isAccurate);
                        object.put("feedback", userfeedback);
                        object.put("clientVersion", BuildConfig.VERSION_NAME);
                        object.put("osVersion", Build.VERSION.SDK_INT);
                        object.saveInBackground(new SaveCallback() {
                            @Override
                            public void done(ParseException e) {
                                if(e == null){
                                    displayLog(ualId+" feedback data saved success "+object.getObjectId());
                                    //sendsms(ualId+" feedback data saved success "+object.getObjectId());
                                    sendnotification(context);
                                }
                                else{
                                    displayLog(ualId+" feedback data save error "+e.toString());
                                    //sendsms(ualId+" feedback data save error "+e.toString());
                                    sendnotification(context);
                                }
                            }
                        });
                    }
                    else{
                        sendsms(ualId+" could not be found in the backend");
                    }
                }
                else{
                    displayLog("parse query error "+e.toString());
                }
            }
        });

    }

    private void displayLog(String log){
        //Log.i("ReplyBroadcast", log);
    }

}
