package io.okheart.android;

import android.Manifest;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.webkit.GeolocationPermissions;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import static io.okheart.android.OkHi.checkInternet;

public class OkHeartActivity extends AppCompatActivity {

    private static final String TAG = "OkHeartActivity";
    private static WebView myWebView;
    private static JSONObject jsonObject;
    private static Double lat, lng;
    private static Float acc;
    private static String firstname,lastname, phonenumber, apiKey, color, name, logo;
    private static OkHiCallback okHiCallback;
    private static boolean completedWell, isWebInterface;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(io.okheart.android.R.layout.activity_okheart);
        completedWell = false;
        isWebInterface = false;
        checkInternet();
        lat = null;
        lng = null;
        acc = null;
        color = null;
        name = null;
        logo = null;

        try{
            Bundle bundle = getIntent().getExtras();
            try {
                firstname = bundle.getString("firstname");
            } catch (Exception e) {
                displayLog("buildingname error " + e.toString());
            }

            try {
                lastname = bundle.getString("lastname");
            } catch (Exception e) {
                displayLog("buildingname error " + e.toString());
            }
            try {
                phonenumber = bundle.getString("phone");
            } catch (Exception e) {
                displayLog("buildingname error " + e.toString());
            }

            File filesDir = new File(getFilesDir()+"/okheart.txt");
            if(filesDir.exists()){
                displayLog("filesdir exists");
                try {
                    apiKey = getStringFromFile(filesDir.getAbsolutePath());
                    displayLog("api key "+apiKey);
                } catch (Exception e) {
                    // Hmm, the applicationId file was malformed or something. Assume it
                    // doesn't match.
                    displayLog("error "+e.toString());
                }
            }
            else{
                displayLog("filesdir does not exist");
            }

            File filesDirCustom = new File(getFilesDir()+"/custom.txt");
            if(filesDirCustom.exists()){
                displayLog("custom dir exists");
                try {
                    String customString = getStringFromFile(filesDirCustom.getAbsolutePath());
                    displayLog("custom string "+customString);
                    if(customString != null){
                        if(customString.length() > 0){
                            JSONObject jsonObject = new JSONObject(customString);
                            String tempColor = jsonObject.optString("color");
                            String tempName = jsonObject.optString("name");
                            String tempLogo = jsonObject.optString("logo");
                            if(tempColor != null){
                                if(tempColor.length() > 0){
                                    color = tempColor;
                                }
                            }
                            if(tempName != null){
                                if(tempName.length() > 0){
                                    name = tempName;
                                }
                            }
                            if(tempLogo != null){
                                if(tempLogo.length() > 0){
                                    logo = tempLogo;
                                }
                            }
                        }
                    }
                } catch (Exception e) {
                    // Hmm, the applicationId file was malformed or something. Assume it
                    // doesn't match.
                    displayLog("error "+e.toString());
                }
            }
            else{
                displayLog("custom dir does not exist");
            }

        }
        catch (Exception e){

        }


        /*
        firstname = "Ramogi";
        lastname = "Ochola";
        phonenumber = "+254713567907";
        apiKey = "r:b59a93ba7d80a95d89dff8e4c52e259a";
        //apiKey = "r:b4877fc0324225741db19553d67f147b";
        */



        myWebView = io.okheart.android.OkHeartActivity.this.findViewById(R.id.webview);
        myWebView.setWebViewClient(new MyWebViewClient());
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
        myWebView.addJavascriptInterface(new io.okheart.android.WebAppInterface(
                io.okheart.android.OkHeartActivity.this), "Android");
        //myWebView.loadUrl("https://manager-v4.okhi.dev");
        //myWebView.loadUrl("https://7b70b228.ngrok.io");
        if(apiKey != null) {
            if( apiKey.equalsIgnoreCase("r:b59a93ba7d80a95d89dff8e4c52e259a" ) ){
                myWebView.loadUrl("https://manager-v4.okhi.dev");
            }
            else{
                myWebView.loadUrl("https://manager-v4.okhi.co");
            }
        } else {
            myWebView.loadUrl("https://manager-v4.okhi.co");
        }



