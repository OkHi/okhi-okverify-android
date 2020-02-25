package io.okheart.android.utilities;


import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

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

import io.okheart.android.datamodel.FieldItemObject;

public class ConfigurationFile {

    private static final String TAG = "ConfigurationFile";
    private static Context context;
    private static HashMap<String, List<FieldItemObject>> customFields;
    private io.okheart.android.database.DataProvider dataProvider;

    public ConfigurationFile(final Context context) {
        displayLog("ConfigurationFile called ");
        ConfigurationFile.context = context;
        dataProvider = new io.okheart.android.database.DataProvider(context);

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
                                                    if (noForeground != null) {
                                                        displayLog("noforeground is not null");
                                                        if (noForeground.length() > 0) {
                                                            displayLog("noforeground length is not zero");
                                                            String brand = Build.MANUFACTURER;
                                                            if (noForeground.toLowerCase().contains(brand.toLowerCase())) {
                                                                displayLog("we have brand " + noForeground + " " + brand);
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
/*
    private void getCustomFields() {
        try {
            displayLog("custom start " + OkSquirrelApplication.getLoginaffiliation());
            ParseQuery<ParseObject> query = ParseQuery.getQuery("ConfigurationFile");
            query.whereEqualTo("name", "customfieldslist");
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
                                        JSONObject jsonObject = new JSONObject(bytes);
                                        JSONArray jsonArray = jsonObject.optJSONArray("customfield");
                                        //displayLog("jsonArray length "+jsonArray.length());
                                        HashMap<String, List<FieldItemObject>> customFields = new HashMap<>();
                                        for (int i = 0; i < jsonArray.length(); i++) {
                                            try {
                                                JSONObject customObject = jsonArray.optJSONObject(i);
                                                String affiliation = customObject.optString("affiliation");
                                                JSONArray fieldsArray = customObject.optJSONArray("fields");
                                                //displayLog("affiliation "+affiliation);
                                                //displayLog("fields array length "+fieldsArray.length());
                                                List<FieldItemObject> fieldItemObjects = new ArrayList<>();
                                                for (int y = 0; y < fieldsArray.length(); y++) {
                                                    JSONObject fieldsObject = fieldsArray.optJSONObject(y);
                                                    String fieldName = fieldsObject.optString("fieldName");
                                                    String fieldType = fieldsObject.optString("fieldType");
                                                    String fieldDefaultValue = fieldsObject.optString("fieldDefaultValue");
                                                    Boolean isOptional = fieldsObject.optBoolean("isOptional");

                                                    FieldItemObject fieldItemObject = new FieldItemObject();
                                                    fieldItemObject.setFieldName(fieldName);
                                                    fieldItemObject.setFieldDefaultValue(fieldDefaultValue);
                                                    fieldItemObject.setFieldType(fieldType);
                                                    fieldItemObject.setOptional(isOptional);
                                                    fieldItemObjects.add(fieldItemObject);
                                                }
                                                customFields.put(affiliation, fieldItemObjects);

                                            } catch (Exception e1) {
                                                displayLog("inner json object error " + e1.toString());
                                            }
                                        }
                                        displayLog("custom end 2");
                                        OkSquirrelApplication.setCustomFields(customFields);

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
                        displayLog("parse object exception " + e.toString());
                    }
                }
            });
        } catch (Exception e) {
            displayLog("parse query configuration file error " + e.toString());
        }
    }

*/

    private void displayLog(String me) {
        Log.i(TAG, me);
    }

}
