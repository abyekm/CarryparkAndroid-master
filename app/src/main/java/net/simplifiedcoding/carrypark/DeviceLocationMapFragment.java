package net.simplifiedcoding.carrypark;

import android.Manifest;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.location.*;
import com.google.android.gms.maps.*;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.squareup.picasso.Picasso;

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
import net.simplifiedcoding.carrypark.Adapter.DeviceFreeLockRecyclearview;
import net.simplifiedcoding.carrypark.Adapter.UsedLockerDeviceRecyclerViewAdapter;
import net.ui.BottomNavigation;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import java.io.Serializable;
import java.util.*;

import static android.Manifest.permission.ACCESS_COARSE_LOCATION;
import static android.Manifest.permission.ACCESS_FINE_LOCATION;
import static androidx.core.content.ContextCompat.checkSelfPermission;


public class DeviceLocationMapFragment extends Fragment implements OnMapReadyCallback {
    int PERMISSION_ID = 44;

    GoogleMap googleMap;
    MapView mMapView;
    TextView tvPlace, tvAvilable, tvInuse, tvFree;
    ApiInterface apiService;
    private List<LocationData> locationDataList;
    LocationManager locationManager;
    String latitude_, longitude_;
    private static final int REQUEST_LOCATION = 1;
    List<FreeDeviceModel> freeDeviceModelList;
    DeviceFreeLockRecyclearview deviceListAdapter;
    UsedLockerDeviceRecyclerViewAdapter usedLockerDeviceRecyclerViewAdapter;
    RecyclerView rvDevicesList, rvLockedDeviceList;
    TextView etUserName, tvUnlockData1, tvUnlockData2, tv_unlock_data3;
    Button btnProceed;
    FusedLocationProviderClient mFusedLocationClient;
    LinearLayout llLockData1, llLockData2;
    private LinearLayoutManager linearLayoutManagerAllRv;
    String languageToLoad = "en";
    List<UsedLocker> usedLockers = new ArrayList<>();
    boolean not_first_time_showing_info_window, isFirstTime = true;
    private List<String> imagesList;


// location

    private ArrayList permissionsToRequest;
    private ArrayList permissionsRejected = new ArrayList();
    private ArrayList permissions = new ArrayList();
    private final static int ALL_PERMISSIONS_RESULT = 101;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {


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


        return inflater.inflate(R.layout.fragment_device_location_map, null);


    }


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        apiService = ApiClient.getClient().create(ApiInterface.class);
        mMapView = (MapView) view.findViewById(R.id.mapView);
        if (mMapView != null)
            mMapView.onCreate(savedInstanceState);
        tvAvilable = (TextView) view.findViewById(R.id.tv_avilable);
        tvInuse = (TextView) view.findViewById(R.id.tv_inuse);
        tvFree = (TextView) view.findViewById(R.id.tv_free);
        etUserName = (TextView) view.findViewById(R.id.et_user);
        tvPlace = (TextView) view.findViewById(R.id.tv_place);
        btnProceed = (Button) view.findViewById(R.id.button_connect_device);
        llLockData1 = (LinearLayout) view.findViewById(R.id.ll_lock_data1);
        llLockData2 = (LinearLayout) view.findViewById(R.id.ll_lock_data2);
        tvUnlockData1 = (TextView) view.findViewById(R.id.tv_unlock_data1);
        tvUnlockData2 = (TextView) view.findViewById(R.id.tv_unlock_data2);
        tv_unlock_data3 = (TextView) view.findViewById(R.id.tv_unlock_data3);
        imagesList = new ArrayList<>();
        CarryParkApplication.setIsFromSplash(false);
        locationDataList = new ArrayList<>();
        ActivityCompat.requestPermissions(getActivity(),
                new String[]{ACCESS_FINE_LOCATION}, REQUEST_LOCATION);
        permissions.add(ACCESS_FINE_LOCATION);
        permissions.add(ACCESS_COARSE_LOCATION);
        freeDeviceModelList = new ArrayList<>();
        rvDevicesList = (RecyclerView) view.findViewById(R.id.rv_devices);
        rvLockedDeviceList = (RecyclerView) view.findViewById(R.id.rvlocked_device);
        rvLockedDeviceList.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(CarryParkApplication.getCurrentContext());
        rvLockedDeviceList.setLayoutManager(linearLayoutManager);
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(getActivity());

        rvDevicesList.setHasFixedSize(true);
        linearLayoutManagerAllRv = new LinearLayoutManager(CarryParkApplication.getCurrentContext());

