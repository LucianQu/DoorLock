package com.blg.rtu.frmFunction.http;

import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.List;

public class JsonUtil {
    public static <T> List<T> getListFromJSON(String paramString, Class<T[]> paramClass)
    {
        String str;
        if (paramString != null)
        {
            str = paramString;
            if (!paramString.isEmpty()) {}
        }
        else
        {
            str = "[]";
        }
        return Arrays.asList((T[])new Gson().fromJson(str, paramClass));
    }

    public static <T> List<T> getListFromJSON(JSONArray paramJSONArray, Class<T[]> paramClass)
    {
        String s ;
        if ((paramJSONArray != null) && (paramJSONArray.length() > 0)) {
            s = paramJSONArray.toString();
        } else {
            s = null;
        }
        return getListFromJSON(s, paramClass);
    }

    public static <T> T getModelFromJSON(String paramString, Class<T> paramClass)
    {
        String str;
        if (paramString != null)
        {
            str = paramString;
            if (!paramString.isEmpty()) {}
        }
        else
        {
            str = "{}";
        }
        return (T)new Gson().fromJson(str, paramClass);
    }

    public static <T> T getModelFromJSON(String paramString, Type paramType)
    {
        String str;
        if (paramString != null)
        {
            str = paramString;
            if (!paramString.isEmpty()) {}
        }
        else
        {
            str = "{}";
        }
        return (T)new Gson().fromJson(str, paramType);
    }

    public static <T> T getModelFromJSON(JSONObject paramJSONObject, Class<T> paramClass)
    {
        String s ;
        if (paramJSONObject != null) {
            s = paramJSONObject.toString();
        } else {
            s = null;
        }
        return (T)getModelFromJSON(s, paramClass);
    }
}
