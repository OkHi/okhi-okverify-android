package io.okverify.android.asynctask;

import android.os.AsyncTask;

import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.concurrent.TimeUnit;

import io.okverify.android.callback.SegmentIdentifyCallBack;
import io.okverify.android.utilities.BasicAuthInterceptor;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;

/**
 * Created by ramogiochola on 6/21/16.
 */
public class SegmentIdentifyTask extends AsyncTask<Void, Void, String> {

    public static final String appLayer = "client";
    public static final String product = "okHeartAndroidSDK";
    public static final String formFactor = "mobile";
    public static final String appType = "native";
    public static final String librarytrackerwaybill = "OkAnalytics.java";
    public static final String versiontrackerwaybill = "2.0.0";
    private static final String TAG = "SegmentIdentifyTask";
    private static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
    private SegmentIdentifyCallBack segmentIdentifyCallBack;
    private JSONObject jsonObject;
    private int responseCode;
    private String production;


    public SegmentIdentifyTask(SegmentIdentifyCallBack segmentIdentifyCallBack, JSONObject jsonObject, String production) {
        displayLog("SegmentIdentifyTask called");

        this.segmentIdentifyCallBack = segmentIdentifyCallBack;
        //this.jsonObject = jsonObject;
        this.production = production;

        /*
        try {
            jsonObject.put("height", getHeightScreen());
        } catch (Exception e) {
            displayLog(" height error " + e.toString());
        }
        try {
            jsonObject.put("width", getWidthScreen());
        } catch (Exception e) {
            displayLog(" width error " + e.toString());
        }
         try {
            jsonObject.put("screen", screen);
        } catch (Exception e) {
            displayLog(" versiontrackerwaybill error " + e.toString());
        }
        try {
            jsonObject.put("model", model);
        } catch (Exception e) {
            displayLog(" versiontrackerwaybill error " + e.toString());
        }
        try {
            contextdeviceandproduct.putValue("cookieToken", OkDriverApplication.getDeviceid());
        } catch (Exception e) {
            displayLog(" versiontrackerwaybill error " + e.toString());
        }

        try {
            contextdeviceandproduct.putValue("device", device);
        } catch (Exception e) {
            displayLog(" versiontrackerwaybill error " + e.toString());
        }
        */
        try {
            jsonObject.put("formFactor", formFactor);
        } catch (Exception e) {
            displayLog(" formFactor error " + e.toString());
        }
        try {
            jsonObject.put("appType", appType);
        } catch (Exception e) {
            displayLog(" appType error " + e.toString());
        }

        try {
            JSONObject trackerWaybill = new JSONObject();
            trackerWaybill.put("library", librarytrackerwaybill);
            trackerWaybill.put("version", versiontrackerwaybill);
            JSONObject waybill = new JSONObject();
            waybill.put("tracker", trackerWaybill);
            jsonObject.put("waybill", waybill);
            JSONObject payload = new JSONObject();
            payload.put("okAnalyticsEvent", jsonObject);
            this.jsonObject = payload;
        } catch (Exception e) {
            displayLog(" waybill error " + e.toString());
        }
    }

    @Override
    protected String doInBackground(Void... params) {
        String results = "";

        try {
            String urlString = "https://api.segment.io/v1/identify";
            String writekey;
            if (production.equalsIgnoreCase("PROD")) {
                writekey = io.okverify.android.utilities.Constants.ANALYTICS_WRITE_KEY_PROD_OMTM;
            } else if (production.equalsIgnoreCase("DEVMASTER")) {
                writekey = io.okverify.android.utilities.Constants.ANALYTICS_WRITE_KEY_DEV_OMTM;
            } else if (production.equalsIgnoreCase("SANDBOX")) {
                writekey = io.okverify.android.utilities.Constants.ANALYTICS_WRITE_KEY_DEV_OMTM;
            } else {
                writekey = io.okverify.android.utilities.Constants.ANALYTICS_WRITE_KEY_PROD_OMTM;
            }

            OkHttpClient.Builder b = new OkHttpClient.Builder();
            b.addInterceptor(new BasicAuthInterceptor(writekey, null));
            b.connectTimeout(15, TimeUnit.SECONDS);
            b.writeTimeout(15, TimeUnit.SECONDS);
            b.readTimeout(15, TimeUnit.SECONDS);
            OkHttpClient client = b.build();

            RequestBody requestBody = RequestBody.create(JSON, jsonObject.toString());

            Request request = new Request.Builder()
                    .url(urlString)
                    .post(requestBody)
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
        if ((200 <= responseCode) && (responseCode < 300)) {

            segmentIdentifyCallBack.querycomplete(result, true);
        } else {

            segmentIdentifyCallBack.querycomplete(result, false);

        }
    }

    private void displayLog(String log) {
        //Log.i(TAG, log);
    }
}
