package com.blg.rtu.frmFunction.util;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

public class QMUIStatusBarHelper {
    private static final int STATUSBAR_TYPE_ANDROID6 = 3;
    private static final int STATUSBAR_TYPE_DEFAULT = 0;
    private static final int STATUSBAR_TYPE_FLYME = 2;
    private static final int STATUSBAR_TYPE_MIUI = 1;
    private static final int STATUS_BAR_DEFAULT_HEIGHT_DP = 25;
    private static int mStatuBarType = 0;
    private static int sStatusbarHeight = -1;
    private static Integer sTransparentValue;
    public static float sVirtualDensity = -1.0F;
    public static float sVirtualDensityDpi = -1.0F;

    @TargetApi(23)
    private static boolean Android6SetStatusBarLightMode(Window paramWindow, boolean paramBoolean)
    {
        View localView = paramWindow.getDecorView();
        int i;
        if (paramBoolean) {
            i = 8192;
        } else {
            i = 256;
        }
        localView.setSystemUiVisibility(changeStatusBarModeRetainFlag(paramWindow, i));
        return true;
    }

    public static boolean FlymeSetStatusBarLightMode(Window paramWindow, boolean paramBoolean)
    {
        Android6SetStatusBarLightMode(paramWindow, paramBoolean);
        Field localField1  ;
        Field localField2  ;
        WindowManager.LayoutParams localLayoutParams1 ;
        if (paramWindow != null) {}
        try
        {
             localLayoutParams1 = paramWindow.getAttributes();
            localField1 = WindowManager.LayoutParams.class.getDeclaredField("MEIZU_FLAG_DARK_STATUS_BAR_ICON");
            localField2 = WindowManager.LayoutParams.class.getDeclaredField("meizuFlags");
            localField1.setAccessible(true);
            localField2.setAccessible(true);
            int i = localField1.getInt(null);
            int j = localField2.getInt(localLayoutParams1);
            if (!paramBoolean) {
                localField2.setInt(localLayoutParams1, i);
                paramWindow.setAttributes(localLayoutParams1);
                return true;
            }
            i = j | i;
        }
        catch (Exception paramWindow1)
        {
            for (;;)
            {
                WindowManager.LayoutParams localLayoutParams;
                int j;
                continue;
            }
        }
        return false;
    }

    public static boolean MIUISetStatusBarLightMode(Window paramWindow, boolean paramBoolean)
    {
        Object localObject = null;
        if (paramWindow != null) {
            localObject = paramWindow.getClass();
        }
        try
        {
            Class localClass = Class.forName("android.view.MiuiWindowManager$LayoutParams");
            int i = localClass.getField("EXTRA_FLAG_STATUS_BAR_DARK_MODE").getInt(localClass);
            Method localObject1 = ((Class)localObject).getMethod("setExtraFlags", new Class[] { Integer.TYPE, Integer.TYPE });
            if (paramBoolean)
            {
                ((Method)localObject).invoke(paramWindow, new Object[] { Integer.valueOf(i), Integer.valueOf(i) });
                return true;
            }
            ((Method)localObject).invoke(paramWindow, new Object[] { Integer.valueOf(0), Integer.valueOf(i) });
            return true;
        }
        catch (Exception paramWindow1)
        {
            return false;
        }

    }

    @TargetApi(23)
    private static int changeStatusBarModeRetainFlag(Window paramWindow, int paramInt)
    {
        return retainSystemUiFlag(paramWindow, retainSystemUiFlag(paramWindow, retainSystemUiFlag(paramWindow, retainSystemUiFlag(paramWindow, retainSystemUiFlag(paramWindow, retainSystemUiFlag(paramWindow, paramInt, 1024), 4), 2), 4096), 1024), 512);
    }

