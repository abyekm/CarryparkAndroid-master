package net.others;

import android.content.Context;
import android.content.SharedPreferences;

import net.simplifiedcoding.carrypark.R;

import static net.CarryParkApplication.getCurrentContext;


/*
 * Created by Ȿ₳Ɲ @ GIZMEON ©  on 07-03-2017.
 */

public class SharedPreferenceUtility {


    private static SharedPreferences sharedPreferences;


    public static SharedPreferences getSharedPreferenceInstance() {
        if (sharedPreferences == null)
            sharedPreferences = getCurrentContext().getSharedPreferences(
                    getCurrentContext().getString(R.string.USER_PREFERENCES),
                    Context.MODE_PRIVATE);

        return sharedPreferences;
    }

    //* SAVING USER ID & TOKEN *//

    public static void saveHashValueComparisonAtUnLock(String DeviceName,boolean status)
    {
        SharedPreferences.Editor sharedPreferencesEditor = getSharedPreferenceInstance().edit();

        sharedPreferencesEditor.putString("dev_name_unLock_hash", DeviceName);
        sharedPreferencesEditor.putBoolean("dev_name_unLock_staus", status);
        sharedPreferencesEditor.commit();
    }

    public static void saveEmailId(String email)
    {
        SharedPreferences.Editor sharedPreferencesEditor = getSharedPreferenceInstance().edit();

        sharedPreferencesEditor.putString("email", email);
        sharedPreferencesEditor.commit();
    }

    public static void savePhoneNumber(String phone)
    {
        SharedPreferences.Editor sharedPreferencesEditor = getSharedPreferenceInstance().edit();

        sharedPreferencesEditor.putString("phone_num", phone);
        sharedPreferencesEditor.commit();
    }

    public static String getPhoneNumber() {

        return getSharedPreferenceInstance().getString("phone_num", "");
    }

    public static String getDeviceNameAtHashValueComAtUnLock() {

        return getSharedPreferenceInstance().getString("dev_name_unLock_hash", "");
    }
    public static boolean getDeviceStatusAtHashValueComAtUnLock() {

        return getSharedPreferenceInstance().getBoolean("dev_name_unLock_staus", false);
    }


    public static void saveLang(boolean isEnglish,boolean isJapanease,boolean isKorean,boolean isChinease)
    {
        SharedPreferences.Editor sharedPreferencesEditor = getSharedPreferenceInstance().edit();

        sharedPreferencesEditor.putBoolean("isEnglish", isEnglish);
        sharedPreferencesEditor.putBoolean("isJapanease", isJapanease);
        sharedPreferencesEditor.putBoolean("isKorean", isKorean);
        sharedPreferencesEditor.putBoolean("isChinease", isChinease);
        sharedPreferencesEditor.commit();
    }
    public static void saveLangLocal(String lang)
    {
        SharedPreferences.Editor sharedPreferencesEditor = getSharedPreferenceInstance().edit();

        sharedPreferencesEditor.putString("langu_age", lang);

        sharedPreferencesEditor.commit();
    }

    public static String getLangLocal() {

        return getSharedPreferenceInstance().getString("langu_age", "ja");
    }
    public static void saveHashValues(String hashValues)
    {
        SharedPreferences.Editor sharedPreferencesEditor = getSharedPreferenceInstance().edit();

        sharedPreferencesEditor.putString("hashValues", hashValues);
        sharedPreferencesEditor.commit();
    }

    public static void saveScannedDeviceInfo_isLock(boolean isLock)
    {
        SharedPreferences.Editor sharedPreferencesEditor = getSharedPreferenceInstance().edit();

        sharedPreferencesEditor.putBoolean("isLock", isLock);
        sharedPreferencesEditor.commit();
    }


    public static void saveScanClickEvent(boolean isScan)
    {
        SharedPreferences.Editor sharedPreferencesEditor = getSharedPreferenceInstance().edit();

        sharedPreferencesEditor.putBoolean("isScan", isScan);
        sharedPreferencesEditor.commit();
    }

    public static void saveIsCheckedLockerSize(boolean CheckedLockerSize)
    {
        SharedPreferences.Editor sharedPreferencesEditor = getSharedPreferenceInstance().edit();

        sharedPreferencesEditor.putBoolean("CheckedLockerSize", CheckedLockerSize);
        sharedPreferencesEditor.commit();
    }
    public static boolean getIsCheckedLockerSize() {

        return getSharedPreferenceInstance().getBoolean("CheckedLockerSize", false);
    }

