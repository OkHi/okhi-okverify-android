package io.okheart.android.datamodel;

/**
 * Created by ramogiochola on 5/5/16.
 */
public class AddressItem {

    /*
    private int background = 0;
    private int checked = 0;
    private int carupgradedelivered = 0;
    private byte[] picture;
    */
    private int isOddress;

    //private Long databaseid;
    //private int unique;
    private String customername;
    //private String cashier;
    private String phonecustomer;
    //private String phonedriver;
    //private String orderid;
    //private String deliveryid;
    //private String checknumber;
    //private String state;
    //private Long statetime;
    private String affiliation;
    //private String driverid;
    //private String drivername;
    //private String paymenttype;
    private String branch;
    //private String amount;
    private String ualid;
    private String aflid;
    private String imageurl;
    private String unit;
    private String direction;
    private String propnumber;
    private String propname;
    private String floor;
    private String deliverynotes;
    private double lat;
    private double lng;
    private String route;
    //private String checkoutid;
    //private byte[] blob;
    //private String url;
    //private int manualinput;
    //private String orderstate;
    //private Long diff;
    //private String currency;
    //private String currencycode;
    //private String riderobjectid;
    /*
    private String ticks;
    private String riderHome;
    private String taskId;
    private String tripId;
    private String customerId;
    private String destinationId;
    private String trackingUrl;
    private String bitlyLink;
    */
    private String locationNickname;
    private String traditionalBuildingName;
    private String businessName;
    private String traditionalStreetNumber;
    private String traditionalStreetName;
    private String toTheDoor;
    private String traditionalBuildingNumber;
    private String streetNumber;
    private String streetName;
    //private Long createdattime;
    //private Long riderassigntime;
    //private Long outfordeliverytime;
    //private Long completeddeliverytime;


    private String sourceAffiliation;
    private String sourceBranch;
    private String sourceBrand;
    private String addressType;
    private String internalAddressType;
    private String customeruserid;

    private String isNewUser;
    private String isEmptyUal;
    private Double acc;

    private Integer addressFrequency;
    private String createdon;
    private String lastused;
    private String locationName;
    private String uniqueId;

    public AddressItem() {

    }

    public String getBranch() {
        return branch;
    }