    public static Integer getStatusBarAPITransparentValue(Context paramContext)
    {
        if (sTransparentValue != null) {
            return sTransparentValue;
        }
        String[] arrayOfString = paramContext.getPackageManager().getSystemSharedLibraryNames();
        int j = arrayOfString.length;
        int i = 0;
        String paramContext1 = null;
        while (i < j)
        {
            String str = arrayOfString[i];
            if ("touchwiz".equals(str)) {
                paramContext1 = "SYSTEM_UI_FLAG_TRANSPARENT_BACKGROUND";
            } else if (str.startsWith("com.sonyericsson.navigationbar")) {
                paramContext1 = "SYSTEM_UI_FLAG_TRANSPARENT";
            }
            i += 1;
        }
        if (paramContext != null) {}
        try
        {
            Field paramContext2 = View.class.getField(paramContext1);
            if ((paramContext2 != null) && (paramContext2.getType() == Integer.TYPE)) {
                sTransparentValue = Integer.valueOf(paramContext2.getInt(null));
            }
        }
        catch (Exception paramContext3)
        {
            for (;;) {}
        }
        return sTransparentValue;
    }

    public static int getStatusbarHeight(Context paramContext)
    {
        if (sStatusbarHeight == -1) {
            initStatusBarHeight(paramContext);
        }
        return sStatusbarHeight;
    }

    private static void initStatusBarHeight(Context paramContext)
    {
        Object localObject4 = null;
        Object localObject1 = null;
        Object localObject2 = null;
        try
        {
            Class localClass = Class.forName("com.android.internal.R$dimen");
            Object localObject3 = localClass.newInstance();
            localObject1 = localObject4;
            try
            {
                boolean bool = QMUIDeviceHelper.isMeizu();
                localObject1 = localObject2;
                if (bool) {
                    try
                    {
                        localObject1 = localClass.getField("status_bar_height_large");
                    }
                    catch (Throwable localThrowable4)
                    {
                        localObject1 = localObject4;
                        localThrowable4.printStackTrace();
                        localObject1 = localObject2;
                    }
                }
                localObject2 = localObject1;
                localObject4 = localObject3;
                localObject2 = localClass.getField("status_bar_height");
                localObject4 = localObject3;
            }
            catch (Throwable localThrowable2)
            {
                localObject2 = localObject3;
            }

        }catch (Throwable localThrowable3){
            localObject2 = null;
            localThrowable3.printStackTrace();
        }

        Object localObject5 = localObject2;
        localObject2 = localObject1;
        if (localObject1 != null) {
            if ((localObject2 != null) && (localObject5 != null)) {
                try
                {
                    int i = Integer.parseInt(((Field)localObject2).get(localObject5).toString());
                    sStatusbarHeight = paramContext.getResources().getDimensionPixelSize(i);
                }
                catch (Throwable localThrowable1)
                {
                    localThrowable1.printStackTrace();
                }
            }
        }

        if ((QMUIDeviceHelper.isTablet(paramContext)) && (sStatusbarHeight > QMUIDisplayHelper.dp2px(paramContext, 25)))
        {
            sStatusbarHeight = 0;
            return;
        }
        if ((sStatusbarHeight <= 0) || (sStatusbarHeight > QMUIDisplayHelper.dp2px(paramContext, 50)))
        {
            if (sVirtualDensity == -1.0F)
            {
                sStatusbarHeight = QMUIDisplayHelper.dp2px(paramContext, 25);
                return;
            }
            sStatusbarHeight = (int)(sVirtualDensity * 25.0F + 0.5F);
        }
    }

    public static boolean isFullScreen(Activity paramActivity)
    {
        try
        {
            int i = paramActivity.getWindow().getAttributes().flags;
            if ((i & 0x400) != 0) {
                return true;
            }
        }
        catch (Exception paramActivity1)
        {
            paramActivity1.printStackTrace();
        }
        return false;
    }

    private static boolean isMIUICustomStatusBarLightModeImpl()
    {
        return (QMUIDeviceHelper.isMIUIV5()) || (QMUIDeviceHelper.isMIUIV6()) || (QMUIDeviceHelper.isMIUIV7()) || (QMUIDeviceHelper.isMIUIV8());
    }

    public static boolean isSupportTranslucent()
    {
        if (Build.VERSION.SDK_INT < 19) {
            return false;
        }
        if (!QMUIDeviceHelper.isMeizu())
        {
            if (QMUIDeviceHelper.isMIUI()) {
                return true;
            }
            return Build.VERSION.SDK_INT >= 21;
        }
        return true;
    }

