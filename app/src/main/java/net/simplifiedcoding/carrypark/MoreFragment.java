package net.simplifiedcoding.carrypark;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.*;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.IBinder;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


import net.CarryParkApplication;
import net.others.ConstantProject;
import net.others.DialogManager;
import net.others.SharedPreferenceUtility;
import net.rest.Utils;
import net.ui.UserSelectionActivity;

import java.util.Locale;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by Belal on 1/23/2018.tv_logout
 */

public class MoreFragment extends Fragment implements View.OnClickListener, ServiceConnection, SerialListener {
    // security
    private boolean initialStart = true;
    private enum Connected {False, Pending, True}
    private Connected connected = Connected.True;
    private TextView receiveText;

    private SerialService service;
    public MoreFragment() {
        // Required empty public constructor
    }

    private TextView tvLogout,tvUser,tv_change_pin,tv_edit_account_details,tv_help;
    private SharedPreferences sharedPreferences;

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


        return inflater.inflate(R.layout.fragment_more, null);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (service==null)
        {
            service=CarryParkApplication.getService();
        }

        sharedPreferences = this.getActivity().getSharedPreferences("carry_park",MODE_PRIVATE);
        tvLogout = (TextView) view.findViewById(R.id.tv_logout);
        tvUser = (TextView) view.findViewById(R.id.tv_user);
        tv_change_pin = (TextView) view.findViewById(R.id.tv_change_pin);
        tv_edit_account_details = (TextView)view.findViewById(R.id.tv_edit_account_details);
        tv_help = (TextView) view.findViewById(R.id.tv_help);
        String userName = sharedPreferences.getString("user_name",null);
        //tvUser.setText(userName);
        //tvUser.setText("Hi,"+userName);
        //tvUser.setText(getString(R.string.hi)+", "+userName);
        if (CarryParkApplication.isIsEnglishLang())
        {
            tvUser.setText(getString(R.string.hi)+", "+userName);
        }
        else if (CarryParkApplication.isIsJapaneaseLang())
        {
            //"ようこそ、Carryparkへ
            //○○さん"
            tvUser.setText("ようこそ "+userName+"さん");
        }
        else
        {
            tvUser.setText(getString(R.string.hi)+", "+userName);
        }
        tvLogout.setOnClickListener(this);
        tv_change_pin.setOnClickListener(this);
        CarryParkApplication.setIsAliPay(false);

        tv_edit_account_details.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                 EditAccountDetailsFragment ldf = new EditAccountDetailsFragment();
                getFragmentManager().beginTransaction().add(R.id.fragment_container, ldf).commit();
            }
        });
        tv_help.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                HelpFragment  ldf = new HelpFragment();
                getFragmentManager().beginTransaction().add(R.id.fragment_container, ldf).commit();

            }
        });

        disconnect();

    }

    @Override
    public void onClick(View v) {
        switch(v.getId()) {
            case R.id.tv_logout:
                //sharedPreferences= this.getActivity().getSharedPreferences("carry_park",MODE_PRIVATE);

              DialogManager.showLogoutDialogue(new DialogManager.IMultiActionDialogOnClickListener() {
                    @Override
                    public void onPositiveClick() {
                        SharedPreferences.Editor editor=sharedPreferences.edit();
                        editor.putString("IS_LOGGED_IN","FALSE");
                        editor.putString("user_name",null);
                        editor.apply();
                        Intent intent = new Intent(getContext(), UserSelectionActivity.class);
                        startActivity(intent);
                        getActivity().finish();
                    }

                    @Override
                    public void onNegativeClick() {

                    }
                });
              /*  new AlertDialog.Builder(CarryParkApplication.getCurrentContext())
                        .setTitle(R.string.logout)
                        .setMessage(R.string.are_u_sre_u_wnt_logout)

                        // Specifying a listener allows you to take an action before dismissing the dialog.
                        // The dialog is automatically dismissed when a dialog button is clicked.
                        .setPositiveButton(getResources().getString(R.string.logOut), new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                // Continue with delete operation


                            }
                        })

                        // A null listener allows the button to dismiss the dialog and take no further action.
                        .setNegativeButton(android.R.string.no, null)
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .show();*/
                break;
            case R.id.tv_change_pin:
                replaceFragment();
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

    public void replaceFragment() {
        /*FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, someFragment);
        transaction.addToBackStack(null);
        transaction.commit();*/

        //Put the value
        ChangePinFragment ldf = new ChangePinFragment();
        /*Bundle args = new Bundle();
        args.putString("scannedDeviceCode", scanned_device_code);
        ldf.setArguments(args);*/


        //Inflate the fragment
        getFragmentManager().beginTransaction().add(R.id.fragment_container, ldf).commit();


    }

    @Override
    public void onResume() {
        super.onResume();

        if(!Utils.isNetworkConnectionAvailable(CarryParkApplication.getCurrentContext())){
            DialogManager.showAlertSingleActionDialog(CarryParkApplication.getCurrentActivity(), CarryParkApplication.getCurrentContext().getResources().getString(R.string.no_internet), "ok", new DialogManager.IUniActionDialogOnClickListener() {
                @Override
                public void onPositiveClick() {

                }
            });
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
        getActivity().bindService(new Intent(getActivity(), SerialService.class), this, Context.BIND_AUTO_CREATE);
    }
    private void disconnect() {
        connected =Connected.False;
        if (service!=null)
            service.disconnect();

    }
}
