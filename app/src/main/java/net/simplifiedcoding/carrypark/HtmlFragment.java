package net.simplifiedcoding.carrypark;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.*;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.IBinder;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.*;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
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
import net.rest.model.PaymentStatus;
import net.ui.BottomNavigation;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static android.content.Context.MODE_PRIVATE;


public class HtmlFragment extends DialogFragment implements View.OnClickListener, ServiceConnection, SerialListener {

    private SharedPreferences sharedPreferences;
    private long currentTimeOfTimer = ConstantProject.TimeOut15Minitue;
    String PaymentType="";

    WebView webView;
    //BottomNavigationView navigation;
    RelativeLayout rlWebview;
    LinearLayout llPaymentCompleted, llCancel;
    //String base_url = "\"https://stg.ribenmeishi.com/weblink/api/checkout\"";
    String base_url = "https://stg.ribenmeishi.com/weblink/api/checkout";
    String UrlPayment = null;
    ApiInterface apiService;

    //  String content = null;
    //Fragment targetFragment = null;
    Button btnCancel;
    TextView tvTimeRremainingWebview;
    private Fragment targetFragment;


    private enum Connected {False, Pending, True}

    private HtmlFragment.Connected connected = HtmlFragment.Connected.True;
    private SerialService service;

    public HtmlFragment() {
        // Required empty public constructor
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
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


        return inflater.inflate(R.layout.fragment_html, null);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (service == null) {
            service = CarryParkApplication.getService();
        }
        sharedPreferences = this.getActivity().getSharedPreferences("carry_park", MODE_PRIVATE);
        apiService = ApiClient.getClient().create(ApiInterface.class);

        // navigation = getActivity().findViewById(R.id.navigation);
        //  navigation.setVisibility(View.GONE);
        // init webView
        CarryParkApplication.setIsAliPay(false);

        webView = (WebView) view.findViewById(R.id.simpleHtmlWebView);
        btnCancel = (Button) view.findViewById(R.id.btn_cancel);
        llCancel = (LinearLayout) view.findViewById(R.id.ll_cancel);
        tvTimeRremainingWebview = (TextView) view.findViewById(R.id.tv_time_remaining_webview);
        btnCancel.setOnClickListener(this);
        //rlWebview = (RelativeLayout)view.findViewById(R.id.rl_webview);
        //llPaymentCompleted = (LinearLayout)view.findViewById(R.id.ll_payment_completed);
        if (getArguments() != null) {
            UrlPayment = getArguments().getString("device_token");

            //deviceToken = "\"tk_4346455888a0aed85adfc29\"";

           /* content= " <!DOCTYPE html><html lang=\"en\"><head><meta name=\"viewport\" content=\"width=device-width, initial-scale=1\"><style>\n" +
                    ".loader {\n" +
                    "        border: 16px solid #f3f3f3;\n" +
                    "        border-radius: 50%;\n" +
                    "        border-top: 16px solid #3498db;\n" +
                    "        width: 120px;\n" +
                    "        height: 120px;\n" +
                    "        -webkit-animation: spin 2s linear infinite; / Safari /\n" +
                    "                animation: spin 2s linear infinite;\n" +
                    "        display:block;\n" +
                    "        margin: 0 auto;\n" +
                    "        position: absolute;\n" +
                    "        top: calc(50% - 120px);\n" +
                    "        left: 0;\n" +
                    "        right:0;\n" +
                    "\n" +
                    "    }\n" +
                    "    button{\n" +
                    "        visibility:hidden;\n" +
                    "    }\n" +
                    "\n" +
                    "\n" +
                    "/ Safari /\n" +
                    "    @-webkit-keyframes spin {\n" +
                    "        0% { -webkit-transform: rotate(0deg); }\n" +
                    "        100% { -webkit-transform: rotate(360deg); }\n" +
                    "    }\n" +
                    "\n" +
                    "    @keyframes spin {\n" +
                    "        0% { transform: rotate(0deg); }\n" +
                    "        100% { transform: rotate(360deg); }\n" +
                    "    }\n" +
                    "</style></head><body onload=\"myFunction()\"><div class=\"loader\"></div><form action="+"\""+base_url+"\""+" method=\"post\" ><input type=\"hidden\" name=\"payment_id\" value="+"\""+deviceToken+"\""+"><button id=\"sumb\" type=\"submit\"><span>Checkout</span></button></form><script>\n" +
                    "    function myFunction() {\n" +
                    "        document.getElementById(\"sumb\").click();\n" +
                    "    }\n" +
                    "</script></body></html>\n";
*/

            //count down timer for webview fragment
           new CountDownTimer(currentTimeOfTimer , 1000) { // adjust the milli seconds here

                public void onTick(long millisUntilFinished) {
                    if (SharedPreferenceUtility.isJapanease())
                    {
                        tvTimeRremainingWebview.setText(""+String.format("%d 分",
                                TimeUnit.MILLISECONDS.toMinutes( millisUntilFinished),
                                TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished) -
                                        TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished))));

                    }
                    else {
                        tvTimeRremainingWebview.setText(""+String.format("%d m",
                                TimeUnit.MILLISECONDS.toMinutes( millisUntilFinished),
                                TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished) -
                                        TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished))));

                    }

                    currentTimeOfTimer = millisUntilFinished;
                }

                public void onFinish() {
                    //closing payment and go to previous page
                    InitialPaymentFragment("pay_failure", false, true);


                }
            }.start();
        }

        // displaying text in WebView

        //webView.loadData(content, "text/html", "UTF-8");

        //webView.loadData(content, "text/html", null);

        //webView.loadDataWithBaseURL(null, content, "text/html", "utf-8", null);

        // displaying content in WebView from html file that stored in assets folder

        webView.setWebViewClient(new WebViewClient() {
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }
        });
        WebSettings webSettings = webView.getSettings();

       // String userAgent = String.format("%s [%s/%s]", webSettings.getUserAgentString(), "Chrome", BuildConfig.VERSION_NAME);
        webView.getSettings().setSaveFormData(false);

       // webView.getSettings().setUserAgentString(userAgent);
        webView.getSettings().setJavaScriptEnabled(true);
        // webView.loadData(deviceToken,"text/html",null);
        webView.loadUrl(UrlPayment);

        webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);

                // Log.d("WebView", "your current url when webpage loading.." + url);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                //  Log.d("WebView", "your current url when webpage loading.. finish" + url);
                super.onPageFinished(view, url);
            }

            @Override
            public void onLoadResource(WebView view, String url) {
                // TODO Auto-generated method stub
                super.onLoadResource(view, url);
                Log.e("urls",url);
                //https://stg-p.takeme.com/qrpay/tk_67b81a853c0872b2ad89f2a5645ee/P9df90c088fa44df652d/googlepay
                try {
                    String html = URLDecoder.decode(url, "UTF-8").substring(9);
                    sourceReceived(html);
                } catch (UnsupportedEncodingException e) {
                    Log.e("example", "failed to decode source", e);
                }

                if (url.contains("linepay"))
                {
                    PaymentType="lp";
                    sendSuccessMail();
                }

                if (url.contains("payment=alipay")) {



                    InitialPaymentFragmentAliPAy("pay_cancel", false, true);


                }
                if (url.contains("applepay"))
                {
                    PaymentType="ap";
                    sendSuccessMail();


                }

                if (url.contains("wechat_oauth")) {
                    PaymentType="wp";
                    sendSuccessMail();


                    InitialPaymentFragmentWebChat("pay_cancel", false, true);

                }
                //if(url.contains("https://js.stripe.com/v3/fingerprinted/js/m-outer-5af13a74058f4fb832e146db610e8300.js") || url.contains("https://m.stripe.network/inner.html#url=https%3A%2F%2Fstg.ribenmeishi.com%2Fweblink%2Fpayment&title=&referrer=&muid=cfd40c19-42e1-401e-be57-2590d264334c&sid=5154595d-98aa-453e-9b08-339b803580ed&preview=false") || url.contains("https://stg-cdn.ribenmeishi.com/weblink/favicon.ico") || url.contains("https://m.stripe.com/4")) {
                //if(url.contains("https://m.stripe.com/4")) {
                if (url.contains("https://js.stripe.com/v3/elements-inner-card")) {
                    PaymentType="cc";
                    sendSuccessMail();

                   // llCancel.setVisibility(view.GONE);
                    //
                } else {
                    //btnCancel.setVisibility(View.GONE);&timestamp=1585674374894&version=ca664fcd&frame_width=360&element=cardNumber&key=pk_test_PKd2hagYoRWNrbMbSXY92vHF&referrer=https%3A%2F%2Fstg.ribenmeishi.com%2Fweblink%2Fcredit%2Ftk_198beb855ce852b501f00d3&stripe_js_id=f7b62fd8-e127-4e2f-8ec6-0536a71264a9&wrapper=unknown&esModule=false
                }
                if (url.contains("167.172.39.84")) {

                    callIsPaymentSuccess();


                    /*PaymentFragment targetFragment = new PaymentFragment ();
                    replaceFragment(targetFragment,"pay_success");*/
                }




            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                //  System.out.println("when you click on any interlink on webview that time you got url :-" + url);
                return super.shouldOverrideUrlLoading(view, url);
            }
        });
        /*//String webUrl = webView.getUrl();
        if(webView.getUrl().equalsIgnoreCase("cp.phenomtec.com")){
            webView.setVisibility(View.GONE);
        }*/

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_cancel:
                //sjn added for checking which payment fragment to be replaced
                String lockStatus = sharedPreferences.getString("isLocked", null);
                if (lockStatus != null && lockStatus.equalsIgnoreCase("LOCKED")) {
                   /* FinalPaymentFragment targetFragment = new FinalPaymentFragment ();
                    replaceFragment(targetFragment,"pay_cancel");*/
                    InitialPaymentFragment("pay_cancel", false, true);
                } else {
                   /* InitialPaymentFragment targetFragment = new InitialPaymentFragment();
                    replaceFragment(targetFragment,"pay_cancel");*/
                    disconnect();
                    InitialPaymentFragment("pay_cancel", false, true);

                }
                /*PaymentFragment targetFragment = new PaymentFragment ();
                replaceFragment(targetFragment,"pay_cancel");*/
                break;
            /*case R.id.button_login:
                intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
                //attemptLogin();
                break;
            case R.id.et_dob:
                new DatePickerDialog(ForgotPinActivity.this, date, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();
                break;*/
        }
    }

    public void replaceFragment(Fragment targetfragment, String paymentStatus) {

        Bundle args = new Bundle();
        args.putString("is_from", "WebViewFragment");
        args.putString("payment_status", paymentStatus);
        args.putLong("current_time_of_timer", currentTimeOfTimer);
        targetfragment.setArguments(args);

        //Inflate the fragment
        getFragmentManager().beginTransaction().add(R.id.fragment_container, targetfragment).commit();

    }

    public void replaceFragment(Fragment someFragment) {

        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, someFragment);
        transaction.addToBackStack(null);
        transaction.commit();

    }

    public void InitialPaymentFragment(String paymentStatus, boolean success, boolean fail) {
        Activity activity = getActivity();
        if(activity != null && isAdded())
        {
            disconnect();
            Intent intent = new Intent(CarryParkApplication.getCurrentActivity(), BottomNavigation.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            intent.putExtra("page", ConstantProject.InitialPaymentFragment);
            intent.putExtra("is_from", "WebViewFragment");
            intent.putExtra("payment_status", paymentStatus);
            intent.putExtra("pay_success", success);
            intent.putExtra("isAliPay", false);
            intent.putExtra("iswebchat", false);

            intent.putExtra("current_time_of_timer", currentTimeOfTimer);
            intent.putExtra("pay_cancel", fail);
            startActivity(intent);
        }

    }

    public void InitialPaymentFragmentAliPAy(String paymentStatus, boolean success, boolean fail) {
        disconnect();


        Intent intent = new Intent(CarryParkApplication.getCurrentActivity(), BottomNavigation.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent.putExtra("page", ConstantProject.InitialPaymentFragment);
        intent.putExtra("is_from", "WebViewFragment");
        intent.putExtra("payment_status", paymentStatus);
        intent.putExtra("pay_success", success);
        intent.putExtra("isAliPay", true);
        intent.putExtra("iswebchat", false);

        intent.putExtra("current_time_of_timer", currentTimeOfTimer);
        intent.putExtra("pay_cancel", fail);

       startActivity(intent);
    }
    public void InitialPaymentFragmentWebChat(String paymentStatus, boolean success, boolean fail) {
        disconnect();


        Intent intent = new Intent(CarryParkApplication.getCurrentActivity(), BottomNavigation.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent.putExtra("page", ConstantProject.InitialPaymentFragment);
        intent.putExtra("is_from", "WebViewFragment");
        intent.putExtra("payment_status", paymentStatus);
        intent.putExtra("pay_success", success);
        intent.putExtra("isAliPay", false);
        intent.putExtra("iswebchat", true);

        intent.putExtra("current_time_of_timer", currentTimeOfTimer);
        intent.putExtra("pay_cancel", fail);

        startActivity(intent);
    }

    public void moveToFinalPaymentFragment(String paymentStatus, boolean isSuccess, boolean isFail) {
      /*  Bundle args = new Bundle();
        args.putString("is_from", "WebViewFragment");
        args.putString("payment_status",paymentStatus);
        args.putLong("current_time_of_timer",currentTimeOfTimer);
        targetfragment.setArguments(args);*/

        Intent intent = new Intent(CarryParkApplication.getCurrentActivity(), BottomNavigation.class);
        intent.putExtra("page", ConstantProject.PaymentSuccessFragment);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent.putExtra("is_from", "WebViewFragment");
        intent.putExtra("payment_status", paymentStatus);
        intent.putExtra("pay_success", isSuccess);
        intent.putExtra("pay_cancel", isFail);
        intent.putExtra("isAliPay", false);
        intent.putExtra("iswebchat", false);

        intent.putExtra("device_token", UrlPayment);
        intent.putExtra("current_time_of_timer", currentTimeOfTimer);
       startActivity(intent);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (!Utils.isNetworkConnectionAvailable(CarryParkApplication.getCurrentContext())) {
            DialogManager.showAlertSingleActionDialog(CarryParkApplication.getCurrentActivity(), CarryParkApplication.getCurrentContext().getResources().getString(R.string.no_internet), "ok", new DialogManager.IUniActionDialogOnClickListener() {
                @Override
                public void onPositiveClick() {

                }
            });

        }
    }

    private boolean loadFragment(Fragment fragment) {
        //switching fragment
        if (fragment != null) {
            getActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, fragment)
                    .commit();
            return true;
        }
        return false;
    }

    @Override
    public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
        service = ((SerialService.SerialBinder) iBinder).getService();
        service.attach(this);
        CarryParkApplication.setService(service);


    }

    @Override
    public void onServiceDisconnected(ComponentName componentName) {
        service = null;
    }

    public void onSerialConnect() {

        connected = HtmlFragment.Connected.True;
    }

    @Override
    public void onSerialConnectError(Exception e) {
        disconnect();
        ((BaseActivity) getActivity()).hideBusyAnimation();
        DialogManager.showUniActionDialog(CarryParkApplication.getCurrentActivity(), CarryParkApplication.getCurrentContext().getResources().getString(R.string.please_try_again), "ok", new DialogManager.IUniActionDialogOnClickListener() {
            @Override
            public void onPositiveClick() {
                Intent intent = new Intent(CarryParkApplication.getCurrentContext(), BottomNavigation.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                intent.putExtra("page", ConstantProject.SplashActivity);
                startActivity(intent);


            }
        });


    }

    @Override
    public void onSerialRead(byte[] data) {
        receive(data);
    }

    @Override
    public void onSerialIoError(Exception e) {


        DialogManager.showAlertSingleActionDialog(CarryParkApplication.getCurrentActivity(), CarryParkApplication.getCurrentContext().getResources().getString(R.string.Failed_connected) + " " + CarryParkApplication.getScannedDeviceName(), "ok", new DialogManager.IUniActionDialogOnClickListener() {
            @Override
            public void onPositiveClick() {
                connect();

            }
        });


    }

    @Override
    public void onReadDataAtFailure(byte[] data) {

    }


    private void connect() {
        try {
            BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
            BluetoothDevice device = bluetoothAdapter.getRemoteDevice(CarryParkApplication.getDeviceAddressBle());
            connected = HtmlFragment.Connected.Pending;
            SerialSocket socket = new SerialSocket(getActivity().getApplicationContext(), device);
            service.connect(socket);
        } catch (Exception e) {
            onSerialConnectError(e);
        }


    }

    private void send(String str) {

    }

    private void receive(byte[] data) {


    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        getActivity().bindService(new Intent(getActivity(), SerialService.class), this, Context.BIND_AUTO_CREATE);
    }

    @Override
    public void onStart() {
        super.onStart();
        if (service != null)
            service.attach(this);
        else
            getActivity().startService(new Intent(getActivity(), SerialService.class)); // prevents service destroy on unbind from recreated activity caused by orientation change
    }

    private void disconnect() {
        connected = HtmlFragment.Connected.False;
        service.disconnect();

    }

    public void callIsPaymentSuccess() {
        webView.destroy();
        ((BaseActivity) CarryParkApplication.getCurrentActivity()).showBusyAnimation("");
        Map<String, Object> candidateMap = new HashMap<>();
        candidateMap.put("token", SharedPreferenceUtility.getPaymentToken());


        String acess_token = GloablMethods.API_HEADER + AppController.getString(CarryParkApplication.getmContext(), "acess_token");
        apiService =
                ApiClient.getClient().create(ApiInterface.class);
        apiService.CallIsPaymentSuccessAPi(acess_token, candidateMap).enqueue(new Callback<PaymentStatus>() {
            @Override
            public void onResponse(Call<PaymentStatus> call, Response<PaymentStatus> response) {

                if (response.body().isSuccess()) {

                    String status = response.body().getData().getIsPaymentSuccess();//success
                    if (status.contains("success")) {


                         moveToFinalPaymentFragment("pay_success",true,false);

                    } else {
                        InitialPaymentFragment("pay_failure", false, true);

                    }

                }


            }


            @Override
            public void onFailure(Call<PaymentStatus> call, Throwable t) {
                if (!Utils.isNetworkConnectionAvailable(CarryParkApplication.getCurrentContext())) {
                    DialogManager.showAlertSingleActionDialog(CarryParkApplication.getCurrentActivity(),CarryParkApplication.getCurrentContext().getResources().getString(R.string.OperfailedExtnd), "ok", new DialogManager.IUniActionDialogOnClickListener() {
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

    private void sourceReceived(String html) {
       Log.e("html",html);

        if ( html.contains("/qrpay/") && html.contains("https://stg-p.takeme.com/qrpay") && html.contains("googlepay") )
        {

                disconnect();
                PaymentType="gp";
                sendSuccessMail();
                String strPercent = html.split("/checkout&dp=/qrpay/")[1];
                String googlePayUrl="https://stg-p.takeme.com/qrpay/"+strPercent.substring(0,63);
                Log.e("finalStr",googlePayUrl);
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(googlePayUrl));
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.setPackage("com.android.chrome");
                try
                {
                    CarryParkApplication.getCurrentContext().startActivity(intent);
                }
                catch (ActivityNotFoundException ex)
                {
                    //if Chrome browser not installed
                    intent.setPackage(null);
                    CarryParkApplication.getCurrentContext().startActivity(intent);
                }



        }
        if (html.contains("wechat_oauth")) {
            PaymentType="wp";
            sendSuccessMail();


            InitialPaymentFragmentWebChat("pay_cancel", false, true);

        }

    }

    public void sendSuccessMail() {

        Map<String, Object> candidateMap = new HashMap<>();
        candidateMap.put("device_id", CarryParkApplication.getScannedDeviceCode());
        candidateMap.put("payment_gateway", PaymentType);


        String acess_token = GloablMethods.API_HEADER + AppController.getString(CarryParkApplication.getmContext(), "acess_token");
        apiService.sendUserSelectedPaymentMethod(acess_token,candidateMap).enqueue(new Callback<ChangeEmailResponseModel>() {
            @Override
            public void onResponse(Call<ChangeEmailResponseModel> call, Response<ChangeEmailResponseModel> response) {



            }


            @Override
            public void onFailure(Call<ChangeEmailResponseModel> call, Throwable t) {

            }
        });
    }


}

