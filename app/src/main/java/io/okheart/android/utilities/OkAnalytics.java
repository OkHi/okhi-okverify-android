package io.okheart.android.utilities;

import android.content.Context;
import android.os.Build;
import android.provider.Settings;

import com.segment.analytics.Analytics;
import com.segment.analytics.Properties;

import java.util.HashMap;


/**
 * Created by ramogiochola on 6/21/16.
 */
public class OkAnalytics {

    private static final String TAG = "OkAnalytics";
    private Context context;
    private Properties eventProperties;
    private String environment;
    private String type;
    private String subtype;
    private String onObject;
    private String timestamp;
    private String userId;
    private String userRoles;
    private String userAffiliation;
    private String orderUid;
    private String customerCode;
    private String orderId;
    private Boolean isOddress;
    //private String appLayer;
    //private String product;
    private String productVersion;
    private Properties event;
    private String linkedEventId;
    private Properties contextdeviceandproduct;
    private Properties db;
    private Properties waybill;
    private Properties trackerEtc;

    private Properties communication;
    private String channelEventCommunication;
    private String thirdParty;
    private String name;
    private String direction;
    private String duration;
    private String subject;
    private String body;
    private Properties submit;

    private String view;
    private Properties pageUrl;
    private String pageurlraw;
    private String pageurlparsed;
    private Properties referrer;
    private String referrerraw;
    private String referrerparsed;
    private String pageLoadToken;
    private String cookieToken;
    private Properties ip;
    private String ipraw;
    private String ipparsed;
    private Properties userAgent;
    private String userAgentraw;
    private String userAgentparsed;
    private Properties device;
    //private String formFactor;
    //private String appType;
    private Properties screen;
    private String heightScreen;
    private String widthScreen;
    private String model;

    private Properties checkout;
    private Properties ual;
    private Properties afl;
    private Properties ualOwner;
    private Properties overlord;
    private Properties overlordCustomerInfo;
    private Properties userDB;
    private Properties customer;
    private Properties customerInfo;

    private Properties trackerWaybill;
    //private String libraryTrackerWaybill;
    //private String versionTrackerWaybill;
    private Properties logstash;
    private String confFile;
    private String version;


    private String anonymousId;
    private String channelTrackerEtc;
    private Properties contextTrackerEtc;
    private Properties libraryTrackerEtcContext;
    private String nameLibraryTrackerEtcContext;
    private String versionLibraryTrackerEtcContext;
    private String uniqueId;

    /*
    public OkAnalytics(String omtm) {
        context = OkVerifyApplication.getContext();
        //eventProperties = new Properties();
        //initializeStaticParameters();
        //initializeProperties();
        //populateProperties();
    }
    */

    public OkAnalytics(Context context, String environment) {
        this.context = context;
        uniqueId = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
        try {
            //WindowManager wm = (WindowManager) context.getSystemService(WINDOW_SERVICE);
            //DisplayMetrics dm = new DisplayMetrics();
            //wm.getDefaultDisplay().getMetrics(dm);
            //setScreenwidth(dm.widthPixels);
            //setScreenheight(dm.heightPixels);
        } catch (Exception e) {
            displayLog("Error initializing stuff " + e.toString());
        }
        eventProperties = new Properties();
        initializeStaticParameters(environment);
        initializeProperties();
        populateProperties();
    }

    public static String getDeviceModelAndBrand() {
        String manufacturer = Build.MANUFACTURER;
        String model = Build.MODEL;
        if (model.contains(manufacturer)) {
            return model;
        } else {
            return manufacturer + " " + model;
        }
    }

