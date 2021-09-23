package net.simplifiedcoding.carrypark;


import android.app.Application;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.print.PDFPrint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ScrollView;
import android.widget.TextView;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import com.tejpratapsingh.pdfcreator.utils.FileManager;
import com.tejpratapsingh.pdfcreator.utils.PDFUtil;
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
import net.rest.model.ChangeEmailResponseModel;
import net.rest.model.InvoiceModel;
import net.ui.BottomNavigation;
import net.ui.PdfViewerActivity;
import net.ui.UserSelectionActivity;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import java.io.File;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import static android.content.Context.MODE_PRIVATE;


/**
 * A simple {@link Fragment} subclass.
 */
public class DeviceLockedFragment extends Fragment implements View.OnClickListener {

    private SharedPreferences sharedPreferences;
    TextView tvUser, tvDeviceLckOrUnlck, tvLckUnlckDesc, tvDeviceName, tvTv1, tvTv2,tv_deviceName,tv_invoice;
    Button buttonExitFromCarryPark;
    String totalAmount,unlockTime;
    ScrollView sv_PdfScrool,sv_normal;

    String languageToLoad="en";
    Fragment targetFragment = null;
    ApiInterface apiService;

    public DeviceLockedFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment




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


        return inflater.inflate(R.layout.fragment_device_locked, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        sharedPreferences = getActivity().getSharedPreferences("carry_park", MODE_PRIVATE);
        tvUser = (TextView) view.findViewById(R.id.tv_user);
        tvDeviceLckOrUnlck = (TextView) view.findViewById(R.id.tv_deviceLckOrUnlck);
        tvDeviceName = (TextView) view.findViewById(R.id.tv_device_name);
        tvLckUnlckDesc = (TextView) view.findViewById(R.id.tv_lck_unlck_desc);
        tv_deviceName = (TextView) view.findViewById(R.id.tv_deviceName);
        tvTv1 = (TextView) view.findViewById(R.id.tv_tv1);
        tvTv2 = (TextView) view.findViewById(R.id.tv_tv2);
        tv_invoice = (TextView)view.findViewById(R.id.tv_invoice);

        sv_normal = (ScrollView) view.findViewById(R.id.sv_normal);
        String lockOrUnlock = sharedPreferences.getString("isLocked", null);
        String user = sharedPreferences.getString("user_name", null);
        //tvUser.setText("Hi,"+user);
        if (CarryParkApplication.isIsEnglishLang()) {
            tvUser.setText(getString(R.string.hi) + ", " + user);
        } else if (CarryParkApplication.isIsJapaneaseLang()) {
            //"ようこそ、Carryparkへ
            //○○さん"
            tvUser.setText("ようこそ " + user + "さん");
        }
        else
        {
            tvUser.setText(getString(R.string.hi)+", "+user);
        }
        tv_invoice.setVisibility(View.GONE);

        buttonExitFromCarryPark = (Button) view.findViewById(R.id.button_exit_from_carry_park);

        if (SharedPreferenceUtility.isScannedLock()) {
            tvTv1.setText(R.string.successInfoLock);
            tvTv2.setText(R.string.successInfoLockRed);
            buttonExitFromCarryPark.setText(CarryParkApplication.getCurrentContext().getResources().getString(R.string.exitApp));
            tv_deviceName.setText(CarryParkApplication.getScannedDeviceName());
            tv_deviceName.setVisibility(View.VISIBLE);

        } else {
            tv_deviceName.setVisibility(View.GONE);
            tvLckUnlckDesc.setText(R.string.succUnlock);
            tvTv1.setText(R.string.successInfoUnlock);
            tvTv2.setText(R.string.successInfoUnlockRed);
            buttonExitFromCarryPark.setText(CarryParkApplication.getCurrentContext().getResources().getString(R.string.finish));
            callInvoice();
        }

        buttonExitFromCarryPark.setOnClickListener(this);
        CarryParkApplication.setIsAliPay(false);
        tv_invoice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((BottomNavigation) getActivity()).showPDFView();

            }
        });

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.button_exit_from_carry_park:
                /*targetFragment = new DeviceLockedFragment();
                InUseFragment ldf = new InUseFragment ();
                replaceFragment(targetFragment,ldf);*/

                FragmentManager fm = getActivity().getSupportFragmentManager();
                for (int i = 0; i < fm.getBackStackEntryCount(); ++i) {
                    fm.popBackStack();
                }
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("IS_LOGGED_IN", "FALSE");
                editor.apply();
                if (SharedPreferenceUtility.isScannedLock()) {
                    Intent intent = new Intent(getContext(), UserSelectionActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                } else {
                    Intent intent = new Intent(getContext(), UserSelectionActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                }

                getActivity().finish();
                break;
        }

    }

    public void replaceFragment(Fragment someFragment, Fragment currentFragment) {
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, someFragment);
        transaction.addToBackStack(null);
        transaction.commit();

        //Put the value
        //InUseFragment ldf = new InUseFragment ();
        /*Bundle args = new Bundle();
        args.putString("scannedDeviceCode", scanned_device_code);
        ldf.setArguments(args);*/


        //Inflate the fragment
        //getFragmentManager().beginTransaction().add(R.id.fragment_container,currentFragment).commit();


    }
    public void callInvoice() {
        Map<String, Object> candidateMap = new HashMap<>();
        candidateMap.put("device_id", CarryParkApplication.getScannedDeviceCode());


        String acess_token = GloablMethods.API_HEADER + AppController.getString(CarryParkApplication.getmContext(), "acess_token");
        apiService =
                ApiClient.getClient().create(ApiInterface.class);
        apiService.CallInvoiceApi(acess_token, candidateMap).enqueue(new Callback<InvoiceModel>() {
            @Override
            public void onResponse(Call<InvoiceModel> call, Response<InvoiceModel> response) {
                ((BaseActivity) getActivity()).hideBusyAnimation();
                if (response!=null && response.body().getSuccess()) {
                    totalAmount=response.body().getData().getTotal_amount();
                    unlockTime =response.body().getData().getUnlock_time();
                    CarryParkApplication.setInvoiceDate(unlockTime);
                    CarryParkApplication.setInvoiceAmount(totalAmount);
                    tv_invoice.setVisibility(View.VISIBLE);
                } else {
                    DialogManager.showAlertSingleActionDialog(CarryParkApplication.getCurrentActivity(), response.body().getMessage(), "ok", new DialogManager.IUniActionDialogOnClickListener() {
                        @Override
                        public void onPositiveClick() {

                        }
                    });
                }


            }




            @Override
            public void onFailure(Call<InvoiceModel> call, Throwable t) {
                ((BaseActivity) getActivity()).hideBusyAnimation();
                if (!Utils.isNetworkConnectionAvailable(CarryParkApplication.getCurrentContext())) {
                    DialogManager.showAlertSingleActionDialog(CarryParkApplication.getCurrentActivity(), CarryParkApplication.getCurrentContext().getResources().getString(R.string.no_internet), "ok", new DialogManager.IUniActionDialogOnClickListener() {
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




}
