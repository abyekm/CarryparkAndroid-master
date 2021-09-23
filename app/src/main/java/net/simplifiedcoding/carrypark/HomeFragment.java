package net.simplifiedcoding.carrypark;

import android.Manifest;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.bluetooth.*;
import android.content.*;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.*;
import android.provider.Settings;
import android.util.Log;
import android.util.TimingLogger;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.*;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.gms.location.*;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
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
import net.simplifiedcoding.carrypark.Adapter.DevicesInUseAdapter;
import net.ui.BottomNavigation;
import net.ui.MapsActivity;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import java.lang.reflect.Method;
import java.text.DateFormat;
import java.util.*;

import static android.Manifest.permission.ACCESS_COARSE_LOCATION;
import static android.Manifest.permission.ACCESS_FINE_LOCATION;
import static android.content.Context.MODE_PRIVATE;

import static androidx.core.content.ContextCompat.checkSelfPermission;

public class HomeFragment extends Fragment implements ServiceConnection, SerialListener {

    // security
    private boolean initialStart = true;
    private enum Connected {False, Pending, True}
    private Connected connected = Connected.True;
    private TextView receiveText;
    private long currentTimeOfTimer = 3000;


    private SerialService service;
    public HomeFragment() {
        // Required empty public constructor
    }
///

    int PERMISSION_ID = 44;
    Boolean hasInitialPaymentPreviouslyDone = false;

    FusedLocationProviderClient mFusedLocationClient;
    ApiInterface apiService;
    SharedPreferences sharedPreferences;
    RecyclerView rvDevicesInUse;
    Button buttonConnectDevice;
    TextView etUserName;
    DevicesInUseAdapter devicesInUseAdapter;
    List<UsedLocker> usedLockersList = new ArrayList<>();
    Fragment fragment = null;
    View viewBetween;
    LinearLayout ll_checkin, ll_checkOut;
    LocationTrack locationTrack;
    private static final int REQUEST_LOCATION = 1;
    private ArrayList permissionsToRequest;
    private ArrayList permissionsRejected = new ArrayList();
    private ArrayList permissions = new ArrayList();
    private final static int ALL_PERMISSIONS_RESULT = 101;
    String DeviceNameBLE = "";
    static final int MY_PERMISSIONS_REQUEST_CAMERA = 1242;
    ProgressBar progressBar;

    Handler mHandler;
    BluetoothAdapter mBluetoothAdapter;
    BluetoothManager btManager;
    BluetoothAdapter btAdapter;
    private final static int REQUEST_ENABLE_BT = 1;

    private static final int CAMERA = 2;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);


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


        return inflater.inflate(R.layout.fragment_inuse_new, null);


    }


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // navigation = getActivity().findViewById(R.id.navigation);
        // navigation.setVisibility(View.VISIBLE);
        if (service==null)
        {
            service=CarryParkApplication.getService();
        }
        sharedPreferences = getActivity().getSharedPreferences("carry_park", MODE_PRIVATE);
        apiService = ApiClient.getClient().create(ApiInterface.class);

        buttonConnectDevice = (Button) view.findViewById(R.id.button_connect_device);
        etUserName = (TextView) view.findViewById(R.id.et_user);
        viewBetween = (View) view.findViewById(R.id.view_between);
        ll_checkin = (LinearLayout) view.findViewById(R.id.ll_checkin);
        ll_checkOut = (LinearLayout) view.findViewById(R.id.ll_checkOut);
        progressBar = (ProgressBar) view.findViewById(R.id.indeterminateBar);

        rvDevicesInUse = (RecyclerView) view.findViewById(R.id.rv_devices_in_use);
        rvDevicesInUse.setHasFixedSize(true);
        rvDevicesInUse.setLayoutManager(new LinearLayoutManager(getContext()));
        ActivityCompat.requestPermissions(getActivity(),
                new String[]{ACCESS_FINE_LOCATION}, REQUEST_LOCATION);
        permissions.add(ACCESS_FINE_LOCATION);
        permissions.add(ACCESS_COARSE_LOCATION);
        permissionsToRequest = findUnAskedPermissions(permissions);
