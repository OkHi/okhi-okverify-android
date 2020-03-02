package io.okheart.android.asynctask;

import android.content.ContentValues;
import android.content.Context;
import android.os.AsyncTask;
import android.provider.Settings;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import io.okheart.android.callback.GetAddressCallBack;
import io.okheart.android.datamodel.OrderItem;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;

import static io.okheart.android.utilities.Constants.DEV1_APPLICATION_ID;
import static io.okheart.android.utilities.Constants.DEV1_REST_KEY;
import static io.okheart.android.utilities.Constants.DEV1_USERID;
import static io.okheart.android.utilities.Constants.DEV3_APPLICATION_ID;
import static io.okheart.android.utilities.Constants.DEV3_REST_KEY;
import static io.okheart.android.utilities.Constants.DEV3_USERID;
import static io.okheart.android.utilities.Constants.DEV4_APPLICATION_ID;
import static io.okheart.android.utilities.Constants.DEV4_REST_KEY;
import static io.okheart.android.utilities.Constants.DEV4_USERID;
import static io.okheart.android.utilities.Constants.DEVMASTER_APPLICATION_ID;
import static io.okheart.android.utilities.Constants.DEVMASTER_REST_KEY;
import static io.okheart.android.utilities.Constants.PROD_APPLICATION_ID;
import static io.okheart.android.utilities.Constants.PROD_REST_KEY;
import static io.okheart.android.utilities.Constants.SANDBOX_APPLICATION_ID;
import static io.okheart.android.utilities.Constants.SANDBOX_REST_KEY;
import static io.okheart.android.utilities.Constants.product;

/**
 * Created by ramogiochola on 6/6/16.
 */
public class GetAddressTask extends AsyncTask<Void, Void, String> {
    private static final String TAG = "GetAddressTask";
    private GetAddressCallBack getAddressCallBack;
    private HashMap<String, String> postDataParams = new HashMap<>();
    private String phoneNumber;
    private int responseCode;
    private io.okheart.android.database.DataProvider dataProvider;
    private Context context;
    private String firstName, ualId, environment, uniqueId;

    public GetAddressTask(Context context, GetAddressCallBack getAddressCallBack) {

        this.context = context;
        this.dataProvider = new io.okheart.android.database.DataProvider(context);
        this.getAddressCallBack = getAddressCallBack;
        String tempphonenumber = dataProvider.getPropertyValue("phonenumber");
        if ((tempphonenumber.startsWith("07")) && (tempphonenumber.length() == 10)) {
            this.phoneNumber = "+2547" + tempphonenumber.substring(2);
        } else {
            this.phoneNumber = tempphonenumber;
        }
        this.environment = dataProvider.getPropertyValue("environment");
        uniqueId = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
        postDataParams.put("phone", phoneNumber);
        postDataParams.put("branch", "hq_okhi");
        postDataParams.put("affiliation", "okhi");
        try {
            postDataParams.put("product", product);
            postDataParams.put("appType", "android");
            postDataParams.put("productVersion", io.okheart.android.BuildConfig.VERSION_NAME);
        } catch (Exception e) {
            displayLog("error " + e.toString());
        }

    }

