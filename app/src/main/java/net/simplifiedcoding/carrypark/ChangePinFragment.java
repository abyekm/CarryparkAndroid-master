package net.simplifiedcoding.carrypark;


import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

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
import net.rest.model.ChangePinResponse;
import net.ui.BottomNavigation;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEvent;
import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEventListener;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.content.Context.MODE_PRIVATE;


/**
 * A simple {@link Fragment} subclass.
 */
public class ChangePinFragment extends Fragment {



    ApiInterface apiService;
    private Button buttonSaveNewPin;
    ImageView ivBackArrow;
    TextView tvUser;
    private EditText etNewPin,etConfirmPin,etCurrentPin;
    private SharedPreferences sharedPreferences;
    String languageToLoad="en";
    public ChangePinFragment() {
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


        return inflater.inflate(R.layout.fragment_change_pin, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        sharedPreferences = this.getActivity().getSharedPreferences("carry_park",MODE_PRIVATE);
        apiService =
                ApiClient.getClient().create(ApiInterface.class);
        buttonSaveNewPin = (Button)view.findViewById(R.id.button_save_new_pin);
        etNewPin = (EditText)view.findViewById(R.id.et_new_pin);
        etConfirmPin = (EditText)view.findViewById(R.id.et_confirm_pin);
        etCurrentPin = (EditText)view.findViewById(R.id.et_current_pin);
        tvUser = (TextView) view.findViewById(R.id.tv_user);
        ivBackArrow = (ImageView)view.findViewById(R.id.img_back_arrow);
        String userName = sharedPreferences.getString("user_name",null);
        //tvUser.setText("Hi,"+userName);
        if (CarryParkApplication.isIsEnglishLang())
        {
            tvUser.setText(getString(R.string.hi)+", "+userName);
        }
        else if (CarryParkApplication.isIsJapaneaseLang())
        {
            //"ようこそ、Carryparkへ
            //○○さん"
            tvUser.setText("ようこそ "+userName+"さん");
        }
        else
        {
            tvUser.setText(getString(R.string.hi)+", "+userName);
        }

        buttonSaveNewPin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                attemptChangePIN();
            }
        });

        ivBackArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                replaceFragment();
            }
        });
        KeyboardVisibilityEvent.setEventListener(
                getActivity(),
                new KeyboardVisibilityEventListener() {
                    @Override
                    public void onVisibilityChanged(boolean isOpen) {
                        // some code depending on keyboard visiblity status
                        if (isOpen)
                        {
                            buttonSaveNewPin.setVisibility(View.GONE);
                        }
                        else {
                            buttonSaveNewPin.setVisibility(View.VISIBLE);
                        }
                    }
                });

        CarryParkApplication.setIsAliPay(false);
    }

    private void replaceFragment() {

        EditAccountDetailsFragment ldf = new EditAccountDetailsFragment();
        getFragmentManager().beginTransaction().add(R.id.fragment_container, ldf).commit();

       /* MoreFragment ldf = new MoreFragment();
        getFragmentManager().beginTransaction().add(R.id.fragment_container, ldf).commit();*/
    }

    private void attemptChangePIN() {

        // Reset errors.
        etCurrentPin.setError(null);
        etNewPin.setError(null);
        etConfirmPin.setError(null);

        // Store values at the time of the login attempt.
        final String currentPIN = etCurrentPin.getText().toString();
        final String newPIN = etNewPin.getText().toString();
        final String confirmPIN = etConfirmPin.getText().toString();

        boolean cancel = false;
        View focusView = null;
        if(TextUtils.isEmpty(currentPIN)|| TextUtils.isEmpty(newPIN)|| TextUtils.isEmpty(confirmPIN))
        {
            DialogManager.showAlertSingleActionDialog(CarryParkApplication.getCurrentActivity(), getString(R.string.all_pin_required), "ok", new DialogManager.IUniActionDialogOnClickListener() {
                @Override
                public void onPositiveClick() {

                }
            });
            cancel = true;
        }

        else if (TextUtils.isEmpty(currentPIN)) {
            DialogManager.showAlertSingleActionDialog(CarryParkApplication.getCurrentActivity(), getString(R.string.pinLogValid), "ok", new DialogManager.IUniActionDialogOnClickListener() {
                @Override
                public void onPositiveClick() {

                }
            });
            focusView = etCurrentPin;
            cancel = true;
        }else if(!(currentPIN.length()==4)){
            DialogManager.showAlertSingleActionDialog(CarryParkApplication.getCurrentActivity(), getString(R.string.pinLogValid), "ok", new DialogManager.IUniActionDialogOnClickListener() {
                @Override
                public void onPositiveClick() {

                }
            });
            focusView = etCurrentPin;
            cancel = true;
        }
        else if (TextUtils.isEmpty(newPIN)) {
            DialogManager.showAlertSingleActionDialog(CarryParkApplication.getCurrentActivity(), getString(R.string.pinLogValid), "ok", new DialogManager.IUniActionDialogOnClickListener() {
                @Override
                public void onPositiveClick() {

                }
            });
            focusView = etNewPin;
            cancel = true;
        }else if(!(newPIN.length()==4)){
            DialogManager.showAlertSingleActionDialog(CarryParkApplication.getCurrentActivity(), getString(R.string.pinLogValid), "ok", new DialogManager.IUniActionDialogOnClickListener() {
                @Override
                public void onPositiveClick() {

                }
            });
            focusView = etNewPin;
            cancel = true;
        }

        else if (TextUtils.isEmpty(confirmPIN)) {
            DialogManager.showAlertSingleActionDialog(CarryParkApplication.getCurrentActivity(), getString(R.string.pinLogValid), "ok", new DialogManager.IUniActionDialogOnClickListener() {
                @Override
                public void onPositiveClick() {

                }
            });
            focusView = etConfirmPin;
            cancel = true;
        }else if(!(confirmPIN.length()==4)){
            DialogManager.showAlertSingleActionDialog(CarryParkApplication.getCurrentActivity(), getString(R.string.pinLogValid), "ok", new DialogManager.IUniActionDialogOnClickListener() {
                @Override
                public void onPositiveClick() {

                }
            });
            focusView = etConfirmPin;
            cancel = true;
        }


        if (cancel) {
           // focusView.requestFocus();
        } else {
            if(validate()){
                APIChangePIN(currentPIN,confirmPIN);
                // startActivity(new Intent(RegistrationForm.this, Home.class));
            }

        }
    }

    private boolean validate() {
        boolean temp=true;
        String pin=etNewPin.getText().toString();
        String cpin=etConfirmPin.getText().toString();
        if(!pin.equals(cpin)){
            DialogManager.showAlertSingleActionDialog(CarryParkApplication.getCurrentActivity(),CarryParkApplication.getCurrentContext().getResources().getString(R.string.pinmismatch), "ok", new DialogManager.IUniActionDialogOnClickListener() {
                @Override
                public void onPositiveClick() {

                }


            });
            temp=false;
        }
        return temp;
    }
    private void APIChangePIN(String newPIN, String confirmPIN) {
        ((BaseActivity) getActivity()).showBusyAnimation("");

        Map<String, Object> candidateMap = new HashMap<>();

//        candidateMap.put("country_id", mLoggedInUser.country_id);
//        candidateMap.put("province_id", mLoggedInUser.province_id);
//        candidateMap.put("city_id", mLoggedInUser.cityId);
//        candidateMap.put("address", mLoggedInUser.address);
//        candidateMap.put("zip", mLoggedInUser.zip);
        if (SharedPreferenceUtility.isJapanease()) {
            if (CarryParkApplication.isIsJapaneaseLang())
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
        candidateMap.put("current_pin",newPIN);
        candidateMap.put("new_pin",confirmPIN);
        String acess_token = GloablMethods.API_HEADER+ AppController.getString(getActivity(), "acess_token");
        apiService.changePIN(acess_token,candidateMap).enqueue(new Callback<ChangePinResponse>() {
            @Override
            public void onResponse(Call<ChangePinResponse> call, Response<ChangePinResponse> response) {


                if(response.code() == 200){
                    ((BaseActivity) getActivity()).hideBusyAnimation();

                    Boolean status = response.body().getSuccess();
                    if(status == true){

                        DialogManager.showUniActionDialog(CarryParkApplication.getCurrentActivity(), getResources().getString(R.string.pinChangeSuccess), "ok", new DialogManager.IUniActionDialogOnClickListener() {
                            @Override
                            public void onPositiveClick() {
                                Intent intent = new Intent(CarryParkApplication.getCurrentContext(), BottomNavigation.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                intent.putExtra("page", ConstantProject.MoreFragment);
                                startActivity(intent);
                            }
                        });


                    }else if(status == false){
                        String msg = response.body().getMessage();
                        DialogManager.showAlertSingleActionDialog(CarryParkApplication.getCurrentActivity(),msg, "ok", new DialogManager.IUniActionDialogOnClickListener() {
                            @Override
                            public void onPositiveClick() {

                            }


                        });                       }
                }/*else if(response.code() == 200 && response.body().getData() == null) {
                    progressDialog.dismiss();
                    String msg = response.body().getMessage();
                }*/ else if(response.code() == 404){
                    ((BaseActivity) getActivity()).hideBusyAnimation();

                    DialogManager.showAlertSingleActionDialog(CarryParkApplication.getCurrentActivity(),CarryParkApplication.getCurrentContext().getResources().getString(R.string.validation_error), "ok", new DialogManager.IUniActionDialogOnClickListener() {
                        @Override
                        public void onPositiveClick() {

                        }


                    });
                }
            }

            @Override
            public void onFailure(Call<ChangePinResponse> call, Throwable t) {
                ((BaseActivity) getActivity()).hideBusyAnimation();
                if(!Utils.isNetworkConnectionAvailable(CarryParkApplication.getCurrentContext())){
                    DialogManager.showAlertSingleActionDialog(CarryParkApplication.getCurrentActivity(), CarryParkApplication.getCurrentContext().getResources().getString(R.string.please_try_again), "ok", new DialogManager.IUniActionDialogOnClickListener() {
                        @Override
                        public void onPositiveClick() {

                        }
                    });

                }else {
                    DialogManager.showAlertSingleActionDialog(CarryParkApplication.getCurrentActivity(),t.getMessage(), "ok", new DialogManager.IUniActionDialogOnClickListener() {
                        @Override
                        public void onPositiveClick() {

                        }


                    });                   }
            }
        });
    }

}
