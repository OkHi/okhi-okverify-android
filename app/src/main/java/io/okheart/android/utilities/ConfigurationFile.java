package io.okheart.android.utilities;


import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.provider.Settings;

import androidx.work.BackoffPolicy;
import androidx.work.Constraints;
import androidx.work.Data;
import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.NetworkType;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;

import com.parse.GetCallback;
import com.parse.GetDataCallback;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class ConfigurationFile {

    private static final String TAG = "ConfigurationFile";
    private static Context context;
    private io.okheart.android.database.DataProvider dataProvider;
    private String uniqueId;

    public ConfigurationFile(final Context context) {
        displayLog("ConfigurationFile called ");
        ConfigurationFile.context = context;
        dataProvider = new io.okheart.android.database.DataProvider(context);
        uniqueId = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);

        try {
            Parse.initialize(new Parse.Configuration.Builder(context)
                    .applicationId(io.okheart.android.utilities.Constants.DEVMASTER_APPLICATION_ID)
                    .clientKey(io.okheart.android.utilities.Constants.DEVMASTER_CLIENT_ID)
                    .server("https://parseapi.back4app.com/").enableLocalDataStore().build());
        } catch (Exception e4) {
            displayLog("parse initialize error " + e4.toString());
        }

        try {
            displayLog("custom start");
            ParseQuery<ParseObject> query = ParseQuery.getQuery("ConfigurationFile");
            query.whereEqualTo("name", "verifyconfigs");
            query.getFirstInBackground(new GetCallback<ParseObject>() {
                @Override
                public void done(ParseObject object, ParseException e) {
                    if (e == null) {
                        ParseFile parseFile = object.getParseFile("file");
                        parseFile.getDataInBackground(new GetDataCallback() {
                            public void done(byte[] data, ParseException e) {
                                if (e == null) {
                                    // data has the bytes for the resume
                                    String bytes = new String(data);
                                    //displayLog(bytes);

                                    try {
                                        String applicationKey = dataProvider.getPropertyValue("applicationKey");
                                        String verify = dataProvider.getPropertyValue("verify");
                                        JSONObject jsonObject = new JSONObject(bytes);
                                        JSONObject verifyObject = jsonObject.optJSONObject("verify");
                                        JSONObject defaultObject = verifyObject.optJSONObject("default");
                                        Integer resume_ping_frequency = defaultObject.optInt("resume_ping_frequency");
                                        Integer ping_frequency = defaultObject.optInt("ping_frequency");
                                        Integer background_frequency = defaultObject.optInt("background_frequency");
                                        String sms_template = defaultObject.optString("sms_template");
                                        Double gps_accuracy = defaultObject.optDouble("gps_accuracy");
                                        Boolean kill_switch = defaultObject.optBoolean("kill_switch");
                                        String noForeground = jsonObject.optString("noForeground");
                                        displayLog("foreground " + noForeground);
                                        displayLog("verify " + verify);
                                        if (verify != null) {
                                            if (verify.length() > 0) {
                                                if (verify.equalsIgnoreCase("true")) {

                                                    try {
                                                        boolean reallyKill = false;
                                                        JSONObject loopkillswitch = jsonObject.optJSONObject("loopkillswitch");
                                                        JSONArray apikeyArray = loopkillswitch.optJSONArray("api_key");
                                                        if (apikeyArray != null) {
                                                            if (apikeyArray.length() > 0) {
                                                                displayLog("length " + apikeyArray.length());
                                                                for (int i = 0; i < apikeyArray.length(); i++) {
                                                                    String tempKey = apikeyArray.getString(i);
                                                                    displayLog("tempKey " + tempKey);
                                                                    displayLog("applicationKey " + applicationKey);
                                                                    if (tempKey.equalsIgnoreCase(applicationKey)) {
                                                                        reallyKill = true;
                                                                    } else {
                                                                        displayLog("nothing is getting killed");
                                                                    }
                                                                }
                                                            } else {
                                                                displayLog("apikey lenght is zero");
                                                            }
                                                        } else {
                                                            displayLog("apikey array is null");
                                                        }
                                                        if (reallyKill) {
                                                            displayLog("something is getting killed");
                                                            try {
                                                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                                                    context.stopService(new Intent(context, io.okheart.android.services.LocationService.class));
                                                                } else {
                                                                    context.stopService(new Intent(context, io.okheart.android.services.LocationService.class));
                                                                }
                                                                stopPeriodicPing();

                                                            } catch (Exception jse) {
                                                                displayLog("jsonexception jse " + jse.toString());
                                                            }
                                                        } else {
                                                            displayLog("nothing is getting killed");
                                                            if (noForeground != null) {
                                                                displayLog("noforeground is not null");
                                                                if (noForeground.length() > 0) {
                                                                    displayLog("noforeground length is not zero");

                                                                    String brand = Build.MANUFACTURER;
                                                                    if (noForeground.toLowerCase().contains(brand.toLowerCase())) {
                                                                        displayLog("we have brand " + noForeground + " " + brand);
                                                                        decideWhatToStart();

                                                                    } else {
                                                                        displayLog("we do not have brand " + noForeground + " " + brand);
                                                                        try {
                                                                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                                                                context.startForegroundService(new Intent(context, io.okheart.android.services.LocationService.class));
                                                                            } else {
                                                                                context.startService(new Intent(context, io.okheart.android.services.LocationService.class));
                                                                            }

                                                                        } catch (Exception jse) {
                                                                            displayLog("jsonexception jse " + jse.toString());
                                                                        }
                                                                    }
                                                                    /////
                                                                } else {
                                                                    displayLog("noforeground length is zero");
                                                                    try {
                                                                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                                                            context.startForegroundService(new Intent(context, io.okheart.android.services.LocationService.class));
                                                                        } else {
                                                                            context.startService(new Intent(context, io.okheart.android.services.LocationService.class));
                                                                        }

                                                                    } catch (Exception jse) {
                                                                        displayLog("jsonexception jse " + jse.toString());
                                                                    }
                                                                }
                                                            } else {
                                                                displayLog("noforeground is null");
                                                                try {
                                                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                                                        context.startForegroundService(new Intent(context, io.okheart.android.services.LocationService.class));
                                                                    } else {
                                                                        context.startService(new Intent(context, io.okheart.android.services.LocationService.class));
                                                                    }

                                                                } catch (Exception jse) {
                                                                    displayLog("jsonexception jse " + jse.toString());
                                                                }
                                                            }

                                                        }

                                                    } catch (Exception e1) {
                                                        displayLog("error getting loopkillswitch " + e1.toString());
                                                    }
                                                    //////

                                                    ///
                                                }
                                            }
                                        }



                                        if (applicationKey != null) {
                                            if (applicationKey.length() > 0) {
                                                try {
                                                    JSONObject api_keys = jsonObject.optJSONObject("api_keys");
                                                    String environment = api_keys.optString(applicationKey);
                                                    displayLog("environment " + environment);
                                                    Parse.destroy();
                                                    dataProvider.insertStuff("environment", "" + environment);

                                                    if (environment != null) {
                                                        if (environment.length() > 0) {
                                                            if (environment.equalsIgnoreCase("PROD")) {
                                                                try {
                                                                    Parse.initialize(new Parse.Configuration.Builder(context)
                                                                            .applicationId(io.okheart.android.utilities.Constants.PROD_APPLICATION_ID)
                                                                            .clientKey(Constants.PROD_CLIENT_ID)
                                                                            .server("https://parseapi.back4app.com/").enableLocalDataStore().build());
                                                                } catch (Exception e4) {
                                                                    displayLog("parse initialize error " + e4.toString());
                                                                }
                                                            } else if (environment.equalsIgnoreCase("DEVMASTER")) {
                                                                try {
                                                                    Parse.initialize(new Parse.Configuration.Builder(context)
                                                                            .applicationId(io.okheart.android.utilities.Constants.DEVMASTER_APPLICATION_ID)
                                                                            .clientKey(io.okheart.android.utilities.Constants.DEVMASTER_CLIENT_ID)
                                                                            .server("https://parseapi.back4app.com/").enableLocalDataStore().build());
                                                                } catch (Exception e4) {
                                                                    displayLog("parse initialize error " + e4.toString());
                                                                }
                                                            } else if (environment.equalsIgnoreCase("SANDBOX")) {
                                                                try {
                                                                    Parse.initialize(new Parse.Configuration.Builder(context)
                                                                            .applicationId(io.okheart.android.utilities.Constants.SANDBOX_APPLICATION_ID)
                                                                            .clientKey(Constants.SANDBOX_CLIENT_ID)
                                                                            .server("https://parseapi.back4app.com/").enableLocalDataStore().build());
                                                                } catch (Exception e4) {
                                                                    displayLog("parse initialize error " + e4.toString());
                                                                }
                                                            } else {
                                                                try {
                                                                    Parse.initialize(new Parse.Configuration.Builder(context)
                                                                            .applicationId(io.okheart.android.utilities.Constants.PROD_APPLICATION_ID)
                                                                            .clientKey(Constants.PROD_CLIENT_ID)
                                                                            .server("https://parseapi.back4app.com/").enableLocalDataStore().build());
                                                                } catch (Exception e4) {
                                                                    displayLog("parse initialize error " + e4.toString());
                                                                }
                                                            }
                                                        } else {
                                                            try {
                                                                Parse.initialize(new Parse.Configuration.Builder(context)
                                                                        .applicationId(io.okheart.android.utilities.Constants.PROD_APPLICATION_ID)
                                                                        .clientKey(Constants.PROD_CLIENT_ID)
                                                                        .server("https://parseapi.back4app.com/").enableLocalDataStore().build());
                                                            } catch (Exception e4) {
                                                                displayLog("parse initialize error " + e4.toString());
                                                            }
                                                        }
                                                    } else {
                                                        try {
                                                            Parse.initialize(new Parse.Configuration.Builder(context)
                                                                    .applicationId(io.okheart.android.utilities.Constants.PROD_APPLICATION_ID)
                                                                    .clientKey(Constants.PROD_CLIENT_ID)
                                                                    .server("https://parseapi.back4app.com/").enableLocalDataStore().build());
                                                        } catch (Exception e4) {
                                                            displayLog("parse initialize error " + e4.toString());
                                                        }
                                                    }
                                                } catch (Exception e1) {

                                                }
                                                JSONArray killArray = jsonObject.optJSONArray("kill_switch");
                                                for (int i = 0; i < killArray.length(); i++) {
                                                    String affiliation = killArray.getString(i);
                                                    //displayLog("affiliation "+affiliation+" applicationKey "+applicationKey);
                                                    if (affiliation.equalsIgnoreCase(applicationKey)) {
                                                        displayLog("we are killing shit");
                                                        kill_switch = true;
                                                    }
                                                }
                                            }

                                        }
                                        dataProvider.insertStuff("noforeground", "" + noForeground);
                                        dataProvider.insertStuff("resume_ping_frequency", "" + resume_ping_frequency);
                                        dataProvider.insertStuff("ping_frequency", "" + ping_frequency);
                                        dataProvider.insertStuff("background_frequency", "" + background_frequency);
                                        dataProvider.insertStuff("sms_template", "" + sms_template);
                                        dataProvider.insertStuff("gps_accuracy", "" + gps_accuracy);
                                        dataProvider.insertStuff("kill_switch", "" + kill_switch);

                                        /*
                                        displayLog("resume_ping_frequency "+resume_ping_frequency+" ping_frequency "+ ping_frequency+
                                                " background_frequency "+background_frequency+" sms_template "+sms_template+"" +
                                                " gps_accuracy "+gps_accuracy+" kill_switch "+kill_switch);
                                        */

                                        /*
                                        OkHi.setBackground_frequency(background_frequency);
                                        OkHi.setGps_accuracy(gps_accuracy);
                                        OkHi.setResume_ping_frequency(resume_ping_frequency);
                                        OkHi.setPing_frequency(ping_frequency);
                                        OkHi.setSms_template(sms_template);
                                        OkHi.setKill_switch(kill_switch);
                                        */

                                    } catch (Exception e1) {
                                        displayLog("error getting json object results " + e1.toString());
                                    }

                                } else {
                                    // something went wrong
                                    displayLog("parsefile parse exception " + e.toString());
                                }
                            }
                        });


                    } else {
                        displayLog("1 parse object exception " + e.toString());

                    }
                }
            });
        } catch (Exception e) {
            displayLog("parse query configuration file error " + e.toString());
        }
    }


    private void decideWhatToStart() {
        List<io.okheart.android.datamodel.AddressItem> addressItemList = dataProvider.getAllAddressList();
        displayLog("addressItemList " + addressItemList.size());
        if (addressItemList.size() > 0) {

            String tempKill = dataProvider.getPropertyValue("kill_switch");
            if (tempKill != null) {
                if (tempKill.length() > 0) {
                    if (tempKill.equalsIgnoreCase("true")) {
                        String tempResume_ping_frequency = dataProvider.getPropertyValue("resume_ping_frequency");
                        if (tempResume_ping_frequency != null) {
                            if (tempResume_ping_frequency.length() > 0) {
                                Integer pingTime = Integer.parseInt(tempResume_ping_frequency);
                                startReplacePeriodicPing(pingTime, uniqueId);
                            } else {
                                startReplacePeriodicPing(360000000, uniqueId);
                            }
                        } else {
                            startReplacePeriodicPing(360000000, uniqueId);
                        }
                    } else {
                        String tempPing_frequency = dataProvider.getPropertyValue("ping_frequency");
                        if (tempPing_frequency != null) {
                            if (tempPing_frequency.length() > 0) {
                                Integer pingTime = Integer.parseInt(tempPing_frequency);
                                startKeepPeriodicPing(pingTime, uniqueId);
                            } else {
                                startKeepPeriodicPing(3600000, uniqueId);
                            }
                        } else {
                            startKeepPeriodicPing(3600000, uniqueId);
                        }
                    }
                } else {
                    String tempPing_frequency = dataProvider.getPropertyValue("ping_frequency");
                    if (tempPing_frequency != null) {
                        if (tempPing_frequency.length() > 0) {
                            Integer pingTime = Integer.parseInt(tempPing_frequency);
                            startKeepPeriodicPing(pingTime, uniqueId);
                        } else {
                            startKeepPeriodicPing(3600000, uniqueId);
                        }
                    } else {
                        startKeepPeriodicPing(3600000, uniqueId);
                    }
                }
            } else {
                String tempPing_frequency = dataProvider.getPropertyValue("ping_frequency");
                if (tempPing_frequency != null) {
                    if (tempPing_frequency.length() > 0) {
                        Integer pingTime = Integer.parseInt(tempPing_frequency);
                        startKeepPeriodicPing(pingTime, uniqueId);
                    } else {
                        startKeepPeriodicPing(3600000, uniqueId);
                    }
                } else {
                    startKeepPeriodicPing(3600000, uniqueId);
                }
            }
        } else {
            stopPeriodicPing();
        }
    }


    ///Please enable this after testing
    private void stopPeriodicPing() {

        try {
            HashMap<String, String> loans = new HashMap<>();
            loans.put("uniqueId", uniqueId);
            HashMap<String, String> parameters = new HashMap<>();
            parameters.put("eventName", "Data Collection Service");
            parameters.put("subtype", "stopPeriodicPing");
            parameters.put("type", "doWork");
            parameters.put("onObject", "app");
            parameters.put("view", "OkHi");
            sendEvent(parameters, loans);
        } catch (Exception e1) {
            displayLog("error attaching afl to ual " + e1.toString());
        }
        WorkManager.getInstance().cancelUniqueWork("ramogi");

    }

    private void startKeepPeriodicPing(Integer pingTime, String uniqueId) {
        displayLog("workmanager startKeepPeriodicPing " + pingTime);
        try {

            try {
                HashMap<String, String> loans = new HashMap<>();
                loans.put("uniqueId", uniqueId);
                HashMap<String, String> parameters = new HashMap<>();
                parameters.put("eventName", "Data Collection Service");
                parameters.put("subtype", "startKeepPeriodicPing");
                parameters.put("type", "doWork");
                parameters.put("onObject", "app");
                parameters.put("view", "OkHi");
                sendEvent(parameters, loans);
            } catch (Exception e1) {
                displayLog("error attaching afl to ual " + e1.toString());
            }

            Constraints constraints = new Constraints.Builder()
                    .setRequiredNetworkType(NetworkType.CONNECTED)
                    .build();

            Data inputData = new Data.Builder()
                    .putString("uniqueId", uniqueId)
                    .build();

            PeriodicWorkRequest request =
                    new PeriodicWorkRequest.Builder(MyWorker.class, pingTime, TimeUnit.MILLISECONDS)
                            .setInputData(inputData)
                            .setConstraints(constraints)
                            .setBackoffCriteria(BackoffPolicy.LINEAR, 30, TimeUnit.SECONDS)
                            .build();

            WorkManager.getInstance().enqueueUniquePeriodicWork("ramogi", ExistingPeriodicWorkPolicy.KEEP, request);

        } catch (Exception e) {
            displayLog("my worker error " + e.toString());
        }
    }

    private void startReplacePeriodicPing(Integer pingTime, String uniqueId) {
        displayLog("workmanager startReplacePeriodicPing");
        try {
            try {
                HashMap<String, String> loans = new HashMap<>();
                loans.put("uniqueId", uniqueId);
                HashMap<String, String> parameters = new HashMap<>();
                parameters.put("eventName", "Data Collection Service");
                parameters.put("subtype", "startReplacePeriodicPing");
                parameters.put("type", "doWork");
                parameters.put("onObject", "app");
                parameters.put("view", "OkHi");
                sendEvent(parameters, loans);
            } catch (Exception e1) {
                displayLog("error attaching afl to ual " + e1.toString());
            }

            Constraints constraints = new Constraints.Builder()
                    .setRequiredNetworkType(NetworkType.CONNECTED)
                    .build();

            Data inputData = new Data.Builder()
                    .putString("uniqueId", uniqueId)
                    .build();

            PeriodicWorkRequest request =
                    new PeriodicWorkRequest.Builder(MyWorker.class, pingTime, TimeUnit.MILLISECONDS)
                            .setInputData(inputData)
                            .setConstraints(constraints)
                            .setBackoffCriteria(BackoffPolicy.LINEAR, 30, TimeUnit.SECONDS)
                            .build();

            WorkManager.getInstance().enqueueUniquePeriodicWork("ramogi", ExistingPeriodicWorkPolicy.REPLACE, request);


        } catch (Exception e) {
            displayLog("my worker error " + e.toString());
        }
    }


    private void sendEvent(HashMap<String, String> parameters, HashMap<String, String> loans) {
        try {
            String environment = dataProvider.getPropertyValue("environment");

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

            io.okheart.android.utilities.OkAnalytics okAnalytics = new io.okheart.android.utilities.OkAnalytics(context, environment);
            okAnalytics.sendToAnalytics(parameters, loans, environment);
        } catch (Exception e) {
            displayLog("error sending photoexpanded analytics event " + e.toString());
        }
    }

    private void displayLog(String me) {
        //Log.i(TAG, me);
    }

}
