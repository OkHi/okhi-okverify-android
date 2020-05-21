package io.okverify.android.asynctask;

import android.os.AsyncTask;
import android.util.Log;

import com.parse.ParseObject;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;

import io.okverify.android.BuildConfig;
import io.okverify.android.callback.TransitsCallBack;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;


/**
 * Created by ramogiochola on 6/21/16.
 */
public class TransitsTask extends AsyncTask<Void, Void, String> {

    private static final String TAG = "TransitsTask";
    private TransitsCallBack transitsCallBack;
    private HashMap<String, Object> postDataParams = new HashMap<>();
    private int responseCode;
    //private Context context;
    private String environment;
    private JSONArray transitsArray;
    private JSONObject jsonObject, finalObject, meta, lib;
    //private String applicationkey, branchid;
    private String token;
    //private io.okverify.android.database.DataProvider dataProvider;


    public TransitsTask(TransitsCallBack transitsCallBack, ParseObject parseObject, String environment, String token) {
        displayLog("SendCustomLinkSmsTask called");

        this.transitsCallBack = transitsCallBack;
        //this.context = context;
        this.environment = environment;
        //this.dataProvider = new DataProvider(context);
        //this.applicationkey = applicationkey;
        //this.branchid = branchid;
        this.token = token;

        try{
            JSONObject geoPoint = new JSONObject();
            geoPoint.put("lat", parseObject.get("latitude"));
            geoPoint.put("lon", parseObject.get("longitude"));
            jsonObject = new JSONObject();
            transitsArray = new JSONArray();
            //JSONObject actualObject = new JSONObject();
            JSONArray idsArray = new JSONArray();
            idsArray.put(parseObject.get("ualId"));
            jsonObject.put("ids", idsArray);
            jsonObject.put("platform", parseObject.get("platform"));
            //jsonObject.put("geopointProvider", parseObject.get("provider"));
            jsonObject.put("geopoint_provider", parseObject.get("provider"));
            jsonObject.put("transition_date", System.currentTimeMillis());
            jsonObject.put("transition_event", parseObject.get("transition"));
            jsonObject.put("device_os_name", parseObject.get("OSName"));
            jsonObject.put("device_os_version", ""+parseObject.get("OSVersion"));
            jsonObject.put("device_manufacturer", parseObject.get("brand"));
            jsonObject.put("device_model", parseObject.get("model"));
            jsonObject.put("geo_point_source", parseObject.get("geofence"));
            //jsonObject.put("geoPointSource", parseObject.get("geofence"));
            jsonObject.put("gps_accuracy", parseObject.get("gpsAccuracy"));
            //jsonObject.put("accuracy", parseObject.get("gpsAccuracy"));
            //jsonObject.put("gpsAccuracy", parseObject.get("gpsAccuracy"));
            jsonObject.put("geo_point", geoPoint);
            jsonObject.put("version_code", BuildConfig.VERSION_CODE);
            jsonObject.put("version_name", BuildConfig.VERSION_NAME);

            displayLog("1 "+jsonObject.getDouble("gps_accuracy"));
            //displayLog("2 "+jsonObject.getDouble("accuracy"));
            displayLog("3 "+jsonObject.getString("geo_point_source"));
            transitsArray.put(jsonObject);
            finalObject = new JSONObject();
            finalObject.put("transits", transitsArray);
            JSONObject lib = new JSONObject();
            lib.put("name", "okverifyMobileAndroid");
            lib.put("version", BuildConfig.VERSION_NAME);
            meta = new JSONObject();
            meta.put("lib", lib);
            finalObject.put("transits",transitsArray);
            finalObject.put("meta", meta);
            finalObject.put("lib", lib);
            displayLog(finalObject.toString());
            //jsonObject.put("transits", transitsArray);

        }
        catch (Exception e){
            displayLog("error puting song object "+e.toString());
        }
    }

