package net.ui;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Bundle;
import android.provider.Settings;
import android.text.TextUtils;
import android.text.format.DateFormat;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.webkit.WebView;
import android.widget.*;
import androidx.constraintlayout.widget.ConstraintLayout;
import com.bruce.pickerview.popwindow.DatePickerPopWin;

import net.CarryParkApplication;
import net.others.BaseActivity;
import net.others.ConstantProject;
import net.others.DialogManager;
import net.others.SharedPreferenceUtility;
import net.picker.Country;
import net.picker.CountryPicker;
import net.picker.CountryPickerListener;
import net.rest.ApiClient;
import net.rest.ApiInterface;
import net.rest.Utils;
import net.rest.model.UserRegisterResponse;
import net.simplifiedcoding.carrypark.R;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import java.text.SimpleDateFormat;
import java.util.*;

public class RegistrationActivity extends BaseActivity implements View.OnClickListener {
    int mYear, mMonth, mDay;
    final int DATE_DIALOG = 1;

    Button buttonSignUp;
    ImageView imgBackArrow;
    EditText etFullName, etMobileNumber, etEmailAddress, etDob;
    final Calendar myCalendar = Calendar.getInstance();
    ApiInterface apiService;
    String deviceMacId;
    TextView tvTermsOfService, tvPrivacypolicy;
    String languageToLoad = "en";
    Calendar cal, cal1;
    private SharedPreferences sharedPreferences;
    LinearLayout ll_bag_layout, linearLayout2;
    private boolean isPrivacyPolicyOpened, isTermsofServiceOpened;
    ConstraintLayout con_rootView;
    private String blockCharacterSet = "~#^|$%&*!+-.";
    String dobForApi = "";
    TextView tv_nationality;
    String nationality="";
    TextView tv_country_phone;
    boolean isCountryPhone=false;
    ImageView iv_flag;
    LinearLayout ll_country_code;
    TextView tv_dialCode;
    private String code_country="",code_dial="",countryName="";
    public static Country[] COUNTRIESLang = new Country[0];
    //
    public static Country[] COUNTRIESLangENG = new Country[0];
    private String CountryJapan;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
        getBaseContext().getResources().updateConfiguration(config,
                getBaseContext().getResources().getDisplayMetrics());
        setContentView(R.layout.activity_user_registration);
        CarryParkApplication.setCurrentContext(this);
        apiService =
                ApiClient.getClient().create(ApiInterface.class);
        buttonSignUp = (Button) findViewById(R.id.button_proceedto_pin);
        imgBackArrow = (ImageView) findViewById(R.id.img_back_arrow);
        etFullName = (EditText) findViewById(R.id.et_full_name);
        etMobileNumber = (EditText) findViewById(R.id.et_mobile_number);
        tv_country_phone = (TextView) findViewById(R.id.tv_country_phone);
        ll_country_code = (LinearLayout) findViewById(R.id.ll_country_code);
        iv_flag = (ImageView) findViewById(R.id.iv_flag);
        etEmailAddress = (EditText) findViewById(R.id.et_email_address);
        etDob = (EditText) findViewById(R.id.et_dob);
        tvTermsOfService = (TextView) findViewById(R.id.tv_terms_of_service);
        tvPrivacypolicy = (TextView) findViewById(R.id.tv_privacypolicy);
        ll_bag_layout = (LinearLayout) findViewById(R.id.ll_bag_layout);
        linearLayout2 = (LinearLayout) findViewById(R.id.linearLayout2);
        con_rootView = (ConstraintLayout) findViewById(R.id.con_rootView);
        tv_nationality =(TextView) findViewById(R.id.tv_nationality);
        tv_dialCode = (TextView) findViewById(R.id.tv_dialCode);

        code_country ="JP";
        code_dial ="+81";
        nationality="japan";
        CountryJapan="日本";

