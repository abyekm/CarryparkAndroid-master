package net.ui;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Environment;
import android.provider.Settings;
import android.os.Bundle;
import android.util.Log;

import net.CarryParkApplication;
import net.others.BaseActivity;
import net.others.ConstantProject;
import net.others.DialogManager;
import net.others.SharedPreferenceUtility;
import net.rest.ApiClient;
import net.rest.ApiInterface;
import net.rest.Utils;
import net.simplifiedcoding.carrypark.R;

public class SplashActivity extends BaseActivity {

    String isLogged = "";

    ApiInterface apiService;

    private SharedPreferences sharedPreferences;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        CarryParkApplication.setCurrentContext(this);


        killAllBackgroundTask();
        apiService =
                ApiClient.getClient().create(ApiInterface.class);
        String  deviceMacId = Settings.Secure.getString(SplashActivity.this.getContentResolver(), Settings.Secure.ANDROID_ID);

        SharedPreferenceUtility.SaveDeviceIdMacID(deviceMacId);
        //checking Internet

        sharedPreferences=getSharedPreferences("carry_park",MODE_PRIVATE);
        SharedPreferences.Editor editor=sharedPreferences.edit();
        if(sharedPreferences.getString("IS_LOGGED_IN",null)!= null){
            isLogged = sharedPreferences.getString("IS_LOGGED_IN",null);
        }
        SharedPreferenceUtility.saveIsCheckedLockerSize(false);



        Thread background = new Thread() {
            public void run() {

                try {
                    // Thread will sleep for 5

                    sleep(2*1000);

                    if(isLogged.equalsIgnoreCase("TRUE")){
                        if (SharedPreferenceUtility.isJapanease())
                        {
                            CarryParkApplication.setIsJapaneaseLang(true);
                            CarryParkApplication.setIsEnglishLang(false);
                        }
                        else {
                            CarryParkApplication.setIsJapaneaseLang(false);
                            CarryParkApplication.setIsEnglishLang(true);
                        }
                       // CarryParkApplication.setIsFromSplash(true);
                        Intent i = new Intent(SplashActivity.this, UserSelectionActivity.class);
                        startActivity(i);
                    }else {

                            Intent i = new Intent(SplashActivity.this, WelcomeActivity.class);
                            startActivity(i);

                    }
                    //Remove activity
                    finish();

                } catch (Exception e) {

                }
            }
        };

        // start thread
        background.start();

    }



    @Override
    protected void onDestroy() {

        super.onDestroy();

    }

}

