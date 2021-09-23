package net.simplifiedcoding.carrypark;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.BluetoothLeScanner;
import android.content.*;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.location.LocationManager;
import android.os.*;
import android.util.Log;
import android.view.*;
import android.widget.*;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
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
import net.rest.model.HashApiResponse;
import net.rest.model.LockerDetailsResponse;
import net.rest.model.LogAppErrorModel;
import net.ui.BottomNavigation;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import java.io.IOException;
import java.text.DateFormat;
import java.util.*;

import static android.content.Context.MODE_PRIVATE;
import static android.telephony.MbmsDownloadSession.RESULT_CANCELLED;
import static net.others.BaseActivity.finalArrayOfFoundBTDevices;


public class CodeScannerFragment extends Fragment {
    boolean isDeepLink;


    boolean isQrcodeScanned/*,isClickedPopup*/;
    String scannedQRCode = "";
    /*SurfaceView surfaceView;
    private BarcodeDetector barcodeDetector;
    private CameraSource cameraSource;
   private static final int REQUEST_CAMERA_PERMISSION = 201; */
    CountDownTimer /*waitingForThreeMinitueForScan =null*/TimmerForScannedResult;
    LockerDetailsResponse lockerDetailsResponseList = new LockerDetailsResponse();
    String avilableDevice = "";
    CountDownTimer waitForConnecttoDevice,waitForConnecttoDeviceSecond;


    // private CodeScanner mCodeScanner;
    ImageView imv_close;
    ApiInterface apiService;
    private SharedPreferences sharedPreferences;
    boolean hasInitialPaymentPreviouslyDone;
    Button btn_scan;
    String DeviceCode="";


    //Camera
    static final String TAG = "CamTest";
    static final int MY_PERMISSIONS_REQUEST_CAMERA = 1242;
    private static final int MSG_CAMERA_OPENED = 1;
    private static final int MSG_SURFACE_READY = 2;
    //private final Handler mHandler = new Handler(this);
   /* private Handler mHandlerSec = new Handler();
    SurfaceView mSurfaceView;
    SurfaceHolder mSurfaceHolder;
    CameraManager mCameraManager;
    String[] mCameraIDsList;
    CameraDevice.StateCallback mCameraStateCB;
    CameraDevice mCameraDevice;
    CameraCaptureSession mCaptureSession;
    boolean mSurfaceCreated = true;
    boolean mIsCameraConfigured = false;
    private Surface mCameraSurface = null;
*/
    // security
    public String DeviceNameBLE = "", DeviceID = "";
    BluetoothManager btManager;
    BluetoothAdapter btAdapter;
    BluetoothLeScanner btScanner;
    private final static int REQUEST_ENABLE_BT = 1;
    private static final int PERMISSION_REQUEST_COARSE_LOCATION = 1;
    String languageToLoad="en";


    private enum ScanState {NONE, LESCAN, DISCOVERY, DISCOVERY_FINISHED}

    private ScanState scanState = ScanState.NONE;
    private static final long LESCAN_PERIOD = ConstantProject.TimoutThirtySecond; // similar to bluetoothAdapter.startDiscovery
    private Handler leScanStopHandler = new Handler();
    private BluetoothAdapter.LeScanCallback leScanCallback;
    private BroadcastReceiver discoveryBroadcastReceiver;
    private IntentFilter discoveryIntentFilter;

    private Menu menu;
    private BluetoothAdapter bluetoothAdapter;
    private ArrayList<BluetoothDevice> listItems = new ArrayList<>();
    private ArrayAdapter<BluetoothDevice> listAdapter;
    String Address = "";
    boolean isMovedToConnection = false;
    String timeLog="";


    BluetoothAdapter mBluetoothAdapter;

    // connection
    private enum Connected {
        False, Pending, True
    }


    String isFrom = null;
    private boolean isPaymentCancell;

    private SerialService service;

