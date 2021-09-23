package net.ui;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.location.Location;
import android.location.LocationManager;
import android.os.Looper;
import android.provider.Settings;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.TextView;


import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import com.google.android.gms.location.*;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import net.CarryParkApplication;
import net.others.ConstantProject;
import net.others.DialogManager;
import net.others.SharedPreferenceUtility;
import net.rest.ApiClient;
import net.rest.ApiInterface;
import net.rest.Utils;
import net.simplifiedcoding.carrypark.R;

import java.util.Locale;

public class WelcomeActivity extends AppCompatActivity implements View.OnClickListener {
    TextView tv_welcome, tv1, tv2,tv_version;
    SharedPreferences sharedPreferences;
    RadioButton radioEnglish, radioJapanese, radioKorean, radioChinese;
    Button getStarted;
    SharedPreferences.Editor editor;
    ApiInterface apiService;
    FusedLocationProviderClient mFusedLocationClient;
    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;
    int PERMISSION_ID = 44;
    String AppVersionName;

    int MY_PERMISSIONS_REQUEST_CAMERA = 1;
    private String version_name="バージョン: ";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //checking Internet
        apiService =
                ApiClient.getClient().create(ApiInterface.class);

        loadLocale();
        Locale locale = new Locale("ja");
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.locale = locale;
        getBaseContext().getResources().updateConfiguration(config,
                getBaseContext().getResources().getDisplayMetrics());


        setContentView(R.layout.activity_welcome);
        CarryParkApplication.setCurrentContext(this);
        sharedPreferences = getSharedPreferences("carry_park", MODE_PRIVATE);

        getStarted = (Button) findViewById(R.id.button_get_started);

        radioEnglish = (RadioButton) findViewById(R.id.radio_english);
        radioChinese = (RadioButton) findViewById(R.id.radio_chinese);
        radioJapanese = (RadioButton) findViewById(R.id.radio_japanese);
        radioKorean = (RadioButton) findViewById(R.id.radio_korean);
        tv_version = (TextView) findViewById(R.id.tv_version);
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        tv_welcome = (TextView) findViewById(R.id.tv_welcome);
        tv1 = (TextView) findViewById(R.id.tv1);
        tv2 = (TextView) findViewById(R.id.tv2);
        radioJapanese.setChecked(true);
        tv1.setText("大型手荷物専用預かり監視システム");
        tv2.setText("セキュリティと監視システム");
        tv_welcome.setText("Carryparkへようこそ");
        getStarted.setText("開始");

        checkLocationPermission();

        CarryParkApplication.setIsEnglishLang(false);
        CarryParkApplication.setIsJapaneaseLang(true);
        CarryParkApplication.setIsKorean(false);
        CarryParkApplication.setIsChinease(false);
        SharedPreferenceUtility.saveLang(false, true, false, false);

        getStarted.setOnClickListener(this);

        if (sharedPreferences.getString("selected_lang", null) == null) {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("selected_lang", "en");
            editor.apply();
        }

        String user = sharedPreferences.getString("selected_lang", null);
        if (user != null) {
            if (sharedPreferences.getString("selected_lang", null).equalsIgnoreCase("ja")) {

                CarryParkApplication.setIsJapaneaseLang(true);
            } else {

                CarryParkApplication.setIsEnglishLang(true);
            }
        } else


