package io.okverify.android.asynctask;

import android.os.AsyncTask;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;

/**
 * Created by ramogiochola on 8/1/16.
 */
public class HeartBeatTask extends AsyncTask<Void, Void, Boolean> {

    private static final String TAG = "HeartBeatTask";
    private HashMap<String, String> postDataParams = new HashMap<>();
    private int responseCode;
    private io.okverify.android.callback.HeartBeatCallBack heartBeatCallBack;
    private String message;

    public HeartBeatTask(io.okverify.android.callback.HeartBeatCallBack heartBeatCallBack1) {
        heartBeatCallBack = heartBeatCallBack1;
    }

    @Override
    protected Boolean doInBackground(Void... params) {
        Boolean results = false;

        try {
            String appId, restApi, urlString;
            urlString = "https://okhi.back4app.io/heartbeat";
            appId = "nJXDQ0HXsM8uTP9pDLmL2BQmHL2c0AnE0vDPGrL7";
            restApi = "aL41lwOjUcqaADriWfkMFQG49qqBl4tGoI3n838n";

            OkHttpClient.Builder b = new OkHttpClient.Builder();
            b.connectTimeout(5, TimeUnit.SECONDS);
            b.readTimeout(5, TimeUnit.SECONDS);
            b.writeTimeout(5, TimeUnit.SECONDS);

            OkHttpClient client = b.build();

            // Initialize Builder (not RequestBody)
            FormBody.Builder builder = new FormBody.Builder();

            // Add Params to Builder
            for (Map.Entry<String, String> entry : postDataParams.entrySet()) {
                try {
                    builder.add(entry.getKey(), entry.getValue());
                } catch (Exception e) {
                    displayLog(" builder.add error  " + e.toString());
                }
            }

            RequestBody requestBody = builder.build();

            Request request = new Request.Builder()
                    .url(urlString)
                    .post(requestBody)
                    .addHeader("X-Parse-Application-Id", appId)
                    .addHeader("X-Parse-REST-API-Key", restApi)
                    .build();

            Response response = client.newCall(request).execute();
            ResponseBody responseBody = response.body();
            message = responseBody.string();
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
    protected void onPostExecute(Boolean result) {
        displayLog("heartbeat postexecute " + responseCode + " message " + message);
        if ((200 <= responseCode) && (responseCode < 300)) {
            heartBeatCallBack.querycomplete(true);
        } else {
            heartBeatCallBack.querycomplete(false);
        }
    }

    private void displayLog(String log) {
        //Log.i(TAG, log);
    }
}
