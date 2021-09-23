package net.rest;


import net.rest.model.*;

import java.util.Map;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface ApiInterface {
    /*@POST("userRegister")
    Call<UserRegisterResponse> signUp(@Body RequestBody request);*/
    //signup
    @POST("userRegister")
    Call<UserRegisterResponse> signUp(@Body Map<String, Object> request);

    @POST("userLogin")
    Call<UserLoginResponse> login(@Body Map<String, Object> request);

    @POST("userOTPlogin")
    Call<UserLoginResponse> login_new(@Body Map<String, Object> request);

    @POST("verifyPin")
    Call<PinVerifyResponse> verifyPin(@Body Map<String, Object> request);

    //Get the detail of specific Locker (post) http://192.168.2.24:8888/carryPark/api/locker/show
    @POST("locker/show")
    Call<LockerDetailsResponse> lockerDetails(@Header(GloablMethods.HEADER_AUTHORIZATION) String acces_token,@Body Map<String, Object> request);

    //Get the detail user (get) http://192.168.2.24:8888/carryPark/api/locker/show
    @GET("user")
    Call<UserDetailsResponse> userDetails(@Header(GloablMethods.HEADER_AUTHORIZATION) String acces_token,@Query("lang_id") String lang);

    //to change the verification pin
    @POST("changePin")
    Call<ChangePinResponse> changePIN(@Header(GloablMethods.HEADER_AUTHORIZATION) String acces_token,@Body Map<String, Object> request);

    //to get the pin in registered mail inbox
    @POST("resendPin")
    Call<ResendPinResponse> resendPIN(@Body Map<String, Object> request);

    @POST("forgotPin")
    Call<ResendPinResponse> forgotPin(@Body Map<String, Object> request);

    /*@POST("payment")
    Call<PaymentApiResponse> getTokenFromPaymentAPI(@Header(GloablMethods.HEADER_AUTHORIZATION) String acces_token,@Body Map<String, Object> request);
*/
    /*@POST("paymentConfirmation")
    Call<PaymentConfirmationApiResponse> paymentConfirmationAPI(@Header(GloablMethods.HEADER_AUTHORIZATION) String acces_token, @Body Map<String, Object> request);
*/
    @POST("locker_lock")
    Call<LockApiResponse> lockingAPI(@Header(GloablMethods.HEADER_AUTHORIZATION) String acces_token, @Body Map<String, Object> request);

    @POST("locker_unlock")
    Call<UnLockApiResponse> UnlockingAPI(@Header(GloablMethods.HEADER_AUTHORIZATION) String acces_token, @Body Map<String, Object> request);

    @POST("unlock_request")
    Call<UnLockRequestApiResponse> UnlockingRequestAPI(@Header(GloablMethods.HEADER_AUTHORIZATION) String acces_token, @Body Map<String, Object> request);

    @POST("success")
    Call<SuccessApiResponse> successAPI(@Header(GloablMethods.HEADER_AUTHORIZATION) String acces_token, @Body Map<String, Object> request);
//composeHashMsg
    //   To get the hsh values

    @POST("composeHashMsg")
    Call<HashApiResponse> getHashValues(@Header(GloablMethods.HEADER_AUTHORIZATION) String acces_token, @Body Map<String, Object> request);
//Error Logging API

    @POST("logError")
    Call<HashApiResponse> postErrorLog(@Header(GloablMethods.HEADER_AUTHORIZATION) String acces_token, @Body Map<String, Object> request);

    // Api for nearest location  : nearestLocations
    @POST("nearestLocations")
    Call<NearestLocationResponseModel> getNearestLocations(@Header(GloablMethods.HEADER_AUTHORIZATION) String access_token,@Body Map<String, Object> request);

    // Api for nearest location  : nearestLocations
    @POST("getDevicesByLocation")
    Call<DeviceByLocationData> getDevicesByLocation(@Header(GloablMethods.HEADER_AUTHORIZATION) String access_token,@Body Map<String, Object> request);

//API for universlPayment
    @POST("universal_payment")
    Call<PredefinedPaymentResponse> CallUniversal_payment(@Header(GloablMethods.HEADER_AUTHORIZATION) String access_token,@Body Map<String, Object> request);


    @POST("check_payment_status")
    Call<PaymentStatus> CallIsPaymentSuccessAPi(@Header(GloablMethods.HEADER_AUTHORIZATION) String access_token,@Body Map<String, Object> request);

    @POST("changeEmailId")
    Call<ChangeEmailResponseModel> CallChangeEmailId(@Header(GloablMethods.HEADER_AUTHORIZATION) String access_token,@Body Map<String, Object> request);


    @POST("deRegister")
    Call<ChangeEmailResponseModel> CallDeRegister(@Header(GloablMethods.HEADER_AUTHORIZATION) String access_token);

    @POST("enquiryMail")
    Call<ChangeEmailResponseModel> CallEnquiryMail(@Header(GloablMethods.HEADER_AUTHORIZATION) String access_token);

    @POST("logAppError")
    Call<LogAppErrorModel> LogAppErrors(@Header(GloablMethods.HEADER_AUTHORIZATION) String access_token,@Body Map<String, Object> request);


    //sendSuccessMail

    @POST("sendSuccessMail")
    Call<ChangeEmailResponseModel> sendSuccessMail(@Header(GloablMethods.HEADER_AUTHORIZATION) String access_token,@Body Map<String, Object> request);
//setPaymentGateway

    @POST("setPaymentGateway")
    Call<ChangeEmailResponseModel> sendUserSelectedPaymentMethod(@Header(GloablMethods.HEADER_AUTHORIZATION) String access_token,@Body Map<String, Object> request);
// Change Mobile Number

    @POST("changeMobileNumber")
    Call<ChangeEmailResponseModel> CallChangeMobileNumber(@Header(GloablMethods.HEADER_AUTHORIZATION) String access_token,@Body Map<String, Object> request);

    //set_payment_bypass_time
    @POST("set_payment_bypass_time")
    Call<ChangeEmailResponseModel> CallPaymentBypassTime(@Header(GloablMethods.HEADER_AUTHORIZATION) String access_token,@Body Map<String, Object> request);


    @POST("invoice")
    Call<InvoiceModel> CallInvoiceApi(@Header(GloablMethods.HEADER_AUTHORIZATION) String access_token,@Body Map<String, Object> request);

}

