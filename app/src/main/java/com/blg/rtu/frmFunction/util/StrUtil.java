package com.blg.rtu.frmFunction.util;

import android.text.TextUtils;
import android.util.Log;

import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.text.DecimalFormat;
import java.util.Locale;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
public class StrUtil {
    private static final String SPLIT = ",";
    private static final String TAG = "StrUtil";

    public static String decode(String paramString)
    {
        try
        {
            paramString = URLDecoder.decode(paramString, "UTF-8");
            return paramString;
        }
        catch (UnsupportedEncodingException e)
        {
            Log.e("StrUtil", e.toString());
        }
        return null;
    }

    public static String encode(String paramString)
    {
        try
        {
            paramString = URLEncoder.encode(paramString, "UTF-8");
            return paramString;
        }
        catch (UnsupportedEncodingException e)
        {
            Log.e("StrUtil", e.toString());
        }
        return null;
    }

    public static String filterHtml(String paramString)
    {
        if (TextUtils.isEmpty(paramString)) {
            return "";
        }
        Matcher localMatcher = Pattern.compile("\\<img (.*?)>").matcher(paramString);
        while (localMatcher.find()) {
            paramString = paramString.replace(localMatcher.group(0), "[图片]");
        }
        localMatcher = Pattern.compile("\\<(.*?)>").matcher(paramString);
        while (localMatcher.find()) {
            paramString = paramString.replace(localMatcher.group(0), "");
        }
        return paramString.replaceAll("&nbsp;", "");
    }

    public static String format2Double(double paramDouble)
    {
        return new DecimalFormat("#0.00").format(new BigDecimal(paramDouble));
    }

    public static String format2Doubles(double paramDouble)
    {
        DecimalFormat localDecimalFormat = new DecimalFormat("#0.00");
        BigDecimal localBigDecimal = new BigDecimal(paramDouble);
        return String.format(Locale.getDefault(), "￥%s", new Object[] { localDecimalFormat.format(localBigDecimal) });
    }

    public static String format2Float(float paramFloat)
    {
        if (paramFloat > 8388608.0F)
        {
            StringBuilder localStringBuilder = new StringBuilder();
            localStringBuilder.append("float值太大，可能产生精度导致的问题，请用formatDouble：value");
            localStringBuilder.append(paramFloat);
            Log.e("StrUtil", localStringBuilder.toString());
        }
        return new DecimalFormat("#0.00").format(new BigDecimal(paramFloat));
    }

    public static String format2Float(String paramString)
    {
        String str = paramString;
        if (TextUtils.isEmpty(paramString)) {
            str = "0";
        }
        return new DecimalFormat("#0.00").format(new BigDecimal(str));
    }

    public static String format2FloatBy10Thousand(float paramFloat)
    {
        return String.format(Locale.getDefault(), "%.2f", new Object[] { Float.valueOf(paramFloat / 10000.0F) });
    }

    public static String format2FloatByThousand(float paramFloat)
    {
        if (paramFloat > 8388608.0F)
        {
            StringBuilder localStringBuilder = new StringBuilder();
            localStringBuilder.append("float精度太大，可能产生精度导致的问题，请用formatDouble：value");
            localStringBuilder.append(paramFloat);
            Log.e("StrUtil", localStringBuilder.toString());
        }
        return new DecimalFormat(",###,##0.00").format(new BigDecimal(nullToStr(Float.valueOf(paramFloat))));
    }

    public static String format2FloatByThousand(String paramString)
    {
        String str = paramString;
        if (TextUtils.isEmpty(paramString)) {
            str = "0";
        }
        return new DecimalFormat(",###,##0.00").format(new BigDecimal(str));
    }

    public static String format2Money(float paramFloat)
    {
        DecimalFormat localDecimalFormat = new DecimalFormat("#0.00");
        BigDecimal localBigDecimal = new BigDecimal(paramFloat);
        return String.format(Locale.getDefault(), "￥%s", new Object[] { localDecimalFormat.format(localBigDecimal) });
    }

    public static String format2Money(String paramString)
    {
        Object localObject = paramString;
        if (TextUtils.isEmpty(paramString)) {
            localObject = "0";
        }
        DecimalFormat decimalFormat = new DecimalFormat("#0.00");
        localObject = new BigDecimal((String)localObject);
        return String.format(Locale.getDefault(), "￥%s", new Object[] { decimalFormat.format(localObject) });
    }

    public static String format3Float(float paramFloat)
    {
        if (paramFloat > 8388608.0F)
        {
            StringBuilder localStringBuilder = new StringBuilder();
            localStringBuilder.append("float值太大，可能产生精度导致的问题，请用formatDouble：value");
            localStringBuilder.append(paramFloat);
            Log.e("StrUtil", localStringBuilder.toString());
        }
        return new DecimalFormat("#0.000").format(new BigDecimal(paramFloat));
    }

