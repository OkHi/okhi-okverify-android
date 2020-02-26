package io.okheart.android.activity;

import android.Manifest;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.KeyEvent;
import android.webkit.GeolocationPermissions;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import io.okheart.android.OkHi;
import io.okheart.android.database.DataProvider;


public class OkHeartActivity extends AppCompatActivity {

    private static final String TAG = "OkHeartActivity";
    private static WebView myWebView;
    private static JSONObject jsonObject;
    private static Double lat, lng;
    private static Float acc;
    private static String firstname, lastname, phonenumber, apiKey, color, name, logo, appbarcolor;
    private static Boolean appbarvisible, enablestreetview;
    private static io.okheart.android.callback.OkHiCallback okHiCallback;
    private static boolean completedWell, isWebInterface;
    private static String uniqueId;
    private static String verify;
    private static DataProvider dataProvider;
    private static String environment;


    private static String convertStreamToString(InputStream is) throws IOException {
        //displayLog("convertStreamToString1");
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();
        String line = null;
        Boolean firstLine = true;
        while ((line = reader.readLine()) != null) {
            if (firstLine) {
                sb.append(line);
                firstLine = false;
            } else {
                sb.append("\n").append(line);
            }
        }
        reader.close();
        //displayLog("convertStreamToString2");
        return sb.toString();
    }

    private static String getStringFromFile(String filePath) throws IOException {
        //displayLog("getStringFromFile1");
        File fl = new File(filePath);
        FileInputStream fin = new FileInputStream(fl);
        String ret = convertStreamToString(fin);
        //Make sure you close all streams.
        fin.close();
        //displayLog("getStringFromFile2");
        return ret;
    }

    private static String getFirstname() {
        return firstname;
    }

    private static void setFirstname(String firstname) {
        OkHeartActivity.firstname = firstname;
    }

    private static String getLastname() {
        return lastname;
    }

    private static void setLastname(String lastname) {
        OkHeartActivity.lastname = lastname;
    }

    private static String getPhonenumber() {
        return phonenumber;
    }

    private static void setPhonenumber(String phonenumber) {
        OkHeartActivity.phonenumber = phonenumber;
    }

    private static Double getLat() {
        return lat;
    }

    private static void setLat(Double lat) {
        OkHeartActivity.lat = lat;
    }

    private static Double getLng() {
        return lng;
    }

    private static void setLng(Double lng) {
        OkHeartActivity.lng = lng;
    }

    private static Float getAcc() {
        return acc;
    }

    private static void setAcc(Float acc) {
        OkHeartActivity.acc = acc;
    }

    private static void displayLog(String log) {
        //Log.i(TAG, log);
    }

    private static boolean isCompletedWell() {
        return completedWell;
    }

    public static void setCompletedWell(boolean completedWell) {
        OkHeartActivity.completedWell = completedWell;
    }

    private static boolean isIsWebInterface() {
        return isWebInterface;
    }

