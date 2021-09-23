package net.simplifiedcoding.carrypark;


import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import net.CarryParkApplication;
import net.others.*;
import net.rest.ApiClient;
import net.rest.ApiInterface;
import net.rest.GloablMethods;
import net.rest.Utils;
import net.rest.global.AppController;
import net.rest.model.UnLockRequestApiResponse;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

import net.ui.BottomNavigation;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.content.Context.MODE_PRIVATE;


public class FinalPaymentFragment extends Fragment implements View.OnClickListener {
    //private PaymentSuccessFragment targetFragment = null;
    String isFrom = null,paymentStatus = null;
    private Fragment targetFragment = null;

    ApiInterface apiService;
    //private long currentTimeOfTimer = 60000;
    private long currentTimeOfTimer = 900000;
    private int rounded_hours;
    Boolean hasFinalPaymentDone = false;
    private String unLockToken;
    Boolean isPaymentRequired = false;
    public FinalPaymentFragment() {
        // Required empty public constructor
    }

    Integer amount =0;
    SharedPreferences sharedPreferences;
    TextView tvUser,tvRatePerHour,tvInitialAmtTotal,tvUsageAmtTotal,tvRatePerHour_3,tvRatePerHour_minus_final,tvRatePerHour_4,tvTotalAmtTotal,tvResMul_3,tvResMul_4,tvFinalPayAmount,tvTimeRemaining,tvInitialPaymentTitle,tvFinalPayAmountLabel;
    TextView tvDeviceName,tvStartTime,tvEndTime,tvDurationUsed,tvRatePerHourUsage,tvHourUsage,tvUsageAmt;
    Button buttonPayToPaymentSuccess;
    String ratePerHour,initialCharge,deviceName;
    int maxTime;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        String languageToLoad="en";
        if (SharedPreferenceUtility.isJapanease())
        {
            languageToLoad  = "ja";
        }
        else if (SharedPreferenceUtility.isEnglish())
        {
            languageToLoad  = "en";
        }
        else if (SharedPreferenceUtility.isChinease())
        {
            languageToLoad  = ConstantProject.ChineaseLocali;
        }
        else if (SharedPreferenceUtility.isKorean())
        {
            languageToLoad  = "ko";
        }
        Locale locale = new Locale(languageToLoad);
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.locale = locale;
        CarryParkApplication.getCurrentContext().getResources().updateConfiguration(config,
                CarryParkApplication.getCurrentContext().getResources().getDisplayMetrics());