    public void initializeStaticParameters(String environment) {

        try {
            setEnvironment(environment);
            /*
            if(productionVersion){
                setEnvironment("PROD");
            }
            else if(DEVMASTER){
                setEnvironment("DEVMASTER");
            }else if(SANDBOX){
                setEnvironment( "SANDBOX");
            }
            else{
                setEnvironment( "PROD");
            }

            if(environment.equalsIgnoreCase("PROD")){
                trackjson.put("environment", "PROD");
            }
            else if(environment.equalsIgnoreCase("DEVMASTER")){
                trackjson.put("environment", "DEVMASTER");
            }else if(environment.equalsIgnoreCase("SANDBOX")){
                trackjson.put("environment", "SANDBOX");
            }
            else{
                trackjson.put("environment", "PROD");
            }
            */
        } catch (Exception e) {
            displayLog(" getenvironment error " + e.toString());
        }

        try {
            //setUserRoles(OkVerifyApplication.getUserRoles());
        } catch (Exception e) {
            displayLog(" getuserroles error " + e.toString());
        }
        try {
            setUserAffiliation("acme");
            setCustomerCode("acme");
        } catch (Exception e) {
            displayLog(" getaffiliation error " + e.toString());
        }
        /*
        try {
            setAppLayer(appLayer);
        } catch (Exception e) {
            displayLog(" applayer error " + e.toString());
        }
        try {
            setProduct(product);
        } catch (Exception e) {
            displayLog(" product error " + e.toString());
        }
        */
        try {
            setProductVersion(io.okheart.android.BuildConfig.VERSION_NAME);
        } catch (Exception e) {
            displayLog(" getVersion error " + e.toString());
        }
        try {
            setUserAgentraw(Build.VERSION.RELEASE);
        } catch (Exception e) {
            displayLog(" getDeviceAndroidVersion error " + e.toString());
        }
        /*
        try {
            setFormFactor(formFactor);
        } catch (Exception e) {
            displayLog(" formfactor error " + e.toString());
        }
        */
        /*
        try {
            setAppType(appType);
        } catch (Exception e) {
            displayLog(" apptype error " + e.toString());
        }
        */
        try {
            //setHeightScreen("" + OkVerifyApplication.getScreenheight());
        } catch (Exception e) {
            displayLog(" screen height error " + e.toString());
        }
        try {
            //setWidthScreen("" + OkVerifyApplication.getScreenwidth());
        } catch (Exception e) {
            displayLog(" screen width error " + e.toString());
        }
        try {
            setModel(getDeviceModelAndBrand());
        } catch (Exception e) {
            displayLog(" getDeviceModelAndBrand error " + e.toString());
        }
        /*
        try {
            setLibraryTrackerWaybill(Constants.librarytrackerwaybill);
        } catch (Exception e) {
            displayLog(" librarytrackerwaybill error " + e.toString());
        }
        try {
            setVersionTrackerWaybill(Constants.versiontrackerwaybill);
        } catch (Exception e) {
            displayLog(" versiontrackerwaybill error " + e.toString());
        }
        */
        try {
            //setUserId(OkVerifyApplication.getLoginuserid());
        } catch (Exception e) {
            displayLog(" ParseUser.getCurrentUser().getObjectId() error " + e.toString());
        }
    }

    public void initializeProperties() {

        event = new Properties();
        contextdeviceandproduct = new Properties();
        db = new Properties();
        waybill = new Properties();
        trackerEtc = new Properties();
        communication = new Properties();
        submit = new Properties();
        pageUrl = new Properties();
        referrer = new Properties();
        ip = new Properties();
        userAgent = new Properties();
        device = new Properties();
        screen = new Properties();
        checkout = new Properties();
        ual = new Properties();
        afl = new Properties();
        ualOwner = new Properties();
        overlord = new Properties();
        overlordCustomerInfo = new Properties();
        userDB = new Properties();
        customer = new Properties();
        customerInfo = new Properties();
        trackerWaybill = new Properties();
        logstash = new Properties();
        contextTrackerEtc = new Properties();
        libraryTrackerEtcContext = new Properties();

    }

    public void populateProperties() {
        try {
            screen.putValue("height", getHeightScreen());
        } catch (Exception e) {
            displayLog(" height error " + e.toString());
        }
        try {
            screen.putValue("width", getWidthScreen());
        } catch (Exception e) {
            displayLog(" width error " + e.toString());
        }
        try {
            device.putValue("formFactor", io.okheart.android.utilities.Constants.formFactor);
        } catch (Exception e) {
            displayLog(" formFactor error " + e.toString());
        }
        try {
            device.putValue("appType", io.okheart.android.utilities.Constants.appType);
        } catch (Exception e) {
            displayLog(" appType error " + e.toString());
        }
        try {
            device.putValue("screen", screen);
        } catch (Exception e) {
            displayLog(" versiontrackerwaybill error " + e.toString());
        }
        try {
            device.putValue("model", model);
        } catch (Exception e) {
            displayLog(" versiontrackerwaybill error " + e.toString());
        }
        try {
            contextdeviceandproduct.putValue("cookieToken", uniqueId);
        } catch (Exception e) {
            displayLog(" versiontrackerwaybill error " + e.toString());
        }

        try {
            contextdeviceandproduct.putValue("device", device);
        } catch (Exception e) {
            displayLog(" versiontrackerwaybill error " + e.toString());
        }
        try {
            trackerWaybill.putValue("library", io.okheart.android.utilities.Constants.librarytrackerwaybill);
        } catch (Exception e) {
            displayLog(" versiontrackerwaybill error " + e.toString());
        }
        try {
            trackerWaybill.putValue("version", io.okheart.android.utilities.Constants.versiontrackerwaybill);
        } catch (Exception e) {
            displayLog(" trackerWaybill error " + e.toString());
        }
        try {
            waybill.putValue("tracker", getTrackerWaybill());
        } catch (Exception e) {
            displayLog(" waybill error " + e.toString());
        }


    }

