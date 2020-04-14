package io.okverify.android.asynctask;

import android.os.AsyncTask;

import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.concurrent.TimeUnit;

import io.okverify.android.callback.SegmentTrackCallBack;
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
public class SegmentTrackTask extends AsyncTask<Void, Void, String> {

    public static final String appLayer = "client";
    public static final String product = "okHeartAndroidSDK";
    public static final String formFactor = "mobile";
    public static final String appType = "native";
    public static final String librarytrackerwaybill = "OkAnalytics.java";
    public static final String versiontrackerwaybill = "2.0.0";
    private static final String TAG = "SegmentTrackTask";
    private static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
    private SegmentTrackCallBack segmentTrackCallBack;
    private JSONObject jsonObject;
    private int responseCode;
    private String production;


    public SegmentTrackTask(SegmentTrackCallBack segmentTrackCallBack, JSONObject jsonObject, String production) {
        displayLog("SegmentIdentifyTask called");

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

        this.segmentTrackCallBack = segmentTrackCallBack;
        //this.jsonObject = jsonObject;
        this.production = production;
    }

    @Override
    protected String doInBackground(Void... params) {
        String results = "";

        try {
            String urlString = "https://api.segment.io/v1/track";
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

            displayLog(jsonObject.toString());

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
        displayLog(result);
        if ((200 <= responseCode) && (responseCode < 300)) {

            segmentTrackCallBack.querycomplete(result, true);
        } else {

            segmentTrackCallBack.querycomplete(result, false);

        }
    }

    private void displayLog(String log) {
        //Log.i(TAG, log);
    }
}
