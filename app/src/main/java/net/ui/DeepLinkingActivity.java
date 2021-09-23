package net.ui;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import androidx.fragment.app.Fragment;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.CarryParkApplication;
import net.others.BaseActivity;
import net.others.ConstantProject;
import net.others.DialogManager;
import net.others.SharedPreferenceUtility;
import net.rest.ApiClient;
import net.rest.ApiInterface;
import net.rest.GloablMethods;
import net.rest.Utils;
import net.rest.global.AppController;
import net.rest.model.LockerDetailsResponse;
import net.rest.model.PaymentStatus;
import net.rest.model.UsedLocker;
import net.rest.model.UserDetailsResponse;
import net.simplifiedcoding.carrypark.Adapter.DevicesInUseAdapter;
import net.simplifiedcoding.carrypark.CodeScannerFragment;
import net.simplifiedcoding.carrypark.HtmlFragment;
import net.simplifiedcoding.carrypark.R;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DeepLinkingActivity extends BaseActivity {
    ApiInterface apiService;
    LockerDetailsResponse lockerDetailsResponseList = new LockerDetailsResponse();
    public String DeviceNameBLE = "", DeviceID = "";
    boolean hasInitialPaymentPreviouslyDone;
    List<UsedLocker> usedLockersList = new ArrayList<>();
    private SharedPreferences sharedPreferences;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_deeplink);
        CarryParkApplication.setCurrentContext(this);


        apiService =
                ApiClient.getClient().create(ApiInterface.class);
        callIsPaymentSuccess();
        CarryParkApplication.setScannedDeviceCode(SharedPreferenceUtility.getDeepLinkDeviceCode());
        CarryParkApplication.setScannedDeviceName(SharedPreferenceUtility.getDeepLinkDeviceName());
        CarryParkApplication.setDeviceAddressBle(SharedPreferenceUtility.getDeepLinkdeviceAddress());
    }


    public void callIsPaymentSuccess() {
        showBusyAnimation("");

        Map<String, Object> candidateMap = new HashMap<>();
        candidateMap.put("token", SharedPreferenceUtility.getPaymentToken());


        String acess_token = GloablMethods.API_HEADER + AppController.getString(CarryParkApplication.getmContext(), "acess_token");
        apiService =
                ApiClient.getClient().create(ApiInterface.class);
        apiService.CallIsPaymentSuccessAPi(acess_token, candidateMap).enqueue(new Callback<PaymentStatus>() {
            @Override
            public void onResponse(Call<PaymentStatus> call, Response<PaymentStatus> response) {

                if (response.body().isSuccess()) {
                    String status = response.body().getData().getIsPaymentSuccess();//success

                    getUserDetails(status );



                }


            }


            @Override
            public void onFailure(Call<PaymentStatus> call, Throwable t) {
                hideBusyAnimation();
                if (!Utils.isNetworkConnectionAvailable(CarryParkApplication.getCurrentContext())) {
                    DialogManager.showAlertSingleActionDialog(CarryParkApplication.getCurrentActivity(), CarryParkApplication.getCurrentContext().getResources().getString(R.string.OperfailedExtnd), "ok", new DialogManager.IUniActionDialogOnClickListener() {
                        @Override
                        public void onPositiveClick() {

                        }


                    });
                } else {
                    DialogManager.showAlertSingleActionDialog(CarryParkApplication.getCurrentActivity(), CarryParkApplication.getCurrentContext().getResources().getString(R.string.OperfailedExtnd), "ok", new DialogManager.IUniActionDialogOnClickListener() {
                        @Override
                        public void onPositiveClick() {

                        }


                    });
                }
            }
        });
    }




    private void getUserDetails( String  statusResponse) {

        String acess_token = GloablMethods.API_HEADER + AppController.getString(this, "acess_token");
        Log.e("acess_token",acess_token);
        String lan="";
        if (SharedPreferenceUtility.isJapanease()) {
            if (CarryParkApplication.isIsJapaneaseLang())
                lan =ConstantProject.forJapaneaseResponse;

        } else if (SharedPreferenceUtility.isEnglish()){
            lan= "en";
        }
        else if (SharedPreferenceUtility.isKorean()){
            lan= ConstantProject.forKoreanResponse;
        }
        else if (SharedPreferenceUtility.isChinease()){
            lan= ConstantProject.forChineaseResponse;
        }
        apiService.userDetails(acess_token, lan).enqueue(new Callback<UserDetailsResponse>() {
            @Override
            public void onResponse(Call<UserDetailsResponse> call, Response<UserDetailsResponse> response) {
                String code = String.valueOf(response.code());
                // Log.e("paymentStatus", response.body().getData().getPaymentInfo());
                String device_status = response.body().getData().getLocation();
                sharedPreferences = CarryParkApplication.getCurrentContext().getSharedPreferences("carry_park", MODE_PRIVATE);

                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("user_name", response.body().getData().getUser().getFirstName());
                editor.apply();

                try {

                } catch (Exception e) {
                    e.printStackTrace();
                }

                if (response.code() == 200 && response.body().getData() != null) {
                    Boolean status = response.body().getData().getSuccess();
                    if (status == true) {
                        SharedPreferenceUtility.saveEmailId(response.body().getData().getUser().getEmail());
                        CarryParkApplication.setUserName(response.body().getData().getUser().getFirstName());
                        usedLockersList.clear();
                        usedLockersList.addAll(response.body().getData().getUser().getUsedLockers());
                        CarryParkApplication.setUsedLockerList(usedLockersList);


                        if (usedLockersList!=null)
                        {
                            if (usedLockersList.size()>0)
                            {
                                boolean isTrue =false;
                                for (int i=0;i<usedLockersList.size();i++)
                                {
                                    if (usedLockersList.get(i).getPresentStatus().equalsIgnoreCase("LOCKED"))
                                    {
                                        isTrue=true;
                                    }
                                }


                            }else {

                            }
                        }
                        CarryParkApplication.setUsedLockerList(usedLockersList);




                    }

                    if (statusResponse.contains("success")) {
                        hideBusyAnimation();
                        Intent intent = new Intent(DeepLinkingActivity.this, BottomNavigation.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        intent.putExtra("page", ConstantProject.DeepLinking);
                        startActivity(intent);



                    } else {
                        hideBusyAnimation();
                        Intent intent = new Intent(CarryParkApplication.getCurrentContext(), BottomNavigation.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        intent.putExtra("page", ConstantProject.SplashActivity);
                        startActivity(intent);

                    }


                    /*else if(status == false){
                        String msg = response.body().getMessage();
                    }*/
                }/*else if(response.code() == 200 && response.body().getData() == null) {
                    progressDialog.dismiss();
                    String msg = response.body().getMessage();
                }*/ else if (response.code() == 404) {
                    DialogManager.showAlertSingleActionDialog(CarryParkApplication.getCurrentActivity(),CarryParkApplication.getCurrentContext().getResources().getString(R.string.validation_error), "ok", new DialogManager.IUniActionDialogOnClickListener() {
                        @Override
                        public void onPositiveClick() {

                        }


                    });
                }
            }

            @Override
            public void onFailure(Call<UserDetailsResponse> call, Throwable t) {
                if (!Utils.isNetworkConnectionAvailable(CarryParkApplication.getCurrentContext())) {
                    DialogManager.showAlertSingleActionDialog(CarryParkApplication.getCurrentActivity(), CarryParkApplication.getCurrentContext().getResources().getString(R.string.no_internet), "ok", new DialogManager.IUniActionDialogOnClickListener() {
                        @Override
                        public void onPositiveClick() {

                        }
                    });
                } else {
                    DialogManager.showAlertSingleActionDialog(CarryParkApplication.getCurrentActivity(),CarryParkApplication.getCurrentContext().getResources().getString(R.string.OperfailedExtnd), "ok", new DialogManager.IUniActionDialogOnClickListener() {
                        @Override
                        public void onPositiveClick() {

                        }


                    });
                }
            }
        });
    }

}
