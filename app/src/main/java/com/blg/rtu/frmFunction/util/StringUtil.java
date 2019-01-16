package com.blg.rtu.frmFunction.util;

import android.text.TextUtils;
import java.util.Locale;
import java.util.UUID;
public class StringUtil {
    public static int counterChars(String paramString)
    {
        boolean bool = TextUtils.isEmpty(paramString);
        int j = 0;
        if (bool) {
            return 0;
        }
        int i = 0;
        while (j < paramString.length())
        {
            int k = paramString.charAt(j);
            if ((k > 0) && (k < 127)) {
                i += 1;
            } else {
                i += 2;
            }
            j += 1;
        }
        return i;
    }

    public static final String filterUCS4(String paramString)
    {
        if (TextUtils.isEmpty(paramString)) {
            return paramString;
        }
        int j = paramString.length();
        int i = 0;
        if (paramString.codePointCount(0, j) == paramString.length()) {
            return paramString;
        }
        StringBuilder localStringBuilder = new StringBuilder();
        while (i < paramString.length())
        {
            j = paramString.codePointAt(i);
            i += Character.charCount(j);
            if (!Character.isSupplementaryCodePoint(j)) {
                localStringBuilder.appendCodePoint(j);
            }
        }
        return localStringBuilder.toString();
    }

    public static String get32UUID()
    {
        return UUID.randomUUID().toString().replaceAll("-", "");
    }

    public static String get36UUID()
    {
        return UUID.randomUUID().toString();
    }

    public static String getPercentString(float paramFloat)
    {
        return String.format(Locale.US, "%d%%", new Object[] { Integer.valueOf((int)(paramFloat * 100.0F)) });
    }

    public static boolean isEmpty(String paramString)
    {
        return TextUtils.isEmpty(paramString);
    }

    public static String makeMd5(String paramString)
    {
        return MD5.getStringMD5(paramString);
    }

    public static String removeBlanks(String paramString)
    {
        if (paramString == null) {
            return null;
        }
        StringBuilder localStringBuilder = new StringBuilder();
        localStringBuilder.append(paramString);
        int i = localStringBuilder.length() - 1;
        while (i >= 0)
        {
            if ((' ' == localStringBuilder.charAt(i)) || ('\n' == localStringBuilder.charAt(i)) || ('\t' == localStringBuilder.charAt(i)) || ('\r' == localStringBuilder.charAt(i))) {
                localStringBuilder.deleteCharAt(i);
            }
            i -= 1;
        }
        return localStringBuilder.toString();
    }
}
