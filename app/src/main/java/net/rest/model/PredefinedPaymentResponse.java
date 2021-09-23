package net.rest.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class PredefinedPaymentResponse {

    @SerializedName("success")
    @Expose
    private boolean success;

    @SerializedName("data")
    @Expose
    private data data;





    public class data{
        @SerializedName("url")
        @Expose
        public String url;

        public String getUrl() {
            return url;
        }

        @SerializedName("predefined_payment")
        @Expose
        public predefined_payment predefined_payment;

        public PredefinedPaymentResponse.predefined_payment getPredefined_payment() {
            return predefined_payment;
        }
    }

    public class predefined_payment{
        @SerializedName("token")
        @Expose
        public String token;

        public String getToken() {
            return token;
        }
    }
    public boolean isSuccess() {
        return success;
    }

    public PredefinedPaymentResponse.data getData() {
        return data;
    }
}
