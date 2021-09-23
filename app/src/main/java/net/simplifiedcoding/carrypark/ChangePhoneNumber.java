package net.simplifiedcoding.carrypark;

import android.app.Activity;
import android.content.res.Configuration;
import android.graphics.Color;
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
import androidx.fragment.app.FragmentActivity;

import net.CarryParkApplication;
import net.others.BaseActivity;
import net.others.ConstantProject;
import net.others.DialogManager;
import net.others.SharedPreferenceUtility;
import net.picker.CountryPicker;
import net.picker.CountryPickerListener;
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

public class ChangePhoneNumber extends Fragment {
    String languageToLoad = "en";
    TextView tv_current_phone,tv_user,tv_country_phone,tv_dialCode;
    EditText et_current_pin, etMobileNumber, et_confirm_new_phone;
    ImageView iv_flag;

    Button button_save_new_phone;
    ImageView img_back_arrow;

    private FragmentActivity myContext;

    ApiInterface apiService;


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


        return inflater.inflate(R.layout.fragment_change_phone, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        apiService =
                ApiClient.getClient().create(ApiInterface.class);

        tv_current_phone = (TextView) view.findViewById(R.id.tv_current_phone);
        et_current_pin = (EditText) view.findViewById(R.id.et_current_pin);
        etMobileNumber = (EditText) view.findViewById(R.id.et_mobile_number);

        et_confirm_new_phone = (EditText) view.findViewById(R.id.et_confirm_new_phone);
        tv_user = (TextView) view.findViewById(R.id.tv_user);
        button_save_new_phone = (Button) view.findViewById(R.id.button_save_new_phone);
        tv_current_phone.setText(SharedPreferenceUtility.getPhoneNumber());
        img_back_arrow = (ImageView) view.findViewById(R.id.img_back_arrow);
        tv_country_phone = (TextView) view.findViewById(R.id.tv_country_phone);
        iv_flag = (ImageView) view.findViewById(R.id.iv_flag);
        tv_dialCode = (TextView) view.findViewById(R.id.tv_dialCode);


        if (CarryParkApplication.isIsEnglishLang())
        {
            tv_user.setText(getString(R.string.hi)+", "+CarryParkApplication.getUserName());
        }
        else if (CarryParkApplication.isIsJapaneaseLang())
        {
            //"ようこそ、Carryparkへ
            //○○さん"
            tv_user.setText("ようこそ "+CarryParkApplication.getUserName()+"さん");
        }
        else
        {
            tv_user.setText(getString(R.string.hi)+", "+CarryParkApplication.getUserName());
        }
        tv_country_phone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CountryPicker picker = CountryPicker.newInstance("Select Country",languageToLoad);  // dialog title
                picker.setListener(new CountryPickerListener() {
                    @Override
                    public void onSelectCountry(String name, String code, String dialCode, int flagDrawableResID) {
                        // Implement your code here
                        tv_country_phone.setText(code);
                        iv_flag.setImageResource(flagDrawableResID);
                        picker.dismiss();

                    }
                });

                picker.show(myContext.getSupportFragmentManager(), "COUNTRY_PICKER");
            }
        });
        iv_flag.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CountryPicker picker = CountryPicker.newInstance("Select Country",languageToLoad);  // dialog title
                picker.setListener(new CountryPickerListener() {
                    @Override
                    public void onSelectCountry(String name, String code, String dialCode, int flagDrawableResID) {
                        // Implement your code here
                        tv_country_phone.setText(code);
                        iv_flag.setImageResource(flagDrawableResID);
                        picker.dismiss();

                    }
                });

                picker.show(myContext.getSupportFragmentManager(), "COUNTRY_PICKER");
            }
        });
        tv_dialCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CountryPicker picker = CountryPicker.newInstance("Select Country",languageToLoad);  // dialog title
                picker.setListener(new CountryPickerListener() {
                    @Override
                    public void onSelectCountry(String name, String code, String dialCode, int flagDrawableResID) {
                        // Implement your code here
                        tv_country_phone.setText(code);
                        iv_flag.setImageResource(flagDrawableResID);
                        picker.dismiss();

                    }
                });

                picker.show(myContext.getSupportFragmentManager(), "COUNTRY_PICKER");
            }
        });

        img_back_arrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditAccountDetailsFragment ldf = new EditAccountDetailsFragment();
                getFragmentManager().beginTransaction().add(R.id.fragment_container, ldf).commit();

            }
        });

        tv_country_phone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CountryPicker picker = CountryPicker.newInstance("Select Country",languageToLoad);  // dialog title
                picker.setListener(new CountryPickerListener() {
                    @Override
                    public void onSelectCountry(String name, String code, String dialCode, int flagDrawableResID) {
                        // Implement your code here

                          tv_country_phone.setText(code);
                            iv_flag.setImageResource(flagDrawableResID);
                        tv_dialCode.setText(dialCode);

                            picker.dismiss();



                    }
                });

            }
        });
        button_save_new_phone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (et_current_pin.getText().toString().isEmpty() || etMobileNumber.getText().toString().isEmpty() || et_confirm_new_phone.getText().toString().isEmpty())
                {
                    DialogManager.showAlertSingleActionDialog(CarryParkApplication.getCurrentActivity(), CarryParkApplication.getCurrentContext().getResources().getString(R.string.allEmailNeed), "ok", new DialogManager.IUniActionDialogOnClickListener() {
                        @Override
                        public void onPositiveClick() {

                        }
                    });
                }

               else if (et_current_pin.getText().toString().isEmpty()) {
                    DialogManager.showAlertSingleActionDialog(CarryParkApplication.getCurrentActivity(), CarryParkApplication.getCurrentContext().getResources().getString(R.string.allEmailNeed), "ok", new DialogManager.IUniActionDialogOnClickListener() {
                        @Override
                        public void onPositiveClick() {

                        }
                    });
                } else if (etMobileNumber.getText().toString().isEmpty() || et_confirm_new_phone.getText().toString().isEmpty()) {
                    DialogManager.showAlertSingleActionDialog(CarryParkApplication.getCurrentActivity(), CarryParkApplication.getCurrentContext().getResources().getString(R.string.emptyMobile), "ok", new DialogManager.IUniActionDialogOnClickListener() {
                        @Override
                        public void onPositiveClick() {

                        }
                    });
                } else if (!(etMobileNumber.getText().toString().length() >= 4 && etMobileNumber.getText().toString().length() <= 14)) {
                    DialogManager.showAlertSingleActionDialog(CarryParkApplication.getCurrentActivity(), CarryParkApplication.getCurrentContext().getResources().getString(R.string.emptyMobile), "ok", new DialogManager.IUniActionDialogOnClickListener() {
                        @Override
                        public void onPositiveClick() {

                        }
                    });
                } else if (et_current_pin.getText().toString().isEmpty() || et_current_pin.getText().toString().length() != 4) {
                    DialogManager.showAlertSingleActionDialog(CarryParkApplication.getCurrentActivity(), CarryParkApplication.getCurrentContext().getResources().getString(R.string.pinInvalid), "ok", new DialogManager.IUniActionDialogOnClickListener() {
                        @Override
                        public void onPositiveClick() {

                        }
                    });
                } else if (!etMobileNumber.getText().toString().equalsIgnoreCase(et_confirm_new_phone.getText().toString())) {
                    DialogManager.showAlertSingleActionDialog(CarryParkApplication.getCurrentActivity(), CarryParkApplication.getCurrentContext().getResources().getString(R.string.phoneNumMismatch), "ok", new DialogManager.IUniActionDialogOnClickListener() {
                        @Override
                        public void onPositiveClick() {

                        }
                    });
                } else {

                    callChangeMobileNumber(et_confirm_new_phone.getText().toString(), tv_dialCode.getText().toString(), et_current_pin.getText().toString());
                }

            }
        });


        KeyboardVisibilityEvent.setEventListener(
                getActivity(),
                new KeyboardVisibilityEventListener() {
                    @Override
                    public void onVisibilityChanged(boolean isOpen) {
                        // some code depending on keyboard visiblity status
                        if (isOpen) {
                            button_save_new_phone.setVisibility(View.GONE);
                        } else {
                            button_save_new_phone.setVisibility(View.VISIBLE);
                        }
                    }
                });


    }


    public void callChangeMobileNumber(String mobile, String code, String pin) {
        ((BaseActivity) getActivity()).showBusyAnimation("");
        Map<String, Object> candidateMap = new HashMap<>();
        candidateMap.put("mobile", mobile);
        candidateMap.put("phone_code", code);
        candidateMap.put("pin", pin);
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
        apiService.CallChangeMobileNumber(acess_token, candidateMap).enqueue(new Callback<ChangeEmailResponseModel>() {
            @Override
            public void onResponse(Call<ChangeEmailResponseModel> call, Response<ChangeEmailResponseModel> response) {
                ((BaseActivity) getActivity()).hideBusyAnimation();
                if (response!=null && response.body().isSuccess()) {
                    DialogManager.showUniActionDialog(CarryParkApplication.getCurrentActivity(), response.body().getMessage(), "ok", new DialogManager.IUniActionDialogOnClickListener() {
                        @Override
                        public void onPositiveClick() {
                            SharedPreferenceUtility.savePhoneNumber("+"+tv_dialCode.getText().toString()+" "+ et_confirm_new_phone.getText().toString());

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
    @Override
    public void onAttach(Activity activity) {
        myContext=(FragmentActivity) activity;
        super.onAttach(activity);
    }

}
