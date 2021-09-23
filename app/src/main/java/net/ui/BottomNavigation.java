package net.ui;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.PorterDuff;
import android.net.Uri;
import android.os.Bundle;
import android.print.PDFPrint;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import com.hendrix.pdfmyxml.viewRenderer.AbstractViewRenderer;
import com.tejpratapsingh.pdfcreator.utils.FileManager;
import com.tejpratapsingh.pdfcreator.utils.PDFUtil;
import net.CarryParkApplication;
import net.others.*;
import net.rest.ApiClient;
import net.rest.ApiInterface;
import net.rest.GloablMethods;
import net.rest.Utils;
import net.rest.global.AppController;
import net.rest.model.UserDetailsResponse;
import net.simplifiedcoding.carrypark.*;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

import static net.others.DialogManager.copyFileOrDirectory;

public class BottomNavigation extends BaseActivity {

    LinearLayout llScan_button;
    LinearLayout llInuse;
    LinearLayout llMore;
    LinearLayout llFootbar;
    LinearLayout llFooterCamera;
    LinearLayout ll_home;
    public static LinearLayout ll_pdf_create;
    public static Button btn_close;
    public static TextView tv_temp_1, tv_carrypark_co, tv_telphone, tv_receipt, tv_amount, tv_date_of_use, id_tex1, id_tex2;
    boolean isClickableScan, isClicableInuse, isSettingsEnable;

