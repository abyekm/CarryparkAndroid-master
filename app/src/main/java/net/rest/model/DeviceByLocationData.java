package net.rest.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class DeviceByLocationData {
    @SerializedName("success")
    @Expose
    private Boolean success;


    @SerializedName("data")
    @Expose
    private AvailableDeviceData data;

    public Boolean getSuccess() {
        return success;
    }

    public AvailableDeviceData getData() {
        return data;
    }
}
