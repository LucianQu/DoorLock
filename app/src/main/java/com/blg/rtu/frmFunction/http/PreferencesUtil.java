package com.blg.rtu.frmFunction.http;


import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.util.Log;

public class PreferencesUtil {
    public static final String PREFERENCE_NAME = "PreferencesUtil";

    private PreferencesUtil()
    {
        throw new AssertionError();
    }

    public static boolean clean(Context paramContext)
    {
        Editor editor = paramContext.getSharedPreferences("PreferencesUtil", 0).edit();
        editor.clear();
        return editor.commit();
    }

    public static boolean getBoolean(Context paramContext, String paramString)
    {
        return getBoolean(paramContext, paramString, false);
    }

    public static boolean getBoolean(Context paramContext, String paramString, boolean paramBoolean)
    {
        return paramContext.getSharedPreferences("PreferencesUtil", 0).getBoolean(paramString, paramBoolean);
    }

    public static float getFloat(Context paramContext, String paramString)
    {
        return getFloat(paramContext, paramString, 0.0F);
    }

    public static float getFloat(Context paramContext, String paramString, float paramFloat)
    {
        SharedPreferences preferences = paramContext.getSharedPreferences("PreferencesUtil", 0);
        try
        {
            paramFloat = preferences.getFloat(paramString, paramFloat);
            return paramFloat;
        }
        catch (Exception e)
        {
            Log.e("PreferencesUtil", e.toString());
        }
        return 0.0F;
    }

    public static int getInt(Context paramContext, String paramString)
    {
        return getInt(paramContext, paramString, -1);
    }

    public static int getInt(Context paramContext, String paramString, int paramInt)
    {
        SharedPreferences preferences = paramContext.getSharedPreferences("PreferencesUtil", 0);
        try
        {
            int i = preferences.getInt(paramString, paramInt);
            return i;
        }
        catch (Exception e)
        {
            Log.e("PreferencesUtil", paramContext.toString());
        }
        return paramInt;
    }

    public static long getLong(Context paramContext, String paramString)
    {
        return getLong(paramContext, paramString, -1L);
    }

    public static long getLong(Context paramContext, String paramString, long paramLong)
    {
        SharedPreferences preferences = paramContext.getSharedPreferences("PreferencesUtil", 0);
        try
        {
            long l = preferences.getLong(paramString, paramLong);
            return l;
        }
        catch (Exception e)
        {
            Log.e("PreferencesUtil", paramContext.toString());
        }
        return paramLong;
    }

    public static String getString(Context paramContext, String paramString)
    {
        return getString(paramContext, paramString, "");
    }

    public static String getString(Context paramContext, String paramString1, String paramString2)
    {
        return paramContext.getSharedPreferences("PreferencesUtil", 0).getString(paramString1, paramString2);
    }

    public static boolean putBoolean(Context paramContext, String paramString, boolean paramBoolean)
    {
        Editor editor = paramContext.getSharedPreferences("PreferencesUtil", 0).edit();
        editor.putBoolean(paramString, paramBoolean);
        return editor.commit();
    }

    public static boolean putFloat(Context paramContext, String paramString, float paramFloat)
    {
        Editor editor = paramContext.getSharedPreferences("PreferencesUtil", 0).edit();
        editor.putFloat(paramString, paramFloat);
        return editor.commit();
    }

    public static boolean putInt(Context paramContext, String paramString, int paramInt)
    {
        Editor editor = paramContext.getSharedPreferences("PreferencesUtil", 0).edit();
        editor.putInt(paramString, paramInt);
        return editor.commit();
    }

    public static boolean putLong(Context paramContext, String paramString, long paramLong)
    {
        Editor editor = paramContext.getSharedPreferences("PreferencesUtil", 0).edit();
        editor.putLong(paramString, paramLong);
        return editor.commit();
    }

    public static boolean putString(Context paramContext, String paramString1, String paramString2)
    {
        Editor editor = paramContext.getSharedPreferences("PreferencesUtil", 0).edit();
        editor.putString(paramString1, paramString2);
        return editor.commit();
    }

    public static boolean removeSharedPreferenceByKey(Context paramContext, String paramString)
    {
        Editor editor = paramContext.getSharedPreferences("PreferencesUtil", 0).edit();
        editor.remove(paramString);
        return editor.commit();
    }
}