    public static String format3Float(String paramString)
    {
        String str = paramString;
        if (TextUtils.isEmpty(paramString)) {
            str = "0";
        }
        return new DecimalFormat("#0.000").format(new BigDecimal(str));
    }

    public static String format3FloatByThousand(float paramFloat)
    {
        if (paramFloat > 8388608.0F)
        {
            StringBuilder localStringBuilder = new StringBuilder();
            localStringBuilder.append("float值太大，可能产生精度导致的问题，请用formatDouble：value");
            localStringBuilder.append(paramFloat);
            Log.e("StrUtil", localStringBuilder.toString());
        }
        return new DecimalFormat(",###,##0.000").format(new BigDecimal(nullToStr(Float.valueOf(paramFloat))));
    }

    public static String format3FloatByThousand(String paramString)
    {
        String str = paramString;
        if (TextUtils.isEmpty(paramString)) {
            str = "0";
        }
        return new DecimalFormat(",###,##0.000").format(new BigDecimal(str));
    }

    public static String formatDistance(String paramString)
    {
        double d = nullToDouble(paramString).doubleValue();
        if (d > 1000000.0D) {
            return ">1000km";
        }
        if (d > 10000.0D) {
            return String.format(Locale.getDefault(), "%dkm", new Object[] { Integer.valueOf((int)d / 1000) });
        }
        if (d >= 1000.0D) {
            return String.format(Locale.getDefault(), "%skm", new Object[] { formatFloat((float)(d / 1000.0D)) });
        }
        return String.format(Locale.getDefault(), "%dm", new Object[] { Integer.valueOf((int)d / 1000) });
    }

    public static String formatFloat(float paramFloat)
    {
        if (paramFloat > 8388608.0F)
        {
            StringBuilder localStringBuilder = new StringBuilder();
            localStringBuilder.append("float值太大，可能产生精度导致的问题，请用formatDouble：value");
            localStringBuilder.append(paramFloat);
            Log.e("StrUtil", localStringBuilder.toString());
        }
        return new DecimalFormat("#0.0").format(new BigDecimal(paramFloat));
    }

    public static String formatMoney(float paramFloat)
    {
        if (paramFloat >= 10000.0F) {
            return String.format("%s万", new Object[] { format2FloatBy10Thousand(paramFloat) });
        }
        return format2Float(paramFloat);
    }

    public static String formatToInt(String paramString)
    {
        String str = paramString;
        if (TextUtils.isEmpty(paramString)) {
            str = "0";
        }
        return new DecimalFormat("#0").format(new BigDecimal(str));
    }

    public static String generateNonceStr()
    {
        return UUID.randomUUID().toString().replaceAll("-", "").substring(0, 32);
    }

    public static String getStarStr(int paramInt)
    {
        StringBuilder localStringBuilder = new StringBuilder();
        int i = 0;
        while (i < paramInt)
        {
            localStringBuilder.append("*");
            i += 1;
        }
        return localStringBuilder.toString();
    }

    public static String handleCity(String paramString)
    {
        if ((paramString != null) && (paramString.endsWith("市"))) {
            return paramString.replace("市", "");
        }
        return paramString;
    }

    public static boolean isEmail(String paramString)
    {
        try
        {
            boolean bool = Pattern.compile("^([a-z0-9A-Z]+[-|_|\\.]?)+[a-z0-9A-Z]@([a-z0-9A-Z]+(-[a-z0-9A-Z]+)?\\.)+[a-zA-Z]{2,}$").matcher(paramString).matches();
            return bool;
        }
        catch (Exception e)
        {
        }
        return false;
    }

    public static boolean isHttpUrl(String paramString)
    {
        return (!TextUtils.isEmpty(paramString)) && (paramString.startsWith("http"));
    }

    public static boolean isImagePath(String paramString)
    {
        return (paramString.endsWith(".png")) || (paramString.endsWith(".PNG")) || (paramString.endsWith(".jpg")) || (paramString.endsWith(".JPG")) || (paramString.endsWith(".jpeg")) || (paramString.endsWith(".JPEG"));
    }

