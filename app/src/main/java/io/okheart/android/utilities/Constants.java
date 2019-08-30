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


    public static String getUTCtimestamp() {
        TimeZone tz = TimeZone.getTimeZone("UTC");
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm'Z'");
        df.setTimeZone(tz);
        String nowAsISO = df.format(new Date());
        ////Log.i("Constants",nowAsISO);

        return nowAsISO;
    }
}
