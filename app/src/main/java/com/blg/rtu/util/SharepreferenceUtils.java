package com.blg.rtu.util;

/**
 *
 */

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class SharepreferenceUtils {
    public static final String PREF_USERID_CHANELID = "Preference_Userid_Channelid";
    public static final String PREF_USER_INFO = "Preference_User_Info";
    public static final String XIANG_INFO = "xiang_info";
    public static final String CUN_INFO = "cun_info";
    public static final String XIANG_CODE_INFO_ONETIME = "xiang_code_info_OneTime";
    public static final String XIANG_INFO_ONETIME = "xiang_info_OneTime";
    public static final String CUN_INFO_ONETIME = "cun_info_OneTime";
    public static final String AREA_INFO = "area_info";
    private static final String XIANG_CUN_NUM="xiang_cun_num";

    /**
     * 保存用户设定的xcm
     *
     * @param context
     * @param xcms
     */
    public static void saveSmXcm(Context context, String xcms) {
        SharedPreferences preferences = context.getSharedPreferences("sm_xcm", 0);
        Editor editor = preferences.edit();
        editor.putString("save_sm_xcm", xcms);
        editor.commit();
    }

    /**
     * 获取用户设定xcm，本项目默认为"10cm-20cm-40cm"
     *
     * @param context
     * @return
     */
    public static String getSmXcm(Context context) {
        SharedPreferences preferences = context.getSharedPreferences("sm_xcm", 0);
        String xcms = preferences.getString("save_sm_xcm", "10cm-20cm-40cm");
        return xcms;
    }

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

    public static void saveProtocolType(Context context, int type) {
        SharedPreferences preferences = context.getSharedPreferences("preference_protocol_type", 0);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putInt("protocol_type", type);
        editor.commit();
    }

    public static int getProtocolTpye(Context context) {
        SharedPreferences preferences = context.getSharedPreferences("preference_protocol_type", 0);
        int type = preferences.getInt("protocol_type", -1);
        return type;
    }

    public static void saveHasLearn(Context context, boolean is) {
        SharedPreferences preferences = context.getSharedPreferences("preference_HasLearn", 0);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean("HasLearn", is);
        editor.commit();
    }

    public static boolean getIsWifi(Context context) {
        SharedPreferences preferences = context.getSharedPreferences("preference_IsWifi", 0);
        boolean type = preferences.getBoolean("IsWifi", false);
        return type;
    }

    public static void saveIsWifi(Context context, boolean is) {
        SharedPreferences preferences = context.getSharedPreferences("preference_IsWifi", 0);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean("IsWifi", is);
        editor.commit();
    }

    public static boolean getHasLearn(Context context) {
        SharedPreferences preferences = context.getSharedPreferences("preference_HasLearn", 0);
        boolean type = preferences.getBoolean("HasLearn", false);
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

    public static void saveDeviceId(Context context, String is) {
        SharedPreferences preferences = context.getSharedPreferences("preference_id", 0);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("device_id", is);
        editor.commit();
    }
    public static void savePassword(Context context, String is) {
        SharedPreferences preferences = context.getSharedPreferences("preference_password", 0);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("password", is);
        editor.commit();
    }

    public static String getPassword(Context context) {
        SharedPreferences preferences = context.getSharedPreferences("preference_password", 0);
        String device_password = preferences.getString("password", "");
        return device_password;
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

    public static String getDeviceId(Context context) {
        SharedPreferences preferences = context.getSharedPreferences("preference_id", 0);
        String deviceId = preferences.getString("device_id", "");
        return deviceId;
    }



    public static void savePhoneIMEI(Context context, String imei) {
        SharedPreferences preferences = context.getSharedPreferences("preference_phone_imei", 0);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("phone_imei", imei);
        editor.commit();
    }

    public static String getPhoneIMEI(Context context) {
        SharedPreferences preferences = context.getSharedPreferences("preference_phone_imei", 0);
        String immi = preferences.getString("phone_imei", "");
        return immi;
    }

    public static void saveUserId(Context context, String userId) {
        SharedPreferences preferences = context.getSharedPreferences("preference_user_id",0) ;
        SharedPreferences.Editor editor = preferences.edit() ;
        editor.putString("user_id", userId) ;
        editor.commit() ;
    }

    public static String getUserId(Context context) {
        SharedPreferences preferences = context.getSharedPreferences("preference_user_id", 0);
        String userId = preferences.getString("user_id", "");
        return userId;
    }

    public static void saveSurplusWaterWarnDown(Context context, String is) {
        SharedPreferences preferences = context.getSharedPreferences("preference_surplus_warndown", 0);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("surplus_water_warndown", is);
        editor.commit();
    }

    public static String getSurplusWaterWarnDown(Context context) {
        SharedPreferences preferences = context.getSharedPreferences("preference_surplus_warndown", 0);
        String surplus_water_warndown = preferences.getString("surplus_water_warndown", "");
        return surplus_water_warndown;
    }

    public static void savePumpOpResult(Context context, boolean isOpen) {
        SharedPreferences preferences = context.getSharedPreferences("preference_pump_status", 0);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean("pumpstatus", isOpen);
        editor.commit();
    }

    public static boolean getPumpOpResult(Context context) {
        SharedPreferences preferences = context.getSharedPreferences("preference_pump_status", 0);
        boolean isOpen = preferences.getBoolean("pumpstatus", false);
        return isOpen;
    }
//    public static void saveUserLoginInfo(Context context, LoginInfo loginInfo) {
//        if (loginInfo != null) {
//            SharedPreferences preferences = context.getSharedPreferences("preference_user_login", 0);
//            SharedPreferences.Editor editor = preferences.edit();
//            editor.putString("xiangCode", loginInfo.getXiangCode());
//            //editor.putString("isActive", loginInfo.getIsActive());
//            editor.putString("xiangName", loginInfo.getXiangCode());
//            editor.putString("isAdmin", loginInfo.getXiangCode());
//            editor.putString("cunCode", loginInfo.getXiangCode());
//            editor.putString("uLevl", loginInfo.getXiangCode());
//            editor.putString("uNo", loginInfo.getXiangCode());
//            editor.putString("cunName", loginInfo.getXiangCode());
//        }
//    }



    /**
     * 设置行政区选择数据
     *
     * @param context
     * @param areaInfo
     */
    public static void setAreaInfo(Context context, String areaInfo) {
        SharedPreferences preference = context.getSharedPreferences(AREA_INFO, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preference.edit();
        editor.putString("areaInfo", areaInfo);
        editor.commit();
    }

    /**
     * 获得行政区选择数据
     *
     * @param context
     * @return
     */
    public static String getAreaInfo(Context context) {
        SharedPreferences preference = context.getSharedPreferences(AREA_INFO, Context.MODE_PRIVATE);
        String areaInfo = preference.getString("areaInfo", "");
        return areaInfo;
    }

    public static void setXiangInfo(Context context, String info) {
        SharedPreferences preference = context.getSharedPreferences(XIANG_INFO, Context.MODE_PRIVATE);
        Editor editor = preference.edit();
        editor.putString("xiang", info);
        editor.commit();

    }

    public static String getXiangInfo(Context context) {
        SharedPreferences preference = context.getSharedPreferences(XIANG_INFO, Context.MODE_PRIVATE);
        return preference.getString("xiang", "");
    }

    public static void setCunInfo(Context context, String xiangSn, String cunInfo) {
        SharedPreferences preference = context.getSharedPreferences(CUN_INFO, Context.MODE_PRIVATE);
        Editor editor = preference.edit();
        if (xiangSn.equals("0"))
            editor.putString("cunXOne", cunInfo);
        if (xiangSn.equals("1"))
            editor.putString("cunXTwo", cunInfo);
        if (xiangSn.equals("2"))
            editor.putString("cunXThree", cunInfo);
        if (xiangSn.equals("3"))
            editor.putString("cunXFour", cunInfo);
        if (xiangSn.equals("4"))
            editor.putString("cunXFive", cunInfo);
        if (xiangSn.equals("5"))
            editor.putString("cunXSix", cunInfo);
        if (xiangSn.equals("6"))
            editor.putString("cunXSeven", cunInfo);
        editor.commit();
    }

    public static String getCunInfo(Context context, String XiangSn) {
        SharedPreferences preference = context.getSharedPreferences(CUN_INFO, Context.MODE_PRIVATE);
        if (XiangSn.equals("0")) {
            return preference.getString("cunXOne", "");
        } else if (XiangSn.equals("1")) {
            return preference.getString("cunXTwo", "");
        } else if (XiangSn.equals("2")) {
            return preference.getString("cunXThree", "");
        } else if (XiangSn.equals("3")) {
            return preference.getString("cunXFour", "");
        } else if (XiangSn.equals("4")) {
            return preference.getString("cunXFive", "");
        } else if (XiangSn.equals("5")) {
            return preference.getString("cunXSix", "");
        } else if (XiangSn.equals("6")) {
            return preference.getString("cunXSeven", "");
        }
        return null;
    }

    /**
     * 保存乡镇编号
     * @param context
     * @param info
     */
    public static void setXiangCunNum(Context context, String info) {
        SharedPreferences preference = context.getSharedPreferences(XIANG_CUN_NUM, Context.MODE_PRIVATE);
        Editor editor = preference.edit();
        editor.putString(XIANG_CUN_NUM, info);
        editor.commit();

    }


    /**
     * 获得乡镇编号
     * @param context
     * @return
     */
    public static String getXiangCunNum(Context context) {
        SharedPreferences preference = context.getSharedPreferences(XIANG_CUN_NUM, Context.MODE_PRIVATE);
        return preference.getString(XIANG_CUN_NUM, "");
    }

    /****************************************************************************************************/
    public static void setXiangCodeInfoOneTime(Context context, String info) {
        SharedPreferences preference = context.getSharedPreferences(XIANG_CODE_INFO_ONETIME, Context.MODE_PRIVATE);
        Editor editor = preference.edit();
        editor.putString("xiang_code", info);
        editor.commit();

    }

    public static String getXiangCodeInfoOneTime(Context context) {
        SharedPreferences preference = context.getSharedPreferences(XIANG_CODE_INFO_ONETIME, Context.MODE_PRIVATE);
        return preference.getString("xiang_code", "");
    }

    public static void setXiangInfoOneTime(Context context, String info) {
        SharedPreferences preference = context.getSharedPreferences(XIANG_INFO_ONETIME, Context.MODE_PRIVATE);
        Editor editor = preference.edit();
        editor.putString("xiang", info);
        editor.commit();

    }

    public static String getXiangInfoOneTime(Context context) {
        SharedPreferences preference = context.getSharedPreferences(XIANG_INFO_ONETIME, Context.MODE_PRIVATE);
        return preference.getString("xiang", "");
    }

    /**
     * 搞了7个乡
     * @param context
     * @param xiangSn
     * @param cunInfo
     */
    public static void setCunInfoOneTime(Context context, String xiangSn, String cunInfo) {
        SharedPreferences preference = context.getSharedPreferences(CUN_INFO_ONETIME, Context.MODE_PRIVATE);
        Editor editor = preference.edit();
        if (xiangSn.equals("0"))
            editor.putString("cunXOne", cunInfo);
        if (xiangSn.equals("1"))
            editor.putString("cunXTwo", cunInfo);
        if (xiangSn.equals("2"))
            editor.putString("cunXThree", cunInfo);
        if (xiangSn.equals("3"))
            editor.putString("cunXFour", cunInfo);
        if (xiangSn.equals("4"))
            editor.putString("cunXFive", cunInfo);
        if (xiangSn.equals("5"))
            editor.putString("cunXSix", cunInfo);
        if (xiangSn.equals("6"))
            editor.putString("cunXSeven", cunInfo);
        editor.commit();
    }

    public static String getCunInfoOneTime(Context context, String XiangSn) {
        SharedPreferences preference = context.getSharedPreferences(CUN_INFO_ONETIME, Context.MODE_PRIVATE);
        if (XiangSn.equals("0")) {
            return preference.getString("cunXOne", "");
        } else if (XiangSn.equals("1")) {
            return preference.getString("cunXTwo", "");
        } else if (XiangSn.equals("2")) {
            return preference.getString("cunXThree", "");
        } else if (XiangSn.equals("3")) {
            return preference.getString("cunXFour", "");
        } else if (XiangSn.equals("4")) {
            return preference.getString("cunXFive", "");
        } else if (XiangSn.equals("5")) {
            return preference.getString("cunXSix", "");
        } else if (XiangSn.equals("6")) {
            return preference.getString("cunXSeven", "");
        }
        return null;
    }

    /*****************************************************************************************************/
    public static class Screen {
        public int widthPixels;
        public int heightPixels;

        public Screen(int widthPixels, int heightPixels) {
            this.widthPixels = widthPixels;
            this.heightPixels = heightPixels;
        }

        public String toString() {
            return "(" + this.widthPixels + "," + this.heightPixels + ")";
        }
    }

}