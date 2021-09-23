package net.simplifiedcoding.carrypark;


import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.*;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.IBinder;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import net.CarryParkApplication;
import net.others.ConstantProject;
import net.others.DialogManager;
import net.others.SharedPreferenceUtility;

import java.util.Locale;


/**
 * A simple {@link Fragment} subclass.
 */
public class ForgotPinFragment extends Fragment implements ServiceConnection, SerialListener {
    Fragment targetFragment = null;
Button button_login;
    private boolean initialStart = true;
    private enum Connected {False, Pending, True}
    private Connected connected = Connected.True;
    private TextView receiveText;

    private SerialService service;
    public ForgotPinFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
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


        return inflater.inflate(R.layout.fragment_forgot_pin, container, false);

    }
    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (service==null)
        {
            service=CarryParkApplication.getService();
        }
        button_login = (Button) view.findViewById(R.id.button_login);
        button_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
        receiveText = view.findViewById(R.id.et_email_address);


    }


    @Override
    public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
        service = ((SerialService.SerialBinder) iBinder).getService();
        service.attach(this);
        CarryParkApplication.setService(service);
        if (initialStart && isResumed()) {
            initialStart = false;
            getActivity().runOnUiThread(this::connect);
        }

    }

    @Override
    public void onServiceDisconnected(ComponentName componentName)  {
        service = null;
    }

    public void onSerialConnect() {


        connected = Connected.True;
    }

    @Override
    public void onSerialConnectError(Exception e) {

    }

    @Override
    public void onSerialRead(byte[] data) {
        receive(data);
    }

   @Override
    public void onSerialIoError(Exception e) {




    }

    @Override
    public void onReadDataAtFailure(byte[] data) {

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
        if(connected != Connected.True) {
            return;
        }
        try {
            byte[] data = (str).getBytes();
            service.write(data);
        } catch (Exception e) {
            onSerialIoError(e);
        }
    }
    private void receive(byte[] data) {

        receiveText.append(new String(data));

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
}
