package io.okheart.android.asynctask;

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

public class SendSMS extends AsyncTask<Void, Void, String> {
    private static final String TAG = "SendSMS";
    private String phonenumber, message;
    private HashMap<String, String> postDataParams = new HashMap<>();
    private int responseCode;

    public SendSMS(String phonenumber, String message) {
        this.phonenumber = phonenumber;
        this.message = message;
        postDataParams.put("phonenumber", phonenumber);
        postDataParams.put("message", message);
        postDataParams.put("userId", "24Lilj8IIy");
        postDataParams.put("sessionToken", "r:ee30a6552f7e5dfab48f4234bd1ffc1b");
        postDataParams.put("customName", "test");
        postDataParams.put("phoneNumber", phonenumber);
        postDataParams.put("phone", phonenumber);
    }

    @Override
    protected String doInBackground(Void... params) {
        String results = null;
        String appId = io.okheart.android.utilities.Constants.SANDBOX_APPLICATION_ID;
        String restApi = io.okheart.android.utilities.Constants.SANDBOX_REST_KEY;
        String urlString = "https://okhi-sbox.back4app.io/send-sms";
        try {

            OkHttpClient.Builder b = new OkHttpClient.Builder();
            b.connectTimeout(15, TimeUnit.SECONDS);
            b.readTimeout(15, TimeUnit.SECONDS);
            b.writeTimeout(15, TimeUnit.SECONDS);

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
        displayLog("sms postexecute " + responseCode + " phonenumber " + phonenumber);
    }

    private void displayLog(String log) {
        //Log.i(TAG, log);
    }
}
