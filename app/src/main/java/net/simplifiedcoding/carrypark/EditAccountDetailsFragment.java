package net.simplifiedcoding.carrypark;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.Nullable;
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
import net.ui.UserSelectionActivity;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import java.util.Locale;

import static android.content.Context.MODE_PRIVATE;

public class EditAccountDetailsFragment extends Fragment {

    TextView tv_change_pin;
    TextView tv_change_email;
    TextView tv_deregistraction;
    TextView tv_change_phone_number;
    ImageView img_back_arrow;
    TextView tv_user;
    private SharedPreferences sharedPreferences;
    ApiInterface apiService;


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


        return inflater.inflate(R.layout.fragment_edit_accountdetails, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        tv_change_pin = (TextView) view.findViewById(R.id.tv_change_pin);
        tv_change_email = (TextView) view.findViewById(R.id.tv_change_email);
        tv_deregistraction = (TextView) view.findViewById(R.id.tv_deregistraction);
        img_back_arrow = (ImageView) view.findViewById(R.id.img_back_arrow);
        tv_user = (TextView) view.findViewById(R.id.tv_user);
        sharedPreferences = getActivity().getSharedPreferences("carry_park", MODE_PRIVATE);
        tv_change_phone_number = (TextView) view.findViewById(R.id.tv_change_phone_number);
        apiService =
                ApiClient.getClient().create(ApiInterface.class);

        tv_change_pin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                replaceFragmentToChangePin();
            }
        });

        tv_change_email.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ChangeEmailAddressFragment ldf = new ChangeEmailAddressFragment();
                getFragmentManager().beginTransaction().add(R.id.fragment_container, ldf).commit();
            }
        });

        img_back_arrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MoreFragment ldf = new MoreFragment();
                getFragmentManager().beginTransaction().add(R.id.fragment_container, ldf).commit();

            }
        });
        tv_change_phone_number.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                ChangePhoneNumber ldf = new ChangePhoneNumber();
                getFragmentManager().beginTransaction().add(R.id.fragment_container, ldf).commit();

            }
        });
        tv_deregistraction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!((BaseActivity) getActivity()).isNetworkAvailable())
                {
                    DialogManager.showAlertSingleActionDialog(CarryParkApplication.getCurrentActivity(), CarryParkApplication.getCurrentContext().getResources().getString(R.string.OperfailedExtnd), "ok", new DialogManager.IUniActionDialogOnClickListener() {
                        @Override
                        public void onPositiveClick() {

                        }
                    });
                }
                else {

                    DialogManager.showMultiActionDialogue(CarryParkApplication.getCurrentActivity(), CarryParkApplication.getCurrentContext().getResources().getString(R.string.deRegister),  new DialogManager.IMultiActionDialogOnClickListener() {
                        @Override
                        public void onPositiveClick() {
                            callDeRegistraction();
                        }

                        @Override
                        public void onNegativeClick() {

                        }
                    });


                }

            }
        });

        String userName = sharedPreferences.getString("user_name", null);
        if (CarryParkApplication.isIsEnglishLang()) {
            tv_user.setText(getString(R.string.hi) + ", " + userName);
        } else if (CarryParkApplication.isIsJapaneaseLang()) {
            //"ようこそ、Carryparkへ
            //○○さん"
            tv_user.setText("ようこそ " + userName + "さん");
        }
        else
        {
            tv_user.setText(getString(R.string.hi)+", "+userName);
        }

    }

    public void replaceFragmentToChangePin() {

        ChangePinFragment ldf = new ChangePinFragment();
        getFragmentManager().beginTransaction().add(R.id.fragment_container, ldf).commit();


    }

    public void callDeRegistraction() {
        ((BaseActivity) getActivity()).showBusyAnimation("");

        String acess_token = GloablMethods.API_HEADER + AppController.getString(CarryParkApplication.getmContext(), "acess_token");
        apiService.CallDeRegister(acess_token).enqueue(new Callback<ChangeEmailResponseModel>() {
            @Override
            public void onResponse(Call<ChangeEmailResponseModel> call, Response<ChangeEmailResponseModel> response) {
                if(getActivity()!=null)
                    ((BaseActivity) getActivity()).hideBusyAnimation();
                if (response!=null && response.body()!=null && response.body().isSuccess()) {
                    DialogManager.showUniActionDialog(CarryParkApplication.getCurrentActivity(), response.body().getMessage(), "ok", new DialogManager.IUniActionDialogOnClickListener() {
                        @Override
                        public void onPositiveClick() {

                            SharedPreferences.Editor editor=sharedPreferences.edit();
                            editor.putString("IS_LOGGED_IN","FALSE");
                            editor.putString("user_name",null);
                            editor.apply();
                            Intent intent = new Intent(getContext(), UserSelectionActivity.class);
                            startActivity(intent);
                            getActivity().finish();
                        }
                    });

                } else {
                    if (response!=null && response.body()!=null && response.body().getMessage()!=null)
                    {
                        DialogManager.showUniActionDialog(CarryParkApplication.getCurrentActivity(), response.body().getMessage(), "ok", new DialogManager.IUniActionDialogOnClickListener() {
                            @Override
                            public void onPositiveClick() {

                            }
                        });
                    }

                }


            }


            @Override
            public void onFailure(Call<ChangeEmailResponseModel> call, Throwable t) {
                ((BaseActivity) getActivity()).hideBusyAnimation();
                if (!Utils.isNetworkConnectionAvailable(CarryParkApplication.getCurrentContext())) {
                    DialogManager.showAlertSingleActionDialog(CarryParkApplication.getCurrentActivity(),CarryParkApplication.getCurrentContext().getResources().getString(R.string.no_internet), "ok", new DialogManager.IUniActionDialogOnClickListener() {
                        @Override
                        public void onPositiveClick() {

                        }


                    });                   } else {
                    DialogManager.showAlertSingleActionDialog(CarryParkApplication.getCurrentActivity(),CarryParkApplication.getCurrentContext().getResources().getString(R.string.OperfailedExtnd), "ok", new DialogManager.IUniActionDialogOnClickListener() {
                        @Override
                        public void onPositiveClick() {

                        }


                    });                   }
            }
        });
    }


}
