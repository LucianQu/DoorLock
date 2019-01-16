package com.blg.rtu.frmFunction.util;
import android.content.Context;

import com.blg.rtu.frmFunction.bean.AccountInfo;
import com.blg.rtu.frmFunction.bean.DeviceCacheVo;
import com.blg.rtu.frmFunction.bean.FamilyVo;
import com.blg.rtu.frmFunction.bean.PositionVo;
import com.blg.rtu.frmFunction.bean.UserVo;
import com.blg.rtu.frmFunction.bean.WeatherVo;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
public class DataUtil {
    private static int playBackId;
    private static DataUtil single;
    private FamilyVo family;
    private List<FamilyVo> familys;
    private PositionVo positionVo;
    private String ssid;
    private WeatherVo weatherVo;

    public static DataUtil getInstance()
    {
        if (single == null) {
            single = new DataUtil();
        }
        return single;
    }

    public void addDeviceCache(Context paramContext, String paramString1, String paramString2)
    {
        ArrayList localArrayList = new ArrayList();
        localArrayList.addAll(getDeviceCaches(paramContext));
        Iterator localIterator = localArrayList.iterator();
        while (localIterator.hasNext())
        {
            DeviceCacheVo localDeviceCacheVo = (DeviceCacheVo)localIterator.next();
            if (paramString1.equals(localDeviceCacheVo.getSn())) {
                localArrayList.remove(localDeviceCacheVo);
            }
        }
        try
        {
            localArrayList.add(new DeviceCacheVo(paramString1, paramString2));
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        PreferencesUtil.putString(paramContext, "deviceCaches", new Gson().toJson(localArrayList));
    }

    public void addFamilys(Context paramContext, List<FamilyVo> paramList)
    {
        PreferencesUtil.putString(paramContext, "familys", new Gson().toJson(paramList));
    }

    public void addPosition(Context paramContext, PositionVo paramPositionVo)
    {
        PreferencesUtil.putString(paramContext, "position", paramPositionVo.toString());
    }

    public void addSsid(Context paramContext, String paramString)
    {
        PreferencesUtil.putString(paramContext, "ssid", paramString);
    }

    public void addVideoPreview(Context paramContext, Map paramMap)
    {
        PreferencesUtil.putString(paramContext, "videoPreview", new Gson().toJson(paramMap));
    }

    public void addWeather(Context paramContext, WeatherVo paramWeatherVo)
    {
        PreferencesUtil.putString(paramContext, "weather", paramWeatherVo.toString());
    }

    public boolean addWifi(Context paramContext, Map paramMap)
    {
        return PreferencesUtil.putString(paramContext, "settingWifi", new Gson().toJson(paramMap));
    }

    public FamilyVo getCurrentFamily()
    {
        return this.family;
    }

    public String getDeviceCache(Context paramContext, String paramString)
    {
        List<DeviceCacheVo> list = getDeviceCaches(paramContext);
        if ((list != null) && (list.size() > 0))
        {
            Iterator iterator = list.iterator();
            while (iterator.hasNext())
            {
                DeviceCacheVo localDeviceCacheVo = (DeviceCacheVo)iterator.next();
                if (paramString.equals(localDeviceCacheVo.getSn())) {
                    return localDeviceCacheVo.getPwd();
                }
            }
        }
        return "";
    }

    public List<DeviceCacheVo> getDeviceCaches(Context paramContext)
    {
        return JsonUtil.getListFromJSON(StrUtil.nullToStr(PreferencesUtil.getString(paramContext, "deviceCaches")), DeviceCacheVo[].class);
    }

    public List<FamilyVo> getFamilys(Context paramContext)
    {
        if (this.familys == null)
        {
            String s = PreferencesUtil.getString(paramContext, "familys");
            if (!StringUtil.isEmpty(s)) {
                this.familys = ((List)new Gson().fromJson(s, new TypeToken() {}.getType()));
            }
        }
        return this.familys;
    }

    public int getPlayBackId()
    {
        try
        {
            playBackId += 1;
            if (playBackId > 30) {
                playBackId = 1;
            }
            int i = playBackId;
            return i;
        }
        finally {}
    }

    public PositionVo getPosition(Context paramContext)
    {
        if (this.positionVo == null)
        {
            String s = PreferencesUtil.getString(paramContext, "position");
            if (!StringUtil.isEmpty(s)) {
                this.positionVo = ((PositionVo)JsonUtil.getModelFromJSON(s, PositionVo.class));
            }
        }
        return this.positionVo;
    }

    public String getSsid(Context paramContext)
    {
        if (this.ssid == null) {
            this.ssid = PreferencesUtil.getString(paramContext, "ssid");
        }
        return this.ssid;
    }

    public Map getVideoPreview(Context paramContext)
    {
        String s = PreferencesUtil.getString(paramContext, "videoPreview");
        if (!StringUtil.isEmpty(s)) {
            return (Map)JsonUtil.getModelFromJSON(s, Map.class);
        }
        return null;
    }

    public String getVideoSound(Context paramContext)
    {
        return PreferencesUtil.getString(paramContext, "videoSound");
    }

    public WeatherVo getWeather(Context paramContext)
    {
        if (this.weatherVo == null)
        {
            String s = PreferencesUtil.getString(paramContext, "weather");
            if (!StringUtil.isEmpty(s)) {
                this.weatherVo = ((WeatherVo)JsonUtil.getModelFromJSON(s, WeatherVo.class));
            }
        }
        return this.weatherVo;
    }

    public Map getWifi(Context paramContext)
    {
        String s = PreferencesUtil.getString(paramContext, "settingWifi");
        if (!StringUtil.isEmpty(s)) {
            return (Map)JsonUtil.getModelFromJSON(s, Map.class);
        }
        return null;
    }

    public void openVideoSound(Context paramContext, String paramString)
    {
        PreferencesUtil.putString(paramContext, "videoSound", paramString);
    }

    public boolean removeVideoSound(Context paramContext)
    {
        return PreferencesUtil.removeSharedPreferenceByKey(paramContext, "videoSound");
    }

    public void saveUserData(Context paramContext, UserVo paramUserVo)
    {
        AccountInfo localAccountInfo = new AccountInfo();
        localAccountInfo.setUserId(paramUserVo.getTid());
        localAccountInfo.setUserName(paramUserVo.getLoginName());
        localAccountInfo.setCountryCode(paramUserVo.getCountryCode());
        localAccountInfo.setPhone(paramUserVo.getPhone());
        localAccountInfo.setNickName(paramUserVo.getUserName());
        localAccountInfo.setPortrait(paramUserVo.getImageUrl());
        localAccountInfo.setLoginType(paramUserVo.getLoginType());
        localAccountInfo.setNotice(paramUserVo.getNotice());
        localAccountInfo.setNoticeMode(paramUserVo.getNoticeMode());
        AccountUtil.getInstance().saveAccountInfo(paramContext, localAccountInfo);
    }

    public void setCurrentFamily(FamilyVo paramFamilyVo)
    {
        this.family = paramFamilyVo;
    }
}