    public static int retainSystemUiFlag(Window paramWindow, int paramInt1, int paramInt2)
    {
        int i = paramInt1;
        if ((paramWindow.getDecorView().getSystemUiVisibility() & paramInt2) == paramInt2) {
            i = paramInt1 | paramInt2;
        }
        return i;
    }

    public static boolean setStatusBarDarkMode(Activity paramActivity)
    {
        if (mStatuBarType == 0) {
            return true;
        }
        if (mStatuBarType == 1) {
            return MIUISetStatusBarLightMode(paramActivity.getWindow(), false);
        }
        if (mStatuBarType == 2) {
            return FlymeSetStatusBarLightMode(paramActivity.getWindow(), false);
        }
        if (mStatuBarType == 3) {
            return Android6SetStatusBarLightMode(paramActivity.getWindow(), false);
        }
        return true;
    }

    public static boolean setStatusBarLightMode(Activity paramActivity)
    {
        if (QMUIDeviceHelper.isZTKC2016()) {
            return false;
        }
        if (mStatuBarType != 0) {
            return setStatusBarLightMode(paramActivity, mStatuBarType);
        }
        if (Build.VERSION.SDK_INT >= 19)
        {
            if (((isMIUICustomStatusBarLightModeImpl()) || ((Build.VERSION.SDK_INT < 23) && (QMUIDeviceHelper.isMIUIV9()))) && (MIUISetStatusBarLightMode(paramActivity.getWindow(), true)))
            {
                mStatuBarType = 1;
                return true;
            }
            if (FlymeSetStatusBarLightMode(paramActivity.getWindow(), true))
            {
                mStatuBarType = 2;
                return true;
            }
            if (Build.VERSION.SDK_INT >= 23)
            {
                Android6SetStatusBarLightMode(paramActivity.getWindow(), true);
                mStatuBarType = 3;
                return true;
            }
        }
        return false;
    }

    private static boolean setStatusBarLightMode(Activity paramActivity, int paramInt)
    {
        if (paramInt == 1) {
            return MIUISetStatusBarLightMode(paramActivity.getWindow(), true);
        }
        if (paramInt == 2) {
            return FlymeSetStatusBarLightMode(paramActivity.getWindow(), true);
        }
        if (paramInt == 3) {
            return Android6SetStatusBarLightMode(paramActivity.getWindow(), true);
        }
        return false;
    }

    public static void setVirtualDensity(float paramFloat)
    {
        sVirtualDensity = paramFloat;
    }

    public static void setVirtualDensityDpi(float paramFloat)
    {
        sVirtualDensityDpi = paramFloat;
    }

    public static boolean supportTransclentStatusBar6()
    {
        return (!QMUIDeviceHelper.isZUKZ1()) && (!QMUIDeviceHelper.isZTKC2016());
    }

    public static void translucent(Activity paramActivity)
    {
        translucent(paramActivity, 1073741824);
    }

    @TargetApi(19)
    public static void translucent(Activity paramActivity,  int paramInt)
    {
        if (Build.VERSION.SDK_INT < 19) {
            return;
        }
        if ((!QMUIDeviceHelper.isMeizu()) && (!QMUIDeviceHelper.isMIUI()))
        {
            if (Build.VERSION.SDK_INT >= 21)
            {
                Window paramActivity1 = paramActivity.getWindow();
                paramActivity1.getDecorView().setSystemUiVisibility(1280);
                if ((Build.VERSION.SDK_INT >= 23) && (supportTransclentStatusBar6()))
                {
                    paramActivity1.clearFlags(67108864);
                    paramActivity1.addFlags(Integer.MIN_VALUE);
                    paramActivity1.setStatusBarColor(0);
                    return;
                }
                paramActivity1.clearFlags(67108864);
                paramActivity1.addFlags(Integer.MIN_VALUE);
                paramActivity1.setStatusBarColor(paramInt);
            }
            return;
        }
        paramActivity.getWindow().setFlags(67108864, 67108864);
    }

    @Retention(RetentionPolicy.SOURCE)
    private static @interface StatusBarType {}
}