        return inflater.inflate(R.layout.fragment_final_payment, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        apiService =
                ApiClient.getClient().create(ApiInterface.class);
        sharedPreferences = getActivity().getSharedPreferences("carry_park",MODE_PRIVATE);
        isFrom = getArguments().getString("is_from");
        paymentStatus = getArguments().getString("payment_status");
        tvUser = (TextView)view.findViewById(R.id.tv_user);
        buttonPayToPaymentSuccess = (Button)view.findViewById(R.id.button_pay_to_payment_success_final);
       // tvPaymentCancelled = (TextView)view.findViewById(R.id.tv_payment_cancelled);

        tvRatePerHour = (TextView)view.findViewById(R.id.tv_rate_per_hour_final);
        tvInitialAmtTotal = (TextView)view.findViewById(R.id.tv_initialAmt_total);
        tvUsageAmtTotal = (TextView)view.findViewById(R.id.tv_usageAmt_total);
        tvRatePerHour_3 = (TextView)view.findViewById(R.id.tv_rate_per_hour_3_final);
        //tvRatePerHour_4 = (TextView)view.findViewById(R.id.tv_rate_per_hour_4);
        tvTotalAmtTotal = (TextView)view.findViewById(R.id.tv_totalAmt_total);
        tvRatePerHour_minus_final = (TextView)view.findViewById(R.id.rate_per_hr_minus_final);
        tvResMul_3 = (TextView)view.findViewById(R.id.tv_res_mul_3_final_Amount);
        //for usage amount
        tvRatePerHourUsage = (TextView)view.findViewById(R.id.tv_rate_per_hour_usage);
        tvHourUsage = (TextView)view.findViewById(R.id.tv_hour_usage);
        tvUsageAmt = (TextView)view.findViewById(R.id.tv_usage_amt);
        //tvResMul_4 = (TextView)view.findViewById(R.id.tv_res_mul_4);



        //tvInitialPaymentTitle = (TextView)view.findViewById(R.id.tv_final_payment_title);
        tvTimeRemaining = (TextView)view.findViewById(R.id.tv_time_remaining);
        tvFinalPayAmountLabel =  (TextView)view.findViewById(R.id.tv_final_pay_amount_label);
        tvFinalPayAmount = (TextView)view.findViewById(R.id.tv_final_pay_amount);

        tvDeviceName = (TextView)view.findViewById(R.id.tv_device_name);
        tvStartTime = (TextView)view.findViewById(R.id.tv_start_time);
        tvEndTime = (TextView)view.findViewById(R.id.tv_end_time);
        tvDurationUsed = (TextView)view.findViewById(R.id.tv_duration_used);
        CarryParkApplication.setIsAliPay(false);
        confirmPayment();
        if(isFrom.equalsIgnoreCase("WebViewFragment") && paymentStatus.equalsIgnoreCase("pay_success")){
            // buttonPayToPaymentSuccess.setText("Proceed");
            currentTimeOfTimer = getArguments().getLong("current_time_of_timer");
            //confirmPayment();
            AsyncTask.execute(new Runnable() {
                @Override
                public void run() {
                    //TODO your background code
                    confirmPayment();
                }
            });
            //calling unlock request API

        }
        else if(isFrom.equalsIgnoreCase("WebViewFragment") && paymentStatus.equalsIgnoreCase("pay_cancel")){
           // tvPaymentCancelled.setVisibility(View.VISIBLE);
            //calling unlock request API
            currentTimeOfTimer = getArguments().getLong("current_time_of_timer");
            String device_id = sharedPreferences.getString("device_id",null);
            //confirmPayment();
            callUnlockRequestAPI(device_id);
        }else if(isFrom.equalsIgnoreCase("InUseFragment")){
            String device_id = sharedPreferences.getString("device_id",null);
            //confirmPayment();
            callUnlockRequestAPI(device_id);
        }
        buttonPayToPaymentSuccess.setOnClickListener(this);

        //getActivity().getSharedPreferences("carry_park",MODE_PRIVATE);
        String user = sharedPreferences.getString("user_name",null);
        //tvUser.setText("Hi,"+user);

        ratePerHour = sharedPreferences.getString("rate_per_hour",null);
        initialCharge = sharedPreferences.getString("initial_charge",null);
        maxTime = Integer.parseInt(sharedPreferences.getString("max_time",null));
        deviceName  = sharedPreferences.getString("device_name",null);
        tvDeviceName.setText(deviceName);

        if (CarryParkApplication.isIsEnglishLang())
        {
            tvUser.setText(getString(R.string.hi)+", "+user);
        }
        else if (CarryParkApplication.isIsJapaneaseLang())
        {
            //"ようこそ、Carryparkへ
            //○○さん"
            tvUser.setText("ようこそ Carryparkへ"+user+"さん");
        }
        /*int rate_p_h = Integer.parseInt(ratePerHour);
        int rate_p_h_2 = rate_p_h*2;
        int rate_p_h_3 = rate_p_h*4;
        int rate_p_h_4 = rate_p_h*8;
        String rateperhr_str = String.valueOf(rate_p_h);
        String rateperhr_2_str = String.valueOf(rate_p_h_2);
        String rateperhr_3_str = String.valueOf(rate_p_h_3);
        String rateperhr_4_str = String.valueOf(rate_p_h_4);
        tvRatePerHour.setText("¥ "+rateperhr_str);
        tvRatePerHour_hourDuration.setText(rateperhr_str);
        tvRatePerHour_3.setText(rateperhr_str);
        tvRatePerHour_4.setText(rateperhr_str);
        tvResMul_2.setText("¥ "+rateperhr_2_str);
        tvResMul_3.setText("¥ "+rateperhr_3_str);
        tvResMul_4.setText("¥ "+rateperhr_4_str);
        tvInitialPayAmount.setText("¥ "+initialCharge);
        buttonPayToPaymentSuccess.setText("Pay ¥ "+initialCharge);*/

        //String lockStatus = sharedPreferences.getString("isLocked", null);
        /*if(lockStatus!=null && lockStatus.equalsIgnoreCase("LOCKED")){
            //tvInitialPaymentTitle.setText(R.string.final_payment);
            //tvFinalPayAmountLabel.setText(R.string.final_payment_amount);
        }*/

        new CountDownTimer(currentTimeOfTimer , 1000) { // adjust the milli seconds here

            public void onTick(long millisUntilFinished) {
                tvTimeRemaining.setText(""+String.format("%d m",
                        TimeUnit.MILLISECONDS.toMinutes( millisUntilFinished),
                        TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished) -
                                TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished))));

