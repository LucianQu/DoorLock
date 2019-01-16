package com.blg.rtu.frmFunction.util;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.NetworkInfo.State;
import android.net.Uri;
import android.os.Build;
import android.os.Build.VERSION;
import android.os.Environment;
import android.provider.MediaStore.Images.Media;
import android.support.v4.content.FileProvider;
import android.util.Log;

import com.blg.rtu.frmFunction.http.ComApplication;
import com.blg.rtu.frmFunction.util.QMUIStatusBarHelper;

import java.io.File;

import static android.content.Intent.FLAG_GRANT_READ_URI_PERMISSION;

public class AppUtil {
    private static final String TAG = "AppUtil";

    public static int dip2px(Context paramContext, float paramFloat)
    {
        return (int)(paramFloat * paramContext.getResources().getDisplayMetrics().density + 0.5F);
    }

    public static String getDeviceModel()
    {
        return Build.MODEL;
    }

    public static String getDeviceOS()
    {
        return VERSION.RELEASE;
    }

    private static PackageInfo getPackageInfo()
    {
        Object localObject = ComApplication.getContext().getPackageManager();
        try
        {
            localObject = ((PackageManager)localObject).getPackageInfo(ComApplication.getContext().getPackageName(), 0);
            return (PackageInfo)localObject;
        }
        catch (NameNotFoundException localNameNotFoundException)
        {
            Log.e("AppUtil", localNameNotFoundException.toString());
        }
        return null;
    }

    public static String getPackageName()
    {
        PackageInfo localPackageInfo = getPackageInfo();
        if (localPackageInfo != null) {
            return localPackageInfo.packageName;
        }
        return null;
    }

    public static int getScreenHeight(Context paramContext)
    {
        return paramContext.getResources().getDisplayMetrics().heightPixels;
    }

    public static int getScreenWidth(Context paramContext)
    {
        return paramContext.getResources().getDisplayMetrics().widthPixels;
    }

    public static int getStatusBarHeight(Activity paramActivity)
    {
        int i = paramActivity.getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (i > 0) {
            return paramActivity.getResources().getDimensionPixelSize(i);
        }
        return 0;
    }

    public static int getTargetSdkVersion(Context paramContext)
    {
        try
        {
            int i = paramContext.getPackageManager().getPackageInfo(paramContext.getPackageName(), 0).applicationInfo.targetSdkVersion;
            return i;
        }
        catch (NameNotFoundException e)
        {
            Log.e("AppUtil", paramContext.toString());
        }
        return 14;
    }

    public static int getVerCode()
    {
        PackageInfo localPackageInfo = getPackageInfo();
        if (localPackageInfo != null) {
            return localPackageInfo.versionCode;
        }
        return 0;
    }

    public static String getVerName()
    {
        PackageInfo localPackageInfo = getPackageInfo();
        if (localPackageInfo != null) {
            return localPackageInfo.versionName;
        }
        return null;
    }

    public static void install(Context paramContext, File paramFile)
    {
        try
        {
            Intent localIntent = new Intent("android.intent.action.VIEW");
            localIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            if (VERSION.SDK_INT >= 24)
            {
                StringBuilder localStringBuilder = new StringBuilder();
                localStringBuilder.append(paramContext.getPackageName());
                localStringBuilder.append(".provider");
                Uri uri = FileProvider.getUriForFile(paramContext, localStringBuilder.toString(), paramFile);
                localIntent.addFlags(FLAG_GRANT_READ_URI_PERMISSION);
                localIntent.setDataAndType(uri, "application/vnd.android.package-archive");
            }
            else
            {
                localIntent.setDataAndType(Uri.fromFile(paramFile), "application/vnd.android.package-archive");
            }
            paramContext.startActivity(localIntent);
            return;
        }
        catch (Exception e)
        {
            Log.e("",e.toString());
        }
    }

