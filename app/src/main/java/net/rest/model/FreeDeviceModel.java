package net.rest.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class FreeDeviceModel {



    @SerializedName("location")
    @Expose
    private String location;

    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("device_name")
    @Expose
    private String device_name;
    @SerializedName("device_id")
    @Expose
    private String device_id;
    @SerializedName("latitude")
    @Expose
    private String latitude;
    @SerializedName("longitude")
    @Expose
    private String longitude;

    @SerializedName("address_1")
    @Expose
    private String address_1;
    @SerializedName("address_2")
    @Expose
    private String address_2;
    @SerializedName("landmark")
    @Expose
    private String landmark;
    @SerializedName("pincode")
    @Expose
    private String pincode;
    @SerializedName("initial_charges")
    @Expose
    private Integer initial_charges;
    @SerializedName("usage_hour")
    @Expose
    private Integer usage_hour;
    @SerializedName("rate_per_hour")
    @Expose
    private Integer rate_per_hour;
    @SerializedName("max_time")
    @Expose
    private Integer max_time;
    @SerializedName("initial_hours")
    @Expose
    private Integer initial_hours;
    @SerializedName("start_time")
    @Expose
    private String start_time;
    @SerializedName("end_time")
    @Expose
    private String end_time;
    @SerializedName("present_status")
    @Expose
    private String present_status;
    @SerializedName("status")
    @Expose
    private String status;
    @SerializedName("lock_process_status")
    @Expose
    private String lock_process_status;
    @SerializedName("lock_processed_time")
    @Expose
    private String lock_processed_time;
    @SerializedName("initial_payment_status")
    @Expose
    private String initial_payment_status;
    @SerializedName("initial_payment_time")
    @Expose
    private String initial_payment_time;
    @SerializedName("current_user")
    @Expose
    private Integer current_user;
    @SerializedName("qr_code")
    @Expose
    private String qr_code;
    @SerializedName("random_table")
    @Expose
    private String random_table;
    @SerializedName("battery_level")
    @Expose
    private String battery_level;
    @SerializedName("close_hash")
    @Expose
    private String close_hash;
    @SerializedName("open_hash")
    @Expose
    private String open_hash;
    @SerializedName("created_at")
    @Expose
    private String created_at;
    @SerializedName("updated_at")
    @Expose
    private String updated_at;
    @SerializedName("characteristicUUID")
    @Expose
    private String characteristicUUID;
    @SerializedName("serviceUUID")
    @Expose
    private String serviceUUID;

    @SerializedName("initial_payment")
    @Expose
    private boolean initial_payment;
    @SerializedName("final_payment")
    @Expose
    private boolean final_payment;

    @SerializedName("works_24_hours")
    @Expose
    private int works_24_hours;

    @SerializedName("getDevicesByLocation")
    @Expose
    private int getDevicesByLocation;

    @SerializedName("ext_unit_time")
     @Expose
    private int ext_unit_time;


    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public Integer getId() {
        return id;
    }

    public String getDevice_name() {
        return device_name;
    }

    public String getDevice_id() {
        return device_id;
    }

    public String getLatitude() {
        return latitude;
    }

    public String getAddress_1() {
        return address_1;
    }

    public String getAddress_2() {
        return address_2;
    }

    public String getLandmark() {
        return landmark;
    }

    public Integer getInitial_charges() {
        return initial_charges;
    }

    public Integer getInitial_hours() {
        return initial_hours;
    }

    public String getEnd_time() {
        return end_time;
    }

    public String getInitial_payment_status() {
        return initial_payment_status;
    }

    public String getInitial_payment_time() {
        return initial_payment_time;
    }

    public Integer getCurrent_user() {
        return current_user;
    }

    public String getBattery_level() {
        return battery_level;
    }

    public String getClose_hash() {
        return close_hash;
    }

    public String getCreated_at() {
        return created_at;
    }

    public String getCharacteristicUUID() {
        return characteristicUUID;
    }

    public String getLongitude() {
        return longitude;
    }

    public String getPincode() {
        return pincode;
    }

    public Integer getUsage_hour() {
        return usage_hour;
    }

    public Integer getRate_per_hour() {
        return rate_per_hour;
    }

    public Integer getMax_time() {
        return max_time;
    }

    public String getStart_time() {
        return start_time;
    }

    public String getPresent_status() {
        return present_status;
    }

    public String getStatus() {
        return status;
    }

    public String getLock_process_status() {
        return lock_process_status;
    }

    public String getLock_processed_time() {
        return lock_processed_time;
    }

    public String getQr_code() {
        return qr_code;
    }

    public String getRandom_table() {
        return random_table;
    }

    public String getOpen_hash() {
        return open_hash;
    }

    public String getUpdated_at() {
        return updated_at;
    }

    public String getServiceUUID() {
        return serviceUUID;
    }

    public boolean isInitial_payment() {
        return initial_payment;
    }

    public boolean isFinal_payment() {
        return final_payment;
    }

    public int getWorks_24_hours() {
        return works_24_hours;
    }

    public int getGetDevicesByLocation() {
        return getDevicesByLocation;
    }

    public int getExt_unit_time() {
        return ext_unit_time;
    }
}
