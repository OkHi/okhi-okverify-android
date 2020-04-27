package io.okverify.android.asynctask;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.parse.ParseObject;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import io.okverify.android.callback.TransitsCallBack;
import io.okverify.android.callback.VerificationCallBack;
import io.okverify.android.database.DataProvider;
import io.okverify.android.utilities.OkAnalytics;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;


/**
 * Created by ramogiochola on 6/21/16.
 */
public class VerificationTokenTask extends AsyncTask<Void, Void, String> {

    private static final String TAG = "VerificationTokenTask";
    private VerificationCallBack verificationCallBack;
    private HashMap<String, Object> postDataParams = new HashMap<>();
    private int responseCode;
    private Context context;
    private String environment;
    private JSONObject jsonObject;
    private String applicationkey, branchid;
    private DataProvider dataProvider;


    public VerificationTokenTask(Context context, VerificationCallBack verificationCallBack, String applicationkey, String branchid, String environment) {
        displayLog("VerificationTokenTask called");

        this.verificationCallBack = verificationCallBack;
        this.context = context;
        this.environment = environment;
        this.dataProvider = new DataProvider(context);
        this.applicationkey = applicationkey;
        this.branchid = branchid;
        this.postDataParams.put("user-id", branchid);

        try{
            /*
            jsonObject = new JSONObject();
            JSONArray transitsArray = new JSONArray();
            JSONObject actualObject = new JSONObject();
            JSONArray idsArray = new JSONArray();
            idsArray.put(parseObject.get("ids"));
            actualObject.put("ids", idsArray);
            actualObject.put("transition_date", System.currentTimeMillis());
            actualObject.put("transition_event", parseObject.get("transition_event"));
            actualObject.put("device_os_name", parseObject.get("device_os_name"));
            actualObject.put("device_os_version", parseObject.get("device_os_version"));
            actualObject.put("geo_point_source", parseObject.get("geo_point_source"));
            actualObject.put("geopoint_provider", parseObject.get("geopoint_provider"));
            actualObject.put("gps_accuracy", parseObject.get("gps_accuracy"));
            actualObject.put("geo_point", parseObject.get("geo_point"));
            transitsArray.put(actualObject);
            jsonObject.put("transits", transitsArray);
            */

        }
        catch (Exception e){
            displayLog("error puting song object "+e.toString());
        }


        /*
        try {
            if (parseObject.containsKey("ids")) {
                postDataParams.put("ids", parseObject.get("ids"));
                displayLog("uniqueId" + " " + parameters.get("uniqueId"));
            }

        } catch (Exception e) {
            displayLog("customName error " + e.toString());
        }
        try {
            if (parameters.containsKey("customName")) {
                postDataParams.put("customName", parameters.get("customName"));
                displayLog("customName" + " " + parameters.get("customName"));
            }

        } catch (Exception e) {
            displayLog("customName error " + e.toString());
        }
        try {
            if (parameters.containsKey("phoneNumber")) {
                postDataParams.put("phoneNumber", parameters.get("phoneNumber"));
                displayLog("phoneNumber" + " " + parameters.get("phoneNumber"));
            }
        } catch (Exception e) {
            displayLog("phoneNumber  error " + e.toString());
        }
        try {
            if (parameters.containsKey("message")) {
                String msg = parameters.get("message");
                postDataParams.put("message", msg);
                displayLog("message" + " " + msg);
            }
        } catch (Exception e) {
            displayLog("message  error " + e.toString());
        }
        try {
            if (parameters.containsKey("phone")) {
                postDataParams.put("phone", parameters.get("phone"));
                displayLog("phone" + " " + parameters.get("phone"));
            }
        } catch (Exception e) {
            displayLog("phone  error " + e.toString());
        }
        try {
            if (parameters.containsKey("clientName")) {
                postDataParams.put("clientName", parameters.get("clientName"));
                displayLog("clientName" + " " + parameters.get("clientName"));
            }
        } catch (Exception e) {
            displayLog("clientName error " + e.toString());
        }

        try {

            if (parameters.containsKey(io.okverify.android.utilities.Constants.COLUMN_PHONECUSTOMER)) {
                postDataParams.put(io.okverify.android.utilities.Constants.COLUMN_PHONECUSTOMER, parameters.get(io.okverify.android.utilities.Constants.COLUMN_PHONECUSTOMER));
                displayLog(io.okverify.android.utilities.Constants.COLUMN_PHONECUSTOMER + " @@@ " + parameters.get(io.okverify.android.utilities.Constants.COLUMN_PHONECUSTOMER));
            } else {
                displayLog("we have no phonecustomer @@@ ");
            }

        } catch (Exception e) {
            displayLog(io.okverify.android.utilities.Constants.COLUMN_PHONECUSTOMER + " @@@ error " + e.toString());
        }

        try {
            if (parameters.containsKey("userId")) {
                postDataParams.put("userId", parameters.get("userId"));
                displayLog("userId " + parameters.get("userId"));
            }
        } catch (Exception e) {
            displayLog("userId error " + e.toString());
        }
        try {
            if (parameters.containsKey("sessionToken")) {
                postDataParams.put("sessionToken", parameters.get("sessionToken"));
                displayLog("sessionToken " + parameters.get("sessionToken"));
            }
        } catch (Exception e) {
            displayLog("sessionToken error " + e.toString());
        }
        try {
            if (parameters.containsKey(io.okverify.android.utilities.Constants.COLUMN_AFFILIATION)) {
                postDataParams.put(io.okverify.android.utilities.Constants.COLUMN_AFFILIATION, parameters.get(io.okverify.android.utilities.Constants.COLUMN_AFFILIATION));
                displayLog(io.okverify.android.utilities.Constants.COLUMN_AFFILIATION + " " + parameters.get(io.okverify.android.utilities.Constants.COLUMN_AFFILIATION));
            }
        } catch (Exception e) {
            displayLog("affiliation error " + e.toString());
        }
        try {

            if (parameters.containsKey(io.okverify.android.utilities.Constants.COLUMN_BRANCH)) {
                postDataParams.put(io.okverify.android.utilities.Constants.COLUMN_BRANCH, parameters.get(io.okverify.android.utilities.Constants.COLUMN_BRANCH));
                displayLog(io.okverify.android.utilities.Constants.COLUMN_BRANCH + " " + parameters.get(io.okverify.android.utilities.Constants.COLUMN_BRANCH));
            }

        } catch (Exception e) {
            displayLog("branch error " + e.toString());
        }
        try {
            if (parameters.containsKey("businessName")) {
                postDataParams.put("businessName", parameters.get("businessName"));
                displayLog("businessName" + " " + parameters.get("businessName"));
            }
        } catch (Exception e) {
            displayLog("businessName error " + e.toString());
        }
        try {

            if (parameters.containsKey("traditionalBuildingName")) {
                postDataParams.put("traditionalBuildingName", parameters.get("traditionalBuildingName"));
                displayLog("traditionalBuildingName" + " " + parameters.get("traditionalBuildingName"));
            }

        } catch (Exception e) {
            displayLog("traditionalBuildingName error " + e.toString());
        }
        try {
            if (parameters.containsKey("traditionalBuildingNumber")) {
                postDataParams.put("traditionalBuildingNumber", parameters.get("traditionalBuildingNumber"));
                displayLog("traditionalBuildingNumber" + " " + parameters.get("traditionalBuildingNumber"));
            }
        } catch (Exception e) {
            displayLog("traditionalBuildingNumber error " + e.toString());
        }
        try {

            if (parameters.containsKey("unit")) {
                postDataParams.put("unit", parameters.get("unit"));
                displayLog("unit" + " " + parameters.get("unit"));
            }

        } catch (Exception e) {
            displayLog("unit error " + e.toString());
        }
        try {
            if (parameters.containsKey("floor")) {
                postDataParams.put("floor", parameters.get("floor"));
                displayLog("floor" + " " + parameters.get("floor"));
            }

        } catch (Exception e) {
            displayLog("floor error " + e.toString());
        }
        try {
            if (parameters.containsKey("addressType")) {
                postDataParams.put("addressType", parameters.get("addressType"));
                displayLog("addressType" + " " + parameters.get("addressType"));
            }
        } catch (Exception e) {
            displayLog("floor error " + e.toString());
        }
        try {

            if (parameters.containsKey("ualId")) {
                postDataParams.put("ualId", parameters.get("ualId"));
                displayLog("@@@ ualId" + " " + parameters.get("ualId"));
            } else {
                displayLog("@@@ we have no ual id");
            }

        } catch (Exception e) {
            displayLog("@@@ claimUALId error " + e.toString());
        }
        try {

            if (parameters.containsKey("claimUALId")) {
                postDataParams.put("claimUALId", parameters.get("claimUALId"));
                displayLog("@@@ claimUALId" + " " + parameters.get("claimUALId"));
            } else {
                displayLog("@@@ we have no ual id");
            }

        } catch (Exception e) {
            displayLog("@@@ claimUALId error " + e.toString());
        }
        try {
            postDataParams.put("productVersion", BuildConfig.VERSION_NAME);
            postDataParams.put("product", io.okverify.android.utilities.Constants.product);
            postDataParams.put("appType", "android");
        } catch (Exception e) {
            displayLog("error adding three params " + e.toString());
        }
        */

    }