        COUNTRIESLang = new Country[]{ new Country("AE", "アラブ首長国連邦", "+971",  R.drawable.flag_ae), new Country("AF", "アフガニスタン", "+93",  R.drawable.flag_af),  new Country("AI", "アンギラ", "+1",  R.drawable.flag_ai), new Country("AL", "アルバニア", "+355",  R.drawable.flag_al), new Country("AM", "アルメニア", "+374",  R.drawable.flag_am), new Country("AO", "アンゴラ", "+244",  R.drawable.flag_ao), new Country("AQ", "南極大陸", "+672",  R.drawable.flag_aq), new Country("AR", "アルゼンチン", "+54",  R.drawable.flag_ar) , new Country("AT", "オーストリア", "+43",  R.drawable.flag_at), new Country("AU", "オーストラリア", "+61",  R.drawable.flag_au),   new Country("BD", "バングラデシュ", "+880",  R.drawable.flag_bd), new Country("BE", "ベルギー", "+32",  R.drawable.flag_be),new Country("BG", "ブルガリア", "+359",  R.drawable.flag_bg), new Country("BH", "バーレーン", "+973",  R.drawable.flag_bh),   new Country("BM", "バミューダ", "+1",  R.drawable.flag_bm),new Country("BO", "ボリビア", "+591",  R.drawable.flag_bo), new Country("BR", "ブラジル", "+55",  R.drawable.flag_br), new Country("BZ", "ベリーズ", "+501",  R.drawable.flag_bz), new Country("CA", "カナダ", "+1",  R.drawable.flag_ca),   new Country("CF", "中央アフリカ共和国", "+236",   R.drawable.flag_cf), new Country("CG", "コンゴ", "+242",   R.drawable.flag_cg), new Country("CH", "スイス", "+41",   R.drawable.flag_ch),  new Country("CL", "チリ", "+56",   R.drawable.flag_cl), new Country("CM", "カメルーン", "+237",   R.drawable.flag_cm), new Country("CN", "中国", "+86",   R.drawable.flag_cn), new Country("CO", "コロンビア", "+57",   R.drawable.flag_co), new Country("CR", "コスタリカ", "+506",   R.drawable.flag_cr), new Country("CU", "キューバ", "+53",   R.drawable.flag_cu),new Country("CZ", "チェコ共和国", "+420",  R.drawable.flag_cz), new Country("DE", "ドイツ", "+49",  R.drawable.flag_de), new Country("DK", "デンマーク", "+45",  R.drawable.flag_dk), new Country("DM", "ドミニカ", "+1",  R.drawable.flag_dm), new Country("DZ", "アルジェリア", "+213",  R.drawable.flag_dz), new Country("EC", "エクアドル", "+593",  R.drawable.flag_ec), new Country("EG", "エジプト", "+20",  R.drawable.flag_eg),new Country("ER", "エリトリア", "+291",  R.drawable.flag_er), new Country("ES", "スペイン", "+34",  R.drawable.flag_es), new Country("ET", "エチオピア", "+251",  R.drawable.flag_et), new Country("FI", "フィンランド", "+358",  R.drawable.flag_fi), new Country("FJ", "フィジー", "+679",  R.drawable.flag_fj),  new Country("FR", "フランス", "+33",  R.drawable.flag_fr), new Country("GB", "イギリス", "+44",  R.drawable.flag_gb),  new Country("GL", "グリーンランド", "+299",  R.drawable.flag_gl),  new Country("GR", "ギリシャ", "+30",  R.drawable.flag_gr), new Country("HK", "香港", "+852",  R.drawable.flag_hk),new Country("HU", "ハンガリー", "+36",  R.drawable.flag_hu), new Country("ID", "インドネシア", "+62",  R.drawable.flag_id), new Country("IE", "アイルランド", "+353",  R.drawable.flag_ie), new Country("IL", "イスラエル", "+972",  R.drawable.flag_il), new Country("IN", "インド", "+91",  R.drawable.flag_in), new Country("IQ", "イラク", "+964",  R.drawable.flag_iq),  new Country("IS", "アイスランド", "+354",  R.drawable.flag_is), new Country("IT", "イタリア", "+39",  R.drawable.flag_it), new Country("JE", "ジャージー", "+44",  R.drawable.flag_je), new Country("JM", "ジャマイカ", "+1",  R.drawable.flag_jm), new Country("JO", "ヨルダン", "+962",  R.drawable.flag_jo), new Country("JP", "日本", "+81",  R.drawable.flag_jp),  new Country("KP", "北朝鮮", "+850",  R.drawable.flag_kp), new Country("KR", "韓国", "+82",  R.drawable.flag_kr), new Country("KW", "クウェート", "+965",  R.drawable.flag_kw), new Country("LB", "レバノン", "+961",  R.drawable.flag_lb), new Country("LK", "スリランカ", "+94",  R.drawable.flag_lk), new Country("ML", "マリ", "+223",  R.drawable.flag_ml), new Country("MM", "ミャンマー", "+95",  R.drawable.flag_mm), new Country("MN", "モンゴル", "+976",  R.drawable.flag_mn), new Country("MT", "マルタ", "+356",  R.drawable.flag_mt),  new Country("MX", "メキシコ", "+52",  R.drawable.flag_mx), new Country("MY", "マレーシア", "+60",  R.drawable.flag_my),     new Country("NL", "オランダ", "+31",  R.drawable.flag_nl), new Country("NO", "ノルウェー", "+47",  R.drawable.flag_no), new Country("NP", "ネパール", "+977",  R.drawable.flag_np),  new Country("NZ", "ニュージーランド", "+64",  R.drawable.flag_nz), new Country("OM", "オマーン", "+968",  R.drawable.flag_om),  new Country("PE", "ペルー", "+51",  R.drawable.flag_pe),  new Country("PH", "フィリピン", "+63",  R.drawable.flag_ph), new Country("PK", "パキスタン", "+92",  R.drawable.flag_pk), new Country("PL", "ポーランド", "+48",  R.drawable.flag_pl), new Country("QA", "カタール", "+974",  R.drawable.flag_qa),  new Country("RS", "セルビア", "+381",  R.drawable.flag_rs), new Country("RU", "ロシア", "+7",  R.drawable.flag_ru),  new Country("SA", "サウジアラビア", "+966",  R.drawable.flag_sa),  new Country("SD", "スーダン", "+249",  R.drawable.flag_sd), new Country("SE", "スウェーデン", "+46",  R.drawable.flag_se), new Country("SG", "シンガポール", "+65",  R.drawable.flag_sg),  new Country("SO", "ソマリア", "+252",  R.drawable.flag_so),     new Country("SZ", "スワジランド", "+268",  R.drawable.flag_sz),  new Country("TH", "タイ", "+66",  R.drawable.flag_th),   new Country("TR", "トルコ", "+90",  R.drawable.flag_tr),  new Country("TW", "台湾", "+886",  R.drawable.flag_tw),  new Country("UA", "ウクライナ", "+380",  R.drawable.flag_ua),  new Country("US", "アメリカ", "+1",  R.drawable.flag_us),  new Country("YE", "イエメン", "+967",  R.drawable.flag_ye),  new Country("ZA", "南アフリカ", "+27",  R.drawable.flag_za),  new Country("ZW", "ジンバブエ", "+263",  R.drawable.flag_zw)};
        COUNTRIESLangENG = new Country[]{ new Country("AE", "United Arab Emirates", "+971",  R.drawable.flag_ae), new Country("AF", "Afghanistan", "+93",  R.drawable.flag_af), new Country("AI", "Anguilla", "+1",  R.drawable.flag_ai), new Country("AL", "Albania", "+355",  R.drawable.flag_al), new Country("AM", "Armenia", "+374",  R.drawable.flag_am), new Country("AO", "Angola", "+244",  R.drawable.flag_ao), new Country("AQ", "Antarctica", "+672",  R.drawable.flag_aq), new Country("AR", "Argentina", "+54",  R.drawable.flag_ar) , new Country("AT", "Austria", "+43",  R.drawable.flag_at), new Country("AU", "Australia", "+61",  R.drawable.flag_au), new Country("BD", "Bangladesh", "+880",  R.drawable.flag_bd), new Country("BE", "Belgium", "+32",  R.drawable.flag_be),new Country("BG", "Bulgaria", "+359",  R.drawable.flag_bg), new Country("BH", "Bahrain", "+973",  R.drawable.flag_bh),   new Country("BM", "Bermuda", "+1",  R.drawable.flag_bm),  new Country("BO", "Bolivia, Plurinational State of", "+591",  R.drawable.flag_bo), new Country("BR", "Brazil", "+55",  R.drawable.flag_br), new Country("BZ", "Belize", "+501",  R.drawable.flag_bz), new Country("CA", "Canada", "+1",  R.drawable.flag_ca),new Country("CF", "Central African Republic", "+236",  R.drawable.flag_cf), new Country("CG", "Congo", "+242",   R.drawable.flag_cg), new Country("CH", "Switzerland", "+41",   R.drawable.flag_ch),  new Country("CL", "Chile", "+56",   R.drawable.flag_cl), new Country("CM", "Cameroon", "+237",   R.drawable.flag_cm), new Country("CN", "China", "+86",   R.drawable.flag_cn), new Country("CO", "Colombia", "+57",   R.drawable.flag_co), new Country("CR", "Costa Rica", "+506",   R.drawable.flag_cr), new Country("CU", "Cuba", "+53",   R.drawable.flag_cu),  new Country("CZ", "Czech Republic", "+420",   R.drawable.flag_cz), new Country("DE", "Germany", "+49",   R.drawable.flag_de),new Country("DK", "Denmark", "+45",   R.drawable.flag_dk), new Country("DM", "Dominica", "+1",   R.drawable.flag_dm), new Country("DZ", "Algeria", "+213",   R.drawable.flag_dz), new Country("EC", "Ecuador", "+593",   R.drawable.flag_ec), new Country("EE", "Estonia", "+372",   R.drawable.flag_ee), new Country("EG", "Egypt", "+20",   R.drawable.flag_eg), new Country("ER", "Eritrea", "+291",  R.drawable.flag_er), new Country("ES", "Spain", "+34",  R.drawable.flag_es), new Country("ET", "Ethiopia", "+251",  R.drawable.flag_et), new Country("FI", "Finland", "+358",  R.drawable.flag_fi), new Country("FJ", "Fiji", "+679",  R.drawable.flag_fj),  new Country("FR", "France", "+33",  R.drawable.flag_fr),new Country("GB", "United Kingdom", "+44",  R.drawable.flag_gb),   new Country("GL", "Greenland", "+299",  R.drawable.flag_gl),new Country("GR", "Greece", "+30",  R.drawable.flag_gr),  new Country("HK", "Hong Kong", "+852",  R.drawable.flag_hk),  new Country("HU", "Hungary", "+36",  R.drawable.flag_hu), new Country("ID", "Indonesia", "+62",  R.drawable.flag_id), new Country("IE", "Ireland", "+353",  R.drawable.flag_ie), new Country("IL", "Israel", "+972",  R.drawable.flag_il), new Country("IN", "India", "+91",  R.drawable.flag_in), new Country("IQ", "Iraq", "+964",  R.drawable.flag_iq), new Country("IS", "Iceland", "+354",  R.drawable.flag_is), new Country("IT", "Italy", "+39",  R.drawable.flag_it), new Country("JE", "Jersey", "+44",  R.drawable.flag_je), new Country("JM", "Jamaica", "+1",  R.drawable.flag_jm), new Country("JO", "Jordan", "+962",  R.drawable.flag_jo), new Country("JP", "Japan", "+81",  R.drawable.flag_jp),   new Country("KP", "North Korea", "+850",  R.drawable.flag_kp), new Country("KR", "South Korea", "+82",  R.drawable.flag_kr), new Country("KW", "Kuwait", "+965",  R.drawable.flag_kw),new Country("LB", "Lebanon", "+961",  R.drawable.flag_lb),   new Country("LK", "Sri Lanka", "+94",  R.drawable.flag_lk),  new Country("ML", "Mali", "+223",  R.drawable.flag_ml), new Country("MM", "Myanmar", "+95",  R.drawable.flag_mm), new Country("MN", "Mongolia", "+976",  R.drawable.flag_mn), new Country("MT", "Malta", "+356",  R.drawable.flag_mt),new Country("MX", "Mexico", "+52",  R.drawable.flag_mx), new Country("MY", "Malaysia", "+60",  R.drawable.flag_my),   new Country("NL", "Netherlands", "+31",  R.drawable.flag_nl), new Country("NO", "Norway", "+47",  R.drawable.flag_no), new Country("NP", "Nepal", "+977",  R.drawable.flag_np),  new Country("NZ", "New Zealand", "+64",  R.drawable.flag_nz), new Country("OM", "Oman", "+968",  R.drawable.flag_om),  new Country("PE", "Peru", "+51",  R.drawable.flag_pe),   new Country("PH", "Philippines", "+63",  R.drawable.flag_ph), new Country("PK", "Pakistan", "+92",  R.drawable.flag_pk), new Country("PL", "Poland", "+48",  R.drawable.flag_pl), new Country("QA", "Qatar", "+974",  R.drawable.flag_qa), new Country("RS", "Serbia", "+381",  R.drawable.flag_rs), new Country("RU", "Russia", "+7",  R.drawable.flag_ru),  new Country("SA", "Saudi Arabia", "+966",  R.drawable.flag_sa),   new Country("SD", "Sudan", "+249",  R.drawable.flag_sd), new Country("SE", "Sweden", "+46",  R.drawable.flag_se), new Country("SG", "Singapore", "+65",  R.drawable.flag_sg),   new Country("SO", "Somalia", "+252",  R.drawable.flag_so),  new Country("SZ", "Swaziland", "+268",  R.drawable.flag_sz),  new Country("TH", "Thailand", "+66",  R.drawable.flag_th), new Country("TR", "Turkey", "+90",  R.drawable.flag_tr),  new Country("TW", "Taiwan", "+886",  R.drawable.flag_tw),new Country("UA", "Ukraine", "+380",  R.drawable.flag_ua), new Country("US", "United States", "+1",  R.drawable.flag_us), new Country("YE", "Yemen", "+967",  R.drawable.flag_ye),  new Country("ZA", "South Africa", "+27",  R.drawable.flag_za),new Country("ZW", "Zimbabwe", "+263",  R.drawable.flag_zw)};

        tv_nationality.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isCountryPhone =false;
                CountryPicker picker = CountryPicker.newInstance("Select Country",languageToLoad);  // dialog title
                picker.setListener(new CountryPickerListener() {
                    @Override
                    public void onSelectCountry(String name, String code, String dialCode, int flagDrawableResID) {
                        // Implement your code here

                        if ( isCountryPhone )
                        {
                            tv_country_phone.setText(code);
                            iv_flag.setImageResource(flagDrawableResID);

                            tv_nationality.setText(name);
                            tv_nationality.setTextColor(Color.parseColor("#000000"));
                            //nationality =name;
                            getEnglish(dialCode);

                            code_country =code;
                            code_dial =dialCode;
                            tv_dialCode.setText(dialCode);


                            picker.dismiss();
                        }
                        else {
                            tv_nationality.setText(name);
                            tv_nationality.setTextColor(Color.parseColor("#000000"));
                            //nationality =name;
                            getEnglish(dialCode);

                            countryName =name;
                            getJapanease(dialCode);

                            picker.dismiss();
                        }

                    }
                });