    @Override
    protected String doInBackground(Void... params) {
        String results = "";

        try {

            //check the previous event, if its same otherwise call transit
            //check the duration if its less than an hour dont proceed, if its more go ahead
            String urlString;

            if (environment.equalsIgnoreCase("PROD")) {
                urlString = "https://okhi.back4app.io/send-sms";
            } else if (environment.equalsIgnoreCase("DEVMASTER")) {
                urlString = "https://dev-api.okhi.io/v5/users/transits";
                //urlString = "https://yolo.requestcatcher.com";
            } else if (environment.equalsIgnoreCase("SANDBOX")) {
                urlString = "https://okhi-sbox.back4app.io/send-sms";
            } else {
                urlString = "https://okhi.back4app.io/send-sms";
            }

            OkHttpClient.Builder b = new OkHttpClient.Builder();
            b.connectTimeout(15, TimeUnit.SECONDS);
            b.readTimeout(15, TimeUnit.SECONDS);
            b.writeTimeout(15, TimeUnit.SECONDS);

            OkHttpClient client = b.build();

            MediaType JSON = MediaType.parse("application/json; charset=utf-8");
            RequestBody body = RequestBody.create(JSON, finalObject.toString());

            /*
            RequestBody formBody = new FormBody.Builder()
                    .add("meta", meta.toString())
                    .add("transits", transitsArray.toString())
                    .build();
            */

            Request request = new Request.Builder()
                    .url(urlString)
                    .post(body)
                    .addHeader("Authorization", "Bearer "+token)
                    .build();

            Response response = client.newCall(request).execute();
            ResponseBody responseBody = response.body();
            results = responseBody.string();
            responseCode = response.code();

        } catch (UnsupportedEncodingException e) {
            displayLog("unsupported encoding exception " + e.toString());
        } catch (IOException io) {
            displayLog("io exception " + io.toString());
        } catch (IllegalArgumentException iae) {
            displayLog("illegal argument exception " + iae.toString());
        }

        return results;
    }

    @Override
    protected void onPostExecute(String result) {

        displayLog("response code " + responseCode + " onPostExecute result " + result);
        //displayLog(OkVerifyApplication.getUniqueId());


        if ((200 <= responseCode) && (responseCode < 300)) {

            transitsCallBack.querycomplete(result, true);
            /*
            try {
                HashMap<String, String> loans = new HashMap<>();
                loans.put("phonenumber", postDataParams.get("phone"));
                loans.put("uniqueId", postDataParams.get("uniqueId"));
                HashMap<String, String> parameters = new HashMap<>();
                parameters.put("eventName", "Manual Ping");
                parameters.put("subtype", "manualPing");
                parameters.put("type", "onPostExecute");
                parameters.put("onObject", "success");
                parameters.put("view", "sendCustomLinkTask");
                //parameters.put("branch", "branch");
                //parameters.put("ualId", model.getUalId());
                sendEvent(parameters, loans);
            } catch (Exception e1) {
                displayLog("error attaching afl to ual " + e1.toString());
            }
            */
        } else {
            transitsCallBack.querycomplete(result, false);
            /*
            try {
                HashMap<String, String> loans = new HashMap<>();
                loans.put("phonenumber", postDataParams.get("phone"));
                //loans.put("ualId", model.getUalId());
                HashMap<String, String> parameters = new HashMap<>();
                parameters.put("eventName", "Manual Ping");
                parameters.put("subtype", "manualPing");
                parameters.put("type", "onPostExecute");
                parameters.put("onObject", "failed");
                parameters.put("view", "sendCustomLinkTask");
                //parameters.put("branch", "branch");
                //parameters.put("ualId", model.getUalId());
                sendEvent(parameters, loans);
            } catch (Exception e1) {
                displayLog("error attaching afl to ual " + e1.toString());
            }
            if (result != null) {
                if (result.length() > 2) {
                    sendCustomLinkSmsCallBack.querycomplete(result, false);
                } else {
                    sendCustomLinkSmsCallBack.querycomplete("Please check your internet settings or data bundles", false);
                }
            } else {
                sendCustomLinkSmsCallBack.querycomplete("Please check your internet settings or data bundles", false);
            }
            */
        }
    }

    private void sendEvent(HashMap<String, String> parameters, HashMap<String, String> loans) {
        try {
            //OkAnalytics okAnalytics = new OkAnalytics();
            //okAnalytics.sendToAnalytics(parameters, loans, environment);
        } catch (Exception e) {
            displayLog("error sending photoexpanded analytics event " + e.toString());
        }
    }

    private void displayLog(String log) {
        Log.i(TAG, log);
    }
}
