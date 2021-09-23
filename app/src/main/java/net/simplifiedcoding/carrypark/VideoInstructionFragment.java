package net.simplifiedcoding.carrypark;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.BluetoothLeScanner;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.widget.TableLayout;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.TextView;


import androidx.viewpager.widget.ViewPager;
import com.viewpagerindicator.CirclePageIndicator;
import net.CarryParkApplication;
import net.others.CommonMethod;
import net.others.ConstantProject;
import net.others.DialogManager;
import net.others.SharedPreferenceUtility;
import net.rest.Utils;
import net.simplifiedcoding.carrypark.Adapter.SlidingImage_Adapter;
import net.ui.BottomNavigation;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

import static android.content.Context.MODE_PRIVATE;


public class VideoInstructionFragment extends Fragment implements View.OnClickListener {

    private SharedPreferences sharedPreferences;
    private InitialPaymentFragment targetFragment = null;
    private static ViewPager mPager;
    private static int currentPage = 0;
    private static int NUM_PAGES = 0;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_video_instruction, null);
    }
    WebView mWebView;
    Button buttonProceedToPayment;
    TextView tvUser,tv_fianlPayInfo1,tv_placel,tv_videol1,tv_device_name,tv_free_of_charge;
    ;
    String languageToLoad = "en";
    //for bluetooth enabling sjn test
    BluetoothManager btManager;
    BluetoothAdapter btAdapter;
    BluetoothLeScanner btScanner;
    TextView tvPlace,tvDepositeTime,tv_deposite_timel;
    TableLayout table_view;
    boolean isPaymentRequired,isFinalPayment,isRedirectionCalled;
    private final static int REQUEST_ENABLE_BT = 1;
    private List<String> imageList;
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
        tv_free_of_charge =(TextView) view.findViewById(R.id.tv_free_of_charge);
        tv_device_name= (TextView) view.findViewById(R.id.tv_device_name);
        tvDepositeTime = (TextView) view.findViewById(R.id.tv_deposite_time);
        table_view=(TableLayout) view.findViewById(R.id.table_view);
        tv_fianlPayInfo1 = (TextView)view.findViewById(R.id.tv_fianlPayInfo1);
        tv_placel=(TextView)view.findViewById(R.id.tv_placel);
        tv_deposite_timel = (TextView)view.findViewById(R.id.tv_deposite_timel);
        tv_videol1 =(TextView)view.findViewById(R.id.tv_videol1);
        mPager = (ViewPager) view.findViewById(R.id.pager);
        CirclePageIndicator indicator = (CirclePageIndicator)
                view.findViewById(R.id.indicator);


        if (getArguments()!=null)
        {
            isPaymentRequired =getArguments().getBoolean(ConstantProject.initial_payment);
            isFinalPayment = CarryParkApplication.isIsFinalPayment();
            imageList =getArguments().getStringArrayList(ConstantProject.video_screenImages);
        }

        mWebView.setWebChromeClient(new WebChromeClient());
        //mWebView.loadUrl("https://youtu.be/zILw5eV9QBQ");
        //mWebView.loadUrl("https://www.radiantmediaplayer.com/media/big-buck-bunny-360p.mp4");

        mWebView.loadUrl(ConstantProject.VideoUrlFull);
        tvPlace.setText(CarryParkApplication.getPlace()+"／"+CarryParkApplication.getLandMark());
        // tvPlace.setText(CarryParkApplication.getLandMark());

        if (isPaymentRequired)
        {
            table_view.setVisibility(View.VISIBLE);
        }
        else {
            table_view.setVisibility(View.GONE);
        }
        if (isFinalPayment)
        {
            tv_fianlPayInfo1.setVisibility(View.VISIBLE);

        }
        else {
            tv_fianlPayInfo1.setVisibility(View.GONE);

        }


      /*  mWebView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return true;
            }
        });*/
        CarryParkApplication.setIsAliPay(false);

        sharedPreferences = getActivity().getSharedPreferences("carry_park",MODE_PRIVATE);

        //getActivity().getSharedPreferences("carry_park",MODE_PRIVATE);
        String user = sharedPreferences.getString("user_name",null);
        if (user!=null)
        {
            CarryParkApplication.setUserName(user);
        }
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
        tv_device_name.setText(CarryParkApplication.getDeviceName());
        if (isPaymentRequired)
        {
            tv_free_of_charge.setVisibility(View.GONE);
        }
        else {
            tv_free_of_charge.setVisibility(View.VISIBLE);
        }
        String onehrs=CarryParkApplication.getMinituesRate();
        if (SharedPreferenceUtility.isEnglish())
        {
            //Basic charge/2 hours/200 yen
            if (isPaymentRequired)
            {
                if (CarryParkApplication.getInitial_hours().equals("1"))
                {
                    tv_videol1.setText("Basic charge／"+CarryParkApplication.getInitial_hours()+" hour"+"／"+CarryParkApplication.getInitial_charges()+" yen");

                }
                else {
                    tv_videol1.setText("Basic charge／"+CarryParkApplication.getInitial_hours()+" hours"+"／"+CarryParkApplication.getInitial_charges()+" yen");

                }
            }
           //Excess charge/1 hour/ 100 yen
            if (isFinalPayment)
                tv_fianlPayInfo1.setText("Excess charge／"+CarryParkApplication.getMinituesRate()+" min／"+CarryParkApplication.getRate_per_hour()+" yen");

        }

        else if (SharedPreferenceUtility.isJapanease() )
        {

            //基本料金／2時間／200円
            if (isPaymentRequired)
            tv_videol1.setText("基本料金／"+CarryParkApplication.getInitial_hours()+"分"+"／"+CarryParkApplication.getInitial_charges()+"円");
            //超過料金/１時間/100円
            if (isFinalPayment)
                tv_fianlPayInfo1.setText("延長料金"+"／"+onehrs+"分"+"／"+CarryParkApplication.getRate_per_hour()+"円");


        }
        else if (SharedPreferenceUtility.isChinease() )
        {
            //"基本费用/ 2小时/ 200日元"
            if (isPaymentRequired)
            tv_videol1.setText("基本费用／"+CarryParkApplication.getInitial_hours()+"分钟"+"／"+CarryParkApplication.getInitial_charges()+" 日元");
           //超额收费/ 1小时/ 100日元
            if (isFinalPayment)
                tv_fianlPayInfo1.setText("超额收费／"+onehrs+"分钟／ "+CarryParkApplication.getRate_per_hour()+" 日元");


        }
        else if (SharedPreferenceUtility.isKorean())
        {
            //""기본 요금 / 2 시간 / 200 엔""
            if (isPaymentRequired)
            tv_videol1.setText("기본 요금／"+CarryParkApplication.getInitial_hours()+"분"+"／"+CarryParkApplication.getInitial_charges()+" 엔");
            if (isFinalPayment)
                tv_fianlPayInfo1.setText("기본 요금／"+onehrs+"분/"+CarryParkApplication.getRate_per_hour()+" 엔");


        }
        if (SharedPreferenceUtility.isChinease())
        {
            tv_placel.setText("位置");
            tv_deposite_timel.setText("存款时间");
            buttonProceedToPayment.setText("继续");

        }
        if (SharedPreferenceUtility.isKorean())
        {
            tv_placel.setText("위치");
            tv_deposite_timel.setText("입금 시간");
            buttonProceedToPayment.setText("발하다");
        }
        btManager = (BluetoothManager) getActivity().getSystemService(Context.BLUETOOTH_SERVICE);
        btAdapter = btManager.getAdapter();
        btScanner = btAdapter.getBluetoothLeScanner();


        if (btAdapter != null && !btAdapter.isEnabled()) {
            Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableIntent, REQUEST_ENABLE_BT);
        }


        if (CarryParkApplication.getLocationDataList()!=null)
        {
            if (CarryParkApplication.getWorks_24_hours()==1)
            {
                tvDepositeTime.setText(CarryParkApplication.getCurrentContext().getResources().getString(R.string.hrs));
            }
            else if (CarryParkApplication.getLocationDataList().get(0).getStart_time()!=null && CarryParkApplication.getLocationDataList().get(0).getEnd_time()!=null)
            {
                tv_free_of_charge.setVisibility(View.GONE);
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
                Date date_start =null,date_end=null;


                try {
                    DateFormat timeFormat = new SimpleDateFormat("HH:mm:ss");

                    date_start = timeFormat.parse(CarryParkApplication.getDepositeTime());
                    date_end = timeFormat.parse(CarryParkApplication.getEndTime());



                }
                catch (ParseException e) {
                    e.printStackTrace();
                }
                if (CarryParkApplication.isIsJapaneaseLang())
                {


                    if (date_start!=null  && date_end!=null)
                    tvDepositeTime.setText(CommonMethod.timeFormatInJapaneaseAvoidZero(amOrpm.format(date_start),hh.format(date_start),mm.format(date_start)) +" ~ "+CommonMethod.timeFormatInJapanease(amOrpm.format(date_end),hh.format(date_end),mm.format(date_end)));

                }
                else
                {
                    if (date_start!=null  && date_end!=null)
                    tvDepositeTime.setText(CommonMethod.timeFormatInEnglish(amOrpm.format(date_start),hh.format(date_start),mm.format(date_start)) +" - "+CommonMethod.timeFormatInEnglish(amOrpm.format(date_end),hh.format(date_end),mm.format(date_end)));

                }

            }
        }



        if (imageList!=null)
        {
            if (imageList.size()>0)
            {
                mWebView.setVisibility(View.GONE);
                mPager.setVisibility(View.VISIBLE);
            }
            else
            {
               // mWebView.setVisibility(View.VISIBLE);
                mPager.setVisibility(View.GONE);
            }
        }

        mPager.setAdapter(new SlidingImage_Adapter(CarryParkApplication.getCurrentContext(),imageList));




        indicator.setViewPager(mPager);

        final float density = getResources().getDisplayMetrics().density;

