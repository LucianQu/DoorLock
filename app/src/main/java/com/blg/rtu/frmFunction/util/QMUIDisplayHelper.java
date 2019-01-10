package com.blg.rtu.frmFunction.util;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.net.ConnectivityManager;
import android.os.Build.VERSION;
import android.os.Environment;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Display;
import android.view.ViewConfiguration;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;

import java.util.Locale;

public class QMUIDisplayHelper {
    public static final float DENSITY = Resources.getSystem().getDisplayMetrics().density;
    private static final String TAG = "Devices";
    public static float sDensity;
    private static Boolean sHasCamera;

    public static void cancelFullScreen(Context paramContext)
    {
        if ((paramContext instanceof Activity))
        {
            Activity paramContext1 = (Activity)paramContext;
            LayoutParams localLayoutParams = paramContext1.getWindow().getAttributes();
            localLayoutParams.flags &= 0xFBFF;
            paramContext1.getWindow().setAttributes(localLayoutParams);
            paramContext1.getWindow().addFlags(512);
        }
    }

    public static int dp2px(Context paramContext, int paramInt)
    {
        return (int)(getDensity(paramContext) * paramInt + 0.5D);
    }

    public static int dpToPx(int paramInt)
    {
        return (int)(paramInt * DENSITY + 0.5F);
    }

    public static int getActionBarHeight(Context paramContext)
    {
        TypedValue localTypedValue = new TypedValue();
        if (paramContext.getTheme().resolveAttribute(16843499, localTypedValue, true)) {
            return TypedValue.complexToDimensionPixelSize(localTypedValue.data, paramContext.getResources().getDisplayMetrics());
        }
        return 0;
    }

    public static String getCurCountryLan(Context paramContext)
    {
        Configuration paramContext1 = paramContext.getResources().getConfiguration();
        Locale locale ;
        if (VERSION.SDK_INT >= 24) {
            locale = paramContext1.getLocales().get(0);
        } else {
            locale = paramContext1.locale;
        }
        StringBuilder localStringBuilder = new StringBuilder();
        localStringBuilder.append(locale.getLanguage());
        localStringBuilder.append("-");
        localStringBuilder.append(locale.getCountry());
        return localStringBuilder.toString();
    }

    public static float getDensity(Context paramContext)
    {
        if (sDensity == 0.0F) {
            sDensity = getDisplayMetrics(paramContext).density;
        }
        return sDensity;
    }

    public static DisplayMetrics getDisplayMetrics(Context paramContext)
    {
        DisplayMetrics localDisplayMetrics = new DisplayMetrics();
        ((WindowManager)paramContext.getApplicationContext().getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay().getMetrics(localDisplayMetrics);
        return localDisplayMetrics;
    }

    public static int getNavMenuHeight(Context paramContext)
    {
        return getRealScreenSize(paramContext)[1] - getScreenHeight(paramContext);
    }

    public static int[] getRealScreenSize(Context paramContext)
    {
        Display paramContex1 = ((WindowManager)paramContext.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
        DisplayMetrics metrics = new DisplayMetrics();
        paramContex1.getRealMetrics(metrics);
        int k = (metrics).widthPixels;
        int i = (metrics).heightPixels;
        return new int[] { k, i };
    }

    public static int getScreenHeight(Context paramContext)
    {
        return getDisplayMetrics(paramContext).heightPixels;
    }

    public static int getScreenWidth(Context paramContext)
    {
        return getDisplayMetrics(paramContext).widthPixels;
    }

    public static int getStatusBarHeight(Context paramContext)
    {
        try
        {
            Class localClass = Class.forName("com.android.internal.R$dimen");
            Object localObject = localClass.newInstance();
            int i = Integer.parseInt(localClass.getField("status_bar_height").get(localObject).toString());
            i = paramContext.getResources().getDimensionPixelSize(i);
            return i;
        }
        catch (Exception paramContext1)
        {
            paramContext1.printStackTrace();
        }
        return 0;
    }

    public static boolean hasCamera(Context paramContext)
    {
        if (sHasCamera == null)
        {
            PackageManager paramContext1 = paramContext.getPackageManager();
            boolean bool1 = paramContext1.hasSystemFeature("android.hardware.camera.front");
            boolean bool2 = paramContext1.hasSystemFeature("android.hardware.camera");
            if ((!bool1) && (!bool2)) {
                bool1 = false;
            } else {
                bool1 = true;
            }
            sHasCamera = Boolean.valueOf(bool1);
        }
        return sHasCamera.booleanValue();
    }

    public static boolean hasHardwareMenuKey(Context paramContext)
    {
        if (VERSION.SDK_INT < 11) {
            return true;
        }
        if (VERSION.SDK_INT >= 14) {
            return ViewConfiguration.get(paramContext).hasPermanentMenuKey();
        }
        return false;
    }

    public static boolean hasInternet(Context paramContext)
    {
        return ((ConnectivityManager)paramContext.getSystemService(Context.CONNECTIVITY_SERVICE)).getActiveNetworkInfo() != null;
    }

    public static boolean hasStatusBar(Context paramContext)
    {
        if ((paramContext instanceof Activity)) {
            return (((Activity)paramContext).getWindow().getAttributes().flags & 0x400) != 1024;
        }
        return true;
    }

    public static boolean isElevationSupported()
    {
        return VERSION.SDK_INT >= 21;
    }

    public static boolean isFullScreen(Activity paramActivity)
    {
        return (paramActivity.getWindow().getAttributes().flags & 0x400) == 1024;
    }

    public static boolean isPackageExist(Context paramContext, String paramString)
    {
        try
        {
            PackageInfo paramContext1 = paramContext.getPackageManager().getPackageInfo(paramString, 0);
            return paramContext1 != null;
        }
        catch (NameNotFoundException paramContext1) {}
        return false;
    }

    public static boolean isSdcardReady()
    {
        return "mounted".equals(Environment.getExternalStorageState());
    }

    public static boolean isZhCN(Context paramContext)
    {
        Configuration paramContext1 = paramContext.getResources().getConfiguration();
        Locale paramContext2 ;
        if (VERSION.SDK_INT >= 24) {
            paramContext2 = paramContext1.getLocales().get(0);
        } else {
            paramContext2 = paramContext1.locale;
        }
        return paramContext2.getCountry().equalsIgnoreCase("CN");
    }

    public static int px2dp(Context paramContext, int paramInt)
    {
        return (int)(paramInt / getDensity(paramContext) + 0.5D);
    }

    public static int pxToDp(float paramFloat)
    {
        return (int)(paramFloat / DENSITY + 0.5F);
    }

    public static void setFullScreen(Context paramContext)
    {
        if ((paramContext instanceof Activity))
        {
            Activity paramContext1 = (Activity)paramContext;
            LayoutParams localLayoutParams = paramContext1.getWindow().getAttributes();
            localLayoutParams.flags |= 0x400;

            paramContext1.getWindow().setAttributes(localLayoutParams);
            paramContext1.getWindow().addFlags(512);
        }
    }
}
