package net.ui;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Bundle;
import android.provider.Settings;
import android.text.TextUtils;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
import android.widget.*;
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
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import java.text.SimpleDateFormat;
import java.util.*;

import static android.content.ContentValues.TAG;

public class LoginInNewDeviceActivity extends BaseActivity implements View.OnClickListener {
    TextView tvCreateAccnt, tvForgotPin;
    Button buttonLogin;
    ImageView imgBackArrow;
    EditText etEmailAddress, etDob, etPin;
    final Calendar myCalendar = Calendar.getInstance();

    ApiInterface apiService;
    String languageToLoad = "en";

    private SharedPreferences sharedPreferences;
    Calendar cal, cal1;
    LinearLayout llBackground;
    String dobForApi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (SharedPreferenceUtility.isJapanease()) {
            languageToLoad = "ja";
        } else if (SharedPreferenceUtility.isEnglish()) {
            languageToLoad = "en";
        } else if (SharedPreferenceUtility.isChinease()) {
            languageToLoad = ConstantProject.ChineaseLocali;
        } else if (SharedPreferenceUtility.isKorean()) {
            languageToLoad = "ko";
        }
        Locale locale = new Locale(languageToLoad);
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.locale = locale;
        getBaseContext().getResources().updateConfiguration(config,
                getBaseContext().getResources().getDisplayMetrics());

        setContentView(R.layout.activity_login_in_newdevice);
        CarryParkApplication.setCurrentContext(this);
        if (!Utils.isNetworkConnectionAvailable(getApplicationContext())) {
            /*new AlertDialog.Builder(getApplicationContext())
                    .setTitle(R.string.alert)
                    .setMessage(R.string.no_internet_connection)
                    .setCancelable(false)
                    .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            // Whatever..
                        }
                    }).show();*/
        }
        sharedPreferences = getSharedPreferences("carry_park", MODE_PRIVATE);
        apiService =
                ApiClient.getClient().create(ApiInterface.class);
        tvCreateAccnt = (TextView) findViewById(R.id.tv_create_accnt);
        imgBackArrow = (ImageView) findViewById(R.id.img_back_arrow);
        buttonLogin = (Button) findViewById(R.id.button_login);
        etEmailAddress = (EditText) findViewById(R.id.et_email_address);
        etDob = (EditText) findViewById(R.id.et_dob);
        etPin = (EditText) findViewById(R.id.et_pin);
        tvForgotPin = (TextView) findViewById(R.id.tv_forgot_pin);
        llBackground = (LinearLayout) findViewById(R.id.ll_bag_layout);
        tvCreateAccnt.setOnClickListener(this);
        imgBackArrow.setOnClickListener(this);
        buttonLogin.setOnClickListener(this);
        etDob.setOnClickListener(this);
        tvForgotPin.setOnClickListener(this);
        if (CarryParkApplication.isIsFromLoginasNewUser()) {
            tvForgotPin.setText(CarryParkApplication.getCurrentContext().getResources().getString(R.string.forgotPin));
        } else {
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
                attemptLogin();
                break;
            case R.id.et_dob:

                hideKeyboard(CarryParkApplication.getCurrentActivity());
                etPin.clearFocus();
                etEmailAddress.clearFocus();

                String myFormat = "yyyy-MM-dd"; //In which you need put here
                SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);

                Calendar c = Calendar.getInstance();
                Date today = c.getTime();
                String tody = sdf.format(today.getTime());

