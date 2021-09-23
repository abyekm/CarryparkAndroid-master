package net.rest.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class AvailableDeviceData {

    @SerializedName("total_count")
    @Expose
    private int total_count;

    @SerializedName("free_device_count")
    @Expose
    private int free_device_count;

    @SerializedName("locked_device_count")
    @Expose
    private int locked_device_count;

    @SerializedName("free_devices")
    @Expose
    private List<FreeDeviceModel> freeDeviceModelList;

    public int getTotal_count() {
        return total_count;
    }

    public int getFree_device_count() {
        return free_device_count;
    }

    public int getLocked_device_count() {
        return locked_device_count;
    }

    public List<FreeDeviceModel> getFreeDeviceModelList() {
        return freeDeviceModelList;
    }
}