    public static String getHashValues() {

        return getSharedPreferenceInstance().getString("hashValues", "");
    }
    public static String getEmailId() {

        return getSharedPreferenceInstance().getString("email", "");
    }


    public static boolean isScanClickEvent() {

        return getSharedPreferenceInstance().getBoolean("isScan", false);
    }

    public static boolean isScannedLock() {

        return getSharedPreferenceInstance().getBoolean("isLock", false);
    }
    public static boolean isEnglish() {

        return getSharedPreferenceInstance().getBoolean("isEnglish", false);
    }
    public static boolean isJapanease() {

        return getSharedPreferenceInstance().getBoolean("isJapanease", false);
    }
    public static boolean isKorean() {

        return getSharedPreferenceInstance().getBoolean("isKorean", false);
    }
    public static boolean isChinease() {

        return getSharedPreferenceInstance().getBoolean("isChinease", false);
    }

    public static boolean isPinVerifyied() {

        return getSharedPreferenceInstance().getBoolean("pinVerify", false);
    }

    public static void SavePinVerified(boolean isVerifyied)
    {
        SharedPreferences.Editor sharedPreferencesEditor = getSharedPreferenceInstance().edit();

        sharedPreferencesEditor.putBoolean("pinVerify", isVerifyied);
        sharedPreferencesEditor.commit();
    }
    public static void SaveDeviceIdMacID(String  deviceId)
    {
        SharedPreferences.Editor sharedPreferencesEditor = getSharedPreferenceInstance().edit();

        sharedPreferencesEditor.putString("deviceId", deviceId);
        sharedPreferencesEditor.commit();
    }
    public static String  getDeviceIdMacID() {

        return getSharedPreferenceInstance().getString("deviceId", "");
    }

    public static  void  SavePaymentToken(String PayToken)
    {
        SharedPreferences.Editor sharedPreferencesEditor = getSharedPreferenceInstance().edit();

        sharedPreferencesEditor.putString("PayToken", PayToken);
        sharedPreferencesEditor.commit();
    }

    public static String  getPaymentToken() {

        return getSharedPreferenceInstance().getString("PayToken", "");
    }
    public static void saveLastKnownLatAndLong(String  latitude,String longitude)
    {
        SharedPreferences.Editor sharedPreferencesEditor = getSharedPreferenceInstance().edit();

        sharedPreferencesEditor.putString("latitude", latitude);
        sharedPreferencesEditor.putString("longitude", longitude);
        sharedPreferencesEditor.commit();
    }
    public static String getLatitude()
    {
        return getSharedPreferenceInstance().getString("latitude", "35.6762");

    }
    public static String getLongitude()
    {
        return getSharedPreferenceInstance().getString("longitude", "139.6503");

    }

    public static void saveDeviceAddress(String  deviceName,String deviceAddress)
    {
        SharedPreferences.Editor sharedPreferencesEditor = getSharedPreferenceInstance().edit();

        sharedPreferencesEditor.putString(deviceName, deviceAddress);
        sharedPreferencesEditor.commit();
    }

    public static String getDeviceAddress(String deviceName)
    {
        return getSharedPreferenceInstance().getString(deviceName, "00");

    }

    public static void saveDeviceCurrentStatus(String  status)
    {
        SharedPreferences.Editor sharedPreferencesEditor = getSharedPreferenceInstance().edit();

        sharedPreferencesEditor.putString("status", status);
        sharedPreferencesEditor.commit();
    }
    public static String getDevicePresentStatus()
    {
        return getSharedPreferenceInstance().getString("status", "");

    }

    public static void saveDeepLinkDeviceDetails(String  deviceNameDeepLink,String deviceAddressDeepLink,String deviceCodeDeepLink)
    {
        SharedPreferences.Editor sharedPreferencesEditor = getSharedPreferenceInstance().edit();

        sharedPreferencesEditor.putString("deviceNameDeepLink", deviceNameDeepLink);
        sharedPreferencesEditor.putString("deviceAddressDeepLink", deviceAddressDeepLink);
        sharedPreferencesEditor.putString("deviceCodeDeepLink", deviceCodeDeepLink);
        sharedPreferencesEditor.commit();
    }
    public static String getDeepLinkDeviceName()
    {
        return getSharedPreferenceInstance().getString("deviceNameDeepLink", "");

    }

    public static String getDeepLinkdeviceAddress()
    {
        return getSharedPreferenceInstance().getString("deviceAddressDeepLink", "");

    }
    public static String getDeepLinkDeviceCode()
    {
        return getSharedPreferenceInstance().getString("deviceCodeDeepLink", "");

    }


}
