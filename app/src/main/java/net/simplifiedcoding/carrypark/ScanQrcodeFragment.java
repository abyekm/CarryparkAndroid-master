package net.simplifiedcoding.carrypark;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.budiyev.android.codescanner.CodeScanner;
import com.budiyev.android.codescanner.CodeScannerView;
import com.budiyev.android.codescanner.DecodeCallback;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.Result;

import net.CarryParkApplication;
import net.others.ConstantProject;
import net.others.SharedPreferenceUtility;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import static android.app.Activity.RESULT_OK;

public class ScanQrcodeFragment extends Fragment  {

    private CodeScanner mCodeScanner;
    FrameLayout content_frame;
    Activity activity;
    CodeScannerView scannerView;

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


          activity = getActivity();
        View root = inflater.inflate(R.layout.fragment_scanner, container, false);
         scannerView = root.findViewById(R.id.scanner_view);

       mCodeScanner = new CodeScanner(activity, scannerView);
        mCodeScanner.setTouchFocusEnabled(false);
        CarryParkApplication.setIsRescan(false);



        mCodeScanner.setDecodeCallback(new DecodeCallback() {
            @Override
            public void onDecoded(@NonNull final Result result) {
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {



                        if (result.getText()!=null)
                        {
                            if (result.getText().toString().equals("07152283"))
                            {
                                Log.e("comments","Scan resultd"+"07152283");

                            }
                            else
                            {
                                if (mCodeScanner!=null)
                                {
                                    mCodeScanner.stopPreview();
                                    mCodeScanner.releaseResources();
                                    mCodeScanner=null;

                                }
                                Log.e("comments","Scan resultd"+""+result.getText());
                                getFragmentManager().beginTransaction().remove(ScanQrcodeFragment.this).commitAllowingStateLoss();


                              //  getActivity().getFragmentManager().popBackStack();
                                CodeScannerFragment codeScannerFragment = new CodeScannerFragment();
                                Bundle args = new Bundle();
                                args.putString("qrcode", result.getText());

                                codeScannerFragment.setArguments(args);
                                getFragmentManager().beginTransaction().add(R.id.fragment_container, codeScannerFragment).commit();

                            }

                        }
                    }
                });
            }
        });
        scannerView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mCodeScanner.startPreview();
            }
        });
        mCodeScanner.setAutoFocusEnabled(true);


        return root;


    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);




    }

    @Override
    public void onResume() {
        super.onResume();
        if (mCodeScanner!=null)
        {
            mCodeScanner.startPreview();
        }
        else {
            try {
                mCodeScanner = new CodeScanner(activity, scannerView);
                mCodeScanner.setTouchFocusEnabled(false);
                mCodeScanner.startPreview();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }



    }



    @Override
    public void onDestroy() {

        if (mCodeScanner!=null)
        {
            mCodeScanner.releaseResources();
            mCodeScanner.stopPreview();
            mCodeScanner=null;

        }

        super.onDestroy();
    }

    @Override
    public void onStop() {


        super.onStop();
        if (mCodeScanner!=null)
        {
            mCodeScanner.releaseResources();
            mCodeScanner.stopPreview();
            mCodeScanner=null;

        }

    }
}
