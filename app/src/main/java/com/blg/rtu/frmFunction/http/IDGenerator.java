package com.blg.rtu.frmFunction.http;
import java.util.UUID;

public class IDGenerator {
    public static String getApkName()
    {
        StringBuilder localStringBuilder = new StringBuilder();
        localStringBuilder.append(getDefaultUUID());
        localStringBuilder.append(".apk");
        return localStringBuilder.toString();
    }

    public static String getDefaultUUID()
    {
        return UUID.randomUUID().toString().replace("-", "");
    }

    public static String getImgName()
    {
        StringBuilder localStringBuilder = new StringBuilder();
        localStringBuilder.append(getDefaultUUID());
        localStringBuilder.append(".jpg");
        return localStringBuilder.toString();
    }
}
