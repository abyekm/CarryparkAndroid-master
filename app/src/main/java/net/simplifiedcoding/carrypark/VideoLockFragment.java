package net.simplifiedcoding.carrypark;

import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.BluetoothLeScanner;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import net.CarryParkApplication;
import net.others.ConstantProject;
import net.others.SharedPreferenceUtility;
import net.rest.Utils;

import java.util.Locale;

import static android.content.Context.MODE_PRIVATE;

public class VideoLockFragment extends Fragment implements View.OnClickListener {

    private SharedPreferences sharedPreferences;
    private InitialPaymentFragment targetFragment = null;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_video_lock, null);
    }
    WebView mWebView;
    Button buttonProceedToPayment;
    TextView tvUser;
    String languageToLoad = "en";
    //for bluetooth enabling sjn test
    BluetoothManager btManager;
    BluetoothAdapter btAdapter;
    BluetoothLeScanner btScanner;
    TextView tvPlace,tvDepositeTime;
    private final static int REQUEST_ENABLE_BT = 1;
    //
    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {

        //test-----------------------------------------------------------------------------------
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

        //this.setContentView(R.layout.activity_main);
        //----------------------------------------------------------------------------------
        super.onViewCreated(view, savedInstanceState);
        tvUser = (TextView)view.findViewById(R.id.tv_user);
        buttonProceedToPayment = (Button)view.findViewById(R.id.button_proceed_to_payment);
        buttonProceedToPayment.setOnClickListener(this);


        mWebView = (WebView) view.findViewById(R.id.webview);
        mWebView.setWebViewClient(new WebViewClient());
        mWebView.getSettings().setJavaScriptEnabled(true);
        mWebView.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
        mWebView.getSettings().setPluginState(WebSettings.PluginState.ON);
        mWebView.getSettings().setUserAgentString(null);
        mWebView.getSettings().setMediaPlaybackRequiresUserGesture(false);
        tvPlace =(TextView) view.findViewById(R.id.tv_place) ;
        tvDepositeTime = (TextView) view.findViewById(R.id.tv_deposite_time);

        mWebView.setWebChromeClient(new WebChromeClient());
        //mWebView.loadUrl("https://youtu.be/zILw5eV9QBQ");
       // mWebView.loadUrl("https://www.radiantmediaplayer.com/media/bbb-360p.mp4");
        mWebView.loadUrl(ConstantProject.VideoUrlFull);


        mWebView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return true;
            }
        });
        CarryParkApplication.setIsAliPay(false);

        sharedPreferences = getActivity().getSharedPreferences("carry_park",MODE_PRIVATE);

        //getActivity().getSharedPreferences("carry_park",MODE_PRIVATE);
        String user = sharedPreferences.getString("user_name",null);
        //tvUser.setText("Hi,"+user);
        if (CarryParkApplication.isIsEnglishLang())
        {
            tvUser.setText(getString(R.string.hi)+", "+user);
        }
        else if (CarryParkApplication.isIsJapaneaseLang())
        {
            //"ようこそ、Carryparkへ
            //○○さん"
            tvUser.setText("ようこそ "+user+"さん");
        }
        else
        {
            tvUser.setText(getString(R.string.hi)+", "+user);
        }

        btManager = (BluetoothManager) getActivity().getSystemService(Context.BLUETOOTH_SERVICE);
        btAdapter = btManager.getAdapter();
        btScanner = btAdapter.getBluetoothLeScanner();

        if (btAdapter != null && !btAdapter.isEnabled()) {
            Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableIntent, REQUEST_ENABLE_BT);
        }
    }



    @Override
    public void onClick(View v) {
        switch(v.getId()) {
            case R.id.button_proceed_to_payment:
                  if(!Utils.isNetworkConnectionAvailable(CarryParkApplication.getCurrentContext())){
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
                }else{
                    mWebView.destroy();
                    targetFragment = new InitialPaymentFragment();
                    replaceFragment(targetFragment);
                }


                break;

        }
    }

    public void replaceFragment(Fragment someFragment) {
        String lockStatus = sharedPreferences.getString("isLocked", null);
        if(lockStatus!=null && lockStatus.equalsIgnoreCase("LOCKED")){
            /*tvInitialPaymentTitle.setText(R.string.final_payment);
            tvInitialPayAmountLabel.setText(R.string.final_payment_amount);*/
            FinalPaymentFragment ldf = new FinalPaymentFragment ();
            Bundle args = new Bundle();
            args.putString("is_from", "VideoFragment");
            ldf.setArguments(args);

            //Inflate the fragment
            getFragmentManager().beginTransaction().add(R.id.fragment_container,ldf).commit();
        }else{
            InitialPaymentFragment ldf = new InitialPaymentFragment();
            Bundle args = new Bundle();
            args.putString("is_from", "VideoFragment");
            ldf.setArguments(args);

            //Inflate the fragment
            getFragmentManager().beginTransaction().add(R.id.fragment_container,ldf).commit();
        }
    }
    @Override
    public void onResume() {
        super.onResume();
        if(!Utils.isNetworkConnectionAvailable(getContext())){
            new AlertDialog.Builder(getContext())
                    .setTitle(R.string.alert)
                    .setMessage(R.string.no_internet)
                    .setCancelable(false)
                    .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            // Whatever..
                        }
                    }).show();
        }
    }


}
