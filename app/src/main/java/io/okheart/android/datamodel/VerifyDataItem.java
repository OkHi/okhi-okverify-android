package io.okheart.android.datamodel;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.IgnoreExtraProperties;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

@IgnoreExtraProperties
public class VerifyDataItem implements Serializable {

    private Boolean acCharge;
    private Float acc;
    private Integer batteryLevel;
    private Boolean isCharging;
    private Boolean isPlugged;
    private Double latitude;
    private Double longitude;
    private String model;
    private String ssid;
    private Long timemilliseconds;
    private Timestamp timestamp;
    private Boolean usbCharge;

    private String OSName;
    private Integer OSVersion;
    private List<Map<String, Object>> addresses;
    private Integer appVersionCode;
    private String appVersionName;
    private String brand;
    private String device;
    private GeoPoint geoPoint;
    private String geoPointSource;
    private Float gpsAccuracy;
    private String uniqueId;

    private String distance;
    private String text;
    private String title;
    private String ualId;

    private String firstName;
    private String lastName;
    private String phone;
    private String streetName;

    private String propertyName;
    private String directions;
    private String placeId;
    private String url;
    private String plusCode;
    private List<String> configuredSSIDs;

    private Boolean verified;
    private Double score;

    public VerifyDataItem() {

    }

    public Boolean getAcCharge() {
        return acCharge;
    }

    public void setAcCharge(Boolean acCharge) {
        this.acCharge = acCharge;
    }

    public Float getAcc() {
        return acc;
    }

    public void setAcc(Float acc) {
        this.acc = acc;
    }

    public Integer getBatteryLevel() {
        return batteryLevel;
    }

    public void setBatteryLevel(Integer batteryLevel) {
        this.batteryLevel = batteryLevel;
    }

    public Boolean getCharging() {
        return isCharging;
    }

    public void setCharging(Boolean charging) {
        isCharging = charging;
    }

    public Boolean getPlugged() {
        return isPlugged;
    }

    public void setPlugged(Boolean plugged) {
        isPlugged = plugged;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getSsid() {
        return ssid;
    }

    public void setSsid(String ssid) {
        this.ssid = ssid;
    }

    public Long getTimemilliseconds() {
        return timemilliseconds;
    }

    public void setTimemilliseconds(Long timemilliseconds) {
        this.timemilliseconds = timemilliseconds;
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
    }

    public Boolean getUsbCharge() {
        return usbCharge;
    }

    public void setUsbCharge(Boolean usbCharge) {
        this.usbCharge = usbCharge;
    }

    public String getOSName() {
        return OSName;
    }

    public void setOSName(String OSName) {
        this.OSName = OSName;
    }

    public Integer getOSVersion() {
        return OSVersion;
    }

    public void setOSVersion(Integer OSVersion) {
        this.OSVersion = OSVersion;
    }

    public List<Map<String, Object>> getAddresses() {
        return addresses;
    }

    public void setAddresses(List<Map<String, Object>> addresses) {
        this.addresses = addresses;
    }

    public Integer getAppVersionCode() {
        return appVersionCode;
    }

    public void setAppVersionCode(Integer appVersionCode) {
        this.appVersionCode = appVersionCode;
    }

    public String getAppVersionName() {
        return appVersionName;
    }

    public void setAppVersionName(String appVersionName) {
        this.appVersionName = appVersionName;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public String getDevice() {
        return device;
    }

    public void setDevice(String device) {
        this.device = device;
    }

    public GeoPoint getGeoPoint() {
        return geoPoint;
    }

    public void setGeoPoint(GeoPoint geoPoint) {
        this.geoPoint = geoPoint;
    }

    public String getGeoPointSource() {
        return geoPointSource;
    }

    public void setGeoPointSource(String geoPointSource) {
        this.geoPointSource = geoPointSource;
    }

    public Float getGpsAccuracy() {
        return gpsAccuracy;
    }

    public void setGpsAccuracy(Float gpsAccuracy) {
        this.gpsAccuracy = gpsAccuracy;
    }

    public String getUniqueId() {
        return uniqueId;
    }

    public void setUniqueId(String uniqueId) {
        this.uniqueId = uniqueId;
    }

    public String getDistance() {
        return distance;
    }

    public void setDistance(String distance) {
        this.distance = distance;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getUalId() {
        return ualId;
    }

    public void setUalId(String ualId) {
        this.ualId = ualId;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getStreetName() {
        return streetName;
    }

    public void setStreetName(String streetName) {
        this.streetName = streetName;
    }

    public String getPropertyName() {
        return propertyName;
    }

    public void setPropertyName(String propertyName) {
        this.propertyName = propertyName;
    }

    public String getDirections() {
        return directions;
    }

    public void setDirections(String directions) {
        this.directions = directions;
    }

    public String getPlaceId() {
        return placeId;
    }

    public void setPlaceId(String placeId) {
        this.placeId = placeId;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getPlusCode() {
        return plusCode;
    }

    public void setPlusCode(String plusCode) {
        this.plusCode = plusCode;
    }

    public List<String> getConfiguredSSIDs() {
        return configuredSSIDs;
    }

    public void setConfiguredSSIDs(List<String> configuredSSIDs) {
        this.configuredSSIDs = configuredSSIDs;
    }

    public Boolean getVerified() {
        return verified;
    }

    public void setVerified(Boolean verified) {
        this.verified = verified;
    }

    public Double getScore() {
        return score;
    }

    public void setScore(Double score) {
        this.score = score;
    }
}