                DatePickerPopWin pickerPopWin = new DatePickerPopWin.Builder(LoginInNewDeviceActivity.this, new DatePickerPopWin.OnDatePickedListener() {
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
                pickerPopWin.showPopWin(LoginInNewDeviceActivity.this);



              /*  cal = Calendar.getInstance();
                cal1 = Calendar.getInstance();
                // TODO Auto-generated method stub
                DatePickerDialog dialog = new DatePickerDialog(LoginInNewDeviceActivity.this, new DatePickerDialog.OnDateSetListener() {

                    @Override
                    public void onDateSet(DatePicker arg0, int arg1, int arg2, int arg3) {
                        myCalendar.set(Calendar.YEAR, arg1);
                        myCalendar.set(Calendar.MONTH, arg2);
                        myCalendar.set(Calendar.DAY_OF_MONTH, arg3);

                        updateLabel();
                        // TODO Auto-generated method stub
                    }
                }, cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH));
                //dialog.getDatePicker().setMinDate(System.currentTimeMillis());
                cal1.add(Calendar.DAY_OF_MONTH, -1);
                dialog.getDatePicker().setMaxDate(cal1.getTimeInMillis());
                dialog.show();*/
                break;
            case R.id.tv_forgot_pin:
                // attemptResendPin();
                Intent intent1 = new Intent(LoginInNewDeviceActivity.this, ForgotPinActivity.class);
                startActivity(intent1);
                break;
        }
    }

    private void attemptResendPin() {
        etEmailAddress.setError(null);

        final String email = etEmailAddress.getText().toString();

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


        if (cancel) {
            focusView.requestFocus();
        } else {
            doResendPin(email);
        }
    }

