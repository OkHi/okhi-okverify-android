package io.okheart.android.activity;

import androidx.appcompat.app.AppCompatActivity;

public class TestActivity extends AppCompatActivity {
    /*

    private static final int MY_PERMISSIONS_ACCESS_FINE_LOCATION = 83;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);

        OkHi.initialize("r:b59a93ba7d80a95d89dff8e4c52e259a", true);

        String firstname = "Ramogi";
        String lastname = "Ochola";
        String phonenumber = "+254713567907";

        OkHiCallback okHiCallback = new OkHiCallback() {
            @Override
            public void querycomplete(JSONObject result) {
                displayLog("result "+result);

                String message = result.optString("message");
                displayLog(message);

                if(message.equalsIgnoreCase("fatal_exit")){
                    JSONObject payload = result.optJSONObject("payload");
                    displayLog("payload "+payload.toString());
                    int errorCode = payload.optInt("errorCode", 0);
                    displayLog(""+errorCode);
                    if(errorCode == -1){
                        OkHi.requestPermission(TestActivity.this, MY_PERMISSIONS_ACCESS_FINE_LOCATION);
                    }

                }

            }
        };

        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("firstName", firstname);
            jsonObject.put("lastName", lastname);
            jsonObject.put("phone", phonenumber);
            OkHi.displayClient(okHiCallback, jsonObject);
        } catch (JSONException e) {
            displayLog("json exception error " + e.toString());
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_ACCESS_FINE_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                displayLog("onrequest permission");
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                    displayLog("accepted location permissions");

                } else {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    displayLog("denied location permission");

                }
                return;
            }
        }
    }


    private void displayLog(String logs){
        Log.i("TestActivity", logs);
    }
    */
}