    public void initializeDynamicParameters(String type, String subtype, String onObject, String view, String orderId, HashMap<String, String> hashMap) {
        setType(type);
        setSubtype(subtype);
        setOnObject(onObject);
        setView(view);
        setOrderId(orderId);
        setSubmit(hashMap);
    }

    public void setCommunication(String channel, String thirdParty, String name, String direction,
                                 String duration, String subject, String body) {
        getCommunication().clear();

        getCommunication().putValue("channel", channel);
        getCommunication().putValue("thirdParty", thirdParty);
        getCommunication().putValue("name", name);
        getCommunication().putValue("direction", direction);
        getCommunication().putValue("duration", duration);
        getCommunication().putValue("subject", subject);
        getCommunication().putValue("body", body);
        getEvent().putValue("communication", getCommunication());
    }

    public void sendToAnalytics(String branch, String deliveryId, String userId, String userAffiliation, String environment) {

        try {
            contextdeviceandproduct.putValue("view", getView());
        } catch (Exception e) {
            displayLog(" contextdeviceandproduct error " + e.toString());
        }
        try {
            eventProperties.putValue("timestamp", Constants.getUTCtimestamp());
        } catch (Exception e) {
            displayLog(" Constants.getUTCtimestamp() error " + e.toString());
        }
        eventProperties.putValue("deliveryId", deliveryId);
        eventProperties.putValue("environment", environment);
        /*
        if(productionVersion){
            eventProperties.putValue("environment", "PROD");
        }
        else if(DEVMASTER){
            eventProperties.putValue("environment", "DEVMASTER");
        }else if(SANDBOX){
            eventProperties.putValue("environment", "SANDBOX");
        }
        else{
            eventProperties.putValue("environment", "PROD");
        }
        String environment = dataProvider.getPropertyValue("environment");
        if(environment != null){
            if(environment.length() > 0){
                if(environment.equalsIgnoreCase("PROD")){
                    trackjson.put("environment", "PROD");
                }
                else if(environment.equalsIgnoreCase("DEVMASTER")){
                    trackjson.put("environment", "DEVMASTER");
                }else if(environment.equalsIgnoreCase("SANDBOX")){
                    trackjson.put("environment", "SANDBOX");
                }
                else{
                    trackjson.put("environment", "PROD");
                }
            }
            else{
                trackjson.put("environment", "PROD");
            }
        }
        else{
            trackjson.put("environment", "PROD");
        }
        */
        eventProperties.putValue("type", type);
        eventProperties.putValue("subtype", subtype);
        eventProperties.putValue("onObject", onObject);
        eventProperties.putValue("userId", userId);
        eventProperties.putValue("userRoles", userRoles);
        eventProperties.putValue("userAffiliation", userAffiliation);
        eventProperties.putValue("branch", branch);
        eventProperties.putValue("customerCode", customerCode);
        eventProperties.putValue("orderId", orderId);
        //getEvent().putValue("submit", getSubmit());
        eventProperties.putValue("event", getEvent());
        //displayLog("event string " + getEvent().toString());
        eventProperties.putValue("appLayer", io.okheart.android.utilities.Constants.appLayer);
        eventProperties.putValue("product", io.okheart.android.utilities.Constants.product);
        eventProperties.putValue("productVersion", productVersion);
        eventProperties.putValue("context", contextdeviceandproduct);
        eventProperties.putValue("waybill", waybill);
        //Options options = new Options();
        //options.setIntegration(Options.ALL_INTEGRATIONS_KEY,false);
        //options.setIntegration("OkAnalytics DEV",true);
        //Analytics analytics = new Analytics.Builder(context, ANALYTICS_WRITE_KEY).tag("devomtmindex").build();
        //Analytics.setSingletonInstance(null);
        //Analytics.setSingletonInstance(analytics);
        //analytics.with(context).track("okAnalyticsEvent", eventProperties);
        Analytics.with(context).track("okAnalyticsEvent", eventProperties);

        //getCommunication().clear();
        //getSubmit().clear();
    }

