package net.rest.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class PaymentStatus {
    @SerializedName("success")
    @Expose
    private boolean Success;
    @SerializedName("data")
    @Expose
    public  Data data;
    public class Data
    {
        @SerializedName("payment")
        @Expose
        private String  isPaymentSuccess;

        public String getIsPaymentSuccess() {
            return isPaymentSuccess;
        }
    }

    public boolean isSuccess() {
        return Success;
    }

    public Data getData() {
        return data;
    }
}