    public static void setIsWebInterface(boolean isWebInterface) {
        OkHeartActivity.isWebInterface = isWebInterface;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(io.okheart.android.R.layout.activity_okheart);
        dataProvider = new DataProvider(this);
        environment = dataProvider.getPropertyValue("environment");
        displayLog("environment " + environment);

        /*
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                startForegroundService(new Intent(this, io.okheart.android.services.ForegroundService.class));
            } else {
                startService(new Intent(this, io.okheart.android.services.ForegroundService.class));
            }
        } catch (Exception jse) {
            displayLog("jsonexception jse " + jse.toString());
        }
        */

        completedWell = false;
        isWebInterface = false;
        //checkInternet();
        lat = null;
        lng = null;
        acc = null;
        color = null;
        name = null;
        logo = null;
        verify = "false";

        //OkHi.initialize("r:b59a93ba7d80a95d89dff8e4c52e259a", true, false);
        //OkHi.customize("rgb(0,179,255)", "Mula", "https://cdn.okhi.co/okhi-logo-white.svg");

        try {
            Bundle bundle = getIntent().getExtras();
            try {
                firstname = bundle.getString("firstname");
            } catch (Exception e) {
                displayLog("firstname error " + e.toString());
            }

            try {
                lastname = bundle.getString("lastname");
            } catch (Exception e) {
                displayLog("lastname error " + e.toString());
            }
            try {
                phonenumber = bundle.getString("phone");
            } catch (Exception e) {
                displayLog("phonenumber error " + e.toString());
            }
            try {
                uniqueId = bundle.getString("uniqueId");
            } catch (Exception e) {
                displayLog("uniqueId error " + e.toString());
            }

            File filesDirVerify = new File(getFilesDir() + "/verify.txt");
            if (filesDirVerify.exists()) {
                displayLog("filesDirVerify exists");
                try {
                    verify = getStringFromFile(filesDirVerify.getAbsolutePath());
                    displayLog("verify " + verify);
                } catch (Exception e) {
                    // Hmm, the applicationId file was malformed or something. Assume it
                    // doesn't match.
                    displayLog("filesDirVerify error " + e.toString());
                }
            } else {
                displayLog("filesDirVerify does not exist");
            }

            File filesDir = new File(getFilesDir() + "/okheart.txt");
            if (filesDir.exists()) {
                displayLog("filesdir exists");
                try {
                    apiKey = getStringFromFile(filesDir.getAbsolutePath());
                    displayLog("api key " + apiKey);
                } catch (Exception e) {
                    // Hmm, the applicationId file was malformed or something. Assume it
                    // doesn't match.
                    displayLog("error " + e.toString());
                }
            } else {
                displayLog("filesdir does not exist");
            }

            File filesDirCustom = new File(getFilesDir() + "/custom.txt");
            if (filesDirCustom.exists()) {
                displayLog("custom dir exists");
                try {
                    String customString = getStringFromFile(filesDirCustom.getAbsolutePath());
                    displayLog("custom string " + customString);
                    if (customString != null) {
                        if (customString.length() > 0) {
                            JSONObject jsonObject = new JSONObject(customString);
                            String tempColor = jsonObject.optString("color");
                            String tempName = jsonObject.optString("name");
                            String tempLogo = jsonObject.optString("logo");
                            String tempappbarcolor = jsonObject.optString("appbarcolor", "#f0f0f0");
                            Boolean tempappbarvisible = jsonObject.optBoolean("appbarvisibility", false);
                            Boolean tempstreetview = jsonObject.optBoolean("enablestreetview", false);
                            if (tempColor != null) {
                                if (tempColor.length() > 0) {
                                    color = tempColor;
                                }
                            }
                            if (tempName != null) {
                                if (tempName.length() > 0) {
                                    name = tempName;
                                }
                            }
                            if (tempLogo != null) {
                                if (tempLogo.length() > 0) {
                                    logo = tempLogo;
                                }
                            }

                            if (tempappbarcolor != null) {
                                if (tempappbarcolor.length() > 0) {
                                    appbarcolor = tempappbarcolor;
                                }
                            }
                            if (tempappbarvisible != null) {
                                appbarvisible = tempappbarvisible;
                            }
                            if (tempstreetview != null) {
                                enablestreetview = tempstreetview;
                            }

                        }
                    }
                } catch (Exception e) {
                    // Hmm, the applicationId file was malformed or something. Assume it
                    // doesn't match.
                    displayLog("error " + e.toString());
                }
            } else {
                displayLog("custom dir does not exist");
            }

        } catch (Exception e) {

        }

        /*
        firstname = "Ramogi";
        lastname = "Ochola";
        phonenumber = "+254713567907";

        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("firstName", firstname);
            jsonObject.put("lastName", lastname);
            jsonObject.put("phone", phonenumber);
            OkHi.displayClient(okHiCallback, jsonObject);
        } catch (JSONException e) {
            displayLog("json exception error " + e.toString());
        }
        */
        //displayLog("color " + color + " name " + name + " logo " + logo);
        //displayLog("appbarcolor " + appbarcolor + " appbarvisible " + appbarvisible + " enablestreetview " + enablestreetview);

/*
        firstname = "Ramogi";
        lastname = "Ochola";
        phonenumber = "+254713567907";
        */

        //apiKey = "r:b59a93ba7d80a95d89dff8e4c52e259a";


        //apiKey = "r:ee30a6552f7e5dfab48f4234bd1ffc1b";

        //apiKey = "r:b4877fc0324225741db19553d67f147b";

        myWebView = OkHeartActivity.this.findViewById(io.okheart.android.R.id.webview);
        myWebView.setWebViewClient(new MyWebViewClient());

        /*
        if(verify != null){
            if(verify.length() > 0){
                if(verify.equalsIgnoreCase("true")){
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                        ActivityCompat.requestPermissions(this, new String[]{
                                Manifest.permission.ACCESS_FINE_LOCATION,
                                Manifest.permission.ACCESS_COARSE_LOCATION,
                                Manifest.permission.ACCESS_BACKGROUND_LOCATION
                        }, 0);
                    }
                    else{
                        ActivityCompat.requestPermissions(this, new String[]{
                                Manifest.permission.ACCESS_FINE_LOCATION,
                                Manifest.permission.ACCESS_COARSE_LOCATION
                        }, 0);
                    }
                }
                else if(verify.equalsIgnoreCase("false")){
                    ActivityCompat.requestPermissions(this, new String[]{
                            Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_COARSE_LOCATION
                    }, 0);
                }
                else{
                    ActivityCompat.requestPermissions(this, new String[]{
                            Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_COARSE_LOCATION
                    }, 0);
                }
            }
            else{
                ActivityCompat.requestPermissions(this, new String[]{
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                }, 0);
            }
        }
        else{
            ActivityCompat.requestPermissions(this, new String[]{
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
            }, 0);
        }
        */

        ActivityCompat.requestPermissions(this, new String[]{
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
        }, 0);


        WebSettings webSettings = myWebView.getSettings();
        webSettings.setLoadsImagesAutomatically(true);
        webSettings.setJavaScriptEnabled(true);
        webSettings.setGeolocationEnabled(true);
        webSettings.setJavaScriptCanOpenWindowsAutomatically(true);
        webSettings.setAppCacheEnabled(true);
        webSettings.setDatabaseEnabled(true);
        webSettings.setDomStorageEnabled(true);
        myWebView.addJavascriptInterface(new WebAppInterface(OkHeartActivity.this, apiKey), "Android");
        //myWebView.loadUrl("https://manager-v4.okhi.dev");
        //myWebView.loadUrl("https://7b70b228.ngrok.io");


        if (environment != null) {
            if (environment.length() > 0) {
                if (environment.equalsIgnoreCase("PROD")) {
                    myWebView.loadUrl("https://manager-v4.okhi.co");
                } else if (environment.equalsIgnoreCase("DEVMASTER")) {
                    myWebView.loadUrl("https://manager-v4.okhi.dev");
                } else if (environment.equalsIgnoreCase("SANDBOX")) {
                    myWebView.loadUrl("https://sandbox-manager-v4.okhi.dev");
                } else {
                    myWebView.loadUrl("https://manager-v4.okhi.co");
                }
            } else {
                myWebView.loadUrl("https://manager-v4.okhi.co");
            }
        } else {
            myWebView.loadUrl("https://manager-v4.okhi.co");
        }

        /*
        if(productionVersion){
            myWebView.loadUrl("https://manager-v4.okhi.co");
        }
        else if(DEVMASTER){
            myWebView.loadUrl("https://manager-v4.okhi.dev");
        }else if(SANDBOX){
            myWebView.loadUrl("https://sandbox-manager-v4.okhi.dev");
        }
        else{
            myWebView.loadUrl("https://manager-v4.okhi.co");
        }
        */

        /*
        if (apiKey != null) {
            if (apiKey.equalsIgnoreCase("r:6d828427b625cda9bf9013dd80a93f97")) {
                myWebView.loadUrl("https://manager-v4.okhi.dev");
            } else if (apiKey.equalsIgnoreCase("r:ee30a6552f7e5dfab48f4234bd1ffc1b")) {
                myWebView.loadUrl("https://sandbox-manager-v4.okhi.dev");
            } else {
                myWebView.loadUrl("https://manager-v4.okhi.co");
            }
        } else {
            // myWebView.loadUrl("https://manager-v4.okhi.co");
        }
        */


        /*
        if(environment == null){
            if (apiKey != null) {
                if (apiKey.equalsIgnoreCase("r:6d828427b625cda9bf9013dd80a93f97")) {
                    environment = "DEVMASTER";
                    myWebView.loadUrl("https://manager-v4.okhi.dev");
                } else if (apiKey.equalsIgnoreCase("r:ee30a6552f7e5dfab48f4234bd1ffc1b")) {
                    environment = "SANDBOX";
                    myWebView.loadUrl("https://sandbox-manager-v4.okhi.dev");
                } else {
                    environment = "PROD";
                    myWebView.loadUrl("https://manager-v4.okhi.co");
                }
            } else {
                // myWebView.loadUrl("https://manager-v4.okhi.co");
            }
        }
        else{
            if(environment.length() > 0){

            }
            else{
                if (apiKey.equalsIgnoreCase("r:6d828427b625cda9bf9013dd80a93f97")) {
                    environment = "DEVMASTER";
                    myWebView.loadUrl("https://manager-v4.okhi.dev");
                } else if (apiKey.equalsIgnoreCase("r:ee30a6552f7e5dfab48f4234bd1ffc1b")) {
                    environment = "SANDBOX";
                    myWebView.loadUrl("https://sandbox-manager-v4.okhi.dev");
                } else {
                    environment = "PROD";
                    myWebView.loadUrl("https://manager-v4.okhi.co");
                }
            }
        }
        */

        myWebView.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onGeolocationPermissionsShowPrompt(String origin, GeolocationPermissions.Callback callback) {
                callback.invoke(origin, true, false);
            }
        });

        try {
            okHiCallback = OkHi.getCallback();
            if (okHiCallback != null) {
                displayLog("okheartcallback is not null");
            } else {
                displayLog("okheartcallback is null");
            }
        } catch (Exception e) {
            displayLog("error calling back " + e.toString());
        }

        try {
            /*
            Boolean production = false;
            if (apiKey != null) {
                if (apiKey.equalsIgnoreCase("r:6d828427b625cda9bf9013dd80a93f97")) {

                } else if (apiKey.equalsIgnoreCase("r:ee30a6552f7e5dfab48f4234bd1ffc1b")) {

                } else {
                    production = true;
                }
            } else {
                //production = true;
            }

            final Boolean productionVersion = production;
            */

            JSONObject identifyjson = new JSONObject();
            identifyjson.put("userId", "8VXRqG8YhN");
            try {
                io.okheart.android.callback.SegmentIdentifyCallBack segmentIdentifyCallBack = new io.okheart.android.callback.SegmentIdentifyCallBack() {
                    @Override
                    public void querycomplete(String response, boolean status) {
                        if (status) {
                            displayLog("things went ok with send to omtm identify");

                            try {
                                io.okheart.android.callback.SegmentTrackCallBack segmentTrackCallBack = new io.okheart.android.callback.SegmentTrackCallBack() {
                                    @Override
                                    public void querycomplete(String response, boolean status) {
                                        if (status) {
                                            displayLog("things went ok with send to omtm track");
                                        } else {
                                            displayLog("something went wrong with send to omtm track");
                                        }
                                    }
                                };
                                JSONObject eventjson = new JSONObject();
                                eventjson.put("userId", "8VXRqG8YhN");
                                eventjson.put("event", "SDK Initialization");

                                JSONObject trackjson = new JSONObject();

                                if (environment != null) {
                                    if (environment.length() > 0) {
                                        if (environment.equalsIgnoreCase("PROD")) {
                                            trackjson.put("environment", "PROD");
                                        } else if (environment.equalsIgnoreCase("DEVMASTER")) {
                                            trackjson.put("environment", "DEVMASTER");
                                        } else if (environment.equalsIgnoreCase("SANDBOX")) {
                                            trackjson.put("environment", "SANDBOX");
                                        } else {
                                            trackjson.put("environment", "PROD");
                                        }
                                    } else {
                                        trackjson.put("environment", "PROD");
                                    }
                                } else {
                                    trackjson.put("environment", "PROD");
                                }
                                trackjson.put("event", "SDK start");

                                trackjson.put("action", "start");
                                trackjson.put("actionSubtype", "start");
                                trackjson.put("clientProduct", "okHeartAndroidSDK");
                                trackjson.put("clientProductVersion", io.okheart.android.BuildConfig.VERSION_NAME);
                                trackjson.put("clientKey", apiKey);
                                trackjson.put("appLayer", "client");
                                trackjson.put("onObject", "sdk");
                                trackjson.put("product", "okHeartAndroidSDK");
                                trackjson.put("type", "start");
                                trackjson.put("subtype", "start");
                                trackjson.put("uniqueId", uniqueId);

                                eventjson.put("properties", trackjson);
                                io.okheart.android.asynctask.SegmentTrackTask segmentTrackTask = new io.okheart.android.asynctask.SegmentTrackTask(segmentTrackCallBack, eventjson, environment);
                                segmentTrackTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                            } catch (JSONException e) {
                                displayLog("track error omtm error " + e.toString());
                            }
                        } else {
                            displayLog("something went wrong with send to omtm identify");
                        }

                    }
                };
                io.okheart.android.asynctask.SegmentIdentifyTask segmentIdentifyTask = new io.okheart.android.asynctask.SegmentIdentifyTask(segmentIdentifyCallBack, identifyjson, environment);
                segmentIdentifyTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

            } catch (Exception e) {
                displayLog("Error initializing analytics_omtm " + e.toString());
            }
        } catch (Exception jse) {
            displayLog("jsonexception jse " + jse.toString());
        }

    }

    public void startApp() {
        //checkInternet();

        try {
            if (color != null) {
                if (color.length() > 0) {
                } else {
                    color = "rgb(0, 131, 143)";
                }
            } else {
                color = "rgb(0, 131, 143)";
            }
            if (name != null) {
                if (name.length() > 0) {

                } else {
                    name = "OkHi";
                }
            } else {
                name = "OkHi";
            }
            if (logo != null) {
                if (logo.length() > 0) {

                } else {
                    logo = "https://cdn.okhi.co/okhi-logo-white.svg";
                }
            } else {
                logo = "https://cdn.okhi.co/okhi-logo-white.svg";
            }

            if (appbarcolor != null) {
                if (appbarcolor.length() > 0) {
                } else {
                    appbarcolor = "#f0f0f0";
                }
            } else {
                appbarcolor = "#f0f0f0";
            }
            if (appbarvisible != null) {

            } else {
                appbarvisible = false;
            }
            if (enablestreetview != null) {
            } else {
                enablestreetview = false;
            }


            displayLog("color " + color + " name " + name + " logo " + logo);
            displayLog("appbarcolor " + appbarcolor + " appbarvisible " + appbarvisible + " enablestreetview " + enablestreetview);


            String stuff = "{\n" +
                    "  \"message\": \"select_location\",\n" +
                    "  \"payload\": {\n" +
                    "    \"user\": {\n" +
                    "      \"firstName\": \"" + firstname + "\",\n" +
                    "      \"lastName\": \"" + lastname + "\",\n" +
                    "      \"phone\": \"" + phonenumber + "\"\n" +
                    "    },\n" +
                    "    \"style\": {\n" +
                    "      \"base\": {\n" +
                    "        \"color\": \"" + color + "\",\n" +
                    "        \"name\": \"" + name + "\",\n" +
                    "        \"logo\": \"" + logo + "\"\n" +
                    "      }\n" +
                    "    },\n" +

                    "    \"config\": {\n" +
                    "      \"appBar\": {\n" +
                    "        \"color\": \"" + appbarcolor + "\",\n" +
                    "        \"visible\": " + appbarvisible + "\n" +
                    "      },\n" +
                    "    \"streetView\": " + enablestreetview + "\n" +
                    "    },\n" +

                    "    \"auth\": {\n" +
                    "      \"apiKey\": \"" + apiKey + "\"\n" +
                    "    },\n" +
                    "    \"parent\": {\n" +
                    "      \"name\": \"okHeartAndroidSDK\",\n" +
                    "      \"version\": \"" + io.okheart.android.BuildConfig.VERSION_NAME + "\",\n" +
                    "      \"build\": \"" + io.okheart.android.BuildConfig.VERSION_CODE + "\",\n" +
                    "      \"namespace\": \"com.develop.okheartandroidsdk.okhi\"\n" +
                    "    }\n" +
                    "  }\n" +
                    "}";
            displayLog(stuff);
            myWebView.evaluateJavascript("javascript:receiveAndroidMessage(" + stuff + ")", null);
        } catch (Exception e) {
            displayLog("jsonexception error " + e.toString());
        }
    }

    private void sendGPSLocation(JSONObject jsonObject) {

        myWebView.evaluateJavascript("javascript:receiveAndroidData(" + jsonObject.toString() + ")", null);

    }

    @Override
    protected void onDestroy() {

        try {

            if (completedWell) {
                /*
                final JSONObject jsonObject1 = new JSONObject();
                jsonObject1.put("message","non_fatal_exit");
                JSONObject payload1 = new JSONObject();
                payload1.put("Response","Address creation completed successfully");
                jsonObject1.put("payload",payload1);
                displayLog(jsonObject.toString());
                okHiCallback.querycomplete(jsonObject1);
                */
            } else {
                if (isWebInterface) {

                } else {
                    final JSONObject jsonObject1 = new JSONObject();
                    jsonObject1.put("message", "fatal_exit");
                    JSONObject payload1 = new JSONObject();
                    payload1.put("Error", "Address creation did not complete");
                    jsonObject1.put("payload", payload1);
                    displayLog(jsonObject.toString());
                    okHiCallback.querycomplete(jsonObject1);
                }

            }

        } catch (Exception e) {
            displayLog("error calling back 1 " + e.toString());
        }
        super.onDestroy();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK) && myWebView.canGoBack()) {
            myWebView.goBack();
            return true;
        }

        return super.onKeyDown(keyCode, event);
    }

    private class MyWebViewClient extends WebViewClient {
        private static final String TAG = "MyWebViewClient";

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            // TODO Auto-generated method stub
            super.onPageStarted(view, url, favicon);
            displayLog("onPageStarted");
        }


        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            displayLog("shouldOverrideUrlLoading");
            if (Uri.parse(url).getHost().equals("https://manager-v4.okhi.co")) {
                // This is my website, so do not override; let my WebView load the page

                return false;
            } else return !Uri.parse(url).getHost().equals("https://manager-v4.okhi.dev");
            // Otherwise, the link is not for a page on my site, so launch another Activity that handles URLs
            //Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            //startActivity(intent);
        }

        @Override
        public void onPageFinished(WebView view, String urlString) {
            displayLog("onPageFinished loadVariables(newURL) " + urlString);
            /*
            if(newURL!="") {
                myWebView.loadUrl("javascript:loadVariables(" + "\"" + newURL + "\")");
            }
            */
        }

        private void displayLog(String log) {
            //Log.i(TAG, log);
        }
    }
}