                currentTimeOfTimer = millisUntilFinished;
            }

            public void onFinish() {

            }
        }.start();
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.button_pay_to_payment_success_final:
                /*targetFragment = new PaymentSuccessFragment();
                InUseFragment ldf = new InUseFragment ();
                replaceFragment(targetFragment,ldf);*/
                if(isFrom.equalsIgnoreCase("InUseFragment") || (isFrom.equalsIgnoreCase("WebViewFragment") && paymentStatus.equalsIgnoreCase("pay_cancel"))) {
                 //   getPaymentToken();
                    //sjn
                    if(isPaymentRequired.equals(false)){
                        if(amount>0)
                        {
                            targetFragment = new HtmlFragment();
                            replaceFragment(unLockToken);
                        }
                        else {
                            targetFragment = new PaymentSuccessFragment();
                            replaceFragment(targetFragment);
                        }

                    }else{
                        targetFragment = new PaymentSuccessFragment();
                        replaceFragment(targetFragment);
                    }

                } else if(isFrom.equalsIgnoreCase("WebViewFragment") && paymentStatus.equalsIgnoreCase("pay_success")){
                     if(hasFinalPaymentDone == false){
                         // getPaymentToken();
                         targetFragment = new HtmlFragment();
                         replaceFragment(unLockToken);
                     }
                }/*else if(isFrom.equalsIgnoreCase("WebViewFragment") && paymentStatus.equalsIgnoreCase("pay_cancel")){
                    getPaymentToken();
                }*/
                break;
        }
    }

    private void confirmPayment() {
        ((BaseActivity) getActivity()).showBusyAnimation("");
        Map<String, Object> candidateMap = new HashMap<>();
        candidateMap.put("device_id",sharedPreferences.getString("device_id",null));
        if(SharedPreferenceUtility.isJapanease()){
            if (CarryParkApplication.isIsJapaneaseLang())
            candidateMap.put("lang_id", ConstantProject.forJapaneaseResponse);

        }else{
            candidateMap.put("lang_id", "en");
        }
        String acess_token = GloablMethods.API_HEADER+ AppController.getString(getActivity(), "acess_token");
       /* apiService.paymentConfirmationAPI(acess_token,candidateMap).enqueue(new Callback<PaymentConfirmationApiResponse>() {
            @Override
            public void onResponse(Call<PaymentConfirmationApiResponse> call, Response<PaymentConfirmationApiResponse> response) {
                ((BaseActivity) getActivity()).hideBusyAnimation();
                Log.e(TAG,"------>" + "Response");
                Log.e(TAG,response.toString());
                Log.e("responseCode", String.valueOf(response.code()));

                        if(response.code() == 200){
                    Boolean status = response.body().getSuccess();
                    if(status == true){
                        String token = response.body().getData().getToken();

                        Log.d("payment_flag",response.body().getData().getPayment().toString());
                        if(response.body().getData().getPayment() == false){
                            hasFinalPaymentDone = false;
                            String msg = response.body().getMessage();
                        }else{
                            hasFinalPaymentDone = true;
     }

                    }else{
                        String device_id = sharedPreferences.getString("device_id",null);
                        callUnlockRequestAPI(device_id);
                    }
                }else if(response.code() == 404){
                            }


            }

            @Override
            public void onFailure(Call<PaymentConfirmationApiResponse> call, Throwable t) {
                ((BaseActivity) getActivity()).hideBusyAnimation();
                if(!Utils.isNetworkConnectionAvailable(getContext())){
                }else {
                }
            }
        });*/
    }

    private void getPaymentToken() {


        Map<String, Object> candidateMap = new HashMap<>();

//        candidateMap.put("country_id", mLoggedInUser.country_id);
//        candidateMap.put("province_id", mLoggedInUser.province_id);
//        candidateMap.put("city_id", mLoggedInUser.cityId);
//        candidateMap.put("address", mLoggedInUser.address);
//        candidateMap.put("zip", mLoggedInUser.zip);
        if(SharedPreferenceUtility.isJapanease()){
            if (CarryParkApplication.isIsJapaneaseLang())
            candidateMap.put("lang_id", ConstantProject.forJapaneaseResponse);

            // candidateMap.put("lang_id", sharedPreferences.getString("selected_lang",null));
        }else{
            candidateMap.put("lang_id", "en");
        }


        candidateMap.put("device_id",sharedPreferences.getString("device_id",null));
        String acess_token = GloablMethods.API_HEADER+ AppController.getString(getActivity(), "acess_token");
       /* apiService.getTokenFromPaymentAPI(acess_token,candidateMap).enqueue(new Callback<PaymentApiResponse>() {
            @Override
            public void onResponse(Call<PaymentApiResponse> call, Response<PaymentApiResponse> response) {
                Log.e(TAG,"------>" + "Response");
                Log.e(TAG,response.toString());
                Log.e("responseCode", String.valueOf(response.code()));
                Log.e("responseBody", String.valueOf(response.body()));
                String code = String.valueOf(response.code());

                *//*if(response.code() == 200){
                    progressDialog.dismiss();
                    Intent intent = new Intent(getApplicationContext(), VerifyActivity.class);
                    startActivity(intent);
                }else if(response.code() == 404){
                    progressDialog.dismiss();
                }else if(response.code() == 403){
                    progressDialog.dismiss();
                }*//*
                if(response.code() == 200){
                    Boolean status = response.body().getSuccess();
                    if(status == true){
                        String token = response.body().getData().getToken();
                        //InUseFragment ldf = new InUseFragment ();

                        //BottomSheetFragment_WebView bottomSheetDialog = BottomSheetFragment_WebView.newInstance();
                        //bottomSheetDialog.show(getActivity().getSupportFragmentManager(), "Bottom Sheet Dialog Fragment");

                        *//*Bundle args = new Bundle();
                        args.putString("key", "value");
                        BottomSheetFragment_WebView bottomSheetDialog = BottomSheetFragment_WebView.newInstance();
                        bottomSheetDialog.setArguments(args);
                        bottomSheetDialog.show(getActivity().getSupportFragmentManager(), "Bottom Sheet Dialog Fragment");*//*
                        targetFragment = new HtmlFragment();
                        replaceFragment(token);

                    }
                }else if(response.code() == 404){

                    Gson gson = new GsonBuilder().create();
                    PaymentApiResponse pojo = new PaymentApiResponse();
                    try {
                        pojo = gson.fromJson(response.errorBody().string(), PaymentApiResponse.class);
                    } catch (IOException e) { }
                }*//*else{
                    try {
                        JSONObject jObjError = new JSONObject(response.errorBody().string());
                        JSONObject meta = jObjError.getJSONObject("meta");
                        Snackbar snackbar = Snackbar
                                .make(getView(), meta.getString("message"), Snackbar.LENGTH_LONG);
                        snackbar.show();

                    } catch (Exception e) {
                        Log.d("exception",e.getMessage());
                    }
                }*//*



            }

            @Override
            public void onFailure(Call<PaymentApiResponse> call, Throwable t) {
                if(!Utils.isNetworkConnectionAvailable(CarryParkApplication.getCurrentContext())){
                    new AlertDialog.Builder(getContext())
                            .setTitle(R.string.alert)
                            .setMessage(R.string.no_internet_connection)
                            .setCancelable(false)
                            .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    // Whatever..
                                }
                            }).show();
                }else {
                }
            }
        });*/
    }

    //call callUnlockRequestAPI
    private void callUnlockRequestAPI(String deviceId) {



        Map<String, Object> candidateMap = new HashMap<>();

        candidateMap.put("device_id",deviceId);
        candidateMap.put("lock_status",1);
        if (SharedPreferenceUtility.isJapanease()) {
                candidateMap.put("lang_id", ConstantProject.forJapaneaseResponse);

        } else if (SharedPreferenceUtility.isEnglish()){
            candidateMap.put("lang_id", "en");
        }
        else if (SharedPreferenceUtility.isKorean()){
            candidateMap.put("lang_id", ConstantProject.forKoreanResponse);
        }
        else if (SharedPreferenceUtility.isChinease()){
            candidateMap.put("lang_id",  ConstantProject.forChineaseResponse);
        }
        String acess_token = GloablMethods.API_HEADER+ AppController.getString(getActivity(), "acess_token");
        apiService.UnlockingRequestAPI(acess_token,candidateMap).enqueue(new Callback<UnLockRequestApiResponse>() {
            @Override
            public void onResponse(Call<UnLockRequestApiResponse> call, Response<UnLockRequestApiResponse> response) {

                if(response!=null && response.code() == 200 && response.body().getSuccess()==true) {
                  //  Log.e("payment_require_flag",response.body().getData().getPaymentRequire().toString());
                    //sjn
                  //  isPaymentRequired = response.body().getData().getPaymentRequire();
                    /*if(response.body().getData().getPaymentRequire() == false){
                        targetFragment = new PaymentSuccessFragment();
                        replaceFragment(targetFragment);
                    }else {*/

                        String start = response.body().getData().getStartDate();
                        String end = response.body().getData().getEndDate();
                        if (response.body().getData().getAmount()!=null)
                        {
                            amount=response.body().getData().getAmount();
                        }
                        String bal_amount_frmAPI = String.valueOf(response.body().getData().getAmount());
                         // Date date_start = null,date_end = null; //2020-05-20T04:47:15.000000Z
     //tinu                  // SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSSSS");
                    SimpleDateFormat sdf = null;
                    SimpleDateFormat day = null;
                    SimpleDateFormat month = null;
                    SimpleDateFormat year = null;
                    SimpleDateFormat amOrpm = null;
                    SimpleDateFormat hh = null;
                    SimpleDateFormat mm = null;
                    try {
                        sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSSSSX", Locale.ENGLISH);
                        day = new SimpleDateFormat("d");
                        month = new SimpleDateFormat("M");
                        year = new SimpleDateFormat("y");
                        amOrpm = new SimpleDateFormat("a");
                        hh = new SimpleDateFormat("hh");
                        mm = new SimpleDateFormat("mm");
                    } catch (Exception e) {
                        e.printStackTrace();
                        sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSSSS");
                        day = new SimpleDateFormat("d");
                        month = new SimpleDateFormat("M");
                        year = new SimpleDateFormat("y");
                        amOrpm = new SimpleDateFormat("a");
                        hh = new SimpleDateFormat("hh");
                        mm = new SimpleDateFormat("mm");
                    }
                    try {
                            Date date_start;
                            date_start = sdf.parse(response.body().getData().getStartDate());
                            //SimpleDateFormat outputFormat = new SimpleDateFormat("yyyy-MMM-d,hh:mm a", Locale.ENGLISH);
                            SimpleDateFormat outputFormat = new SimpleDateFormat(getContext().getResources().getString(R.string.paymentDateFormat), Locale.ENGLISH);
                            // assuming a timezone in India




                            //outputFormat.setTimeZone(TimeZone.getTimeZone("Asia/Kolkata"));
                            outputFormat.setTimeZone(TimeZone.getDefault());

                            if (CarryParkApplication.isIsJapaneaseLang())
                            {

                               String  dateToStr= CommonMethod.dateFormatInJapanease(month.format(date_start),day.format(date_start),amOrpm.format(date_start),
                                        year.format(date_start),hh.format(date_start),mm.format(date_start));
                                tvStartTime.setText(dateToStr);

                            }
                            else
                            {
                                tvStartTime.setText(outputFormat.format(date_start).toString());

                            }

                            /////////////

                            ////////////
                            //System.out.println(outputFormat.format(date));
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }

                        try {
                            Date date_end;
                            date_end = sdf.parse(response.body().getData().getEndDate());
                            SimpleDateFormat outputFormat = new SimpleDateFormat(getContext().getResources().getString(R.string.paymentDateFormat), Locale.ENGLISH);
                            outputFormat.setTimeZone(TimeZone.getDefault());


                            if (CarryParkApplication.isIsJapaneaseLang())
                            {

                                String  dateToStr= CommonMethod.dateFormatInJapanease(month.format(date_end),day.format(date_end),amOrpm.format(date_end),
                                        year.format(date_end),hh.format(date_end),mm.format(date_end));
                                tvEndTime.setText(dateToStr);

                            }
                            else
                            {
                                tvEndTime.setText(outputFormat.format(date_end).toString());

                            }
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }


                        int hours = response.body().getData().getDuration() / 3600;
                        int minutes = (response.body().getData().getDuration() % 3600) / 60;
                        int seconds = response.body().getData().getDuration() % 60;

                        if (minutes > 0 || seconds > 0) {
                            rounded_hours = hours + 1;
                        }

                        String timeString = String.format("%02d:%02d:%02d", hours, minutes, seconds);

                        String time = timeString; //mm:ss
                        String[] units = time.split(":"); //will break the string up into an array
                        int hours_dur = Integer.parseInt(units[0]);//first element
                        int minutes_dur = Integer.parseInt(units[1]); //second element
                        int seconds_dur = Integer.parseInt(units[2]); //hours element
                        int duration = 60 * minutes + seconds; //add up our values
                        String h = String.valueOf(hours_dur) + " h ";
                        String m = String.valueOf(minutes_dur) + " m ";
                        String s = String.valueOf(seconds_dur) + " s ";
                        if (hours_dur>0)
                        {
                            tvDurationUsed.setText(h + m );
                        }
                        else {
                            tvDurationUsed.setText( m );
                        }


                        //int ratePerHr = response.body().getData().getAmount();
                        int ratePerHr = Integer.parseInt(sharedPreferences.getString("rate_per_hour", null));
                        int initialCharge = Integer.parseInt(sharedPreferences.getString("initial_charge", null));
                        //sharedPreferences.getString("rate_per_hour",null);
                        tvRatePerHour.setText("¥ " + sharedPreferences.getString("rate_per_hour", null));
                        //tvRatePerHour.setText("¥ "+response.body().getData().getAmount().toString());

                        //row usage amount
                        tvRatePerHourUsage.setText("¥ " + sharedPreferences.getString("rate_per_hour", null));
                        int hoursUsed = 0;
                        if (rounded_hours <= maxTime) {
                             hoursUsed = rounded_hours-1;
                            //new_total_amt = initialCharge + ratePerHr * rounded_hours;
                        }else if(rounded_hours > maxTime){
                            hoursUsed = maxTime-1;
                            //new_total_amt = initialCharge + ratePerHr * (maxTime-1);
                        }

                        String usedHours = String.valueOf(hoursUsed);
                        tvHourUsage.setText(usedHours+" H");
                        int amountUsage = ratePerHr * hoursUsed;
                        String usageAmount = String.valueOf(amountUsage);
                        tvUsageAmt.setText("¥ " + usageAmount);


                        // row
                        tvUsageAmtTotal.setText("¥ " + usageAmount);
                        String rounded_hrs = String.valueOf(rounded_hours);
                        tvInitialAmtTotal.setText("¥ " + sharedPreferences.getString("initial_charge", null));
                        int amtTotal = amountUsage + initialCharge;
                        String totalAmt = String.valueOf(amtTotal);
                        tvTotalAmtTotal.setText("¥ " + totalAmt);
                        int hrs_used = Integer.parseInt(rounded_hrs);
                                      tvResMul_3.setText("¥ " + sharedPreferences.getString("initial_charge", null));

                        tvFinalPayAmount.setText("¥ " + bal_amount_frmAPI);

                        if(isPaymentRequired.equals(false))
                            buttonPayToPaymentSuccess.setText(R.string.proceed);
                        else
                            buttonPayToPaymentSuccess.setText("Pay ¥ " + bal_amount_frmAPI);

                        unLockToken = response.body().getData().getToken();

                } else if (response.code() == 404) {
                    try {
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    //String msg = response.body().getMessage();

                }
            }
            @Override
            public void onFailure(Call<UnLockRequestApiResponse> call, Throwable t) {
                try {
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if(!Utils.isNetworkConnectionAvailable(CarryParkApplication.getCurrentContext())){
                    DialogManager.showAlertSingleActionDialog(CarryParkApplication.getCurrentActivity(),CarryParkApplication.getCurrentContext().getResources().getString(R.string.no_internet), "ok", new DialogManager.IUniActionDialogOnClickListener() {
                        @Override
                        public void onPositiveClick() {

                        }


                    });                }else {
                    DialogManager.showAlertSingleActionDialog(CarryParkApplication.getCurrentActivity(),t.getMessage(), "ok", new DialogManager.IUniActionDialogOnClickListener() {
                        @Override
                        public void onPositiveClick() {

                        }


                    });
                }
            }
        });
    }



    public void replaceFragment(String token) {


       /* //Put the value
        HtmlFragment ldf = new HtmlFragment ();
        Bundle args = new Bundle();
        args.putString("device_token", token);
        args.putLong("current_time_of_timer", currentTimeOfTimer);
        ldf.setArguments(args);


        //Inflate the fragment
        getFragmentManager().beginTransaction().add(R.id.fragment_container,ldf).commit();*/

        Intent intent = new Intent(getActivity(), BottomNavigation.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent.putExtra("page", ConstantProject.HtmlFragment);
        intent.putExtra("device_token",token);
        intent.putExtra("current_time_of_timer",currentTimeOfTimer);
        getActivity().startActivity(intent);


    }

    public void replaceFragment(Fragment someFragment) {

        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, someFragment);
        transaction.addToBackStack(null);
        transaction.commit();

    }

}
