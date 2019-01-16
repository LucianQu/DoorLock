package com.blg.rtu.frmFunction.util;

import android.content.Context;
import android.content.SharedPreferences.Editor;
import android.content.res.Configuration;
import android.os.Build.VERSION;
import android.util.DisplayMetrics;

import com.google.gson.Gson;

import java.util.Locale;

public class LocaleUtils {
    public static final Locale LOCALE_CHINESE = Locale.CHINESE;
    public static final Locale LOCALE_ENGLISH = Locale.ENGLISH;
    private static final String LOCALE_FILE = "LOCALE_FILE";
    private static final String LOCALE_KEY = "LOCALE_KEY";
    public static final Locale LOCALE_RUSSIAN = new Locale("ru");

    public static Locale getCurrentLocale(Context paramContext)
    {
        if (VERSION.SDK_INT >= 24) {
            return paramContext.getResources().getConfiguration().getLocales().get(0);
        }
        return paramContext.getResources().getConfiguration().locale;
    }

    public static Locale getUserLocale(Context paramContext)
    {
        return jsonToLocale(paramContext.getSharedPreferences("LOCALE_FILE", 0).getString("LOCALE_KEY", ""));
    }

    private static Locale jsonToLocale(String paramString)
    {
        return (Locale)new Gson().fromJson(paramString, Locale.class);
    }

    private static String localeToJson(Locale paramLocale)
    {
        return new Gson().toJson(paramLocale);
    }

    public static boolean needUpdateLocale(Context paramContext, Locale paramLocale)
    {
        return paramLocale != null;
    }

    public static void saveUserLocale(Context paramContext, Locale paramLocale)
    {
        Editor editor = paramContext.getSharedPreferences("LOCALE_FILE", 0).edit();
        editor.putString("LOCALE_KEY", localeToJson(paramLocale));
        editor.apply();
    }

    public static void updateLocale(Context paramContext, Locale paramLocale)
    {
        if (needUpdateLocale(paramContext, paramLocale))
        {
            Configuration localConfiguration = paramContext.getResources().getConfiguration();
            if (VERSION.SDK_INT >= 17) {
                localConfiguration.setLocale(paramLocale);
            } else {
                localConfiguration.locale = paramLocale;
            }
            DisplayMetrics localDisplayMetrics = paramContext.getResources().getDisplayMetrics();
            paramContext.getResources().updateConfiguration(localConfiguration, localDisplayMetrics);
            saveUserLocale(paramContext, paramLocale);
        }
    }
}
