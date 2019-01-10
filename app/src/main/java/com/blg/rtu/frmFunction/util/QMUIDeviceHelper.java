package com.blg.rtu.frmFunction.util;

import android.annotation.TargetApi;
import android.app.AppOpsManager;
import android.content.Context;
import android.os.Binder;
import android.os.Build;
import android.text.TextUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class QMUIDeviceHelper {
    private static final String FLYME = "flyme";
    private static final String KEY_FLYME_VERSION_NAME = "ro.build.display.id";
    private static final String KEY_MIUI_VERSION_NAME = "ro.miui.ui.version.name";
    private static final String[] MEIZUBOARD = new String[10];
    private static final String TAG = "QMUIDeviceHelper";
    private static final String ZTEC2016 = "zte c2016";
    private static final String ZUKZ1 = "zuk z1";
    private static String sFlymeVersionName;
    private static boolean sIsTabletChecked = false;
    private static boolean sIsTabletValue = false;
    private static String sMiuiVersionName;

    /* Error */
    static
    {
        // Byte code:
        //   0: iconst_4
        //   1: anewarray 37	java/lang/String
        //   4: dup
        //   5: iconst_0
        //   6: ldc 39
        //   8: aastore
        //   9: dup
        //   10: iconst_1
        //   11: ldc 41
        //   13: aastore
        //   14: dup
        //   15: iconst_2
        //   16: ldc 43
        //   18: aastore
        //   19: dup
        //   20: iconst_3
        //   21: ldc 45
        //   23: aastore
        //   24: putstatic 47	com/fuchun/common/util/ui/QMUIDeviceHelper:MEIZUBOARD	[Ljava/lang/String;
        //   27: new 49	java/io/FileInputStream
        //   30: dup
        //   31: new 51	java/io/File
        //   34: dup
        //   35: invokestatic 57	android/os/Environment:getRootDirectory	()Ljava/io/File;
        //   38: ldc 59
        //   40: invokespecial 63	java/io/File:<init>	(Ljava/io/File;Ljava/lang/String;)V
        //   43: invokespecial 66	java/io/FileInputStream:<init>	(Ljava/io/File;)V
        //   46: astore_1
        //   47: new 68	java/util/Properties
        //   50: dup
        //   51: invokespecial 70	java/util/Properties:<init>	()V
        //   54: astore_0
        //   55: aload_0
        //   56: aload_1
        //   57: invokevirtual 74	java/util/Properties:load	(Ljava/io/InputStream;)V
        //   60: ldc 76
        //   62: invokestatic 82	java/lang/Class:forName	(Ljava/lang/String;)Ljava/lang/Class;
        //   65: ldc 84
        //   67: iconst_1
        //   68: anewarray 78	java/lang/Class
        //   71: dup
        //   72: iconst_0
        //   73: ldc 37
        //   75: aastore
        //   76: invokevirtual 88	java/lang/Class:getDeclaredMethod	(Ljava/lang/String;[Ljava/lang/Class;)Ljava/lang/reflect/Method;
        //   79: astore_1
        //   80: aload_0
        //   81: aload_1
        //   82: ldc 14
        //   84: invokestatic 92	com/fuchun/common/util/ui/QMUIDeviceHelper:getLowerCaseName	(Ljava/util/Properties;Ljava/lang/reflect/Method;Ljava/lang/String;)Ljava/lang/String;
        //   87: putstatic 94	com/fuchun/common/util/ui/QMUIDeviceHelper:sMiuiVersionName	Ljava/lang/String;
        //   90: aload_0
        //   91: aload_1
        //   92: ldc 11
        //   94: invokestatic 92	com/fuchun/common/util/ui/QMUIDeviceHelper:getLowerCaseName	(Ljava/util/Properties;Ljava/lang/reflect/Method;Ljava/lang/String;)Ljava/lang/String;
        //   97: putstatic 96	com/fuchun/common/util/ui/QMUIDeviceHelper:sFlymeVersionName	Ljava/lang/String;
        //   100: return
        //   101: astore_0
        //   102: aload_0
        //   103: athrow
        //   104: astore_0
        //   105: return
        // Local variable table:
        //   start	length	slot	name	signature
        //   54	37	0	localProperties	Properties
        //   101	2	0	localObject1	Object
        //   104	1	0	localException	Exception
        //   46	46	1	localObject2	Object
        // Exception table:
        //   from	to	target	type
        //   27	100	101	finally
        //   27	100	104	java/lang/Exception
    }

    private static boolean _isTablet(Context paramContext)
    {
        return (paramContext.getResources().getConfiguration().screenLayout & 0xF) >= 3;
    }

    @TargetApi(19)
    private static boolean checkOp(Context paramContext, int paramInt)
    {
        int i = Build.VERSION.SDK_INT;
        boolean bool = false;
        if (i >= 19)
        {
            AppOpsManager localAppOpsManager = (AppOpsManager)paramContext.getSystemService(Context.APP_OPS_SERVICE);
            try
            {
                paramInt = ((Integer)localAppOpsManager.getClass().getDeclaredMethod("checkOp", new Class[] { Integer.TYPE, Integer.TYPE, String.class }).invoke(localAppOpsManager, new Object[] { Integer.valueOf(paramInt), Integer.valueOf(Binder.getCallingUid()), paramContext.getPackageName() })).intValue();
                if (paramInt == 0) {
                    bool = true;
                }
                return bool;
            }
            catch (Exception paramContext1)
            {
                paramContext1.printStackTrace();
            }
        }
        return false;
    }


    public static boolean isFloatWindowOpAllowed(Context paramContext)
    {
        if (Build.VERSION.SDK_INT >= 19) {
            return checkOp(paramContext, 24);
        }
        boolean bool = false;
        try
        {
            int i = paramContext.getApplicationInfo().flags;
            if ((i & 0x8000000) == 134217728) {
                bool = true;
            }
            return bool;
        }
        catch (Exception paramContext1)
        {
            paramContext1.printStackTrace();
        }
        return false;
    }

    public static boolean isFlyme()
    {
        return (!TextUtils.isEmpty(sFlymeVersionName)) && (sFlymeVersionName.contains("flyme"));
    }

    public static boolean isFlymeVersionHigher5_2_4()
    {
        Object localObject = sFlymeVersionName;
        boolean bool2 = false;
        int i = 1;
        if ((localObject != null) && (!sFlymeVersionName.equals("")))
        {
            localObject = Pattern.compile("(\\d+\\.){2}\\d").matcher(sFlymeVersionName);
            if (((Matcher)localObject).find())
            {
                localObject = ((Matcher)localObject).group();
                if ((localObject != null) && (!((String)localObject).equals("")))
                {
                    String[] localObject1 = ((String)localObject).split("\\.");
                    if (localObject1.length == 3)
                    {
                        if (Integer.valueOf(localObject1[0]).intValue() < 5) {}
                        do
                        {
                            do
                            {
                                i = 0 ;
                                if (Integer.valueOf(localObject1[0]).intValue() > 5) {
                                    break;
                                }
                            } while (Integer.valueOf(localObject1[1]).intValue() < 2);
                            if (Integer.valueOf(localObject1[1]).intValue() > 2) {
                                break;
                            }
                        } while (Integer.valueOf(localObject1[2]).intValue() < 4);
                        Integer.valueOf(localObject1[2]).intValue();
                    }
                }
            }
        }
        boolean bool1 = false;
        if (isMeizu())
        {
            bool1 = false;
            if (i != 0) {
                bool1 = true;
            }
        }
        return bool1;
    }

    public static boolean isMIUI()
    {
        return TextUtils.isEmpty(sMiuiVersionName) ^ true;
    }

    public static boolean isMIUIV5()
    {
        return "v5".equals(sMiuiVersionName);
    }

    public static boolean isMIUIV6()
    {
        return "v6".equals(sMiuiVersionName);
    }

    public static boolean isMIUIV7()
    {
        return "v7".equals(sMiuiVersionName);
    }

    public static boolean isMIUIV8()
    {
        return "v8".equals(sMiuiVersionName);
    }

    public static boolean isMIUIV9()
    {
        return "v9".equals(sMiuiVersionName);
    }

    public static boolean isMeizu()
    {
        return (isPhone(MEIZUBOARD)) || (isFlyme());
    }

    private static boolean isPhone(String[] paramArrayOfString)
    {
        String str = Build.BOARD;
        if (str == null) {
            return false;
        }
        int j = paramArrayOfString.length;
        int i = 0;
        while (i < j)
        {
            if (str.equals(paramArrayOfString[i])) {
                return true;
            }
            i += 1;
        }
        return false;
    }

    public static boolean isTablet(Context paramContext)
    {
        if (sIsTabletChecked) {
            return sIsTabletValue;
        }
        sIsTabletValue = _isTablet(paramContext);
        sIsTabletChecked = true;
        return sIsTabletValue;
    }

    public static boolean isXiaomi()
    {
        return Build.BRAND.toLowerCase().contains("xiaomi");
    }

    public static boolean isZTKC2016()
    {
        String str = Build.MODEL;
        return (str != null) && (str.toLowerCase().contains("zte c2016"));
    }

    public static boolean isZUKZ1()
    {
        String str = Build.MODEL;
        return (str != null) && (str.toLowerCase().contains("zuk z1"));
    }
}