//TINU   9.7479076,76.6348957,15z

        CarryParkApplication.setScannedDeviceCode("");
        CarryParkApplication.setDeviceAddressBle("");
        SharedPreferenceUtility.SavePinVerified(true);

        String user = sharedPreferences.getString("user_name", null);

      if (SharedPreferenceUtility.isScanClickEvent())
        {


              showLoading();

            progressBar.setVisibility(View.VISIBLE);
            ll_checkin.setVisibility(View.GONE);
            ll_checkOut.setVisibility(View.GONE);
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                public void run() {
                    dismissLoading();
                    SharedPreferenceUtility.saveScanClickEvent(false);


                   Fragment fragment = null;
                    fragment = new ScanQrcodeFragment();
                    loadFragment(fragment);

                    ((BottomNavigation) getActivity()).EvaluateScanClick();
                }
            }, 1000);




        }
      else {
          progressBar.setVisibility(View.GONE);
          ll_checkin.setVisibility(View.VISIBLE);
          ll_checkOut.setVisibility(View.VISIBLE);

      }

        // etUserName.setText(getString(R.string.hi)+", "+user);
        if (CarryParkApplication.isIsEnglishLang()) {
            etUserName.setText(getString(R.string.hi) + ", " + user);
        } else if (CarryParkApplication.isIsJapaneaseLang()) {
            //"ようこそ、Carryparkへ
            //○○さん"
            etUserName.setText("ようこそ " + user + "さん");
        }
        else
        {
            etUserName.setText(getString(R.string.hi)+", "+user);
        }



     // String batt= ""+ checkBatteryPower("63c0");
      //  Log.e("battery",batt);
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(getActivity());

        sharedPreferences = getActivity().getSharedPreferences("carry_park", MODE_PRIVATE);
        viewBetween.setVisibility(View.GONE);
        getUserDetails();

        initializeBluetooth();
        btManager = (BluetoothManager) getActivity().getSystemService(Context.BLUETOOTH_SERVICE);
        btAdapter = btManager.getAdapter();


        BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (mBluetoothAdapter == null) {
           Toast.makeText(getContext(),getContext().getResources().getString(R.string.bleNotavbl),Toast.LENGTH_LONG);
        }
        else {
            mBluetoothAdapter.enable();
        }



                                                                                            //     https://stg-p.takeme.com/qrpay/tk_67b81a853c0872b2ad89f2a5645ee/                                                    P9df90c088fa44df652d/googlepay
    /*  String content="ww.google-analytics.com/collect?v=1&_v=j86&a=1762454784&t=pageview&_s=2&dl=https://stg-p.takeme.com/qrpay/tk_67b81a853c0872b2ad89f2a5645ee/checkout&dp=/qrpay/tk_67b81a853c0872b2ad89f2a5645ee/P9df90c088fa44df652d/googlepay&ul=ja&de=UTF-8&dt=支払い - TakeMe Pay&sd=24-bit&sr=360x780&vp=360x660&je=0&_u=CACAAEABAAAAAC~&jid=&gjid=&cid=160219778.1600144055&tid=UA-80768718-11&_gid=655469620.1600144055&z=954045394";

      if (content.contains("ww.google-analytics.com/")&& content.contains("UTF-8&dt=支払い - TakeMe Pay&sd")&& content.contains("/qrpay/") )
      {

      }*/
       /* String content="ww.google-analytics.com/collect?v=1&_v=j86&a=1762454784&t=pageview&_s=2&dl=https://stg-p.takeme.com/qrpay/tk_67b81a853c0872b2ad89f2a5645ee/checkout&dp=/qrpay/tk_67b81a853c0872b2ad89f2a5645ee/P9df90c088fa44df652d/googlepay&ul=ja&de=UTF-8&dt=支払い - TakeMe Pay&sd=24-bit&sr=360x780&vp=360x660&je=0&_u=CACAAEABAAAAAC~&jid=&gjid=&cid=160219778.1600144055&tid=UA-80768718-11&_gid=655469620.1600144055&z=954045394";

        String strPercents = content.split("pageview&_s=2&dl=")[1];
        String strPercent = content.split("/checkout&dp=/qrpay/")[1];
        String s="https://stg-p.takeme.com/qrpay/"+strPercent.substring(0,63);
        Log.e("finalStr",s);

*/

        //  callUniversalPayment();
       // sendSuccessMail();

        ll_checkin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
              //  postErrorLog();

               CarryParkApplication.setIsCheckIn(true);
                SharedPreferenceUtility.saveScannedDeviceInfo_isLock(true);
                Fragment fragment = null;
                fragment = new DeviceLocationMapFragment();
                replaceFragment(fragment);


            }
        });


        ll_checkOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CarryParkApplication.setIsCheckIn(false);
                if (usedLockersList != null && usedLockersList.size() > 0) {
                    boolean isTrue = false;
                    for (int i = 0; i < usedLockersList.size(); i++) {
                        if (usedLockersList.get(i).getPresentStatus().equalsIgnoreCase("LOCKED")) {
                            isTrue = true;
                        }
                    }
                    if (isTrue) {
                        SharedPreferenceUtility.saveScannedDeviceInfo_isLock(false);
                        Fragment fragment = null;
                        fragment = new DeviceLocationMapFragment();
                        replaceFragment(fragment);
                    }

                } else {

                }


            }
        });
        if (checkSelfPermission(
                getContext(), ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && checkSelfPermission(
                getContext(), ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(), new String[]{ACCESS_FINE_LOCATION}, REQUEST_LOCATION);
        }

        mFusedLocationClient.getLastLocation()
                .addOnSuccessListener(CarryParkApplication.getCurrentActivity(), new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        // Got last known location. In some rare situations this can be null.
                        if (location != null) {
                            SharedPreferenceUtility.saveLastKnownLatAndLong("" + location.getLatitude(),"" + location.getLongitude());
                        } else {
                        }
                    }
                });





    }


    @SuppressLint("MissingPermission")
    private void getLastLocation() {
        if (checkPermissions()) {
            if (isLocationEnabled()) {
                mFusedLocationClient.getLastLocation().addOnCompleteListener(
                        new OnCompleteListener<Location>() {
                            @Override
                            public void onComplete(@NonNull Task<Location> task) {
                                Location location = task.getResult();
                                if (location == null) {
                                    requestNewLocationData();
                                } else {
                                    SharedPreferenceUtility.saveLastKnownLatAndLong("" + location.getLatitude(),"" + location.getLongitude());



                                }
                            }
                        }
                );
            } else {
                startActivityForResult(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS), 1);


                getLocation();
            }
        } else {
            requestPermissions();
        }
        disconnect();


    }


    @SuppressLint("MissingPermission")
    private void requestNewLocationData() {

        LocationRequest mLocationRequest = new LocationRequest();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setInterval(0);
        mLocationRequest.setFastestInterval(0);
        mLocationRequest.setNumUpdates(1);

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(getActivity());
        mFusedLocationClient.requestLocationUpdates(
                mLocationRequest, mLocationCallback,
                Looper.myLooper()
        );

    }

    private LocationCallback mLocationCallback = new LocationCallback() {
        @Override
        public void onLocationResult(LocationResult locationResult) {
            Location mLastLocation = locationResult.getLastLocation();
            SharedPreferenceUtility.saveLastKnownLatAndLong("" + mLastLocation.getLatitude(),"" +  mLastLocation.getLongitude());



        }
    };

    private boolean checkPermissions() {
        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            return true;
        }
        return false;
    }

    private void requestPermissions() {
        ActivityCompat.requestPermissions(
                getActivity(),
                new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION},
                PERMISSION_ID
        );
    }

    private boolean isLocationEnabled() {
        LocationManager locationManager = (LocationManager) getContext().getSystemService(Context.LOCATION_SERVICE);
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(
                LocationManager.NETWORK_PROVIDER
        );
    }

    public void getLocation() {
        if (checkSelfPermission(CarryParkApplication.getCurrentContext(),Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && checkSelfPermission(CarryParkApplication.getCurrentContext(),Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    Activity#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for Activity#requestPermissions for more details.
            return;
        }
        mFusedLocationClient.getLastLocation()
                .addOnSuccessListener(CarryParkApplication.getCurrentActivity(), new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        // Got last known location. In some rare situations this can be null.
                        if (location != null) {
                            SharedPreferenceUtility.saveLastKnownLatAndLong("" + location.getLatitude(),"" + location.getLongitude());



                        } else {
                        }
                    }
                });




    }



    private void getUserDetails() {
        if (!SharedPreferenceUtility.isScanClickEvent())
        ((BaseActivity) getActivity()).showBusyAnimation("");
        String acess_token = GloablMethods.API_HEADER + AppController.getString(getActivity(), "acess_token");
          Log.e("acess_token",acess_token);
            String lan="";
        if (SharedPreferenceUtility.isJapanease()) {
            if (CarryParkApplication.isIsJapaneaseLang())
                lan =ConstantProject.forJapaneaseResponse;

        } else if (SharedPreferenceUtility.isEnglish()){
           lan= "en";
        }
        else if (SharedPreferenceUtility.isKorean()){
            lan= ConstantProject.forKoreanResponse;
        }
        else if (SharedPreferenceUtility.isChinease()){
           lan= ConstantProject.forChineaseResponse;
        }
        apiService.userDetails(acess_token, lan).enqueue(new Callback<UserDetailsResponse>() {
            @Override
            public void onResponse(Call<UserDetailsResponse> call, Response<UserDetailsResponse> response) {
                String code = String.valueOf(response.code());
                // Log.e("paymentStatus", response.body().getData().getPaymentInfo());

                try {
                    ((BaseActivity) getActivity()).hideBusyAnimation();
                } catch (Exception e) {
                    e.printStackTrace();
                }

                if (response.code() == 200 && response.body().getData() != null) {
                    Boolean status = response.body().getData().getSuccess();
                    if (status == true) {
                        SharedPreferenceUtility.savePhoneNumber("+"+response.body().getData().getUser().getPhoneCode()+" "+response.body().getData().getUser().getMobile());

                        SharedPreferenceUtility.saveEmailId(response.body().getData().getUser().getEmail());
                        CarryParkApplication.setUserName(response.body().getData().getUser().getFirstName());
                        usedLockersList.clear();
                               usedLockersList.addAll(response.body().getData().getUser().getUsedLockers());
                        CarryParkApplication.setUsedLockerList(usedLockersList);
                        if (CarryParkApplication.isIsEnglishLang()) {
                            etUserName.setText(CarryParkApplication.getCurrentContext().getString(R.string.hi) + ", " + CarryParkApplication.getUserName());
                        } else if (CarryParkApplication.isIsJapaneaseLang()) {
                            //"ようこそ、Carryparkへ
                            //○○さん"
                            etUserName.setText("ようこそ " +  CarryParkApplication.getUserName() + "さん");
                        }

                        if (usedLockersList!=null)
                               {
                                   if (usedLockersList.size()>0)
                                   {
                                       boolean isTrue =false;
                                       for (int i=0;i<usedLockersList.size();i++)
                                       {
                                           if (usedLockersList.get(i).getPresentStatus().equalsIgnoreCase("LOCKED"))
                                           {
                                               isTrue=true;
                                           }
                                       }
                                       if (isTrue)
                                       {
                                           ll_checkOut.setBackgroundResource(R.drawable.bg_roundedbutton);

                                       }
                                       else {
                                           ll_checkOut.setBackgroundResource(R.drawable.btnbg_curved_dark);

                                       }

                                   }else {

                                   }
                               }else {
                            ll_checkOut.setBackgroundResource(R.drawable.btnbg_curved_dark);

                               }
                        CarryParkApplication.setUsedLockerList(usedLockersList);
                        if (usedLockersList.size() > 0) {
                            viewBetween.setVisibility(View.VISIBLE);
                            devicesInUseAdapter = new DevicesInUseAdapter(getContext(), usedLockersList, getContext(), new DevicesInUseAdapter.IDevicesInUseCall() {

                                @Override
                                public void IDevicesInUseScanCall(UsedLocker usedLocker) {
                                    //  CarryParkApplication.setIsInuseScan(true);
                                    if (!Utils.isNetworkConnectionAvailable(getContext())) {
                                        DialogManager.showAlertSingleActionDialog(CarryParkApplication.getCurrentActivity(), CarryParkApplication.getCurrentContext().getResources().getString(R.string.no_internet), "ok", new DialogManager.IUniActionDialogOnClickListener() {
                                            @Override
                                            public void onPositiveClick() {

                                            }
                                        });
                                    } else {


                                    }
                                }


                                @Override
                                public void IDeviceInUseGetDirectionCall(UsedLocker usedLocker) {
                                    SharedPreferenceUtility.saveLastKnownLatAndLong("" + usedLocker.getLatitude(),"" + usedLocker.getLongitude());


                                    Intent intent = new Intent(CarryParkApplication.getCurrentContext(), MapsActivity.class);
                                    startActivity(intent);
                                }
                            });
                            rvDevicesInUse.setAdapter(devicesInUseAdapter);

                        } else {
                            buttonConnectDevice.setVisibility(View.GONE);
                        }


                    }/*else if(status == false){
                        String msg = response.body().getMessage();
                    }*/
                }/*else if(response.code() == 200 && response.body().getData() == null) {
                    progressDialog.dismiss();
                    String msg = response.body().getMessage();
                }*/ else if (response.code() == 404) {
                    ((BaseActivity) getActivity()).hideBusyAnimation();
                    DialogManager.showAlertSingleActionDialog(CarryParkApplication.getCurrentActivity(),CarryParkApplication.getCurrentContext().getResources().getString(R.string.validation_error), "ok", new DialogManager.IUniActionDialogOnClickListener() {
                        @Override
                        public void onPositiveClick() {

                        }


                    });
                }
            }

            @Override
            public void onFailure(Call<UserDetailsResponse> call, Throwable t) {
                ((BaseActivity) getActivity()).hideBusyAnimation();
                if (!Utils.isNetworkConnectionAvailable(CarryParkApplication.getCurrentContext())) {
                    DialogManager.showAlertSingleActionDialog(CarryParkApplication.getCurrentActivity(), CarryParkApplication.getCurrentContext().getResources().getString(R.string.no_internet), "ok", new DialogManager.IUniActionDialogOnClickListener() {
                        @Override
                        public void onPositiveClick() {

                        }
                    });
                } else {
                    DialogManager.showAlertSingleActionDialog(CarryParkApplication.getCurrentActivity(),CarryParkApplication.getCurrentContext().getResources().getString(R.string.OperfailedExtnd), "ok", new DialogManager.IUniActionDialogOnClickListener() {
                        @Override
                        public void onPositiveClick() {

                        }


                    });
                }
            }
        });
    }
    private ArrayList findUnAskedPermissions(ArrayList wanted) {
        ArrayList result = new ArrayList();

        for (Object perm : wanted) {
            if (!hasPermission((String) perm)) {
                result.add(perm);
            }
        }

        return result;
    }

    private boolean hasPermission(String permission) {
        if (canMakeSmores()) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                return (checkSelfPermission(getContext(),permission) == PackageManager.PERMISSION_GRANTED);
            }
        }
        return true;
    }
    private boolean canMakeSmores() {
        return (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP_MR1);
    }


    public void replaceFragment (Fragment someFragment, Fragment currentFragment){
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, someFragment);
        transaction.addToBackStack(null);
        transaction.commit();


    }

   /* public void replaceFragment (Fragment someFragment, String scanned_device_code){
        //Put the value
        InUseFragment ldf = new InUseFragment();
        Bundle args = new Bundle();
        args.putString("scannedDeviceCode", scanned_device_code);
        args.putString("isFrom", "scan_qrcode_fragment");
        args.putBoolean("hasInitialPaymentPreviouslyDone", hasInitialPaymentPreviouslyDone);
        ldf.setArguments(args);


        //Inflate the fragment
        getFragmentManager().beginTransaction().add(R.id.fragment_container, ldf).commit();
    }

    public void replaceFragmentToScan (String scanned_device_code){
        Intent intent = new Intent(getActivity(), BottomNavigation.class);
        intent.putExtra("page", ConstantProject.DevicesInUseAdapter);
        intent.putExtra("scannedDeviceCode", scanned_device_code);
        intent.putExtra(ConstantProject.isFrom, ConstantProject.DevicesInUseAdapter);
        intent.putExtra("hasInitialPaymentPreviouslyDone", hasInitialPaymentPreviouslyDone);
        getActivity().startActivity(intent);
    }*/

    public int calculateAmount ( int hour, int initialAmount, int RatePerHurs){

        int amt = initialAmount + ((hour - 1) * RatePerHurs);
        return amt;

    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == 1) {
            switch (requestCode) {
                case 1:
                    getLocation();
                    break;
            }
        }
    }
    @Override
    public void onResume() {
        super.onResume();
        getLastLocation();

    }


    public void replaceFragment(Fragment someFragment) {
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, someFragment);
        transaction.addToBackStack(null);
        transaction.commit();

    }

    @TargetApi(Build.VERSION_CODES.M)
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == PERMISSION_ID) {
            if(grantResults.length>0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                // Granted. Start getting the location information
            }
            else if (requestCode ==1)
            {
            }
        }
        switch (requestCode) {

            case ALL_PERMISSIONS_RESULT:
                for (Object perms : permissionsToRequest) {
                    if (!hasPermission((String) perms)) {
                        permissionsRejected.add(perms);
                    }
                }

                if (permissionsRejected.size() > 0) {


                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        if (shouldShowRequestPermissionRationale(String.valueOf(permissionsRejected.get(0)))) {
                            showMessageOKCancel(CarryParkApplication.getCurrentContext().getResources().getString(R.string.enablelocation),
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                                requestPermissions((String[]) permissionsRejected.toArray(new String[permissionsRejected.size()]), ALL_PERMISSIONS_RESULT);
                                            }
                                        }
                                    });
                            return;
                        }
                    }

                }

                break;
            case MY_PERMISSIONS_REQUEST_CAMERA:
                if (ActivityCompat.checkSelfPermission(CarryParkApplication.getCurrentContext(), Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED)
                   /* try {
                        mCameraManager.openCamera(mCameraIDsList[1], mCameraStateCB, new Handler());
                    } catch (CameraAccessException e) {
                        e.printStackTrace();
                    }*/
                break;
        }

    }

    private void showMessageOKCancel(String message, DialogInterface.OnClickListener okListener) {
        new AlertDialog.Builder(getContext())
                .setMessage(message)
                .setPositiveButton("OK", okListener)
                .setNegativeButton("Cancel", null)
                .create()
                .show();
    }
    @Override
    public void onStart() {
        super.onStart();

        //requesting permission
        int permissionCheck;
        permissionCheck = ContextCompat.checkSelfPermission(CarryParkApplication.getCurrentContext(), Manifest.permission.CAMERA);
        if (permissionCheck != PackageManager.PERMISSION_GRANTED) {

            if (ActivityCompat.shouldShowRequestPermissionRationale(CarryParkApplication.getCurrentActivity(), Manifest.permission.CAMERA)) {

            } else {
                ActivityCompat.requestPermissions(CarryParkApplication.getCurrentActivity(), new String[]{Manifest.permission.CAMERA}, MY_PERMISSIONS_REQUEST_CAMERA);
            }
        } else {

        }
        if (service != null)
            service.attach(this);
        else
            getActivity().startService(new Intent(getActivity(), SerialService.class)); // prevents service destroy on unbind from recreated activity caused by orientation change

    }

    private void initializeBluetooth() {
        mHandler = new Handler();

        final BluetoothManager bluetoothManager =
                (BluetoothManager) getActivity().getSystemService(Context.BLUETOOTH_SERVICE);
        mBluetoothAdapter = bluetoothManager.getAdapter();

        // Checks if Bluetooth is supported on the device.
        if (mBluetoothAdapter == null) {
            return;
        }

        else{
            if(!mBluetoothAdapter.isEnabled()){
                if (mBluetoothAdapter == null) {
                    Toast.makeText(getContext(),getContext().getResources().getString(R.string.bleNotavbl),Toast.LENGTH_LONG);
                }
                else {
                    mBluetoothAdapter.enable();
                }
            }
        }

        Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();
        if (pairedDevices.size() > 0) {
            for (BluetoothDevice device : pairedDevices) {
                try {
                    Method m = device.getClass()
                            .getMethod("removeBond", (Class[]) null);
                    m.invoke(device, (Object[]) null);
                } catch (Exception e) {
                }
            }
        }
    }


    @Override
    public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
        service = ((SerialService.SerialBinder) iBinder).getService();
        service.attach(this);
        CarryParkApplication.setService(service);
        if (initialStart && isResumed()) {
            initialStart = false;

        }
    }

    @Override
    public void onServiceDisconnected(ComponentName componentName) {

            service = null;
    }

    @Override
    public void onSerialConnect() {

    }

    @Override
    public void onSerialConnectError(Exception e) {

    }

    @Override
    public void onSerialRead(byte[] data) {

    }

    @Override
    public void onSerialIoError(Exception e) {

    }

    @Override
    public void onReadDataAtFailure(byte[] data) {

    }
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        CarryParkApplication.getCurrentActivity().bindService(new Intent(getActivity(), SerialService.class), this, Context.BIND_AUTO_CREATE);
    }
    private void disconnect() {
        connected = Connected.False;
        if (service!=null)
        service.disconnect();

    }



    private void callUniversalPayment() {



        String acess_token = GloablMethods.API_HEADER + AppController.getString(CarryParkApplication.getCurrentActivity(), "acess_token");
        Map<String, Object> candidateMap = new HashMap<>();
//SharedPreferenceUtility.getDeviceIdMacID()
        candidateMap.put("device_id", "ABEF4F63-C349-AB00-B36D-EEC525A12ds1");
        apiService.CallUniversal_payment(acess_token, candidateMap).enqueue(new Callback<PredefinedPaymentResponse>() {
            @Override
            public void onResponse(Call<PredefinedPaymentResponse> call, Response<PredefinedPaymentResponse> response) {
                ((BaseActivity) CarryParkApplication.getCurrentActivity()).hideBusyAnimation();


                if (response.code() == 200 && response.body() != null) {
                    if (response.body().isSuccess()) {
                        SharedPreferenceUtility.SavePaymentToken(response.body().getData().getPredefined_payment().getToken());
                        String url = response.body().getData().getUrl();

                        if(url!=null && !url.isEmpty())
                        {
                           // String urlString = "http://google.com";
                            Intent intent = new Intent(Intent.ACTION_VIEW,Uri.parse(url));
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
                    }


                } else if (response.code() == 404) {

                    DialogManager.showAlertSingleActionDialog(CarryParkApplication.getCurrentActivity(),CarryParkApplication.getCurrentContext().getResources().getString(R.string.validation_error), "ok", new DialogManager.IUniActionDialogOnClickListener() {
                        @Override
                        public void onPositiveClick() {

                        }


                    });                   }
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
                    DialogManager.showAlertSingleActionDialog(CarryParkApplication.getCurrentActivity(),CarryParkApplication.getCurrentContext().getResources().getString(R.string.OperfailedExtnd), "ok", new DialogManager.IUniActionDialogOnClickListener() {
                        @Override
                        public void onPositiveClick() {

                        }


                    });                   }
            }
        });
    }


    public double checkBatteryPower(String input) {

        int decimal = Integer.parseInt(input, 16);

        double power = new Double(decimal) / new Double(65535);
        double baltteryPower = 3.3 * power;
        baltteryPower = baltteryPower * 4.3;
        return baltteryPower;

    }

    public void sendSuccessMail() {

        Map<String, Object> candidateMap = new HashMap<>();
        candidateMap.put("device_id", "CP001-00000057");
        candidateMap.put("payment_method", "cc");


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

    public void loadFragment(Fragment fragment) {
        getFragmentManager().beginTransaction().add(R.id.fragment_container,fragment).commit();

    }
    void showLoading(){
        progressBar.setVisibility(View.VISIBLE);
       CarryParkApplication.getCurrentActivity().getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
    }
    void dismissLoading(){
        progressBar.setVisibility(View.INVISIBLE);
        CarryParkApplication.getCurrentActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
    }
}
