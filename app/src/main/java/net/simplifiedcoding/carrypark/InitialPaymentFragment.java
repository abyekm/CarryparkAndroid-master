package net.simplifiedcoding.carrypark;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.BluetoothLeScanner;
import android.content.*;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.Network;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.IBinder;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.method.ScrollingMovementMethod;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import com.google.android.gms.common.api.GoogleApiClient;
import net.CarryParkApplication;
import net.others.*;
import net.rest.ApiClient;
import net.rest.ApiInterface;
import net.rest.GloablMethods;
import net.rest.Utils;
import net.rest.global.AppController;
import net.rest.model.*;
import net.ui.BottomNavigation;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

import static android.content.Context.MODE_PRIVATE;


public class InitialPaymentFragment extends Fragment implements ServiceConnection, SerialListener {
    private static final long SCAN_PERIOD = 5000;
    private final static int REQUEST_ENABLE_BT = 1;
    private static final int PERMISSION_REQUEST_COARSE_LOCATION = 1;
    public String DeviceNameBLE = "", DeviceID = "";
    ApiInterface apiService;
    Fragment targetFragment = null;
    SharedPreferences sharedPreferences;
    List<UsedLocker> usedLockersList = new ArrayList<>();
    //Boolean hasFinalPaymentDone = false;
    Boolean isPaymentRequired = false;
    Integer amount = 0;
    TextView tv_caution, tv_free_of_charge;
    LinearLayout ll_pay, ll_camera_layout;
    int maxTime=0;
    String unLockToken;
    CountDownTimer counterForPaymentStartOrSuccessScreen, manageResponse,wakeUpMessageResponse;
    Button btn_scan;
    boolean isCalledApi, isPreviouslyButtonClicked;
    double batteryPower;
    int isConnectionError = 0;
    int isConnectionTimeOut = 0;
    String LastSend="sent:", LastInput="Receive:",TestingCommand="";
    String PreviousInput = "", CurrentInput = "";
    TextView tv_comment, tv_rate_per;
    //BottomNavigationView navigation;
    // to check if we are connected to Network
    boolean isConnected = true;
    // Stops scanning after 5 seconds.
    String TAG = "BLE";
    String RESPONSE_MSG, LogProcess;
    BluetoothAdapter mBluetoothAdapter;
    String timeString="0";
    //// sec
    LinearLayout ll_unlock, ll_lock;
    BluetoothManager btManager;
    BluetoothAdapter btAdapter;
    BluetoothLeScanner btScanner;
    String deviceCode = "asdf";
    Button buttonPayToPaymentSuccess;
    String ratePerHour="0", initialCharge="0", deviceName;
    TextView nameDevice, tvPlace, tvDepositeTime, tvTime, tvDeviceName, tvUsageTime, tv_initial_pay_amount, tv_payment_cancelled;
    Boolean hasInitialPaymentPreviouslyDone = false;
    LinearLayout ll_usageTime, ll_departmentTime, ll_settlement_time;
    String qrCode;
    String isFrom = null, paymentStatus = null;
    boolean isLockerUsed;
    int connect = 0;
    boolean hashValueSendForUnlock = false, checktheResponseisReceived = false,checktheResponseisReceivedAtWakeup = false;
    private int rounded_hours=0;
    private GoogleApiClient client;
    // to check if we are monitoring Network
    private boolean monitoringConnectivity = false;
    private boolean isLockSecurityReadIN, isLockSecurityReadIHashValueUnLock;
    private TextView tvUser, tv_overTime, tv_settlement_charge, tv_start_time, tv_end_time, tv_please_note;
    private long currentTimeOfTimer = ConstantProject.TimeOut15Minitue;
    private boolean isPaymentCancell;
    private TextView receiveText;
    private boolean initialStart = true;
    private Connected connected = Connected.False;
    private boolean iswebchat, isAlreadyCalledUnlockScreen = false;
    private String deviceAddress;
    private SerialService service;
    String timeNotify = "";
    private CountDownTimer  splitresponse;
    private  int count=0;
    private TextView tv_comments;
    private boolean wakeupCall=false;
    int hours =0;
    int minutes = 0;
    int seconds =0;
    String h = "0";
    String m = "0";
    String s = "0";
    int hours_dur = 0;//first element
    int minutes_dur = 0; //second element
    int seconds_dur =0; //hours element
    int duration = 0; //add up our values

    private ConnectivityManager.NetworkCallback connectivityCallback
            = new ConnectivityManager.NetworkCallback() {
        @Override
        public void onAvailable(Network network) {
            isConnected = true;
            //LogUtility.LOGD(TAG, "INTERNET CONNECTED");
        }

        @Override
        public void onLost(Network network) {
            isConnected = false;
            //LogUtility.LOGD(TAG, "INTERNET LOST");
        }
    };

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        String languageToLoad = "en";
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

        return inflater.inflate(R.layout.fragment_initial_payment, null);


    }


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // navigation = getActivity().findViewById(R.id.navigation);
        // navigation.setVisibility(View.VISIBLE);
        sharedPreferences = getActivity().getSharedPreferences("carry_park", MODE_PRIVATE);
        apiService = ApiClient.getClient().create(ApiInterface.class);
        tvUser = (TextView) view.findViewById(R.id.tv_user);

        CarryParkApplication.setIsAliPay(false);
        qrCode = getArguments().getString("qrcode");
        ll_camera_layout = (LinearLayout) view.findViewById(R.id.ll_camera_layout);

        buttonPayToPaymentSuccess = (Button) view.findViewById(R.id.button_pay_to_payment_success);
        nameDevice = (TextView) view.findViewById(R.id.tv_device_name);
        tvPlace = (TextView) view.findViewById(R.id.tv_place);
        tvDepositeTime = (TextView) view.findViewById(R.id.tv_deposite_time);
        tvTime = (TextView) view.findViewById(R.id.tv_time);
        tvPlace.setText(CarryParkApplication.getPlace());
        tvDeviceName = (TextView) view.findViewById(R.id.tv_deviceName);
        tvUsageTime = (TextView) view.findViewById(R.id.tv_usageTime);
        tv_overTime = (TextView) view.findViewById(R.id.tv_overTime);
        tv_settlement_charge = (TextView) view.findViewById(R.id.tv_settlement_charge);
        tv_start_time = (TextView) view.findViewById(R.id.tv_start_time);
        tv_end_time = (TextView) view.findViewById(R.id.tv_end_time);
        ll_lock = (LinearLayout) view.findViewById(R.id.ll_lock);
        ll_unlock = (LinearLayout) view.findViewById(R.id.ll_unlock);
        tv_initial_pay_amount = (TextView) view.findViewById(R.id.tv_initial_pay_amount);
        ll_pay = (LinearLayout) view.findViewById(R.id.ll_pay);
        ll_usageTime = (LinearLayout) view.findViewById(R.id.ll_usageTime);
        ll_departmentTime = (LinearLayout) view.findViewById(R.id.ll_departmentTime);
        ll_settlement_time = (LinearLayout) view.findViewById(R.id.ll_settlement_time);
        tv_caution = (TextView) view.findViewById(R.id.tv_caution);
        btn_scan = (Button) view.findViewById(R.id.btn_scan);
        tv_comment = (TextView) view.findViewById(R.id.tv_comment);
        tv_rate_per = (TextView) view.findViewById(R.id.tv_rate_per);
        tv_please_note = (TextView) view.findViewById(R.id.tv_please_note);
        tv_free_of_charge = (TextView) view.findViewById(R.id.tv_free_of_charge);
        tv_comments = (TextView)view.findViewById(R.id.tv_comments);
        tv_comments.setVisibility(View.GONE);

        tv_payment_cancelled = (TextView) view.findViewById(R.id.tv_payment_cancelled);
        receiveText = view.findViewById(R.id.receive_text);
        receiveText.setTextColor(getResources().getColor(R.color.colorRecieveText)); // set as default color to reduce number of spans
        receiveText.setMovementMethod(ScrollingMovementMethod.getInstance());
        ll_camera_layout.setVisibility(View.VISIBLE);
        deviceAddress = CarryParkApplication.getDeviceAddressBle();
        btn_scan.setVisibility(View.GONE);

        tv_comment.setVisibility(View.GONE);
        tv_comment.setText("Comment:");
       /* if (!SharedPreferenceUtility.isScannedLock())
        {
            callInvoice();
        }
*/
        tvUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               // ((BottomNavigation) getActivity()).showPDFView();

            }
        });


        //REMOVE.....................
      /*ll_camera_layout.setVisibility(View.GONE);
        ((BaseActivity) getActivity()).hideBusyAnimation();
        buttonPayToPaymentSuccess.setVisibility(View.VISIBLE);

      */  //REMOVE.....................

      ((BaseActivity) getActivity()).showBusyAnimation("");

        timeNotify = CarryParkApplication.getTimeLogContent() + "init ";

        //.....................


        if (CarryParkApplication.isIsEnglishLang()) {

            tvUser.setText(getString(R.string.hi) + ", " + CarryParkApplication.getUserName());
        } else if (CarryParkApplication.isIsJapaneaseLang()) {
            //"ようこそ、Carryparkへ
            //○○さん"
            tvUser.setText("ようこそ " + CarryParkApplication.getUserName() + "さん");
        } else {
            tvUser.setText(getString(R.string.hi) + ", " + CarryParkApplication.getCurrentUser());
        }
        ((BottomNavigation) getActivity()).disableScan();


        if (getArguments() != null) {


            if (getArguments().getString("is_from") != null)
                isFrom = getArguments().getString("is_from");
            if (isFrom.equalsIgnoreCase(ConstantProject.CodeScannerFragment)) {
                if (getArguments().getString("device") != null) {
                    deviceAddress = getArguments().getString("device");
                }
            } else {
                deviceAddress = CarryParkApplication.getDeviceAddressBle();
            }
            if (getArguments().getString("payment_status") != null)
                paymentStatus = getArguments().getString("payment_status");

            if (isFrom.equalsIgnoreCase("WebViewFragment") && paymentStatus.equalsIgnoreCase("pay_success")) {
                // buttonPayToPaymentSuccess.setText("Proceed");

                currentTimeOfTimer = getArguments().getLong("current_time_of_timer");


            }

            if (isFrom.equalsIgnoreCase("WebViewFragment") && paymentStatus.equalsIgnoreCase("pay_failure")) {
                // buttonPayToPaymentSuccess.setText("Proceed");
                ll_camera_layout.setVisibility(View.GONE);

                currentTimeOfTimer = getArguments().getLong("current_time_of_timer");

               /* AsyncTask.execute(new Runnable() {
                    @Override
                    public void run() {
                        //TODO your background code
                        confirmPayment();
                    }
                });*/

                //  tv_payment_cancelled.setText(CarryParkApplication.getCurrentContext().getResources().getString(R.string.pay_failed));
                //  tv_payment_cancelled.setVisibility(View.VISIBLE);

                DialogManager.showAlertSingleActionDialog(CarryParkApplication.getCurrentActivity(), CarryParkApplication.getCurrentContext().getResources().getString(R.string.init_pay), "ok", new DialogManager.IUniActionDialogOnClickListener() {
                    @Override
                    public void onPositiveClick() {
                        callUniversalPayment();
                    }
                });

            } else if (isFrom.equalsIgnoreCase("WebViewFragment") && paymentStatus.equalsIgnoreCase("pay_cancel")) {
                currentTimeOfTimer = getArguments().getLong("current_time_of_timer");
                ll_camera_layout.setVisibility(View.GONE);

                //  tv_payment_cancelled.setText(CarryParkApplication.getCurrentContext().getResources().getString(R.string.pay_cancelled));
                //  tv_payment_cancelled.setVisibility(View.VISIBLE);
                buttonPayToPaymentSuccess.setVisibility(View.VISIBLE);
                buttonPayToPaymentSuccess.setText(CarryParkApplication.getCurrentContext().getResources().getString(R.string.pay));


            } else if (getArguments().getBoolean("iswebchat")) {
                ll_camera_layout.setVisibility(View.GONE);

                DialogManager.showAlertSingleActionDialog(CarryParkApplication.getCurrentActivity(), CarryParkApplication.getCurrentContext().getResources().getString(R.string.iswebchat), "ok", new DialogManager.IUniActionDialogOnClickListener() {
                    @Override
                    public void onPositiveClick() {
                        callUniversalPayment();
                    }
                });
            }
            if (getArguments().getBoolean("isAliPay")) {
                ll_camera_layout.setVisibility(View.GONE);

                DialogManager.showAlertSingleActionDialog(CarryParkApplication.getCurrentActivity(), CarryParkApplication.getCurrentContext().getResources().getString(R.string.alipayNotSupport), "ok", new DialogManager.IUniActionDialogOnClickListener() {
                    @Override
                    public void onPositiveClick() {
                        callUniversalPayment();

                    }
                });
            }

        }


        buttonPayToPaymentSuccess.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//REMOVE AT PRODUCTION

                if (!isPreviouslyButtonClicked) {
                    isPreviouslyButtonClicked = true;
                    if (counterForPaymentStartOrSuccessScreen != null) {
                        counterForPaymentStartOrSuccessScreen.cancel();
                        counterForPaymentStartOrSuccessScreen = null;
                    }

                    if (SharedPreferenceUtility.isScannedLock()) {
                        LockSecurityReadIN(RESPONSE_MSG, hasInitialPaymentPreviouslyDone);
                    } else {
                        LockSecurityReadIHashValueUnLock(RESPONSE_MSG, hasInitialPaymentPreviouslyDone);
                    }
                }
              //   sampleCall();


            }
        });


/////////////////////////////////////////  security implementation


        btManager = (BluetoothManager) getActivity().getSystemService(Context.BLUETOOTH_SERVICE);
        btAdapter = btManager.getAdapter();
        btScanner = btAdapter.getBluetoothLeScanner();

        if (btAdapter != null && !btAdapter.isEnabled()) {
            Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableIntent, REQUEST_ENABLE_BT);
        }
