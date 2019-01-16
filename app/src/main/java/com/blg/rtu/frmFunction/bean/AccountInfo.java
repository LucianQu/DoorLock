package com.blg.rtu.frmFunction.bean;

import com.google.gson.Gson;
import java.io.Serializable;

public class AccountInfo implements Serializable {
    private String countryCode;
    private String email;
    private int loginType;
    private String nickName;
    private int notice;
    private int noticeMode;
    private String phone;
    private String portrait;
    private long userId;
    private String userName;

    public String getCountryCode()
    {
        return this.countryCode;
    }

    public String getEmail()
    {
        return this.email;
    }

    public int getLoginType()
    {
        return this.loginType;
    }

    public String getNickName()
    {
        return this.nickName;
    }

    public int getNotice()
    {
        return this.notice;
    }

    public int getNoticeMode()
    {
        return this.noticeMode;
    }

    public String getPhone()
    {
        return this.phone;
    }

    public String getPortrait()
    {
        return this.portrait;
    }

    public long getUserId()
    {
        return this.userId;
    }

    public String getUserName()
    {
        return this.userName;
    }

    public void setCountryCode(String paramString)
    {
        this.countryCode = paramString;
    }

    public void setEmail(String paramString)
    {
        this.email = paramString;
    }

    public void setLoginType(int paramInt)
    {
        this.loginType = paramInt;
    }

    public void setNickName(String paramString)
    {
        this.nickName = paramString;
    }

    public void setNotice(int paramInt)
    {
        this.notice = paramInt;
    }

    public void setNoticeMode(int paramInt)
    {
        this.noticeMode = paramInt;
    }

    public void setPhone(String paramString)
    {
        this.phone = paramString;
    }

    public void setPortrait(String paramString)
    {
        this.portrait = paramString;
    }

    public void setUserId(long paramLong)
    {
        this.userId = paramLong;
    }

    public void setUserName(String paramString)
    {
        this.userName = paramString;
    }

    public String toString()
    {
        return new Gson().toJson(this);
    }
}
