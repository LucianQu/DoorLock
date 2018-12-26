package com.blg.rtu.util;

/**
 *
 */

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class SharepreferenceUtils {

    public static void savePswisSave(Context context, boolean issave) {
        SharedPreferences preference = context.getSharedPreferences(
                "preference_psw_save", 0);
        SharedPreferences.Editor editor = preference.edit();
        editor.putBoolean("issave", issave);
        editor.commit();
    }

    public static boolean getPswisSave(Context context) {
        SharedPreferences preference = context.getSharedPreferences(
                "preference_psw_save", 0);
        boolean issave = preference.getBoolean("issave", false);
        return issave;
    }

    public static void saveHasLearn(Context context, boolean is) {
        SharedPreferences preferences = context.getSharedPreferences("preference_HasLearn", 0);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean("HasLearn", is);
        editor.commit();
    }

    public static boolean getHasLearn(Context context) {
        SharedPreferences preferences = context.getSharedPreferences("preference_HasLearn", 0);
        boolean type = preferences.getBoolean("HasLearn", false);
        return type;
    }

    public static void saveIsWifi(Context context, boolean is) {
        SharedPreferences preferences = context.getSharedPreferences("preference_IsWifi", 0);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean("IsWifi", is);
        editor.commit();
    }

    public static boolean getIsWifi(Context context) {
        SharedPreferences preferences = context.getSharedPreferences("preference_IsWifi", 0);
        boolean type = preferences.getBoolean("IsWifi", false);
        return type;
    }

    public static void saveIsDoor(Context context, boolean is) {
        SharedPreferences preferences = context.getSharedPreferences("preference_IsWifi", 0);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean("IsDoor", is);
        editor.commit();
    }

    public static boolean getIsDoor(Context context) {
        SharedPreferences preferences = context.getSharedPreferences("preference_IsWifi", 0);
        boolean type = preferences.getBoolean("IsDoor", true);
        return type;
    }

    public static void saveMessage(Context context, String is) {
        SharedPreferences preferences = context.getSharedPreferences("preference_Message", 0);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("Message", is);
        editor.commit();
    }

    public static String getMessage(Context context) {
        SharedPreferences preferences = context.getSharedPreferences("preference_Message", 0);
        String rechargemax = preferences.getString("Message", "");
        return rechargemax;
    }

    public static void saveJiaQuan(Context context, String is) {
        SharedPreferences preferences = context.getSharedPreferences("preference_Message", 0);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("jiaquan", is);
        editor.commit();
    }

    public static String getJiaQuan(Context context) {
        SharedPreferences preferences = context.getSharedPreferences("preference_Message", 0);
        String rechargemax = preferences.getString("jiaquan", "0.00");
        return rechargemax;
    }

    public static void saveIpPort(Context context, String is) {
        SharedPreferences preferences = context.getSharedPreferences("preference_ip_port", 0);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("ip_port", is);
        editor.commit();
    }

    public static String getIpPort(Context context) {
        SharedPreferences preferences = context.getSharedPreferences("preference_ip_port", 0);
        String device_password = preferences.getString("ip_port", "http://47.107.34.32:8090");
        return device_password;
    }

    public static void saveWifiIp(Context context, String is) {
        SharedPreferences preferences = context.getSharedPreferences("preference_wifi_ip", 0);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("wifi_ip", is);
        editor.commit();
    }

    public static String getWifiIp(Context context) {
        SharedPreferences preferences = context.getSharedPreferences("preference_wifi_ip", 0);
        String device_password = preferences.getString("wifi_ip", "192.168.4.1");
        return device_password;
    }
    public static void saveWifiPort(Context context, int is) {
        SharedPreferences preferences = context.getSharedPreferences("preference_wifi_port", 0);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putInt("wifi_port", is);
        editor.commit();
    }

    public static int getWifiPort(Context context) {
        SharedPreferences preferences = context.getSharedPreferences("preference_wifi_port", 0);
        int port = preferences.getInt("wifi_port", 60009);
        return port;
    }

    public static void saveComPassword(Context context, String is) {
        SharedPreferences preferences = context.getSharedPreferences("preference_password_com", 0);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("password_com", is);
        editor.commit();
    }

    public static String getComPassword(Context context) {
        SharedPreferences preferences = context.getSharedPreferences("preference_password_com", 0);
        String device_password = preferences.getString("password_com", "0102");
        return device_password;
    }

    public static void saveDoorDeviceId(Context context, String is) {
        SharedPreferences preferences = context.getSharedPreferences("preference_id", 0);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("device_id", is);
        editor.commit();
    }

    public static String getDoorDeviceId(Context context) {
        SharedPreferences preferences = context.getSharedPreferences("preference_id", 0);
        String deviceId = preferences.getString("device_id", "");
        return deviceId;
    }

    public static void saveJkDeviceId(Context context, String is) {
        SharedPreferences preferences = context.getSharedPreferences("preference_id", 0);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("device_jk_id", is);
        editor.commit();
    }

    public static String getJkDeviceId(Context context) {
        SharedPreferences preferences = context.getSharedPreferences("preference_id", 0);
        String deviceId = preferences.getString("device_jk_id", "");
        return deviceId;
    }

    public static void saveJkDevicePw(Context context, String is) {
        SharedPreferences preferences = context.getSharedPreferences("preference_id", 0);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("device_jk_pw", is);
        editor.commit();
    }

    public static String getJkDeviceIdLast(Context context) {
        SharedPreferences preferences = context.getSharedPreferences("preference_id", 0);
        String deviceId = preferences.getString("device_jk_last_id", "");
        return deviceId;
    }

    public static void saveJkDeviceIdLast(Context context, String is) {
        SharedPreferences preferences = context.getSharedPreferences("preference_id", 0);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("device_jk_last_id", is);
        editor.commit();
    }

    public static String getJkDevicePwLast(Context context) {
        SharedPreferences preferences = context.getSharedPreferences("preference_id", 0);
        String deviceId = preferences.getString("device_jk_last_pw", "");
        return deviceId;
    }

    public static void saveJkDevicePwLast(Context context, String is) {
        SharedPreferences preferences = context.getSharedPreferences("preference_id", 0);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("device_jk_last_pw", is);
        editor.commit();
    }

    public static String getJkDevicePw(Context context) {
        SharedPreferences preferences = context.getSharedPreferences("preference_id", 0);
        String deviceId = preferences.getString("device_jk_pw", "");
        return deviceId;
    }
    public static void saveWindowDeviceId(Context context, String is) {
        SharedPreferences preferences = context.getSharedPreferences("preference_id", 0);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("window_id", is);
        editor.commit();
    }
    public static String getWindowDeviceId(Context context) {
        SharedPreferences preferences = context.getSharedPreferences("preference_id", 0);
        String deviceId = preferences.getString("window_id", "");
        return deviceId;
    }
    public static void saveDoorPassword(Context context, String is) {
        SharedPreferences preferences = context.getSharedPreferences("preference_password", 0);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("password", is);
        editor.commit();
    }
    public static String getDoorPassword(Context context) {
        SharedPreferences preferences = context.getSharedPreferences("preference_password", 0);
        String device_password = preferences.getString("password", "");
        return device_password;
    }

    public static void saveWindowPassword(Context context, String is) {
        SharedPreferences preferences = context.getSharedPreferences("preference_password", 0);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("win_password", is);
        editor.commit();
    }

    public static String getWindowPassword(Context context) {
        SharedPreferences preferences = context.getSharedPreferences("preference_password", 0);
        String device_password = preferences.getString("win_password", "");
        return device_password;
    }

}