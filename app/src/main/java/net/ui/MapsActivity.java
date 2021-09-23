package net.ui;

import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.Build;
import android.util.Log;
import android.widget.TextView;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;
import android.os.Bundle;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.gms.maps.*;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import net.CarryParkApplication;

import net.others.ConstantProject;
import net.others.DialogManager;
import net.others.SharedPreferenceUtility;
import net.rest.ApiClient;
import net.rest.ApiInterface;
import net.rest.GloablMethods;
import net.rest.Utils;
import net.rest.global.AppController;
import net.rest.model.DeviceByLocationData;
import net.rest.model.FreeDeviceModel;
import net.rest.model.LocationData;
import net.rest.model.NearestLocationResponseModel;
import net.simplifiedcoding.carrypark.Adapter.DeviceListAdapter;
import net.simplifiedcoding.carrypark.LocationTrack;
import net.simplifiedcoding.carrypark.R;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static android.Manifest.permission.ACCESS_COARSE_LOCATION;
import static android.Manifest.permission.ACCESS_FINE_LOCATION;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    TextView tvPlace;
    ApiInterface apiService;
    private List<LocationData> locationDataList;
    LocationManager locationManager;
    String latitude, longitude;
    private static final int REQUEST_LOCATION = 1;
    List<FreeDeviceModel> freeDeviceModelList;
    DeviceListAdapter deviceListAdapter;
    RecyclerView rvDevicesList;
    private Marker myMarker;
    private ArrayList permissionsToRequest;
    private ArrayList permissionsRejected = new ArrayList();
    private ArrayList permissions = new ArrayList();
    private final static int ALL_PERMISSIONS_RESULT = 101;
    LocationTrack locationTrack;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        CarryParkApplication.setCurrentContext(this);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        apiService = ApiClient.getClient().create(ApiInterface.class);

        tvPlace = (TextView)findViewById(R.id.tv_place);
        rvDevicesList = (RecyclerView) findViewById(R.id.rv_devices);
        locationDataList = new ArrayList<>();
        ActivityCompat.requestPermissions(this,
                new String[]{ACCESS_FINE_LOCATION}, REQUEST_LOCATION);
        permissions.add(ACCESS_FINE_LOCATION);
        permissions.add(ACCESS_COARSE_LOCATION);
        freeDeviceModelList = new ArrayList<>();
        rvDevicesList = (RecyclerView)findViewById(R.id.rv_devices);
        rvDevicesList.setHasFixedSize(true);
        rvDevicesList.setLayoutManager(new LinearLayoutManager(this));
        permissionsToRequest = findUnAskedPermissions(permissions);
        //get the permissions we have asked for before but are not granted..
        //we will store this in a global list to access later.


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {


            if (permissionsToRequest.size() > 0)
                requestPermissions((String[]) permissionsToRequest.toArray(new String[permissionsToRequest.size()]), ALL_PERMISSIONS_RESULT);
        }






    }



    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        LatLng sydney = new LatLng(-34, 151);
        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
        // Add a marker in Sydney and move the camera
        if (checkSelfPermission(
               ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && checkSelfPermission(
               ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(CarryParkApplication.getCurrentActivity(), new String[]{ACCESS_FINE_LOCATION}, REQUEST_LOCATION);
        }
        mMap.setMyLocationEnabled(true);

        getLocation();


       /* mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                if (marker.getTitle() != null && marker.getTitle().isEmpty()) {
                    for (int i = 0; i < locationDataList.size(); i++) {
                        if (locationDataList.get(i).getLocation().equals(marker.getTitle())) {
                            getDevicesByLocation(locationDataList.get(i).getLocation_id());
                        }
                    }
                }

                return true;
            }
        });*/


    }
    public void getLocation()
    {
        locationTrack = new LocationTrack(this);


        if (locationTrack.canGetLocation()) {


            double longitude = locationTrack.getLongitude();
            double latitude = locationTrack.getLatitude();
            getLocationDetails(String.valueOf(latitude),String.valueOf(longitude));

        } else {

            locationTrack.showSettingsAlert();
        }


    }
    private void getLocationDetails( String latitude,String longitude) {
        String acess_token = GloablMethods.API_HEADER + AppController.getString(CarryParkApplication.getCurrentActivity(), "acess_token");
        Map<String, Object> candidateMap = new HashMap<>();
        candidateMap.put("latitude", latitude);
        candidateMap.put("longitude", longitude);
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

        Log.e("acess_token",acess_token);
        apiService.getNearestLocations(acess_token, candidateMap).enqueue(new Callback<NearestLocationResponseModel>() {
            @Override
            public void onResponse(Call<NearestLocationResponseModel> call, Response<NearestLocationResponseModel> response) {
                if (response.code() == 200 && response.body().getData() != null) {

                    Boolean status = response.body().getSuccess();
                    if (status == true) {
                        locationDataList.addAll(response.body().getData());
                        if (response.body().getData()!=null)
                        {
                            if (mMap.isMyLocationEnabled())
                            {
                                for (int i = 0; i < locationDataList.size(); i++) {
                                    // Location location = locationDataList.get(i).getLocation();
                                    if (locationDataList.get(i).getLatitude()!=null && locationDataList.get(i).getLongitude()!=null)
                                    {
                                        LatLng position = new LatLng(Double.valueOf(locationDataList.get(i).getLatitude()), Double.valueOf(locationDataList.get(i).getLongitude()));
                                        mMap.addMarker(new MarkerOptions().position(position).title("dfsdf").snippet("dfdsf"));
                                        if (i == 0) {
                                            CameraPosition camPos = new CameraPosition(position, 12, 0, 0);
                                            mMap.moveCamera(CameraUpdateFactory.newCameraPosition(camPos));
                                            // fillTextViews(location);
                                        }
                                    }

                                }
                            }
                        }


                    }
                } else if (response.code() == 404) {

                    DialogManager.showAlertSingleActionDialog(CarryParkApplication.getCurrentActivity(),CarryParkApplication.getCurrentContext().getResources().getString(R.string.validation_error), "ok", new DialogManager.IUniActionDialogOnClickListener() {
                        @Override
                        public void onPositiveClick() {

                        }


                    });      }
            }

            @Override
            public void onFailure(Call<NearestLocationResponseModel> call, Throwable t) {

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

    private void getDevicesByLocation(int location_id) {

        String acess_token = GloablMethods.API_HEADER + AppController.getString(CarryParkApplication.getCurrentActivity(), "acess_token");
        Map<String, Object> candidateMap = new HashMap<>();

        if (SharedPreferenceUtility.isEnglish())
        {
            candidateMap.put("location_id", location_id);
        }

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

        apiService.getDevicesByLocation(acess_token, candidateMap).enqueue(new Callback<DeviceByLocationData>() {
            @Override
            public void onResponse(Call<DeviceByLocationData> call, Response<DeviceByLocationData> response) {
                if (response.code() == 200 && response.body() != null) {

                    Boolean status = response.body().getSuccess();
                    if (status == true) {
                        if (response.body().getData()!=null)
                        {
                            freeDeviceModelList.addAll(response.body().getData().getFreeDeviceModelList());

                            deviceListAdapter = new DeviceListAdapter(CarryParkApplication.getCurrentActivity(), freeDeviceModelList, CarryParkApplication.getCurrentContext(),CarryParkApplication.getCurrentContext().getResources().getString(R.string.succLock), new DeviceListAdapter.IDeviceListAdapter() {


                                @Override
                                public void IDevicesConnect(FreeDeviceModel freeDeviceModel) {

                                }
                            });
                            rvDevicesList.setAdapter(deviceListAdapter);
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
            public void onFailure(Call<DeviceByLocationData> call, Throwable t) {

                if (!Utils.isNetworkConnectionAvailable(CarryParkApplication.getCurrentContext())) {

                    DialogManager.showAlertSingleActionDialog(CarryParkApplication.getCurrentActivity(), CarryParkApplication.getCurrentContext().getResources().getString(R.string.please_try_again), "ok", new DialogManager.IUniActionDialogOnClickListener() {
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
                return (checkSelfPermission(permission) == PackageManager.PERMISSION_GRANTED);
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
        new AlertDialog.Builder(CarryParkApplication.getCurrentActivity())
                .setMessage(message)
                .setPositiveButton("OK", okListener)
                .setNegativeButton("Cancel", null)
                .create()
                .show();
    }


}
