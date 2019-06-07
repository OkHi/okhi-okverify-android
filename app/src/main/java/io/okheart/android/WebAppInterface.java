package io.okheart.android;

import android.util.Log;
import android.webkit.JavascriptInterface;

import org.json.JSONException;
import org.json.JSONObject;

public class WebAppInterface {
    private static final String TAG = "WebAppInterface";
    OkHeartActivity mContext;

    /** Instantiate the interface and set the context */
    WebAppInterface(OkHeartActivity c) {
        mContext = c;
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

    private void displayLog(String log){
        Log.i(TAG,log);
    }
}

