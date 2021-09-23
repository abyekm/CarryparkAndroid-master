package net.rest.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class LocationData {


    @SerializedName("location")
    @Expose
    private String location;



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

    @SerializedName("latitude")
    @Expose
    private String latitude;

    @SerializedName("longitude")
    @Expose
    private String longitude;

    @SerializedName("initial_hours")
    @Expose
    private Integer initial_hours;

    @SerializedName("initial_charges")
    @Expose
    private Integer initial_charges;

    @SerializedName("rate_per_hour")
    @Expose
    private Integer rate_per_hour;

    @SerializedName("max_time")
    @Expose
    private Integer max_time;

    @SerializedName("start_time")
    @Expose
    private String start_time;

    @SerializedName("end_time")
    @Expose
    private String end_time;

    @SerializedName("created_at")
    @Expose
    private String created_at;

    @SerializedName("updated_at")
    @Expose
    private String updated_at;

    @SerializedName("distance")
    @Expose
    private Double distance;

    @SerializedName("photo")
    @Expose
    private String photo;

    @SerializedName("photos")
    @Expose
    private List<String> photosList;


    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
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

    public String getPincode() {
        return pincode;
    }

    public String getLatitude() {
        return latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public Integer getInitial_hours() {
        return initial_hours;
    }

    public Integer getInitial_charges() {
        return initial_charges;
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

    public String getEnd_time() {
        return end_time;
    }

    public String getCreated_at() {
        return created_at;
    }

    public String getUpdated_at() {
        return updated_at;
    }

    public Double getDistance() {
        return distance;
    }

    public String getPhoto() {
        return photo;
    }

    public List<String> getPhotosList() {
        return photosList;
    }
}
