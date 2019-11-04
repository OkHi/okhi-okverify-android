package io.okheart.android.utilities;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

public class Constants {

    public static final String ANALYTICS_WRITE_KEY_PROD_OMTM = "peBXJt77v4yE3eI23IKFIMUAw6paI6UH";
    public static final String ANALYTICS_WRITE_KEY_DEV_OMTM = "peBXJt77v4yE3eI23IKFIMUAw6paI6UH";
    //public static final String ANALYTICS_WRITE_KEY_PROD_OMTM = "z5jGk4usT5Y5kCwLHItPIq0PIdcIYXG9";
    //public static final String ANALYTICS_WRITE_KEY_DEV_OMTM = "GTb0hGQWisE7BmxmkVyUBHBk22CDgxoB";
    public static final String DEV_KEY = "r:b59a93ba7d80a95d89dff8e4c52e259a";
    public static final String REMOTE_BACKGROUND_LOCATION_FREQUENCY = "background_frequency";
    public static final String REMOTE_ADDRESS_FREQUENCY_THRESHOLD = "address_frequency";
    public static final String REMOTE_SMS_TEMPLATE = "sms_template";
    public static final String REMOTE_GEOSEARCH_RADIUS = "geosearch_radius";
    public static final String REMOTE_GPS_ACCURACY = "gps_accuracy";
    public static final String REMOTE_PING_FREQUENCY = "ping_frequency";
    public static final String REMOTE_RESUME_PING_FREQUENCY = "resume_ping_frequency";
    public static final String REMOTE_KILL_SWITCH = "kill_switch";
    public static final String REMOTE_AUTO_STOP = "auto_stop";
    public static final String COLUMN_PHONECUSTOMER = "phonecustomer";
    public static final String COLUMN_IMAGEURL = "imageurl";
    public static final String COLUMN_DIRECTION = "traditionalAddress";
    public static final String COLUMN_LAT = "latitude";
    public static final String COLUMN_LNG = "longitude";
    public static final String COLUMN_PROPERTYNUMBER = "propertynumber";
    public static final String COLUMN_PROPERTYNAME = "propertyname";
    public static final String COLUMN_CUSTOMERNAME = "clientName";
    public static final String COLUMN_CLAIMUALID = "ualId";
    public static final String COLUMN_AFFILIATION = "affiliation";
    public static final String COLUMN_BRANCH = "branch";
    public static final String COLUMN_LOCATIONNAME = "locationname";
    public static final String COLUMN_LOCATIONNICKNAME = "locationnickname";
    public static final String COLUMN_STREETNAME = "streetname";
    public static final String COLUMN_ACCURACY = "accuracy";
    public static final String COLUMN_TRADITIONALSTREETNAME = "traditionalstreetname";
    public static final String COLUMN_TRADITIONALSTREETNUMBER = "traditionalstreetnumber";
    public static final String COLUMN_TRADITIONALBUILDINGNAME = "traditionalbuildingname";
    public static final String COLUMN_TRADITIONALBUILDINGNUMBER = "traditionalbuildingnumber";
    public static final String COLUMN_BUSINESSNAME = "businessname";
    public static final String COLUMN_TOTHEDOOR = "tothedoor";
    public static final String COLUMN_UALUPDATED = "ualupdated";
    public static final String COLUMN_ADDRESSTYPE = "addresstype";
    public static final String COLUMN_STREETNUMBER = "streetnumber";
    //public static final String COLUMN_CREATEDATTIME = "createdattime";
    public static final String COLUMN_INTERNAL_ADDRESSTYPE = "internalAddressType";
    public static final String COLUMN_CUSTOMERUSERID = "customeruserid";
    public static final String COLUMN_ISNEWUSER = "isnewuser";
    public static final String COLUMN_ISEMPTYUAL = "isemptyual";
    public static final String COLUMN_CLAIMAFLID = "aflId";
    public static final String COLUMN_PROPERTY = "property";
    public static final String COLUMN_VALUE = "value";
    public static final String COLUMN_FLOOR = "floor";
    public static final String COLUMN_DELIVERY_NOTES = "deliverynotes";
    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_ISODDRESS = "isoddress";
    public static final String COLUMN_UNIT = "unit";
    public static final String COLUMN_ROUTE = "route";
    public static final String COLUMN_ADDRESSFREQUENCY = "addressfrequency";
    public static final String COLUMN_CREATEDON = "createdon";
    public static final String COLUMN_LASTUSED = "lastused";

    public static final String TABLE_NAME_RUNLIST = "artcafferunlist";
    public static final String TABLE_NAME_STUFF = "artcaffestuff";

    public static final String appLayer = "client";
    public static final String product = "okHeartAndroidSDK";
    public static final String formFactor = "mobile";
    public static final String appType = "native";
    public static final String librarytrackerwaybill = "OkAnalytics.java";
    public static final String versiontrackerwaybill = "2.0.0";
    public static final String ANALYTICS_WRITE_KEY = "1WVXBz8WdoFA29enq6zH0dQ33c3rxzPQ";
    public static final String DEVMASTER_APPLICATION_ID = "39qx0cn6q1IZM7XCA0H2uYstqwLazABGupEUTMg0";
    public static final String DEVMASTER_REST_KEY = "RZPpMBEsL9S6rfcTAyXEp623AxZwXxQeHXFAJT4R";
    public static final String DEVMASTER_CLIENT_ID = "2s23x3Bsqhattvq2VzPNJwnNCbpoja2DkoN2v4OR";


    public static String getUTCtimestamp() {
        TimeZone tz = TimeZone.getTimeZone("UTC");
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm'Z'");
        df.setTimeZone(tz);
        String nowAsISO = df.format(new Date());
        ////Log.i("Constants",nowAsISO);

        return nowAsISO;
    }
}
