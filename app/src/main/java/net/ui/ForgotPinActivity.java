package net.ui;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.widget.*;
import android.text.TextUtils;
import android.view.View;

import com.bruce.pickerview.popwindow.DatePickerPopWin;
import net.CarryParkApplication;
import net.others.BaseActivity;
import net.others.ConstantProject;
import net.others.DialogManager;
import net.others.SharedPreferenceUtility;
import net.rest.ApiClient;
import net.rest.ApiInterface;
import net.rest.Utils;
import net.rest.global.AppController;
import net.rest.model.ResendPinResponse;
import net.rest.model.UserLoginResponse;
import net.simplifiedcoding.carrypark.R;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ForgotPinActivity extends BaseActivity implements View.OnClickListener {
    TextView tvCreateAccnt, tvForgotPin;
    Button buttonLogin;
    ImageView imgBackArrow;
    EditText etEmailAddress, etDob, etPin;
    final Calendar myCalendar = Calendar.getInstance();
    ApiInterface apiService;
    String languageToLoad = "en";
    private SharedPreferences sharedPreferences;
    LinearLayout llResendOtp,ll_bag_layout;
    Calendar cal,cal1;
    String dobForApi="";
    //public static String carryPark="carry_park";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

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
        getBaseContext().getResources().updateConfiguration(config,
                getBaseContext().getResources().getDisplayMetrics());

        setContentView(R.layout.activity_login);
        CarryParkApplication.setCurrentContext(this);

        sharedPreferences = getSharedPreferences("carry_park", MODE_PRIVATE);
        apiService =
                ApiClient.getClient().create(ApiInterface.class);
        tvCreateAccnt = (TextView) findViewById(R.id.tv_create_accnt);
        imgBackArrow = (ImageView) findViewById(R.id.img_back_arrow);
        buttonLogin = (Button) findViewById(R.id.button_login);
        ll_bag_layout = (LinearLayout) findViewById(R.id.ll_bag_layout);

        etEmailAddress = (EditText) findViewById(R.id.et_email_address);
        etDob = (EditText) findViewById(R.id.et_dob);
        etPin = (EditText) findViewById(R.id.et_pin);
        tvForgotPin = (TextView) findViewById(R.id.tv_forgot_pin);
        tvCreateAccnt.setOnClickListener(this);
        imgBackArrow.setOnClickListener(this);
        buttonLogin.setOnClickListener(this);
        etDob.setOnClickListener(this);
        tvForgotPin.setOnClickListener(this);
        llResendOtp = (LinearLayout) findViewById(R.id.ll_resendOtp);
        llResendOtp.setVisibility(View.GONE);
        if (CarryParkApplication.isIsFromLoginasNewUser())
        {
            tvForgotPin.setText(CarryParkApplication.getCurrentContext().getResources().getString(R.string.forgotPin));
        }
        else
        {
            tvForgotPin.setText(CarryParkApplication.getCurrentContext().getResources().getString(R.string.btnResendOtp));

        }

        //アカウント作成
        hideKeyboard(CarryParkApplication.getCurrentActivity());

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_create_accnt:
                Intent intent = new Intent(getApplicationContext(), RegistrationActivity.class);
                startActivity(intent);
                break;
            case R.id.img_back_arrow:
                onBackPressed();
                break;
            case R.id.button_login:
                /*intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);*/
                attemptResendPin();
                break;
            case R.id.et_dob:
                hideKeyboard(CarryParkApplication.getCurrentActivity());
                String myFormat = "yyyy-MM-dd"; //In which you need put here
                SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);

                Calendar c = Calendar.getInstance();
                Date today = c.getTime();
                String tody = sdf.format(today.getTime());

                etEmailAddress.clearFocus();
                etPin.clearFocus();
                DatePickerPopWin pickerPopWin = new DatePickerPopWin.Builder(ForgotPinActivity.this, new DatePickerPopWin.OnDatePickedListener() {
                    @Override
                    public void onDatePickCompleted(int year, int month, int day, String dateDesc) {
                        myCalendar.set(Calendar.YEAR, year);
                        myCalendar.set(Calendar.MONTH, month - 1);
                        myCalendar.set(Calendar.DAY_OF_MONTH, day);
                        updateLabel();
                    }
                }).textConfirm(CarryParkApplication.getCurrentContext().getResources().getString(R.string.done)) //text of confirm button
                        .textCancel(CarryParkApplication.getCurrentContext().getResources().getString(R.string.cancel)) //text of cancel button
                        .btnTextSize(16) // button text size
                        .viewTextSize(25) // pick view text size
                        .colorCancel(Color.parseColor("#f2f7fa")) //color of cancel button
                        .colorConfirm(Color.parseColor("#2496EC"))//color of confirm button
                        .minYear(1900) //min year in loop
                        .maxYear(2022) // max year in loop
                        .dateChose(tody) // date chose when init popwindow
                        .build();
                pickerPopWin.showPopWin(ForgotPinActivity.this);

                break;
            case R.id.tv_forgot_pin:
                attemptResendPin();
                break;
        }
    }

    private void attemptResendPin() {
        etEmailAddress.setError(null);

        final String email = etEmailAddress.getText().toString();

        boolean cancel = false,isdob=false;
        View focusView = null;

        // Check for a valid email address.
        if (TextUtils.isEmpty(email)) {
            DialogManager.showAlertSingleActionDialog(CarryParkApplication.getCurrentActivity(), getString(R.string.invalidEmail), "ok", new DialogManager.IUniActionDialogOnClickListener() {
                @Override
                public void onPositiveClick() {

                }
            });
            focusView = etEmailAddress;
            cancel = true;

        } else if (!isEmailValid(email)) {
            DialogManager.showAlertSingleActionDialog(CarryParkApplication.getCurrentActivity(), getString(R.string.invalidEmail), "ok", new DialogManager.IUniActionDialogOnClickListener() {
                @Override
                public void onPositiveClick() {

                }
            });
            focusView = etEmailAddress;
            cancel = true;

        }
        else if (TextUtils.isEmpty(etDob.getText().toString()))
        {
            isdob=true;
           // cancel = true;
           DialogManager.showAlertSingleActionDialog(CarryParkApplication.getCurrentActivity(), CarryParkApplication.getCurrentContext().getResources().getString(R.string.empty_dob), "ok", new DialogManager.IUniActionDialogOnClickListener() {
               @Override
               public void onPositiveClick() {

               }
           });
        }


        if (!isdob)
        {
            if (cancel)  {
                focusView.requestFocus();
            } else {
                doResendPin(email);
            }
        }

    }

    private void doResendPin(String email) {
        CarryParkApplication.setDob(dobForApi);

       showBusyAnimation("");

        Map<String, Object> candidateMap = new HashMap<>();

//        candidateMap.put("country_id", mLoggedInUser.country_id);
//        candidateMap.put("province_id", mLoggedInUser.province_id);
//        candidateMap.put("city_id", mLoggedInUser.cityId);
//        candidateMap.put("address", mLoggedInUser.address);
//        candidateMap.put("zip", mLoggedInUser.zip);
        candidateMap.put("dob", dobForApi);
        candidateMap.put("email", email);
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

        /*candidateMap.put("mobile", mobilenumber);
        candidateMap.put("country_id", "91");
        candidateMap.put("mac_id", "54545858745454545");*/
        apiService.forgotPin(candidateMap).enqueue(new Callback<ResendPinResponse>() {
            @Override
            public void onResponse(Call<ResendPinResponse> call, Response<ResendPinResponse> response) {
                if(response.code() == 200){
                    hideBusyAnimation();

                    if (response.body().getSuccess())
                    {
                        String msg = String.valueOf(response.body().getMessage());
                        llResendOtp.setVisibility(View.VISIBLE);
                        ll_bag_layout.setVisibility(View.VISIBLE);

                        DialogManager.showUniActionDialog(CarryParkApplication.getCurrentActivity(), getResources().getString(R.string.OTPpromt), "ok", new DialogManager.IUniActionDialogOnClickListener() {
                            @Override
                            public void onPositiveClick() {
                                Intent intent =new Intent(ForgotPinActivity.this,LoginActivityNew.class);
                                startActivity(intent);
                            }
                        });

                    }
                    else {
                        DialogManager.showAlertSingleActionDialog(CarryParkApplication.getCurrentActivity(),response.body().getMessage(), "ok", new DialogManager.IUniActionDialogOnClickListener() {
                            @Override
                            public void onPositiveClick() {

                            }
                        });
                    }




                }else{
                    DialogManager.showUniActionDialog(CarryParkApplication.getCurrentActivity(), response.body().getMessage(), "ok", new DialogManager.IUniActionDialogOnClickListener() {
                        @Override
                        public void onPositiveClick() {
                           /* Intent intent = new Intent(LoginInNewDeviceActivity.this, LoginActivityNew.class);
                            startActivity(intent);*/
                        }
                    });

                }
            }

            @Override
            public void onFailure(Call<ResendPinResponse> call, Throwable t) {
                hideBusyAnimation();
                if (!Utils.isNetworkConnectionAvailable(getApplicationContext())) {
                    DialogManager.showAlertSingleActionDialog(CarryParkApplication.getCurrentActivity(),CarryParkApplication.getCurrentContext().getResources().getString(R.string.OperfailedExtnd), "ok", new DialogManager.IUniActionDialogOnClickListener() {
                        @Override
                        public void onPositiveClick() {

                        }


                    });                   /* new AlertDialog.Builder(getApplicationContext())
                            .setTitle(R.string.alert)
                            .setMessage(R.string.no_internet_connection)
                            .setCancelable(false)
                            .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    // Whatever..
                                }
                            }).show();*/
                } else {
                    DialogManager.showAlertSingleActionDialog(CarryParkApplication.getCurrentActivity(),CarryParkApplication.getCurrentContext().getResources().getString(R.string.OperfailedExtnd), "ok", new DialogManager.IUniActionDialogOnClickListener() {
                        @Override
                        public void onPositiveClick() {

                        }


                    });                }
            }
        });
    }

    //-----------------------
    @Override
    public void onBackPressed() {
        Intent intent =new Intent(ForgotPinActivity.this,LoginActivityNew.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

        startActivity(intent);
        //Execute your code here
        finish();
    }

    /*DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
            // TODO Auto-generated method stub
            myCalendar.set(Calendar.YEAR, year);
            myCalendar.set(Calendar.MONTH, month);
            myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            updateLabel();
        }
    };*/

    private void updateLabel() {

        String myFormat = "yyyy-MM-dd"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);

        Calendar c = Calendar.getInstance();
        Date today = c.getTime();
        Date selected_date = myCalendar.getTime();

        if ( sdf.format(today).compareTo(sdf.format(selected_date)) < 0) {
            etDob.setText("");
        }
        else if (selected_date!=null )
        {
            String dayOfTheWeek = (String) DateFormat.format("EEEE", selected_date); // Thursday
            String day          = (String) DateFormat.format("d",   selected_date); // 20
            String monthString  = (String) DateFormat.format("MMM",  selected_date); // Jun
            String monthNumber  = (String) DateFormat.format("M",   selected_date); // 06
            String year         = (String) DateFormat.format("yyyy", selected_date); // 2013

           /* Log.e("Da,dayOfTheWeek",dayOfTheWeek);
            Log.e("Da,day",day);
            Log.e("Da,monthString",monthString);
            Log.e("Da,monthNumber",monthNumber);
            Log.e("Da,year",year);
*/
            if (SharedPreferenceUtility.isJapanease())
            {
                etDob.setText(year+"年"+monthNumber+"月"+day+"日");
            }
            else {
                etDob.setText(monthString+" "+day+","+year);

            }

        }
        if ( sdf.format(today).compareTo(sdf.format(selected_date)) < 0) {
            etDob.setText("");
        } else {
            dobForApi = sdf.format(myCalendar.getTime());
        }

       /* String myFormat = "yyyy-MM-dd"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);

        etDob.setText(sdf.format(myCalendar.getTime()));*/
    }

    private void attemptLogin() {

        // Reset errors.
        etEmailAddress.setError(null);
        etDob.setError(null);
        etPin.setError(null);

        // Store values at the time of the login attempt.
        final String email = etEmailAddress.getText().toString();
        final String dob = etDob.getText().toString();
        final String Pin = etPin.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid email address.
        if (TextUtils.isEmpty(email)) {
            DialogManager.showAlertSingleActionDialog(CarryParkApplication.getCurrentActivity(), getString(R.string.invalidEmail), "ok", new DialogManager.IUniActionDialogOnClickListener() {
                @Override
                public void onPositiveClick() {

                }
            });
            focusView = etEmailAddress;
            cancel = true;
        } else if (!isEmailValid(email)) {
            DialogManager.showAlertSingleActionDialog(CarryParkApplication.getCurrentActivity(), getString(R.string.invalidEmail), "ok", new DialogManager.IUniActionDialogOnClickListener() {
                @Override
                public void onPositiveClick() {

                }
            });

            focusView = etEmailAddress;
            cancel = true;
        }

        // Check for a valid password, if the user entered one.
        else if (TextUtils.isEmpty(dob)) {
            etDob.setError(getString(R.string.empty_dob));
            focusView = etDob;
            cancel = true;
        } /*else if (!isPasswordValid(password)) {
            mPasswordView.setError(getString(R.string.password_grater_than_6));
            focusView = mPasswordView;
            cancel = true;
        }*/


        //sjn added
        else if (TextUtils.isEmpty(Pin)) {
            etPin.setError(getString(R.string.pinEmpty));
            focusView = etPin;
            cancel = true;
        }

        if (cancel) {
            focusView.requestFocus();
        } else {
            doLogin(email, dob, Pin);
        }
    }

    private void doLogin(String email, String dob, String pin) {

       showBusyAnimation("");

        Map<String, Object> candidateMap = new HashMap<>();

//        candidateMap.put("country_id", mLoggedInUser.country_id);
//        candidateMap.put("province_id", mLoggedInUser.province_id);
//        candidateMap.put("city_id", mLoggedInUser.cityId);
//        candidateMap.put("address", mLoggedInUser.address);
//        candidateMap.put("zip", mLoggedInUser.zip);
        candidateMap.put("email",email);
        candidateMap.put("dob", dob);
        candidateMap.put("pin", pin);
        candidateMap.put("client_id", "1");
        candidateMap.put("client_secret", "wrxHoKmixc2vduglSqbaRYbw9Nsqz1B2vFRYFd6A");
        candidateMap.put("grant_type", "password");
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
        //candidateMap.put("lang_id", "1");
        /*candidateMap.put("mobile", mobilenumber);
        candidateMap.put("country_id", "91");
        candidateMap.put("mac_id", "54545858745454545");*/
        apiService.login(candidateMap).enqueue(new Callback<UserLoginResponse>() {
            @Override
            public void onResponse(Call<UserLoginResponse> call, Response<UserLoginResponse> response) {
                String code = String.valueOf(response.code());

                /*if(response.code() == 200){
                    progressDialog.dismiss();
                    Intent intent = new Intent(getApplicationContext(), VerifyActivity.class);
                    startActivity(intent);
                }else if(response.code() == 404){
                    progressDialog.dismiss();
                }else if(response.code() == 403){
                    progressDialog.dismiss();
                }*/
                if(response.code() == 200 && response.body().getData() != null){
                    hideBusyAnimation();

                    Boolean status = response.body().getSuccess();
                    if(status == true){
                        SharedPreferences.Editor editor=sharedPreferences.edit();
                        editor.putString("access_token",response.body().getData().getAccessToken());
                        editor.putString("IS_LOGGED_IN","TRUE");
                        editor.apply();
                        //sjn
                        AppController.setString(getApplicationContext(),"acess_token", response.body().getData().getAccessToken());
                        Intent intent = new Intent(CarryParkApplication.getCurrentContext(), BottomNavigation.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        intent.putExtra("page", ConstantProject.ScanQRCodeFragment);
                        startActivity(intent);
                        finish();
                    }/*else if(status == false){
                        String msg = response.body().getMessage();
                    }*/
                }else if(response.code() == 200 && response.body().getData() == null) {
                   hideBusyAnimation();

                    String msg = response.body().getMessage();
                    DialogManager.showAlertSingleActionDialog(CarryParkApplication.getCurrentActivity(),msg, "ok", new DialogManager.IUniActionDialogOnClickListener() {
                        @Override
                        public void onPositiveClick() {

                        }
                    });
                              } else if(response.code() == 404){
                    hideBusyAnimation();

                    DialogManager.showAlertSingleActionDialog(CarryParkApplication.getCurrentActivity(),response.body().getMessage(), "ok", new DialogManager.IUniActionDialogOnClickListener() {
                        @Override
                        public void onPositiveClick() {

                        }
                    });

                }
            }

            @Override
            public void onFailure(Call<UserLoginResponse> call, Throwable t) {
                hideBusyAnimation();
                if(!Utils.isNetworkConnectionAvailable(getApplicationContext())){
                    DialogManager.showAlertSingleActionDialog(CarryParkApplication.getCurrentActivity(),CarryParkApplication.getCurrentContext().getResources().getString(R.string.OperfailedExtnd), "ok", new DialogManager.IUniActionDialogOnClickListener() {
                        @Override
                        public void onPositiveClick() {

                        }


                    });                    /*new AlertDialog.Builder(getApplicationContext())
                            .setTitle(R.string.alert)
                            .setMessage(R.string.no_internet_connection)
                            .setCancelable(false)
                            .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    // Whatever..
                                }
                            }).show();*/
                }else {
                    DialogManager.showAlertSingleActionDialog(CarryParkApplication.getCurrentActivity(),CarryParkApplication.getCurrentContext().getResources().getString(R.string.OperfailedExtnd), "ok", new DialogManager.IUniActionDialogOnClickListener() {
                        @Override
                        public void onPositiveClick() {

                        }


                    });                }
            }
        });
    }

    private boolean isEmailValid(String email) {
        return (email.contains("@") && email.contains("."));
    }

    /*@Override
    public void onResume() {
        super.onResume();

        if(!Utils.isNetworkConnectionAvailable(getApplicationContext())){
            new AlertDialog.Builder(getApplicationContext())
                    .setTitle(R.string.alert)
                    .setMessage(R.string.no_internet_connection)
                    .setCancelable(false)
                    .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            // Whatever..
                        }
                    }).show();
        }
    }*/

    @Override
    public void applyOverrideConfiguration(Configuration overrideConfiguration) {
        if (overrideConfiguration != null) {
            int uiMode = overrideConfiguration.uiMode;
            overrideConfiguration.setTo(getBaseContext().getResources().getConfiguration());
            overrideConfiguration.uiMode = uiMode;
        }
        super.applyOverrideConfiguration(overrideConfiguration);
    }
}