/// secu  ////
        // Make sure we have access coarse location enabled, if not, prompt the user to enable it
        if (getActivity().checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            final AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
            builder.setTitle(getResources().getString(R.string.locationacees1));
            builder.setMessage(R.string.locationaccess2);
            builder.setPositiveButton(android.R.string.ok, null);
            builder.setOnDismissListener(new DialogInterface.OnDismissListener() {
                @Override
                public void onDismiss(DialogInterface dialog) {
                    requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, PERMISSION_REQUEST_COARSE_LOCATION);
                }
            });
            builder.show();
        }


        Date today = new Date();
        //SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss a");
        SimpleDateFormat format = new SimpleDateFormat(getResources().getString(R.string.paymentDateFormat));

        SimpleDateFormat day = new SimpleDateFormat("d");
        SimpleDateFormat month = new SimpleDateFormat("M");
        SimpleDateFormat year = new SimpleDateFormat("y");
        SimpleDateFormat amOrpm = new SimpleDateFormat("a");
        SimpleDateFormat hh = new SimpleDateFormat("hh");
        SimpleDateFormat mm = new SimpleDateFormat("mm");


        String dateToStr = format.format(today);


        if (CarryParkApplication.isIsJapaneaseLang()) {

            dateToStr = CommonMethod.dateFormatInJapanease(month.format(today), day.format(today), amOrpm.format(today),
                    year.format(today), hh.format(today), mm.format(today));
        }
        // tvCurrentTime.setText(dateToStr);

        System.out.println(dateToStr);
        tvTime.setText(dateToStr);


        new CountDownTimer(currentTimeOfTimer, 1000) { // adjust the milli seconds here

            public void onTick(long millisUntilFinished) {
                currentTimeOfTimer = millisUntilFinished;
            }

            public void onFinish() {
            }
        }.start();


        if (SharedPreferenceUtility.isScannedLock()) {
            ll_unlock.setVisibility(View.GONE);
            ll_lock.setVisibility(View.VISIBLE);
            if (CarryParkApplication.getLockerDetailsResponseList().getData().isInitial_payment_bypass()) {
                callPaymentBypassTime();

            }
            getLockerDetails(CarryParkApplication.getScannedDeviceCode().replaceAll("\\s+", ""));
            LogProcess = "Lock";

        } else {
            if (sharedPreferences.getString("rate_per_hour", "0") != null  && !sharedPreferences.getString("rate_per_hour", "0").equals("null") )
                ratePerHour = sharedPreferences.getString("rate_per_hour", "0");
            if (sharedPreferences.getString("initial_charge", "0") != null  && !sharedPreferences.getString("initial_charge", "0").equals("null") )
                initialCharge = sharedPreferences.getString("initial_charge", "0");
            if (sharedPreferences.getString("max_time", "0") != null  && !sharedPreferences.getString("max_time", "0").equals("null") )
                maxTime = Integer.parseInt(sharedPreferences.getString("max_time", "0"));
            if (sharedPreferences.getString("device_name", "0") != null)
                deviceName = sharedPreferences.getString("device_name", "0");
            tvDeviceName.setText(deviceName);
            getLockerDetails(CarryParkApplication.getScannedDeviceCode().replaceAll("\\s+", ""));

            callUnlockRequestAPI(CarryParkApplication.getScannedDeviceCode());
            LogProcess = "unLock";
            ll_unlock.setVisibility(View.VISIBLE);
            ll_lock.setVisibility(View.GONE);


        }

        if (SharedPreferenceUtility.isScannedLock()) {
            if (CarryParkApplication.isHasInitialPaymentPreviouslyDone()) {
                ll_pay.setVisibility(View.GONE);
                buttonPayToPaymentSuccess.setText(CarryParkApplication.getCurrentContext().getResources().getString(R.string.proceed));
            }
        }

    }


    /* private void confirmPayment() {

         Map<String, Object> candidateMap = new HashMap<>();
         candidateMap.put("device_id",sharedPreferences.getString("device_id",null));
         if(sharedPreferences.getString("selected_lang",null)!=null){
             if (CarryParkApplication.isIsJapaneaseLang())
                 candidateMap.put("lang_id", ConstantProject.forJapaneaseResponse);

         }else{
             candidateMap.put("lang_id", "en");
         }
         String acess_token = GloablMethods.API_HEADER+ AppController.getString(getActivity(), "acess_token");
         Log.e("acess_token",acess_token);
         apiService.paymentConfirmationAPI(acess_token,candidateMap).enqueue(new Callback<PaymentConfirmationApiResponse>() {
             @Override
             public void onResponse(Call<PaymentConfirmationApiResponse> call, Response<PaymentConfirmationApiResponse> response) {
                 Log.e(AppController.TAG,"------>" + "Response");
                 Log.e(AppController.TAG,response.toString());
                 Log.e("responseCode", String.valueOf(response.code()));

                 if(response.code() == 200){
                     Boolean status = response.body().getSuccess();
                     if(status == true){
                         String token = response.body().getData().getToken();

                         Log.d("payment_flag",response.body().getData().getPayment().toString());
                         if(response.body().getData().getPayment() == false){
                             hasFinalPaymentDone = false;
                             String msg = response.body().getMessage();
                         }else{
                             hasFinalPaymentDone = true;
                         }
                         callUnlockRequestAPI(CarryParkApplication.getScannedDeviceCode());

                     }else{
                         String device_id = sharedPreferences.getString("device_id",null);
                         callUnlockRequestAPI(CarryParkApplication.getScannedDeviceCode());
                     }
                 }else if(response.code() == 404){
                 }


             }

             @Override
             public void onFailure(Call<PaymentConfirmationApiResponse> call, Throwable t) {
                 // progressDialog.dismiss();
                 if(!Utils.isNetworkConnectionAvailable(getContext())){
                 }else {
                 }
             }
         });
     }*/
    private void callUnlockRequestAPI(String deviceId) {

        Map<String, Object> candidateMap = new HashMap<>();

        candidateMap.put("device_id", deviceId);
        candidateMap.put("lock_status", 1);
        if (SharedPreferenceUtility.isJapanease()) {
            candidateMap.put("lang_id", ConstantProject.forJapaneaseResponse);

        } else if (SharedPreferenceUtility.isEnglish()) {
            candidateMap.put("lang_id", "en");
        } else if (SharedPreferenceUtility.isKorean()) {
            candidateMap.put("lang_id", ConstantProject.forKoreanResponse);
        } else if (SharedPreferenceUtility.isChinease()) {
            candidateMap.put("lang_id", ConstantProject.forChineaseResponse);
        }
        String acess_token = GloablMethods.API_HEADER + AppController.getString(getActivity(), "acess_token");
        apiService.UnlockingRequestAPI(acess_token, candidateMap).enqueue(new Callback<UnLockRequestApiResponse>() {
            @Override
            public void onResponse(Call<UnLockRequestApiResponse> call, Response<UnLockRequestApiResponse> response) {

                if (response.code() == 200 && response.body().getSuccess() == true) {

                    if (response.body().getData() != null) {
                        isPaymentRequired = response.body().getData().getPayment_require();
                        CarryParkApplication.setIsPaymentRequired(isPaymentRequired);

                    }


                    if (CarryParkApplication.IsPaymentByPassingNoRequired()) {

                        ll_pay.setVisibility(View.VISIBLE);
                        tv_free_of_charge.setVisibility(View.GONE);
                        ll_usageTime.setVisibility(View.VISIBLE);
                        ll_departmentTime.setVisibility(View.VISIBLE);
                        ll_settlement_time.setVisibility(View.VISIBLE);
                        buttonPayToPaymentSuccess.setText(CarryParkApplication.getCurrentContext().getResources().getString(R.string.pay));
                    } else {


                        ll_pay.setVisibility(View.GONE);
                        tv_free_of_charge.setVisibility(View.VISIBLE);
                        ll_usageTime.setVisibility(View.GONE);
                        ll_departmentTime.setVisibility(View.GONE);
                        ll_settlement_time.setVisibility(View.GONE);
                        buttonPayToPaymentSuccess.setText(CarryParkApplication.getCurrentContext().getResources().getString(R.string.proceed));


                    }

                     if (response !=null && response.body()!=null && response.body().getData().getAmount() != null) {
                        amount = response.body().getData().getAmount();


                    }
                    if (response !=null && response.body()!=null && response.body().getData().getOvertime() != null) {

                        if (response.body().getData().getOvertime()!=null)
                        {
                            hours = response.body().getData().getOvertime() / 3600;
                            minutes = (response.body().getData().getOvertime() % 3600) / 60;
                            seconds = response.body().getData().getOvertime() % 60;
                        }

                        if (minutes > 0 || seconds > 0) {
                            rounded_hours = hours + 1;
                        }

                         timeString = String.format("%02d:%02d:%02d", hours, minutes, seconds);
                        if (!timeString.equals("0") &&  timeString.contains(":"))
                        {
                            String time = timeString; //mm:ss
                            String[] units = time.split(":");
                            //will break the string up into an array

                            if (units[0]!=null)
                             hours_dur = Integer.parseInt(units[0]);//first element
                            if (units[1]!=null)
                                minutes_dur = Integer.parseInt(units[1]); //second element
                            if (units[2]!=null)
                                seconds_dur = Integer.parseInt(units[2]); //hours element
                                duration = 60 * minutes + seconds; //add up our values
                             h = String.valueOf(hours_dur);
                             m = String.valueOf(minutes_dur);
                             s = String.valueOf(seconds_dur);
                        }



                        if (hours > 0) {
                            if (SharedPreferenceUtility.isEnglish()) {
                                tv_overTime.setText("" + h + " h" + m + " m");

                            }
                            if (SharedPreferenceUtility.isJapanease()) {
                                tv_overTime.setText("" + h + ConstantProject.Jikan + m + ConstantProject.Pun);

                            }

                        } else if (minutes > 0) {
                            if (SharedPreferenceUtility.isEnglish()) {
                                tv_overTime.setText("" + m + " m");

                            }
                            if (SharedPreferenceUtility.isJapanease()) {
                                tv_overTime.setText("" + m + ConstantProject.Pun);

                            }

                        } else {
                            if (SharedPreferenceUtility.isEnglish()) {
                                tv_overTime.setText(m + " m");

                            }
                            if (SharedPreferenceUtility.isJapanease()) {
                                tv_overTime.setText(m + ConstantProject.Pun);

                            }
                        }
                    }
                    String bal_amount_frmAPI = String.valueOf(response.body().getData().getAmount());
                    // Date date_start = null,date_end = null; //2020-05-20T04:47:15.000000Z
                    //tinu                  // SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSSSS");
                    SimpleDateFormat sdf = null;
                    SimpleDateFormat day = null;
                    SimpleDateFormat month = null;
                    SimpleDateFormat year = null;
                    SimpleDateFormat amOrpm = null;
                    SimpleDateFormat hh = null;
                    SimpleDateFormat mm = null;
                    try {
                        sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSSSSX", Locale.ENGLISH);
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
                        Date date_start;
                        if (response.body().getData().getStartDate() != null) {
                            date_start = sdf.parse(response.body().getData().getStartDate());
                            //SimpleDateFormat outputFormat = new SimpleDateFormat("yyyy-MMM-d,hh:mm a", Locale.ENGLISH);
                            SimpleDateFormat outputFormat = new SimpleDateFormat(getContext().getResources().getString(R.string.paymentDateFormat), Locale.ENGLISH);
                            // assuming a timezone in India


                            //outputFormat.setTimeZone(TimeZone.getTimeZone("Asia/Kolkata"));
                            outputFormat.setTimeZone(TimeZone.getDefault());

                            if (CarryParkApplication.isIsJapaneaseLang()) {

                                String dateToStr = CommonMethod.dateFormatInJapanease(month.format(date_start), day.format(date_start), amOrpm.format(date_start),
                                        year.format(date_start), hh.format(date_start), mm.format(date_start));
                                tv_start_time.setText(dateToStr);

                            } else {
                                tv_start_time.setText(outputFormat.format(date_start).toString());

                            }

                        }

                        /////////////

                        ////////////
                        //System.out.println(outputFormat.format(date));
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }

                    try {
                        Date date_end;
                        if (response!=null && response.body()!=null && response.body().getData().getEndDate() != null) {
                            date_end = sdf.parse(response.body().getData().getEndDate());
                            //SimpleDateFormat outputFormat = new SimpleDateFormat("yyyy-MMM-d,hh:mm a", Locale.ENGLISH);
                            SimpleDateFormat outputFormat = new SimpleDateFormat(getContext().getResources().getString(R.string.paymentDateFormat), Locale.ENGLISH);
                            // assuming a timezone in India
                            // outputFormat.setTimeZone(TimeZone.getTimeZone("Asia/Kolkata"));
                            outputFormat.setTimeZone(TimeZone.getDefault());
                            //System.out.println(outputFormat.format(date));


                            if (CarryParkApplication.isIsJapaneaseLang()) {

                                String dateToStr = CommonMethod.dateFormatInJapanease(month.format(date_end), day.format(date_end), amOrpm.format(date_end),
                                        year.format(date_end), hh.format(date_end), mm.format(date_end));
                                tv_end_time.setText(dateToStr);

                            } else {
                                tv_end_time.setText(outputFormat.format(date_end).toString());

                            }
                        }

                    } catch (ParseException e) {
                        e.printStackTrace();
                    }

                    int hours = 0, minutes = 0, seconds = 0;
                    if (response.body().getData().getDuration() != null) {
                        hours = response.body().getData().getDuration() / 3600;
                        seconds = response.body().getData().getDuration() % 60;
                        minutes = (response.body().getData().getDuration() % 3600) / 60;
                    }


                    if (minutes > 0 || seconds > 0) {
                        rounded_hours = hours + 1;
                    }

                    String timeString = String.format("%02d:%02d:%02d", hours, minutes, seconds);

                    String time = timeString; //mm:ss
                    String[] units = time.split(":"); //will break the string up into an array
                    if (units[0]!=null)
                     hours_dur = Integer.parseInt(units[0]);//first element
                    if (units[1]!=null)
                        minutes_dur = Integer.parseInt(units[1]); //second element
                    if (units[2]!=null)
                        seconds_dur = Integer.parseInt(units[2]); //hours element
                        duration = 60 * minutes + seconds; //add up our values
                     h = String.valueOf(hours_dur);
                     m = String.valueOf(minutes_dur);
                     s = String.valueOf(seconds_dur) + " s ";
                    /*if (hours_dur>0)
                    {
                        tvUsageTime.setText(h + m );
                    }
                    else {
                        tvUsageTime.setText( m );
                    }*/
                    if (hours > 0) {
                        if (SharedPreferenceUtility.isEnglish()) {
                            tvUsageTime.setText("" + h + " h" + m + " m");

                        }
                        if (SharedPreferenceUtility.isJapanease()) {
                            tvUsageTime.setText("" + h + ConstantProject.Jikan + m + ConstantProject.Pun);

                        }

                    } else if (minutes > 0) {
                        if (SharedPreferenceUtility.isEnglish()) {
                            tvUsageTime.setText("" + m + " m");

                        }
                        if (SharedPreferenceUtility.isJapanease()) {
                            tvUsageTime.setText("" + m + ConstantProject.Pun);

                        }

                    }
                    int ratePerHr=0;

                    try {
                        if (sharedPreferences.getString("rate_per_hour", "0")!=null && !sharedPreferences.getString("rate_per_hour", "0").equals("null") && !sharedPreferences.getString("rate_per_hour", "0").isEmpty() )

                        {
                            ratePerHr = Integer.parseInt(sharedPreferences.getString("rate_per_hour", "0"));

                        }
                    } catch (NumberFormatException e) {
                        e.printStackTrace();
                    }
                    int initialCharge = 0;
                    try {
                        if (sharedPreferences.getString("initial_charge", "0")!=null  && !sharedPreferences.getString("initial_charge", "0").isEmpty() && !sharedPreferences.getString("initial_charge", "0").equals("null") )

                        {
                            initialCharge = Integer.parseInt(sharedPreferences.getString("initial_charge", "0"));

                        }
                    } catch (NumberFormatException e) {
                        e.printStackTrace();
                    }
                    int hoursUsed = 0;

                    try {
                        if (rounded_hours <= maxTime) {
                            hoursUsed = rounded_hours - 1;
                        } else if (rounded_hours > maxTime) {
                            hoursUsed = maxTime - 1;
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    String usedHours = String.valueOf(hoursUsed);
                    //  tvHourUsage.setText(usedHours+" H");
                    int amountUsage = ratePerHr * hoursUsed;
                    String usageAmount = String.valueOf(amountUsage);
                    //  tvUsageAmt.setText("¥ " + usageAmount);


                    // row
                    // tvUsageAmtTotal.setText("¥ " + usageAmount);
                    String rounded_hrs = String.valueOf(rounded_hours);
                    int amtTotal = amountUsage + initialCharge;
                    String totalAmt = String.valueOf(amtTotal);
                    //tvTotalAmtTotal.setText("¥ " + totalAmt);
                    int hrs_used = Integer.parseInt(rounded_hrs);

                    if (SharedPreferenceUtility.isEnglish()) {
                        tv_settlement_charge.setText(bal_amount_frmAPI + " " + "yen");

                    } else {
                        tv_settlement_charge.setText(bal_amount_frmAPI + " " + ConstantProject.YenSymbol);

                    }

                    if (isPaymentRequired.equals(false))
                        buttonPayToPaymentSuccess.setText(R.string.takeOutBag);
                    else
                        buttonPayToPaymentSuccess.setText(getResources().getString(R.string.pay));

                    unLockToken = response.body().getData().getToken();

                    CarryParkApplication.setUnLockToken(unLockToken);

                } else if (response.code() == 404) {
                    try {
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    //String msg = response.body().getMessage();

                }
            }

            @Override
            public void onFailure(Call<UnLockRequestApiResponse> call, Throwable t) {
                try {
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (!Utils.isNetworkConnectionAvailable(CarryParkApplication.getCurrentContext())) {
                    DialogManager.showAlertSingleActionDialog(CarryParkApplication.getCurrentActivity(), CarryParkApplication.getCurrentContext().getResources().getString(R.string.OperfailedExtnd), "ok", new DialogManager.IUniActionDialogOnClickListener() {
                        @Override
                        public void onPositiveClick() {

                        }


                    });
                } else {
                    DialogManager.showAlertSingleActionDialog(CarryParkApplication.getCurrentActivity(), t.toString(), "ok", new DialogManager.IUniActionDialogOnClickListener() {
                        @Override
                        public void onPositiveClick() {

                        }


                    });
                }
            }
        });
    }

    private void getLockerDetails(final String scannedDeviceCode) {


        if (CarryParkApplication.getLockerDetailsResponseList().getData() != null) {

            if (CarryParkApplication.getLockerDetailsResponseList().getData() != null) {

                if (CarryParkApplication.getLockerDetailsResponseList().getData().getPayment_require() != null) {

                    if (CarryParkApplication.getLockerDetailsResponseList().getData().getPayment_require()) {

                        CarryParkApplication.setIsPaymentByPassingNotRequired(true);
                        ll_pay.setVisibility(View.VISIBLE);
                        tv_free_of_charge.setVisibility(View.GONE);
                        ll_usageTime.setVisibility(View.VISIBLE);
                        ll_departmentTime.setVisibility(View.VISIBLE);
                        ll_settlement_time.setVisibility(View.VISIBLE);
                        buttonPayToPaymentSuccess.setText(CarryParkApplication.getCurrentContext().getResources().getString(R.string.pay));

                    } else {
                        ll_pay.setVisibility(View.GONE);
                        tv_free_of_charge.setVisibility(View.VISIBLE);

                        ll_usageTime.setVisibility(View.GONE);
                        ll_departmentTime.setVisibility(View.GONE);
                        ll_settlement_time.setVisibility(View.GONE);
                        buttonPayToPaymentSuccess.setText(CarryParkApplication.getCurrentContext().getResources().getString(R.string.proceed));

                        CarryParkApplication.setIsPaymentByPassingNotRequired(false);

                    }
                }
                if (CarryParkApplication.getLockerDetailsResponseList().getData().getWorks_24_hours() == 1) {
                    hideCautionMessage();
                    tvDepositeTime.setText(CarryParkApplication.getCurrentContext().getResources().getString(R.string.hrs));

                } else if (CarryParkApplication.getLockerDetailsResponseList().getData().getStart_time() != null && !CarryParkApplication.getLockerDetailsResponseList().getData().getStart_time().isEmpty() && CarryParkApplication.getLockerDetailsResponseList().getData().getEnd_time() != null && !CarryParkApplication.getLockerDetailsResponseList().getData().getEnd_time().isEmpty()) {

                    /////////////

                    SimpleDateFormat sdf = null;
                    SimpleDateFormat day = null;
                    SimpleDateFormat month = null;
                    SimpleDateFormat year = null;
                    SimpleDateFormat amOrpm = null;
                    SimpleDateFormat hh = null;
                    SimpleDateFormat mm = null;
                    try {
                        sdf = new SimpleDateFormat("HH:mm:ss", Locale.ENGLISH);
                        amOrpm = new SimpleDateFormat("a");
                        hh = new SimpleDateFormat("hh");
                        mm = new SimpleDateFormat("mm");
                    } catch (Exception e) {
                        e.printStackTrace();
                        sdf = new SimpleDateFormat("HH:mm:ss.SSS");
                        day = new SimpleDateFormat("d");
                        month = new SimpleDateFormat("M");
                        year = new SimpleDateFormat("y");
                        amOrpm = new SimpleDateFormat("a");
                        hh = new SimpleDateFormat("hh");
                        mm = new SimpleDateFormat("mm");
                    }
                    Date date_start = null, date_end = null;
                    String startDate, endDate;

                    Date time = null;
                    String timeOut;
                    try {
                        DateFormat timeFormat = new SimpleDateFormat("HH:mm:ss");

                        if (CarryParkApplication.getLockerDetailsResponseList().getData().getStart_time() != null)
                            date_start = timeFormat.parse(CarryParkApplication.getLockerDetailsResponseList().getData().getStart_time());
                        if (CarryParkApplication.getLockerDetailsResponseList().getData().getEnd_time() != null)
                            date_end = timeFormat.parse(CarryParkApplication.getLockerDetailsResponseList().getData().getEnd_time());


                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    if (SharedPreferenceUtility.isJapanease()) {


                        tvDepositeTime.setText(CommonMethod.timeFormatInJapanease(amOrpm.format(date_start), hh.format(date_start), mm.format(date_start)) + " ~ " + CommonMethod.timeFormatInJapanease(amOrpm.format(date_end), hh.format(date_end), mm.format(date_end)));
                        tv_caution.setText(CommonMethod.timeFormatInJapanease(amOrpm.format(date_end), hh.format(date_end), mm.format(date_end)) + CarryParkApplication.getCurrentActivity().getString(R.string.plsNoteDtl));

                    } else {

                        tvDepositeTime.setText(CommonMethod.timeFormatInEnglish(amOrpm.format(date_start), hh.format(date_start), mm.format(date_start)) + " - " + CommonMethod.timeFormatInEnglish(amOrpm.format(date_end), hh.format(date_end), mm.format(date_end)));
                        tv_caution.setText(CarryParkApplication.getCurrentActivity().getString(R.string.plsNoteDtl) + CommonMethod.timeFormatInEnglish(amOrpm.format(date_end), hh.format(date_end), mm.format(date_end)));

                    }


                    ////////////////


                }
                tvDeviceName.setText(CarryParkApplication.getLockerDetailsResponseList().getData().getDeviceName());
                /*if (SharedPreferenceUtility.isEnglish()) {
                    tv_initial_pay_amount.setText(getResources().getString(R.string.basic_fee) + " " + CarryParkApplication.getLockerDetailsResponseList().getData().getInitialCharges() + "\t" + " yen");

                } else {
                    tv_initial_pay_amount.setText(getResources().getString(R.string.basic_fee) + " " + CarryParkApplication.getLockerDetailsResponseList().getData().getInitialCharges() + " 円");

                }*/
                if (CarryParkApplication.getLockerDetailsResponseList().getData().getPayment_require()) {
                    if (SharedPreferenceUtility.isEnglish()) {
                        //Basic charge/2 hours/200 yen
                        if (CarryParkApplication.getLockerDetailsResponseList().getData().getPayment_require()) {
                            if (CarryParkApplication.getLockerDetailsResponseList().getData().getInitial_hours() == 1) {
                                tv_initial_pay_amount.setText("Basic charge／" + CarryParkApplication.getLockerDetailsResponseList().getData().getInitial_hours() + " min" + "／" + CarryParkApplication.getLockerDetailsResponseList().getData().getInitial_charges() + " yen");

                            } else {
                                tv_initial_pay_amount.setText("Basic charge／" + CarryParkApplication.getLockerDetailsResponseList().getData().getInitial_hours() + " min" + "／" + CarryParkApplication.getLockerDetailsResponseList().getData().getInitial_charges() + " yen");

                            }
                        }
                        //Excess charge/1 hour/ 100 yen
                        if (CarryParkApplication.getLockerDetailsResponseList().getData().isFinal_payment())
                            tv_rate_per.setText("Excess charge／"+CarryParkApplication.getLockerDetailsResponseList().getData().getExt_unit_time()+" min／ " + CarryParkApplication.getLockerDetailsResponseList().getData().getRate_per_hour() + " yen");

                    } else if (SharedPreferenceUtility.isJapanease()) {
                        //基本料金／2時間／200円
                        String onehr = ""+CarryParkApplication.getLockerDetailsResponseList().getData().getExt_unit_time();
                        if (CarryParkApplication.getLockerDetailsResponseList().getData().getPayment_require())
                            tv_initial_pay_amount.setText("基本料金／" + CarryParkApplication.getLockerDetailsResponseList().getData().getInitial_hours() + "分" + "／" + CarryParkApplication.getLockerDetailsResponseList().getData().getInitial_charges() + "円");
                        //超過料金/１時間/100円
                        if (CarryParkApplication.getLockerDetailsResponseList().getData().isFinal_payment())
                            tv_rate_per.setText("延長料金／" + onehr + "分／ " + CarryParkApplication.getLockerDetailsResponseList().getData().getRate_per_hour() + "円");


                    } else if (SharedPreferenceUtility.isChinease()) {
                        //"基本费用/ 2小时/ 200日元"
                        if (CarryParkApplication.getLockerDetailsResponseList().getData().getPayment_require())
                            tv_initial_pay_amount.setText("基本费用／" + CarryParkApplication.getLockerDetailsResponseList().getData().getInitial_hours() + " 分钟" + "/" + CarryParkApplication.getLockerDetailsResponseList().getData().getInitial_charges() + " 日元");
                        //超额收费/ 1小时/ 100日元
                        if (CarryParkApplication.getLockerDetailsResponseList().getData().isFinal_payment())
                            tv_rate_per.setText("超额收费／ "+CarryParkApplication.getLockerDetailsResponseList().getData().getExt_unit_time()+"分钟／ " + CarryParkApplication.getLockerDetailsResponseList().getData().getRate_per_hour() + " 日元");


                    } else if (SharedPreferenceUtility.isKorean()) {
                        //""기본 요금 / 2 시간 / 200 엔""
                        if (CarryParkApplication.getLockerDetailsResponseList().getData().getPayment_require())
                            tv_initial_pay_amount.setText("기본 요금／" + CarryParkApplication.getLockerDetailsResponseList().getData().getInitial_hours() + " 분" + "／" + CarryParkApplication.getLockerDetailsResponseList().getData().getInitial_charges() + " 엔");
                        if (CarryParkApplication.getLockerDetailsResponseList().getData().isFinal_payment())
                            tv_rate_per.setText("기본 요금／"+ CarryParkApplication.getLockerDetailsResponseList().getData().getExt_unit_time()+"분／" + CarryParkApplication.getLockerDetailsResponseList().getData().getRate_per_hour() + " 엔");


                    }
                } else {
                    ll_pay.setVisibility(View.GONE);
                    tv_free_of_charge.setVisibility(View.VISIBLE);

                }


            }
            if (CarryParkApplication.getLockerDetailsResponseList().getData().getDeviceName() != null && !CarryParkApplication.getLockerDetailsResponseList().getData().getDeviceName().isEmpty())

                DeviceNameBLE = CarryParkApplication.getLockerDetailsResponseList().getData().getDeviceName();
            DeviceID = CarryParkApplication.getLockerDetailsResponseList().getData().getDeviceId();
            nameDevice.setText(CarryParkApplication.getLockerDetailsResponseList().getData().getDeviceName());


            if (CarryParkApplication.getLockerDetailsResponseList().getData().getPresentStatus().equalsIgnoreCase("locked")) {

            } else {
                for (int i = 0; i < CarryParkApplication.getUsedLockerList().size(); i++) {
                    if (CarryParkApplication.getUsedLockerList().get(i).getDeviceId().equalsIgnoreCase(CarryParkApplication.getLockerDetailsResponseList().getData().getDeviceId())) {

                        if (CarryParkApplication.getLockerDetailsResponseList().getData().getPresentStatus().equalsIgnoreCase("unlocked")) {
                            hasInitialPaymentPreviouslyDone = true;
                        }
                    }
                }
            }


            Boolean status = CarryParkApplication.getLockerDetailsResponseList().getSuccess();
            if (status == true && CarryParkApplication.getLockerDetailsResponseList().getData() != null) {


                String location = CarryParkApplication.getLockerDetailsResponseList().getData().getLocation();
                String deviceId = CarryParkApplication.getLockerDetailsResponseList().getData().getDeviceId();
                String deviceName = CarryParkApplication.getLockerDetailsResponseList().getData().getDeviceName();
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("device_id", CarryParkApplication.getLockerDetailsResponseList().getData().getDeviceId());
                if (CarryParkApplication.getLockerDetailsResponseList().getData().getRate_per_hour()!=null)
                editor.putString("rate_per_hour", String.valueOf(CarryParkApplication.getLockerDetailsResponseList().getData().getRate_per_hour()));
                if (CarryParkApplication.getLockerDetailsResponseList().getData().getInitial_charges()!=null)
                    editor.putString("initial_charge", String.valueOf(CarryParkApplication.getLockerDetailsResponseList().getData().getInitial_charges()));
                editor.putString("device_name", deviceName);

                        /*if(response.body().)
                        editor.putString("isLocked", "UNLOCKED");*/
                //setting present status(Lock or Unlock) to sharedPref
                String presentStatus = CarryParkApplication.getLockerDetailsResponseList().getData().getPresentStatus();
                if (presentStatus.equalsIgnoreCase("unlocked")) {
                    editor.putString("isLocked", "UNLOCKED");
                    editor.apply();
                } else {

                    editor.putString("isLocked", "LOCKED");
                    editor.apply();
                }
                if (CarryParkApplication.getLockerDetailsResponseList().getData().getLockInfo()!=null)
                    editor.putString("lock_info", CarryParkApplication.getLockerDetailsResponseList().getData().getLockInfo());
                if (CarryParkApplication.getLockerDetailsResponseList().getData().getUnlockInfo()!=null)
                    editor.putString("unlock_info", CarryParkApplication.getLockerDetailsResponseList().getData().getUnlockInfo());
                if (CarryParkApplication.getLockerDetailsResponseList().getData().getSuccessInfo()!=null)
                    editor.putString("additional_info", CarryParkApplication.getLockerDetailsResponseList().getData().getSuccessInfo());
                if (CarryParkApplication.getLockerDetailsResponseList().getData().getMaxTime()!=null)
                    editor.putString("max_time", CarryParkApplication.getLockerDetailsResponseList().getData().getMaxTime());
                editor.apply();

                String ratePerHour = String.valueOf(CarryParkApplication.getLockerDetailsResponseList().getData().getRate_per_hour());
                // tvLocationContent.setText(location);

                // tvDeviceContent.setText(deviceName);
                if (hasInitialPaymentPreviouslyDone) {

                    SharedPreferenceUtility.saveScannedDeviceInfo_isLock(true);
                    buttonPayToPaymentSuccess.setText(CarryParkApplication.getCurrentContext().getResources().getString(R.string.proceed));

                } else if (CarryParkApplication.getLockerDetailsResponseList().getData().getPresentStatus().equalsIgnoreCase("locked")) {
                    SharedPreferenceUtility.saveScannedDeviceInfo_isLock(true);
                } else if (CarryParkApplication.getLockerDetailsResponseList().getData().getPresentStatus().equalsIgnoreCase("unlocked") && !hasInitialPaymentPreviouslyDone) {
                    SharedPreferenceUtility.saveScannedDeviceInfo_isLock(true);
                }


            } else if (status == false) {
                String msg = CarryParkApplication.getLockerDetailsResponseList().getMessage();
                disconnect();
                DialogManager.showAlertSingleActionDialog(CarryParkApplication.getCurrentActivity(), msg, "ok", new DialogManager.IUniActionDialogOnClickListener() {
                    @Override
                    public void onPositiveClick() {
                        Intent intent = new Intent(CarryParkApplication.getCurrentContext(), BottomNavigation.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        intent.putExtra("page", ConstantProject.SplashActivity);
                        startActivity(intent);

                    }
                });

            }


        }


    }

    public void replaceFragment(Fragment someFragment, Fragment currentFragment) {
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, someFragment);
        transaction.addToBackStack(null);
        transaction.commit();


    }

    public void replaceFragment(Fragment someFragment) {

        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, someFragment);
        transaction.addToBackStack(null);
        transaction.commit();

    }

    public void replaceFragment(Fragment someFragment, String scanned_device_code) {
        //Put the value
        InitialPaymentFragment ldf = new InitialPaymentFragment();
        Bundle args = new Bundle();
        args.putString("scannedDeviceCode", scanned_device_code);
        args.putString("isFrom", "scan_qrcode_fragment");
        args.putBoolean("hasInitialPaymentPreviouslyDone", hasInitialPaymentPreviouslyDone);
        ldf.setArguments(args);


        //Inflate the fragment
        getFragmentManager().beginTransaction().add(R.id.fragment_container, ldf).commit();
    }



    public int calculateAmount(int hour, int initialAmount, int RatePerHurs) {

        int amt = initialAmount + ((hour - 1) * RatePerHurs);
        return amt;

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        // ignore requestCode as there is only one in this fragment
        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
        } else {
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setTitle(getText(R.string.location_denied_title));
            builder.setMessage(getText(R.string.location_denied_message));
            builder.setPositiveButton(android.R.string.ok, null);
            builder.show();
        }
    }


    //////////////////////////// Security Implementation

    public void postErrorLog(final String ErrorCode, final String message, String givenInputs, int ng, String battery, String eng, String jap) {

        Map<String, Object> candidateMap = new HashMap<>();

        candidateMap.put("device_id", CarryParkApplication.getScannedDeviceCode());
        candidateMap.put("error_code", ErrorCode);
        candidateMap.put("comments", givenInputs);
        candidateMap.put("error_message", message);
        candidateMap.put("battery_level", batteryPower);
        candidateMap.put("error_message_en", eng);
        candidateMap.put("error_message_jp", jap);
        candidateMap.put("ng", 1);
        String acess_token = GloablMethods.API_HEADER + AppController.getString(CarryParkApplication.getmContext(), "acess_token");
        apiService =
                ApiClient.getClient().create(ApiInterface.class);
        apiService.postErrorLog(acess_token, candidateMap).enqueue(new Callback<HashApiResponse>() {
            @Override
            public void onResponse(Call<HashApiResponse> call, Response<HashApiResponse> response) {


            }


            @Override
            public void onFailure(Call<HashApiResponse> call, Throwable t) {
                if (!Utils.isNetworkConnectionAvailable(CarryParkApplication.getCurrentContext())) {
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

    @Override
    public void onStart() {
        super.onStart();
        if (service != null)
            service.attach(this);
        else
            getActivity().startService(new Intent(getActivity(), SerialService.class)); // prevents service destroy on unbind from recreated activity caused by orientation change

    }

    @Override
    public void onStop() {

        if (service != null && !getActivity().isChangingConfigurations())
            service.detach();

        super.onStop();
    }

    @SuppressWarnings("deprecation")
    // onAttach(context) was added with API 23. onAttach(activity) works for all API versions

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        getActivity().bindService(new Intent(getActivity(), SerialService.class), this, Context.BIND_AUTO_CREATE);
    }

    @Override
    public void onDetach() {


        try {
            getActivity().unbindService(this);
        } catch (Exception ignored) {
        }

        super.onDetach();
    }

    @Override
    public void onResume() {
        super.onResume();

//REMOVE
       if (initialStart && service != null) {
            initialStart = false;
            if (connected != Connected.True)
                getActivity().runOnUiThread(this::connect);
            else {
                //  getLockerDetailsFromScanner(CarryParkApplication.getScannedDeviceCode());

            }
        }
        if (CarryParkApplication.isIsAliPay()) {
            DialogManager.showAlertSingleActionDialog(CarryParkApplication.getCurrentActivity(), getResources().getString(R.string.alipayNotSupport), "ok", new DialogManager.IUniActionDialogOnClickListener() {
                @Override
                public void onPositiveClick() {
                }
            });
            CarryParkApplication.setIsAliPay(false);

        }
    }

    @Override
    public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
//REMOVE

    service = ((SerialService.SerialBinder) iBinder).getService();
        service.attach(this);
        CarryParkApplication.setService(service);
        if (initialStart && isResumed()) {
            initialStart = false;
            if (connected != Connected.True)
                getActivity().runOnUiThread(this::connect);
            else {
                // getLockerDetailsFromScanner(CarryParkApplication.getScannedDeviceCode());

            }
        }
    }

    @Override
    public void onServiceDisconnected(ComponentName componentName) {
        service = null;
    }

    @Override
    public void onSerialConnect() {

        status("connected");
        connected = Connected.True;
        timeNotify = timeNotify + "connected " + DateFormat.getTimeInstance().format(new Date());


        if (!isCalledApi) {

            isCalledApi = true;
            getLockerDetailsFromScanner(CarryParkApplication.getScannedDeviceCode());


        }
        CountDownForPayment();


    }

    @Override
    public void onSerialConnectError(Exception e) {

            disconnect();
        String msg;
        ((BaseActivity) getActivity()).hideBusyAnimation();
        ((BaseActivity) getActivity()).hideBusyAnimation();
        ((BaseActivity) getActivity()).hideBusyAnimation();
        if (SharedPreferenceUtility.isJapanease())
        {
            msg=ConstantProject.errorLogDeviceNotDiscurvedJADisplay;
        }
        else {
            msg=ConstantProject.errorLogDeviceNotDiscurvedENDisplay;
        }

        postErrorLog("105", msg, "", 1,
                "", ConstantProject.errorLogDeviceNotDiscurvedENServer, ConstantProject.errorLogDeviceNotDiscurvedJAServer);
        Activity activity = getActivity();
        if (activity != null && isAdded()) {
            Intent intent = new Intent(CarryParkApplication.getCurrentContext(), BottomNavigation.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            intent.putExtra("page", ConstantProject.SplashActivity);
            startActivity(intent);
        }

 /*      if (isConnectionError<4 && !CarryParkApplication.isIsConnectedWithSavedAddress()) {
           timeNotify=timeNotify+e.toString()+DateFormat.getTimeInstance().format(new Date());

           disconnect();
            getActivity().runOnUiThread(this::connect);
            isConnectionError++;
        } else {
            if (CarryParkApplication.isIsConnectedWithSavedAddress())
                sentAppLog("BLE device not discovered or not in range",
                        "BLE device not discovered or not in range."+e.toString(),"BLEデバイスが見つからないか範囲外です。");
           disconnect();
            ((BaseActivity) getActivity()).hideBusyAnimation();
            disconnect();
            DialogManager.showRescanDialogue(new DialogManager.IMultiActionDialogOnClickListener() {
                @Override
                public void onPositiveClick() {
                    ((BaseActivity) getActivity()).hideBusyAnimation();
                    ((BaseActivity) getActivity()).hideBusyAnimation();
                    ((BaseActivity) getActivity()).hideBusyAnimation();
                    CarryParkApplication.setIsRescan(true);
                    getActivity().getFragmentManager().popBackStack();
                    CodeScannerFragment codeScannerFragment = new CodeScannerFragment();
                    Bundle args = new Bundle();
                    args.putString("qrcode", CarryParkApplication.getScannedDeviceCode());

                    codeScannerFragment.setArguments(args);
                    getFragmentManager().beginTransaction().add(R.id.fragment_container, codeScannerFragment).commit();

                }

                @Override
                public void onNegativeClick() {
                    String msg;
                    ((BaseActivity) getActivity()).hideBusyAnimation();
                    ((BaseActivity) getActivity()).hideBusyAnimation();
                    ((BaseActivity) getActivity()).hideBusyAnimation();
                    if (SharedPreferenceUtility.isJapanease())
                    {
                        msg=ConstantProject.errorLogDeviceNotDiscurvedJADisplay;
                    }
                    else {
                        msg=ConstantProject.errorLogDeviceNotDiscurvedENDisplay;
                    }
                    sentAppLog("BLE device not discovered or not in range",
                            "BLE device not discovered or not in range","BLEデバイスが見つからないか範囲外です。");

                    postErrorLog("105", msg, "", 1,
                            "", ConstantProject.errorLogDeviceNotDiscurvedENServer, ConstantProject.errorLogDeviceNotDiscurvedJAServer);
                    Activity activity = getActivity();
                    if (activity != null && isAdded()) {
                        Intent intent = new Intent(CarryParkApplication.getCurrentContext(), BottomNavigation.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        intent.putExtra("page", ConstantProject.SplashActivity);
                        startActivity(intent);
                    }
                }
            });

        }*/

    }

    @Override
    public void onSerialRead(byte[] data) {
        if (wakeupCall)
        {
            TestingCommand=TestingCommand+" , 1422";
            checktheResponseisReceivedAtWakeup=true;
            receiveText.setText("");
            receiveText.append(new String(data));
            LastInput = LastInput+ " , "+receiveText.getText().toString();


        }
        else
        {
            TestingCommand=TestingCommand+" , 1432";

            receive(data);
        }

    }

    @Override
    public void onSerialIoError(Exception e) {
        String msg;


        if (SharedPreferenceUtility.isJapanease()) {
            msg = ConstantProject.errorLogDeviceNotDiscurvedJADisplay;
        } else {
            msg = ConstantProject.errorLogDeviceNotDiscurvedENDisplay;
        }
        postErrorLog("105", msg, "", 1,
                "", ConstantProject.errorLogDeviceNotDiscurvedENServer, ConstantProject.errorLogDeviceNotDiscurvedJAServer);
        Activity activity = getActivity();

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

       /* if (isConnectionTimeOut < 3) {
            timeNotify = timeNotify + e.toString() + DateFormat.getTimeInstance().format(new Date());

            disconnect();
            getActivity().runOnUiThread(this::connect);
            isConnectionTimeOut++;
        } else {


            disconnect();
            DialogManager.showRescanDialogue(new DialogManager.IMultiActionDialogOnClickListener() {
                @Override
                public void onPositiveClick() {
                    CarryParkApplication.setIsRescan(true);
                    getActivity().getFragmentManager().popBackStack();
                    CodeScannerFragment codeScannerFragment = new CodeScannerFragment();
                    Bundle args = new Bundle();
                    args.putString("qrcode", CarryParkApplication.getScannedDeviceCode());

                    codeScannerFragment.setArguments(args);
                    getFragmentManager().beginTransaction().add(R.id.fragment_container, codeScannerFragment).commit();

                }

                @Override
                public void onNegativeClick() {
                    String msg;

                    if (SharedPreferenceUtility.isJapanease()) {
                        msg = ConstantProject.errorLogDeviceNotDiscurvedJADisplay;
                    } else {
                        msg = ConstantProject.errorLogDeviceNotDiscurvedENDisplay;
                    }
                    sentAppLog("BLE device not discovered or not in range",
                            "BLE device not discovered or not in range", "BLEデバイスが見つからないか範囲外です。");
                    postErrorLog("105", msg, "", 1,
                            "", ConstantProject.errorLogDeviceNotDiscurvedENServer, ConstantProject.errorLogDeviceNotDiscurvedJAServer);
                    Activity activity = getActivity();
                    if (activity != null && isAdded()) {
                        Intent intent = new Intent(CarryParkApplication.getCurrentContext(), BottomNavigation.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        intent.putExtra("page", ConstantProject.SplashActivity);
                        startActivity(intent);
                    }
                }
            });


        }*/


    }

    /*
     * Serial + UI
     */
    private void connect() {
        try {
            timeNotify = timeNotify + "connecting at" + DateFormat.getTimeInstance().format(new Date());

            BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
            //me:
            // deviceAddress ="80:1F:12:B2:2C:28";
            // cibin:
            // deviceAddress ="80:1F:12:B2:2C:40";
            //  deviceAddress ="80:1F:12:B2:2C:28";
            // BluetoothDevice device = bluetoothAdapter.getRemoteDevice(CarryParkApplication.getDeviceAddressBle());
            BluetoothDevice device = bluetoothAdapter.getRemoteDevice(deviceAddress);
            status("connecting...");
            connected = Connected.Pending;
            SerialSocket socket = new SerialSocket(getActivity().getApplicationContext(), device);
            service.connect(socket);
        } catch (Exception e) {
            onSerialConnectError(e);
        }
    }

    private void disconnect() {
        connected = Connected.False;
        service.disconnect();

    }

    private void receive(byte[] data) {

        receiveText.setText("");
        receiveText.append(new String(data));
        Log.e("comments", receiveText.getText().toString());
        RESPONSE_MSG = receiveText.getText().toString();
        LastInput = LastInput+ " , "+RESPONSE_MSG;

        PreviousInput = RESPONSE_MSG;
        checktheResponseisReceived = true;
        if (!PreviousInput.equalsIgnoreCase(CurrentInput)) {
            tv_comments.setText(tv_comment.getText().toString()+ "\n"+"Receive" + RESPONSE_MSG);
            tv_comment.setText(tv_comment.getText().toString() + "\n" + "Receive" + PreviousInput);
            CurrentInput = RESPONSE_MSG;
            if (SharedPreferenceUtility.isScannedLock()) {
                timeNotify = timeNotify + "receive response at lock" + DateFormat.getTimeInstance().format(new Date());
                //LockSecurityReadIN(receiveText.getText().toString(), hasInitialPaymentPreviouslyDone);
                ((BaseActivity) getActivity()).hideBusyAnimation();
                sentAppLog("Time Log", "Time Log", "時間ログ");
                LockSecurityReadinAlearts(receiveText.getText().toString(), hasInitialPaymentPreviouslyDone);

            }


            if (!SharedPreferenceUtility.isScannedLock()) {
                timeNotify = timeNotify + "receive response at unlock" + DateFormat.getTimeInstance().format(new Date());

                // LockSecurityReadIHashValueUnLock(receiveText.getText().toString(), hasInitialPaymentPreviouslyDone);
                ((BaseActivity) getActivity()).hideBusyAnimation();
                ((BaseActivity) getActivity()).hideBusyAnimation();
                ((BaseActivity) getActivity()).hideBusyAnimation();
                sentAppLog("Time Log", "Time Log", "時間ログ");

                LockSecurityReadIHashValueUnLockAlart(RESPONSE_MSG, hasInitialPaymentPreviouslyDone);
            }

        }


    }

    private void status(String str) {

        SpannableStringBuilder spn = new SpannableStringBuilder(str + '\n');
        spn.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.colorStatusText)), 0, spn.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        receiveText.append(spn);


    }

    private void send(String str) {
        Log.e("comments",str);
        tv_comments.setText(tv_comment.getText().toString()+ "\n"+str);
        LastSend = LastSend+" , "+ str;
        tv_comment.setText(tv_comment.getText().toString() + "\n" + "Send" + LastSend);

        try {
            byte[] data = (str).getBytes();
            service.write(data);
        } catch (Exception e) {
            onSerialIoError(e);
        }
    }

    @Override
    public void onReadDataAtFailure(byte[] data) {

    }

    public void LockSecurityReadIN(String messageResponse, boolean hasInitialPaymentPreviouslyDone) {
        {//S:N=0,S=0,L=0,P=0,B=5b40
            String response = "";
            // messageResponse ="S: N=1, S=0, L=0, P=0, B=5b40";
            if (messageResponse != null)
                response = messageResponse.replaceAll("\r", "");

            if (CarryParkApplication.getCurrentSenderFragment().equalsIgnoreCase(ConstantProject.VideoInstructionFragment)) {
                String N, S, L, P, B;


                String[] separated = response.split(",");

                if (response != null && response.contains("N") && response.contains("B")) {
                    N = separated[0].substring(separated[0].length() - 1);
                    S = separated[1].substring(separated[1].length() - 1);
                    L = separated[2].substring(separated[2].length() - 1);
                    P = separated[3].substring(separated[3].length() - 1);
                    B = separated[4].substring(separated[4].length() - 5);

                    String textWithoutPrefix = B.substring(1);

                    batteryPower = checkBatteryPower(textWithoutPrefix);

                    if (!Utils.isNetworkConnectionAvailable(CarryParkApplication.getCurrentContext())) {
                        new AlertDialog.Builder(CarryParkApplication.getCurrentContext())
                                .setTitle(R.string.alert)
                                .setMessage(R.string.no_internet)
                                .setCancelable(false)
                                .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        // Whatever..
                                    }
                                }).show();
                    } else {
                        //tinu
                        //case1: s=0,l=0 not initial payment done ->normal case
                        //case2: s=0,l=0 initial payment done -> logout after initial payment done
                        // case3: s=1,l=0 initial payment done ->logout at manual lock_img done
                        // case:4: s=1,l-1 initial payment done ->Not called mm
                        if (batteryPower < ConstantProject.batteryPower) {
                            disconnect();

                            DialogManager.showAlertSingleActionDialog(CarryParkApplication.getCurrentActivity(), CarryParkApplication.getCurrentContext().getResources().getString(R.string.btryPoweLock), "ok", new DialogManager.IUniActionDialogOnClickListener() {
                                @Override
                                public void onPositiveClick() {
                                    sentAppLog("Battery low" + batteryPower, "Battery low" + batteryPower,
                                            "バッテリー残量不足です。" + batteryPower);

                                    postErrorLog(messageResponse, "バッテリー残量がご利用頂くには少な過ぎます。", LastSend + " ," + LastInput, 0, "" + batteryPower, ConstantProject.errorLogBatteryEngServer, ConstantProject.errorLogBatteryJapServer);
                                    Intent intent = new Intent(CarryParkApplication.getCurrentContext(), BottomNavigation.class);
                                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                    intent.putExtra("page", ConstantProject.SplashActivity);
                                    startActivity(intent);

                                }
                            });
                        } else {
                            if (S.equalsIgnoreCase("0") && L.equalsIgnoreCase("0")) {
                                //case1 and case2
                                if (!CarryParkApplication.IsPaymentByPassingNoRequired()) {
                                    // moveToPaymentSucees();
                                    CarryParkApplication.setIsAwaitingForPutLockDown(false);
                                    CarryParkApplication.setIsSendMM(false);
                                    targetFragment = new PaymentSuccessFragment();
                                    replaceFragmentLock(targetFragment);
                                } else {
                                    if (hasInitialPaymentPreviouslyDone) {
                                        CarryParkApplication.setIsAwaitingForPutLockDown(false);
                                        CarryParkApplication.setIsSendMM(false);

                                        targetFragment = new PaymentSuccessFragment();
                                        replaceFragmentLock(targetFragment);
                                    } else {
                                        CarryParkApplication.setIsAwaitingForPutLockDown(false);
                                        CarryParkApplication.setIsSendMM(false);

                                        getPaymentToken();
                                    }

                                }


                                CarryParkApplication.setIsSendRR(false);
                                CarryParkApplication.setIsAwaitingForPutLockDown(false);


                            }


                            if (S.equalsIgnoreCase("1") && L.equalsIgnoreCase("0")) {
                                //case3
                                if (isLockerUsed) {
                                    CarryParkApplication.setIsAwaitingForPutLockDown(true);
                                    CarryParkApplication.setIsSendMM(false);

                                } else {
                                    CarryParkApplication.setIsAwaitingForPutLockDown(false);
                                    CarryParkApplication.setIsSendMM(false);

                                }

                                if (!CarryParkApplication.IsPaymentByPassingNoRequired()) {
                                    // moveToPaymentSucees();
                                    targetFragment = new PaymentSuccessFragment();
                                    replaceFragmentLock(targetFragment);

                                } else {

                                    if (hasInitialPaymentPreviouslyDone) {
                                        // moveToPaymentSucees();
                                        targetFragment = new PaymentSuccessFragment();
                                        replaceFragmentLock(targetFragment);
                                    } else {
                                        getPaymentToken();
                                    }


                                }


                            }
                            if (S.equalsIgnoreCase("1") && L.equalsIgnoreCase("1")) {
                                CarryParkApplication.setIsAwaitingForPutLockDown(false);
                                CarryParkApplication.setIsSendMM(true);

                                CarryParkApplication.setForgotToCallSuccessApi(true);
                                if (!CarryParkApplication.IsPaymentByPassingNoRequired()) {
                                    //moveToPaymentSucees();
                                    targetFragment = new PaymentSuccessFragment();
                                    replaceFragmentLock(targetFragment);

                                } else {
                                    if (hasInitialPaymentPreviouslyDone) {
                                        // moveToPaymentSucees();
                                        targetFragment = new PaymentSuccessFragment();
                                        replaceFragmentLock(targetFragment);
                                    } else {
                                        getPaymentToken();
                                    }
                                }


                            }
                        }
///
                    }


                }
            }
/// end: 1. Lock response of IN;

/// st: 1. Lock response of IN;


        }

    }

    public void LockSecurityReadinAlearts(String messageResponse, boolean hasInitialPaymentPreviouslyDone) {
        {//S:N=0,S=0,L=0,P=0,B=5b40
            // messageResponse ="S: N=1, S=0, L=0, P=0, B=5b40";
            ((BaseActivity) getActivity()).hideBusyAnimation();
            ((BaseActivity) getActivity()).hideBusyAnimation();
            String response = messageResponse.replaceAll("\r", "");

            if (CarryParkApplication.getCurrentSenderFragment().equalsIgnoreCase(ConstantProject.VideoInstructionFragment)) {
                String N, S, L, P, B;
                String[] separated = response.split(",");

                if (response != null && response.contains("N") && response.contains("B")) {
                    N = separated[0].substring(separated[0].length() - 1);
                    S = separated[1].substring(separated[1].length() - 1);
                    L = separated[2].substring(separated[2].length() - 1);
                    P = separated[3].substring(separated[3].length() - 1);
                    B = separated[4].substring(separated[4].length() - 5);

                    String textWithoutPrefix = B.substring(1);
                    // short value = Short.parseShort(textWithoutPrefix, 16);

                    // double balteryPower = 3.3 * (Double.valueOf(value) / Double.valueOf(65535)) * (43 / 10);
                    batteryPower = checkBatteryPower(textWithoutPrefix);


                    //tinu
                    //case1: s=0,l=0 not initial payment done ->normal case
                    //case2: s=0,l=0 initial payment done -> logout after initial payment done
                    // case3: s=1,l=0 initial payment done ->logout at manual lock_img done
                    // case:4: s=1,l-1 initial payment done ->Not called mm
                    if (batteryPower < ConstantProject.batteryPower) {
                        ll_camera_layout.setVisibility(View.VISIBLE);
                        disconnect();
                        DialogManager.showAlertSingleActionDialog(CarryParkApplication.getCurrentActivity(), CarryParkApplication.getCurrentContext().getResources().getString(R.string.btryPoweLock), "ok", new DialogManager.IUniActionDialogOnClickListener() {
                            @Override
                            public void onPositiveClick() {
                                sentAppLog("Battery low" + batteryPower, "Battery low" + batteryPower,
                                        "バッテリー残量不足です。" + batteryPower);

                                postErrorLog(messageResponse, ConstantProject.errorLogBatteryJapServer, LastSend + " ," + LastInput, 0, "" + batteryPower, ConstantProject.errorLogBatteryEngServer, ConstantProject.errorLogBatteryJapServer);

                                Intent intent = new Intent(CarryParkApplication.getCurrentContext(), BottomNavigation.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                intent.putExtra("page", ConstantProject.SplashActivity);
                                startActivity(intent);

                            }
                        });

                    } else if (S.equalsIgnoreCase("0") && L.equalsIgnoreCase("1")) {
                        CarryParkApplication.setIsSendRR(false);
                        CarryParkApplication.setIsAwaitingForPutLockDown(false);

                        DialogManager.showAlertSingleActionDialog(CarryParkApplication.getCurrentActivity(), CarryParkApplication.getCurrentContext().getResources().getString(R.string.upholdHandle), "ok", new DialogManager.IUniActionDialogOnClickListener() {
                            @Override
                            public void onPositiveClick() {
                                ((BaseActivity) getActivity()).hideBusyAnimation();
                                disconnect();
                                Intent intent = new Intent(CarryParkApplication.getCurrentContext(), BottomNavigation.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                intent.putExtra("page", ConstantProject.SplashActivity);
                                startActivity(intent);

                            }
                        });
                    } else if (S.equalsIgnoreCase("1") || L.equalsIgnoreCase("1")) {
                        if (CarryParkApplication.getUsedLockerList() != null) {
                            if (CarryParkApplication.getUsedLockerList().size() > 0) {
                                for (int i = 0; i < CarryParkApplication.getUsedLockerList().size(); i++) {
                                    if (CarryParkApplication.getUsedLockerList().get(i).getDeviceName().equalsIgnoreCase(CarryParkApplication.getScannedDeviceName())) {
                                        CarryParkApplication.setForgotToCallSuccessApi(true);
                                        CarryParkApplication.setIsSendRR(false);
                                        CarryParkApplication.setIsAwaitingForPutLockDown(false);
                                        ll_camera_layout.setVisibility(View.GONE);
                                        buttonPayToPaymentSuccess.setVisibility(View.VISIBLE);

                                    }
                                }

                            } else {
                                disconnect();
                                DialogManager.showAlertSingleActionDialog(CarryParkApplication.getCurrentActivity(), CarryParkApplication.getCurrentContext().getResources().getString(R.string.oldDev), "ok", new DialogManager.IUniActionDialogOnClickListener() {
                                    @Override
                                    public void onPositiveClick() {
                                        ((BaseActivity) getActivity()).hideBusyAnimation();


                                        Intent intent = new Intent(CarryParkApplication.getCurrentContext(), BottomNavigation.class);
                                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                        intent.putExtra("page", ConstantProject.SplashActivity);
                                        startActivity(intent);


                                    }
                                });
                            }
                        } else {
                            disconnect();
                            DialogManager.showAlertSingleActionDialog(CarryParkApplication.getCurrentActivity(), CarryParkApplication.getCurrentContext().getResources().getString(R.string.oldDev), "ok", new DialogManager.IUniActionDialogOnClickListener() {
                                @Override
                                public void onPositiveClick() {
                                    ((BaseActivity) getActivity()).hideBusyAnimation();


                                    Intent intent = new Intent(CarryParkApplication.getCurrentContext(), BottomNavigation.class);
                                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                    intent.putExtra("page", ConstantProject.SplashActivity);
                                    startActivity(intent);


                                }
                            });

                        }

                    } else if (!hasInitialPaymentPreviouslyDone) {


                        ll_camera_layout.setVisibility(View.GONE);
                        buttonPayToPaymentSuccess.setVisibility(View.VISIBLE);
                        CarryParkApplication.setIsAwaitingForPutLockDown(false);
                        CarryParkApplication.setIsSendMM(false);

                        if (S.equalsIgnoreCase("0") && L.equalsIgnoreCase("0")) {
                            //case1 and case2
                            ll_camera_layout.setVisibility(View.GONE);
                            buttonPayToPaymentSuccess.setVisibility(View.VISIBLE);

                            if (!CarryParkApplication.IsPaymentByPassingNoRequired()) {
                                // moveToPaymentSucees();
                                        /*CarryParkApplication.setIsAwaitingForPutLockDown(false);
                                        CarryParkApplication.setIsSendMM(false);
                                        targetFragment = new PaymentSuccessFragment();
                                        replaceFragmentLock(targetFragment);*/

                                ll_camera_layout.setVisibility(View.GONE);
                                buttonPayToPaymentSuccess.setVisibility(View.VISIBLE);


                            } else {
                                if (hasInitialPaymentPreviouslyDone) {
                                    CarryParkApplication.setIsAwaitingForPutLockDown(false);
                                    CarryParkApplication.setIsSendMM(false);
                                    ((BaseActivity) getActivity()).hideBusyAnimation();
                                    targetFragment = new PaymentSuccessFragment();
                                    replaceFragmentLock(targetFragment);
                                } else {
                                    ll_camera_layout.setVisibility(View.GONE);
                                    buttonPayToPaymentSuccess.setVisibility(View.VISIBLE);

                                }

                            }


                            CarryParkApplication.setIsSendRR(false);
                            CarryParkApplication.setIsAwaitingForPutLockDown(false);


                        } else if (S.equalsIgnoreCase("1") && L.equalsIgnoreCase("0")) {
                            //case1 and case2
                            CarryParkApplication.setIsSendRR(false);
                            CarryParkApplication.setIsAwaitingForPutLockDown(true);

                            if (!CarryParkApplication.IsPaymentByPassingNoRequired()) {
                                // moveToPaymentSucees();
                                        /*CarryParkApplication.setIsAwaitingForPutLockDown(false);
                                        CarryParkApplication.setIsSendMM(false);
                                        targetFragment = new PaymentSuccessFragment();
                                        replaceFragmentLock(targetFragment);*/

                                ll_camera_layout.setVisibility(View.GONE);
                                buttonPayToPaymentSuccess.setVisibility(View.VISIBLE);


                            } else {
                                if (hasInitialPaymentPreviouslyDone) {
                                    CarryParkApplication.setIsAwaitingForPutLockDown(false);
                                    CarryParkApplication.setIsSendMM(false);

                                    ((BaseActivity) getActivity()).hideBusyAnimation();


                                    targetFragment = new PaymentSuccessFragment();
                                    replaceFragmentLock(targetFragment);
                                } else {
                                    ll_camera_layout.setVisibility(View.GONE);
                                    buttonPayToPaymentSuccess.setVisibility(View.VISIBLE);

                                }

                            }


                            CarryParkApplication.setIsSendRR(false);
                            CarryParkApplication.setIsAwaitingForPutLockDown(false);


                        }


                    }
                    //11:Device in use
                    else {
                        ll_camera_layout.setVisibility(View.GONE);
                        buttonPayToPaymentSuccess.setVisibility(View.VISIBLE);
                        if (S.equalsIgnoreCase("0") && L.equalsIgnoreCase("0")) {
                            //case1 and case2
                            ll_camera_layout.setVisibility(View.GONE);
                            buttonPayToPaymentSuccess.setVisibility(View.VISIBLE);

                            if (!CarryParkApplication.IsPaymentByPassingNoRequired()) {
                                // moveToPaymentSucees();
                                CarryParkApplication.setIsAwaitingForPutLockDown(false);
                                CarryParkApplication.setIsSendMM(false);
                                ((BaseActivity) getActivity()).hideBusyAnimation();
                                targetFragment = new PaymentSuccessFragment();
                                replaceFragmentLock(targetFragment);
                            } else {
                                if (hasInitialPaymentPreviouslyDone) {
                                    CarryParkApplication.setIsAwaitingForPutLockDown(false);
                                    CarryParkApplication.setIsSendMM(false);
                                    ((BaseActivity) getActivity()).hideBusyAnimation();
                                    targetFragment = new PaymentSuccessFragment();
                                    replaceFragmentLock(targetFragment);
                                } else {
                                    ll_camera_layout.setVisibility(View.GONE);
                                    buttonPayToPaymentSuccess.setVisibility(View.VISIBLE);

                                }

                            }


                            CarryParkApplication.setIsSendRR(false);
                            CarryParkApplication.setIsAwaitingForPutLockDown(false);


                        }
                        if (S.equalsIgnoreCase("1") && L.equalsIgnoreCase("0")) {
                            //case3
                            ll_camera_layout.setVisibility(View.GONE);
                            buttonPayToPaymentSuccess.setVisibility(View.VISIBLE);

                            CarryParkApplication.setIsAwaitingForPutLockDown(true);
                            CarryParkApplication.setIsSendMM(false);

                            if (!CarryParkApplication.IsPaymentByPassingNoRequired()) {
                                // moveToPaymentSucees();
                                ((BaseActivity) getActivity()).hideBusyAnimation();
                                targetFragment = new PaymentSuccessFragment();
                                replaceFragmentLock(targetFragment);

                            } else {

                                if (hasInitialPaymentPreviouslyDone) {
                                    // moveToPaymentSucees();
                                    ((BaseActivity) getActivity()).hideBusyAnimation();
                                    targetFragment = new PaymentSuccessFragment();
                                    replaceFragmentLock(targetFragment);
                                }


                            }


                        }
                        if (S.equalsIgnoreCase("1") && L.equalsIgnoreCase("1")) {
                            CarryParkApplication.setIsAwaitingForPutLockDown(false);
                            CarryParkApplication.setIsSendMM(true);
                            ll_camera_layout.setVisibility(View.GONE);
                            buttonPayToPaymentSuccess.setVisibility(View.VISIBLE);

                            if (!CarryParkApplication.IsPaymentByPassingNoRequired()) {
                                //moveToPaymentSucees();
                                ((BaseActivity) getActivity()).hideBusyAnimation();
                                targetFragment = new PaymentSuccessFragment();
                                replaceFragmentLock(targetFragment);

                            } else {
                                if (hasInitialPaymentPreviouslyDone) {
                                    // moveToPaymentSucees();
                                    ((BaseActivity) getActivity()).hideBusyAnimation();
                                    targetFragment = new PaymentSuccessFragment();
                                    replaceFragmentLock(targetFragment);
                                }
                            }


                        }

                    }
///


                }

                // ll_camera_layout.setVisibility(View.GONE);
                // buttonPayToPaymentSuccess.setVisibility(View.VISIBLE);

            }
/// end: 1. Lock response of IN;

/// st: 1. Lock response of IN;


        }

    }

    public void LockSecurityReadIHashValueUnLockAlart(String messageResponse, boolean hasInitialPaymentPreviouslyDone) {
        {

            String response = messageResponse.replaceAll("\r", "");
/// st: 1. Lock response of IN;
            if (response.contains("S") && response.contains("L")) {

                String N, S, L, P, B;

                String[] separated = response.split(",");

                if (response != null && response.contains("L") && response.contains("S")) {
                    N = separated[0].substring(separated[0].length() - 1);
                    S = separated[1].substring(separated[1].length() - 1);
                    L = separated[2].substring(separated[2].length() - 1);
                    P = separated[3].substring(separated[3].length() - 1);
                    B = separated[4].substring(separated[4].length() - 5);
                    String textWithoutPrefix = B.substring(1);

                    // double balteryPower = 3.3 * (Double.valueOf(value) / Double.valueOf(65535)) * (43 / 10);
                    batteryPower = checkBatteryPower(textWithoutPrefix);
                    ll_camera_layout.setVisibility(View.GONE);
                    buttonPayToPaymentSuccess.setVisibility(View.VISIBLE);

                    if (batteryPower < ConstantProject.batteryPower) {
                        DialogManager.showAlertSingleActionDialog(CarryParkApplication.getCurrentActivity(), CarryParkApplication.getCurrentContext().getResources().getString(R.string.btryPower), "ok", new DialogManager.IUniActionDialogOnClickListener() {
                            @Override
                            public void onPositiveClick() {
                                sentAppLog("Battery low" + batteryPower, "Battery low" + batteryPower,
                                        "バッテリー残量不足です。" + batteryPower);

                                postErrorLog(response, ConstantProject.errorLogBatteryJapDisplay, LastSend + " ," + LastInput, 1, "" + batteryPower, ConstantProject.errorLogBatteryEngServer, ConstantProject.errorLogBatteryJapServer);


                            }
                        });


                    }


                }


            }


            if (response.contains("ER") || response.contains("msg1=") && response.contains("msg2=")) {
                ll_camera_layout.setVisibility(View.VISIBLE);
                buttonPayToPaymentSuccess.setVisibility(View.GONE);
                postErrorLog(response, getResources().getString(R.string.deviceAuth), LastSend + " ," + LastInput, 1, "" + batteryPower, ConstantProject.errorLogDeviceAuthFailEng, ConstantProject.errorLogDeviceAuthFailJap);
                sentAppLog("At unlock Screen, Device Auth failed.",
                        "At unlock Screen, Device Auth failed.",
                        "解錠画面でデバイス認証に失敗しました。");

                disconnect();
               /* DialogManager.showAlertSingleActionDialog(CarryParkApplication.getCurrentActivity(), getResources().getString(R.string.deviceAuth), "ok", new DialogManager.IUniActionDialogOnClickListener() {
                    @Override
                    public void onPositiveClick() {
                        //REMOVE AT PRODUCTION
                        //SKIP


                        Intent intent = new Intent(CarryParkApplication.getCurrentContext(), BottomNavigation.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        intent.putExtra("page", ConstantProject.SplashActivity);
                        startActivity(intent);


                   *//*ll_camera_layout.setVisibility(View.GONE);
                        buttonPayToPaymentSuccess.setVisibility(View.VISIBLE);*//*
                    }
                });*/

                DialogManager.showCustDialog(CarryParkApplication.getCurrentActivity(),"",CarryParkApplication.getfirst_name(),CarryParkApplication.getPersonemail(),CarryParkApplication.getPersonMobile(),CarryParkApplication.getcmpny_name() , new DialogManager.IUniActionDialogOnClickListener() {
                    @Override
                    public void onPositiveClick() {

                        Intent intent = new Intent(CarryParkApplication.getCurrentContext(), BottomNavigation.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        intent.putExtra("page", ConstantProject.SplashActivity);
                        startActivity(intent);

                    }


                });


            }
        }

    }

    public void LockSecurityReadIHashValueUnLock(String messageResponse, boolean hasInitialPaymentPreviouslyDone) {
        {


            String response = messageResponse.replaceAll("\r", "");
/// st: 1. Lock response of IN;
            if (response.contains("S") && response.contains("L")) {

                String N, S, L, P, B;

                String[] separated = response.split(",");

                if (response != null && response.contains("L") && response.contains("S")) {
                    N = separated[0].substring(separated[0].length() - 1);
                    S = separated[1].substring(separated[1].length() - 1);
                    L = separated[2].substring(separated[2].length() - 1);
                    P = separated[3].substring(separated[3].length() - 1);
                    B = separated[4].substring(separated[4].length() - 5);
                    String textWithoutPrefix = B.substring(1);
                    //short value = Short.parseShort(textWithoutPrefix, 16);

                    // double balteryPower = 3.3 * (Double.valueOf(value) / Double.valueOf(65535)) * (43 / 10);
                    if (L.equalsIgnoreCase("1") && S.equals("1")) {
                        SharedPreferences sharedPreferences = CarryParkApplication.getCurrentActivity().getSharedPreferences("carry_park", MODE_PRIVATE);
                        CarryParkApplication.setIsSendRR(false);

                        if (!CarryParkApplication.IsPaymentByPassingNoRequired()) {
                            //moveToPaymentSucees();
                            targetFragment = new PaymentSuccessFragment();
                            replaceFragmentLock(targetFragment);

                        } else {
                            if (!CarryParkApplication.isIsPaymentRequired()) {
                                // moveToPaymentSucees();
                                targetFragment = new PaymentSuccessFragment();
                                replaceFragmentLock(targetFragment);
                            } else {

                                callUniversalPayment();
                                   /* Intent intent = new Intent(CarryParkApplication.getCurrentContext(), BottomNavigation.class);
                                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                    intent.putExtra("page", ConstantProject.HtmlFragment);
                                    intent.putExtra("device_token", CarryParkApplication.getUnLockToken());
                                    intent.putExtra("current_time_of_timer",900000);
                                    startActivity(intent);*/
                            }
                        }


                    } else if (L.equalsIgnoreCase("0") && S.equals("0")) {
                        //tinu
                        CarryParkApplication.setIsSendRR(true);
                        CarryParkApplication.setForgotToCallSuccessApi(true);
                        // moveToPaymentSucees();
                                /*targetFragment = new PaymentSuccessFragment();
                                replaceFragmentLock(targetFragment);*/

                        if (!CarryParkApplication.IsPaymentByPassingNoRequired()) {
                            //moveToPaymentSucees();

                            if (!CarryParkApplication.isIsPaymentRequired()) {
                                // moveToPaymentSucees();
                                targetFragment = new PaymentSuccessFragment();
                                replaceFragmentLock(targetFragment);
                            } else {


                                callUniversalPayment();
                                    /*Intent intent = new Intent(CarryParkApplication.getCurrentContext(), BottomNavigation.class);
                                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                    intent.putExtra("page", ConstantProject.HtmlFragment);
                                    intent.putExtra("device_token", CarryParkApplication.getUnLockToken());
                                    intent.putExtra("current_time_of_timer",900000);
                                    startActivity(intent);*/
                            }

                        } else {
                            if (!CarryParkApplication.isIsPaymentRequired()) {
                                // moveToPaymentSucees();
                                targetFragment = new PaymentSuccessFragment();
                                replaceFragmentLock(targetFragment);
                            } else {

                                callUniversalPayment();
                                   /* Intent intent = new Intent(CarryParkApplication.getCurrentContext(), BottomNavigation.class);
                                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                    intent.putExtra("page", ConstantProject.HtmlFragment);
                                    intent.putExtra("device_token", CarryParkApplication.getUnLockToken());
                                    intent.putExtra("current_time_of_timer",900000);
                                    startActivity(intent);*/
                            }
                        }

                    } else {
                        CarryParkApplication.setIsSendRR(false);

                        if (!CarryParkApplication.IsPaymentByPassingNoRequired()) {
                            //moveToPaymentSucees();
                            targetFragment = new PaymentSuccessFragment();
                            replaceFragmentLock(targetFragment);

                        } else {
                            if (!CarryParkApplication.isIsPaymentRequired()) {
                                // moveToPaymentSucees();
                                targetFragment = new PaymentSuccessFragment();
                                replaceFragmentLock(targetFragment);
                            } else {

                                callUniversalPayment();
                                   /* Intent intent = new Intent(CarryParkApplication.getCurrentContext(), BottomNavigation.class);
                                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                    intent.putExtra("page", ConstantProject.HtmlFragment);
                                    intent.putExtra("device_token", CarryParkApplication.getUnLockToken());
                                    intent.putExtra("current_time_of_timer",900000);
                                    startActivity(intent);*/
                            }
                        }


                    }
                }


            }


            if (response.contains("O:R=OK")) {

                if (!CarryParkApplication.IsPaymentByPassingNoRequired()) {
                    //moveToPaymentSucees();
                    targetFragment = new PaymentSuccessFragment();
                    replaceFragmentLock(targetFragment);
                } else {
                    if (!CarryParkApplication.isIsPaymentRequired()) {
                        // moveToPaymentSucees();
                        targetFragment = new PaymentSuccessFragment();
                        replaceFragmentLock(targetFragment);
                    } else {


                        callUniversalPayment();
                           /* Intent intent = new Intent(CarryParkApplication.getCurrentActivity(), BottomNavigation.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            intent.putExtra("page", ConstantProject.HtmlFragment);
                            intent.putExtra("device_token", CarryParkApplication.getUnLockToken());
                            intent.putExtra("current_time_of_timer",900000);
                            startActivity(intent);*/
                    }
                }


            }
//REMOVE
            if (response.contains("ER") || response.contains("msg1=") && response.contains("msg2=")) {
                sentAppLog("At unlock Screen, Device error with response contain ER or msg1=",
                        "At unlock Screen, Device error with response contain ER or msg1=",
                        "解錠画面で、デバイスがERまたはmsg1= を含む応答をしました。");
                disconnect();
                postErrorLog(response, getResources().getString(R.string.deviceAuth), LastSend + " ," + LastInput, 1, "" + batteryPower, ConstantProject.errorLogDeviceAuthFailEng, ConstantProject.errorLogDeviceAuthFailJap);

              /*  DialogManager.showAlertSingleActionDialog(CarryParkApplication.getCurrentActivity(), getResources().getString(R.string.deviceAuth), "ok", new DialogManager.IUniActionDialogOnClickListener() {
                    @Override
                    public void onPositiveClick() {
                        //SKIP


                        Intent intent = new Intent(CarryParkApplication.getCurrentContext(), BottomNavigation.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        intent.putExtra("page", ConstantProject.SplashActivity);
                        startActivity(intent);
*/



                 /* if (!CarryParkApplication.IsPaymentByPassingNoRequired())
                        {
                            //moveToPaymentSucees();
                            targetFragment = new PaymentSuccessFragment();
                            replaceFragmentLock(targetFragment);

                        }
                        else {
                            if (!CarryParkApplication.isIsPaymentRequired()) {
                                // moveToPaymentSucees();
                                targetFragment = new PaymentSuccessFragment();
                                replaceFragmentLock(targetFragment);
                            } else {
                                callUniversalPayment();

                                Intent intenthtml = new Intent(getActivity(), BottomNavigation.class);
                                intenthtml.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                intenthtml.putExtra("page", ConstantProject.HtmlFragment);
                                intenthtml.putExtra("device_token", CarryParkApplication.getUnLockToken());
                                intenthtml.putExtra("current_time_of_timer",900000);
                                getActivity().startActivity(intenthtml);
                            }
                        }*/
                /*    }
                });*/


                  DialogManager.showCustDialog(CarryParkApplication.getCurrentActivity(),"",CarryParkApplication.getfirst_name(),CarryParkApplication.getPersonemail(),CarryParkApplication.getPersonMobile(),CarryParkApplication.getcmpny_name() , new DialogManager.IUniActionDialogOnClickListener() {
                    @Override
                    public void onPositiveClick() {

                        Intent intent = new Intent(CarryParkApplication.getCurrentContext(), BottomNavigation.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        intent.putExtra("page", ConstantProject.SplashActivity);
                        startActivity(intent);
                    }


                });




            }

        }


    }

    private void getLockerDetailsFromScanner(String scannedDeviceCode) {

        if (CarryParkApplication.getLockerDetailsResponseList() != null) {


            Boolean status = CarryParkApplication.getLockerDetailsResponseList().getSuccess();
            if (status == true && CarryParkApplication.getLockerDetailsResponseList().getData() != null) {

                CarryParkApplication.setInitial_charge("" + CarryParkApplication.getLockerDetailsResponseList().getData().getInitial_charges());

                String location = CarryParkApplication.getLockerDetailsResponseList().getData().getLocation();
                String deviceId = CarryParkApplication.getLockerDetailsResponseList().getData().getDeviceId();
                String deviceName = CarryParkApplication.getLockerDetailsResponseList().getData().getDeviceName();
                if (CarryParkApplication.getLockerDetailsResponseList().getData().getPayment_require() != null) {
                    CarryParkApplication.setIsPaymentByPassingNotRequired(CarryParkApplication.getLockerDetailsResponseList().getData().getPayment_require());
                }
                String ratePerHour = String.valueOf(CarryParkApplication.getLockerDetailsResponseList().getData().getRate_per_hour());
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("device_id", CarryParkApplication.getLockerDetailsResponseList().getData().getDeviceId());
                if (CarryParkApplication.getLockerDetailsResponseList().getData().getRate_per_hour()!=null)
                    editor.putString("rate_per_hour", String.valueOf(CarryParkApplication.getLockerDetailsResponseList().getData().getRate_per_hour()));
                if (CarryParkApplication.getLockerDetailsResponseList().getData().getInitial_charges()!=null)
                editor.putString("initial_charge", String.valueOf(CarryParkApplication.getLockerDetailsResponseList().getData().getInitial_charges()));
                editor.putString("device_name", deviceName);
                String presentStatus = CarryParkApplication.getLockerDetailsResponseList().getData().getPresentStatus();

                if (presentStatus.equalsIgnoreCase("unlocked")) {
                    editor.putString("isLocked", "UNLOCKED");
                    editor.apply();
                } else {
                    editor.putString("isLocked", "LOCKED");
                    editor.apply();
                }
                if (CarryParkApplication.getLockerDetailsResponseList().getData().getLockInfo()!=null)
                    editor.putString("lock_info", CarryParkApplication.getLockerDetailsResponseList().getData().getLockInfo());
                if (CarryParkApplication.getLockerDetailsResponseList().getData().getUnlockInfo()!=null)
                    editor.putString("unlock_info", CarryParkApplication.getLockerDetailsResponseList().getData().getUnlockInfo());
                if (CarryParkApplication.getLockerDetailsResponseList().getData().getSuccessInfo()!=null)
                    editor.putString("additional_info", CarryParkApplication.getLockerDetailsResponseList().getData().getSuccessInfo());
                if (CarryParkApplication.getLockerDetailsResponseList().getData().getMaxTime()!=null)
                    editor.putString("max_time", CarryParkApplication.getLockerDetailsResponseList().getData().getMaxTime());
                editor.apply();

                if (CarryParkApplication.getLockerDetailsResponseList().getData().getClose_hash() != null) {
                    CarryParkApplication.setCloseHashValue(CarryParkApplication.getLockerDetailsResponseList().getData().getClose_hash());
                }

                for (int i = 0; i < CarryParkApplication.getUsedLockerList().size(); i++) {
                    if (CarryParkApplication.getUsedLockerList().get(i).getDeviceId().equalsIgnoreCase(CarryParkApplication.getLockerDetailsResponseList().getData().getDeviceId())) {
                        hasInitialPaymentPreviouslyDone = true;
                        SharedPreferenceUtility.saveScannedDeviceInfo_isLock(true);
                    }
                }


                if (CarryParkApplication.getLockerDetailsResponseList().getData().getStatus().equals("active") && CarryParkApplication.getLockerDetailsResponseList().getData().getPresentStatus().equalsIgnoreCase("unlocked") && !hasInitialPaymentPreviouslyDone) {
                    SharedPreferenceUtility.saveScannedDeviceInfo_isLock(true);// going to lock_img

                } else {
                    SharedPreferenceUtility.saveScannedDeviceInfo_isLock(false);// going to un

                    //  tvPriceContent.setText(response.body().getData().getPresentStatus());
                    // tv_status.setText(getResources().getString(R.string.status));
                    if (hasInitialPaymentPreviouslyDone && !CarryParkApplication.getLockerDetailsResponseList().getData().getPresentStatus().equalsIgnoreCase("locked")) {
                        SharedPreferenceUtility.saveScannedDeviceInfo_isLock(true);
                        // tvPriceContent.setText(CarryParkApplication.getCurrentContext().getString(R.string.awaitingLock));
                    }
                    if (CarryParkApplication.getLockerDetailsResponseList().getData().getPresentStatus().equalsIgnoreCase("locked")) {
                        SharedPreferenceUtility.saveScannedDeviceInfo_isLock(false);

                    }
                }
                WakeupCall();


            } else if (status == false) {
                String msg = CarryParkApplication.getLockerDetailsResponseList().getMessage();

                disconnect();

                DialogManager.showAlertSingleActionDialog(CarryParkApplication.getCurrentActivity(), msg, "ok", new DialogManager.IUniActionDialogOnClickListener() {
                    @Override
                    public void onPositiveClick() {
                        Intent intent = new Intent(CarryParkApplication.getCurrentContext(), BottomNavigation.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        intent.putExtra("page", ConstantProject.SplashActivity);
                        startActivity(intent);
                    }
                });


            }


        }


    }






    public void replaceFragmentLock(Fragment targetFragment) {
        Bundle args = new Bundle();
        args.putString("is_from", "InUseFragment");
        targetFragment.setArguments(args);

        getActivity().getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, targetFragment, targetFragment.getClass().getSimpleName()).addToBackStack(null).commit();

    }

    private void getPaymentToken() {

        callUniversalPayment();


    }

    public void replaceFragmentPaymentSDK(String token) {
        if (CarryParkApplication.getCurrentContext() != null) {

            Intent intent = new Intent(CarryParkApplication.getCurrentContext(), BottomNavigation.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            intent.putExtra("page", ConstantProject.HtmlFragment);
            intent.putExtra("device_token", token);
            intent.putExtra("current_time_of_timer", 900000);
            startActivity(intent);
        }


    }

    public void CountDownForPayment() {
        counterForPaymentStartOrSuccessScreen = new CountDownTimer(ConstantProject.TimoutOneminitue, 50) {

            public void onTick(long millisUntilFinished) {
                //called every 10 second, which could be used to
                //send messages or some other action
            }

            public void onFinish() {

                if (RESPONSE_MSG != null && !RESPONSE_MSG.isEmpty()) {
                    if (SharedPreferenceUtility.isScannedLock()) {
                        //LockSecurityReadIN(receiveText.getText().toString(), hasInitialPaymentPreviouslyDone);
                        ((BaseActivity) getActivity()).hideBusyAnimation();
                        LockSecurityReadinAlearts(receiveText.getText().toString(), hasInitialPaymentPreviouslyDone);
                    }


                    if (!SharedPreferenceUtility.isScannedLock()) {
                        // LockSecurityReadIHashValueUnLock(receiveText.getText().toString(), hasInitialPaymentPreviouslyDone);
                        ((BaseActivity) getActivity()).hideBusyAnimation();


                        LockSecurityReadIHashValueUnLockAlart(RESPONSE_MSG, hasInitialPaymentPreviouslyDone);
                    }


                } else {
                    sentAppLog("Time out at Initial payment Screen. It may due to the device not responding to the command  or the User not clicked the pay/proceed button within 1 minute", "Time out at Initial payment Screen. It may due to the device not responding to the command  or the User not clicked the pay/proceed button within 1 minute", "初期費用支払い時にタイムアウトしました。デバイスが応答していないか、ユーザーが1分以内に「支払う」「続ける」をタップしなかった可能性があります。");
                    disconnect();

                    DialogManager.showAlertSingleActionDialog(CarryParkApplication.getCurrentActivity(), CarryParkApplication.getCurrentContext().getResources().getString(R.string.timeOut), "ok", new DialogManager.IUniActionDialogOnClickListener() {
                        @Override
                        public void onPositiveClick() {
                            Intent intent = new Intent(CarryParkApplication.getCurrentContext(), BottomNavigation.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            intent.putExtra("page", ConstantProject.SplashActivity);
                            startActivity(intent);
                        }
                    });


                }


            }
        }.start();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        if (counterForPaymentStartOrSuccessScreen != null) {
            counterForPaymentStartOrSuccessScreen.cancel();
            counterForPaymentStartOrSuccessScreen = null;
        }

    }

    public double checkBatteryPower(String input) {

        int decimal = Integer.parseInt(input, 16);

        double power = new Double(decimal) / new Double(65535);
        double baltteryPower = 3.3 * power;
        baltteryPower = baltteryPower * 4.3;
        CarryParkApplication.setBatterypower("" + baltteryPower);
        return baltteryPower;

    }

    private void callUniversalPayment() {
        ((BaseActivity) getActivity()).showBusyAnimation("");


        String acess_token = GloablMethods.API_HEADER + AppController.getString(CarryParkApplication.getCurrentActivity(), "acess_token");
        Map<String, Object> candidateMap = new HashMap<>();
//SharedPreferenceUtility.getDeviceIdMacID()

        candidateMap.put("device_id", CarryParkApplication.getScannedDeviceCode());
        apiService.CallUniversal_payment(acess_token, candidateMap).enqueue(new Callback<PredefinedPaymentResponse>() {
            @Override
            public void onResponse(Call<PredefinedPaymentResponse> call, Response<PredefinedPaymentResponse> response) {
                ((BaseActivity) CarryParkApplication.getCurrentActivity()).hideBusyAnimation();


                if (response.code() == 200 && response.body() != null) {
                    if (response.body().isSuccess()) {
                        SharedPreferenceUtility.SavePaymentToken(response.body().getData().getPredefined_payment().getToken());
                        String url = response.body().getData().getUrl();
                        targetFragment = new HtmlFragment();


/*
                        Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        i.setPackage("com.android.chrome");
                        try {
                            startActivity(i);
                        } catch (ActivityNotFoundException e) {
                            // Chrome is probably not installed
                            // Try with the default browser
                            i.setPackage(null);
                            startActivity(i);
                        }*/


                        replaceFragmentPaymentSDK(url);
                        // Log.e("URLL", url);
                        //Log.e("URLL To", response.body().getData().getPredefined_payment().getToken());
                    }


                } else if (response.code() == 404) {

                    DialogManager.showAlertSingleActionDialog(CarryParkApplication.getCurrentActivity(), CarryParkApplication.getCurrentContext().getResources().getString(R.string.validation_error), "ok", new DialogManager.IUniActionDialogOnClickListener() {
                        @Override
                        public void onPositiveClick() {

                        }


                    });
                }
            }

            @Override
            public void onFailure(Call<PredefinedPaymentResponse> call, Throwable t) {
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

    public void sentAppLog(String comments, String commentEn, String commentjp) {


        String acess_token = GloablMethods.API_HEADER + AppController.getString(CarryParkApplication.getmContext(), "acess_token");
        Map<String, Object> candidateMap = new HashMap<>();
        candidateMap.put("device_id", CarryParkApplication.getScannedDeviceCode());
        candidateMap.put("app", ConstantProject.DEVICE);
        candidateMap.put("device_status", SharedPreferenceUtility.getDevicePresentStatus()  + ">>" + LastSend+":"+LastInput);
        candidateMap.put("payment_require", "" + CarryParkApplication.IsPaymentByPassingNoRequired());
        candidateMap.put("device_model", ((BaseActivity) getActivity()).getDeviceName());
        candidateMap.put("device_version", ((BaseActivity) getActivity()).DeviceVersion());
        candidateMap.put("scanned_qr_code", CarryParkApplication.getScannedDeviceCode() +"("+ SharedPreferenceUtility.getLatitude()+","+SharedPreferenceUtility.getLongitude()+")");
        candidateMap.put("command_sent", LastSend);
        candidateMap.put("command_received", LastInput);
        candidateMap.put("comments", comments);
        candidateMap.put("error_message_en", commentEn);
        candidateMap.put("error_message_jp", commentjp);


        apiService.LogAppErrors(acess_token, candidateMap).enqueue(new Callback<LogAppErrorModel>() {
            @Override
            public void onResponse(Call<LogAppErrorModel> call, Response<LogAppErrorModel> response) {


            }


            @Override
            public void onFailure(Call<LogAppErrorModel> call, Throwable t) {

                if (!Utils.isNetworkConnectionAvailable(CarryParkApplication.getCurrentContext())) {
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

    // connection code
    private enum Connected {
        False, Pending, True
    }


    public void sampleCall() {

        if (!CarryParkApplication.IsPaymentByPassingNoRequired()) {
            // moveToPaymentSucees();
            CarryParkApplication.setIsAwaitingForPutLockDown(false);
            CarryParkApplication.setIsSendMM(false);
            targetFragment = new PaymentSuccessFragment();
            replaceFragmentLock(targetFragment);
        } else {
            if (hasInitialPaymentPreviouslyDone) {
                CarryParkApplication.setIsAwaitingForPutLockDown(false);
                CarryParkApplication.setIsSendMM(false);

                targetFragment = new PaymentSuccessFragment();
                replaceFragmentLock(targetFragment);
            } else {
                CarryParkApplication.setIsAwaitingForPutLockDown(false);
                CarryParkApplication.setIsSendMM(false);

                getPaymentToken();
            }

        }


        CarryParkApplication.setIsSendRR(false);
        CarryParkApplication.setIsAwaitingForPutLockDown(false);


    }

    public void hideCautionMessage() {
        tv_please_note.setVisibility(View.GONE);
        tv_caution.setVisibility(View.GONE);
        // tv_free_of_charge.setVisibility(View.VISIBLE);

    }


    public void callPaymentBypassTime() {
        Map<String, Object> candidateMap = new HashMap<>();
        candidateMap.put("device_id", CarryParkApplication.getScannedDeviceCode());


        String acess_token = GloablMethods.API_HEADER + AppController.getString(CarryParkApplication.getmContext(), "acess_token");
        apiService =
                ApiClient.getClient().create(ApiInterface.class);
        apiService.CallPaymentBypassTime(acess_token, candidateMap).enqueue(new Callback<ChangeEmailResponseModel>() {
            @Override
            public void onResponse(Call<ChangeEmailResponseModel> call, Response<ChangeEmailResponseModel> response) {
                ((BaseActivity) getActivity()).hideBusyAnimation();
                if(response.body()!=null)
                {
                    if (response.body().isSuccess()) {


                    } else {
                        DialogManager.showAlertSingleActionDialog(CarryParkApplication.getCurrentActivity(), response.body().getMessage(), "ok", new DialogManager.IUniActionDialogOnClickListener() {
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



    private void WakeupCall()
    {
        wakeupCall =true;
        TestingCommand=TestingCommand+" , 2817";
        wakeUpMessageResponse = new CountDownTimer(ConstantProject.TimeoutTenSecond, 1000) {
            public void onTick(long millisUntilFinished) {
                if (!checktheResponseisReceivedAtWakeup) {
                    TestingCommand=TestingCommand+" , 2821";
                    checktheResponseisReceivedAtWakeup = false;
                    getActivity().runOnUiThread(this::status);


                } else {
                    TestingCommand=TestingCommand+" , 2827";
                    wakeupCall=false;
                    doOperations();
                    if (wakeUpMessageResponse != null) {
                        TestingCommand=TestingCommand+" , 2831";
                        wakeUpMessageResponse.cancel();
                        wakeUpMessageResponse = null;
                    }


                }

            }

            private void status() {
                CarryParkApplication.setCurrentSenderFragment(ConstantProject.VideoInstructionFragment);

                send("st;");
            }

            public void onFinish() {
                if (wakeUpMessageResponse != null) {
                    wakeUpMessageResponse.cancel();
                    wakeUpMessageResponse = null;
                }

            }
        }.start();
    }

    public void doOperations()
    {
        TestingCommand=TestingCommand+" , 2857";
        if (SharedPreferenceUtility.isScannedLock()) {
            hashValueSendForUnlock = false;


            if (CarryParkApplication.getUsedLockerList() != null) {
                if (CarryParkApplication.getUsedLockerList().size() > 0) {
                    isLockerUsed = false;
                    for (int i = 0; i < CarryParkApplication.getUsedLockerList().size(); i++) {
                        if (CarryParkApplication.getUsedLockerList().get(i).getDeviceName().equalsIgnoreCase(CarryParkApplication.getLockerDetailsResponseList().getData().getDeviceName())) {
                            isLockerUsed = true;
                        }
                    }

                    if (isLockerUsed) {
                        manageResponse = new CountDownTimer(ConstantProject.TimeoutTenSecond, 1000) {
                            //check each 2 seconds
                            public void onTick(long millisUntilFinished) {
                                if (!checktheResponseisReceived) {
                                    checktheResponseisReceived = false;
                                    getActivity().runOnUiThread(this::status);


                                } else {
                                    if (manageResponse != null) {
                                        manageResponse.cancel();
                                        manageResponse = null;
                                    }
                                }

                            }

                            private void status() {
                                CarryParkApplication.setCurrentSenderFragment(ConstantProject.VideoInstructionFragment);

                                send("st;");
                            }

                            public void onFinish() {


                            }
                        }.start();

                    } else {
                        //  sentAppLog("IN send Wait for response");

                        manageResponse = new CountDownTimer(ConstantProject.TimeoutTenSecond, 1000) {
                            //check each 2 seconds
                            public void onTick(long millisUntilFinished) {
                                if (!checktheResponseisReceived) {
                                    checktheResponseisReceived = false;
                                    getActivity().runOnUiThread(this::SendIN);


                                } else {
                                    if (manageResponse != null) {
                                        manageResponse.cancel();
                                        manageResponse = null;
                                    }
                                }

                            }

                            private void SendIN() {
                                CarryParkApplication.setCurrentSenderFragment(ConstantProject.VideoInstructionFragment);
                                send(ConstantProject.IN);
                            }

                            public void onFinish() {


                            }
                        }.start();


                    }

                } else if (hasInitialPaymentPreviouslyDone) {
                    manageResponse = new CountDownTimer(ConstantProject.TimeoutTenSecond, 1000) {
                        //check each 2 seconds
                        public void onTick(long millisUntilFinished) {
                            if (!checktheResponseisReceived) {
                                checktheResponseisReceived = false;
                                getActivity().runOnUiThread(this::status);


                            } else {
                                if (manageResponse != null) {
                                    manageResponse.cancel();
                                    manageResponse = null;
                                }
                            }

                        }

                        private void status() {
                            CarryParkApplication.setCurrentSenderFragment(ConstantProject.VideoInstructionFragment);

                            send("st;");
                        }

                        public void onFinish() {


                        }
                    }.start();
                    // sentAppLog("St send Wait for response");

                } else {
                    manageResponse = new CountDownTimer(ConstantProject.TimeoutTenSecond, 1000) {
                        //check each 2 seconds
                        public void onTick(long millisUntilFinished) {
                            if (!checktheResponseisReceived) {
                                checktheResponseisReceived = false;
                                getActivity().runOnUiThread(this::SendIN);


                            } else {
                                if (manageResponse != null) {
                                    manageResponse.cancel();
                                    manageResponse = null;
                                }
                            }

                        }

                        private void SendIN() {
                            CarryParkApplication.setCurrentSenderFragment(ConstantProject.VideoInstructionFragment);
                            send(ConstantProject.IN);
                        }

                        public void onFinish() {


                        }
                    }.start();

                }
            } else if (hasInitialPaymentPreviouslyDone) {
                manageResponse = new CountDownTimer(ConstantProject.TimeoutTenSecond, 1000) {
                    //check each 2 seconds
                    public void onTick(long millisUntilFinished) {
                        if (!checktheResponseisReceived) {

                            checktheResponseisReceived = false;
                            getActivity().runOnUiThread(this::status);


                        } else {
                            if (manageResponse != null) {
                                manageResponse.cancel();
                                manageResponse = null;
                            }
                        }

                    }

                    private void status() {
                        CarryParkApplication.setCurrentSenderFragment(ConstantProject.VideoInstructionFragment);

                        send("st;");
                    }

                    public void onFinish() {


                    }
                }.start();

            } else {
                manageResponse = new CountDownTimer(ConstantProject.TimeoutTenSecond, 1000) {
                    //check each 2 seconds
                    public void onTick(long millisUntilFinished) {
                        if (!checktheResponseisReceived) {
                            checktheResponseisReceived = false;
                            getActivity().runOnUiThread(this::SendIN);


                        } else {
                            if (manageResponse != null) {
                                manageResponse.cancel();
                                manageResponse = null;
                            }
                        }

                    }

                    private void SendIN() {
                        CarryParkApplication.setCurrentSenderFragment(ConstantProject.VideoInstructionFragment);
                        send(ConstantProject.IN);
                    }

                    public void onFinish() {


                    }
                }.start();

            }


        } else {


            //REMOVE AT PRODUCTION
            manageResponse = new CountDownTimer(ConstantProject.TimeoutTenSecond,1000) {
                //check each 2 seconds
                public void onTick(long millisUntilFinished) {
                    if (!checktheResponseisReceived) {
                        checktheResponseisReceived = false;
                        getActivity().runOnUiThread(this::SendOCloseHashValue);


                    } else {
                        if (splitresponse !=null)
                        {
                            splitresponse .cancel();
                            splitresponse =null;
                        }
                        if (manageResponse != null) {
                            manageResponse.cancel();
                            manageResponse = null;
                        }
                    }

                }

                private void SendOCloseHashValue() {
                    count=0;
                    CarryParkApplication.setCurrentSenderFragment(ConstantProject.VideoInstructionFragment);

                    String hashValue= "I:" + CarryParkApplication.getCloseHashValue() + ";";//35
                    List<String> list= new ArrayList<String>();

                    int index = 0;
                    while (index<hashValue.length()) {

                        list.add(hashValue.substring(index, Math.min(index+8,hashValue.length())));
                        index=index+8;
                    }

                    splitresponse = new CountDownTimer(2000, 100) {

                        public void onTick(long millisUntilFinished) {
                            if (list.size()>count && count<8)
                            {
                                send(list.get(count));
                            }
                            count++;



                        }
                        public void onFinish() {
                            if (splitresponse != null) {
                                splitresponse.cancel();
                                splitresponse = null;
                            }



                        }
                    }.start();

                    // zxczx
                    // send("I:" + CarryParkApplication.getCloseHashValue() + ";");
                }

                public void onFinish() {


                }
            }.start();


///
        }
    }

}