    @Override
    protected String doInBackground(Void... params) {
        String results = "";
        try {
            String appId, restApi, urlString;
            switch (environment.toLowerCase()) {
                case "prod":
                    postDataParams.put("userId", "GrlaR3LHUP");
                    postDataParams.put("sessionToken", "r:177ccbe18102a4cb078c09759a6ee421");
                    appId = PROD_APPLICATION_ID;
                    restApi = PROD_REST_KEY;
                    urlString = "https://okhi.back4app.io/getAddressByPhone_forHD";
                    break;
                case "sandbox":
                    postDataParams.put("userId", "GrlaR3LHUP");
                    postDataParams.put("sessionToken", "r:985e5e735e002e7d6efd93a5c59ad095");
                    appId = SANDBOX_APPLICATION_ID;
                    restApi = SANDBOX_REST_KEY;
                    urlString = "https://okhi-sbox.back4app.io/getAddressByPhone_forHD";
                    break;
                case "devmaster":
                    postDataParams.put("userId", "GrlaR3LHUP");
                    postDataParams.put("sessionToken", "r:51dbfa56dc9990128739ab9b24c64d67");
                    appId = DEVMASTER_APPLICATION_ID;
                    restApi = DEVMASTER_REST_KEY;
                    urlString = "https://okhicore-development-master.back4app.com/getAddressByPhone_forHD";
                    break;
                case "dev1":
                    postDataParams.put("userId", DEV1_USERID);
                    appId = DEV1_APPLICATION_ID;
                    restApi = DEV1_REST_KEY;
                    urlString = "https://okhi-d1.back4app.io/getAddressByPhone_forHD";
                    break;
                case "dev3":
                    postDataParams.put("userId", DEV3_USERID);
                    appId = DEV3_APPLICATION_ID;
                    restApi = DEV3_REST_KEY;
                    urlString = "https://okhicore-development-3.back4app.com/getAddressByPhone_forHD";
                    break;
                case "dev4":
                    postDataParams.put("userId", DEV4_USERID);
                    appId = DEV4_APPLICATION_ID;
                    restApi = DEV4_REST_KEY;
                    urlString = "https://okhi-d4.back4app.io/getAddressByPhone_forHD";
                    break;
                default:
                    postDataParams.put("userId", "GrlaR3LHUP");
                    postDataParams.put("sessionToken", "r:177ccbe18102a4cb078c09759a6ee421");
                    appId = PROD_APPLICATION_ID;
                    restApi = PROD_REST_KEY;
                    urlString = "https://okhi.back4app.io/getAddressByPhone_forHD";
                    break;
            }

            OkHttpClient.Builder b = new OkHttpClient.Builder();
            b.connectTimeout(15, TimeUnit.SECONDS);
            b.writeTimeout(15, TimeUnit.SECONDS);
            b.readTimeout(15, TimeUnit.SECONDS);

            OkHttpClient client = b.build();

            // Initialize Builder (not RequestBody)
            FormBody.Builder builder = new FormBody.Builder();

            // Add Params to Builder
            for (Map.Entry<String, String> entry : postDataParams.entrySet()) {
                builder.add(entry.getKey(), entry.getValue());
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
            displayLog("getAddressByPhone_forHD unsupported encoding exception " + e.toString());
        } catch (IOException io) {
            displayLog("getAddressByPhone_forHD io exception " + io.toString());
        } catch (IllegalArgumentException iae) {
            displayLog("getAddressByPhone_forHD illegal argument exception " + iae.toString());
        }


        return results;
    }

    @Override
    protected void onPostExecute(String result) {
        displayLog(result);
        if ((200 <= responseCode) && (responseCode < 300)) {
            try {
                updateDatabase(result);
            } catch (Exception e) {
                displayLog("update database error " + e.toString());
            }

            displayLog(result);
            getAddressCallBack.querycomplete(result, true);
        } else {
            displayLog(result);
            getAddressCallBack.querycomplete(result, false);
        }
    }

    private void updateDatabase(String response) {

        try {
            JSONObject address = new JSONObject(response);
            JSONArray result = address.getJSONArray("result");


            if (result.length() == 0) {


            } else {

                displayLog("result length " + result.length());
                int loopAddress;
                if (result.length() > 10) {
                    loopAddress = 10;
                } else {
                    loopAddress = result.length();
                }

                for (int i = 0; i < loopAddress; i++) {

                    try {
                        OrderItem orderItem = new OrderItem();
                        orderItem.setCustomerphone(phoneNumber);
                        JSONObject addressObject = (JSONObject) result.get(i);
                        firstName = addressObject.optString("firstName");
                        JSONObject ual = addressObject.optJSONObject("UAL");
                        JSONObject afl = addressObject.optJSONObject("AFL");
                        orderItem.setCustomername(firstName);
                        if (ual != null) {
                            try {

                                String locationNickname = ual.optString("locationNickname");
                                String directionsFromClosestLandmark = ual.optString("directionsFromClosestLandmark");
                                String toTheDoor = ual.optString("toTheDoor");
                                String businessName = ual.optString("businessName");
                                String traditionalStreetName = ual.optString("traditionalStreetName");
                                String traditionalStreetNumber = ual.optString("traditionalStreetNumber");
                                String traditionalBuildingName = ual.optString("traditionalBuildingName");
                                String addressObjectId = ual.optString("addressObjectId");
                                Double latitude = ual.optDouble("latitude");
                                Double longitude = ual.optDouble("longitude");
                                String route = ual.optString("route");
                                String propertyName = ual.optString("propertyName");
                                String propertyNumber = ual.optString("propertyNumber");
                                String street_name = ual.optString("street_name");
                                String location_name = ual.optString("location_name");
                                ualId = ual.optString("objectId");
                                if (ualId.equalsIgnoreCase("sbWyOaBmk7")) {
                                    try {

                                        displayLog("********** ************");
                                        displayLog(ualId + " address ual " + ual.toString());
                                    } catch (Exception e1) {
                                        displayLog("error displaying result " + e1.toString());
                                    }
                                }
                                String customName = ual.optString("dd");
                                String floor = ual.optString("floor");
                                String deliveryNotes = ual.optString("deliveryNotes");
                                String unit = ual.optString("unit");
                                String gatePhotoUrlSmall = null;

                                orderItem.setDeliveryNotes(deliveryNotes);
/*
                                displayLog("firstName "+firstName);
                                displayLog("phonenumber "+phoneNumber);
                                displayLog("locationNickname "+locationNickname);
                                displayLog("route "+route);
                                displayLog("streetname "+street_name);
                                displayLog("locationname "+location_name);
                                displayLog("latitude "+latitude);
                                displayLog("longitude "+longitude);
                                displayLog("propertynumber "+propertyNumber);
                                displayLog("directionsFromClosestLandmark "+directionsFromClosestLandmark);
                                displayLog("tothedoor "+toTheDoor);
                                displayLog("businessname "+businessName);
                                displayLog("traditionalStreetName "+traditionalStreetName);
                                displayLog("propertyname "+propertyName);
                                displayLog("traditionalBuildingName "+traditionalBuildingName);
                                displayLog("addressObjectId "+addressObjectId);
                                displayLog("traditionalStreetNumber "+traditionalStreetNumber);
                                displayLog("gatePhotoUrlSmall "+gatePhotoUrlSmall);
                                displayLog("unit "+unit);
                                displayLog("deliveryNotes "+deliveryNotes);
                                displayLog("isOddress "+orderItem.getIsoddress());
*/
                                if (afl != null) {
                                    displayLog("afl is not null " + afl.toString());
                                    if (ualId.equalsIgnoreCase("sbWyOaBmk7")) {
                                        try {

                                            displayLog("********** ************");
                                            displayLog(ualId + " address afl 1 " + afl.toString());
                                        } catch (Exception e1) {
                                            displayLog("error displaying result " + e1.toString());
                                        }
                                    }
                                    try {
                                        if (street_name != null) {
                                            if (street_name.length() > 0) {

                                            } else {
                                                street_name = afl.optString("streetName");
                                            }
                                        } else {
                                            street_name = afl.optString("streetName");
                                        }
                                        if (directionsFromClosestLandmark != null) {
                                            if (directionsFromClosestLandmark.length() > 0) {

                                            } else {
                                                directionsFromClosestLandmark = afl.optString("directionsFromClosestLandmark");
                                            }
                                        } else {
                                            directionsFromClosestLandmark = afl.optString("directionsFromClosestLandmark");
                                        }

                                        if (toTheDoor != null) {
                                            if (toTheDoor.length() > 0) {

                                            } else {
                                                toTheDoor = afl.optString("toTheDoor");
                                            }
                                        } else {
                                            toTheDoor = afl.optString("toTheDoor");
                                        }
                                        if (businessName != null) {
                                            if (businessName.length() > 0) {

                                            } else {
                                                businessName = afl.optString("businessName");
                                            }
                                        } else {
                                            businessName = afl.optString("businessName");
                                        }
                                        if (traditionalStreetName != null) {
                                            if (traditionalStreetName.length() > 0) {

                                            } else {
                                                traditionalStreetName = afl.optString("traditionalStreetName");
                                            }
                                        } else {
                                            traditionalStreetName = afl.optString("traditionalStreetName");
                                        }

                                        if (traditionalStreetNumber != null) {
                                            if (traditionalStreetNumber.length() > 0) {

                                            } else {
                                                traditionalStreetNumber = afl.optString("traditionalStreetNumber");
                                            }
                                        } else {
                                            traditionalStreetNumber = afl.optString("traditionalStreetNumber");
                                        }

                                        if (traditionalBuildingName != null) {
                                            if (traditionalBuildingName.length() > 0) {

                                            } else {
                                                traditionalBuildingName = afl.optString("traditionalBuildingName");
                                            }
                                        } else {
                                            traditionalBuildingName = afl.optString("traditionalBuildingName");
                                        }

                                        if (addressObjectId != null) {
                                            if (addressObjectId.length() > 0) {

                                            } else {
                                                addressObjectId = afl.optString("addressObjectId");
                                            }
                                        } else {
                                            addressObjectId = afl.optString("addressObjectId");
                                        }

                                        try {
                                            Double afllatitude = afl.optDouble("latitude");
                                            if (afllatitude.isNaN()) {
                                                displayLog("1 afl latitude " + latitude);
                                            } else {
                                                latitude = afllatitude;
                                                orderItem.setLat(latitude);
                                                displayLog("1 afl latitude " + latitude);
                                            }
                                            Double afllongitude = afl.optDouble("longitude");
                                            if (afllongitude.isNaN()) {
                                                displayLog("1 afl longitude " + longitude);
                                            } else {
                                                longitude = afllongitude;
                                                orderItem.setLng(longitude);
                                                displayLog("2 afl longitude " + longitude);
                                            }
                                        } catch (Exception e1) {
                                            displayLog("lat lng error " + e1.toString());
                                        }
                                        String aflRoute = afl.optString("route");
                                        if (aflRoute != null) {
                                            if (aflRoute.length() > 0) {
                                                route = aflRoute;
                                            }
                                        }
                                        displayLog("propertyName " + propertyName);
                                        String aflPropertyName = afl.optString("propertyName");
                                        if (aflPropertyName != null) {
                                            if (aflPropertyName.length() > 0) {
                                                propertyName = aflPropertyName;
                                            }
                                        }
                                        displayLog("afl property name " + propertyName);
                                        String aflPropertyNumber = afl.optString("propertyNumber");
                                        if (aflPropertyNumber != null) {
                                            if (aflPropertyNumber.length() > 0) {
                                                propertyNumber = aflPropertyNumber;
                                            }
                                        }

                                        String aflStreetName = afl.optString("streetName");
                                        if (aflStreetName != null) {
                                            if (!(aflStreetName.length() > 0)) {
                                                street_name = aflStreetName;
                                            }
                                        }
                                    } catch (Exception e) {
                                        displayLog("error getting afl values " + e.toString());
                                    }
                                } else {
                                    afl = ual.optJSONObject("AFL");
                                    if (afl != null) {
                                        if (street_name != null) {
                                            if (street_name.length() > 0) {

                                            } else {
                                                street_name = afl.optString("streetName");
                                            }
                                        } else {
                                            street_name = afl.optString("streetName");
                                        }
                                        if (ualId.equalsIgnoreCase("sbWyOaBmk7")) {
                                            try {

                                                displayLog("********** ************");
                                                displayLog(ualId + " address afl 2 " + afl.toString());
                                            } catch (Exception e1) {
                                                displayLog("error displaying result " + e1.toString());
                                            }
                                        }
                                        try {
                                            if (directionsFromClosestLandmark != null) {
                                                if (directionsFromClosestLandmark.length() > 0) {

                                                } else {
                                                    directionsFromClosestLandmark = afl.optString("directionsFromClosestLandmark");
                                                }
                                            } else {
                                                directionsFromClosestLandmark = afl.optString("directionsFromClosestLandmark");
                                            }

                                            if (toTheDoor != null) {
                                                if (toTheDoor.length() > 0) {

                                                } else {
                                                    toTheDoor = afl.optString("toTheDoor");
                                                }
                                            } else {
                                                toTheDoor = afl.optString("toTheDoor");
                                            }
                                            if (businessName != null) {
                                                if (businessName.length() > 0) {

                                                } else {
                                                    businessName = afl.optString("businessName");
                                                }
                                            } else {
                                                businessName = afl.optString("businessName");
                                            }
                                            if (traditionalStreetName != null) {
                                                if (traditionalStreetName.length() > 0) {

                                                } else {
                                                    traditionalStreetName = afl.optString("traditionalStreetName");
                                                }
                                            } else {
                                                traditionalStreetName = afl.optString("traditionalStreetName");
                                            }

                                            if (traditionalStreetNumber != null) {
                                                if (traditionalStreetNumber.length() > 0) {

                                                } else {
                                                    traditionalStreetNumber = afl.optString("traditionalStreetNumber");
                                                }
                                            } else {
                                                traditionalStreetNumber = afl.optString("traditionalStreetNumber");
                                            }

                                            if (traditionalBuildingName != null) {
                                                if (traditionalBuildingName.length() > 0) {

                                                } else {
                                                    traditionalBuildingName = afl.optString("traditionalBuildingName");
                                                }
                                            } else {
                                                traditionalBuildingName = afl.optString("traditionalBuildingName");
                                            }

                                            if (addressObjectId != null) {
                                                if (addressObjectId.length() > 0) {

                                                } else {
                                                    addressObjectId = afl.optString("addressObjectId");
                                                }
                                            } else {
                                                addressObjectId = afl.optString("addressObjectId");
                                            }

                                            try {
                                                Double afllatitude = afl.optDouble("latitude");
                                                if (afllatitude.isNaN()) {
                                                    displayLog("1 afl latitude " + latitude);
                                                } else {
                                                    latitude = afllatitude;
                                                    orderItem.setLat(latitude);
                                                    displayLog("1 afl latitude " + latitude);
                                                }
                                                Double afllongitude = afl.optDouble("longitude");
                                                if (afllongitude.isNaN()) {
                                                    displayLog("1 afl longitude " + longitude);
                                                } else {
                                                    longitude = afllongitude;
                                                    orderItem.setLng(longitude);
                                                    displayLog("2 afl longitude " + longitude);

                                                }
                                            } catch (Exception e1) {
                                                displayLog("lat lng error " + e1.toString());
                                            }
                                            String aflRoute = afl.optString("route");
                                            if (aflRoute != null) {
                                                if (aflRoute.length() > 0) {
                                                    displayLog("aflroute " + aflRoute);
                                                    route = aflRoute;
                                                }
                                            }
                                            String aflPropertyName = afl.optString("propertyName");
                                            if (aflPropertyName != null) {
                                                if (aflPropertyName.length() > 0) {
                                                    propertyName = aflPropertyName;
                                                }
                                            }
                                            String aflPropertyNumber = afl.optString("propertyNumber");
                                            if (aflPropertyNumber != null) {
                                                if (aflPropertyNumber.length() > 0) {
                                                    propertyNumber = aflPropertyNumber;
                                                }
                                            }

                                            String aflStreetName = afl.optString("streetName");
                                            if (aflStreetName != null) {
                                                if (!(aflStreetName.length() > 0)) {
                                                    street_name = aflStreetName;
                                                }
                                            }
                                        } catch (Exception e) {
                                            displayLog("error getting afl values " + e.toString());
                                        }
                                    }
                                }

                                try {

                                    if (propertyNumber != null) {
                                        if (propertyNumber.length() > 0) {
                                            orderItem.setPropertynumber(propertyNumber);
                                        }
                                    }
                                    if (propertyName != null) {
                                        if (propertyName.length() > 0) {
                                            orderItem.setPropertyname(propertyName);
                                        }
                                    }
                                    if (unit != null) {
                                        if (unit.length() > 0) {
                                            orderItem.setUnit(unit);
                                        }
                                    }
                                    if (route != null) {
                                        if (route.length() > 0) {
                                            orderItem.setRoute(route);
                                        }
                                    }
                                    if (floor != null) {
                                        if (floor.length() > 0) {
                                            orderItem.setFloor(floor);
                                        }
                                    }
                                    if (ualId != null) {
                                        if (ualId.length() > 0) {
                                            orderItem.setClaimualid(ualId);
                                        }
                                    }
                                    if (locationNickname != null) {
                                        if (locationNickname.length() > 0) {
                                            orderItem.setLocationNickName(locationNickname);
                                        }
                                    }
                                    if (directionsFromClosestLandmark != null) {
                                        if (directionsFromClosestLandmark.length() > 0) {
                                            orderItem.setDirection(directionsFromClosestLandmark);
                                        }
                                    }
                                    if (toTheDoor != null) {
                                        if (toTheDoor.length() > 0) {
                                            orderItem.setToTheDoor(toTheDoor);
                                        }
                                    }

                                    if (businessName != null) {
                                        if (businessName.length() > 0) {
                                            orderItem.setBusinessName(businessName);
                                        }
                                    }

                                    if (traditionalStreetName != null) {
                                        if (traditionalStreetName.length() > 0) {
                                            orderItem.setTraditionalStreetName(traditionalStreetName);
                                        }
                                    }
                                    if (toTheDoor != null) {
                                        if (toTheDoor.length() > 0) {
                                            orderItem.setToTheDoor(toTheDoor);
                                        }
                                    }

                                    if (street_name != null) {
                                        if (street_name.length() > 0) {
                                            orderItem.setStreetName(street_name);
                                        }
                                    }
                                    if (traditionalBuildingName != null) {
                                        if (traditionalBuildingName.length() > 0) {
                                            orderItem.setTraditionalBuildingName(traditionalBuildingName);
                                        }
                                    }
                                    if (traditionalStreetNumber != null) {
                                        if (traditionalStreetNumber.length() > 0) {
                                            orderItem.setTraditionalStreetNumber(traditionalStreetNumber);
                                        }
                                    }



                                    /*
                                    displayLog("firstName "+firstName);
                                    displayLog("phonenumber "+phoneNumber);
                                    displayLog("locationNickname "+locationNickname);
                                    displayLog("route "+route);
                                    displayLog("streetname "+street_name);
                                    displayLog("locationname "+location_name);
                                    displayLog("latitude "+latitude);
                                    displayLog("longitude "+longitude);
                                    displayLog("propertynumber "+propertyNumber);
                                    displayLog("directionsFromClosestLandmark "+directionsFromClosestLandmark);
                                    displayLog("tothedoor "+toTheDoor);
                                    displayLog("businessname "+businessName);
                                    displayLog("traditionalStreetName "+traditionalStreetName);
                                    displayLog("propertyname "+propertyName);
                                    displayLog("traditionalBuildingName "+traditionalBuildingName);
                                    displayLog("addressObjectId "+addressObjectId);
                                    displayLog("traditionalStreetNumber "+traditionalStreetNumber);
                                    displayLog("gatePhotoUrlSmall "+gatePhotoUrlSmall);
                                    displayLog("unit "+unit);
                                    displayLog("isOddress "+orderItem.getIsoddress());
                                    */
                                    //if(orderItem.getClaimualid().equalsIgnoreCase("qjpGnIjgJb")) {
                                    /*
                                        displayLog("ualId " + orderItem.getClaimualid());
                                        displayLog("firstName " + orderItem.getCustomername());
                                        displayLog("phonenumber " + orderItem.getCustomerphone());
                                        displayLog("locationNickname " + orderItem.getLocationNickName());
                                        displayLog("route " + orderItem.getRoute());
                                        displayLog("streetname " + orderItem.getStreetName());
                                        displayLog("locationname " + orderItem.getLocationNickName());
                                        displayLog("latitude " + orderItem.getLat());
                                        displayLog("longitude " + orderItem.getLng());
                                        displayLog("propertynumber " + orderItem.getPropertynumber());
                                        displayLog("directionsFromClosestLandmark " + orderItem.getDirection());
                                        displayLog("tothedoor " + orderItem.getToTheDoor());
                                        displayLog("businessname " + orderItem.getBusinessName());
                                        displayLog("traditionalStreetName " + orderItem.getTraditionalStreetName());
                                        displayLog("propertyname " + orderItem.getPropertyname());
                                        displayLog("traditionalBuildingName " + orderItem.getTraditionalBuildingName());
                                        displayLog("traditionalStreetNumber " + orderItem.getTraditionalStreetNumber());
                                        displayLog("imageUrl " + orderItem.getImageUrl());
                                        displayLog("unit " + orderItem.getUnit());
                                        displayLog("direction " + orderItem.getDirection());
                                        displayLog("isOddress " + orderItem.getIsoddress());
                                        displayLog("lat " + orderItem.getLat());
                                        displayLog("lng " + orderItem.getLng());
                                        */
                                    //}
                                    if (orderItem.getLat() != null) {
                                        if (orderItem.getLat() != 0.0) {
                                            ContentValues contentValues = new ContentValues();
                                            contentValues.put(io.okheart.android.utilities.Constants.COLUMN_CUSTOMERNAME, firstName);
                                            contentValues.put(io.okheart.android.utilities.Constants.COLUMN_PHONECUSTOMER, phoneNumber);
                                            contentValues.put(io.okheart.android.utilities.Constants.COLUMN_ROUTE, orderItem.getRoute());
                                            contentValues.put(io.okheart.android.utilities.Constants.COLUMN_STREETNAME, orderItem.getStreetName());
                                            contentValues.put(io.okheart.android.utilities.Constants.COLUMN_PROPERTYNAME, orderItem.getPropertyname());
                                            contentValues.put(io.okheart.android.utilities.Constants.COLUMN_DIRECTION, orderItem.getDirection());
                                            contentValues.put(io.okheart.android.utilities.Constants.COLUMN_LOCATIONNICKNAME, orderItem.getLocationNickName());
                                            contentValues.put(io.okheart.android.utilities.Constants.COLUMN_CLAIMUALID, ualId);
                                            contentValues.put(io.okheart.android.utilities.Constants.COLUMN_IMAGEURL, "");
                                            contentValues.put(io.okheart.android.utilities.Constants.COLUMN_LOCATIONNAME, orderItem.getLocationNickName());
                                            contentValues.put(io.okheart.android.utilities.Constants.COLUMN_BRANCH, "hq_okhi");
                                            contentValues.put(io.okheart.android.utilities.Constants.COLUMN_LAT, orderItem.getLat());
                                            contentValues.put(io.okheart.android.utilities.Constants.COLUMN_LNG, orderItem.getLng());
                                            contentValues.put(io.okheart.android.utilities.Constants.COLUMN_UNIQUEID, uniqueId);

                                            Long y = dataProvider.insertAddressList(contentValues);
                                            displayLog("inserted address " + y);
                                        }
                                    }
                                } catch (Exception e) {
                                    displayLog("Error inserting address " + e.toString());
                                }
                            } catch (Exception e) {
                                displayLog("error getting ual values " + e.toString());
                            }

                        } else {
                            displayLog("outside ual");
                        }
                    } catch (Exception e) {
                        displayLog("for loop error " + e.toString());
                    }
                    displayLog("************ ********************");
                }
            }
        } catch (JSONException e) {
            displayLog("Json object error " + e.toString());

        }
    }

    private void displayLog(String log) {
        //Log.i(TAG, log);
    }
}