    public static boolean isMobileNo(String paramString)
    {
        try
        {
            boolean bool = Pattern.compile("^1[3456789]\\d{9}$").matcher(paramString).matches();
            return bool;
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return false;
    }

    public static boolean isNumber(String paramString)
    {
        if (paramString != null)
        {
            if ("".equals(paramString.trim())) {
                return false;
            }
            Boolean localBoolean = Boolean.valueOf(false);
            if (paramString.matches("^-?[0-9]+$")) {
                localBoolean = Boolean.valueOf(true);
            }
            return localBoolean.booleanValue();
        }
        return false;
    }

    public static boolean nullToBoolean(Object paramObject)
    {
        paramObject = nullToStr(paramObject);
        if ("".equals(paramObject)) {
            return false;
        }
        return Boolean.valueOf((String)paramObject).booleanValue();
    }

    public static Double nullToDouble(Object paramObject)
    {
        Double localDouble = Double.valueOf(0.0D);
        paramObject = nullToStr(paramObject);
        if ("".equals(paramObject)) {
            return localDouble;
        }
        try
        {
            paramObject = Double.valueOf((String)paramObject);
            return (Double)paramObject;
        }
        catch (NumberFormatException e)
        {
            Log.e("StrUtil", ((NumberFormatException)e).toString());
        }
        return localDouble;
    }

    public static Float nullToFloat(Object paramObject)
    {
        paramObject = nullToStr(paramObject);
        if ("".equals(paramObject)) {
            return Float.valueOf(0.0F);
        }
        return Float.valueOf(strToFloat((String)paramObject));
    }

    public static int nullToInt(Object paramObject)
    {
        return nullToInt(paramObject, 0);
    }

    public static int nullToInt(Object paramObject, int paramInt)
    {
        paramObject = nullToStr(paramObject);
        if ("".equals(paramObject)) {
            return paramInt;
        }
        try
        {
            paramInt = Integer.parseInt((String)paramObject);
            return paramInt;
        }
        catch (NumberFormatException e)
        {
            Log.e("StrUtil", ((NumberFormatException)e).toString());
        }
        return 0;
    }

    public static Long nullToLong(Object paramObject)
    {
        Long localLong = Long.valueOf(0L);
        paramObject = nullToStr(paramObject);
        if ("".equals(paramObject)) {
            return localLong;
        }
        try
        {
            paramObject = Long.valueOf((String)paramObject);
            return (Long)paramObject;
        }
        catch (NumberFormatException e)
        {
            Log.e("StrUtil", ((NumberFormatException)e).toString());
        }
        return localLong;
    }

    public static Long nullToLongToMS(Object paramObject)
    {
        Long localLong = Long.valueOf(0L);
        paramObject = nullToStr(paramObject);
        if ("".equals(paramObject)) {
            return localLong;
        }
        try
        {
            paramObject = Long.valueOf((String)paramObject);
            return (Long)paramObject;
        }
        catch (NumberFormatException e)
        {
            Log.e("StrUtil", ((NumberFormatException)e).toString());
        }
        return localLong;
    }

    public static String nullToStr(Object paramObject)
    {
        if ((paramObject != null) && (!paramObject.toString().equals("null"))) {
            return paramObject.toString().trim();
        }
        return "";
    }

    public static String shieldBankCardNo(String paramString)
    {
        Object localObject = paramString;
        if (!TextUtils.isEmpty(paramString))
        {
            localObject = paramString;
            if (paramString.length() > 8)
            {
                localObject = new StringBuilder();
                ((StringBuilder)localObject).append(paramString.substring(0, 4));
                ((StringBuilder)localObject).append(getStarStr(paramString.length() - 8));
                ((StringBuilder)localObject).append(paramString.substring(paramString.length() - 4, paramString.length()));
                localObject = ((StringBuilder)localObject).toString();
            }
        }
        return (String)localObject;
    }

    public static String shieldMobile(String paramString)
    {
        Object localObject = paramString;
        if (!TextUtils.isEmpty(paramString))
        {
            localObject = paramString;
            if (paramString.length() == 11)
            {
                localObject = new StringBuilder();
                ((StringBuilder)localObject).append(paramString.substring(0, 3));
                ((StringBuilder)localObject).append("****");
                ((StringBuilder)localObject).append(paramString.substring(7, 11));
                localObject = ((StringBuilder)localObject).toString();
            }
        }
        return (String)localObject;
    }

    public static double strToDouble(String paramString)
    {
        if (paramString == null) {
            return 0.0D;
        }
        try
        {
            double d = Double.parseDouble(paramString);
            boolean bool = Double.isNaN(d);
            if (bool) {
                return 0.0D;
            }
            return d;
        }
        catch (NumberFormatException e)
        {
            Log.e("StrUtil", e.toString());
        }
        return 0.0D;
    }

    public static float strToFloat(String paramString)
    {
        if (paramString == null) {
            return 0.0F;
        }
        try
        {
            float f = Float.parseFloat(paramString);
            boolean bool = Float.isNaN(f);
            if (bool) {
                return 0.0F;
            }
            return f;
        }
        catch (NumberFormatException e)
        {
            Log.e("StrUtil", e.toString());
        }
        return 0.0F;
    }

    public static int strToInt(String paramString)
    {
        if (paramString == null) {
            return 0;
        }
        try
        {
            int i = Integer.parseInt(paramString);
            return i;
        }
        catch (NumberFormatException e)
        {
            Log.e("StrUtil", e.toString());
        }
        return 0;
    }
}
