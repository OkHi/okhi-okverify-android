package io.okheart.android.activity;

import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import java.util.HashMap;

import io.okheart.android.R;
import io.okheart.android.database.DataProvider;

public class SettingsActivity extends AppCompatActivity {

    private static final String TAG = "SettingsActivity";
    static final int PICK_CONTACT_REQUEST = 1;
    private Button settingsBtn, closeBtn;
    private DataProvider dataProvider;
    private String phonenumber, applicationKey, uniqueId, environment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        dataProvider = new io.okheart.android.database.DataProvider(this);

        settingsBtn = findViewById(R.id.settingsbtn);
        closeBtn = findViewById(R.id.closebtn);
        closeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    HashMap<String, String> loans = new HashMap<>();
                    loans.put("uniqueId", uniqueId);
                    loans.put("applicationKey", applicationKey);
                    loans.put("phonenumber", phonenumber);
                    HashMap<String, String> parameters = new HashMap<>();
                    parameters.put("eventName", "Foreground Notification");
                    parameters.put("subtype", "closed");
                    parameters.put("type", "close");
                    parameters.put("onObject", "settings");
                    parameters.put("view", "settingsActivityView");
                    sendEvent(parameters, loans);
                } catch (Exception e1) {
                    displayLog("error attaching afl to ual " + e1.toString());
                }
                onBackPressed();
            }
        });

        settingsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String channelId = getString(io.okheart.android.R.string.default_notification_channel_id);
                try {
                    HashMap<String, String> loans = new HashMap<>();
                    loans.put("uniqueId", uniqueId);
                    loans.put("applicationKey", applicationKey);
                    loans.put("phonenumber", phonenumber);
                    HashMap<String, String> parameters = new HashMap<>();
                    parameters.put("eventName", "Foreground Notification");
                    parameters.put("subtype", "opened");
                    parameters.put("type", "open");
                    parameters.put("onObject", "settings");
                    parameters.put("view", "settingsActivityView");
                    sendEvent(parameters, loans);
                } catch (Exception e1) {
                    displayLog("error attaching afl to ual " + e1.toString());
                }
                goToNotificationSettings(channelId, SettingsActivity.this);
            }
        });
        try {
            uniqueId = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
            environment = dataProvider.getPropertyValue("environment");
            phonenumber = dataProvider.getPropertyValue("phonenumber");
            applicationKey = dataProvider.getPropertyValue("applicationKey");
            displayLog("uniqueId " + uniqueId + " phonenumber " + phonenumber + " applicationKey " + applicationKey);
        } catch (Exception e) {
            displayLog("error getting dataprovider values " + e.toString());
        }

        try {
            HashMap<String, String> loans = new HashMap<>();
            loans.put("uniqueId", uniqueId);
            loans.put("applicationKey", applicationKey);
            loans.put("phonenumber", phonenumber);
            HashMap<String, String> parameters = new HashMap<>();
            parameters.put("eventName", "Foreground Notification");
            parameters.put("subtype", "loaded");
            parameters.put("type", "load");
            parameters.put("onObject", "settings");
            parameters.put("view", "settingsActivityView");
            //parameters.put("killswitch", "" + remotekillswitch);
            //parameters.put("ualId", model.getUalId());
            sendEvent(parameters, loans);
        } catch (Exception e1) {
            displayLog("error attaching afl to ual " + e1.toString());
        }
    }


    public void goToNotificationSettings(String channel, Context context) {
        Intent intent = new Intent();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            intent.addFlags(android.content.Intent.FLAG_ACTIVITY_NEW_TASK);
            if (channel != null) {
                intent.setAction(Settings.ACTION_CHANNEL_NOTIFICATION_SETTINGS);
                intent.putExtra(Settings.EXTRA_CHANNEL_ID, channel);
            } else {
                intent.setAction(Settings.ACTION_APP_NOTIFICATION_SETTINGS);
            }
            intent.putExtra(Settings.EXTRA_APP_PACKAGE, context.getPackageName());
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            if (channel != null) {
                intent.setAction(Settings.ACTION_CHANNEL_NOTIFICATION_SETTINGS);
                intent.putExtra(Settings.EXTRA_CHANNEL_ID, channel);
            } else {
                intent.setAction(Settings.ACTION_APP_NOTIFICATION_SETTINGS);
            }
            intent.putExtra(Settings.EXTRA_APP_PACKAGE, context.getPackageName());
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N_MR1) {
            intent.setAction(Settings.ACTION_APP_NOTIFICATION_SETTINGS);
            intent.putExtra(Settings.EXTRA_APP_PACKAGE, context.getPackageName());
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            intent.setAction(Settings.ACTION_APP_NOTIFICATION_SETTINGS);
            intent.putExtra("app_package", context.getPackageName());
            intent.putExtra("app_uid", context.getApplicationInfo().uid);
        } else if (Build.VERSION.SDK_INT == Build.VERSION_CODES.KITKAT) {
            intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
            intent.addCategory(Intent.CATEGORY_DEFAULT);
            intent.setData(Uri.parse("package:" + context.getPackageName()));
        }
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        stackBuilder.addNextIntentWithParentStack(intent);
        startActivityForResult(intent, PICK_CONTACT_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // Check which request we're responding to
        if (requestCode == PICK_CONTACT_REQUEST) {
            // Make sure the request was successful
            if (resultCode == RESULT_OK) {

                // The user picked a contact.
                // The Intent's data Uri identifies which contact was selected.

                // Do something with the contact here (bigger example below)
            }
        }
    }

    private void sendEvent(HashMap<String, String> parameters, HashMap<String, String> loans) {
        try {
            io.okheart.android.utilities.OkAnalytics okAnalytics = new io.okheart.android.utilities.OkAnalytics(this, environment);
            okAnalytics.sendToAnalytics(parameters, loans, environment);
        } catch (Exception e) {
            displayLog("error sending photoexpanded analytics event " + e.toString());
        }
    }

    private void displayLog(String log) {
        //Log.i(TAG, log);
    }

}