    public void setBranch(String branch) {
        this.branch = branch;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public int getIsOddress() {
        return isOddress;
    }

    public void setIsOddress(int isOddress) {
        this.isOddress = isOddress;
    }

    public String getCustomername() {
        return customername;
    }

    public void setCustomername(String customername) {
        this.customername = customername;
    }

    public String getPhonecustomer() {
        return phonecustomer;
    }

    public void setPhonecustomer(String phonecustomer) {
        this.phonecustomer = phonecustomer;
    }

    public String getAffiliation() {
        return affiliation;
    }

    public void setAffiliation(String affiliation) {
        this.affiliation = affiliation;
    }

    public String getUalid() {
        return ualid;
    }

    public void setUalid(String ualid) {
        this.ualid = ualid;
    }

    public String getAflid() {
        return aflid;
    }

    public void setAflid(String aflid) {
        this.aflid = aflid;
    }

    public String getImageurl() {
        return imageurl;
    }

    public void setImageurl(String imageurl) {
        this.imageurl = imageurl;
    }

    public String getDirection() {
        return direction;
    }

    public void setDirection(String direction) {
        this.direction = direction;
    }

    public String getPropnumber() {
        return propnumber;
    }

    public void setPropnumber(String propnumber) {
        this.propnumber = propnumber;
    }

    public String getPropname() {
        return propname;
    }

    public void setPropname(String propname) {
        this.propname = propname;
    }

    public String getFloor() {
        return floor;
    }

    public void setFloor(String floor) {
        this.floor = floor;
    }

    public String getDeliverynotes() {
        return deliverynotes;
    }

    public void setDeliverynotes(String deliverynotes) {
        this.deliverynotes = deliverynotes;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLng() {
        return lng;
    }

    public void setLng(double lng) {
        this.lng = lng;
    }

    public String getRoute() {
        return route;
    }

    public void setRoute(String route) {
        this.route = route;
    }

    public String getLocationNickname() {
        return locationNickname;
    }

    public void setLocationNickname(String locationNickname) {
        this.locationNickname = locationNickname;
    }

    public String getTraditionalBuildingName() {
        return traditionalBuildingName;
    }

    public void setTraditionalBuildingName(String traditionalBuildingName) {
        this.traditionalBuildingName = traditionalBuildingName;
    }

    public String getBusinessName() {
        return businessName;
    }

    public void setBusinessName(String businessName) {
        this.businessName = businessName;
    }

    public String getTraditionalStreetNumber() {
        return traditionalStreetNumber;
    }

    public void setTraditionalStreetNumber(String traditionalStreetNumber) {
        this.traditionalStreetNumber = traditionalStreetNumber;
    }

    public String getTraditionalStreetName() {
        return traditionalStreetName;
    }

    public void setTraditionalStreetName(String traditionalStreetName) {
        this.traditionalStreetName = traditionalStreetName;
    }

    public String getToTheDoor() {
        return toTheDoor;
    }

    public void setToTheDoor(String toTheDoor) {
        this.toTheDoor = toTheDoor;
    }

    public String getTraditionalBuildingNumber() {
        return traditionalBuildingNumber;
    }

    public void setTraditionalBuildingNumber(String traditionalBuildingNumber) {
        this.traditionalBuildingNumber = traditionalBuildingNumber;
    }

    public String getStreetNumber() {
        return streetNumber;
    }

    public void setStreetNumber(String streetNumber) {
        this.streetNumber = streetNumber;
    }

    public String getStreetName() {
        return streetName;
    }

    public void setStreetName(String streetName) {
        this.streetName = streetName;
    }

    public String getSourceAffiliation() {
        return sourceAffiliation;
    }

    public void setSourceAffiliation(String sourceAffiliation) {
        this.sourceAffiliation = sourceAffiliation;
    }

    public String getSourceBranch() {
        return sourceBranch;
    }

    public void setSourceBranch(String sourceBranch) {
        this.sourceBranch = sourceBranch;
    }

    public String getSourceBrand() {
        return sourceBrand;
    }

    public void setSourceBrand(String sourceBrand) {
        this.sourceBrand = sourceBrand;
    }

    public String getAddressType() {
        return addressType;
    }

    public void setAddressType(String addressType) {
        this.addressType = addressType;
    }

    public String getInternalAddressType() {
        return internalAddressType;
    }

    public void setInternalAddressType(String internalAddressType) {
        this.internalAddressType = internalAddressType;
    }

    public String getCustomeruserid() {
        return customeruserid;
    }

    public void setCustomeruserid(String customeruserid) {
        this.customeruserid = customeruserid;
    }

    public String getIsNewUser() {
        return isNewUser;
    }

    public void setIsNewUser(String isNewUser) {
        this.isNewUser = isNewUser;
    }

    public String getIsEmptyUal() {
        return isEmptyUal;
    }

    public void setIsEmptyUal(String isEmptyUal) {
        this.isEmptyUal = isEmptyUal;
    }

    public Double getAcc() {
        return acc;
    }

    public void setAcc(Double acc) {
        this.acc = acc;
    }

    public Integer getAddressFrequency() {
        return addressFrequency;
    }

    public void setAddressFrequency(Integer addressFrequency) {
        this.addressFrequency = addressFrequency;
    }

    public String getCreatedon() {
        return createdon;
    }

    public void setCreatedon(String createdon) {
        this.createdon = createdon;
    }

    public String getLastused() {
        return lastused;
    }

    public void setLastused(String lastused) {
        this.lastused = lastused;
    }

    public String getLocationName() {
        return locationName;
    }

    public void setLocationName(String locationName) {
        this.locationName = locationName;
    }

    public String getUniqueId() {
        return uniqueId;
    }

    public void setUniqueId(String uniqueId) {
        this.uniqueId = uniqueId;
    }
}