    private void doResendPin(String email) {
        String deviceMacId = Settings.Secure.getString(LoginInNewDeviceActivity.this.getContentResolver(), Settings.Secure.ANDROID_ID);

        showBusyAnimation("");

        Map<String, Object> candidateMap = new HashMap<>();

//        candidateMap.put("country_id", mLoggedInUser.country_id);
//        candidateMap.put("province_id", mLoggedInUser.province_id);
//        candidateMap.put("city_id", mLoggedInUser.cityId);
//        candidateMap.put("address", mLoggedInUser.address);
//        candidateMap.put("zip", mLoggedInUser.zip);
        candidateMap.put("email", email);
        if (SharedPreferenceUtility.isJapanease()) {
            candidateMap.put("lang_id", ConstantProject.forJapaneaseResponse);

        } else if (SharedPreferenceUtility.isEnglish()) {
            candidateMap.put("lang_id", "en");
        } else if (SharedPreferenceUtility.isKorean()) {
            candidateMap.put("lang_id", ConstantProject.forKoreanResponse);
        } else if (SharedPreferenceUtility.isChinease()) {
            candidateMap.put("lang_id", ConstantProject.forChineaseResponse);
        }
        candidateMap.put("mac_id", deviceMacId);
        apiService.resendPIN(candidateMap).enqueue(new Callback<ResendPinResponse>() {
            @Override
            public void onResponse(Call<ResendPinResponse> call, Response<ResendPinResponse> response) {
                if (response.code() == 200) {
                    hideBusyAnimation();
                    String msg = String.valueOf(response.body().getMessage());
                    llBackground.setVisibility(View.VISIBLE);
                    DialogManager.showAlertSingleActionDialog(CarryParkApplication.getCurrentActivity(), CarryParkApplication.getCurrentContext().getResources().getString(R.string.OTPpromt), "ok", new DialogManager.IUniActionDialogOnClickListener() {
                        @Override
                        public void onPositiveClick() {
                            Intent intent = new Intent(LoginInNewDeviceActivity.this, LoginActivityNew.class);
                            startActivity(intent);
                        }
                    });


                } else {
                   // llBackground.setVisibility(View.VISIBLE);
                    DialogManager.showAlertSingleActionDialog(CarryParkApplication.getCurrentActivity(), response.body().getMessage(), "ok", new DialogManager.IUniActionDialogOnClickListener() {
                        @Override
                        public void onPositiveClick() {


                        }
                    });

                }
            }

            @Override
            public void onFailure(Call<ResendPinResponse> call, Throwable t) {
                hideBusyAnimation();
                if (!Utils.isNetworkConnectionAvailable(getApplicationContext())) {
                    DialogManager.showAlertSingleActionDialog(CarryParkApplication.getCurrentActivity(), CarryParkApplication.getCurrentContext().getResources().getString(R.string.no_internet), "ok", new DialogManager.IUniActionDialogOnClickListener() {
                        @Override
                        public void onPositiveClick() {

                        }


                    });                      /* new AlertDialog.Builder(getApplicationContext())
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
                    DialogManager.showAlertSingleActionDialog(CarryParkApplication.getCurrentActivity(), CarryParkApplication.getCurrentContext().getResources().getString(R.string.OperfailedExtnd), "ok", new DialogManager.IUniActionDialogOnClickListener() {
                        @Override
                        public void onPositiveClick() {

                        }


                    });
                }
            }
        });
    }

    //-----------------------
    @Override
    public void onBackPressed() {
        //Execute your code here
        Intent intent = new Intent(LoginInNewDeviceActivity.this, LoginActivityNew.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

        startActivity(intent);
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

        if (sdf.format(today).compareTo(sdf.format(selected_date)) < 0) {
            etDob.setText("");
        } else if (selected_date != null) {
            String dayOfTheWeek = (String) DateFormat.format("EEEE", selected_date); // Thursday
            String day = (String) DateFormat.format("d", selected_date); // 20
            String monthString = (String) DateFormat.format("MMM", selected_date); // Jun
            String monthNumber = (String) DateFormat.format("M", selected_date); // 06
            String year = (String) DateFormat.format("yyyy", selected_date); // 2013

            if (SharedPreferenceUtility.isJapanease()) {
                etDob.setText(year + "年" + monthNumber + "月" + day + "日");
            } else {
                etDob.setText(monthString + " " + day + "," + year);

            }

        }
        if ( sdf.format(today).compareTo(sdf.format(selected_date)) < 0) {
            etDob.setText("");
        } else {
            dobForApi = sdf.format(myCalendar.getTime());
        }
    }

    private void attemptLogin() {
        String deviceMacId = Settings.Secure.getString(LoginInNewDeviceActivity.this.getContentResolver(), Settings.Secure.ANDROID_ID);

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
            focusView = etDob;
            cancel = true;
            DialogManager.showAlertSingleActionDialog(CarryParkApplication.getCurrentActivity(), CarryParkApplication.getCurrentContext().getResources().getString(R.string.empty_dob), "ok", new DialogManager.IUniActionDialogOnClickListener() {
                @Override
                public void onPositiveClick() {

                }
            });
        } /*else if (!isPasswordValid(password)) {
            mPasswordView.setError(getString(R.string.password_grater_than_6));
            focusView = mPasswordView;
            cancel = true;
        }*/


        //sjn added
        else if (TextUtils.isEmpty(Pin)) {
            DialogManager.showAlertSingleActionDialog(CarryParkApplication.getCurrentActivity(), getString(R.string.pinEmpty), "ok", new DialogManager.IUniActionDialogOnClickListener() {
                @Override
                public void onPositiveClick() {

                }
            });
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
        String deviceMacId = Settings.Secure.getString(LoginInNewDeviceActivity.this.getContentResolver(), Settings.Secure.ANDROID_ID);

        showBusyAnimation("");

        Map<String, Object> candidateMap = new HashMap<>();
        candidateMap.put("mac_id", deviceMacId);
//        candidateMap.put("country_id", mLoggedInUser.country_id);
//        candidateMap.put("province_id", mLoggedInUser.province_id);
//        candidateMap.put("city_id", mLoggedInUser.cityId);
//        candidateMap.put("address", mLoggedInUser.address);
//        candidateMap.put("zip", mLoggedInUser.zip);
        candidateMap.put("email", email);
        candidateMap.put("dob", dobForApi);
        candidateMap.put("pin", pin);
        candidateMap.put("client_id", "1");
        candidateMap.put("client_secret", "wrxHoKmixc2vduglSqbaRYbw9Nsqz1B2vFRYFd6A");
        candidateMap.put("grant_type", "password");
        if (SharedPreferenceUtility.isJapanease()) {
            candidateMap.put("lang_id", ConstantProject.forJapaneaseResponse);

        } else if (SharedPreferenceUtility.isEnglish()) {
            candidateMap.put("lang_id", "en");
        } else if (SharedPreferenceUtility.isKorean()) {
            candidateMap.put("lang_id", ConstantProject.forKoreanResponse);
        } else if (SharedPreferenceUtility.isChinease()) {
            candidateMap.put("lang_id", ConstantProject.forChineaseResponse);
        }
        //candidateMap.put("lang_id", "1");
        /*candidateMap.put("mobile", mobilenumber);
        candidateMap.put("country_id", "91");
        candidateMap.put("mac_id", "54545858745454545");*/
        Log.d("login_params", "map==" + candidateMap);
        apiService.login(candidateMap).enqueue(new Callback<UserLoginResponse>() {
            @Override
            public void onResponse(Call<UserLoginResponse> call, Response<UserLoginResponse> response) {
                Log.e(TAG, "------>" + "Response");
                Log.e(TAG, response.toString());
                Log.e("login_responseCode", String.valueOf(response.code()));
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
                if (response.code() == 200 && response.body().getData() != null) {
                    hideBusyAnimation();
                    Boolean status = response.body().getSuccess();
                    if (status == true) {
                        Log.e("login_accessoken", response.body().getData().getAccessToken());
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putString("access_token", response.body().getData().getAccessToken());
                        editor.putString("IS_LOGGED_IN", "TRUE");
                        editor.apply();
                        //sjn
                        AppController.setString(getApplicationContext(), "acess_token", response.body().getData().getAccessToken());
                        Intent intent = new Intent(CarryParkApplication.getCurrentContext(), BottomNavigation.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        intent.putExtra("page", ConstantProject.SplashActivity);
                        startActivity(intent);
                        finish();
                    } else if (status == false) {
                        String msg = response.body().getMessage();
                        llBackground.setVisibility(View.VISIBLE);
                        DialogManager.showAlertSingleActionDialog(CarryParkApplication.getCurrentActivity(), response.body().getMessage(), "ok", new DialogManager.IUniActionDialogOnClickListener() {
                            @Override
                            public void onPositiveClick() {
                                Intent intent = new Intent(LoginInNewDeviceActivity.this, LoginActivityNew.class);
                                startActivity(intent);

                            }
                        });

                    }
                } else if (response.code() == 200 && response.body().getData() == null) {
                    hideBusyAnimation();
                  //  llBackground.setVisibility(View.VISIBLE);
                    DialogManager.showAlertSingleActionDialog(CarryParkApplication.getCurrentActivity(), response.body().getMessage(), "ok", new DialogManager.IUniActionDialogOnClickListener() {
                        @Override
                        public void onPositiveClick() {
                            Intent intent = new Intent(LoginInNewDeviceActivity.this, LoginActivityNew.class);
                            startActivity(intent);

                        }
                    });

                } else if (response.code() == 404) {
                    hideBusyAnimation();
                   // llBackground.setVisibility(View.VISIBLE);
                    DialogManager.showUniActionDialog(CarryParkApplication.getCurrentActivity(), CarryParkApplication.getCurrentContext().getResources().getString(R.string.validation_error), "ok", new DialogManager.IUniActionDialogOnClickListener() {
                        @Override
                        public void onPositiveClick() {
                           /* Intent intent = new Intent(LoginInNewDeviceActivity.this, LoginActivityNew.class);
                            startActivity(intent);*/
                        }
                    });
                }
            }

            @Override
            public void onFailure(Call<UserLoginResponse> call, Throwable t) {
                hideBusyAnimation();
                if (!Utils.isNetworkConnectionAvailable(getApplicationContext())) {
                    DialogManager.showAlertSingleActionDialog(CarryParkApplication.getCurrentActivity(), CarryParkApplication.getCurrentContext().getResources().getString(R.string.no_internet), "ok", new DialogManager.IUniActionDialogOnClickListener() {
                        @Override
                        public void onPositiveClick() {

                        }


                    });                       /*new AlertDialog.Builder(getApplicationContext())
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
                    DialogManager.showAlertSingleActionDialog(CarryParkApplication.getCurrentActivity(), CarryParkApplication.getCurrentContext().getResources().getString(R.string.OperfailedExtnd), "ok", new DialogManager.IUniActionDialogOnClickListener() {
                        @Override
                        public void onPositiveClick() {

                        }


                    });
                }
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
