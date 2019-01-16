package com.blg.rtu.frmFunction.bean;

import java.io.Serializable;

public class DeviceInfo implements Serializable {
    private boolean activation;
    private String deviceNo;
    private String ip;
    private int language;
    private int server;
    private String uuId;

    public DeviceInfo(String paramString1, String paramString2, String paramString3, boolean paramBoolean, int paramInt1, int paramInt2)
    {
        this.uuId = paramString1;
        this.deviceNo = paramString2;
        this.ip = paramString3;
        this.activation = paramBoolean;
        this.server = paramInt1;
        this.language = paramInt2;
    }

    public String getDeviceNo()
    {
        return this.deviceNo;
    }

    public String getIp()
    {
        return this.ip;
    }

    public int getLanguage()
    {
        return this.language;
    }

    public int getServer()
    {
        return this.server;
    }

    public String getUuId()
    {
        return this.uuId;
    }

    public boolean isActivation()
    {
        return this.activation;
    }

    public void setActivation(boolean paramBoolean)
    {
        this.activation = paramBoolean;
    }

    public void setDeviceNo(String paramString)
    {
        this.deviceNo = paramString;
    }

    public void setIp(String paramString)
    {
        this.ip = paramString;
    }

    public void setLanguage(int paramInt)
    {
        this.language = paramInt;
    }

    public void setServer(int paramInt)
    {
        this.server = paramInt;
    }

    public void setUuId(String paramString)
    {
        this.uuId = paramString;
    }
}
