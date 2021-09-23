package net.simplifiedcoding.carrypark;

import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
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
import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEvent;
import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEventListener;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import static android.content.Context.MODE_PRIVATE;

public class ChangeEmailAddressFragment extends Fragment {
    EditText et_current_pin, et_confirm_new_email, et_new_email;
    ImageView img_back_arrow;
    TextView tv_user;
    private SharedPreferences sharedPreferences;
    ApiInterface apiService;
    TextView tv_current_email;
    Button button_save_new_email;
    String languageToLoad = "ja";


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
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
        CarryParkApplication.getCurrentContext().getResources().updateConfiguration(config,
                CarryParkApplication.getCurrentContext().getResources().getDisplayMetrics());


        return inflater.inflate(R.layout.fragment_change_email_address, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        apiService =
                ApiClient.getClient().create(ApiInterface.class);

        et_current_pin = (EditText) view.findViewById(R.id.et_current_pin);
        et_new_email = (EditText) view.findViewById(R.id.et_new_email);
        et_confirm_new_email = (EditText) view.findViewById(R.id.et_confirm_new_email);
        img_back_arrow = (ImageView) view.findViewById(R.id.img_back_arrow);
        tv_user = (TextView) view.findViewById(R.id.tv_user);
        tv_current_email = (TextView) view.findViewById(R.id.tv_current_email);
        sharedPreferences = getActivity().getSharedPreferences("carry_park", MODE_PRIVATE);
        button_save_new_email = (Button) view.findViewById(R.id.button_save_new_email);

        img_back_arrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditAccountDetailsFragment ldf = new EditAccountDetailsFragment();
                getFragmentManager().beginTransaction().add(R.id.fragment_container, ldf).commit();

            }
        });

        KeyboardVisibilityEvent.setEventListener(
                getActivity(),
                new KeyboardVisibilityEventListener() {
                    @Override
                    public void onVisibilityChanged(boolean isOpen) {
                        // some code depending on keyboard visiblity status
                        if (isOpen) {
                            button_save_new_email.setVisibility(View.GONE);
                        } else {
                            button_save_new_email.setVisibility(View.VISIBLE);
                        }
                    }
                });
        String userName = sharedPreferences.getString("user_name", null);
        if (SharedPreferenceUtility.isEnglish()) {
            tv_user.setText(getString(R.string.hi) + ", " + userName);
        } else if (SharedPreferenceUtility.isJapanease()) {
            //"ようこそ、Carryparkへ
            //○○さん"
            tv_user.setText("ようこそ " + userName + "さん");
        } else {
            tv_user.setText(getString(R.string.hi) + ", " + userName);
        }

        tv_current_email.setText(SharedPreferenceUtility.getEmailId());

        button_save_new_email.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ChangeEmailAddress();
            }
        });


    }

    public void ChangeEmailAddress() {
        if (!((BaseActivity) getActivity()).isNetworkAvailable()) {
            DialogManager.showAlertSingleActionDialog(CarryParkApplication.getCurrentActivity(), CarryParkApplication.getCurrentContext().getResources().getString(R.string.OperfailedExtnd), "ok", new DialogManager.IUniActionDialogOnClickListener() {
                @Override
                public void onPositiveClick() {

                }
            });
        } else if (et_current_pin.getText().toString().isEmpty() || et_new_email.getText().toString().isEmpty() ||
                et_confirm_new_email.getText().toString().isEmpty()) {
            DialogManager.showAlertSingleActionDialog(CarryParkApplication.getCurrentActivity(), CarryParkApplication.getCurrentContext().getResources().getString(R.string.allEmailNeed), "ok", new DialogManager.IUniActionDialogOnClickListener() {
                @Override
                public void onPositiveClick() {

                }
            });
        } else if (et_current_pin.getText().toString().length() != 4) {
            DialogManager.showAlertSingleActionDialog(CarryParkApplication.getCurrentActivity(), CarryParkApplication.getCurrentContext().getResources().getString(R.string.pinInvalid), "ok", new DialogManager.IUniActionDialogOnClickListener() {
                @Override
                public void onPositiveClick() {

                }
            });
        } else if (!et_new_email.getText().toString().equals(et_confirm_new_email.getText().toString())) {
            DialogManager.showAlertSingleActionDialog(CarryParkApplication.getCurrentActivity(), CarryParkApplication.getCurrentContext().getResources().getString(R.string.Emailmismatch), "ok", new DialogManager.IUniActionDialogOnClickListener() {
                @Override
                public void onPositiveClick() {

                    et_confirm_new_email.requestFocus();


                }
            });
        } else {
            callChangeEmailAddress();
        }
    }

    public void callChangeEmailAddress() {
        ((BaseActivity) getActivity()).showBusyAnimation("");
        Map<String, Object> candidateMap = new HashMap<>();
        candidateMap.put("current_email", tv_current_email.getText().toString());
        candidateMap.put("new_email", et_new_email.getText().toString());
        candidateMap.put("pin", et_current_pin.getText().toString());
        if (SharedPreferenceUtility.isJapanease()) {
            candidateMap.put("lang_id", ConstantProject.forJapaneaseResponse);

        } else if (SharedPreferenceUtility.isEnglish()) {
            candidateMap.put("lang_id", "en");
        } else if (SharedPreferenceUtility.isKorean()) {
            candidateMap.put("lang_id", ConstantProject.forKoreanResponse);
        } else if (SharedPreferenceUtility.isChinease()) {
            candidateMap.put("lang_id", ConstantProject.forChineaseResponse);
        }

        String acess_token = GloablMethods.API_HEADER + AppController.getString(CarryParkApplication.getmContext(), "acess_token");
        apiService =
                ApiClient.getClient().create(ApiInterface.class);
        apiService.CallChangeEmailId(acess_token, candidateMap).enqueue(new Callback<ChangeEmailResponseModel>() {
            @Override
            public void onResponse(Call<ChangeEmailResponseModel> call, Response<ChangeEmailResponseModel> response) {
                ((BaseActivity) getActivity()).hideBusyAnimation();
                if (response!=null && response.body().isSuccess()) {
                    DialogManager.showUniActionDialog(CarryParkApplication.getCurrentActivity(), response.body().getMessage(), "ok", new DialogManager.IUniActionDialogOnClickListener() {
                        @Override
                        public void onPositiveClick() {
                            SharedPreferenceUtility.saveEmailId(tv_current_email.getText().toString());

                            EditAccountDetailsFragment ldf = new EditAccountDetailsFragment();
                            getFragmentManager().beginTransaction().add(R.id.fragment_container, ldf).commit();

                        }
                    });

                } else {
                    DialogManager.showAlertSingleActionDialog(CarryParkApplication.getCurrentActivity(), response.body().getMessage(), "ok", new DialogManager.IUniActionDialogOnClickListener() {
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
                    DialogManager.showAlertSingleActionDialog(CarryParkApplication.getCurrentActivity(), CarryParkApplication.getCurrentContext().getResources().getString(R.string.no_internet), "ok", new DialogManager.IUniActionDialogOnClickListener() {
                        @Override
                        public void onPositiveClick() {

                        }


                    });
                } else {
                    DialogManager.showAlertSingleActionDialog(CarryParkApplication.getCurrentActivity(), CarryParkApplication.getCurrentContext().getResources().getString(R.string.no_internet), "ok", new DialogManager.IUniActionDialogOnClickListener() {
                        @Override
                        public void onPositiveClick() {

                        }


                    });
                }
            }
        });
    }

}