    TextView tvScan, tvInuse, tvMore;
    ImageView ivScan, ivInuse, ivMore;
    private SharedPreferences sharedPreferences;
    String isFrom = null;
    String languageToLoad = "en";
    String newFragment;
    ApiInterface apiService;
    private static final int CAMERA = 2;
    private static final int BLUETOOTH = 2;
    public static Button button_download;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bottumnav);
        CarryParkApplication.setCurrentContext(this);
        isClickableScan = true;
        isClicableInuse = true;
        isSettingsEnable = true;
        apiService =
                ApiClient.getClient().create(ApiInterface.class);

        //test

        sharedPreferences = getBaseContext().getSharedPreferences("carry_park", MODE_PRIVATE);
        if (SharedPreferenceUtility.isJapanease()) {
            languageToLoad = "ja"; // your language
        } else {
            languageToLoad = "en"; // your language
        }

        Locale locale = new Locale(languageToLoad);
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.locale = locale;
        getBaseContext().getResources().updateConfiguration(config,
                getBaseContext().getResources().getDisplayMetrics());


        init();
        llFooterCamera.setVisibility(View.GONE);
        int cameraPermission = ContextCompat.checkSelfPermission(CarryParkApplication.getCurrentActivity(), Manifest.permission.CAMERA);

        if (cameraPermission != PackageManager.PERMISSION_GRANTED) {
            /*final AlertDialog.Builder builder = new AlertDialog.Builder(CarryParkApplication.getCurrentActivity());
            builder.setTitle(CarryParkApplication.getCurrentContext().getResources().getString(R.string.alert));
            builder.setMessage(CarryParkApplication.getCurrentContext().getResources().getString(R.string.camera_per));
            builder.setPositiveButton(android.R.string.ok, null);
            builder.setOnDismissListener(new DialogInterface.OnDismissListener() {
                @Override
                public void onDismiss(DialogInterface dialog) {

                }
            });
            builder.show();*/
            requestPermissions(new String[]{Manifest.permission.CAMERA}, CAMERA);
        }

        int blueTu = ContextCompat.checkSelfPermission(CarryParkApplication.getCurrentActivity(), Manifest.permission.BLUETOOTH);

        if (blueTu != PackageManager.PERMISSION_GRANTED) {
            final AlertDialog.Builder builder = new AlertDialog.Builder(CarryParkApplication.getCurrentContext());
            builder.setTitle(CarryParkApplication.getCurrentContext().getResources().getString(R.string.alert));
            builder.setMessage(CarryParkApplication.getCurrentContext().getResources().getString(R.string.camera_per));
            builder.setPositiveButton(android.R.string.ok, null);
            builder.setOnDismissListener(new DialogInterface.OnDismissListener() {
                @Override
                public void onDismiss(DialogInterface dialog) {
                    requestPermissions(new String[]{Manifest.permission.BLUETOOTH}, BLUETOOTH);
                }
            });
            builder.show();
        }


        Bundle bundle = getIntent().getExtras();

        if (bundle.getString("page") != null) {
            newFragment = bundle.getString("page");

        }
        // tinu: change fragment
        if (newFragment.equalsIgnoreCase(ConstantProject.ScanQRCodeFragment)) {

            OnScanClick();
            isSettingsEnable = false;
            isClicableInuse = true;
            isClickableScan = true;
            SharedPreferenceUtility.saveScanClickEvent(true);
            Intent intent = new Intent(CarryParkApplication.getCurrentContext(), BottomNavigation.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            intent.putExtra("page", ConstantProject.SplashActivity);
            startActivity(intent);
           /* Fragment fragment = new ScanQrcodeFragment();

            getSupportFragmentManager().beginTransaction()

                    .replace(R.id.fragment_container, fragment,

                            fragment.getClass().getSimpleName()).commit();*/


        } else if (newFragment.equalsIgnoreCase(ConstantProject.MoreFragment)) {
            if (CarryParkApplication.getUsedLockerList() != null) {
                if (CarryParkApplication.getUsedLockerList().size() > 0) {
                    for (int i = 0; i < CarryParkApplication.getUsedLockerList().size(); i++) {
                        if (CarryParkApplication.getUsedLockerList().get(i).getPresentStatus().equalsIgnoreCase("LOCKED")) {
                            disableScan();
                        }
                    }

                } else {
                    enableScanAndInUseClic();
                }
            } else {
                enableScanAndInUseClic();
            }
            Fragment fragment = new MoreFragment();

            getSupportFragmentManager().beginTransaction()

                    .replace(R.id.fragment_container, fragment,

                            fragment.getClass().getSimpleName()).commit();

            OnMoreClick();
            llFootbar.setVisibility(View.VISIBLE);

        } else if (newFragment.equalsIgnoreCase(ConstantProject.HtmlFragment)) {

            String device_token = null;
            if (bundle.getString("device_token") != null) {
                device_token = bundle.getString("device_token");

            }
            //Long current_time_of_timer = null;
            Long current_time_of_timer = getIntent().getLongExtra("current_time_of_timer", 0);
            if (current_time_of_timer == 0) {
                current_time_of_timer = Long.valueOf(9000);
            }

            Fragment fragment = new HtmlFragment();
            Bundle args = new Bundle();
            args.putString("device_token", device_token);
            args.putLong("current_time_of_timer", current_time_of_timer);
            fragment.setArguments(args);
            getSupportFragmentManager().beginTransaction()

                    .replace(R.id.fragment_container, fragment,

                            fragment.getClass().getSimpleName()).commit();

            llFootbar.setVisibility(View.GONE);
        } else if (newFragment.equalsIgnoreCase(ConstantProject.PaymentSuccessFragment)) {
            String device_token = null;
            if (bundle.getString("device_token") != null) {
                device_token = bundle.getString("device_token");

            }
            Long current_time_of_timer = Long.valueOf(1000);
            if (bundle.getString("current_time_of_timer") != null) {
                current_time_of_timer = Long.valueOf(bundle.getString("current_time_of_timer"));

            }
            Fragment fragment = new PaymentSuccessFragment();
            Bundle args = new Bundle();
            args.putString("device_token", device_token);
            args.putLong("current_time_of_timer", current_time_of_timer);
            fragment.setArguments(args);
            getSupportFragmentManager().beginTransaction()

                    .replace(R.id.fragment_container, fragment,

                            fragment.getClass().getSimpleName()).commit();
            llFootbar.setVisibility(View.VISIBLE);
            disableAllNav();

        } else if (newFragment.equalsIgnoreCase(ConstantProject.FinalPaymentFragment)) {
            OnInUseClick(false);
            String is_from = null;
            if (bundle.getString("is_from") != null) {
                is_from = bundle.getString("is_from");

            }
            String paymentStatus = null;
            if (bundle.getString("payment_status") != null) {
                paymentStatus = bundle.getString("payment_status");

            }
            Boolean pay_success = bundle.getBoolean("pay_success");
            Boolean pay_cancel = bundle.getBoolean("pay_cancel");


            Long current_time_of_timer = Long.valueOf(0);
            if (bundle.getString("current_time_of_timer") != null) {
                current_time_of_timer = Long.valueOf(bundle.getString("current_time_of_timer"));

            }

             /*  Bundle args = new Bundle();
        args.putString("is_from", "WebViewFragment");
        args.putString("payment_status",paymentStatus);
        args.putLong("current_time_of_timer",currentTimeOfTimer);
        targetfragment.setArguments(args);*/

            Fragment fragment = new FinalPaymentFragment();
            Bundle args = new Bundle();
            args.putString("is_from", is_from);
            args.putString("payment_status", paymentStatus);
            args.putLong("current_time_of_timer", current_time_of_timer);
            args.putBoolean("pay_cancel", pay_cancel);
            args.putBoolean("pay_success", pay_success);
            fragment.setArguments(args);
            getSupportFragmentManager().beginTransaction()

                    .replace(R.id.fragment_container, fragment,

                            fragment.getClass().getSimpleName()).commit();
            llFootbar.setVisibility(View.VISIBLE);
        } else if (newFragment.equalsIgnoreCase(ConstantProject.InitialPaymentFragment)) {
            OnInUseClick(false);
            String is_from = null;
            if (bundle.getString("is_from") != null) {
                is_from = bundle.getString("is_from");

            }
            String qrcode = null;
            if (bundle.getString("qrcode") != null) {
                qrcode = bundle.getString("qrcode");

            }
            String device = "";
            if (bundle.getString("device") != null) {
                qrcode = bundle.getString("device");

            }


            String paymentStatus = null;
            if (bundle.getString("payment_status") != null) {
                paymentStatus = bundle.getString("payment_status");

            }
            Long current_time_of_timer = Long.valueOf(90000);
            if (bundle.getString("current_time_of_timer") != null) {
                current_time_of_timer = Long.valueOf(bundle.getString("current_time_of_timer"));

            }

            Boolean pay_success = bundle.getBoolean("pay_success");
            Boolean pay_cancel = bundle.getBoolean("pay_cancel");
            Boolean isAliPay = bundle.getBoolean("isAliPay");
            Boolean iswebchat = bundle.getBoolean("iswebchat");


            Fragment fragment = new InitialPaymentFragment();
            Bundle args = new Bundle();
            args.putString("is_from", is_from);
            args.putString("qrcode", qrcode);
            args.putString("device", device);
            args.putBoolean("pay_cancel", pay_cancel);
            args.putBoolean("pay_success", pay_success);
            args.putString("payment_status", paymentStatus);
            args.putLong("current_time_of_timer", current_time_of_timer);
            args.putBoolean("isAliPay", isAliPay);
            args.putBoolean("iswebchat", iswebchat);
            fragment.setArguments(args);
            getSupportFragmentManager().beginTransaction()

                    .replace(R.id.fragment_container, fragment,

                            fragment.getClass().getSimpleName()).commit();
            llFootbar.setVisibility(View.VISIBLE);
            disableScanAndEnableInUseClic();
        } else if (newFragment.equalsIgnoreCase(ConstantProject.SplashActivity)) {
            boolean isDisableScan = false;

            if (CarryParkApplication.getUsedLockerList() != null) {
                if (CarryParkApplication.getUsedLockerList().size() > 0) {

                    for (int i = 0; i < CarryParkApplication.getUsedLockerList().size(); i++) {
                        if (CarryParkApplication.getUsedLockerList().get(i).getPresentStatus().equalsIgnoreCase("LOCKED")) {
                           if (!SharedPreferenceUtility.isScanClickEvent())
                           {
                               disableScan();
                               isDisableScan = true;
                           }

                        }
                    }

                } else {
                    if (!SharedPreferenceUtility.isScanClickEvent())
                    {
                        isDisableScan = false;
                        enableScanAndInUseClic();
                    }

                }
            } else {
                if (!SharedPreferenceUtility.isScanClickEvent())
                {
                    isDisableScan = false;
                    enableScanAndInUseClic();
                }

            }
            OnInUseClick(isDisableScan);

            Fragment fragment = null;
            fragment = new HomeFragment();
            loadFragment(fragment);
            if (!SharedPreferenceUtility.getIsCheckedLockerSize())
                getUserDetails();
        }
        //Not using now
        else if (newFragment.equalsIgnoreCase(ConstantProject.DevicesInUseAdapter)) {
            String scannedDeviceCode = null;
            if (bundle.getString("scannedDeviceCode") != null) {
                scannedDeviceCode = bundle.getString("scannedDeviceCode");

            }
            String isFrom = null;
            if (bundle.getString(ConstantProject.isFrom) != null) {
                isFrom = bundle.getString(ConstantProject.isFrom);

            }
            boolean hasInitialPaymentPreviouslyDone = bundle.getBoolean("hasInitialPaymentPreviously");


            // it is from the DevicesInUseAdapter recyclerview adapter and redirect to scan screen
            Fragment fragment = new ScanQrcodeFragment();
            Bundle args = new Bundle();
            args.putString(ConstantProject.isFrom, isFrom);
            args.putString("scannedDeviceCode", scannedDeviceCode);
            args.putBoolean("hasInitialPaymentPreviouslyDone", hasInitialPaymentPreviouslyDone);

            fragment.setArguments(args);
            getSupportFragmentManager().beginTransaction()

                    .replace(R.id.fragment_container, fragment,

                            fragment.getClass().getSimpleName()).commit();
            llFootbar.setVisibility(View.VISIBLE);

            if (CarryParkApplication.getUsedLockerList() != null) {
                if (CarryParkApplication.getUsedLockerList().size() > 0) {
                    for (int i = 0; i < CarryParkApplication.getUsedLockerList().size(); i++) {
                        if (CarryParkApplication.getUsedLockerList().get(i).getPresentStatus().equalsIgnoreCase("LOCKED")) {
                            disableScan();
                        }
                    }

                } else {
                    enableScanAndInUseClic();
                }
            } else {
                enableScanAndInUseClic();
            }
        } else if (newFragment.equalsIgnoreCase(ConstantProject.CodeScannerFragment)) {
           /* if(isClickableScan)
            {*/
            OnScanClick();
            isSettingsEnable = false;
            isClicableInuse = true;
            isClickableScan = true;
            Fragment fragment = new ScanQrcodeFragment();

            getSupportFragmentManager().beginTransaction()

                    .replace(R.id.fragment_container, fragment,

                            fragment.getClass().getSimpleName()).commit();

            // }

        } else if (newFragment.equalsIgnoreCase(ConstantProject.DeviceLockedFragment)) {

            enableAll();
            SharedPreferenceUtility.saveIsCheckedLockerSize(false);
            Fragment fragment = new DeviceLockedFragment();

            getSupportFragmentManager().beginTransaction()

                    .replace(R.id.fragment_container, fragment,

                            fragment.getClass().getSimpleName()).commit();


        } else if (newFragment.equalsIgnoreCase(ConstantProject.VideoInstructionFragment)) {
            tvScan.setTextColor(CarryParkApplication.getCurrentActivity().getResources().getColor(R.color.colorBlack));
            ivScan.setImageResource(R.drawable.scan_black);
            ivScan.setColorFilter(getResources().getColor(R.color.colorBlack), PorterDuff.Mode.SRC_IN);


            Fragment fragment = new VideoInstructionFragment();
            Boolean initial_payment = bundle.getBoolean(ConstantProject.initial_payment);
            Boolean final_payment = bundle.getBoolean("finalP");
            List<String> myStrings = bundle.getStringArrayList(ConstantProject.video_screenImages);

            Bundle args = new Bundle();
            args.putBoolean(ConstantProject.initial_payment, initial_payment);
            args.putBoolean("finalP", final_payment);
            args.putStringArrayList(ConstantProject.video_screenImages, (ArrayList<String>) myStrings);
            fragment.setArguments(args);
            getSupportFragmentManager().beginTransaction()

                    .replace(R.id.fragment_container, fragment,

                            fragment.getClass().getSimpleName()).commit();

            boolean isDisableScan = false;

            if (CarryParkApplication.getUsedLockerList() != null) {
                if (CarryParkApplication.getUsedLockerList().size() > 0) {
                    for (int i = 0; i < CarryParkApplication.getUsedLockerList().size(); i++) {
                        if (CarryParkApplication.getUsedLockerList().get(i).getPresentStatus().equalsIgnoreCase("LOCKED")) {
                            disableScan();
                            isDisableScan = true;
                        }
                    }

                } else {
                    isDisableScan = false;
                    enableScanAndInUseClic();
                }
            } else {
                isDisableScan = false;
                enableScanAndInUseClic();
            }
            OnInUseClick(isDisableScan);

        } else if (newFragment.equalsIgnoreCase(ConstantProject.HelpFragment)) {
            OnMoreClick();
            Fragment fragment = new HelpFragment();
            getSupportFragmentManager().beginTransaction()

                    .replace(R.id.fragment_container, fragment,

                            fragment.getClass().getSimpleName()).commit();

        } else if (newFragment.equalsIgnoreCase(ConstantProject.DeepLinking)) {
           /* if(isClickableScan)
            {*/
            boolean isDisableScan = false;

            if (CarryParkApplication.getUsedLockerList() != null) {
                if (CarryParkApplication.getUsedLockerList().size() > 0) {
                    boolean isTrue = false;
                    for (int i = 0; i < CarryParkApplication.getUsedLockerList().size(); i++) {
                        if (CarryParkApplication.getUsedLockerList().get(i).getPresentStatus().equalsIgnoreCase("LOCKED")) {
                            isTrue = true;
                        }
                    }
                    if (isTrue) {
                        disableScan();
                        isDisableScan = true;
                    }

                } else {
                    isDisableScan = false;
                    enableScanAndInUseClic();
                }
            } else {
                isDisableScan = false;
                enableScanAndInUseClic();
            }
            OnInUseClick(isDisableScan);


            isSettingsEnable = false;
            isClicableInuse = true;
            isClickableScan = true;
            Fragment fragment = new CodeScannerFragment();
            Bundle args = new Bundle();
            args.putString("qrcode", "deeplink");
            fragment.setArguments(args);
            getSupportFragmentManager().beginTransaction()

                    .replace(R.id.fragment_container, fragment,

                            fragment.getClass().getSimpleName()).commit();

            // }

        }


        llMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                SharedPreferenceUtility.saveIsCheckedLockerSize(false);

                OnMoreClick();
                Fragment fragment = null;
                fragment = new MoreFragment();
                loadFragment(fragment);
                if (CarryParkApplication.getUsedLockerList() != null) {
                    if (CarryParkApplication.getUsedLockerList().size() > 0) {
                        for (int i = 0; i < CarryParkApplication.getUsedLockerList().size(); i++) {
                            if (CarryParkApplication.getUsedLockerList().get(i).getPresentStatus().equalsIgnoreCase("LOCKED")) {
                                disableScan();
                            }
                        }

                    } else {
                        enableScanAndInUseClic();
                    }
                } else {
                    enableScanAndInUseClic();
                }


            }
        });
        btn_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hidePdfView();
            }
        });
        ll_home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean isDisableScan = false;
                if (CarryParkApplication.getUsedLockerList() != null) {
                    if (CarryParkApplication.getUsedLockerList().size() > 0) {
                        boolean isTrue = false;
                        for (int i = 0; i < CarryParkApplication.getUsedLockerList().size(); i++) {
                            if (CarryParkApplication.getUsedLockerList().get(i).getPresentStatus().equalsIgnoreCase("LOCKED")) {
                                disableScan();
                                isDisableScan = true;
                            }
                        }

                    } else {
                        isDisableScan = false;

                        enableScanAndInUseClic();
                    }

                } else {
                    isDisableScan = false;

                    enableScanAndInUseClic();
                }
                OnInUseClick(isDisableScan);
                Fragment fragment = null;
                fragment = new HomeFragment();
                loadFragment(fragment);

                if (!SharedPreferenceUtility.getIsCheckedLockerSize())
                    getUserDetails();
            }
        });
        llInuse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean isDisableScan = false;

                if (CarryParkApplication.getUsedLockerList() != null) {
                    if (CarryParkApplication.getUsedLockerList().size() > 0) {
                        boolean isTrue = false;
                        for (int i = 0; i < CarryParkApplication.getUsedLockerList().size(); i++) {
                            if (CarryParkApplication.getUsedLockerList().get(i).getPresentStatus().equalsIgnoreCase("LOCKED")) {
                                isTrue = true;
                            }
                        }
                        if (isTrue) {
                            disableScan();
                            isDisableScan = true;
                        }

                    } else {
                        isDisableScan = false;
                        enableScanAndInUseClic();
                    }
                } else {
                    isDisableScan = false;
                    enableScanAndInUseClic();
                }
                OnInUseClick(isDisableScan);
                Fragment fragment = null;
                fragment = new HomeFragment();
                loadFragment(fragment);
                if (!SharedPreferenceUtility.getIsCheckedLockerSize())
                    getUserDetails();

            }
        });

        llScan_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (CarryParkApplication.isIsFromSplash()) {
                    if (CarryParkApplication.getUsedLockerList() != null) {
                        if (CarryParkApplication.getUsedLockerList().size() > 0) {
                            boolean isTrue = false;
                            for (int i = 0; i < CarryParkApplication.getUsedLockerList().size(); i++) {
                                if (CarryParkApplication.getUsedLockerList().get(i).getPresentStatus().equalsIgnoreCase("LOCKED")) {
                                    isTrue = true;
                                }
                            }
                            if (isTrue)
                                disableScan();
                            else {
                                enableScan();
                                OnScanClick();
                                SharedPreferenceUtility.saveScanClickEvent(true);
                                Intent intent = new Intent(CarryParkApplication.getCurrentContext(), BottomNavigation.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                intent.putExtra("page", ConstantProject.SplashActivity);
                                startActivity(intent);
                              /*  Fragment fragment = null;
                                fragment = new ScanQrcodeFragment();
                                loadFragment(fragment);*/

                            }
                        } else {
                            OnScanClick();
                            SharedPreferenceUtility.saveScanClickEvent(true);
                            Intent intent = new Intent(CarryParkApplication.getCurrentContext(), BottomNavigation.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            intent.putExtra("page", ConstantProject.SplashActivity);
                            startActivity(intent);
                           /* Fragment fragment = null;
                            fragment = new ScanQrcodeFragment();
                            loadFragment(fragment);*/
                        }
                    } else {
                        OnScanClick();
                        SharedPreferenceUtility.saveScanClickEvent(true);
                        Intent intent = new Intent(CarryParkApplication.getCurrentContext(), BottomNavigation.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        intent.putExtra("page", ConstantProject.SplashActivity);
                        startActivity(intent);
                        /*Fragment fragment = null;
                        fragment = new ScanQrcodeFragment();
                        loadFragment(fragment);*/
                    }
                } else if (isClickableScan) {
                    OnScanClick();
                    SharedPreferenceUtility.saveScanClickEvent(true);

                    /*Fragment fragment = null;
                    fragment = new ScanQrcodeFragment();
                    loadFragment(fragment);*/
                    Intent intent = new Intent(CarryParkApplication.getCurrentContext(), BottomNavigation.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    intent.putExtra("page", ConstantProject.SplashActivity);
                    startActivity(intent);
                }

            }
        });


        if (SharedPreferenceUtility.isJapanease()) {
            tvScan.setText("スキャン");
            tvMore.setText("設定");
            tvInuse.setText("ホーム");
        } else if (SharedPreferenceUtility.isEnglish()) {
            tvScan.setText("Scan");
            tvMore.setText("More");
            tvInuse.setText("Home");
        } else if (SharedPreferenceUtility.isKorean()) {
            tvScan.setText("스캔");
            tvMore.setText("더");
            tvInuse.setText("본관");
        } else if (SharedPreferenceUtility.isChinease()) {
            tvScan.setText("扫瞄");
            tvMore.setText("更多");
            tvInuse.setText("主画面");
        }

    }


    public void OnScanClick() {
        tvScan.setTextColor(CarryParkApplication.getCurrentActivity().getResources().getColor(R.color.colorAccent));
        ivScan.setImageResource(R.drawable.scan_red);
        ivScan.setColorFilter(getResources().getColor(R.color.colorAccent), PorterDuff.Mode.SRC_IN);

        tvMore.setTextColor(CarryParkApplication.getCurrentActivity().getResources().getColor(R.color.colorBlack));
        ivMore.setImageResource(R.drawable.settings_black);
        ivMore.setColorFilter(getResources().getColor(R.color.colorBlack), PorterDuff.Mode.SRC_IN);

        tvInuse.setTextColor(CarryParkApplication.getCurrentActivity().getResources().getColor(R.color.colorBlack));
        ivInuse.setImageResource(R.drawable.innuse_black);
        ivInuse.setColorFilter(getResources().getColor(R.color.colorBlack), PorterDuff.Mode.SRC_IN);


    }

    public void OnInUseClick(boolean disableScan) {
        tvInuse.setTextColor(CarryParkApplication.getCurrentContext().getResources().getColor(R.color.colorAccent));
        ivInuse.setImageResource(R.drawable.innuse_black);
        ivInuse.setColorFilter(getResources().getColor(R.color.colorAccent), PorterDuff.Mode.SRC_IN);


        tvMore.setTextColor(CarryParkApplication.getCurrentContext().getResources().getColor(R.color.colorBlack));
        ivMore.setImageResource(R.drawable.settings_black);
        ivMore.setColorFilter(getResources().getColor(R.color.colorBlack), PorterDuff.Mode.SRC_IN);

        if (!disableScan) {
            tvScan.setTextColor(CarryParkApplication.getCurrentActivity().getResources().getColor(R.color.colorBlack));
            ivScan.setImageResource(R.drawable.scan_black);
            ivScan.setColorFilter(getResources().getColor(R.color.colorBlack), PorterDuff.Mode.SRC_IN);

        }


    }

    public void OnMoreClick() {
        tvMore.setTextColor(CarryParkApplication.getCurrentActivity().getResources().getColor(R.color.colorAccent));
        ivMore.setImageResource(R.drawable.settings_red);
        ivMore.setColorFilter(getResources().getColor(R.color.colorAccent), PorterDuff.Mode.SRC_IN);


        tvScan.setTextColor(CarryParkApplication.getCurrentActivity().getResources().getColor(R.color.colorBlack));
        ivScan.setImageResource(R.drawable.scan_black);
        ivScan.setColorFilter(getResources().getColor(R.color.colorBlack), PorterDuff.Mode.SRC_IN);

        tvInuse.setTextColor(CarryParkApplication.getCurrentActivity().getResources().getColor(R.color.colorBlack));
        ivInuse.setImageResource(R.drawable.innuse_black);
        ivInuse.setColorFilter(getResources().getColor(R.color.colorBlack), PorterDuff.Mode.SRC_IN);


    }

    public void init() {
        llScan_button = (LinearLayout) findViewById(R.id.ll_scan_button);
        llInuse = (LinearLayout) findViewById(R.id.ll_inuse);
        llMore = (LinearLayout) findViewById(R.id.ll_more);
        tvScan = (TextView) findViewById(R.id.tv_scan);
        tvInuse = (TextView) findViewById(R.id.tv_inuse);
        tvMore = (TextView) findViewById(R.id.tv_more);
        ivScan = (ImageView) findViewById(R.id.iv_scan);
        ivInuse = (ImageView) findViewById(R.id.iv_inuse);
        ivMore = (ImageView) findViewById(R.id.iv_more);
        llFootbar = (LinearLayout) findViewById(R.id.ll_footbar);
        llFooterCamera = (LinearLayout) findViewById(R.id.ll_footbar2);
        ll_home = (LinearLayout) findViewById(R.id.ll_home);
        ll_pdf_create = (LinearLayout) findViewById(R.id.ll_pdf_create);
        button_download = (Button) findViewById(R.id.button_download);
        btn_close = (Button) findViewById(R.id.btn_close);
        tv_temp_1 = (TextView) findViewById(R.id.tv_temp_1);
        tv_carrypark_co = (TextView) findViewById(R.id.tv_carrypark_co);
        tv_telphone = (TextView) findViewById(R.id.tv_telphone);
        tv_receipt = (TextView) findViewById(R.id.tv_receipt);
        tv_amount = (TextView) findViewById(R.id.tv_amount);
        tv_date_of_use = (TextView) findViewById(R.id.tv_date_of_use);
        id_tex1 = (TextView) findViewById(R.id.id_tex1);
        id_tex2 = (TextView) findViewById(R.id.id_tex2);

    }

    private boolean loadFragment(Fragment fragment) {
        //switching fragment
        if (fragment != null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, fragment)
                    .commit();
            return true;
        }
        return false;
    }

    @Override
    public void onBackPressed() {

        if (CarryParkApplication.isIsAliPay()) {


            DialogManager.showUniActionDialog(CarryParkApplication.getCurrentActivity(), getResources().getString(R.string.alipayNotSupport), "ok", new DialogManager.IUniActionDialogOnClickListener() {
                @Override
                public void onPositiveClick() {
                }
            });
            super.onBackPressed();
            finish();


        } else {
            //switching fragment
            Fragment fragment = new ScanQrcodeFragment();
            HomeFragment fragment1 = new HomeFragment();
            if (fragment1.isVisible()) {
                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.fragment_container, fragment)
                        .commit();
            }
            android.os.Process.killProcess(android.os.Process.myPid());

            finish();
        }


    }

    private void getUserDetails() {
        String acess_token = GloablMethods.API_HEADER + AppController.getString(BottomNavigation.this, "acess_token");
        //String acess_token = sharedPreferences.getString("access_token", null);
        String lan = "";
        if (SharedPreferenceUtility.isJapanease()) {
            lan = ConstantProject.forJapaneaseResponse;

        } else if (SharedPreferenceUtility.isEnglish()) {
            lan = "en";
        } else if (SharedPreferenceUtility.isKorean()) {
            lan = ConstantProject.forKoreanResponse;
        } else if (SharedPreferenceUtility.isChinease()) {
            lan = ConstantProject.forChineaseResponse;
        }
        apiService.userDetails(acess_token, lan).enqueue(new Callback<UserDetailsResponse>() {
            @Override
            public void onResponse(Call<UserDetailsResponse> call, Response<UserDetailsResponse> response) {
                String code = String.valueOf(response.code());
                if (response.code() == 200 && response.body().getData() != null) {
                    //progressDialog.dismiss();
                    Boolean status = response.body().getData().getSuccess();
                    if (status == true) {
                        //progressDialog.dismiss();
                        String device_status = response.body().getData().getLocation();
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putString("user_name", response.body().getData().getUser().getFirstName());
                        editor.apply();
                        boolean isDisableScan = false;

                        CarryParkApplication.setUsedLockerList(response.body().getData().getUser().getUsedLockers());
                        SharedPreferenceUtility.saveIsCheckedLockerSize(true);
                        if (CarryParkApplication.getUsedLockerList() != null) {
                            boolean isTrue = false;
                            for (int i = 0; i < CarryParkApplication.getUsedLockerList().size(); i++) {
                                if (CarryParkApplication.getUsedLockerList().get(i).getPresentStatus().equalsIgnoreCase("LOCKED")) {
                                    isTrue = true;
                                }
                            }
                            if (CarryParkApplication.getUsedLockerList().size() > 0) {

                                if (isTrue) {
                                    isDisableScan = true;
                                    disableScan();
                                }

                            } else {
                                isDisableScan = false;
                                OnInUseClick(isDisableScan);
                                enableScanAndInUseClic();
                            }
                        } else {
                            isDisableScan = false;
                            OnInUseClick(isDisableScan);
                            enableScanAndInUseClic();
                        }
                    }/*else if(status == false){
                        String msg = response.body().getMessage();
                    }*/
                }/*else if(response.code() == 200 && response.body().getData() == null) {
                    progressDialog.dismiss();
                    String msg = response.body().getMessage();
                }*/ else if (response.code() == 404) {
                }
            }

            @Override
            public void onFailure(Call<UserDetailsResponse> call, Throwable t) {


            }
        });
    }


    public void disableScanAndEnableInUseClic() {
        isClickableScan = false;
        isClicableInuse = true;

        tvInuse.setTextColor(CarryParkApplication.getCurrentActivity().getResources().getColor(R.color.colorAccent));
        ivInuse.setImageResource(R.drawable.innuse_red);
        ivInuse.setColorFilter(getResources().getColor(R.color.colorAccent), PorterDuff.Mode.SRC_IN);


        tvScan.setTextColor(CarryParkApplication.getCurrentActivity().getResources().getColor(R.color.colorPrimaryDarkK));
        ivScan.setImageResource(R.drawable.scan_black);
        ivScan.setColorFilter(getResources().getColor(R.color.colorPrimaryDarkK), PorterDuff.Mode.SRC_IN);


    }

    public void disableAllNav() {
        isClickableScan = false;
        isClicableInuse = false;
        isSettingsEnable = false;

        tvInuse.setTextColor(CarryParkApplication.getCurrentActivity().getResources().getColor(R.color.colorPrimaryDarkK));
        ivInuse.setImageResource(R.drawable.innuse_black);
        ivInuse.setColorFilter(getResources().getColor(R.color.colorPrimaryDarkK), PorterDuff.Mode.SRC_IN);


        tvScan.setTextColor(CarryParkApplication.getCurrentActivity().getResources().getColor(R.color.colorPrimaryDarkK));
        ivScan.setImageResource(R.drawable.scan_black);
        ivScan.setColorFilter(getResources().getColor(R.color.colorPrimaryDarkK), PorterDuff.Mode.SRC_IN);


        tvMore.setTextColor(CarryParkApplication.getCurrentContext().getResources().getColor(R.color.colorPrimaryDarkK));
        ivMore.setImageResource(R.drawable.settings_black);
        ivMore.setColorFilter(getResources().getColor(R.color.colorPrimaryDarkK), PorterDuff.Mode.SRC_IN);
        llScan_button.setClickable(false);
        llInuse.setClickable(false);
        llMore.setClickable(false);


    }

    public void disableScan() {
        isClickableScan = false;
        tvScan.setTextColor(CarryParkApplication.getCurrentActivity().getResources().getColor(R.color.colorPrimaryDarkK));
        ivScan.setImageResource(R.drawable.scan_black);
        ivScan.setColorFilter(getResources().getColor(R.color.colorPrimaryDarkK), PorterDuff.Mode.SRC_IN);

    }

    public void enableScan() {
        isClickableScan = true;
        tvScan.setTextColor(CarryParkApplication.getCurrentActivity().getResources().getColor(R.color.colorBlack));
        ivScan.setImageResource(R.drawable.scan_black);
        ivScan.setColorFilter(getResources().getColor(R.color.colorBlack), PorterDuff.Mode.SRC_IN);

    }

    public void enableScanAndInUseClic() {
        isClickableScan = true;
        isClicableInuse = true;
        llScan_button.setClickable(true);
        llInuse.setClickable(true);

        llScan_button.setEnabled(true);
        llInuse.setEnabled(true);

    }

    public void enableAll() {
        isClickableScan = true;
        isClicableInuse = true;
        isSettingsEnable = true;

        tvInuse.setTextColor(CarryParkApplication.getCurrentActivity().getResources().getColor(R.color.colorBlack));
        ivInuse.setImageResource(R.drawable.innuse_black);
        ivInuse.setColorFilter(getResources().getColor(R.color.colorBlack), PorterDuff.Mode.SRC_IN);


        tvScan.setTextColor(CarryParkApplication.getCurrentActivity().getResources().getColor(R.color.colorBlack));
        ivScan.setImageResource(R.drawable.scan_black);
        ivScan.setColorFilter(getResources().getColor(R.color.colorBlack), PorterDuff.Mode.SRC_IN);


        tvMore.setTextColor(CarryParkApplication.getCurrentContext().getResources().getColor(R.color.colorBlack));
        ivMore.setImageResource(R.drawable.settings_black);
        ivMore.setColorFilter(getResources().getColor(R.color.colorBlack), PorterDuff.Mode.SRC_IN);
        llScan_button.setClickable(true);

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case CAMERA:
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    //Start your camera handling here
                } else {
                    //AppUtils.showUserMessage("You declined to allow the app to access your camera", this);
                }
        }
    }

    public void EvaluateScanClick() {
        enableScan();
        OnScanClick();
    }

    public void showPDFView() {
        showDialogueInvoice();
        ll_pdf_create.setVisibility(View.VISIBLE);

    }

    public static void hidePdfView() {
        ll_pdf_create.setVisibility(View.GONE);
    }

    public static void showDialogueInvoice() {


        tv_amount.setText("￥" + CarryParkApplication.getInvoiceAmount() + "-");
        SimpleDateFormat sdf = null;
        SimpleDateFormat day = null;
        SimpleDateFormat month = null;
        SimpleDateFormat year = null;
        SimpleDateFormat amOrpm = null;
        SimpleDateFormat hh = null;
        SimpleDateFormat mm = null;
        try {
            sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH);
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
            Date date_end;
            date_end = sdf.parse(CarryParkApplication.getInvoiceDate());
            SimpleDateFormat outputFormat = new SimpleDateFormat(CarryParkApplication.getCurrentContext().getResources().getString(R.string.paymentDateFormat), Locale.ENGLISH);
            outputFormat.setTimeZone(TimeZone.getDefault());


            if (CarryParkApplication.isIsJapaneaseLang()) {

                String dateToStr = CommonMethod.dateFormatInJapanease(month.format(date_end), day.format(date_end), amOrpm.format(date_end),
                        year.format(date_end), hh.format(date_end), mm.format(date_end));
                tv_date_of_use.setText(CarryParkApplication.getCurrentContext().getResources().getString(R.string.date_of_use) + " " + dateToStr);

            } else {
                tv_date_of_use.setText(CarryParkApplication.getCurrentContext().getResources().getString(R.string.date_of_use) + " " + outputFormat.format(date_end));


            }
        } catch (ParseException e) {
            e.printStackTrace();
        }


        btn_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                hidePdfView();
            }
        });


        AbstractViewRenderer page = new AbstractViewRenderer(CarryParkApplication.getCurrentActivity(), R.layout.dialogue_invoice) {
            private String _text;


            public void setText(String text) {
                _text = text;
            }

            @Override
            protected void initView(View view) {


            }
        };
