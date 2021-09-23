package net.rest.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class InvoiceModel {
    @SerializedName("success")
    @Expose
    private Boolean success ;
    @SerializedName("message")
    @Expose
    private String message;

    @SerializedName("data")
    @Expose
    private Data data;


    public class Data{
        @SerializedName("unlock_time")
        @Expose
        private String unlock_time;
        @SerializedName("total_amount")
        @Expose
        private String total_amount;

        public String getUnlock_time() {
            return unlock_time;
        }

        public String getTotal_amount() {
            return total_amount;
        }
    }

    public Boolean getSuccess() {
        return success;
    }

    public String getMessage() {
        return message;
    }

    public Data getData() {
        return data;
    }
}