                picker.show(getSupportFragmentManager(), "COUNTRY_PICKER");
            }
        });
        ll_country_code.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isCountryPhone =true;
                CountryPicker picker = CountryPicker.newInstance("Select Country",languageToLoad);  // dialog title
                picker.setListener(new CountryPickerListener() {
                    @Override
                    public void onSelectCountry(String name, String code, String dialCode, int flagDrawableResID) {
                        // Implement your code here

                        if ( isCountryPhone )
                        {
                            tv_country_phone.setText(code);
                            iv_flag.setImageResource(flagDrawableResID);

                            tv_nationality.setText(name);
                            tv_nationality.setTextColor(Color.parseColor("#000000"));
                           // nationality =name;
                            getEnglish(dialCode);

                            code_country =code;
                            code_dial =dialCode;
                            tv_dialCode.setText(dialCode);

                            getJapanease(dialCode);

                            picker.dismiss();
                        }
                        else {
                            tv_nationality.setText(name);
                            tv_nationality.setTextColor(Color.parseColor("#000000"));
                            //nationality =name;
                            getEnglish(dialCode);

                            countryName =name;

                            picker.dismiss();
                        }

                    }
                });

                picker.show(getSupportFragmentManager(), "COUNTRY_PICKER");
            }
        });
        tv_dialCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isCountryPhone =true;
                CountryPicker picker = CountryPicker.newInstance("Select Country",languageToLoad);  // dialog title
                picker.setListener(new CountryPickerListener() {
                    @Override
                    public void onSelectCountry(String name, String code, String dialCode, int flagDrawableResID) {
                        // Implement your code here

                        if ( isCountryPhone )
                        {
                            tv_country_phone.setText(code);
                            iv_flag.setImageResource(flagDrawableResID);

                            tv_nationality.setText(name);
                            tv_nationality.setTextColor(Color.parseColor("#000000"));
                           // nationality =name;
                            getEnglish(dialCode);

                            code_country =code;
                            code_dial =dialCode;
                            tv_dialCode.setText(dialCode);



                            picker.dismiss();
                        }
                        else {
                            tv_nationality.setText(name);
                            tv_nationality.setTextColor(Color.parseColor("#000000"));
                            getEnglish(dialCode);
                           // nationality =name;
                            countryName =name;

                            picker.dismiss();
                        }

                    }
                });

                picker.show(getSupportFragmentManager(), "COUNTRY_PICKER");
            }
        });
        if (SharedPreferenceUtility.isChinease())
        {
            tv_nationality.setText("日本");
        }
        else if (SharedPreferenceUtility.isEnglish())
        {
            tv_nationality.setText("Japan");
        }
        else if (SharedPreferenceUtility.isKorean())
        {
            tv_nationality.setText("일본");
        }
        else if (SharedPreferenceUtility.isJapanease())
        {
            tv_nationality.setText("日本");
        }




        buttonSignUp.setOnClickListener(this);
        imgBackArrow.setOnClickListener(this);
        etDob.setOnClickListener(this);
        deviceMacId = Settings.Secure.getString(RegistrationActivity.this.getContentResolver(), Settings.Secure.ANDROID_ID);
        final Calendar ca = Calendar.getInstance();
        mYear = ca.get(Calendar.YEAR);
        mMonth = ca.get(Calendar.MONTH);
        mDay = ca.get(Calendar.DAY_OF_MONTH);

        tvPrivacypolicy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PrivacyPolicyPopup();
            }
        });
        tvTermsOfService.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                TermsofServicePopup();
            }
        });

        con_rootView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                int heightDiff = con_rootView.getRootView().getHeight() - con_rootView.getHeight();

                if (heightDiff > 400) {
                    linearLayout2.setVisibility(View.GONE);

                } else {
                    linearLayout2.setVisibility(View.VISIBLE);

                }
            }
        });
        hideKeyboard(CarryParkApplication.getCurrentActivity());


    }


    private void getJapanease( String code_dial)
    {
        for (int i=0;i<COUNTRIESLang.length;i++)
        {
            if (COUNTRIESLang[i].getDialCode().equals(code_dial))
            {
                CountryJapan = COUNTRIESLang[i].getName();
               // Log.e("CountryName",CountryJapan);
            }
        }
    }
    private void getEnglish( String code_dial)
    {
        for (int i=0;i<COUNTRIESLangENG.length;i++)
        {
            if (COUNTRIESLangENG[i].getDialCode().equals(code_dial))
            {
                nationality = COUNTRIESLangENG[i].getName();
                // Log.e("CountryName",CountryJapan);
            }
        }
    }
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.button_proceedto_pin:

                /*Intent intent = new Intent(getApplicationContext(), VerifyActivity.class);
                startActivity(intent);*/
                if (isValidRegister()) {
                    CarryParkApplication.setDob(dobForApi);
                    attemptRegister(etFullName.getText().toString(), etEmailAddress.getText().toString(), code_dial, etMobileNumber.getText().toString(), dobForApi, countryName);

                   /* if (isPrivacyPolicyOpened && isTermsofServiceOpened) {

                        attemptRegister(etFullName.getText().toString(), etEmailAddress.getText().toString(), ccp.getSelectedCountryCode().toString(), etMobileNumber.getText().toString(), etDob.getText().toString(), ccp.getSelectedCountryName());


                    } else {
                        DialogManager.showUniActionDialog(RegistrationActivity.this, getResources().getString(R.string.acceptTermsCond), "OK", new DialogManager.IUniActionDialogOnClickListener() {
                            @Override
                            public void onPositiveClick() {

                            }
                        });
                    }*/
                }
                break;
            case R.id.tv_privacypolicy:
                PrivacyPolicyPopup();
                break;
            case R.id.tv_terms_of_service:
                TermsofServicePopup();
                break;
            case R.id.img_back_arrow:
                onBackPressed();
                break;
            case R.id.et_dob:
                hideKeyboard(CarryParkApplication.getCurrentActivity());
                etEmailAddress.clearFocus();
                etFullName.clearFocus();
                etMobileNumber.clearFocus();


                String myFormat = "yyyy-MM-dd"; //In which you need put here
                SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);

                Calendar c = Calendar.getInstance();
                Date today = c.getTime();
                String tody = sdf.format(today.getTime());


                DatePickerPopWin pickerPopWin = new DatePickerPopWin.Builder(RegistrationActivity.this, new DatePickerPopWin.OnDatePickedListener() {
                    @Override
                    public void onDatePickCompleted(int year, int month, int day, String dateDesc) {
                        myCalendar.set(Calendar.YEAR, year);
                        myCalendar.set(Calendar.MONTH, month - 1);
                        myCalendar.set(Calendar.DAY_OF_MONTH, day);
                        updateLabel();
                    }
                }).textConfirm(CarryParkApplication.getCurrentContext().getResources().getString(R.string.done)) //text of confirm button
                        .textCancel(CarryParkApplication.getCurrentContext().getResources().getString(R.string.cancel)) //text of cancel button
                        .btnTextSize(16) // button text size
                        .viewTextSize(25) // pick view text size
                        .colorCancel(Color.parseColor("#f2f7fa")) //color of cancel button
                        .colorConfirm(Color.parseColor("#2496EC"))//color of confirm button
                        .minYear(1900) //min year in loop
                        .maxYear(2022) // max year in loop
                        .dateChose(tody) // date chose when init popwindow
                        .build();
                pickerPopWin.showPopWin(RegistrationActivity.this);



                break;
        }
    }


   /* DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
            // TODO Auto-generated method stub
            myCalendar.set(Calendar.YEAR, year);
            myCalendar.set(Calendar.MONTH, month);
            myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);


            updateLabel();
        }

    };*/


    private void updateLabel() {
        String myFormat = "yyyy-MM-dd"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);

        Calendar c = Calendar.getInstance();
        Date today = c.getTime();
        Date selected_date = myCalendar.getTime();
        if (selected_date != null) {
            String dayOfTheWeek = (String) DateFormat.format("EEEE", selected_date); // Thursday
            String day = (String) DateFormat.format("d", selected_date); // 20
            String monthString = (String) DateFormat.format("MMM", selected_date); // Jun
            String monthNumber = (String) DateFormat.format("M", selected_date); // 06
            String year = (String) DateFormat.format("yyyy", selected_date); // 2013

           /* Log.e("Da,dayOfTheWeek",dayOfTheWeek);
            Log.e("Da,day",day);
            Log.e("Da,monthString",monthString);
            Log.e("Da,monthNumber",monthNumber);
            Log.e("Da,year",year);
*/
            if (SharedPreferenceUtility.isJapanease()) {
                etDob.setText(year + "年" + monthNumber + "月" + day + "日");
            } else {
                etDob.setText(monthString + " " + day + "," + year);

            }

        }
        if ( sdf.format(today).compareTo(sdf.format(selected_date)) < 0) {
            etDob.setText("");
        } else {
            dobForApi = sdf.format(myCalendar.getTime());
        }
    }


    //Registration
    public void attemptRegister(String name, final String email, String ccp_id, String mobilenumber, String dob, String ccp_name) {
        showBusyAnimation("");
        Map<String, Object> candidateMap = new HashMap<>();

        candidateMap.put("first_name", name);
        candidateMap.put("email", email);
        candidateMap.put("dob", dob);
        candidateMap.put("mobile", mobilenumber);
        candidateMap.put("mac_id", deviceMacId);
        candidateMap.put("phone_code", ccp_id);
        candidateMap.put("country_jp",CountryJapan);
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
        candidateMap.put("country", nationality);

        String android_id = Settings.Secure.getString(getApplicationContext().getContentResolver(),
                Settings.Secure.ANDROID_ID);
        candidateMap.put("mac_id", android_id);
        apiService.signUp(candidateMap).enqueue(new Callback<UserRegisterResponse>() {
            @Override
            public void onResponse(Call<UserRegisterResponse> call, Response<UserRegisterResponse> response) {
                hideBusyAnimation();

                if (response.code() == 200) {
                    //progressDialog.dismiss();
                    if (response.body().getSuccess() == true) {

                        ll_bag_layout.setVisibility(View.VISIBLE);
                        String pinmsg="";
                        if (SharedPreferenceUtility.isJapanease())
                        {
                            pinmsg="メールアドレスに送信された暗証番号を入力してください";
                        }
                        else if (SharedPreferenceUtility.isKorean()){
                            pinmsg="이메일 주소로 전송 된 PIN을 입력하세요.";
                        }
                        else if (SharedPreferenceUtility.isChinease()){
                            pinmsg="请输入发送到您的电子邮件地址的PIN码";
                        }
                        else if (SharedPreferenceUtility.isEnglish()){
                            pinmsg="Please enter the PIN, that sent to your email address";
                        }
                        DialogManager.showUniActionDialog(CarryParkApplication.getCurrentActivity(), pinmsg, "ok", new DialogManager.IUniActionDialogOnClickListener() {
                            @Override
                            public void onPositiveClick() {
                                SharedPreferenceUtility.saveEmailId(email);
                                Intent intent = new Intent(getApplicationContext(), LoginActivityNew.class);
                                startActivity(intent);

                                //sjn test for navigating to login page after verfying pin
                                SharedPreferenceUtility.SavePinVerified(false);

                                finish();
                            }
                        });



                    } else if (response.body().getSuccess() == false) {
                        String msg = response.body().getMessage();
                        DialogManager.showAlertSingleActionDialog(CarryParkApplication.getCurrentActivity(), msg, "ok", new DialogManager.IUniActionDialogOnClickListener() {
                            @Override
                            public void onPositiveClick() {

                            }


                        });
                    }


                } else if (response.code() == 404) {

                    hideBusyAnimation();
                    //String error = response.body().getData()
                    DialogManager.showAlertSingleActionDialog(CarryParkApplication.getCurrentActivity(), CarryParkApplication.getCurrentContext().getResources().getString(R.string.validation_error), "ok", new DialogManager.IUniActionDialogOnClickListener() {
                        @Override
                        public void onPositiveClick() {

                        }


                    });
                }
            }

            @Override
            public void onFailure(Call<UserRegisterResponse> call, Throwable t) {
                hideBusyAnimation();
                if (!Utils.isNetworkConnectionAvailable(getApplicationContext())) {
                    DialogManager.showAlertSingleActionDialog(CarryParkApplication.getCurrentActivity(), CarryParkApplication.getCurrentContext().getResources().getString(R.string.OperfailedExtnd), "ok", new DialogManager.IUniActionDialogOnClickListener() {
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

    private boolean isValidRegister() {
        Calendar c = Calendar.getInstance();
        Date today = c.getTime();

        if (etFullName.getText().toString().isEmpty()) {

            DialogManager.showAlertSingleActionDialog(CarryParkApplication.getCurrentActivity(), CarryParkApplication.getCurrentContext().getResources().getString(R.string.enter_username), "ok", new DialogManager.IUniActionDialogOnClickListener() {
                @Override
                public void onPositiveClick() {

                }
            });

            etFullName.requestFocus();
            return false;
        }
        else if (nationality.isEmpty())
        {

            DialogManager.showAlertSingleActionDialog(CarryParkApplication.getCurrentActivity(), CarryParkApplication.getCurrentContext().getResources().getString(R.string.emptycountry), "ok", new DialogManager.IUniActionDialogOnClickListener() {
                @Override
                public void onPositiveClick() {

                }
            });
            return false;
        }
        else if (etMobileNumber.getText().toString().isEmpty()) {
            DialogManager.showAlertSingleActionDialog(CarryParkApplication.getCurrentActivity(), CarryParkApplication.getCurrentContext().getResources().getString(R.string.emptyMobile), "ok", new DialogManager.IUniActionDialogOnClickListener() {
                @Override
                public void onPositiveClick() {

                }
            });
            etMobileNumber.requestFocus();
            return false;
        } else if (!(etMobileNumber.getText().toString().length() >= 4 && etMobileNumber.getText().toString().length() <= 14)) {
            DialogManager.showAlertSingleActionDialog(CarryParkApplication.getCurrentActivity(), CarryParkApplication.getCurrentContext().getResources().getString(R.string.phone_eror), "ok", new DialogManager.IUniActionDialogOnClickListener() {
                @Override
                public void onPositiveClick() {

                }
            });
            etMobileNumber.requestFocus();
            return false;
        }
        /*else if(etMobileNumber.getText().toString().length()!=10){
            etMobileNumber.setError(getString(R.string.phone_eror));
            etMobileNumber.requestFocus();
            return false;
        }*/
        else if (etEmailAddress.getText().toString().isEmpty()) {
            DialogManager.showAlertSingleActionDialog(CarryParkApplication.getCurrentActivity(), getString(R.string.invalidEmail), "ok", new DialogManager.IUniActionDialogOnClickListener() {
                @Override
                public void onPositiveClick() {

                }
            });            etEmailAddress.requestFocus();
            return false;
        } else if (!isValidEmail(etEmailAddress.getText().toString())) {
            DialogManager.showAlertSingleActionDialog(CarryParkApplication.getCurrentActivity(), getString(R.string.invalidEmail), "ok", new DialogManager.IUniActionDialogOnClickListener() {
                @Override
                public void onPositiveClick() {

                }
            });
            etEmailAddress.requestFocus();
            return false;
        } else if (etDob.getText().toString().isEmpty()) {
            DialogManager.showAlertSingleActionDialog(CarryParkApplication.getCurrentActivity(), getString(R.string.empty_dob), "ok", new DialogManager.IUniActionDialogOnClickListener() {
                @Override
                public void onPositiveClick() {

                }
            });
            etDob.requestFocus();
            return false;
        }
        /*else if (!isPasswordValid(edPassword.getText().toString())) {
            edPassword.setError(getString(R.string.password_grater_than_6));
            edPassword.requestFocus();
            return false;
        }*/
        /*if(termsAndConditions == false){
            ZaskaDialog dialog = new ZaskaDialog(this, "", getString(R.string.please_accept_terms_conditions), new ZaskaDialog.DialogResult() {
                @Override
                public void onPositiveClick() {

                }

                @Override
                public void onCancelClicked() {

                }
            });
            dialog.show();
            dialog.hideNegativeButton();
            return false;
        }*/
        return true;

    }

    @Override
    public void onBackPressed() {
        //Execute your code here
        finish();
    }

    public static boolean isValidEmail(CharSequence target) {
        return (!TextUtils.isEmpty(target) && Patterns.EMAIL_ADDRESS.matcher(target).matches());
    }
    /*@Override
    protected void onPostResume() {
        super.onPostResume();
        if(!Utils.isNetworkConnectionAvailable(getApplicationContext())){
            new AlertDialog.Builder(getApplicationContext())
                    .setTitle(R.string.alert)
                    .setMessage(R.string.no_internet_connection)
                    .setCancelable(false)
                    .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            // Whatever..
                        }
                    }).show();
        }
    }*/

    public void PrivacyPolicyPopup() {
        final Dialog dialog = new Dialog(RegistrationActivity.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.privacy_policy);

        LinearLayout dialogButton = (LinearLayout) dialog.findViewById(R.id.button_layout);
        TextView tvClose = (TextView) dialog.findViewById(R.id.tv_close);
        TextView tvHeading = (TextView) dialog.findViewById(R.id.tv_heading);
        TextView text = (TextView) dialog.findViewById(R.id.text);

        WebView webView = (WebView) dialog.findViewById(R.id.webView);
        String data="";
        if (SharedPreferenceUtility.isEnglish()) {
            tvHeading.setText("Privacy Policy");
            tvClose.setText("Close");
            data = "Carry Park Co., Ltd. (hereinafter “Company”) handles the personal information (hereinafter “User Information”) regarding the users (hereinafter “User”) of the services provided by the Company as follows. \"This policy\").\n" +
                    "\n" +
                    "Article 1 (User information)\n" +
                    "\"User information\" refers to \"personal information\" as referred to in the Personal Information Protection Law, which is information about living individuals, including the name, date of birth, address, telephone number, contact information, etc. included in the information. Specified by the description of\n" +
                    "Information such as personal identification information and appearance, fingerprints, voiceprint data, and the insured number on a health insurance card\n" +
                    "This refers to information that can identify a specific individual from the information itself.\n" +
                    "\n" +
                    "Article 2 (acquisition of user information)\n" +
                    "We may ask you for personal information such as your name, date of birth, address, telephone number, email address, bank account number, and credit card number when you register for use. In addition, between the user and the partner etc.\n" +
                    "Information about transaction records and payments, including personal information of users\n" +
                    "May be collected from the company).\n" +
                    "2. Our company can acquire the location information during the service usage period of our company based on the individual consent of the user.\n" +
                    "\n" +
                    "Article 3 (Purpose of acquiring and using user information)\n" +
                    "The purposes for which we collect and use user information are as follows.\n" +
                    "① To provide and operate our services\n" + "\n" +
                    "② Usage fee billing\n" + "\n" +
                    "③ Identity verification, user authentication\n" + "\n" +
                    "④ User behavior measurement\n" + "\n" +
                    "⑤ Delivery and confirmation of advertisements\n" + "\n" +
                    "⑥Implement important notifications such as changes to the terms of this service\n" + "\n" +
                    "⑦ Responding to information and inquiries regarding this service\n" + "\n" +
                    "⑧ Other purposes set in various services provided by the Company\n" + "\n" +
                    "\n" +
                    "Article 4 (Change of purpose of use)\n" +
                    "The Company will change the purpose of use of user information only when it is reasonably recognized that the purpose of use is relevant to that before the change.\n" +
                    "2. When we change the purpose of use, we will notify the user of the purpose after the change by the method prescribed by our company.\n" +
                    "\n" +
                    "Article 5 (Providing user information to a third party)\n" +
                    "We will not provide user information to a third party without the prior consent of the user. However, in the following cases, the user information may be provided to a third party.\n" +
                    "(1) Upon cooperation with an external service or authentication using an external service, contact the external service operator.\n" +
                    "When providing user information\n" +
                    "(2) Our company must cooperate with the government agency or local public entity or the person entrusted with it to carry out the affairs stipulated by law, and obtaining consent from the user will hinder the performance of the affairs. When there is a risk\n" +
                    "③ When permitted by other laws and regulations\n" +
                    "\n" +
                    "Article 6 (disclosure of user information)\n" +
                    "When the user requests the disclosure of personal information in accordance with the provisions of the Personal Information Protection Law,\n" +
                    "After confirming that the request is from the person in question, we will disclose it to the user without delay. However, this does not apply if the Company is not obligated to disclose under the Personal Information Protection Law and other laws.\n" +
                    "2. Notwithstanding the provisions of the preceding paragraph, in principle, we will not disclose any information other than personal information.\n" +
                    "\n" +
                    "Article 7 (correction and deletion of user information)\n" +
                    "If the user's own personal information is incorrect, the user shall follow the procedure specified by the Company.\n" +
                    "From here, you can request correction, addition or deletion (hereinafter referred to as \"correction\") of personal information to our company.\n" +
                    "2. If we receive the request from the user from the preceding paragraph and determine that it is necessary to respond to that request, we will delay\n" +
                    "Without correction of the personal information.\n" +
                    "3. When the Company makes a correction, etc. based on the provisions of the preceding paragraph, or when it decides not to make a correction, etc.\n" +
                    "Notify the user without delay.\n" +
                    "\n" +
                    "Article 8 (suspension of use of personal information, etc.)\n" +
                    "We believe that personal information is handled by the person beyond the scope of the purpose of use, or\n" +
                    "The use is suspended or erased because it was acquired by a means (hereinafter referred to as \"suspension, etc.\"\n" +
                    "If necessary, we will conduct the necessary investigation without delay.\n" +
                    "2. If it is determined that it is necessary to comply with the request based on the survey results in the preceding paragraph, we will stop using the personal information without delay.\n" +
                    "3. If we suspend the use or decide not to do so, we will promptly notify the user\n" +
                    "Will be notified.\n" +
                    "Four. When the Company has a large amount of cost to suspend the use, or when it is difficult to suspend the use, and if it is possible to take alternative measures necessary to protect the rights and interests of the user, Alternative\n" +
                    "Shall be taken.\n" +
                    "\n" +
                    "Article 9 (Change of privacy policy)\n" +
                    "The contents of this policy shall be notified to the user, except for matters specified by laws and other regulations.\n" +
                    "Can be changed without.\n" +
                    "2. Except when otherwise specified by us, the changed privacy policy is from the time of posting on this website.\n" +
                    "Shall take effect.\n" +
                    "\n" +
                    "Article 10 (Inquiry window)\n" +
                    "For inquiries regarding this policy, please contact the following contact.\n" +
                    "\n" +
                    "Residence: Shibuya Miyata Building 7F, 1-12-14 Jinnan, Shibuya-ku, Tokyo 150-0041\n" +
                    "Company name: Carry Park Co., Ltd.\n" +
                    "Responsibility: Customer Service Office\n 〇〇〇〇\n" +
                    "E-mail: info@carrypark.com\n";

        } else if (SharedPreferenceUtility.isJapanease()){
            tvHeading.setText("プライバシーポリシー");
            tvClose.setText("閉じる");
            data = "キャリーパーク株式会社（以下「当社」という）は、当社の提供するサービスの利用者（以下「ユーザー」という）に関する個人情報（以下「ユーザー情報」という）の取り扱いについて、以下のとおりプライバシーポリシー（以下「本ポリシー」という）を定めます。\n" +
                    "\n" +
                    "第１条（ユーザー情報）\n" +
                    "「ユーザー情報」とは、個人情報保護法にいう「個人情報」を指すものとし、生存する個人に関する情報であって、当該情報に含まれる氏名、生年月日、住所、電話番号、連絡先その他の記述等により特定の\n" +
                    "個人を識別できる情報及び容貌、指紋、声紋にかかるデータ、及び健康保険証の被保険者番号などの\n" +
                    "当該情報単体から特定の個人を識別できる情報を指します。\n" +
                    "\n" +
                    "第２条（ユーザー情報の取得）\n" +
                    "当社は、ユーザーが利用登録をする際に氏名、生年月日、住所、電話番号、メールアドレス、銀行口座番号、クレジットカード番号等の個人情報をお尋ねすることがあります。また、ユーザーと提携先等の間で\n" +
                    "なされたユーザーの個人情報を含む取引記録や決済に関する情報を、当社の提携先（銀行、クレジット\n" +
                    "会社等）から収集することがあります。\n" +
                    "2．当社は、ユーザーの個別同意に基づいて当社サービス利用期間中の位置情報を取得することができます。\n" +
                    "\n" +
                    "第３条（ユーザー情報を取得・利用する目的）\n" +
                    "当社がユーザー情報を取得・利用する目的は、以下のとおりです。\n" +
                    "①\t当社サービスの提供及び運営のため\n" + "\n" +
                    "②\t利用料金請求\n" + "\n" +
                    "③\t本人確認、ユーザー認証\n" + "\n" +
                    "④\tユーザーの行動測定\n" + "\n" +
                    "⑤\t広告の配信及びその確認\n" + "\n" +
                    "⑥\t本サービスに関する規約変更等の重要な通知の実施\n" + "\n" +
                    "⑦\t本サービスに関するご案内・お問い合わせへの対応\n" + "\n" +
                    "⑧\tその他当社が提供する各種サービスにおいて定める目的\n" + "\n" +
                    "\n" +
                    "第４条（利用目的の変更）\n" +
                    "当社は、利用目的が変更前と関連性を有すると合理的に認められる場合に限り、ユーザー情報の利用目的を変更するものとします。\n" +
                    "2．利用目的の変更を行った場合には、変更後の目的について、当社所定の方法により、ユーザーに通知します。\n" +
                    "\n" +
                    "第５条（ユーザー情報の第三者提供）\n" +
                    "当社は、ユーザー情報に関して、予めユーザーの同意を得ることなく、第三者に提供しません。但し、次に定める場合にはこの限りではなく、ユーザー情報を第三者に提供することがあります。\n" +
                    "①\t外部サービスとの連携または、外部サービスを利用した認証にあたり、当該外部サービス運営会社に\n" +
                    "ユーザー情報を提供する場合\n" +
                    "②\t国の機関もしくは地方公共団体またはその委託を受けた者が法令の定める事務を遂行することに対して当社が協力する必要があり、かつユーザーからの同意取得が当該事務の遂行に支障を及ぼすおそれがある場合\n" +
                    "③\tその他法令で認められる場合\n" +
                    "\n" +
                    "第６条（ユーザー情報の開示）\n" +
                    "当社は、ユーザーから、個人情報保護法の定めに基づき個人情報の開示を求められたときは、ユーザー\n" +
                    "ご本人からのご請求であることを確認のうえで、ユーザーに対し遅滞なく開示を行います。但し、個人情報保護法その他の法令により、当社が開示の義務を負わない場合は、この限りではありません。\n" +
                    "2．前項の定めに関わらず、個人情報以外の情報については、開示しないことを原則とします。\n" +
                    "\n" +
                    "第７条（ユーザー情報の訂正及び削除）\n" +
                    "ユーザーは、当社の保有する自己の個人情報が誤った情報である場合には、当社が定める手続きに\n" +
                    "より、当社に対して個人情報の訂正、追加または削除（以下「訂正等」という）を請求することができます。\n" +
                    "2．当社は、ユーザーから前項の請求を受けてその請求に応じる必要があると判断した場合には、遅滞\n" +
                    "なく当該個人情報の訂正等を行うものとします。\n" +
                    "3．当社は、前項の規定に基づき訂正等を行った場合、または訂正等を行わない旨の決定をしたときは\n" +
                    "遅滞なくこれをユーザーに通知します。\n" +
                    "\n" +
                    "第８条（個人情報の利用停止等）\n" +
                    "当社は、本人から、個人情報が利用目的の範囲を超えて取り扱われているという理由、または不正の\n" +
                    "手段により取得されたものであるという理由により、その利用の停止または消去（以下「利用停止等」と\n" +
                    "いう）を求められた場合には、遅滞なく必要な調査を行います。\n" +
                    "2．前項の調査結果に基づき、その請求に応じる必要があると判断した場合には、遅滞なく当該個人情報の利用停止等を行います。\n" +
                    "3．当社は、利用停止等を行った場合、または行わない旨の決定をしたときは、遅滞なくこれをユーザー\n" +
                    "に通知します。\n" +
                    "4．当社は、利用停止等に多額の費用を有する場合その他利用停止等を行うことが困難な場合であって、ユーザーの権利利益を保護するために必要なこれに代わるべき措置をとれる場合は、この代替え策を\n" +
                    "講じるものとします。\n" +
                    "\n" +
                    "第９条（プライバシーポリシーの変更）\n" +
                    "本ポリシーの内容は、法令その他本ポリシーに別段の定めのある事項を除いて、ユーザーに通知すること\n" +
                    "なく変更することができるものとします。\n" +
                    "2．当社が別途定める場合を除いて、変更後のプライバシーポリシーは、本ウェブサイトに掲載したときから\n" +
                    "効力を生じるものとします。\n" +
                    "\n" +
                    "第10条（お問い合わせ窓口）\n" +
                    "本ポリシーに関するお問い合わせは、下記の窓口までお願いいたします。\n" +
                    "\n" +
                    "住 所:〒150-0041　東京都渋谷区神南1－12－14　渋谷宮田ビル7F\n" +
                    "社 名:キャリーパーク株式会社\n" +
                    "担 当:お客様相談室　\n 〇〇　〇〇\n" +
                    "E-mail:info@carrypark.com\n";
        }
        else if (SharedPreferenceUtility.isKorean()){
            tvHeading.setText("개인 정보 보호 정책");
            tvClose.setText("닫기");
            data = "캐리 파크 주식회사 (이하 \"당사\")은 회사가 제공하는 서비스의 이용자 (이하 \"사용자\"라한다)에 관한 개인 정보 (이하 \"사용자 정보\"라한다)의 취급에 대해 다음과 같이 개인 정보 보호 정책 (이하 \"본 정책\"이라한다)을 정합니다.\n" +
                    "\n" +
                    "제 1 조 (사용자 정보)\n" +
                    "\"사용자 정보\"는 개인 정보 보호법에서 말하는 \"개인 정보\"를 의미하며 생존하는 개인에 관한 정보로서 당해 정보에 포함되어있는 성명, 생년월일, 주소, 전화 번호, 연락처 기타 의 기술 등에 의해 특정\n" +
                    "개인 식별 정보와 외모 지문, 성문에 걸리는 데이터 및 건강 보험증의 피보험자 번호 등\n" +
                    "해당 정보 단체에서 특정 개인을 식별 할 수있는 정보를 말합니다.\n" +
                    "\n" +
                    "제 2 조 (사용자 정보 받기)\n" +
                    "우리는 사용자가 이용 등록을 할 때 성명, 생년월일, 주소, 전화 번호, 이메일 주소, 은행 계좌 번호, 신용 카드 번호 등의 개인 정보를 요청할 수 있습니다. 또한 사용자와 파트너 등의 사이에서\n" +
                    "제출 된 사용자의 개인 정보를 포함한 거래 기록 및 결제에 관한 정보를 저희 파트너 (은행, 신용\n" +
                    "회사 등)에서 수집 할 수 있습니다.\n" +
                    "2. 당사는 사용자의 개별 동의에 따라 당사 서비스 이용 기간 동안의 위치 정보를 취득 할 수 있습니다.\n" +
                    "\n" +
                    "제 3 조 (사용자 정보를 취득 · 이용 목적)\n" +
                    "당사가 사용자 정보를 취득 · 이용 목적은 다음과 같습니다.\n" +
                    "① 회사 서비스의 제공 및 운영을위한\n" +
                    "② 이용 요금 청구\n" +
                    "③ 본인 확인, 사용자 인증\n" +
                    "④ 사용자 행동 측정\n" +
                    "⑤ 광고 게재 및 그 확인\n" +
                    "⑥ 본 서비스 약관 변경 등의 중요한 알림 실시\n" +
                    "⑦ 본 서비스에 관한 안내 · 문의에 대한 대응\n" +
                    "⑧ 기타 당사가 제공하는 각종 서비스에서 정하는 목적\n" +
                    "\n" +
                    "제 4 조 (이용 목적의 변경)\n" +
                    "당사는 이용 목적이 변경 전과 관련성을 가지면 합리적으로 인정되는 경우에 한하여 사용자 정보의 이용 목적을 변경하는 것으로합니다.\n" +
                    "2. 이용 목적을 변경 한 경우에는 변경 후의 목적에 대해 당사 소정의 방법으로 사용자에게 알립니다.\n" +
                    "\n" +
                    "제 5 조 (사용자 정보의 제삼자 제공)\n" +
                    "당사는 사용자 정보에 대해 미리 사용자의 동의없이 제 3 자에게 제공하지 않습니다. 단, 다음 정하는 경우에는 그러하지 아니라 사용자 정보를 제삼자에게 제공 할 수 있습니다.\n" +
                    "① 외부 서비스와의 제휴 또는 외부 서비스를 이용한 인증에있어서, 상기 외부 서비스 운영 회사에\n" +
                    "사용자 정보를 제공하는 경우\n" +
                    "② 국가 기관 또는 지방 공공 단체 또는 그 위탁을받은자가 법령이 정하는 사무를 수행하는 것에 대해 우리가 협력해야하며 사용자의 동의 취득이 해당 사무의 수행에 지장을 미칠 우려가있는 경우\n" +
                    "③ 기타 법령에서 인정되는 경우\n" +
                    "\n" +
                    "제 6 조 (사용자 정보의 공개)\n" +
                    "당사는 사용자로부터 개인 정보 보호법의 규정에 따라 개인 정보의 공개를 요구 한 때에는 사용자\n" +
                    "본인의 요청임을 확인 데다가, 사용자에게 지체없이 공개합니다. 단, 개인 정보 보호법 기타 법령에 의하여 당사가 공시 의무가없는 경우에는 그러하지 않습니다.\n" +
                    "2. 전항의 규정에 관계없이 개인 정보 이외의 정보를 공개하지 않는 것을 원칙으로합니다.\n" +
                    "\n" +
                    "제 7 조 (사용자 정보의 정정 및 삭제)\n" +
                    "사용자는 당사가 보유하고있는 자기의 개인 정보가 잘못된 정보 인 경우에는 회사가 정한 절차에\n" +
                    "더 우리에 대해 개인 정보의 정정, 추가 또는 삭제 (이하 \"개정 등\"이라한다)을 청구 할 수 있습니다.\n" +
                    "2. 당사는 사용자 전항의 청구를 받아 그 청구에 응할 필요가 있다고 판단되는 경우에는 지체\n" +
                    "없이 해당 개인 정보의 정정 등을 실시하는 것으로합니다.\n" +
                    "3. 당사는 전항의 규정에 따라 정정 등을 행한 경우 또는 정정 등을하지 않는 취지의 결정을했을 때는\n" +
                    "지체없이이를 사용자에게 알립니다.\n" +
                    "\n" +
                    "제 8 조 (개인 정보의 이용 정지 등)\n" +
                    "당사는 본인으로부터 개인 정보를 이용 목적의 범위를 넘어 취급하고 있다는 이유 또는 부정의\n" +
                    "수단에 의해 취득 된 것이라는 이유로 그 이용 정지 또는 삭제 (이하 \"이용 정지 등\"\n" +
                    "함)을 요구하는 경우에는 지체없이 필요한 조사를 실시합니다.\n" +
                    "2. 전항의 조사 결과에 따라 그 청구에 응할 필요가 있다고 판단되는 경우에는 지체없이 해당 개인 정보의 이용 정지 등을 실시합니다.\n" +
                    "3. 회사는 이용 정지 등을 실시했을 경우, 또는하지 않는 취지의 결정을 한 때에는 지체없이이를 사용자\n" +
                    "에 통지합니다.\n" +
                    "4. 회사는 이용 정지 등에 많은 비용을 갖는 경우 기타 이용 정지 등을 실시하는 것이 곤란한 경우로서, 사용자의 권익을 보호하기 위해 필요한이를 대체 할 조치를 취할 경우,이 대체 방법을\n" +
                    "강구하여야합니다.\n" +
                    "\n" +
                    "제 9 조 (개인 정보 보호 정책의 변경)\n" +
                    "본 정책의 내용은 법령 기타 본 정책에 특별한 규정이있는 사항을 제외하고 사용자에게 통지 할\n" +
                    "없이 변경 될 수있는 것으로합니다.\n" +
                    "2. 당사가 별도로 정하는 경우를 제외하고는 변경 후의 개인 정보 보호 정책은 본 웹 사이트에 게재 한 때부터\n" +
                    "효력이 발생합니다.\n" +
                    "\n" +
                    "제 10 조 (문의처)\n" +
                    "본 정책에 관한 문의는 아래 창구로 부탁드립니다.\n" +
                    "\n" +
                    "주 소 : 〒150-0041 도쿄도 시부야 구 진난 1-12-14 시부야 미야 빌딩 7F\n" +
                    "회사 명 : 캐리 파크 주식회사\n" +
                    "담당 : 고객 상담실 \n〇〇 〇〇\n" +
                    "E-mail :info@carrypark.com";
        }
        if (SharedPreferenceUtility.isChinease()) {
            tvHeading.setText("隐私政策");
            tvClose.setText("关");
            data = "Carry Park Co.，Ltd.（以下称为“我们的公司”）具有以下隐私政策（以下称为“用户信息”），有关处理与公司提供的服务的用户（以下称为“用户”）有关的个人信息（以下称为“用户信息”）。 “此政策”）。\n" +
                    "\n" +
                    "第一条（用户信息）\n" +
                    "“用户信息”是指《个人信息保护法》所指的“个人信息”，是关于在世个人的信息，包括信息中包括的姓名，出生日期，地址，电话号码，联系信息等。指定者\n" +
                    "健康保险卡上的信息，例如个人身份信息和外观，指纹，声纹数据以及被保险人号码\n" +
                    "这是指可以从信息本身中识别特定个人的信息。\n" +
                    "\n" +
                    "第2条（获取用户信息）\n" +
                    "当您注册使用时，我们可能会要求您提供个人信息，例如您的姓名，生日，地址，电话号码，电子邮件地址，银行帐号和信用卡号。此外，在用户和合作伙伴之间。\n" +
                    "有关交易记录和付款的信息（包括用户的个人信息）将提供给我们的合作伙伴（银行，信用\n" +
                    "可以从公司收集）。\n" +
                    "2。根据用户的个人同意，本公司可以在本公司的服务使用期内获取位置信息。\n" +
                    "\n" +
                    "第三条（获取和使用用户信息的目的）\n" +
                    "我们收集和使用用户信息的目的如下。\n" +
                    "①提供和运营我们的服务\n" +
                    "②使用费计费\n" +
                    "③身份验证，用户认证\n" +
                    "④用户行为测度\n" +
                    "⑤广告的交付和确认\n" +
                    "⑥执行重要通知，例如更改服务条款\n" +
                    "to回应有关此服务的信息和查询\n" +
                    "⑧公司提供的各种服务中设定的其他目的\n" +
                    "\n" +
                    "第4条（使用目的的变更）\n" +
                    "只有在合理确认使用目的与更改之前的目的有关的情况下，公司才能更改用户信息的使用目的。\n" +
                    "2。当我们改变使用目的时，我们将通过本公司规定的方法将改变后的目的通知用户。\n" +
                    "\n" +
                    "第5条（向第三方提供用户信息）\n" +
                    "未经用户事先同意，我们不会将用户信息提供给第三方。但是，这不限于以下情况，并且可以将用户信息提供给第三方。\n" +
                    "（1）与外部服务合作或使用外部服务进行认证后，请与外部服务运营公司联系。\n" +
                    "提供用户信息时\n" +
                    "（2）我公司必须与政府机关或地方公共团体或受托人合作，以执行法律规定的事务，而征得用户的同意会阻碍事务的执行。有风险时\n" +
                    "③其他法律法规允许时\n" +
                    "\n" +
                    "第6条（用户信息的公开）\n" +
                    "当用户根据《个人信息保护法》的要求要求披露个人信息时，\n" +
                    "确认请求来自相关人员后，我们将立即将其透露给用户。但是，如果公司没有义务根据《个人信息保护法》和其他法律进行披露，则此规定不适用。\n" +
                    "2。尽管有前款的规定，原则上我们不会透露个人信息以外的任何信息。\n" +
                    "\n" +
                    "第7条（更正和删除用户信息）\n" +
                    "如果用户自己的个人信息不正确，则用户应遵循公司规定的程序。\n" +
                    "从这里，您可以要求对我们的个人信息进行更正，添加或删除（以下称为“更正”）。\n" +
                    "2。如果我们从上一段收到用户的请求，并确定有必要对该请求进行响应，我们将延迟\n" +
                    "未经更正，此类个人信息应予以更正。\n" +
                    "3。公司根据前款规定进行更正等，或者决定不进行更正等的。\n" +
                    "立即通知用户。\n" +
                    "\n" +
                    "第8条（暂停使用个人信息等）\n" +
                    "我们认为，个人信息是由个人处理的，超出了使用目的的范围，或者\n" +
                    "由于它是通过某种方式获得的，因此其使用被暂停或删除（以下称为“使用暂停等”）。\n" +
                    "如有必要，我们将立即进行必要的调查。\n" +
                    "2。如果根据前段的调查结果确定有必要遵守要求，我们将立即停止使用个人信息。\n" +
                    "3。如果用户暂停或决定不使用它，我们将及时通知用户\n" +
                    "将得到通知。\n" +
                    "四。当公司因暂停使用而付出巨额费用，或者很难暂停使用，并且有可能采取必要的替代措施保护用户的权益时，另类\n" +
                    "应采取。\n" +
                    "\n" +
                    "第9条（隐私政策的变更）\n" +
                    "除法律和本政策其他条款规定的事项外，本政策的内容应通知用户。\n" +
                    "无需更改即可。\n" +
                    "2。除非我们另行指定，否则更改后的隐私权政策自发布于本网站起。\n" +
                    "生效。\n" +
                    "\n" +
                    "第十条（咨询联络点）\n" +
                    "有关此政策的查询，请联系以下联系人。\n" +
                    "\n" +
                    "住所：东京都涩谷区津南1-12-14涩谷宫田7楼150-0041\n" +
                    "公司名称：Carry Park Co.，Ltd.\n" +
                    "职责：客户服务办公室\n〇〇〇〇\n" +
                    "电子邮件：info@carrypark.com";

        }
        text.setText(data);
        dialogButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isPrivacyPolicyOpened = true;
                dialog.dismiss();

                // finish();

            }
        });

        dialog.show();
    }

    public void TermsofServicePopup() {
        final Dialog dialog = new Dialog(RegistrationActivity.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.terms_of_service);

        LinearLayout dialogButton = (LinearLayout) dialog.findViewById(R.id.button_layout);
        TextView tvClose = (TextView) dialog.findViewById(R.id.tv_close);
        WebView webView = (WebView) dialog.findViewById(R.id.webView);
        TextView textView = (TextView) dialog.findViewById(R.id.text);
        TextView tvTermsOfService = (TextView) dialog.findViewById(R.id.tv_termsofservice);
        String data="";
        if (SharedPreferenceUtility.isEnglish()) {
            tvTermsOfService.setText("Terms of service");
            tvClose.setText("Close");
            data = "This agreement (hereinafter referred to as \"this agreement\") is carried by Carry Park Co., Ltd. Use, temporary deposit\n" +
                    "It defines the terms of use of the service (hereinafter referred to as \"this service\"). All users (hereinafter referred to as \"users\") can use this service in accordance with these Terms.\n" +
                    "This agreement is subject to change. If there is a change, please visit our website one month before the change.\n" +
                    "I will announce it.\n" +
                    "\n" +
                    "Article 1 (How to use)\n" +
                    "To use this service, enter the QR code of the CP device installed at the destination you are using\n" +
                    "You can use it by reading it with a smart phone and downloading the application.\n" +
                    "\n" +
                    "Article 2 (use time)\n" +
                    "The usage time of this service varies depending on the installation location to be used, so check it beforehand before using it.\n" +
                    "Please give me.\n" +
                    "2. In addition, handling may be restricted or suspended if the Company deems necessary due to an emergency.\n" +
                    "\n" +
                    "Article 3 (use fee)\n" +
                    "The usage fee for this service varies depending on the installation location. Please check before using it.\n" +
                    "\n" +
                    "Article 4 (back restrictions)\n" +
                    "In this service, the only bag that can be kept is a carry bag, which can be locked and has a handle.\n" +
                    "The total length, width, and width is 170 cm or less, and the total weight is 30 kg or less.\n" +
                    "\n" +
                    "Article 5 (Rejection of custody)\n" +
                    "We will not be able to keep the bag etc. that contains the next fried food.\n" +
                    "(1) Valuables (cash, cash cards, prepaid cards, securities, jewelry, precious metals, calligraphy, antiques,\n" +
                    "Expensive items such as cameras) and valuable items, documents, materials, etc. for users of 30,000 yen or more\n" +
                    "High-priced items\n" +
                    "(2) Volatile or toxic substances or dangerous substances such as explosives\n" +
                    "(3) Dead bodies, remains, and carcasses\n" +
                    "(4) Illegal items such as stolen items, items such as guns and swords that are prohibited from possession and carrying by law, and\n" +
                    "Things that may be used in crime\n" +
                    "(5) Those that emit odors, those that are filthy, those that are perishable and easily damaged, or those that pollute CP equipment\n" +
                    "Things that may be damaged\n" +
                    "(6) Animals, negligence (over 30 kg in total weight), and other items deemed unsuitable for storage\n" +
                    "2. When it becomes clear that the above-mentioned items were contained, or when there is a suspicion of them,\n" +
                    "We will take appropriate measures such as opening, separate storage and disposal.\n" +
                    "\n" +
                    "Article 6 (use period and collection)\n" +
                    "This service shall be for 3 consecutive days including the date of the start date of use. If the user still deposits the bag in the same CP device for more than 4 days, move the bag to another location and use it for 30 days including the start date of use.\n" +
                    "I will keep it for a while. However, as the charge during storage, multiply the amount equivalent to the charge for one day of the CP device used by the number of storage days.\n" +
                    "The storage amount will be the same amount.\n" +
                    "2. In addition, if you do not collect the item after 30 days from the start of storage, the user\n" +
                    "Assuming that the rights have been waived, we will dispose of the bag and the disposal price will be the storage fee.\n" +
                    "\n" +
                    "Article 7 (User's liability)\n" +
                    "The user shall be liable for damages caused to the Company or a third party by the user, such as damage to the CP device or damage to the back of another CP device.\n" +
                    "\n" +
                    "Article 8 (Disclaimer and liability of our company)\n" +
                    "In the event of damage or damage to the back of the CP device, we shall not be liable for the damage.\n" +
                    "\n" +
                    "Article 9 (Handling of personal information)\n" +
                    "We will comply with our \"Privacy Policy\" for personal information obtained by using this service.\n" +
                    "Handle it properly.\n" +
                    "\n" +
                    "Article 10 (Governing Law/Jurisdiction)\n" +
                    "In interpreting this agreement, Japanese law shall be the governing law.\n" +
                    "2. In the event of a dispute regarding this service, we exclusively agree with the court that has jurisdiction over the location of our head office.\n" +
                    "The court of jurisdiction.\n" +
                    "that's all \n"+"Contact: info@carrypark.com";

        } else if (SharedPreferenceUtility.isJapanease()){
            tvTermsOfService.setText("利用規約");
            tvClose.setText("閉じる");
            data = "この規約（以下「本規約」という）は、キャリーパーク株式会社（以下「当社」という）がキャリーバック（以下「バック」という）を、キャリーパーク機器CP-001（以下「CP機器」という）を利用して、一時預\n" +
                    "かりするサービス（以下「本サービス」という）の利用条件を定めるものです。ユーザーの皆様（以下「ユーザー」という）には、本規約に従って本サービスをご利用いただけます。\n" +
                    "なお、この規約は変更される場合があります。変更がある場合は変更の1か月前に当社ホームページ\n" +
                    "で告知いたします。\n" +
                    "\n" +
                    "第１条（利用方法）\n" +
                    "本サービスの利用方法は、ご利用になる目的地に設置してあるCP機器のQRコードを、ユーザーの\n" +
                    "スマートホンで読み取っていただき、そのアプリケーションをダウンロードすることによりご利用ができます。\n" +
                    "\n" +
                    "第２条（利用時間）\n" +
                    "本サービスの利用時間は、ご利用になる設置場所のよって異なりますので、予めご確認のうえご利用\n" +
                    "ください。\n" +
                    "2．また、緊急事態により当社が必要と認めた場合には、取り扱いを制限または停止することがあります。\n" +
                    "\n" +
                    "第３条（利用料金）\n" +
                    "本サービスの利用料金は、設置場所により異なりますので、予めご確認のうえご利用ください。\n" +
                    "\n" +
                    "第４条（バックの制限）\n" +
                    "本サービスにおいて、お預かりできるバックはキャリーバックのみであり、施錠ができ、取手が付いていて、\n" +
                    "縦、横、幅の合計寸法が170㎝以下のもで、総重量が30㎏以下のものに限ります。\n" +
                    "\n" +
                    "第５条（お預かりの拒否）\n" +
                    "次に揚げるものを入れたバック等はお預かりできません。\n" +
                    "（1）\t貴重品（現金・キャッシュカード・プリペイドカード・有価証券・宝石・貴金属・書画・骨董品・\n" +
                    "カメラ等の高価品）及びユーザーにとって貴重な物品、書類、資料等30,000円以上の\n" +
                    "　　　　　　高額物品\n" +
                    "（2）\t揮発性もしくは毒性のあるものまたは爆発物等の危険物\n" +
                    "（3）\t死体・遺骨・死骸\n" +
                    "（4）\t盗品等の不法物品、銃砲刀剣類など法令により所持、携帯が禁止されているもの及び、\n" +
                    "犯罪に使用されるおそれのあるもの\n" +
                    "（5）\t臭気を発するもの、不潔なもの、腐敗変質もしくは破損しやすいもの、またはCP機器を汚損・\n" +
                    "毀損するおそれのあるもの\n" +
                    "（6）\t動物、重量過失（総重量30㎏を超過するもの）、その他保管に適さないとみとめられるもの\n" +
                    "2．上記の物品が入れられていた事実が分かったとき、またはその疑いがあるときは、当社においてバックの\n" +
                    "開披・別途保管・廃棄その他適当な措置をとらせていただきます。\n" +
                    "\n" +
                    "第６条（使用期間とお引き取り）\n" +
                    "本サービスは、使用開始日の日を含めて連続3日間とします。ユーザーが4日以上過ぎてもなお同一のCP機器にバックを預けている場合には、そのバックを別の場所に移動し、使用開始日を含めて30日\n" +
                    "間保管します。但し、保管中の料金として、使用したCP機器の1日分の料金相当額に保管日数を乗\n" +
                    "じた金額を保管料としていただきます。\n" +
                    "2．また、保管開始から30日以上を経過してもお引き取りがない場合には、ユーザーがバックに対する\n" +
                    "権利を放棄したとみなし、当社はそのバックを処分し、その処分代金は保管料とさせていただきます。\n" +
                    "\n" +
                    "第７条（ユーザーの賠償責任）\n" +
                    "CP機器を破損した場合、または他のCP機器のバックに損害を与えた場合等、ユーザーが当社または第三者に与えた損害は、ユーザーに賠償していただきます。\n" +
                    "\n" +
                    "第８条（免責事項と当社賠償責任）\n" +
                    "CP機器のバックに滅失または毀損の障害が生じたときも、当社は基本的にその賠償の責任を負わないものとします。\n" +
                    "\n" +
                    "第９条（個人情報の取り扱い）\n" +
                    "当社は、本サービスの利用によって取得する個人情報については、当社「プライバシーポリシー」に従い\n" +
                    "適切に取り扱うものとします。\n" +
                    "\n" +
                    "第10条（準拠法・裁判管轄）\n" +
                    "本規約の解釈にあたっては、日本法を準拠法とします。\n" +
                    "2．本サービスに関して紛争が生じた場合には、当社の本店所在地を管轄する裁判所を専属的合意\n" +
                    "管轄裁判所とします。\n" +
                    "以上\n" +
                    "お問い合わせ先： info@carrypark.com" +
                    "\n";
        }
        else if (SharedPreferenceUtility.isKorean())
        {
            tvTermsOfService.setText("서비스 약관");
            tvClose.setText("닫기");
            data = "이 약관 (이하 \"약관\"이라한다)은 캐리 파크 주식회사 (이하 \"회사\"라한다)가 캐리 백 (이하 \"백\"이라한다) 캐리 파크 장비 CP-001 (이하 \"CP 기기\"라한다)를 이용하여 일시 예치\n" +
                    "かりする 서비스 (이하 \"서비스\"라한다)의 이용 조건을 정하는 것입니다. 사용자의 여러분 (이하 \"사용자\"라한다)는 본 약관에 따라 본 서비스를 이용하실 수 있습니다.\n" +
                    "또한,이 약관은 변경 될 수 있습니다. 변경이있는 경우 변경 1 개월 전에 당사 홈페이지\n" +
                    "에서 고지하겠습니다.\n" +
                    "\n" +
                    "제 1 조 (이용 방법)\n" +
                    "본 서비스의 이용 방법은 이용하시는 목적지에 설치되어있는 CP 기기의 QR 코드를 사용자의\n" +
                    "스마트 폰으로 읽어 주시고, 그 응용 프로그램을 다운로드하여 사용할 수 있습니다.\n" +
                    "\n" +
                    "제 2 조 (이용 시간)\n" +
                    "본 서비스의 이용 시간은 이용하시는 설치 장소마다 다양하기 때문에, 미리 확인 후 이용\n" +
                    "바랍니다.\n" +
                    "2. 또한 비상 사태에 의해 당사가 필요하다고 인정하는 경우에는 취급을 제한 또는 중지 할 수 있습니다.\n" +
                    "\n" +
                    "제 3 조 (이용 요금)\n" +
                    "본 서비스의 이용 요금은 설치 장소에 따라 다르므로 미리 확인하신 후 이용해주십시오.\n" +
                    "\n" +
                    "제 4 조 (백 제한)\n" +
                    "본 서비스에서 위탁이 가능한 백 캐리 백 뿐이며 시정 할 수 손잡이가 달려있어,\n" +
                    "가로, 세로, 폭의 총 크기가 170㎝ 이하의 모델 총 무게가 30㎏ 이하인 것에 한합니다.\n" +
                    "\n" +
                    "제 5 조 (위탁의 거부)\n" +
                    "다음 튀김을 넣은 가방 등은 보관하지 않습니다.\n" +
                    "(1) 귀중품 (현금 · 현금 카드 · 선불 카드 · 증권 · 보석 · 귀금속 · 서화 · 골동품 ·\n" +
                    "카메라 등 고가품) 및 사용자에게 귀중한 물품, 서류, 자료 등 30,000 엔 이상\n" +
                    "고액 물품\n" +
                    "(2) 휘발성 또는 독성이있는 것 또는 폭발물 등 위험물\n" +
                    "(3) 시체 · 유골 · 시체\n" +
                    "(4) 채집 등 불법 물품 총포 도검류 등 법령에 의해 소유, 휴대가 금지되는 것과,\n" +
                    "범죄에 사용되는 우려가있는 것\n" +
                    "(5) 악취를 발하는 것, 더러운 것, 부패 변질 또는 파손되기 쉬운 물건이나 CP 장비를 오손 ·\n" +
                    "훼손 할 우려가있는 것\n" +
                    "(6) 동물, 무게 과실 (총 무게 30㎏을 초과하는 것), 기타 보관에 적합하지 않다고 인정할 수있는 것\n" +
                    "2. 위의 물품이 담겨 있던 사실을 안 때 또는 그 의심이있는 경우에는 당사에서 백\n" +
                    "개봉 · 별도 보관 · 폐기 기타 적절한 조치를 취하겠습니다.\n" +
                    "\n" +
                    "제 6 조 (사용 기간 및 수령)\n" +
                    "본 서비스는 사용 개시일의 날을 포함하여 연속 3 일간합니다. 사용자가 4 일 이상 지나서도 여전히 동일한 CP 기기에 맡기고있는 경우에는 그 백을 다른 위치로 이동하여 사용 개시일을 포함하여 30 일\n" +
                    "동안 보관합니다. 단, 보관중인 요금으로 사용한 CP 장비의 1 일분의 요금 상당액에 보관 일수를 제곱\n" +
                    "여긴 금액을 보관료로드립니다.\n" +
                    "2. 또한 보관 개시로부터 30 일 이상 경과해도 수령이없는 경우에는 사용자가 백에 대한\n" +
                    "권리를 포기한 것으로 간주하고, 우리는 그 백을 처분하고 처분 대금은 보관료로하겠습니다.\n" +
                    "\n" +
                    "제 7 조 (사용자의 배상 책임)\n" +
                    "CP 기기가 손상된 경우 또는 다른 CP 기기의 백에 손해를 끼친 경우 등 사용자가 당사 또는 제삼자에게 준 손해는 사용자에게 배상해야합니다.\n" +
                    "\n" +
                    "제 8 조 (면책 조항 및 당사 책임)\n" +
                    "CP 기기의 백 멸실 또는 훼손의 장애가 발생한 경우에도 당사는 기본적으로 그 배상의 책임을지지 않습니다합니다.\n" +
                    "\n" +
                    "제 9 조 (개인 정보의 취급)\n" +
                    "당사는 본 서비스의 이용에 의해 취득한 개인 정보는 당사 \"개인 정보 보호 정책\"에 따라\n" +
                    "적절하게 취급해야합니다.\n" +
                    "\n" +
                    "제 10 조 (준거법 · 재판관)\n" +
                    "본 약관의 해석에 있어서는 일본 법을 준거법으로합니다.\n" +
                    "2. 본 서비스에 관해서 분쟁이 생겼을 경우, 당사의 본점 소재지를 관할하는 법원을 전속 적 합의\n" +
                    "관할 법원으로합니다.\n" +
                    "이상"+" \n" +
                    "연락처 : info@carrypark.com";

        }
        else if (SharedPreferenceUtility.isChinease())
        {
            tvTermsOfService.setText("服务条款");
            tvClose.setText("关");
            data = "本协议（以下称为“本协议”）由随身携带有限公司（以下称为“本公司”）作为随身携带（以下称为“后退”）和随身携带设备CP-001（以下称为“ CP设备”）携带。使用，临时存款\n" +
                    "它定义了服务（以下称为“本服务”）的使用条款。所有用户（以下称为“用户”）均可根据这些条款使用此服务。\n" +
                    "请注意，本协议可能会更改。如果有更改，请在更改前一个月访问我们的网站。\n" +
                    "将宣布。\n" +
                    "\n" +
                    "第1条（使用方法）\n" +
                    "要使用此服务，请输入您正在使用的目的地上安装的CP设备的QR码。\n" +
                    "您可以通过在智能手机上阅读并下载应用程序来使用它。\n" +
                    "\n" +
                    "第2条（使用时间）\n" +
                    "该服务的使用时间因要使用的安装位置而异，因此请在使用前进行检查。\n" +
                    "请给我。\n" +
                    "2。此外，如果公司在紧急情况下认为有必要，则可能会限制或暂停处理。\n" +
                    "\n" +
                    "第三条（使用费）\n" +
                    "此项服务的使用费因安装位置而异，请在使用前进行确认。\n" +
                    "\n" +
                    "第4条（后退限制）\n" +
                    "在此服务中，唯一可以保存的袋子是随身携带的袋子，可以将其锁定并带有提手。\n" +
                    "长，宽和宽的总和为170厘米或更小，总重量为30公斤或更小。\n" +
                    "\n" +
                    "第5条（拒绝羁押）\n" +
                    "我们将不能保留装有下一个油炸食品的袋子等。\n" +
                    "（1）贵重物品（现金，现金卡，预付卡，证券，珠宝，贵金属，书法，古董，\n" +
                    "30,000日圆以上的使用者，相机等贵重物品，贵重物品，文件，资料等\n" +
                    "高价商品\n" +
                    "（2）易挥发或有毒物质或爆炸物等危险物质\n" +
                    "（3）尸体，遗体和尸体\n" +
                    "（4）依法禁止拥有和携带的赃物等非法物品，枪支剑等物品；以及\n" +
                    "可能用于犯罪的事物\n" +
                    "（5）散发出臭味的东西，肮脏的东西，易于变质或变质的东西或污染CP设备的东西。\n" +
                    "可能会损坏的东西\n" +
                    "（6）体重过轻（总重量超过30公斤的动物）和其他不适合存放的物品\n" +
                    "2。如果明确发现已放入上述物品，或有任何怀疑，\n" +
                    "我们将分别采取适当的措施，例如开放，储存和处置。\n" +
                    "\n" +
                    "第6条（使用期限和回收）\n" +
                    "该服务应连续3天（包括使用之日）。如果用户仍将袋子存放在同一CP设备中超过4天，请将袋子移动到另一个位置并使用30天（包括使用开始日期）。\n" +
                    "我会保留一段时间。但是，作为存储期间的费用，请将相当于CP设备一天使用费用的金额乘以存储天数。\n" +
                    "存储量将相同。\n" +
                    "2。此外，如果您从开始储存起30天后仍未收集物品，则用户\n" +
                    "假设权利已被放弃，我们将处理袋子，处置价格为仓储费。\n" +
                    "\n" +
                    "第七条（用户责任）\n" +
                    "用户应对用户对公司或第三方造成的损害负责，例如对CP设备的损坏或对另一CP设备的背面的损坏。\n" +
                    "\n" +
                    "第8条（免责声明和我们的责任）\n" +
                    "如果CP设备的背面有损坏或损坏，本公司将不承担任何责任。\n" +
                    "\n" +
                    "第9条（个人信息的处理）\n" +
                    "对于使用此服务获取的个人信息，我们将遵守“隐私政策”。\n" +
                    "正确处理。\n" +
                    "\n" +
                    "第十条（适用法律/管辖权）\n" +
                    "在解释本协议时，应以日本法律为准据法。\n" +
                    "2。如果对此服务产生争议，我们将完全同意对我们总部所在地具有管辖权的法院。\n" +
                    "管辖法院。\n" +
                    "就这样"+"\n" +
                    "联系方式：info@carrypark.com";

        }
        textView.setText(data);
        dialogButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                dialog.dismiss();
                isTermsofServiceOpened = true;
                // finish();

            }
        });

        dialog.show();
    }

    @Override
    public void applyOverrideConfiguration(Configuration overrideConfiguration) {
        if (overrideConfiguration != null) {
            int uiMode = overrideConfiguration.uiMode;
            overrideConfiguration.setTo(getBaseContext().getResources().getConfiguration());
            overrideConfiguration.uiMode = uiMode;
        }
        super.applyOverrideConfiguration(overrideConfiguration);
    }
}