    public static boolean isConnect(Context paramContext)
    {
        try
        {
            ConnectivityManager  connectivity = (ConnectivityManager)paramContext.getSystemService(Context.CONNECTIVITY_SERVICE);
            if (paramContext != null)
            {
                NetworkInfo networkInfo = connectivity.getActiveNetworkInfo();
                if ((networkInfo != null) && (networkInfo.isConnected()))
                {
                    State state = networkInfo.getState();
                    State localState = State.CONNECTED;
                    if (state == localState) {
                        return true;
                    }
                }
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return false;
    }

  /*  public static void isNetWorkAvailable(String paramString, Comparable<Boolean> paramComparable)
    {
        new Thread(new Runnable()
        {
            public void handleMessage(Message paramAnonymousMessage)
            {
                super.handleMessage(paramAnonymousMessage);
                if (this.val$callback != null)
                {
                    Comparable localComparable = this.val$callback;
                    boolean bool;
                    if (paramAnonymousMessage.arg1 == 0) {
                        bool = true;
                    } else {
                        bool = false;
                    }
                    localComparable.compareTo(Boolean.valueOf(bool));
                }
            }
        }
        {
            *//* Error *//*
            public void run()
            {
                // Byte code:
                //   0: invokestatic 34	java/lang/Runtime:getRuntime	()Ljava/lang/Runtime;
                //   3: astore_1
                //   4: new 36	android/os/Message
                //   7: dup
                //   8: invokespecial 37	android/os/Message:<init>	()V
                //   11: astore_2
                //   12: new 39	java/lang/StringBuilder
                //   15: dup
                //   16: invokespecial 40	java/lang/StringBuilder:<init>	()V
                //   19: astore_3
                //   20: aload_3
                //   21: ldc 42
                //   23: invokevirtual 46	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
                //   26: pop
                //   27: aload_3
                //   28: aload_0
                //   29: getfield 19	com/fuchun/common/util/AppUtil$2:val$address	Ljava/lang/String;
                //   32: invokevirtual 46	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
                //   35: pop
                //   36: new 48	java/io/InputStreamReader
                //   39: dup
                //   40: aload_1
                //   41: aload_3
                //   42: invokevirtual 52	java/lang/StringBuilder:toString	()Ljava/lang/String;
                //   45: invokevirtual 56	java/lang/Runtime:exec	(Ljava/lang/String;)Ljava/lang/Process;
                //   48: invokevirtual 62	java/lang/Process:getInputStream	()Ljava/io/InputStream;
                //   51: invokespecial 65	java/io/InputStreamReader:<init>	(Ljava/io/InputStream;)V
                //   54: astore_3
                //   55: new 67	java/io/BufferedReader
                //   58: dup
                //   59: aload_3
                //   60: invokespecial 70	java/io/BufferedReader:<init>	(Ljava/io/Reader;)V
                //   63: astore 4
                //   65: aload 4
                //   67: invokevirtual 73	java/io/BufferedReader:readLine	()Ljava/lang/String;
                //   70: ifnonnull +11 -> 81
                //   73: aload_2
                //   74: iconst_m1
                //   75: putfield 77	android/os/Message:arg1	I
                //   78: goto +8 -> 86
                //   81: aload_2
                //   82: iconst_0
                //   83: putfield 77	android/os/Message:arg1	I
                //   86: aload 4
                //   88: invokevirtual 80	java/io/BufferedReader:close	()V
                //   91: aload_3
                //   92: invokevirtual 81	java/io/InputStreamReader:close	()V
                //   95: goto +17 -> 112
                //   98: astore_3
                //   99: goto +27 -> 126
                //   102: astore_3
                //   103: aload_2
                //   104: iconst_m1
                //   105: putfield 77	android/os/Message:arg1	I
                //   108: aload_3
                //   109: invokevirtual 84	java/lang/Exception:printStackTrace	()V
                //   112: aload_1
                //   113: invokevirtual 87	java/lang/Runtime:gc	()V
                //   116: aload_0
                //   117: getfield 21	com/fuchun/common/util/AppUtil$2:val$handler	Landroid/os/Handler;
                //   120: aload_2
                //   121: invokevirtual 93	android/os/Handler:sendMessage	(Landroid/os/Message;)Z
                //   124: pop
                //   125: return
                //   126: aload_1
                //   127: invokevirtual 87	java/lang/Runtime:gc	()V
                //   130: aload_0
                //   131: getfield 21	com/fuchun/common/util/AppUtil$2:val$handler	Landroid/os/Handler;
                //   134: aload_2
                //   135: invokevirtual 93	android/os/Handler:sendMessage	(Landroid/os/Message;)Z
                //   138: pop
                //   139: aload_3
                //   140: athrow
                // Local variable table:
                //   start	length	slot	name	signature
                //   0	141	0	this	2
                //   3	124	1	localRuntime	Runtime
                //   11	124	2	localMessage	Message
                //   19	73	3	localObject1	Object
                //   98	1	3	localObject2	Object
                //   102	38	3	localException	Exception
                //   63	24	4	localBufferedReader	java.io.BufferedReader
                // Exception table:
                //   from	to	target	type
                //   12	78	98	finally
                //   81	86	98	finally
                //   86	95	98	finally
                //   103	112	98	finally
                //   12	78	102	java/lang/Exception
                //   81	86	102	java/lang/Exception
                //   86	95	102	java/lang/Exception
            }
        }).start();
    }*/

   /* public static boolean isSupportTranslucent()
    {
        return QMUIStatusBarHelper.isSupportTranslucent();
    }*/

    public static boolean isTabletDevice(Context paramContext)
    {
        return (paramContext.getResources().getConfiguration().screenLayout & 0xF) >= 3;
    }

    public static void onTastOnlyOne(Activity paramActivity) {}

    public static void openAlbum(Activity paramActivity, int paramInt)
    {
        try
        {
            paramActivity.startActivityForResult(new Intent("android.intent.action.PICK", Media.EXTERNAL_CONTENT_URI), paramInt);
            return;
        }
        catch (Exception localException)
        {
            Log.i("AppUtil", nullToStr(localException));
            Intent localIntent = new Intent("android.intent.action.GET_CONTENT");
            localIntent.setType("image/*");
            paramActivity.startActivityForResult(localIntent, paramInt);
        }
    }

    public static String nullToStr(Object paramObject)
    {
        if ((paramObject != null) && (!paramObject.toString().equals("null"))) {
            return paramObject.toString().trim();
        }
        return "";
    }

    public static String pathToUri(String paramString)
    {
        if (paramString.startsWith("file:///")) {
            return paramString;
        }
        StringBuilder localStringBuilder = new StringBuilder();
        localStringBuilder.append("file://");
        localStringBuilder.append(paramString);
        return localStringBuilder.toString();
    }

    public static int px2dp(Context paramContext, float paramFloat)
    {
        return (int)(paramFloat / paramContext.getResources().getDisplayMetrics().density + 0.5F);
    }

 /*   public static boolean selfPermissionGranted(Context paramContext, String paramString)
    {
        if (Build.VERSION.SDK_INT >= 23)
        {
            if (getTargetSdkVersion(paramContext) >= 23) {
                if (paramContext.checkSelfPermission(paramString) == 0) {
                    return true;
                }
            }
            while (PermissionChecker.checkSelfPermission(paramContext, paramString) != 0) {
                return false;
            }
        }
        return true;
    }*/

    public static void setStatusBarLightMode(Activity paramActivity, boolean paramBoolean)
    {
        if (paramBoolean)
        {
            QMUIStatusBarHelper.setStatusBarLightMode(paramActivity);
            return;
        }
        QMUIStatusBarHelper.setStatusBarDarkMode(paramActivity);
    }

    public static int sp2px(Context paramContext, float paramFloat)
    {
        return (int)(paramFloat * paramContext.getResources().getDisplayMetrics().scaledDensity + 0.5F);
    }

    public static void syncAlbum(Context paramContext, String paramString)
    {
        if (VERSION.SDK_INT >= 19)
        {
            Intent localIntent = new Intent("android.intent.action.MEDIA_SCANNER_SCAN_FILE");
            localIntent.setData(Uri.fromFile(new File(paramString).getAbsoluteFile()));
            paramContext.sendBroadcast(localIntent);
            return;
        }
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("file://");
        stringBuilder.append(Environment.getExternalStorageDirectory());
        paramContext.sendBroadcast(new Intent("android.intent.action.MEDIA_MOUNTED", Uri.parse(stringBuilder.toString())));
    }

   /* public static void toPermissionManager(Activity paramActivity)
    {
        PermissionManagement.toPermissionManager(paramActivity);
    }*/

    public static void toQQAddFriend(Context paramContext, String paramString)
    {
        try
        {
            StringBuilder localStringBuilder = new StringBuilder();
            localStringBuilder.append("mqqwpa://im/chat?chat_type=wpa&uin=");
            localStringBuilder.append(paramString);
            localStringBuilder.append("&version=1");
            paramContext.startActivity(new Intent("android.intent.action.VIEW", Uri.parse(localStringBuilder.toString())));
            return;
        }
        catch (Exception e)
        {
            Log.d("AppUtil", e.toString());
        }
    }

   /* public static void translucent(Activity paramActivity)
    {
        QMUIStatusBarHelper.translucent(paramActivity, 1073741824);
    }*/
}
