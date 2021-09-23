package net;

import android.app.Activity;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.widget.ArrayAdapter;
import androidx.multidex.MultiDexApplication;
import com.splunk.mint.Mint;
import net.others.SharedPreferenceUtility;
import net.rest.model.*;
import net.simplifiedcoding.carrypark.SerialService;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class CarryParkApplication extends MultiDexApplication {
    private static Context mContext;
    private static Activity mActivity;
    public static boolean isEnglishLang;
    public static boolean isJapaneaseLang;
    public static boolean isKorean;
    public static boolean isChinease;
    public static String deviceId;
    public static boolean isInuseScan;

    public static String  deviceName;
    public static boolean isFromLoginasNewUser;
    public static boolean isAliPay;
    public static boolean isFromSplash;
    public static String scannedDeviceCode;
    public static String deviceAddressBle;
    public static String scannedDeviceName;
    public static String currentUser;
    public static String deviceAddress;
    public static String currentSenderFragment;
    public static boolean isLockProcess;
    public static String RANDOM_APP_KEY;
    public static List<UsedLocker> usedLockerList = new ArrayList<>();
    public static boolean isHashValueSend;
    public static boolean isStatusChecking;
    public static int User_ID;
    public static String closeHashValue;
    public static boolean isAwaitingForPutLockDown;
    public static boolean isSendMM;
    public static boolean isSendRR;
    public static String userName;
    public static String place;
    public static String depositeTime;
    public static String unLockToken;
    public static  boolean isPaymentRequired;
    public static String initial_charge;
    public static boolean isPaymentByPassingRequired;
    public static String dob;
    public static boolean hasInitialPaymentPreviouslyDone;
    public static String  nearestDevice;
    public static List<LocationData> locationDataList;
    public static SerialService service;
    public static boolean isCheckIn;
    public static LockerDetailsResponse lockerDetailsResponseList;
    public static List<BluetoothModel> mDeviceList;
    public static ArrayList<BluetoothDevice>    listItems = new ArrayList<>();
    public static ArrayAdapter<BluetoothDevice>listAdapter;
    public static boolean ForgotToCallSuccessApi;
    public static boolean isConnectedWithSavedAddress;
    public static boolean isRescan;
    public static String EndTime;
    public static String initial_charges;
    public static String initial_hours;
    public static String rate_per_hour;
    public static int works_24_hours;
    public static String timeLogContent;
    public static String landMark;
    public static String batterypower;
    public static String openPdfLocation;
    public static String invoiceAmount;
    public static String invoiceDate;
    public static boolean isFinalPayment;
    public static String minituesRate;

    public static String mobile;
    public static String email;
    public static String first_name;
    public static String cmpny_name;


    public static String getPersonMobile() {
        return mobile;
    }

    public static void setPersonMobile(String mobile) {
        CarryParkApplication.mobile = mobile;
    }

    public static String getPersonemail() {
        return email;
    }

    public static void setPersonemail(String email) {
        CarryParkApplication.email = email;
    }

    public static String getfirst_name() {
        return first_name;
    }

    public static void setfirst_name(String first_name) {
        CarryParkApplication.first_name = first_name;
    }

    public static String getcmpny_name() {
        return cmpny_name;
    }

    public static void setcmpny_name(String cmpny_name) {
        CarryParkApplication.cmpny_name = cmpny_name;
    }

    public static Context getCurrentContext() {
        return mContext;
    }

    public static void setCurrentContext(Context mContext) {
        CarryParkApplication.mContext = mContext;
        CarryParkApplication.mActivity = (Activity) mContext;


    }
    public static Activity getCurrentActivity() {
        return mActivity;
    }


    @Override
    public void onCreate() {


        super.onCreate();
        mContext = getApplicationContext();
        Mint.initAndStartSession(this, "10af703e");


    }


    public static boolean isIsEnglishLang() {
        return isEnglishLang;
    }

    public static void setIsEnglishLang(boolean isEnglishLang) {
        CarryParkApplication.isEnglishLang = isEnglishLang;
    }

    public static boolean isIsJapaneaseLang() {
        return isJapaneaseLang;
    }

    public static void setIsJapaneaseLang(boolean isJapaneaseLang) {
        CarryParkApplication.isJapaneaseLang = isJapaneaseLang;
    }





    public static boolean isIsInuseScan() {
        return isInuseScan;
    }

    public static void setIsInuseScan(boolean isInuseScan) {
        CarryParkApplication.isInuseScan = isInuseScan;
    }




    public static String getDeviceName() {
        return deviceName;
    }

    public static void setDeviceName(String deviceName) {
        CarryParkApplication.deviceName = deviceName;
    }

    public static boolean isIsFromLoginasNewUser() {
        return isFromLoginasNewUser;
    }

    public static void setIsFromLoginasNewUser(boolean isFromLoginasNewUser) {
        CarryParkApplication.isFromLoginasNewUser = isFromLoginasNewUser;
    }

    public static List<UsedLocker> getUsedLockerList() {
        return usedLockerList;
    }

    public static void setUsedLockerList(List<UsedLocker> usedLockerList) {
        CarryParkApplication.usedLockerList = usedLockerList;
    }

    public static String getCurrentUser() {
        return currentUser;
    }

    public static void setCurrentUser(String currentUser) {
        CarryParkApplication.currentUser = currentUser;
    }

    public static boolean isIsAliPay() {
        return isAliPay;
    }

    public static void setIsAliPay(boolean isAliPay) {
        CarryParkApplication.isAliPay = isAliPay;
    }

    public static boolean isIsFromSplash() {
        return isFromSplash;
    }

    public static void setIsFromSplash(boolean isFromSplash) {
        CarryParkApplication.isFromSplash = isFromSplash;
    }

    public static String getScannedDeviceCode() {
        return scannedDeviceCode;
    }

    public static void setScannedDeviceCode(String scannedDeviceCode) {
        CarryParkApplication.scannedDeviceCode = scannedDeviceCode;
    }

    public static String getDeviceAddressBle() {
        return deviceAddressBle;
    }

    public static void setDeviceAddressBle(String deviceAddressBle) {
        CarryParkApplication.deviceAddressBle = deviceAddressBle;
    }

    public static SerialService getService() {
        return service;
    }

    public static void setService(SerialService service) {
        CarryParkApplication.service = service;
    }

    public static String getScannedDeviceName() {
        return scannedDeviceName;
    }

    public static void setScannedDeviceName(String scannedDeviceName) {
        CarryParkApplication.scannedDeviceName = scannedDeviceName;
    }





    public static Context getmContext() {
        return mContext;
    }

    public static void setmContext(Context mContext) {
        CarryParkApplication.mContext = mContext;
    }

    public static String getCurrentSenderFragment() {
        return currentSenderFragment;
    }

    public static void setCurrentSenderFragment(String currentSenderFragment) {
        CarryParkApplication.currentSenderFragment = currentSenderFragment;
    }

    public static boolean isIsLockProcess() {
        return isLockProcess;
    }

    public static void setIsLockProcess(boolean isLockProcess) {
        CarryParkApplication.isLockProcess = isLockProcess;
    }

    public static String getRandomAppKey() {
        return RANDOM_APP_KEY;
    }

    public static void setRandomAppKey(String randomAppKey) {
        RANDOM_APP_KEY = randomAppKey;
    }

    public static boolean isIsHashValueSend() {
        return isHashValueSend;
    }

    public static void setIsHashValueSend(boolean isHashValueSend) {
        CarryParkApplication.isHashValueSend = isHashValueSend;
    }

    public static int getUser_ID() {
        return User_ID;
    }

    public static void setUser_ID(int user_ID) {
        User_ID = user_ID;
    }

    public static boolean isIsStatusChecking() {
        return isStatusChecking;
    }

    public static void setIsStatusChecking(boolean isStatusChecking) {
        CarryParkApplication.isStatusChecking = isStatusChecking;
    }

    public static String getCloseHashValue() {
        return closeHashValue;
    }

    public static void setCloseHashValue(String closeHashValue) {
        CarryParkApplication.closeHashValue = closeHashValue;
    }

    public static boolean isIsAwaitingForPutLockDown() {
        return isAwaitingForPutLockDown;
    }

    public static void setIsAwaitingForPutLockDown(boolean isAwaitingForPutLockDown) {
        CarryParkApplication.isAwaitingForPutLockDown = isAwaitingForPutLockDown;
    }

    public static boolean isIsSendMM() {
        return isSendMM;
    }

    public static void setIsSendMM(boolean isSendMM) {
        CarryParkApplication.isSendMM = isSendMM;
    }

    public static boolean isIsSendRR() {
        return isSendRR;
    }

    public static void setIsSendRR(boolean isSendRR) {
        CarryParkApplication.isSendRR = isSendRR;
    }

    public static String getUserName() {
        return userName;
    }

    public static void setUserName(String userName) {
        CarryParkApplication.userName = userName;
    }

    public static String getPlace() {
        return place;
    }

    public static void setPlace(String place) {
        CarryParkApplication.place = place;
    }

    public static String getDepositeTime() {
        return depositeTime;
    }

    public static void setDepositeTime(String depositeTime) {
        CarryParkApplication.depositeTime = depositeTime;
    }

    public static String getInvoiceAmount() {
        return invoiceAmount;
    }

    public static void setInvoiceAmount(String invoiceAmount) {
        CarryParkApplication.invoiceAmount = invoiceAmount;
    }

    public static String getUnLockToken() {
        return unLockToken;
    }

    public static void setUnLockToken(String unLockToken) {
        CarryParkApplication.unLockToken = unLockToken;
    }

    public static boolean isIsPaymentRequired() {
        return isPaymentRequired;
    }

    public static void setIsPaymentRequired(boolean isPaymentRequired) {
        CarryParkApplication.isPaymentRequired = isPaymentRequired;
    }

    public static String getInitial_charge() {
        return initial_charge;
    }

    public static void setInitial_charge(String initial_charge) {
        CarryParkApplication.initial_charge = initial_charge;
    }


    public static boolean IsPaymentByPassingNoRequired() {
        return isPaymentByPassingRequired;
    }

    public static void setIsPaymentByPassingNotRequired(boolean isPaymentByPassingRequired) {
        CarryParkApplication.isPaymentByPassingRequired = isPaymentByPassingRequired;


    }

    public static String getDob() {
        return dob;
    }

    public static void setDob(String dob) {
        CarryParkApplication.dob = dob;
    }


    public static boolean isHasInitialPaymentPreviouslyDone() {
        return hasInitialPaymentPreviouslyDone;
    }

    public static void setHasInitialPaymentPreviouslyDone(boolean hasInitialPaymentPreviouslyDone) {
        CarryParkApplication.hasInitialPaymentPreviouslyDone = hasInitialPaymentPreviouslyDone;
    }

    public static String getNearestDevice() {
        return nearestDevice;
    }

    public static List<LocationData> getLocationDataList() {
        return locationDataList;
    }

    public static void setLocationDataList(List<LocationData> locationDataList) {
        CarryParkApplication.locationDataList = locationDataList;
    }

    public static boolean isIsCheckIn() {
        return isCheckIn;
    }

    public static void setIsCheckIn(boolean isCheckIn) {
        CarryParkApplication.isCheckIn = isCheckIn;
    }

    public static void setNearestDevice(String nearestDevice) {
        CarryParkApplication.nearestDevice = nearestDevice;
    }

    public static LockerDetailsResponse getLockerDetailsResponseList() {
        return lockerDetailsResponseList;
    }

    public static void setLockerDetailsResponseList(LockerDetailsResponse lockerDetailsResponseList) {
        CarryParkApplication.lockerDetailsResponseList = lockerDetailsResponseList;
    }

    public static List<BluetoothModel> getmDeviceList() {
        return mDeviceList;
    }

    public static void setmDeviceList(List<BluetoothModel> mDeviceList) {
        CarryParkApplication.mDeviceList = mDeviceList;
    }

    public static ArrayList<BluetoothDevice> getListItems() {
        return listItems;
    }

    public static void setListItems(ArrayList<BluetoothDevice> listItems) {
        CarryParkApplication.listItems = listItems;
    }

    public static ArrayAdapter<BluetoothDevice> getListAdapter() {
        return listAdapter;
    }

    public static void setListAdapter(ArrayAdapter<BluetoothDevice> listAdapter) {
        CarryParkApplication.listAdapter = listAdapter;
    }

    public static boolean isIsKorean() {
        return isKorean;
    }

    public static void setIsKorean(boolean isKorean) {
        CarryParkApplication.isKorean = isKorean;
    }

    public static boolean isIsChinease() {
        return isChinease;
    }

    public static void setIsChinease(boolean isChinease) {
        CarryParkApplication.isChinease = isChinease;
    }

    public static boolean isForgotToCallSuccessApi() {
        return ForgotToCallSuccessApi;
    }

    public static void setForgotToCallSuccessApi(boolean forgotToCallSuccessApi) {
        ForgotToCallSuccessApi = forgotToCallSuccessApi;
    }

    public static boolean isIsConnectedWithSavedAddress() {
        return isConnectedWithSavedAddress;
    }

    public static void setIsConnectedWithSavedAddress(boolean isConnectedWithSavedAddress) {
        CarryParkApplication.isConnectedWithSavedAddress = isConnectedWithSavedAddress;
    }

    public static boolean isIsRescan() {
        return isRescan;
    }

    public static void setIsRescan(boolean isRescan) {
        CarryParkApplication.isRescan = isRescan;
    }

    public static String getEndTime() {
        return EndTime;
    }

    public static void setEndTime(String endTime) {
        EndTime = endTime;
    }

    public static String getInitial_charges() {
        return initial_charges;
    }

    public static void setInitial_charges(String initial_charges) {
        CarryParkApplication.initial_charges = initial_charges;
    }

    public static String getInitial_hours() {
        return initial_hours;
    }

    public static void setInitial_hours(String initial_hours) {
        CarryParkApplication.initial_hours = initial_hours;
    }

    public static String getRate_per_hour() {
        return rate_per_hour;
    }

    public static void setRate_per_hour(String rate_per_hour) {
        CarryParkApplication.rate_per_hour = rate_per_hour;
    }

    public static int getWorks_24_hours() {
        return works_24_hours;
    }

    public static void setWorks_24_hours(int works_24_hours) {
        CarryParkApplication.works_24_hours = works_24_hours;
    }

    public static String getTimeLogContent() {
        return timeLogContent;
    }

    public static void setTimeLogContent(String timeLogContent) {
        CarryParkApplication.timeLogContent = timeLogContent;
    }

    public static String getLandMark() {
        return landMark;
    }

    public static void setLandMark(String landMark) {
        CarryParkApplication.landMark = landMark;
    }

    public static String getBatterypower() {
        return batterypower;
    }

    public static void setBatterypower(String batterypower) {
        CarryParkApplication.batterypower = batterypower;
    }

    public static String getOpenPdfLocation() {
        return openPdfLocation;
    }

    public static void setOpenPdfLocation(String openPdfLocation) {
        CarryParkApplication.openPdfLocation = openPdfLocation;
    }



    public static String getInvoiceDate() {
        return invoiceDate;
    }

    public static void setInvoiceDate(String invoiceDate) {
        CarryParkApplication.invoiceDate = invoiceDate;
    }

    public static boolean isIsFinalPayment() {
        return isFinalPayment;
    }

    public static void setIsFinalPayment(boolean isFinalPayment) {
        CarryParkApplication.isFinalPayment = isFinalPayment;
    }

    public static String getMinituesRate() {
        return minituesRate;
    }

    public static void setMinituesRate(String minituesRate) {
        CarryParkApplication.minituesRate = minituesRate;
    }
}

