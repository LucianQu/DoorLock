package com.blg.rtu.frmFunction.bean;

import java.io.Serializable;

public class DeviceCacheVo implements Serializable {
    private String pwd;
    private String sn;

    DeviceCacheVo() {}

    public DeviceCacheVo(String paramString1, String paramString2)
    {
        this.sn = paramString1;
        this.pwd = paramString2;
    }

    public String getPwd()
    {
        return this.pwd;
    }

    public String getSn()
    {
        return this.sn;
    }

    public void setPwd(String paramString)
    {
        this.pwd = paramString;
    }

    public void setSn(String paramString)
    {
        this.sn = paramString;
    }
}
