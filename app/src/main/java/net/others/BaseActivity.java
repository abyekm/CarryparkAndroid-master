package net.others;


import android.app.Activity;
import android.app.Dialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.*;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.text.TextUtils;
import android.util.Log;
import android.view.*;
import android.view.inputmethod.InputMethodManager;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import net.CarryParkApplication;
import net.rest.ApiInterface;
import net.rest.model.BluetoothObject;
import net.simplifiedcoding.carrypark.R;

import java.util.*;

import static net.CarryParkApplication.getCurrentActivity;



public class BaseActivity extends AppCompatActivity {

    private Dialog progressDialog;

    private LinearLayout navigationDrawer;
    ApiInterface apiService;
    private int activityCode;
    public  static ArrayList<BluetoothObject> finalArrayOfFoundBTDevices;
    Fragment targetFragment = null;

    //BLE Scan


    private BluetoothAdapter           bluetoothAdapter;

    public void showBusyAnimation(final String message) {

        getCurrentActivity().runOnUiThread(new Runnable() {

            @Override
            public void run() {

                LayoutInflater inflater = (LayoutInflater) CarryParkApplication.getCurrentContext().getSystemService(LAYOUT_INFLATER_SERVICE);
                View progressLayout = inflater.inflate(R.layout.custom_progress_dialog_view, null);

                TextView loadingText = (TextView) progressLayout.findViewById(R.id.tv_loadingmsg);

                if (message.equalsIgnoreCase("")) {

                    loadingText.setVisibility(View.GONE);
                } else {
                    loadingText.setVisibility(View.VISIBLE);
                    loadingText.setText(message);
                }

                progressDialog = new Dialog(CarryParkApplication.getCurrentContext());
                progressDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                progressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                progressDialog.setContentView(progressLayout);
                progressDialog.setCancelable(true);
                progressDialog.setCanceledOnTouchOutside(false);
                if(!((Activity) CarryParkApplication.getCurrentContext()).isFinishing())
                {
                    progressDialog.show();
                }



            }
        });

    }


    public void hideBusyAnimation() {

        getCurrentActivity().runOnUiThread(new Runnable() {
            public void run() {

                try {
                    if(!((Activity) CarryParkApplication.getCurrentContext()).isFinishing())
                    {
                        if ((progressDialog != null) && progressDialog.isShowing()) {
                            progressDialog.dismiss();
                        }
                    }

                } catch (final Exception e) {
                    e.printStackTrace();
                } finally {
                    progressDialog = null;
                }
            }
        });

    }








    public void killAllBackgroundTask()
    {
        //displayListOfFoundDevices();

      /*  List<ApplicationInfo> packages;
        PackageManager pm;
        pm = getPackageManager();
        //get a list of installed apps.
        packages = pm.getInstalledApplications(0);

        ActivityManager mActivityManager = (ActivityManager)CarryParkApplication.getCurrentContext().getSystemService(Context.ACTIVITY_SERVICE);

        for (ApplicationInfo packageInfo : packages) {
            if((packageInfo.flags & ApplicationInfo.FLAG_SYSTEM)==1)continue;
            if(packageInfo.packageName.equals(getApplicationContext().getPackageName())) continue;
            mActivityManager.killBackgroundProcesses(packageInfo.packageName);
        }*/
    }