    @Override
    protected String doInBackground(Void... params) {
        String results = "";

        try {
            String appId, restApi, urlString;

            if (environment.equalsIgnoreCase("PROD")) {
                appId = io.okverify.android.utilities.Constants.PROD_APPLICATION_ID;
                restApi = io.okverify.android.utilities.Constants.PROD_REST_KEY;
                urlString = "https://okhi.back4app.io/send-sms";
            } else if (environment.equalsIgnoreCase("DEVMASTER")) {
                appId = io.okverify.android.utilities.Constants.DEVMASTER_APPLICATION_ID;
                restApi = io.okverify.android.utilities.Constants.DEVMASTER_REST_KEY;
                urlString = "https://okhicore-development-master.back4app.com/send-sms";
            } else if (environment.equalsIgnoreCase("SANDBOX")) {
                appId = io.okverify.android.utilities.Constants.SANDBOX_APPLICATION_ID;
                restApi = io.okverify.android.utilities.Constants.SANDBOX_REST_KEY;
                displayLog("sandbox "+branchid);
                urlString = "https://dev-api.okhi.io/v5/auth/verification-token?user-id="+branchid;
            } else {
                appId = io.okverify.android.utilities.Constants.PROD_APPLICATION_ID;
                restApi = io.okverify.android.utilities.Constants.PROD_REST_KEY;
                urlString = "https://okhi.back4app.io/send-sms";
            }

            OkHttpClient.Builder b = new OkHttpClient.Builder();
            b.connectTimeout(15, TimeUnit.SECONDS);
            b.readTimeout(15, TimeUnit.SECONDS);
            b.writeTimeout(15, TimeUnit.SECONDS);

            OkHttpClient client = b.build();

            // Initialize Builder (not RequestBody)
            FormBody.Builder builder = new FormBody.Builder();

            // Add Params to Builder
            for (Map.Entry<String, Object> entry : postDataParams.entrySet()) {
                try {
                    builder.add(entry.getKey(), (String) entry.getValue());
                } catch (Exception e) {
                    displayLog(" builder.add error  " + e.toString());
                }
            }

            RequestBody requestBody = builder.build();

            Request request = new Request.Builder()
                    .url(urlString)
                    .get()
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

        displayLog("response code " + responseCode + " onPostExecute result " + result);
        //displayLog(OkVerifyApplication.getUniqueId());


        if ((200 <= responseCode) && (responseCode < 300)) {

            verificationCallBack.querycomplete(result, true);
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
            verificationCallBack.querycomplete(result, false);
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
            OkAnalytics okAnalytics = new OkAnalytics(context, environment);
            okAnalytics.sendToAnalytics(parameters, loans, environment);
        } catch (Exception e) {
            displayLog("error sending photoexpanded analytics event " + e.toString());
        }
    }

    private void displayLog(String log) {
        Log.i(TAG, log);
    }
}
