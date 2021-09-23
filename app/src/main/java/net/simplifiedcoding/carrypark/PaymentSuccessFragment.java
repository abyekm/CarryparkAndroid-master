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
import android.media.MediaPlayer;
import android.os.*;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import com.google.android.gms.common.api.GoogleApiClient;
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
import net.rest.model.*;
import net.ui.BottomNavigation;
import net.ui.UserSelectionActivity;
import pl.droidsonroids.gif.GifImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import java.util.*;

import static android.content.Context.MODE_PRIVATE;
import static net.others.ConstantProject.*;


/**
 * A simple {@link Fragment} subclass.
 */
public class PaymentSuccessFragment extends Fragment implements View.OnClickListener, ServiceConnection, SerialListener {

    ///sec

    private enum Connected {False, Pending, True}

    private Connected connected = Connected.True;
    private TextView receiveText, tv_user;
    private SerialService service;
    LinearLayout ll_lock_click;
    Button btn_send;
    boolean isFirstTime;
    TextView tv_unlock_d1, tv_lockText;
    boolean isButtonEnable = false, isLeverLocked = false, isStopSound = false;
    ImageView iv_lockerImg;

    // Security KEY
    private String RANDOM_APP_KEY = "";
    private String HASH_VALUE = "";
    private boolean isMSG1UnlockSent = false, isMSG1lockSent = false, isHashValueResponseLock = false, isHashValueResponseUnlock = false;

    ///end: Sec
    String MSG1 = "";
    private SharedPreferences sharedPreferences;
    TextView tvUser, tv_lock1;
    Button buttonProceedToDeviceLocked;
    Fragment targetFragment = null;
    int buttonCount = 0;
    boolean isShowed = false, isSecondTrail = false,isShowingBusyAniReconnect=false;
    String LastSend, LastInput, waiting_for = "";
    int trailCount = 0;
    // private long currentTimeOfTimer = 300000;
    // BottomNavigationView navigation;zxc

    private ApiInterface apiService;
    GifImageView gif;
    double batteryPower;
    int count;



    TextView tv_connectTodevice, tv_unlock, tv_comment;
    TextView tv_streem_operation;
    LinearLayout ll_unlock_view, ll_lock, ll_unLock_click;
    boolean reconnecting = false, isDeviceStatusChecked, isDeviceStatusOK, isDeviceStatusChecking;
    // WebView mWebView;
    boolean CountofbuttonClick;
    boolean isInitialStage;


    String device_auth_failed = "";
    boolean checktheResponseisReceived=false;
    CountDownTimer manageResponse,splitresponse,IhashValueSplit,OHasValueSplit;

    public PaymentSuccessFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
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