    public void sendToAnalytics(String branch, String deliveryId, String userId, String userAffiliation,
                                String eventName, String isOddress, String isOkAppUser, String pageurlraw, String lookupId, String error, String environment) {
        pageUrl.putValue("raw", pageurlraw);

        try {
            contextdeviceandproduct.putValue("view", getView());
        } catch (Exception e) {
            displayLog(" contextdeviceandproduct error " + e.toString());
        }
        try {
            eventProperties.putValue("timestamp", Constants.getUTCtimestamp());
        } catch (Exception e) {
            displayLog(" Constants.getUTCtimestamp() error " + e.toString());
        }
        eventProperties.putValue("error", error);
        eventProperties.putValue("lookupId", lookupId);
        eventProperties.putValue("isOkAppUser", isOkAppUser);
        eventProperties.putValue("isOddress", isOddress);
        eventProperties.putValue("deliveryId", deliveryId);
        eventProperties.putValue("environment", environment);
        /*
        if(productionVersion){
            eventProperties.putValue("environment", "PROD");
        }
        else if(DEVMASTER){
            eventProperties.putValue("environment", "DEVMASTER");
        }else if(SANDBOX){
            eventProperties.putValue("environment", "SANDBOX");
        }
        else{
            eventProperties.putValue("environment", "PROD");
        }
        */
        eventProperties.putValue("type", type);
        eventProperties.putValue("subtype", subtype);
        eventProperties.putValue("onObject", onObject);
        eventProperties.putValue("userId", userId);
        eventProperties.putValue("userRoles", userRoles);
        eventProperties.putValue("userAffiliation", userAffiliation);
        eventProperties.putValue("branch", branch);
        eventProperties.putValue("customerCode", customerCode);
        eventProperties.putValue("orderId", orderId);
        //getEvent().putValue("submit", getSubmit());
        eventProperties.putValue("event", getEvent());
        //displayLog("event string " + getEvent().toString());
        eventProperties.putValue("appLayer", io.okheart.android.utilities.Constants.appLayer);
        eventProperties.putValue("product", io.okheart.android.utilities.Constants.product);
        eventProperties.putValue("productVersion", productVersion);
        eventProperties.putValue("clientProduct", io.okheart.android.utilities.Constants.product);
        eventProperties.putValue("clientProductVersion", productVersion);
        eventProperties.putValue("context", contextdeviceandproduct);
        eventProperties.putValue("waybill", waybill);
        eventProperties.putValue("pageUrl", pageUrl);
        //Options options = new Options();
        //options.setIntegration(Options.ALL_INTEGRATIONS_KEY,false);
        //options.setIntegration("OkAnalytics DEV",true);
        //Analytics analytics = new Analytics.Builder(context, ANALYTICS_WRITE_KEY).tag("devomtmindex").build();
        //Analytics.setSingletonInstance(null);
        //Analytics.setSingletonInstance(analytics);
        Analytics.with(context).track(eventName, eventProperties);
        //OkVerifyApplication.getAnalytics().with(context).track(eventName, eventProperties);
        //getCommunication().clear();
        //getSubmit().clear();
    }