        myWebView.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onGeolocationPermissionsShowPrompt(String origin, GeolocationPermissions.Callback callback) {
                callback.invoke(origin, true, false);
            }
        });

        try{
            okHiCallback = OkHi.getCallback();
            if(okHiCallback != null){
                displayLog("okheartcallback is not null");
            }
            else{
                displayLog("okheartcallback is null");
            }
        }
        catch (Exception e){
            displayLog("error calling back "+e.toString());
        }
    }

    public void startApp() {
        checkInternet();

        try{

            JSONObject user = new JSONObject();
            user.put("firstName",firstname);
            user.put("lastName", lastname);
            user.put("phone",phonenumber);

            JSONObject base = new JSONObject();
            if(color != null){
                if(color.length() > 0) {
                    base.put("color", color);
                }
                else{
                    base.put("color","rgb(0, 131, 143)");
                }
            }
            else{
                base.put("color","rgb(0, 131, 143)");
            }
            if(name != null){
                if( name.length() > 0) {
                    base.put("name", name);
                }
                else{
                    base.put("name" ,"OkHi");
                }
            }
            else{
                base.put("name" ,"OkHi");
            }
            if(logo != null){
                if(logo.length() > 0) {
                    base.put("logo", Uri.parse(logo));
                }
                else{
                    String url = "https://cdn.okhi.co/okhi-logo-white.svg";
                    base.put("logo", Uri.parse(url));
                }
            }
            else{
                String url = "https://cdn.okhi.co/okhi-logo-white.svg";
                base.put("logo", Uri.parse(url));
            }
            JSONObject style = new JSONObject();
            style.put("base",base);

            JSONObject auth = new JSONObject();
            //auth.put( "apiKey", "r:66ae2e0d6e79d8ae06b8b692c2724609");
            //auth.put( "apiKey", "r:b59a93ba7d80a95d89dff8e4c52e259a");
            auth.put( "apiKey", apiKey);

            JSONObject parent = new JSONObject();
            parent.put( "name", "okHeartAndroidSDK");
            parent.put( "version", BuildConfig.VERSION_NAME);
            parent.put( "build", BuildConfig.VERSION_CODE);
            parent.put( "namespace", "com.develop.okheartandroidsdk.okhi");

            JSONObject location = new JSONObject();
            location.put("id", "x5yMOop8rY");

            JSONObject payload = new JSONObject();
            payload.put("user",user);
            payload.put("style",style);
            payload.put("auth", auth);
            payload.put("parent", parent);
            //payload.put("location",location);

            jsonObject = new JSONObject();
            jsonObject.put("message","app_state");
            jsonObject.put("payload",payload);
            displayLog("url "+jsonObject.getJSONObject("payload").getJSONObject("style").getJSONObject("base").getString("logo"));

            displayLog(""+jsonObject.toString());

        }
        catch (JSONException e){
            displayLog("jsonexception error "+e.toString());
        }
        myWebView.evaluateJavascript("javascript:receiveAndroidMessage("+jsonObject.toString()+")" , null);

    }

    public void sendGPSLocation(JSONObject jsonObject) {

        myWebView.evaluateJavascript("javascript:receiveAndroidData("+jsonObject.toString()+")" , null);

    }


    @Override
    protected void onDestroy(){

        try{

            if(completedWell){
                /*
                final JSONObject jsonObject1 = new JSONObject();
                jsonObject1.put("message","non_fatal_exit");
                JSONObject payload1 = new JSONObject();
                payload1.put("Response","Address creation completed successfully");
                jsonObject1.put("payload",payload1);
                displayLog(jsonObject.toString());
                okHiCallback.querycomplete(jsonObject1);
                */
            }
            else{
                if(isWebInterface){

                }
                else{
                    final JSONObject jsonObject1 = new JSONObject();
                    jsonObject1.put("message","fatal_exit");
                    JSONObject payload1 = new JSONObject();
                    payload1.put("Error","Address creation did not complete");
                    jsonObject1.put("payload",payload1);
                    displayLog(jsonObject.toString());
                    okHiCallback.querycomplete(jsonObject1);
                }

            }

        }
        catch (Exception e){
            displayLog("error calling back 1 "+e.toString());
        }
        super.onDestroy();
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
            displayLog("onPageFinished loadVariables(newURL) "+urlString);
            /*
            if(newURL!="") {
                myWebView.loadUrl("javascript:loadVariables(" + "\"" + newURL + "\")");
            }
            */
        }

        private void displayLog(String log){
            Log.i(TAG,log);
        }
    }



    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK) && myWebView.canGoBack()) {
            myWebView.goBack();
            return true;
        }

        return super.onKeyDown(keyCode, event);
    }

    public static String convertStreamToString(InputStream is) throws IOException {
        displayLog("convertStreamToString1");
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();
        String line = null;
        Boolean firstLine = true;
        while ((line = reader.readLine()) != null) {
            if(firstLine){
                sb.append(line);
                firstLine = false;
            } else {
                sb.append("\n").append(line);
            }
        }
        reader.close();
        displayLog("convertStreamToString2");
        return sb.toString();
    }

    public static String getStringFromFile (String filePath) throws IOException {
        displayLog("getStringFromFile1");
        File fl = new File(filePath);
        FileInputStream fin = new FileInputStream(fl);
        String ret = convertStreamToString(fin);
        //Make sure you close all streams.
        fin.close();
        displayLog("getStringFromFile2");
        return ret;
    }



    public static String getFirstname() {
        return firstname;
    }

    public static void setFirstname(String firstname) {
        io.okheart.android.OkHeartActivity.firstname = firstname;
    }

    public static String getLastname() {
        return lastname;
    }

    public static void setLastname(String lastname) {
        io.okheart.android.OkHeartActivity.lastname = lastname;
    }

    public static String getPhonenumber() {
        return phonenumber;
    }

    public static void setPhonenumber(String phonenumber) {
        io.okheart.android.OkHeartActivity.phonenumber = phonenumber;
    }

    public static Double getLat() {
        return lat;
    }

    public static void setLat(Double lat) {
        OkHeartActivity.lat = lat;
    }

    public static Double getLng() {
        return lng;
    }

    public static void setLng(Double lng) {
        OkHeartActivity.lng = lng;
    }

    public static Float getAcc() {
        return acc;
    }

    public static void setAcc(Float acc) {
        OkHeartActivity.acc = acc;
    }

    private static void displayLog(String log){
        Log.i(TAG,log);
    }

    public static boolean isCompletedWell() {
        return completedWell;
    }

    public static void setCompletedWell(boolean completedWell) {
        OkHeartActivity.completedWell = completedWell;
    }

    public static boolean isIsWebInterface() {
        return isWebInterface;
    }

    public static void setIsWebInterface(boolean isWebInterface) {
        OkHeartActivity.isWebInterface = isWebInterface;
    }
}