        return inflater.inflate(R.layout.fragment_payment_success, container, false);
    }

    //sjn ble
    //sjn addded
    //--
    //sjn test
    BluetoothManager btManager;
    BluetoothAdapter btAdapter;
    BluetoothLeScanner btScanner;

    private final static int REQUEST_ENABLE_BT = 1;
    private static final int PERMISSION_REQUEST_COARSE_LOCATION = 1;


    private boolean successApiUnlockisCalled;
    private int CountofTheStatusChecking = 0;


    BluetoothAdapter mBluetoothAdapter;


    String lockOrUnlock, LogProcess;
    CountDownTimer waitTimer, waitingForButtonClick, ThreeMinitueTimingForLock;


    // Stops scanning after 5 seconds.
    private Handler mHandler = new Handler();
    private static final long SCAN_PERIOD = 5000;
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;

    //sjn ble--
    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        sharedPreferences = getActivity().getSharedPreferences("carry_park", MODE_PRIVATE);
        if (service == null) {
            service = CarryParkApplication.getService();
        }
        successApiUnlockisCalled = false;
        apiService =
                ApiClient.getClient().create(ApiInterface.class);
        tvUser = (TextView) view.findViewById(R.id.tv_user);
        tv_comment = (TextView) view.findViewById(R.id.tv_comment_ps);
        buttonProceedToDeviceLocked = (Button) view.findViewById(R.id.button_proceed_to_device_locked);
        buttonProceedToDeviceLocked.setOnClickListener(this);
        tv_lockText = (TextView) view.findViewById(R.id.tv_lockText);
        gif = (GifImageView) view.findViewById(R.id.gif);
        receiveText = (TextView) view.findViewById(R.id.tv_recive);
        String user = sharedPreferences.getString("user_name", null);
        tv_unlock_d1 = view.findViewById(R.id.tv_unlock_d1);
        isButtonEnable = false;
        tv_connectTodevice = (TextView) view.findViewById(R.id.tv_connectTodevice);
        tv_streem_operation = (TextView) view.findViewById(R.id.tv_streem_operation);
        ll_unlock_view = (LinearLayout) view.findViewById(R.id.ll_unlock_view);
        ll_unLock_click = (LinearLayout) view.findViewById(R.id.ll_unLock_click);
        tv_unlock = (TextView) view.findViewById(R.id.tv_unlock);
        ll_lock = (LinearLayout) view.findViewById(R.id.ll_lock);
        ll_lock_click = (LinearLayout) view.findViewById(R.id.ll_lock_click);
        tv_user = (TextView) view.findViewById(R.id.tv_user);
        tv_lock1 = (TextView) view.findViewById(R.id.tv_lock1);
        buttonProceedToDeviceLocked.setVisibility(View.GONE);
        btn_send = (Button) view.findViewById(R.id.btn_send);
        iv_lockerImg =(ImageView)view.findViewById(R.id.iv_lockerImg);
       /* mWebView = (WebView) view.findViewById(R.id.webview);
        mWebView.setWebViewClient(new WebViewClient());
        mWebView.getSettings().setJavaScriptEnabled(true);
        mWebView.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
        mWebView.getSettings().setPluginState(WebSettings.PluginState.ON);
        mWebView.getSettings().setUserAgentString(null);
        mWebView.getSettings().setMediaPlaybackRequiresUserGesture(false);
        mWebView.setWebChromeClient(new WebChromeClient());
*/
        ((BottomNavigation) getActivity()).disableAllNav();


      /*  mWebView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return true;
            }
        });*/

        ll_unLock_click.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((BaseActivity) getActivity()).showBusyAnimation("");
                if (waitingForButtonClick != null) {
                    waitingForButtonClick.cancel();
                    waitingForButtonClick = null;
                }
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        clickMoveToNext();
                    }
                }, 1000);


            }
        });
        // RAPT
       tv_comment.setVisibility(View.GONE);
        tv_comment.setText("Comment:");
        ll_lock_click.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((BaseActivity) getActivity()).showBusyAnimation("");
                if (waitingForButtonClick != null) {
                    waitingForButtonClick.cancel();
                    waitingForButtonClick = null;
                }
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        clickMoveToNext();
                    }
                }, 1000);

            }
        });
        tv_user.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //REMOVE AT PRODUCTION
              // successApi(CarryParkApplication.getScannedDeviceCode(), "LOCKED", true);
            }
        });

        tv_connectTodevice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //REMOVE AT PRODUCTION
               /* String device_id = CarryParkApplication.getScannedDeviceCode();
                isDeviceStatusChecking = false;
                callAPILock(device_id);
*/
                //  String inputString = "C:R=NG";
                //  receiveTest(inputString);
            }
        });

        CarryParkApplication.setIsAliPay(false);

        if (CarryParkApplication.isIsEnglishLang()) {
            tvUser.setText(getString(R.string.hi) + ", " + user);
        } else if (CarryParkApplication.isIsJapaneaseLang()) {
            //"ようこそ、Carryparkへ
            //○○さん"
            tvUser.setText("ようこそ " + user + "さん");
        } else {
            tv_user.setText(getString(R.string.hi) + ", " + CarryParkApplication.getCurrentUser());
        }

        lockOrUnlock = sharedPreferences.getString("isLocked", null);// current status of the device
        if (lockOrUnlock != null && lockOrUnlock.equalsIgnoreCase("LOCKED")) {// trying to unlocl
            buttonProceedToDeviceLocked.setText(R.string.unlock);
            ll_lock.setVisibility(View.GONE);
            ll_unlock_view.setVisibility(View.VISIBLE);
            LogProcess = "Unlock";
            gif.setVisibility(View.GONE);
            iv_lockerImg.setVisibility(View.GONE);


            //sec

            ////////////////
        } else if (lockOrUnlock != null && lockOrUnlock.equalsIgnoreCase("UNLOCKED")) {
            //  String lock_info = sharedPreferences.getString("lock_info", null);

            iv_lockerImg.setVisibility(View.VISIBLE);
            ll_lock.setVisibility(View.VISIBLE);
            ll_unlock_view.setVisibility(View.GONE);
            LogProcess = "Lock";
            gif.setVisibility(View.GONE);


        }
        //sjn ble code
        initializeBluetooth();

        btManager = (BluetoothManager) getActivity().getSystemService(Context.BLUETOOTH_SERVICE);
        btAdapter = btManager.getAdapter();
        btScanner = btAdapter.getBluetoothLeScanner();

        if (btAdapter != null && !btAdapter.isEnabled()) {
            Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableIntent, REQUEST_ENABLE_BT);
        }

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


        if (SharedPreferenceUtility.isScannedLock()) {
            if (SharedPreferenceUtility.isJapanease()) {
                waiting_for = "ガシャという音がしたらロックレバーを\n"+"下にさげてください。";

                tv_lock1.setText("所定の位置に手荷物をセットして下さい\n" +
                        "セットが終わったら、\n「施錠へ」をタップして下さい");
                tv_lockText.setText("施錠へ");

            } else if (SharedPreferenceUtility.isEnglish()) {
                tv_lockText.setText("Lock");
                waiting_for = "Waiting for putting the lever";

                tv_lock1.setText("Please set your baggage in place\nWhen you have finished setting,\ntap Lock");


            }
            // mWebView.loadUrl(ConstantProject.VideoUrlManualLock);
            tv_streem_operation.setVisibility(View.GONE);
            ll_lock_click.setVisibility(View.VISIBLE);
            ll_unlock_view.setVisibility(View.GONE);
            ll_lock.setVisibility(View.VISIBLE);
            showAwaitingLock();


        } else {
            // mWebView.setVisibility(View.GONE);

            // mWebView.loadUrl(ConstantProject.VideoUrlFull);
            tv_streem_operation.setVisibility(View.VISIBLE);
            ll_lock_click.setVisibility(View.GONE);
            ll_unlock_view.setVisibility(View.VISIBLE);
            ll_unLock_click.setVisibility(View.VISIBLE);

            ll_lock.setVisibility(View.GONE);
            if (SharedPreferenceUtility.isJapanease()) {
                tv_unlock_d1.setText("解錠をタップすると\nロックレバーが上がります");
                tv_unlock.setText("解錠");
            } else if (SharedPreferenceUtility.isEnglish()) {
                tv_unlock_d1.setText("Tap unlock to raise the lock lever");
                tv_unlock.setText("UnLock");


            }
        }


    }


    private void initializeBluetooth() {
        mHandler = new Handler();

        // Use this check to determine whether BLE is supported on the device.  Then you can
        // selectively disable BLE-related features.
        // Initializes a Bluetooth adapter.  For API level 18 and above, get a reference to
        // BluetoothAdapter through BluetoothManager.
        final BluetoothManager bluetoothManager =
                (BluetoothManager) getActivity().getSystemService(Context.BLUETOOTH_SERVICE);
        mBluetoothAdapter = bluetoothManager.getAdapter();

        // Checks if Bluetooth is supported on the device.
        if (mBluetoothAdapter == null) {
            return;
        }
        //sjn
        else {
            if (!mBluetoothAdapter.isEnabled()) {
                mBluetoothAdapter.enable();
            }
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.button_proceed_to_device_locked:

                break;
        }

    }

    private void checkStatusEveryTenSecond() {
        waitTimer = new CountDownTimer(ConstantProject.TimeOut1and30Seconds, 5000) {

            public void onTick(long millisUntilFinished) {
                //called every 10 second, which could be used to
                //send messages or some other action  46040,38432
                if (millisUntilFinished < 30000) {
                    isStopSound = true;
                }

                send("st;");
            }

            public void onFinish() {
                disconnect();
                Intent intent = new Intent(getActivity(), BottomNavigation.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                intent.putExtra("page", ConstantProject.DeviceLockedFragment);
                intent.putExtra(ConstantProject.isFrom, ConstantProject.PaymentSuccessFragment);
                getActivity().startActivity(intent);
                //  disconnectAndMoveToHome();
                //After 60000 milliseconds (60 sec) finish current
                //if you would like to execute something when time finishes
            }
        }.start();
    }

    public void replaceFragment(Fragment someFragment, Fragment currentFragment) {
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, someFragment);
        transaction.addToBackStack(null);
        transaction.commit();


    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_REQUEST_COARSE_LOCATION: {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    System.out.println("coarse location permission granted");
                } else {
                    final AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                    builder.setTitle("Functionality limited");
                    builder.setMessage("Since location access has not been granted, this app will not be able to discover beacons when in the background.");
                    builder.setPositiveButton(android.R.string.ok, null);
                    builder.setOnDismissListener(new DialogInterface.OnDismissListener() {

                        @Override
                        public void onDismiss(DialogInterface dialog) {
                        }

                    });
                    builder.show();
                }
                return;
            }
        }
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
        super.onStop();


    }

    //call Lock API
    private void callAPILock(final String deviceId) {

        ((BaseActivity) CarryParkApplication.getCurrentActivity()).showBusyAnimation("");

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
        candidateMap.put("battery_level", CarryParkApplication.getBatterypower());
        String acess_token = GloablMethods.API_HEADER + AppController.getString(getActivity(), "acess_token");
        apiService.lockingAPI(acess_token, candidateMap).enqueue(new Callback<LockApiResponse>() {
            @Override
            public void onResponse(Call<LockApiResponse> call, Response<LockApiResponse> response) {
                ((BaseActivity) CarryParkApplication.getCurrentActivity()).hideBusyAnimation();
                if (response.code() == 200 && response.body().getSuccess() == true) {

                    //  successApi(deviceId,"LOCKED");
                    String RANDOM_APP_KEY_all = randomAlphaNumeric(1);
                    String RANDOM_Digit = randomDigit(1);
                    String RANDOM_ALPHACAPS = randomAlphaCaps(1);
                    String RANDOM_ALPHASMALL = randomAlphaSmall(1);
                    RANDOM_APP_KEY = RANDOM_APP_KEY_all + RANDOM_Digit + RANDOM_ALPHACAPS + RANDOM_ALPHASMALL;
                    CarryParkApplication.setRandomAppKey(RANDOM_APP_KEY);
                    MSG1 = "M:" + RANDOM_APP_KEY + ";";
                    isMSG1UnlockSent = false;
                    isMSG1lockSent = true;
                    CarryParkApplication.setCurrentSenderFragment(ConstantProject.PaymentSuccessFragment);
                    CarryParkApplication.setIsLockProcess(true);
                    getActivity().runOnUiThread(this::SendMSG1);

                } else if (response.code() == 404) {
                    ((BaseActivity) CarryParkApplication.getCurrentActivity()).hideBusyAnimation();
                    String msg = String.valueOf(response.body().getMessage());
                    DialogManager.showAlertSingleActionDialog(CarryParkApplication.getCurrentActivity(), msg, "ok", new DialogManager.IUniActionDialogOnClickListener() {
                        @Override
                        public void onPositiveClick() {

                        }


                    });
                }
            }

            private void SendMSG1() {
                send(MSG1);
            }

            @Override
            public void onFailure(Call<LockApiResponse> call, Throwable t) {
                ((BaseActivity) CarryParkApplication.getCurrentActivity()).hideBusyAnimation();
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


    //call Unlock API
    private void callAPIUnLock(final String deviceId) {

        ((BaseActivity) getActivity()).showBusyAnimation("");
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
        candidateMap.put("battery_level", CarryParkApplication.getBatterypower());
        String acess_token = GloablMethods.API_HEADER + AppController.getString(getActivity(), "acess_token");
        apiService.UnlockingAPI(acess_token, candidateMap).enqueue(new Callback<UnLockApiResponse>() {
            @Override
            public void onResponse(Call<UnLockApiResponse> call, Response<UnLockApiResponse> response) {
                ((BaseActivity) getActivity()).hideBusyAnimation();
                if (response.code() == 200 && response.body().getSuccess() == true) {
                    //calling success Api
                    // successApi(deviceId,"UNLOCKED");
                    String RANDOM_APP_KEY_all = randomAlphaNumeric(1);
                    String RANDOM_Digit = randomDigit(1);
                    String RANDOM_ALPHACAPS = randomAlphaCaps(1);
                    String RANDOM_ALPHASMALL = randomAlphaSmall(1);
                    RANDOM_APP_KEY = RANDOM_APP_KEY_all + RANDOM_Digit + RANDOM_ALPHACAPS + RANDOM_ALPHASMALL;
                    CarryParkApplication.setRandomAppKey(RANDOM_APP_KEY);
                    MSG1 = "M:" + RANDOM_APP_KEY + ";";
                    CarryParkApplication.setCurrentSenderFragment(ConstantProject.PaymentSuccessFragment);
                    CarryParkApplication.setIsLockProcess(false);

                    isMSG1UnlockSent = true;
                    isMSG1lockSent = false;
                    CarryParkApplication.getCurrentActivity().runOnUiThread(this::SendMSG1);


                } else if (response.code() == 404) {
                    ((BaseActivity) CarryParkApplication.getCurrentActivity()).hideBusyAnimation();
                    //String msg = response.body().getMessage();
                    DialogManager.showAlertSingleActionDialog(CarryParkApplication.getCurrentActivity(), CarryParkApplication.getCurrentContext().getResources().getString(R.string.validation_error), "ok", new DialogManager.IUniActionDialogOnClickListener() {
                        @Override
                        public void onPositiveClick() {

                        }


                    });
                }
            }

            private void SendMSG1() {
                send(MSG1);
            }


            @Override
            public void onFailure(Call<UnLockApiResponse> call, Throwable t) {
                ((BaseActivity) CarryParkApplication.getCurrentActivity()).hideBusyAnimation();
                if (!Utils.isNetworkConnectionAvailable(getContext())) {
                } else {
                }
            }
        });
    }

    //success Api
    private void successApi(String deviceId, final String lckOrUnlck, boolean isShow) {
        successApiUnlockisCalled = true;
        ((BaseActivity) getActivity()).hideBusyAnimation();
        if (isShow)
            ((BaseActivity) getActivity()).showBusyAnimation("");


        Map<String, Object> candidateMap = new HashMap<>();
        candidateMap.put("device_id", deviceId);
        if (SharedPreferenceUtility.isJapanease()) {
            candidateMap.put("lang_id", ConstantProject.forJapaneaseResponse);

        } else if (SharedPreferenceUtility.isEnglish()) {
            candidateMap.put("lang_id", "en");
        } else if (SharedPreferenceUtility.isKorean()) {
            candidateMap.put("lang_id", ConstantProject.forKoreanResponse);
        } else if (SharedPreferenceUtility.isChinease()) {
            candidateMap.put("lang_id", ConstantProject.forChineaseResponse);
        }
        // mWebView.destroy();
        String acess_token = GloablMethods.API_HEADER + AppController.getString(getActivity(), "acess_token");
        apiService.successAPI(acess_token, candidateMap).enqueue(new Callback<SuccessApiResponse>() {
            @Override
            public void onResponse(Call<SuccessApiResponse> call, Response<SuccessApiResponse> response) {
                if (isShow)

                    ((BaseActivity) getActivity()).hideBusyAnimation();


                //REMOVE
             /* disconnect();
                Intent intent1 = new Intent(getActivity(), BottomNavigation.class);
                intent1.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                intent1.putExtra("page", ConstantProject.DeviceLockedFragment);
                intent1.putExtra(ConstantProject.isFrom, ConstantProject.PaymentSuccessFragment);
                getActivity().startActivity(intent1);
*/


                // progressDialog.dismiss();
                if (response.code() == 200 && response.body().getSuccess() == true) {

                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    //commented only for demo
                    //editor.putString("isLocked", "UNLOCKED");
                    editor.putString("isLocked", lckOrUnlck);
                    editor.apply();
                    //
                    successApiUnlockisCalled = true;
                    if (!SharedPreferenceUtility.isScannedLock()) {
                        disconnect();
                       Intent intent = new Intent(getActivity(), BottomNavigation.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        intent.putExtra("page", ConstantProject.DeviceLockedFragment);
                        intent.putExtra(ConstantProject.isFrom, ConstantProject.PaymentSuccessFragment);
                        getActivity().startActivity(intent);
               } else {
                        if (isLeverLocked) {
                            if (waitTimer != null) {
                                waitTimer.cancel();
                                waitTimer = null;
                            }
                          disconnect();
                            Activity activity = getActivity();
                            if (activity != null ) {
                                Intent intent = new Intent(getActivity(), BottomNavigation.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                intent.putExtra("page", ConstantProject.DeviceLockedFragment);
                                intent.putExtra(ConstantProject.isFrom, ConstantProject.PaymentSuccessFragment);
                                getActivity().startActivity(intent);
                            }

                        }
                    }


                } else if (response.code() == 404) {
                    // progressDialog.dismiss();
                    String msg = String.valueOf(response.body().getMessage());
                    DialogManager.showAlertSingleActionDialog(CarryParkApplication.getCurrentActivity(), msg, "ok", new DialogManager.IUniActionDialogOnClickListener() {
                        @Override
                        public void onPositiveClick() {

                        }


                    });
                }
            }

            @Override
            public void onFailure(Call<SuccessApiResponse> call, Throwable t) {
                if (isShow)

                    ((BaseActivity) getActivity()).hideBusyAnimation();

                // progressDialog.dismiss();
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

    public void moveToInuse() {

        Intent intent = new Intent(CarryParkApplication.getCurrentContext(), BottomNavigation.class);
        intent.putExtra("page", ConstantProject.InUseFragment);
        intent.putExtra("scannedDeviceCode", "");
        intent.putExtra("isFrom", ConstantProject.PaymentSuccessFragment);
        intent.putExtra("hasInitialPaymentPreviouslyDone", false);
        getActivity().startActivity(intent);
    }


    // stsrt: security IMPlementation


    @Override
    public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
        service = CarryParkApplication.getService();
        if (service != null)
            service.attach(this);
        else
            getActivity().startService(new Intent(getActivity(), SerialService.class)); // prevents service destroy on unbind from recreated activity caused by orientation change


    }

    public void callSuccessApiLock() {
        CarryParkApplication.setIsSendMM(false);
        CarryParkApplication.setIsStatusChecking(false);
        CarryParkApplication.setIsAwaitingForPutLockDown(false);

        if (waitTimer != null) {
            waitTimer.cancel();
            waitTimer = null;
        }
        if (ThreeMinitueTimingForLock != null) {
            ThreeMinitueTimingForLock.cancel();
            ThreeMinitueTimingForLock = null;
        }
        successApi(CarryParkApplication.getScannedDeviceCode(), "LOCKED", true);
    }

    @Override
    public void onServiceDisconnected(ComponentName componentName) {
        service = null;
    }

    public void onSerialConnect() {
        isSecondTrail = true;
        ((BaseActivity) CarryParkApplication.getCurrentActivity()).hideBusyAnimation();
        ((BaseActivity) CarryParkApplication.getCurrentActivity()).hideBusyAnimation();

        connected = Connected.True;



            clickMoveToNext();





    }



    private void sendSt() {

        send("st;");
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

        postErrorLog("105", msg, "", "",
                1, ConstantProject.errorLogDeviceNotDiscurvedENServer, ConstantProject.errorLogDeviceNotDiscurvedJAServer);
        Activity activity = getActivity();
        if (activity != null && isAdded()) {
            Intent intent = new Intent(CarryParkApplication.getCurrentContext(), BottomNavigation.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            intent.putExtra("page", ConstantProject.SplashActivity);
            startActivity(intent);
        }


        /*if (trailCount < 10) {
            trailCount++;
            if (!isShowingBusyAniReconnect)
            {
                ((BaseActivity) getActivity()).showBusyAnimation("");
                isShowingBusyAniReconnect=true;
            }
            connect();

        } else {
            if (LogProcess.equalsIgnoreCase("Lock"))
            {
                sentAppLog( e.toString()+"Java exceptions at Lock, BLE connection disconnected, and move to the home screen with the popup please try again later",
                        e.toString()+"Java exceptions at Lock, BLE connection disconnected, and move to the home screen with the popup please try again later",
                        "施錠時のJava例外処理で、BLE接続の切断により「もう一度お試しください」を表示し、ホーム画面に戻りました。");

            }
            else {

                sentAppLog( e.toString()+"Java exceptions at unLock, BLE connection disconnected, and move to the home screen with the popup please try again later",
                        e.toString()+"Java exceptions at unLock, BLE connection disconnected, and move to the home screen with the popup please try again later",
                        "解錠時のJava例外処理で、BLE接続の切断により「もう一度お試しください」を表示し、ホーム画面に戻りました。");

            }
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

*/

    }

    @Override
    public void onSerialRead(byte[] data) {
        receive(data);
    }

    @Override
    public void onSerialIoError(Exception e) {
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
        /*if (trailCount < 10) {
            trailCount++;
            if (!isShowingBusyAniReconnect)
            {
                isShowingBusyAniReconnect=true;
                ((BaseActivity) getActivity()).showBusyAnimation("");
            }
            connect();


        } else {
            if (LogProcess.equalsIgnoreCase("Lock"))
            {
                sentAppLog( e.toString()+"Java exceptions at Lock, BLE connection disconnected, and move to the home screen with the popup please try again later",
                        e.toString()+"Java exceptions at Lock, BLE connection disconnected, and move to the home screen with the popup please try again later",
                        "施錠時のJava例外処理で、BLE接続の切断により「もう一度お試しください」を表示し、ホーム画面に戻りました。");

            }
            else {

                sentAppLog( e.toString()+"Java exceptions at unLock, BLE connection disconnected, and move to the home screen with the popup please try again later",
                        e.toString()+"Java exceptions at unLock, BLE connection disconnected, and move to the home screen with the popup please try again later",
                        "解錠時のJava例外処理で、BLE接続の切断により「もう一度お試しください」を表示し、ホーム画面に戻りました。");

            }

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
*/

    }

    @Override
    public void onReadDataAtFailure(byte[] data) {
        receive(data);
    }


    private void connect() {
        try {
            BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
            BluetoothDevice device = bluetoothAdapter.getRemoteDevice(CarryParkApplication.getDeviceAddressBle());
            connected = Connected.Pending;
            SerialSocket socket = new SerialSocket(getActivity().getApplicationContext(), device);
            service.connect(socket);
        } catch (Exception e) {
            onSerialConnectError(e);
        }


    }

    private void send(String str) {
        Log.e("comments",str);

        LastSend = str;
        tv_comment.setText(tv_comment.getText().toString() + "\n" + "Send: " + LastSend);
        if (connected != Connected.True) {
            return;
        }
        try {
            byte[] data = (str).getBytes();
            service.write(data);
        } catch (Exception e) {
            onSerialIoError(e);
        }
    }

    public void receive(byte[] data) {

        receiveText.setText("");
        receiveText.append(new String(data));
        LastInput = receiveText.getText().toString();
        checktheResponseisReceived=true;
        tv_comment.setText(tv_comment.getText().toString() + "\n" + "receive: " + LastInput);
        isInitialStage = false;//        Log.e("comments",str);

        Log.e("comments", receiveText.getText().toString());



        if (CarryParkApplication.getCurrentSenderFragment().equalsIgnoreCase(ConstantProject.PaymentSuccessFragment) &&
                CarryParkApplication.isIsLockProcess() && isMSG1lockSent ) {
            isMSG1lockSent = false;
            String X, Y, Z, response = "", messageResponse = receiveText.getText().toString();
            if (messageResponse != null && messageResponse.length() > 0 && messageResponse.contains("P") && messageResponse.contains("X")
                    && messageResponse.contains("Y") && messageResponse.contains("Z")) {
                // message is response of the MSG1;
                //“P:X=15,Y=5,Z=5”.
                if (messageResponse.contains("\r")) {
                    response = messageResponse.replaceAll("\r", "");

                }
                String[] separated = response.split(",");
                // value of N

                X = separated[0].substring(separated[0].indexOf("=") + 1, separated[0].length());
                Y = separated[1].substring(separated[1].indexOf("=") + 1, separated[1].length());
                Z = separated[2].substring(separated[2].indexOf("=") + 1, separated[2].length());

/// Call Hash Value API

                getHashValuesAtLock(CarryParkApplication.getScannedDeviceCode(), "close", X, Y, Z);
            }

        }
        if (CarryParkApplication.getCurrentSenderFragment().equalsIgnoreCase(ConstantProject.PaymentSuccessFragment) && CarryParkApplication.isIsHashValueSend() && isHashValueResponseLock ) {


            ((BaseActivity) getActivity()).hideBusyAnimation();
            //REMOVE IN PRODUCTION
            if (splitresponse != null) {
                splitresponse.cancel();
                splitresponse = null;
            }
            if (OHasValueSplit != null) {
                OHasValueSplit.cancel();
                OHasValueSplit = null;
            }


            if (receiveText.getText().toString().contains("NG") || receiveText.getText().toString().contains("ER")) {
                ((BaseActivity) getActivity()).hideBusyAnimation();
                disconnect();
                sentAppLog( "Hash value response at Lock, Device error with response contain ER or NG","Hash value response at Lock, Device error with response contain ER or NG",
                        "施錠Hash valueを返す際、デバイスがERまたはNGを含む応答をしました。");

                postErrorLog(receiveText.getText().toString(), "デバイスの認証に失敗しました", LastSend + " ," + LastInput, "" + batteryPower, 1, ConstantProject.errorLogDeviceAuthFailEng, ConstantProject.errorLogDeviceAuthFailJap);
               /* DialogManager.showUniActionDialog(CarryParkApplication.getCurrentActivity(), CarryParkApplication.getCurrentContext().getResources().getString(R.string.deviceAuth), "ok", new DialogManager.IUniActionDialogOnClickListener() {
                    @Override
                    public void onPositiveClick() {
                        //  sentAppLog("Lock screen, Hashvalue response at lock");
                        ((BaseActivity) getActivity()).hideBusyAnimation();
//SKIP
                       Intent intent = new Intent(CarryParkApplication.getCurrentContext(), BottomNavigation.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        intent.putExtra("page", ConstantProject.SplashActivity);
                        startActivity(intent);




                    }


                });*/

                DialogManager.showCustDialog(CarryParkApplication.getCurrentActivity(),"",CarryParkApplication.getfirst_name(),CarryParkApplication.getPersonemail(),CarryParkApplication.getPersonMobile(),CarryParkApplication.getcmpny_name() , new DialogManager.IUniActionDialogOnClickListener() {
                    @Override
                    public void onPositiveClick() {
                        ((BaseActivity) getActivity()).hideBusyAnimation();
//SKIP
                        Intent intent = new Intent(CarryParkApplication.getCurrentContext(), BottomNavigation.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        intent.putExtra("page", ConstantProject.SplashActivity);
                        startActivity(intent);

                    }


                });



            }


            String N, S, L, B, response = receiveText.getText().toString();
            // REMOVE AT PRo
            // response = "S:N=0,S=1,L=0,P=0,B=5f80";
            if (response.contains("\r"))
                response = response.replaceAll("\r", "");


            if (response.contains("S") && response.contains("N") && response.contains("L") && response.contains("P") && response.contains("B")) {
                String[] separated = response.split(",");

                N = separated[0].substring(separated[0].length() - 1);
                S = separated[1].substring(separated[1].length() - 1);
                L = separated[2].substring(separated[2].length() - 1);
                B = separated[4].substring(separated[4].length() - 5);


                if (SharedPreferenceUtility.isScannedLock()) {
                    if (S.equalsIgnoreCase("1") && L.equalsIgnoreCase("0")) {
                        ((BaseActivity) getActivity()).hideBusyAnimation();

                        ((BaseActivity) getActivity()).showBusyAnimation(waiting_for);
                        ((BaseActivity) getActivity()).showBusyAnimation(waiting_for);
                        CarryParkApplication.setIsStatusChecking(true);
                        isHashValueResponseLock = false;
                        if (!successApiUnlockisCalled) {
                            SendMM();
                            successApi(CarryParkApplication.getScannedDeviceCode(), "LOCKED", false);

                        }
                        if (waitingForButtonClick != null) {
                            waitingForButtonClick.cancel();
                            waitingForButtonClick = null;
                        }

                        StatusCheckIn5Seconds();


                    }
                }

            }

        }
        if (isMSG1UnlockSent ) {

            String X, Y, Z, response = "", messageResponse = receiveText.getText().toString();
            if (messageResponse != null && messageResponse.length() > 0 && messageResponse.contains("P") && messageResponse.contains("X")
                    && messageResponse.contains("Y") && messageResponse.contains("Z")) {
                // message is response of the MSG1;
                //“P:X=15,Y=5,Z=5”.
                if (messageResponse.contains("\r")) {
                    response = messageResponse.replaceAll("\r", "");

                }
                String[] separated = response.split(",");
                // value of N

                X = separated[0].substring(separated[0].indexOf("=") + 1, separated[0].length());
                Y = separated[1].substring(separated[1].indexOf("=") + 1, separated[1].length());
                Z = separated[2].substring(separated[2].indexOf("=") + 1, separated[2].length());

/// Call Hash Value API

                getHashValuesAtUnLock(CarryParkApplication.getScannedDeviceCode(), "open", X, Y, Z);


            }


        }

        if (isHashValueResponseUnlock ) {
            ((BaseActivity) getActivity()).hideBusyAnimation();
            if (receiveText.getText().toString().contains("O:R=OK")) {

                //  SendRR();

            } else if (receiveText.getText().toString().contains("NG") || receiveText.getText().toString().contains("ER")) {
                //REMOVE AT PRODUCTION
                disconnect();
               postErrorLog(receiveText.getText().toString(),"開く際の機器エラー（ロック解除）。 Sはオンになりますが、Nもオンのままです。 内部プレートが機能していないようです。", LastSend + " ," + LastInput, "" + batteryPower, 1, "Device error when opening (unlocked). S is on, but N is still on. The inner plate doesn't seem to be working.", "開く際の機器エラー（ロック解除）。 Sはオンになりますが、Nもオンのままです。 内部プレートが機能していないようです。");
                sentAppLog("Hash value response at unlocking, Device error with response contain ER or NG",
                        "Hash value response at unlocking, Device error with response contain ER or NG",
                        "解錠Hash valueを返す際、デバイスがERまたはNGを含む応答をしました。");
                if (SharedPreferenceUtility.isJapanease()) {
                    device_auth_failed = ConstantProject.errorLogDeviceAuthFailJap;
                } else {
                    device_auth_failed = ConstantProject.errorLogDeviceAuthFailEng;
                }

                DialogManager.showUniActionDialog(CarryParkApplication.getCurrentActivity(), device_auth_failed, "ok", new DialogManager.IUniActionDialogOnClickListener() {
                    @Override
                    public void onPositiveClick() {
                        //tinu
                    Intent intent = new Intent(CarryParkApplication.getCurrentContext(), BottomNavigation.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        intent.putExtra("page", ConstantProject.SplashActivity);
                        startActivity(intent);



                        //
                        // SendRR();
                    }

                });
            }

            String response = receiveText.getText().toString();
            if (response.contains("S") && response.contains("L")) {

                String N, S, L, P, B;

                String[] separated = response.split(",");

                if (response != null && response.contains("N") && response.contains("B")) {
                    N = separated[0].substring(separated[0].length() - 1);
                    S = separated[1].substring(separated[1].length() - 1);
                    L = separated[2].substring(separated[2].length() - 1);
                    P = separated[3].substring(separated[3].length() - 1);
                    B = separated[4].substring(separated[4].length() - 5);


                     if (L.equalsIgnoreCase("0") && S.equals("0")) {

                         SendRR();


                         CarryParkApplication.setIsSendRR(false);

                         if (!successApiUnlockisCalled)
                             successApi(CarryParkApplication.getScannedDeviceCode(), "UNLOCKED", true);
                     }

                }

            }


        }

        if (CarryParkApplication.isIsStatusChecking()) {
            StatusResponseTenSecondResponse(receiveText.getText().toString());


        }
        if (isDeviceStatusChecking) {
            ////////////////


            String response = receiveText.getText().toString().replaceAll("\r", "");
/// st: 1. Lock response of st;
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
                    // short value = Short.parseShort(textWithoutPrefix, 16);
                    //  batteryPower = checkBatteryPower(textWithoutPrefix);
                    // double balteryPower = 3.3 * (Double.valueOf(value) / Double.valueOf(65535)) * (43 / 10);

                    if (S.equalsIgnoreCase("0") && L.equalsIgnoreCase("1")) {
                        CarryParkApplication.setIsSendRR(false);
                        CarryParkApplication.setIsAwaitingForPutLockDown(false);

                        DialogManager.showUniActionDialog(CarryParkApplication.getCurrentActivity(), CarryParkApplication.getCurrentContext().getResources().getString(R.string.upholdHandle), "ok", new DialogManager.IUniActionDialogOnClickListener() {
                            @Override
                            public void onPositiveClick() {
                                ((BaseActivity) getActivity()).hideBusyAnimation();
                            }
                        });


                    } else {

                        String device_id = CarryParkApplication.getScannedDeviceCode();
                        isDeviceStatusChecking = false;
                        callAPILock(device_id);
                    }


                }

            }

            //////////////
        }

    }


    //call HashValueAPI
    public void getHashValuesAtLock(final String deviceId, final String lckOrUnlck, String X_value, String Y_value, String Z_value) {

        Map<String, Object> candidateMap = new HashMap<>();
        candidateMap.put("device_id", CarryParkApplication.getScannedDeviceCode());
        candidateMap.put("command_text", lckOrUnlck);
        candidateMap.put("random_app_key", CarryParkApplication.getRandomAppKey());
        candidateMap.put("starting_position", X_value);
        candidateMap.put("key_string_length", Y_value);
        candidateMap.put("skip_chars", Z_value);
        candidateMap.put("User_ID", CarryParkApplication.getUser_ID());

        String acess_token = GloablMethods.API_HEADER + AppController.getString(CarryParkApplication.getmContext(), "acess_token");

        apiService.getHashValues(acess_token, candidateMap).enqueue(new Callback<HashApiResponse>() {
            @Override
            public void onResponse(Call<HashApiResponse> call, Response<HashApiResponse> response) {


                // if (response.code() == 200 && response.body().getSuccess() == true) {
/// sending hash value to Device


                SharedPreferenceUtility.saveHashValues(response.body().getData());
                if (response.body().getData() != null && !response.body().getData().isEmpty()) {
                    isHashValueResponseLock = true;
                    isHashValueResponseUnlock = false;
                    HASH_VALUE = response.body().getData();

                    CarryParkApplication.setCurrentSenderFragment(ConstantProject.PaymentSuccessFragment);
                    CarryParkApplication.getCurrentActivity().runOnUiThread(this::SendHashValue);
                }


            }

            private void SendHashValue() {
                CarryParkApplication.setIsHashValueSend(true);
                CarryParkApplication.setCurrentSenderFragment(ConstantProject.PaymentSuccessFragment);

                String hashValue= "C:" + HASH_VALUE + ";";//35
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
               // send("C:" + HASH_VALUE + ";");

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

    public void getHashValuesAtUnLock(final String deviceId, final String lckOrUnlck, String X_value, String Y_value, String Z_value) {
        ((BaseActivity) getActivity()).showBusyAnimation("");
        Map<String, Object> candidateMap = new HashMap<>();
        candidateMap.put("device_id", CarryParkApplication.getScannedDeviceCode());
        candidateMap.put("command_text", lckOrUnlck);
        candidateMap.put("random_app_key", CarryParkApplication.getRandomAppKey());
        candidateMap.put("starting_position", X_value);
        candidateMap.put("key_string_length", Y_value);
        candidateMap.put("skip_chars", Z_value);
        candidateMap.put("User_ID", CarryParkApplication.getUser_ID());

        String acess_token = GloablMethods.API_HEADER + AppController.getString(CarryParkApplication.getmContext(), "acess_token");

        apiService.getHashValues(acess_token, candidateMap).enqueue(new Callback<HashApiResponse>() {
            @Override
            public void onResponse(Call<HashApiResponse> call, Response<HashApiResponse> response) {
                ((BaseActivity) getActivity()).hideBusyAnimation();


                // if (response.code() == 200 && response.body().getSuccess() == true) {
/// sending hash value to Device


                SharedPreferenceUtility.saveHashValues(response.body().getData());
                if (response.body().getData() != null && !response.body().getData().isEmpty()) {
                    isMSG1UnlockSent = false;
                    isHashValueResponseLock = false;
                    isHashValueResponseUnlock = true;
                    HASH_VALUE = response.body().getData();

                    CarryParkApplication.setCurrentSenderFragment(ConstantProject.PaymentSuccessFragment);
                    CarryParkApplication.getCurrentActivity().runOnUiThread(this::SendHashValue);


                }


            }

            private void SendHashValue() {
                CarryParkApplication.setIsHashValueSend(true);
                CarryParkApplication.setCurrentSenderFragment(ConstantProject.PaymentSuccessFragment);

                //send("O:" + HASH_VALUE + ";");
                count=0;

                String hashValue= "O:" + HASH_VALUE + ";";//35
                List<String> list= new ArrayList<String>();

                int index = 0;
                while (index<hashValue.length()) {

                    list.add(hashValue.substring(index, Math.min(index+8,hashValue.length())));
                    index=index+8;
                }

                OHasValueSplit = new CountDownTimer(2000, 100) {

                    public void onTick(long millisUntilFinished) {
                        if (list.size()>count && count<8)
                        {
                            send(list.get(count));
                        }
                        count++;



                    }
                    public void onFinish() {
                        if (OHasValueSplit != null) {
                            OHasValueSplit.cancel();
                            OHasValueSplit = null;
                        }



                    }
                }.start();




            }

            @Override
            public void onFailure(Call<HashApiResponse> call, Throwable t) {
                ((BaseActivity) getActivity()).hideBusyAnimation();

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

    public void SendRR() {
        isHashValueResponseUnlock = false;

        CarryParkApplication.getCurrentActivity().runOnUiThread(this::sendrr);

    }

    private void sendrr() {
        send("RR;");
    }

    public void SendMM() {
        CarryParkApplication.getCurrentActivity().runOnUiThread(this::sendmm);
        // getActivity().runOnUiThread(this::sendmm);

    }


    public void sendmm() {
        send("MM;");
    }

  /*  private void checkStatusEveryTenSecond() {

        long period = 10000;
        Timer task = new Timer();
        task.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                send("st;");
            }
        }, 10000, period);


    }*/

    public boolean isDeviceStatusChecking() {
        return isDeviceStatusChecking;
    }

    public void postErrorLog(final String ErrorCode, final String message, String givenInputs, String battery, int ng, String eng, String jap) {

        Map<String, Object> candidateMap = new HashMap<>();
        candidateMap.put("device_id", CarryParkApplication.getScannedDeviceCode());
        candidateMap.put("error_code", ErrorCode);
        candidateMap.put("comments", givenInputs);
        candidateMap.put("error_message", message);
        candidateMap.put("battery_level", battery);
        candidateMap.put("error_message_en", eng);
        candidateMap.put("error_message_jp", jap);
        candidateMap.put("ng", 1);
        String acess_token = GloablMethods.API_HEADER + AppController.getString(CarryParkApplication.getmContext(), "acess_token");

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

    public void StatusResponseTenSecondResponse(String message) {
        message = message.replaceAll("\r", "");
        if (message.equalsIgnoreCase("C:R=OK")) {


        }
        String N, S, L, B, response = null;
        response = message.replaceAll("\r", "");
        if (response.contains("N") && response.contains("S") && response.contains("L") && response.contains("B")) {

            String[] separated = response.split(",");

            N = separated[0].substring(separated[0].length() - 1);
            S = separated[1].substring(separated[1].length() - 1);
            L = separated[2].substring(separated[2].length() - 1);
            B = separated[4].substring(separated[4].length() - 5);


            if (SharedPreferenceUtility.isScannedLock()) {
                if (S.equalsIgnoreCase("1") && L.equalsIgnoreCase("1")) {
                    ((BaseActivity) getActivity()).hideBusyAnimation();
                    ((BaseActivity) getActivity()).hideBusyAnimation();
                    sendSuccessMail();
                    isLeverLocked = true;

                    if (waitTimer != null) {
                        waitTimer.cancel();
                        waitTimer = null;
                    }
                    if (!successApiUnlockisCalled) {

                        CarryParkApplication.setIsStatusChecking(false);
                        SendMM();
                        callSuccessApiLock();
                    } else {

                        Intent intent = new Intent(getActivity(), BottomNavigation.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        intent.putExtra("page", ConstantProject.DeviceLockedFragment);
                        intent.putExtra(ConstantProject.isFrom, ConstantProject.PaymentSuccessFragment);
                        getActivity().startActivity(intent);


                    }


                } else {
                    if (CountofTheStatusChecking != 0 && CountofTheStatusChecking != 1) {
                        CountofTheStatusChecking++;
                        if (!isStopSound)
                            playSound();
                    } else {

                        CountofTheStatusChecking++;
                    }

                }
            } else {

            }


        }


    }
    // end: security IMPlementation


    @Override
    public void onDestroy() {
        if (connected != Connected.False)
            disconnect();
        getActivity().stopService(new Intent(getActivity(), SerialService.class));
        super.onDestroy();
    }

    private void disconnect() {
        connected = Connected.False;
        service.disconnect();

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        disconnect();
        if (waitTimer != null) {
            waitTimer.cancel();
            waitTimer = null;
        }

        if (waitingForButtonClick != null) {
            waitingForButtonClick.cancel();
            waitingForButtonClick = null;
        }
        if (ThreeMinitueTimingForLock != null) {
            ThreeMinitueTimingForLock.cancel();
            ThreeMinitueTimingForLock = null;
        }

        getFragmentManager().beginTransaction().remove(PaymentSuccessFragment.this).commitAllowingStateLoss();

    }

    public void clickMoveToNext() {

        if (!isButtonEnable) {
            isButtonEnable = true;
            isInitialStage = true;

            if (lockOrUnlock.contains("UNLOCKED")) {
                if (waitingForButtonClick != null) {
                    waitingForButtonClick.cancel();
                    waitingForButtonClick = null;
                }
                iv_lockerImg.setVisibility(View.GONE);
                gif.setVisibility(View.VISIBLE);


                if (!CarryParkApplication.isIsAwaitingForPutLockDown() && !CarryParkApplication.isIsSendMM()) {
                    ((BaseActivity) getActivity()).hideBusyAnimation();
                    manageResponse = new CountDownTimer(ConstantProject.TimeoutFiftySecond, 1000) {
                        //check each 2 seconds
                        public void onTick(long millisUntilFinished) {

                            if (!checktheResponseisReceived)
                            {
                                checktheResponseisReceived=false;
                                getActivity().runOnUiThread(this::status);
                            }
                            else {
                                if (manageResponse != null) {
                                    manageResponse.cancel();
                                    manageResponse = null;
                                }

                            }



                        }

                        private void status() {
                            isDeviceStatusChecking = true;
                            send("st;");
                        }

                        public void onFinish() {




                        }
                    }.start();



                }
                if (CarryParkApplication.isIsAwaitingForPutLockDown() && !CarryParkApplication.isIsSendMM()) {
                    ((BaseActivity) getActivity()).hideBusyAnimation();
                    ((BaseActivity) getActivity()).showBusyAnimation(waiting_for);
                    CarryParkApplication.setIsStatusChecking(true);
                    isHashValueResponseLock = false;
                    if (waitingForButtonClick != null) {
                        waitingForButtonClick.cancel();
                        waitingForButtonClick = null;
                    }
                    StatusCheckIn5Seconds();


                } else if (!CarryParkApplication.isIsAwaitingForPutLockDown() && CarryParkApplication.isIsSendMM()) {
                    CarryParkApplication.setIsStatusChecking(false);
                    ((BaseActivity) getActivity()).hideBusyAnimation();
                    if (!successApiUnlockisCalled) {
                        SendMM();
                        successApi(CarryParkApplication.getScannedDeviceCode(), "Lock", false);
                    }

                } else if (CarryParkApplication.isForgotToCallSuccessApi()) {
                    CarryParkApplication.setIsStatusChecking(false);
                    ((BaseActivity) getActivity()).hideBusyAnimation();
                    if (!successApiUnlockisCalled) {
                        SendMM();
                        successApi(CarryParkApplication.getScannedDeviceCode(), "Lock", false);
                    }
                }


            } else if (lockOrUnlock.contains("LOCKED")) {
                if (waitingForButtonClick != null) {
                    waitingForButtonClick.cancel();
                    waitingForButtonClick = null;
                }
                iv_lockerImg.setVisibility(View.GONE);
                gif.setVisibility(View.GONE);


//REMOVE

           if (CarryParkApplication.isIsSendRR() || CarryParkApplication.isForgotToCallSuccessApi()) {
                    SendRR();


                    CarryParkApplication.setIsSendRR(false);
                    if (!successApiUnlockisCalled)
                        successApi(CarryParkApplication.getScannedDeviceCode(), "UNLOCKED", true);


                } else {
                    ((BaseActivity) getActivity()).hideBusyAnimation();
                    String device_id = sharedPreferences.getString("device_id", null);
                   manageResponse = new CountDownTimer(ConstantProject.TimeoutFiftySecond, 1000) {
                       //check each 2 seconds
                       public void onTick(long millisUntilFinished) {

                           if (!checktheResponseisReceived)
                           {
                               checktheResponseisReceived=false;
                               getActivity().runOnUiThread(this::SendIHashValue);
                           }
                           else {
                               if (manageResponse != null) {
                                   manageResponse.cancel();
                                   manageResponse = null;
                               }
                               if (IhashValueSplit != null) {
                                   IhashValueSplit.cancel();
                                   IhashValueSplit = null;
                               }
                               callAPIUnLock(device_id);
                           }



                       }

                       private void SendIHashValue() {

                           //IhashValueSplit
                          // send("I:" + CarryParkApplication.getCloseHashValue() + ";");
                          count =0;
                           String hashValue= "I:" + CarryParkApplication.getCloseHashValue() + ";";//35
                           List<String> list= new ArrayList<String>();

                           int index = 0;
                           while (index<hashValue.length()) {

                               list.add(hashValue.substring(index, Math.min(index+8,hashValue.length())));
                               index=index+8;
                           }

                           IhashValueSplit = new CountDownTimer(2000, 100) {

                               public void onTick(long millisUntilFinished) {
                                   if (list.size()>count && count<8)
                                   {
                                       send(list.get(count));
                                   }
                                   count++;



                               }
                               public void onFinish() {
                                   if (IhashValueSplit != null) {
                                       IhashValueSplit.cancel();
                                       IhashValueSplit = null;
                                   }



                               }
                           }.start();


                       }

                       public void onFinish() {




                       }
                   }.start();



            }


            }
        }

    }




    public String randomAlphaNumeric(int count) {
        StringBuilder builder = new StringBuilder();
        while (count-- != 0) {
            int character = (int) (Math.random() * ALPHA_NUMERIC_STRING.length());
            builder.append(ALPHA_NUMERIC_STRING.charAt(character));
        }
        return builder.toString();

    }

    public String randomDigit(int count) {
        StringBuilder builder = new StringBuilder();
        while (count-- != 0) {
            int character = (int) (Math.random() * DIGITS.length());
            builder.append(DIGITS.charAt(character));
        }
        return builder.toString();

    }

    public String randomAlphaCaps(int count) {
        StringBuilder builder = new StringBuilder();
        while (count-- != 0) {
            int character = (int) (Math.random() * ALPHACAPS.length());
            builder.append(ALPHACAPS.charAt(character));
        }
        return builder.toString();

    }

    public String randomAlphaSmall(int count) {
        StringBuilder builder = new StringBuilder();
        while (count-- != 0) {
            int character = (int) (Math.random() * ALPHASMALL.length());
            builder.append(ALPHASMALL.charAt(character));
        }
        return builder.toString();

    }

    public void playSound() {

        VibratePhone();
        MediaPlayer mediaPlaye = MediaPlayer.create(CarryParkApplication.getCurrentContext(), R.raw.message_beeps);
        mediaPlaye.start(); // no need to call prepare(); create() does that for you


    }

    private void startTimer() {
        ThreeMinitueTimingForLock = new CountDownTimer(ConstantProject.TimoutOneminitue, 50) {

            public void onTick(long millisUntilFinished) {
                //called every 10 second, which could be used to
                //send messages or some other action


            }

            public void onFinish() {
                if (waitTimer != null) {
                    waitTimer.cancel();
                    waitTimer = null;
                }
                disconnect();

               /* DialogManager.showAlertSingleActionDialog(CarryParkApplication.getCurrentActivity(), CarryParkApplication.getCurrentContext().getResources().getString(R.string.timeOut), "ok", new DialogManager.IUniActionDialogOnClickListener() {
                    @Override
                    public void onPositiveClick() {
                        Intent intent = new Intent(CarryParkApplication.getCurrentContext(), BottomNavigation.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        intent.putExtra("page", ConstantProject.SplashActivity);
                        startActivity(intent);
                    }
                });*/

                //  disconnectAndMoveToHome();


            }
        }.start();
    }

    private void checkButtonClicked() {
        waitingForButtonClick = new CountDownTimer(ConstantProject.TimoutOneminitue, 5000) {

            public void onTick(long millisUntilFinished) {
                //called every 10 second, which could be used to
                //send messages or some other action
                if (buttonCount != 0) {
                    if (!isShowed) {
                        isShowed = true;


                        if (SharedPreferenceUtility.isJapanease()) {
                            DialogManager.showAlertSingleActionDialogJP(CarryParkApplication.getCurrentActivity(), "施錠ボタンをタップしてください", "", new DialogManager.IUniActionDialogOnClickListener() {
                                @Override
                                public void onPositiveClick() {

                                }
                            });
                        } else {
                            DialogManager.showAlertSingleActionDialog(CarryParkApplication.getCurrentActivity(), CarryParkApplication.getCurrentContext().getResources().getString(R.string.tapLockButton), "", new DialogManager.IUniActionDialogOnClickListener() {
                                @Override
                                public void onPositiveClick() {

                                }
                            });
                        }


                        playSound();

                    } else {
                        playSound();
                    }
                }


                buttonCount++;

            }

            public void onFinish() {
                String msg="時間切れです。再度お試し下さい";
                if (SharedPreferenceUtility.isJapanease()) {
                   msg="時間切れです。再度お試し下さい。";
                }
                else if (SharedPreferenceUtility.isEnglish())
                {
                    msg="Timeout. Please try again";
                }
                else if (SharedPreferenceUtility.isKorean())
                {
                    msg="타임 아웃. 다시 시도하십시오";
                }
                else if (SharedPreferenceUtility.isChinease())
                {
                    msg="超时。 请再试一遍";
                }

                ///
                disconnect();

                DialogManager.showAlertSingleActionDialog(CarryParkApplication.getCurrentActivity(), msg, "ok", new DialogManager.IUniActionDialogOnClickListener() {
                    @Override
                    public void onPositiveClick() {
                        Activity activity = getActivity();
                        if (activity != null && isAdded()) {

                            Intent intent = new Intent(CarryParkApplication.getCurrentContext(), BottomNavigation.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            intent.putExtra("page", ConstantProject.SplashActivity);
                            startActivity(intent);
                        }

                    }
                });

                // disconnectAndMoveToHome();

            }
        }.start();
    }


    private void StatusCheckIn5Seconds() {
        ll_lock_click.setVisibility(View.GONE);
        CarryParkApplication.getCurrentActivity().runOnUiThread(this::checkStatusEveryTenSecond);

    }


    public void VibratePhone() {
        Vibrator v = (Vibrator) CarryParkApplication.getCurrentContext().getSystemService(Context.VIBRATOR_SERVICE);
// Vibrate for 500 milliseconds
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            v.vibrate(VibrationEffect.createOneShot(500, VibrationEffect.DEFAULT_AMPLITUDE));
        } else {
            //deprecated in API 26
            v.vibrate(500);
        }
    }

    public void showAwaitingLock() {


        if (CarryParkApplication.isIsAwaitingForPutLockDown() && !CarryParkApplication.isIsSendMM()) {
            // startTimer();
            ((BaseActivity) getActivity()).hideBusyAnimation();
            ((BaseActivity) getActivity()).showBusyAnimation(waiting_for);
            CarryParkApplication.setIsStatusChecking(true);
            isHashValueResponseLock = false;
            if (waitingForButtonClick != null) {
                waitingForButtonClick.cancel();
                waitingForButtonClick = null;
            }
            StatusCheckIn5Seconds();


        } else {
            if (!CarryParkApplication.isIsSendMM())
                checkButtonClicked();

        }
    }

  /*  public void disconnectAndMoveToHome()
    {
        disconnect();
        Intent intent = new Intent(CarryParkApplication.getCurrentContext(), BottomNavigation.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent.putExtra("page", ConstantProject.SplashActivity);
        startActivity(intent);

    }
*/


    public void sentAppLog(String comments,String commentEn,String commentjp) {


        String acess_token = GloablMethods.API_HEADER + AppController.getString(CarryParkApplication.getmContext(), "acess_token");
        Map<String, Object> candidateMap = new HashMap<>();
        candidateMap.put("device_id", CarryParkApplication.getScannedDeviceCode());
        candidateMap.put("app", ConstantProject.DEVICE);
        candidateMap.put("device_status", SharedPreferenceUtility.getDevicePresentStatus()+" "+tv_comment.getText().toString());
        candidateMap.put("payment_require", CarryParkApplication.IsPaymentByPassingNoRequired());
        candidateMap.put("device_model", ((BaseActivity) getActivity()).getDeviceName());
        candidateMap.put("device_version", ((BaseActivity) getActivity()).DeviceVersion());
        candidateMap.put("scanned_qr_code", CarryParkApplication.getScannedDeviceCode());
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

    public void sendSuccessMail() {

        Map<String, Object> candidateMap = new HashMap<>();
        candidateMap.put("device_id", CarryParkApplication.getScannedDeviceCode());
        String acess_token = GloablMethods.API_HEADER + AppController.getString(CarryParkApplication.getmContext(), "acess_token");
        apiService.sendSuccessMail(acess_token,candidateMap).enqueue(new Callback<ChangeEmailResponseModel>() {
            @Override
            public void onResponse(Call<ChangeEmailResponseModel> call, Response<ChangeEmailResponseModel> response) {



            }


            @Override
            public void onFailure(Call<ChangeEmailResponseModel> call, Throwable t) {

            }
        });
    }


}