    public boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }


    public  void hideKeyboard(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        //Find the currently focused view, so we can grab the correct window token from it.
        View view = activity.getCurrentFocus();
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = new View(activity);
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    public void callBackgroundScanning()
    {
        new Thread(new FilterDeviceMethod2()).start();
    }

    class FilterDeviceMethod2 implements Runnable {
        @Override
        public void run() {
            int BLUETOOTH_REQUEST = 1;
            bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

            if (bluetoothAdapter == null) {
                DialogManager.showAlertSingleActionDialog(CarryParkApplication.getCurrentActivity(),CarryParkApplication.getCurrentContext().getResources().getString(R.string.bluetooth_not_support), "ok", new DialogManager.IUniActionDialogOnClickListener() {
                    @Override
                    public void onPositiveClick() {

                    }


                });
                // Show proper message here
                finish();
            } else {


                if (!bluetoothAdapter.isEnabled()) {
                    Intent enableBluetoothIntent = new Intent(
                            BluetoothAdapter.ACTION_REQUEST_ENABLE);
                    startActivityForResult(enableBluetoothIntent, BLUETOOTH_REQUEST);
                } else {
                   // setButtonsEnabled(true);
                }
            }
            bluetoothAdapter.enable();
            displayListOfFoundDevices();

        }
    }






    private void displayListOfFoundDevices()
    {
        ArrayList <BluetoothObject> arrayOfFoundBTDevices = null;

        arrayOfFoundBTDevices = new ArrayList<BluetoothObject>();
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        // start looking for bluetooth devices
        bluetoothAdapter.startDiscovery();

        // Discover new devices
        // Create a BroadcastReceiver for ACTION_FOUND
         finalArrayOfFoundBTDevices = arrayOfFoundBTDevices;
        final BroadcastReceiver mReceiver = new BroadcastReceiver()
        {
            @Override
            public void onReceive(Context context, Intent intent)
            {
                String action = intent.getAction();
                // When discovery finds a device
                if (BluetoothDevice.ACTION_FOUND.equals(action))
                {
                    // Get the bluetoothDevice object from the Intent
                    BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);

                    // Get the "RSSI" to get the signal strength as integer,
                    // but should be displayed in "dBm" units
                    int rssi = intent.getShortExtra(BluetoothDevice.EXTRA_RSSI,Short.MIN_VALUE);

                    // Create the device object and add it to the arrayList of devices
                    BluetoothObject bluetoothObject = new BluetoothObject();
                    bluetoothObject.setName(device.getName());
                    bluetoothObject.setAddress(device.getAddress());
                    bluetoothObject.setUuid(""+device.getUuids());


                    // Log.e("BlueTooth",device.getName()+device.getAddress());
                   /* bluetoothObject.setBluetooth_state(device.getBondState());
                    bluetoothObject.setBluetooth_type(device.getType());    // requires API 18 or higher
                    bluetoothObject.setBluetooth_rssi(rssi);
*/
                    finalArrayOfFoundBTDevices.add(bluetoothObject);


                }
            }
        };
        // Register the BroadcastReceiver
        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        registerReceiver(mReceiver, filter);
    }

    public static String getDeviceName() {
        String manufacturer = Build.MANUFACTURER;
        String model = Build.MODEL;
        if (model.startsWith(manufacturer)) {
            return capitalize(model);
        }
        return capitalize(manufacturer) + " " + model;
    }

    public static String capitalize(String str) {
        if (TextUtils.isEmpty(str)) {
            return str;
        }
        char[] arr = str.toCharArray();
        boolean capitalizeNext = true;

        StringBuilder phrase = new StringBuilder();
        for (char c : arr) {
            if (capitalizeNext && Character.isLetter(c)) {
                phrase.append(Character.toUpperCase(c));
                capitalizeNext = false;
                continue;
            } else if (Character.isWhitespace(c)) {
                capitalizeNext = true;
            }
            phrase.append(c);
        }

        return phrase.toString();
    }

    public String DeviceVersion()
    {
        String manufacturer = Build.MANUFACTURER;
        String model = Build.MODEL;
        int version = Build.VERSION.SDK_INT;
        String versionRelease = Build.VERSION.RELEASE;
        String Appversion="";
        try {
            PackageInfo pInfo = CarryParkApplication.getCurrentContext().getPackageManager().getPackageInfo(getPackageName(), 0);
            Appversion = pInfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        String  data= "manufacturer :"+manufacturer+", "
                +" model :"+model+", "+
                " Android version :"+versionRelease
                +" Carrypark Version :"+ Appversion;
        return data;


    }





}