    public CodeScannerFragment() {
        leScanCallback = (device, rssi, scanRecord) -> {
            if (device != null && getActivity() != null) {
                getActivity().runOnUiThread(() -> {
                    updateScan(device);
                });
            }
        };
        discoveryBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent.getAction().equals(BluetoothDevice.ACTION_FOUND)) {
                    BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                    if (device.getType() != BluetoothDevice.DEVICE_TYPE_CLASSIC && getActivity() != null) {
                        getActivity().runOnUiThread(() -> updateScan(device));
                    }
                }
                if (intent.getAction().equals((BluetoothAdapter.ACTION_DISCOVERY_FINISHED))) {
                    scanState = ScanState.DISCOVERY_FINISHED; // don't cancel again
                    stopScan();
                }
            }
        };
        discoveryIntentFilter = new IntentFilter();
        discoveryIntentFilter.addAction(BluetoothDevice.ACTION_FOUND);
        discoveryIntentFilter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        if (getActivity().getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH))
            bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        listAdapter = new ArrayAdapter<BluetoothDevice>(getActivity(), 0, listItems) {
            @Override
            public View getView(int position, View view, ViewGroup parent) {
                BluetoothDevice device = listItems.get(position);
                if (view == null)
                    view = getActivity().getLayoutInflater().inflate(R.layout.device_list_item, parent, false);
                TextView text1 = view.findViewById(R.id.text1);
                TextView text2 = view.findViewById(R.id.text2);
                if (device.getName() == null || device.getName().isEmpty())
                    text1.setText("<unnamed>");
                else
                    text1.setText(device.getName());
                text2.setText(device.getAddress());
                return view;
            }
        };


    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
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


        return inflater.inflate(R.layout.activity_main, null);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        apiService =
                ApiClient.getClient().create(ApiInterface.class);
        sharedPreferences = getActivity().getSharedPreferences("carry_park", MODE_PRIVATE);
        //  isButtonClicked = false;
        // CodeScannerView scannerView = view.findViewById(R.id.scanner_view);
        // mCodeScanner = new CodeScanner(getContext(), scannerView);
        // imv_close = (ImageView) view.findViewById(R.id.imv_close);
        btn_scan = (Button) view.findViewById(R.id.btn_scan);
        // mCodeScanner.setAutoFocusEnabled(true);
        //mCodeScanner.setTouchFocusEnabled(false);
      //  surfaceView = (SurfaceView) view.findViewById(R.id.SurfaceViewPreview);
        CarryParkApplication.setScannedDeviceCode("");

        CarryParkApplication.setIsFromSplash(false);

        avilableDevice = "";
        SharedPreferenceUtility.SavePinVerified(true);
        CarryParkApplication.setIsInuseScan(false);
        getActivity().getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN); //show the activity in full screen

        // callTimer();
        btn_scan.setClickable(true);
        /*scannerView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //mCodeScanner.startPreview();
            }
        });*/
        btManager = (BluetoothManager) getActivity().getSystemService(Context.BLUETOOTH_SERVICE);
        btAdapter = btManager.getAdapter();
        btScanner = btAdapter.getBluetoothLeScanner();
        if (btAdapter != null)
            btAdapter.enable();


        btn_scan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                initializeBluetooth();




           /*  if (waitingForThreeMinitueForScan!=null)
                {
                    waitingForThreeMinitueForScan.cancel();
                    waitingForThreeMinitueForScan =null;
                }*/

                BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
                if (mBluetoothAdapter == null) {
                    DialogManager.showAlertSingleActionDialog(CarryParkApplication.getCurrentActivity(), CarryParkApplication.getCurrentContext().getResources().getString(R.string.bleNotavbl), "ok", new DialogManager.IUniActionDialogOnClickListener() {
                        @Override
                        public void onPositiveClick() {
                            Intent intent = new Intent(CarryParkApplication.getCurrentContext(), BottomNavigation.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            intent.putExtra("page", ConstantProject.SplashActivity);
                            startActivity(intent);

                        }
                    });
                } else if (!mBluetoothAdapter.isEnabled()) {
                    DialogManager.showAlertSingleActionDialog(CarryParkApplication.getCurrentActivity(), CarryParkApplication.getCurrentContext().getResources().getString(R.string.bleNotavbldesc), "ok", new DialogManager.IUniActionDialogOnClickListener() {
                        @Override
                        public void onPositiveClick() {
                            Intent intent = new Intent(CarryParkApplication.getCurrentContext(), BottomNavigation.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            intent.putExtra("page", ConstantProject.SplashActivity);
                            startActivity(intent);
                        }
                    });
                } else {
                    callTimerForScannedResult();
                    btn_scan.setVisibility(View.GONE);
                    //isButtonClicked = true;


                }


            }
        });


        /////////////////////////////////////////  security implementation


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


        startScan();
        if (getArguments()!=null)
        {
            if (getArguments().getString("qrcode")!=null)

            {

                if (CarryParkApplication.isIsRescan())
                {
                    Log.e("comments","Code Scanner"+""+1);

                    DeviceNameBLE=CarryParkApplication.getDeviceName();
                    moveToConnection();

                }
                else {
                    Log.e("comments","Code Scanner"+""+2);
                    String qrcode= getArguments().getString("qrcode");
                    if (qrcode.contains("deeplink"))
                    {
                        CarryParkApplication.setScannedDeviceCode(SharedPreferenceUtility.getDeepLinkDeviceCode());
                        isDeepLink=true;
                        getLockerDetailsFromScanner(SharedPreferenceUtility.getDeepLinkDeviceCode());

                    }
                    else {
                        Log.e("comments","Code Scanner"+""+3);
                        isDeepLink=false;



                        moveToInitialPaymentFragment(qrcode);
                        CarryParkApplication.setScannedDeviceCode(qrcode);
                    }

                }


            }
            else {
                Log.e("comments","Code Scanner"+""+4);
            }
        }
        else {
            Log.e("comments","Code Scanner"+""+5);
        }


    }


    public void callTimerForScannedResult() {


        TimmerForScannedResult = new CountDownTimer(ConstantProject.TimeoutFiftySecond, 50) {

            public void onTick(long millisUntilFinished) {
                //called every 10 second, which could be used to
                //send messages or some other action

            }

            public void onFinish() {
                if (!isQrcodeScanned) {
                    DialogManager.showAlertSingleActionDialog(CarryParkApplication.getCurrentActivity(), CarryParkApplication.getCurrentContext().getResources().getString(R.string.timeoutError), "ok", new DialogManager.IUniActionDialogOnClickListener() {
                        @Override
                        public void onPositiveClick() {
                            Activity activity = getActivity();
                            if (activity != null && isAdded()) {
                                Intent intent = new Intent(CarryParkApplication.getCurrentContext(), BottomNavigation.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                intent.putExtra("page", ConstantProject.CodeScannerFragment);
                                startActivity(intent);
                            }


                        }
                    });

                }


            }
        }.start();

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 0) {
            if (resultCode == getActivity().RESULT_OK) {
                String contents = data.getStringExtra("SCAN_RESULT");
            }
            if (resultCode == RESULT_CANCELLED) {
                //handle cancel
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        getActivity().registerReceiver(discoveryBroadcastReceiver, discoveryIntentFilter);
     //   initialiseDetectorsAndSources();

    }

    @Override
    public void onPause() {
        super.onPause();
        stopScan();
        getActivity().unregisterReceiver(discoveryBroadcastReceiver);
        /*try {
            if (cameraSource != null)
                cameraSource.release();
        } catch (Exception e) {
            e.printStackTrace();
        }*/


    }

    public void moveToInitialPaymentFragment(String scanned_device_code) {

        String msg = "", okMsg = CarryParkApplication.getCurrentContext().getResources().getString(R.string.proceed);
        String device_code = scanned_device_code;
        String device_id = "";
        if(device_code.contains("=")) {
            String[] id = device_code.split("=");
            device_id = id[1];

        }else {

            device_id = scanned_device_code;
        }

        CarryParkApplication.setScannedDeviceCode(device_id);

        if (SharedPreferenceUtility.isJapanease()) {
            msg = device_id + " " + CarryParkApplication.getCurrentContext().getResources().getString(R.string.deviceConfirmation);

        } else {
            msg = CarryParkApplication.getCurrentContext().getResources().getString(R.string.deviceConfirmation) + " " + device_id;
        }

        String finalMsg = msg;
        String finalMsg1 = msg;
        getActivity().runOnUiThread(() -> {
            DialogManager.showConfirmDialogue(CarryParkApplication.getCurrentActivity(), finalMsg1, "ok", okMsg, new DialogManager.IMultiActionDialogOnClickListener() {

                @Override
                public void onPositiveClick() {
                    if (TimmerForScannedResult != null) {
                        TimmerForScannedResult.cancel();
                        TimmerForScannedResult = null;
                    }


                    if(scanned_device_code.contains("http://159.65.150.119")||scanned_device_code.contains("http://167.172.39.84")) {

                        if (scanned_device_code.contains("=")) {
                            String[] id = scanned_device_code.split("=");
                            String device_id = id[1];

                            getLockerDetailsFromScanner(device_id);

                        } else {

                            getLockerDetailsFromScanner(scanned_device_code);

                        }

                    }else {

                        getLockerDetailsFromScanner(scanned_device_code);


                    }




                }

                @Override
                public void onNegativeClick() {
                    if (TimmerForScannedResult != null) {
                        TimmerForScannedResult.cancel();
                        TimmerForScannedResult = null;
                    }
                    //getActivity().runOnUiThread(this::moveToCodeScanFrag);
                    Intent intent = new Intent(getActivity(), BottomNavigation.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    intent.putExtra("page", ConstantProject.CodeScannerFragment);
                    intent.putExtra(ConstantProject.isFrom, ConstantProject.CodeScannerFragment);
                    getActivity().startActivity(intent);


                }


            });

        });


        // surfaceView.setVisibility(View.GONE);


    }

    private void getLockerDetailsFromScanner(String scannedDeviceCode) {

        timeLog="Api "+ DateFormat.getTimeInstance().format(new Date());

        ((BaseActivity) getActivity()).showBusyAnimation("");
        Map<String, Object> candidateMap = new HashMap<>();

        if (SharedPreferenceUtility.isJapanease()) {
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

        candidateMap.put("device_id", scannedDeviceCode);
        String acess_token = GloablMethods.API_HEADER + AppController.getString(getActivity(), "acess_token");

        apiService.lockerDetails(acess_token, candidateMap).enqueue(new Callback<LockerDetailsResponse>() {
            @Override
            public void onResponse(Call<LockerDetailsResponse> call, Response<LockerDetailsResponse> response) {

                String code = String.valueOf(response.code());
                if (response.code() == 200) {
                    if (response!=null && response.body().getData()!=null)
                    {
                        lockerDetailsResponseList.setData(response.body().getData());
                        lockerDetailsResponseList.setMessage(response.body().getMessage());
                        lockerDetailsResponseList.setSuccess(response.body().getSuccess());
                        CarryParkApplication.setLockerDetailsResponseList(lockerDetailsResponseList);
                        SharedPreferenceUtility.saveDeviceCurrentStatus(response.body().getData().getPresentStatus());

                    }
                     // progressDialog.dismiss();
                    Boolean status = response.body().getSuccess();
                    if (status == true && response.body().getData() != null) {
                        String mobile = response.body().getData().getMobile();
                        String email = response.body().getData().getEmail();
                        String first_name = response.body().getData().getFirstName();
                        String cmpny_name = response.body().getData().getCompany_name();

                        CarryParkApplication.setPersonMobile(mobile);
                        CarryParkApplication.setPersonemail(email);
                        CarryParkApplication.setfirst_name(first_name);
                        CarryParkApplication.setcmpny_name(cmpny_name);


                        String device_status = response.body().getData().getLocation();
                        CarryParkApplication.setScannedDeviceName(response.body().getData().getDeviceName());
                        CarryParkApplication.setPlace(device_status);
                        CarryParkApplication.setDeviceName(response.body().getData().getDeviceName());
                        DeviceNameBLE = response.body().getData().getDeviceName();
                        CarryParkApplication.setDeviceName(DeviceNameBLE);
                        DeviceID = response.body().getData().getDeviceId();

                        if (response.body().getData().getPayment_require() != null) {
                            CarryParkApplication.setIsPaymentByPassingNotRequired(response.body().getData().getPayment_require());
                        }
                        if (response.body().getData().getDeviceName() != null && !response.body().getData().getDeviceName().isEmpty())


                            if (response.body().getData().getOpen_hash() != null) {
                                CarryParkApplication.setCloseHashValue(response.body().getData().getOpen_hash());
                            }
                        String ratePerHour = String.valueOf(response.body().getData().getRate_per_hour());

                        for (int i = 0; i < CarryParkApplication.getUsedLockerList().size(); i++) {
                            if (CarryParkApplication.getUsedLockerList().get(i).getDeviceId().equalsIgnoreCase(response.body().getData().getDeviceId()) && !response.body().getData().getPresentStatus().equalsIgnoreCase("locked")) {
                                SharedPreferenceUtility.saveScannedDeviceInfo_isLock(true);
                                hasInitialPaymentPreviouslyDone = true;
                            }

                        }


                        if (response.body().getData().getStatus().equals("active") && response.body().getData().getPresentStatus().equalsIgnoreCase("unlocked")) {

                            SharedPreferenceUtility.saveScannedDeviceInfo_isLock(true);
                        }
                        if (response.body().getData().getPresentStatus().equalsIgnoreCase("locked")) {
                            SharedPreferenceUtility.saveScannedDeviceInfo_isLock(false);

                        }

                        CarryParkApplication.setHasInitialPaymentPreviouslyDone(hasInitialPaymentPreviouslyDone);
                        CarryParkApplication.setScannedDeviceCode(scannedDeviceCode);
                        ((BaseActivity) getActivity()).hideBusyAnimation();
                        if (isDeepLink)
                        {
                            moveToConnection();
                        }
                        else {
                            nextDialogue(scannedDeviceCode);



                        }

                    } else if (status == false) {
                        String msg = response.body().getMessage();
                        int lenn = 0;
                        String[] msge = msg.split("/");
                        String show_msg = msge[0];
                        String secnd = msge[1];
                        String[] third = secnd.split("=");
                        String five = third[1];
                        String[] six = five.split(",");
                        String first_name = six[0];
                        String seven = third[3];
                        String[] eight = seven.split(",");
                        String email = eight[0];
                        String nine = third[5];
                        String[] ten = nine.split(",");
                        String mobile = ten[0];
                        String cmpny_name = "";
                         lenn = third.length;
                         if(lenn>6){
                             cmpny_name = third[6];

                         }



                        DialogManager.showCustDialog(CarryParkApplication.getCurrentActivity(),show_msg,first_name,email,mobile,cmpny_name , new DialogManager.IUniActionDialogOnClickListener() {
                            @Override
                            public void onPositiveClick() {
                                ((BaseActivity) getActivity()).hideBusyAnimation();
                                Intent intent = new Intent(CarryParkApplication.getCurrentContext(), BottomNavigation.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                intent.putExtra("page", ConstantProject.SplashActivity);
                                startActivity(intent);
                            }


                        });

                     /*   DialogManager.showAlertSingleActionDialog(CarryParkApplication.getCurrentActivity(), msg, "ok", new DialogManager.IUniActionDialogOnClickListener() {
                            @Override
                            public void onPositiveClick() {

                                //getActivity().runOnUiThread(this::moveToHomeFrag);
                                Intent intent = new Intent(CarryParkApplication.getCurrentContext(), BottomNavigation.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                intent.putExtra("page", ConstantProject.SplashActivity);
                                startActivity(intent);

                            }
                        });*/



                        /*targetFragment = new ScanQRCodeFragment();
                        InUseFragment ldf = new InUseFragment ();
                        replaceFragment(targetFragment,ldf);*/
                    }
                }/*else if(response.code() == 200 && response.body().getData() == null) {
                    progressDialog.dismiss();
                    String msg = response.body().getMessage();
                }*/ else if (response.code() == 404) {
                    //progressDialog.dismiss();

                    Gson gson = new GsonBuilder().create();
                    LockerDetailsResponse pojo = new LockerDetailsResponse();
                    try {
                        pojo = gson.fromJson(response.errorBody().string(), LockerDetailsResponse.class);
                        DialogManager.showAlertSingleActionDialog(CarryParkApplication.getCurrentActivity(),pojo.getMessage(), "ok", new DialogManager.IUniActionDialogOnClickListener() {
                            @Override
                            public void onPositiveClick() {
                                ((BaseActivity) getActivity()).hideBusyAnimation();
                                Intent intent = new Intent(CarryParkApplication.getCurrentContext(), BottomNavigation.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                intent.putExtra("page", ConstantProject.SplashActivity);
                                startActivity(intent);
                            }


                        });
                    } catch (IOException e) {
                    }





                }
                else {
                   /* DialogManager.showAlertSingleActionDialog(CarryParkApplication.getCurrentActivity(),CarryParkApplication.getCurrentActivity().getResources().getString(R.string.api_error), "ok", new DialogManager.IUniActionDialogOnClickListener() {
                        @Override
                        public void onPositiveClick() {
                            postErrorLog("","デバイス情報が一致していません","",1,"","Device information does not match","デバイス情報が一致していません");
                          //  sentAppLog("network connection error","network connection error"+response.code(),CarryParkApplication.getCurrentActivity().getResources().getString(R.string.no_internet));

                            ((BaseActivity) getActivity()).hideBusyAnimation();
                            Intent intent = new Intent(CarryParkApplication.getCurrentContext(), BottomNavigation.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            intent.putExtra("page", ConstantProject.SplashActivity);
                            startActivity(intent);
                        }


                    });*/

                    String msg = response.body().getMessage();
                    int lenn = 0;
                    String[] msge = msg.split("/");
                    String show_msg = msge[0];
                    String secnd = msge[1];
                    String[] third = secnd.split("=");
                    String five = third[1];
                    String[] six = five.split(",");
                    String first_name = six[0];
                    String seven = third[3];
                    String[] eight = seven.split(",");
                    String email = eight[0];
                    String nine = third[5];
                    String[] ten = nine.split(",");
                    String mobile = ten[0];
                    String cmpny_name = "";
                    lenn = third.length;
                    if(lenn>6){
                        cmpny_name = third[6];

                    }



                    DialogManager.showCustDialog(CarryParkApplication.getCurrentActivity(),show_msg,first_name,email,mobile,cmpny_name , new DialogManager.IUniActionDialogOnClickListener() {
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
            }

            @Override
            public void onFailure(Call<LockerDetailsResponse> call, Throwable t) {
                ((BaseActivity) getActivity()).hideBusyAnimation();
                if (!Utils.isNetworkConnectionAvailable(CarryParkApplication.getCurrentContext())) {
                    DialogManager.showAlertSingleActionDialog(CarryParkApplication.getCurrentActivity(), CarryParkApplication.getCurrentContext().getResources().getString(R.string.no_internet), "ok", new DialogManager.IUniActionDialogOnClickListener() {
                        @Override
                        public void onPositiveClick() {

                        }
                    });

                } else {
                    ((BaseActivity) getActivity()).hideBusyAnimation();
                    DialogManager.showAlertSingleActionDialog(CarryParkApplication.getCurrentActivity(),CarryParkApplication.getCurrentContext().getResources().getString(R.string.no_internet), "ok", new DialogManager.IUniActionDialogOnClickListener() {
                        @Override
                        public void onPositiveClick() {

                        }


                    });                   }
            }
        });
    }

    public void moveToNext(String scannedDeviceCode) {
        SharedPreferenceUtility.saveDeepLinkDeviceDetails(DeviceNameBLE,Address,DeviceID);

        Intent intent = new Intent(getActivity(), BottomNavigation.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent.putExtra("page", ConstantProject.InitialPaymentFragment);
        intent.putExtra("qrcode", scannedDeviceCode);
        intent.putExtra("device", scannedDeviceCode);
        intent.putExtra("is_from", ConstantProject.CodeScannerFragment);
        intent.putExtra("isAliPay", false);
        getActivity().startActivity(intent);
    }


    public void nextDialogue(String scannedDeviceCode) {
        String nextMsg = "";
        if (!CarryParkApplication.isIsCheckIn() && !SharedPreferenceUtility.isScannedLock()) {
            // moveToNext(scannedDeviceCode);
            moveToConnection();


        } else if (CarryParkApplication.isIsCheckIn() && !SharedPreferenceUtility.isScannedLock()) {
            nextMsg = CarryParkApplication.getCurrentContext().getString(R.string.takeOut);

            DialogManager.showConfirmDialogue(CarryParkApplication.getCurrentActivity(), CarryParkApplication.getCurrentContext().getResources().getString(R.string.scanCheckOutMsg), "ok", nextMsg, new DialogManager.IMultiActionDialogOnClickListener() {
                @Override
                public void onPositiveClick() {
                    //moveToNext(scannedDeviceCode);
                    moveToConnection();
                }

                @Override
                public void onNegativeClick() {

                    //getActivity().runOnUiThread(this::moveToCodeScanFrag);
                    Intent intent = new Intent(getActivity(), BottomNavigation.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    intent.putExtra("page", ConstantProject.CodeScannerFragment);
                    intent.putExtra(ConstantProject.isFrom, ConstantProject.CodeScannerFragment);
                    getActivity().startActivity(intent);

                }
            });
        } else if (!CarryParkApplication.isIsCheckIn() && SharedPreferenceUtility.isScannedLock()) {
            nextMsg = CarryParkApplication.getCurrentContext().getString(R.string.deposit);

            DialogManager.showConfirmDialogue(CarryParkApplication.getCurrentActivity(), CarryParkApplication.getCurrentContext().getResources().getString(R.string.scanCheckinMsg), "ok", nextMsg, new DialogManager.IMultiActionDialogOnClickListener() {
                @Override
                public void onPositiveClick() {
                    // moveToNext(scannedDeviceCode);
                    moveToConnection();
                }

                @Override
                public void onNegativeClick() {
                    Intent intent = new Intent(getActivity(), BottomNavigation.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    intent.putExtra("page", ConstantProject.CodeScannerFragment);
                    intent.putExtra(ConstantProject.isFrom, ConstantProject.CodeScannerFragment);
                    getActivity().startActivity(intent);
                }
            });
        } else if (CarryParkApplication.isIsCheckIn() && SharedPreferenceUtility.isScannedLock()) {
            // moveToNext(scannedDeviceCode);
            moveToConnection();
        }

    }

    public void moveToConnection() {
        if (!isMovedToConnection) {
            ((BaseActivity) getActivity()).showBusyAnimation("");
            isMovedToConnection = true;
            if (CarryParkApplication.getDeviceName() != null && !CarryParkApplication.getDeviceName().isEmpty()) {
                checkStatusEveryIntervels();
            }

        }

    }

    ///Camera

    @Override
    public void onStart() {
        super.onStart();

        //requesting permission
        int permissionCheck;
        permissionCheck = ContextCompat.checkSelfPermission(getContext(), Manifest.permission.CAMERA);
        if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), Manifest.permission.CAMERA)) {

            } else {
                ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.CAMERA}, MY_PERMISSIONS_REQUEST_CAMERA);
            }
        } else {
            /*try {
                mCameraManager.openCamera(mCameraIDsList[0], mCameraStateCB, new Handler());
            } catch (CameraAccessException e) {
                e.printStackTrace();
            }*/
        }

        /*DialogManager.showUniActionDialog(CarryParkApplication.getCurrentActivity(), CarryParkApplication.getCurrentContext().getResources().getString(R.string.tap_scan_to_read), "ok", new DialogManager.IUniActionDialogOnClickListener() {
            @Override
            public void onPositiveClick() {
               // isClickedPopup=true;
            }
        });*/
    }

    @Override
    public void onStop() {
        super.onStop();

    }
    //////////////////////////// Security Implementation


    private void initializeBluetooth() {


        final BluetoothManager bluetoothManager =
                (BluetoothManager) getActivity().getSystemService(Context.BLUETOOTH_SERVICE);
        mBluetoothAdapter = bluetoothManager.getAdapter();

        // Checks if Bluetooth is supported on the device.
        if (mBluetoothAdapter == null) {
            return;
        } else {
            if (!mBluetoothAdapter.isEnabled()) {
                mBluetoothAdapter.enable();
            }
        }
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        menu = null;


       /* if (waitingForThreeMinitueForScan!=null)
        {
            waitingForThreeMinitueForScan.cancel();
            waitingForThreeMinitueForScan =null;
        }*/
        if (TimmerForScannedResult != null) {
            TimmerForScannedResult.cancel();
            TimmerForScannedResult = null;
        }
    }


    @SuppressLint("StaticFieldLeak") // AsyncTask needs reference to this fragment
    private void startScan() {
        if (scanState != ScanState.NONE)
            return;
        scanState = ScanState.LESCAN;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (CarryParkApplication.getCurrentContext().checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                scanState = ScanState.NONE;
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setTitle(R.string.location_permission_title);
                builder.setMessage(R.string.location_permission_message);
                builder.setPositiveButton(android.R.string.ok,
                        (dialog, which) -> requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, 0));
                builder.show();
                return;
            }
            LocationManager locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
            boolean locationEnabled = false;
            try {
                locationEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
            } catch (Exception ignored) {
            }
            try {
                locationEnabled |= locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
            } catch (Exception ignored) {
            }
            if (!locationEnabled)
                scanState = ScanState.DISCOVERY;
            // Starting with Android 6.0 a bluetooth scan requires ACCESS_COARSE_LOCATION permission, but that's not all!
            // LESCAN also needs enabled 'location services', whereas DISCOVERY works without.
            // Most users think of GPS as 'location service', but it includes more, as we see here.
            // Instead of asking the user to enable something they consider unrelated,
            // we fall back to the older API that scans for bluetooth classic _and_ LE
            // sometimes the older API returns less results or slower
        }
        listItems.clear();
        listAdapter.notifyDataSetChanged();
        //etEmptyText("<scanning...>");
       // UUID[] serviceUuids={UUID.fromString("0x49535343-FE7D-4AE5-8FA9-9FAFD205E455"), UUID.fromString("0x49535343-1E4D-4BD9-BA61-23C647249616")};

        if (scanState == ScanState.LESCAN) {
            Log.e("leScan","out");
            leScanStopHandler.postDelayed(this::stopScan, LESCAN_PERIOD);
            new AsyncTask<Void, Void, Void>() {
                @Override
                protected Void doInBackground(Void[] params) {
                    Log.e("leScan","int");
                    bluetoothAdapter.startLeScan(null, leScanCallback);
                    return null;
                }
            }.execute(); // start async to prevent blocking UI, because startLeScan sometimes take some seconds
        } else {
            bluetoothAdapter.startDiscovery();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        // ignore requestCode as there is only one in this fragment
        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            new Handler(Looper.getMainLooper()).postDelayed(this::startScan, 1); // run after onResume to avoid wrong empty-text
        } else {
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setTitle(getText(R.string.location_denied_title));
            builder.setMessage(getText(R.string.location_denied_message));
            builder.setPositiveButton(android.R.string.ok, null);
            builder.show();
        }
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_CAMERA:
                if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED)
                    /*try {
                        mCameraManager.openCamera(mCameraIDsList[1], mCameraStateCB, new Handler());
                    } catch (CameraAccessException e) {
                        e.printStackTrace();
                    }*/
                    break;
        }
    }

    private void updateScan(BluetoothDevice device) {
        if (scanState == ScanState.NONE)
            return;
        if (listItems.indexOf(device) < 0) {
            listItems.add(device);
            if (device!=null && device.getName()!=null)

            Collections.sort(listItems, CodeScannerFragment::compareTo);
            listAdapter.notifyDataSetChanged();

        }
    }

    static int compareTo(BluetoothDevice a, BluetoothDevice b) {
        boolean aValid = a.getName() != null && !a.getName().isEmpty();
        boolean bValid = b.getName() != null && !b.getName().isEmpty();
        if (aValid && bValid) {
            int ret = a.getName().compareTo(b.getName());
            if (ret != 0) return ret;
            return a.getAddress().compareTo(b.getAddress());
        }
        if (aValid) return -1;
        if (bValid) return +1;
        return a.getAddress().compareTo(b.getAddress());
    }


    private void stopScan() {
        // Log.e("Step","Stop scan");
        if (scanState == CodeScannerFragment.ScanState.NONE)
            return;
        if (menu != null) {

        }
        switch (scanState) {
            case LESCAN:
                leScanStopHandler.removeCallbacks(this::stopScan);
                bluetoothAdapter.stopLeScan(leScanCallback);
                break;
            case DISCOVERY:
                bluetoothAdapter.cancelDiscovery();
                break;
            default:
                // already canceled
        }
        scanState = CodeScannerFragment.ScanState.NONE;

    }

    public void connectToDevice() {
         Log.e("comments","1");

        timeLog=timeLog+"Checking"+ DateFormat.getTimeInstance().format(new Date());

        // DeviceNameBLE ="DESKTOP-DVQ63AJ";
        CarryParkApplication.setDeviceName(DeviceNameBLE);

        for (int i = 0; i < listItems.size(); i++) {
            if (listItems.get(i).getName() != null && !listItems.get(i).getName().isEmpty()) {
                Log.e("comments",""+listItems.get(i).getName());
                if (listItems.get(i).getName().equalsIgnoreCase(DeviceNameBLE)) {
                    Log.e("DeviceName",listItems.get(i).getName());
                    if (waitForConnecttoDevice != null) {
                        waitForConnecttoDevice.cancel();
                        waitForConnecttoDevice = null;
                    }
                    timeLog=timeLog+"Found at"+ DateFormat.getTimeInstance().format(new Date());

                    Address = listAdapter.getItem(i).getAddress();
                    Log.e("comments","Address"+Address);

                    CarryParkApplication.setDeviceAddressBle(Address);
                    //Log.e("UUID",""+listAdapter.getItem(i).getUuids());
                    SharedPreferenceUtility.saveDeviceAddress(DeviceNameBLE,Address);


                    break;
                }
            }

        }


        //REMOVE AT PRODUCTIO
        //TINU
       //Address ="80:1F:12:B2:2C:28";
        //CarryParkApplication.setDeviceAddressBle(Address);

        //CarryParkApplication.setDeviceAddressBle(Address);
        // Address ="2B:B2:BD:C1:35:17";
        // Address ="28:B2:BD:C1:35:17";
        if (Address != null && !Address.isEmpty()) {
            ((BaseActivity) getActivity()).hideBusyAnimation();
            //   Log.e("Step","1Check");
            stopScan();
            if (waitForConnecttoDevice != null) {
                waitForConnecttoDevice.cancel();
                waitForConnecttoDevice = null;
            }

            getActivity().runOnUiThread(new Runnable() {
                public void run() {

                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            SharedPreferenceUtility.saveDeepLinkDeviceDetails(DeviceNameBLE,Address,DeviceID);

                            SharedPreferenceUtility.saveDeviceAddress(DeviceNameBLE,Address);
                            CarryParkApplication.setIsConnectedWithSavedAddress(false);
                            CarryParkApplication.setDeviceAddressBle(Address);
                            CarryParkApplication.setTimeLogContent(timeLog);
                            Bundle args = new Bundle();
                            args.putString("device", Address);
                            args.putString("is_from", ConstantProject.CodeScannerFragment);
                            Fragment fragment = new InitialPaymentFragment();
                            fragment.setArguments(args);
                            getFragmentManager().beginTransaction().replace(R.id.fragment_container, fragment, "terminal").addToBackStack(null).commit();

                        }
                    }, 500);

                       }
            });


        }




    }







    private void checkStatusEveryIntervels() {
        timeLog=timeLog+" start Device search"+ DateFormat.getTimeInstance().format(new Date());

        waitForConnecttoDevice = new CountDownTimer(ConstantProject.TimeoutTenSecond, 3000) {
//check each 3 seconds
            public void onTick(long millisUntilFinished) {

                connectToDevice();
                Log.e("comments","Counter start"+waitForConnecttoDevice);
                Log.e("comments","Counter start"+timeLog);

            }

            public void onFinish() {
                if (waitForConnecttoDevice != null) {
                    waitForConnecttoDevice.cancel();
                    waitForConnecttoDevice = null;
                }

                //REMOVE IN Production
           /* Address="1234";
                SharedPreferenceUtility.saveDeepLinkDeviceDetails(DeviceNameBLE,Address,DeviceID);

                SharedPreferenceUtility.saveDeviceAddress(DeviceNameBLE,Address);
                CarryParkApplication.setIsConnectedWithSavedAddress(false);
                CarryParkApplication.setDeviceAddressBle(Address);
                CarryParkApplication.setTimeLogContent(timeLog);
                Bundle args = new Bundle();
                args.putString("device", Address);
                args.putString("is_from", ConstantProject.CodeScannerFragment);
                Fragment fragment = new InitialPaymentFragment();
                fragment.setArguments(args);
                getFragmentManager().beginTransaction().replace(R.id.fragment_container, fragment, "terminal").addToBackStack(null).commit();

*/

               timeLog=timeLog+"finish first,Aganin check"+ DateFormat.getTimeInstance().format(new Date());
                Log.e("comments","Counter end"+timeLog);
                    stopScan();

                BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
                if (mBluetoothAdapter.isEnabled()) {
                    mBluetoothAdapter.disable();
                }


                final Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        // Do something after 5s = 5000ms
                        BluetoothAdapter mBluetoothAdapter2 = BluetoothAdapter.getDefaultAdapter();
                        mBluetoothAdapter2.enable();
                        startScan();
                        //((BaseActivity) getActivity()).callBackgroundScanning();
                        checkStatusEveryIntervelsSecondtime();
                    }
                }, 3000);



               /* startScan();
                    ((BaseActivity) getActivity()).callBackgroundScanning();
                    checkStatusEveryIntervelsSecondtime();

*/


            }
        }.start();
    }


    private void checkStatusEveryIntervelsSecondtime() {
        ((BaseActivity) getActivity()).hideBusyAnimation();
        ((BaseActivity) getActivity()).showBusyAnimation(CarryParkApplication.getCurrentContext().getResources().getString(R.string.bleSearching));
        waitForConnecttoDeviceSecond = new CountDownTimer(ConstantProject.TimeoutFiftySecond, 2000) {
            //check each 2 seconds
            public void onTick(long millisUntilFinished) {
                avilableDevice = "";
                checkAgainTheDevice( false);
                Log.e("comments","check each 2 seconds"+timeLog);
            }

            public void onFinish() {
                ((BaseActivity) getActivity()).hideBusyAnimation();
                if (waitForConnecttoDevice != null) {
                    waitForConnecttoDevice.cancel();
                    waitForConnecttoDevice = null;
                }



                if (waitForConnecttoDeviceSecond != null){
                    waitForConnecttoDeviceSecond.cancel();
                    waitForConnecttoDeviceSecond = null;
                }

                avilableDevice = "";
                checkAgainTheDevice( true);


            }
        }.start();
    }




    public void postErrorLog(final String ErrorCode, final String message, String givenInputs, int ng, String battery, String eng, String jap) {

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
        apiService =
                ApiClient.getClient().create(ApiInterface.class);
        apiService.postErrorLog(acess_token, candidateMap).enqueue(new Callback<HashApiResponse>() {
            @Override
            public void onResponse(Call<HashApiResponse> call, Response<HashApiResponse> response) {


            }


            @Override
            public void onFailure(Call<HashApiResponse> call, Throwable t) {
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

    public void checkAgainTheDevice( boolean isFinal) {
        //Log.e("Step","call after 10");
        timeLog=timeLog+"Again At"+ DateFormat.getTimeInstance().format(new Date());

       for (int i = 0; i < listItems.size(); i++) {
            if (listItems.get(i) != null && listItems.get(i).getName() != null)

                avilableDevice = avilableDevice + " , " + listItems.get(i).getName() + "(" + listItems.get(i).getUuids() + ")";

            if (listItems.get(i).getName() != null && !listItems.get(i).getName().isEmpty()) {

                if (listItems.get(i).getName().equalsIgnoreCase(DeviceNameBLE)) {
                    Address = listAdapter.getItem(i).getAddress();
                    if (waitForConnecttoDevice != null) {
                        waitForConnecttoDevice.cancel();
                        waitForConnecttoDevice = null;
                    }



                    if (waitForConnecttoDeviceSecond != null){
                        waitForConnecttoDeviceSecond.cancel();
                        waitForConnecttoDeviceSecond = null;
                    }

                    CarryParkApplication.setDeviceAddressBle(Address);
                    break;
                }
            }

        }

        if (Address != null && !Address.isEmpty()) {
          //  ((BaseActivity) getActivity()).hideBusyAnimation();


            getActivity().runOnUiThread(new Runnable() {
                public void run() {
                    stopScan();
                    //   SharedPreferenceUtility.saveDeviceAddress(DeviceNameBLE,Address);
                    //   Log.e("Step","wait1Conne");
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            if (waitForConnecttoDevice != null) {
                                waitForConnecttoDevice.cancel();
                                waitForConnecttoDevice = null;
                            }


                            timeLog=timeLog+"Find at second"+ DateFormat.getTimeInstance().format(new Date());

                            if (waitForConnecttoDeviceSecond != null){
                                waitForConnecttoDeviceSecond.cancel();
                                waitForConnecttoDeviceSecond = null;
                            }
                            SharedPreferenceUtility.saveDeepLinkDeviceDetails(DeviceNameBLE,Address,DeviceID);

                            ((BaseActivity) getActivity()).hideBusyAnimation();
                            CarryParkApplication.setDeviceAddressBle(Address);
                            SharedPreferenceUtility.saveDeviceAddress(DeviceNameBLE,Address);
                            CarryParkApplication.setIsConnectedWithSavedAddress(false);
                           CarryParkApplication.setTimeLogContent(timeLog);
                            Bundle args = new Bundle();
                            args.putString("device", Address);
                            args.putString("is_from", ConstantProject.CodeScannerFragment);
                            Fragment fragment = new InitialPaymentFragment();
                            fragment.setArguments(args);
                            getFragmentManager().beginTransaction().replace(R.id.fragment_container, fragment, "terminal").addToBackStack(null).commit();



                        }
                    }, 500);
                          }
            });


        } else {

            finalArrayOfFoundBTDevices =new ArrayList<>();
            for (int i = 0; i < finalArrayOfFoundBTDevices.size(); i++) {
                if (finalArrayOfFoundBTDevices.get(i) != null && finalArrayOfFoundBTDevices.get(i).getName() != null)
                    avilableDevice = avilableDevice + " , " + finalArrayOfFoundBTDevices.get(i).getName() + "(" + "" + finalArrayOfFoundBTDevices.get(i).getUuid() + ")";

                if (finalArrayOfFoundBTDevices.get(i).getName() != null && !finalArrayOfFoundBTDevices.get(i).getName().isEmpty()) {

                    if (finalArrayOfFoundBTDevices.get(i).getName().equalsIgnoreCase(DeviceNameBLE)) {
                        Address = finalArrayOfFoundBTDevices.get(i).getAddress();
                        if (waitForConnecttoDevice != null) {
                            waitForConnecttoDevice.cancel();
                            waitForConnecttoDevice = null;
                        }



                        if (waitForConnecttoDeviceSecond != null){
                            waitForConnecttoDeviceSecond.cancel();
                            waitForConnecttoDeviceSecond = null;
                        }

                        CarryParkApplication.setDeviceAddressBle(Address);
                        break;
                    }
                }

            }


            if (Address != null && !Address.isEmpty()) {
               // ((BaseActivity) getActivity()).hideBusyAnimation();

                getActivity().runOnUiThread(new Runnable() {
                    public void run() {
                        stopScan();
                        if (waitForConnecttoDevice != null) {
                            waitForConnecttoDevice.cancel();
                            waitForConnecttoDevice = null;
                        }



                        if (waitForConnecttoDeviceSecond != null){
                            waitForConnecttoDeviceSecond.cancel();
                            waitForConnecttoDeviceSecond = null;
                        }
                        ((BaseActivity) getActivity()).hideBusyAnimation();
                        // SharedPreferenceUtility.saveDeviceAddress(DeviceNameBLE,Address);

                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                timeLog=timeLog+"Found at.."+ DateFormat.getTimeInstance().format(new Date());

                                SharedPreferenceUtility.saveDeepLinkDeviceDetails(DeviceNameBLE,Address,DeviceID);

                                CarryParkApplication.setDeviceAddressBle(Address);
                                SharedPreferenceUtility.saveDeviceAddress(DeviceNameBLE,Address);
                                CarryParkApplication.setIsConnectedWithSavedAddress(false);
                                CarryParkApplication.setTimeLogContent(timeLog);
                                Bundle args = new Bundle();
                                // Log.e("Step","wait2Conne");
                                args.putString("device", Address);
                                args.putString("is_from", ConstantProject.CodeScannerFragment);
                                Fragment fragment = new InitialPaymentFragment();
                                fragment.setArguments(args);
                                getFragmentManager().beginTransaction().replace(R.id.fragment_container, fragment, "terminal").addToBackStack(null).commit();

                            }
                        }, 500);

                              }
                });


            } else {







                if (isFinal)
                {
                    if (waitForConnecttoDevice != null) {
                        waitForConnecttoDevice.cancel();
                        waitForConnecttoDevice = null;
                    }



                    if (waitForConnecttoDeviceSecond != null){
                        waitForConnecttoDeviceSecond.cancel();
                        waitForConnecttoDeviceSecond = null;
                    }

                        ((BaseActivity) getActivity()).hideBusyAnimation();
                        DialogManager.showRescanDialogue(new DialogManager.IMultiActionDialogOnClickListener() {
                            @Override
                            public void onPositiveClick() {
                               // ((BaseActivity) getActivity()).showBusyAnimation("");
                                stopScan();
                             //   startScan();

                                //checkStatusEveryIntervels();
                                sentAppLog("BLE device not discovered or not in the range. user-select  Rescan option.","BLE device not discovered or not in the range. user-select  Rescan option.","BLEデバイスが見つからないか範囲外です。再スキャン選択画面を表示。");

                                Fragment fragment = null;
                                fragment = new ScanQrcodeFragment();
                                loadFragment(fragment);
                            }

                            @Override
                            public void onNegativeClick() {
                                String msg;

                                if (SharedPreferenceUtility.isJapanease())
                                {
                                    msg=ConstantProject.errorLogDeviceNotDiscurvedJADisplay;
                                }
                                else {
                                    msg=ConstantProject.errorLogDeviceNotDiscurvedENDisplay;
                                }
                                sentAppLog("BLE device not discovered or not in range","BLE device not discovered or not in range","BLEデバイスが見つからないか範囲外です。");
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



                }



            }
        }
    }


    public void sentAppLog(String comments,String commentEn,String commentjp) {


        String acess_token = GloablMethods.API_HEADER + AppController.getString(CarryParkApplication.getmContext(), "acess_token");
        Map<String, Object> candidateMap = new HashMap<>();
        candidateMap.put("device_id", CarryParkApplication.getScannedDeviceCode());
        candidateMap.put("app", ConstantProject.DEVICE);
        candidateMap.put("device_status", "Scanning for " + CarryParkApplication.getScannedDeviceName()+"present status"+SharedPreferenceUtility.getDevicePresentStatus());
        candidateMap.put("payment_require", "" + CarryParkApplication.IsPaymentByPassingNoRequired());
        candidateMap.put("device_model", ((BaseActivity) getActivity()).getDeviceName());
        candidateMap.put("device_version", ((BaseActivity) getActivity()).DeviceVersion());
        candidateMap.put("scanned_qr_code", CarryParkApplication.getScannedDeviceCode());
        candidateMap.put("available_blt_devices", avilableDevice);
        candidateMap.put("command_sent", "no");
        candidateMap.put("command_received", "no");
        candidateMap.put("error_message_en", commentEn);
        candidateMap.put("error_message_jp", commentjp);
        candidateMap.put("comments", comments);//commentsForAppLog


        apiService.LogAppErrors(acess_token, candidateMap).enqueue(new Callback<LogAppErrorModel>() {
            @Override
            public void onResponse(Call<LogAppErrorModel> call, Response<LogAppErrorModel> response) {


            }


            @Override
            public void onFailure(Call<LogAppErrorModel> call, Throwable t) {

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
    public void DeepLinkConnection()
    {
        DeviceNameBLE= SharedPreferenceUtility.getDeepLinkDeviceName();
        Address=SharedPreferenceUtility.getDeepLinkdeviceAddress();
        DeviceID =SharedPreferenceUtility.getDeepLinkDeviceCode();

        SharedPreferenceUtility.saveDeepLinkDeviceDetails(DeviceNameBLE,Address,DeviceID);

        CarryParkApplication.setDeviceAddressBle(Address);
        SharedPreferenceUtility.saveDeviceAddress(DeviceNameBLE,Address);
        CarryParkApplication.setIsConnectedWithSavedAddress(false);
        CarryParkApplication.setTimeLogContent(timeLog);
        Bundle args = new Bundle();
        args.putString("device", Address);
        args.putString("is_from", ConstantProject.CodeScannerFragment);
        Fragment fragment = new InitialPaymentFragment();
        fragment.setArguments(args);
        getFragmentManager().beginTransaction().replace(R.id.fragment_container, fragment, "terminal").addToBackStack(null).commit();

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
}