            radioEnglish.setChecked(true);
        radioKorean.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                radioChinese.setChecked(false);
                radioEnglish.setChecked(false);
                radioJapanese.setChecked(false);
                tv1.setText("대형 수하물 보관 모니터링 시스템");
                tv2.setText("보안 및 모니터링 시스템");
                tv_welcome.setText("Carrypark에 오신 것을 환영합니다");
                getStarted.setText("시작하다");
                version_name="버전: ";
                tv_version.setText(version_name+AppVersionName);


            }
        });
        radioChinese.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                radioKorean.setChecked(false);
                radioEnglish.setChecked(false);
                radioJapanese.setChecked(false);
                tv1.setText("大件行李保管监控系统");
                tv2.setText("安防监控系统");
                tv_welcome.setText("欢迎来到Carrypark");
                getStarted.setText("开始使用");
                version_name="版本: ";
                tv_version.setText(version_name+AppVersionName);



            }
        });
        radioEnglish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tv1.setText("Large baggage custody monitoring system");
                tv2.setText("Security & Monitoring System");
                tv_welcome.setText("Welcome to Carrypark");
                getStarted.setText("Get Started");
                version_name="Version: ";
                tv_version.setText(version_name+AppVersionName);


                radioKorean.setChecked(false);
                radioChinese.setChecked(false);
                radioJapanese.setChecked(false);
            }
        });
        radioJapanese.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tv1.setText("大型手荷物専用預かり監視システム");
                tv2.setText("セキュリティと監視システム");
                tv_welcome.setText("Carryparkへようこそ");
                getStarted.setText("開始");
                version_name="バージョン: ";
                tv_version.setText(version_name+AppVersionName);


                radioKorean.setChecked(false);
                radioChinese.setChecked(false);
                radioEnglish.setChecked(false);
            }
        });
        //  checkForCameraPermission();

        PackageInfo pinfo = null;
        try {
            pinfo = getPackageManager().getPackageInfo(getPackageName(), 0);
            int versionNumber = pinfo.versionCode;
             AppVersionName = pinfo.versionName;
            tv_version.setText(version_name+AppVersionName);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.button_get_started:
                setLanguageAndMove();
        }
    }


    private void setLocale(String lang) {
        Locale locale = new Locale(lang);
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.locale = locale;
        getBaseContext().getResources().updateConfiguration(config, getBaseContext().getResources().getDisplayMetrics());
        //save data to shared preferences
        SharedPreferences.Editor editor = getSharedPreferences("settings", MODE_PRIVATE).edit();
        editor.putString("My_Lang", lang);
        editor.apply();
    }


    //load language saved in shared preference
    public void loadLocale() {
        SharedPreferences prefs = getSharedPreferences("settings", Activity.MODE_PRIVATE);
        String language = prefs.getString("My_Lang", "ja");
        setLocale(language);
    }


    public void setLanguageAndMove() {



           if (radioChinese.isChecked())
           {
               CarryParkApplication.setIsEnglishLang(false);
               CarryParkApplication.setIsJapaneaseLang(false);
               CarryParkApplication.setIsKorean(false);
               CarryParkApplication.setIsChinease(true);
               SharedPreferenceUtility.saveLang(false, false, false, true);

               //korean
               SharedPreferences.Editor editor = sharedPreferences.edit();
               editor.putString("selected_lang", ConstantProject.ChineaseLocali);
               editor.apply();

               setLocale(ConstantProject.ChineaseLocali);
               SharedPreferenceUtility.saveLangLocal(ConstantProject.ChineaseLocali);
              // recreate();
           }
           else if (radioKorean.isChecked())
           {
               CarryParkApplication.setIsEnglishLang(false);
               CarryParkApplication.setIsJapaneaseLang(false);
               CarryParkApplication.setIsKorean(true);
               CarryParkApplication.setIsChinease(false);
               SharedPreferenceUtility.saveLang(false, false, true, false);

               //korean
               SharedPreferences.Editor editor = sharedPreferences.edit();
               editor.putString("selected_lang", "ko");
               editor.apply();

               setLocale("ko");
               SharedPreferenceUtility.saveLangLocal("ko");
             //  recreate();

           }
            else if (radioEnglish.isChecked()) {
                CarryParkApplication.setIsEnglishLang(true);
                CarryParkApplication.setIsJapaneaseLang(false);
                CarryParkApplication.setIsKorean(false);
                CarryParkApplication.setIsChinease(false);
                SharedPreferenceUtility.saveLang(true, false, false, false);

                //English
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("selected_lang", "en");
                editor.apply();

                setLocale("en");
               SharedPreferenceUtility.saveLangLocal("en");

               // recreate();
            } else if (radioJapanese.isChecked()) {
                CarryParkApplication.setIsJapaneaseLang(true);
                CarryParkApplication.setIsEnglishLang(false);
                SharedPreferenceUtility.saveLang(false, true, false, false);
                CarryParkApplication.setIsKorean(false);
                CarryParkApplication.setIsChinease(false);
                //Japanese
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("selected_lang", "ja");
                editor.apply();

                setLocale("ja");
               SharedPreferenceUtility.saveLangLocal("ja");
               // recreate();
            }


            Intent intent = new Intent(getApplicationContext(), UserSelectionActivity.class);
            startActivity(intent);
            finish();


    }

    public boolean checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {


            DialogManager.showUniActionDialog(CarryParkApplication.getCurrentActivity(),"お近くのロッカーを見つけるため、現在位置の照会を許可してください", "ok", new DialogManager.IUniActionDialogOnClickListener() {
                @Override
                public void onPositiveClick() {

                    if (ActivityCompat.shouldShowRequestPermissionRationale(WelcomeActivity.this,
                            Manifest.permission.ACCESS_FINE_LOCATION)) {

                        // Show an explanation to the user *asynchronously* -- don't block
                        // this thread waiting for the user's response! After the user
                        // sees the explanation, try again to request the permission.
                        ActivityCompat.requestPermissions(WelcomeActivity.this,
                                new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                                MY_PERMISSIONS_REQUEST_LOCATION);



                    } else {
                        // No explanation needed, we can request the permission.
                        ActivityCompat.requestPermissions(WelcomeActivity.this,
                                new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                                MY_PERMISSIONS_REQUEST_LOCATION);
                    }
                }
            });
            // Should we show an explanation?

            return false;
        } else {
            return true;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // location-related task you need to do.
                    if (ContextCompat.checkSelfPermission(this,
                            Manifest.permission.ACCESS_FINE_LOCATION)
                            == PackageManager.PERMISSION_GRANTED) {

                        //Request location updates:
                        getLocation();
                    }

                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.

                }
                return;
            }

        }
    }

    public void getLocation() {


        if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    Activity#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for Activity#requestPermissions for more details.
            return;
        }
        mFusedLocationClient.getLastLocation()
                .addOnSuccessListener(CarryParkApplication.getCurrentActivity(), new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        // Got last known location. In some rare situations this can be null.
                        if (location != null) {
                            SharedPreferenceUtility.saveLastKnownLatAndLong("" + location.getLatitude(), "" + location.getLongitude());

                            //  getLocationDetails(CarryParkApplication.getLatitude(),CarryParkApplication.getLongitude());


                        }
                    }
                });



    }
    @SuppressLint("MissingPermission")
    private void getLastLocation(){
        if (checkPermissions()) {
            if (isLocationEnabled()) {
                mFusedLocationClient.getLastLocation().addOnCompleteListener(
                        new OnCompleteListener<Location>() {
                            @Override
                            public void onComplete(@NonNull Task<Location> task) {
                                Location location = task.getResult();
                                if (location == null) {
                                    requestNewLocationData();
                                } else {
                                    SharedPreferenceUtility.saveLastKnownLatAndLong("" + location.getLatitude(),"" +location.getLongitude());



                                }
                            }
                        }
                );
            } else {
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(intent);
            }
        } else {
            requestPermissions();
        }
    }

    private boolean checkPermissions(){
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
            return true;
        }
        return false;
    }

    private boolean isLocationEnabled(){
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(
                LocationManager.NETWORK_PROVIDER
        );
    }

    @SuppressLint("MissingPermission")
    private void requestNewLocationData(){

        LocationRequest mLocationRequest = new LocationRequest();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setInterval(0);
        mLocationRequest.setFastestInterval(0);
        mLocationRequest.setNumUpdates(1);

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        mFusedLocationClient.requestLocationUpdates(
                mLocationRequest, mLocationCallback,
                Looper.myLooper()
        );

    }

    private LocationCallback mLocationCallback = new LocationCallback() {
        @Override
        public void onLocationResult(LocationResult locationResult) {
            Location mLastLocation = locationResult.getLastLocation();
            SharedPreferenceUtility.saveLastKnownLatAndLong("" + mLastLocation.getLatitude(),"" + mLastLocation.getLongitude());


        }
    };
    private void requestPermissions(){
        ActivityCompat.requestPermissions(
                this,
                new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION},
                PERMISSION_ID
        );
    }
}