    public void sendToAnalytics(HashMap<String, String> parameters, HashMap<String, String> loans, String environment) {

        try {
            setSubmit(loans);
        } catch (Exception e) {
            displayLog(" setSubmit error " + e.toString());
        }
        try {
            if (parameters.containsKey("view")) {
                setView(parameters.get("view"));
            }
            contextdeviceandproduct.putValue("view", getView());
        } catch (Exception e) {
            displayLog(" contextdeviceandproduct error " + e.toString());
        }
        try {
            eventProperties.putValue("timestamp", Constants.getUTCtimestamp());
        } catch (Exception e) {
            displayLog(" Constants.getUTCtimestamp() error " + e.toString());
        }
        if (parameters.containsKey("address")) {
            eventProperties.putValue("address", parameters.get("address"));
        }
        if (parameters.containsKey("latitude")) {
            eventProperties.putValue("latitude", parameters.get("latitude"));
        }
        if (parameters.containsKey("longitude")) {
            eventProperties.putValue("longitude", parameters.get("longitude"));
        }
        if (parameters.containsKey("gpsAccuracy")) {
            eventProperties.putValue("gpsAccuracy", parameters.get("gpsAccuracy"));
        }
        if (parameters.containsKey("address")) {
            eventProperties.putValue("address", parameters.get("address"));
        }
        if (parameters.containsKey("verified")) {
            eventProperties.putValue("verified", parameters.get("verified"));
        }
        if (parameters.containsKey("distance")) {
            eventProperties.putValue("distance", parameters.get("distance"));
        }
        if (parameters.containsKey("batteryLevel")) {
            eventProperties.putValue("batteryLevel", parameters.get("batteryLevel"));
        }
        if (parameters.containsKey("isPlugged")) {
            eventProperties.putValue("isPlugged", parameters.get("isPlugged"));
        }
        if (parameters.containsKey("isCharging")) {
            eventProperties.putValue("isCharging", parameters.get("isCharging"));
        }
        if (parameters.containsKey("usbCharge")) {
            eventProperties.putValue("usbCharge", parameters.get("usbCharge"));
        }
        if (parameters.containsKey("acCharge")) {
            eventProperties.putValue("acCharge", parameters.get("acCharge"));
        }
        if (parameters.containsKey("remoteGPSAccuracy")) {
            eventProperties.putValue("remoteGPSAccuracy", parameters.get("remoteGPSAccuracy"));
        }
        if (parameters.containsKey("uniqueId")) {
            eventProperties.putValue("uniqueId", parameters.get("uniqueId"));
        }
        if (parameters.containsKey("phone")) {
            eventProperties.putValue("phone", parameters.get("phone"));
            eventProperties.putValue("phonenumber", parameters.get("phonenumber"));
        }
        if (parameters.containsKey("phonenumber")) {
            eventProperties.putValue("phone", parameters.get("phone"));
            eventProperties.putValue("phonenumber", parameters.get("phonenumber"));
        }
        if (parameters.containsKey("appKey")) {
            eventProperties.putValue("appKey", parameters.get("appKey"));
        }
        if (parameters.containsKey("verify")) {
            eventProperties.putValue("verify", parameters.get("verify"));
        }


        if (parameters.containsKey("error")) {
            eventProperties.putValue("error", parameters.get("error"));
        }
        if (parameters.containsKey("shortUrl")) {
            eventProperties.putValue("lookupId", parameters.get("shortUrl"));
        }
        if (parameters.containsKey("isOkAppUser")) {
            eventProperties.putValue("isOkAppUser", parameters.get("isOkAppUser"));
        }
        if (parameters.containsKey("isOddress")) {
            eventProperties.putValue("isOddress", parameters.get("isOddress"));
        }
        if (parameters.containsKey("deliveryId")) {
            eventProperties.putValue("deliveryId", parameters.get("deliveryId"));
        }
        if (parameters.containsKey("branch")) {
            eventProperties.putValue("branch", parameters.get("branch"));
        }
        if (parameters.containsKey("addressUrl")) {
            pageUrl.putValue("raw", parameters.get("addressUrl"));
        }
        if (parameters.containsKey("expanded")) {
            eventProperties.putValue("expanded", parameters.get("expanded"));
        }
        if (parameters.containsKey("ualId")) {
            eventProperties.putValue("ualId", parameters.get("ualId"));
        }
        if (parameters.containsKey("type")) {
            eventProperties.putValue("type", parameters.get("type"));
        }
        if (parameters.containsKey("subtype")) {
            eventProperties.putValue("subtype", parameters.get("subtype"));
        }
        if (parameters.containsKey("onObject")) {
            eventProperties.putValue("onObject", parameters.get("onObject"));
        }
        if (parameters.containsKey("orderId")) {
            eventProperties.putValue("orderId", parameters.get("orderId"));
        }
        if (parameters.containsKey("secondLine")) {
            eventProperties.putValue("secondLine", parameters.get("secondLine"));
        }
        if (parameters.containsKey("firstLine")) {
            eventProperties.putValue("firstLine", parameters.get("firstLine"));
        }
        if (parameters.containsKey("minimized")) {
            eventProperties.putValue("minimized", parameters.get("minimized"));
        }
        if (parameters.containsKey("label")) {
            eventProperties.putValue("label", parameters.get("label"));
        }
        if (parameters.containsKey("appKey")) {
            eventProperties.putValue("appKey", parameters.get("appKey"));
        }
        try {
            if (parameters.containsKey("location")) {
                eventProperties.put("location", parameters.get("location"));
            }

        } catch (Exception e) {
            displayLog("error setting geohash location " + e.toString());
        }
        try {
            if (parameters.containsKey("userAffiliation")) {
                eventProperties.put("userAffiliation", parameters.get("userAffiliation"));
            }

        } catch (Exception e) {
            displayLog("error getting userAffiliation " + e.toString());
        }
        eventProperties.putValue("userId", userId);
        eventProperties.putValue("environment", environment);
        /*
        if(productionVersion){
            eventProperties.putValue("environment", "PROD");
        }
        else if(DEVMASTER){
            eventProperties.putValue("environment", "DEVMASTER");
        }else if(SANDBOX){
            eventProperties.putValue("environment", "SANDBOX");
        }
        else{
            eventProperties.putValue("environment", "PROD");
        }
        */
        eventProperties.putValue("userRoles", userRoles);
        eventProperties.putValue("customerCode", customerCode);
        eventProperties.putValue("event", getEvent());
        eventProperties.putValue("appLayer", io.okheart.android.utilities.Constants.appLayer);
        eventProperties.putValue("product", io.okheart.android.utilities.Constants.product);
        eventProperties.putValue("productVersion", productVersion);
        eventProperties.putValue("clientProduct", io.okheart.android.utilities.Constants.product);
        eventProperties.putValue("clientProductVersion", productVersion);
        eventProperties.putValue("context", contextdeviceandproduct);
        eventProperties.putValue("waybill", waybill);
        eventProperties.putValue("pageUrl", pageUrl);
        //Options options = new Options();
        //options.setIntegration(Options.ALL_INTEGRATIONS_KEY,false);
        //options.setIntegration("OkAnalytics DEV",true);
        //Analytics analytics = new Analytics.Builder(context, ANALYTICS_WRITE_KEY).tag("devomtmindex").build();
        //Analytics.setSingletonInstance(null);
        //Analytics.setSingletonInstance(analytics);
        //displayLog("before sending");
        Analytics.with(context).track(parameters.get("eventName"), eventProperties);
        //OkVerifyApplication.getAnalytics().with(context).track(parameters.get("eventName"), eventProperties);
        //getCommunication().clear();
        //getSubmit().clear();
        //displayLog("after sending");
    }