        rvDevicesList.setLayoutManager(linearLayoutManagerAllRv);
        permissionsToRequest = findUnAskedPermissions(permissions);
        //get the permissions we have asked for before but are not granted..
        //we will store this in a global list to access later.
        if (ActivityCompat.checkSelfPermission(CarryParkApplication.getCurrentActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(CarryParkApplication.getCurrentActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        mFusedLocationClient.getLastLocation()
                .addOnSuccessListener(CarryParkApplication.getCurrentActivity(), new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        // Got last known location. In some rare situations this can be null.
                        if (location != null) {
                            SharedPreferenceUtility.saveLastKnownLatAndLong("" + location.getLatitude(), "" + location.getLongitude());

                        }
                    }
                });

        if (CarryParkApplication.isIsEnglishLang()) {
            etUserName.setText(getString(R.string.hi) + ", " + CarryParkApplication.getUserName());
        } else if (CarryParkApplication.isIsJapaneaseLang()) {
            //"ようこそ、Carryparkへ
            //○○さん"
            etUserName.setText("ようこそ " + CarryParkApplication.getUserName() + "さん");
        } else {
            etUserName.setText(getString(R.string.hi) + ", " + CarryParkApplication.getUserName());
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {


            if (permissionsToRequest.size() > 0)
                requestPermissions((String[]) permissionsToRequest.toArray(new String[permissionsToRequest.size()]), ALL_PERMISSIONS_RESULT);
        }


        mMapView.onResume();

        try {
            MapsInitializer.initialize(getActivity().getApplicationContext());
        } catch (Exception e) {
            e.printStackTrace();
        }
        btnProceed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (SharedPreferenceUtility.isScannedLock()) {

                    if (freeDeviceModelList != null) {
                        if (freeDeviceModelList.size() > 0) {
                            CarryParkApplication.setNearestDevice(freeDeviceModelList.get(0).getDevice_name());
                            CarryParkApplication.setPlace(freeDeviceModelList.get(0).getLocation());
                            CarryParkApplication.setDepositeTime(freeDeviceModelList.get(0).getStart_time());
                            CarryParkApplication.setLandMark(freeDeviceModelList.get(0).getLandmark());

                            CarryParkApplication.setEndTime(freeDeviceModelList.get(0).getEnd_time());
                            CarryParkApplication.setInitial_charges("" + freeDeviceModelList.get(0).getInitial_charges());
                            CarryParkApplication.setDeviceName("" + freeDeviceModelList.get(0).getDevice_name());
                            CarryParkApplication.setInitial_hours("" + freeDeviceModelList.get(0).getInitial_hours());
                            CarryParkApplication.setRate_per_hour("" + freeDeviceModelList.get(0).getRate_per_hour());
                            CarryParkApplication.setMinituesRate(""+freeDeviceModelList.get(0).getExt_unit_time());
                            CarryParkApplication.setWorks_24_hours(freeDeviceModelList.get(0).getWorks_24_hours());

                            if (freeDeviceModelList.get(0).getLandmark() != null) {
                                for (int m = 0; m < locationDataList.size(); m++) {
                                    if (freeDeviceModelList.get(0).getLandmark().equals(locationDataList.get(m).getLandmark())) {
                                        if (locationDataList.get(m).getPhoto() != null && !locationDataList.get(m).getPhoto().isEmpty()) {
                                            imagesList.add(locationDataList.get(m).getPhoto());
                                        }
                                        if (locationDataList.get(m).getPhotosList() != null) {
                                            if (locationDataList.get(m).getPhotosList().size() > 0) {
                                                for (int i = 0; i < locationDataList.get(m).getPhotosList().size(); i++) {
                                                    imagesList.add(locationDataList.get(m).getPhotosList().get(i));
                                                }
                                            }
                                        }

                                    }
                                }
                            }


                            Intent intent = new Intent(getActivity(), BottomNavigation.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            CarryParkApplication.setIsFinalPayment(freeDeviceModelList.get(0).isFinal_payment());
                            intent.putExtra("page", ConstantProject.VideoInstructionFragment);
                            if (freeDeviceModelList != null && freeDeviceModelList.get(0) != null) {

                                intent.putExtra("finalP", freeDeviceModelList.get(0).isFinal_payment());
                                intent.putExtra(ConstantProject.initial_payment, freeDeviceModelList.get(0).isInitial_payment());

                                intent.putExtra(ConstantProject.video_screenImages, (Serializable) imagesList);


                            } else {
                                intent.putExtra(ConstantProject.video_screenImages, (Serializable) imagesList);
                                intent.putExtra(ConstantProject.initial_payment, freeDeviceModelList.get(0).isInitial_payment());
                                intent.putExtra(ConstantProject.final_payment, freeDeviceModelList.get(0).isFinal_payment());


                            }

                            intent.putExtra(ConstantProject.isFrom, ConstantProject.DeviceLocationMapFragment);

                            getActivity().startActivity(intent);


                        } else {
                            DialogManager.showAlertSingleActionDialog(CarryParkApplication.getCurrentActivity(), CarryParkApplication.getCurrentContext().getResources().getString(R.string.noLockedDev), "ok", new DialogManager.IUniActionDialogOnClickListener() {
                                @Override
                                public void onPositiveClick() {

                                }


                            });
                        }
                    }


                } else {
                    Intent intent = new Intent(getActivity(), BottomNavigation.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    intent.putExtra("page", ConstantProject.ScanQRCodeFragment);
                    intent.putExtra(ConstantProject.isFrom, ConstantProject.ScanQRCodeFragment);
                    getActivity().startActivity(intent);
                }

            }
        });
        mMapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap mMap) {
                googleMap = mMap;
                if (checkSelfPermission(
                        getContext(), ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && checkSelfPermission(
                        getContext(), ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(getActivity(), new String[]{ACCESS_FINE_LOCATION}, REQUEST_LOCATION);
                }
                googleMap.setMyLocationEnabled(true);


                if (googleMap != null) {
                    if (!SharedPreferenceUtility.isScannedLock()) {
                        rvLockedDeviceList.setVisibility(View.VISIBLE);
                        rvDevicesList.setVisibility(View.GONE);
                        for (int i = 0; i < CarryParkApplication.getUsedLockerList().size(); i++) {
                            // Location location = locationDataList.get(i).getLocation();
                            if (CarryParkApplication.getUsedLockerList().get(i).getLatitude() != null && CarryParkApplication.getUsedLockerList().get(i).getLongitude() != null) {
                                LatLng position = new LatLng(Double.valueOf(CarryParkApplication.getUsedLockerList().get(i).getLatitude()), Double.valueOf(CarryParkApplication.getUsedLockerList().get(i).getLongitude()));
                                googleMap.addMarker(new MarkerOptions().position(position).title(CarryParkApplication.getUsedLockerList().get(i).getLocation()).snippet(CarryParkApplication.getUsedLockerList().get(i).getAddress_1()));
                                if (i == 0) {
                                   // CameraPosition camPos = new CameraPosition(position, 12, 0, 0);

                                    CameraPosition cameraPosition = new CameraPosition.Builder()
                                            .target(position)
                                            .zoom(12)
                                            .bearing(0)
                                            .tilt(0)
                                            .build();
                                    googleMap.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
                                    googleMap.setPadding(0,320,0,0);
                                   // googleMap.moveCamera(CameraUpdateFactory.newCameraPosition(camPos));
                                    // fillTextViews(location);

                                }
                            }

                        }


                        usedLockers = CarryParkApplication.getUsedLockerList();
                        if (usedLockers.get(0).getFinal_payment()) {
                            tvUnlockData1.setVisibility(View.VISIBLE);
                            tvUnlockData2.setVisibility(View.VISIBLE);
                            tv_unlock_data3.setVisibility(View.VISIBLE);
                            setFinalPayValue();
                        } else {
                            tvUnlockData1.setVisibility(View.GONE);
                            tvUnlockData2.setVisibility(View.GONE);
                            tv_unlock_data3.setVisibility(View.GONE);
                        }
                        usedLockerDeviceRecyclerViewAdapter = new UsedLockerDeviceRecyclerViewAdapter(CarryParkApplication.getCurrentContext(),
                                CarryParkApplication.getCurrentActivity(), usedLockers, getContext().getResources().getString(R.string.inuse), new UsedLockerDeviceRecyclerViewAdapter.CustomOnClickListener() {
                            @Override
                            public void IDevicesConnect(UsedLocker usedLocker) {
                                String urlString = "http://maps.google.com/maps?f=d&hl=en&" + "saddr=" + SharedPreferenceUtility.getLatitude() + "," + SharedPreferenceUtility.getLongitude() + "&daddr=" + usedLocker.getLatitude() + "," + usedLocker.getLongitude() + "&ie=UTF8&0&om=0&output=kml";

                                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(urlString));
                                intent.setPackage("com.google.android.apps.maps");
                                startActivity(intent);
                            }

                            @Override
                            public void SwapPositionToTop(int position) {

                                if (!isFirstTime)
                                {
                                    List<UsedLocker> usedLockersNew = new ArrayList<>();
                                    usedLockersNew.add(usedLockers.get(position));
                                    usedLockers.remove(position);
                                    usedLockersNew.addAll(usedLockers);
                                    usedLockers.clear();
                                    usedLockers.addAll(usedLockersNew);
                                    if (usedLockers != null)
                                        usedLockerDeviceRecyclerViewAdapter.notifyItemRangeChanged(0, usedLockers.size());

                                    if (usedLockers.get(0).getFinal_payment()) {
                                        tvUnlockData1.setVisibility(View.VISIBLE);
                                        tvUnlockData2.setVisibility(View.VISIBLE);
                                        setFinalPayValue();
                                    } else {
                                        tvUnlockData1.setVisibility(View.GONE);
                                        tvUnlockData2.setVisibility(View.GONE);
                                    }

                                }
                                else {
                                    isFirstTime =false;
                                    LatLng lantlong = new LatLng(Double.valueOf(usedLockers.get(position).getLatitude()),Double.valueOf(usedLockers.get(position).getLongitude()));

                                    googleMap.addMarker(new MarkerOptions().position(lantlong).title(usedLockers.get(position).getLocation()).snippet(usedLockers.get(position).getAddress_1())).showInfoWindow();
                                    CameraPosition cameraPosition = new CameraPosition.Builder()
                                            .target(lantlong)
                                            .zoom(15)
                                            .bearing(0)
                                            .tilt(0)
                                            .build();
                                    googleMap.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
                                    googleMap.setPadding(0,320,0,0);
                                    ((BaseActivity) getActivity()).showBusyAnimation("");

                                    final Handler handler = new Handler(Looper.getMainLooper());
                                    handler.postDelayed(new Runnable() {
                                        @Override
                                        public void run() {
                                            List<UsedLocker> usedLockersNew = new ArrayList<>();
                                            usedLockersNew.add(usedLockers.get(position));
                                            usedLockers.remove(position);
                                            usedLockersNew.addAll(usedLockers);
                                            usedLockers.clear();
                                            usedLockers.addAll(usedLockersNew);
                                            if (usedLockers != null)
                                                usedLockerDeviceRecyclerViewAdapter.notifyItemRangeChanged(0, usedLockers.size());

                                            if (usedLockers.get(0).getFinal_payment()) {
                                                tvUnlockData1.setVisibility(View.VISIBLE);
                                                tvUnlockData2.setVisibility(View.VISIBLE);
                                                setFinalPayValue();
                                            } else {
                                                tvUnlockData1.setVisibility(View.GONE);
                                                tvUnlockData2.setVisibility(View.GONE);
                                            }
                                    ((BaseActivity) getActivity()).hideBusyAnimation();

                                            //Do something after 100ms
                                        }
                                    }, 2000);



                                }




                            }
                        });


                        rvLockedDeviceList.setAdapter(usedLockerDeviceRecyclerViewAdapter);
                    }

                    googleMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {
                        @Override
                        public View getInfoWindow(Marker marker) {

                            return null;
                        }

                        @Override
                        public View getInfoContents(Marker marker) {
                           // View v = getLayoutInflater().inflate(R.layout.info_window, null);
                            View v =LayoutInflater.from(getActivity()).inflate(R.layout.info_window, null);

                            TextView showTitle = (TextView) v.findViewById(R.id.showTitle);
                            TextView showSnappest = (TextView) v.findViewById(R.id.showSnappest);
                            ImageView id_imageView = (ImageView) v.findViewById(R.id.id_imageView);
                            TextView address = (TextView) v.findViewById(R.id.address);


                            LatLng latLng = marker.getPosition();
                            showTitle.setText(marker.getTitle());
                            showSnappest.setText(marker.getSnippet());
                            if (SharedPreferenceUtility.isScannedLock()) {
                                if (marker.getTitle() != null && locationDataList != null) {
                                    for (int k = 0; k < locationDataList.size(); k++) {
                                        if (marker.getTitle().equalsIgnoreCase(locationDataList.get(k).getLandmark())) {
                                            if (locationDataList.get(k).getLandmark() != null) {
                                                showTitle.setText(locationDataList.get(k).getLandmark());
                                            }
                                            if (locationDataList.get(k).getAddress_1() != null) {
                                                showSnappest.setText(locationDataList.get(k).getAddress_1());
                                            }
                                            if (locationDataList.get(k).getLandmark() != null) {
                                                address.setText(locationDataList.get(k).getLandmark());
                                            }
                                            if (locationDataList.get(k).getPhoto() != null && !locationDataList.get(k).getPhoto().isEmpty()) {
                                                // Picasso.with(CarryParkApplication.getCurrentActivity()).load(locationDataList.get(k).getPhoto()).into(id_imageView);

                                                //    Glide.with(CarryParkApplication.getCurrentActivity()).load(locationDataList.get(k).getPhoto()).into(id_imageView);
                                                // inflate view window

                                                // set other views content

                                                // set image view like this:
                                                if (not_first_time_showing_info_window) {
                                                    Picasso.with(CarryParkApplication.getCurrentContext()).load(locationDataList.get(k).getPhoto()).into(id_imageView);
                                                } else { // if it's the first time, load the image with the callback set
                                                    not_first_time_showing_info_window = true;
                                                    Picasso.with(CarryParkApplication.getCurrentContext()).load(locationDataList.get(k).getPhoto()).into(id_imageView, new InfoWindowRefresher(marker));
                                                }


                                            } else {
                                                // Picasso.with(CarryParkApplication.getCurrentContext()).load(R.drawable.cp_logo).into(id_imageView);


                                                Glide.with(CarryParkApplication.getCurrentContext())
                                                        .load(CarryParkApplication.getCurrentContext().getResources().getDrawable(R.drawable.cp_logo))
                                                        .placeholder(CarryParkApplication.getCurrentContext().getResources().getDrawable(R.drawable.cp_logo))
                                                        .into(id_imageView);
                                                // id_imageView.setVisibility(View.GONE);
                                            }

                                        }
                                    }
                                }
                                if (marker.getTitle() != null && !marker.getTitle().isEmpty()) {
                                    for (int i = 0; i < locationDataList.size(); i++) {
                                        if (locationDataList.get(i).getLandmark().equals(marker.getTitle())) {
                                            tvPlace.setText("\t" + locationDataList.get(i).getLandmark());

                                            getDevicesByLocation(locationDataList.get(i).getLandmark());
                                            CarryParkApplication.setPlace(locationDataList.get(i).getLocation());
                                            CarryParkApplication.setDepositeTime(locationDataList.get(i).getStart_time());
                                        }
                                    }
                                }
                            } else {

                                if (marker.getTitle() != null && CarryParkApplication.getUsedLockerList() != null) {
                                    for (int k = 0; k < CarryParkApplication.getUsedLockerList().size(); k++) {
                                        if (marker.getTitle().equalsIgnoreCase(CarryParkApplication.getUsedLockerList().get(k).getLocation())) {
                                            if (CarryParkApplication.getUsedLockerList().get(k).getLocation() != null) {
                                                showTitle.setText(CarryParkApplication.getUsedLockerList().get(k).getLocation());
                                            }
                                            if (CarryParkApplication.getUsedLockerList().get(k).getAddress_1() != null) {
                                                showSnappest.setText(CarryParkApplication.getUsedLockerList().get(k).getAddress_1());
                                            }
                                            if (CarryParkApplication.getUsedLockerList().get(k).getLandmark() != null) {
                                                address.setText(CarryParkApplication.getUsedLockerList().get(k).getLandmark());
                                            }
                                            if (CarryParkApplication.getUsedLockerList().get(k).getPhoto() != null && !CarryParkApplication.getUsedLockerList().get(k).getPhoto().isEmpty()) {
                                                // Picasso.with(CarryParkApplication.getCurrentActivity()).load(locationDataList.get(k).getPhoto()).into(id_imageView);

                                                //    Glide.with(CarryParkApplication.getCurrentActivity()).load(locationDataList.get(k).getPhoto()).into(id_imageView);
                                                // inflate view window

                                                // set other views content

                                                // set image view like this:
                                                if (not_first_time_showing_info_window) {
                                                    Picasso.with(CarryParkApplication.getCurrentContext()).load(CarryParkApplication.getUsedLockerList().get(k).getPhoto()).into(id_imageView);
                                                } else { // if it's the first time, load the image with the callback set
                                                    not_first_time_showing_info_window = true;
                                                    Picasso.with(CarryParkApplication.getCurrentContext()).load(CarryParkApplication.getUsedLockerList().get(k).getPhoto()).into(id_imageView, new InfoWindowRefresher(marker));
                                                }


                                            } else {
                                                // Picasso.with(CarryParkApplication.getCurrentContext()).load(R.drawable.cp_logo).into(id_imageView);


                                                Glide.with(CarryParkApplication.getCurrentContext())
                                                        .load(CarryParkApplication.getCurrentContext().getResources().getDrawable(R.drawable.cp_logo))
                                                        .placeholder(CarryParkApplication.getCurrentContext().getResources().getDrawable(R.drawable.cp_logo))
                                                        .into(id_imageView);
                                                // id_imageView.setVisibility(View.GONE);
                                            }

                                        }
                                    }
                                }

                            }


                            return v;
                        }
                    });


                    googleMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
                        @Override
                        public void onInfoWindowClick(Marker marker) {
                            // Called when ANY InfoWindow is clicked
                        }
                    });

                }
            }
        });

        if (checkSelfPermission(
                getContext(), ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && checkSelfPermission(
                getContext(), ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(), new String[]{ACCESS_FINE_LOCATION}, REQUEST_LOCATION);
        }


        if (SharedPreferenceUtility.isScannedLock()) {
            llLockData1.setVisibility(View.VISIBLE);
            llLockData2.setVisibility(View.VISIBLE);
            tvUnlockData1.setVisibility(View.GONE);
            tvUnlockData2.setVisibility(View.GONE);
            tv_unlock_data3.setVisibility(View.GONE);
        } else {
            llLockData1.setVisibility(View.GONE);
            llLockData2.setVisibility(View.GONE);

            tv_unlock_data3.setText(getResources().getString(R.string.unlock_charge_info_one));
            if (CarryParkApplication.getUsedLockerList().get(0).getFinal_payment() == true) {
                tvUnlockData1.setVisibility(View.VISIBLE);
                tvUnlockData2.setVisibility(View.VISIBLE);
                tv_unlock_data3.setVisibility(View.VISIBLE);
            }
        }
        getLastLocation();
        getLocation();

        getLocationDetails(SharedPreferenceUtility.getLatitude(), SharedPreferenceUtility.getLongitude());

        //tinu
        // getLocationDetails("9.672370","76.568670");

    }

    @SuppressLint("MissingPermission")
    public void getLocation() {
        if (SharedPreferenceUtility.isScannedLock()) {
            rvLockedDeviceList.setVisibility(View.GONE);
            rvDevicesList.setVisibility(View.VISIBLE);
            mFusedLocationClient.getLastLocation()
                    .addOnSuccessListener(CarryParkApplication.getCurrentActivity(), new OnSuccessListener<Location>() {
                        @Override
                        public void onSuccess(Location location) {
                            // Got last known location. In some rare situations this can be null.
                            if (location != null) {
                                SharedPreferenceUtility.saveLastKnownLatAndLong("" + location.getLatitude(), "" + location.getLongitude());

                                //  getLocationDetails(CarryParkApplication.getLatitude(),CarryParkApplication.getLongitude());


                            }
                        }
                    });


        }
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
                                    SharedPreferenceUtility.saveLastKnownLatAndLong("" + location.getLatitude(), "" + location.getLongitude());


                                }
                            }
                        }
                );
            } else {
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(intent);
            }
        } else {
            requestPermissions();
        }
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
            SharedPreferenceUtility.saveLastKnownLatAndLong("" + mLastLocation.getLatitude(), "" + mLastLocation.getLongitude());


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

    @Override
    public void onResume() {
        super.onResume();
        mMapView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mMapView.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mMapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mMapView.onLowMemory();
    }

    private void getLocationDetails(String latitude, String longitude) {
        ((BaseActivity) getActivity()).showBusyAnimation("");
        String acess_token = GloablMethods.API_HEADER + AppController.getString(getActivity(), "acess_token");
        Map<String, Object> candidateMap = new HashMap<>();
       candidateMap.put("latitude", latitude);
        candidateMap.put("longitude", longitude);

       /* candidateMap.put("latitude", "35.7846069");
        candidateMap.put("longitude", "139.5971595");
*/
        if (SharedPreferenceUtility.isJapanease()) {

            candidateMap.put("lang_id", ConstantProject.forJapaneaseResponse);

        } else if (SharedPreferenceUtility.isEnglish()) {
            candidateMap.put("lang_id", "en");
        } else if (SharedPreferenceUtility.isKorean()) {
            candidateMap.put("lang_id", ConstantProject.forKoreanResponse);
        } else if (SharedPreferenceUtility.isChinease()) {
            candidateMap.put("lang_id", ConstantProject.forChineaseResponse);
        }
        apiService.getNearestLocations(acess_token, candidateMap).enqueue(new Callback<NearestLocationResponseModel>() {
            @Override
            public void onResponse(Call<NearestLocationResponseModel> call, Response<NearestLocationResponseModel> response) {

                if (response.code() == 200 && response.body().getData() != null) {

                    Boolean status = response.body().getSuccess();
                    if (status == true) {
                        locationDataList.clear();


                        locationDataList.addAll(response.body().getData());


                        CarryParkApplication.setLocationDataList(locationDataList);
                        if (locationDataList != null) {
                            if (locationDataList.size() > 0) {
                                getDevicesByLocation(locationDataList.get(0).getLandmark());
                            }
                        }

                        if (SharedPreferenceUtility.isScannedLock()) {
                            for (int i = 0; i < locationDataList.size(); i++) {
                                if (locationDataList.get(i).getLatitude() != null && locationDataList.get(i).getLongitude() != null) {
                                    LatLng position = new LatLng(Double.valueOf(locationDataList.get(i).getLatitude()), Double.valueOf(locationDataList.get(i).getLongitude()));
                                    googleMap.addMarker(new MarkerOptions().position(position).title(locationDataList.get(i).getLandmark()).snippet(locationDataList.get(i).getAddress_1()));
                                    if (i == 0) {
                                        //CameraPosition camPos = new CameraPosition(position, 12, 0, 0);
                                        CameraPosition cameraPosition = new CameraPosition.Builder()
                                                .target(position)
                                                .zoom(12)
                                                .bearing(0)
                                                .tilt(0)
                                                .build();
                                        googleMap.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
                                        googleMap.setPadding(0,320,0,0);
                                        // googleMap.moveCamera(CameraUpdateFactory.newCameraPosition(camPos));
                                        // fillTextViews(location);
                                    }
                                }

                            }
                        } else {
                            for (int i = 0; i < CarryParkApplication.getUsedLockerList().size(); i++) {
                                // Location location = locationDataList.get(i).getLocation();
                                if (CarryParkApplication.getUsedLockerList().get(i).getLatitude() != null && CarryParkApplication.getUsedLockerList().get(i).getLongitude() != null) {
                                    LatLng position = new LatLng(Double.valueOf(CarryParkApplication.getUsedLockerList().get(i).getLatitude()), Double.valueOf(CarryParkApplication.getUsedLockerList().get(i).getLongitude()));
                                    googleMap.addMarker(new MarkerOptions().position(position).title(CarryParkApplication.getUsedLockerList().get(i).getLocation()).snippet(CarryParkApplication.getUsedLockerList().get(i).getAddress_1()));
                                    if (i == 0) {
                                       // CameraPosition camPos = new CameraPosition(position, 12, 0, 0);
                                       // googleMap.moveCamera(CameraUpdateFactory.newCameraPosition(camPos));
                                        CameraPosition cameraPosition = new CameraPosition.Builder()
                                                .target(position)
                                                .zoom(15)
                                                .bearing(0)
                                                .tilt(0)
                                                .build();
                                        googleMap.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
                                        googleMap.setPadding(0,320,0,0);
                                        // fillTextViews(location);
                                    }
                                }

                            }
                        }


                    }
                } else if (response.code() == 404) {
                    ((BaseActivity) getActivity()).hideBusyAnimation();
                    DialogManager.showAlertSingleActionDialog(CarryParkApplication.getCurrentActivity(), CarryParkApplication.getCurrentContext().getResources().getString(R.string.validation_error), "ok", new DialogManager.IUniActionDialogOnClickListener() {
                        @Override
                        public void onPositiveClick() {

                        }


                    });
                }


            }

            @Override
            public void onFailure(Call<NearestLocationResponseModel> call, Throwable t) {
                ((BaseActivity) getActivity()).hideBusyAnimation();

                if (!Utils.isNetworkConnectionAvailable(CarryParkApplication.getCurrentContext())) {
                    DialogManager.showAlertSingleActionDialog(CarryParkApplication.getCurrentActivity(), CarryParkApplication.getCurrentContext().getResources().getString(R.string.no_internet), "ok", new DialogManager.IUniActionDialogOnClickListener() {
                        @Override
                        public void onPositiveClick() {

                        }
                    });
                } else {
                    DialogManager.showAlertSingleActionDialog(CarryParkApplication.getCurrentActivity(), CarryParkApplication.getCurrentContext().getResources().getString(R.string.please_try_again), "ok", new DialogManager.IUniActionDialogOnClickListener() {
                        @Override
                        public void onPositiveClick() {

                        }
                    });
                }
            }
        });
    }

    private void getDevicesByLocation(String landmark) {

        String acess_token = GloablMethods.API_HEADER + AppController.getString(CarryParkApplication.getCurrentActivity(), "acess_token");
        Map<String, Object> candidateMap = new HashMap<>();
        candidateMap.put("landmark", landmark);
        if (SharedPreferenceUtility.isJapanease()) {

            candidateMap.put("lang_id", ConstantProject.forJapaneaseResponse);

        } else if (SharedPreferenceUtility.isEnglish()) {
            candidateMap.put("lang_id", "en");
        } else if (SharedPreferenceUtility.isKorean()) {
            candidateMap.put("lang_id", ConstantProject.forKoreanResponse);
        } else if (SharedPreferenceUtility.isChinease()) {
            candidateMap.put("lang_id", ConstantProject.forChineaseResponse);
        }
        apiService.getDevicesByLocation(acess_token, candidateMap).enqueue(new Callback<DeviceByLocationData>() {
            @Override
            public void onResponse(Call<DeviceByLocationData> call, Response<DeviceByLocationData> response) {
                ((BaseActivity) CarryParkApplication.getCurrentActivity()).hideBusyAnimation();

                if (response.code() == 200 && response.body() != null) {

                    Boolean status = response.body().getSuccess();
                    if (status == true) {
                        if (response.body().getData() != null) {
                            freeDeviceModelList.clear();
                            freeDeviceModelList.addAll(response.body().getData().getFreeDeviceModelList());
                            if (response.body().getData() != null) {

                                if (response.body().getData().getFreeDeviceModelList() != null)
                                    CarryParkApplication.setNearestDevice(response.body().getData().getFreeDeviceModelList().get(0).getDevice_name());
                                tvPlace.setText("\t" + response.body().getData().getFreeDeviceModelList().get(0).getLandmark());
                                CarryParkApplication.setPlace(locationDataList.get(0).getLocation());
                                CarryParkApplication.setDepositeTime(locationDataList.get(0).getStart_time());
                                if (CarryParkApplication.isIsEnglishLang()) {
                                    tvInuse.setText(CarryParkApplication.getCurrentContext().getResources().getString(R.string.inuse) + " x " + response.body().getData().getLocked_device_count());
                                    tvAvilable.setText("" + response.body().getData().getTotal_count());
                                    tvFree.setText(CarryParkApplication.getCurrentContext().getResources().getString(R.string.free) + " x " + response.body().getData().getFree_device_count());

                                } else {
                                    tvInuse.setText(CarryParkApplication.getCurrentContext().getResources().getString(R.string.inuse) + " x " + response.body().getData().getLocked_device_count() + "台");
                                    tvAvilable.setText("" + response.body().getData().getTotal_count() + "台");
                                    tvFree.setText(CarryParkApplication.getCurrentContext().getResources().getString(R.string.free) + " x " + response.body().getData().getFree_device_count() + "台");

                                }

                            }
                            deviceListAdapter = new DeviceFreeLockRecyclearview(CarryParkApplication.getCurrentContext(),
                                    CarryParkApplication.getCurrentActivity(), freeDeviceModelList, CarryParkApplication.getCurrentActivity().getResources().getString(R.string.free), new DeviceFreeLockRecyclearview.CustomOnClickListener() {
                                @Override
                                public void IDevicesConnect(FreeDeviceModel freeDeviceModel) {
                                    String urlString = "http://maps.google.com/maps?f=d&hl=en&" + "saddr=" + SharedPreferenceUtility.getLatitude() + "," + SharedPreferenceUtility.getLongitude() + "&daddr=" + freeDeviceModel.getLatitude() + "," + freeDeviceModel.getLongitude() + "&ie=UTF8&0&om=0&output=kml";

                                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(urlString));
                                    intent.setPackage("com.google.android.apps.maps");
                                    startActivity(intent);
                                    //
                                }

                                @Override
                                public void SwapPositionToTop(int position) {
                                    if (!isFirstTime)
                                    {
                                        List<FreeDeviceModel> freeDeviceModelListnew = new ArrayList<>();
                                        freeDeviceModelListnew.add(freeDeviceModelList.get(position));
                                        freeDeviceModelList.remove(position);
                                        freeDeviceModelListnew.addAll(freeDeviceModelList);
                                        freeDeviceModelList.clear();
                                        freeDeviceModelList.addAll(freeDeviceModelListnew);
                                        deviceListAdapter.notifyItemRangeChanged(0, freeDeviceModelList.size());
                                        deviceListAdapter.notifyDataSetChanged();

                                    }
                                    else {
                                        isFirstTime =false;
                                        LatLng lantlong = new LatLng(Double.valueOf(freeDeviceModelList.get(position).getLatitude()),Double.valueOf(freeDeviceModelList.get(position).getLongitude()));

                                        googleMap.addMarker(new MarkerOptions().position(lantlong).title(freeDeviceModelList.get(position).getLandmark()).snippet(freeDeviceModelList.get(position).getAddress_1())).showInfoWindow();
                                        CameraPosition cameraPosition = new CameraPosition.Builder()
                                                .target(lantlong)
                                                .zoom(11)
                                                .bearing(0)
                                                .tilt(0)
                                                .build();
                                        googleMap.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
                                        googleMap.setPadding(0,320,0,0);
                                        ((BaseActivity) getActivity()).showBusyAnimation("");

                                        final Handler handler = new Handler(Looper.getMainLooper());
                                        handler.postDelayed(new Runnable() {
                                            @Override
                                            public void run() {

                                                List<FreeDeviceModel> freeDeviceModelListnew = new ArrayList<>();
                                                freeDeviceModelListnew.add(freeDeviceModelList.get(position));
                                                freeDeviceModelList.remove(position);
                                                freeDeviceModelListnew.addAll(freeDeviceModelList);
                                                freeDeviceModelList.clear();
                                                freeDeviceModelList.addAll(freeDeviceModelListnew);
                                                deviceListAdapter.notifyItemRangeChanged(0, freeDeviceModelList.size());
                                                deviceListAdapter.notifyDataSetChanged();
                                              //  ((BaseActivity) getActivity()).hideBusyAnimation();

                                                //Do something after 100ms
                                            }
                                        }, 2000);



                                    }




                                }


                            });


                            rvDevicesList.setAdapter(deviceListAdapter);
                        }

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
            public void onFailure(Call<DeviceByLocationData> call, Throwable t) {
                ((BaseActivity) getActivity()).hideBusyAnimation();

                if (!Utils.isNetworkConnectionAvailable(CarryParkApplication.getCurrentContext())) {
                    DialogManager.showAlertSingleActionDialog(CarryParkApplication.getCurrentActivity(), CarryParkApplication.getCurrentContext().getResources().getString(R.string.no_internet), "ok", new DialogManager.IUniActionDialogOnClickListener() {
                        @Override
                        public void onPositiveClick() {

                        }
                    });
                } else {
                    DialogManager.showAlertSingleActionDialog(CarryParkApplication.getCurrentActivity(), CarryParkApplication.getCurrentContext().getResources().getString(R.string.validation_error), "ok", new DialogManager.IUniActionDialogOnClickListener() {
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
                return (checkSelfPermission(getContext(), permission) == PackageManager.PERMISSION_GRANTED);
            }
        }
        return true;
    }

    private boolean canMakeSmores() {
        return (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP_MR1);
    }


    @TargetApi(Build.VERSION_CODES.M)
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {

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
                            showMessageOKCancel("These permissions are mandatory for the application. Please allow access.",
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
    public void onMapReady(GoogleMap googleMap) {

        if (googleMap != null) {
            googleMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                @Override
                public boolean onMarkerClick(Marker marker) {
                    if (marker.getTitle() != null && marker.getTitle().isEmpty()) {
                        if (!SharedPreferenceUtility.isScannedLock()) {
                            rvLockedDeviceList.setVisibility(View.VISIBLE);
                            rvDevicesList.setVisibility(View.GONE);
                            for (int i = 0; i < CarryParkApplication.getUsedLockerList().size(); i++) {
                                // Location location = locationDataList.get(i).getLocation();
                                if (CarryParkApplication.getUsedLockerList().get(i).getLatitude() != null && CarryParkApplication.getUsedLockerList().get(i).getLongitude() != null) {
                                    LatLng position = new LatLng(Double.valueOf(CarryParkApplication.getUsedLockerList().get(i).getLatitude()), Double.valueOf(CarryParkApplication.getUsedLockerList().get(i).getLongitude()));
                                    googleMap.addMarker(new MarkerOptions().position(position).title(CarryParkApplication.getUsedLockerList().get(i).getLocation()).snippet(""));
                                    if (i == 0) {
                                        CameraPosition cameraPosition = new CameraPosition.Builder()
                                                .target(position)
                                                .zoom(12)
                                                .bearing(0)
                                                .tilt(0)
                                                .build();
                                        googleMap.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
                                        googleMap.setPadding(0,320,0,0);
                                        /*CameraPosition camPos = new CameraPosition(position, 12, 0, 0);
                                        googleMap.moveCamera(CameraUpdateFactory.newCameraPosition(camPos));
                                       */
                                        // fillTextViews(location);
                                    }
                                }

                            }

                        } else {
                            for (int i = 0; i < locationDataList.size(); i++) {
                                if (locationDataList.get(i).getLandmark().equals(marker.getTitle())) {
                                    getDevicesByLocation(locationDataList.get(i).getLandmark());
                                }
                            }
                        }

                    }

                    return true;
                }
            });
        }

    }

    public void replaceFragment(Fragment someFragment) {
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, someFragment);
        transaction.addToBackStack(null);
        transaction.commit();


    }

    public void setFinalPayValue() {
        String onehours = ""+usedLockers.get(0).getExt_unit_time();
        if (SharedPreferenceUtility.isEnglish()) {
            //Excess charge/1 hour/ 100 yen
            //replaceAll("\\s+","")

            String input = "";
            if (usedLockers.get(0).getInitial_hours() == 1) {
                input = "Basic charge" + "／" + usedLockers.get(0).getInitial_hours() + " min" + "／" + usedLockers.get(0).getInitialCharges() + " yen";
                tvUnlockData1.setText(input);

            } else {
                input = "Basic charge／" + usedLockers.get(0).getInitial_hours() + " min" + "／" + usedLockers.get(0).getInitialCharges() + " yen";

                tvUnlockData1.setText(input);

            }
            input = "Excess charge／"+usedLockers.get(0).getExt_unit_time()+" min／" + usedLockers.get(0).getRatePerHour()  + " yen";
            tvUnlockData2.setText(input);

        } else if (SharedPreferenceUtility.isJapanease()) {



            tvUnlockData1.setText("基本料金" + "／" + usedLockers.get(0).getInitial_hours() + "分" + "／" + usedLockers.get(0).getInitialCharges() + "円");
            tvUnlockData2.setText("延長料金" + "／" + onehours + "分" + "／" + usedLockers.get(0).getRatePerHour() + "円");


        } else if (SharedPreferenceUtility.isChinease()) {
            //超额收费/ 1小时/ 100日元
            //            tv_videol1.setText();
            tvUnlockData1.setText("基本费用／" + usedLockers.get(0).getInitial_hours() + "分钟" + "／" + usedLockers.get(0).getInitialCharges() + " 日元");
            tvUnlockData2.setText("超额收费／"+onehours+"分钟／ " + usedLockers.get(0).getRatePerHour() + " 日元");


        } else if (SharedPreferenceUtility.isKorean()) {
            //
            tvUnlockData1.setText("기본 요금／" + usedLockers.get(0).getInitial_hours() + "분" + "／" + usedLockers.get(0).getInitialCharges() + " 엔");
            tvUnlockData2.setText("기본 요금／"+onehours+"분／" + usedLockers.get(0).getRatePerHour() + " 엔");


        }
    }

    private class InfoWindowRefresher implements Callback, com.squareup.picasso.Callback {
        private Marker markerToRefresh;

        private InfoWindowRefresher(Marker markerToRefresh) {
            this.markerToRefresh = markerToRefresh;
        }


        @Override
        public void onResponse(Call call, Response response) {
            markerToRefresh.showInfoWindow();

        }

        @Override
        public void onFailure(Call call, Throwable t) {
            markerToRefresh.showInfoWindow();

        }

        @Override
        public void onSuccess() {
            markerToRefresh.showInfoWindow();
        }

        @Override
        public void onError() {
            markerToRefresh.hideInfoWindow();
        }
    }
}
