package com.blg.rtu.frmFunction.http;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Environment;
import android.util.Log;

import java.io.File;

public class FileConfig {
    private static final String CRASH = "/crash/";
    private static final String IMAGE_CACHE = "/images/";
    private static final String NOMEDIA_FILE = "/.nomedia";
    private static final String TAG = "FileConfig";
    private static final String TEMP = "/temp/";

    public static File getCacheDataDir(Context paramContext)
    {
        String state = "";
        File file = null ;
        try
        {
            state = Environment.getExternalStorageState();
        }
        catch (NullPointerException localNullPointerException)
        {
        }
        if (("mounted".equals(state)) && (hasExternalStoragePermission(paramContext))) {
            file = getExternalCacheDir(paramContext);
        } else {
            file = null;
        }
        if (file == null) {
            file = paramContext.getCacheDir();
        }
        if (file == null)
        {
            StringBuilder stringBuilder = new StringBuilder();
            ((StringBuilder)stringBuilder).append("/data/data/");
            ((StringBuilder)stringBuilder).append(paramContext.getPackageName());
            ((StringBuilder)stringBuilder).append("/cache/");
            String path = ((StringBuilder)stringBuilder).toString();
            file = new File(path);

            StringBuilder stringBuilder1 = new StringBuilder();
            ((StringBuilder)stringBuilder1).append("Can't define system cache directory! '%s' will be used.");
            ((StringBuilder)stringBuilder1).append(paramContext);
            Log.w("FileConfig", ((StringBuilder)stringBuilder1).toString());

        }
        return (File)file;
    }

    public static String getCrashDir()
    {
        File localFile = getFilesDataDir(ComApplication.getContext());
        StringBuilder localStringBuilder = new StringBuilder();
        localStringBuilder.append(localFile.getAbsolutePath());
        localStringBuilder.append("/crash/");
        localFile = new File(localStringBuilder.toString());
        if ((!localFile.exists()) && (!localFile.mkdirs()))
        {
            Log.w("FileConfig", "Can't create image cache dir! ");
            return null;
        }
        localStringBuilder = new StringBuilder();
        localStringBuilder.append(localFile.getAbsolutePath());
        localStringBuilder.append("/");
        return localStringBuilder.toString();
    }

    private static File getExternalCacheDir(Context paramContext)
    {
        File file = paramContext.getExternalCacheDir();
        if ((!file.exists()) && (!file.mkdirs()))
        {
            Log.w("FileConfig", "Unable to create external cache directory");
            return null;
        }
        return file;
    }

    private static File getExternalFilesDir(Context paramContext)
    {
        File file = null ;
        file = paramContext.getExternalFilesDir(null);
        if ((!file.exists()) && (!file.mkdirs()))
        {
            Log.w("FileConfig", "Unable to create external cache directory");
            return null;
        }
        return file;
    }

    public static File getFilesDataDir(Context paramContext)
    {
        String state = "" ;
        File file = null ;
        try
        {
            state = Environment.getExternalStorageState();
        }
        catch (NullPointerException localNullPointerException)
        {
        }
        if (("mounted".equals(state)) && (hasExternalStoragePermission(paramContext))) {
            file = getExternalFilesDir(paramContext);
        } else {
            file = null;
        }
        if (file == null) {
            file = paramContext.getFilesDir();
        }
        if (file == null)
        {
            StringBuilder builder = new StringBuilder();
            ((StringBuilder)builder).append("/data/data/");
            ((StringBuilder)builder).append(paramContext.getPackageName());
            ((StringBuilder)builder).append("/files/");
            String path = "" ;
            path = ((StringBuilder)builder).toString();
            StringBuilder builder1 = new StringBuilder();
            ((StringBuilder)builder1).append("Can't define system cache directory! '%s' will be used.");
            ((StringBuilder)builder1).append(paramContext);
            Log.w("FileConfig", ((StringBuilder)builder1).toString());

            file = new File(path);
        }
        return (File)file;
    }

    public static File getImageCacheDir()
    {
        File localFile = getCacheDataDir(ComApplication.getContext());
        Object localObject = new StringBuilder();
        ((StringBuilder)localObject).append(localFile.getAbsolutePath());
        ((StringBuilder)localObject).append("/images/");
        localFile = new File(((StringBuilder)localObject).toString());
        if ((!localFile.exists()) && (!localFile.mkdirs()))
        {
            Log.w("FileConfig", "Can't create image cache dir! ");
            return null;
        }
        localObject = new StringBuilder();
        ((StringBuilder)localObject).append(localFile.getAbsolutePath());
        ((StringBuilder)localObject).append("/.nomedia");
        localObject = new File(((StringBuilder)localObject).toString());
        if (!((File)localObject).exists()) {
            ((File)localObject).mkdirs();
        }
        return localFile;
    }

    public static String getImagePath()
    {
        StringBuilder localStringBuilder = new StringBuilder();
        localStringBuilder.append(getTempDir());
        localStringBuilder.append("/");
        localStringBuilder.append(IDGenerator.getImgName());
        return localStringBuilder.toString();
    }

    public static String getTempDir()
    {
        File localFile = getCacheDataDir(ComApplication.getContext());
        StringBuilder localStringBuilder = new StringBuilder();
        localStringBuilder.append(localFile.getAbsolutePath());
        localStringBuilder.append("/temp/");
        localFile = new File(localStringBuilder.toString());
        if ((!localFile.exists()) && (!localFile.mkdirs()))
        {
            Log.w("FileConfig", "Can't create image cache dir! ");
            return null;
        }
        localStringBuilder = new StringBuilder();
        localStringBuilder.append(localFile.getAbsolutePath());
        localStringBuilder.append("/");
        return localStringBuilder.toString();
    }

    private static boolean hasExternalStoragePermission(Context paramContext)
    {
        return paramContext.checkCallingOrSelfPermission("android.permission.WRITE_EXTERNAL_STORAGE") == PackageManager.PERMISSION_GRANTED;
    }
}
