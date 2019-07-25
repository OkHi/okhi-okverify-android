package io.okheart.android.activity;

import android.os.AsyncTask;
import android.util.Log;
import android.webkit.JavascriptInterface;

import org.json.JSONException;
import org.json.JSONObject;

import io.okheart.android.BuildConfig;
import io.okheart.android.OkHi;
import io.okheart.android.activity.OkHeartActivity;
import io.okheart.android.asynctask.SegmentIdentifyTask;
import io.okheart.android.asynctask.SegmentTrackTask;
import io.okheart.android.callback.SegmentIdentifyCallBack;
import io.okheart.android.callback.SegmentTrackCallBack;

public class WebAppInterface {
    private static final String TAG = "WebAppInterface";
    OkHeartActivity mContext;
    private static String appkey;

    /** Instantiate the interface and set the context */
    WebAppInterface(OkHeartActivity c, String applicationKey) {
        mContext = c;
        appkey = applicationKey;
    }

    /** Show a message from the web page */
    @JavascriptInterface
    public void receiveMessage(String results) {
        displayLog("receiveMessage called "+results);

        try{
            final JSONObject jsonObject = new JSONObject(results);

            String message = jsonObject.optString("message");
            JSONObject payload = jsonObject.optJSONObject("payload");
            if(payload != null){
                displayLog("payload is not null "+payload);
            }
            else{
                displayLog("payload is null "+payload);
                String backuppayload = jsonObject.optString("payload");
                if(backuppayload != null){
                    payload = new JSONObject();
                    payload.put("Error",backuppayload);
                }
                else{

                }
            }

            try {

                switch (message) {
                    case "app_state":
                        displayLog("app_state");
                        if(payload != null){
                            Boolean ready = payload.optBoolean("ready");
                            if(ready != null) {
                                if (ready) {
                                    try{
                                        sendEvent(appkey, "app_state");
                                    }
                                    catch (Exception e){
                                        displayLog("error sending event "+e.toString());
                                    }
                                    mContext.runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            mContext.startApp();
                                        }
                                    });
                                }
                            }
                        }
                        break;
                    case "location_created":
                        displayLog("location_created");
                        try{
                            OkHi.getCallback().querycomplete(jsonObject);
                        }
                        catch (Exception e){
                            displayLog("error calling back "+e.toString());
                        }
                        try{
                            sendEvent(appkey, "location_created");
                        }
                        catch (Exception e){
                            displayLog("error sending event "+e.toString());
                        }
                        finally {
                            mContext.setCompletedWell(true);
                            mContext.setIsWebInterface(true);
                            mContext.finish();
                        }

                        break;
                    case "location_updated":
                        displayLog("location_updated");
                        try{
                            OkHi.getCallback().querycomplete(jsonObject);
                        }
                        catch (Exception e){
                            displayLog("error calling back "+e.toString());
                        }
                        try{
                            sendEvent(appkey, "location_updated");
                        }
                        catch (Exception e){
                            displayLog("error sending event "+e.toString());
                        }
                        finally {
                            mContext.setCompletedWell(true);
                            mContext.setIsWebInterface(true);
                            mContext.finish();
                        }
                        break;
                    case "location_selected":
                        displayLog("location_selected");
                        try{
                            OkHi.getCallback().querycomplete(jsonObject);
                        }
                        catch (Exception e){
                            displayLog("error calling back "+e.toString());
                        }
                        try{
                            sendEvent(appkey, "location_selected");
                        }
                        catch (Exception e){
                            displayLog("error sending event "+e.toString());
                        }
                        finally {
                            mContext.setCompletedWell(true);
                            mContext.setIsWebInterface(true);
                            mContext.finish();
                        }
                        break;
                    case "fatal_exit":
                        displayLog("fatal_exit");
                        try{
                            OkHi.getCallback().querycomplete(jsonObject);
                        }
                        catch (Exception e){
                            displayLog("error calling back "+e.toString());

                        }
                        try{
                            sendEvent(appkey, "fatal_exit");
                        }
                        catch (Exception e){
                            displayLog("error sending event "+e.toString());
                        }
                        finally {
                            mContext.setCompletedWell(true);
                            mContext.setIsWebInterface(true);
                            mContext.finish();
                        }
                        break;
                        /*
                    case "android_retrieve_gps_location":
                        displayLog("android_retrieve_gps_location");
                        if(OkHeartActivity.getLat() != null){
                            mContext.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    try{
                                        JSONObject jsonObject = new JSONObject();
                                        jsonObject.put("message","android_gps_location_found");
                                        JSONObject payload = new JSONObject();
                                        payload.put("lat", OkHeartActivity.getLat());
                                        payload.put("lng", OkHeartActivity.getLng());
                                        payload.put( "accuracy", OkHeartActivity.getAcc());
                                        payload.put( "timestamp", System.currentTimeMillis());
                                        jsonObject.put("payload",payload);
                                        displayLog(jsonObject.toString());
                                        mContext.sendGPSLocation(jsonObject);
                                    }
                                    catch (JSONException e){
                                        displayLog("jsonexception error "+e.toString());
                                    }
                                }
                            });
                        }
                        else{
                            mContext.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    try{
                                        JSONObject jsonObject = new JSONObject();
                                        jsonObject.put("message","android_gps_location_not_found");
                                        JSONObject payload = new JSONObject();
                                        payload.put("Error", "location not found");
                                        jsonObject.put("payload",payload);
                                        displayLog(jsonObject.toString());
                                        mContext.sendGPSLocation(jsonObject);
                                    }
                                    catch (JSONException e){
                                        displayLog("jsonexception error "+e.toString());
                                    }
                                }
                            });
                        }

                        break;
                        */
                    default:
                        displayLog("default");
                        break;

                }
            }
            catch (Exception e){
                displayLog("switch error "+e.toString());
            }
        }
        catch (JSONException e){
            displayLog("");
        }
    }

    private void sendEvent(final String appkey, final String action){
        try {
            Boolean production = false;
            if(appkey != null) {
                if( appkey.equalsIgnoreCase("r:b59a93ba7d80a95d89dff8e4c52e259a" ) ){

                }
                else{
                    production = true;
                }
            } else {
                production = true;
            }

            final Boolean productionVersion = production;

            JSONObject identifyjson = new JSONObject();
            //identifyjson.put("userId", userId);
            try {
                SegmentIdentifyCallBack segmentIdentifyCallBack = new SegmentIdentifyCallBack() {
                    @Override
                    public void querycomplete(String response, boolean status) {
                        if(status){
                            displayLog("things went ok with send to omtm identify");

                            try {
                                SegmentTrackCallBack segmentTrackCallBack = new SegmentTrackCallBack() {
                                    @Override
                                    public void querycomplete(String response, boolean status) {
                                        if(status){
                                            displayLog("things went ok with send to omtm track");
                                        }
                                        else{
                                            displayLog("something went wrong with send to omtm track");
                                        }
                                    }
                                };
                                JSONObject eventjson = new JSONObject();
                                //eventjson.put("userId", userId);
                                eventjson.put("event", "SDK Events");

                                JSONObject trackjson = new JSONObject();

                                if(productionVersion){
                                    trackjson.put("environment", "PROD");
                                }
                                else{
                                    trackjson.put("environment", "DEVMASTER");

                                }
                                trackjson.put("event", "SDK Events");

                                trackjson.put("action", action);
                                trackjson.put("actionSubtype", action);
                                trackjson.put("clientProduct", "okHeartAndroidSDK");
                                trackjson.put("clientProductVersion", BuildConfig.VERSION_NAME);
                                trackjson.put("clientKey",appkey);
                                trackjson.put("appLayer", "client");
                                trackjson.put("onObject", "sdk");
                                trackjson.put("product", "okHeartAndroidSDK");



                                eventjson.put("properties", trackjson);
                                SegmentTrackTask segmentTrackTask = new SegmentTrackTask(segmentTrackCallBack, eventjson, productionVersion);
                                segmentTrackTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                            }
                            catch (JSONException e){
                                displayLog("track error omtm error "+e.toString());
                            }
                        }
                        else{
                            displayLog("something went wrong with send to omtm identify");
                        }

                    }
                };
                SegmentIdentifyTask segmentIdentifyTask = new SegmentIdentifyTask(segmentIdentifyCallBack, identifyjson, productionVersion);
                segmentIdentifyTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

            } catch (Exception e) {
                displayLog("Error initializing analytics_omtm " + e.toString());
            }
        } catch (Exception jse){
            displayLog("jsonexception jse "+jse.toString());
        }

    }

    private void displayLog(String log){
        Log.i(TAG,log);
    }
}