// you can reuse the bitmap if you want
        page.setReuseBitmap(true);
        button_download.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String first_text = tv_temp_1.getText().toString();
                String second_text = tv_carrypark_co.getText().toString();
                String third_text = tv_telphone.getText().toString();
                String fourth_text = tv_receipt.getText().toString();

                String fifth_text = tv_amount.getText().toString();

                String six_text = tv_date_of_use.getText().toString();
                String seventh_text = id_tex1.getText().toString();
                String eight = id_tex2.getText().toString();
                PdfCreator(first_text, second_text, third_text, fourth_text, fifth_text, six_text, seventh_text, eight);


            }
        });
    }

    public static void PdfCreator(String one, String two, String three,
                                  String four, String five, String six, String seven, String eight) {


        //////////////////////////////////
        // Create Temp File to save Pdf To
        //  final  File savedPDFFile = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).toString());

        final File savedPDFFile = FileManager.getInstance().createTempFile(CarryParkApplication.getCurrentContext(), "pdf", false);
// Generate Pdf From Html


        PDFUtil.generatePDFFromHTML(CarryParkApplication.getCurrentContext(), savedPDFFile, "<!DOCTYPE html>\n" +
                "<html>\n" +
                "\n" +
                "<body>\n" + "<style>\n" + "div.corner {\n" +

                "   margin-left: 70px;\n" +
                "text-align: left;" +
                "    font-size: 20px;\n" +
                "}" +
                "h1 {text-align: center;}\n" +
                "p {text-align: center;}\n" +
                "div {text-align: center;}\n" +
                "div.cent {\n" +
                " \n" +
                "    font-size: 20px;\n" +
                "    text-align: center;\n" +
                "}\n" +
                "</style>" +
                "\n" +
                "<div style=\"text-align: center\">\n" +
                "\n" +
                "<br>" +

                "<br>" +
                "<br>" +
                "<br>" +
                "<br>" +

                "<br>" +
                "<br>" +
                "<br>" +
                "<br>" +
                "<img src=\"http://167.172.39.84/images/cp_withouticon_logo.png\" width=\"250\" height=\"100\" align=\"center\">\n" +
                "</div>" +
                "<br>" +
                "<br>" +
                "\n" +
                "<div  class=\"cent\">" + one + "</div>\n" +
                "<div  class=\"cent\">" + two + "</div>\n" +
                "\n" +
                "<div  class=\"cent\">" + three + "</div>\n" +
                "\n" +
                "\n" +
                "\n" +
                "\n" +
                "<h1 class=\"cent\">" + four + "</h1>\n" +
                "<h1 class=\"cent\">" + five + "</h1>\n" +
                "\n" +

                "<hr  color=\"black\"style=\"width:100%;text-align:left;margin-left:0\">\n" +
                "\n" +
                "<div class=\"cent\">" + six + "</div>" +
                "<br>" +
                "<br>" +
                "<div class=\"corner\">" + seven + "\n" +
                "</div>" +
                "<div class=\"corner\">" + eight + "\n" +
                "</div>" +

                "\n" +
                "\n" +
                "\n" +
                "\n" +
                "\n" +
                "\n" +
                "\n" +
                "\n" +
                "\n" +
                "\n" +
                "\n" +
                "</body>\n" +
                "</html>\n", new PDFPrint.OnPDFPrintListener() {
            @Override
            public void onSuccess(File file) {
                // Open Pdf Viewer
                Uri pdfUri = Uri.fromFile(savedPDFFile);
            /* File path=   Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);

                File sourceFile = savedPDFFile;
                File destFile = new File(path+"carrypark.pdf");

                if (sourceFile.renameTo(destFile)) {
                    System.out.println("File moved successfully");
                } else {
                    System.out.println("Failed to move file");
                }*/
                //  moveFile("/storage/emulated/0/Android/data/net.simplifiedcoding.carrypark/files/temp/1606406174843.pdf","/storage/emulated/0/Download/carryp.pdf");
                copyFileOrDirectory(pdfUri.getPath(), "/storage/emulated/0/Download/carrypark");
                CarryParkApplication.setOpenPdfLocation(pdfUri.getPath());
                Intent intentPdfViewer = new Intent(CarryParkApplication.getCurrentActivity(), PdfViewerActivity.class);
                intentPdfViewer.putExtra(PdfViewerActivity.PDF_FILE_URI, pdfUri);

                CarryParkApplication.getCurrentContext().startActivity(intentPdfViewer);
            }

            @Override
            public void onError(Exception exception) {
                exception.printStackTrace();
            }
        });
    }


    private void moveToHomeFromScanClick(){

    Fragment fragment = null;
    fragment =new

    HomeFragment();

    loadFragment(fragment);
                if(!SharedPreferenceUtility.getIsCheckedLockerSize())

    getUserDetails();
}
}