//Set circle indicator radius
        indicator.setRadius(5 * density);

        NUM_PAGES =imageList.size();

        // Auto start of viewpager
        /* final Handler handler = new Handler();
        final Runnable Update = new Runnable() {
            public void run() {
                if (currentPage == NUM_PAGES) {
                    currentPage = 0;
                }
                mPager.setCurrentItem(currentPage++, true);
            }
        };
       Timer swipeTimer = new Timer();
        swipeTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                handler.post(Update);
            }
        }, 3000, 3000);*/

        // Pager listener over indicator
        indicator.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            @Override
            public void onPageSelected(int position) {
                currentPage = position;

            }

            @Override
            public void onPageScrolled(int pos, float arg1, int arg2) {

            }

            @Override
            public void onPageScrollStateChanged(int pos) {

            }
        });

    }



    @Override
    public void onClick(View v) {
        switch(v.getId()) {
            case R.id.button_proceed_to_payment:
                //mWebView.stopLoading();
               // mWebView.destroy();
                /*targetFragment = new PaymentFragment();
                InUseFragment ldf = new InUseFragment ();
                replaceFragment(targetFragment,ldf);*/
                if(!Utils.isNetworkConnectionAvailable(CarryParkApplication.getCurrentContext())){
                    DialogManager.showAlertSingleActionDialog(CarryParkApplication.getCurrentActivity(), CarryParkApplication.getCurrentContext().getResources().getString(R.string.no_internet), "ok", new DialogManager.IUniActionDialogOnClickListener() {
                        @Override
                        public void onPositiveClick() {

                        }
                    });
                }else{
                    if (!isRedirectionCalled)
                    {
                        isRedirectionCalled=true;
                        mWebView.destroy();

                        Fragment fragment = null;
                        fragment = new ScanQrcodeFragment();
                        loadFragment(fragment);
                        ((BottomNavigation) getActivity()).EvaluateScanClick();


                    }

/*
                    Intent intent = new Intent(getActivity(), BottomNavigation.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    intent.putExtra("page", ScanQRCodeFragment);
                    getActivity().startActivity(intent);*/
                }


                break;
            /*case R.id.img_back_arrow:
                onBackPressed();
                break;
            case R.id.button_login:
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

    public void loadFragment(Fragment fragment) {
        getFragmentManager().beginTransaction().add(R.id.fragment_container,fragment).commit();

    }


    @Override
    public void onResume() {
        super.onResume();
        if(!Utils.isNetworkConnectionAvailable(getContext())){
            DialogManager.showAlertSingleActionDialog(CarryParkApplication.getCurrentActivity(), CarryParkApplication.getCurrentContext().getResources().getString(R.string.no_internet), "ok", new DialogManager.IUniActionDialogOnClickListener() {
                @Override
                public void onPositiveClick() {

                }
            });
        }
    }


    public String  getTime(String inTime)
    {


        Date time = null;
        String timeOut;
        try {
            DateFormat timeFormat = new SimpleDateFormat("HH:mm:ss");
           // timeFormat.setTimeZone(TimeZone.getTimeZone("GMT")); // Add this. "GMT" works too.

            time = timeFormat.parse(inTime);
            timeOut=time.toString();


        }
        catch (ParseException e) {
            e.printStackTrace();
            return "";
        }

        return timeOut;
    }
}
