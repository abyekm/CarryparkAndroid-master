package net.simplifiedcoding.carrypark;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
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
import net.ui.CommonQandAActivity;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import java.util.Locale;

import static android.content.Context.MODE_PRIVATE;

public class HelpFragment extends Fragment {
    ImageView img_back_arrow;
    TextView tv_user,tv_common_qanda,tv_enq_by_mail,tv_help_desk_solution;
    private SharedPreferences sharedPreferences;
    ApiInterface apiService;
    private final static int MY_PERMISSIONS_REQUEST_CALL_PHONE = 111;


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


        return inflater.inflate(R.layout.fragment_help, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        img_back_arrow = (ImageView) view.findViewById(R.id.img_back_arrow);
        tv_user = (TextView) view.findViewById(R.id.tv_user);
        tv_common_qanda = (TextView)view.findViewById(R.id.tv_common_qanda);
        tv_enq_by_mail = (TextView)view.findViewById(R.id.tv_enq_by_mail);
        tv_help_desk_solution = (TextView)view.findViewById(R.id.tv_help_desk_solution);
        apiService =
                ApiClient.getClient().create(ApiInterface.class);

        sharedPreferences = getActivity().getSharedPreferences("carry_park", MODE_PRIVATE);

        img_back_arrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MoreFragment ldf = new MoreFragment();
                getFragmentManager().beginTransaction().add(R.id.fragment_container, ldf).commit();

            }
        });
        tv_common_qanda.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {



                Intent intent =new Intent(CarryParkApplication.getCurrentActivity(), CommonQandAActivity.class);
                startActivity(intent);
            }
        });
        tv_enq_by_mail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!((BaseActivity) getActivity()).isNetworkAvailable())
                {
                    DialogManager.showUniActionDialog(CarryParkApplication.getCurrentActivity(), CarryParkApplication.getCurrentContext().getResources().getString(R.string.OperfailedExtnd), "ok", new DialogManager.IUniActionDialogOnClickListener() {
                        @Override
                        public void onPositiveClick() {

                        }
                    });
                }
                else {
                    callEnquiryByMAil();
                }

            }
        });

        tv_help_desk_solution.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent mIntent = new Intent(Intent.ACTION_CALL);

                String number = ("tel:" + ConstantProject.helpDeskNumber);
                mIntent = new Intent(Intent.ACTION_CALL);
                mIntent.setData(Uri.parse(number));
// Here, thisActivity is the current activity
                if (ContextCompat.checkSelfPermission(CarryParkApplication.getCurrentActivity(),
                        Manifest.permission.CALL_PHONE)
                        != PackageManager.PERMISSION_GRANTED) {

                    ActivityCompat.requestPermissions(CarryParkApplication.getCurrentActivity(),
                            new String[]{Manifest.permission.CALL_PHONE},
                            MY_PERMISSIONS_REQUEST_CALL_PHONE);

                    // MY_PERMISSIONS_REQUEST_CALL_PHONE is an
                    // app-defined int constant. The callback method gets the
                    // result of the request.
                } else {
                    startActivity(mIntent);
                    //You already have permission
                    try {
                        startActivity(mIntent);
                    } catch(SecurityException e) {
                        e.printStackTrace();
                    }
                }




            }
        });



        String userName = sharedPreferences.getString("user_name",null);
        if (CarryParkApplication.isIsEnglishLang())
        {
            tv_user.setText(getString(R.string.hi)+", "+userName);
        }
        else if (CarryParkApplication.isIsJapaneaseLang())
        {
            //"ようこそ、Carryparkへ
            //○○さん"
            tv_user.setText("ようこそ "+userName+"さん");
        }
        else
        {
            tv_user.setText(getString(R.string.hi)+", "+userName);
        }
    }

    public void callEnquiryByMAil() {

        if(!((Activity) CarryParkApplication.getCurrentContext()).isFinishing())
        {
            ((BaseActivity) getActivity()).showBusyAnimation("");
        }

        String acess_token = GloablMethods.API_HEADER + AppController.getString(CarryParkApplication.getmContext(), "acess_token");
        apiService.CallEnquiryMail(acess_token).enqueue(new Callback<ChangeEmailResponseModel>() {
            @Override
            public void onResponse(Call<ChangeEmailResponseModel> call, Response<ChangeEmailResponseModel> response) {
                ((BaseActivity) getActivity()).hideBusyAnimation();
                if (response.body().isSuccess()) {
                    DialogManager.showUniActionDialog(CarryParkApplication.getCurrentActivity(), response.body().getMessage(), "ok", new DialogManager.IUniActionDialogOnClickListener() {
                        @Override
                        public void onPositiveClick() {

                        }
                    });

                } else {
                    DialogManager.showUniActionDialog(CarryParkApplication.getCurrentActivity(), response.body().getMessage(), "ok", new DialogManager.IUniActionDialogOnClickListener() {
                        @Override
                        public void onPositiveClick() {
                        }
                    });
                }


            }


            @Override
            public void onFailure(Call<ChangeEmailResponseModel> call, Throwable t) {
                ((BaseActivity) getActivity()).hideBusyAnimation();
                if (!Utils.isNetworkConnectionAvailable(CarryParkApplication.getCurrentContext())) {
                    DialogManager.showAlertSingleActionDialog(CarryParkApplication.getCurrentActivity(),CarryParkApplication.getCurrentContext().getResources().getString(R.string.OperfailedExtnd), "ok", new DialogManager.IUniActionDialogOnClickListener() {
                        @Override
                        public void onPositiveClick() {

                        }


                    });                } else {
                    DialogManager.showAlertSingleActionDialog(CarryParkApplication.getCurrentActivity(),CarryParkApplication.getCurrentContext().getResources().getString(R.string.OperfailedExtnd), "ok", new DialogManager.IUniActionDialogOnClickListener() {
                        @Override
                        public void onPositiveClick() {

                        }


                    });                }
            }
        });




    }
    @TargetApi(Build.VERSION_CODES.M)
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_CALL_PHONE: {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {



                } else {

                }
                return;
            }
        }

    }


}