    /*
    public void sendToOMTM(Context context1, String event, Traits traits, Properties eventproperties2) {

        displayLog("send to omtm");
        //OkVerifyApplication.getAnalytics_omtm().with(context).identify(traits);
        //OkVerifyApplication.getAnalytics_omtm().with(context).track( event, eventproperties2);
        try {

            Options options = new Options();
            options.setIntegration(Options.ALL_INTEGRATIONS_KEY,false);
            options.setIntegration("OMTM_v2_DEV",true);
            //analytics_omtm.with(context).identify(traits);

            Analytics analytics = new Analytics.Builder(context, ANALYTICS_WRITE_KEY_DEV_OMTM).tag("devomtmindex").build();
            Analytics.setSingletonInstance(null);
            Analytics.setSingletonInstance(analytics);
            analytics.with(context).track( event, eventproperties2);

            //OkVerifyApplication.getAnalytics().with(context).track( event, eventproperties2);
        } catch (Exception e) {
            displayLog("Error initializing analytics_omtm " + e.toString());
        }

    }
    */

    public void setGeoHash(String location) {
        try {
            eventProperties.put("location", location);
        } catch (Exception e) {
            displayLog("error setting geohash location " + e.toString());
        }
    }

    public Context getContext() {
        return context;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    public Properties getEventProperties() {
        return eventProperties;
    }

    public void setEventProperties(Properties eventProperties) {
        this.eventProperties = eventProperties;
    }

    public String getEnvironment() {
        return environment;
    }

    public void setEnvironment(String environment) {
        this.environment = environment;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getSubtype() {
        return subtype;
    }

    public void setSubtype(String subtype) {
        this.subtype = subtype;
    }

    public String getOnObject() {
        return onObject;
    }

    public void setOnObject(String onObject) {
        this.onObject = onObject;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserRoles() {
        return userRoles;
    }

    public void setUserRoles(String userRoles) {
        this.userRoles = userRoles;
    }

    public String getUserAffiliation() {
        return userAffiliation;
    }

    public void setUserAffiliation(String userAffiliation) {
        this.userAffiliation = userAffiliation;
        setCustomerCode(userAffiliation);
    }

    public String getOrderUid() {
        return orderUid;
    }

    public void setOrderUid(String orderUid) {
        this.orderUid = orderUid;
    }

    public String getCustomerCode() {
        return customerCode;
    }

    public void setCustomerCode(String customerCode) {
        this.customerCode = customerCode;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public Boolean getOddress() {
        return isOddress;
    }

    public void setOddress(Boolean oddress) {
        isOddress = oddress;
    }

    /*
        public String getAppLayer() {
            return appLayer;
        }

        public void setAppLayer(String appLayer) {
            this.appLayer = appLayer;
        }

        public String getProduct() {
            return product;
        }

        public void setProduct(String product) {
            this.product = product;
        }
    */
    public String getProductVersion() {
        return productVersion;
    }

    public void setProductVersion(String productVersion) {
        this.productVersion = productVersion;
    }

    public Properties getEvent() {
        return event;
    }

    public void setEvent(Properties event) {
        this.event = event;
    }

    public String getLinkedEventId() {
        return linkedEventId;
    }

    public void setLinkedEventId(String linkedEventId) {
        this.linkedEventId = linkedEventId;
    }

    public Properties getContextdeviceandproduct() {
        return contextdeviceandproduct;
    }

    public void setContextdeviceandproduct(Properties contextdeviceandproduct) {
        this.contextdeviceandproduct = contextdeviceandproduct;
    }

    public Properties getDb() {
        return db;
    }

    public void setDb(Properties db) {
        this.db = db;
    }

    public Properties getWaybill() {
        return waybill;
    }

    public void setWaybill(Properties waybill) {
        this.waybill = waybill;
    }

    public Properties getTrackerEtc() {
        return trackerEtc;
    }

    public void setTrackerEtc(Properties trackerEtc) {
        this.trackerEtc = trackerEtc;
    }

    public Properties getCommunication() {
        return communication;
    }

    public void setCommunication(Properties communication) {
        this.communication = communication;
    }

    public String getChannelEventCommunication() {
        return channelEventCommunication;
    }

    public void setChannelEventCommunication(String channelEventCommunication) {
        this.channelEventCommunication = channelEventCommunication;
    }

    public String getThirdParty() {
        return thirdParty;
    }

    public void setThirdParty(String thirdParty) {
        this.thirdParty = thirdParty;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDirection() {
        return direction;
    }

    public void setDirection(String direction) {
        this.direction = direction;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public Properties getSubmit() {
        return submit;
    }

    public void setSubmit(HashMap<String, String> hashMap) {

        getSubmit().clear();
        try {
            getSubmit().putValue("SDK", "" + Build.VERSION.SDK_INT);
        } catch (Exception e) {
            displayLog("sdk submit error " + e.toString());
        }

        try {
            displayLog(" hashmap ");
            for (String key : hashMap.keySet()) {
                getSubmit().putValue(key, hashMap.get(key));
            }
            getEvent().putValue("submit", getSubmit());
        } catch (Exception e) {
            displayLog(" submission error " + e.toString());
        }
    }

    public void setSubmit(Properties submit) {
        this.submit = submit;
    }

    public String getView() {
        return view;
    }

    public void setView(String view) {
        this.view = view;
    }

    public Properties getPageUrl() {
        return pageUrl;
    }

    public void setPageUrl(Properties pageUrl) {
        this.pageUrl = pageUrl;
    }

    public String getPageurlraw() {
        return pageurlraw;
    }

    public void setPageurlraw(String pageurlraw) {
        this.pageurlraw = pageurlraw;
    }

    public String getPageurlparsed() {
        return pageurlparsed;
    }

    public void setPageurlparsed(String pageurlparsed) {
        this.pageurlparsed = pageurlparsed;
    }

    public Properties getReferrer() {
        return referrer;
    }

    public void setReferrer(Properties referrer) {
        this.referrer = referrer;
    }

    public String getReferrerraw() {
        return referrerraw;
    }

    public void setReferrerraw(String referrerraw) {
        this.referrerraw = referrerraw;
    }

    public String getReferrerparsed() {
        return referrerparsed;
    }

    public void setReferrerparsed(String referrerparsed) {
        this.referrerparsed = referrerparsed;
    }

    public String getPageLoadToken() {
        return pageLoadToken;
    }

    public void setPageLoadToken(String pageLoadToken) {
        this.pageLoadToken = pageLoadToken;
    }

    public String getCookieToken() {
        return cookieToken;
    }

    public void setCookieToken(String cookieToken) {
        this.cookieToken = cookieToken;
    }

    public Properties getIp() {
        return ip;
    }

    public void setIp(Properties ip) {
        this.ip = ip;
    }

    public String getIpraw() {
        return ipraw;
    }

    public void setIpraw(String ipraw) {
        this.ipraw = ipraw;
    }

    public String getIpparsed() {
        return ipparsed;
    }

    public void setIpparsed(String ipparsed) {
        this.ipparsed = ipparsed;
    }

    public Properties getUserAgent() {
        return userAgent;
    }

    public void setUserAgent(Properties userAgent) {
        this.userAgent = userAgent;
    }

    public String getUserAgentraw() {
        return userAgentraw;
    }

    public void setUserAgentraw(String userAgentraw) {
        this.userAgentraw = userAgentraw;
    }

    public String getUserAgentparsed() {
        return userAgentparsed;
    }

    public void setUserAgentparsed(String userAgentparsed) {
        this.userAgentparsed = userAgentparsed;
    }

    public Properties getDevice() {
        return device;
    }

    public void setDevice(Properties device) {
        this.device = device;
    }

    /*
        public String getFormFactor() {
            return formFactor;
        }

        public void setFormFactor(String formFactor) {
            this.formFactor = formFactor;
        }
    */
/*
    public String getAppType() {
        return appType;
    }

    public void setAppType(String appType) {
        this.appType = appType;
    }
*/
    public Properties getScreen() {
        return screen;
    }

    public void setScreen(Properties screen) {
        this.screen = screen;
    }

    public String getHeightScreen() {
        return heightScreen;
    }

    public void setHeightScreen(String heightScreen) {
        this.heightScreen = heightScreen;
    }

    public String getWidthScreen() {
        return widthScreen;
    }

    public void setWidthScreen(String widthScreen) {
        this.widthScreen = widthScreen;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public Properties getCheckout() {
        return checkout;
    }

    public void setCheckout(Properties checkout) {
        this.checkout = checkout;
    }

    public Properties getUal() {
        return ual;
    }

    public void setUal(Properties ual) {
        this.ual = ual;
    }

    public Properties getAfl() {
        return afl;
    }

    public void setAfl(Properties afl) {
        this.afl = afl;
    }

    public Properties getUalOwner() {
        return ualOwner;
    }

    public void setUalOwner(Properties ualOwner) {
        this.ualOwner = ualOwner;
    }

    public Properties getOverlord() {
        return overlord;
    }

    public void setOverlord(Properties overlord) {
        this.overlord = overlord;
    }

    public Properties getOverlordCustomerInfo() {
        return overlordCustomerInfo;
    }

    public void setOverlordCustomerInfo(Properties overlordCustomerInfo) {
        this.overlordCustomerInfo = overlordCustomerInfo;
    }

    public Properties getUserDB() {
        return userDB;
    }

    public void setUserDB(Properties userDB) {
        this.userDB = userDB;
    }

    public Properties getCustomer() {
        return customer;
    }

    public void setCustomer(Properties customer) {
        this.customer = customer;
    }

    public Properties getCustomerInfo() {
        return customerInfo;
    }

    public void setCustomerInfo(Properties customerInfo) {
        this.customerInfo = customerInfo;
    }

    public Properties getTrackerWaybill() {
        return trackerWaybill;
    }

    /*
    public void setTrackerWaybill(Properties trackerWaybill) {
        this.trackerWaybill = trackerWaybill;
    }

    public String getLibraryTrackerWaybill() {
        return libraryTrackerWaybill;
    }

    public void setLibraryTrackerWaybill(String libraryTrackerWaybill) {
        this.libraryTrackerWaybill = libraryTrackerWaybill;
    }

    public String getVersionTrackerWaybill() {
        return versionTrackerWaybill;
    }

    public void setVersionTrackerWaybill(String versionTrackerWaybill) {
        this.versionTrackerWaybill = versionTrackerWaybill;
    }
*/
    public Properties getLogstash() {
        return logstash;
    }

    public void setLogstash(Properties logstash) {
        this.logstash = logstash;
    }

    public String getConfFile() {
        return confFile;
    }

    public void setConfFile(String confFile) {
        this.confFile = confFile;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getAnonymousId() {
        return anonymousId;
    }

    public void setAnonymousId(String anonymousId) {
        this.anonymousId = anonymousId;
    }

    public String getChannelTrackerEtc() {
        return channelTrackerEtc;
    }

    public void setChannelTrackerEtc(String channelTrackerEtc) {
        this.channelTrackerEtc = channelTrackerEtc;
    }

    public Properties getContextTrackerEtc() {
        return contextTrackerEtc;
    }

    public void setContextTrackerEtc(Properties contextTrackerEtc) {
        this.contextTrackerEtc = contextTrackerEtc;
    }

    public Properties getLibraryTrackerEtcContext() {
        return libraryTrackerEtcContext;
    }

    public void setLibraryTrackerEtcContext(Properties libraryTrackerEtcContext) {
        this.libraryTrackerEtcContext = libraryTrackerEtcContext;
    }

    public String getNameLibraryTrackerEtcContext() {
        return nameLibraryTrackerEtcContext;
    }

    public void setNameLibraryTrackerEtcContext(String nameLibraryTrackerEtcContext) {
        this.nameLibraryTrackerEtcContext = nameLibraryTrackerEtcContext;
    }

    public String getVersionLibraryTrackerEtcContext() {
        return versionLibraryTrackerEtcContext;
    }

    public void setVersionLibraryTrackerEtcContext(String versionLibraryTrackerEtcContext) {
        this.versionLibraryTrackerEtcContext = versionLibraryTrackerEtcContext;
    }

    public void displayLog(String log) {
        //Log.i(TAG, log);
    }
}
