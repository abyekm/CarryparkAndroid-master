package net.ui;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.provider.Settings;
import android.view.KeyEvent;
import android.view.inputmethod.InputMethodManager;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.core.app.ActivityCompat;
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

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivityNew extends BaseActivity implements View.OnClickListener{
    Button buttonLogin;
    ImageView imgBackArrow;
    EditText etPin1,etPin2,etPin3,etPin4;
    ApiInterface apiService;
    ImageView  iv_icon;

    TextView tvCreateAccnt,tvForgotPin,tv_login_as_different_user;
    private SharedPreferences sharedPreferences;
    String languageToLoad = "en";
    TextView tvResendOTP;
    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };
   // TextView tv_searchNearestLoc;
    //TextView tvResendOTP;
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
        this.setContentView(R.layout.activity_resendotp);
        //setContentView(R.layout.activity_login_new);
        CarryParkApplication.setCurrentContext(LoginActivityNew.this);
        sharedPreferences=getSharedPreferences("carry_park",MODE_PRIVATE);
        apiService =
                ApiClient.getClient().create(ApiInterface.class);
        buttonLogin = (Button)findViewById(R.id.button_login);
       // tvCreateAccnt = (TextView)findViewById(R.id.tv_create_accnt_login_new);
        tvForgotPin = (TextView)findViewById(R.id.tv_forgot_pin);
        tv_login_as_different_user = (TextView)findViewById(R.id.tv_login_as_different_user);
        imgBackArrow = (ImageView)findViewById(R.id.img_back_arrow);
        etPin1 = (EditText) findViewById(R.id.et_pin1);
        etPin2 = (EditText) findViewById(R.id.et_pin2);
        etPin3 = (EditText) findViewById(R.id.et_pin3);
        etPin4 = (EditText) findViewById(R.id.et_pin4);
        tvResendOTP =(TextView)findViewById(R.id.tv_resendOTP);
        iv_icon = (ImageView) findViewById(R.id.iv_icon);

        //  tvResendOTP =(TextView)findViewById(R.id.tv_resendOTP);
        CarryParkApplication.setIsEnglishLang(SharedPreferenceUtility.isEnglish());
        CarryParkApplication.setIsJapaneaseLang(SharedPreferenceUtility.isJapanease());

        buttonLogin.setOnClickListener(this);
        imgBackArrow.setOnClickListener(this);
       // tvCreateAccnt.setOnClickListener(this);
        tvForgotPin.setOnClickListener(this);
        tv_login_as_different_user.setOnClickListener(this);
        EditText[] edit = {etPin1, etPin2, etPin3, etPin4};

        etPin1.addTextChangedListener(new GenericTextWatcher(etPin1, edit));
        etPin2.addTextChangedListener(new GenericTextWatcher(etPin2, edit));
        etPin3.addTextChangedListener(new GenericTextWatcher(etPin3, edit));
        etPin4.addTextChangedListener(new GenericTextWatcher(etPin4, edit));

        etPin1.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if(keyCode == KeyEvent.KEYCODE_DEL){
                    //on backspace
                    etPin1.setText("");
                }
                return false;
            }
        });
        etPin2.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if(keyCode == KeyEvent.KEYCODE_DEL){
                    //on backspace
                    etPin2.setText("");
                    etPin1.requestFocus();
                }
                return false;
            }
        });
        etPin3.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if(keyCode == KeyEvent.KEYCODE_DEL){
                    //on backspace
                    etPin3.setText("");
                    etPin2.requestFocus();
                }
                return false;
            }
        });
        etPin4.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if(keyCode == KeyEvent.KEYCODE_DEL){
                    //on backspace
                    etPin4.setText("");
                    etPin3.requestFocus();
                }
                return false;
            }
        });
      //  tv_searchNearestLoc = (TextView)findViewById(R.id.tv_searchNearestLoc);
       // tv_searchNearestLoc.setOnClickListener(this);

        // show the resend pin only for the first time before pin verified
        SharedPreferences shared = getSharedPreferences("carry_park", MODE_PRIVATE);
        String isPinVerified = shared.getString("hasVerifiedPinEntered", "");


        SharedPreferenceUtility.saveIsCheckedLockerSize(false);
        tvResendOTP.setVisibility(View.GONE);
        if (SharedPreferenceUtility.isPinVerifyied())
        {
            tvResendOTP.setVisibility(View.GONE);
        }
        else {

            if (SharedPreferenceUtility.getEmailId()!=null && !SharedPreferenceUtility.getEmailId().isEmpty())
                tvResendOTP.setVisibility(View.VISIBLE);
        }

        tvResendOTP.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (SharedPreferenceUtility.getEmailId()!=null && !SharedPreferenceUtility.getEmailId().isEmpty())
                {
                    doResendPin(SharedPreferenceUtility.getEmailId());

                }
                else
                {
                    DialogManager.showUniActionDialog(CarryParkApplication.getCurrentActivity(), CarryParkApplication.getCurrentContext().getResources().getString(R.string.not_reg), "ok", new DialogManager.IUniActionDialogOnClickListener() {
                        @Override
                        public void onPositiveClick() {

                        }
                    });
                }
            }
        });

        hideKeyboard(CarryParkApplication.getCurrentActivity());
        verifyStoragePermissions(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.button_login:
                    attemptVerify();

                break;
            case R.id.img_back_arrow:
                onBackPressed();
                break;
           /* case R.id.tv_create_accnt_login_new:
                Intent intent = new Intent(getApplicationContext(), RegistrationActivity.class);
                startActivity(intent);
                break;*/
            case R.id.tv_forgot_pin:
                CarryParkApplication.setIsFromLoginasNewUser(false);
                Intent intent = new Intent(getApplicationContext(), ForgotPinActivity.class);
                startActivity(intent);
                break;
            case R.id.tv_login_as_different_user:
                CarryParkApplication.setIsFromLoginasNewUser(true);
                intent = new Intent(getApplicationContext(), LoginInNewDeviceActivity.class);
                startActivity(intent);
                break;
                 /*intent = new Intent(getApplicationContext(), RegistrationActivity.class);
                startActivity(intent);*/
            case R.id.tv_searchNearestLoc:
              /*  intent = new Intent(getApplicationContext(), MapsActivity.class);
                startActivity(intent);
*/

                break;

        }
    }

    @Override
    public void onBackPressed() {
        //Execute your code here
        finish();
    }
    public static void verifyStoragePermissions(Activity activity) {
        // Check if we have write permission
        int permission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if (permission != PackageManager.PERMISSION_GRANTED) {
            // We don't have permission so prompt the user
            ActivityCompat.requestPermissions(
                    activity,
                    PERMISSIONS_STORAGE,
                    REQUEST_EXTERNAL_STORAGE
            );
        }
    }
    private void doResendPin(String email) {

        showBusyAnimation("");

        Map<String, Object> candidateMap = new HashMap<>();

//        candidateMap.put("country_id", mLoggedInUser.country_id);
//        candidateMap.put("province_id", mLoggedInUser.province_id);
//        candidateMap.put("city_id", mLoggedInUser.cityId);
//        candidateMap.put("address", mLoggedInUser.address);
//        candidateMap.put("zip", mLoggedInUser.zip);
        candidateMap.put("dob",CarryParkApplication.getDob());
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
        apiService.resendPIN(candidateMap).enqueue(new Callback<ResendPinResponse>() {
            @Override
            public void onResponse(Call<ResendPinResponse> call, Response<ResendPinResponse> response) {
                if(response.code() == 200){
                    hideBusyAnimation();
                    if (response.body().getSuccess())
                    {
                        DialogManager.showUniActionDialog(CarryParkApplication.getCurrentActivity(), getResources().getString(R.string.OTPpromt), "ok", new DialogManager.IUniActionDialogOnClickListener() {
                            @Override
                            public void onPositiveClick() {

                            }
                        });
                    }
                    else {
                        DialogManager.showUniActionDialog(CarryParkApplication.getCurrentActivity(), response.body().getMessage(), "ok", new DialogManager.IUniActionDialogOnClickListener() {
                            @Override
                            public void onPositiveClick() {

                            }
                        });
                    }




                }else{
                    DialogManager.showUniActionDialog(CarryParkApplication.getCurrentActivity(), getResources().getString(R.string.validation_error), "ok", new DialogManager.IUniActionDialogOnClickListener() {
                        @Override
                        public void onPositiveClick() {

                        }
                    });
                }
            }

            @Override
            public void onFailure(Call<ResendPinResponse> call, Throwable t) {
                hideBusyAnimation();
                if (!Utils.isNetworkConnectionAvailable(CarryParkApplication.getCurrentContext())) {
                    DialogManager.showAlertSingleActionDialog(CarryParkApplication.getCurrentActivity(),CarryParkApplication.getCurrentContext().getResources().getString(R.string.OperfailedExtnd), "ok", new DialogManager.IUniActionDialogOnClickListener() {
                        @Override
                        public void onPositiveClick() {

                        }


                    });
                   /* new AlertDialog.Builder(getApplicationContext())
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


                    });
                }
            }
        });
    }

    private void attemptVerify() {

        // Reset errors.
        etPin1.setError(null);
        etPin2.setError(null);
        etPin3.setError(null);
        etPin4.setError(null);

        // Store values at the time of the login attempt.
        final String pin1 = etPin1.getText().toString();
        final String pin2 = etPin2.getText().toString();
        final String pin3 = etPin3.getText().toString();
        final String pin4 = etPin4.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid password, if the user entered one.
        if (TextUtils.isEmpty(pin1)) {
            DialogManager.showAlertSingleActionDialog(CarryParkApplication.getCurrentActivity(), getString(R.string.pinLogValid), "ok", new DialogManager.IUniActionDialogOnClickListener() {
                @Override
                public void onPositiveClick() {

                }
            });
            focusView = etPin1;
            cancel = true;
        } /*else if (!isPasswordValid(password)) {
            mPasswordView.setError(getString(R.string.password_grater_than_6));
            focusView = mPasswordView;
            cancel = true;
        }*/


        //sjn added
        else if (TextUtils.isEmpty(pin2)) {
            DialogManager.showAlertSingleActionDialog(CarryParkApplication.getCurrentActivity(), getString(R.string.pinLogValid), "ok", new DialogManager.IUniActionDialogOnClickListener() {
                @Override
                public void onPositiveClick() {

                }
            });
            focusView = etPin2;
            cancel = true;
        }
        else if (TextUtils.isEmpty(pin3)) {
            DialogManager.showAlertSingleActionDialog(CarryParkApplication.getCurrentActivity(), getString(R.string.pinLogValid), "ok", new DialogManager.IUniActionDialogOnClickListener() {
                @Override
                public void onPositiveClick() {

                }
            });
            focusView = etPin3;
            cancel = true;
        }
       else if (TextUtils.isEmpty(pin4)) {
            DialogManager.showAlertSingleActionDialog(CarryParkApplication.getCurrentActivity(), getString(R.string.pinLogValid), "ok", new DialogManager.IUniActionDialogOnClickListener() {
                @Override
                public void onPositiveClick() {

                }
            });
            focusView = etPin4;
            cancel = true;
        }

        /*// Check for a valid email address.
        if (TextUtils.isEmpty(email)) {
            etEmailAddress.setError(getString(R.string.error_field_required));
            focusView = etEmailAddress;
            cancel = true;
        } else if (!isEmailValid(email)) {
            etEmailAddress.setError(getString(R.string.error_invalid_email));
            focusView = etEmailAddress;
            cancel = true;
        }*/

        if (cancel) {
            focusView.requestFocus();
        } else {
            doLogin(pin1+pin2+pin3+pin4);
        }
    }

    private void doLogin(String pin) {

       showBusyAnimation("");

        Map<String, Object> candidateMap = new HashMap<>();

//        candidateMap.put("country_id", mLoggedInUser.country_id);
//        candidateMap.put("province_id", mLoggedInUser.province_id);
//        candidateMap.put("city_id", mLoggedInUser.cityId);
//        candidateMap.put("address", mLoggedInUser.address);
//        candidateMap.put("zip", mLoggedInUser.zip);
        /*candidateMap.put("email",email);
        candidateMap.put("dob", dob);*/
        candidateMap.put("pin", pin);
        String uniqueID = UUID.randomUUID().toString();

        String android_id = Settings.Secure.getString(getApplicationContext().getContentResolver(),
                Settings.Secure.ANDROID_ID);
        candidateMap.put("mac_id", android_id);

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

        apiService.login_new(candidateMap).enqueue(new Callback<UserLoginResponse>() {
            @Override
            public void onResponse(Call<UserLoginResponse> call, Response<UserLoginResponse> response) {

                if(response.code() == 200 && response.body().getData() != null){
                   hideBusyAnimation();
                    Boolean status = response.body().getSuccess();
                    if(status == true){
                        //Log.e("access_token",response.body().getData().getAccessToken());
                        SharedPreferences.Editor editor=sharedPreferences.edit();
                        editor.putString("access_token",response.body().getData().getAccessToken());
                        editor.putString("IS_LOGGED_IN","TRUE");

                        //editor.putString("user_name",response.body().getData().getUser().getFirstName());

                        SharedPreferenceUtility.saveScanClickEvent(false);
                        //sjn test for navigating to login page after verfying pin
                        editor.putString("hasVerifiedPinEntered","true");

                        editor.apply();
                        //sjn
                        AppController.setString(getApplicationContext(),"acess_token", response.body().getData().getAccessToken());
                        //sjn
                        //AppController.setString(getApplicationContext(),"user_name", response.body().getData().getUser().getFirstName());
                        Intent intent = new Intent(CarryParkApplication.getCurrentContext(), BottomNavigation.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        intent.putExtra("page", ConstantProject.SplashActivity);
                        startActivity(intent);
                        finish();
                    }else {
                        etPin1.setError(null);
                        etPin2.setError(null);
                        etPin3.setError(null);
                        etPin4.setError(null);
                        etPin1.setText("");
                        etPin2.setText("");
                        etPin3.setText("");
                        etPin4.setText("");
                        etPin1.requestFocus();
                    }
                }else if(response.code() == 200 && response.body().getData() == null) {
                   hideBusyAnimation();
                    String msg = response.body().getMessage();
                    DialogManager.showAlertSingleActionDialog(CarryParkApplication.getCurrentActivity(), msg, "ok", new DialogManager.IUniActionDialogOnClickListener() {
                        @Override
                        public void onPositiveClick() {
                            etPin1.setError(null);
                            etPin2.setError(null);
                            etPin3.setError(null);
                            etPin4.setError(null);
                            etPin1.setText("");
                            etPin2.setText("");
                            etPin3.setText("");
                            etPin4.setText("");
                            etPin1.requestFocus();
                        }


                    });
                } else if(response.code() == 404){
                    hideBusyAnimation();
                    DialogManager.showAlertSingleActionDialog(CarryParkApplication.getCurrentActivity(),CarryParkApplication.getCurrentContext().getResources().getString(R.string.validation_error), "ok", new DialogManager.IUniActionDialogOnClickListener() {
                        @Override
                        public void onPositiveClick() {
                            etPin1.setError(null);
                            etPin2.setError(null);
                            etPin3.setError(null);
                            etPin4.setError(null);
                            etPin1.setText("");
                            etPin2.setText("");
                            etPin3.setText("");
                            etPin4.setText("");
                            etPin1.requestFocus();


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


    public static class GenericTextWatcher implements TextWatcher {
        private final EditText[] editText;
        private View view;
        public GenericTextWatcher(View view, EditText editText[])
        {
            this.editText = editText;
            this.view = view;
        }

        @Override
        public void afterTextChanged(Editable editable) {
            String text = editable.toString();
            /*switch (view.getId()) {
                case R.id.et_pin1:
                    if (text.length() == 1)
                        editText[1].requestFocus();
                    break;
                case R.id.et_pin2:
                    if (text.length() == 1)
                        editText[2].requestFocus();
                    else if (text.length() == 0)
                        editText[0].requestFocus();
                    break;
                case R.id.et_pin3:
                    if (text.length() == 1)
                        editText[3].requestFocus();
                    else if (text.length() == 0)
                        editText[1].requestFocus();
                    break;
                case R.id.et_pin4:
                    if (text.length() == 0)
                        editText[2].requestFocus();
                    break;
            }*/

            switch (view.getId()) {
                case R.id.et_pin1:
                    if (text.length() == 1)
                        editText[1].requestFocus();
                    break;
                case R.id.et_pin2:
                    if (text.length() == 1)
                        editText[2].requestFocus();
                    else if (text.length() == 0)
                        editText[1].requestFocus();
                    break;
                case R.id.et_pin3:
                    if (text.length() == 1)
                        editText[3].requestFocus();
                    else if (text.length() == 0)
                        editText[2].requestFocus();
                    break;
                case R.id.et_pin4:
                    if (text.length() == 0)
                        editText[3].requestFocus();
                    else
                        hideKeyboard(CarryParkApplication.getCurrentActivity());
                    break;
            }
        }

        private void hideKeyboard(Activity activity) {
            InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
            //Find the currently focused view, so we can grab the correct window token from it.
            View view = activity.getCurrentFocus();
            //If no view currently has focus, create a new one, just so we can grab a window token from it
            if (view == null) {
                view = new View(activity);
            }
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }

        @Override
        public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
        }

        @Override
        public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
        }
    }

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
